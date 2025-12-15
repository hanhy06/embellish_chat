# Available Style Types

| Type                    | Description                                                                                                                                                      | Option                  |
|-------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------|-------------------------|
| `METADATA`              | When the mouse hovers over the text, display the time the server received it, and when clicked, copy it to the clipboard.                                        | No options are required |
| `LOG`                   | Logs the text and sender information to the server console when the message is processed.                                                                        | No options are required |
| `COLOR_HEX`             | Applies the color specified by the HEX code provided as an option.                                                                                               | HEX code                |
| `COLOR_RAINBOW`         | Cycles through rainbow colors.                                                                                                                                   | saturation              |
| `COLOR_GRADIENT`        | Applies a color gradient using the provided HEX codes.                                                                                                           | HEX codes               |
| `COLOR_PRESET`          | Uses a predefined color name from the `colorPreset` section.                                                                                                     | color preset            |
| `COLOR_SHADOW`          | Applies the HEX code color provided as an option to the shadow.                                                                                                  | HEX code                |
| `COLOR_TEAM`            | Colors the text using the player’s team color.                                                                                                                   | No options are required |
| `COMMAND_RUN`           | Immediately executes the command supplied via the option when the text is processed.                                                                             | command                 |
| `CLICK_COMMAND_RUN`     | Runs the command supplied via the option when the text is clicked.                                                                                               | command                 |
| `CLICK_COMMAND_SUGGEST` | Suggests the command supplied via the option when the text is clicked. The suggested command is placed into the chat input field but not executed automatically. | command                 |
| `CLICK_COPY`            | Copies the text supplied via the option to the clipboard when the text is clicked.                                                                               | text to copy            |
| `HOVER_TEXT`            | Displays the text supplied via the option when the mouse hovers over it.                                                                                         | text                    |
| `HOVER_ITEM`            | Displays the item in the player's inventory slot when the mouse hovers over it.                                                                                  | item slot               |
| `FONT`                  | Changes the font style.                                                                                                                                          | font id                 |
| `URL`                   | Allows opening the URL provided as an option when clicked.                                                                                                       | url                     |
| `BOLD`                  | Bold text.                                                                                                                                                       | No options are required |
| `ITALIC`                | Italic text.                                                                                                                                                     | No options are required |
| `UNDERLINE`             | Underlined text.                                                                                                                                                 | No options are required |
| `STRIKETHROUGH`         | Strikethrough text.                                                                                                                                              | No options are required |
| `OBFUSCATED`            | Applies Minecraft style obfuscation to make the text unreadable.                                                                                                 | No options are required |
| `REPLACE`               | Replaces the matched text using the provided option string while preserving the original style.                                                                  | Replacement string      |
| `MASK`                  | Replaces the original string with the characters supplied via options, adjusting to match the original length.                                                   | Replacement character   |
| `UPPER`                 | Transforms every matched substring into uppercase characters.                                                                                                    | No options are required |
| `LOWER`                 | Transforms every matched substring into lowercase characters.                                                                                                    | No options are required |
| `CLEAR`                 | Removes all styles.                                                                                                                                              | No options are required |
| `JSON`                  | Parses the JSON string supplied via the option and displays it as a text component.                                                                              | JSON string             |

> **Notes**
>
> * `COLOR_SHADOW` is not available in versions earlier than 1.21.2.
> * The COLOR_GRADIENT type also supports three or more colors.
>
> | **HOVER_ITEM Value**  | **Description**              |
> |-----------------------|------------------------------|
> | -1                    | Uses the main-hand item      |
> | 0 ~ 8                 | Uses the hotbar slot         |
> | 9 ~ 35                | Uses the inventory slot      |
> | 36 ~ 39               | Uses the armor slot          |
> | 40                    | Uses the off-hand slot       |
> | 41                    | Uses the horse armor slot    |
> | 42                    | Uses the saddle slot         |

---

# Usage

## Single Style

```
{
  "pattern": "__(.+?)__()",
  "styles": [
    {
      "styleType": "UNDERLINE",
      "preset": ""
    }
  ]
}
```
This is the most basic way to use it.

## Multiple Style

```
{
  "pattern": "_\\*(.+?)\\*_()",
  "styles": [
    {
      "styleType": "UNDERLINE",
      "preset": ""
    },
    {
      "styleType": "BOLD",
      "preset": ""
    }
  ]
}
```
You can use multiple types in a single rule.

> **Notes**
> 
> * For multi-style rules, options are separated using the configured delimiter.
> * For example, if you combine rainbow and font, the user must enter it like 0.3,minecraft:alt (the ',' is the default delimiter).

## Preset

```
{
  "pattern": "\\[([^\\]]+?)]<(RAINBOW)>",
  "styles": [
    {
      "styleType": "COLOR_RAINBOW",
      "preset": "0.7"
    }
  ]
}
```

You can set a preset by specifying it, and if the preset is empty, the option provided by the user will be used.


## Applying It – Global Style

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


## Applying It – Using It Directly as an Argument

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


## Applying It – Creating an Emoticon

```
{
  "pattern": "(:diamond:)()",
  "styles": [
    {
      "styleType": "JSON",
      "preset": "{\"type\": \"object\", \"atlas\": \"minecraft:blocks\", \"sprite\": \"item/diamond\"}"
    }
  ]
}
```

If you use the JSON type, you can replace the matched string with JSON.
The example above shows how to use the JSON type to replace `:diamond:` with a diamond icon.
You can read about Minecraft’s atlas [Text component format wiki](https://minecraft.wiki/w/Text_component_format#Atlas_Object_Type) and [Atlas wiki](https://minecraft.wiki/w/Atlas).


## Applying It – Text Placeholder API

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
