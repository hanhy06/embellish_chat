# Embellish Chat

Embellish Chat modernizes your Minecraft server’s chat experience with a fully server-side design. It introduces Markdown-style formatting, interactive message events, and a robust mention system—no client installation required. It also supports integration with LuckPerms, Styled Nickname, and various other mods.

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
  ],
  "prefixes": {
    " ... ": " ... "
  }
}
```
* **`colors`**: This is used in the color presets for styling.
* **`atlas`**: This is used in the atlas presets for styling.
* **`whitelist`**: This is used in the `URL` style type. If left empty, all URLs are allowed.
* **`prefixes`**: stylingRules, like mentionRules, uses permission nodes as its keys. The prefix supports placeholder tags and placeholder functionality.

---

## Compatibility

### Supported
* **Fabric Permissions API(Embedded):** Full integration for permission-based rules.
* **Text Placeholder API:** Supports placeholders in mention titles and presets.
* **Styled Nicknames:** Supports mentioning players by nickname.
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

- Embellish Chat: **3.4.0 (DEV)**
- Minecraft: **1.21.11**
- World: **Singleplayer, Superflat**
- CPU: **13th Gen Intel(R) Core(TM) i7-1360P**

- Max RAM: **4 GB**
- System: **Windows 11**
- Config: **Default**

### Test Scenario

For each test, the server was stressed for a total of 1,000 ticks, applying up to 500 chat messages per tick (approximately 10,000 messages per second).
The reported value represents the average tick processing time (MSPT) of the mod measured during that period.

Each message was between 70 and 100 characters in length, and realistic, naturally occurring chat messages were used.

The messages used in the tests are as follows:

| Type         | Length | Test String                                                                                      |
|--------------|--------|--------------------------------------------------------------------------------------------------|
| Plain Text   | 81     | `Hello everyone! What are you all doing on the server today? I am mining diamonds.`              |
| Styling Only | 94     | `**Trading now!** Check my [inv] and [end]. Selling the [Legendary Sword]<#00FFFF> cheap :fire:` |
| Mention Only | 75     | `@everyone Gather @here for the weekend boss raid! @team(red) get ready too.`                    |
| Mixed        | 93     | `@team(blue) charge the boss! ~~No retreat~~ My [inv] is full of potions, yell :heart: if low.`  |

### Results and Analysis

<canvas id="mstpChart"></canvas>

A single tick in Minecraft allows for a **50ms** processing window. As indicated in the data above, the most resource-intensive mode (**Mention Only (ON)**) consumes approximately **14.27ms** when processing **500 messages per tick**.

This results in a safety margin of **35ms** within the tick limit. Consequently, even under extreme load conditions equivalent to **10,000 messages per second**, the system is designed to minimize server lag (TPS drops) and maintain stability.

### Reason for Separating Mention Notification (ON/OFF)
The Mention feature test was divided into notification enabled (ON) and disabled (OFF) states to isolate the latency caused by Minecraft's native packet processing.  
When notifications are enabled, the server must send additional vanilla packets to the target players to play sounds and display screen titles, which adds a slight processing overhead.  
Therefore, we separated these states to more accurately measure the pure computational performance of the mod itself.

In real-world multiplayer environments, this packet-sending overhead can scale with the number of recipients (online players), so performance may degrade further depending on player count and mention usage patterns.  
If you want to disable sound/title notifications entirely, set `mentionBroadcast` to `false` in `embellish-chat/config.json`.

### Reason for Test Configuration Changes
The previous testing environment assumed extreme conditions and did not perfectly represent a realistic server environment. To derive more practical and meaningful performance metrics, the test conditions were updated as follows:

* **Realistic Message Length:** We used natural sentence structures reflecting actual player chat patterns and adjusted the character count to between 70 and 100 characters to match average chat lengths.
* **Simulating a Production Server Environment:** To create an environment similar to an actively running server setup, optimization mods such as Sodium and Lithium were applied together.
* **Isolated Performance Measurement:** Although the server environment itself was optimized, the millisecond per tick (MSPT) processing time measured in this test represents the isolated computational performance of the Embellish Chat mod's internal logic. The external optimization mods do not directly intervene in or affect this mod's chat processing results.

---

## Links

* **Download:** [Modrinth](https://modrinth.com/mod/embellish-chat)
* **Source Code:** [GitHub](https://github.com/hanhy06/embellish-chat)
* **Issues:** [Bug Tracker](https://github.com/hanhy06/embellish-chat/issues)
