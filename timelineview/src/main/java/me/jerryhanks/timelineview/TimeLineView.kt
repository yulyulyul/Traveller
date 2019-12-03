package me.jerryhanks.timelineview

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.RelativeLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


/**
 * @author <@Po10cio> on 10/17/17 for StepViewApp
 *
 *
 * Copyright (c) 2017 MAX. All rights reserved.
 */

class TimeLineView : RelativeLayout {
    private lateinit var recyclerView: RecyclerView

    constructor(context: Context) : super(context) {
        init()
    }


    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }


    private fun init() {
        View.inflate(context, R.layout.timeline_view, this)
        recyclerView = findViewById(R.id.recycler)
        recyclerView.isNestedScrollingEnabled = true
        recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
    }

    fun setIndicatorAdapter(adapter: IndicatorAdapter<*>) {
        recyclerView.adapter = adapter
    }

    fun scrollToTop(){
        try {
            recyclerView.scrollToPosition(0)
        } catch (e: Exception) {
            throw Throwable("No items")
        }
    }

    fun scrollToBottom(){
        try {
            recyclerView.scrollToPosition(recyclerView.adapter!!.itemCount-1)
        } catch (e: Exception) {
            throw Throwable("No items")
        }
    }
}