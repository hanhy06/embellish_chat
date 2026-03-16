# Commands

Embellish Chat provides commands for both regular players and administrators.

## User Commands

Available to all players.

| Command                          | Description                                                        |
|----------------------------------|--------------------------------------------------------------------|
| `/embellish-chat help style`     | Shows the available style syntax and examples.                     |
| `/embellish-chat help mention`   | Shows the available mention types and targets.                     |
| `/embellish-chat notification`   | Toggles personal mention notifications, if enabled in the config.  |
| `/embellish-chat open <player>`  | Opens the last shared inventory, ender chest, or item for a player. |

## Admin Commands

Requires OP Level 2 or appropriate permissions.

| Command       | Arguments                | Description                                                                                                       |
|---------------|--------------------------|-------------------------------------------------------------------------------------------------------------------|
| `reload`      | `None`                   | Reloads all files under `config/embellish-chat/`.                                                                 |
| `ban`         | `<player>`               | Prevents a player from using mod features.                                                                        |
| `pardon`      | `<player>`               | Restores access to the mod for a player.                                                                          |
| `stress_test` | `<ticks> <count> <text>` | Repeatedly simulates `<count>` messages for `<ticks>` to stress-test the server's message-processing performance. |
| `regex_test`  | `<regex> <text>`         | Tests the provided `<regex>` against `<text>` and highlights capture groups in the match result.                  |
