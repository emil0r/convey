(ns convey.interop
  (:import [java.util.concurrent Executor Executors]))

(defonce ^:private executor (Executors/newSingleThreadExecutor))

(defn next-tick [f]
(let [bound-f (bound-fn [& args] (apply f args))]
  (.execute ^Executor executor bound-f))
nil)

(def empty-queue clojure.lang.PersistentQueue/EMPTY)
