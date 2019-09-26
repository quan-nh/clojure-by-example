;; Closing a channel indicates that no more values will be sent on it.
;; This can be useful to communicate completion to the channel’s receivers.
(ns closing-channels
  (:require [clojure.core.async :refer [chan go-loop >! <! >!! <!! close!]]))

;; In this example we’ll use a jobs channel to communicate work to be done from the main script to a worker goroutine.
;; When we have no more jobs for the worker we’ll close the jobs channel.
(def jobs (chan 5))
(def done (chan))

;; Here’s the worker goroutine.
;; It repeatedly receives from jobs with `j (<! jobs)`.
;; The value will be `nil` if jobs has been closed and all values in the channel have already been received.
;; We use this to notify on done when we’ve worked all our jobs.
(go-loop []
         (if-let [j (<! jobs)]
           (do (println "received job" j)
               (recur))
           (do (println "received all jobs")
               (>! done true))))

;; This sends 3 jobs to the worker over the jobs channel, then closes it.
(dotimes [j 3]
  (>!! jobs j)
  (println "sent job" j))

(close! jobs)
(println "sent all jobs")

;; We await the worker using the synchronization approach we saw earlier.
(<!! done)

"sent job 0
received job 0
sent job 1
received job 1
sent job 2
received job 2
sent all jobs
received all jobs"
