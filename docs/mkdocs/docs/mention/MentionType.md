# Mention Types

`mentionType` selects how a mention action resolves target players.

| Type                    | Option               | Target                                                                            |
|-------------------------|----------------------|-----------------------------------------------------------------------------------|
| `PLAYER`                | Player name          | One online player. Nickname support is available when the integration is present. |
| `TEAM`                  | Scoreboard team name | Players in a specific scoreboard team.                                            |
| `INSIDE`                | Radius               | Players within the radius of the sender in the same world.                        |
| `EVERYONE`              | None                 | All online players.                                                               |
| `WORLD`                 | World ID             | Players in a specific world.                                                      |
| `LUCK_PERMS_GROUP`      | Group name           | Players in a LuckPerms group. Requires LuckPerms.                                 |
| `ADVANCED_CHAT_CHANNEL` | Channel name         | Players in an Advanced Chat channel. Requires Advanced Chat.                      |
| `PERMISSION`            | Permission node      | Players with a specific Fabric Permissions API permission.                        |
| `CUSTOM`                | Target selector      | Players matched by a vanilla target selector.                                     |
