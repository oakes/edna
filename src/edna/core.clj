(ns edna.core
  (:require [alda.now :as now]
            [alda.sound :as sound]
            [edna.parse :as parse]
            [clojure.string :as str]
            [alda.lisp.score :as als]
            [alda.lisp.events :as ale]
            [alda.lisp.attributes :as ala]
            [alda.lisp.model.duration :as almd]
            [alda.lisp.model.pitch :as almp]
            [alda.sound.midi :as midi])
  (:import [javax.sound.midi MidiSystem]))

(def default-attrs {:octave 4 :length 1/4 :tempo 120
                    :pan 50 :quantize 90 :transpose 0
                    :volume 100 :parent-ids [] :play? true})

(defmulti build-score (fn [val parent-attrs] (first val)))

(defmethod build-score :score [[_ {:keys [subscores] :as score}]
                               {:keys [sibling-id parent-ids] :as parent-attrs}]
  (let [id (inc (or sibling-id 0))
        {:keys [instrument] :as attrs} (merge parent-attrs (select-keys score [:instrument]))]
    [(ale/part (if instrument (name instrument) {})
       (when sibling-id
         (ale/at-marker (str/join "." (conj parent-ids sibling-id))))
       (first
         (reduce
           (fn [[subscores attrs] subscore]
             (let [attrs (assoc attrs :parent-ids
                           (conj parent-ids id))
                   [subscore attrs] (build-score subscore attrs)]
               [(conj subscores subscore) attrs]))
           [[] (dissoc attrs :sibling-id)]
           (vec subscores)))
       (ale/marker (str/join "." (conj parent-ids id))))
     (assoc parent-attrs :sibling-id id)]))

(defmethod build-score :concurrent-score [[_ scores]
                                          {:keys [sibling-id parent-ids] :as parent-attrs}]
  (let [id (inc (or sibling-id 0))
        instruments (map :instrument scores)]
    (when (not= (count instruments) (count (set instruments)))
      (throw (Exception. (str
                           "Can't use the same instrument "
                           "multiple times in the same set "
                           "(this limitation my change eventually)"))))
    [(ale/part {}
       (reduce
         (fn [scores score]
           (let [[score _] (build-score [:score score] parent-attrs)]
             (conj scores score)))
         []
         (vec scores))
       (ale/marker (str/join "." (conj parent-ids id))))
     (assoc parent-attrs :sibling-id id)]))

(defmethod build-score :attrs [[_ {:keys [note] :as attrs}] parent-attrs]
  (if note
    (let [[note {:keys [sibling-id]}] (build-score [:note note] (merge parent-attrs attrs))]
      [note (assoc parent-attrs :sibling-id sibling-id)])
    [nil (merge parent-attrs attrs)]))

(defmethod build-score :note [[_ note]
                              {:keys [instrument octave length tempo
                                      pan quantize transpose volume
                                      sibling-id parent-ids play?]
                               :as parent-attrs}]
  (when-not instrument
    (throw (Exception. (str "Can't play " note " without specifying an instrument"))))
  (if-not play?
    [nil parent-attrs]
    (let [id (inc (or sibling-id 0))
          {:keys [note accidental octave-op octaves]} (parse/parse-note note)
          note (keyword (str note))
          accidental (case accidental
                       \# :sharp
                       \= :flat
                       \_ :natural
                       nil)
          octaves (or octaves
                      (if octave-op [\1] [\0]))
          octave-change (cond-> (Integer/valueOf (str/join octaves))
                                (= \- octave-op) (* -1))]
      [(ale/part (name instrument)
         (when sibling-id
           (ale/at-marker (str/join "." (conj parent-ids sibling-id))))
         (ala/octave (+ octave octave-change))
         (ala/tempo tempo)
         (ala/pan pan)
         (ala/quantize quantize)
         (ala/transpose transpose)
         (ala/volume volume)
         (ale/note
           (or (some->> accidental (almp/pitch note))
               (almp/pitch note))
           (almd/duration (almd/note-length (/ 1 length))))
         (ale/marker (str/join "." (conj parent-ids id))))
       (assoc parent-attrs :sibling-id id)])))

(defmethod build-score :chord [[_ chord]
                               {:keys [instrument sibling-id parent-ids play?]
                                :as parent-attrs}]
  (when-not instrument
    (throw (Exception. (str "Can't play "
                         (set (map second chord))
                         " without specifying an instrument"))))
  (if-not play?
    [nil parent-attrs]
    (let [id (inc (or sibling-id 0))
          attrs (-> parent-attrs
                    (assoc :parent-ids (conj parent-ids id))
                    (dissoc :sibling-id))]
      [(ale/part (name instrument)
         (when sibling-id
           (ale/at-marker (str/join "." (conj parent-ids sibling-id))))
         (apply ale/chord
           (map (fn [note]
                  (first (build-score note attrs)))
             chord))
         (ale/marker (str/join "." (conj parent-ids id))))
       (assoc parent-attrs :sibling-id id)])))

(defmethod build-score :rest [[_ _] {:keys [length sibling-id parent-ids]
                                     :as parent-attrs}]
  (let [id (inc (or sibling-id 0))]
    [[(when sibling-id
        (ale/at-marker (str/join "." (conj parent-ids sibling-id))))
      (ale/pause (almd/duration (almd/note-length (/ 1 length))))
      (ale/marker (str/join "." (conj parent-ids id)))]
     (assoc parent-attrs :sibling-id id)]))

(defmethod build-score :length [[_ length] parent-attrs]
  [nil (assoc parent-attrs :length length)])

(defmethod build-score :default [[subscore-name] parent-attrs]
  (throw (Exception. (str subscore-name " not recognized"))))

(defn edna->alda [content]
  (->> default-attrs
       (build-score (parse/parse content))
       first))

(defn stop! [score]
  (some-> score sound/tear-down!)
  nil)

(defn play! [content]
  (binding [midi/*midi-synth* (midi/new-midi-synth)
            sound/*play-opts* {:async? true
                               :one-off? true}]
    (-> content edna->alda als/score sound/play! :score)))

(defn export! [content out]
  (binding [midi/*midi-synth* (midi/new-midi-synth)
            sound/*play-opts* {:async? false
                               :one-off? true}]
    (-> content edna->alda als/score sound/create-sequence!
        (MidiSystem/write 0 out))))

