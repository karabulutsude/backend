package com.jollifiy.gameanalytics.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProfileRequest {

    private String playerId;
    private String deviceId;
    private String country;
}