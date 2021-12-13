# Manhunt Plus: A Minecraft Manhunt plugin 🔌

---

## Developer Documentation 👋

Developed by [Sebastian Sole](https://github.com/Sebastian-Sole).

## What is Manhunt ❓

In Manhunt, there are 3 teams: Hunters, Runners, and Spectators.

Hunters try to kill the runners before they beat the Ender Dragon.

Runners must beat the Ender Dragon before they get killed once!

> Inspired by [Dream](https://youtube.com/dreamtraps)'s Manhunt series on YouTube.

## Features 🚩

### Compass tracking

Right-click with your compass to choose which player to track. The compass will point to that runner.

### Track portals

If a runner is in the Nether or in the End, the compass will track their last-used portal!

### Assign teams

Assign teams using the in-game `/team` command in order to to distinguish between players!

## Coming Soon 🏁

### Discord integration

Optionally integrate with Discord for extra features!

Automatically assign Discord roles according to teams.

Automatically (or manually) play music in your Discord voice channel that mirrors the action happening!

## How to use the plugin 🎮

1. Move the `.jar` from the [Releases tab](https://github.com/Sebastian-Sole/ManhuntPlus/releases/) to your `plugins` directory.
2. Make sure your config file is up to date and has all the required options.
3. Assign roles with `/speedrunner`, `/hunter`, and `/spectator`.
4. If you want automatic music, type `/music auto` now.
5. Type `/start`!
6. After the headstart period is over, hunters should be able to start tracking runners by right-clicking with their compass.

### Important to note

Teams are not persistent between server sessions. If you shut down the server, you'll have to re-assign teams and type `/start` again.

Please have players put their items in chests before typing `/start` if you're doing this, because the command clears inventories.

### Discord Setup Instructions _COMING SOON_

- Create a Discord app from the [Developer Portal](https://discord.com/developers/applications).
- Add a **Bot** under the bots tab.
- Take note of your **Client ID** (in the General Information tab) and your **Token** (under the Bot tab).
- Go to the following link, replacing `123YourClientID456` with your client ID: `https://discord.com/oauth2/authorize?scope=bot&client_id=123YourClientID456&permissions=8`
- Select the Discord server to add your bot to.
- Add your Client ID and Token to the respective fields in the **config file**.
- Go to your Discord server, and add the following values to the config file, in accordance with the table above (you can `Right Click > Copy ID` if you turn on Developer Mode in Discord settings!):
  - the ID of your server
  - the ID of the voice channel you want your music to play in
  - the ID of the hunter, runner, and spectator Discord roles you want to be automatically assigned

**Please note** that for auto-role-assignment to work, each Discord user's nickname for your Server must be set to their Minecraft username.

## Commands 💻

| Command                    | Description                                                                                    |
| -------------------------- | ---------------------------------------------------------------------------------------------- |
| `/speedrunner <username>`  | Assign speedrunner role                                                                        |
| `/hunter <username>`       | Assign hunter role                                                                             |
| `/spectator <username>`    | Assign spectator role                                                                          |
| `/start`                   | Start the match                                                                                |
| `/end`                     | End the match                                                                                  |
| `/compass`                 | Give yourself a compass                                                                        |
| `/setheadstart <duration>` | Sets the headstart duration, in seconds.                                                       |
| `/runnerhelp`              | Rewards runner for killing hunters                                                             |
| `/hunterhelp`              | If hunter is killed by a runner, they respawn with increasingly better items.                  |
| `/chestgenerate`           | Upon breaking a block, there is a small probability that a chest will spawn with random items. |

## Discord-related commands 💻

| Command              | Description                                                                                                                          |
| -------------------- | ------------------------------------------------------------------------------------------------------------------------------------ |
| `/music list`        | Gets a list of available tracks to play.                                                                                             |
| `/music <trackname>` | Plays a specific track by nickname and turns off auto-music.                                                                         |
| `/music stop`        | Stops all music playing and turns off auto-music.                                                                                    |
| `/music auto`        | Turns on auto-music, which plays different tracks based on game events and hunter/runner distance.                                   |
| `/music forceupdate` | Forces the music to update to match the current hunter/runner distance. Use `/music stop` first if a special event track is playing. |

## Configuration Options 🛠

Edit the `plugins/MinecraftManhunt/config.yml` file with the following options:

### Game Options

| Key                    | Description                                                                                                     | Type    | Required?                     |
| ---------------------- | --------------------------------------------------------------------------------------------------------------- | ------- | ----------------------------- |
| headStartDuration      | How long the hunters should get blindness and slowness when the match starts, in secs.                          | int     | Required                      |
| compassEnabledInNether | Set to true to allow the compass to work in the nether.                                                         | boolean | Optional, defaults to `true`  |
| sendUsageData          | Set to true to send anonymized, aggregated usage data to help improve the plugin.                               | boolean | Optional, defaults to `false` |
| uuid                   | Randomized id that is automatically assigned if `sendUsageData` is enabled. **Please do not touch this field.** | string  | Do not set manually           |

### Extra Options

| Key                     | Description                                                                                                 | Type    | Required?                                                                 |
| ----------------------- | ----------------------------------------------------------------------------------------------------------- | ------- | ------------------------------------------------------------------------- |
| setRunnersToSpecOnDeath | Set to true to set runners' gamemodes to spectator when they die.                                           | boolean | Optional, defaults to `true`                                              |
| huntersColor            | The color to give to the `hunters` team.                                                                    | string  | Optional                                                                  |
| runnersColor            | The color to give to the `runners` team.                                                                    | string  | Optional                                                                  |
| spectatorsColor         | The color to give to the `spectators` team.                                                                 | string  | Optional                                                                  |
| clearRunnerInvOnStart   | Set to true to clear the runners' inventories and experience when the game starts.                          | boolean | Optional, defaults to `false`                                             |
| clearHunterInvOnStart   | Set to true to clear the hunters' inventories and experience when the game starts.                          | boolean | Optional, defaults to `false`                                             |
| clearItemDropsOnStart   | Set to true to clear all Item entities when the game starts.                                                | boolean | Optional, defaults to `false`                                             |
| setTimeToZero           | Set to true to reset the game time when the game starts.                                                    | boolean | Optional, defaults to `true`                                              |
| startGameByHit          | Set to true to start the game when a runner hits a hunter, instead of when the `/start` command is used.    | boolean | Optional, defaults to `false`                                             |
| preGameWorldBorder      | Set to true to enforce a world border before the game starts (useful to keep players from running too far). | boolean | Optional, defaults to `false`                                             |
| preGameBorderSize       | States how big the pre-game world border would be, if enabled.                                              | int     | Optional, defaults to `100`; ignored when `preGameWorldBorder` is `false` |

### Discord Integration _COMING SOON_

| Key                  | Description                                                                                                                                                               | Type                          | Required?                             |
| -------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | ----------------------------- | ------------------------------------- |
| enableDiscord        | Set to true to turn on Discord integration. Read below for more information.                                                                                              | boolean                       | Required                              |
| discordToken         | Enter the token of your Discord bot here.                                                                                                                                 | string                        | Required if `enableDiscord` is `true` |
| ip                   | The Discord status message portion. Will display as `Playing {value}` so it's recommended that you make this your server's IP.                                            | string                        | Optional                              |
| parseDiscordCommands | Set to true if you want music commands to be run by sending a message in Discord text channels. This allows anyone in your Discord server to run music commands, however. | Optional, defaults to `false` |
| discordServerId      | The ID of your Discord server that the bot is on.                                                                                                                         | string                        | Required if `enableDiscord` is `true` |
| musicChannelId       | The ID of the voice channel that the bot should play music on.                                                                                                            | string                        | Required if `enableDiscord` is `true` |
| hunterRoleId         | The ID of the role to assign to Hunters.                                                                                                                                  | string                        | Optional                              |
| runnnerRoleId        | The ID of the role to assign to Runners.                                                                                                                                  | string                        | Optional                              |
| spectatorRoleId      | The ID of the role to assign to Spectators.                                                                                                                               | string                        | Optional                              |

> Note: if any of the role IDs are missing or invalid, no roles will be assigned.

## Permissions 🔑

| Permission                     | Description                                                                                                                           | Recommended level |
| ------------------------------ | ------------------------------------------------------------------------------------------------------------------------------------- | ----------------- |
| `minecraftmanhunt.hunter`      | Allow `/hunter` command                                                                                                               | everyone          |
| `minecraftmanhunt.speedrunner` | Allow `/speedrunner` command                                                                                                          | everyone          |
| `minecraftmanhunt.spectator`   | Allow `/spectator` command                                                                                                            | everyone          |
| `minecraftmanhunt.clearteams`  | Allow `/clearteams` command                                                                                                           | operators         |
| `minecraftmanhunt.start`       | Allow `/start` command                                                                                                                | operators         |
| `minecraftmanhunt.end`         | Allow `/end` command                                                                                                                  | operators         |
| `minecraftmanhunt.compass`     | Allow `/compass` command                                                                                                              | everyone          |
| `minecraftmanhunt.music`       | Allow `/music` command. Note that music commands can also be typed in Discord if `processDiscordCommands` in config is set to `true`. | everyone          |
| `minecraftmanhunt.config`      | Allow config-changing commands, such as `/setheadstart`.                                                                              | operators         |

## Advanced: Contribute to the code base 🤝

Please read the [`CONTRIBUTING.md`](./CONTRIBUTING.md) documentation for more information on how to contribute to the code base.

---

## Credit where credit is due ✨

This project is a further development on `yoonicode`'s original project:

Made by Eric ([yoonicode.com](http://yoonicode.com/?utm_source=github&utm_medium=web&utm_campaign=manhunt-github))

Github: [yummypasta](https://github.com/yummypasta)

Minecraft: [i18n](https://namemc.com/profile/i18n.4)
