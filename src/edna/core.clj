(ns edna.core
  (:require [alda.lisp :as al]
            [alda.now :as now]
            [edna.parse :refer [parse parse-note]]
            [clojure.edn :as edn]
            [clojure.string :as str]))

(def default-attrs {:octave 4 :length 1/4 :tempo 120 :parent-ids []})

(defmulti build-score (fn [val parent-attrs] (first val)))

(defmethod build-score :score [[_ {:keys [instrument subscores]}]
                               {:keys [sibling-id parent-ids] :as parent-attrs}]
  (let [id (inc (or sibling-id 0))
        attrs (if instrument
                (assoc parent-attrs :instrument instrument)
                parent-attrs)]
    [(al/part (if instrument (name instrument) {})
       (when sibling-id
         (al/at-marker (str/join "." (conj parent-ids sibling-id))))
       (first
         (reduce
           (fn [[subscores attrs] subscore]
             (let [attrs (assoc attrs :parent-ids
                           (conj parent-ids id))
                   [subscore attrs] (build-score subscore attrs)]
               [(conj subscores subscore) attrs]))
           [[] (dissoc attrs :sibling-id)]
           (vec subscores)))
       (al/marker (str/join "." (conj parent-ids id))))
     (assoc parent-attrs :sibling-id id)]))

(defmethod build-score :concurrent-score [[_ scores]
                                          {:keys [sibling-id parent-ids] :as parent-attrs}]
  (let [id (inc (or sibling-id 0))]
    [(al/part {}
       (reduce
         (fn [scores score]
           (let [[score _] (build-score [:score score] parent-attrs)]
             (conj scores score)))
         []
         (vec scores))
       (al/marker (str/join "." (conj parent-ids id))))
     (assoc parent-attrs :sibling-id id)]))

(defmethod build-score :attrs [[_ {:keys [note] :as attrs}] parent-attrs]
  (if note
    [(first (build-score [:note note] (merge parent-attrs attrs)))
     parent-attrs]
    [nil (merge parent-attrs attrs)]))

(defmethod build-score :note [[_ note]
                              {:keys [octave length tempo
                                      sibling-id parent-ids]
                               :as parent-attrs}]
  (let [id (inc (or sibling-id 0))
        {:keys [note pitch octave-op octaves]} (parse-note note)
        note (keyword (str note))
        pitch (case pitch
                \# :sharp
                \= :flat
                :natural)
        octaves (or octaves
                    (if octave-op [\1] [\0]))
        octave-change (cond-> (Integer/valueOf (str/join octaves))
                              (= \- octave-op) (* -1))]
    [[(when sibling-id
        (al/at-marker (str/join "." (conj parent-ids sibling-id))))
      (al/octave (+ octave octave-change))
      (al/tempo tempo)
      (al/note
        (al/pitch note pitch)
        (al/duration (al/note-length (/ 1 length))))
      (al/marker (str/join "." (conj parent-ids id)))]
     (assoc parent-attrs :sibling-id id)]))

(defmethod build-score :chord [[_ chord]
                               {:keys [sibling-id parent-ids] :as parent-attrs}]
  (let [id (inc (or sibling-id 0))
        attrs (-> parent-attrs
                  (assoc :parent-ids (conj parent-ids id))
                  (dissoc :sibling-id))]
    [[(when sibling-id
        (al/at-marker (str/join "." (conj parent-ids sibling-id))))
      (apply al/chord
        (map (fn [note]
               (first (build-score note attrs)))
          chord))
      (al/marker (str/join "." (conj parent-ids id)))]
     (assoc parent-attrs :sibling-id id)]))

(defmethod build-score :rest [[_ _] {:keys [length] :as parent-attrs}]
  [(al/pause (al/duration (al/note-length (/ 1 length))))
   parent-attrs])

(defmethod build-score :octave [[_ octave] parent-attrs]
  [nil (update parent-attrs :octave + octave)])

(defmethod build-score :default [[subscore-name] parent-attrs]
  (throw (Exception. (str subscore-name " not recognized"))))

(defn edna->alda [content]
  (first (build-score [:score (parse content)] default-attrs)))

(defn play!
  ([*score]
   (play! *score []))
  ([*score content]
   (when @*score
     (now/tear-down! *score))
   (reset! *score
     (deref
       (now/with-new-score
         (now/play! (edna->alda content)))))))

(defonce *my-score (atom nil))
#_
(play! *my-score
  (-> "examples/dueling-banjos.edn" slurp edn/read-string))
(def aeriths-theme
  [[:guitar {:tempo 64}
    {:length 1/8} #{:-d :-a :e :f#} :a
    {:length 1/2} #{:f# :+d}
    {:length 1/8} #{:-e :e :+c} :a
    {:length 1/2} #{:c :e}]
   [:guitar {:tempo 64}
    {:length 1/8} #{:-d :-a :e :f#} :a :+d :+c# :+e :+d :b :+c#
    {:length 1/2} #{:-e :c :a}
    {:length 1/2} #{:c :e}]
   [:guitar {:tempo 64}
    {:length 1/8} #{:-d :-a :e :f#} :a
    {:length 1/4} #{:f# :+d} :e :d
    {:tempo 140 :length 1/8} #{:-2a :c :+c} :a
    {:length 1/4} #{:c :e} :-b :-a]
   [:guitar {:tempo 74}
    {:length 1/8} :d :e #{:-d :d :f# :+d} :-a :d :-a :f# :-a
    :d :-a :-d :-a :d
    {:length 1/4} #{:d :f#}]
   [:guitar {:tempo 74}
    {:length 1/8} #{:-d :d} #{:-c# :e} #{:-2b :f#} :-f# :d :-b
    {:length 1/4} :d
    {:length 1/8} #{:-f# :-a :d :f#} :g #{:-2a :a} :-f# :c# :-a
    {:length 1/4} #{:c# :f#}]
   [:guitar {:tempo 74}
    {:length 1/8} #{:-g :d :g :b} :+c# #{:-2b :+d} :-d
    #{:-b :g :b} :-g
    {:length 1/4} :-b #{:-g :e :g}
    {:length 1/16} #{:-d :f# :a} :b
    {:length 1/8} #{:-a :a} :e :-a
    {:length 1/4} :f#]
   [:guitar {:tempo 74 :length 1/8}
    #{:-d :a} :f# #{:-2a :c#} :-f# :-a :c# #{:-2a :+c#}
    :-f# :-a :c# #{:-2a :b} :-f# :-a :f# #{:-2a :a} :-f#]
   [:guitar {:tempo 74 :length 1/8}
    #{:-a :b} :a #{:-2b :d} :-d :-a :-b #{:-d :b} :-a
    :-b :d #{:-d :a} :-a :-b :d
    #{:g :-d} :-a]])
#_
(play! *my-score (last aeriths-theme))

