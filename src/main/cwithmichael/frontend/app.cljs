(ns cwithmichael.frontend.app
  (:require
   ["react" :as react]
   [clojure.string :as str]
   [reagent.dom :as rdom]))

(defn history [history]
  [:div.history (str/join history)])

(defn button [label handler]
  [:button.button {:on-click #(handler label)} label])

(defn keypad [button-press]
  [:div.keypad
   [:div.keypadRow
    (button "7" #(button-press "digit" %))
    (button "8" #(button-press "digit" %))
    (button "9" #(button-press "digit" %))
    (button "/" #(button-press "op" %))]
   [:div.keypadRow
    (button "4" #(button-press "digit" %))
    (button "5" #(button-press "digit" %))
    (button "6" #(button-press "digit" %))
    (button "*" #(button-press "op" %))]
   [:div.keypadRow
    (button "1" #(button-press "digit" %))
    (button "2" #(button-press "digit" %))
    (button "3" #(button-press "digit" %))
    (button "-" #(button-press "op" %))]
   [:div.keypadRow
    (button "0" #(button-press "digit" %))
    (button "clear" #(button-press "clear" %))
    (button "=" #(button-press "compute" %))
    (button "+" #(button-press "op" %))]])

(defn display [result]
  [:div.display result])

(defn compute [result & [op num & xs]]
  (case op
    "+" (recur (+ result num) xs)
    "-" (recur (- result num) xs)
    "/" (recur (/ result num) xs)
    "*" (recur (* result num) xs)
    result))

(defn compute-press [display  history]
  (let [history (conj history display)
        result (apply compute history)]
    result))

(defn calculator []
  (let [[queue set-history] (react/useState [])
        [display-value set-display] (react/useState 0)
        button-press (fn [variant val]
                       (case variant
                         "digit"  (set-display (long (str display-value val)))
                         "op" (do (set-history (conj queue display-value val))
                                  (set-display 0))
                         "compute" (let [computed-value (compute-press display-value queue)]
                                     (set-display computed-value)
                                     (set-history []))
                         "clear" (do (set-display "0")
                                     (set-history []))))]
    [:div.calculator
     [:h2.calculatorTitle "Basic Calculator"]
     [:f> history queue]
     [:f> display display-value]
     [:f> keypad #(button-press %1 %2)]]))

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(defn init []
  (rdom/render [:f> calculator] (js/document.getElementById "root")))
