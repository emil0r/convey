(ns build
  (:require
    [clojure.tools.build.api :as b]))

(def version "2024.11.15")
(def target "target")
(def classes (str target "/classes"))
(def lib 'org.clojars.emil0r/convey)
(def jar-file (format "target/%s.jar" (name lib)))

(defn jar-opts
  [opts]
  (let [basis    (b/create-basis opts)
        src-dirs (:paths basis)]
    (assoc opts
           :basis basis
           :target-dir classes
           :class-dir classes
           :lib lib
           :version version
           :src-dirs src-dirs
           :jar-file jar-file)))

(defn clean
  [_]
  (b/delete {:path target}))

(defn jar
  "Build the library jar.
   Make sure to update the version number before building and deploying a new version."
  [opts]
  (let [opts (jar-opts opts)]
    (println (format "Writing pom file to %s" (:class-dir opts)))
    (b/write-pom opts)
    (println (format "Copying %s to %s" (:src-dirs opts) (:target-dir opts)))
    (b/copy-dir opts)
    (println (format "Writing jar file %s with contents from %s" (:jar-file opts) (:class-dir opts)))
    (b/jar opts)))
