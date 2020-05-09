package ca.mcit.bixi

case class Enrichedtrip(
                         start_date: String,
                         start_station_code:  String,
                         end_date: String,
                         end_station_code: String,
                         duration_sec: String,
                         is_member: String,
                         system_id: String,
                         language:String,
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
