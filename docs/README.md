# Embellish Chat for Fabric

**Embellish Chat** is a Fabric mod that enhances the chat experience on Minecraft servers. It makes player communication more expressive and convenient with Markdown‑style formatting, mentions, clickable links, and mention notifications.

---

## ✨ Key Features

* **Markdown‑Style Formatting**: Bold, italic, underline, strikethrough, obfuscation, color, custom font, and links. Works in public chat, private messages, and commands.
* **Mention System**: Mention individual players, your team, everyone, or nearby players with `@`. Online targets receive a notification; mentions auto‑tint to the player/team color.
* **Message Metadata**: Hover to see the send time, and click a message to copy it to the clipboard.

---

## 🛠️ Markdown Usage

Use the following patterns directly in the chat window:

| Feature         | Syntax            | Example                                                       | Preview                                                                                                  |
|-----------------| ----------------- |---------------------------------------------------------------|----------------------------------------------------------------------------------------------------------|
| Bold            | `**Text**`        | `**You really need to read this!**`                           | ![Bold](https://github.com/hanhy06/embellish-chat/blob/1.21.9/docs/images/Bold.png?raw=true)             |
| Italic          | `_Text_`          | `_This is top secret..._`                                     | ![Italic](https://github.com/hanhy06/embellish-chat/blob/1.21.9/docs/images/Italic.png?raw=true)         |
| Underline       | `__Text__`        | `__Check this out__`                                          | ![Underline](https://github.com/hanhy06/embellish-chat/blob/1.21.9/docs/images/Underline.png?raw=true)   |
| Strikethrough   | `~~Text~~`        | `~~We don’t talk about this anymore~~`                        | ![Strike](https://github.com/hanhy06/embellish-chat/blob/1.21.9/docs/images/Strikethrough.png?raw=true)  |
| Obfuscated      | `\|\|Text\|\|`    | `\|\|Unreadable text\|\|`                                     | ![Obfuscated](https://github.com/hanhy06/embellish-chat/blob/1.21.9/docs/images/Obfuscated.gif?raw=true) |
| Color (Hex)     | `[Text]<#RRGGBB>` | `[Blue]<#0000FF> like the deep ocean`                         | ![Color](https://github.com/hanhy06/embellish-chat/blob/1.21.9/docs/images/Color_Hex.png?raw=true)       |
| Color (Preset)  | `[Text]<preset>`  | `[pink]<pink> pig`                                            | ![Color](https://github.com/hanhy06/embellish-chat/blob/1.21.9/docs/images/Color_Preset.png?raw=true)    |
| Color (Rainbow) | `[Text]<RAINBOW>` | `look at this [rainbow]<RAINBOW>`                             | ![Color](https://github.com/hanhy06/embellish-chat/blob/1.21.9/docs/images/Color_Rainbow.png?raw=true)   |
| Link            | `[Text](URL)`     | `Download it [here](https://modrinth.com/mod/embellish-chat)` | ![Link](https://github.com/hanhy06/embellish-chat/blob/1.21.9/docs/images/Link.gif?raw=true)             |
| Font            | `[Text]{path}`    | `[Blorp Zorp]{minecraft:alt}`                                 | ![Font](https://github.com/hanhy06/embellish-chat/blob/1.21.9/docs/images/Font.png?raw=true)             |
| Mention         | `@PlayerName`     | `Hello, @User`                                                | ![Mention](https://github.com/hanhy06/embellish-chat/blob/1.21.9/docs/images/Mention.png?raw=true)       |

> **Notes**
>
> * For security, the **Link** feature only recognizes URLs using the `https://` protocol.
> * *Preset* values depend on the mod's configuration (e.g., `pink`, `blue`, etc.).
> * `path` for **Font** accepts a namespaced ID such as `minecraft:alt`.
> * If multiple style types exist and no preset is configured, the user can enter one manually. The separator can be checked through /ec help style.
> * There are many style types in addition to the ones shown here. For more details, please refer to the [StyleWiki.md](https://github.com/hanhy06/embellish-chat/blob/1.21.9/docs/wiki/StyleWiki.md)

---

## 🗣️ Mention System

| Target          | Behavior                                                                                                                                                                                                                                                                |
|-----------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `@Player`       | Mentions a specific player. If the player is online, the mention adopts the player’s display name style (including team color). If the player is offline or does not exist, the mention uses the player’s team color if available, otherwise the default mention color. |
| `@team(name)`   | Mentions all players in the specified team. If the team has a color, the mention is displayed in that color; otherwise it falls back to the default mention color.                                                                                                      |
| `@group(name)`  | Mentions all players in the specified LuckPerms group.                                                                                                                                                                                                                  |
| `@everyone`     | Mentions all players on the server. Always uses the **default mention color**.                                                                                                                                                                                          |
| `@here`         | Mentions all players within a configurable radius in the same world as the sender. The default radius is **64 blocks**, adjustable in the configuration. Uses the **default mention color**.                                                                            |

> **Notes**
> 
> * Online mention targets receive a notification, and the message is automatically styled using the appropriate color and formatting based on the target’s team, display name, or default rules.
> * The mention notification sound uses the **UI** sound category by default. On **Minecraft 1.21.5 and earlier**, it falls back to the **PLAYER** category.
> * `@group(name)` requires LuckPerms. Without it, the mention resolves to no players.

---

## ⌨️ Commands

* **`/embellish-chat reload`** — Reloads the configuration from `config/embellish-chat.json`.
* **`/embellish-chat ban <player>`** — Prevents the specified player from using the mod’s all features.
* **`/embellish-chat pardon <player>`** — Restores access to the mod’s all features for the specified player.
* **`/embellish-chat test regex <regex> <test>`** - Compiles the `<regex>` pattern and matches it against the `<test>` string.
* **`/embellish-chat test stress <count> <test>`** - Sends a fake message containing `<test>` exactly `<count>` times with the permissions of the user who executed the command, and processes it the same way as a real message.

* **`/ec help mention`** - Displays the currently available mentions, how to use them, the mention targets, and the applicable style types.
* **`/ec help style`** - Displays the currently available styles, how to use them, the applicable style types, and the required input options.
* **`/ec notification`** - Allows you to decide whether to receive a notification when you are mentioned.

> **Notes**
>
> * All commands in the /embellish-chat family require OP level 2.
> * Commands in the /ec family do not require any OP level and can be used by all users.
> * /ec notification requires LuckPerms, otherwise the setting is unavailable.
> * Through ```notificationEnable```, you can configure whether users are allowed to set their notification preferences using /ec notification. If ```notificationEnable``` is false, the user’s preference is ignored and notifications are sent for all mentions.

---

## ⚙️ Configuration

The configuration file is located at: `config/embellish-chat.json`.

### Sample

```
{
  // version
  "version": "2.3.0",
  
  //rules
  "stylingRules": {
    "embellish-chat.chat": [
      {
        "pattern": "\\[([^\\]]+?)]\\((https://.*?)\\)",
        "styles": [
          {
            "styleType": "URL",
            "preset": ""
          }
        ]
      },
      ...
    ],
    "embellish-chat.command_argument": []
  },
  "mentionRules": {
    "embellish-chat.mention": [
      {
        "pattern": "@here()",
        "sound": "entity.experience_orb.pickup",
        "pitch": 1.75,
        "title": "%player:displayname% mentioned you",
        "mentions": [
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
  
  // presets
  "delimiter": ",",
  "timestamp": "yyyy-MM-dd HH:mm:ss",
  "urlColor": "#0000EE",
  "colorPreset": {
    "dark green": "#00AA00",
    ...
  },
  "notificationCommandEnable": true,
  "mentionColor": "#FF55FF",
  
  // banned players
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

A mention rule is applied only when the message satisfies all targets specified in the rule — effectively using the intersection of all mention targets.

### Mention Rule Structure

Defines the text styling rules.<br>
Each rule consists of a regular expression (`pattern`), mention action list(`mentions`) and style action list(`styles`).

```
{
  "pattern": "@here()",
  "sound": "entity.experience_orb.pickup",
  "pitch": 1.75,
  "title": "%player:displayname% mentioned you",
  "mentions": [
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

* **pattern** The pattern must contain one capturing group, which represents the mention option. For example, this could be a team name or a LuckPerms group name.
* **sound** This is the ID of the sound that the mentioner hears.
* **pitch** This is the pitch of the sound that the mentioner hears.”
* **title** “This is the title that appears on the mentioner’s screen. It supports the Text Placeholder API, and %player:displayname% is the name of the person who was mentioned.”
* **mentions** The mentions field is a list of mention actions. Each action specifies a mentionType and an optional preset.
* **styles** The styles field works the same way as in the styling rules section.

you can see more detail [MentionWiki.md](https://github.com/hanhy06/embellish-chat/blob/1.21.9/docs/wiki/MentionWiki.md)

---

### LuckPerms & Permission

This mode supports the permission features of the LuckPerms and Fabric Permissions API.

The keys in stylingRules and mentionRules represent each permission (such as chat, command, mention, etc.). These keys are default keys, and the system works even if they are not explicitly defined.

The mod checks from top to bottom and applies the rules registered for each permission.

Both the `LUCK_PERMS_GROUP` mention type and `/ec notification` require LuckPerms.
If it’s not installed, these features do nothing.


---

### Text Placeholder API

This mod supports dynamic data through the Text Placeholder API.
The Text Placeholder API applies to the mention title, all presets used in rules, and the options that users can configure.

---
## 📊 TPS Latency Test

**Test Environment**

* Version: Embellish Chat 2.3.0 (DEV)
* CPU: 13th Gen Intel(R) Core(TM) i7-1360P
* RAM: 2GB max
* System: Windows 11

* Minecraft: 1.21.10
* World: Single Play / Superflat

**These results illustrate general performance trends rather than a strict scientific benchmark, as the testing environment does not fully replicate all possible server configurations.**

The graph below shows the TPS (Ticks Per Second) latency measurements for this mod.

Each test message was configured to include 230 or 50 characters per tick, and the system was stressed by sending up to 400 × 20 chat messages per second. Although the average latency increases as the message rate rises, most servers handle around 200 × 20 messages per second (≈4,000 messages) or fewer, making TPS impact negligible under typical gameplay conditions.

* ```execute as @a run embellish-chat test stress n "@hanhy ~~[Test]<RAINBOW>__ 100% working!__||50||~~"```
* ```execute as @a run embellish-chat test stress n "@everyone @here **Check out this new [update]<green> __news__** right [here](https://github.com/hanhy06/embellish-chat)! _First come, first served — join now for an exclusive ||special|| gift!_ ~~If you come late, there won't be any left~~"```

![Latency](https://github.com/hanhy06/embellish-chat/blob/1.21.9/docs/images/Latency2.3.0.png?raw=true)

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