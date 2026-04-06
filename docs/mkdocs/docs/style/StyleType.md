# Style Type

## Type Table

| Category         | Type                                                                                                                               |
|------------------|------------------------------------------------------------------------------------------------------------------------------------|
| **Color**        | `COLOR_HEX`, `COLOR_RAINBOW`, `COLOR_GRADIENT`, `COLOR_PRESET`, `COLOR_SHADOW`, `COLOR_TEAM`                                       |
| **Formatting**   | `BOLD`, `ITALIC`, `UNDERLINE`, `STRIKETHROUGH`, `OBFUSCATED`, `FONT`, `CLEAR`                                                      |
| **Interaction**  | `CLICK_COMMAND_RUN`, `CLICK_COMMAND_SUGGEST`, `CLICK_COPY`, `HOVER_TEXT`, `HOVER_ITEM`, `URL`, `METADATA`                          |
| **Modification** | `UPPER`, `LOWER`, `CAPITALIZE`, `REPLACE`, `MASK`, `PREFIX`, `SUFFIX`                                                              |
| **Advanced**     | `SHOW_ITEM`, `SHOW_INVENTORY`, `SHOW_ENDER_CHEST`, `ATLAS_PRESET`, `JSON`, `DISCORD_JSON`, `COMMAND_RUN`, `LOG`, `BUBBLE`, `BLOCK` |

## Color Styles

* `COLOR_HEX`: Applies the color specified by the HEX code. **Option:** `Hex Code`
* `COLOR_RAINBOW`: Cycles through rainbow colors. **Option:** `Saturation`
* `COLOR_GRADIENT`: Applies a color gradient using the provided HEX codes. Supports two or more colors. **Option:** `Hex Codes`
* `COLOR_PRESET`: Uses a predefined color name from the `presets.json/color` section. **Option:** `Preset Name`
* `COLOR_SHADOW`: Applies the HEX code color provided as an option to the shadow. Available in 1.21.2+. **Option:** `Hex Code`
* `COLOR_TEAM`: Colors the text using the player's team color. **Option:** `None`

## Formatting Styles

* `BOLD`: Makes the text bold. **Option:** `None`
* `ITALIC`: Makes the text italic. **Option:** `None`
* `UNDERLINE`: Underlines the text. **Option:** `None`
* `STRIKETHROUGH`: Applies a strikethrough style to the text. **Option:** `None`
* `OBFUSCATED`: Applies Minecraft-style obfuscation to make the text unreadable. **Option:** `None`
* `FONT`: Changes the font to the specified font ID. **Option:** `Font ID`
* `CLEAR`: Removes all currently applied styles. **Option:** `None`

## Interaction

* `CLICK_COMMAND_RUN`: Immediately executes the supplied command when the text is clicked. **Option:** `Command`
* `CLICK_COMMAND_SUGGEST`: Places the supplied command into the chat input field when clicked. **Option:** `Command`
* `CLICK_COPY`: Copies the supplied text to the clipboard when the text is clicked. **Option:** `Text`
* `HOVER_TEXT`: Displays the supplied text when the mouse hovers over it. **Option:** `Text`
* `HOVER_ITEM`: Displays the item description for a specified inventory slot when hovered over. For example, `-1` is the main hand and `40` is the off hand. **Option:** `Slot ID`
* `URL`: Opens the provided URL in a browser when clicked. The domain or subdomain must be included in `presets.json/whitelist`. If the list is empty, all URLs are allowed. **Option:** `URL`
* `METADATA`: Displays the server receipt time on hover and copies it to the clipboard on click. **Option:** `None`

## Modification

* `UPPER`: Transforms every matched substring into uppercase characters. **Option:** `None`
* `LOWER`: Transforms every matched substring into lowercase characters. **Option:** `None`
* `CAPITALIZE`: Converts only the first character of the string to uppercase. **Option:** `None`
* `REPLACE`: Replaces the matched text with the provided string while preserving the original style. **Option:** `Text`
* `MASK`: Repeats the provided character or string once for each character in the original text. **Option:** `Text`
* `PREFIX`: Attaches the provided text before the original text. **Option:** `Text`
* `SUFFIX`: Attaches the provided text after the original text. **Option:** `Text`

## Advanced

* `SHOW_ITEM`: Displays the description and texture of the item or block held by the player. This style type must be placed at the very bottom. **Option:** `Atlas Path (atlas;sprite)`
* `SHOW_INVENTORY`: Displays the user's face. Clicking it shows the user's inventory snapshot. This style type must be placed at the very bottom, and the inventory snapshot is cleared when the player leaves the server. **Option:** `None`
* `SHOW_ENDER_CHEST`: Displays the user's face. Clicking it shows the user's ender chest snapshot. This style type must be placed at the very bottom, and the inventory snapshot is cleared when the player leaves the server. **Option:** `None`
* `ATLAS_PRESET`: Uses a predefined atlas name from the `presets.json/atlas` section. This style type cannot be used in versions below 1.21.8 and must be placed at the very bottom. **Option:** `Preset Name`
* `JSON`: Parses the provided JSON string and displays it as a text component. **Option:** `JSON Data`
* `DISCORD_JSON`: Sends the provided JSON payload to Discord through the specified webhook. **Option**: `webhook;JSON Data`
* `COMMAND_RUN`: Immediately executes the supplied command when the text is processed. **Option:** `Command`
* `LOG`: Logs the text and sender information to the server console. **Option:** `None`
* `BUBBLE`: Displays a speech bubble above the user's head. This type must be placed above `SHOW_ITEM` and `SHOW_INVENTORY`. **Option:** `None`
* `BLOCK`: Stops the message from being sent. **Option:** `None`
