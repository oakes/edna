(ns edna.parse
  (:require [clojure.spec.alpha :as s]
            [alda.lisp.instruments.midi]
            [alda.lisp.model.instrument :refer [*stock-instruments*]]))

(def instruments (->> *stock-instruments* keys (map keyword) set))

(def octave-operators #{\+ \-})

(def digits #{\0 \1 \2 \3 \4 \5 \6 \7 \8 \9})

(def notes #{\c \d \e \f \g \a \b})

(def pitches #{\# \= \_})

(s/def ::note-parts (s/cat
                      :octave-op (s/? octave-operators)
                      :octaves (s/* digits)
                      :note notes
                      :pitch (s/* pitches)))

(defn parse-note [note]
  (if-not (keyword? note)
    :clojure.spec.alpha/invalid
    (s/conform ::note-parts (seq (name note)))))

(defn note? [x]
  (not= :clojure.spec.alpha/invalid (parse-note x)))

(s/def ::note note?)
(s/def ::rest #{:r})

(s/def ::octave number?)
(s/def ::length number?)
(s/def ::tempo number?)
(s/def ::attrs (s/keys :opt-un [::octave ::length ::tempo ::note]))
(s/def ::note-attrs (s/keys :opt-un [::octave ::length ::tempo] :req-un [::note]))

(s/def ::chord (s/coll-of
                 (s/or
                   :note ::note
                   :rest ::rest
                   :attrs ::note-attrs)
                 :kind set?))

(s/def ::score (s/cat
                   :instrument (s/? instruments)
                   :subscores (s/* ::subscore)))

(s/def ::subscore (s/alt
                    :note ::note
                    :rest ::rest
                    :octave integer?
                    :attrs ::attrs
                    :chord ::chord
                    :concurrent-score (s/coll-of ::score :kind set?)
                    :score (s/spec ::score)))

(defn parse [content]
  (when-not (vector? content)
    (throw (Exception. "Input value must be a vector")))
  (let [res (s/conform ::score content)]
    (if (= res :clojure.spec.alpha/invalid)
      (throw (Exception. (s/explain-str ::score content)))
      res)))

