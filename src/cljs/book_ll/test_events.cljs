(ns book-ll.test-events
  (:require [reagent.core :as r]
            [re-frame.core :as rf]))

(rf/reg-event-db
  :assoc-test-ctx
  (fn [db [_ mp]]
    (if-let [test-ctx (:test-ctx db)]
      (update db :test-ctx swap! test-ctx merge mp)
      (assoc db :test-ctx (r/atom mp)))))
