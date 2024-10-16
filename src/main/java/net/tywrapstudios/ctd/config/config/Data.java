package net.tywrapstudios.ctd.config.config;

import java.util.ArrayList;
import java.util.List;

public class Data {
    /**
     * This is where the order of the entries and the entries themselves are defined.
     */
    public String CONFIG_DO_NOT_TOUCH = "1.2";
    public String c0 = "After you've changed any of the values in this file, you can run /reload to apply changes in-game.";
    public List<String> discord_webhooks = new ArrayList<>();
    public boolean only_send_messages = false;
    public boolean debug_mode = false;
    public boolean embed_mode = false;
    public String c1 = "Please provide your own Pastebin API key from https://pastebin.com/doc_api#1";
    public String pastebin_api_key = "";
    public String c2 = "The setting below must be an RGB int, so not a `255, 255, 255` type of thing.";
    public String c3 = "Use this site if you want to use this feature:";
    public String c4 = "http://www.shodor.org/~efarrow/trunk/html/rgbint.html";
    public int embed_color_rgb_int = 5489270;
    public String c5 = "A list of role ID's that users are allowed to ping from MC.";
    public List<String> role_ids = new ArrayList<>();
    public String c6 = "Set this to true if you wish not to receive LOGGER.error messages. NOT RECOMMENDED!!";
    public boolean suppress_warns = false;

    public void update() {
    }
}
