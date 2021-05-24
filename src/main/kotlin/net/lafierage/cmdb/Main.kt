package net.lafierage.cmdb

import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.requests.GatewayIntent
import net.lafierage.cmdb.listener.BanListener
import net.lafierage.cmdb.listener.UserJoinListener
import net.lafierage.cmdb.listener.KickListener
import net.lafierage.cmdb.model.Server
import net.lafierage.cmdb.utils.BOT_TOKEN

val servers = ArrayList<Server>()

fun main() {
    val jda = JDABuilder.create(BOT_TOKEN, arrayListOf(GatewayIntent.GUILD_MEMBERS))
        .addEventListeners(BanListener())
        .addEventListeners(KickListener())
        .addEventListeners(UserJoinListener())
        .build()
    jda.awaitReady()
}