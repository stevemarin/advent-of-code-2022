(ns day14
  (:require [clojure.string :as str]
            [clojure.edn :as edn]
            [clojure.core.matrix :as m]
            [clojure.java.io :as io]))

(defn formation-extremes
  [f dim formations]
  (let [formations (flatten formations)
        ;; gotta take all xs or ys and data are (x y x y x y...) so
        ;; we partiion and just drop first element for ys
        formations (cond
                     (= dim  :x) formations
                     (= dim :y) (rest formations))]
    (->> formations
         (partition 1 2)
         flatten
         (reduce f))))

(defn all-extremes
  [formations]
  (for [dim [:x :y] f [min max]] (formation-extremes f dim formations)))

(defn process-line
  [line]
  (->> (str/split line #" -> ")
       (map #(str/split % #","))
       (map #(map edn/read-string %))
       flatten
       (partition 4 2)))

;; merge these two functions into one accepting :row or :col

(defn set-col-slice
  [cave x y-start y-end]
  (let [y-min (min y-start y-end)
        y-max (max y-start y-end)]
    (doseq [y (range y-min (inc y-max))]
      (println "setting col" y x)
      (m/mset! cave y x 1)))
  cave)

(defn set-row-slice
  [cave y x-start x-end]
  (let [x-min (min x-start x-end)
        x-max (max x-start x-end)]
    (doseq [x (range x-min (inc x-max))]
      (println "setting row" y x)
      (m/mset! cave y x 1)))
  cave)

(defn one-line
  [cave line]
  (let [[x-start y-start x-end y-end] line]
    (println "aaa" x-start x-end y-start y-end)
    (cond
      (= x-start x-end) (set-col-slice cave x-start y-start y-end)
      (= y-start y-end) (set-row-slice cave y-start x-start x-end))
    cave))

(defn one-formation
  [cave formation]
  (doseq [part formation] (one-line cave part))
  cave)

(defn all-formations
  [formations]
  (let [[min-x max-x min-y max-y] (all-extremes formations)
        ;; x-shape (+ 5 (- max-x min-x))
        ;; y-shape (+ 5 (- max-y min-y))
        ;; shape (map #(+ 5 %) [y-shape x-shape])
        shape (map #(+ 5 %) [max-y max-x])
        cave (m/mutable (m/zero-array shape))]
    (println "shape:" shape)
    (doseq [formation formations] (one-formation cave formation))
    cave))

(defn read-file
  [filename]
  (->> filename
       (format "data/%s")
       io/file
       io/reader
       line-seq
       (map process-line)))


(read-file "day14_sample.txt")

(def formations (read-file "day14_sample.txt"))
(all-extremes formations)

(all-formations formations)

;; add plotting for qa
;; try to reduce size of matrix (row/col offset)