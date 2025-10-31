# Available Mention Types

| Type               | Description                                                                                                         | Option               |
| ------------------ |---------------------------------------------------------------------------------------------------------------------|----------------------|
| `EVERYONE`         | Mentions **all players** on the server.                                                                             | No options required  |
| `HERE`             | Mentions players **within a specific radius** around the sender (same world only). Radius is taken from the option. | Radius               |
| `TEAM`             | Mentions **all players on the sender’s team**.                                                                      | Team name            |
| `PLAYER`           | Mentions a **specific player** by name.                                                                             | Player name          |
| `LUCK_PERMS_GROUP` | Mentions **all players in a specific LuckPerms group**. Requires LuckPerms to be installed.                         | LuckPerms group name |

> **Notes**
> 
> * All mentions use the mentionColor defined in the configuration by default.
> * The TEAM and PLAYER types follow the style of the team they belong to.
> * You can modify the mention styles in the config.json file.