# Performance

Embellish Chat processes chat messages with regular expressions, style actions, mention targeting, and optional
notification packets. This benchmark is synthetic, so it should be read as a pressure test rather than a normal
multiplayer workload.

## Test Setup

| Item           | Value                               |
|----------------|-------------------------------------|
| Embellish Chat | `3.4.0 (DEV)`                       |
| Minecraft      | `1.21.11`                           |
| World          | Singleplayer, Superflat             |
| CPU            | 13th Gen Intel(R) Core(TM) i7-1360P |
| Max RAM        | 4 GB                                |
| System         | Windows 11                          |
| Config         | Default                             |

## Scenario

The test ran for 1,000 ticks. Each tick processed up to 500 chat messages. Results are average processing time in
milliseconds per tick.

| Type         | Length | Test String                                                                                      |
|--------------|--------|--------------------------------------------------------------------------------------------------|
| Plain Text   | 81     | `Hello everyone! What are you all doing on the server today? I am mining diamonds.`              |
| Styling Only | 94     | `**Trading now!** Check my [inv] and [end]. Selling the [Legendary Sword]<#00FFFF> cheap :fire:` |
| Mention Only | 75     | `@everyone Gather @here for the weekend boss raid! @team(red) get ready too.`                    |
| Mixed        | 93     | `@team(blue) charge the boss! ~~No retreat~~ My [inv] is full of potions, yell :heart: if low.`  |

## Results

<canvas id="mstpChart"></canvas>

| Message Type         | 50 msg/tick | 100 msg/tick | 300 msg/tick | 500 msg/tick |
|----------------------|-------------|--------------|--------------|--------------|
| Plain Text           | 0.27 ms     | 1.06 ms      | 3.77 ms      | 5.83 ms      |
| Styling Only         | 0.90 ms     | 2.16 ms      | 6.93 ms      | 10.99 ms     |
| Mention Only (ON)    | 3.06 ms     | 4.86 ms      | 9.83 ms      | 14.27 ms     |
| Mention Only (OFF)   | 1.18 ms     | 3.03 ms      | 8.69 ms      | 13.00 ms     |
| Mixed                | 1.28 ms     | 2.57 ms      | 7.06 ms      | 12.00 ms     |

Minecraft targets a 50 ms tick budget. In this benchmark, the heaviest default case is `Mention Only (ON)` at 14.27 ms
while processing 500 messages in one tick.

!!! note "How to read this chart"
    These numbers isolate Embellish Chat's message-processing work. Real servers also depend on player count, other mods,
    and network conditions.

## Tuning Tips

| Tip                                       | Why it helps                                               |
|-------------------------------------------|------------------------------------------------------------|
| Keep unused rule groups empty             | Fewer regular expressions are checked for every message.   |
| Check rule order                          | Rule order matters when multiple patterns match the same text.    |
| Disable mention notifications if unneeded | Reduces overlay text and sound packet work.                 |
