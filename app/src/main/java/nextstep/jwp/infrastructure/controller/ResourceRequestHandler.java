package nextstep.jwp.infrastructure.controller;

import nextstep.jwp.model.web.ContentType;
import nextstep.jwp.model.web.ResourceFinder;
import nextstep.jwp.model.web.StatusCode;
import nextstep.jwp.model.web.request.CustomHttpRequest;
import nextstep.jwp.model.web.response.CustomHttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;

public class ResourceRequestHandler implements HttpRequestHandler {

    private static final Logger log = LoggerFactory.getLogger(ResourceRequestHandler.class);
    private static final String RESOURCE_DELIMITER = "\\.";

    @Override
    public void service(CustomHttpRequest request, CustomHttpResponse response) throws Exception {
        log.debug("Http Request - ResourceRequestHandler");
        String contentType = ContentType.findContentType(
                request.getUri().split(RESOURCE_DELIMITER)[1]
        );

        String resource = ResourceFinder.resource(request.getUri());
        response.setStatusLine(StatusCode.OK, request.getVersionOfProtocol());
        headers(response, resource.getBytes().length, contentType);
        response.setResponseBody(resource);
    }

    private void headers(CustomHttpResponse response, int resourceLength, String contentType) {
        response.addHeaders("Content-Type", Collections.singletonList(contentType));
        response.addHeaders("Content-Length", Collections.singletonList(resourceLength + ""));
    }
}
