package com.example.ddmdemo.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
public class GeolocationQueryDto {
    private String radius;
    private String latitude;
    private String longitude;
}
