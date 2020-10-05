(ns book-ll.core
  (:require
    [reagent.core :as r]
    [kee-frame.core :as kf]
    [re-frame.core :as rf]
    [ajax.core :as http]
    [book-ll.ajax :as ajax]
    [book-ll.routing :as routing]
    [book-ll.view :as view]
    [book-ll.test-events]))


(rf/reg-event-fx
  ::load-about-page
  (constantly nil))

(kf/reg-controller
  ::about-controller
  {:params (constantly true)
   :start  [::load-about-page]})

(rf/reg-sub
  :docs
  (fn [db _]
    (:docs db)))

(kf/reg-chain
  ::load-home-page
  (fn [_ _]
    {:http-xhrio {:method          :get
                  :uri             "/api/docs"
                  :response-format (http/raw-response-format)
                  :on-failure      [:common/set-error]}})
  (fn [{:keys [db]} [_ docs]]
    {:db (assoc db :docs docs)}))

(kf/reg-controller
  ::home-controller
  {:params (constantly true)
   :start  [::load-home-page]})

(rf/reg-sub
  :book
  (fn [db _]
    (:book db)))

(kf/reg-chain
  ::load-book
  (fn [{:keys [db]} n]
    {:http-xhrio {:method          :get
                  :uri             (str "/api/book/" (first n))
                  :response-format (http/raw-response-format)
                  :on-failure      [:common/set-error]}
     :db (assoc db :book-n n)}
    )
  (fn [{:keys [db]} [_ b]]
    {:db (assoc db :book b)}
    ))

(kf/reg-controller
  ::book-controller
  {:params (fn [route]
             (when (= :book (-> route :data :name))
               (-> route :path-params :id)))
   :start (fn [_ id] [::load-book id])
   })


(def def-test-ctx {:cx 50 :cy 50 :radius 50 :fill "red" :colr {:r 10 :g 10 :b 10}})

#_(kf/reg-chain
  ::load-test-ctx
  (fn [{:keys [db]} _]
    {:db (assoc db :test-ctx (r/atom def-test-ctx))}
    ))

(rf/reg-sub
  :test-ctx
  (fn [db _]
    (:test-ctx db)))

(kf/reg-controller
  ::test-controller
  {:params (constantly true)
   :start [:assoc-test-ctx def-test-ctx]})

;; -------------------------
;; Initialize app
(defn ^:dev/after-load mount-components
  ([] (mount-components true))
  ([debug?]
   (rf/clear-subscription-cache!)
   (kf/start! {:debug?         (boolean debug?)
               :routes         routing/routes
               :hash-routing?  true
               :initial-db     {}
               :root-component [view/root-component]})))

(defn init! [debug?]
  (ajax/load-interceptors!)
  (mount-components debug?))
