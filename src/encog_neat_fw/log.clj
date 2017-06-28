(ns encog-neat-fw.log
  (:require [encog-neat-fw.training :as t])
  (:import [org.encog.util.simple EncogUtility]))

(declare convert-log-argument)

;; Logger

(defn- resolve-logger-message [args trainer]
  (for [arg (vec args)]
    (convert-log-argument arg trainer)))

(defmulti convert-log-argument (fn [argument trainer] (class argument)))

(defmethod convert-log-argument clojure.lang.Keyword [kwd trainer-sym]
  `(t/get-property ~kwd ~trainer-sym))

(defmethod convert-log-argument clojure.lang.PersistentList [lst trainer]
  (resolve-logger-message lst trainer))

;; (defmethod convert-log-argument clojure.lang.IFn [func trainer]
(defmethod convert-log-argument :default [arg _]
  arg)

(defn- create-log-func [args]
  (let [trainer-sym (gensym "trainer")]
    `(fn [~trainer-sym]
       (apply str ~(concat '(list) (resolve-logger-message args trainer-sym))))))

(defmacro create-logger [each-n-messages & args]
  `(let [counter# (atom 0)
         logger-message# ~(create-log-func args)]
     (fn [trainer#]
      (swap! counter# inc)
      (when (<= ~each-n-messages @counter#)
        (reset! counter# 0)
        (println (logger-message# trainer#))))))

(defn eval-network [network dataset]
  (EncogUtility/evaluate network dataset))
