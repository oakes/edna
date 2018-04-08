(ns edna.examples
  (:require [reagent.core :as r])
  (:require-macros [edna.examples :refer [edna->data-uri]]
                   [dynadoc.example :refer [defexample]]))

(defn ^:private init-card [card uri]
  (when (-> card .-childNodes .-length (not= 0))
    (throw (js/Error. "These examples can't be edited, because edna needs the JVM to generate music.")))
  (set! (.-textAlign (.-style card)) "center")
  (reagent.core/render-component [:audio {:src uri :controls true}] card))

(def example-1 (edna->data-uri [:piano :c]))

(defexample example-1
  {:doc "Hit middle c on a piano"
   :with-focus [focus [:piano :c]]
   :with-card card}
  (init-card card example-1)
  nil)

