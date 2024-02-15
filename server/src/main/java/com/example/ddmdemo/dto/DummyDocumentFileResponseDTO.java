package com.example.ddmdemo.dto;

public record DummyDocumentFileResponseDTO(String serverFilename, String signatoryName, String signatoryLastname, String governmentName, String governmentLevel, String address, String content) {
}
