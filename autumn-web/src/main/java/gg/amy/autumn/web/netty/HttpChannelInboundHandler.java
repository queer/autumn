package gg.amy.autumn.web.netty;

import gg.amy.autumn.di.annotation.Inject;
import gg.amy.autumn.web.http.HttpMethod;
import gg.amy.autumn.web.http.*;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;
import org.slf4j.Logger;

import javax.annotation.Nonnull;
import java.nio.charset.StandardCharsets;
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
            final var method = HttpMethod.fromNetty(request.method());
            final var path = request.uri();
            logger.info("req: {} {}", method.name(), path);

            final var req = ImmutableRequest.builder()
                    .method(method)
                    .path(path)
                    .body(request.content().array())
                    .build();

            final var maybeRoute = router.match(method, path);
            byte[] response;
            int status;
            if(maybeRoute.isPresent()) {
                try {
                    final var route = maybeRoute.get();
                    logger.trace("object = {}, req = {}", route.object(), req);
                    final Response res = (Response) route.method().invoke(route.object(), req);
                    status = res.status();
                    response = res.body();
                } catch(final Throwable e) {
                    logger.error("error handling request! ;-;", e);
                    final String message;
                    if(e.getMessage() == null) {
                        message = "<no message>";
                    } else {
                        message = e.getMessage();
                    }
                    response = message.getBytes(StandardCharsets.UTF_8);
                    status = 500;
                }
            } else {
                status = 404;
                response = "it's not here D:".getBytes(StandardCharsets.UTF_8);
            }

            logger.info("res: {} {} -> {}", method.name(), path, status);

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
