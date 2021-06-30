package net.lafierage.cmdb.listener

import com.google.api.services.sheets.v4.Sheets
import net.dv8tion.jda.api.audit.ActionType
import net.dv8tion.jda.api.audit.AuditLogEntry
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.Role
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.events.guild.GuildBanEvent
import net.dv8tion.jda.api.events.guild.GuildUnbanEvent
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.lafierage.cmdb.model.AbusiveBan
import net.lafierage.cmdb.model.BannedUser
import net.lafierage.cmdb.model.Server
import net.lafierage.cmdb.servers
import net.lafierage.cmdb.setData
import net.lafierage.cmdb.updateData
import net.lafierage.cmdb.utils.RANGE_BANS
import net.lafierage.cmdb.utils.UNBAN_POSITION
import java.util.*
import java.util.concurrent.TimeUnit

class BanListener(private val service: Sheets) : ListenerAdapter() {


    override fun onGuildMemberRemove(event: GuildMemberRemoveEvent) {
        val guild = event.guild
        guild
            .retrieveAuditLogs()
            .queueAfter(1, TimeUnit.SECONDS) { logs ->
                for (log in logs) {
                    if (log.targetIdLong == event.user.idLong) {
                        // The user has been kicked
                        if (log.type == ActionType.BAN) {
                            logBannedUser(event, log)
                        }
                        break // Leaving to be sure that we don't get an older log of kick
                    }
                }
            }
        super.onGuildMemberRemove(event)
    }

    override fun onGuildUnban(event: GuildUnbanEvent) {
        logUnbannedUser(event.user)
        super.onGuildUnban(event)
    }

    private fun logBannedUser(event: GuildMemberRemoveEvent, log: AuditLogEntry) {
        val member: Member = event.member!!
        val user = event.user
        val roles: List<Role> = member.roles.filter { role ->
            role.position == member.roles.maxOfOrNull { it.position }
        }
        val bannedUser = BannedUser(
            date = Calendar.getInstance().time,
            pseudo = user.asTag,
            role = if(roles.isNotEmpty()) roles[0].name else "Aucun",
            banner = log.user!!.asTag,
            reason = log.reason ?: "",
            isAbusive = AbusiveBan.getRandomAbusiveBan(),
            isUnbanned = false,
            hasBeenInvited = false
        )
        println("Ban : $bannedUser")
        setData(service, RANGE_BANS, bannedUser)
    }

    private fun logUnbannedUser(user: User) {
        println("Unban : ${user.asTag}")
        updateData(service, true, user.asTag,true)
    }
}