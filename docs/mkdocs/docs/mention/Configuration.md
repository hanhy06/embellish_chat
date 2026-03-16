# Configuration

Mention rules are stored in `config/embellish-chat/mentions.json` under the top-level `mention_rules` object.

## Configuration Structure

```
{
  "pattern": "@everyone()",
  "comment": "<blue><b>Pattern</b></blue>: @everyone\n<dark_aqua><b>Comment</b></dark_aqua>: mention all online players\n",

  "title": "%player:displayname% mentioned you",
  "sound": {
    "id": "minecraft:entity.experience_orb.pickup",
    "category": "UI",
    "volume": 1.0,
    "pitch": 1.75
  },

  "cooldown": 0,
  "onlyTarget": false,

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
      "styleType": "COLOR_PRESET",
      "preset": "light purple"
    }
  ]
}
```

* **`pattern`**: A regular expression used to scan chat text. It must contain one capture group, and that capture group becomes the mention option such as a team name or LuckPerms group.
* **`comment`**: Help text shown in `/embellish-chat help mention`.
* **`title`**: The title shown to players who are mentioned. `%player:displayname%` resolves to the display name of the player who sent the message.
* **`sound`**: Notification sound settings including `id`, `category`, `volume`, and `pitch`.
* **`cooldown`**: Mention cooldown in seconds. Set it to `0` to disable the cooldown.
* **`onlyTarget`**: When `true`, the message is not broadcast globally and is delivered only to the matched targets.
* **`mentions`**: The mention actions that run when the rule matches. `mentionType` selects the target rule, and `preset` provides an optional preset value. See [Mention Type](MentionType.md) for the full list.
* **`styles`**: Styles applied when the mention is triggered. This works the same way as the styling rules in `styles.json`.

## File Layout

```json
{
  "mention_rules": {
    "embellish-chat.mention": [
      {
        "pattern": "@everyone()",
        "comment": "<blue><b>Pattern</b></blue>: @everyone\n<dark_aqua><b>Comment</b></dark_aqua>: mention all online players\n",
        "title": "%player:displayname% mentioned you",
        "sound": {
          "id": "minecraft:entity.experience_orb.pickup",
          "category": "UI",
          "volume": 1.0,
          "pitch": 1.75
        },
        "cooldown": 0,
        "onlyTarget": false,
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
            "styleType": "COLOR_PRESET",
            "preset": "light purple"
          }
        ]
      }
    ]
  }
}
```

* **`mention_rules`**: Top-level container for all mention rules.
* **Permission node keys**: Each key such as `embellish-chat.mention` is treated as a permission node.
* **Rule order matters**: Rules are processed from top to bottom, so broad rules should be placed below more specific ones.

## Usage Patterns

### Single Mention

```
{
  "pattern": "@everyone()",

  "title": "%player:displayname% mentioned you",
  "sound": {
    "id": "minecraft:entity.experience_orb.pickup",
    "category": "UI",
    "volume": 1.0,
    "pitch": 1.75
  },

  "cooldown": 0,
  "onlyTarget": false,

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
      "styleType": "COLOR_PRESET",
      "preset": "light purple"
    }
  ]
}

```

This is the simplest possible mention rule.

### Multiple Mention

```
{
  "pattern": "@red-team-here()",

  "title": "%player:displayname% mentioned you",
  "sound": {
    "id": "minecraft:entity.experience_orb.pickup",
    "category": "UI",
    "volume": 1.0,
    "pitch": 1.75
  },

  "cooldown": 0,
  "onlyTarget": false,

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

This mentions red-team players within a 64-block radius.

### Preset

```
{
  "pattern": "@red()",

  "title": "%player:displayname% mentioned you",
  "sound": {
    "id": "minecraft:entity.experience_orb.pickup",
    "category": "UI",
    "volume": 1.0,
    "pitch": 1.75
  },

  "cooldown": 0,
  "onlyTarget": false,

  "mentions": [
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

This version always targets the red team.
