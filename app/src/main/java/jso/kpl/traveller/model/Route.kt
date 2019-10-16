package jso.kpl.traveller.model

import android.util.Log

/**
 * Route Other Detail의 루트에 들어갈 데이터 모델
 *
 */
class Route
{
    var location:String
    var cost:String

    constructor(_location : String, _cost:String)
    {
        Log.d(TAG, "create, location -> " + _location + " cost -> " + _cost)
        this.location = _location
        this.cost = _cost
    }

    companion object
    {
        val TAG:String = "Demo.Route"
    }
}