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

* **`pattern`**: A regular expression used to scan chat text. It must contain two capture groups: `group 1` is the text that will be styled, and `group 2` is the option value passed to the style.
* **`comment`**: Help text shown in `/embellish-chat help style`.
* **`styles`**: The styles applied to capture group 1. `styleType` selects the behavior, and `preset` provides a fixed option value. If `preset` is empty, the content of capture group 2 is used instead. Each `styleType` must match a handler registered in `StyleRegistry`. See [Style Type](StyleType.md) for the full list.

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

This is the simplest style rule.

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

You can combine multiple style types in a single rule.

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

You can hard-code a preset value, or leave it empty to use the option captured from the player's message.
