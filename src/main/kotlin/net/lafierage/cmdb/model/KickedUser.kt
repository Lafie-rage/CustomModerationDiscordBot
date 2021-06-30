package net.lafierage.cmdb.model

import com.google.api.services.sheets.v4.model.ValueRange
import com.google.gson.GsonBuilder
import net.lafierage.cmdb.utils.endIsAbusive
import net.lafierage.cmdb.utils.getDateFormat
import java.util.*
import kotlin.collections.ArrayList

data class KickedUser(
    val date: Date,
    val pseudo: String,
    val role: String,
    val kicker: String,
    val reason: String,
    val isAbusive: AbusiveBan ,
    val hasBeenInvited: Boolean = false
): User {

    override fun toString(): String {
        val gson = GsonBuilder().setPrettyPrinting().create()
        return gson.toJson(this)
    }

    override fun toValueList(): ValueRange {
        val asList = ArrayList<Any>()
        asList.add(getDateFormat().format(date))
        asList.add(pseudo)
        asList.add(role)
        asList.add(kicker)
        asList.add(reason)
        asList.add(isAbusive.message + endIsAbusive)
        asList.add(if(hasBeenInvited) "Oui" else "Non")
        val values = listOf(asList)
        val valueRange = ValueRange()
        valueRange.setValues(values)
        return valueRange
    }
}
