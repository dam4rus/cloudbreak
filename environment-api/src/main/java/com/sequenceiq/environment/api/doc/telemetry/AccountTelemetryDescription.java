package com.sequenceiq.environment.api.doc.telemetry;

public class AccountTelemetryDescription {

    public static final String GET = "get account specific telemetry settings";
    public static final String GET_NOTES = "Gather telemetry related settings (features and anonymization rules) " +
            "these are used as global settings for environments";
    public static final String GET_DEFAULT = "get default account specific telemetry settings";
    public static final String GET_DEFAULT_NOTES = "Gather default telemetry account level settings - default rules contains email/card number regex patterns";
    public static final String GET_FEATURES = "get account specific telemetry settings";
    public static final String GET_FEATURES_NOTES = "In CDP";
    public static final String POST = "update account specific telemetry settings";
    public static final String POST_NOTES = "Update account level telemetry settings - will override already existing settings and archive old one";
    public static final String TEST = "test anonymization pattern";
    public static final String TEST_NOTES = "Testing anonymization patterns - check the patterns are valid and found matches";

    private AccountTelemetryDescription() {
    }
}
