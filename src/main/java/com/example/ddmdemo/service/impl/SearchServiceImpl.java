package com.example.ddmdemo.service.impl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.GeoLocation;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import com.example.ddmdemo.controller.SearchController;
import com.example.ddmdemo.dto.GeolocationQueryDto;
import com.example.ddmdemo.dto.HighlighterDto;
import com.example.ddmdemo.dto.QueryDataDto;
import com.example.ddmdemo.dto.ResultDataDto;
import com.example.ddmdemo.indexmodel.DummyIndex;
import com.example.ddmdemo.mapper.ResultDataMapper;
import com.example.ddmdemo.service.interfaces.SearchService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.elasticsearch.geometry.Point;
import org.elasticsearch.index.query.GeoDistanceQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.jsoup.Jsoup;
import java.util.logging.Logger;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.HighlightQuery;
import org.springframework.data.elasticsearch.core.query.StringQuery;
import org.springframework.data.elasticsearch.core.query.highlight.Highlight;
import org.springframework.data.elasticsearch.core.query.highlight.HighlightField;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SearchServiceImpl implements SearchService {

    private final ElasticsearchOperations elasticsearchTemplate;

    @Override
    public List<ResultDataDto> advancedSearch(List<QueryDataDto> expression, Pageable pageable) {
        List<HighlightField> fields = new ArrayList<>();
        for (QueryDataDto data: expression){
            fields.add(new HighlightField(data.getField()));
        }
        Highlight highlight1 = new Highlight(fields);
        HighlightQuery query = new HighlightQuery(highlight1, DummyIndex.class);
        var searchQueryBuilder =
                new NativeQueryBuilder()
                        .withQuery(buildAdvancedSearchQuery(expression))
                        .withHighlightQuery(query)
                        .withPageable(pageable);

        var data = runQuery(searchQueryBuilder.build());
        List<ResultDataDto> results = new ArrayList<>();
        for (SearchHit<DummyIndex> hit : data) {
            DummyIndex indexUnit = hit.getContent();
            try {
                String highlight = getHighlight(hit);
                results.add(new ResultDataMapper().mapToDto(indexUnit, highlight));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return results;
    }

    private String getHighlight(SearchHit<DummyIndex> hit) {
        return hit.getHighlightFields().values()
                .stream()
                .reduce((strings1, strings2) -> Stream.concat(strings1.stream(), strings2.stream()).collect(Collectors.toList()))
                .map(strings -> String.join(" ... ", strings))
                .map(this::stripHtml)

                .orElseGet(() -> {
                            String lawText = hit.getContent().getLawText();
                            String contractText = hit.getContent().getContractText();
                            if (lawText != null) {
                                return lawText.substring(0, 200).concat(" ... ");
                            } else if (contractText != null) {
                                return contractText.substring(0, 250).concat(" ... ");
                            } else {
                                return hit.getContent().getContentSr().substring(0, 250).concat(" ... ");
                            }
                        }
                );
    }

    private String stripHtml(String s) {
        return Jsoup.parse(s).text();
    }

    private boolean isPhraseSearch(String value) {
        boolean isPhraseSearch = value.startsWith("\"") && value.endsWith("\"");
        return isPhraseSearch;
    }

    public Query buildAdvancedSearchQuery(List<QueryDataDto> operands) {
        return BoolQuery.of(b -> {
            AtomicBoolean firstToken = new AtomicBoolean(true);
            operands.forEach(operand -> {
                String field = operand.getField();
                String value = operand.getValue();
                String operator = operand.getOperator().trim();
                boolean isPhrase = isPhraseSearch(value);

                if (isPhrase){
                    if (firstToken.get()){
                        if (operands.size() > 1){
                            if (operands.get(1).getOperator().equalsIgnoreCase("or")){
                                b.should(s -> s.matchPhrase(mt -> mt.field(field).query(value)));
                            }else{
                                b.must(s -> s.matchPhrase(mt -> mt.field(field).query(value)));
                            }
                        }else{
                            b.must(s -> s.matchPhrase(mt -> mt.field(field).query(value)));
                        }
                        firstToken.set(false);
                    }else{

                        if (operator.equalsIgnoreCase("AND")) {
                            b.must(m -> m.matchPhrase(mt -> mt.field(field).query(value)));
                        } else if (operator.equalsIgnoreCase("OR")) {
                            b.should(s -> s.matchPhrase(mt -> mt.field(field).query(value)));
                        } else if (operator.equalsIgnoreCase("NOT")) {
                            b.mustNot(mn -> mn.matchPhrase(mt -> mt.field(field).query(value)));
                        }
                    }
                }else{
                    if (firstToken.get()){
                        if (operands.size() > 1){
                            if (operands.get(1).getOperator().equalsIgnoreCase("or")){
                                b.should(s -> s.match(mt -> mt.field(field).query(value)));
                            }else{
                                b.must(s -> s.match(mt -> mt.field(field).query(value)));
                            }
                        }else{
                        b.must(s -> s.match(mt -> mt.field(field).query(value)));
                        }
                        firstToken.set(false);
                    }else{

                        if (operator.equalsIgnoreCase("AND")) {
                            b.must(m -> m.match(mt -> mt.field(field).query(value)));
                        } else if (operator.equalsIgnoreCase("OR")) {
                            b.should(s -> s.match(mt -> mt.field(field).query(value)));
                        } else if (operator.equalsIgnoreCase("NOT")) {
                            b.mustNot(mn -> mn.match(mt -> mt.field(field).query(value)));
                        }
                    }
                }
            });
            return b;
        })._toQuery();
    }

    private SearchHits<DummyIndex> runQuery(NativeQuery searchQuery) {

        var searchHits = elasticsearchTemplate.search(searchQuery, DummyIndex.class,
                IndexCoordinates.of("dummy_index"));
        return searchHits;
    }

    @Override
    public List<ResultDataDto> geolocationSearch(GeolocationQueryDto dto) throws IOException {
        double latitude = Double.parseDouble(dto.getLatitude());
        double longitude = Double.parseDouble(dto.getLongitude());
        int radius = Integer.parseInt(dto.getRadius());

        String queryString = String.format(
                "{\"geo_distance\": {\"distance\": \"%dkm\", \"governmentCoordinate\": [%f, %f]}}",
                radius, longitude, latitude);
        List<HighlightField> fields = new ArrayList<>();
        fields.add(new HighlightField("governmentCoordinate"));
        Highlight highlight1 = new Highlight(fields);
        HighlightQuery query = new HighlightQuery(highlight1, DummyIndex.class);

        NativeQuery searchQuery = new NativeQueryBuilder()
                .withQuery(new StringQuery(queryString))
                .withHighlightQuery(query)
                .build();

        SearchHits<DummyIndex> data = elasticsearchTemplate.search(searchQuery, DummyIndex.class, IndexCoordinates.of("dummy_index"));
        List<ResultDataDto> results = new ArrayList<>();
        for (SearchHit<DummyIndex> hit : data) {
            DummyIndex indexUnit = hit.getContent();
            try {
                String highlight = getHighlight(hit);
                results.add(new ResultDataMapper().mapToDto(indexUnit, highlight));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return results;
    }
}
