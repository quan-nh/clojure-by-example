;; We can use channels to synchronize execution across goroutines.
;; Here’s an example of using a blocking receive to wait for a goroutine to finish. When waiting for multiple goroutines to finish, you may prefer to use a WaitGroup.
(ns channel-synchronization
  (:require [clojure.core.async :refer [chan go >!! <!!]]))

(defn worker
  "This is the function we’ll run in a goroutine.
   The done channel will be used to notify another goroutine that this function’s work is done."
  [done]
  (print "working...")
  (Thread/sleep 1000)
  (println "done")
  ; Send a value to notify that we’re done.
  (>!! done true))

;; Start a worker goroutine, giving it the channel to notify on.
(def done (chan 1))
(go (worker done))

;; Block until we receive a notification from the worker on the channel.
(<!! done)

;; If you removed the `(<!! done)` line from this program, the program would exit before the worker even started.
"working...done"
