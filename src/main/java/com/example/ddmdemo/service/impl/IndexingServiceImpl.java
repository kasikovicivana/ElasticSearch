package com.example.ddmdemo.service.impl;

import com.example.ddmdemo.controller.SearchController;
import com.example.ddmdemo.dto.DummyDocumentFileResponseDTO;
import com.example.ddmdemo.exceptionhandling.exception.LoadingException;
import com.example.ddmdemo.exceptionhandling.exception.StorageException;
import com.example.ddmdemo.indexmodel.DummyIndex;
import com.example.ddmdemo.indexrepository.DummyIndexRepository;
import com.example.ddmdemo.model.DummyTable;
import com.example.ddmdemo.respository.DummyRepository;
import com.example.ddmdemo.service.interfaces.FileService;
import com.example.ddmdemo.service.interfaces.IndexingService;
import jakarta.transaction.Transactional;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.RequiredArgsConstructor;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.tika.Tika;
import org.apache.tika.language.detect.LanguageDetector;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.data.geo.Point;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;

@Service
@RequiredArgsConstructor
public class IndexingServiceImpl implements IndexingService {

    private final DummyIndexRepository dummyIndexRepository;

    private final DummyRepository dummyRepository;

    private final FileService fileService;

    private final LanguageDetector languageDetector;

    private static final Logger LOG = Logger.getLogger(SearchController.class.getName());




    @Override
    @Transactional
    public DummyDocumentFileResponseDTO indexDocument(MultipartFile documentFile, boolean isContract) {
        var newEntity = new DummyTable();
        var newIndex = new DummyIndex();

        var title = Objects.requireNonNull(documentFile.getOriginalFilename()).split("\\.")[0];
        newIndex.setTitle(title);
        newEntity.setTitle(title);

        var documentContent = extractDocumentContent(documentFile);
        if (isContract){
            System.out.println(documentContent);
            String line = documentContent.split("\n")[0];
            String line1 = documentContent.split("\n")[1];
            String line2 = documentContent.split("\n")[2];
            int num = documentContent.split("\n").length-3;
            String line3 = documentContent.split("\n")[num];

            String patternString = "Uprava za\\s+(.*)";
            Pattern pattern = Pattern.compile(patternString);
            Matcher matcher = pattern.matcher(line);
            if (matcher.find()) {
                String governmentName = matcher.group(1).trim();
                newIndex.setGovernmentName(governmentName);
            }
            String patternString2 = "Nivo uprave:\\s+(.*)";
            Pattern pattern2 = Pattern.compile(patternString2);
            Matcher matcher2 = pattern2.matcher(line1);
            if (matcher2.find()) {
                String governmentLevel = matcher2.group(1).trim();
                newIndex.setGovernmentLevel(governmentLevel);
            }
            String patternString3 = "GRAD\\)\\s+(.*?)\\s+\\(format:";
            Pattern pattern3 = Pattern.compile(patternString3);
            Matcher matcher3 = pattern3.matcher(line2);
            if (matcher3.find()) {
                String governmentAddress = matcher3.group(1).trim();
                newIndex.setAddress(governmentAddress);
                GeoPoint point = getCoordinates(governmentAddress);
                if (point != null){
                    newIndex.setGovernmentCoordinate(point);
                }
            }
            String[] names = line3.split(" ");
            String[] filteredNames = Arrays.stream(names)
                    .filter(s -> !s.isEmpty() && !s.contains("\r"))
                    .toArray(String[]::new);
            newIndex.setSignatoryName(filteredNames[filteredNames.length-2].trim());
            newIndex.setSignatoryLastname(filteredNames[filteredNames.length-1].trim());
            String patternString4 = "\\s+(.*)\\(format:";
            Pattern pattern4 = Pattern.compile(patternString4);
            Matcher matcher4 = pattern4.matcher(line1);
            if (matcher4.find()) {
                String clientCity = matcher4.group(1).trim();
                LOG.info("STATISTIC-LOG clientCity=" + clientCity.split(",")[clientCity.split(",").length - 1].trim() + ",signatoryName=" + newIndex.getSignatoryName() + " " + newIndex.getSignatoryLastname() + ",governmentName=" + newIndex.getGovernmentName());
            }
        }

        if (detectLanguage(documentContent).equals("SR")) {
            newIndex.setContentSr(documentContent);
        } else {
            newIndex.setContentEn(documentContent);
        }
        newEntity.setTitle(title);

        var serverFilename = fileService.store(documentFile, UUID.randomUUID().toString());
        newIndex.setServerFilename(serverFilename);
        newEntity.setServerFilename(serverFilename);

        newEntity.setMimeType(detectMimeType(documentFile));
        var savedEntity = dummyRepository.save(newEntity);

        newIndex.setDatabaseId(savedEntity.getId());
        dummyIndexRepository.save(newIndex);

        DummyDocumentFileResponseDTO dto = new DummyDocumentFileResponseDTO(serverFilename, newIndex.getSignatoryName(), newIndex.getSignatoryLastname(), newIndex.getGovernmentName(), newIndex.getGovernmentLevel(), newIndex.getAddress(), documentContent);
        return dto;
    }

    private GeoPoint getCoordinates(String address){
        String encodedAddress = URLEncoder.encode(address, StandardCharsets.UTF_8);
        String requestURL = "https://nominatim.openstreetmap.org/search?q=" + encodedAddress + "&format=json&limit=1";
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(requestURL))
                .header("User-Agent", "Example Geocode Request") // Nominatim requires a user agent header
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println(response.body());
            JSONArray results = new JSONArray(response.body());
            if (results.length() > 0) {
                JSONObject firstResult = results.getJSONObject(0);
                double latitude = Double.parseDouble(firstResult.getString("lat"));
                double longitude = Double.parseDouble(firstResult.getString("lon"));
                return new GeoPoint(latitude, longitude);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String extractDocumentContent(MultipartFile multipartPdfFile) {
        String documentContent;
        try (var pdfFile = multipartPdfFile.getInputStream()) {
            var pdDocument = PDDocument.load(pdfFile);
            var textStripper = new PDFTextStripper();
            documentContent = textStripper.getText(pdDocument);
            pdDocument.close();
        } catch (IOException e) {
            throw new LoadingException("Error while trying to load PDF file content.");
        }

        return documentContent;
    }

    private String detectLanguage(String text) {
        var detectedLanguage = languageDetector.detect(text).getLanguage().toUpperCase();
        if (detectedLanguage.equals("HR") || detectedLanguage.equals("EN")) {
            detectedLanguage = "SR";
        }

        return detectedLanguage;
    }

    private String detectMimeType(MultipartFile file) {
        var contentAnalyzer = new Tika();

        String trueMimeType;
        String specifiedMimeType;
        try {
            trueMimeType = contentAnalyzer.detect(file.getBytes());
            specifiedMimeType =
                Files.probeContentType(Path.of(Objects.requireNonNull(file.getOriginalFilename())));
        } catch (IOException e) {
            throw new StorageException("Failed to detect mime type for file.");
        }

        if (!trueMimeType.equals(specifiedMimeType) &&
            !(trueMimeType.contains("zip") && specifiedMimeType.contains("zip"))) {
            throw new StorageException("True mime type is different from specified one, aborting.");
        }

        return trueMimeType;
    }
}