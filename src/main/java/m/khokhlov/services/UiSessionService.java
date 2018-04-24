package m.khokhlov.services;

import ca.watier.echesscommon.enums.ChessEventMessage;
import ca.watier.echesscommon.interfaces.WebSocketService;
import ca.watier.echesscommon.sessions.Player;
import ca.watier.echesscommon.utils.Assert;
import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static ca.watier.echesscommon.utils.CacheConstants.CACHE_UI_SESSION_NAME;
import static ca.watier.echesscommon.utils.Constants.REQUESTED_SESSION_ALREADY_DEFINED;
import static ca.watier.echesscommon.utils.Constants.THE_CLIENT_LOST_THE_CONNECTION;

@Service
public class UiSessionService {

    private final Cache<UUID, Player> CACHE_UI;
    private final WebSocketService webSocketService;

    @Autowired
    public UiSessionService(WebSocketService webSocketService, CacheConfigurationBuilder<UUID, Player> uuidPlayerCacheConfiguration) {
        CacheManager CACHE_MANAGER = CacheManagerBuilder.newCacheManagerBuilder()
                .withCache(CACHE_UI_SESSION_NAME, uuidPlayerCacheConfiguration)
                .build();

        CACHE_MANAGER.init();
        CACHE_UI = CACHE_MANAGER.getCache(CACHE_UI_SESSION_NAME, UUID.class, Player.class);
        this.webSocketService = webSocketService;
    }

    public String createNewSession(Player player) {
        String uuidAsString = null;
        UUID uuid = UUID.randomUUID();

        if (!isUiSessionActive(uuid)) {
            uuidAsString = uuid.toString();
            player.addUiSession(uuid);
            CACHE_UI.put(uuid, player);
        } else {
            webSocketService.fireUiEvent(uuid.toString(), ChessEventMessage.UI_SESSION_ALREADY_INITIALIZED, REQUESTED_SESSION_ALREADY_DEFINED);
        }

        return uuidAsString;
    }

    public boolean isUiSessionActive(UUID uuid) {
        return CACHE_UI.containsKey(uuid);
    }

    public void refresh(String uuid) {
        Assert.assertNotEmpty(uuid);
        Player player = CACHE_UI.get(UUID.fromString(uuid));

        if (player == null) {
            webSocketService.fireUiEvent(uuid, ChessEventMessage.UI_SESSION_EXPIRED, THE_CLIENT_LOST_THE_CONNECTION);
        }
    }

    public Player getItemFromCache(UUID uuid) {
        return CACHE_UI.get(uuid);
    }
}
