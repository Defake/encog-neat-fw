(ns encog-neat-fw.data
  (:import [org.encog.ml.data.basic BasicMLData BasicMLDataSet]))

(defn- double-2d-arr-from-vec [data]
  (into-array (map double-array data)))

(defn dataset-basic [input-data output-data]
  "Creates dataset from two vectors: input vector and output vector
   Usage: (dataset-basic [[0 0] [0 1] [1 0] [1 1]]
                         [[0] [1] [1] [0]])"
  (let [input (double-2d-arr-from-vec input-data)
        output (double-2d-arr-from-vec output-data)]
    (BasicMLDataSet. input output)))

(defn data-basic [datavec]
  "Usage: (data-basic [0 1])"
  (BasicMLData. (double-array datavec)))

;; Converting data from BasicMLData to more convenient form

(defmulti get-data
  "Extract data from BasicMLData. Returns vector of specific type.
   Usage: (get-data :int-round (BasicMLData. (double-array [0.04421 1.2 -1.7 0.504])))"
  (fn [data-type ^BasicMLData data] data-type))

(defmethod get-data :double [_ data]
  (vec (.getData data)))

(defmethod get-data :int-round [_ data]
  (vec (map #(Math/round %) (.getData data))))

(defmethod get-data :int-floor [_ data]
  (vec (map #(int %) (.getData data))))

(defmethod get-data :int-ceil [_ data]
  (vec (map #(int (Math/ceil %)) (.getData data))))

(defmethod get-data :int [_ data]
  (get-data :int-floor data))

;; Resolving data passed to networks

(defmulti resolve-data
  "Ensure to return BasicMLData"
  (fn [data] (class data)))

(defmethod resolve-data clojure.lang.PersistentVector [data]
  (data-basic data))

(defmethod resolve-data org.encog.ml.data.basic.BasicMLData [data]
  data)

