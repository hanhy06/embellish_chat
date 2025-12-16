# Available Mention Types

| Type               | Description                                                                                                        | Option                   |
|--------------------|--------------------------------------------------------------------------------------------------------------------|--------------------------|
| `EVERYONE`         | Mentions **all players** on the server.                                                                            | No options required      |
| `INSIDE`           | Mentions players **within a specific radius** around the sender (same world only). Radius is taken from the option. | Radius                   |
| `TEAM`             | Mentions **all players in the specified team**.                                                                      | Team name                |
| `PLAYER`           | Mentions a **specific player** by name.                                                                            | Player name              |
| `LUCK_PERMS_GROUP` | Mentions **all players in a specific LuckPerms group**. Requires LuckPerms to be installed.                        | LuckPerms group name     |
| `WORLD`            | Mentions all players in a **specific world**.                                                                      | World name               |
| `CUSTOM`           | Mentions all players in a **target selectors**.                                                                    | vanilla target selectors |
> **Notes**
>
> * `TEAM` and `PLAYER` types follow the color of their respective team. If the team has no color, the `defaultTeamColor` value is used.
> * You can modify the mention styles in the config.json file.
> * You can find more details about target selectors on the [Target selectors wiki](https://minecraft.wiki/w/Target_selectors)

---

# Usage

# Single Mention

```
{
  "pattern": "@([A-Za-z0-9_]{1,16})(?=\\b|\\s|$)",
  "title": "%player:displayname% mentioned you",
  "cooldown": 0,
  "sound": {
    "id": "minecraft:entity.experience_orb.pickup",
    "category": "UI",
    "volume": 1.0,
    "pitch": 1.75
  },
  "mentions": [
    {
      "mentionType": "PLAYER",
      "preset": ""
    }
  ],
  "styles": [
    {
      "styleType": "BOLD",
      "preset": ""
    }
  ]
}

```

This is the most basic way to use it.

## Multiple Mention

```
{
  "pattern": "@red-team-here()",
  "title": "%player:displayname% mentioned you",
  "cooldown": 0,
  "sound": {
    "id": "minecraft:entity.experience_orb.pickup",
    "category": "UI",
    "volume": 1.0,
    "pitch": 1.75
  },
  "mentions": [
    {
      "mentionType": "INSIDE",
      "preset": "64"
    },
    {
      "mentionType": "TEAM",
      "preset": "red"
    }
  ],
  "styles": [
    {
      "styleType": "BOLD",
      "preset": ""
    },
    {
      "styleType": "COLOR_HEX",
      "preset": "#FFAAAA"
    }
  ]
}
```

Mentions any red-team player within a 64-block radius.

## Application – Making an announcement

```
{
  "pattern": "\\[notification\\]()",
  "title": "%player:displayname% mentioned you",
  "cooldown": 0,
  "sound": {
    "id": "minecraft:entity.experience_orb.pickup",
    "category": "UI",
    "volume": 1.0,
    "pitch": 1.75
  },
  "mentions": [
    {
      "mentionType": "EVERYONE",
      "preset": ""
    }
  ],
  "styles": [
    {
      "styleType": "BOLD",
      "preset": ""
    },
    {
      "styleType": "COLOR_HEX",
      "preset": "#FFAAAA"
    }
  ]
}
```

Typing `[notification]` will send an alert to everyone.

## Application - Admin Mention

```
{
  "pattern": "@admin()",
  "title": "%player:displayname% mentioned you",
  "cooldown": 0,
  "sound": {
    "id": "minecraft:entity.experience_orb.pickup",
    "category": "UI",
    "volume": 1.0,
    "pitch": 1.75
  },
  "mentions": [
    {
      "mentionType": "LUCK_PERMS_GROUP",
      "preset": "admin"
    }
  ],
  "styles": [
    {
      "styleType": "BOLD",
      "preset": ""
    },
    {
      "styleType": "COLOR_GRADIENT",
      "preset": "#FF5555#C77DFF"
    },
    {
      "styleType": "CLICK_COMMAND_RUN",
      "preset": "execute in %world:id% run tp %player:pos_x% %player:pos_y% %player:pos_z%"
    },
    {
      "styleType": "DISCORD_JSON",
      "preset": "{\"embeds\":[{\"title\":\"%player:name_unformatted% mentioned admins\",\"color\":16753920,\"description\":\"TP command\\n```mcfunction\\nexecute in %world:id% run tp %player:pos_x% %player:pos_y% %player:pos_z%\\n```\",\"fields\":[{\"name\":\"Player\",\"value\":\"`%player:name_unformatted%`\",\"inline\":true},{\"name\":\"UUID\",\"value\":\"`%player:uuid%`\",\"inline\":true},{\"name\":\"World\",\"value\":\"`%world:id%` (`%world:name%`)\",\"inline\":true},{\"name\":\"Position\",\"value\":\"`%player:pos_x% %player:pos_y% %player:pos_z%`\",\"inline\":true},{\"name\":\"Ping\",\"value\":\"`%player:ping% ms`\",\"inline\":true},{\"name\":\"Server\",\"value\":\"`%server:name%`  `TPS:%server:tps%`  `MSPT:%server:mspt%`\",\"inline\":false},{\"name\":\"Time\",\"value\":\"`%server:time%`\",\"inline\":false}]}]}"
    }
  ]
}

```

![Mention](https://github.com/hanhy06/embellish-chat/blob/v2.6.0/%2B1.21.11/docs/images/Mention.gif?raw=true)

You can put any value accepted as an option into the preset. Used this way, you can always trigger the administrator simply by using @admin. Additionally, by using CLICK_COMMAND_RUN in the styles section, the summoned administrator can easily teleport to the location.
If you also use DISCORD_JSON, the administrator will receive a notification on Discord when they are mentioned.