package org.example.dto;

import org.json.JSONObject;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RegisterRequest {
    private String login;
    private String display_name;

    public String toJsonString() {
        JSONObject obj = new JSONObject();
        obj.put("login", login);
        obj.put("display_name", display_name);
        return obj.toString();
    }
}
