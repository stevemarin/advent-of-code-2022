(ns day15
  (:require [clojure.java.io :as io]
            [clojure.string :as s]
            [clojure.edn :as edn])
  (:import org.ejml.ops.DSemiRings
           org.ejml.data.DMatrixSparseCSC
           org.ejml.sparse.csc.CommonOpsWithSemiRing_DSCC))

(defn parse-line [line]
  (rest (first (re-seq #"Valve ([A-Z]+) has flow rate=([0-9]+); tunnel[s]? lead[s]? to valve[s]? (.*)" line))))

(defn into-valve-map
  [l]
  (let [[name flow neighbors] l
        flow (edn/read-string flow)
        neighbors (s/split neighbors #", ")]
    {name {:flow flow :neighbors (zipmap neighbors (repeat 1))}}))

(defn parse-input [filename]
  (->> (format "data/%s" filename)
       io/reader
       line-seq
       (map parse-line)
       (map into-valve-map)
       (apply merge)))

(defn floyd-warshall [cave name-to-idx]
  (let [node-names (sort (keys cave))
        num-nodes (count node-names)
        adj (new DMatrixSparseCSC num-nodes num-nodes)
        out (new DMatrixSparseCSC num-nodes num-nodes)]

    (doseq [i (range num-nodes)
            j (range num-nodes)]
      (.set adj i j 999.9))

    (doseq [[name node] (seq cave)]
      (doseq [[neighbor distance] (:neighbors node)]
        (.set adj (get name-to-idx name) (get name-to-idx neighbor) distance)))

    (doseq [i (range num-nodes)]
      (.set adj i i 0.0))

    (CommonOpsWithSemiRing_DSCC/mult adj adj out DSemiRings/MIN_PLUS)

    (doseq [_ (range num-nodes)]
      (CommonOpsWithSemiRing_DSCC/mult adj out out DSemiRings/MIN_PLUS))

    out))



(defn update-neighbors
  [cave pairwise node name-to-idx]
  (let [non-zero-nodes (for [[node {:keys [flow]}] (seq cave) :when (pos? flow)] node)
        idx (get name-to-idx node)
        dists (reduce merge (for [non-zero-node non-zero-nodes :when (not= node non-zero-node)]
                              {non-zero-node (.get pairwise idx (get name-to-idx non-zero-node))}))]
    {node {:flow (get-in cave [node :flow]) :neighbors dists}}))

(defn update-cave
  [cave]
  (let [node-names (sort (keys cave))
        num-nodes (count node-names)
        name-to-idx (zipmap node-names (range num-nodes))
        pairwise (floyd-warshall cave name-to-idx)
        updated-neighbors (for [[node {:keys [flow]}] cave :when (or (= node "AA") (pos? flow))]
                            (update-neighbors cave pairwise node name-to-idx))]
    (reduce merge updated-neighbors)))

(def cave (parse-input "day16_sample.txt"))
cave
(def cave (update-cave (parse-input "day16_sample.txt")))

(def k (sort (keys cave)))
(def bits (reduce merge (for [[n i] (partition 2 (interleave k (range (count k))))] {n (bit-shift-left 1 i)})))
(def flows (reduce merge (for [[n {:keys [flow]}] (sort-by :flow cave)] {n flow})))

bits
flows

(defn greedy-sort-func [minutes distance flow]
  (* (max (- minutes distance) 0) flow))

(defn get-scores
  [cave node minutes mask pressure scores level]
  (let [scores (assoc scores mask (max (or (get scores mask) 0) pressure))
        neighbors (get-in cave [node :neighbors])]
    (println "opening..." node "with" minutes "minutes and level" level)
    (loop [neighbors neighbors
           minutes minutes
           mask mask
           pressure pressure
           scores scores]
      (let [[[next-node distance] & neighbors] neighbors
            next-node-bits (get bits next-node)]
        (cond
          (nil? next-node) scores
          (and (> (- minutes distance 1) 0) (zero? (bit-and next-node-bits mask)))
          (get-scores cave
                      next-node
                      (- minutes distance 1)
                      (bit-or next-node-bits mask)
                      (+ pressure (* (- minutes distance 1) (get flows next-node)))
                      scores
                      (inc level))
          :else (recur neighbors minutes mask pressure scores))))))

(get-scores cave "AA" 30 0 0 {} 0)

;; (def node-names (sort (keys cave)))
;; (def num-nodes (count node-names))
;; (def name-to-idx (zipmap node-names (range num-nodes)))
;; (def pairwise (floyd-warshall cave name-to-idx))

(Integer/toString 126 2)

(dissoc {:a 1 :b 2 :g 1 :f 4} :f :g)