package m.khokhlov.controllers;

import m.khokhlov.services.UiSessionService;
import ca.watier.echesscommon.pojos.Ping;
import ca.watier.echesscommon.utils.SessionUtils;
import ca.watier.echesscommon.responses.StringResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
@RestController
@RequestMapping("/api/ui")
public class UiController {
    private final UiSessionService uiSessionService;

    @Autowired
    public UiController(UiSessionService uiSessionService) {
        this.uiSessionService = uiSessionService;
    }

    /**
     * Create and bind a ui session to the player
     *
     * @param session
     * @return
     */
    @RequestMapping(path = "/id/1", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public StringResponse createNewGame(HttpSession session) {
        return new StringResponse(uiSessionService.createNewSession(SessionUtils.getPlayer(session)));
    }

    @MessageMapping("/api/ui/ping")
    @SendTo("/topic/ping")
    public void ping(Ping uuid) {
        uiSessionService.refresh(uuid.getUuid());
    }
}
