package org.example.dto;

import org.json.JSONObject;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthDto {
    private String username;
    private String password;

    public String toJsonString() {
        JSONObject json = new JSONObject();
        json.put("username", username);
        json.put("password", password);
        return json.toString();
    }
}
