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
  [cave offset-x offset-y x y-start y-end]
  (let [y-min (- (min y-start y-end) offset-y)
        y-max (- (max y-start y-end) offset-y)]
    (doseq [y (range y-min (inc y-max))]
      (m/mset! cave y (- x offset-x) 1)))
  cave)

(defn set-row-slice
  [cave offset-x offset-y y x-start x-end]
  (let [x-min (- (min x-start x-end) offset-x)
        x-max (- (max x-start x-end) offset-x)]
    (doseq [x (range x-min (inc x-max))]
      (m/mset! cave (- y offset-y) x 1)))
  cave)

(defn one-line
  [cave offset-x offset-y line]
  (let [[x-start y-start x-end y-end] line]
    (cond
      (= x-start x-end) (set-col-slice cave offset-x offset-y x-start y-start y-end)
      (= y-start y-end) (set-row-slice cave offset-x offset-y y-start x-start x-end))
    cave))

(defn one-formation
  [cave offset-x offset-y formation]
  (doseq [part formation] (one-line cave offset-x offset-y part))
  cave)

(defn all-formations
  [formations]
  (let [[min-x max-x min-y max-y] (all-extremes formations)
        offset 5
        offset-x (- min-x offset)
        offset-y (- min-y offset)
        x-shape (- max-x offset-x)
        y-shape (- max-y offset-y)
        shape (map #(+ offset %) [y-shape x-shape])
        cave (m/mutable (m/zero-array shape))]
    (println "shape:" shape)
    (doseq [formation formations] (one-formation cave offset-x offset-y formation))
    {:cave cave :offset-x offset-x :offset-y offset-y}))

(defn read-file
  [filename]
  (->> filename
       (format "data/%s")
       io/file
       io/reader
       line-seq
       (map process-line)))

;; (def formations (read-file "day14.txt"))
(def formations (read-file "day14_sample.txt"))
(def aaa (:cave (all-formations formations)))
(def rows (m/row-count aaa))

(doseq [tmp
      (map str/join (m/transpose (partition rows (replace {0.0 "." 1 "#"} (flatten (m/transpose aaa))))))]
  (println tmp))
