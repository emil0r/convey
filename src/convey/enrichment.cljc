(ns convey.enrichment
  (:require [clojure.zip :as zip]
            [convey.logger :refer [console]]
            [convey.zipper :refer [zipper]]))

#?(:cljs (defn- get-event-value [e]
           (try
             (-> e .-target .-value)
             (catch js/Error _
               (try
                 (-> e .-detail)
                 (catch js/Error _
                   nil))))))

#?(:cljs (defn enrich-event-value [{:replicant/keys [^js js-event]} node loc]
           (if js-event
             (zip/replace loc (get-event-value js-event))
             node)))

#?(:cljs (defn enrich-dom-node [{:replicant/keys [node]} node' loc]
           (if node
             (zip/replace loc node)
             node')))

(defn enrich-db-get [{:keys [db]} node loc]
  (let [parent (-> loc zip/up)
        parent-node (-> parent zip/node)]
    (if (and (vector? parent-node)
             (keyword? (second parent-node)))
      (zip/replace parent (get @db (second parent-node)))
      (do
        (console :error ":db/get assumes a vector of two keywords like so: [:db/get :db-key-here]. The current value is ", parent-node)
        loc))))

(def enrichments
  {:db/get enrich-db-get
   #?@(:cljs [:dom/node enrich-dom-node])
   #?@(:cljs [:event.target/value enrich-event-value])})

(defn enrich-protected [_ _node loc']
  (loop [loc (-> loc' zip/up zip/remove)
         siblings (zip/rights loc')]
    (let [[sibling & siblings] siblings]
      (if (nil? sibling)
        ;; when we're done we move past the data that was protected
        (zip/next loc)
        (recur (-> loc (zip/insert-right sibling) zip/right) siblings)))))


(defn- skip-loc [next-loc]
  (cond (some? (-> next-loc zip/right))
        (recur (-> next-loc zip/right))

        (some? (-> next-loc zip/up zip/right))
        (recur (-> next-loc zip/up zip/right))

        :else
        (zip/root next-loc)))

(defn enrich-actions [ctx enrichments actions]
  (try
    (loop [loc (zipper actions)]
      (let [next-loc (zip/next loc)]
        (if (zip/end? next-loc)
          (zip/root loc)
          (let [next-node (zip/node next-loc)]
            (cond (vector? next-node)
                  (recur next-loc)

                  (= :oiiku/protected next-node)
                  (recur (enrich-protected ctx next-node next-loc))

                  (keyword? next-node)
                  (if (enrichments next-node)
                    (recur ((enrichments next-node) ctx next-node next-loc))
                    (recur next-loc))

                  (map? next-node)
                  (skip-loc next-loc)

                  :else
                  (recur next-loc))))))
    (catch #?(:cljs js/Error :clj Exception e) e
      (console :error e)
      nil)))

(defn protect-actions-from-enrichment [actions]
  [:convey/protected actions])
