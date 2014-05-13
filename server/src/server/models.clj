(ns server.models
  (:use (korma [core :only [database defentity entity-fields pk table]])
        (lobos core config connectivity)))

(defentity location-data
  (pk :id)
  (table :location_data)
  (database db)
  (entity-fields :timestamp :latitude :longitude))

