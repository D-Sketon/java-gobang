package gobang.adapter;

import gobang.enums.GameEvent;

public interface CommunicationAdapter {

    /**
     * @param event 游戏事件
     * @param data 事件数据
     */
    void sendEvent(GameEvent event, Object data);
}
