;; _Timeouts_ are important for programs that connect to external resources or that otherwise need to bound execution time.
;; Implementing timeouts in Clojure is easy and elegant thanks to channels and alts.
(ns timeouts
  (:require [clojure.core.async :refer [chan go >! alt!! timeout]]))

;; For our example, suppose we’re executing an external call that returns its result on a channel c1 after 2s.
;; Note that the channel is buffered, so the send in the goroutine is nonblocking.
;; This is a common pattern to prevent goroutine leaks in case the channel is never read.
(def c1 (chan 1))
(go (do (Thread/sleep 2000)
        (>! c1 "result 1")))

;; Here’s the select implementing a timeout.
;; `timeout` creates a channel that waits for a specified ms, then closes.
;; Since `alt` proceeds with the first receive that’s ready, we’ll take the timeout case if the operation takes more than the allowed 1s.
(alt!!
  c1 ([res] (println res))
  (timeout 1000) (println "timeout 1"))

;; If we allow a longer timeout of 3s, then the receive from c2 will succeed and we’ll print the result.
(def c2 (chan 1))
(go (do (Thread/sleep 2000)
        (>! c2 "result 2")))

(alt!!
  c2 ([res] (println res))
  (timeout 3000) (println "timeout 2"))

;; Running this program shows the first operation timing out and the second succeeding.
"timeout 1"
"result 2"
