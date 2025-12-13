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
  "sound": "entity.experience_orb.pickup",
  "pitch": 1.75,
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
  "sound": "entity.experience_orb.pickup",
  "pitch": 1.75,
  "title": "%player:displayname% mentioned you",
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
  "sound": "entity.experience_orb.pickup",
  "pitch": 1.75,
  "title": "%player:displayname% mentioned you",
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
        "sound": "entity.experience_orb.pickup",
        "pitch": 1.75,
        "title": "%player:displayname% mentioned you",
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
            "styleType": "COLOR_HEX",
            "preset": "#FFAAAA"
          },
          {
            "styleType": "CLICK_COMMAND_RUN",
            "preset": "execute in %world:id% run tp %player:pos_x% %player:pos_y% %player:pos_z%"
          }
        ]
      }
```

You can put any value accepted as an option into the preset. Used this way, you can always trigger the administrator simply by using @admin.
Additionally, by using CLICK_COMMAND_RUN in the styles section, the summoned administrator can easily teleport to the location.