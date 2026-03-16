# Mention System

The mention system helps players notify specific people or groups in chat. It goes beyond simple name highlighting by supporting targeting rules based on teams, distance, worlds, and permissions.

## Mechanism

The mention process works in three stages:

1. **Detection**: The mod finds mention patterns such as `@everyone` or `@player` in the chat message.
2. **Targeting**: Based on the configured **Mention Type**, it resolves the actual recipients.
3. **Notification**: The target players receive visual and audio feedback so the message stands out.

This makes it easier to deliver important messages to the right players without spamming everyone else.

## Documentation Structure

This section is split into three parts:

* **[Configuration](Configuration.md)**
  Explains how to define mention rules in `mentions.json`, including notification sounds, titles, and cooldowns.

* **[Mention Type](MentionType.md)**
  Lists every available mention type and explains how each one selects its targets.

* **[Application](Application.md)**
  Shows practical examples such as admin pings, announcements, and private staff channels.
