package m.khokhlov.services;

import ca.watier.echesscommon.enums.ChessEventMessage;
import ca.watier.echesscommon.enums.Side;
import ca.watier.echesscommon.interfaces.WebSocketService;
import ca.watier.echesscommon.utils.Assert;
import ca.watier.echesscommon.responses.ChessEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;


@Service
public class WebSocketServiceImpl implements WebSocketService {

    private static final String TOPIC = "/topic/";
    private final SimpMessagingTemplate template;

    @Autowired
    public WebSocketServiceImpl(SimpMessagingTemplate template) {
        this.template = template;
    }

    public void fireSideEvent(String uuid, Side side, ChessEventMessage evtMessage, String message) {
        Assert.assertNotNull(side, evtMessage);
        Assert.assertNotEmpty(uuid);
        Assert.assertNotEmpty(message);

        template.convertAndSend(TOPIC + uuid + '/' + side, new ChessEvent(evtMessage, message));
    }

    @Override
    public void fireSideEvent(String uuid, Side side, ChessEventMessage evtMessage, String message, Object obj) {
        Assert.assertNotNull(side, evtMessage);
        Assert.assertNotEmpty(uuid);
        Assert.assertNotEmpty(message);

        ChessEvent payload = new ChessEvent(evtMessage, message);
        payload.setObj(obj);
        template.convertAndSend(TOPIC + uuid + '/' + side, payload);
    }

    public void fireUiEvent(String uiUuid, ChessEventMessage evtMessage, String message) {
        template.convertAndSend(TOPIC + uiUuid, new ChessEvent(evtMessage, message));
    }


    public void fireGameEvent(String uuid, ChessEventMessage evtMessage, Object message) {
        template.convertAndSend(TOPIC + uuid, new ChessEvent(evtMessage, message));
    }

    @Override
    public void fireGameEvent(String uuid, ChessEventMessage refreshBoard) {
        template.convertAndSend(TOPIC + uuid, new ChessEvent(refreshBoard));
    }
}
