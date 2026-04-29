# Mention System

The mention system finds configured patterns in chat, resolves the players who should receive the alert, and optionally
sends overlay text or sound feedback. It can behave like familiar `@player` mentions, server-wide announcements, local
chat alerts, or private channels.

## Mental Model

| Step                | What happens                                                                                            |
|---------------------|---------------------------------------------------------------------------------------------------------|
| 1. Permission check | The player receives mention rule groups they are allowed to use.                                        |
| 2. Detection        | Each mention rule scans the message with its regular expression.                                        |
| 3. Targeting        | Mention actions resolve players from names, teams, groups, worlds, channels, permissions, or selectors. |
| 4. Styling          | The matched mention text is styled in the final chat message.                                           |
| 5. Notification     | Target players receive overlay text and sound feedback when notifications are enabled.                  |

## What Mentions Can Target

| Target                | Example use                                              |
|-----------------------|----------------------------------------------------------|
| One player            | `@Steve`                                                 |
| Everyone online       | `@everyone`                                              |
| Nearby players        | `@here`                                                  |
| Scoreboard team       | `@team(red)`                                             |
| Minecraft world       | `@world(minecraft:overworld)`                            |
| LuckPerms group       | `@group(admin)`                                          |
| Advanced Chat channel | `@channel(staff)`                                        |
| Permission node       | Notify everyone with a specific permission.              |
| Target selector       | Use vanilla selectors such as `@a[scores={timer=..5}]`.  |

!!! note "A mention rule can combine targets"
    Multiple mention actions in one rule are intersected. For example, `INSIDE` + `TEAM` targets players who are both
    inside the radius and in the selected team.

## Reading Path

| Page                              | Use it for                                                                     |
|-----------------------------------|--------------------------------------------------------------------------------|
| [Configuration](Configuration.md) | Learn the exact JSON shape for mention rules.                                  |
| [Mention Types](MentionType.md)   | Look up every available `mentionType`.                                         |
| [Recipes](Application.md)         | Copy practical examples for admin alerts, announcements, and private channels. |
