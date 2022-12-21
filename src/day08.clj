(ns day08
  (:require [clojure.java.io :as io]
            [clojure.string :as str]
            [clojure.edn :as edn]
            [clojure.core.matrix :as mat]))

(defn print-each
  [col]
  (for [c col] (println c)))

(defn mat-to-nums
  [m]
  (for [row m] (map edn/read-string row)))

(defn get-input
  [filename]
  (->> (format "data/%s" filename)
       (io/file)
       (slurp)
       (str/split-lines)
       (map #(str/split % #""))
       (mat-to-nums)))

(defn get-mask-array
  [row]
  (loop [max-so-far -1
         mask '()
         row row]
    (if (empty? row)
      (reverse mask)
      (let [current (first row)
            current-max (max max-so-far current)
            current-mask (if (> current max-so-far) 1 0)]
        (recur current-max (conj mask current-mask) (next row))))))

(defn get-mask-matrix
  [mat]
  (for [idx (range (count mat))]
    (get-mask-array (nth mat idx))))

(defn part-one
  [filename]
  (let [input (get-input filename)
        l-to-r (get-mask-matrix input)
        r-to-l (map reverse (get-mask-matrix (map reverse input)))
        t-to-b (mat/transpose (get-mask-matrix (mat/transpose input)))
        b-to-t (mat/transpose (map reverse (get-mask-matrix (map reverse (mat/transpose input)))))]
    (->> (reduce mat/add [l-to-r r-to-l t-to-b b-to-t])
         (flatten)
         (filter #(> % 0))
         (count))))

(part-one "day08_sample.txt")
(part-one "day08.txt")

(defn drop-left
  [n arr]
  (mat/transpose (drop n (mat/transpose arr))))

(defn get-mask-array-tree
  [row]
  (let [first-tree (first row)
        row (rest row)]
    (loop [row row
           num-trees 0]
      (if (empty? row)
        num-trees
        (let [next-tree (first row)
              row (rest row)]
          (if (> first-tree next-tree)
            (recur row (inc num-trees))
            (recur '() (inc num-trees))))))))

(defn get-mask-matrix-tree
  [mat]
  (mat/transpose
   (for [idx (range (count mat))]
     (->> (drop-left idx mat)
          (map get-mask-array-tree)))))

(do
  (assert (= (get-mask-array-tree [3 0 3 7 3]) 2))
  (assert (= (get-mask-array-tree [0 3 7 3]) 1))
  (assert (= (get-mask-array-tree [3 7 3]) 1))
  (assert (= (get-mask-array-tree [7 3]) 1))
  (assert (= (get-mask-array-tree [3]) 0)))

(def input (get-input "day08_sample.txt"))
(print-each input)

(defn part-two
  [filename]
  (let [input (get-input filename)
        l-to-r (get-mask-matrix-tree input)
        r-to-l (map reverse (get-mask-matrix-tree (map reverse input)))
        t-to-b (mat/transpose (get-mask-matrix-tree (mat/transpose input)))
        b-to-t (mat/transpose (map reverse (get-mask-matrix-tree (map reverse (mat/transpose input)))))]
    (->>
     (reduce mat/mul [l-to-r r-to-l t-to-b b-to-t])
     (flatten)
     (apply max)
     )))

(part-two "day08_sample.txt")
(part-two "day08.txt")
