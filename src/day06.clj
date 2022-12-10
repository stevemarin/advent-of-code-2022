(ns day06
  (:require [clojure.java.io :as io]
            [clojure.string :as str]))

(defn get-input
  [filename]
  (slurp (io/file (format "data/%s" filename))))


(def samples {"mjqjpqmgbljsphdztnvjfqwrcgsmlb" 7,
              "bvwbjplbgvbhsrlpgdmjqwftvncz" 5,
              "nppdvjthqldpwncqszvftbrmjlhg" 6,
              "nznrnfrfntjfmvfwmzdfjlvtqnbhcprsg" 10,
              "zcfzfwzzqfrljwzlrfnpqdbhtmscgvjw" 11})

(defn get-first-distinct
  [sample subvec-length]
  (let [sample (str/split sample #"")]
    (loop [idx 0]
      (if (apply distinct? (subvec sample idx (+ subvec-length idx)))
        (+ idx subvec-length)
        (recur (inc idx))))))

(doseq [[k v] samples]
  (assert (= v (get-first-distinct k 4))))

(-> (get-input "day06.txt")
    (get-first-distinct 4))

(def samples2 {"mjqjpqmgbljsphdztnvjfqwrcgsmlb" 19,
               "bvwbjplbgvbhsrlpgdmjqwftvncz" 23,
               "nppdvjthqldpwncqszvftbrmjlhg" 23,
               "nznrnfrfntjfmvfwmzdfjlvtqnbhcprsg" 29,
               "zcfzfwzzqfrljwzlrfnpqdbhtmscgvjw" 26})

(doseq [[k v] samples2]
  (assert (= v (get-first-distinct k 14))))

(-> (get-input "day06.txt")
    (get-first-distinct 14))
