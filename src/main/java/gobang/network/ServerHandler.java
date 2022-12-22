package gobang.network;

import com.google.gson.Gson;
import gobang.adapter.RemoteGameAdapter;
import gobang.entity.RemoteParam;
import gobang.game.GameServer;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class ServerHandler extends ChannelInboundHandlerAdapter {

    RemoteGameAdapter adapter;
    GameServer gameServer;

    public ServerHandler(GameServer gameServer) {
        this.gameServer = gameServer;
    }


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        this.adapter = new RemoteGameAdapter();
        this.adapter.setChannel(ctx.channel());
        this.adapter.setSelf(gameServer);
        gameServer.joinGameRemote(this.adapter);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        String json = msg.toString();
        if (json.equals("ping"))
            return;
        RemoteParam remoteParam = new Gson().fromJson(json, RemoteParam.class);
        this.adapter.receiveEvent(remoteParam.getGameEvent(), remoteParam.getParamJson());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
    }
}
