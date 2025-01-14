(ns convey.action.db
  (:require [convey.action :refer [reg-action]]))

(reg-action
 :db/assoc
 (fn [_ args db _]
   (apply swap! db assoc args)))
(reg-action
 :db/dissoc
 (fn [_ args db _]
   (apply swap! db dissoc args)))
(reg-action
 :db/assoc-in
 (fn [_ [ks value] db _]
   (apply swap! db assoc-in ks value)))
(reg-action
 :db/dissoc-in
 (fn [_ ks db _]
   (apply swap! db update-in (butlast ks) dissoc (last ks))))
