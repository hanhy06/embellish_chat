# Available Mention Types

| Type               | Description                                                                                                    | Option                |
| ------------------ | -------------------------------------------------------------------------------------------------------------- |-----------------------|
| `EVERYONE`         | Mentions **all players** on the server. Uses the group-mention styling and notifies every online player.       | No options required   |
| `HERE`             | Mentions players **within a certain radius** around the sender (same world). Radius is taken from the pattern. | Radius (numeric)      |
| `TEAM`             | Mentions **all players on the sender’s team**. Team color is applied automatically.                            | Team name             |
| `PLAYER`           | Mentions a **specific player** by name. Applies the player’s team color if available.                          | Player name           |
| `LUCK_PERMS_GROUP` | Mentions **all players in a specific LuckPerms group**. Requires LuckPerms to be installed.                    | LuckPerms group name  |
