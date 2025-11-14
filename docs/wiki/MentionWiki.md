# Available Mention Types

| Type               | Description                                                                                                         | Option               |
|--------------------|---------------------------------------------------------------------------------------------------------------------|----------------------|
| `EVERYONE`         | Mentions **all players** on the server.                                                                             | No options required  |
| `INSIDE`           | Mentions players **within a specific radius** around the sender (same world only). Radius is taken from the option. | Radius               |
| `TEAM`             | Mentions **all players on the sender’s team**.                                                                      | Team name            |
| `PLAYER`           | Mentions a **specific player** by name.                                                                             | Player name          |
| `LUCK_PERMS_GROUP` | Mentions **all players in a specific LuckPerms group**. Requires LuckPerms to be installed.                         | LuckPerms group name |

> **Notes**
> 
> * All mentionSegments use the mentionColor defined in the configuration by default.
> * The TEAM and PLAYER types follow the style of the team they belong to.
> * You can modify the mentionSegment styles in the config.json file.

---

# Usage

# Single Mention

```
{
  "pattern": "@([A-Za-z0-9_]{1,16})(?=\\b|\\s|$)",
  "mentionSegments": [
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
  "mentionSegments": [
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
  "pattern": "[notification]()",
  "mentionSegments": [
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
  "mentionSegments": [
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
    }
  ]
}
```

You can put any value accepted as an option into the preset. Used this way, you can always trigger the administrator simply by using `@admin`.