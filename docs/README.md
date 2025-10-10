# EmbellishChat for Fabric

EmbellishChat is a Fabric mod designed to enhance the chat experience on Minecraft servers. It makes communication between players more dynamic and convenient with features like Markdown-style text formatting, player mentions, clickable links, and mention notifications & history.

✨ Key Features

* **Extended Markdown-Style Chat Formatting:** Use Markdown-like styles (bold, italic, underline, strikethrough, obfuscation, color, font, url). Works in normal chat and whispers.

* **Clickable Links:** Create clickable links in chat. For security, only the `https` protocol is recognized.

* **Player Mention System:** Use the `@` symbol to mention other players.

* **Notification:** Mentioned players who are online will hear a notification sound. In version 1.21.6 and above, the notification sound category is UI (in lower versions, it belongs to the PLAYER category).

* **Team Colors:** If a mentioned player is on a team, their target will be displayed in their team's color.

* **Message Metadata & Copy:** Hovering over a chat message displays its timestamp. Clicking the message copies its content to your clipboard.

* **Default Style** You can set the default color and font of chat messages through the settings.

* **Customizable by Server:** All major features can be enabled, disabled, or fine-tuned by server administrators via the `embellish_chat.json` file.

---

🛠️ Usage

Use the following formats in the chat window to apply various styles to your messages.

| Feature           | Syntax                                    | Example                                                       | Result                                                                                                                        |
|-------------------|-------------------------------------------|:--------------------------------------------------------------|-------------------------------------------------------------------------------------------------------------------------------|
| Bold              | `**Text**`                                | `**Important Message**`                                       | ![Bold](https://github.com/hanhy06/embellish_chat/blob/fabric/1.21.9/docs/images/Bold.png?raw=true)                           |
| Italic            | `_Text_`                                  | `_A point I want to emphasize_`                               | ![Italic](https://github.com/hanhy06/embellish_chat/blob/fabric/1.21.9/docs/images/Italic.png?raw=true)                       |
| Underline         | `__Text__`                                | `__This looks like a link__`                                  | ![Underline](https://github.com/hanhy06/embellish_chat/blob/fabric/1.21.9/docs/images/Underline.png?raw=true)                 |
| Strike            | `~~Text~~`                                | `~~This content is outdated~~`                                | ![Strike](https://github.com/hanhy06/embellish_chat/blob/fabric/1.21.9/docs/images/Strike.png?raw=true)                       |
| Obfuscated        | `\|\|Text\|\|`                            | `\|\|This will be unreadable\|\|`                             | ![Obfuscated](https://github.com/hanhy06/embellish_chat/blob/fabric/1.21.9/docs/images/Obfuscated.gif?raw=true)               |
| Color             | `[Text]<#hex or color preset or rainbow>` | `look at this [rainbow]<rainbow>`                             | ![Color](https://github.com/hanhy06/embellish_chat/blob/fabric/1.21.9/docs/images/Color.png?raw=true)                         |
| Link              | `[Text](URL)`                             | `Download it [here](https://modrinth.com/mod/embellish_chat)` | ![Link](https://github.com/hanhy06/embellish_chat/blob/fabric/1.21.9/docs/images/Link.gif?raw=true)                           |
| Font              | `[Text]{Path}`                            | `[Blorp Zorp]{minecraft:alt}`                                 | ![Font](https://github.com/hanhy06/embellish_chat/blob/fabric/1.21.9/docs/images/Font.png?raw=true)                           |
| Mention           | `@PlayerName`                             | `Hello, @Player492!`                                          | ![Mention](https://github.com/hanhy06/embellish_chat/blob/fabric/1.21.9/docs/images/Mention.png?raw=true)                     |
| Escape Formatting | Use a backslash `\`                       | `This is not \**bold**.`                                      | ![Escape_Formatting](https://github.com/hanhy06/embellish_chat/blob/fabric/1.21.9/docs/images/Escape_Formatting.png?raw=true) |

---

⚙️ Command

- **`/embellish_chat reload`**: Reloads the mod's configuration from the `embellish_chat.json` file.

- **`/embellish_chat ban <player>`**: Prevents the specified player from using the chat styling features.

- **`/embellish_chat pardon <player>`**: Allows the specified player to use the chat styling features again.

---
⚙️ Configuration

### Sample `embellish_chat.json`

You can find `embellish_chat.json` in your config folder.

```
{
  "inChatStylingEnabled": true,
  "mentionEnabled": true,
  "fontEnabled": true,
  "coloringEnabled": true,
  "rainbowEnabled": true,
  "openUriEnabled": true,
  "markdownEnabled": true,
  "offlineColorEnabled": true,
  "defaultMentionColor": "0xFFFF55",
  "defaultMentionSound": "minecraft:entity.experience_orb.pickup",
  "defaultMentionPitch": 1.75,
  "defaultMentionMessage": " mentioned you",
  "defaultChatColor": "0x000000",
  "defaultChatFont": "",
  "defaultColorPreset": {
    "dark green": "0x00AA00",
    ...
  },
  "bannedPlayerList": [
    ...
  ]
}
```

### Configuration Options

- **`inChatStylingEnabled`**: If `true`, enables all chat styling features (color, font, mentions, markdown, etc.).
- **`mentionEnabled`**: If `true`, enables the `@` mention feature.
- **`fontEnabled`**: If `true`, allows specifying a font for chat messages.
- **`coloringEnabled`**: If `true`, enables custom text coloring with HEX codes or preset.
- **`rainbowEnabled`** If `true`, enables rainbow text in coloring.
- **`openUriEnabled`**: If `true`, enables clickable links in chat.
- **`markdownEnabled`**: If `true`, enables Markdown formatting (bold, italic, underline, strikethrough, obfuscation).
- **`offlineColorEnabled`**: This mod currently iterates through all teams to check which team an offline player belongs to in order to retrieve their team color.
  If this option is set to false, the mod will no longer iterate through teams to obtain the color.
  It is recommended to disable this option if server performance is critical, the computer has low specifications, or the server has a large number of players.
- **`defaultMentionColor`**: Sets the default HEX color for mentioned players not on a team.
- **`defaultMentionSound`**: Sets the sound event ID to play on mention.
- **`defaultMentionPitch`**: Sets the pitch of the mention sound.
- **`defaultMentionMessage`**: Sets the message displayed after the sender's target on mention.
- **`defaultChatColor`**: Sets the default text color.
  If the value is 0, no color is applied.
  If the value is less than 0, rainbow mode is activated and a gradient is applied to all text.
  This behavior is not affected by rainbowEnabled.
- **`defaultChatFont`**: Sets the default font for chat messages.
- **`defaultColorPreset`**: This is a user-defined preset. It can be defined as `"key" : "hex color value"` (for example, `"poo" : "0x4E3629"`).
- **`bannedPlayerList`**: Keeps track of the UUIDs of banned users. Players on this list are not allowed to use styling features.

---

📜 License & Etc.

This project is licensed under the **Apache License 2.0**.

To ensure everyone gets the latest and safest version, please download the mod from the official sources below. I would appreciate it if you link to these pages rather than re-hosting the files.

If you discover a bug or would like to suggest a new feature, please use the Discord server to let me know.

Official Download on Modrinth: [here](https://modrinth.com/mod/embellish_chat)

Source Code on GitHub: [here](https://github.com/hanhy06/embellish_chat)
