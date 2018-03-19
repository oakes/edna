(ns edna.core
  (:require [clojure.spec.alpha :as s]
            [alda.lisp.instruments.midi]
            [alda.lisp.model.instrument :refer [*stock-instruments*]]))

(def dueling-banjos
  [[:guitar
    {:octave 3 :length 1/8} :b +1 :c
    {:length 1/4} :d -1 :b +1 :c -1 :a :b :g :a]
   [:banjo
    {:octave 3 :length 1/8} :b +1 :c
    {:length 1/4} :d -1 :b +1 :c -1 :a :b :g :a]
   [:guitar
    {:octave 3 :length 1/8} :b +1 :c
    {:length 1/4} :d -1 :b +1 :c -1 :a :b :g :a]
   [:guitar
    {:octave 3 :length 1} :d
    {:length 1/4} :g :g :a :b :g :b
    {:length 1} :a]
   
   [:banjo
    {:octave 3 :length 1/4} :g :g :a :b
    {:length 1} :g]
   
   [:guitar
    {:octave 2 :length 1/8} :g :g
    {:length 1/4} :g :a :b +1 :c :d :c -1
    {:length 1} :b]
   [:banjo
    {:octave 3 :length 1/8} :g :g
    {:length 1/4} :g :a :b +1 :c :d :c -1
    {:length 1} :b]
   [:guitar
    {:octave 2 :length 1/8} :g :g
    {:length 1/4} :g :a :b +1 :c :d :c -1
    {:length 1} :b]
   [:banjo
    {:octave 3 :length 1/8} :g :g
    {:length 1/4} :g :a :b +1 :c :d :c -1
    {:length 1} :b]
   [:guitar
    {:octave 3 :length 1/8} :g :g
    {:length 1/4} :g :a :b +1 :c :d :c -1
    {:length 1} :b]
   [:banjo
    {:octave 3 :length 1/8} :g :g
    {:length 1/4} :g :a :b +1 :c :d :c -1
    {:length 1} :b]
   
   [:guitar
    {:octave 4 :length 1/8} #{:d :-b :-g} #{:d :-b :-g}
    {:length 1/4} #{:d :-b :-g} #{:e :c :-g} #{:d :-b :-g}]
   [:banjo
    {:octave 4 :length 1/8} #{:d :-b :-g} #{:d :-b :-g}
    {:length 1/4} #{:d :-b :-g} #{:e :c :-g} #{:d :-b :-g}]
   [:guitar
    {:octave 4 :length 1/8} #{:d :-b :-g} #{:d :-b :-g}
    {:length 1/4} #{:d :-b :-g} #{:e :c :-g} #{:d :-b :-g}]
   [:banjo
    {:octave 4 :length 1/8} #{:d :-b :-g} #{:d :-b :-g}
    {:length 1/4} #{:d :-b :-g} #{:e :c :-g} #{:d :-b :-g}]
   
   [:guitar
    {:octave 2 :length 1/8} :b +1 :c
    {:length 1/4} :d -1 :b +1 :c -1 :a :b :g :a]
   [:banjo
    {:octave 3 :length 1/8} :b +1 :c
    {:length 1/4} :d -1 :b +1 :c -1 :a :b :g :a]
   
   #{[:banjo
      {:octave 3 :length 1/16} :b +1 :c
      {:length 1/8} :d -1 :b +1 :c -1 :a :b :g :a]
     [:guitar
      {:octave 3 :length 1/16} :r :r
      {:length 1/8} :g :r :d :r :g :g :d]}
   #{[:banjo
      {:octave 3 :length 1/16} :b +1 :c
      {:length 1/8} :d -1 :b +1 :c -1 :a :b :g :a]
     [:guitar
      {:octave 3 :length 1/16} :r :r
      {:length 1/8} :g :r :d :r :g :g :d]}
   #{[:banjo
      {:octave 3 :length 1/16} :b +1 :c
      {:length 1/8} :d -1 :b +1 :c -1 :a :b :g :a]
     [:guitar
      {:octave 3 :length 1/4} :g
      {:length 1/8} :a :b
      {:length 1/4} :g
      {:length 1/8} :a :d]}
   #{[:banjo
      {:octave 3 :length 1/16} :b +1 :c
      {:length 1/8} :d -1 :b +1 :c -1 :a :b :g :a]
     [:guitar
      {:octave 3 :length 1/4} :g
      {:length 1/8} :a :b
      {:length 1/4} :g
      {:length 1/8} #{:f :-a} :b
      {:length 1/4} :c]}])

(def instruments (->> *stock-instruments* keys (map keyword) set))

(def octave-operators #{\+ \-})

(def digits #{\0 \1 \2 \3 \4 \5 \6 \7 \8 \9})

(def notes #{\c \d \e \f \g \a \b})

(def pitches #{\# \= \_})

(s/def ::note (s/cat
                :octave-operator (s/? octave-operators)
                :octaves (s/* digits)
                :note notes
                :pitch (s/* pitches)))

(defn parse-note [note]
  (if-not (keyword? note)
    :clojure.spec.alpha/invalid
    (s/conform ::note (seq (name note)))))

(defn note? [x]
  (not= :clojure.spec.alpha/invalid (parse-note x)))

(s/def ::chord (s/coll-of note? :kind set?))

(s/def ::octave number?)
(s/def ::length number?)
(s/def ::attrs (s/keys :opt-un [::octave ::length]))

(s/def ::parts (s/* (s/alt
                      :note note?
                      :rest #{:r}
                      :octave integer?
                      :attrs ::attrs
                      :chord ::chord
                      :subsection (s/spec (s/cat
                                            :instrument (s/? instruments)
                                            :parts ::parts)))))

(s/def ::section (s/cat
                   :instrument instruments
                   :parts ::parts))

(s/def ::concurrent-sections (s/coll-of ::section :kind set?))

(s/def ::sections
  (s/coll-of (s/or
               :single ::section
               :concurrent ::concurrent-sections)))

(s/conform ::sections dueling-banjos)

