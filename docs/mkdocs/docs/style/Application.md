# Application

## Inline Icons

```
{
  "pattern": "(:fire:)()",
  "styles": [
    {
      "styleType": "JSON",
      "preset": "{\"type\": \"object\", \"atlas\": \"minecraft:blocks\", \"sprite\": \"block/campfire_fire\"}"
    }
  ]
}

```

![Fire](https://github.com/hanhy06/embellish-chat/blob/v2.6.2/%2B1.21.11/docs/images/Fire.gif?raw=true)

If you use the `JSON` type, you can replace the matched string with JSON.
The example above shows how to use the `JSON` type to replace `:fire:` with a fire atlas.<br>
You can read about Minecraft’s atlas [Text component format wiki](https://minecraft.wiki/w/Text_component_format#Atlas_Object_Type) and [Atlas wiki](https://minecraft.wiki/w/Atlas).

## Showing off items

```
{
  "pattern": "(\\[i(.*?)\\])",
  "styles": [
    {
      "styleType": "SHOW_ITEM",
      "preset": ""
    }
  ]
}
```

![Show_Item](https://github.com/hanhy06/embellish-chat/blob/v2.6.2/%2B1.21.11/docs/images/Show_Item.gif?raw=true)

If you use the `SHOW_ITEM` type, you can show off not only the item’s description but also the image of the item you are holding.

To specify a custom texture path, use `[i minecraft:blocks;block/grass_block_side]`. Otherwise, simply using `[i]` will automatically infer the texture from the held item.

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

![Command_Run](https://github.com/hanhy06/embellish-chat/blob/v2.6.2/%2B1.21.11/docs/images/Command_Run.gif?raw=true)

Triggers and the COMMAND_RUN type allow chat to function as a macro.
The example above demonstrates a showcase datapack that enables expressions like crying or joy through chat.

If you want, you can download it [here](https://modrinth.com/datapack/embellish-chat-showcase).

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

## Text Placeholder API

```
{
  "pattern": "(\\[shop])()",
  "styles": [
    {
      "styleType": "REPLACE",
      "preset": "≫ This is %player:name%’s shop ≪"
    },
    {
      "styleType": "CLICK_COMMAND_RUN",
      "preset": "/shop open %player:name%"
    },
    {
      "styleType": "BOLD",
      "preset": ""
    }
  ]
}
```

This is how you can apply the Text Placeholder API.
You can check the default placeholders of the Placeholder API on the official website. [Here](https://placeholders.pb4.eu/user/default-placeholders/#list-of-placeholders)