package gobang.network;

import gobang.adapter.RemoteGameAdapter;
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
        gameServer.joinGameRemote(this.adapter);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
    }
}
