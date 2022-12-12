(ns day07
  (:require [clojure.java.io :as io]
            [clojure.string :as str]
            [clojure.edn :as edn]))

(def initialize-directory {:files []})

(defn get-file-object
  [line]
  (cond
    (str/starts-with? line "$") nil
    (str/starts-with? line "dir ") (last (str/split line #" "))
    :else (let [tmp (str/split line #" ")
                size (edn/read-string (first tmp))
                name (last tmp)]
            (list name size))))

(defn update-file-system
  [file-object cwd file-system]
  (cond
    (nil? file-object) file-system
    (list? file-object) (update-in file-system (conj cwd :files) #(conj % file-object))
    (string? file-object) (update-in file-system (conj cwd file-object) initialize-directory)
    ))

(defn update-cwd
  [line cwd]
  (cond
    (= line "$ cd ..") (pop cwd)
    (str/starts-with? line "$ cd ") (conj cwd (last (str/split line #" ")))
    :else cwd))

;; (defn get-index
;;   [lists index]
;;   (if (int? index)
;;     (get lists index)
;;     (loop [lists lists index index]
;;       (if (empty? index)
;;         lists
;;         (recur (get lists (first index)) (pop index))))))

(defn get-input
  [filename]
  (let [lines (str/split-lines (slurp (io/file (format "data/%s" filename))))]
    (loop [lines lines cwd [] file-system {"/" initialize-directory}]
      (if (empty? lines)
        file-system
        (let [line (first lines)
              object (get-file-object line)
              file-system (update-file-system object cwd file-system)]
          (println "aaa" (update-cwd line cwd) object)
          (println "ccc" file-system)
          (recur (rest lines) (update-cwd line cwd) file-system))))))

(def bbb (get-input "day07_sample.txt"))

(get-in bbb ["/" "a" "e"])

(def aaa
  {"/" {:files '('("b.txt" 14848514)
                 '("c.dat" 8504156))
        :size (+ 14848514 8504156 29116 2557 62596 584 4060174 8033020 5626152 7214296)
        "a" {:files '('("f" 29116)
                      '("g" 2557)
                      '("h.lst" 62596))
             :size (+ 29116 2557 62596 584)
             "e" {:files '('("i" 584)) :size 584}}
        "d" {:files '('("j" 4060174)
                      '("d.log" 8033020)
                      '("d.ext" 5626152)
                      '("k" 7214296))
             :size (+ 4060174 8033020 5626152 7214296)}}})

;; aaa

;; (get-index aaa '("/" "a" :files))
;; (update-in aaa ["/" "a" :files] #(conj % '(1 1)))
;; (update-in aaa ["/" "a" "bbbbb"] (initialize-directory))


"
- / (dir)
  - a (dir)
    - e (dir)
      - i (file, size=584)
    - f (file, size=29116)
    - g (file, size=2557)
    - h.lst (file, size=62596)
  - b.txt (file, size=14848514)
  - c.dat (file, size=8504156)
  - d (dir)
    - j (file, size=4060174)
    - d.log (file, size=8033020)
    - d.ext (file, size=5626152)
    - k (file, size=7214296)

$ cd /
$ ls
dir a
14848514 b.txt
8504156 c.dat
dir d
$ cd a
$ ls
dir e
29116 f
2557 g
62596 h.lst
$ cd e
$ ls
584 i
$ cd ..
$ cd ..
$ cd d
$ ls
4060174 j
8033020 d.log
5626152 d.ext
7214296 k
"