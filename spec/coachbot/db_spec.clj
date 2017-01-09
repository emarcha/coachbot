;
; Copyright (c) 2016, Courage Labs, LLC.
;
; This file is part of CoachBot.
;
; CoachBot is free software: you can redistribute it and/or modify
; it under the terms of the GNU Affero General Public License as published by
; the Free Software Foundation, either version 3 of the License, or
; (at your option) any later version.
;
; CoachBot is distributed in the hope that it will be useful,
; but WITHOUT ANY WARRANTY; without even the implied warranty of
; MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
; GNU Affero General Public License for more details.
;
; You should have received a copy of the GNU Affero General Public License
; along with CoachBot.  If not, see <http://www.gnu.org/licenses/>.
;

(ns coachbot.db-spec
  (:require [coachbot.db :as db]
            [coachbot.mocking :refer :all]
            [clojure.java.jdbc :as jdbc]
            [speclj.core :refer :all]
            [taoensso.timbre :as log])
  (:import (java.io File)))

;; Change :error to :info to see the full schema
(describe-with-level :error "Database Schema"
  (with-clean-db [ds]
    (it "should get the full schema"
      (jdbc/query @ds ["script simple nodata to 'schema.sql'"])
      (let [f (File. "schema.sql")
            _ (log/info (slurp f))
            exists? (.exists f)
            deleted? (.delete f)]
        (should exists?)
        (should deleted?)))))

#_(describe-with-level :error "My local mysql db"
  ; create database coachbot default character set utf8;
  (with-all ds (db/make-db-datasource "mysql"
                                      "jdbc:mysql://localhost/coachbot"
                                      "root" ""))
  (it "should load the schema"
    (should= #{"bq_question_groups" "channel_questions" "question_answers"
               "question_groups" "scu_question_groups" "slack_teams"
               "questions_asked" "custom_questions" "schema_version"
               "base_questions" "channel_question_answers"
               "channel_questions_asked" "slack_coaching_users" "user_activity"
               "slack_coaching_channels"}
             (into #{}
                   (map :tables_in_coachbot
                        (jdbc/query @ds ["show tables"]))))))