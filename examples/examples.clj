(ns edna.examples
  (:require [edna.core :as edna]
            [clojure.edn :as edn]
            [clojure.java.io :as io]
            [alda.now :as now]
            [alda.sound :as sound]
            [alda.sound.midi :as midi]))

(defonce *state (atom nil))

(swap! *state (partial run! edna/stop!))

;(-> "examples/dueling-banjos.edn" slurp edn/read-string (edna/export! (io/file "banjos.mid")))

#_
(swap! *state conj
  (-> "examples/dueling-banjos.edn" slurp edn/read-string edna/play!))

#_
(swap! *state conj
  (-> "examples/aeriths-theme.edn" slurp edn/read-string edna/play!))

