package gg.amy.autumn.web;

import gg.amy.autumn.application.annotation.Run;
import gg.amy.autumn.di.AutumnDI;
import gg.amy.autumn.di.annotation.*;
import gg.amy.autumn.web.http.Router;
import gg.amy.autumn.web.netty.HttpChannelInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * @author amy
 * @since 5/1/21.
 */
@Component
@Singleton
@Depends(Router.class)
public class HttpServer {
    private final ServerBootstrap bootstrap = new ServerBootstrap();
    private final EventLoopGroup masterGroup;
    private final EventLoopGroup slaveGroup;
    private ChannelFuture channelFuture;

    @Config("autumn.web.port")
    private int port = 8080;

    @Inject
    private AutumnDI di;

    public HttpServer() {
        if(Epoll.isAvailable()) {
            masterGroup = new EpollEventLoopGroup();
            slaveGroup = new EpollEventLoopGroup();
        } else {
            masterGroup = new NioEventLoopGroup();
            slaveGroup = new NioEventLoopGroup();
        }
    }

    @Run
    public void spawnServer() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            slaveGroup.shutdownGracefully();
            masterGroup.shutdownGracefully();
            channelFuture.channel().closeFuture().syncUninterruptibly();
        }));

        final var initializer = new HttpChannelInitializer();
        di.injectComponents(initializer);

        final Class<? extends ServerSocketChannel> socketChannelClass;
        if(Epoll.isAvailable()) {
            socketChannelClass = EpollServerSocketChannel.class;
        } else {
            socketChannelClass = NioServerSocketChannel.class;
        }

        bootstrap.group(masterGroup, slaveGroup)
                .channel(socketChannelClass)
                .option(ChannelOption.SO_BACKLOG, 128)
                .option(ChannelOption.SO_REUSEADDR, true)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childHandler(initializer);

        channelFuture = bootstrap.bind(port).syncUninterruptibly();
    }
}
