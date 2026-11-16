package game.gui;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * Routes the process's standard output and input through a {@link GameBridge}.
 *
 * <p>Two parts of the existing game do not use the display they are given:
 * three plant behaviours print growth messages straight to
 * {@code System.out}, and the engine's menu paging creates its own console
 * display, which prints to {@code System.out} and reads {@code System.in}.
 * Redirecting both streams lets the graphical game show those messages and
 * answer those reads without changing either piece of code.</p>
 */
public final class ConsoleRedirect implements AutoCloseable {

    private final PrintStream originalOut = System.out;
    private final InputStream originalIn = System.in;

    /**
     * Installs the redirect. Call this before the game creates any display.
     *
     * @param bridge the bridge that receives output and supplies input
     */
    public ConsoleRedirect(GameBridge bridge) {
        Objects.requireNonNull(bridge);
        System.setOut(new PrintStream(new LineOutput(bridge), true, StandardCharsets.UTF_8));
        System.setIn(new KeyInput(bridge));
    }

    /**
     * Returns the console output stream that was active before the redirect.
     *
     * @return the original standard output
     */
    public PrintStream originalOut() {
        return originalOut;
    }

    /**
     * Restores the original standard streams.
     */
    @Override
    public void close() {
        System.setOut(originalOut);
        System.setIn(originalIn);
    }

    /** Collects printed bytes and passes on each completed line. */
    private static final class LineOutput extends OutputStream {
        private final GameBridge bridge;
        private final ByteArrayOutputStream pending = new ByteArrayOutputStream();

        LineOutput(GameBridge bridge) {
            this.bridge = bridge;
        }

        @Override
        public synchronized void write(int value) {
            if (value == '\n') {
                String line = pending.toString(StandardCharsets.UTF_8);
                pending.reset();
                if (line.endsWith("\r")) {
                    line = line.substring(0, line.length() - 1);
                }
                bridge.printed(line);
            } else {
                pending.write(value);
            }
        }
    }

    /** Answers each console read with one key and a line break. */
    private static final class KeyInput extends InputStream {
        private final GameBridge bridge;
        private byte[] pending = new byte[0];
        private int position;

        KeyInput(GameBridge bridge) {
            this.bridge = bridge;
        }

        private void fill() {
            if (position >= pending.length) {
                pending = (bridge.nextKey() + "\n").getBytes(StandardCharsets.UTF_8);
                position = 0;
            }
        }

        @Override
        public int read() {
            fill();
            return pending[position++] & 0xFF;
        }

        @Override
        public int read(byte[] buffer, int offset, int length) {
            if (length == 0) {
                return 0;
            }
            fill();
            int count = Math.min(length, pending.length - position);
            System.arraycopy(pending, position, buffer, offset, count);
            position += count;
            return count;
        }

        @Override
        public int available() {
            return Math.max(0, pending.length - position);
        }
    }
}
