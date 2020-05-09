package ca.mcit.bixi
import java.util.Properties
import org.apache.kafka.clients.consumer.{ConsumerRecords, KafkaConsumer}
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import scala.collection.JavaConverters._
import scala.io.Source

object Enricher extends  App {
  val property: Properties = new Properties()
  property.put("bootstrap.servers", "172.16.129.58:9092")
  property.put("group.id", "etzn")
  property.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
  property.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
  property.put("auto.offset.reset", "earliest")
  val consumer = new KafkaConsumer[String, String](property)

  val producerProp: Properties = new Properties()
  producerProp.put("bootstrap.servers", "172.16.129.58:9092")
  producerProp.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
  producerProp.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")
  val producer = new KafkaProducer[String, String](producerProp)

  consumer.subscribe(List("Btrip").asJava)
  val stationlist: Iterator[String] = Source.fromFile("/home/bd-user/Downloads/Bixi/kafka/000000_0").getLines()
  var MyMap: Map[String, Any] = Map()
  while (stationlist.hasNext) {
    val stationNext = Station(stationlist.next)
    val x = Station.toCsv(stationNext)
    MyMap ++= Map(stationNext.short_name -> x)
  }
  while (true) {
    val polledRecords: ConsumerRecords[String, String] = consumer.poll(1000) //println("Batch start => " + new DateTime())
    polledRecords.forEach(consumerRecord => { //println(consumerRecord.value())
      val cr = consumerRecord.value().toString;
      val sr = MyMap.getOrElse(Trip(cr).start_station_code, ",,,,,,,,,,,,,,,,,,,,,, ")
      val y = cr + "," + sr
      println(y)
      val producedMessage = new ProducerRecord[String, String]("enriched_record_satya", y)
      producer.send(producedMessage)
    })
    consumer.commitSync()
    Thread.sleep(4000)
  }
}


