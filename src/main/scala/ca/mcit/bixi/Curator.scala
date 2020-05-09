package ca.mcit.bixi

import org.apache.kafka.clients.consumer.{ConsumerConfig, ConsumerRecord}
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.sql.SparkSession
import org.apache.spark.streaming.dstream.{DStream, InputDStream}
import org.apache.spark.SparkContext
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.streaming.kafka010.{ConsumerStrategies, KafkaUtils, LocationStrategies}

object Curator extends App {

  val kafkaParams = Map[String, String](
    ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG -> "172.16.129.58:9092",
    ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG -> classOf[StringDeserializer].getName,
    ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG -> classOf[StringDeserializer].getName,
    ConsumerConfig.GROUP_ID_CONFIG -> "bix",
    ConsumerConfig.AUTO_OFFSET_RESET_CONFIG -> "earliest",
    ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG -> "false")
  val topic = "enriched_record_satya"
  val spark: SparkSession = SparkSession
    .builder()
    .appName("Sample Spark SQL")
    .master("local[*]")
    .getOrCreate()
  val sc: SparkContext = spark.sparkContext
  val ssc: StreamingContext = new StreamingContext(sc, Seconds(5))
  val stream: InputDStream[ConsumerRecord[String, String]] = KafkaUtils.createDirectStream[String, String](
    ssc,
    LocationStrategies.PreferConsistent,
    ConsumerStrategies.Subscribe[String, String](Array(topic), kafkaParams))
  val ssDF = spark.read
    .format("csv")
    .option("header", "true")
    .option("inferschema", "true")
    .load("/user/cloudera/summer2019/satya_bixifeed/stationcsv") //station_status.csv from HiveCli JSON to CSV
  ssDF.createOrReplaceTempView("b")
  ssDF.show(1)
  val x: DStream[String] = stream.map(record => record.value())
  x.foreachRDD(microRdd => {
    import spark.implicits._
    val df = microRdd
      .map(_.split(",", -1))
      .map(x => Enrichedtrip(x(0), x(1), x(2), x(3), x(4), x(5), x(6), x(7), x(8), x(9), x(10), x(11), x(12),
        x(13), x(14), x(15), x(16), x(17), x(18), x(19), x(20), x(21), x(22)))
      .toDF
    df.show(3)
    df.createOrReplaceTempView("a")
    val enrichedbixi = spark.sql("select a.start_date,a.start_station_code,a.end_date,a.end_station_code,a.duration_sec,a.is_member," +
      "a.system_id,a.language,a.system_startDate,a.timezone,a.station_Id,a.short_name,a.capacity,a.latitude,a.longitude,a.surcharge_waiver,a.key_dispenser,a.kiosk,a.extr_id,a.rental_method_a,a.rental_method_b," +
      "b.station_id as sidd,b.num_bikes_available,b.num_ebikes_available,b.num_bikes_disabled,b.num_docks_available,b.num_docks_disabled, " +
      "b.is_installed,b.is_renting,b.is_returning,b.last_reporte,b.keyms " +
      "from  a left outer join b on a.station_Id = b.station_id")
    enrichedbixi.toDF()
    enrichedbixi.show(1)
    enrichedbixi.coalesce(1).write.mode("append").option("header", "true").csv(("/user/cloudera/summer2019/satya_bixifeed/finalres"))
  })
  ssc.start()
  ssc.awaitTermination()
}

