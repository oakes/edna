(require
  '[edna.core :as edna]
  '[{{name}}.{{core-name}} :as c]
  '[cljs.build.api :as api]
  '[clojure.java.io :as io])

(def mp3-name "{{name}}.mp3")
(println "Building" mp3-name)
(edna/export! (c/read-music) {:type :mp3, :out (io/file mp3-name)})
(println "Build complete:" mp3-name)

(defn delete-children-recursively! [f]
  (when (.isDirectory f)
    (doseq [f2 (.listFiles f)]
      (delete-children-recursively! f2)))
  (when (.exists f) (io/delete-file f)))

(def out-file "resources/public/main.js")
(def out-dir "resources/public/main.out")

(println "Building main.js")
(delete-children-recursively! (io/file out-dir))
(api/build "src" {:main          '{{name}}.{{core-name}}
                  :optimizations :advanced
                  :output-to     out-file
                  :output-dir    out-dir
                  :infer-externs true})
(delete-children-recursively! (io/file out-dir))
(println "Build complete:" out-file)
(System/exit 0)
