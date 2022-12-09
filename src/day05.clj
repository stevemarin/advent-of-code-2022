(ns day05
  (:require [clojure.java.io :as io]
            [clojure.string :as str]
            [clojure.edn :as edn]))

(defn get-input
  [filename]
  (str/split (slurp (io/file (format "data/%s" filename))) #"\n\n"))

(defn get-top-portion
  [filename]
  (-> (get-input filename)
      (first)
      (str/split-lines)
      (drop-last)))

(defn get-bottom-portion
  [filename]
  (-> (get-input filename)
      (last)
      (str/split-lines)))

(defn get-num-columns
  [s]
  (/ (+ (count s) 1) 4))

(defn is-space?
  [c]
  (= c \space))

(defn get-column
  [col lines]
  (->> (map #(str % " ") lines)
       (map #(partition 4 %))
       (map #(nth % col))
       (map #(nth % 1))
       (drop-while is-space?)
       (reverse)))

(defn get-starting-locations
  [top-lines]
  (let [num-columns (get-num-columns (first top-lines))
        starting (for [col (range num-columns)] (get-column col top-lines))]
    (zipmap (range 1 (+ 1 num-columns)) (map vec starting))))

(defn get-column-from-bottom
  [bottom-lines idx]
  (->> bottom-lines
       (map #(nth % idx))))

(defn get-columns-from-bottom
  [s]
  (let [ss (map #(str/split % #" ") s)]
    (apply map get-column-from-bottom [[ss ss ss] [1 3 5]])))

(defn parse-vec-strings
  [s]
  (map #(edn/read-string %) s))

(defn get-rules
  [filename]
  (->> (get-bottom-portion filename)
       (get-columns-from-bottom)
       (map parse-vec-strings)
       (apply map vector)
       (flatten)
       (partition 3)))

(defn move-crate
  [crate-locations from-idx to-idx]
  (let [from (crate-locations from-idx)
        crate (peek from)]
    (-> crate-locations
        (update from-idx pop)
        (update to-idx conj crate))))

(defn move-crates
  [crate-locations [num-moves from-idx to-idx]]
  (loop [crate-locations crate-locations num-moves num-moves]
    (if (= 0 num-moves)
      crate-locations
      (recur (move-crate crate-locations from-idx to-idx) (dec num-moves)))))

(defn process-moves
  [crate-locations moves]
  (loop [crate-locations crate-locations moves moves]
    (if (= 0 (count moves))
      crate-locations
      (recur (move-crates crate-locations (first moves)) (rest moves)))))

(defn get-top-crates [m]
  (apply str (map last (vals (sort m)))))

(defn move-crates-part2
  [crate-locations move]
  (let [num-moves (nth move 0)
        from-idx (nth move 1)
        to-idx (nth move 2)
        from (crate-locations from-idx)
        crates (take-last num-moves from)
        drop-last (partial drop-last num-moves)]
\    (-> crate-locations
        (update from-idx drop-last)
        (update to-idx concat crates))))

(defn process-moves-part2
  [crate-locations moves]
  (loop [crate-locations crate-locations moves moves]
    (if (= 0 (count moves))
      crate-locations
      (recur (move-crates-part2 crate-locations (first moves)) (rest moves)))))

(defn part-two [filename]
  (let [rules (get-rules filename)]
    (-> (get-top-portion filename)
        (get-starting-locations)
        (process-moves-part2 rules)
        (get-top-crates))))

(defn part-one [filename]
  (let [rules (get-rules filename)]
    (-> (get-top-portion filename)
        (get-starting-locations)
        (process-moves rules)
        (get-top-crates))))

(assert (= "CMZ" (part-one "day05_sample.txt")))
(part-one "day05.txt")

(assert (= "MCD" (part-two "day05_sample.txt")))
(part-two "day05.txt")
