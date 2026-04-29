# Configuration

Embellish Chat stores its configuration in `config/embellish-chat/`. The files are split by responsibility so server
owners can edit general settings, style rules, mention rules, and shared presets independently.

!!! tip "Use the config generator for complex rules"
    Regular expressions are powerful, but they are easy to break. For most servers,
    the [Web Config Generator](../config-generator/index.html) is the safest way to build and edit rules.

## File Layout

| File            | Purpose                                                                        |
|-----------------|--------------------------------------------------------------------------------|
| `config.json`   | General settings, command alias, notification toggles, and saved player lists. |
| `styles.json`   | Style rules that transform chat text.                                          |
| `mentions.json` | Mention rules that resolve targets and notify players.                         |
| `presets.json`  | Shared prefixes, URL whitelist, icons, item sprites, and named colors.         |

When the server loads, Embellish Chat reads these files together. Missing sections are restored from built-in defaults.

## config.json

```json
{
  "version": "current mod version",
  "delimiter": ",",
  "timestamp": "yyyy-MM-dd HH:mm:ss",
  "command_alias": "ec",
  "url_color": "#0000EE",
  "team_color": "#FF55FF",
  "notify_command_enabled": true,
  "notify_mention_enabled": true,
  "require_same_channel": true,
  "disable_vanilla_chat_format": false,
  "banned_players": [],
  "notify_off_players": []
}
```

| Key                           | Description                                                                                                     |
|-------------------------------|-----------------------------------------------------------------------------------------------------------------|
| `version`                     | This is the current version of the mod. Please do not modify it arbitrarily, as the settings may fail to load.  |
| `delimiter`                   | Separates multiple options inside some configured patterns. It is handled as a regular expression.              |
| `timestamp`                   | Format used by metadata hover and copy behavior.                                                                |
| `command_alias`               | Registers an extra root command that redirects to `/embellish-chat`. The default is `/ec`.                      |
| `url_color`                   | Default color used by URL style output.                                                                         |
| `team_color`                  | Base color used for team and player mentions when no stronger style overrides it.                               |
| `notify_command_enabled`      | Allows players to toggle their own mention notifications with `/embellish-chat notification`.                   |
| `notify_mention_enabled`      | Enables overlay text and sound feedback when a mention targets a player.                                        |
| `require_same_channel`        | Limits Advanced Chat channel mentions to the sender's current channel.                                          |
| `disable_vanilla_chat_format` | Disables vanilla chat formatting so custom full-message formatting can take over.                               |
| `banned_players`              | Stores players blocked from using Embellish Chat features.                                                      |
| `notify_off_players`          | Stores players who turned mention notifications off.                                                            |

!!! warning "Escape regex delimiters"
    `delimiter` is compiled as a regular expression. Special characters such as `|`, `.`, `?`, and `\` must be escaped
    correctly.

## Rule Files

`styles.json` and `mentions.json` are the main rule files. Their top-level keys are permission groups, and each group
contains the rules available to players with that permission.

| File            | Detailed reference                                      |
|-----------------|---------------------------------------------------------|
| `styles.json`   | [Style Configuration](../style/Configuration.md)        |
| `mentions.json` | [Mention Configuration](../mention/Configuration.md)    |

Use the reference pages for exact pattern shape, action order, and rule examples.

## presets.json

```json
{
  "prefix": {
    "chat.prefix.default": "<b>[%player:displayname%]</b> "
  },
  "whitelist": [],
  "icon": {},
  "item": {},
  "color": {
    "warning": "#FFAA00"
  }
}
```

| Section     | Used by                                                     |
|-------------|-------------------------------------------------------------|
| `prefix`    | Permission-based chat prefixes and full-message formatting. |
| `whitelist` | URL validation. Empty means every URL is allowed.           |
| `icon`      | `ICON_PRESET` and `/embellish-chat help icon`.              |
| `item`      | Sprite overrides used by `SHOW_ITEM`.                       |
| `color`     | Named colors used by `COLOR_PRESET`.                        |

## Reload Behavior

`/embellish-chat reload` reloads config files and applies updated rules, timestamps, colors, icons, item sprites, URL
whitelist entries, and notification settings without restarting the server.

!!! note "Version mismatch behavior"
    If the stored config version does not match the running mod version, the mod keeps the current in-memory configuration
    and ignores the mismatched load.
