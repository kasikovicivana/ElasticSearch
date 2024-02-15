package com.example.ddmdemo.controller;

import com.example.ddmdemo.dto.GeolocationQueryDto;
import com.example.ddmdemo.dto.QueryDataDto;
import com.example.ddmdemo.dto.ResultDataDto;
import com.example.ddmdemo.indexmodel.DummyIndex;
import com.example.ddmdemo.service.interfaces.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "api/search")
@CrossOrigin(origins = "http://localhost:4200")
public class SearchController {
    @Autowired
    private SearchService searchService;

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> search(@RequestBody List<QueryDataDto> dto) throws Exception {
        try {
            List<ResultDataDto> result = searchService.advancedSearch(dto, PageRequest.of(0, 100));
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @PostMapping(value = "/geolocation", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> geolocationSearch(@RequestBody GeolocationQueryDto dto) throws Exception {

        try {
            List<ResultDataDto> result = searchService.geolocationSearch(dto);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
