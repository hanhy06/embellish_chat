# Embellish Chat for Fabric

**Embellish Chat** is a Fabric mod that enhances the chat experience on Minecraft servers. It makes player communication more expressive and convenient with Markdown‑style formatting, mentionSegments, clickable links, and mentionSegment notifications.

---

## ✨ Key Features

* **Markdown‑Style Formatting**: Bold, italic, underline, strikethrough, obfuscation, color, custom font, and links. Works in public chat, private messages, and commands.
* **Mention System**: Mention individual players, your team, everyone, or nearby players with `@`. Online targets receive a notification; mentionSegments auto‑tint to the player/team color.
* **Message Metadata**: Hover to see the send time, and click a message to copy it to the clipboard.

---

## 🛠️ Markdown Usage

Use the following patterns directly in the chat window:

| Feature         | Syntax            | Example                                                       | Preview                                                                                                         |
|-----------------| ----------------- |---------------------------------------------------------------|-----------------------------------------------------------------------------------------------------------------|
| Bold            | `**Text**`        | `**You really need to read this!**`                           | ![Bold](https://github.com/hanhy06/embellish-chat/blob/1.21.9/docs/images/Bold.png?raw=true)             |
| Italic          | `_Text_`          | `_This is top secret..._`                                     | ![Italic](https://github.com/hanhy06/embellish-chat/blob/1.21.9/docs/images/Italic.png?raw=true)         |
| Underline       | `__Text__`        | `__Check this out__`                                          | ![Underline](https://github.com/hanhy06/embellish-chat/blob/1.21.9/docs/images/Underline.png?raw=true)   |
| Strikethrough   | `~~Text~~`        | `~~We don’t talk about this anymore~~`                        | ![Strike](https://github.com/hanhy06/embellish-chat/blob/1.21.9/docs/images/Strikethrough.png?raw=true)  |
| Obfuscated      | `\|\|Text\|\|`    | `\|\|Unreadable text\|\|`                                     | ![Obfuscated](https://github.com/hanhy06/embellish-chat/blob/1.21.9/docs/images/Obfuscated.gif?raw=true) |
| Color (Hex)     | `[Text]<#RRGGBB>` | `[Blue]<#0000FF> like the deep ocean`                         | ![Color](https://github.com/hanhy06/embellish-chat/blob/1.21.9/docs/images/Color_Hex.png?raw=true)       |
| Color (Preset)  | `[Text]<preset>`  | `[pink]<pink> pig`                                            | ![Color](https://github.com/hanhy06/embellish-chat/blob/1.21.9/docs/images/Color_Preset.png?raw=true)    |
| Color (Rainbow) | `[Text]<RAINBOW>` | `look at this [rainbow]<RAINBOW>`                             | ![Color](https://github.com/hanhy06/embellish-chat/blob/1.21.9/docs/images/Color_Rainbow.png?raw=true)   |
| Color (Shadow)  | `[Text]<SD:#hex>` | `[greeea]<SD:#00ff00>`                                        | ![Color](https://github.com/hanhy06/embellish-chat/blob/1.21.9/docs/images/Color_Shadow.png?raw=true)    |
| Link            | `[Text](URL)`     | `Download it [here](https://modrinth.com/mod/embellish-chat)` | ![Link](https://github.com/hanhy06/embellish-chat/blob/1.21.9/docs/images/Link.gif?raw=true)             |
| Font            | `[Text]{path}`    | `[Blorp Zorp]{minecraft:alt}`                                 | ![Font](https://github.com/hanhy06/embellish-chat/blob/1.21.9/docs/images/Font.png?raw=true)             |
| Mention         | `@PlayerName`     | `Hello, @User`                                                | ![Mention](https://github.com/hanhy06/embellish-chat/blob/1.21.9/docs/images/Mention.png?raw=true)       |

> **Notes**
>
> * For security, the **Link** feature only recognizes URLs using the `https://` protocol.
> * *Preset* values depend on the mod's configuration (e.g., `pink`, `blue`, etc.).
> * `path` for **Font** accepts a namespaced ID such as `minecraft:alt`.
> * If multiple style types exist and no preset is configured, the user can enter one manually. The separator can be checked through /ec help style.

---

## 🗣️ Mention System

| Target          | Behavior                                                                                                                                                                                                                                                                |
|-----------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `@Player`       | Mentions a specific player. If the player is online, the mentionSegment adopts the player’s display name style (including team color). If the player is offline or does not exist, the mentionSegment uses the player’s team color if available, otherwise the default mentionSegment color. |
| `@team(name)`   | Mentions all players in the specified team. If the team has a color, the mentionSegment is displayed in that color; otherwise it falls back to the default mentionSegment color.                                                                                                      |
| `@group(name)`  | Mentions all players in the specified LuckPerms group.                                                                                                                                                                                                                  |
| `@everyone`     | Mentions all players on the server. Always uses the **default mentionSegment color**.                                                                                                                                                                                          |
| `@here`         | Mentions all players within a configurable radius in the same world as the sender. The default radius is **64 blocks**, adjustable in the configuration. Uses the **default mentionSegment color**.                                                                            |

> **Notes**
> 
> * Online mentionSegment targets receive a notification, and the message is automatically styled using the appropriate color and formatting based on the target’s team, display name, or default rules.
> * The mentionSegment notification sound uses the **UI** sound category by default. On **Minecraft 1.21.5 and earlier**, it falls back to the **PLAYER** category.
> * `@group(name)` requires LuckPerms. Without it, the mentionSegment resolves to no players.
---

## ⌨️ Commands

* **`/embellish_chat reload`** — Reloads the configuration from `config/embellish_chat.json`.
* **`/embellish_chat ban <player>`** — Prevents the specified player from using the mod’s all features.
* **`/embellish_chat pardon <player>`** — Restores access to the mod’s all features for the specified player.
* **`/embellish_chat test regex <regex> <test>`** - Compiles the `<regex>` pattern and matches it against the `<test>` string.
* **`/embellish_chat test stress <count> <test>`** - Sends a fake message containing `<test>` exactly `<count>` times with the permissions of the user who executed the command, and processes it the same way as a real message.

* **`/ec help mentionSegment`** - Displays the currently available mentionSegments, how to use them, the mentionSegment targets, and the applicable style types.
* **`/ec help style`** - Displays the currently available styles, how to use them, the applicable style types, and the required input options.
* **`/ec notification`** - Allows you to decide whether to receive a notification when you are mentioned.

> **Notes**
>
> * All commands in the /embellish_chat family require OP level 2.
> * Commands in the /ec family do not require any OP level and can be used by all users.
> * /ec notification requires LuckPerms, otherwise the setting is unavailable.

---

## ⚙️ Configuration

The configuration file is located at: `config/embellish_chat.json`.

### Sample

```
{
  //version
  "version": "2.2.0",
  
  //rules
  "stylingRules": {
    "embellish_chat.chat": [
      {
        "pattern": "\\[([^\\]]+?)]\\((.*?)\\)",
        "styles": [
          {
            "styleType": "URL",
            "preset": ""
          }
        ]
      },
      ...
    ],
    "embellish_chat.command_argument": [
      ...
    ]
  },
  "mentionRules": {
    "embellish_chat.mentionSegment": [
      {
        "pattern": "@here()",
        "mentionSegments": [
          {
            "mentionType": "INSIDE",
            "preset": "64"
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
  
  //presets
  "delimiter": ",",
  "timestamp": "yyyy-MM-dd HH:mm:ss",
  "urlColor": "0x0000EE",
  "colorPreset": {
    "green": "0x55FF55",
    "dark green": "0x00AA00",
    "black": "0x000000",
    "yellow": "0xFFFF55",
    "dark red": "0xAA0000",
    "light purple": "0xFF55FF",
    "dark purple": "0xAA00AA",
    "gold": "0xFFAA00",
    "red": "0xFF5555",
    "dark gray": "0x555555",
    "aqua": "0x55FFFF",
    "gray": "0xAAAAAA",
    "white": "0xFFFFFF",
    "blue": "0x5555FF",
    "dark aqua": "0x00AAAA",
    "dark blue": "0x0000AA"
  },
  "mentionColor": "0xFF55FF",
  "mentionSound": "minecraft:entity.experience_orb.pickup",
  "mentionPitch": 1.75,
  "mentionTitlePrefix": "",
  "mentionTitleSuffix": " mentioned you",
  
  //banned player list
  "bannedPlayerList": []
}
```

---

## Styling Configuration

Embellish Chat , powered by a regular expression , provides powerful flexibility to style nearly any chat pattern imaginable.
However, this approach parses every chat message and may cause performance degradation on large-scale servers. (It is expected to have no significant performance impact on typical general servers.)
Furthermore, creating custom rules can be challenging because Regex itself is inherently complex. We recommend using various AI tools for assistance with rule creation and optimization.

<span style="color:red">
In this mod, the order of mentionSegment rules and styling rules is extremely important!
Depending on the order, serious bugs may occur, so please be careful.  
The mod applies mentionSegments and styling from top to bottom in the given order.
</span>

### Style Rule Structure

This section defines the text styling rules.<br>
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

you can see more detail [StyleWiki.md](https://github.com/hanhy06/embellish-chat/blob/1.21.9/docs/wiki/StyleWiki.md)

---

## Mention Configuration

A mentionSegment rule is applied only when the message satisfies all targets specified in the rule — effectively using the intersection of all mentionSegment targets.

### Mention Rule Structure

Defines the text styling rules.<br>
Each rule consists of a regular expression (`pattern`), mentionSegment action list(`mentionSegments`) and style action list(`styles`).

```
{
    "pattern": "@here()",
    "mentionSegments": [
      {
        "mentionType": "INSIDE",
        "preset": "64"
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

* **pattern** The pattern must contain one capturing group, which represents the mentionSegment option. For example, this could be a team name or a LuckPerms group name.
* **mentionSegments** The mentionSegments field is a list of mentionSegment actions. Each action specifies a mentionType and an optional preset.
* **styles** The styles field works the same way as in the styling rules section.

you can see more detail [MentionWiki.md](https://github.com/hanhy06/embellish-chat/blob/1.21.9/docs/wiki/MentionWiki.md)

---

### LuckPerms & Permission

This mode supports the permission features of the LuckPerms and Fabric Permissions API.

The keys in stylingRules and mentionRules represent each permission (such as chat, command, mentionSegment, etc.). These keys are default keys, and the system works even if they are not explicitly defined.

The mod checks from top to bottom and applies the rules registered for each permission.

Both the `LUCK_PERMS_GROUP` mentionSegment type and `/ec notification` require LuckPerms.
If it’s not installed, these features do nothing.

---
## 📊 TPS Latency Test

**Test Environment**

* Version: Embellish Chat 2.2.0 (DEV)
* CPU: 13th Gen Intel(R) Core(TM) i7-1360P
* RAM: 2GB max
* System: Windows 11

* Minecraft: 1.21.10
* World: Single Play / Superflat

**These results illustrate general performance trends rather than a strict scientific benchmark, as the testing environment does not fully replicate all possible server configurations.**

The graph below shows the TPS (Ticks Per Second) latency measurements for this mod.

Each test message was configured to include 230 or 50 characters per tick, and the system was stressed by sending up to 400 × 20 chat messages per second. Although the average latency increases as the message rate rises, most servers handle around 200 × 20 messages per second (≈4,000 messages) or fewer, making TPS impact negligible under typical gameplay conditions.

* ```execute as @a run embellish_chat test stress n "@hanhy ~~[Test]<RAINBOW>__ 100% working!__||50||~~"```
* ```execute as @a run embellish_chat test stress n "@everyone @here **Check out this new [update]<green> __news__** right [here](https://github.com/hanhy06/embellish_chat)! _First come, first served — join now for an exclusive ||special|| gift!_ ~~If you come late, there won't be any left~~"```

![Latency](https://github.com/hanhy06/embellish-chat/blob/1.21.9/docs/images/Latency2.2.0.png?raw=true)

---

## 📜 License & Links

This project is licensed under the **Apache License 2.0**.

Please download the mod from the official sources below to ensure you have the latest, safest version. Linking to these pages is appreciated; please avoid re‑hosting files.

* **Official Download (Modrinth):** [https://modrinth.com/mod/embellish-chat](https://modrinth.com/mod/embellish-chat)
* **Source Code (GitHub):** [https://github.com/hanhy06/embellish-chat](https://github.com/hanhy06/embellish-chat)

---

## ✨ Feedback & Support

Found a bug or have a feature request? Please open an issue or reach out on the project’s Discord server.

If you want to receive updates sooner, please press the heart ❤️ on our Modrinth page! Your support means a lot!