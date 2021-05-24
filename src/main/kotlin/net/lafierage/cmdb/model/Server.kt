package net.lafierage.cmdb.model

import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.User

class Server(val server: Guild) {
    val userBanned: ArrayList<User> = ArrayList()

    override fun equals(other: Any?): Boolean = other is Server && server.idLong == other.server.idLong
            || other is Guild && other.idLong == server.idLong
}