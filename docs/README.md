# EmbellishChat for Fabric

EmbellishChat is a Fabric mod designed to enhance the chat experience on Minecraft servers. It makes communication between players more dynamic and convenient with features like Markdown-style text formatting, player mentions, clickable links, and mention notifications & history.

✨ Key Features

* **Markdown-Style Chat Formatting:** Emphasize your messages with various text styles.

* **HEX Color Code Support:** Freely apply any color to your text using the `#RRGGBB` format.

* **Clickable Links:** Create clickable links in chat.

* **Player Mention System:** Use the `@` symbol to mention other players.

* **Notification:** Mentioned players who are online will hear a notification sound.

* **Team Colors:** If a mentioned player is on a team, their name will be displayed in their team's color.

* **Message Metadata & Copy:** Hovering over a chat message displays its timestamp. Clicking the message copies its content to your clipboard.

* **Default Style** You can set the default color and font of chat messages through the settings.

* **Customizable by Server:** All major features can be enabled, disabled, or fine-tuned by server administrators via the `embellish_chat.json` file.

---

🛠️ Usage

Use the following formats in the chat window to apply various styles to your messages.

| Feature           | Syntax              | Example                                                       | Result                                                                                                                 |
|-------------------|---------------------|:--------------------------------------------------------------|------------------------------------------------------------------------------------------------------------------------|
| Bold              | `**Text**`          | `**Important Message**`                                       | ![Bold](https://github.com/hanhy06/embellish_chat/blob/master/docs/images/Bold.png?raw=true)                           |
| Italic            | `_Text_`            | `_A point I want to emphasize_`                               | ![Italic](https://github.com/hanhy06/embellish_chat/blob/master/docs/images/Italic.png?raw=true)                       |
| Underline         | `__Text__`          | `__This looks like a link__`                                  | ![Underline](https://github.com/hanhy06/embellish_chat/blob/master/docs/images/Underline.png?raw=true)                 |
| Strike            | `~~Text~~`          | `~~This content is outdated~~`                                | ![Strike](https://github.com/hanhy06/embellish_chat/blob/master/docs/images/Strike.png?raw=true)                       |
| Obfuscated        | `\|\|Text\|\|`      | `\|\|This will be unreadable\|\|`                             | ![Obfuscated](https://github.com/hanhy06/embellish_chat/blob/master/docs/images/Obfuscated.gif?raw=true)               |
| Color             | `#HexCodeText#`     | `#FF5555Hello World#`                                         | ![Color](https://github.com/hanhy06/embellish_chat/blob/master/docs/images/Color.png?raw=true)                         |
| Link              | `[Text](URL)`       | `Download it [here](https://modrinth.com/mod/embellish_chat)` | ![Link](https://github.com/hanhy06/embellish_chat/blob/master/docs/images/Link.gif?raw=true)                           |
| Font              | `[Text]{Path}`      | `[Blorp Zorp]{minecraft:alt}`                                 | ![Font](https://github.com/hanhy06/embellish_chat/blob/master/docs/images/Font.png?raw=true)                           |
| Mention           | `@PlayerName`       | `Hello, @Player492!`                                          | ![Mention](https://github.com/hanhy06/embellish_chat/blob/master/docs/images/Mention.png?raw=true)                     |
| Escape Formatting | Use a backslash `\` | `This is not \**bold**.`                                      | ![Escape_Formatting](https://github.com/hanhy06/embellish_chat/blob/master/docs/images/Escape_Formatting.png?raw=true) |

---

⚙️ Configuration

### Sample `embellish_chat.json`

You can find `embellish_chat.json` in your config folder.

```jsonc
{
  "inChatStylingEnabled": true,
  "markdownEnabled": true,
  "openUriEnabled": true,
  "coloringEnabled": true,
  "fontEnabled": true,
  "mentionEnabled": true,
  "defaultMentionColor": "0xFFFF55",
  "defaultMentionSound": "minecraft:entity.experience_orb.pickup",
  "defaultChatColor": "0x0",
  "defaultChatFont": ""
}
```

### Configuration Options

* `inChatStylingEnabled`: If `true`, enables all chat styling features (color, font, mentions, markdown, etc.).
* `markdownEnabled`: If `true`, enables Markdown-like formatting (bold, italic, underline, strikethrough, obfuscation, links).
* `openUriEnabled`: If `true`, enables clickable links in chat.
* `coloringEnabled`: If `true`, enables custom text coloring with HEX codes.
* `fontEnabled`: If `true`, allows specifying a font for chat messages.
* `mentionEnabled`: If `true`, enables the `@` mention feature.
* `defaultMentionColor`: Sets the default HEX color for mentioned players not on a team.
* `defaultMentionSound`: Sets the sound event ID to play on mention.
* `defaultChatColor`: Sets the default text color. If `0`, not applied.
* `defaultChatFont`: Sets the default font for chat messages.

---

📜 License & Distribution

This project is licensed under the **Apache License 2.0**.

To ensure everyone gets the latest and safest version, please download the mod from the official sources below. I would appreciate it if you link to these pages rather than re-hosting the files.

Official Download on Modrinth: [here](https://modrinth.com/mod/embellish_chat)

Source Code on GitHub: [here](https://github.com/hanhy06/embellish_chat)
