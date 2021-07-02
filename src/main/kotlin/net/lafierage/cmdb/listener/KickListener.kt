package net.lafierage.cmdb.listener

import com.google.api.services.sheets.v4.Sheets
import net.dv8tion.jda.api.audit.ActionType
import net.dv8tion.jda.api.audit.AuditLogEntry
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.Role
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.lafierage.cmdb.model.AbusiveBan
import net.lafierage.cmdb.model.KickedUser
import net.lafierage.cmdb.setData
import net.lafierage.cmdb.utils.RANGE_KICKS
import java.util.*
import java.util.concurrent.TimeUnit

class KickListener(private val service: Sheets) : ListenerAdapter() {
    override fun onGuildMemberRemove(event: GuildMemberRemoveEvent) {
        val guild = event.guild
        guild
            .retrieveAuditLogs()
            .queueAfter(1, TimeUnit.SECONDS) { logs ->
                for (log in logs) {
                    if (log.targetIdLong == event.user.idLong) {
                        // The user has been kicked
                        if (log.type == ActionType.KICK) {
                            logUserKicked(event, log)
                        }
                        break // Leaving to be sure that we don't get an older log of kick
                    }
                }
            }
        super.onGuildMemberRemove(event)
    }

    private fun logUserKicked(event: GuildMemberRemoveEvent, log: AuditLogEntry) {
        val member: Member = event.member!!
        val user: User = event.user
        val roles: List<Role> = member.roles.filter { role ->
            role.position == member.roles.maxOfOrNull { it.position }
        }
        val kickedUser = KickedUser(
            date = Calendar.getInstance().time,
            pseudo = user.asTag,
            role = if (roles.isNotEmpty()) roles[0].name else "Aucun",
            kicker = log.user!!.asTag,
            reason = log.reason ?: "",
            isAbusive = AbusiveBan.getRandomAbusiveBan(),
            hasBeenInvited = false
        )
        println("Kick : $kickedUser")
        setData(service, RANGE_KICKS, kickedUser)
    }
}