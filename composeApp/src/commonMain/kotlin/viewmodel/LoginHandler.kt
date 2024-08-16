package viewmodel

import Api.Api
import Api.ApiClient
import InspectionItem
import kotlinx.datetime.Clock
import org.lighthousegames.logging.logging

class  LoginHandler {
    suspend fun Login(mobile: String, password: String, response: (res: loginmodel?,failed:Boolean) -> Unit) {
        val log = logging("KMLogging Tag")
        try {

            val formdata = hashMapOf(
                "phone" to mobile,
                "password" to password
            )


            val res = ApiClient().loginCall(Api.Login.url     , formdata)
            log.d { "$res" }

            response(res,false)
        } catch (e: Exception) {
            log.d { e.toString() }
            // Handle exceptions or errors here
            response(null,true)
        }
    }


    suspend fun sendTruckData(driverId:String,InspectionList:List<InspectionItem>,image:ByteArray,MeterReading:String,remark:String,
                              response:(res: message?,failed:Boolean)->Unit){
        val log = logging("sendTruckData")
        try {
            val list:ArrayList<ApiClient.PrecheckItem> = ArrayList()
            InspectionList.forEach {
                list.add(ApiClient.PrecheckItem(
                    it.name,
                    it.status.name
                ))
            }
            val nowUnixtime = Clock.System.now().nanosecondsOfSecond.toString()
            val res=ApiClient().postPrecheckData(
                driverId=driverId,
                meterReading = MeterReading,
                precheckItems = list as List<ApiClient.PrecheckItem>,
                imageUpload = image,
                date = nowUnixtime,
                remark=remark

            )
            log.d { res}
            if(res==null){
                response(null,true)
            }
            else{
                response(res,false)
            }


        }
        catch (e:Exception){
            log.d { e.toString() }
            response(null,true)
        }


    }

    suspend fun gethistory(driverId: String,response: (res: HistoryModel?, failed: Boolean) -> Unit){
        val log = logging("sendendtrip")
        try {

            val res=ApiClient().getweekhistoru(driverId)
            log.d { res }

            if(res==null){
                response(null,true)
            }
            else{
                response(res,false)
            }


        }
        catch (e:Exception){
            log.d { e.toString() }
            response(null,true)
        }
    }
    suspend fun getprofile(driverId: String,response: (res: profilemodel?, failed: Boolean) -> Unit){
        val log = logging("sendendtrip")
        try {

            val res=ApiClient().profile(driverId)
            log.d { res }

            if(res==null){
                response(null,true)
            }
            else{
                response(res,false)
            }


        }
        catch (e:Exception){
            log.d { e.toString() }
            response(null,true)
        }
    }
    suspend fun sendendtrip(driverId: String,MeterReading: String,image: ByteArray,response:(res: endtripmodel?,failed:Boolean)->Unit){
        val log = logging("sendendtrip")
        try {

            val res=ApiClient().endtrip(driverId,MeterReading,image)
            log.d { res }

            if(res==null){
                response(null,true)
            }
            else{
                response(res,false)
            }


        }
        catch (e:Exception){
            log.d { e.toString() }
            response(null,true)
        }
    }
    data class precheckstatus(val truck:Boolean,val endtrip:Boolean,val inspection:Boolean)
    suspend fun getPrecheckStatus(driverId: String,response:(res:precheckstatus?,failed:Boolean)->Unit)
    {
        val log = logging("checkstatus")
        try {
            val res=ApiClient().getPrecheckStatus(driverId)
            log.d { res }
            if(res==null){
                response(null,true)
            }
            else{
                response(
                    precheckstatus(
                        res.precheck_data.truck_precheck=="1" ,
                        res        .inspectionItems.end=="1",
                        res.truck_precheck.trailer_precheck =="1"

                    ),

                    false
                )

            }

        }
        catch (e:Exception){
            log.d { e.toString() }
            response(null,true)
        }
    }
    suspend fun ResetData(driverId: String, response: (res: message?, failed: Boolean) -> Unit) {
        val log = logging("checkstatus")
        try {
            val res = ApiClient().postResetData(driverId)
            log.d { res }
            if (res == null) {
                response(null, true)
            } else {
                response(res, false)
            }
        }
        catch (e:Exception){
            log.d { e.toString() }
            response(null,true)
        }
    }
    suspend fun sendTrailerdata(
        driverId: String,
        InspectionList: List<InspectionItem>,
        Remark:String,
        response: (res:message?,failed:Boolean)->Unit
    ){
        val log = logging("sendTrailerData")
        try {
            val list= ArrayList<Inspection>()
            InspectionList.forEach {
                list.add(
                    Inspection(
                    it.name,
                    it.status.name
                )
                )
            }
            val trailerModel=trailer_model(
                driver_id = driverId,
                inspection_items = list as List<Inspection>,
                Remark

            )
            val res=ApiClient().postTrailerCheckData(trailerModel)
              log.d { res}
            if(res==null){
                response(null,true)
            }
            else{
                response(res,false)
            }
        }catch (e:Exception){
            log.d { e.toString() }
            response(null,true)
        }
    }


}