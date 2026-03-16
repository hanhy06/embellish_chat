# Compatibility

## Supported

* **Fabric Permissions API (Embedded):** Full integration for permission-based rules.
* **Text Placeholder API (Embedded):** Supports placeholders in mention titles and presets.
* **Styled Nicknames:** Allows mentions to match player nicknames.
* **LuckPerms:** Required for `@group` mentions.
* **Geyser:** Basic support. Mentions work, but click and hover events are limited on Bedrock.

## Not Supported / Conflicts

* **Styled Chat:** Incompatible. If both are installed, Styled Chat overrides Embellish Chat formatting. *Workaround:* Remove all `style_rules` from Embellish Chat if you only want to keep the mention features.
