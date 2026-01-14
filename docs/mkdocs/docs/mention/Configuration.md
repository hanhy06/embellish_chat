# Configuration

## Configuration Structure

```
{
  "pattern": "@everyone()",
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
      "styleType": "COLOR_PRESET",
      "preset": "light purple"
    }
  ]
}
```

* **`pattern`**: This is a regular expression for scanning text. It must have one capture group.
    * This is the mention option (e.g. team name, LuckPerms group).
* **`title`**: This is the title shown on the mentioned player's screen.
    * `%player:displayname%` is the display name of the player who sent the mention.
* **`cooldown`**: This is the mention cooldown time in seconds.
    * Set to `0` to disable the cooldown.
* **`sound`**: Defines the notification sound settings.
    * `id`: Sound identifier.
    * `category`: Sound category.
    * `volume`: Sound volume.
    * `pitch`: Sound pitch.
* **`mentions`**: Defines the mention actions to be executed.
    * `mentionType`: This is the type of mention action.
    * `preset`: This is an optional preset value.
* **`styles`**: Defines the styles to be applied when the mention is triggered.
    * Works the same way as in the styling rules section.

## Usage Patterns

### Single Mention

```
{
  "pattern": "@everyone()",
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
      "styleType": "COLOR_PRESET",
      "preset": "light purple"
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

### Preset

```
{
  "pattern": "@red()",
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

This way, only the red team will always be in the mansion