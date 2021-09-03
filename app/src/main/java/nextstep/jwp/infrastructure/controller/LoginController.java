package nextstep.jwp.infrastructure.controller;

import nextstep.jwp.model.User;
import nextstep.jwp.model.web.ResourceFinder;
import nextstep.jwp.model.web.StatusCode;
import nextstep.jwp.model.web.request.CustomHttpRequest;
import nextstep.jwp.model.web.response.CustomHttpResponse;
import nextstep.jwp.model.web.service.LoginService;
import nextstep.jwp.model.web.sessions.HttpSession;
import nextstep.jwp.model.web.sessions.HttpSessions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class LoginController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);
    private static final String LOGIN_SUCCESS_URL = "/index.html";
    private static final String LOGIN_FAILURE_URL = "/401.html";
    private final LoginService loginService;

    public LoginController(LoginService loginService) {
        this.loginService = loginService;
    }

    @Override
    protected void doGet(CustomHttpRequest request, CustomHttpResponse response) throws Exception {
        log.debug("Http Request - GET /login");
        String resource = ResourceFinder.resource(request.getUri() + ".html");

        if (existsUserSession(request)) {
            log.debug("session activated - login passed");
            response.setStatusLine(StatusCode.FOUND, request.getVersionOfProtocol());
            response.forward(LOGIN_SUCCESS_URL);
            return;
        }

        response.setStatusLine(StatusCode.OK, request.getVersionOfProtocol());
        addContentHeader(response, resource.getBytes().length);
        response.setResponseBody(resource);
    }

    @Override
    protected void doPost(CustomHttpRequest request, CustomHttpResponse response) {
        log.debug("Http Request - POST /login");
        response.setStatusLine(StatusCode.FOUND, request.getVersionOfProtocol());
        String account = request.getBodyValue("account");
        String password = request.getBodyValue("password");

        try {
            User user = loginService.login(account, password);
            String sessionId = UUID.randomUUID().toString();
            HttpSession session = new HttpSession(sessionId);
            session.setAttribute(sessionId, user);
            HttpSessions.addSession(sessionId, session);
            response.addSessionCookieHeader(sessionId);
            response.forward(LOGIN_SUCCESS_URL);
        } catch (Exception e) {
            response.forward(LOGIN_FAILURE_URL);
        }
    }

    private boolean existsUserSession(CustomHttpRequest request) {
        return request.existsSessionIdCookie();
    }
}
