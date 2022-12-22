(ns day09
  (:require [clojure.java.io :as io]
            [clojure.string :as str]
            [clojure.edn :as edn]
            [clojure.core.matrix :as m]))

(defn get-input
  [filename]
  (->>
   (format "data/%s" filename)
   (io/file)
   (slurp)
   (str/split-lines)
   (map #(str/split % #" "))
   (map #(list (first %) (edn/read-string (last %))))))

;; TODO could use mutability for a speed up
;; TODO could stop inner loop after first non-moving knot

(defn move-up [pos] (m/add pos [0 1]))
(defn move-down [pos] (m/add pos [0 -1]))
(defn move-left [pos] (m/add pos [-1 0]))
(defn move-right [pos] (m/add pos [1 0]))

(defn move-one
  [positions step knot direction]
  (let [pos (m/mget positions step knot)
        pos (cond
              (= direction "U") (move-up pos)
              (= direction "D") (move-down pos)
              (= direction "L") (move-left pos)
              (= direction "R") (move-right pos))]
    (m/mset positions step knot pos)))

(defn update-knot
  [positions step knot]
  (let [head (m/mget positions step (dec knot))
        tail (m/mget positions step knot)
        head-x (m/mget head 0)
        head-y (m/mget head 1)
        tail-x (m/mget tail 0)
        tail-y (m/mget tail 1)
        tail (cond
               ;; if and row or column differences are > 2; return nil
               (or (> (abs (- head-x tail-x)) 2) (> (abs (- head-y tail-y)) 2)) nil
               ;; if touching (including diagonal); do nothing
               (and (<= (abs (- tail-x head-x)) 1) (<= (abs (- tail-y head-y)) 1)) tail
               ;; if same row; move up or down
               (and (= tail-x head-x) (= (- tail-y head-y) 2)) (move-down tail)
               (and (= tail-x head-x) (= (- tail-y head-y) -2)) (move-up tail)
               ;; if same column; move left or right
               (and (= tail-y head-y) (= (- tail-x head-x) 2)) (move-left tail)
               (and (= tail-y head-y) (= (- tail-x head-x) -2)) (move-right tail)
               ;; if different row & column; move diagonally
               (and (>= (- tail-x head-x) 1) (>= (- tail-y head-y) 1)) (move-left (move-down tail))
               (and (>= (- tail-x head-x) 1) (<= (- tail-y head-y) -1)) (move-left (move-up tail))
               (and (<= (- tail-x head-x) -1) (>= (- tail-y head-y) 1)) (move-right (move-down tail))
               (and (<= (- tail-x head-x) -1) (<= (- tail-y head-y) -1)) (move-right (move-up tail)))]
    (m/mset positions step knot tail)))

(defn move-all
  [positions direction]
  (let [[num-steps num-knots _] (m/shape positions)
        positions (as-> positions pos
                    (m/get-row pos (dec num-steps))
                    (m/conjoin positions pos)
                    (move-one pos num-steps 0 direction))]
    (loop [knot-idx 1
           positions positions]
      (if (= (identity num-knots) knot-idx)
        positions
        (recur (inc knot-idx) (update-knot positions num-steps knot-idx))))))

(defn move-all-num-times
  [positions num-times direction]
  (loop [positions positions num-times num-times]
    (if (= num-times 0)
      positions
      (recur (move-all positions direction) (dec num-times)))))

(defn do-it
  [filename num-knots]
  (loop [positions (m/fill (m/zero-array [1 num-knots 2]) 0) moves (get-input filename)]
    (if (empty? moves)
      (count (set (map #(peek %) positions)))
      (let [move (first moves)
            direction (first move)
            times (last move)
            positions (move-all-num-times positions times direction)]
        (recur positions (rest moves))))))


(assert (= (do-it "day09_sample.txt" 2) 13))
(do-it "day09.txt" 2)

(assert (= 1 (do-it "day09_sample.txt" 10)))
(assert (= 1 (do-it "day09_sample2.txt" 36)))
(do-it "day09.txt" 10)
