(ns leiningen.new.edna
  (:require [leiningen.new.templates :as t]
            [clojure.string :as str]))

(defn sanitize-name [s]
  (as-> s $
        (str/trim $)
        (str/lower-case $)
        (str/replace $ "'" "")
        (str/replace $ #"[^a-z0-9]" " ")
        (str/split $ #" ")
        (remove empty? $)
        (str/join "-" $)))

(defn edna-data [name]
  (let [sanitized-name (sanitize-name name)]
    (when-not (seq sanitized-name)
      (throw (Exception. (str "Invalid name: " name))))
    {:name sanitized-name
     :dir (str/replace sanitized-name "-" "_")}))

(defn edna*
  [data]
  (let [render (t/renderer "edna")]
    {"README.md" (render "README.md" data)
     ".gitignore" (render "gitignore" data)
     "build.boot" (render "build.boot" data)
     "boot.properties" (render "boot.properties" data)
     "src/music.clj" (render "music.clj" data)
     (str "src/" (:dir data) "/core.cljs") (render "core.cljs" data)
     (str "src/" (:dir data) "/core.clj") (render "core.clj" data)
     "resources/public/index.html" (render "index.html" data)
     "resources/public/main.cljs.edn" (render "main.cljs.edn" data)}))

(defn edna
  [name & _]
  (let [data (edna-data name)
        path->content (edna* data)]
    (apply t/->files data (vec path->content))))

