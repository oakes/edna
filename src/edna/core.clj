(ns edna.core
  (:require [alda.lisp :as al]
            [alda.now :as now]
            [edna.parse :refer [parse]]
            [clojure.edn :as edn]))

(def default-attrs {:octave 4 :length 1/4})

(defmulti build-score (fn [val parent-attrs] (first val)))

(defmethod build-score :score [[_ {:keys [instrument subscores]}] parent-attrs]
  (let [attrs (if instrument
                (assoc parent-attrs :instrument instrument)
                parent-attrs)]
    [(first
       (reduce
         (fn [[subscores attrs] subscore]
           (let [[subscore attrs] (build-score subscore attrs)]
             [(conj subscores subscore) attrs]))
         [[] attrs]
         subscores))
     parent-attrs]))

(defmethod build-score :default [[subscore-name] parent-attrs]
  (throw (Exception. (str subscore-name " not recognized"))))

(defn edna->alda [content]
  (first (build-score [:score (parse content)] default-attrs)))

(defn example []
  (edn/read-string (slurp "examples/dueling-banjos.edn")))

