package net.lafierage.cmdb.listener

import net.dv8tion.jda.api.audit.ActionType
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import java.util.concurrent.TimeUnit

class KickListener : ListenerAdapter() {
    override fun onGuildMemberRemove(event: GuildMemberRemoveEvent) {
        event.guild
            .retrieveAuditLogs()
            .queueAfter(1, TimeUnit.SECONDS) { logs ->
                for (log in logs) {
                    if (log.targetIdLong == event.user.idLong) {
                        if (log.type == ActionType.KICK) { // The user has been kicked
                            logUserKicked(event.user)
                        }
                        break // Leaving to be sure that we don't get an older log of kick
                    }
                }
            }
        super.onGuildMemberRemove(event)
    }

    private fun logUserKicked(user: User) {
        println("Kick : $user")
    }
}