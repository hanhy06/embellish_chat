# EmbellishChat for Fabric

EmbellishChat is a Fabric mod designed to enhance the chat experience on Minecraft servers. It makes communication between players more dynamic and convenient with features like Markdown-style text formatting, player mentions, clickable links, and mention notifications & history.

## ✨ Key Features

- **Extended Markdown-Style Chat Formatting:** Use Markdown-like styles (bold, italic, underline, strikethrough, obfuscation, color, font, url). Works in normal chat and whispers.

- **Mention System:** You can mention players, teams, everyone, or here using the @ symbol.
  If the mentioned target is online, they will receive a notification,
  and the message will be automatically formatted in the color of their team.

- **Metadata System:** When you hover the mouse over a message, you can see the time it was sent,
  and clicking the message automatically copies it to the clipboard.

- **Default Style System:** You can configure the default color and font of chat messages through the settings.

---

## 🛠️ Using Markdown

For security reasons, when using the Markdown URL feature,
only links with the https:// protocol are detected.

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

## 🛠️ Using Mention

The mention notification sound uses the UI category by default.
If the Minecraft version is 1.21.5 or earlier, it instead uses the PLAYER category.

| Feature   | Explanation                                                                                                                                                                                                            |
|-----------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| @Player   | Mentions a player. The mention is automatically bolded and colored using either the team color the player belongs to or the default mention color.                                                                     |
| @everyone | Mentions all players on the server. Uses the group mention color, and you can configure it so that only players with operator (op) privileges are allowed to use it.                                                   |
| @here     | Mentions all players within a certain distance of the sender in the same world. The default range is 64 blocks, but it can be adjusted through the settings. The group mention color is used for this type of mention. |
| @team     | Mentions all players in the sender’s team. By default, the mention is displayed in the team’s color, and if the team has no assigned color, it uses the group mention color instead.                                   |

---

## ⚙️ Command

- **`/embellish_chat reload`**: Reloads the mod's configuration from the `embellish_chat.json` file.

- **`/embellish_chat ban <player>`**: Prevents the specified player from using the mod features.

- **`/embellish_chat pardon <player>`**: Allows the specified player to use the mod features again.

---

## ⚙️ Configuration

---

## Sample `embellish_chat.json`

You can find `embellish_chat.json` in your config folder.

```
{
  // styling
  "inChatStylingEnabled": true,
  "fontEnabled": true,
  "coloringEnabled": true,
  "rainbowEnabled": true,
  "openUriEnabled": true,
  "markdownEnabled": true,
  "metadataEnabled": true,
  "colorPreset": {
    "black": "0x000000",
     ...
  },

  // mention
  "mentionEnabled": true,
  "groupMentionOpOnly": true,
  "offlineColorEnabled": true,
  "mentionColor": "0xFFFF55",
  "groupMentionColor": "0x0000AA",
  "mentionSound": "minecraft:entity.experience_orb.pickup",
  "mentionPitch": 1.75,
  "mentionTitlePrefix": "",
  "mentionTitleSuffix": " mentioned you",
  "hereRadius": 64.0,

  // default style
  "chatColor": "0x000000",
  "chatFont": "",

  // banned players (UUID list)
  "bannedPlayerList": [
    ...
  ]
}

```

---

## Configuration Options

---

### Styling

- **`inChatStylingEnabled`**: If `true`, enables all chat styling features (color, font, mentions, markdown, etc.).
- **`fontEnabled`**: If `true`, allows specifying a font for chat messages.
- **`coloringEnabled`**: If `true`, enables custom text coloring with HEX codes or preset.
- **`rainbowEnabled`**: If `true`, enables rainbow text in coloring.
- **`openUriEnabled`**: If `true`, enables clickable links in chat.
- **`markdownEnabled`**: If `true`, enables Markdown formatting (bold, italic, underline, strikethrough, obfuscation).
- **`metadataEnabled`**: If `true`, adds metadata to messages received by the user.
  This metadata includes the time the message was sent and allows the message to be automatically copied to the clipboard when clicked.
- **`colorPreset`**: A user-defined color preset. It can be defined as `"key" : "hex color value"` (for example, `"poo" : "0x4E3629"`).

---

### Mention

- **`mentionEnabled`**: If `true`, enables the `@` mention feature.
- **`groupMentionOpOnly`**: Determines whether a player must have operator (op) privileges to use everyone, here, or team mentions.
  If set to `false`, all players can use group mentions.
- **`offlineColorEnabled`**: When enabled, the mod iterates through all teams to check which team an offline player belongs to in order to retrieve their team color.
  If this option is set to `false`, the mod will no longer iterate through teams to obtain the color.
  It is recommended to disable this option if server performance is critical, the computer has low specifications, or the server has a large number of players.
- **`mentionColor`**: Sets the default HEX color for mentioned players who are not in a team.
- **`groupMentionColor`**: Sets the color used for group mentions (`@everyone`, `@here`, `@team`).
- **`mentionSound`**: Sets the sound event ID to play on mention.
- **`mentionPitch`**: Sets the pitch of the mention sound.
- **`mentionTitlePrefix`**: Text displayed before the mentioned target in the notification title.
- **`mentionTitleSuffix`**: Text displayed after the mentioned target in the notification title.
- **`hereRadius`**: When using the `@here` mention, it mentions all players within this radius around the sender.

---

### Etc.

- **`chatColor`**: Sets the default text color.
  If the value is `0`, no color is applied.
  If the value is less than `0`, rainbow mode is activated and a gradient is applied to all text.
  This behavior is not affected by `rainbowEnabled`.
- **`chatFont`**: Sets the default font for chat messages.
- **`bannedPlayerList`**: Keeps track of the UUIDs of banned users. Players on this list are not allowed to use styling features.

---

📜 License & Etc.

This project is licensed under the **Apache License 2.0**.

To ensure everyone gets the latest and safest version, please download the mod from the official sources below. I would appreciate it if you link to these pages rather than re-hosting the files.

If you discover a bug or would like to suggest a new feature, please use the Discord server to let me know.

Official Download on Modrinth: [here](https://modrinth.com/mod/embellish_chat)

Source Code on GitHub: [here](https://github.com/hanhy06/embellish_chat)
