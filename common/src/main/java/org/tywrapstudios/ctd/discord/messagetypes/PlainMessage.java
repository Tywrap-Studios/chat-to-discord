package org.tywrapstudios.ctd.discord.messagetypes;

import org.json.JSONException;
import org.json.JSONObject;

public class PlainMessage {
    private String username;
    private String avatarUrl;
    private String content;

    public PlainMessage() {
    }

    public String getUsername() {
        return username;
    }

    public PlainMessage setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public PlainMessage setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
        return this;
    }

    public String getContent() {
        return content;
    }

    public PlainMessage setContent(String content) {
        this.content = content;
        return this;
    }

    public JSONObject get() {
        JSONObject obj = new JSONObject();
        try {
            obj.put("username", username);
            obj.put("avatar_url", avatarUrl);
            obj.put("content", content);

        } catch (JSONException exception) {
            exception.printStackTrace();
        }
        return obj;
    }
}
