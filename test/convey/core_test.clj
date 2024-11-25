(ns convey.core-test
  (:require [convey.core :as sut]
            [clojure.test :as t]))

(defonce catch (atom {}))
(defn reset-dispatch! []
  (alter-var-root #'sut/dispatcher (fn [_] nil)))

(t/use-fixtures
  :each
  (fn [f]
    (reset-dispatch!)
    (reset! catch {})
    (f)))

(defmethod sut/action! ::test [_ args db _]
  (swap! db assoc ::test args))


(defn render [state]
  (reset! catch (::test state))
  (println ::rendered (::test state)))


(t/deftest convey-init-test
  (let [db (atom {})]
    (t/testing "init of convey"
      (t/is (nil? sut/dispatcher))
      (sut/init db {} render {})

      (t/is (some? sut/dispatcher))

      (sut/dispatch {} [[::test :my :args]])
      (Thread/sleep 500)

      (t/is (= (::test @db)
               [:my :args]))
      (t/is (= @catch
               [:my :args])))))
