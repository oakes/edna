(ns leiningen.new.edna
  (:require [leiningen.new.templates :as t]))

(defn edna
  [name & [package-name]]
  (let [render (t/renderer "edna")
        package-name (t/sanitize (t/multi-segment (or package-name name)))
        package-prefix (->> (.lastIndexOf package-name ".")
                            (subs package-name 0))
        main-ns (t/sanitize-ns package-name)
        data {:app-name name
              :name (t/project-name name)
              :package package-name
              :namespace main-ns
              :path (t/name-to-path main-ns)}]
    (t/->files data
      ["README.md" (render "README.md" data)]
      [".gitignore" (render "gitignore" data)]
      ["build.boot" (render "build.boot" data)]
      ["boot.properties" (render "boot.properties" data)]
      ["src/{{path}}.clj" (render "core.clj" data)])))

