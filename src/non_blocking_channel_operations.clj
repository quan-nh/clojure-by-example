;; Basic sends and receives on channels are blocking.
;; However, we can use `alt` with a default clause to implement non-blocking sends, receives, and even non-blocking multi-way selects.
(ns non-blocking-channel-operations
  (:require [clojure.core.async :refer [chan go >! alt!!]]))

(def messages (chan))
(def signals (chan))

;; Here’s a non-blocking receive.
;; If a value is available on messages then select will take the <-messages case with that value.
;; If not it will immediately take the default case.
(alt!!
  messages ([msg] (println "received message" msg))
  :default (println "no message received"))

;; A non-blocking send works similarly.
;; Here msg cannot be sent to the messages channel, because the channel has no buffer and there is no receiver.
;; Therefore the default case is selected.
(def msg "hi")
(alt!!
  [[messages msg]] (println "sent message" msg)
  :default (println "no message sent"))

;; We can use multiple cases above the default clause to implement a multi-way non-blocking select.
;; Here we attempt non-blocking receives on both messages and signals.
(alt!!
  messages ([msg] (println "received message" msg))
  signals  ([sig] (println "received signal" sig))
  :default (println "no activity"))

"no message received"
"no message sent"
"no activity"
