package gg.amy.autumn.web;

import gg.amy.autumn.application.annotation.Run;
import gg.amy.autumn.di.AutumnDI;
import gg.amy.autumn.di.annotation.Component;
import gg.amy.autumn.di.annotation.Depends;
import gg.amy.autumn.di.annotation.Inject;
import gg.amy.autumn.di.annotation.Singleton;
import gg.amy.autumn.web.http.Router;
import gg.amy.autumn.web.netty.HttpChannelInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
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
    private final EventLoopGroup masterGroup = new NioEventLoopGroup();
    private final EventLoopGroup slaveGroup = new NioEventLoopGroup();
    private ChannelFuture channelFuture;

    @Inject
    private AutumnDI di;

    @Run
    public void spawnServer() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            slaveGroup.shutdownGracefully();
            masterGroup.shutdownGracefully();
            channelFuture.channel().closeFuture().syncUninterruptibly();
        }));

        final var initializer = new HttpChannelInitializer();
        di.injectComponents(initializer);
        bootstrap.group(masterGroup, slaveGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 128)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childHandler(initializer);

        channelFuture = bootstrap.bind(8080).syncUninterruptibly();
    }
}
