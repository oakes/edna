(ns edna.examples
  (:require [reagent.core :as r])
  (:require-macros [edna.examples :refer [edna->data-uri]]
                   [dynadoc.example :refer [defexample]]))

(def example-1 (edna->data-uri [:piano :c]))

(defexample example-1
  {:doc "Hit middle c on a piano"
   :with-focus [focus [:piano :c]]
   :with-card card}
  (set! (.-textAlign (.-style card)) "center")
  (reagent.core/unmount-component-at-node card)
  (reagent.core/render-component [:audio {:src example-1 :controls true}] card)
  nil)

