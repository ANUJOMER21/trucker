package Api

enum class Api(val url:String) {

    Login("https://works.diginspire.in/driverapp/api/login.php"),
    Truck_precheck("https://works.diginspire.in/driverapp/api/truck_precheck.php"),
    Trailer_precheck("https://works.diginspire.in/driverapp/api/trailer_precheck.php"),
    CheckData("https://works.diginspire.in/driverapp/api/precheck_status.php"),
    EndTrip("https://works.diginspire.in/driverapp/api/end_date.php")
}