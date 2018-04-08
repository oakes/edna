(ns edna.examples
  (:require-macros [edna.examples :refer [edna->data-uri]]
                   [dynadoc.example :refer [defexample]]))

(defn ^:private init-card [card uri]
  (when (-> card .-childNodes .-length (not= 0))
    (throw (js/Error. "These examples can't be edited, because edna needs the JVM to generate music.")))
  (set! (.-textAlign (.-style card)) "center")
  (set! (.-lineHeight (.-style card)) "200px")
  (let [elem (js/document.createElement "audio")]
    (set! (.-src elem) uri)
    (set! (.-controls elem) true)
    (set! (.-verticalAlign (.-style elem)) "middle")
    (.appendChild card elem)))

(def example-1-single-notes (edna->data-uri [:piano :c :d :e :f]))

(defexample example-1-single-notes
  {:doc "A few notes, one at a time"
   :with-focus [focus [:piano :c :d :e :f]]
   :with-card card}
  (init-card card example-1-single-notes)
  nil)

(def example-2-chord (edna->data-uri [:piano #{:c :d}]))

(defexample example-2-chord
  {:doc "A chord"
   :with-focus [focus [:piano  #{:c :d}]]
   :with-card card}
  (init-card card example-2-chord)
  nil)

