package com.sequenceiq.common.model;

import java.util.Arrays;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public enum CloudStorageCdpService {

    ZEPPELIN_NOTEBOOK_S3,
    ZEPPELIN_NOTEBOOK,
    YARN_LOG,
    HIVE_METASTORE_WAREHOUSE,
    HIVE_METASTORE_EXTERNAL_WAREHOUSE,
    RANGER_AUDIT;

    @JsonIgnore
    public static String typeListing() {
        return Arrays.stream(CloudStorageCdpService.values()).map(Enum::name).collect(Collectors.joining(", "));
    }
}