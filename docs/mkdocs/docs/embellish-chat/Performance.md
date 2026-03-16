# Performance

## Test Setup

These tests were performed in a synthetic stress environment to measure **worst-case** performance.
They do **not** represent normal server conditions.

- Embellish Chat: **3.4.0 (DEV)**
- Minecraft: **1.21.11**
- World: **Singleplayer, Superflat**
- CPU: **13th Gen Intel(R) Core(TM) i7-1360P**
- Max RAM: **4 GB**
- System: **Windows 11**
- Config: **Default**

## Test Scenario

For each test, the server was stressed for a total of 1,000 ticks, with up to 500 chat messages processed per tick (about 10,000 messages per second).
The reported value represents the average tick processing time (MSPT) of the mod measured during that period.

Each message was between 70 and 100 characters in length, and realistic, naturally occurring chat messages were used.

The messages used in the tests are as follows:

| Type         | Length | Test String                                                                                      |
|--------------|--------|--------------------------------------------------------------------------------------------------|
| Plain Text   | 81     | `Hello everyone! What are you all doing on the server today? I am mining diamonds.`              |
| Styling Only | 94     | `**Trading now!** Check my [inv] and [end]. Selling the [Legendary Sword]<#00FFFF> cheap :fire:` |
| Mention Only | 75     | `@everyone Gather @here for the weekend boss raid! @team(red) get ready too.`                    |
| Mixed        | 93     | `@team(blue) charge the boss! ~~No retreat~~ My [inv] is full of potions, yell :heart: if low.`  |

## Results and Analysis

<canvas id="mstpChart"></canvas>

A single Minecraft tick allows for a **50 ms** processing window. As shown in the data above, the most expensive mode, **Mention Only (ON)**, uses about **14.27 ms** while processing **500 messages per tick**.

That leaves a safety margin of roughly **35 ms** within the tick budget. Even under this extreme load, the system is designed to reduce server lag and maintain stable TPS.

## Reason for Separating Mention Notification (ON/OFF)

The mention test was divided into notification enabled (ON) and disabled (OFF) states to isolate the extra latency caused by Minecraft's native packet processing.
When notifications are enabled, the server sends additional vanilla packets to target players for sounds and titles, which adds some overhead.
Separating these states makes it easier to measure the mod's own processing cost more accurately.

In real multiplayer environments, this packet overhead can scale with the number of recipients, so performance may vary depending on player count and mention usage.
If you want to disable sound/title notifications entirely, set `notify_mention_enabled` to `false` in `config/embellish-chat/config.json`.

## Reason for Test Configuration Changes

The previous benchmark used extremely aggressive assumptions and was not close to a typical production server. To produce more practical metrics, the test setup was updated as follows:

* **Realistic Message Length:** We used natural sentence structures reflecting actual player chat patterns and adjusted the character count to between 70 and 100 characters to match average chat lengths.
* **Simulating a Production Server Environment:** To create an environment similar to an actively running server setup, optimization mods such as Sodium and Lithium were applied together.
* **Isolated Performance Measurement:** Although the server environment itself was optimized, the millisecond per tick (MSPT) processing time measured in this test represents the isolated computational performance of the Embellish Chat mod's internal logic. The external optimization mods do not directly intervene in or affect this mod's chat processing results.
