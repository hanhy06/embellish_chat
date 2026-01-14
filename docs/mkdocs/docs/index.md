# Embellish Chat

**Embellish Chat** allows you to modernize your Minecraft server's chat experience. It introduces Markdown-style formatting, interactive message events, and a robust mention system, all powered by the Fabric API.

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

* **Version:** Do not modify the `version` field manually.
* **Rules:** The core logic lies in `stylingRules` and `mentionRules`.
* **Order Matters:** Rules are processed from **top to bottom**. Placing a catch-all rule at the top may override specific rules below it.

> **Notes**
> * The `delimiter` is internally processed using a regular expression. If you want to use a special character like `|` as a separator, please enter the escaped version of the delimiter.
> * If the `defaultTeamColor` value is missing or `null`, it will not be automatically colored.
> * We strongly recommend using the **[Web Config Generator](https://hanhy06.github.io/embellish-chat/docs/wiki/config-generator.html)** to generate valid JSON configurations without syntax errors.

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
