# Style System

The Style system is the core feature of Embellish Chat. It allows you to intercept chat messages matching specific patterns and apply various visual or interactive effects to them.

## Mechanism

The styling process works in two steps:

1. **Pattern Matching**: The plugin scans chat messages using Regular Expressions (Regex) to find specific text.
2. **Style Application**: Once a match is found, the defined styles are applied to the text.

This structure allows for dynamic chat modification, ranging from simple keyword highlighting to complex interactive menus.

## Documentation Structure

This section is divided into three parts to guide you through the system:

* **[Configuration](Configuration.md)**
  Explains the JSON structure used to define rules. It covers how to set up the `pattern` and `styles` fields properly.

* **[Type](StyleType.md)**
  A reference list of all available style types. It details every option for colors, formatting, and interactions (such as hover text or click events).

* **[Application](Application.md)**
  Provides practical examples and use cases. It demonstrates how to combine patterns and styles to create features like item display, chat macros, and custom formatting.