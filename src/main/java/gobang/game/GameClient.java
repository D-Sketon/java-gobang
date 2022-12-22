package gobang.game;

import gobang.adapter.CommunicationAdapter;
import gobang.entity.ActionParam;
import gobang.entity.Player;
import gobang.entity.Vector2D;
import gobang.enums.GameEvent;
import gobang.network.ClientOnline;
import gobang.ui.BoardPanel;
import gobang.ui.ControlPanel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Setter
@Slf4j
public class GameClient extends AbstractGameEventHandler {

    private CommunicationAdapter communicationAdapter;
    private boolean isTurn;
    private Integer playerId;

    private BoardPanel boardPanel;
    private ControlPanel controlPanel;

    private ClientOnline clientOnline;

    public GameClient() {
        this.gameContext = new GameContext();
        this.isTurn = false;
        this.clientOnline = new ClientOnline();
        // maybe todo
    }

    public GameClient(BoardPanel boardPanel, ControlPanel controlPanel) {
        this();
        this.boardPanel = boardPanel;
        this.controlPanel = controlPanel;
    }

    private void communicate(GameEvent event, Object data) {
        log.info("PlayerTurn = " + playerId + " communicates to Server with " + event + " as " + data);
        communicationAdapter.sendEvent(event, data);
    }


    public void onPlayerJoin(Player player) {
        log.info("PlayerId = " + player.getType() + " onPlayerJoin");
        gameContext.getPlayers().put(player.getPlayerId(), player);
    }

    public void onTurnStart(int playerId) {
        log.info("PlayerTurn = " + playerId + " onTurnStart");
        if (playerId == this.playerId) {
            isTurn = true;
        }
    }

    public void onTurnEnd(int playerId, Vector2D position) {
        log.info("PlayerTurn = " + playerId + " onTurnEnd");
        if (playerId == this.playerId) {
            isTurn = false;
        }
    }

    public void onPlayerSurrender(int playerId) {

    }

    public void onGameResult(int playerId) {

    }

    public void onColorReset(Player player) {
        if (this.playerId == player.getPlayerId()) {
            gameContext.getPlayers().get(playerId).setType(player.getType());
            // 回调函数
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void playerChess(Vector2D position) {
        communicate(GameEvent.TURN_END, new ActionParam(this.playerId, position));
    }

    public void surrender() {
        communicate(GameEvent.PLAYER_SURRENDER, new ActionParam(this.playerId, null));
    }

}
