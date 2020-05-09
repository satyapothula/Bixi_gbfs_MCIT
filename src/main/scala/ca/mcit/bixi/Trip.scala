package ca.mcit.bixi

case class Trip(
                 start_date: String,
                 start_station_code:  String,
                 end_date: String,
                 end_station_code: String,
                 duration_sec: String,
                 is_member: String
               )

object Trip {
  def apply(csvLine: String): Trip = {
    val t = csvLine.split(",", -1)
    new Trip(t(0), t(1), t(2), t(3), t(4), t(5))
  }

  def toCsv(trip: Trip): String = {
      trip.start_date + ","
      trip.start_date + "," +
      trip.start_station_code + "," +
      trip.end_date + "," +
      trip.end_station_code + "," +
      trip.duration_sec + "," +
      trip.is_member + ","
  }
}
