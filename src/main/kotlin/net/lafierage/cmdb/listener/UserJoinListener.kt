package net.lafierage.cmdb.listener

import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter

class UserJoinListener : ListenerAdapter() {
    override fun onGuildMemberJoin(event: GuildMemberJoinEvent) {
        logMemberJoining(event.user)
        super.onGuildMemberJoin(event)
    }

    private fun logMemberJoining(user: User) {
        println("Join : $user")
        // Check user long id
        // Log && remove user
    }
}
