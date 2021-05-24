package net.lafierage.cmdb.utils

import com.google.gson.Gson
import com.google.gson.stream.JsonReader
import net.lafierage.cmdb.model.BotCredentials
import java.io.FileReader

val BOT_TOKEN: BotCredentials = Gson().fromJson(JsonReader(FileReader("src/main/resources/credentials/bot_credentials.json")), BotCredentials::class.java)