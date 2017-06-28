(ns encog-neat-fw.core
  (:require [encog-neat-fw.data :as d])
  (:import [org.encog Encog]
           [org.encog.neural.neat NEATPopulation]))

(defn create-population [input-count output-count population-size]
  (doto (NEATPopulation. input-count output-count population-size)
        (.setInitialConnectionDensity 1.0) ;; not required, but speeds training
        (.reset)))

(defn compute
  "Get output value of the network"
  ([network data]
   (compute network :double data))

  ([network data-type data]
   (d/get-data data-type (.compute network (d/resolve-data data)))))

(defn shutdown []
  (.shutdown (Encog/getInstance)))
