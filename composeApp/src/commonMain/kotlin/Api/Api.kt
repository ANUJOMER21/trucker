package Api

enum class Api(val url:String) {

    Login("${Baseurl}login.php"),
    Truck_precheck("${Baseurl}truck_precheck.php"),
    Trailer_precheck("${Baseurl}trailer_precheck.php"),
    CheckData("${Baseurl}precheck_status.php"),
    EndTrip("${Baseurl}end_date.php"),
    Location("${Baseurl}submit_location.php"),
    Admin("https://works.diginspire.in/driverapp/admin.php"),
    History("https://works.diginspire.in/driverapp/api/lastweekdata.php"),
    Profile("https://works.diginspire.in/driverapp/api/profile.php"),
    Reset("https://works.diginspire.in/driverapp/api/resetstatus.php")
}
val Baseurl="https://works.diginspire.in/driverapp/api/"