# Style Types

`styleType` selects the action applied to matched text.

## Color

| Type             | Option      | Description                                                  |
|------------------|-------------|--------------------------------------------------------------|
| `COLOR_HEX`      | Hex color   | Applies a color such as `#00AAFF`.                           |
| `COLOR_RAINBOW`  | Saturation  | Applies a rainbow color sequence.                            |
| `COLOR_GRADIENT` | Hex colors  | Applies a gradient using two or more colors.                 |
| `COLOR_PRESET`   | Preset name | Uses a named color from `presets.json`.                      |
| `COLOR_SHADOW`   | Hex color   | Applies a text shadow color on supported Minecraft versions. |
| `COLOR_TEAM`     | None        | Uses the sender's scoreboard team color.                     |

## Formatting

| Type            | Option  | Description                            |
|-----------------|---------|----------------------------------------|
| `BOLD`          | None    | Makes text bold.                       |
| `ITALIC`        | None    | Makes text italic.                     |
| `UNDERLINE`     | None    | Underlines text.                       |
| `STRIKETHROUGH` | None    | Adds strikethrough.                    |
| `OBFUSCATED`    | None    | Applies Minecraft obfuscated text.     |
| `FONT`          | Font ID | Uses a font such as `minecraft:alt`.   |
| `CLEAR`         | None    | Clears currently applied style values. |

## Interaction

| Type                    | Option           | Description                                                           |
|-------------------------|------------------|-----------------------------------------------------------------------|
| `CLICK_COMMAND_RUN`     | Command          | Runs a command when clicked.                                          |
| `CLICK_COMMAND_SUGGEST` | Command          | Places a command into the chat input when clicked.                    |
| `CLICK_COPY`            | Text             | Copies text to the clipboard when clicked.                            |
| `HOVER_TEXT`            | Text             | Shows text on hover.                                                  |
| `HOVER_ITEM`            | Slot ID or empty | Shows an item tooltip. Empty uses the main-hand item.                 |
| `URL`                   | URL              | Opens a URL when clicked. The URL must pass the configured whitelist. |
| `METADATA`              | None             | Adds receipt-time hover text and click-to-copy metadata.              |

## Modification

| Type         | Option | Description                                            |
|--------------|--------|--------------------------------------------------------|
| `UPPER`      | None   | Converts matched text to uppercase.                    |
| `LOWER`      | None   | Converts matched text to lowercase.                    |
| `CAPITALIZE` | None   | Capitalizes the first character.                       |
| `REPLACE`    | Text   | Replaces the matched text while preserving style flow. |
| `MASK`       | Text   | Repeats the option once for each original character.   |
| `PREFIX`     | Text   | Adds text before the match.                            |
| `SUFFIX`     | Text   | Adds text after the match.                             |

## Advanced

| Type               | Option               | Description                                                                |
|--------------------|----------------------|----------------------------------------------------------------------------|
| `SHOW_ITEM`        | `only_name` or empty | Opens a view-only item showcase. `only_name` shows just the item name.     |
| `SHOW_INVENTORY`   | None                 | Opens a view-only inventory showcase when clicked.                         |
| `SHOW_ENDER_CHEST` | None                 | Opens a view-only ender chest showcase when clicked.                       |
| `ICON_PRESET`      | Preset name          | Displays a configured icon from `presets.json`.                            |
| `JSON`             | JSON text component  | Parses a raw Minecraft text component.                                     |
| `DISCORD_JSON`     | `webhook;JSON`       | Sends a JSON payload to a Discord webhook.                                 |
| `COMMAND_RUN`      | Command              | Runs a command when the message is processed.                              |
| `LOG`              | None                 | Logs sender and text information to the server console.                    |
| `BUBBLE`           | None                 | Displays a speech bubble above the sender.                                 |
| `BLOCK`            | Text                 | Stops the message from being sent.                                         |
