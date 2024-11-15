(ns convey.action
  (:require [convey.logger :refer [console]]))


(defmulti action! (fn [action-name _action-args _db _ctx]
                    action-name))

(defmethod action! :dom/prevent-default [_ _ _ ctx]
  #?(:cljs (let [{:replicant/keys [^js js-event]} ctx]
             (.preventDefault js-event))))
(defmethod action! :dom/stop-propagation [_ _ _ ctx]
  #?(:cljs (let[{:replicant/keys [^js js-event]} ctx]
             (.stopPropagation js-event))))
(defmethod action! :db/assoc [_ args db _]
  (apply swap! db assoc args))
(defmethod action! :db/dissoc [_ args db _]
  (apply swap! db dissoc args))
(defmethod action! :db/assoc-in [_ [ks value] db _]
  (apply swap! db assoc-in ks value))
(defmethod action! :db/dissoc-in [_ ks db _]
  (apply swap! db update-in (butlast ks) dissoc (last ks)))
(defmethod action! :dom/set-input-text [_ args _ _]
  #?(:cljs (set! (.-value (first args)) (second args))))
(defmethod action! :dom/focus-element [_ args _ _]
  #?(:cljs (.focus (first args))))
(defmethod action! :default [action-name action-args _ _ ]
  (console :info "Convey: Unknown action" action-name action-args))
