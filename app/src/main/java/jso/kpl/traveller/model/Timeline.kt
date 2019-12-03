package jso.kpl.traveller.model

import me.jerryhanks.timelineview.model.Status
import me.jerryhanks.timelineview.model.TimeLine

class Timeline(status: Status, var sp_place: String?, var sp_expenses: String?, var sp_period: String?, var sp_category: String?, var sp_imgs: String?) : TimeLine(status) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Timeline

        if (sp_place != other.sp_place) return false
        if (sp_expenses != other.sp_expenses) return false
        if (sp_period != other.sp_period) return false
        if (sp_category != other.sp_category) return false
        if (sp_imgs != other.sp_imgs) return false

        return true
    }

    override fun hashCode(): Int {
        var result = sp_place?.hashCode() ?: 0
        result = 31 * result + (sp_expenses?.hashCode() ?: 0)
        result = 31 * result + (sp_period?.hashCode() ?: 0)
        result = 31 * result + (sp_category?.hashCode() ?: 0)
        result = 31 * result + (sp_imgs?.hashCode() ?: 0)
        return result
    }

    override fun toString(): String {
        return "Timeline(sp_place=$sp_place, sp_expenses=$sp_expenses, sp_period=$sp_period, sp_category=$sp_category, sp_imgs=$sp_imgs)"
    }
}