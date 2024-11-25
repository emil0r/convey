(ns convey.action
  (:require [convey.logger :refer [console]]))


(defmulti action! (fn [action-name _action-args _db _ctx]
                    action-name))

(defmethod action! :default [action-name _ _ _ ]
  (console :info "Convey: Unknown action" (pr-str action-name)))
