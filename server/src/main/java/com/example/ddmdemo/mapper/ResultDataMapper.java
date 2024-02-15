package com.example.ddmdemo.mapper;

import com.example.ddmdemo.dto.ResultDataDto;
import com.example.ddmdemo.indexmodel.DummyIndex;

public class ResultDataMapper {
    public ResultDataDto mapToDto(DummyIndex indexUnit, String highlight) {
        ResultDataDto dto = new ResultDataDto();
        dto.setSignatoryName(indexUnit.getSignatoryName());
        dto.setSignatoryLastname(indexUnit.getSignatoryLastname());
        dto.setAddress(indexUnit.getAddress());
        dto.setGovernmentLevel(indexUnit.getGovernmentLevel());
        dto.setFilename(indexUnit.getServerFilename());
        dto.setHighlighter(highlight);
        return dto;
    }
}
