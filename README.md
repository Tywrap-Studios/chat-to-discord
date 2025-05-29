# Chat To Discord; CTD
Yep, you guessed it, just another one of those simplistic mods that send your Minecraft chat to a Discord Webhook.  
But that's not all this mod does! It has a few other features that other mods like this lack.

---
### So, just for good measure, let's list all the features in this mod (in no particular order):
- Sending Player Messages directly to the Webhook.
- Sending Game Messages (e.g. Deaths, Advancements) directly to the Webhook.
- Sending Command Messages (e.g. /say) directly to the Webhook.
- Sending a Start and Stop message for the respective server lifecycle events.
- The `/ctd` command, which displays info about your current settings and the mod!
- Alongside that, `/ctd reload` reloads your config without the need of a laggy vanilla /reload!
- Before your message is sent, it goes through some necessary Compatibility handlers, which most mods lack:
  - Your message is modified to negate global pings (e.g. `@everyone`).
  - Your message is modified to negate invite links.
  - Your message is modified to negate any role pings that you don't allow in Config.
  - Your message and player names in it are modified to bypass Discord's native Markdown features (such as _Cursive text_).
  - When sharing a waypoint using Xaero's Mini-/Worldmap, it displays a very "weird" message, this message can actually be deciphered using some smart Java magic, and this mod rewrites your message to be more legible.
- A fully fletched way to send stuff to a Webhook for addon Creators.
- The Config gets reloaded even when the vanilla /reload command is run.
- If the server crashes, a specialised embed will be sent, displaying the main cause, and also linking to an automatically made Mclogs Site!
---
### A basic Config System is also included!:
```json5
{
  "format_version": "2.0",
  // All configurations for the Discord integration.
  "discord_config": {
    // A list of webhooks in Strings that the mod will send messages to. e.g.: "https://discord.com/api/webhooks/..."
    "discord_webhooks": [],
    // Whether to only send player messages to Discord, and not game related messages (e.g. join/leave messages, deaths, etc.).
    "only_send_messages": false,
    // Whether to send messages as an embed. If false, messages will be sent as plain text.
    "embed_mode": false,
    /* The setting below must be an RGB int, so not a `255, 255, 255` type of thing.
       Use this site if you want to use this feature:
       http://www.shodor.org/~efarrow/trunk/html/rgbint.html
    */
    "embed_color_rgb_int": 5489270,
    // A list of role ID's in Strings that users are allowed to ping from MC. e.g. "123456789012345678"
    "role_ids": []
  },
  // Several configurations for utility features.
  "util_config": {
    // Whether to display debug information in the console.
    "debug_mode": false,
    // Whether to suppress all warnings from this mod. NOT RECOMMENDED.
    "suppress_warns": false
  }
}
```
> [!NOTE]
> This file can be found at `.../config/ctd.json5`.
---
### Extra info:
> [!IMPORTANT]
> It is suggested to completely whitelist the Webhook for your Auto-Moderation bots.  
> Why whitelist? For starters, most bots will flag it as "spamming" since the player name is always included in the message, and thus repeated a few times, whitelisting the Webhook solves the hassle of having to remake it every time your bot deletes it.

> [!NOTE]
> CTD is published under the TYSPAL Licence, which can be viewed [here](./LICENSE).
