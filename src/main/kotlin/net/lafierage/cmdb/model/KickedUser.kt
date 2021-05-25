package net.lafierage.cmdb.model

import com.google.gson.GsonBuilder
import java.util.*

data class KickedUser(
    val date: Date,
    val pseudo: String,
    val role: String,
    val kicker: String,
    val reason: String,
    val isAbusive: AbusiveBan ,
    val hasBeenInvited: Boolean = false
) {
    override fun toString(): String {
        val gson = GsonBuilder().setPrettyPrinting().create()
        return gson.toJson(this)
    }
}
