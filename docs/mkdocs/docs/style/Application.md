# Application

## Text Formatting & Text Placeholder API

<span style="color:red;">Important:</span>This setting must be modified in `embellish-chat/presets.json`, not in `embellish-chat/styles.json`.

```
{
  "prefixes": {
    "chat.prefix.default":"<b>[%player:displayname%]</b> "
  }
}
```

![Formatting](https://github.com/hanhy06/embellish-chat/blob/v3.3.0/%2B1.21.11/docs/images/Formatting.png?raw=true)

You can format the chat by matching all rules from the first English styling set and applying them using the `REPLACE` type.
When using custom formatting, ensure that `useClearFormat` is enabled.


### Bubble Chat

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

![BubbleChat](https://github.com/hanhy06/embellish-chat/blob/v3.3.0/%2B1.21.11/docs/images/BubbleChat.gif?raw=true)

By displaying a speech bubble and preventing the chat message from being sent, 
you can create a more natural conversational experience.

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

![Command_Run](https://github.com/hanhy06/embellish-chat/blob/v3.3.0/%2B1.21.11/docs/images/Command_Run.gif?raw=true)

Triggers and the COMMAND_RUN type allow chat to function as a macro.
The example above demonstrates a showcase datapack that enables expressions like crying or joy through chat.

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

In regular expressions, .+ means all characters. Using this method, you can apply a subtle rainbow effect to all text.

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

If you write it like ((text)), you can make capture group 1 and 2 have the same content in the regular expression.
For example, if you put ((red)) in the preset, every red will be displayed in red.
