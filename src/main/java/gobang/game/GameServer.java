package gobang.game;

import com.google.gson.Gson;
import gobang.adapter.CommunicationAdapter;
import gobang.adapter.LocalGameAdapter;
import gobang.adapter.RemoteGameAdapter;
import gobang.entity.ActionParam;
import gobang.entity.Chess;
import gobang.entity.RemoteParam;
import gobang.enums.ChessType;
import gobang.player.Player;
import gobang.entity.Vector2D;
import gobang.enums.GameEvent;
import gobang.network.ServerOnline;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

import static gobang.entity.Board.HEIGHT;
import static gobang.entity.Board.WIDTH;
import static gobang.enums.ChessType.BLACK;
import static gobang.enums.ChessType.WHITE;

@Slf4j
public class GameServer extends AbstractGameEventHandler {

    @Getter
    private final Map<Integer, CommunicationAdapter> adapterMap;

    @Getter
    private boolean isGameStart;

    @Getter
    private boolean timerStart;

    private int timer;

    private int currentPlayerId;

    private final int lsto = 30;

    private final ServerOnline serverOnline;

    public final static Integer LOCAL_ID = 1;

    public final static Integer REMOTE_ID = 2;

    public GameServer() {
        this.gameContext = new GameContext();
        this.adapterMap = new ConcurrentHashMap<>();
        this.isGameStart = false;
        this.timerStart = false;
        this.timer = 0;
        this.currentPlayerId = LOCAL_ID;
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

        // 将玩家加入游戏上下文
        Player player = new Player(LOCAL_ID, BLACK);
        gameContext.getPlayers().put(player.getPlayerId(), player);

        // 添加一个房主适配器
        LocalGameAdapter adapter = new LocalGameAdapter(communicator);
        adapterMap.put(player.getPlayerId(), adapter);

        // 设置房主playerId
        GameClient gameClient = (GameClient) communicator;
        gameClient.setPlayerId(player.getPlayerId());

        broadcast(GameEvent.PLAYER_JOIN, player);
    }

    public void joinGameRemote(CommunicationAdapter adapter) {
        log.info("New client tries to joinGameRemote...");
        Player player = new Player(REMOTE_ID, WHITE); // 新加入的玩家默认先白棋

        gameContext.getPlayers().put(player.getPlayerId(), player);
        adapterMap.put(player.getPlayerId(), adapter);

        // 把房主传给远程玩家
        Player localPlayer = gameContext.getPlayers().get(LOCAL_ID);
        adapter.sendEvent(GameEvent.PLAYER_JOIN, localPlayer);

        sendPlayerId(player.getPlayerId(), adapter);

        broadcast(GameEvent.PLAYER_JOIN, player);
    }

    public void startGame() {
        // 玩家人数未满
        if(gameContext.getPlayers().size() != 2) {
            return;
        }
        this.isGameStart = true;
//        initTimer();
        onTurnStart(currentPlayerId);
    }

    public void onTurnStart(int playerId) {
        ActionParam param = new ActionParam(playerId, null);
        broadcast(GameEvent.TURN_START, param);
    }

    public void onTurnEnd(int playerId, Vector2D position) {
        Player player = gameContext.getPlayers().get(playerId);
        Chess chess = new Chess();
        chess.setType(player.getType());

        // 校验下棋者是否是当前轮次的玩家
        if (playerId != currentPlayerId) {
            log.error("the current player is " + playerId + " expected " + currentPlayerId);
            sendToPlayer(playerId, GameEvent.ERROR_REQUEST, new ActionParam(playerId, null));
            return;
        }

        // 校验新放置的棋是否合法
        if (!isValidPosition(position)) {
            log.error("the position " + position.toString() + "has benn placed ");
            sendToPlayer(playerId, GameEvent.ERROR_REQUEST, new ActionParam(playerId, null));
            return;
        }

        // 如果是黑棋，需要校验

        log.info("the " + playerId + " player put a chess at" + position.toString());
        gameContext.getBoard().getChess()[position.getX()][position.getY()] = player.getType();
        ActionParam actionParam = new ActionParam(playerId, position);
        broadcast(GameEvent.TURN_END, actionParam);

        // 检测新放置的棋是否决定了胜负
        if (isFinished(position)) {
            return;
        }

        // 通知下一轮次的玩家
        currentPlayerId = currentPlayerId == LOCAL_ID ? REMOTE_ID : LOCAL_ID;
        onTurnStart(currentPlayerId);
    }

    public void onPlayerSurrender(int playerId) {
        broadcast(GameEvent.PLAYER_SURRENDER, new ActionParam(playerId, null));

        int winner = playerId == LOCAL_ID ? REMOTE_ID : LOCAL_ID;
        onGameResult(winner);
    }

