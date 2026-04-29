# Style Configuration

Style rules live in `config/embellish-chat/styles.json` under `style_rules`. Each top-level key is both a rule group and
a permission node.

## Rule Shape

```json
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

| Field       | Required | Description                                                 |
|-------------|----------|-------------------------------------------------------------|
| `pattern`   | Yes      | Regular expression with two capture groups.                 |
| `comment`   | No       | Help text shown in `/embellish-chat help style`.            |
| `styles`    | Yes      | Ordered list of style actions.                              |
| `styleType` | Yes      | A value from [Style Types](StyleType.md).                   |
| `preset`    | Yes      | Fixed option value. If empty, capture group 2 is used.      |

## Permission Groups

```json
{
  "style_rules": {
    "embellish-chat.chat": [],
    "embellish-chat.admin-style": []
  }
}
```

Players only receive rules from groups they can use. This lets you give different style rules to different permission
groups.

!!! warning "Do not leave preset empty for command-like styles"
    For `COMMAND_RUN`, `DISCORD_JSON`, `JSON`, and `BLOCK`, put the intended value in `preset` instead of taking it from
    player-written capture groups.

## Single Style

```json
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

This matches `__Text__` and underlines `Text`. The second capture group is empty because `UNDERLINE` does not need an
option.

## Multiple Styles

```json
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

Style actions run in order. This example makes the captured text both underlined and bold.

## Captured Option

```json
{
  "pattern": "\\[([^\\]]+?)]<(#[0-9a-fA-F]{6})>",
  "styles": [
    {
      "styleType": "COLOR_HEX",
      "preset": ""
    }
  ]
}
```

The player writes `[Sky]<#00AAFF>`. Group 1 is `Sky`, and group 2 is `#00AAFF`.

## Fixed Preset

```json
{
  "pattern": "\\[([^\\]]+?)]<RAINBOW>",
  "styles": [
    {
      "styleType": "COLOR_RAINBOW",
      "preset": "0.7"
    }
  ]
}
```

This rule always uses `0.7` as the rainbow saturation. The player only controls the displayed text.

## Ordering Guidelines

| Rule type                                                 | Place                                          |
|-----------------------------------------------------------|------------------------------------------------|
| Simple formatting rules                                   | Near the top.                                  |
| URL and click interaction rules                           | After basic formatting.                        |
| Full-message rules like `(.+)`                            | Test with nearby matching rules.               |
| Showcase rules such as `[i]`, `[inv]`, and `[end]`        | At the bottom.                                 |
| `BLOCK` rules                                             | Last among rules that should cancel a message. |

!!! warning "Place showcase rules last"
    Put `SHOW_ITEM`, `SHOW_INVENTORY`, and `SHOW_ENDER_CHEST` below rules that may modify the same text. Minecraft may
    crash if another style tries to modify text after one of these showcase types has already been applied.
