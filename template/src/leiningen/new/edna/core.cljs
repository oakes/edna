(ns {{name}}.core
  (:require-macros [{{name}}.core :refer [build-for-cljs]]))

(defonce audio (js/document.createElement "audio"))
(set! (.-src audio) (build-for-cljs))
(.play audio)

