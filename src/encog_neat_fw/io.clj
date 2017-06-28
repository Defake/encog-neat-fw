(ns encog-neat-fw.io
  (:import [org.encog.util.obj SerializeObject]
           [org.encog.neural.neat PersistNEATPopulation]))

(defn save-network [file ann]
  (SerializeObject/save file ann))

(defn load-network [file]
  (SerializeObject/load file))

(defn save-population [output-stream population]
  (.save (PersistNEATPopulation.) output-stream population))

(defn load-population [input-stream]
  (.read (PersistNEATPopulation.) input-stream))
