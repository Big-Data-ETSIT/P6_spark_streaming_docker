package es.upm.dit
import org.apache.spark.sql.SparkSession

object WordConsultStreamingJob {
  final val BASE_PATH = "../../P6_spark_streaming_docker"
  final val inputFile = BASE_PATH + "/wordCountConsult/csv/wordsCount.csv"
  final val outputFile = BASE_PATH + "/result/result.csv"
  def main(args: Array[String]): Unit = {

    print("STARTING SPARK STRUCTURED STREAMING PROGRAM")

    val spark:SparkSession = SparkSession.builder()
      .master("local[*]")
      .appName("SparkByExample")
      .getOrCreate()

    spark.sparkContext.setLogLevel("ERROR")

    val wordsDf = spark.read
      .option("header", true)
      .csv(inputFile)

    val streamingDf = spark.readStream
      .format("socket")
      .option("host","localhost")
      .option("port","9090")
      .load()
      .withColumnRenamed("value", "Word")

    val innerDf = streamingDf.join(wordsDf, "Word")

    innerDf.writeStream
      .format("console")
      .start()
      .awaitTermination()
      
  }
}