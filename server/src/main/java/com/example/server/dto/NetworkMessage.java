package com.example.server.dto;

import lombok.Data;

@Data
public class NetworkMessage {
    private String type;
    private String payload;

    public NetworkMessage() {}

    public NetworkMessage(String type, String payload) {
        this.type = type;
        this.payload = payload;
    }
}
