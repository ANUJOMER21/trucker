package Api

enum class Api(val url:String) {

    Login("${Baseurl}login.php"),
    Truck_precheck("${Baseurl}truck_precheck.php"),
    Trailer_precheck("${Baseurl}trailer_precheck.php"),
    CheckData("${Baseurl}precheck_status.php"),
    EndTrip("${Baseurl}end_date.php"),
    Location("${Baseurl}submit_location.php")

}
val Baseurl="https://works.diginspire.in/driverapp/api/"