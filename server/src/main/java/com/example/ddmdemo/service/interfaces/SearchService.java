package com.example.ddmdemo.service.interfaces;

import com.example.ddmdemo.dto.GeolocationQueryDto;
import com.example.ddmdemo.dto.QueryDataDto;
import com.example.ddmdemo.dto.ResultDataDto;
import com.example.ddmdemo.indexmodel.DummyIndex;

import java.io.IOException;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public interface SearchService {

    List<ResultDataDto> advancedSearch(List<QueryDataDto> expression, Pageable pageable);

    List<ResultDataDto> geolocationSearch(GeolocationQueryDto dto) throws IOException;
}
