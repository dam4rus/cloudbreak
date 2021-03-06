package com.sequenceiq.environment.api.v1.tags.endpoint;

import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.sequenceiq.cloudbreak.jerseyclient.RetryAndMetrics;
import com.sequenceiq.environment.api.doc.tag.TagDescription;
import com.sequenceiq.environment.api.v1.tags.model.request.AccountTagRequests;
import com.sequenceiq.environment.api.v1.tags.model.response.AccountTagResponses;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RetryAndMetrics
@Path("/v1/tags")
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = "/v1/tags", protocols = "http,https", consumes = MediaType.APPLICATION_JSON)
public interface AccountTagEndpoint {

    @GET
    @Path("")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = TagDescription.GET, produces = MediaType.APPLICATION_JSON, notes = TagDescription.GET_NOTES, nickname = "listTagsV1")
    AccountTagResponses list();

    @PUT
    @Path("")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = TagDescription.PUT, produces = MediaType.APPLICATION_JSON, notes = TagDescription.PUT_NOTES, nickname = "putTagsV1")
    AccountTagResponses put(@Valid AccountTagRequests request);
}
