# Style System

The style system turns matched chat text into richer Minecraft text components. A rule finds text with a regular
expression, then applies one or more style actions to the matched part.

## Mental Model

| Step                | What happens                                                             |
|---------------------|--------------------------------------------------------------------------|
| 1. Permission check | The player receives style rule groups they are allowed to use.           |
| 2. Pattern match    | Each rule scans the chat message with its configured regular expression. |
| 3. Select text      | The rule selects the matched text that will be styled.                   |
| 4. Resolve option   | The rule chooses the configured option for each style action.            |
| 5. Apply styles     | Style actions run in order and produce the final chat component.         |

!!! note "Rule order matters"
    Rules are evaluated from top to bottom inside each rule group. Test rule order when multiple rules can match the same
    text.

## What Styles Can Do

| Area         | Examples                                                                                                                     |
|--------------|------------------------------------------------------------------------------------------------------------------------------|
| Color        | Hex colors, gradients, rainbow colors, preset colors, team colors, and text shadow colors.                                   |
| Formatting   | Bold, italic, underline, strikethrough, obfuscated text, custom fonts, and clearing style.                                   |
| Interaction  | Click to run commands, suggest commands, copy text, open URLs, or show hover text.                                           |
| Modification | Replace, mask, prefix, suffix, uppercase, lowercase, and capitalize text.                                                    |
| Advanced     | Item, inventory, and ender chest showcases, icons, JSON components, Discord webhooks, speech bubbles, logs, and blocking. |

## Reading Path

| Page                              | Use it for                                        |
|-----------------------------------|---------------------------------------------------|
| [Configuration](Configuration.md) | Learn the exact JSON shape for style rules.       |
| [Style Types](StyleType.md)       | Look up every available `styleType`.              |
| [Recipes](Application.md)         | Copy practical examples for common server setups. |
