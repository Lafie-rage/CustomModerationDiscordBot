package net.lafierage.cmdb.utils

import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.sheets.v4.SheetsScopes
import com.google.gson.Gson
import com.google.gson.stream.JsonReader
import net.lafierage.cmdb.model.BotCredentials
import net.lafierage.cmdb.model.SpreadSheetCredentials
import java.io.FileReader
import java.text.SimpleDateFormat
import java.util.*

//region Sheets API credentials
const val SPREAD_SHEET_CREDENTIALS_FILE_PATH = "src/main/resources/credentials/spread_sheet_credentials.json"
val SPREAD_SHEET_CREDENTIALS: SpreadSheetCredentials =
    Gson().fromJson(JsonReader(FileReader(SPREAD_SHEET_CREDENTIALS_FILE_PATH)), SpreadSheetCredentials::class.java)
const val APPLICATION_NAME = "Custom Modreation Discord Bot"
const val SHEETS_API_CREDENTIALS_FILE_PATH = "src/main/resources/credentials/sheets_api_credentials.json"
val JSON_FACTORY: JacksonFactory = JacksonFactory.getDefaultInstance()
val SCOPES: MutableSet<String> = Collections.singleton(SheetsScopes.SPREADSHEETS_READONLY)
const val TOKENS_DIRECTORY_PATH = "tokens"
//endregion

//region JDA
const val BOT_CREDENTIALS_FILE_PATH = "src/main/resources/credentials/bot_credentials.json"
val BOT_CREDENTIALS: BotCredentials =
    Gson().fromJson(JsonReader(FileReader(BOT_CREDENTIALS_FILE_PATH)), BotCredentials::class.java)
//endregion

//region APP
const val endIsAbusive = " (champ rempli sous la contrainte)"
fun getDateFormat() = SimpleDateFormat("dd/MM/yyyy hh:mm:ss", Locale.FRANCE)
//endregion