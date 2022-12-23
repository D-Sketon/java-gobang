package gobang.network;

import com.google.gson.Gson;
import gobang.adapter.RemoteGameAdapter;
import gobang.entity.RemoteParam;
import gobang.game.GameClient;
import gobang.ui.MainFrame;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class ClientHandler extends ChannelInboundHandlerAdapter {

    RemoteGameAdapter adapter;

    public ClientHandler(GameClient gameClient) {
        adapter = (RemoteGameAdapter) gameClient.getCommunicationAdapter();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        String json = msg.toString();
        if (json.equals("pong"))
            return;
        RemoteParam remoteParam = new Gson().fromJson(json, RemoteParam.class);
        this.adapter.receiveEvent(remoteParam.getGameEvent(), remoteParam.getParamJson());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        MainFrame.setErrorMsg("连接中断");
    }
}
