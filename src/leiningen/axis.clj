(ns leiningen.axis
  (:use [clojure.java.shell :only [sh]]
	[clojure.string :only [join]]
	[leiningen.classpath :only [get-classpath]]))

(def *WSDL2Java-class* "org.apache.axis.wsdl.WSDL2Java")

(defn- cmd
  "Convert the [wsdl target-package] vector from project.clj to a WSDL2Java call."
  [p [wsdl package extra]]
  (concat
   ["java" "-cp" (join java.io.File/pathSeparatorChar (get-classpath p)) *WSDL2Java-class*
    "-o" (get p :java-source-path "src/java")
    "-p" package
    wsdl]
   extra))

(defn axis
  "Use Apache Axis to generate Java classes for WSDL files.

You will need to add Apache Axis to your project as a dependency, e.g.:
...
  :dependencies [[org.clojure/clojure \"1.2.0\"]
                 [org.clojure/clojure-contrib \"1.2.0\"]
		 [axis/axis \"1.4\"]]

Then, to configure what WSDL files to use and where to put the generated
source files:
...
  :java-source-path \"src/java\"
  :axis [[\"src/wsdl/myservice.wsdl\" \"generated.myservice\"]
	 [\"src/wsdl/myotherservice.wsdl\" \"generated.myotherservice\"]]

Note that :java-source-path is used by the lein-javac plug-in, which is
probably the easiest way to turn the Java code generated into compiled
classes. lein-axis uses this setting as the target directory (for the
root of the generated packages). src/java is used as a default if you
don't provide this value.
"
  [project]
  (doseq [cmd-out (map (comp (partial apply sh)
			     (partial cmd project))
		       (:axis project))]
    (print (str (:err cmd)))))

