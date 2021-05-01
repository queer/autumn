package gg.amy.autumn.web.util;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Random;

/**
 * This is a partial port of the MIT-licenced https://github.com/akhawaja/ksuid.
 * We only care about generating new IDs.
 *
 * @author amy
 * @since 5/1/21.
 */
public final class ID {
    private static final String CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final char CODEFLAG = '9';
    private static final long EPOCH = 1518566400000L;
    private static final int TIMESTAMP_LENGTH = 4;
    private static final int PAYLOAD_LENGTH = 16;
    private static final int MAX_ENCODED_LENGTH = 27;
    private static final Random RANDOM = new SecureRandom();

    private ID() {
    }

    public static String gen() {
        final var output = new ByteArrayOutputStream();
        final var timestamp = makeTimestamp();
        final var payload = makePayload();

        try {
            output.write(timestamp);
            output.write(payload);
        } catch(final IOException e) {
            throw new IllegalStateException(e);
        }

        final String uid = base62(output.toByteArray());
        if(uid.length() > MAX_ENCODED_LENGTH) {
            return uid.substring(0, MAX_ENCODED_LENGTH);
        }
        return uid;
    }

    private static byte[] makeTimestamp() {
        final var utc = ZonedDateTime.now(ZoneOffset.UTC).toInstant().toEpochMilli() / 1000L;
        return intToBytes((int) (utc - EPOCH));
    }

    private static byte[] makePayload() {
        final var bytes = new byte[PAYLOAD_LENGTH];
        RANDOM.nextBytes(bytes);
        return bytes;
    }

    private static byte[] intToBytes(int l) {
        final var result = new byte[4];
        for (int i = 3; i >= 0; i--) {
            result[i] = (byte)(l & 0xFF);
            l >>= 4;
        }
        return result;
    }

    private static String base62(@Nonnull final byte[] in) {
        final var out = new StringBuilder();
        for(int i = 0; i < in.length; i += 3) {
            int b = (in[i] & 0xFC) >> 2;
            append(out, b);

            b = (in[i] & 0x03) << 4;
            if(i + 1 < in.length) {
                b |= (in[i + 1] & 0xF0) >> 4;
                append(out, b);

                b = (in[i + 1] & 0x0F) << 2;
                if(i + 2 < in.length) {
                    b |= (in[i + 2] & 0xC0) >> 6;
                    append(out, b);
                    b = in[i + 2] & 0x3F;

                }
            }
            append(out, b);
        }

        return out.toString();
    }

    private static void append(@Nonnull final StringBuilder out, @Nonnegative final int b) {
        if(b < 61) {
            out.append(CHARS.charAt(b));
        } else {
            out.append(CODEFLAG);
            out.append(CHARS.charAt(b - 61));
        }
    }
}
