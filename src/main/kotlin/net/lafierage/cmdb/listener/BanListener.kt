package net.lafierage.cmdb.listener

import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.events.guild.GuildBanEvent
import net.dv8tion.jda.api.events.guild.GuildUnbanEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.lafierage.cmdb.model.Server
import net.lafierage.cmdb.servers

class BanListener : ListenerAdapter() {


    override fun onGuildBan(event: GuildBanEvent) {
        logBannedUser(event.user, event.guild)
        super.onGuildBan(event)
    }

    override fun onGuildUnban(event: GuildUnbanEvent) {
        logUnbannedUser(event.user)
        super.onGuildUnban(event)
    }

    private fun logBannedUser(user: User, server: Guild) {
        println("Ban : $user")
        servers.forEach {
            if(it.server == server) {
                it.userBanned.add(user)
                return
            }
        }
        servers.add(Server(server))
        servers[servers.lastIndex].userBanned.add(user)
    }

    private fun logUnbannedUser(user: User) {
        println("Unban : $user")

    }
}