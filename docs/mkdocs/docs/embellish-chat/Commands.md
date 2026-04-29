# Commands

Embellish Chat registers its commands under `/embellish-chat`. If `command_alias` is set to a non-empty value in
`config.json`, the same command tree is also available under that alias. The default alias is `/ec`.

!!! note "Examples use the full command name"
    You can replace `/embellish-chat` with `/ec` when the default alias is enabled.

## Player Commands

| Command                         | Description                                                                                          |
|---------------------------------|------------------------------------------------------------------------------------------------------|
| `/embellish-chat help style`    | Shows the style rules available to the player based on permission nodes.                             |
| `/embellish-chat help mention`  | Shows the mention rules available to the player based on permission nodes.                           |
| `/embellish-chat help icon`     | Shows configured icon presets from `presets.json`.                                                   |
| `/embellish-chat notification`  | Toggles the player's personal mention notification setting when `notify_command_enabled` is enabled. |
| `/embellish-chat open <player>` | Opens the last shared item, inventory, or ender chest showcase from the selected player.             |

## Admin Commands

| Command                                              | Description                                                        |
|------------------------------------------------------|--------------------------------------------------------------------|
| `/embellish-chat reload`                             | Reloads config files and applies updated settings.                 |
| `/embellish-chat ban <target>`                       | Blocks selected players from using Embellish Chat features.        |
| `/embellish-chat pardon <target>`                    | Restores selected players after a mod feature ban.                 |
| `/embellish-chat regex_test <regex> <text>`          | Tests a style-rule regex with two capture groups.                  |

## Permissions

Command access is intentionally simple.

| Command Group   | Access                                                |
|-----------------|-------------------------------------------------------|
| Player commands | Available to every player.                            |
| Admin commands  | Requires the server's admin command permission level. |
| Style rules     | Controlled by the top-level keys in `style_rules`.    |
| Mention rules   | Controlled by the top-level keys in `mention_rules`.  |

!!! tip "Permission keys are rule groups"
    A rule group such as `embellish-chat.chat` is both a config key and a permission node. A player only receives rules from
    groups they are allowed to use.
