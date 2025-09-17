# EmbellishChat for Fabric

EmbellishChat is a Fabric mod designed to enhance the chat experience on Minecraft servers. It makes communication between players more dynamic and convenient with features like Markdown-style text formatting, player mentions, clickable links, and mention notifications & history.

✨ Key Features

* **Markdown-Style Chat Formatting:** Emphasize your messages with various text styles.

  **Bold Text**

  _Italic Text_

  __Underlined Text__

  ~~Strikethrough Text~~

  ||obfuscated||

* **HEX Color Code Support:** Freely apply any color to your text using the `#RRGGBB` format.

* **Clickable Links:** Create clickable links in chat.

* **Player Mention System:** Use the `@` symbol to mention other players.

* **Notification:** Mentioned players who are online will hear a notification sound.

* **Team Colors:** If a mentioned player is on a team, their name will be displayed in their team's color.

* **Message Metadata & Copy:** Hovering over a chat message displays its timestamp. Clicking the message copies its content to your clipboard.

* **Customizable by Server:** All major features can be enabled, disabled, or fine-tuned by server administrators via the `embellish_chat.json` file.

💾 Installation

1. Install the Fabric Loader.
2. Download the Fabric API.
3. Download the latest EmbellishChat mod `.jar` file.
4. Place the downloaded Fabric API and EmbellishChat `.jar` files into your `.minecraft/mods` folder.

🛠️ Usage
[README.md](README.md)
Use the following formats in the chat window to apply various styles to your messages.

| Feature           | Syntax              | Example                                                       | Result                                                     |
|-------------------|---------------------|:--------------------------------------------------------------|------------------------------------------------------------|
| **Bold**          | `**Text**`          | `**Important Message**`                                       | **Important Message**                                      |
| *Italic*          | `_Text_`            | `_A point I want to emphasize_`                               | _A point I want to emphasize_                              |
| **Underline**     | `__Text__`          | `__This looks like a link__`                                  | __This looks like a link__                                 |
| ~~Strike~~        | `~~Text~~`          | `~~This content is outdated~~`                                | ~~This content is outdated~~                               |
| obfuscated        | `\|\|Text\|\|`      | `This will be unreadable`                                     | ajsabjkdbakb                                               |
| Color             | `#HexCodeText#`     | `#FF5555Hello World#`                                         | <span style="color:#FF5555;">Hello World</span>            |
| Link              | `[Text](URL)`       | `Download it [here](https://modrinth.com/mod/embellish_chat)` | [here](https://modrinth.com/mod/embellish_chat)            |
| Mention           | `@PlayerName`       | `Hello, @Player492!`                                          | Hello, <span style="color:#FFFF55;">**@Player492**</span>! |
| Escape Formatting | Use a backslash `\` | `This is not \**bold**.`                                      | This is not \**bold**.                                     |

⚙️ Configuration

### Sample `embellish_chat.json`

You can find `embellish_chat.json` in your config folder.

```jsonc
{
  "textPostProcessingEnabled": true,
  "openUriEnabled": true,
  "mentionEnabled": true,
  "defaultMentionColor": "0xFFFF55",
  "defaultMentionSound": "minecraft:entity.experience_orb.pickup"
}
```

### Configuration Options

* `textPostProcessingEnabled`: If `true`, enables Markdown and color formatting for chat messages.
* `openUriEnabled`: If `true`, enables clickable links in chat.
* `mentionEnabled`: If `true`, enables the `@` mention feature.
* `defaultMentionColor`: Sets the default HEX color for mentioned players who are not on a team. Example: `"0xFFFF55"` will color mentions in yello for non-team players.
* `defaultMentionSound`: Sets the sound event ID to be played when a mention occurs. For example, `"minecraft:entity.experience_orb.pickup"` will play the experience orb sound.

---

📜 License & Distribution

This project is licensed under the **CC BY 4.0**.

To ensure everyone gets the latest and safest version, please download the mod from the official sources below. I would appreciate it if you link to these pages rather than re-hosting the files.

Official Download on Modrinth: [here](https://modrinth.com/mod/embellish_chat)

Source Code on GitHub: [here](https://github.com/hanhy06/embellish_chat)
