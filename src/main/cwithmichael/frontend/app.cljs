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

(defn operator-press [op display set-display history set-history]
  (set-history (conj history display op))
  (set-display 0))

(defn digit-press [digit display set-display]
  (set-display (long (str display digit))))

(defn compute [result & [op num & xs]]
  (case op
    "+" (recur (+ result num) xs)
    "-" (recur (- result num) xs)
    "/" (recur (/ result num) xs)
    "*" (recur (* result num) xs)
    result))

(defn compute-press [display set-display history set-history]
  (let [history (conj history display)
        result (apply compute history)]
    (set-display result)
    (set-history [])))

(defn clear-press [set-display set-history]
  (set-display "0")
  (set-history []))

(defn calculator []
  (let [[queue set-history] (react/useState [])
        [display-value set-display] (react/useState 0)
        button-press (fn [variant val]
                       (case variant
                         "digit"  (digit-press val display-value set-display)
                         "op" (operator-press val display-value set-display queue set-history)
                         "compute" (compute-press display-value set-display queue set-history)
                         "clear" (clear-press set-display set-history)))]
    [:div.calculator
     [:h2.calculatorTitle "Basic Calculator"]
     [:f> history queue]
     [:f> display display-value]
     [:f> keypad #(button-press %1 %2)]]))

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(defn init []
  (rdom/render [:f> calculator] (js/document.getElementById "root")))
