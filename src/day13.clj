(ns day13
  (:require [clojure.java.io :as io]
            [clojure.edn :as edn]))

(defn input
  [filename]
  (->>  filename
        (format "data/%s")
        io/file
        io/reader
        line-seq
        (map edn/read-string)
        (filter #(not (nil? %)))
        ))

(defn compare-packets
  "compares two packets recursively and returns:
   -1 -> wrong order
    1 -> right order
    
   internally uses 0 to note the arrays are the same to that
   point and the check should continue to the next item"
  [left right]
  (cond
    (and (number? left) (number? right)) (compare left right)
    (and (number? left) (sequential? right)) (compare-packets [left] right)
    (and (sequential? left) (number? right)) (compare-packets left [right])
    (and (sequential? left) (sequential? right))
    (loop [values (map compare-packets left right)]
      (condp = (first values)
        nil (compare-packets (count left) (count right))
        -1 -1
        1 1
        0 (recur (rest values))))))

(def tests
    (let [packets (input "day13_sample.txt")
          results '(true true false true false true false false)]
      (for [[p r] (partition 2 (interleave packets results))]
        (= (apply compare-packets p) r))))

(assert every? tests)

(defn part-one
  [filename]
  (let [packet-pairs (partition 2 (input filename))
        pair-idxs (range 1 (inc (count packet-pairs)))]
    (->> (for [[left right] packet-pairs] (compare-packets left right))
         (interleave pair-idxs)
         (partition 2)
         (filter #(= -1 (last %)))
         (map first)
         (reduce +))))

(assert (= 13 (part-one "day13_sample.txt")))
(part-one "day13.txt")

(defn get-indices
  [xs]
  (list (.indexOf xs [[2]]) (.indexOf xs [[6]])))

(defn part-two
  [filename]
  (->> filename
       input
       (concat [[[2]] [[6]]])
       (sort-by identity compare-packets)
       get-indices
       (map inc)
       (reduce *)))

(assert (= 140 (part-two "day13_sample.txt")))
(part-two "day13.txt")
