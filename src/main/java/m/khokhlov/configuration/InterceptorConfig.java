package m.khokhlov.configuration;

import m.khokhlov.server.UiSessionHandlerInterceptor;
import m.khokhlov.services.UiSessionService;
import ca.watier.echesscommon.interfaces.WebSocketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class InterceptorConfig extends WebMvcConfigurerAdapter {

    private final UiSessionService uiSessionService;
    private final WebSocketService webSocketService;

    @Autowired
    public InterceptorConfig(UiSessionService uiSessionService, WebSocketService webSocketService) {
        this.uiSessionService = uiSessionService;
        this.webSocketService = webSocketService;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new UiSessionHandlerInterceptor(uiSessionService, webSocketService)).addPathPatterns("/api/game/**");
    }
}
