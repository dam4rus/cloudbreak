package com.sequenceiq.cloudbreak.cloud.event.platform;

import com.sequenceiq.cloudbreak.cloud.event.CloudPlatformRequest;
import com.sequenceiq.cloudbreak.cloud.event.CloudPlatformResult;
import com.sequenceiq.cloudbreak.util.JsonUtil;

public class ResourceDefinitionResult extends CloudPlatformResult {

    private final String definition;

    public ResourceDefinitionResult(CloudPlatformRequest<?> request, String definition) {
        super(request);
        this.definition = definition;
    }

    public ResourceDefinitionResult(String statusReason, Exception errorDetails, CloudPlatformRequest<?> request) {
        super(statusReason, errorDetails, request);
        this.definition = null;
    }

    public String getDefinition() {
        return definition;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ResourceDefinitionResult{");
        sb.append("definition='").append(JsonUtil.minify(definition)).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
