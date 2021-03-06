package com.sequenceiq.it.cloudbreak.action.sdx;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sequenceiq.flow.api.model.FlowLogResponse;
import com.sequenceiq.it.cloudbreak.SdxClient;
import com.sequenceiq.it.cloudbreak.action.Action;
import com.sequenceiq.it.cloudbreak.context.TestContext;
import com.sequenceiq.it.cloudbreak.dto.sdx.SdxTestDto;

public class SdxSetFlowChainIdAction implements Action<SdxTestDto, SdxClient> {

    private static final Logger LOGGER = LoggerFactory.getLogger(SdxSetFlowChainIdAction.class);

    @Override
    public SdxTestDto action(TestContext testContext, SdxTestDto testDto, SdxClient client) throws Exception {
        FlowLogResponse lastFlowByResourceName = client.getSdxClient()
                .flowEndpoint()
                .getLastFlowByResourceName(testDto.getName());
        testDto.setLastKnownFlowChainId(lastFlowByResourceName.getFlowChainId());
        testDto.setLastKnownFlowId(lastFlowByResourceName.getFlowId());
        return testDto;
    }
}
