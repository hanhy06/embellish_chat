# Mention Configuration

Mention rules live in `config/embellish-chat/mentions.json` under `mention_rules`. Each top-level key is both a rule
group and a permission node.

## Rule Shape

```json
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

| Field        | Required | Description                                          |
|--------------|----------|------------------------------------------------------|
| `pattern`    | Yes      | Regular expression with one capture group.           |
| `comment`    | No       | Help text shown in `/embellish-chat help mention`.   |
| `title`      | Yes      | Overlay text. Empty string hides it.                 |
| `sound`      | No       | Sound settings, or `null` for no sound.              |
| `cooldown`   | Yes      | Cooldown in seconds. Use `0` to disable cooldown.    |
| `onlyTarget` | Yes      | Sends the message only to matched targets when true. |
| `mentions`   | Yes      | Ordered targeting actions.                           |
| `styles`     | Yes      | Style actions. Empty list disables styling.          |

## Pattern Option

Mention patterns need one capture group. A mention action uses its configured `preset` first. If `preset` is empty, the
capture group becomes the option.

```json
{
  "pattern": "@team\\(([^)]+)\\)",
  "mentions": [
    {
      "mentionType": "TEAM",
      "preset": ""
    }
  ]
}
```

When a player writes `@team(red)`, the captured option is `red`.

## Fixed Preset

```json
{
  "pattern": "@admin()",
  "mentions": [
    {
      "mentionType": "LUCK_PERMS_GROUP",
      "preset": "admin"
    }
  ]
}
```

This rule always targets the `admin` LuckPerms group. The capture group is empty because the target is fixed.

## Multiple Targeting Actions

```json
{
  "pattern": "@red-here()",
  "mentions": [
    {
      "mentionType": "INSIDE",
      "preset": "64"
    },
    {
      "mentionType": "TEAM",
      "preset": "red"
    }
  ]
}
```

This rule targets players who match all configured mention actions. Use combined actions for local team calls,
staff-only pings, or channel-limited alerts.

## Notification Sound

```json
{
  "sound": {
    "id": "minecraft:entity.experience_orb.pickup",
    "category": "UI",
    "volume": 1.0,
    "pitch": 1.75
  }
}
```

| Field      | Description               |
|------------|---------------------------|
| `id`       | Sound event ID.           |
| `category` | Minecraft sound category. |
| `volume`   | Playback volume.          |
| `pitch`    | Playback pitch.           |

Set `sound` to `null` for silent mention rules.

## onlyTarget

`onlyTarget` controls message delivery.

| Value   | Behavior                                                                                             |
|---------|------------------------------------------------------------------------------------------------------|
| `false` | Sends normally, with overlay or sound feedback.                                                      |
| `true`  | The message is delivered only to matched targets. This can be used as a lightweight private channel. |

!!! warning "Private channels depend on correct targeting"
    If `onlyTarget` is true and the target rule is too broad, the message may reach more players than intended. Test
    private-channel rules before using them on a live server.

## Styling the Mention Text

Mention rules can reuse the same style actions used by `styles.json`.

```json
{
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

This changes the visual appearance of the matched mention text in chat. It does not change who gets notified.
