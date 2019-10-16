package jso.kpl.traveller.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import jso.kpl.traveller.R
import jso.kpl.traveller.model.Route
import jso.kpl.traveller.ui.adapters.RouteNodeAdapter
import kotlinx.android.synthetic.main.testpage.*

class TestPage : AppCompatActivity()
{

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.testpage)


        var abc : RouteNodeAdapter = RouteNodeAdapter(this, rootLinearLay)
        dataList.add(Route("Korea","200만원"))
        dataList.add(Route("Sweden","230만원"))
        dataList.add(Route("Turkey","150만원"))
        dataList.add(Route("Brazil","180만원"))
        dataList.add(Route("France","200만원"))
        dataList.add(Route("Swiss","220만원"))
        dataList.add(Route("Swiass","240만원"))
        dataList.add(Route("France","200만원"))
        dataList.add(Route("Swiss","220만원"))
        dataList.add(Route("Swiass","240만원"))
        dataList.add(Route("France","200만원"))
        dataList.add(Route("Swiss","220만원"))
        dataList.add(Route("Swiass","240만원"))
        dataList.add(Route("France","200만원"))
        dataList.add(Route("Swiss","220만원"))
        dataList.add(Route("Swiass","240만원"))

    }

    companion  object {
        val TAG = "Demo.TestPage"
        var dataList : ArrayList<Route> = ArrayList<Route>()
        var dlistidx : Int = 0
    }

}
