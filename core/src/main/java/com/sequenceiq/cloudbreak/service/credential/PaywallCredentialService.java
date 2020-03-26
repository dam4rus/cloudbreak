package com.sequenceiq.cloudbreak.service.credential;

import static java.util.Collections.singletonMap;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.sequenceiq.cloudbreak.orchestrator.model.SaltPillarProperties;

@Component
public class PaywallCredentialService {

    private static final int SHIFT = 3;

    @Value("${cb.paywall.username:}")
    private String paywallUserName;

    @Value("${cb.paywall.password:}")
    private String paywallPassword;

    public String addCredentialForUrl(String url) {
        if (paywallCredentialAvailable()) {
            StringBuilder stringBuilder = new StringBuilder(url);
            stringBuilder.insert(url.indexOf("://") + SHIFT, String.format("%s:%s@", paywallUserName, paywallPassword));
            url = stringBuilder.toString();
        }
        return url;
    }

    public boolean paywallCredentialAvailable() {
        return !paywallUserName.isEmpty() && !paywallPassword.isEmpty();
    }

    public void getPaywallCredential(Map<String, SaltPillarProperties> servicePillar) {
        servicePillar.put("paywall", new SaltPillarProperties("/hdp/paywall.sls", singletonMap("paywall", createCredential())));
    }

    private Map<String, String> createCredential() {
        return Map.of(
                "paywallUser", paywallUserName,
                "paywallPassword", paywallPassword);
    }
}
