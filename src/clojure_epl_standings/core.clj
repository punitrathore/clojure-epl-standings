(ns clojure-epl-standings.core
  (:require [clojure.contrib.str-utils2 :as str2])
  (:require [net.cgrand.enlive-html :as html])
  (:require [clojure.contrib.pprint])
  )

(def *espnstar-url* (str "http://www.espnstar.com/football/premier-league/standings/"))

;;function to fetch the html content of the url
(defn fetch-espnstar-data []
  (html/html-resource (java.net.URL. *espnstar-url*)))

;;css selector for the table
(def *standings-table-selector* [:table.full-table])

;;css selector for header of table
(def *standings-table-header-selector* [:thead :tr])

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

;; full html of header
(def header-row (map vector (html/select (standings-table-data) *standings-table-header-selector*)))

;; header content to print
(def header-content (rest (rest (:content (first (first header-row))))))

;; removing empty cell from the row
(defn remove-first-cell-from-row [row]
  (rest (:content (first row))))

;; function to print all cells of the row which contains the stats
(defn print-single-team-stats [html]
  (let [content (:content (first html)) next-html (rest html)]
    (if (not (nil? content))
      (print content " "))
    (if(nil? content)
      (newline)
      (recur (rest html)))))

;;function to print all the stats
(defn print-all-team-stats [rows]
   (map print-single-team-stats (map remove-first-cell-from-row rows)))

;; first field is different than other fields, hence needs to be
;; printed out separately
(defn print-first-header-field [html-content]
  (print (:content html-content) " "))

;; rest of header fields whose styling is consistent
(def rest-header-content (rest header-content))

;;prints content of one header field
(defn print-header-field [html]
(print (:content (first (:content (first html))))  " "))

;;prints all the contents of the header fields which have consistent styling
(defn print-rest-of-header-fields [html]
  (let [next-html (rest html)]
   (print-header-field html)
  (if (= "\n" (first next-html))
    (newline)
    (recur next-html))))

;;printing all the stats for all teams
(defn print-team-standings []
  (print "(Position) ")
  (print-first-header-field (first header-content))
  (print-rest-of-header-fields rest-header-content)
  (print-all-team-stats all-rows))

;; main function
(defn -main [& args]
  (print-team-standings))
  
