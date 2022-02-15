package esgi.circulation

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions.{dayofmonth, month, to_timestamp, year}


object Clean {
  def main(args: Array[String]): Unit = {
    // TODO : créer son SparkSession
    val spark = SparkSession
      .builder()
      .appName("clean")
      .master(master = "yarn")
      .getOrCreate()
    import spark.implicits._

    val inputFile = args(0)
    val outputFile = args(1)


    var df = spark.read.option("inferSchema", "true").option("header", "true").csv(inputFile)
    df = df.withColumn("year", year(to_timestamp($"date_debut", "yyyy/MM/dd")))
    df = df.withColumn("month", month(to_timestamp($"date_debut", "yyyy/MM/dd")))
    df = df.withColumn("day", dayofmonth(to_timestamp($"date_debut", "yyyy/MM/dd")))

    df.show()

    //    df.write.parquet(outputFile)
    df.write.partitionBy("year", "month", "day")
      .parquet(outputFile)
  }
}
