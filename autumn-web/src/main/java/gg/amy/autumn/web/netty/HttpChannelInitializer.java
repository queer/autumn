package gg.amy.autumn.web.netty;

import gg.amy.autumn.di.AutumnDI;
import gg.amy.autumn.di.annotation.Inject;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;

import javax.annotation.Nonnull;

/**
 * @author amy
 * @since 5/1/21.
 */
public class HttpChannelInitializer extends ChannelInitializer<SocketChannel> {
    @Inject
    private AutumnDI di;

    @Override
    protected void initChannel(@Nonnull final SocketChannel channel) {
        final var handler = new HttpChannelInboundHandler();
        di.injectComponents(handler);

        channel.pipeline().addLast("codec", new HttpServerCodec());
        // TODO: Sizing
        // 640k should be enough for anyone, I say!
        channel.pipeline().addLast("aggregator", new HttpObjectAggregator(640 * 1024));
        channel.pipeline().addLast("request", handler);
    }
}
