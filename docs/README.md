BetterChat for Fabric

BetterChat is a Fabric mod designed to enhance the chat experience on Minecraft servers. It makes communication between players more dynamic and convenient with features like Markdown-style text formatting, player mentions, and mention notifications & history.

✨ Key Features

* **Markdown-Style Chat Formatting:** Emphasize your messages with various text styles.

  **Bold Text**

  _Italic Text_

  __Underlined Text__

  ~~Strikethrough Text~~

* **HEX Color Code Support:** Freely apply any color to your text using the `#RRGGBB` format.

* **Player Mention System:** Use the `@` symbol to mention other players.

* **Notification:** Mentioned players who are online will hear a notification sound.

* **Team Colors:** If a mentioned player is on a team, their name will be displayed in their team's color.

* **Offline Mention Storage:** Mentions received while a player is offline are securely saved. The player is notified of unread messages upon logging in.

* **Message Metadata & Copy:** Hovering over a chat message displays its timestamp. Clicking the message copies its content to your clipboard.

* **Customizable by Server:** All major features can be enabled, disabled, or fine-tuned by server administrators via the `config.json` file.

💾 Installation

1. Install the Fabric Loader.
2. Download the Fabric API.
3. Download the latest BetterChat mod `.jar` file.
4. Place the downloaded Fabric API and BetterChat `.jar` files into your `.minecraft/mods` folder.

🛠️ Usage

Use the following formats in the chat window to apply various styles to your messages.

| Feature           | Syntax              | Example                         | Result                                                     |
| ----------------- | ------------------- |---------------------------------|------------------------------------------------------------|
| **Bold**          | `**Text**`          | `**Important Message**`         | **Important Message**                                      |
| *Italic*          | `_Text_`            | `_A point I want to emphasize_` | _A point I want to emphasize_                              |
| **Underline**     | `__Text__`          | `__This looks like a link__`    | __This looks like a link__                                 |
| ~~Strike~~        | `~~Text~~`          | `~~This content is outdated~~`  | ~~This content is outdated~~                               |
| Color             | `#HexCodeText#`     | `#FF5555Hello World#`           | <span style="color:#FF5555;">Hello World</span>            |
| Mention           | `@PlayerName`       | `Hello, @Player492!`            | Hello, <span style="color:#FFFF55;">**@Player492**</span>! |
| Escape Formatting | Use a backslash `\` | `This is not \**bold**.`        | This is not \*\*bold**.                                    |

![text rendering preview](./images/preview1.gif)

⚙️ Configuration

When the mod is first loaded, a `better-chat` directory will be created within your world save folder, containing a `config.json` file. You can edit this file to control the mod's features.

### Sample `config.json`

```jsonc
{
  "textPostProcessingEnabled": true,
  "mentionEnabled": true,
  "saveMentionEnabled": true,
  "maxMentionBufferSize": 1000,
  "mentionBufferClearIntervalMinutes": 1,
  "defaultMentionColor": "0xFFFF55",
  "defaultMentionNotificationSound": "minecraft:entity.experience_orb.pickup",
  "maxPlayerDataCacheSize": 1000,
  "playerDataCacheTTLMinutes": 3
}
```

### Configuration Options

* `textPostProcessingEnabled`: If `true`, enables Markdown and color formatting for chat messages.
* `mentionEnabled`: If `true`, enables the `@` mention feature.
* `saveMentionEnabled`: If `true`, saves offline mentions to the database. Mentions received while a player is offline will be stored, and upon logging in, the player will see a popup indicating the number of unread mentions.
* `maxMentionBufferSize`: This is the maximum size of the mention buffer. If this size is exceeded, the mention buffer clear process is automatically triggered.
* `mentionBufferClearIntervalMinutes`: This is the interval at which the mention buffer clear process is executed. The unit is minutes.
* `defaultMentionColor`: Sets the default HEX color for mentioned players who are not on a team. Example: `"0xFFFF55"` will color mentions in yello for non-team players.
* `defaultMentionNotificationSound`: Sets the sound event ID to be played when a mention occurs. For example, `"minecraft:entity.experience_orb.pickup"` will play the experience orb sound.
* `maxPlayerDataCacheSize`: This is the maximum limit of player data cached by the mod (name, UUID, mention notification status, and team color).
* `playerDataCacheTTLMinutes`: This is the TTL (time to live) of the data cached by the server. Once this time is exceeded, the data is automatically removed from the cache.

---

### Version Compatibility

* **Minecraft Versions Supported:** 1.21.5 (tested).
* **Fabric Loader Requirement:** Fabric Loader 1.21.5 or newer.
* **Recommended Fabric API:** 0.16.10 or newer.
* **Java Version:** Java 21 or newer is required to run this mod.

---

### Troubleshooting & FAQ

* **Mod Does Not Load:**

  1. Ensure you have the correct Fabric Loader version installed (check `.minecraft/versions` folder).
  2. Confirm that Fabric API and BetterChat `.jar` files are both in the `mods` folder.
  3. Check the server/client logs for errors related to BetterChat during startup.

* **Color Formatting Not Applying:**

  1. Verify `textPostProcessingEnabled` is set to `true` in `config.json`.
  2. Make sure your HEX color syntax is correct: wrap text as `#RRGGBBYour Message#`.

* **Database Connection Errors:**

  1. Check that `saveMentionEnabled` is `true` and the default SQLite DB file (`better-chat/betterchat.db`) exists or can be created.
  2. Look for `Connected to database` log entries to ensure the DB is accessible.

* **Mentions Not Notifying Players:**

  1. Confirm `mentionEnabled` is `true`.
  2. Ensure the mentioned player is online to hear the notification sound; offline mentions will be stored instead.
  3. If the player is on a team, the mention color will default to the team's color rather than `defaultMentionColor`.

---


📜 License & Distribution

This project is licensed under the **CC BY 4.0**.

To ensure everyone gets the latest and safest version, please download the mod from the official sources below. I would appreciate it if you link to these pages rather than re-hosting the files.

Official Download on Modrinth: [https://modrinth.com/mod/better-chat](https://modrinth.com/mod/better-chat)

Source Code on GitHub: [https://github.com/com.hanhy06/betterchat-1.21.5](https://github.com/com.hanhy06/betterchat-1.21.5)

If you plan to use this mod on a large-scale server, I would appreciate it if you could let me know! This is not a requirement, but I’m curious how and where my work is used.


---

🚀 Roadmap & Future Plans
Here are some of the exciting features currently in development or planned for future releases:
* New Commands & UI:
  * Introduction of /inbox and /betterchat setting commands.
  * These features are planned to utilize the new dialog functionality coming in Minecraft 1.21.6. Consequently, development on this is currently awaiting that game update.
* In-Chat Image Rendering:
  * A feature to display images directly within the chat window using a syntax like [IMAGE URL].
  * This will likely be achieved by rendering the image pixel-by-pixel using a series of JSON text components, similar to [{"text":"■","color":"#RRGGBB"}].