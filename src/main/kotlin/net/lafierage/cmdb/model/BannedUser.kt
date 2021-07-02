package net.lafierage.cmdb.model

import com.google.api.services.sheets.v4.model.ValueRange
import com.google.gson.GsonBuilder
import net.lafierage.cmdb.utils.endIsAbusive
import net.lafierage.cmdb.utils.getDateFormat
import java.util.*

data class BannedUser(
    val date: Date,
    val pseudo: String,
    val role: String,
    val banner: String,
    val reason: String,
    val isAbusive: AbusiveBan,
    val isUnbanned: Boolean = false,
    val hasBeenInvited: Boolean = false
) : User {
    override fun toValueList(): ValueRange {
        val asList = ArrayList<Any>()
        asList.add(getDateFormat().format(date))
        asList.add(pseudo)
        asList.add(role)
        asList.add(banner)
        asList.add(reason)
        asList.add(isAbusive.message + endIsAbusive)
        asList.add(if (isUnbanned) "Oui" else "Non")
        asList.add(if (hasBeenInvited) "Oui" else "Non")
        val values = listOf(asList)
        val valueRange = ValueRange()
        valueRange.setValues(values)
        return valueRange
    }

    override fun toString(): String {
        val gson = GsonBuilder().setPrettyPrinting().create()
        return gson.toJson(this)
    }
}