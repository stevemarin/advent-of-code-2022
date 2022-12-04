(ns day02
  (:require [clojure.java.io :as io]
            [clojure.string :as str]
            [clojure.core.match :refer [match]]))

;; part1
(defn score-throw
  [round]
  (match [round]
    [[_ "X"]] 1
    [[_ "Y"]] 2
    [[_ "Z"]] 3))

(defn score-outcome
  [round]
  (match [round]
    [["A" "X"]] 3
    [["A" "Y"]] 6
    [["A" "Z"]] 0
    [["B" "X"]] 0
    [["B" "Y"]] 3
    [["B" "Z"]] 6
    [["C" "X"]] 6
    [["C" "Y"]] 0
    [["C" "Z"]] 3))

(defn score-round
  [round]
  (+ (score-throw round) (score-outcome round)))

(defn get-input
  [filename]
  (->> (slurp (io/file (format "data/%s" filename)))
       (str/split-lines)
       (map #(str/split %1 #" "))))

(defn part-one
  [filename]
  (->> (get-input filename)
       (map score-round)
       (reduce +)))

;; part 2
(defn score-outcome2
  [round]
  (match [round]
    [[_ "X"]] 0
    [[_ "Y"]] 3
    [[_ "Z"]] 6))

(defn score-throw2
  [round]
  (match [round]
    [["A" "X"]] 3
    [["A" "Y"]] 1
    [["A" "Z"]] 2
    [["B" "X"]] 1
    [["B" "Y"]] 2
    [["B" "Z"]] 3
    [["C" "X"]] 2
    [["C" "Y"]] 3
    [["C" "Z"]] 1))

(defn score-round2
  [round]
  (+ (score-throw2 round) (score-outcome2 round)))

(defn part-two
  [filename]
  (->> (get-input filename)
       (map score-round2)
       (reduce +)))

(assert (== 15 (part-one "day02_sample.txt")))
(assert (== 12 (part-two "day02_sample.txt")))

(part-one "day02.txt")
(part-two "day02.txt")
