(ns edna.examples
  (:require [edna.core :as edna]
            [clojure.edn :as edn]
            [clojure.java.io :as io]))

(defonce *state (atom nil))

(swap! *state (partial run! edna/stop!))

#_
(edna/export!
  (-> "examples/dueling-banjos.edn" slurp edn/read-string)
  {:type :midi, :out (io/file "banjos.mid")})

#_
(edna/export!
  (-> "examples/dueling-banjos.edn" slurp edn/read-string)
  {:type :wav, :out (io/file "banjos.wav")})

#_
(edna/export!
  (-> "examples/dueling-banjos.edn" slurp edn/read-string)
  {:type :mp3, :out (io/file "banjos.mp3")})

#_
(swap! *state conj
  (-> "examples/dueling-banjos.edn" slurp edn/read-string edna/play!))

#_
(swap! *state conj
  (-> "examples/aeriths-theme.edn" slurp edn/read-string edna/play!))

