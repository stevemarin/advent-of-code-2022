(ns day12
  (:require [clojure.string :as str]
            [clojure.java.io :as io]
            ;; [clojure.core.matrix :as m]
            [clojure.data.priority-map :as pm]))


;; (defn non-neg? [value] (if (>= value 0) true false))
;; (defn split-into-chars [line] (str/split line #""))
;; (def alphabet (str/split "abcdefghijklmnopqrstuvwxyz" #""))
;; (def letter-to-num (merge {"S" 0 "E" 26} (zipmap alphabet (range 26))))

;; (defn letters-to-nums
;;   [letters]
;;   (map #(get letter-to-num %) letters))

;; (defn read-map
;;   [filename]
;;   (->>
;;    filename
;;    (format "data/%s")
;;    (io/file)
;;    (slurp)
;;    (str/split-lines)
;;    (map split-into-chars)))

;; (defn find-str
;;   [the-map s]
;;   (->> the-map
;;        (map #(.indexOf % s))
;;        (interleave (range (count the-map)))
;;        (partition 2)
;;        (filter #(non-neg? (second %)))
;;        (flatten)))

;; (defn map-to-numbers
;;   [the-map]
;;   (m/array (map letters-to-nums the-map)))

;; (defn update-adj-up
;;   [mat row col adj]
;;   (let [max-height (+ 1 (m/mget mat row col))
;;         new-row (- row 1)]
;;     (if (and (> row 0) (<= (m/mget mat new-row col) max-height))
;;       (assoc-in adj [[row col] [new-row col]] 1)
;;       adj)))

;; (defn update-adj-down
;;   [mat row col adj]
;;   (let [max-height (+ 1 (m/mget mat row col))
;;         [num-rows _] (m/shape mat)
;;         max-row-index (- num-rows 1)
;;         new-row (+ row 1)]
;;     (if (and (< row max-row-index) (<= (m/mget mat new-row col) max-height))
;;       (assoc-in adj [[row col] [new-row col]] 1)
;;       adj)))

;; (defn update-adj-left
;;   [mat row col adj]
;;   (let [max-height (+ 1 (m/mget mat row col))
;;         new-col (- col 1)]
;;     (if (and (> col 0) (<= (m/mget mat row new-col) max-height))
;;       (assoc-in adj [[row col] [row new-col]] 1)
;;       adj)))

;; (defn update-adj-right
;;   [mat row col adj]
;;   (let [max-height (+ 1 (m/mget mat row col))
;;         [_ num-cols] (m/shape mat)
;;         max-col-index (- num-cols 1)
;;         new-col (+ col 1)]
;;     (if (and (< col max-col-index) (<= (m/mget mat row new-col) max-height))
;;       (assoc-in adj [[row col] [row new-col]] 1)
;;       adj)))

;; (defn check-neighbors
;;   [mat index adj]
;;   (let [[row col] index]
;;     (loop [adj adj
;;            funcs [update-adj-up update-adj-down update-adj-left update-adj-right]]
;;       (if (empty? funcs)
;;         adj
;;         (recur ((peek funcs) mat row col adj) (pop funcs))))))

;; (defn get-adjacency
;;   [mat]
;;   (let [indices (m/index-seq mat)]
;;     (loop [indices indices adj {}]
;;       (if (empty? indices)
;;         adj
;;         (recur (rest indices) (check-neighbors mat (first indices) adj))))))

;; (defn manhattan
;;   "calculates the manhattan distance between to vectors"
;;   [first second]
;;   (int (reduce + (m/abs (m/sub first second)))))

;; (defrecord Node [neighbors came-from h path-score expected-score])

;; (defn neighbor-loop
;;   [current neighbors open-set came-from gScore fScore end-heuristic]
;;   (loop [neighbors (seq neighbors)
;;          open-set open-set
;;          came-from came-from
;;          gScore gScore
;;          fScore fScore]
;;     (if (empty? neighbors)
;;       (list open-set came-from gScore fScore)
;;       (let [[neighbor dist] (first neighbors)
;;             neighbor-gScore (get gScore neighbor)
;;             current-gScore (get gScore current)
;;             tentative-gScore (+ current-gScore dist)]
;;         ;; (println current neighbor current-gScore tentative-gScore neighbor-gScore (+ tentative-gScore (end-heuristic neighbor)))
;;         ;; (println "a" tentative-gScore "b" neighbor-gScore)
;;         (if (< tentative-gScore neighbor-gScore)
;;           (recur (rest neighbors)
;;                  (assoc open-set neighbor (+ tentative-gScore (end-heuristic neighbor)))
;;                  (assoc came-from neighbor current)
;;                  (assoc gScore neighbor tentative-gScore)
;;                  (assoc fScore neighbor (+ tentative-gScore (end-heuristic neighbor))))
;;           (recur (rest neighbors)
;;                  open-set
;;                  came-from
;;                  gScore
;;                  fScore))))))

;; (defn a-star
;;   [adj start end heuristic]
;;   (let [end-heuristic (partial heuristic end)
;;         h-start (end-heuristic start)
;;         open-set (pm/priority-map start h-start)
;;         came-from {}
;;         gScore (merge (zipmap (keys adj) (repeat Long/MAX_VALUE)) {start 0})
;;         fScore (merge (zipmap (keys adj) (repeat Long/MAX_VALUE)) {start h-start})
;;         [current _] (peek open-set)
;;         open-set (pop open-set)]
;;     (loop [current current
;;            open-set open-set
;;            came-from came-from
;;            gScore gScore
;;            fScore fScore]
;;       ;; (println "aaa" current end)
;;       (if (= current end)
;;         ;; (list "open-set" open-set "gScore" gScore "came-from" came-from)
;;         (get gScore end)
;;         (let [neighbors (get adj current)
;;               [open-set came-from gScore fScore] 
;;               (neighbor-loop current neighbors open-set came-from gScore fScore end-heuristic)]
;;           ;; (println current (sort-by val < path-scores))
;;           ;; (println current open-set)
;;           ;; (println)
;;           (recur (first (peek open-set)) (pop open-set) came-from gScore fScore))))))

;; ;; (def the-map (read-map "day12_sample.txt"))
;; ;; (def mat (map-to-numbers the-map))
;; ;; (def adj (get-adjacency mat))
;; ;; (def start (find-str the-map "S"))
;; ;; (def end  (find-str the-map "E"))
;; ;; (a-star adj start end manhattan)

;; (def the-map (read-map "day12.txt"))
;; (def mat (map-to-numbers the-map))
;; (def adj (get-adjacency mat))
;; (def start (find-str the-map "S"))
;; (def end  (find-str the-map "E"))
;; ;; (a-star adj start end manhattan)

;; (m/shape mat)
;; end
;; start
;; adj


(comment
  "Day 12: Hill Climbing Algorithm")

(def sample-input
  (-> "data/day12_sample.txt" io/file io/reader line-seq))

(def puzzle-input
  (-> "data/day12.txt" io/file io/reader line-seq))

(defn char-to-int
  "Start = 0, end = 25, a-z = 0-25."
  [ch]
  (case ch \S 0 \E 25
        (- (int ch) 97)))

(defn parse
  [input]
  (loop [[[y xs :as this] & those] (map-indexed vector input)
         grid [] start nil goal nil]
    (if-not this
      {:grid grid :start start :goal goal}
      (let [start' (or (some->> (str/index-of xs "S") (conj [y])) start)
            goal'  (or (some->> (str/index-of xs "E") (conj [y])) goal)]
        (recur those
               (conj grid (mapv char-to-int (seq xs)))
               start'
               goal')))))

(defn bfs ;; Adapted from my old Project Euler #82 solution and simplified
  "Args: m                     -> the field (matrix) in which to search
        config {:start-coords  -> vector of the starting position [y x]
                :goal?         -> test if current node meets a terminating condition
                :neighbors-fn  -> given node, return all possible neighbors (not nec. existing or unseen)"
  [config]
  (let [{:keys [start-coords goal? neighbors-fn]} config]
    (loop [stack      (pm/priority-map start-coords 0)
           closed-set {}]
      (let [[coords cost :as current] (peek stack)]
        (cond
          (nil? current) nil ; No path, thanks for playing
          (goal? coords) cost
          :else
          (let [neighbors (into {}
                                (for [neighbor (neighbors-fn coords)
                                      :when (not (contains? closed-set neighbor))]
                                  {neighbor (inc cost)}))]
            (recur (merge-with min (pop stack) neighbors)
                   (conj closed-set current))))))))

(defn valid-neighbors
  [grid [y x]]
  (filter (fn [[ny nx]]
            (let [current (get-in grid [y x])
                  nbor    (get-in grid [ny nx] nil)]
              (when nbor
                (<= (- nbor current) 1))))
          [[(dec y) x] [(inc y) x] [y (dec x)] [y (inc x)]]))

(defn starting-points
  [grid]
  (for [y (range (count grid))
        x (range (count (first grid)))
        :when (zero? (get-in grid [y x]))]
    [y x]))

(defn solve
  [input]
  (let [{:keys [grid start goal]} (parse input)]
    (->> (for [start' (cons start (starting-points grid))]
           (bfs {:start-coords start'
                 :goal?        (partial = goal)
                 :neighbors-fn (partial valid-neighbors grid)}))
         ((juxt first #(apply min (remove nil? %))))
         (zipmap [:part-1 :part-2]))))

(solve sample-input)
(solve puzzle-input)
