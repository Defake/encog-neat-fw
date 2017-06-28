(ns encog-neat-fw.core-test
  (:require [clojure.test :refer :all]
            [encog-neat-fw.core :as neat]
            [encog-neat-fw.data :as d]
            [encog-neat-fw.log :as l]
            [encog-neat-fw.training :as t]))

(set! *warn-on-reflection* true)

(deftest neat-fw-tests

  (testing "XOR dataset test"
    (let [dataset (d/dataset-basic [[0 0] [0 1] [1 0] [1 1]]
                                   [[0]   [1]   [1]   [0]])
          trainer (t/create-neat-trainer (neat/create-population 2 1 200)
                                         (t/score-counter :set dataset))]
      (let [logger (l/create-logger 10 "Error:" :error ", Epoch: " :epoch)]
        (t/train trainer :max-error 0.00001 logger))

      (let [net (t/get-best-network trainer)]
        (l/eval-network net dataset)
        (is (= (neat/compute net :int-round [0 0]) [0]))
        (is (= (neat/compute net :int-round [0 1]) [1]))
        (is (= (neat/compute net :int-round [1 0]) [1]))
        (is (= (neat/compute net :int-round [1 1]) [0])))))

;; Caution! This test may take too much time on weak computers
  (testing "Float even func (let's say 0.2 is even and 0.1 is not) test"
    (let [trainer (t/create-neat-trainer (neat/create-population 1 1 1000)
                                         (t/score-counter :function (fn [ann]
                                                                      (reduce #(if %2 % (inc %)) 0
                                                                              [(= (neat/compute ann :int-round [0]) [1])
                                                                               (= (neat/compute ann :int-round [0.1]) [0])
                                                                               (= (neat/compute ann :int-round [0.2]) [1])
                                                                               (= (neat/compute ann :int-round [0.3]) [0])
                                                                               (= (neat/compute ann :int-round [0.4]) [1])
                                                                               (= (neat/compute ann :int-round [0.5]) [0])
                                                                               (= (neat/compute ann :int-round [0.6]) [1])
                                                                               (= (neat/compute ann :int-round [0.7]) [0])
                                                                               (= (neat/compute ann :int-round [0.8]) [1])
                                                                               (= (neat/compute ann :int-round [0.9]) [0])
                                                                               (= (neat/compute ann :int-round [1]) [1])]))
                                                          false))]
      (let [logger (l/create-logger 200 "Error:" :error ", Epoch: " :epoch ", Message num: " (/ :epoch 200))]
        (while (< 1 (t/get-property :error trainer))
          (t/train trainer :iteration 100 logger)))

      (let [net (t/get-best-network trainer)]
        (is (= (neat/compute net :int-round [0]) [1]))
        (is (= (neat/compute net :int-round [1]) [1]))
        (is (= (neat/compute net :int-round [0.2]) [1]))
        (is (= (neat/compute net :int-round [0.7]) [0]))
        (is (= (neat/compute net :int-round [0.3]) [0]))))))
