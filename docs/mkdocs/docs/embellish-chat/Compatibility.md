# Compatibility

Embellish Chat is designed to work as a server-side chat enhancement mod. Some integrations add new targeting options or
placeholder values, while some chat-formatting mods may take priority over its styling pipeline.

## Supported Integrations

| Integration            | Support   | Notes                                                                                                                          |
|------------------------|-----------|--------------------------------------------------------------------------------------------------------------------------------|
| Fabric Permissions API | Built in  | Rule group keys in `style_rules` and `mention_rules` are used as permission nodes.                                             |
| Text Placeholder API   | Built in  | Placeholder values can be used in message headers, mention overlay text, JSON payloads, and other configured text.             |
| Styled Nicknames       | Supported | Player mentions can resolve nicknames when the integration is present.                                                         |
| LuckPerms              | Supported | Required for `LUCK_PERMS_GROUP` mention actions such as `@group(admin)`.                                                       |
| Advanced Chat          | Supported | Required for `ADVANCED_CHAT_CHANNEL` mention actions such as channel mentions.                                                 |
| Geyser                 | Supported | Java and Bedrock players can mention each other normally. Hover text and click events may not fully render on Bedrock clients. |

## Known Conflicts

| Mod         | Behavior                                                                                           | Recommendation                                                                                   |
|-------------|----------------------------------------------------------------------------------------------------|--------------------------------------------------------------------------------------------------|
| Styled Chat | Styled Chat takes priority over Embellish Chat's style processing. Mention processing still works. | If both mods are installed, remove entries from `style_rules` to avoid unnecessary style checks. |

## Practical Notes

!!! note "Client rendering can vary"
    Server-side text components are still interpreted by the client. Java clients usually show hover and click events
    correctly, while Bedrock clients connected through Geyser may display a simpler version.

!!! tip "Keep only the systems you use"
    If another mod already owns chat formatting, use Embellish Chat for mentions and showcases only. Keeping unused style
    rules empty reduces message-processing work.
