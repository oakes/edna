(set-env!
  :source-paths #{"src"}
  :dependencies '[[org.clojure/clojure "1.9.0" :scope "provided"]
                  [javax.xml.bind/jaxb-api "2.3.0" :scope "test"] ; necessary for Java 9 compatibility
                  [nightlight "RELEASE" :scope "test"]
                  [edna "0.1.0"]])

(require
  '[edna.core]
  '[nightlight.boot :refer [nightlight]])

(deftask run []
  (comp
    (wait)
    (nightlight :port 4000)))

