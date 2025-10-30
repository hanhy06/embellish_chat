# Embellish Chat for Fabric

**Embellish Chat** is a Fabric mod that enhances the chat experience on Minecraft servers. It makes player communication more expressive and convenient with Markdown‑style formatting, mentions, clickable links, and mention notifications.

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
  //rules
  "stylingRules": {
    "command": [
      {
        "pattern": "\\*\\*(.+?)\\*\\*()",
        "styles": [
          {
            "styleType": "BOLD",
            "preset": ""
          }
        ]
      },
      {
        "pattern": "__(.+?)__()",
        "styles": [
          {
            "styleType": "UNDERLINE",
            "preset": ""
          }
        ]
      },
      ...
    ],
    "chat": [
      ...
    ]
  },
  "mentionRules": {
    "mention": [
      {
        "pattern": "@here()",
        "mentions": [
          {
            "mentionType": "HERE",
            "preset": ""
          }
        ],
        "styles": [
          {
            "styleType": "BOLD",
            "preset": ""
          }
        ]
      },
      ...
    ]
  },
  
  //preset
  "urlColor": "0x0000EE",
  "colorPreset": {
    "dark green": "0x00AA00",
    "green": "0x55FF55",
    "yellow": "0xFFFF55",
    "black": "0x000000",
    "dark red": "0xAA0000",
    "dark purple": "0xAA00AA",
    "light purple": "0xFF55FF",
    "dark gray": "0x555555",
    "red": "0xFF5555",
    "gold": "0xFFAA00",
    "aqua": "0x55FFFF",
    "gray": "0xAAAAAA",
    "white": "0xFFFFFF",
    "blue": "0x5555FF",
    "dark aqua": "0x00AAAA",
    "dark blue": "0x0000AA"
  },
  "delimiter": "-",
  "mentionColor": "0xFF55FF",
  "mentionSound": "minecraft:entity.experience_orb.pickup",
  "mentionPitch": 1.75,
  "mentionTitlePrefix": "",
  "mentionTitleSuffix": " mentioned you",
 
  //baned player list
  "bannedPlayerList": []
}
```

---

## Styling Configuration

Embellish Chat , powered by a regular expression , provides powerful flexibility to style nearly any chat pattern imaginable.
However, this approach parses every chat message and may cause performance degradation on large-scale servers. (It is expected to have no significant performance impact on typical general servers.)
Furthermore, creating custom rules can be challenging because Regex itself is inherently complex. We recommend using various AI tools for assistance with rule creation and optimization.

### Style Rule Structure

Defines the text styling rules.<br>
Each rule consists of a regular expression (`pattern`) and a style action list(`styles`).

```
{
    "pattern": "\\*\\*(.+?)\\*\\*()",
    "styles": [
        {
            "styleType": "BOLD",
            "preset": ""
        }
    ]
}
```

* **pattern** Must contain **two capturing groups**: 1 the text to apply the style to, 2 an optional captured value that can be passed as an argument.
* **styles** This is a list of style actions. Each action consists of a style type (`styleType`) and style option preset(`preset`).

you can see style type list in StyleWiki.md

---

## Mention Configuration

A mention rule is applied only when the message satisfies all targets specified in the rule — effectively using the intersection of all mention targets.

### Mention Rule Structure

Defines the text styling rules.<br>
Each rule consists of a regular expression (`pattern`), mention action list(`mentions`) and style action list(`styles`).

```
{
    "pattern": "@here()",
    "mentions": [
      {
        "mentionType": "HERE",
        "preset": ""
      }
    ],
    "styles": [
      {
        "styleType": "BOLD",
         "preset": ""
      }
  ]
}
```

* **pattern** The pattern must contain one capturing group, which represents the mention target. For example, this could be a team name or a LuckPerms group name.
* **mentions** The mentions field is a list of mention actions. Each action specifies a mentionType and an optional preset.
* **styles** The styles field works the same way as in the styling rules section.

you can see mention type list in MentionWiki.md

---
## 📊 TPS Latency Test

Test Version : Embellish Chat 2.0.0(DEV)

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

## ✨ Feedback & Support

Found a bug or have a feature request? Please open an issue or reach out on the project’s Discord server.
