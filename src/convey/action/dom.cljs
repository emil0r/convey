(ns convey.action.dom
  (:require [convey.action :refer [action!]]))

(defmethod action! :dom/prevent-default [_ _ _ ctx]
  (let [{:replicant/keys [^js js-event]} ctx]
    (.preventDefault js-event)))
(defmethod action! :dom/stop-propagation [_ _ _ ctx]
  (let[{:replicant/keys [^js js-event]} ctx]
    (.stopPropagation js-event)))
(defmethod action! :dom/set-input-text [_ args _ _]
  (set! (.-value (first args)) (second args)))
(defmethod action! :dom/focus-element [_ args _ _]
  (.focus (first args)))
