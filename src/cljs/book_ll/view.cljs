(ns book-ll.view
  (:require
    [kee-frame.core :as kf]
    [markdown.core :refer [md->html]]
    [reagent.core :as r]
    [re-frame.core :as rf]
    [goog.events :as e])
  (:import [goog.events EventType]))

(defn nav-link [title page]
  [:a.navbar-item
   {:href  (kf/path-for [page])
    :class (when (= page @(rf/subscribe [:nav/page])) "is-active")}
   title])

(defn navbar []
  (r/with-let [expanded? (r/atom false)]
              [:nav.navbar.bg-primary.is-info>div.container
               [:div.navbar-brand
                [:a.navbar-item {:href "/" :style {:font-weight :bold}} "Ать"]
                [:span.navbar-burger.burger
                 {:data-target :nav-menu
                  :on-click    #(swap! expanded? not)
                  :class       (when @expanded? :is-active)}
                 [:span] [:span] [:span]]]
               [:div#nav-menu.navbar-menu
                {:class (when @expanded? :is-active)}
                [:div.navbar-start
                 [nav-link "Старт" :home]
                 (-> (nav-link "Книга" :book) (assoc-in [1 :href] "#/book/1"))
                 [nav-link "Ttest" :test]
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

(defn range-comp [atom min max title]
  [:div.form-group
   [:label {:for title} title [:small @atom]]
   [:input.form-control {:type      "range" :name title :min min :max max :value @atom
                         :on-change (fn [e] (reset! atom (-> e .-target .-value)))}]
   ]
  )

(defn rgb [r g b]
  (str "rgb(" r "," g "," b ")"))

(defn move-hand [atom]
  (fn [evt]
    (when (:movable? @atom)
      (let [svg (js/document.getElementById "svg")
            bnd (.getBoundingClientRect svg)]

        (swap! atom assoc :cx (- (.-clientX evt) (.-x bnd)))
        (swap! atom assoc :cy (- (.-clientY evt) (.-y bnd)))
        ))))

(defn move-all
  ([atom]
   (fn [evt]
     ((move-hand atom) evt)))
  ([& atoms]
   (fn [evt]
     (for [atom atoms]
       ((move-hand atom) evt)))))

(defn test-page []
  [:section.section.tst>div.container>div.content
   (when-let [ctx @(rf/subscribe [:test-ctx])]

     (let [cx (r/cursor ctx [:cx])
           cy (r/cursor ctx [:cy])
           rad (r/cursor ctx [:radius])
           fil (r/cursor ctx [:fill])
           r (r/cursor ctx [:colr :r])
           g (r/cursor ctx [:colr :g])
           b (r/cursor ctx [:colr :b])]
       ;;(js/console.log ctx)
       [:div
        [range-comp cx 0 720 "X coordinate "]
        [range-comp cy 0 480 "Y coordinate "]
        [range-comp rad 0 500 "Radius "]
        [range-comp r 0 255 "Red "]
        [range-comp g 0 255 "Green "]
        [range-comp b 0 255 "Blue "]
        [:svg#svg
         {:width         720 :height 480 :style {:outline "2px solid red" :background-color "#000"}
          :on-mouse-move (move-all ctx)
          }
         [:circle {:cy            @cy :cx @cx :r @rad :style {:fill (rgb @r @g @b)}
                   :on-mouse-down (fn [_] (swap! ctx assoc :movable? true))
                   :on-mouse-up   (fn [_] (swap! ctx assoc :movable? false))
                   }]
         ]
        ])
     )
   ])

(defn root-component []
  [:div
   [navbar]
   [kf/switch-route (fn [route] (-> route :data :name))
    :home home-page
    :about about-page
    :book book-page
    :test test-page
    nil [:div "Пока идёт загрузка, подожди. Или сообщи о том что она не идёт"]]])


