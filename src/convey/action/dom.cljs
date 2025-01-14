(ns convey.action.dom
  (:require [convey.action :refer [reg-action]]))

(reg-action
 :dom/prevent-default
 (fn [_ _ _ ctx]
   (let [{:replicant/keys [^js js-event]} ctx]
     (.preventDefault js-event))))
(reg-action
 :dom/stop-propagation
 (fn [_ _ _ ctx]
   (let[{:replicant/keys [^js js-event]} ctx]
     (.stopPropagation js-event))))
(reg-action
 :dom/set-input-text
 (fn [_ args _ _]
   (set! (.-value (first args)) (second args))))
(reg-action
 :dom/focus-element
 (fn [_ args _ _]
   (.focus (first args))))
