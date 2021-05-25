package net.lafierage.cmdb.model

import com.google.gson.GsonBuilder
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
) {
    override fun toString(): String {
        val gson = GsonBuilder().setPrettyPrinting().create()
        return gson.toJson(this)
    }
}