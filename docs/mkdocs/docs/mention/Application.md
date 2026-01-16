# Application

## Admin Mention

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
      "preset": "{\"embeds\":[{\"title\":\"%player:name_unformatted% mentioned admins\",\"color\":16753920,\"description\":\"TP command\\n```mcfunction\\nexecute in %world:id% run tp %player:pos_x% %player:pos_y% %player:pos_z%\\n```\",\"fields\":[{\"name\":\"Content\",\"value\":\"%embellish-chat:content%\",\"inline\":false},{\"name\":\"UUID\",\"value\":\"`%player:uuid%`\",\"inline\":false},{\"name\":\"Player\",\"value\":\"`%player:name_unformatted%`\",\"inline\":true},{\"name\":\"Ping\",\"value\":\"`%player:ping% ms`\",\"inline\":true},{\"name\":\"\\u200b\",\"value\":\"\\u200b\",\"inline\":true},{\"name\":\"Position\",\"value\":\"`%player:pos_x% %player:pos_y% %player:pos_z%`\",\"inline\":true},{\"name\":\"World\",\"value\":\"`%world:id%`\",\"inline\":true},{\"name\":\"\\u200b\",\"value\":\"\\u200b\",\"inline\":true},{\"name\":\"Server\",\"value\":\"`%server:name%`\",\"inline\":true},{\"name\":\"Time\",\"value\":\"`%server:time%`\",\"inline\":true},{\"name\":\"Status\",\"value\":\"`TPS:%server:tps%` `MSPT:%server:mspt%`\",\"inline\":true}]}]}"
    }
  ]
}

```

![Mention](https://github.com/hanhy06/embellish-chat/blob/v3.0.0/%2B1.21.11/docs/images/Mention.gif?raw=true)

You can put any value accepted as an option into the preset.
When used this way, you can always trigger an admin mention simply by using `@admin`.
Additionally, by using `CLICK_COMMAND_RUN` in the styles section, the mentioned administrator can easily teleport to the sender’s location.
If you also use `DISCORD_JSON`, the administrator will receive a notification in Discord when they are mentioned.

## Making an announcement

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