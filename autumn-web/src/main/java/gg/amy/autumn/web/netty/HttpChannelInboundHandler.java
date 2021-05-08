package gg.amy.autumn.web.netty;

import gg.amy.autumn.di.annotation.Inject;
import gg.amy.autumn.web.http.HttpMethod;
import gg.amy.autumn.web.http.ImmutableRequest;
import gg.amy.autumn.web.http.Router;
import gg.amy.autumn.web.util.ID;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;
import org.slf4j.Logger;

import javax.annotation.Nonnull;
import java.util.Map;

import static io.netty.buffer.Unpooled.copiedBuffer;

/**
 * @author amy
 * @since 5/1/21.
 */
class HttpChannelInboundHandler extends ChannelInboundHandlerAdapter {
    @Inject
    private Router router;
    @Inject
    private Logger logger;

    @Override
    public void channelRead(@Nonnull final ChannelHandlerContext ctx, @Nonnull final Object msg) throws Exception {
        if(msg instanceof final FullHttpRequest request) {
            try {
                final var start = System.nanoTime();
                final var method = HttpMethod.fromNetty(request.method());
                final var path = request.uri();
                final var body = request.content();
                final var bytes = new byte[body.readableBytes()];
                body.readBytes(bytes);
                final var req = ImmutableRequest.builder()
                        .id(ID.gen())
                        .method(method)
                        .path(path)
                        .body(bytes)
                        .params(Map.of())
                        .build();
                logger.info("{}: {} {}", req.id(), method.name(), path);

                final var res = router.runRequest(req);
                final var response = res.body();
                final var status = res.status();

                final HttpMessage out = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.valueOf(status), copiedBuffer(response));
                if(HttpUtil.isKeepAlive(request)) {
                    out.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
                }
                // Helpful headers
                if(!out.headers().contains(HttpHeaderNames.CONTENT_TYPE)) {
                    out.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain");
                }
                if(!out.headers().contains(HttpHeaderNames.CONTENT_LENGTH)) {
                    out.headers().set(HttpHeaderNames.CONTENT_LENGTH, response.length);
                }

                ctx.writeAndFlush(out);

                logger.info("{}: status={} sentIn={}ms", req.id(), status, String.format("%.2f", (System.nanoTime() - start) / 1_000_000D));
            } catch(final Throwable e) {
                // TODO: Handling
                e.printStackTrace();
            }
        } else {
            super.channelRead(ctx, msg);
        }
    }

    @Override
    public void channelReadComplete(@Nonnull final ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(@Nonnull final ChannelHandlerContext ctx, @Nonnull final Throwable cause) {
        final String causeMessage;
        if(cause.getMessage() == null) {
            causeMessage = "<no message>";
        } else {
            causeMessage = cause.getMessage();
        }
        ctx.writeAndFlush(new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1,
                HttpResponseStatus.INTERNAL_SERVER_ERROR,
                copiedBuffer(causeMessage
                        .getBytes())
        ));
    }
}
