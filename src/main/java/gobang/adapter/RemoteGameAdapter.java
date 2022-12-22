package gobang.adapter;

import com.google.gson.Gson;
import gobang.entity.ActionParam;
import gobang.entity.RemoteParam;
import gobang.game.GameClient;
import gobang.player.Player;
import gobang.enums.GameEvent;
import gobang.game.GameEventAware;
import io.netty.channel.Channel;
import lombok.Getter;
import lombok.Setter;

/**
 * 远程C/S通信适配器，通过channel进行通信
 */
@Setter
@Getter
public class RemoteGameAdapter implements CommunicationAdapter{

    private GameEventAware self;

    private Channel channel;

    @Override
    public void sendEvent(GameEvent event, Object data) {
        if (!channel.isActive()) {
            if (self instanceof GameClient)
//                MainController.showErrorDialog("连接中断");
            return;
        }
        String json = new Gson().toJson(data);
        RemoteParam remoteParam = new RemoteParam(event, json);
        try {
            channel.writeAndFlush(new Gson().toJson(remoteParam) + "\n").sync();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void receiveEvent(GameEvent event, String json) {
        ActionParam param = null;
        Player player = null;
        boolean next = self.beforeEvent(event, json);

        if(next) {
            switch (event) {
                case PLAYER_JOIN:
                    player = new Gson().fromJson(json, Player.class);
                    self.onPlayerJoin(player);
                    break;
                case TURN_START:
                    param = new Gson().fromJson(json, ActionParam.class);
                    self.onTurnStart(param.getPlayerId());
                    break;
                case TURN_END:
                    param = new Gson().fromJson(json, ActionParam.class);
                    self.onTurnEnd(param.getPlayerId(), param.getPosition());
                    break;
                case PLAYER_SURRENDER:
                    param = new Gson().fromJson(json, ActionParam.class);
                    self.onPlayerSurrender(param.getPlayerId());
                    break;
                case PLAYER_LEAVE:
                    param = new Gson().fromJson(json, ActionParam.class);
                    self.onPlayerLeave(param.getPlayerId());
                    break;
                case GAME_RESULT:
                    param = new Gson().fromJson(json, ActionParam.class);
                    self.onGameResult(param.getPlayerId());
                    break;
                case COLOR_RESET:
                    player = new Gson().fromJson(json, Player.class);
                    self.onColorReset(player);
                case SEND_ID:
                    int id = Integer.parseInt(json);
                    self.onSendId(id);
            }
        }
    }
}
