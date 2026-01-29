# Embellish Chat for Fabric

Embellish Chat modernizes your Minecraft server’s chat experience with a fully server-side design. It introduces Markdown-style formatting, interactive message events, and a robust mention system—no client installation required.

---

## ✨ Key Features

* **Rich Text Formatting**
    * Express yourself with **bold**, *italic*, __underline__, ~~strikethrough~~, ||obfuscation||, and custom fonts.
    * Supports advanced color options including Hex codes, Gradients, Presets, and Rainbow patterns.
    * Works consistently across public chat, private messages (DMs), and commands.
* **Advanced Mention System**
    * Ping specific players, teams, `@everyone`, or `@here` with visual and auditory notifications.
    * Mention text automatically adapts to the target's color (e.g., team color).
    * Supports detailed targeting like LuckPerms groups (`@group`) and specific worlds (`@world`).
* **Instantly show off your gear!**
    * Type `[i]` to display the item you’re holding, or `[inv]` to display your entire inventoryContext.
    * Other players can hover over the link to view detailed item tooltips.
* **Chat Utilities**
    * **Metadata:** Hover over any message to see the exact timestamp.
    * **Quick Copy:** Click on any message to instantly copy its content to your clipboard.

---

## 🛠️ Styling System

Use the following patterns directly in the chat window to apply styles:

