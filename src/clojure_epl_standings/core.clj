(ns clojure-epl-standings.core
  (:require [clojure.contrib.str-utils2 :as str2])
  (:require [net.cgrand.enlive-html :as html])
  )

(def *espnstar-url* (str "http://www.espnstar.com/football/premier-league/standings/"))

;;function to fetch the html content of the url
(defn fetch-espnstar-data []
  (html/html-resource (java.net.URL. *espnstar-url*)))

;;css selector for the table
(def *standings-table-selector* [:table.full-table])

;;css selector for all the rows
(def *row-team-stats-selector* [:tbody :tr])

;;html content for the standings table
(defn standings-table-data []
  (html/select (fetch-espnstar-data) *standings-table-selector*))


;;each row of the standings table. each row represents stats of one team
(defn all-team-stats-in-rows []
  (html/select (standings-table-data) *row-team-stats-selector*))

;; all the rows of the table
(def all-rows (map vector (all-team-stats-in-rows)))

;; removing empty cell from the row
(defn remove-first-cell-from-row [row]
  (rest (:content (first row))))

;; function to print all cells of the row which contains the stats
(defn print-single-team-stats [html]
  (let [content (:content (first html)) next-html (rest html)]    
    (print content " ")
    (if(nil? content)
      ( println)
      (recur (rest html)))))

;;function to print all the stats
(defn print-all-team-stats [rows]
   (map print-single-team-stats (map remove-first-cell-from-row rows))
   )

;; main function
(defn -main [& args]
  (print-all-team-stats all-rows))
  
