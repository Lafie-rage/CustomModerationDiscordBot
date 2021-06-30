package net.lafierage.cmdb.listener

import com.google.api.services.sheets.v4.Sheets
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.lafierage.cmdb.updateData

class JoinListener(private val service: Sheets) : ListenerAdapter() {
    override fun onGuildMemberJoin(event: GuildMemberJoinEvent) {
        logMemberJoining(event.user)
        super.onGuildMemberJoin(event)
    }

    private fun logMemberJoining(user: User) {
        println("Join : ${user.asTag}")
        updateData(service, false, user.asTag, true)
    }
}
