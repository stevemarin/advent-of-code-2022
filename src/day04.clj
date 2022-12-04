(ns day04
  (:require [clojure.java.io :as io]
            [clojure.string :as str]
            [clojure.set :as set]
            [clojure.edn :as edn]))

(defn get-input
  [filename]
  (->> (slurp (io/file (format "data/%s" filename)))
       (str/split-lines)))

(defn range-as-set
  [s]
  (set (range (nth s 0) (inc (nth s 1)))))

(defn super-or-sub?
  [s]
  (or (set/superset? (nth s 0) (nth s 1)) (set/subset? (nth s 0) (nth s 1))))

(defn part-one
  [filename]
  (->> (get-input filename)
       (map #(str/split % #"[,-]"))
       (flatten)
       (map #(edn/read-string %))
       (partition 2)
       (map range-as-set)
       (partition 2)
       (map #(if (super-or-sub? %) 1 0))
       (reduce +)))

(assert (== 2 (part-one "day04_sample.txt")))
(part-one "day04.txt")

(defn part-two
  [filename]
  (->> (get-input filename)
       (map #(str/split % #"[,-]"))
       (flatten)
       (map #(edn/read-string %))
       (partition 2)
       (map range-as-set)
       (partition 2)
       (map #(if (> (count (set/intersection (nth % 0) (nth % 1))) 0) 1 0))
       (reduce +)
       ))

(assert (== 4 (part-two "day04_sample.txt")))
(part-two "day04.txt")
