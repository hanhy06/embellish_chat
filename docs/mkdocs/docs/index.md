# Embellish Chat

Embellish Chat modernizes your Minecraft server’s chat experience with a fully server-side design. It introduces Markdown-style formatting, interactive message events, and a robust mention system—no client installation required.

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
| **Item**           | `[i]`                  | `Look at my [i]`                 |
| **Inventory**      | `[inv]`                | `Look at my [inv]`               |   
| **Ender Chest**    | `[end]`                | `Look at my [end]`               |   
| **Mention**        | `@Target`              | `@everyone`, `@User`             |
| **Icon**           | `:Icon:`               | `:yes:`, `:fire:`                |

> **Note:** 
> 
> * For complex styling logic or custom rules, refer to the **[Style System](style/StyleSystem.md)**.
> * These icons are available by default. `fire, hunger, heart, yes, no, move`

### Mention Targets

| Target         | Description                                                     |
|:---------------|:----------------------------------------------------------------|
| `@Player`      | Mentions a specific player.                                     |
| `@everyone`    | Mentions **all players** on the server.                         |
| `@here`        | Mentions players within a specific radius (Default: 64 blocks). |
| `@team(name)`  | Mentions members of a specific scoreboard team.                 |
| `@group(name)` | Mentions members of a specific LuckPerms group.                 |
| `@world(name)` | Mentions all players in a specific world.                       |

> **Note:** For complex mention logic or custom rules, refer to the **[Mention System](mention/MentionSystem.md)**.

---

## Commands

### User Commands (`/embellish-chat`)
Available to all players.

| Command                          | Description                                                         |
|----------------------------------|---------------------------------------------------------------------|
| `/embellish-chat help style`     | Displays available styles and syntax usage.                         |
| `/embellish-chat help mention`   | Displays available mention types and targets.                       |
| `/embellish-chat notification`   | Toggles personal mention notifications (if enabled in config).      |
| `/embellish-chat open <player >` | Opens the last shared inventory/ender /item of the specified player |

### Admin Commands (`/embellish-chat`)
Requires OP Level 2 or appropriate permissions.

| Command       | Arguments                | Description                                                                                                       |
|---------------|--------------------------|-------------------------------------------------------------------------------------------------------------------|
| `reload`      | `None`                   | Reloads all files under `config/embellish-chat/`.                                                                 |
| `ban`         | `<player>`               | Blocks a player from using mod features.                                                                          |
| `pardon`      | `<player>`               | Restores mod access for a player.                                                                                 |
| `stress_test` | `<ticks> <count> <text>` | Repeatedly simulates `<count>` messages for `<ticks>` to stress-test the server's message-processing performance. |
| `regex_test ` | `<regex> <text>`         | Tests the provided `<regex>` against `<text>` and highlights capture groups to analyze the match result.          |                                                                                                                  |

---
## Configuration

Embellish Chat provides powerful functionality through the use of regular expressions.
Because regular expressions are inherently difficult, it is recommended to leverage various AI tools for rule creation and optimization.

The configuration file is located at `config/embellish-chat/config.json`.
```
{
  //version
  "version": "3.3.0",
  
  //setting
  "delimiter": ",",
  "timestamp": "yyyy-MM-dd HH:mm:ss",
  "commandAlias": "ec"
  "urlColor": "#0000EE",
  "defaultTeamColor": "#FF55FF",
  "notificationCommandEnable": true,
  "mentionBroadcast": true,
  "useClearFormat": false,
  
  //player list
  "bannedPlayerList": [],
  "notificationOffPlayerList": [],
  
  //webhook
  "webhook": ""
}
```

The configuration file is located at `config/embellish-chat/styles.json`.
```
{
  "stylingRules": {
    "embellish-chat.chat": [
      {
        "pattern": " ... ",
        "styles": [
          {
            "styleType": " ... ",
            "preset": " ... "
          }
        ]
      }
      ...
    ],
    "embellish-chat.command_argument": []
  }
}
```

The configuration file is located at `config/embellish-chat/mentions.json`.
```
{
  "mentionRules": {
    "embellish-chat.mention": [
      {
        "pattern": " ... ",
        "title": " ... ",
        "cooldown": 0,
        "onlyTarget": false,
        "sound": { ... },
        "mentions": [
          {
            "mentionType": " ... ",
            "preset": " ... "
          }
        ],
        "styles": [ ... ]
      }
      ...
    ]
  }
}
```

* The `version` field must not be modified manually.
* The core configuration logic is defined in `stylingRules` and `mentionRules`.
* Rules are processed from top to bottom, so placing a catch-all rule earlier may override more specific rules defined below.
* The `delimiter` value is internally handled as a regular expression; special characters such as `|` must be properly escaped.
* If `defaultTeamColor` is missing or set to `null`, automatic coloring will not be applied.
* To avoid JSON syntax errors and ensure valid configurations, using the **[Web Config Generator](config-generator.html)** is strongly recommended:

The configuration file is located at `config/embellish-chat/presets.json`.
```
{
  "colors": {
    " ... ": " ... ",
  },
  "atlas": {
    " ... ": {
      "atlas": " ... ",
      "sprite": " ... "
    }
  },
  "whitelist": [
    " ... "
  ]
}
```
* **`colors`**: This is used in the color presets for styling.
* **`atlas`**: This is used in the atlas presets for styling.
* **`whitelist`**: This is used in the `URL` style type. If left empty, all URLs are allowed.

---

## Compatibility

### Supported
* **Fabric Permissions API(Embedded):** Full integration for permission-based rules.
* **Text Placeholder API:** Supports placeholders in mention titles and presets.
* **LuckPerms:** Required for `@group` mentions.
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

- Embellish Chat: **3.0.0 (DEV)**
- Minecraft: **1.21.11**
- World: **Singleplayer, Superflat**
- CPU: **13th Gen Intel(R) Core(TM) i7-1360P**

- Max RAM: **4 GB**
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

### Results and Analysis

<div style="display: flex; gap: 10px; justify-content: center; margin-bottom: 15px;">
  <button onclick="updateMode('50')" style="padding: 8px 16px; cursor: pointer; border: 1px solid #ccc; border-radius: 4px;">50 Characters Mode</button>
  <button onclick="updateMode('200')" style="padding: 8px 16px; cursor: pointer; border: 1px solid #ccc; border-radius: 4px;">200 Characters Mode</button>
</div>

<canvas id="mstpChart"></canvas>

A single tick in Minecraft allows for a **50ms** processing window. As indicated in the data above, the most resource-intensive mode (**Mention Only** with notifications enabled) consumes approximately **34ms** when processing **500 messages per tick**.

This results in a safety margin of **16ms** within the tick limit. Consequently, even under extreme load conditions equivalent to **10,000 messages per second**, the system is designed to minimize server lag (TPS drops) and maintain stability.

---

## Links

* **Download:** [Modrinth](https://modrinth.com/mod/embellish-chat)
* **Source Code:** [GitHub](https://github.com/hanhy06/embellish-chat)
* **Issues:** [Bug Tracker](https://github.com/hanhy06/embellish-chat/issues)
