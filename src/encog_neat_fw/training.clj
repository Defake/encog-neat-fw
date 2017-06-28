(ns encog-neat-fw.training
  (:import [org.encog.ml CalculateScore]
           [org.encog Encog]
           [org.encog.neural.neat NEATPopulation NEATUtil]
           [org.encog.neural.networks.training TrainingSetScore]
           [org.encog.util.simple EncogUtility]))

;;  Score counting

(defn- implement-CalculateScore
  "This method was stealed from Enclog library =) <3
   Consumer convenience for implementing the CalculateScore interface which is needed for genetic and simulated annealing training."
  ([minimize? single-threaded? eval-fn]
   (reify CalculateScore
     (calculateScore  [this n] (eval-fn n))
     (shouldMinimize  [this] minimize?)
     (requireSingleThreaded [this] single-threaded?)))
  ([eval-fn]
   (implement-CalculateScore true false eval-fn)))

(defmulti score-counter (fn [counter-type params & _] counter-type))

(defmethod score-counter :set [_ ^org.encog.ml.data.basic.BasicMLDataSet training-set & _]
  (TrainingSetScore. training-set))

(defmethod score-counter :function [_ fitness-function & [single-threaded?]]
  (implement-CalculateScore true (if (nil? single-threaded?) true single-threaded?) fitness-function))

;; NEAT training

(defn create-neat-trainer [population score-counter]
  (NEATUtil/constructNEATTrainer population score-counter))

(defmulti train (fn [trainer train-type param & logger] train-type))

(defmethod train :iteration [trainer _ iterations & [logger]]
  (let [logger (if-let [l logger] l (fn [t]))]
    (dotimes [n iterations]
      (.iteration trainer)
      (logger trainer))))

(defmethod train :max-error [trainer _ max-error & [logger]]
  (let [logger (if-let [l logger] l (fn [t]))]
    (while (< max-error (.getError trainer))
      (.iteration trainer)
      (logger trainer))))

(defn get-best-network [trainer]
  (-> trainer (.getCODEC) (.decode (.getBestGenome trainer))))

;; Retrieving different trainer properties

(defmulti get-property (fn [property-name trainer] property-name))

(defmethod get-property :population [_ trainer]
  (.getPopulation trainer))

(defmethod get-property :network [_ trainer]
  (get-best-network trainer))

(defmethod get-property :error [_ trainer]
  (.getError trainer))

(defmethod get-property :elite-rate [_ trainer]
  (.getEliteRate trainer))

(defmethod get-property :epoch [_ trainer]
  (.getIteration trainer))

(defmethod get-property :iteration [_ trainer]
  (.getIteration trainer))

(defmethod get-property :default [property trainer]
  "_Unknown property_")
