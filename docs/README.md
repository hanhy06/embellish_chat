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

| Target        | Behavior                                                                                                                                                                                                                                                                |
| ------------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `@Player`     | Mentions a specific player. If the player is online, the mention adopts the player’s display name style (including team color). If the player is offline or does not exist, the mention uses the player’s team color if available, otherwise the default mention color. |
| `@team(name)` | Mentions all players in the specified team. If the team has a color, the mention is displayed in that color; otherwise it falls back to the default mention color.                                                                                                      |
| `@everyone`   | Mentions all players on the server. Always uses the **default mention color**.                                                                                                                                                                                          |
| `@here`       | Mentions all players within a configurable radius in the same world as the sender. The default radius is **64 blocks**, adjustable in the configuration. Uses the **default mention color**.                                                                            |

Online mention targets receive a notification, and the message is automatically styled using the appropriate color and formatting based on the target’s team, display name, or default rules.

---

## ⌨️ Commands

* **`/embellish_chat reload`** — Reloads the configuration from `config/embellish_chat.json`.
* **`/embellish_chat ban <player>`** — Prevents the specified player from using the mod’s all features.
* **`/embellish_chat pardon <player>`** — Restores access to the mod’s all features for the specified player.

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

<span style="color:red">
In this mod, the order of mention rules and styling rules is extremely important!
Depending on the order, serious bugs may occur, so please be careful.  
The mod applies mentions and styling from top to bottom in the given order.
</span>

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

you can see style type list in [StyleWiki.md](https://github.com/hanhy06/embellish_chat/blob/fabric/1.21.9/docs/wiki/StyleWiki.md)

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

you can see mention type list in [MentionWiki.md](https://github.com/hanhy06/embellish_chat/blob/fabric/1.21.9/docs/wiki/MentionWiki.md)

---
## 📊 TPS Latency Test

**Test Environment**

Version: Embellish Chat 2.1.0 (DEV)
CPU: 13th Gen Intel(R) Core(TM) i7-1360P
RAM: 2GB max
System: Windows 11

**Since I couldn’t run 400 latency tests per tick, I used command blocks instead.
Because the mention system doesn’t apply to command blocks, the actual latency is expected to be slightly higher than the results shown here.**

The graph below shows the TPS (Ticks Per Second) latency measurements for this mod.

Each test message was configured to include 230 characters per tick, and the system was stressed by sending up to 382 × 20 chat messages per second. Although the average latency increases as the message rate rises, most servers handle around 200 × 20 messages per second (≈4,000 messages) or fewer, making TPS impact negligible under typical gameplay conditions.

```w @a "@everyone @here **Check out this new [update]<green> __news__** right [here](https://github.com/hanhy06/embellish_chat)! _First come, first served — join now for an exclusive ||special|| gift!_ ~~If you come late, there won't be any left~~"```

![Latency](https://github.com/hanhy06/embellish_chat/blob/fabric/1.21.9/docs/images/Latency2.1.0.png?raw=true)

---

## 📜 License & Links

This project is licensed under the **Apache License 2.0**.

Please download the mod from the official sources below to ensure you have the latest, safest version. Linking to these pages is appreciated; please avoid re‑hosting files.

* **Official Download (Modrinth):** [https://modrinth.com/mod/embellish-chat](https://modrinth.com/mod/embellish-chat)
* **Source Code (GitHub):** [https://github.com/hanhy06/embellish_chat](https://github.com/hanhy06/embellish_chat)

---

## ✨ Feedback & Support

Found a bug or have a feature request? Please open an issue or reach out on the project’s Discord server.
