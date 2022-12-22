package gobang.network;

import gobang.game.GameServer;
import gobang.ui.MainFrame;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.ipfilter.AbstractRemoteAddressFilter;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

import java.net.SocketAddress;
import java.util.concurrent.TimeUnit;

@Slf4j
public class ServerOnline {

    private final GameServer gameServer;

    public ServerOnline(GameServer gameServer) {
        this.gameServer = gameServer;
    }

    public void initNetty() {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workGroup = new NioEventLoopGroup();

        ConnectionCounter connectionCounter = new ConnectionCounter();
        RemoteAddressFilter remoteAddressFilter = new RemoteAddressFilter(gameServer);
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workGroup) //设置两个线程组
                    .channel(NioServerSocketChannel.class) //使用NioServerSocketChannel作为服务器通道的实现
                    .option(ChannelOption.SO_BACKLOG, 128) //设置线程队列得到连接个数
                    .childOption(ChannelOption.TCP_NODELAY, true)  //关闭延迟发送
                    .childOption(ChannelOption.SO_KEEPALIVE, true) //设置保持活动连接状态
                    .childHandler(new ChannelInitializer<SocketChannel>() { //给workGroup的EventLoop对应的管道设置处理器
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ByteBuf delimiter = Unpooled.copiedBuffer("\n".getBytes());
                            ch.pipeline().addLast(remoteAddressFilter);
                            ch.pipeline().addLast(new DelimiterBasedFrameDecoder(8192, delimiter));
                            ch.pipeline().addLast(new StringDecoder()); //添加处理类
                            ch.pipeline().addLast(new StringEncoder()); //添加处理类
                            ch.pipeline().addLast(connectionCounter); //限制最大连接数
                            ch.pipeline().addLast(new IdleStateHandler(5, 5, 5, TimeUnit.SECONDS));
                            ch.pipeline().addLast(new ServerHandler(gameServer)); //添加处理类-
                        }
                    });
            //绑定一个端口并同步，生成了一个ChannelFuture对象
            ChannelFuture cf = bootstrap.bind(6668).sync();
            //对关闭通道进行监听
            cf.channel().closeFuture().sync();
        } catch (Exception e) {
            log.error("TCP server init failed: ", e);
            // 回调
            MainFrame.setErrorMsg(e.getMessage());
        } finally {
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }
    };

    @ChannelHandler.Sharable
    static class ConnectionCounter extends ChannelInboundHandlerAdapter {

        private int connections = 0;

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            if (connections < 4) {
                connections++;
                super.channelActive(ctx);
            } else
                ctx.close();
        }

    }

    @ChannelHandler.Sharable
    static class RemoteAddressFilter extends AbstractRemoteAddressFilter<SocketAddress> {

        GameServer gameServer;

        public RemoteAddressFilter(GameServer gameServer) {
            this.gameServer = gameServer;
        }

        @Override
        protected boolean accept(ChannelHandlerContext channelHandlerContext, SocketAddress socketAddress) throws Exception {
            if (gameServer.isGameStart()) {
                log.warn("Reject TCP client:" + channelHandlerContext.channel());
                return false;
            }
            return true;
        }
    }
}
