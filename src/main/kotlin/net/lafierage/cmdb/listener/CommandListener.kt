package net.lafierage.cmdb.listener

import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.lafierage.cmdb.utils.BOT_PREFIX
import net.lafierage.cmdb.utils.SHEETS_URL

class CommandListener : ListenerAdapter() {
    override fun onMessageReceived(event: MessageReceivedEvent) {
        event.message.contentRaw.let { message ->
            if(message.startsWith(BOT_PREFIX)) {
                when {
                    message.startsWith("${BOT_PREFIX}sheet") -> {
                        event.channel.sendMessage("Voici le lien de la feuille des bans et kicks : $SHEETS_URL")
                    }
                }
            }
        }

        super.onMessageReceived(event)
    }
}