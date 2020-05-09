package ca.mcit.bixi

case class Station(
                    system_Id: String,
                    language: String,
                    url: String,
                    system_startDate: String,
                    timezone: String,
                    station_Id: String,
                    station_name: String,
                    short_name: String,
                    capacity: String,
                    latitude: String,
                    longitude: String,
                    surcharge_waiver: String,
                    key_dispenser: String,
                    kiosk: String,
                    extr_id: String,
                    rental_method_a: String,
                    rental_method_b: String
                  )

object Station {
  def apply(csvLine: String): Station = {
    val s = csvLine.split(",", -1)
    new Station(s(0), s(1), s(2), s(3), s(4), s(5), s(6), s(7), s(8), s(9), s(10),
      s(11), s(12), s(13), s(14), s(15), s(16))
  }

  def toCsv(station: Station): String = {
    station.system_Id + "," +
      station.language + "," +
      station.url + "," +
      station.system_startDate + "," +
      station.timezone + "," +
      station.station_Id + "," +
      station.station_name + "," +
      station.short_name + "," +
      station.capacity + "," +
      station.latitude + "," +
      station.longitude + "," +
      station.surcharge_waiver + "," +
      station.key_dispenser + "," +
      station.kiosk + "," +
      station.extr_id + "," +
      station.rental_method_a + "," +
      station.rental_method_b
  }
}
