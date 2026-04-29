# Mention Recipes

This page collects practical mention recipes. Use them as starting points, then adjust permission groups, patterns,
cooldowns, sounds, and styles for your server.

## Admin Ping

Goal: Let players call online administrators with `@admin`, send the same alert to Discord, and give admins a
click-to-teleport action.

```json
{
  "pattern": "@admin()",
  "title": "%player:displayname% mentioned you",
  "sound": {
    "id": "minecraft:entity.experience_orb.pickup",
    "category": "UI",
    "volume": 1.0,
    "pitch": 1.75
  },
  "cooldown": 30,
  "onlyTarget": false,
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
      "preset": "https://discord.com/api/webhooks/...;{\"embeds\":[{\"title\":\"%player:name_unformatted% mentioned admins\",\"color\":16753920,\"description\":\"TP command\\n```mcfunction\\nexecute in %world:id% run tp %player:pos_x% %player:pos_y% %player:pos_z%\\n```\",\"fields\":[{\"name\":\"Content\",\"value\":\"%embellish-chat:content%\",\"inline\":false},{\"name\":\"Player\",\"value\":\"`%player:name_unformatted%`\",\"inline\":true},{\"name\":\"Position\",\"value\":\"`%player:pos_x% %player:pos_y% %player:pos_z%`\",\"inline\":true},{\"name\":\"World\",\"value\":\"`%world:id%`\",\"inline\":true}]}]}"
    }
  ]
}
```

![Mention](../assets/images/Mention.gif)

This rule requires LuckPerms. The fixed mention preset always targets the `admin` group.

!!! tip "Add a cooldown"
    Admin pings are useful, but they can become noisy. A cooldown such as `30` seconds keeps accidental spam under control.

!!! warning "Use a fixed Discord preset"
    Put the webhook URL and JSON payload in `preset`. If `DISCORD_JSON` has an empty preset, it reads the value from
    matched chat text.

## Announcement Tag

Goal: Let selected players send a visible alert with `[notification]`.

```json
{
  "pattern": "\\[notification\\]()",
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
      "styleType": "COLOR_HEX",
      "preset": "#FFAAAA"
    }
  ]
}
```

Put this rule in a permission group that only moderators or server systems can use.

## Staff Channel

Goal: Turn `#staff` into a private staff-only chat prefix.

```json
{
  "pattern": "#staff()",
  "title": "",
  "sound": null,
  "cooldown": 0,
  "onlyTarget": true,
  "mentions": [
    {
      "mentionType": "LUCK_PERMS_GROUP",
      "preset": "staff"
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
    }
  ]
}
```

With `onlyTarget` enabled, the message is delivered only to players in the `staff` group. The empty overlay text and `null`
sound make it behave like a channel instead of an alert.

## Local Team Call

Goal: Notify nearby members of a team.

```json
{
  "pattern": "@red-here()",
  "title": "%player:displayname% called nearby red team members",
  "sound": {
    "id": "minecraft:block.note_block.pling",
    "category": "PLAYERS",
    "volume": 1.0,
    "pitch": 1.2
  },
  "cooldown": 10,
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
      "styleType": "COLOR_HEX",
      "preset": "#FF5555"
    }
  ]
}
```

This pattern is useful for team-based events, raids, or minigames where global `@everyone` alerts would be too loud.
