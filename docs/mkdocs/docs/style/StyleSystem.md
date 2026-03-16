# Style System

The style system is the core feature of Embellish Chat. It lets you match chat text with regular expressions and apply visual or interactive effects to the result.

## Mechanism

The styling process has two steps:

1. **Pattern Matching**: The mod scans chat messages with regular expressions.
2. **Style Application**: When a rule matches, the configured styles are applied to the captured text.

This structure supports everything from simple keyword highlighting to advanced interactive chat behavior.

## Documentation Structure

This section is split into three parts:

* **[Configuration](Configuration.md)**
  Explains the JSON structure used to define style rules, including `pattern`, `comment`, and `styles`.

* **[Style Type](StyleType.md)**
  Lists every available style type, including colors, formatting, hover effects, click actions, and advanced behaviors.

* **[Application](Application.md)**
  Shows practical examples such as custom chat formatting, bubble chat, macros, and global effects.
