(ns day14
  (:require [clojure.string :as str]
            [clojure.edn :as edn]
            [clojure.core.matrix :as m]
            [clojure.java.io :as io]))

(defn formation-extremes
  [f dim formations]
  (let [formations (flatten formations)
        ;; gotta take all xs or ys and data are (col row col row..) so
        ;; we partiion and just drop first element for rows
        formations (cond
                     (= dim :col) formations
                     (= dim :row) (rest formations))]
    (->> formations (partition 1 2) flatten (reduce f))))

(defn all-extremes
  [formations]
  (for [dim [:row :col] f [min max]] (formation-extremes f dim formations)))

(defn process-line
  [line]
  (->> (str/split line #" -> ")
       (map #(str/split % #","))
       (map #(map edn/read-string %))
       flatten
       (partition 4 2)))

(defn set-slice!
  [cave offset-col rows cols]
  (let [[start-row end-row] rows
        [start-col end-col] cols
        min-row (min start-row end-row)
        max-row (max start-row end-row)
        min-col (min start-col end-col)
        max-col (max start-col end-col)]
    (doseq [row (range min-row (inc max-row))
            col (range min-col (inc max-col))]
      (m/mset! cave row (- col offset-col) 1.0)))
  cave)

(defn one-formation
  [cave offset-col formation]
  (doseq
   [part formation]
    (let [[start-col start-row end-col end-row] part]
      (set-slice! cave offset-col [start-row end-row] [start-col end-col])))
  cave)

(defn all-formations
  [formations]
  (let [[start-row end-row start-col end-col] (all-extremes formations)
        num-rows (+ 3 (max start-row end-row))
        min-col (min start-col end-col)
        max-col (max start-col end-col)

        offset-cols 150
        offset-col (- min-col offset-cols)
        num-cols (- max-col offset-col)

        shape [num-rows (+ num-cols offset-cols)]
        cave (m/mutable (m/zero-array shape))]
    (doseq [formation formations] (one-formation cave offset-col formation))
    {:cave cave :offset-col offset-col}))

(defn add-floor
  [cave]
  (let [c (:cave cave)
        [num-rows num-cols] (m/shape c)]
    (m/set-row! c (dec num-rows) (repeat num-cols 1.0))
    (assoc cave :cave c)))

(defn get-cave
  [filename]
  (->> filename
       (format "data/%s")
       io/file
       io/reader
       line-seq
       (map process-line)
       all-formations
       add-floor))

(defn check-location
  [row col cave]
  (if (= 0.0 (m/mget cave row col))
    (list row col)
    nil))

(defn new-location
  [[row col] cave]
  (or (check-location (inc row) col cave)
      (check-location (inc row) (dec col) cave)
      (check-location (inc row) (inc col) cave)))

(defn part-one
  [filename]
  (let [cave (get-cave filename)
        {:keys [cave offset-col]} cave
        max-row (- (m/row-count cave) 2)
        start-loc [0 (- 500 offset-col)]]
    (loop [cave (m/clone cave)
           prev-loc start-loc
           loc (new-location start-loc cave)
           num-drops 0]
      (cond
        (nil? loc) (do
                     (m/mset! cave (first prev-loc) (second prev-loc) 1.0)
                     (recur cave start-loc (new-location start-loc cave) (inc num-drops)))
        (= max-row (first loc)) num-drops
        :else (recur cave loc (new-location prev-loc cave) num-drops)))))

(assert (= 24 (part-one "day14_sample.txt")))
(part-one "day14.txt")

(defn part-two
  [filename]
  (let [cave (get-cave filename)
        {:keys [cave offset-col]} cave
        start-loc [0 (- 500 offset-col)]]
    (loop [cave (m/clone cave)
           prev-loc start-loc
           loc (new-location start-loc cave)
           num-drops 0]
      (cond
        (= (m/mget cave 0 (second start-loc)) 1.0) num-drops
        (nil? loc) (do
                     (m/mset! cave (first prev-loc) (second prev-loc) 1.0)
                     (recur cave start-loc (new-location start-loc cave) (inc num-drops)))
        :else (recur cave loc (new-location prev-loc cave) num-drops)))))

(assert (= 93 (part-two "day14_sample.txt")))
(part-two "day14.txt")

;; fun cave drawing stuff

;; (def cave (get-cave "day14_sample.txt"))
;; (def cave (get-cave "day14.txt"))
;; cave

;; (doseq [tmp (->> (:cave cave)
;;                  flatten
;;                  (replace {0.0 "." 1.0 "#"})
;;                  (partition (m/column-count (:cave cave)))
;;                  (map str/join)
;;                  )]
;;   (println tmp))