# Custom Moderation Discord Bot
All you should know is that I love doing useless stuff that are funny.  
This app is a Discord Bot that logs the kick and bans from my friends server into a Google Sheet that I shared with them.  
No purpose, but that's funny because we like to kick and ban guys for a bad pun for example.

Feel free to use it and modifying it as you wish.  
This project is given as it is with no warranty of working during time.  
I won't support the project after its first release.  
If you've any problem to use it, search for more information on Internet.

# How to use it

## Prerequisites
- I recommend using Intellij IDEA because it's a Kotlin based project.  
- All you'll need is the Kotlin SDK in version 1.5 or higher.
- Gradle 6.8 or higher.

## Configuring the bot and Google API

### Configuring the bot
To create the bot I recommend following the [official Discord Documentation](https://discord.com/developers/docs/intro#bots-and-apps) about that topic.  
Then you'll need to get the bot token from [this page](https://discord.com/developers/applications/).  
Choose your app and go to the bot tab and copy your token.
Paste it in a notepad or whatever.
Then also get your :
- client ID
- client secret

You can get them from the OAuth2 tab.
Copy and paste them in the notepad
  
Then you'll can add it in the json *src/main/resources/credentials/bot_credentials.json* this way

```json
{
  "bot" : {
    "token" : "Your bot token",
    "client" : {
      "id": "Your client ID",
      "secret" : "Your client secret"
    }
  }
}
```

### Configuring the Google API
You can follow the [official Google Sheets API](https://developers.google.com/sheets/api/quickstart/java) tutorial.
As they say in the tutorial, you'll need some useful information :
- Your Google app's credentials that you can find on the API & services > Credentials by downloading the file under ID clients OAuth 2.0
- Your spreadsheet ID that is on the link of your Google sheet between d/ & /edit. For example : https://docs.google.com/spreadsheets/d/HERE_IS_YOU_SPREAD_SHEET_ID/edit#gid=0

Then add the Google app's credentials as *src/main/resources/credentials/sheets_api_credentials.json*. 
Then add the spreadsheet ID in the file *src/main/resources/credentials/spread_sheet_credentials.json* as follows :
```json
{
  "spreadSheet" : {
    "id" : "Your spread sheet ID"
  }
}
```

After doing this, you'll be able to build and run the bot.

## Building and running the project
If you use Intellij IDEA, you can build and run the project by launching the main function in src/main/kolin/net/lafierage/cmdb/Main by simply clicking on the play button next to the function name.

# Contributor

- [Lafie-rage](https://github.com/Lafie-rage)