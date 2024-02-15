package com.example.ddmdemo.indexrepository;

import com.example.ddmdemo.indexmodel.DummyIndex;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DummyIndexRepository
    extends ElasticsearchRepository<DummyIndex, String> {
}
