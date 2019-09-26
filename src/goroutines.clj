;; A goroutine is a lightweight thread of execution.
(ns goroutines
  (:require [clojure.core.async :refer [go]]))

(defn f [from]
  (doseq [i (range 3)]
    (println from ":" i)))

;; Suppose we have a function call `(f s)`.
;; Here’s how we’d call that in the usual way, running it synchronously.
(f "direct")

;; To invoke this function in a goroutine, use `(go (f s))`.
;; This new goroutine will execute concurrently with the calling one.
(go (f "goroutine"))

;; You can also start a goroutine for an anonymous function call.
(go ((fn [msg]
      (println msg)) "going"))

;; Our two function calls are running asynchronously in separate goroutines now.
;; Wait for them to finish (for a more robust approach, use a WaitGroup).
(Thread/sleep 1000)
(println "done")
