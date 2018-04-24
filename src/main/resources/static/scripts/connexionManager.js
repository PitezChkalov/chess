const websocketPath = '/websocket';
let wsClient = null;
let wsClientColor = null;
let wsMainClient = null;
let wsPingClient = null;


class ConnexionManager {
    static connectUiEvent(uuid) {
        if (wsMainClient) {
            wsMainClient.unsubscribe();
        }

        wsMainClient = Stomp.over(new SockJS(websocketPath));
        wsMainClient.connect({}, function () {
            wsMainClient.subscribe('/topic/' + uuid, function (greeting) {
                let parsed = JSON.parse(greeting.body);
                let chessEvent = parsed.event;
                let message = parsed.message;

                switch (chessEvent) {
                    case 'UI_SESSION_EXPIRED':
                        window.setInterval(function () {
                            location.reload();
                        }, 10 * 1000);
                        alertify.error(message, 0);
                        break;
                    case 'PLAYER_JOINED':
                        alertify.success(message, 6);
                        break;
                    case 'TRY_JOIN_GAME':
                        alertify.error(message, 0);
                        break;
                }
            });
        });
    }

    static connectPingEvent() {
        if (wsPingClient) {
            wsPingClient.unsubscribe();
        }

        wsPingClient = Stomp.over(new SockJS(websocketPath));
        wsPingClient.connect({}, function () {
            window.setInterval(function () {
                ConnexionManager.sendPing();
            }, 25 * 1000);
        });
    }

    static sendPing() {
        wsPingClient.send("/app/api/ui/ping", {}, JSON.stringify({'uuid': currentUiUuid}));
    }

    static connectSideEvent(uuid, writeToGameLog) {
        if (wsClientColor) {
            wsClientColor.unsubscribe();
        }

        wsClientColor = Stomp.over(new SockJS(websocketPath));
        wsClientColor.connect({}, function () {
            wsClientColor.subscribe('/topic/' + uuid + '/' + selectedColor, function (greeting) {
                let parsed = JSON.parse(greeting.body);
                let chessEvent = parsed.event;
                let message = parsed.message;
                let obj = parsed.obj;

                switch (chessEvent) {
                    case 'PLAYER_TURN':
                        writeToGameLog(message, chessEvent);
                        break;
                    case 'PAWN_PROMOTION':
                        currentPawnPromotion = message;
                        $('#modalPawnPromotion').modal({
                            closable: false
                        }).modal('show');
                        break;
                    case 'KING_CHECK':
                        alertify.warning(message);
                        break;

                }
            });
        });
    }

    static connectGameEvent(uuid, renderBoard, writeToGameLog) {
        if (wsClient) {
            wsClient.unsubscribe();
        }

        wsClient = Stomp.over(new SockJS(websocketPath));
        wsClient.connect({}, function () {
            wsClient.subscribe('/topic/' + uuid, function (greeting) {
                let parsed = JSON.parse(greeting.body);
                let chessEvent = parsed.event;
                let message = parsed.message;

                switch (chessEvent) {
                    case 'MOVE':
                        renderBoard();
                        writeToGameLog(message, chessEvent);
                        break;
                    case 'PLAYER_JOINED':
                        writeToGameLog(message, chessEvent);
                        break;
                    case 'GAME_WON':
                        writeToGameLog(message, chessEvent);
                        break;
                    case 'GAME_WON_EVENT_MOVE':
                        writeToGameLog(message, chessEvent);
                        break;
                    case 'SCORE_UPDATE':
                        updateScore(message);
                        break;
                    case 'REFRESH_BOARD':
                        renderBoard();
                        break;
                    case 'PAWN_PROMOTION':
                        alertify.warning(message);
                        break;
                    case 'KING_CHECKMATE':
                        alertify.warning(message, 5);
                        break;
                }
            });
        });
    }
}
