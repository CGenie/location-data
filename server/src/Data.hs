{-# LANGUAGE TemplateHaskell #-}

module Data where

import Control.Lens


data GeoLocation = GeoLocation {
    _latitude :: Double,
    _longitude :: Double
}
makeLenses ''GeoLocation
