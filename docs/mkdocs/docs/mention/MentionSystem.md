# Mention System

The Mention system enhances communication on your server by allowing players to notify specific individuals or groups. It goes beyond simple name highlighting by supporting complex targeting rules like teams, radius, and permissions.

## Mechanism

The mention process operates in three stages:

1. **Detection**: The plugin identifies mention keywords (e.g., `@everyone`, `@player`) within the chat message.
2. **Targeting**: Based on the **Mention Type**, the plugin calculates the specific recipients (e.g., players within a 50-block radius).
3. **Notification**: The target players receive a distinct visual cue and an auditory alert to ensure the message is noticed.

This system ensures that critical messages reach the right people without spamming the entire server.

## Documentation Structure

This section is divided into three parts to help you configure and use the mention system:

* **[Configuration](Configuration.md)**
  Explains how to customize the mention behavior in `config.json`. It covers setting up notification sounds, visual formats, and cooldowns.

* **[Type](MentionType.md)**
  A reference list of all available mention types. It details the logic for each type, such as targeting by team, world, or proximity.

* **[Application](Application.md)**
  Provides a guide on how players can use these mentions in-game, including required permissions and syntax examples.