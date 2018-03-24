(ns edna.core
  (:require [alda.lisp :as al]
            [alda.now :as now]
            [edna.parse :refer [parse parse-note]]
            [clojure.edn :as edn]
            [clojure.string :as str]))

(def default-attrs {:octave 4 :length 1/4 :parent-ids []})

(defmulti build-score (fn [val parent-attrs] (first val)))

(defmethod build-score :score [[_ {:keys [instrument subscores]}]
                               {:keys [sibling-id parent-ids] :as parent-attrs}]
  (let [id (inc (or sibling-id 0))
        attrs (if instrument
                (assoc parent-attrs :instrument instrument)
                parent-attrs)
        parts (first
                (reduce
                  (fn [[subscores attrs] subscore]
                    (let [attrs (assoc attrs :parent-ids
                                  (conj parent-ids id))
                          [subscore attrs] (build-score subscore attrs)]
                      [(conj subscores subscore) attrs]))
                  [[] attrs]
                  (vec subscores)))]
    [(if instrument
       (al/part (name instrument)
         (when sibling-id
           (al/at-marker (str/join "." (conj parent-ids sibling-id))))
         parts
         (al/marker (str/join "." (conj parent-ids id))))
       parts)
     (assoc parent-attrs :sibling-id id)]))

(defmethod build-score :attrs [[_ {:keys [note] :as attrs}] parent-attrs]
  (if note
    [(first (build-score [:note note] (merge parent-attrs attrs)))
     parent-attrs]
    [nil (merge parent-attrs attrs)]))

(defmethod build-score :note [[_ note] {:keys [octave length] :as parent-attrs}]
  (let [{:keys [note pitch octave-op octaves]} (parse-note note)
        note (keyword (str note))
        pitch (case pitch
                \# :sharp
                \= :flat
                :natural)
        octaves (or octaves
                    (if octave-op [\1] [\0]))
        octave-change (cond-> (Integer/valueOf (str/join octaves))
                              (= \- octave-op) (* -1))]
    [[(al/octave (+ octave octave-change))
      (al/note
        (al/pitch note pitch)
        (al/duration (al/note-length (/ 1 length))))]
     parent-attrs]))

(defmethod build-score :chord [[_ chord] parent-attrs]
  [(apply al/chord
     (map (fn [note]
            (first (build-score note parent-attrs)))
       chord))
   parent-attrs])

(defmethod build-score :octave [[_ octave] parent-attrs]
  [nil (update parent-attrs :octave + octave)])

(defmethod build-score :concurrent-score [[_ scores] parent-attrs])

(defmethod build-score :default [[subscore-name] parent-attrs]
  (throw (Exception. (str subscore-name " not recognized"))))

(defn edna->alda [content]
  (first (build-score [:score (parse content)] default-attrs)))

(def example (edn/read-string (slurp "examples/dueling-banjos.edn")))

;(now/play! (edna->alda example))

#_
(now/play!
  (edna->alda
    [[:guitar
      {:octave 4 :length 1/8} #{:d :-b :-g} #{:d :-b :-g}
      {:length 1/4} #{:d :-b :-g} #{:e :c :-g} #{:d :-b :-g}]]))

