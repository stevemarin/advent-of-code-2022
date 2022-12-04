(ns day03
  (:require [clojure.java.io :as io]
            [clojure.string :as str]
            [clojure.set :as set]))

(defn get-input
  [filename]
  (->> (slurp (io/file (format "data/%s" filename)))
       (str/split-lines)))

(def alphabet (str/split "abcdefghijklmnopqrstuvwxyz" #""))
(def priority (merge (zipmap alphabet (range 1 27)) (zipmap (map str/upper-case alphabet) (range 27 53))))

;; part 1
(defn intersect-halves
  [s]
  (let [v (str/split s #"")
        half (/ (count v) 2)
        first-half (set (subvec v 0 half))
        second-half (set (subvec v half))]
    (set/intersection first-half second-half)))

(defn part-one
  [filename]
  (->> (get-input filename)
       (map #(intersect-halves %))
       (map #(first (seq %)))
       (map #(priority %))
       (reduce +)))

(assert (== 157 (part-one "day03_sample.txt")))
(part-one "day03.txt")

;; part 2
(defn intersect-three
  [col-of-strings]
  (->>  (map #(set (str/split % #"")) col-of-strings)
        (apply set/intersection)
        (take 1)
        (first)
        (priority)))

(defn part-two
  [filename]
  (->> (get-input filename)
       (partition 3)
       (map #(intersect-three %))
       (reduce +)))

(assert (== 70 (part-two "day03_sample.txt")))
(part-two "day03.txt")
