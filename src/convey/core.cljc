(ns convey.core
  (:require [clojure.walk :as walk]
            [convey.action]
            [convey.logger :refer [console]]
            [convey.enrichment :refer [enrich-actions enrichments]]
            [convey.router :as router]))


(def action! convey.action/action!)

(defmethod action! ::render [_ render-fn db _]
  (render-fn @db))

(defn sync? [actions]
  (true? (:sync (meta actions))))

(defonce dispatcher nil)

(defn init-event-handler [db render enrichments event-queue]
  (fn [replicant-data actions & [flush?]]
    (if flush?
      (router/flush! event-queue)
      (doseq [action actions]
        (let [ctx (assoc replicant-data :db db)
              [action-name & args] (enrich-actions ctx enrichments action)]
          ;; (console :debug "Enriched action" action-name)
          (if (or (sync? action)
                  (sync? actions))
            (action! action-name args db replicant-data)
            (router/push event-queue [action-name args db replicant-data])))))
    (when render
      (router/push event-queue [::render render db {}]))))

(defn dispatch [replicant-data actions]
  (dispatcher replicant-data actions))

#?(:cljs
   (defn dispatch-js [replicant-data actions]
     (dispatcher (walk/keywordize-keys (js->clj replicant-data)) (js->clj actions))))

(defn flush! []
  (dispatcher nil nil true))

(defn init [db render-fn enrichments]
  #?(:cljs (let [f (init-event-handler db render-fn enrichments (router/get-event-queue))]
             (set! dispatcher f))
     :clj  (let [f (init-event-handler db render-fn enrichments (router/get-event-queue))]
             (alter-var-root #'dispatcher (fn [_] f)))))
