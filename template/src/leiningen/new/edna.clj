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

(def initial-score
  "[:piano {:octave 4
         :tempo 74}
 
 1/8 #{:-d :-a :e :f#} :a 1/2 #{:f# :+d}
 1/8 #{:-e :e :+c} :a 1/2 #{:c :e}
 
 1/8 #{:-d :-a :e :f#} :a :+d :+c# :+e :+d :b :+c#
 1/2 #{:-e :c :a} 1/2 #{:c :e}]")

(defn edna-data [name]
  (let [project-name (sanitize-name name)
        core-name "core"]
    (when-not (seq project-name)
      (throw (Exception. (str "Invalid name: " name))))
    {:name project-name
     :core-name core-name
     :project_name (str/replace project-name "-" "_")
     :core_name (str/replace core-name "-" "_")
     :initial-score initial-score}))

(defn edna*
  [{:keys [project_name core_name] :as data}]
  (let [render (t/renderer "edna")
        music (str "(ns " (:name data) ".music)\n\n" (:initial-score data))]
    {"README.md" (render "README.md" data)
     ".gitignore" (render "gitignore" data)
     "deps.edn" (render "deps.edn" data)
     "figwheel-main.edn" (render "figwheel-main.edn" data)
     "dev.cljs.edn" (render "dev.cljs.edn" data)
     "dev.clj" (render "dev.clj" data)
     "prod.clj" (render "prod.clj" data)
     (str "src/" project_name "/music.clj") music
     (str "src/" project_name "/" core_name ".cljs") (render "core.cljs" data)
     (str "src/" project_name "/" core_name ".clj") (render "core.clj" data)
     "resources/public/index.html" (render "index.html" data)}))

(defn edna
  [name & _]
  (let [data (edna-data name)
        path->content (edna* data)]
    (apply t/->files data (vec path->content))))