| Feature          | Syntax                  | Example                                                       | Showcase                                                                                                            |
|:-----------------|:------------------------|:--------------------------------------------------------------|:--------------------------------------------------------------------------------------------------------------------|
| Bold             | `**Text**`              | `**You really need to read this!**`                           | ![Bold](https://github.com/hanhy06/embellish-chat/blob/v3.1.0/%2B1.21.11/docs/images/Bold.png?raw=true)             |
| Italic           | `_Text_`                | `_This is top secret..._`                                     | ![Italic](https://github.com/hanhy06/embellish-chat/blob/v3.1.0/%2B1.21.11/docs/images/Italic.png?raw=true)         |
| Underline        | `__Text__`              | `__Check this out__`                                          | ![Underline](https://github.com/hanhy06/embellish-chat/blob/v3.1.0/%2B1.21.11/docs/images/Underline.png?raw=true)   |
| Strikethrough    | `~~Text~~`              | `~~We don’t talk about this anymore~~`                        | ![Strike](https://github.com/hanhy06/embellish-chat/blob/v3.1.0/%2B1.21.11/docs/images/Strikethrough.png?raw=true)  |
| Obfuscated       | `\|\|Text\|\|`          | `\|\|Unreadable text\|\|`                                     | ![Obfuscated](https://github.com/hanhy06/embellish-chat/blob/v3.1.0/%2B1.21.11/docs/images/Obfuscated.gif?raw=true) |
| Color (Hex)      | `[Text]<#RRGGBB>`       | `[Blue]<#0000FF> like the deep ocean`                         | ![Color](https://github.com/hanhy06/embellish-chat/blob/v3.1.0/%2B1.21.11/docs/images/Color_Hex.png?raw=true)       |
| Color (Gradient) | `[Text]<#RRGGBB #R...>` | `[Grraaaaaaadieeeeent]<#ffaaaa #aaaaff #aaffaa>`              | ![Color](https://github.com/hanhy06/embellish-chat/blob/v3.1.0/%2B1.21.11/docs/images/Color_Gradient.png?raw=true)  |
| Color (Preset)   | `[Text]<preset>`        | `[pink]<pink> pig`                                            | ![Color](https://github.com/hanhy06/embellish-chat/blob/v3.1.0/%2B1.21.11/docs/images/Color_Preset.png?raw=true)    |
| Color (Rainbow)  | `[Text]<RAINBOW>`       | `look at this [rainbow]<RAINBOW>`                             | ![Color](https://github.com/hanhy06/embellish-chat/blob/v3.1.0/%2B1.21.11/docs/images/Color_Rainbow.png?raw=true)   |
| Link             | `[Text](URL)`           | `Download it [here](https://modrinth.com/mod/embellish-chat)` | ![Link](https://github.com/hanhy06/embellish-chat/blob/v3.1.0/%2B1.21.11/docs/images/Link.gif?raw=true)             |
| Font             | `[Text]{path}`          | `[Blorp Zorp]{minecraft:alt}`                                 | ![Font](https://github.com/hanhy06/embellish-chat/blob/v3.1.0/%2B1.21.11/docs/images/Font.png?raw=true)             |
| Item             | `[i]`                   | `Look at my [i]`                                              | ![Item](https://github.com/hanhy06/embellish-chat/blob/v3.1.0/%2B1.21.11/docs/images/Item.png?raw=true)             |
| Inventory        | `[inv]`                 | `Look at my [inv]`                                            | ![Inventory](https://github.com/hanhy06/embellish-chat/blob/v3.1.0/%2B1.21.11/docs/images/Inventory.png?raw=true)   |
| Ender Chest      | `[end]`                 | `Look at my [end]`                                            | ![Inventory](https://github.com/hanhy06/embellish-chat/blob/v3.1.0/%2B1.21.11/docs/images/EnderChest.png?raw=true)  |
| Mention          | `@PlayerName`           | `Hello, @User`                                                | ![Mention](https://github.com/hanhy06/embellish-chat/blob/v3.1.0/%2B1.21.11/docs/images/Mention.png?raw=true)       |

> **Notes**
>
> * **Links:** Only `https://` URLs are supported for security.
> * **Colors:** Presets (e.g., `pink`) are defined in the mod configuration.
> * **Fonts:** The `path` requires a namespaced ID (e.g., `minecraft:alt`).
> * **More Info:** For advanced syntax, style combinations, and detailed rules, refer to `/ec help style` or the [StyleWiki](https://hanhy06.github.io/embellish-chat/site/style/StyleSystem/).

---

## 🗣️ Mention System

| Target         | Behavior                                                                                  |
|:---------------|:------------------------------------------------------------------------------------------|
| `@PlayerName`  | Mentions a specific player.                                                               |
| `@team(name)`  | Mentions all players in the specified team.                                               |
| `@group(name)` | Mentions all players in the specified **LuckPerms** group.                                |
| `@world(name)` | Mentions all players in the specified world.                                              |
| `@everyone`    | Mentions every player on the server.                                                      |
| `@here`        | Mentions players within a configurable radius (default: **64 blocks**) in the same world. |

> **Notes**
>
> * **Behavior:** Successful mentions send a notification sound to the target and automatically tint the text (e.g., to the team color).
> * **Dependencies:** `@group` requires **LuckPerms**. Without it, the mention will be ignored.
> * **Colors:** `@team` and `@Player` use their respective team colors. If no team color is set, the `defaultTeamColor` from the config is used.
> * **Sound:** The notification sound uses the **UI** category (falls back to the **PLAYER** category on Minecraft 1.21.5 and earlier).
> * **More Info:** For a full list of mention types and advanced usage, refer to [MentionWiki](https://hanhy06.github.io/embellish-chat/site/mention/MentionSystem/).

---

## ⌨️ Commands

### Operator Commands
> Requires **OP Level 2** (or `GAMEMASTERS_CHECK` on 1.21.11+).

* **`/embellish-chat reload`** Reloads all configuration files under `/config/embellish-chat/` immediately.
* **`/embellish-chat ban/pardon <player>`** Blocks or restores a player's access to all mod features.
* **`/embellish-chat test regex <regex> <test>`** Compiles a regex pattern and tests it against a string for debugging purposes.
* **`/embellish-chat test stress <count> <test>`** Simulates `<count>` messages to stress-test the server's processing performance.

### User Commands
> Available to **all players** (no permission required).

* **`/ec open <player>`** Opens the last shared inventory of the specified player.
* **`/ec help mention`** Displays the list of available mention targets and usage guides.
* **`/ec help style`** Displays the list of available styles, presets, and syntax guides.
* **`/ec notification`** Toggles your personal mention notification preferences. *(This command is controlled by `notificationCommandEnable` in the configuration).*

---

## ⚙️ Configuration

Embellish Chat provides powerful functionality through the use of regular expressions.
Because regular expressions are inherently difficult, it is recommended to leverage various AI tools for rule creation and optimization.

### Config

The configuration file is located at `config/embellish-chat/config.json`.
```
{
  //version
  "version": "3.1.0",
  
  //preset
  "colorPreset": { ... },
  "atlasPreset": { ... },
  
  //setting
  "delimiter": ",",
  "timestamp": "yyyy-MM-dd HH:mm:ss",
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

* The `version` field must not be modified manually.
* The core configuration logic is defined in `stylingRules` and `mentionRules`.
* Rules are processed from top to bottom, so placing a catch-all rule earlier may override more specific rules defined below.
* The `delimiter` value is internally handled as a regular expression; special characters such as `|` must be properly escaped.
* If `defaultTeamColor` is missing or set to `null`, automatic coloring will not be applied.
* To avoid JSON syntax errors and ensure valid configurations, using the **[Web Config Generator](https://hanhy06.github.io/embellish-chat/site/config-generator.html)** is strongly recommended:

### Styling

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

* **`pattern`**: This is a regular expression for scanning text. It must have two capture groups.
    * `group 1`: This is text to be styled.
    * `group 2`: This is text passed as an option.
* **`styles`**: Defines the styles to be applied to captured group 1.
    * `styleType`: This is the style type. You can use all types listed in the [StyleWiki](https://hanhy06.github.io/embellish-chat/site/style/StyleType/).
    * `preset`: This is a preset value. If a value is provided, it is always used; if it is empty, the content of the user's captured group 2 is used instead.

### Mention

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

* **`pattern`**: This is a regular expression for scanning text. It must have one capture group.
    * This is the mention option (e.g. team name, LuckPerms group).
* **`title`**: This is the title shown on the mentioned player's screen.
    * `%player:displayname%` is the display name of the player who sent the mention.
* **`cooldown`**: This is the mention cooldown time in seconds.
    * Set to `0` to disable the cooldown.
* **`onlyTarget`**:
    * When set to true, prevents the message from being broadcast globally and sends it only to the target.
* **`sound`**: Defines the notification sound settings.
    * `id`: Sound identifier.
    * `category`: Sound category.
    * `volume`: Sound volume.
    * `pitch`: Sound pitch.
* **`mentions`**: Defines the mention actions to be executed.
    * `mentionType`: This is the mention type. You can use all types listed in the [MentionWiki](https://hanhy06.github.io/embellish-chat/site/mention/MentionType/).
    * `preset`: This is an optional preset value.
* **`styles`**: Defines the styles to be applied when the mention is triggered.
    * Works the same way as in the styling rules section.


---
## 📜 Compatibility

### ✅ Fully Supported

* **Fabric Permissions API (Embedded)**
    * The keys defined in `stylingRules` and `mentionRules` directly function as permission nodes.
    * Rules are evaluated from top to bottom based on the player's permissions.
* **Placeholder API**
    * Supports dynamic placeholders in mention titles and style presets.
    * **Exclusive:** Use `%embellish-chat:content%` to access the raw, unparsed chat message.
* **LuckPerms**
    * **Required** for the `@group` mention type. Without it, group mentions will be ignored.
* **Geyser (Bedrock Edition)**
    * Mentions between Java and Bedrock editions work seamlessly.
    * *Note:* Advanced styling (hover text, click events) may not fully render on Bedrock clients.
* **Chat Heads**
    * Fully compatible.

### ❗ Known Conflicts

* **Styled Chat**
    * **Styled Chat takes priority.** If installed, Embellish Chat's *styling* features will be overridden.
    * **Mentions still work:** The mention and notification system remains functional.
    * **Performance Tip:** If you must use both, remove all entries in Embellish Chat's `stylingRules` to prevent unnecessary background processing.
---
## 📊 Performance

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

![MSPT-50](https://github.com/hanhy06/embellish-chat/blob/v3.1.0/%2B1.21.11/docs/images/MSPT3.0.0-50.png?raw=true)
![MSPT-200](https://github.com/hanhy06/embellish-chat/blob/v3.1.0/%2B1.21.11/docs/images/MSPT3.0.0-200.png?raw=true)

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