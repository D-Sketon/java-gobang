package gobang.adapter;

import gobang.entity.ActionParam;
import gobang.entity.Player;
import gobang.enums.GameEvent;
import gobang.game.GameEventAware;

public class LocalGameAdapter implements CommunicationAdapter{

    private final GameEventAware target;

    public LocalGameAdapter(GameEventAware target) {
        this.target = target;
    }

    @Override
    public void sendEvent(GameEvent event, Object data) {
        ActionParam param = null;
        Player cloned = null;

        if(data instanceof ActionParam) {
            param = (ActionParam) data;
        } else if(data instanceof Player) {
            cloned = (Player) data;
        }
        boolean next = target.beforeEvent(event, data);

        if(next) {
            switch (event) {
                case PLAYER_JOIN:
                    target.onPlayerJoin(data instanceof Player ? cloned : null);
                    break;
                case TURN_START:
                    target.onTurnStart(param.getPlayerId());
                    break;
                case TURN_END:
                    target.onTurnEnd(param.getPlayerId(), param.getPosition());
                    break;
                case PLAYER_SURRENDER:
                    target.onPlayerSurrender(param.getPlayerId());
                    break;
                case PLAYER_LEAVE:
                    target.onPlayerLeave(data instanceof Player ? cloned : null);
                    break;
                case GAME_RESULT:
                    target.onGameResult(param.getPlayerId());
                    break;
                case GAME_RESET:
                    target.onGameReset(data instanceof Player ? cloned : null);
                    break;
            }
        }
    }
}
