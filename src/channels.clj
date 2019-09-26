;; Channels are the pipes that connect concurrent goroutines.
;; You can send values into channels from one goroutine and receive those values into another goroutine.
(ns channels
  (:require [clojure.core.async :refer [chan go >! <!!]]))

;; Create a new channel with `chan` function.
(def messages (chan))

;; Send a value into a channel using the `>!` syntax inside `go` blocks.
;; Here we send "ping" to the messages channel we made above, from a new goroutine.
(go (>! messages "ping"))

;; The `<!!` syntax receives a value from the channel in ordinary threads.
;; Here we'll receive the "ping" message we sent above and print it out.
(def msg (<!! messages))
(println msg) ;=> ping

;; When we run the program the "ping" message is successfully passed from one goroutine to another via our channel.

;; By default sends and receives block until both the sender and receiver are ready. This property allowed us to wait at the end of our program for the "ping" message without having to use any other synchronization.
