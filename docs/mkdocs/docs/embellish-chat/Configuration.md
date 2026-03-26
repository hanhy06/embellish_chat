# Configuration

Embellish Chat is powered by regular expressions, which makes it highly flexible but sometimes difficult to configure by hand.
If you are not comfortable writing regex, using an AI assistant or the web config generator can make rule creation much easier.

The mod configuration is split across four files:

* `config.json`: Core settings such as delimiters, timestamps, and notification toggles.
* `styles.json`: Style rules that control formatting and interactions.
* `mentions.json`: Mention rules that control targeting and notifications.
* `presets.json`: Shared preset data for colors, atlas entries, URL whitelists, and chat prefixes.

## config.json

The core settings file is located at `config/embellish-chat/config.json`.

```
{
  //version
  "version": "current mod version",
  
  //setting
  "delimiter": ",",
  "timestamp": "yyyy-MM-dd HH:mm:ss",
  "command_alias": "ec",
  "url_color": "#0000EE",
  "team_color": "#FF55FF",
  "notify_command_enabled": true,
  "notify_mention_enabled": true,
  "disable_vanilla_chat_format": false,
  
  //player list
  "banned_players": [],
  "notify_off_players": []
}
```

* **`version`**: Stores the current mod version. Do not edit this field manually.
* **`delimiter`**: Separator used when parsing certain options. This value is handled internally as a regular expression, so special characters such as `|` must be escaped.
* **`timestamp`**: Date and time format used by timestamp-related output.
* **`command_alias`**: Short alias for the main command.
* **`url_color`**: Default color used for URL-style output.
* **`team_color`**: Base color used when styling `@team` and `@Player` mentions. When the target has team or display styling, that styling is applied on top of this base. If it is missing or set to `null`, those mentions are left without an automatic color.
* **`notify_command_enabled`**: Enables the notification toggle command.
* **`notify_mention_enabled`**: Enables mention sound and title notifications.
* **`disable_vanilla_chat_format`**: Disables the vanilla chat format so Embellish Chat can fully control message formatting.
* **`banned_players`**: List of players blocked from using mod features.
* **`notify_off_players`**: List of players who have mention notifications disabled.

## styles.json

The style rules file is located at `config/embellish-chat/styles.json`.

```
{
  "style_rules": {
    "embellish-chat.chat": [
      {
        "pattern": " ... ",
        "comment": " ... ",
        "styles": [
          {
            "styleType": " ... ",
            "preset": " ... "
          }
        ]
      }
      ...
    ]
  }
}
```

* **`style_rules`**: Top-level container for all styling rules.
* **`embellish-chat.chat`**: Rules applied to normal chat messages.
* Each rule includes **`pattern`** for regex matching, **`comment`** for `/embellish-chat help style`, and **`styles`** for the actions to apply.
* Most custom chat formatting behavior is defined here.
* Rules are processed from top to bottom, so an early catch-all rule can override more specific rules below it.

For the full rule format and every available style type, see **[Style Configuration](../style/Configuration.md)** and **[Style Type](../style/StyleType.md)**.

## mentions.json

The mention rules file is located at `config/embellish-chat/mentions.json`.

```
{
  "mention_rules": {
    "embellish-chat.mention": [
      {
        "pattern": " ... ",
        "comment": " ... ",
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

* **`mention_rules`**: Top-level container for all mention rules.
* **`embellish-chat.mention`**: Rules applied to mention processing.
* Each rule includes **`pattern`** for matching, **`comment`** for `/embellish-chat help mention`, **`title`**, **`cooldown`**, **`onlyTarget`**, **`sound`**, **`mentions`**, and optional **`styles`**.
* Mention behavior and recipient targeting are defined here.
* Rules are processed from top to bottom, so more specific rules should usually be placed first.

For the full rule format and every available mention type, see **[Mention Configuration](../mention/Configuration.md)** and **[Mention Type](../mention/MentionType.md)**.

## presets.json

The shared preset file is located at `config/embellish-chat/presets.json`.

```
{
  "color": {
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
  "prefix": {
    " ... ": " ... "
  }
}
```

* **`color`**: Used for named color presets in style rules.
* **`atlas`**: Used for atlas presets in style rules.
* **`whitelist`**: Used by the `URL` style type. If it is empty, all URLs are allowed.
* **`prefix`**: Uses permission nodes as keys, and each value is parsed as a text component with placeholder tags.

## Notes

* `ConfigManager` automatically separates the configuration into `config.json`, `styles.json`, `mentions.json`, and `presets.json`.
* Missing sections are restored from the built-in defaults when the files are loaded.
* If the stored `version` does not match the running mod version, the mod keeps the current in-memory configuration and ignores the mismatched load.
* To avoid JSON syntax errors and to generate valid configs more easily, using the **[Web Config Generator](../config-generator/index.html)** is strongly recommended.
