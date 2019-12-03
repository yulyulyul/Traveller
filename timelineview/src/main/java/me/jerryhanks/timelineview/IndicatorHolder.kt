package me.jerryhanks.timelineview

import android.view.View
import android.widget.FrameLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView

class IndicatorHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    val topLineIndicator: View = itemView.findViewById(R.id.line_indicator_top)
    val dotIndicator: View = itemView.findViewById(R.id.dot_indicator)
    val bottomLineIndicator: View = itemView.findViewById(R.id.line_indicator_bottom)
    val container: FrameLayout = itemView.findViewById(R.id.content)
    val constraintLayout: ConstraintLayout = itemView.findViewById(R.id.constraintLayout)
}