    public void onGameResult(int playerId) {
        this.isGameStart = false;
        broadcast(GameEvent.GAME_RESULT, new ActionParam(playerId, null));
    }

    public void onColorReset(Player player) {
        broadcast(GameEvent.COLOR_RESET, player);
    }

    public void onPlayerLeave(int playerId) {
        // 房主离开

        // 远程玩家离开
        if (playerId == REMOTE_ID) {
            sendToPlayer(LOCAL_ID, GameEvent.PLAYER_LEAVE, new ActionParam(playerId, null));
            // 将房主重置为黑棋
            Player host = gameContext.getPlayers().get(0);
            host.setType(BLACK); // 房主设为黑棋
            onColorReset(host);
        }
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

    /**
     * 仅用于远程连接有玩家加入时回传玩家Id
     *
     * @param id      玩家Id
     * @param adapter 适配器
     */
    private void sendPlayerId(Integer id, CommunicationAdapter adapter) {
        log.info("Server send remote playerId to remote player ");
        RemoteGameAdapter remoteGameAdapter = (RemoteGameAdapter) adapter;
        String json = id.toString();
        RemoteParam param = new RemoteParam();
        param.setGameEvent(GameEvent.SEND_ID);
        param.setParamJson(json);
        try {
            remoteGameAdapter.getChannel().writeAndFlush(new Gson().toJson(param) + "\n").sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private boolean isValidPosition(Vector2D position) {
        int x = position.getX();
        int y = position.getY();
        return gameContext.getBoard().getChess()[x][y] == null;
    }


    private boolean isFinished(Vector2D position) {
        int consecutiveChess = 1;
        int x = position.getX();
        int y = position.getY();
        ChessType type = gameContext.getBoard().getChess()[position.getX()][position.getY()];

        // 横向五子
        for(int i = x + 1; i < WIDTH; i++) {
            ChessType chessType = gameContext.getBoard().getChess()[i][y];
            if(chessType != null && chessType.equals(type)) {
                consecutiveChess++;
            } else {
                break;
            }
        }
        for(int i = x - 1; i >= 0; i--) {
            ChessType chessType = gameContext.getBoard().getChess()[i][y];
            if(chessType != null && chessType.equals(type)) {
                consecutiveChess++;
            } else {
                break;
            }
        }
        if(consecutiveChess == 5) {
            return true;
        }

        // 竖向五子
        consecutiveChess = 1;
        for(int j = y + 1; j < HEIGHT; j++) {
            ChessType chessType = gameContext.getBoard().getChess()[x][j];
            if(chessType != null && chessType.equals(type)) {
                consecutiveChess++;
            } else {
                break;
            }
        }
        for(int j = y - 1; j >= 0; j--) {
            ChessType chessType = gameContext.getBoard().getChess()[x][j];
            if(chessType != null && chessType.equals(type)) {
                consecutiveChess++;
            } else {
                break;
            }
        }
        if(consecutiveChess == 5) {
            return true;
        }

        // 右侧45五子
        consecutiveChess = 1;
        for(int i = x + 1; i < WIDTH; i++) {
            ChessType chessType = gameContext.getBoard().getChess()[i][y + i - x];
            if(chessType != null && chessType.equals(type)) {
                consecutiveChess++;
            } else {
                break;
            }
        }
        for(int i = x - 1; i >= 0; i--) {
            ChessType chessType = gameContext.getBoard().getChess()[i][y + i - x];
            if(chessType != null && chessType.equals(type)) {
                consecutiveChess++;
            } else {
                break;
            }
        }
        if(consecutiveChess == 5) {
            return true;
        }

        // 左侧45五子
        consecutiveChess = 1;
        for(int i = x + 1; i < WIDTH; i++) {
            ChessType chessType = gameContext.getBoard().getChess()[i][y - i + x];
            if(chessType != null && chessType.equals(type)) {
                consecutiveChess++;
            } else {
                break;
            }
        }
        for(int i = x - 1; i >= 0; i--) {
            ChessType chessType = gameContext.getBoard().getChess()[i][y - i + x];
            if(chessType != null && chessType.equals(type)) {
                consecutiveChess++;
            } else {
                break;
            }
        }
        if(consecutiveChess == 5) {
            return true;
        }

        return false;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void initTimer() {
        new Thread(() -> {
            TimerTask timerTask = new TimerTask() {
                @Override
                public void run() {
                    if (timerStart) {
                        timer++;
                    }
                    if (timer > lsto) {
                        onPlayerSurrender(currentPlayerId);
                    }
                }
            };
            Timer timer = new Timer();
            timer.schedule(timerTask, 1000, 1000);
        }).start();
    }

}
