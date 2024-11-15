(ns convey.interop
  (:require [goog.async.nextTick]))

(def next-tick goog.async.nextTick)
(def empty-queue #queue [])
