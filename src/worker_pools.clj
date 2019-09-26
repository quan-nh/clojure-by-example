;; In this example we’ll look at how to implement a worker pool using goroutines and channels.
(ns worker-pools
  (:require [clojure.core.async :refer [chan go-loop >! <! >!! <!! put! close!]]))

(defn worker
  "Here’s the worker, of which we’ll run several concurrent instances.
   These workers will receive work on the jobs channel and send the corresponding results on results.
   We’ll sleep a second per job to simulate an expensive task."
  [id jobs results]
  (go-loop []
           (when-let [j (<! jobs)]
             (println "worker" id "started  job" j)
             (Thread/sleep 1000)
             (println "worker" id "finished job" j)
             (>! results (* j 2))
             (recur))))

;; In order to use our pool of workers we need to send them work and collect their results.
;; We make 2 channels for this.
(def jobs (chan 100))
(def results (chan 100))

;; This starts up 3 workers, initially blocked because there are no jobs yet.
(dotimes [w 3] (worker w jobs results))

;; Here we send 5 jobs and then close that channel to indicate that’s all the work we have.
(dotimes [j 5] (put! jobs j))
(close! jobs)

;; Finally we collect all the results of the work.
;; This also ensures that the worker goroutines have finished.
;; An alternative way to wait for multiple goroutines is to use a WaitGroup.
(dotimes [_ 5] (<!! results))
