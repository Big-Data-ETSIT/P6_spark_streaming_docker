
// The simplest possible sbt build file is just one line:

scalaVersion := "2.12.15"

name := "word-consult-streaming"
version := "1.0"

val sparkVersion = "3.1.2"

mainClass in Compile := Some("es.upm.dit.WordCountStreamingJob")

libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-core" % sparkVersion,
  "org.apache.spark" %% "spark-sql" % sparkVersion,
  "org.apache.spark" % "spark-streaming_2.12" % sparkVersion 
)
