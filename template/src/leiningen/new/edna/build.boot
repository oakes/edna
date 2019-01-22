(set-env!
  :source-paths #{"src"}
  :resource-paths #{"resources"}
  :dependencies '[[org.clojure/clojure "1.10.0" :scope "provided"]
                  [adzerk/boot-cljs "2.1.5" :scope "test"]
                  [adzerk/boot-reload "0.6.0" :scope "test"]
                  [pandeiro/boot-http "0.8.3" :scope "test"
                   :exclusions [org.clojure/clojure]]
                  [javax.xml.bind/jaxb-api "2.3.0" :scope "test"] ; necessary for Java 9 compatibility
                  [org.clojure/clojurescript "1.10.439" :scope "test"]
                  [nightlight "RELEASE"]
                  [edna "1.6.0"]])

(require
  '[edna.core]
  '[{{name}}.core]
  '[nightlight.boot :refer [nightlight]]
  '[adzerk.boot-cljs :refer [cljs]]
  '[adzerk.boot-reload :refer [reload]]
  '[pandeiro.boot-http :refer [serve]]
  '[clojure.java.io :as io])

(deftask run []
  (comp
    (watch)
    (with-pass-thru _
      ({{name}}.core/-main))
    (nightlight :port 4000)))

(deftask build []
  (let [output (io/file "target" "{{name}}.mp3")]
    (.mkdir (.getParentFile output))
    (with-pass-thru _
      (edna.core/export!
        ({{name}}.core/read-music)
        {:type :mp3
         :out output})
      (println "Built" (.getCanonicalPath output)))))

(deftask run-cljs []
  (comp
    (serve :dir "target/public" :port 3000)
    (watch)
    (reload)
    (cljs
      :optimizations :none
      :compiler-options {:asset-path "main.out"})
    (target)
    (nightlight :port 4000 :url "http://localhost:3000")))

(deftask build-cljs []
  (comp
    (cljs :optimizations :advanced)
    (target)))

