package com.example.ddmdemo.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ResultDataDto {
    private String signatoryName;
    private String signatoryLastname;
    private String governmentName;
    private String governmentLevel;
    private String address;
    private String filename;
    private String highlighter;
}
