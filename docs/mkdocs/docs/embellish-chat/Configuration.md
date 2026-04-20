# Configuration

Embellish Chat provides powerful functionality through regular expressions.
Because regular expressions are inherently difficult, it is recommended to use the web config generator or an AI assistant when creating and refining rules.

The mod configuration is split across four files:

* `config.json`: Core settings such as delimiters, timestamps, and notification toggles.
* `styles.json`: Style rules that control formatting and interactions.
* `mentions.json`: Mention rules that control targeting and notifications.
* `presets.json`: Shared preset data for colors, icon presets, item sprite overrides, URL whitelists, and chat prefixes.

## Config

The configuration file is located at `config/embellish-chat/config.json`.

```json
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
  "require_same_channel": true,
  "disable_vanilla_chat_format": false,

  //player list
  "banned_players": [],
  "notify_off_players": []
}
```

* The `version` field must not be modified manually.
* `ConfigManager` writes `config.json`, `styles.json`, `mentions.json`, and `presets.json` separately, then merges them into one runtime `Config` when loading.
* Missing sections are restored from the built-in defaults during that merge step.
* If the stored `version` does not match the running mod version, the mod keeps the current in-memory configuration and ignores the mismatched load.
* The core configuration logic is defined in `style_rules` and `mention_rules`.
* Rules are processed from top to bottom, so placing a catch-all rule earlier may override more specific rules defined below.
* The `delimiter` value is internally handled as a regular expression, so special characters such as `|` must be escaped properly.
* `command_alias` registers an additional root command that redirects to `/embellish-chat` when the config is loaded with a non-blank value.
* `team_color` is the base color used when styling `@team` and `@Player` mentions.
* If `team_color` is missing or set to `null`, those mentions are left without an automatic color.
* `require_same_channel` limits `ADVANCED_CHAT_CHANNEL` mentions to the sender's current Advanced Chat channel when enabled.
* Reloading the config refreshes the runtime style and mention processors, so updated `style_rules`, `mention_rules`, `timestamp`, `url_color`, `whitelist`, `color`, `icon`, and `item` values take effect immediately.
* To avoid JSON syntax errors and ensure valid configurations, using the **[Web Config Generator](../config-generator/index.html)** is strongly recommended.

## Styling

The configuration file is located at `config/embellish-chat/styles.json`.

```json
{
  "style_rules": {
    "embellish-chat.chat": [
      {
        "pattern": " ... ",
        "comment": "...",
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

* **`pattern`**: A regular expression used to scan chat text. It must contain two capture groups.
  * `group 1`: Text to be styled.
  * `group 2`: Text passed as an option.
* **`comment`**: Help text shown in `/embellish-chat help style`.
* **`styles`**: Defines the styles applied to capture group 1.
  * `styleType`: The style type. See **[Style Type](../style/StyleType.md)** for the full list.
  * `preset`: A fixed option value. If it is empty, the captured content from group 2 is used instead.
* Each top-level key such as `embellish-chat.chat` is also treated as a permission node.
* Each `styleType` name must match a handler registered in the runtime `StyleRegistry`.

For examples and advanced usage, see **[Style System](../style/StyleSystem.md)** and **[Style Configuration](../style/Configuration.md)**.

## Mention

The configuration file is located at `config/embellish-chat/mentions.json`.

```json
{
  "mention_rules": {
    "embellish-chat.mention": [
      {
        "pattern": " ... ",
        "comment": "...",

        "title": " ... ",
        "sound": { ... },

        "cooldown": 0,
        "onlyTarget": false,

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

* **`pattern`**: A regular expression used to scan chat text. It must contain one capture group.
  * The capture group becomes the mention option, such as a team name or LuckPerms group.
* **`comment`**: Help text shown in `/embellish-chat help mention`.
* **`title`**: The title shown on the mentioned player's screen.
  * `%player:displayname%` resolves to the display name of the player who sent the mention.
* **`sound`**: Defines the notification sound settings.
  * `id`: Sound identifier.
  * `category`: Sound category.
  * `volume`: Sound volume.
  * `pitch`: Sound pitch.
* **`cooldown`**: Mention cooldown time in seconds.
  * Set it to `0` to disable the cooldown.
* **`onlyTarget`**:
  * When set to `true`, the message is not broadcast globally and is sent only to the matched targets.
* **`mentions`**: Defines the mention actions to run.
  * `mentionType`: The mention type. See **[Mention Type](../mention/MentionType.md)** for the full list.
  * `preset`: An optional preset value.
* **`styles`**: Defines the styles applied when the mention is triggered.
  * This works the same way as the styling rules section.

For examples and advanced usage, see **[Mention System](../mention/MentionSystem.md)** and **[Mention Configuration](../mention/Configuration.md)**.

## Presets

The configuration file is located at `config/embellish-chat/presets.json`.

```json
{
  "prefix": {
    " ... ": " ... "
  },
  "whitelist": [
    " ... "
  ],
  "icon": {
    " ... ": {
      "atlas": " ... ",
      "sprite": " ... "
    }
  },
  "item": {
    " ... ": {
      "atlas": " ... ",
      "sprite": " ... "
    }
  },
  "color": {
    " ... ": " ... "
  }
}
```

* **`prefix`**: Uses permission nodes as keys, and each value is a string parsed as a text component with placeholder tags.
* **`whitelist`**: Used by the `URL` style type. If it is empty, all URLs are allowed.
* **`icon`**: Used by `ICON_PRESET` and `/embellish-chat help icon`.
* **`item`**: Overrides atlas sprites used by `SHOW_ITEM` for specific item IDs.
* **`color`**: Used in color presets for styling.
