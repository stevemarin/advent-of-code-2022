(ns day07
  (:require [clojure.java.io :as io]
            [clojure.string :as str]
            [clojure.edn :as edn]))

(def initialize-directory {:files [] :size 0})

(defn get-file-object
  [line]
  (cond
    (str/starts-with? line "$") nil
    (str/starts-with? line "dir ") (last (str/split line #" "))
    :else (let [tmp (str/split line #" ")
                size (edn/read-string (first tmp))
                name (last tmp)]
            (list name size))))

(defn parent-paths
  [cwd]
  (loop [cwd cwd
         out []]
    (if (empty? cwd)
      out
      (recur (pop cwd) (conj out cwd)))))

(defn update-file-system
  [file-object cwd file-system]
  (cond
    ;; if it's not a file list or directory, just return current file system
    (nil? file-object) file-system
    ;; if it's a file & size, append
    (list? file-object) (let [file-system (update-in file-system (conj cwd :files) #(conj % file-object))]
                          ;; file-system
                          (loop [file-system file-system paths (parent-paths cwd)]
                            (if (empty? paths)
                              file-system
                              (recur (update-in file-system (conj (peek paths) :size) + (last file-object)) (pop paths)))))
                          ;; )
    ;; and if it's a directory, create and add it
    (string? file-object) (assoc-in file-system (conj cwd file-object) initialize-directory)))

(defn update-cwd
  [line cwd]
  (cond
    (= line "$ cd ..") (pop cwd)
    (str/starts-with? line "$ cd ") (conj cwd (last (str/split line #" ")))
    :else cwd))

(defn flatten-keys [m]
  (if (not (map? m))
    {[] m}
    (into {}
          (for [[k v] m
                [ks v'] (flatten-keys v)]
            [(cons k ks) v']))))

(defn get-input
  [filename]
  (let [lines (str/split-lines (slurp (io/file (format "data/%s" filename))))]
    (loop [lines lines cwd [] file-system {"/" initialize-directory}]
      (if (empty? lines)
        file-system
        (let [line (first lines)
              object (get-file-object line)
              file-system (update-file-system object cwd file-system)]
          (recur (rest lines) (update-cwd line cwd) file-system))))))

(defn part-one
  [path]
  (->> (get-input path)
       (flatten-keys)
       (filter (comp integer? last))
       (filter (comp #(> 100000 %) last))
       (map last)
       (reduce +)))

(part-one "day07_sample.txt")
(part-one "day07.txt")

(defn part-two
  [path]
  (let [input (get-input path)]
  (->> input
       (flatten-keys)
       (filter (comp integer? last))
       (sort-by last)
       (filter (comp #(>= % (- 30000000 (- 70000000 (get-in input ["/" :size])))) last))
       (first)
       )))

(part-two "day07_sample.txt")
(part-two "day07.txt")
