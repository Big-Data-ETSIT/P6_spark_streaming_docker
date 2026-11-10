scalaVersion := "2.13.18"

name := "word-consult-streaming"
version := "1.0"

val sparkVersion = "4.1.1"

// Sintaxis moderna recomendada para sbt 1.x
Compile / mainClass := Some("es.upm.dit.WordConsultStreamingJob")

libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-core" % sparkVersion,
  "org.apache.spark" %% "spark-sql" % sparkVersion
)
