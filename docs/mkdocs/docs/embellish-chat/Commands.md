# Commands

Embellish Chat provides commands for both regular players and administrators.

All commands are available under `/embellish-chat`.
If `command_alias` is loaded with a non-blank value, the same command tree is also registered under that alias. The default alias is `ec`.

## Admin Commands

> Requires **OP Level 2** or `GAMEMASTERS_CHECK` on Minecraft `1.21.11+`.

* **`/embellish-chat reload`** Reloads all configuration files under `config/embellish-chat/` immediately.
* **`/embellish-chat ban <player>`** Blocks a player's access to all mod features.
* **`/embellish-chat pardon <player>`** Restores a player's access to all mod features.
* **`/embellish-chat stress_test <ticks> <count> <text>`** Repeatedly simulates `<count>` messages for `<ticks>` to stress-test the server's message-processing performance.
* **`/embellish-chat regex_test <regex> <text>`** Tests the provided `<regex>` against `<text>` and highlights capture groups to analyze the match result.

## User Commands

> Available to **all players** with no permission requirement.

* **`/embellish-chat open <player>`** Opens the last shared inventory, ender chest, or item of the specified player.
* **`/embellish-chat help mention`** Displays the mention rules available to you based on your permissions.
* **`/embellish-chat help style`** Displays the styling rules available to you based on your permissions.
* **`/embellish-chat help icon`** Displays available icon presets from `presets.json/icon` in `name - icon` format.
* **`/embellish-chat notification`** Toggles your personal mention notification preferences when `notify_command_enabled` is enabled.
