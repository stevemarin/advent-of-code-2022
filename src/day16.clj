(ns day16
  (:require [clojure.java.io :as io]
            [clojure.string :as s]
            [clojure.edn :as edn]
            [clojure.set :as set]
            [clojure.data.priority-map :as pmap])
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

(defn update-state
  [cave state neighbor]
  (let [{:keys [location visited minutes released]} state
        distance (get-in cave [location :neighbors neighbor])
        remaining-minutes (- minutes distance 1)
        flow (get-in cave [neighbor :flow])]
    {:location neighbor
     :visited (conj visited neighbor)
     :minutes remaining-minutes
     :released (+ released (* remaining-minutes flow))}))

(defn search
  [cave states]
  (let [pm (pmap/priority-map-by > (first states) 0)
        completed '()]
    (loop [pm pm completed completed]
      (if (empty? pm)
        completed
        (let [state (first (peek pm))
              pm (pop pm)
              {:keys [location visited]} state
              neighbors (-> cave (get-in [location :neighbors]) keys set)
              updated-states (->> (set/difference neighbors visited)
                                  (map #(update-state cave state %))
                                  (filter #(not (neg? (:minutes %))))
                                  (map #(list % (:released %))))]
          (if (empty? updated-states)
            (recur pm (conj completed state))
            (recur (into pm updated-states) completed)))))))

(defn part1
  [filename]
  (let [cave (update-cave (parse-input filename))
        starting-state {:location "AA" :visited #{"AA"} :minutes 30 :released 0}
        results (search cave [starting-state])]
    (->> results
         (map :released)
         (reduce max))))

(defn distinct-path-sum
  [paths]
  (let [vr (->> (map (fn [{:keys [visited released]}] {visited released}) paths)
                (reduce (partial merge-with max))
                seq)]
    (->> (for [[v1 r1] vr
               [v2 r2] vr
               ;; all paths visit "AA", so we want paths that only intersect there
               ;; and are otherwise disjoint
               :when (#(= #{"AA"} %) (set/intersection v1 v2))]
           (+ r1 r2))
         (reduce max))))

(defn part2
  [filename]
  (let [cave (update-cave (parse-input filename))
        starting-state {:location "AA" :visited #{"AA"} :minutes 26 :released 0}
        paths (search cave [starting-state])]
    (distinct-path-sum paths)))

(println (part1 "day16_sample.txt"))
(println (part1 "day16.txt"))
;; this part doesn't work, since the cave is small
;; and a single person can reach all valves and
;; we expect disjoint paths, we'd need more complex
;; logic to allow early stopping/cooperation
;; (part2 "day16_sample.txt")
(println (part2 "day16.txt"))
