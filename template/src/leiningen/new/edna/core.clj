(ns {{name}}.{{core-name}}
  (:require [edna.core :as edna]))

(defn read-music []
  (load-file "src/{{project_name}}/music.clj"))

(defonce state (atom nil))

(defn -main []
  (swap! state edna/stop!)
  (reset! state (edna/play! (read-music))))

(defmacro build-for-cljs []
  (edna/edna->data-uri (read-music)))

