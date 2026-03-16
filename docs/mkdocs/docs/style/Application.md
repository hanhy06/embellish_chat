# Application

## Text Formatting & Text Placeholder API

<span style="color:red;">Important:</span> This setting must be modified in `config/embellish-chat/presets.json`, not in `config/embellish-chat/styles.json`.

```json
{
  "prefix": {
    "chat.prefix.default":"<b>[%player:displayname%]</b> "
  }
}
```

![Formatting](../assets/images/Formatting.png)

You can reformat the full chat message by matching the complete line and applying a `REPLACE` style.
When using custom chat formatting, enable `disable_vanilla_chat_format` in `config.json`.

## Bubble Chat

```
{
  "pattern": "(.+)()",
  "styles": [
    {
      "styleType": "BUBBLE",
      "preset": ""
    },
    {
      "styleType": "BLOCK",
      "preset": ""
    }
  ]
}
```

![BubbleChat](../assets/images/BubbleChat.gif)

This combination displays a speech bubble and blocks the normal chat message.
It is useful when you want nearby conversation to feel more natural.

## Using chat as a macro

```
[
  {
    "pattern": "(sad)()",
    "styles": [{ "styleType": "COMMAND_RUN", "preset": "/trigger ec.flag set 100" }]
  },
  {
    "pattern": "(like|love)()",
    "styles": [{ "styleType": "COMMAND_RUN", "preset": "/trigger ec.flag set 200" }]
  },
  {
    "pattern": "(hate)()",
    "styles": [{ "styleType": "COMMAND_RUN", "preset": "/trigger ec.flag set 300" }]
  },
  {
    "pattern": "(oh|ah)()",
    "styles": [{ "styleType": "COMMAND_RUN", "preset": "/trigger ec.flag set 400" }]
  }
]
```

![Command_Run](../assets/images/Command_Run.gif)

Using triggers with `COMMAND_RUN` lets chat act like a lightweight macro system.
The example above comes from a showcase datapack that maps words to different emotes or reactions.

## Global Style

```
{
  "pattern": "(.+)()",
  "styles": [
    {
      "styleType": "COLOR_RAINBOW",
      "preset": "0.3"
    }
  ]
}
```

In regular expressions, `.+` matches the full message.
That makes it easy to apply a subtle rainbow effect to every chat line.

## Using It Directly as an Option

```
{
  "pattern": "((red))",
  "styles": [
    {
      "styleType": "COLOR_PRESET",
      "preset": ""
    }
  ]
}
```

Writing a pattern like `((red))` makes capture groups 1 and 2 contain the same text.
For example, this lets `COLOR_PRESET` use `red` as both the matched text and the option value, so every `red` appears in red.
