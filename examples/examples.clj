(ns edna.examples
  (:require [edna.core :as edna]
            [clojure.edn :as edn]))

(defonce *state (atom nil))

(swap! *state (partial run! edna/stop!))

#_
(swap! *state conj
  (-> "examples/dueling-banjos.edn" slurp edn/read-string edna/play!))

#_
(swap! *state conj
  (-> "examples/aeriths-theme.edn" slurp edn/read-string edna/play!))

