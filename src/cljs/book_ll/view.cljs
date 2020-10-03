(ns book-ll.view
  (:require
    [kee-frame.core :as kf]
    [markdown.core :refer [md->html]]
    [reagent.core :as r]
    [re-frame.core :as rf]))

(defn nav-link [title page]
  [:a.navbar-item
   {:href   (kf/path-for [page])
    :class (when (= page @(rf/subscribe [:nav/page])) "is-active")}
   title])

(defn navbar []
  (r/with-let [expanded? (r/atom false)]
    [:nav.navbar.bg-primary.is-info>div.container
     [:div.navbar-brand
      [:a.navbar-item {:href "/" :style {:font-weight :bold}} "Ать"]
      [:span.navbar-burger.burger
       {:data-target :nav-menu
        :on-click #(swap! expanded? not)
        :class (when @expanded? :is-active)}
       [:span][:span][:span]]]
     [:div#nav-menu.navbar-menu
      {:class (when @expanded? :is-active)}
      [:div.navbar-start
       [nav-link "Старт" :home]
       (-> (nav-link "Книга" :book) (assoc-in [1 :href] "#/book/1"))
       [nav-link "О" :about]]]]))

(defn about-page []
  [:section.section>div.container>div.content
   [:img {:src "/img/warning_clojure.png"}]])

(defn home-page []
  [:section.section.avi>div.container>div.content
   (when-let [docs @(rf/subscribe [:docs])]
     [:div {:dangerouslySetInnerHTML {:__html (md->html docs)}}])])

(defn book-page []
  [:section.section.book>div.container>div.content
   (when-let [book @(rf/subscribe [:book])]
     [:div {:dangerouslySetInnerHTML {:__html (md->html book)}}])]
  )

(defn root-component []
  [:div
   [navbar]
   [kf/switch-route (fn [route] (-> route :data :name))
    :home home-page 
    :about about-page
    :book book-page
    nil [:div "Пока идёт загрузка, подожди. Или сообщи о том что она не идёт"]]])


