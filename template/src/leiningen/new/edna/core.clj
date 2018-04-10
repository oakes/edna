(ns {{name}}.core
  (:require [edna.core :as edna]))

(def music
  [:piano {:octave 4
           :tempo 74}
   
   1/8 #{:-d :-a :e :f#} :a 1/2 #{:f# :+d}
   1/8 #{:-e :e :+c} :a 1/2 #{:c :e}
   
   1/8 #{:-d :-a :e :f#} :a :+d :+c# :+e :+d :b :+c#
   1/2 #{:-e :c :a} 1/2 #{:c :e}])

(defonce state (atom nil))

(defn -main []
  (swap! state edna/stop!)
  (reset! state (edna/play! music)))

; for quick development, run this project with `boot run`,
; turn on the instaREPL, and uncomment this line:

;(-main)

; to build your music for the web,
; just run this project with `boot run-cljs`
; and go to http://localhost:3000

(defmacro build-for-cljs []
  (edna/edna->data-uri music))

