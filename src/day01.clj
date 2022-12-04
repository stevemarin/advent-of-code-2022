(ns day01
  (:require [clojure.java.io :as io]
            [clojure.string :as str]))

(defn get-input
  [filename]
  (->> (slurp (io/file (format "data/%s" filename)))
       (str/split-lines)
       (map parse-long)))

(defn part-one
  [filename]
  (->> (get-input filename)
       (partition-by nil?)
       (take-nth 2)
       (map #(apply + %))
       (apply max)))

(defn part-two
  [filename]
  (->> (get-input filename)
       (partition-by nil?)
       (take-nth 2)
       (map #(apply + %))
       (sort-by -)
       (take 3)
       (reduce +)))

(assert (== 24000 (part-one "day01_sample.txt")))
(assert (== 45000 (part-two "day01_sample.txt")))

(part-one "day01.txt")
(part-two "day01.txt")
