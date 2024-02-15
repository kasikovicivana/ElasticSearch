package com.example.ddmdemo.service.interfaces;

import com.example.ddmdemo.dto.DummyDocumentFileResponseDTO;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public interface IndexingService {

//    String indexDocument(MultipartFile documentFile);

    @Transactional
    DummyDocumentFileResponseDTO indexDocument(MultipartFile documentFile, boolean isContract);
}
