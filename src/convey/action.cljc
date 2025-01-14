(ns convey.action)


(defonce actions (atom {}))

(defn reg-action [action-name action-fn]
  (swap! actions assoc action-name action-fn)
  nil)
