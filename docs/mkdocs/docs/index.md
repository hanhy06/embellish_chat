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

| Feature            | Syntax                  | Example                          |
|:-------------------|:------------------------|:---------------------------------|
| **Bold**           | `**Text**`              | `**Important**`                  |
| **Italic**         | `_Text_`                | `_Whisper_`                      |
| **Underline**      | `__Text__`              | `__Title__`                      |
| **Strikethrough**  | `~~Text~~`              | `~~Deleted~~`                    |
| **Obfuscated**     | `\|\|Text\|\|`          | `\|\|Secret\|\|`                 |
| **Color (Hex)**    | `[Text]<#Hex>`          | `[Sky]<#00AAFF>`                 |
| **Color (Preset)** | `[Text]<Preset>`        | `[Warning]<red>`                 |
| **Gradient**       | `[Text]<#Hex #Hex...>`  | `[Fire]<#ffff00 #ff0000>`        |
| **Rainbow**        | `[Text]<RAINBOW>`       | `[Magic]<RAINBOW>`               |
| **Link**           | `[Text](URL)`           | `[Click Me](https://google.com)` |
| **Font**           | `[Text]{Font ID}`       | `[Rune]{minecraft:alt}`          |
| **Item**           | `[i]`                   | `Look at my [i]`                 |
| **Inventory**      | `[inv]`                 | `Look at my [inv]`               |         
| **Mention**        | `@Target`               | `@everyone`, `@User`             |

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

Embellish Chat provides powerful functionality through the use of regular expressions.
Because regular expressions are inherently difficult, it is recommended to leverage various AI tools for rule creation and optimization.

The configuration file is located at `config/embellish-chat.json`.

```
{
  "version": "2.7.0",
  
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
* **Open Parties and Claims:** Required for `@party` mentions.
* **Geyser:** Basic support (Mentions work; Click/Hover events are limited on Bedrock).
* **Chat Heads:** Fully compatible.

### Not Supported / Conflicts
* **Styled Chat:** Incompatible. If used together, Styled Chat overrides formatting.
    * *Workaround:* Remove all `stylingRules` in Embellish Chat to use only the Mention features.

---

## Performance

### Test Setup

These tests were performed in a synthetic stress environment to measure **worst-case** performance.  
They do **not** represent normal server conditions.

- Embellish Chat: **2.6.1 (DEV)**
- Minecraft: **1.21.11**
- World: **Singleplayer, Superflat**
- CPU: **13th Gen Intel(R) Core(TM) i7-1360P**

- Max RAM for JVM: **2 GB**
- System: **Windows 11**
- Config: **Default**

### Test Scenario

For each test:

- The server was stressed with up to **500 chat messages per tick**  
  (≈ **10,000 messages per second**).
- Each message was about **50** or **200** characters long.
- The server was kept under continuous load while sending these messages every tick.Once the MSPT(Milliseconds Per Tick) value stabilized, the average was calculated over that steady-state period.
- The reported value is the **average MSPT** during that period.

The messages used in the tests are:

| Type         | Length | Test String                                                                                                                                                                                                                  |
|--------------|--------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Plain Text   | 50     | `This is a simple plain text message for latency test.`                                                                                                                                                                      |
| Plain Text   | 200    | `This is a standard long message designed to test the baseline performance of the chat system. It contains simple alphanumeric characters and basic punctuation only, without any markdown triggers.`                        |
| Styling Only | 50     | `**Bold** _Italic_ [Red]<red> [Blue]<blue> ~~Strike~~`                                                                                                                                                                       |
| Styling Only | 200    | `**Welcome** to the server! Please read the [rules]<#FF5555> at spawn. _Need help?_ Ask an admin! There is a **secret event** starting soon at the arena. Don't miss the [LEGENDARY PRIZES]<RAINBOW>!`                       |
| Mention Only | 50     | `Hello @everyone is anyone @here? calling @PlayerName`                                                                                                                                                                       |
| Mention Only | 200    | `Attention @everyone on the server. We are gathering @here now. If you are in @team(red) or @team(blue), please report to @PlayerOne. @group(admin) and @world(overworld) players should attend too.`                        |
| Mixed        | 50     | `**Hey** @everyone! Look at [this]<red> _cool_ @here.`                                                                                                                                                                       |
| Mixed        | 200    | `Attention @everyone! The **Boss Raid** is starting. @team(Red) please defend the [Core]<#FF0000>. @here gather at the gate! Watch out for the **hidden assassin**. The prize is [GOD SWORD]<RAINBOW>. Msg @Admin if stuck.` |

![MSPT-50](https://github.com/hanhy06/embellish-chat/blob/v2.7.0/%2B1.21.11/docs/images/MSPT2.6.1-50.png?raw=true)
![MSPT-200](https://github.com/hanhy06/embellish-chat/blob/v2.7.0/%2B1.21.11/docs/images/MSPT2.6.1-200.png?raw=true)

---

## Links

* **Download:** [Modrinth](https://modrinth.com/mod/embellish-chat)
* **Source Code:** [GitHub](https://github.com/hanhy06/embellish-chat)
* **Issues:** [Bug Tracker](https://github.com/hanhy06/embellish-chat/issues)
