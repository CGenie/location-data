(ns lobos.migrations
  (:refer-clojure :exclude [alter drop
                            bigint boolean char double float time])
  (:use (lobos [migration :only [defmigration]] core schema
               config helpers)))

(defmigration add-location-data-table
  (up [] (create
          (table :location_data
                 (integer :location_data_id :auto-inc :primary-key)
                 (varchar :device_id 200)
                 (timestamp :timestamp)
                 (double-precision :latitude)
                 (double-precision :longitude)
                 )))
  (down [] (drop (table :location_data))))
