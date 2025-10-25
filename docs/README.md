# EmbellishChat for Fabric

**EmbellishChat** is a Fabric mod that enhances the Minecraft chat experience on servers. It makes player communication more expressive and convenient with Markdown‑style formatting, mentions, clickable links, and mention notifications.

---

## ✨ Key Features

* **Markdown‑Style Formatting**: Bold, italic, underline, strikethrough, obfuscation, color, custom font, and links. Works in public chat, private messages and command.
* **Mention System**: Mention individual players, your team, everyone, or nearby players with `@`. Online targets receive a notification; mentions auto‑tint to the player/team color.
* **Message Metadata**: Hover to see the send time click a message to copy it to the clipboard.

---

## 🛠️ Markdown Usage

Use the following patterns directly in the chat window:

| Feature         | Syntax            | Example                                                       | Preview                                                                                                         |
|-----------------| ----------------- |---------------------------------------------------------------|-----------------------------------------------------------------------------------------------------------------|
| Bold            | `**Text**`        | `**You really need to read this!**`                           | ![Bold](https://github.com/hanhy06/embellish_chat/blob/fabric/1.21.9/docs/images/Bold.png?raw=true)             |
| Italic          | `_Text_`          | `_This is top secret..._`                                     | ![Italic](https://github.com/hanhy06/embellish_chat/blob/fabric/1.21.9/docs/images/Italic.png?raw=true)         |
| Underline       | `__Text__`        | `__Check this out__`                                          | ![Underline](https://github.com/hanhy06/embellish_chat/blob/fabric/1.21.9/docs/images/Underline.png?raw=true)   |
| Strikethrough   | `~~Text~~`        | `~~We don’t talk about this anymore~~`                        | ![Strike](https://github.com/hanhy06/embellish_chat/blob/fabric/1.21.9/docs/images/Strikethrough.png?raw=true)  |
| Obfuscated      | `\|\|Text\|\|`    | `\|\|Unreadable text\|\|`                                     | ![Obfuscated](https://github.com/hanhy06/embellish_chat/blob/fabric/1.21.9/docs/images/Obfuscated.gif?raw=true) |
| Color (Hex)     | `[Text]<#RRGGBB>` | `[Blue]<#0000FF> like the deep ocean`                         | ![Color](https://github.com/hanhy06/embellish_chat/blob/fabric/1.21.9/docs/images/Color_Hex.png?raw=true)       |
| Color (Preset)  | `[Text]<preset>`  | `[pink]<pink> pig`                                            | ![Color](https://github.com/hanhy06/embellish_chat/blob/fabric/1.21.9/docs/images/Color_Preset.png?raw=true)    |
| Color (Rainbow) | `[Text]<RAINBOW>` | `look at this [rainbow]<RAINBOW>`                             | ![Color](https://github.com/hanhy06/embellish_chat/blob/fabric/1.21.9/docs/images/Color_Rainbow.png?raw=true)   |
| Color (Shadow)  | `[Text]<SD:#hex>` | `[greeea]<SD:#00ff00>`                                        | ![Color](https://github.com/hanhy06/embellish_chat/blob/fabric/1.21.9/docs/images/Color_Shadow.png?raw=true)    |
| Link            | `[Text](URL)`     | `Download it [here](https://modrinth.com/mod/embellish-chat)` | ![Link](https://github.com/hanhy06/embellish_chat/blob/fabric/1.21.9/docs/images/Link.gif?raw=true)             |
| Font            | `[Text]{path}`    | `[Blorp Zorp]{minecraft:alt}`                                 | ![Font](https://github.com/hanhy06/embellish_chat/blob/fabric/1.21.9/docs/images/Font.png?raw=true)             |
| Mention         | `@PlayerName`     | `Hello, @User`                                                | ![Mention](https://github.com/hanhy06/embellish_chat/blob/fabric/1.21.9/docs/images/Mention.png?raw=true)       |

> **Notes**
>
> * For security, the **Link** feature only recognizes URLs using the `https://` protocol.
> * *Preset* values depend on the mod's configuration (e.g., `pink`, `blue`, etc.).
> * `path` for **Font** accepts a namespaced ID such as `minecraft:alt`.

---

## 🗣️ Mention System

The mention notification sound uses the **UI** sound category by default. On **Minecraft 1.21.5 and earlier**, it falls back to the **PLAYER** category.

| Target      | Behavior                                                                                                                                                                      |
| ----------- |-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `@Player`   | Mentions a specific player. The mention is bolded and tinted with the player's team color if available; otherwise the default mention color is used.                          |
| `@team`     | Mentions all players on the sender’s team. Displays in the team color falls back to the group mention color if the team has no color.                                         |
| `@everyone` | Mentions everyone on the server. Uses the **group mention color**.                                                                                                            |
| `@here`     | Mentions all players within a configurable radius in the same world as the sender. Default radius is **64 blocks**; adjustable in settings. Uses the **group mention color**. |

Online mention targets receive a notification, and the message is automatically styled (bold + color) according to team/default rules.

You can configure group mentions so that only players with operator privileges (op) are allowed to use them.

---

## ⌨️ Commands

* **`/embellish_chat reload`** — Reloads the configuration from `config/embellish_chat.json`.
* **`/embellish_chat ban <player>`** — Prevents the specified player from using the mod’s styling features.
* **`/embellish_chat pardon <player>`** — Restores access to the mod’s styling features for the specified player.

---

## ⚙️ Configuration

The configuration file is located at: `config/embellish_chat.json`.

### Sample

```
{
  //style
  "stylingRules": {
    "command": [
      {
        "pattern": "(?<!\\\\)\\*\\*(.+?)\\*\\*()",
        "styleType": "BOLD",
        "option": ""
      },
      {
        "pattern": "(?<!\\\\)__(.+?)__()",
        "styleType": "UNDERLINE",
        "option": ""
      },
      ...
    ],
    "chat": [
      ...
    ]
  },
  "urlColor": "0x0000EE",
  "colorPreset": {
    "dark green": "0x00AA00",
    "green": "0x55FF55",
    ...
  },
  
  //mention
  "mentionEnabled": true,
  "groupMentionOpOnly": true,
  "offlineColorEnabled": true,
  "mentionColor": "0xFFFF55",
  "groupMentionColor": "0xAAAAFF",
  "mentionSound": "minecraft:entity.experience_orb.pickup",
  "mentionPitch": 1.75,
  "mentionTitlePrefix": "",
  "mentionTitleSuffix": " mentioned you",
  "hereRadius": 64.0,
  
  //banned plsyer list
  "bannedPlayerList": []
}
```

---

## Styling Configuration

Embellish Chat , powered by a regular expression , provides powerful flexibility to style nearly any chat pattern imaginable.
However, this approach parses every chat message and may cause performance degradation on large-scale servers. (It is expected to have no significant performance impact on typical general servers.)
Furthermore, creating custom rules can be challenging because Regex itself is inherently complex. We recommend using various AI tools for assistance with rule creation and optimization.

### Rule Structure

Defines the text styling rules.<br>
Each rule consists of a regular expression (`pattern`), a style type (`styleType`), and an option (`option`).

```
{
    "pattern": "(?<!\\\\)\\*\\*(.+?)\\*\\*()",
    "styleType": "BOLD",
    "option": ""
}
```

* **pattern**
  Must contain **two capturing groups**:<br>
  1 the text to apply the style to,<br>
  2 an optional captured value that can be passed as an argument.

* **styleType**
  Specifies the type of style to apply (see the list below).

* **option**
  A constant-like global option that can override dynamic behavior.
  If empty, the captured **group 2** from the regex is used instead.

### Global Style Configuration

```
{
    "pattern": "(.+)()",
    "styleType": "COLOR_HEX",
    "option": "#FFAAAA"
}
```

You can define a global style by capturing the entire text and assigning a fixed option value.
For example, to make all text a specific color, capture all text and set a color in the option field.

### Available Style Types

| Type            | Description                                                                                                                |
| --------------- |----------------------------------------------------------------------------------------------------------------------------|
| `METADATA`      | When the mouse hovers over the text, display the time the server received it, and when clicked, copy it to the clipboard.  |
| `COLOR_HEX`     | Applies the color specified by the HEX code provided as an option.                                                         |
| `COLOR_RAINBOW` | Cycles through rainbow colors                                                                                              |
| `COLOR_PRESET`  | Uses a predefined color name from the `colorPreset` section                                                                |
| `COLOR_SHADOW`  | Applies the HEX code color provided as an option to the shadow.                                                            |
| `FONT`          | Changes the font style                                                                                                     |
| `URL`           | Allows opening the URL provided as an option when clicked.                                                                 |
| `BOLD`          | Bold text (**text**)                                                                                                       |
| `ITALIC`        | Italic text (*text*)                                                                                                       |
| `UNDERLINE`     | Underlined text (**text**)                                                                                                 |
| `STRIKETHROUGH` | Strikethrough text (~~text~~)                                                                                              |
| `OBFUSCATED`    | Applies Minecraft style obfuscation to make the text unreadable.                                                           |

---

## 📊 TPS Latency Test

The graph below shows the TPS (Ticks Per Second) latency measurement results for this mod.

Each command was configured to parse 250 characters per tick,
and the test recorded TPS changes when sending up to 378 × 20 chat messages per second.

As shown in the graph, the average latency gradually increases as the command count grows.
However, since most servers process around 200 × 20 messages per second (≈ 4000 chats) or fewer,
TPS degradation is negligible in typical gameplay environments.

Below are the commands used in the actual test.

```w @a "**_@everyone__ Everyone, [Attention]<#FFAA00>!__** [This link](https://modrinth.com/mod/embellish-chat) is __*very*__ important. [This is [RAINBOW]<RAINBOW>]<green>! Also check out [shadow text]<SD:#55FFFF>. ||secret|| and [font]{minecraft:alt}."```

![Latency](https://github.com/hanhy06/embellish_chat/blob/fabric/1.21.9/docs/images/Latency.png?raw=true)

---

## 📜 License & Links

This project is licensed under the **Apache License 2.0**.

Please download the mod from the official sources below to ensure you have the latest, safest version. Linking to these pages is appreciated; please avoid re‑hosting files.

* **Official Download (Modrinth):** [https://modrinth.com/mod/embellish-chat](https://modrinth.com/mod/embellish-chat)
* **Source Code (GitHub):** [https://github.com/hanhy06/embellish_chat](https://github.com/hanhy06/embellish_chat)

---

## 🐞 Feedback & Support

Found a bug or have a feature request? Please open an issue or reach out on the project’s Discord server.
