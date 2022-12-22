package gobang.game;

import gobang.adapter.CommunicationAdapter;
import gobang.adapter.LocalGameAdapter;
import gobang.entity.ActionParam;
import gobang.entity.Board;
import gobang.entity.Vector2D;
import gobang.enums.ChessType;
import gobang.enums.GameEvent;
import gobang.network.ClientOnline;
import gobang.player.Player;
import gobang.ui.BoardPanel;
import gobang.ui.ControlPanel;
import gobang.ui.MainFrame;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import static gobang.entity.Board.HEIGHT;
import static gobang.entity.Board.WIDTH;
import static gobang.game.GameServer.REMOTE_ID;

@Getter
@Setter
@Slf4j
public class GameClient extends AbstractGameEventHandler {

    private CommunicationAdapter communicationAdapter;
    private boolean isTurn;
    private Integer playerId;
    private ChessType currentColor;

    private BoardPanel boardPanel;
    private ControlPanel controlPanel;

    private ClientOnline clientOnline;

    public GameClient() {
        this.gameContext = new GameContext();
        this.isTurn = false;
        this.clientOnline = new ClientOnline();
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
        controlPanel.onPlayerJoin(player);
    }

    public void onPlayerPrepare(int playerId) {
        Player player = gameContext.getPlayers().get(playerId);
        player.setPrepared(true);
        ChessType type = gameContext.getPlayers().get(playerId).getType();
        controlPanel.onPlayerPrepare(type);
    }

    public void onTurnStart(int playerId) {
        log.info("PlayerTurn = " + playerId + " onTurnStart");
        if (playerId == this.playerId) {
            isTurn = true;
            controlPanel.onSelfTurnStart();
        }
        controlPanel.onTurnStart();
    }

    public void onTurnEnd(int playerId, Vector2D position) {
        log.info("PlayerTurn = " + playerId + " onTurnEnd");
        if (playerId == this.playerId) {
            isTurn = false;
            controlPanel.onSelfTurnEnd();
        }
        ChessType type = gameContext.getPlayers().get(playerId).getType();
        boardPanel.onTurnEnd(type, position);
    }

    public void onPlayerSurrender(int playerId) {
        log.info("Player " + playerId + " surrender");
    }

    public void onGameResult(int playerId) {
        log.info("Player " + playerId + " win");
//        if (communicationAdapter instanceof LocalGameAdapter) {
//            // 本地服务器直接reset，防止出现异步问题
//            reset();
//        }
        boardPanel.onGameResult();
        controlPanel.onGameResult();
        if (playerId == this.playerId) {
            MainFrame.setInfoMsg("您赢了！");
        } else {
            MainFrame.setInfoMsg("您输了！");
        }
    }

    public void onColorChange(Player player) {
        gameContext.getPlayers().get(player.getPlayerId()).setType(player.getType());
    }

    public void onSendId(int playerId) {
        log.info("PlayerId = " + playerId);
        this.playerId = playerId;
    }

    public void onGameReset() {
        log.info("the game is reset");
        Player player = gameContext.getPlayers().get(REMOTE_ID);
        if (player != null) {
            player.setPrepared(false);
        }


        // 清空棋盘
        Board board = gameContext.getBoard();
        for (int i = 0; i < HEIGHT; i++) {
            for (int j = 0; j < WIDTH; j++) {
                board.getChess()[i][j] = null;
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void playerChess(Vector2D position) {
        communicate(GameEvent.TURN_END, new ActionParam(this.playerId, position));
    }

    public void surrender() {
        communicate(GameEvent.PLAYER_SURRENDER, new ActionParam(this.playerId, null));
    }

    public void prepare() {
        communicate(GameEvent.PREPARE, new ActionParam(this.playerId, null));
    }

    public void reset() {
        communicate(GameEvent.GAME_RESET, null);
    }

}
