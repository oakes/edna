(ns edna.alda
  (:require [alda.lisp.instruments.midi]
            [alda.lisp.model.instrument :refer [*stock-instruments*]]))

(defmacro get-instruments []
  (->> *stock-instruments* keys (map keyword) set))

