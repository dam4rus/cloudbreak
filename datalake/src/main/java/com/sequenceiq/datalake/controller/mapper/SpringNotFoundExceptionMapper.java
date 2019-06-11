package com.sequenceiq.datalake.controller.mapper;

import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.Provider;

import org.springframework.stereotype.Component;

import com.sequenceiq.cloudbreak.exception.NotFoundException;

@Provider
@Component
public class SpringNotFoundExceptionMapper extends BaseExceptionMapper<NotFoundException> {

    @Override
    Status getResponseStatus() {
        return Status.NOT_FOUND;
    }

    @Override
    Class<NotFoundException> getExceptionType() {
        return NotFoundException.class;
    }

}