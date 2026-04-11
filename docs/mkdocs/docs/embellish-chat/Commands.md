# Commands

Embellish Chat provides commands for both regular players and administrators.

All commands are available under `/embellish-chat`.
If `command_alias` is loaded with a non-blank value, the same command tree is also registered under that alias. The default alias is `ec`.

## User Commands

Available to all players.

* `/embellish-chat help style`: Shows the style rules available to you based on your permissions.
* `/embellish-chat help mention`: Shows the mention rules available to you based on your permissions.
* `/embellish-chat help icon`: Shows the available icon presets from `presets.json/icon` in `name - icon` format.
* `/embellish-chat notification`: Toggles personal mention notifications, if enabled in the config.
* `/embellish-chat open <player>`: Opens the last shared inventory, ender chest, or item for a player.

## Admin Commands

Requires OP Level 2 or appropriate permissions.

* `reload`: Reloads all files under `config/embellish-chat/`.
* `ban <player>`: Prevents a player from using mod features.
* `pardon <player>`: Restores access to the mod for a player.
* `stress_test <ticks> <count> <text>`: Repeatedly simulates `<count>` messages for `<ticks>` to stress-test the server's message-processing performance.
* `regex_test <regex> <text>`: Tests the provided `<regex>` against `<text>` and highlights capture groups in the match result.
