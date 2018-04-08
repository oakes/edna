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

(def aeriths-theme (edna->data-uri
                     [:piano {:octave 4
                              :tempo 74}
                      [1/8 #{:-d :-a :e :f#} :a 1/2 #{:f# :+d}]
                      [1/8 #{:-e :e :+c} :a 1/2 #{:c :e}]
                      [1/8 #{:-d :-a :e :f#} :a :+d :+c# :+e :+d :b :+c#]
                      [1/2 #{:-e :c :a} 1/2 #{:c :e}]]))

(defexample aeriths-theme
  {:doc "A tune from Final Fantasy VII"
   :with-focus [focus [:piano {:octave 4
                               :tempo 74}
                       [1/8 #{:-d :-a :e :f#} :a 1/2 #{:f# :+d}]
                       [1/8 #{:-e :e :+c} :a 1/2 #{:c :e}]
                       [1/8 #{:-d :-a :e :f#} :a :+d :+c# :+e :+d :b :+c#]
                       [1/2 #{:-e :c :a} 1/2 #{:c :e}]]]
   :with-card card}
  (init-card card aeriths-theme)
  nil)

(def dueling-banjos (edna->data-uri
                      #{[:banjo {:octave 3} 1/16 :b :+c 1/8 :+d :b :+c :a :b :g :a]
                        [:guitar {:octave 3} 1/16 :r :r 1/8 :g :r :d :r :g :g :d]}))

(defexample dueling-banjos
  {:doc "Put different pieces in a set to make them play simultaneously"
   :with-focus [focus #{[:banjo {:octave 3} 1/16 :b :+c 1/8 :+d :b :+c :a :b :g :a]
                        [:guitar {:octave 3} 1/16 :r :r 1/8 :g :r :d :r :g :g :d]}]
   :with-card card}
  (init-card card dueling-banjos)
  nil)

(def intro-1-single-notes (edna->data-uri [:piano :c :d :e :f]))

(defexample intro-1-single-notes
  {:doc "A few notes, one at a time"
   :with-focus [focus [:piano :c :d :e :f]]
   :with-card card}
  (init-card card intro-1-single-notes)
  nil)

(def intro-2-octave (edna->data-uri [:piano :c :d {:octave 3} :c :d]))

(defexample intro-2-octave
  {:doc "By default you're on the 4th octave, but you can change it"
   :with-focus [focus [:piano :c :d {:octave 3} :c :d]]
   :with-card card}
  (init-card card intro-2-octave)
  nil)

(def intro-3-octave (edna->data-uri [:piano :c :-c :+2c]))

(defexample intro-3-octave
  {:doc "You can change an individual note's relative octave with a + or - inside the keyword.
   If you want to change by more than one octave, just put a number after."
   :with-focus [focus [:piano :c :-c :+2c]]
   :with-card card}
  (init-card card intro-3-octave)
  nil)

(def intro-4-length (edna->data-uri [:piano :c :d 1/2 :e :f]))

(defexample intro-4-length
  {:doc "By default, notes are 1/4 length, but you can change that as well"
   :with-focus [focus [:piano :c :d 1/2 :e :f]]
   :with-card card}
  (init-card card intro-4-length)
  nil)

(def intro-5-chord (edna->data-uri [:piano #{:c :d}]))

(defexample intro-5-chord
  {:doc "A chord is just notes in a set"
   :with-focus [focus [:piano #{:c :d}]]
   :with-card card}
  (init-card card intro-5-chord)
  nil)

