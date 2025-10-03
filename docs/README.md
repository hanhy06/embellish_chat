# EmbellishChat for Fabric

EmbellishChat is a Fabric mod designed to enhance the chat experience on Minecraft servers. It makes communication between players more dynamic and convenient with features like Markdown-style text formatting, player mentions, clickable links, and mention notifications & history.

✨ Key Features

* **Extended Markdown-Style Chat Formatting:** Use Markdown-like styles (bold, italic, underline, strikethrough, obfuscation, color, font). Works in normal chat and whispers.

* **Clickable Links:** Create clickable links in chat. For security, only the `https` protocol is recognized.

* **Player Mention System:** Use the `@` symbol to mention other players.

* **Notification:** Mentioned players who are online will hear a notification sound.

* **Team Colors:** If a mentioned player is on a team, their name will be displayed in their team's color.

* **Message Metadata & Copy:** Hovering over a chat message displays its timestamp. Clicking the message copies its content to your clipboard.

* **Default Style** You can set the default color and font of chat messages through the settings.

* **Customizable by Server:** All major features can be enabled, disabled, or fine-tuned by server administrators via the `embellish_chat.json` file.

---

🛠️ Usage

Use the following formats in the chat window to apply various styles to your messages.

| Feature           | Syntax                         | Example                                                       | Result                                                                                                                 |
|-------------------|--------------------------------|:--------------------------------------------------------------|------------------------------------------------------------------------------------------------------------------------|
| Bold              | `**Text**`                     | `**Important Message**`                                       | ![Bold](https://github.com/hanhy06/embellish_chat/blob/1.21.9/docs/images/Bold.png?raw=true)                           |
| Italic            | `_Text_`                       | `_A point I want to emphasize_`                               | ![Italic](https://github.com/hanhy06/embellish_chat/blob/1.21.9/docs/images/Italic.png?raw=true)                       |
| Underline         | `__Text__`                     | `__This looks like a link__`                                  | ![Underline](https://github.com/hanhy06/embellish_chat/blob/1.21.9/docs/images/Underline.png?raw=true)                 |
| Strike            | `~~Text~~`                     | `~~This content is outdated~~`                                | ![Strike](https://github.com/hanhy06/embellish_chat/blob/1.21.9/docs/images/Strike.png?raw=true)                       |
| Obfuscated        | `\|\|Text\|\|`                 | `\|\|This will be unreadable\|\|`                             | ![Obfuscated](https://github.com/hanhy06/embellish_chat/blob/1.21.9/docs/images/Obfuscated.gif?raw=true)               |
| Color             | `[Text]<#hex or color preset>` | `[Hello World]<red>`                                          | ![Color](https://github.com/hanhy06/embellish_chat/blob/1.21.9/docs/images/Color.png?raw=true)                         |
| Link              | `[Text](URL)`                  | `Download it [here](https://modrinth.com/mod/embellish_chat)` | ![Link](https://github.com/hanhy06/embellish_chat/blob/1.21.9/docs/images/Link.gif?raw=true)                           |
| Font              | `[Text]{Path}`                 | `[Blorp Zorp]{minecraft:alt}`                                 | ![Font](https://github.com/hanhy06/embellish_chat/blob/1.21.9/docs/images/Font.png?raw=true)                           |
| Mention           | `@PlayerName`                  | `Hello, @Player492!`                                          | ![Mention](https://github.com/hanhy06/embellish_chat/blob/1.21.9/docs/images/Mention.png?raw=true)                     |
| Escape Formatting | Use a backslash `\`            | `This is not \**bold**.`                                      | ![Escape_Formatting](https://github.com/hanhy06/embellish_chat/blob/1.21.9/docs/images/Escape_Formatting.png?raw=true) |

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
  "openUriEnabled": true,
  "markdownEnabled": true,
  "defaultMentionColor": "0xFFFF55",
  "defaultMentionSound": "minecraft:entity.experience_orb.pickup",
  "defaultMentionPitch": 1.75,
  "defaultMentionMessage": " mentioned you",
  "defaultChatColor": "-0x000001",
  "defaultChatFont": "",
  "defaultColorPreset": {
    "dark green": "0x00AA00",
    ...
  }
}
```

### Configuration Options

- **`inChatStylingEnabled`**: If `true`, enables all chat styling features (color, font, mentions, markdown, etc.).
- **`mentionEnabled`**: If `true`, enables the `@` mention feature.
- **`fontEnabled`**: If `true`, allows specifying a font for chat messages.
- **`coloringEnabled`**: If `true`, enables custom text coloring with HEX codes or preset.
- **`openUriEnabled`**: If `true`, enables clickable links in chat.
- **`markdownEnabled`**: If `true`, enables Markdown formatting (bold, italic, underline, strikethrough, obfuscation).
- **`defaultMentionColor`**: Sets the default HEX color for mentioned players not on a team.
- **`defaultMentionSound`**: Sets the sound event ID to play on mention.
- **`defaultMentionPitch`**: Sets the pitch of the mention sound.
- **`defaultMentionMessage`**: Sets the message displayed after the sender's name on mention.
- **`defaultChatColor`**: Sets the default text color. If `0`, not applied.
- **`defaultChatFont`**: Sets the default font for chat messages.
- **`defaultColorPreset`**: This is a user-defined preset. It can be defined as `"key" : "hex color value"` (for example, `"poo" : "0x4E3629"`).

---

📜 License & Distribution

This project is licensed under the **Apache License 2.0**.

To ensure everyone gets the latest and safest version, please download the mod from the official sources below. I would appreciate it if you link to these pages rather than re-hosting the files.

Official Download on Modrinth: [here](https://modrinth.com/mod/embellish_chat)

Source Code on GitHub: [here](https://github.com/hanhy06/embellish_chat)
