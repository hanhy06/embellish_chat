# Configuration

Style rules are stored in `config/embellish-chat/styles.json` under the top-level `style_rules` object.

## Configuration Structure

```
{
  "pattern": "\\*\\*(.+?)\\*\\*()",
  "comment": "<blue><b>Pattern</b></blue>: **Text**\n<dark_aqua><b>Comment</b></dark_aqua>: bold formatting\n",
  "styles": [
    {
      "styleType": "BOLD",
      "preset": ""
    }
  ]
}
```

* **`pattern`**: This is a regular expression for scanning text. It must have two capture groups.
  * `group 1`: This is text to be styled.
  * `group 2`: This is text passed as an option.
* **`comment`**: This comment is used in `/embellish-chat help style`.
* **`styles`**: Defines the styles to be applied to captured group 1.
  * `styleType`: This is the style type. You can use all types listed in the [StyleWiki](https://hanhy06.github.io/embellish-chat-wiki/style/StyleType/).
  * `preset`: This is a preset value. If a value is provided, it is always used; if it is empty, the content of the user's captured group 2 is used instead.

## File Layout

```json
{
  "style_rules": {
    "embellish-chat.chat": [
      {
        "pattern": "\\*\\*(.+?)\\*\\*()",
        "comment": "<blue><b>Pattern</b></blue>: **Text**\n<dark_aqua><b>Comment</b></dark_aqua>: bold formatting\n",
        "styles": [
          {
            "styleType": "BOLD",
            "preset": ""
          }
        ]
      }
    ],
    "embellish-chat.command_argument": []
  }
}
```

* **`style_rules`**: Top-level container for all styling rules.
* **Permission node keys**: Each key such as `embellish-chat.chat` is treated as a permission node.
* **Rule order matters**: Rules are processed from top to bottom, so broad matches should stay below more specific rules.

## Usage Patterns

### Single Style

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

### Multiple Style

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

### Preset

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
