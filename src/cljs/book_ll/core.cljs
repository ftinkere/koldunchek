(ns book-ll.core
  (:require
    [kee-frame.core :as kf]
    [re-frame.core :as rf]
    [ajax.core :as http]
    [book-ll.ajax :as ajax]
    [book-ll.routing :as routing]
    [book-ll.view :as view]))


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
  (fn [{:keys [db]} [n b]]
    {:db (assoc db :book b)}
    ))

(kf/reg-controller
  ::book-controller
  {:params (fn [route]
             (when (= :book (-> route :data :name))
               (-> route :path-params :id)))
   :start (fn [_ id] [::load-book id])
   })

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
