(ns convey.core
  (:require [convey.action]
            [convey.logger :refer [console]]
            [convey.enrichment :refer [enrich-actions]]
            [convey.router :as router]))


(def action! convey.action/action!)

(defmethod action! ::render [_ render-fn state* _]
  (render-fn @state*))

(defn sync? [actions]
  (true? (:sync (meta actions))))

(defn prepare-state-default [state _ctx]
  state)

(defonce dispatcher nil)

(defn init-event-handler [db ctx render enrichments event-queue]
  (let [prepare-state (ctx :prepare-state prepare-state-default)]
    (fn [replicant-data actions & [flush?]]
      (if flush?
        (router/flush! event-queue)
        (doseq [action actions]
          (let [action-ctx (merge (assoc replicant-data :db db) ctx)
                [action-name & args] (enrich-actions action-ctx enrichments action)]
            ;; (console :debug "Enriched action" action-name)
            (if (or (sync? action)
                    (sync? actions))
              (action! action-name args db replicant-data)
              (router/push event-queue [action-name args db replicant-data])))))
      (when render
        (router/push event-queue [::render render (delay (prepare-state @db ctx))])))))

(defn dispatch [replicant-data actions]
  (dispatcher replicant-data actions))

#?(:cljs
   (defn dispatch-js [replicant-data actions]
     (dispatcher (walk/keywordize-keys (js->clj replicant-data)) (js->clj actions))))

(defn flush! []
  (dispatcher nil nil true))

(defn init [db ctx render-fn enrichments]
  #?(:cljs (let [f (init-event-handler db ctx render-fn enrichments (router/get-event-queue))]
             (set! dispatcher f))
     :clj  (let [f (init-event-handler db ctx render-fn enrichments (router/get-event-queue))]
             (alter-var-root #'dispatcher (fn [_] f)))))
