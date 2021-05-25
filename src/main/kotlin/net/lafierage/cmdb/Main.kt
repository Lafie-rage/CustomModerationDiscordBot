package net.lafierage.cmdb

import com.google.api.client.auth.oauth2.Credential
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.util.store.FileDataStoreFactory
import com.google.api.services.sheets.v4.Sheets
import com.google.api.services.sheets.v4.model.ValueRange
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.requests.GatewayIntent
import net.lafierage.cmdb.listener.BanListener
import net.lafierage.cmdb.listener.KickListener
import net.lafierage.cmdb.listener.UserJoinListener
import net.lafierage.cmdb.model.AbusiveBan
import net.lafierage.cmdb.model.BannedUser
import net.lafierage.cmdb.model.KickedUser
import net.lafierage.cmdb.model.Server
import net.lafierage.cmdb.utils.*
import java.io.File
import java.io.FileReader
import java.io.IOException

val servers = ArrayList<Server>()

fun main() {
    val jda = JDABuilder.create(BOT_CREDENTIALS.bot.token, arrayListOf(GatewayIntent.GUILD_MEMBERS))
        .addEventListeners(BanListener())
        .addEventListeners(KickListener())
        .addEventListeners(UserJoinListener())
        .build()
    jda.awaitReady()

    // Build a new authorized API client service.
    val httpTransport = GoogleNetHttpTransport.newTrustedTransport()
    val service = Sheets.Builder(httpTransport, JSON_FACTORY, getCredentials(httpTransport))
        .setApplicationName(APPLICATION_NAME)
        .build()
    getBannedUsers(service)
    getKickedUsers(service)
}

/**
 * Load and display banned users
 *
 * @return List of banned users
 */
private fun getBannedUsers(service: Sheets): List<BannedUser> {
    val users: MutableList<BannedUser> = ArrayList()
    val range = "Bans!A2:H"
    val values = getData(service, range)
    if (values.isEmpty()) {
        println("No data found for banned users.")
    } else {
        println("BANS")
        for (row in values) {
            users.add(
                BannedUser(
                    date = getDateFormat().parse(row[0].toString()),
                    pseudo = row[1].toString(),
                    role = row[2].toString(),
                    banner = row[3].toString(),
                    reason = row[4].toString(),
                    isAbusive = AbusiveBan.values().filter {
                        row[5].toString().contains(it.message)
                    }[0],
                    isUnbanned = row[6].toString() == "Oui",
                    hasBeenInvited = row[7].toString() == "Oui"
                )
            )
            println(users[users.lastIndex])
        }
    }
    return users
}

/**
 * Load and display kicked users
 *
 * @return List of kicked users
 */
private fun getKickedUsers(service: Sheets): List<KickedUser> {
    val users: MutableList<KickedUser> = ArrayList()
    val range = "Kicks!A2:G"
    val values = getData(service, range)
    if (values.isEmpty()) {
        println("No data found for kicked users.")
    } else {
        println("Kicks")
        for (row in values) {
            users.add(
                KickedUser(
                    date = getDateFormat().parse(row[0].toString()),
                    pseudo = row[1].toString(),
                    role = row[2].toString(),
                    kicker = row[3].toString(),
                    reason = row[4].toString(),
                    isAbusive = AbusiveBan.valueOf(row[5].toString().substringBefore(endIsAbusive)),
                    hasBeenInvited = row[6].toString() == "Oui"
                )
            )
            println(users[users.lastIndex])
        }
    }
    return users
}

private fun getData(service: Sheets, range: String): List<List<Any>> {
    val spreadsheetId = SPREAD_SHEET_CREDENTIALS.spreadSheet.id
    val response: ValueRange = service.spreadsheets().values()[spreadsheetId, range]
        .execute()
    return response.getValues()?.let {
        response.getValues()
    } ?: run {
        arrayOf<List<Any>>().toList()
    }
}

/**
 * Creates an authorized Credential object.
 * @param HTTP_TRANSPORT The network HTTP Transport.
 * @return An authorized Credential object.
 * @throws IOException If the sheets_api_credentials.json file cannot be found.
 */
@Throws(IOException::class)
private fun getCredentials(HTTP_TRANSPORT: NetHttpTransport): Credential? {
    // Load client secrets.
    val jsonReader = FileReader(SHEETS_API_CREDENTIALS_FILE_PATH)
    val clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, jsonReader)

    // Build flow and trigger user authorization request.
    val flow = GoogleAuthorizationCodeFlow.Builder(
        HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES
    )
        .setDataStoreFactory(FileDataStoreFactory(File(TOKENS_DIRECTORY_PATH)))
        .setAccessType("offline")
        .build()
    val receiver = LocalServerReceiver.Builder().setPort(8888).build()
    return AuthorizationCodeInstalledApp(flow, receiver).authorize("user")
}