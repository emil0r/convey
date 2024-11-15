(ns convey.zipper
  (:require [clojure.zip :as zip]))

#?(:clj  (defmulti branch? class))
#?(:cljs (defmulti branch? type))
(defmethod branch? :default [_] false)
#?(:clj  (defmethod branch? clojure.lang.IPersistentVector [v] true))
#?(:clj  (defmethod branch? clojure.lang.IPersistentMap [m] true))
#?(:clj  (defmethod branch? clojure.lang.IPersistentList [l] true))
#?(:clj  (defmethod branch? clojure.lang.ISeq [s] true))
#?(:cljs (defmethod branch? cljs.core/PersistentVector [v] true))
#?(:cljs (defmethod branch? cljs.core/PersistentArrayMap [m] true))
#?(:cljs (defmethod branch? cljs.core/List [l] true))
#?(:cljs (defmethod branch? cljs.core/IndexedSeq [s] true))
#?(:cljs (defmethod branch? cljs.core/LazySeq [s] true))
#?(:cljs (defmethod branch? cljs.core/Cons [s] true))


#?(:clj  (defmulti seq-children class))
#?(:cljs (defmulti seq-children type))
#?(:clj  (defmethod seq-children clojure.lang.IPersistentVector [v] v))
#?(:clj  (defmethod seq-children clojure.lang.IPersistentMap [m] (mapv identity m)))
#?(:clj  (defmethod seq-children clojure.lang.IPersistentList [l] l))
#?(:clj  (defmethod seq-children clojure.lang.ISeq [s] s))
#?(:cljs (defmethod seq-children cljs.core/PersistentVector [v] v))
#?(:cljs (defmethod seq-children cljs.core/PersistentArrayMap [m] (mapv identity m)))
#?(:cljs (defmethod seq-children cljs.core/List [l] l))
#?(:cljs (defmethod seq-children cljs.core/IndexedSeq [s] s))
#?(:cljs (defmethod seq-children cljs.core/LazySeq [s] s))
#?(:cljs (defmethod seq-children cljs.core/Cons [s] s))

#?(:clj  (defmulti make-node (fn [node children] (class node))))
#?(:cljs (defmulti make-node (fn [node children] (type node))))
#?(:clj  (defmethod make-node clojure.lang.IPersistentVector [v children] (vec children)))
#?(:clj  (defmethod make-node clojure.lang.IPersistentMap [m children] (into {} children)))
#?(:clj  (defmethod make-node clojure.lang.IPersistentList [_ children] children))
#?(:clj  (defmethod make-node clojure.lang.ISeq [node children] (apply list children)))
#?(:cljs (defmethod make-node cljs.core/PersistentVector [v children] (vec children)))
#?(:cljs (defmethod make-node cljs.core/PersistentArrayMap [m children] (into {} children)))
#?(:cljs (defmethod make-node cljs.core/List [_ children] children))
#?(:cljs (defmethod make-node cljs.core/IndexedSeq [node children] (apply list children)))
#?(:cljs (defmethod make-node cljs.core/LazySeq [node children] (apply list children)))
#?(:cljs (defmethod make-node cljs.core/Cons [node children] (apply list children)))

#?(:clj  (prefer-method make-node clojure.lang.IPersistentList clojure.lang.ISeq))
#?(:clj  (prefer-method branch? clojure.lang.IPersistentList clojure.lang.ISeq))
#?(:clj  (prefer-method seq-children clojure.lang.IPersistentList clojure.lang.ISeq))
#?(:cljs (prefer-method make-node cljs.core/List cljs.core/IndexedSeq))
#?(:cljs (prefer-method make-node cljs.core/List cljs.core/LazySeq))
#?(:cljs (prefer-method branch? cljs.core/List cljs.core/IndexedSeq))
#?(:cljs (prefer-method branch? cljs.core/List cljs.core/LazySeq))
#?(:cljs (prefer-method seq-children cljs.core/List cljs.core/IndexedSeq))
#?(:cljs (prefer-method seq-children cljs.core/List cljs.core/LazySeq))

(defn zipper [node]
  (zip/zipper branch? seq-children make-node node))
