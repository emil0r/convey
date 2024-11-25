(ns convey.action.db
  (:require [convey.action :refer [action!]]))

(defmethod action! :db/assoc [_ args db _]
  (apply swap! db assoc args))
(defmethod action! :db/dissoc [_ args db _]
  (apply swap! db dissoc args))
(defmethod action! :db/assoc-in [_ [ks value] db _]
  (apply swap! db assoc-in ks value))
(defmethod action! :db/dissoc-in [_ ks db _]
  (apply swap! db update-in (butlast ks) dissoc (last ks)))
