(ns edna.parse
  (:require [clojure.spec.alpha :as s]
            [expound.alpha :as expound]
            #?(:clj [edna.alda :refer [get-instruments]]))
  #?(:cljs (:require-macros [edna.alda :refer [get-instruments]])))

(def instruments (get-instruments))

(def octave-operators #{\+ \-})

(def digits #{\0 \1 \2 \3 \4 \5 \6 \7 \8 \9})

(def notes #{\c \d \e \f \g \a \b})

(def accidentals #{\# \= \_})

(def accidental->keyword
  {\# :sharp
   \= :flat
   \_ :natural})

(s/def ::note-parts (s/cat
                      :octave-op (s/? octave-operators)
                      :octaves (s/* digits)
                      :note notes
                      :accidental (s/? accidentals)))

(defn parse-note [note]
  (if-not (keyword? note)
    ::s/invalid
    (s/conform ::note-parts (seq (name note)))))

(defn note? [x]
  (not= ::s/invalid (parse-note x)))

(s/def ::note note?)
(s/def ::rest #{:r})

(s/def ::octave integer?)
(s/def ::length number?)
(s/def ::tempo integer?)
(s/def ::pan #(<= 0 % 100))
(s/def ::quantize #(<= 0 % 100))
(s/def ::transpose integer?)
(s/def ::volume #(<= 0 % 100))
(s/def ::play? boolean?)
(s/def ::key-signature (s/coll-of ::note :kind set?))
(s/def ::attrs (s/keys :opt-un [::note ::octave ::length ::tempo
                                ::pan ::quantize ::transpose
                                ::volume ::play? ::key-signature]))

(s/def ::chord (s/coll-of
                 (s/or
                   :note ::note
                   :rest ::rest
                   :attrs (s/merge ::attrs (s/keys :req-un [::note])))
                 :kind set?))

(s/def ::score (s/cat
                   :instrument (s/? instruments)
                   :subscores (s/* ::subscore)))

(s/def ::subscore (s/or
                    :note ::note
                    :rest ::rest
                    :length ::length
                    :attrs ::attrs
                    :chord ::chord
                    :concurrent-score (s/coll-of ::score :kind set?)
                    :score (s/spec ::score)))

(defn parse [content]
  (let [res (s/conform ::subscore content)]
    (if (= res ::s/invalid)
      (throw (#?(:clj Exception. :cljs js/Error.) (expound/expound-str ::subscore content)))
      res)))

