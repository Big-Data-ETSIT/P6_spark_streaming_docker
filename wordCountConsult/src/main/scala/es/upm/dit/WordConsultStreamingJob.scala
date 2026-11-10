package es.upm.dit

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions.broadcast // <- Importación necesaria

object WordConsultStreamingJob {
  val inputFile = "../wordCountConsult/csv/wordsCount.csv"
  final val HOST = sys.env.getOrElse("SERVER", "localhost")
  final val MASTER = sys.env.getOrElse("SPARK_MASTER", "local[*]")

  def main(args: Array[String]): Unit = {

    println("STARTING SPARK STRUCTURED STREAMING PROGRAM (OPTIMIZED)")

    val spark: SparkSession = SparkSession.builder()
      .master(MASTER)
      .appName("WordConsultStreamingJob")
      .getOrCreate()

    spark.sparkContext.setLogLevel("ERROR")

    import spark.implicits._

    // 1. Cargar el DataFrame estático de palabras
    val wordsDf = spark.read
      .option("header", "true")
      .option("inferSchema", "true")
      .csv(inputFile)

    // 2. Configurar el stream desde el socket
    val streamingDf = spark.readStream
      .format("socket")
      .option("host", HOST)
      .option("port", "9090")
      .load()
      .withColumnRenamed("value", "Word")

    // 3. Join optimizado aplicando broadcast() al DataFrame estático
    val innerDf = streamingDf.join(broadcast(wordsDf), Seq("Word"), "inner")

    // 4. Ejecutar el stream hacia la consola
    val query = innerDf.writeStream
      .format("console")
      .outputMode("append")
      .start()

    query.awaitTermination()
  }
}
