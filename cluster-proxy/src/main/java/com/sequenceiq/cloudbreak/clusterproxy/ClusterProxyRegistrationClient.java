package com.sequenceiq.cloudbreak.clusterproxy;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sequenceiq.cloudbreak.common.json.JsonUtil;

@Component
public class ClusterProxyRegistrationClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClusterProxyRegistrationClient.class);

    private RestTemplate restTemplate;

    @Inject
    private ClusterProxyConfiguration clusterProxyConfiguration;

    @Autowired
    ClusterProxyRegistrationClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public ConfigRegistrationResponse registerConfig(ConfigRegistrationRequest configRegistrationRequest) {
        String registerConfigUrl = clusterProxyConfiguration.getRegisterConfigUrl();
        try {
            LOGGER.debug("Registering cluster proxy configuration: {}", configRegistrationRequest);
            ResponseEntity<ConfigRegistrationResponse> response = restTemplate.postForEntity(registerConfigUrl,
                    requestEntity(configRegistrationRequest), ConfigRegistrationResponse.class);

            LOGGER.debug("Cluster Proxy config registration response: {}", response);
            return response.getBody();
        } catch (Exception e) {
            String message = String.format("Error registering proxy configuration for cluster '%s' with Cluster Proxy. URL: '%s'",
                    configRegistrationRequest.getClusterCrn(), registerConfigUrl);
            LOGGER.error(message, e);
            throw new ClusterProxyException(message, e);

        }
    }

    public void updateConfig(ConfigUpdateRequest configUpdateRequest) {
        String updateConfigUrl = clusterProxyConfiguration.getUpdateConfigUrl();
        try {
            LOGGER.debug("Updating cluster proxy configuration: {}", configUpdateRequest);
            ResponseEntity<ConfigRegistrationResponse> response = restTemplate.postForEntity(updateConfigUrl,
                    requestEntity(configUpdateRequest), ConfigRegistrationResponse.class);
            LOGGER.debug("Cluster Proxy config update response: {}", response);
        } catch (Exception e) {
            String message = String.format("Error updating configuration for cluster '%s' with Cluster Proxy. URL: '%s'",
                    configUpdateRequest.getClusterCrn(), updateConfigUrl);
            LOGGER.error(message, e);
            throw new ClusterProxyException(message, e);
        }
    }

    public void deregisterConfig(String clusterIdentifier) {
        String removeConfigUrl = clusterProxyConfiguration.getRemoveConfigUrl();
        try {
            LOGGER.debug("Removing cluster proxy configuration for cluster identifier: {}", clusterIdentifier);
            ResponseEntity<ConfigRegistrationResponse> response = restTemplate.postForEntity(removeConfigUrl,
                    requestEntity(new ConfigDeleteRequest(clusterIdentifier)), ConfigRegistrationResponse.class);
            LOGGER.debug("Cluster proxy deregistration response: {}", response);
        } catch (Exception e) {
            String message = String.format("Error de-registering proxy configuration for cluster identifier '%s' from Cluster Proxy. URL: '%s'",
                    clusterIdentifier, removeConfigUrl);
            LOGGER.error(message, e);
            throw new ClusterProxyException(message, e);
        }
    }

    private HttpEntity<String> requestEntity(ConfigRegistrationRequest proxyConfigRequest) throws JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(JsonUtil.writeValueAsString(proxyConfigRequest), headers);
    }

    private HttpEntity<String> requestEntity(ConfigUpdateRequest proxyConfigRequest) throws JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(JsonUtil.writeValueAsString(proxyConfigRequest), headers);
    }

    private HttpEntity<String> requestEntity(ConfigDeleteRequest proxyConfigRequest) throws JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(JsonUtil.writeValueAsString(proxyConfigRequest), headers);
    }
}