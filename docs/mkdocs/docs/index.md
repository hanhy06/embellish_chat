# Embellish Chat

Embellish Chat modernizes your Minecraft server’s chat experience with a fully server-side design. It introduces Markdown-style formatting, interactive message events, and a robust mention system—no client installation required.

## Documentation

Detailed guides for each system can be found below:

* **[Configuration](#configuration)**
    * Learn how to set up `config.json`.
    * Explore the [Web Config Generator](https://hanhy06.github.io/embellish-chat/site/config-generator.html) for easy rule creation.
* **[Style System](style/StyleSystem.md)**
    * Comprehensive guide on text formatting, colors, and regex-based styling rules.
* **[Mention System](mention/MentionSystem.md)**
    * Guide on setting up player, team, and proximity notifications.

---

## Quick Reference

### Syntax Cheat Sheet

Use these patterns directly in the chat window.

| Feature            | Syntax                 | Example                          |
|:-------------------|:-----------------------|:---------------------------------|
| **Bold**           | `**Text**`             | `**Important**`                  |
| **Italic**         | `_Text_`               | `_Whisper_`                      |
| **Underline**      | `__Text__`             | `__Title__`                      |
| **Strikethrough**  | `~~Text~~`             | `~~Deleted~~`                    |
| **Obfuscated**     | `\|\|Text\|\|`         | `\|\|Secret\|\|`                 |
| **Color (Hex)**    | `[Text]<#Hex>`         | `[Sky]<#00AAFF>`                 |
| **Color (Preset)** | `[Text]<Preset>`       | `[Warning]<red>`                 |
| **Gradient**       | `[Text]<#Hex #Hex...>` | `[Fire]<#ffff00 #ff0000>`        |
| **Rainbow**        | `[Text]<RAINBOW>`      | `[Magic]<RAINBOW>`               |
| **Link**           | `[Text](URL)`          | `[Click Me](https://google.com)` |
| **Font**           | `[Text]{Font ID}`      | `[Rune]{minecraft:alt}`          |
| **Mention**        | `@Target`              | `@everyone`, `@User`             |

> **Note:** For complex styling logic or custom rules, refer to the **[Style System](style/StyleSystem.md)**.

### Mention Targets

| Target         | Description                                                     |
|:---------------|:----------------------------------------------------------------|
| `@PlayerName`  | Mentions a specific player.                                     |
| `@everyone`    | Mentions **all players** on the server.                         |
| `@here`        | Mentions players within a specific radius (Default: 64 blocks). |
| `@team(name)`  | Mentions members of a specific scoreboard team.                 |
| `@group(name)` | Mentions members of a specific LuckPerms group.                 |
| `@world(name)` | Mentions all players in a specific world.                       |

> **Note:** For complex mention logic or custom rules, refer to the **[Mention System](mention/MentionSystem.md)**.

---

## Commands

### User Commands (`/ec`)
Available to all players.

| Command            | Description                                                    |
|:-------------------|:---------------------------------------------------------------|
| `/ec help style`   | Displays available styles and syntax usage.                    |
| `/ec help mention` | Displays available mention types and targets.                  |
| `/ec notification` | Toggles personal mention notifications (if enabled in config). |

### Admin Commands (`/embellish-chat`)
Requires OP Level 2 or appropriate permissions.

| Command       | Arguments        | Description                                           |
|:--------------|:-----------------|:------------------------------------------------------|
| `reload`      | `None`           | Reloads `config/embellish-chat.json`.                 |
| `ban`         | `<player>`       | Blocks a player from using mod features.              |
| `pardon`      | `<player>`       | Restores mod access for a player.                     |
| `test regex`  | `<regex> <test>` | Tests a regex pattern against a string.               |
| `test stress` | `<count> <test>` | Simulates `<count>` messages for performance testing. |

---
## Configuration

The configuration file is located at `config/embellish-chat.json`.

```
{
  "version": "2.6.3",
  
  "stylingRules": {
    "embellish-chat.command_argument": [],
    "embellish-chat.chat": [...]
  },
  "mentionRules": {
    "embellish-chat.mention": [...]
  },
  
  "delimiter": ",",
  "timestamp": "yyyy-MM-dd HH:mm:ss",
  "urlColor": "#0000EE",
  "colorPreset": {...},
  "defaultTeamColor": "#FF55FF",
  "notificationCommandEnable": true,
  "mentionBroadcast": true,
  "bannedPlayerList": [],
  "notificationOffPlayerList": [],
  "webhook": ""
```

* The `version` field must not be modified manually.
* The core configuration logic is defined in `stylingRules` and `mentionRules`.
* Rules are processed from top to bottom, so placing a catch-all rule earlier may override more specific rules defined below.
* The `delimiter` value is internally handled as a regular expression; special characters such as `|` must be properly escaped.
* If `defaultTeamColor` is missing or set to `null`, automatic coloring will not be applied.
* To avoid JSON syntax errors and ensure valid configurations, using the [Web Config Generator](https://hanhy06.github.io/embellish-chat/site/config-generator.html) is strongly recommended:
  

---

## Compatibility

### Supported
* **Fabric Permissions API(Embedded):** Full integration for permission-based rules.
* **Text Placeholder API:** Supports placeholders in mention titles and presets.
* **LuckPerms:** Required for `@group` mentions.
* * **Open Parties and Claims:** Required for `@party` mentions.
* **Geyser:** Basic support (Mentions work; Click/Hover events are limited on Bedrock).
* **Chat Heads:** Fully compatible.

### Not Supported / Conflicts
* **Styled Chat:** Incompatible. If used together, Styled Chat overrides formatting.
    * *Workaround:* Remove all `stylingRules` in Embellish Chat to use only the Mention features.

---

## Links

* **Download:** [Modrinth](https://modrinth.com/mod/embellish-chat)
* **Source Code:** [GitHub](https://github.com/hanhy06/embellish-chat)
* **Issues:** [Bug Tracker](https://github.com/hanhy06/embellish-chat/issues)
