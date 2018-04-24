package m.khokhlov.controllers;

import m.khokhlov.services.GameService;
import ca.watier.echesscommon.enums.CasePosition;
import ca.watier.echesscommon.enums.Side;
import ca.watier.echesscommon.utils.Assert;
import ca.watier.echesscommon.utils.SessionUtils;
import ca.watier.echesscommon.responses.BooleanResponse;
import ca.watier.echesscommon.responses.DualValueResponse;
import ca.watier.echesscommon.responses.StringResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("/api/game")
public class GameController {
    private final GameService gameService;

    @Autowired
    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    /**
     * Create a new game
     *
     * @param side
     * @param againstComputer
     * @param observers
     * @param session
     * @return
     */
    @RequestMapping(path = "/create/1", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public StringResponse createNewGame(Side side, boolean againstComputer, boolean observers, String specialGamePieces, HttpSession session) {
        UUID newGame = gameService.createNewGame(SessionUtils.getPlayer(session), specialGamePieces, side, againstComputer, observers);
        Assert.assertNotNull(newGame);

        return new StringResponse(newGame.toString());
    }


    /**
     * Checks if the move is valid
     *
     * @param from
     * @param to
     * @param uuid
     * @param session
     * @return
     */
    @RequestMapping(path = "/move/1", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public BooleanResponse movePieceOfPlayer(CasePosition from, CasePosition to, String uuid, HttpSession session) {
        return gameService.movePiece(from, to, uuid, SessionUtils.getPlayer(session));
    }

    /**
     * Return a list of position that the piece can moves
     *
     * @param from
     * @param uuid
     * @param session
     * @return
     */
    @RequestMapping(path = "/moves/1", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<String> getMovesOfAPiece(CasePosition from, String uuid, HttpSession session) {
        return gameService.getAllAvailableMoves(from, uuid, SessionUtils.getPlayer(session));
    }


    /**
     * Used for the pawn promotion
     *
     * @param uuid
     * @param session
     * @return
     */
    @RequestMapping(path = "/piece/pawn/promotion/1", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public BooleanResponse pawnPromotion(CasePosition to, String uuid, String piece, HttpSession session) {
        return gameService.upgradePiece(to, uuid, piece, SessionUtils.getPlayer(session));
    }


    /**
     * Gets the pieces location
     *
     * @param uuid
     * @param session
     * @return
     */
    @RequestMapping(path = "/pieces/1", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<DualValueResponse> getPieceLocations(String uuid, HttpSession session) {
        return gameService.getPieceLocations(uuid, SessionUtils.getPlayer(session));
    }

    /**
     * Join a game
     *
     * @param uuid
     * @param side
     * @param session
     * @return
     */
    @RequestMapping(path = "/join/1", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public BooleanResponse joinGame(String uuid, Side side, String uiUuid, HttpSession session) {
        return gameService.joinGame(uuid, side, uiUuid, SessionUtils.getPlayer(session));
    }

    /**
     * Change side of a player
     *
     * @param side
     * @param uuid
     * @param session
     * @return
     */
    @RequestMapping(path = "/side/1", method = RequestMethod.POST)
    public BooleanResponse setSideOfPlayer(Side side, String uuid, HttpSession session) {
        return gameService.setSideOfPlayer(SessionUtils.getPlayer(session), side, uuid);
    }
}
