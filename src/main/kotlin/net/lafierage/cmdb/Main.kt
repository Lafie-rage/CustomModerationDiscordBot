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
import com.google.gson.GsonBuilder
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.requests.GatewayIntent
import net.lafierage.cmdb.listener.BanListener
import net.lafierage.cmdb.listener.CommandListener
import net.lafierage.cmdb.listener.JoinListener
import net.lafierage.cmdb.listener.KickListener
import net.lafierage.cmdb.model.AbusiveBan
import net.lafierage.cmdb.model.BannedUser
import net.lafierage.cmdb.model.KickedUser
import net.lafierage.cmdb.model.User
import net.lafierage.cmdb.utils.*
import java.io.File
import java.io.FileReader
import java.io.IOException

fun main() {
    // Build a new authorized API client service.
    val httpTransport = GoogleNetHttpTransport.newTrustedTransport()
    val service = Sheets.Builder(httpTransport, JSON_FACTORY, getCredentials(httpTransport))
        .setApplicationName(APPLICATION_NAME)
        .build()

    val jda = JDABuilder.create(BOT_CREDENTIALS.bot.token, arrayListOf(GatewayIntent.GUILD_MEMBERS))
        .addEventListeners(BanListener(service))
        .addEventListeners(KickListener(service))
        .addEventListeners(JoinListener(service))
        .addEventListeners(CommandListener())
        .enableIntents(GatewayIntent.GUILD_BANS)
        .build()
    jda.awaitReady()

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
    val values = getData(service, RANGE_BANS)
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
    val values = getData(service, RANGE_KICKS)
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
                    isAbusive = AbusiveBan.values().filter {
                        row[5].toString().contains(it.message)
                    }[0],
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

fun setData(service: Sheets, range: String, user: User) {
    val spreadsheetId = SPREAD_SHEET_CREDENTIALS.spreadSheet.id
    val request = service.spreadsheets().values().append(spreadsheetId, range, user.toValueList())
    request.valueInputOption = "RAW"
    val response = request.execute()
    val gson = GsonBuilder().setPrettyPrinting().create()
    println(gson.toJson(response))
}

fun updateData(service: Sheets, isUnban: Boolean, userAsTag: String, value: Any) {
    val spreadsheetId = SPREAD_SHEET_CREDENTIALS.spreadSheet.id
    var row: Int = -1
    var users: List<List<Any>> = arrayListOf()
    if (isUnban) { // On unban event
        val data = getData(service, RANGE_BANS)
        users = data.filter { user ->
            user[1].toString().endsWith(userAsTag.substringAfterLast('#')) && user[6] != "Oui"
        }
        row = data.indexOf(users[0])
    } else { // On join event
        // Check if join after ban
        var data = getData(service, RANGE_BANS)
        users = data.filter { user ->
            user[1].toString().endsWith(userAsTag.substringAfterLast('#')) && user[7] != "Oui"
        }
        if (users.isEmpty()) { // Check if join after kick
            data = getData(service, RANGE_KICKS)
            users = data.filter { user ->
                user[1].toString().endsWith(userAsTag.substringAfterLast('#')) && user[6] != "Oui"
            }
            if (users.isEmpty()) {
                return
            }
            row = data.indexOf(users[0])
        } else {
            row = data.indexOf(users[0])
        }

    }

    val userAsPropertyArray = users[0]

    val user: User = if (isUnban) { // Unban update
        BannedUser(
            date = getDateFormat().parse(userAsPropertyArray[0].toString()),
            pseudo = userAsPropertyArray[1].toString(),
            role = userAsPropertyArray[2].toString(),
            banner = userAsPropertyArray[3].toString(),
            reason = userAsPropertyArray[4].toString(),
            isAbusive = AbusiveBan.values().filter {
                userAsPropertyArray[5].toString().contains(it.message)
            }[0],
            isUnbanned = value as Boolean,
            hasBeenInvited = (userAsPropertyArray[7].toString() == "Oui")
        )
    } else {
        if (userAsPropertyArray.size == 7) {
            KickedUser(
                date = getDateFormat().parse(userAsPropertyArray[0].toString()),
                pseudo = userAsPropertyArray[1].toString(),
                role = userAsPropertyArray[2].toString(),
                kicker = userAsPropertyArray[3].toString(),
                reason = userAsPropertyArray[4].toString(),
                isAbusive = AbusiveBan.values().filter {
                    userAsPropertyArray[5].toString().contains(it.message)
                }[0],
                hasBeenInvited = value as Boolean
            )
        } else {
            BannedUser(
                date = getDateFormat().parse(userAsPropertyArray[0].toString()),
                pseudo = userAsPropertyArray[1].toString(),
                role = userAsPropertyArray[2].toString(),
                banner = userAsPropertyArray[3].toString(),
                reason = userAsPropertyArray[4].toString(),
                isAbusive = AbusiveBan.values().filter {
                    userAsPropertyArray[5].toString().contains(it.message)
                }[0],
                isUnbanned = (userAsPropertyArray[6].toString() == "Oui"),
                hasBeenInvited = value as Boolean
            )
        }
    }
    val values = user.toValueList()
    val range = if (user is BannedUser) getBansRange(row) else getKicksRange(row)
    val request = service.spreadsheets().values().update(spreadsheetId, range, values)
    request.valueInputOption = "RAW"
    val response = request.execute()
    val gson = GsonBuilder().setPrettyPrinting().create()
    println(gson.toJson(response))
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
    ).setDataStoreFactory(FileDataStoreFactory(File(TOKENS_DIRECTORY_PATH)))
        .setAccessType("offline")
        .build()

    val receiver = LocalServerReceiver.Builder().setPort(8888).build()
    return AuthorizationCodeInstalledApp(flow, receiver).authorize("Custom Moderation Discord Bot")
}