(ns day11)

(defn divisable-by? [divisor dividend] (if (zero? (mod dividend divisor)) true false))

(defn apply-relief [worry] (int (/ worry 3)))

(defn determine-throws
  [monkey]
  (let [test-results (map (comp (:test monkey) apply-relief (:op monkey)) (:items monkey))]
    (for [res test-results]
      (if res (:true monkey) (:false monkey)))))

(defn process-throws
  [monkeys from to values]
  (loop [monkeys monkeys to to values values]
    (let [first-to (first to)
          first-value (first values)]
      (if (nil? first-to)
        ;; increments inspections count and zeroes out items
        (assoc-in (update-in monkeys [from :inspections] #(+ % (count (get-in monkeys [from :items])))) [from :items] [])
        (recur (update-in monkeys [first-to :items] #(conj % first-value)) (rest to) (rest values))))))

(defn do-monkey
  [monkeys monkey-idx]
  (let [monkey (get monkeys monkey-idx)]
    (process-throws
     monkeys
     monkey-idx
     (determine-throws (get monkeys monkey-idx))
     (map (comp apply-relief (:op monkey)) (:items monkey)))))

(defn do-round
  [monkeys]
  (let [num-monkeys (reduce max (keys monkeys))]
    (loop [monkeys monkeys idx 0]
      (if (> idx num-monkeys)
        monkeys
        (recur (do-monkey monkeys idx) (inc idx))))))

(defn do-rounds
  [rounds monkeys]
  (loop [monkeys monkeys rounds rounds]
    (if (zero? rounds)
      monkeys
      (recur (do-round monkeys) (dec rounds)))))

(defn part-one
  [monkeys]
  (let [res (do-rounds 20 monkeys)]
    (->>
     (for [k (keys res)]
       (get-in res [k :inspections]))
     (sort)
     (take-last 2)
     (reduce *))))

(def monkeys_samples
  {0 {:items [79 98]
      :op #(* % 19)
      :test (partial divisable-by? 23)
      :true 2
      :false 3
      :inspections 0}
   1 {:items [54 65 75 74]
      :op #(+ % 6)
      :test (partial divisable-by? 19)
      :true 2
      :false 0
      :inspections 0}
   2 {:items [79 60 97]
      :op #(* % %)
      :test (partial divisable-by? 13)
      :true 1
      :false 3
      :inspections 0}
   3 {:items [74]
      :op #(+ % 3)
      :test (partial divisable-by? 17)
      :true 0
      :false 1
      :inspections 0}})

(def monkeys
  {0 {:items [76 88 96 97 58 61 67]
      :op #(* % 19)
      :test (partial divisable-by? 3)
      :true 2
      :false 3
      :inspections 0}
   1 {:items [93 71 79 83 69 70 94 98]
      :op #(+ % 8)
      :test (partial divisable-by? 11)
      :true 5
      :false 6
      :inspections 0}
   2 {:items [50 74 67 92 61 76]
      :op #(* % 13)
      :test (partial divisable-by? 19)
      :true 3
      :false 1
      :inspections 0}
   3 {:items [76 92]
      :op #(+ % 6)
      :test (partial divisable-by? 5)
      :true 1
      :false 6
      :inspections 0}
   4 {:items [74 94 55 87 62]
      :op #(+ % 5)
      :test (partial divisable-by? 2)
      :true 2
      :false 0
      :inspections 0}
   5 {:items [59 62 53 62]
      :op #(* % %)
      :test (partial divisable-by? 7)
      :true 4
      :false 7
      :inspections 0}
   6 {:items [62]
      :op #(+ % 2)
      :test (partial divisable-by? 17)
      :true 5
      :false 7
      :inspections 0}
   7 {:items [85 54 53]
      :op #(+ % 3)
      :test (partial divisable-by? 13)
      :true 4
      :false 0
      :inspections 0}})

(assert (= (part-one monkeys_samples) 10605))
(part-one monkeys)

;; add 23 to make the test work
(defn apply-relief [worry] (mod worry (reduce * [23 3 11 19 5 2 7 17 13])))

(defn part-two
  [rounds monkeys]
  (let [res (do-rounds rounds monkeys)]
    (->>
     (for [k (keys res)]
       (get-in res [k :inspections]))
     (sort)
     (take-last 2)
     (reduce *)
     )))

(assert (= (part-two 10000 monkeys_samples) 2713310158))
(part-two 10000 monkeys)
