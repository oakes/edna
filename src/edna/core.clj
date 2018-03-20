(ns edna.core
  (:require [alda.lisp :as al]
            [edna.parse :refer [parse]]))

(defmulti build-score
  (fn []))

(defn score [content]
  (parse content))

