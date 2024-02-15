package com.example.ddmdemo.indexmodel;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.elasticsearch.annotations.*;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "dummy_index")
@Setting(settingPath = "/configuration/serbian-analyzer-config.json")
public class DummyIndex {
    public static final String SERBIAN_ANALYZER = "serbian_simple";

    @Id
    private String id;

    @Field(type = FieldType.Text, store = true, name = "title")
    private String title;

    @Field(type = FieldType.Text, store = true, name = "content_sr", analyzer = "serbian_simple", searchAnalyzer = "serbian_simple")
    private String contentSr;

    @Field(type = FieldType.Text, store = true, name = "content_en", analyzer = "english", searchAnalyzer = "english")
    private String contentEn;

    @Field(type = FieldType.Text, store = true, name = "signatory_name")
    private String signatoryName;

    @Field(type = FieldType.Text, store = true, name = "signatory_lastname")
    private String signatoryLastname;

    @Field(type = FieldType.Text, store = true, name = "government_name")
    private String governmentName;

    @Field(type = FieldType.Text, store = true, name = "government_level")
    private String governmentLevel;

    @Field(type = FieldType.Text, index = false, store = true)
    private String address;

    @GeoPointField
    private GeoPoint governmentCoordinate;

    @Field(type = FieldType.Text, store = true, name = "server_filename", index = false)
    private String serverFilename;

    @Field(type = FieldType.Integer, store = true, name = "database_id")
    private Integer databaseId;

    @Field(type = FieldType.Text, store = true, analyzer = "serbian_simple", searchAnalyzer = "serbian_simple")
    private String lawText;

    @Field(type = FieldType.Text, store = true, analyzer = "serbian_simple", searchAnalyzer = "serbian_simple")
    private String lawFilename;

    @Field(type = FieldType.Text, store = true, analyzer = "serbian_simple", searchAnalyzer = "serbian_simple")
    private String contractText;

    @Field(type = FieldType.Text, store = true, analyzer = "serbian_simple", searchAnalyzer = "serbian_simple")
    private String contractFilename;

}
