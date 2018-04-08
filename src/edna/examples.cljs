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

(def intro-2-attributes (edna->data-uri [:piano :c :d {:octave 3} :c :d]))

(defexample intro-2-attributes
  {:doc "Hash maps let you change the attributes of everything that comes after them.
   Here are all the attributes you can change:
   
   :octave     - The octave (default is 4)
   :length     - The note length (default is 1/4)
   :tempo      - How fast or slow notes are played (default is 120)
   :pan        - How far left/right the note is panned in your speaker (default is 50)
   :quantize   - The percentage of a note's full duration that is heard (default is 90)
   :transpose  - Moves all notes up or down by a desired number of semitones (default is 0)
   :volume     - The volume (default is 100)
   :play?      - Whether or not to play (default is true)
   
   Read more about attributes here:
   https://github.com/alda-lang/alda/blob/master/doc/attributes.md"
   :with-focus [focus [:piano :c :d {:octave 3} :c :d]]
   :with-card card}
  (init-card card intro-2-attributes)
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
  {:doc "A shorthand way of changing the note length is simply to write the number."
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

