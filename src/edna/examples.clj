(ns edna.examples
  (:require [edna.core :as edna]))

(defmacro edna->data-uri [music]
  (edna/edna->data-uri music))
