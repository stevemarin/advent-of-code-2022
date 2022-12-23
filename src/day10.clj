(ns day10
  (:require [clojure.string :as str]
            [clojure.java.io :as io]
            [clojure.edn :as edn]))

(defn split-by-space [line] (str/split line #" "))

(defn second-to-int [[op value]]
  (if (nil? value)
    [op]
    [op (edn/read-string value)]))

(defn insert-noops
  [line]
  (if (= "noop" (first line))
    [0]
    [0 (last line)]))

(defn cumsum
  [start values]
  (loop [values values totals (list start)]
    (if (empty? values)
      (pop (reverse totals))
      (recur (rest values) (conj totals (+ (first totals) (first values)))))))

(defn get-values
  [filename]
  (->>
   filename
   (format "data/%s")
   (io/file)
   (slurp)
   (str/split-lines)
   (map split-by-space)
   (map second-to-int)
   (map insert-noops)
   (flatten)
   (cumsum 1)))

(get-values "day10_sample.txt")

(defn part-one
  [filename]
  (let [values (get-values filename)]
    (loop [idx 20 total 0]
      (if (> idx 220)
        total
        (recur (+ idx 40) (+ total (* idx (nth values (dec (dec idx))))))))))

(assert (=  (part-one "day10_sample2.txt") 13140))
(part-one "day10.txt")
