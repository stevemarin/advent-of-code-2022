(ns day08
  (:require [clojure.java.io :as io]
            [clojure.string :as str]
            [clojure.edn :as edn]
            [clojure.core.matrix :as mat]))

(defn mat-to-nums
  [m]
  (apply vector (for [row m] (apply vector (map edn/read-string row)))))

(defn row
  [arr row-idx]
  (get arr row-idx))

(defn column
  [arr column-idx]
  (apply vector (map #(row % column-idx) arr)))

(defn get-input
  [filename]
  (->> (format "data/%s" filename)
       (io/file)
       (slurp)
       (str/split-lines)
       (map #(str/split % #""))
       (mat-to-nums)))

(defn get-mask-array
  [arr]
  (loop [max-so-far -1
         mask []
         arr arr]
    (if (empty? arr)
      mask
      (let [current (first arr)
            current-max (max max-so-far current)
            current-mask (if (> current max-so-far) 1 0)]
        (recur current-max (conj mask current-mask) (rest arr))))))

(defn get-mask-mat
  [arr f1 f2]
  (apply vector
         (for [idx (range 5)]
           (-> (f1 arr idx)
               (f2)
               (get-mask-array)))))

(defn part-one
  [filename]
  (let [input (get-input "day08_sample.txt")
        l-to-r (get-mask-mat input row identity)
        r-to-l (map reverse (get-mask-mat input row reverse))
        t-to-b (mat/transpose (get-mask-mat input column identity))
        b-to-t (map reverse (mat/transpose (get-mask-mat input column reverse)))
        ]
    ;; (mat/add l-to-r r-to-l ;t-to-b b-to-t
    ;;          ) 
    r-to-l
    ))

(def a (get-input "day08_sample.txt"))

(for [r a] (println r))
(for [r (part-one "ieaifn")] (println r))



(-> (get-input "day08_sample.txt")
    (get-mask-mat row identity))

(->> (part-one "ieaifn")
    ;;  (flatten)
    ;;  (filter #(> % 0))
    ;;  (count)
     )

