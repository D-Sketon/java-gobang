package gobang.game;

import gobang.adapter.CommunicationAdapter;
import gobang.adapter.LocalGameAdapter;
import gobang.entity.ActionParam;
import gobang.entity.Player;
import gobang.entity.Vector2D;
import gobang.enums.ChessType;
import gobang.enums.GameEvent;
import gobang.network.ServerOnline;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static gobang.enums.ChessType.BLACK;
import static gobang.enums.ChessType.WHITE;

@Slf4j
public class GameServer extends AbstractGameEventHandler {

    @Getter
    private final Map<Integer, CommunicationAdapter> adapterMap;

    @Getter
    private boolean isGameStart;

    private int currentPlayerId;

    private final int lsto = 25;

    private final ServerOnline serverOnline;

    public GameServer() {
        this.gameContext = new GameContext();
        this.adapterMap = new ConcurrentHashMap<>();
        this.isGameStart = false;
        this.currentPlayerId = 0;
        this.serverOnline = new ServerOnline(this);
    }

    /**
     * 开启端口，在局域网内部启动在线服务器
     */
    public void startServer() {
        serverOnline.initNetty();
    }

    public void onLocalJoin(GameEventAware communicator) {
        log.info("New client tries to joinGameLocal...");

        Player player = new Player(0, BLACK);
        gameContext.getPlayers().put(player.getPlayerId(), player);

        LocalGameAdapter adapter = new LocalGameAdapter(communicator);
        adapterMap.put(player.getPlayerId(), adapter);

        broadcast(GameEvent.PLAYER_JOIN, player);
    }

    public void joinGameRemote(CommunicationAdapter adapter) {
        log.info("New client tries to joinGameRemote...");
        Player player = new Player(1, WHITE); // 新加入的玩家默认先白棋

        Player host = gameContext.getPlayers().get(0);
        host.setType(BLACK); // 房主设为黑棋
        onColorReset(host);

        // onLocalPlayer()
        gameContext.getPlayers().put(player.getPlayerId(), player);
        adapterMap.put(player.getPlayerId(), adapter);

        broadcast(GameEvent.PLAYER_JOIN, player);
    }

    public void gameStart() {
        this.isGameStart = true;
        onTurnStart(currentPlayerId);
    }

    public void onTurnStart(int playerId) {
        ActionParam param = new ActionParam(playerId, null);
        broadcast(GameEvent.TURN_START, param);
    }

    public void onTurnEnd(int playerId, Vector2D position) {
        // 校验下棋者是否是当前轮次的玩家
        if (playerId != currentPlayerId) {
            log.error("the current player is " + playerId + " expected " + currentPlayerId);
            sendToPlayer(playerId, GameEvent.ERROR_REQUEST, new ActionParam(playerId, null));
            return;
        }

        // 校验新放置的棋是否合法
        if (false) {
            // error

        }
        ActionParam actionParam = new ActionParam(playerId, position);
        log.info("the " + playerId + " player put a chess at" + position.toString());
        broadcast(GameEvent.TURN_END, actionParam);

        // 检测新放置的棋是否决定了胜负
        if (isFinished(position)) {
            return;
        }

        // 通知下一轮次的玩家
        currentPlayerId = currentPlayerId == 0 ? 1 : 0;
        onTurnStart(currentPlayerId);
    }

    public void onPlayerSurrender(int playerId) {
        broadcast(GameEvent.PLAYER_SURRENDER, new ActionParam(playerId, null));

        int winner = playerId == 0 ? 1 : 0;
        onGameResult(winner);
    }

    public void onGameResult(int playerId) {
        this.isGameStart = false;
        broadcast(GameEvent.GAME_RESULT, new ActionParam(playerId, null));
    }

    public void onColorReset(Player player) {
        broadcast(GameEvent.COLOR_RESET, player);
    }

    /**
     * 向所有玩家广播消息
     *
     * @param event 游戏事件
     * @param data  事件消息
     */
    private void broadcast(GameEvent event, Object data) {
        log.info("Server broadcasts to all clients with " + event + " as " + data);
        adapterMap.forEach((playerId, adapter) -> adapter.sendEvent(event, data));
    }

    /**
     * 给指定玩家发消息
     *
     * @param playerId
     * @param event    事件
     * @param data     事件的参数
     */
    private void sendToPlayer(int playerId, GameEvent event, Object data) {
        log.info("Server communicates to PlayerTurn = " + playerId + " with " + event + " as " + data);
        adapterMap.get(playerId).sendEvent(event, data);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private boolean isValidPosition(Vector2D position) {
        int x = position.getX();
        int y = position.getY();
        return true;
    }


    private boolean isFinished(Vector2D position) {
        return false;
    }

}
