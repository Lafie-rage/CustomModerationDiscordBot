package net.lafierage.cmdb.model

import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.User

class Server(val server: Guild) {
    val userBanned: MutableList<User> = ArrayList()
    val kickedUser: MutableList<User> = ArrayList()

    override fun equals(other: Any?): Boolean = other is Server && server.idLong == other.server.idLong
            || other is Guild && other.idLong == server.idLong

}