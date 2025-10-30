# Available Style Types

| Type            | Description                                                                                                               | Option                   |
|-----------------|---------------------------------------------------------------------------------------------------------------------------|--------------------------|
| `METADATA`      | When the mouse hovers over the text, display the time the server received it, and when clicked, copy it to the clipboard. | No options are required  |
| `COLOR_HEX`     | Applies the color specified by the HEX code provided as an option.                                                        | HEX code                 |
| `COLOR_RAINBOW` | Cycles through rainbow colors                                                                                             | saturation               |
| `COLOR_PRESET`  | Uses a predefined color name from the `colorPreset` section                                                               | color preset             |
| `COLOR_SHADOW`  | Applies the HEX code color provided as an option to the shadow.                                                           | HEX code                 |
| `FONT`          | Changes the font style                                                                                                    | font id                  |
| `URL`           | Allows opening the URL provided as an option when clicked.                                                                | url                      |
| `BOLD`          | Bold text                                                                                                                 | No options are required  |
| `ITALIC`        | Italic text                                                                                                               | No options are required  |
| `UNDERLINE`     | Underlined text                                                                                                           | No options are required  |
| `STRIKETHROUGH` | Strikethrough text                                                                                                        | No options are required  |
| `OBFUSCATED`    | Applies Minecraft style obfuscation to make the text unreadable.                                                          | No options are required  |
| `REPLACE`       | Replaces the matched text using the provided option string while preserving the original style.                           | Replacement string       |
| `MASK`          | Replaces the original string with the characters supplied via options, adjusting to match the original length.            | Replacement character    |
| `UPPER`         | Transforms every matched substring into uppercase characters.                                                             | No options are required  |
| `LOWER`         | Transforms every matched substring into lowercase characters.                                                             | No options are required  |