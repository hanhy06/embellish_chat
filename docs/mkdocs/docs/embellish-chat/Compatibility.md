# Compatibility

## Fully Supported

* **Fabric Permissions API (Embedded):** The keys defined in `style_rules` and `mention_rules` directly function as permission nodes. Rules are evaluated from top to bottom based on the player's permissions.
* **Text Placeholder API (Embedded):** Supports dynamic placeholders in mention titles and style presets. Use `%embellish-chat:content%` to access the raw, unparsed chat message.
* **Styled Nicknames:** Supports mentioning players by nickname.
* **LuckPerms:** Required for the `@group` mention type. Without it, group mentions are ignored.
* **Advanced Chat:** Required for the `@channel` mention type. Without it, channel mentions are ignored.
* **Geyser (Bedrock Edition):** Mentions between Java and Bedrock editions work seamlessly. Advanced styling such as hover text and click events may not fully render on Bedrock clients.

## Known Conflicts

* **Styled Chat:** Styled Chat takes priority. If installed, Embellish Chat styling features are overridden.
* **Mentions still work:** The mention and notification system remains functional.
* **Performance tip:** If both mods must be used together, remove all entries in `style_rules` to prevent unnecessary background processing.
