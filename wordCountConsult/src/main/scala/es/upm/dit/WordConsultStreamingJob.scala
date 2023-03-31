package es.upm.dit
import org.apache.spark.sql.SparkSession

object WordConsultStreamingJob {
  val inputFile = "../wordCountConsult/csv/wordsCount.csv"
  final val HOST = sys.env.getOrElse("SERVER", "localhost")
  final val MASTER = sys.env.getOrElse("SPARK_MASTER", "local[*]")
  def main(args: Array[String]): Unit = {

    print("STARTING SPARK STRUCTURED STREAMING PROGRAM")

    val spark:SparkSession = SparkSession.builder()
      .master(MASTER)
      .appName("SparkByExample")
      .getOrCreate()

    spark.sparkContext.setLogLevel("ERROR")

    val wordsDf = spark.read
      .option("header", true)
      .csv(inputFile)

    val streamingDf = spark.readStream
      .format("socket")
      .option("host",HOST)
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