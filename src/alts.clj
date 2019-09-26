;; One killer feature for channels over queues is the ability to wait
;; on many channels at the same time (like a socket select). This is
;; done with `alts!!` (ordinary threads) or `alts!` in go blocks.
(ns alts
  (:require [clojure.core.async :refer [chan go >! alts!!]]))

;; For our example we’ll select across two channels.
(def c1 (chan))
(def c2 (chan))

;; Each channel will receive a value after some amount of time,
;; to simulate e.g. blocking RPC operations executing in concurrent goroutines.
(go (do (Thread/sleep 1000)
        (>! c1 "one")))
(go (do (Thread/sleep 2000)
        (>! c2 "two")))

;; We’ll use alts to await both of these values simultaneously,
;; printing each one as it arrives.
(time
  (dotimes [_ 2]
    (let [[msg _] (alts!! [c1 c2])]
      (println "received" msg))))

;; We receive the values "one" and then "two" as expected.
"received one"
"received two"

;; Note that the total execution time is only ~2 seconds since both the 1 and 2 second Sleeps execute concurrently.
"Elapsed time: 2001.457899 msecs"
