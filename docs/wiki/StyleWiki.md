# Available Style Types

| Type                    | Description                                                                                                                                                                      | Option                  |
|-------------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|-------------------------|
| `METADATA`              | When the mouse hovers over the text, display the time the server received it, and when clicked, copy it to the clipboard.                                                        | No options are required |
| `COLOR_HEX`             | Applies the color specified by the HEX code provided as an option.                                                                                                               | HEX code                |
| `COLOR_RAINBOW`         | Cycles through rainbow colors                                                                                                                                                    | saturation              |
| `COLOR_PRESET`          | Uses a predefined color name from the `colorPreset` section                                                                                                                      | color preset            |
| `COLOR_SHADOW`          | Applies the HEX code color provided as an option to the shadow.                                                                                                                  | HEX code                |
| `CLICK_COMMAND_RUN`     | Runs the command supplied via the option when the text is clicked.                                                                                                               | command                 |
| `CLICK_COMMAND_SUGGEST` | Suggests the command supplied via the option when the text is clicked. The suggested command is placed into the chat input field but not executed automatically.                 | command                 |
| `HOVER_TEXT`            | Displays the text supplied via the option when the mouse hovers over it.                                                                                                         | text                    |
| `FONT`                  | Changes the font style                                                                                                                                                           | font id                 |
| `URL`                   | Allows opening the URL provided as an option when clicked.                                                                                                                       | url                     |
| `BOLD`                  | Bold text                                                                                                                                                                        | No options are required |
| `ITALIC`                | Italic text                                                                                                                                                                      | No options are required |
| `UNDERLINE`             | Underlined text                                                                                                                                                                  | No options are required |
| `STRIKETHROUGH`         | Strikethrough text                                                                                                                                                               | No options are required |
| `OBFUSCATED`            | Applies Minecraft style obfuscation to make the text unreadable.                                                                                                                 | No options are required |
| `REPLACE`               | Replaces the matched text using the provided option string while preserving the original style.                                                                                  | Replacement string      |
| `MASK`                  | Replaces the original string with the characters supplied via options, adjusting to match the original length.                                                                   | Replacement character   |
| `UPPER`                 | Transforms every matched substring into uppercase characters.                                                                                                                    | No options are required |
| `LOWER`                 | Transforms every matched substring into lowercase characters.                                                                                                                    | No options are required |

> **Notes**
>
> * `COLOR_SHADOW` is not available in versions earlier than 1.21.1.

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
  "pattern": "_*(.+?)*_()",
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