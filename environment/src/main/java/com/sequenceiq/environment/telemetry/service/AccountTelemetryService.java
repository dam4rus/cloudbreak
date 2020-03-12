package com.sequenceiq.environment.telemetry.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import com.sequenceiq.cloudbreak.auth.altus.Crn;
import com.sequenceiq.common.api.telemetry.model.AnonymizationRule;
import com.sequenceiq.common.api.telemetry.model.Features;
import com.sequenceiq.environment.telemetry.domain.AccountTelemetry;
import com.sequenceiq.environment.telemetry.repository.AccountTelemetryRepository;

@Service
public class AccountTelemetryService {

    private static final String EMAIL_PATTERN = "\\b([A-Za-z0-9]|[A-Za-z0-9][A-Za-z0-9\\-\\._]*[A-Za-z0-9])@(([A-Za-z0-9]|[A-Za-z][A-Za-z0-9\\-]*[A-Za-z0-9])\\.)+([A-Za-z0-9]|[A-Za-z0-9][A-Za-z0-9\\-]*[A-Za-z0-9])\\b";

    private static final String EMAIL_REPLACEMENT = "email@redacted.host";

    private static final String CREDIT_CARD_PATTERN = "\\d{4}[^\\w]\\d{4}[^\\w]\\d{4}[^\\w]\\d{4}";

    private static final String CREDIT_CARD_REPLACEMENT = "XXXX-XXXX-XXXX-XXXX";

    private static final String SSN_PATTERN = "\\d{3}[^\\w]\\d{2}[^\\w]\\d{4}";

    private static final String SSN_REPLACEMENT = "XXX-XX-XXXX";

    private final AccountTelemetryRepository accountTelemetryRepository;

    public AccountTelemetryService(AccountTelemetryRepository accountTelemetryRepository) {
        this.accountTelemetryRepository = accountTelemetryRepository;
    }

    public AccountTelemetry create(AccountTelemetry telemetry, String accountId) {
        try {
            accountTelemetryRepository.archiveAll(accountId);
            telemetry.setResourceCrn(createCRN(accountId));
            telemetry.setAccountId(accountId);
            telemetry.setArchived(false);
            accountTelemetryRepository.save(telemetry);
            return telemetry;
        } catch (DataIntegrityViolationException e) {
            throw new AccessDeniedException("Access denied", e);
        }
    }

    public AccountTelemetry getOrDefault(String accountId) {
        try {
            Optional<AccountTelemetry> telemetry = accountTelemetryRepository.findByAccountId(accountId);
            return telemetry.orElse(createDefaultAccuontTelemetry());
        } catch (DataIntegrityViolationException e) {
            throw new AccessDeniedException("Access denied", e);
        }
    }

    public Features updateFeatures(String accountId, Features newFeatures) {
        try {
            Optional<AccountTelemetry> telemetryOpt = accountTelemetryRepository.findByAccountId(accountId);
            AccountTelemetry telemetry = telemetryOpt.orElse(createDefaultAccuontTelemetry());
            Features features = telemetry.getFeatures();
            Features finalFeatures = null;
            if (features != null && newFeatures != null) {
                finalFeatures = new Features();
                finalFeatures.setClusterLogsCollection(features.getClusterLogsCollection());
                finalFeatures.setWorkloadAnalytics(features.getWorkloadAnalytics());
                if (newFeatures.getClusterLogsCollection() != null) {
                    finalFeatures.setClusterLogsCollection(newFeatures.getClusterLogsCollection());
                }
                if (newFeatures.getWorkloadAnalytics() != null) {
                    finalFeatures.setWorkloadAnalytics(newFeatures.getWorkloadAnalytics());
                }
            }
            telemetry.setFeatures(finalFeatures);
            return create(telemetry, accountId).getFeatures();
        } catch (DataIntegrityViolationException e) {
            throw new AccessDeniedException("Access denied", e);
        }
    }

    public AccountTelemetry createDefaultAccuontTelemetry() {
        AccountTelemetry defaultTelemetry = new AccountTelemetry();
        List<AnonymizationRule> defaultRules = new ArrayList<>();

        AnonymizationRule creditCardWithSepRule = new AnonymizationRule();
        creditCardWithSepRule.setValue(CREDIT_CARD_PATTERN);
        creditCardWithSepRule.setReplacement(CREDIT_CARD_REPLACEMENT);

        AnonymizationRule ssnWithSepRule = new AnonymizationRule();
        ssnWithSepRule.setValue(SSN_PATTERN);
        ssnWithSepRule.setReplacement(SSN_REPLACEMENT);

        AnonymizationRule emailRule = new AnonymizationRule();
        ssnWithSepRule.setValue(EMAIL_PATTERN);
        ssnWithSepRule.setReplacement(EMAIL_REPLACEMENT);

        defaultRules.add(creditCardWithSepRule);
        defaultRules.add(ssnWithSepRule);
        defaultRules.add(emailRule);

        Features defaultFeatures = new Features();
        defaultFeatures.addClusterLogsCollection(false);
        defaultTelemetry.setRules(defaultRules);
        defaultTelemetry.setFeatures(defaultFeatures);
        return defaultTelemetry;
    }

    private String createCRN(String accountId) {
        return Crn.builder()
                .setService(Crn.Service.ACCOUNTTELEMETRY)
                .setAccountId(accountId)
                .setResourceType(Crn.ResourceType.ACCOUNT_TELEMETRY)
                .setResource(UUID.randomUUID().toString())
                .build()
                .toString();
    }
}
