(require
  '[figwheel.main :as figwheel]
  '[dynadoc.core :as dynadoc])

(dynadoc/start {:port 5000, :dedupe-pref :cljs})
(figwheel/-main "--build" "dev")

