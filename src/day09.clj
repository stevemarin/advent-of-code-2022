(ns day09
  (:require [clojure.java.io :as io]
            [clojure.string :as str]
            [clojure.edn :as edn]))

(defn get-input
  [filename]
  (->>
   (format "data/%s" filename)
   (io/file)
   (slurp)
   (str/split-lines)
   (map #(str/split % #" "))
   (map #(list (first %) (edn/read-string (last %))))))

(defn move-up [single-pos] (list (first single-pos) (inc (last single-pos))))
(defn move-down [single-pos] (list (first single-pos) (dec (last single-pos))))
(defn move-right [single-pos] (list (inc (first single-pos)) (last single-pos)))
(defn move-left [single-pos] (list (dec (first single-pos)) (last single-pos)))

(defn move
  [single-pos direction]
  (cond
    (= direction "U") (move-up single-pos)
    (= direction "D") (move-down single-pos)
    (= direction "L") (move-left single-pos)
    (= direction "R") (move-right single-pos)))

(defn move-both
  [positions direction]
  (let [[head-pos tail-pos] (first positions)
        head-pos (move head-pos direction)
        [head-x head-y] head-pos
        [tail-x tail-y] tail-pos
        next-pos (cond
                   ;; if and row or column differences are > 2; return nil
                   (or (> (abs (- head-x tail-x)) 2) (> (abs (- head-y tail-y)) 2)) nil
                   ;; if touching (including diagonal); do nothing
                   (and (<= (abs (- tail-x head-x)) 1) (<= (abs (- tail-y head-y)) 1)) (list head-pos tail-pos)
                   ;; if same row; move up or down
                   (and (= tail-x head-x) (= (- tail-y head-y) 2)) (list head-pos (move-down tail-pos))
                   (and (= tail-x head-x) (= (- tail-y head-y) -2)) (list head-pos (move-up tail-pos))
                   ;; if same column; move left or right
                   (and (= tail-y head-y) (= (- tail-x head-x) 2)) (list head-pos (move-left tail-pos))
                   (and (= tail-y head-y) (= (- tail-x head-x) -2)) (list head-pos (move-right tail-pos))
                   ;; if different row & column; move diagonally
                   (and (>= (- tail-x head-x) 1) (>= (- tail-y head-y) 1)) (list head-pos (move-left (move-down tail-pos)))
                   (and (>= (- tail-x head-x) 1) (<= (- tail-y head-y) -1)) (list head-pos (move-left (move-up tail-pos)))
                   (and (<= (- tail-x head-x) -1) (>= (- tail-y head-y) 1)) (list head-pos (move-right (move-down tail-pos)))
                   (and (<= (- tail-x head-x) -1) (<= (- tail-y head-y) -1)) (list head-pos (move-right (move-up tail-pos))))]
    (conj positions next-pos)))

(defn move-both-n-times
  [positions times direction]
  (loop [positions positions times times]
    (if (= times 0)
      positions
      (recur (move-both positions direction) (dec times)))))

(do
  (assert (= (move '(0 0) "L") '(-1  0)))
  (assert (= (move '(0 0) "R") '( 1  0)))
  (assert (= (move '(0 0) "U") '( 0  1)))
  (assert (= (move '(0 0) "D") '( 0 -1))))

(defn part-one
  [filename]
  (loop [positions '(((0 0) (0 0))) moves (get-input filename) tail-positions ()]
    (if (empty? moves)
      (count (set (map #(second %) positions)))
      (let [move (first moves)
            direction (first move)
            times (last move)
            positions (move-both-n-times positions times direction)
            tail-positions (conj tail-positions (last positions))]
        (recur positions (rest moves) tail-positions)))))

(assert (= (part-one "day09_sample.txt") 13))
(part-one "day09.txt")
