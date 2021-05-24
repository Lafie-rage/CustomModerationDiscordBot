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
import com.sun.tools.javac.Main
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.requests.GatewayIntent
import net.lafierage.cmdb.listener.BanListener
import net.lafierage.cmdb.listener.KickListener
import net.lafierage.cmdb.listener.UserJoinListener
import net.lafierage.cmdb.model.Server
import net.lafierage.cmdb.utils.*
import java.io.*


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
    val spreadsheetId = SPREAD_SHEET_CREDENTIALS.spreadSheet.id
    val range = "Class Data!A2:E"
    val service = Sheets.Builder(httpTransport, JSON_FACTORY, getCredentials(httpTransport))
        .setApplicationName(APPLICATION_NAME)
        .build()
    val response: ValueRange = service.spreadsheets().values()[spreadsheetId, range]
        .execute()
    val values: List<List<Any>> = response.getValues()
    if (values.isEmpty()) {
        println("No data found.")
    } else {
        println("Name, Major")
        for (row in values) {
            // Print columns A and E, which correspond to indices 0 and 4.
            System.out.printf("%s, %s\n", row[0], row[4])
        }
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