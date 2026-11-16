package game.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.Point2D;
import java.util.List;
import java.util.function.Consumer;

import javax.swing.JPanel;
import javax.swing.Timer;

/**
 * The single component that fills the game window.
 *
 * <p>It scales the fixed-size picture drawn by {@link GameRenderer} to the
 * window, and turns key presses and mouse clicks into the same single
 * characters the console game reads. It holds no game logic: every key is
 * handed to the game, which decides what it means.</p>
 */
final class GameCanvas extends JPanel {

    private static final long serialVersionUID = 1L;
    private static final int TICK_MILLIS = 450;

    private final transient GameRenderer renderer;
    private final transient UiState state = new UiState();
    private final transient Consumer<Character> keySink;
    private final Timer timer;

    /**
     * Creates the canvas.
     *
     * @param renderer paints the screen
     * @param keySink  receives each key the player chooses
     */
    GameCanvas(GameRenderer renderer, Consumer<Character> keySink) {
        this.renderer = renderer;
        this.keySink = keySink;
        setPreferredSize(new Dimension(GameRenderer.WIDTH, GameRenderer.HEIGHT));
        setBackground(new Color(0x1f1826));
        setOpaque(true);
        setDoubleBuffered(true);
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);

        addKeyListener(new Keys());
        Mouse mouse = new Mouse();
        addMouseListener(mouse);
        addMouseMotionListener(mouse);
        addMouseWheelListener(mouse);

        timer = new Timer(TICK_MILLIS, event -> {
            state.tick++;
            repaint();
        });
    }

    /**
     * Starts the small idle animation.
     */
    void startAnimation() {
        timer.start();
    }

    /**
     * Shows a new snapshot. Call on the Swing thread.
     *
     * @param snapshot the snapshot to show
     */
    void show(GameSnapshot snapshot) {
        state.accept(snapshot);
        repaint();
    }

    /**
     * Shows that the game stopped unexpectedly. Call on the Swing thread.
     *
     * @param message a short description of the problem
     */
    void showError(String message) {
        state.error = message;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D g = (Graphics2D) graphics.create();
        try {
            double scale = scale();
            g.translate(offsetX(scale), offsetY(scale));
            g.scale(scale, scale);
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.clipRect(0, 0, GameRenderer.WIDTH, GameRenderer.HEIGHT);
            renderer.paint(g, state);
        } finally {
            g.dispose();
        }
    }

    private double scale() {
        double scale = Math.min(getWidth() / (double) GameRenderer.WIDTH,
                getHeight() / (double) GameRenderer.HEIGHT);
        return scale <= 0 ? 1.0 : scale;
    }

    private double offsetX(double scale) {
        return (getWidth() - GameRenderer.WIDTH * scale) / 2.0;
    }

    private double offsetY(double scale) {
        return (getHeight() - GameRenderer.HEIGHT * scale) / 2.0;
    }

    private Point2D toCanvas(MouseEvent event) {
        double scale = scale();
        return new Point2D.Double((event.getX() - offsetX(scale)) / scale,
                (event.getY() - offsetY(scale)) / scale);
    }

    private void send(char key) {
        GameSnapshot snapshot = state.snapshot;
        if (snapshot != null && snapshot.awaitingInput() && state.error == null) {
            keySink.accept(key);
        }
    }

    private void scrollLog(int lines) {
        state.logScroll = Math.max(0, Math.min(renderer.maxLogScroll(), state.logScroll + lines));
        repaint();
    }

    private void scrollMenu(int rows) {
        state.menuScroll = Math.max(0, Math.min(renderer.maxMenuScroll(), state.menuScroll + rows));
        repaint();
    }

    /**
     * Translates a pressed key that produces no character, such as an arrow,
     * into the engine's movement key. The numeric keypad works with Num Lock
     * on or off.
     *
     * @param code   the virtual key code
     * @param numpad whether the key is on the numeric keypad
     * @return the game key, or 0 if the key is not a game key
     */
    static char keyForCode(int code, boolean numpad) {
        switch (code) {
            case KeyEvent.VK_UP:
            case KeyEvent.VK_KP_UP:
                return '8';
            case KeyEvent.VK_DOWN:
            case KeyEvent.VK_KP_DOWN:
                return '2';
            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_KP_LEFT:
                return '4';
            case KeyEvent.VK_RIGHT:
            case KeyEvent.VK_KP_RIGHT:
                return '6';
            case KeyEvent.VK_CLEAR:
            case KeyEvent.VK_BEGIN:
                return '5';
            default:
                break;
        }
        if (numpad) {
            switch (code) {
                case KeyEvent.VK_HOME:
                    return '7';
                case KeyEvent.VK_PAGE_UP:
                    return '9';
                case KeyEvent.VK_END:
                    return '1';
                case KeyEvent.VK_PAGE_DOWN:
                    return '3';
                default:
                    break;
            }
        }
        return 0;
    }

    /**
     * Translates a typed character into the key the game expects. Letters are
     * lower-cased because the engine's hotkeys are lower-case, and the space
     * bar is a second way to press 5 (wait).
     *
     * @param typed the character produced by the keyboard
     * @return the game key, or 0 if the character is not a game key
     */
    static char keyForChar(char typed) {
        if (typed == ' ') {
            return '5';
        }
        if (typed == '.') {
            return '>';
        }
        if (typed == ',') {
            return '<';
        }
        if (typed > ' ' && typed < 127) {
            return Character.toLowerCase(typed);
        }
        return 0;
    }

    private final class Keys extends KeyAdapter {

        @Override
        public void keyPressed(KeyEvent event) {
            boolean numpad = event.getKeyLocation() == KeyEvent.KEY_LOCATION_NUMPAD;
            char key = keyForCode(event.getKeyCode(), numpad);
            if (key != 0) {
                send(key);
            } else if (event.getKeyCode() == KeyEvent.VK_PAGE_UP) {
                scrollLog(5);
            } else if (event.getKeyCode() == KeyEvent.VK_PAGE_DOWN) {
                scrollLog(-5);
            }
        }

        @Override
        public void keyTyped(KeyEvent event) {
            if (event.isControlDown() || event.isAltDown() || event.isMetaDown()) {
                return;
            }
            char key = keyForChar(event.getKeyChar());
            if (key != 0) {
                send(key);
            }
        }
    }

    private final class Mouse extends MouseAdapter {

        @Override
        public void mouseMoved(MouseEvent event) {
            state.mouse = toCanvas(event);
            repaint();
        }

        @Override
        public void mouseDragged(MouseEvent event) {
            mouseMoved(event);
        }

        @Override
        public void mouseExited(MouseEvent event) {
            state.mouse = null;
            repaint();
        }

        @Override
        public void mousePressed(MouseEvent event) {
            requestFocusInWindow();
            if (event.getButton() != MouseEvent.BUTTON1) {
                return;
            }
            Point2D point = toCanvas(event);
            List<GameRenderer.Hit> hits = renderer.hits();
            for (int index = hits.size() - 1; index >= 0; index--) {
                GameRenderer.Hit hit = hits.get(index);
                if (hit.bounds().contains(point)) {
                    send(hit.key());
                    return;
                }
            }
        }

        @Override
        public void mouseWheelMoved(MouseWheelEvent event) {
            Point2D point = toCanvas(event);
            int steps = event.getWheelRotation();
            if (renderer.actionsArea().contains(point)) {
                scrollMenu(steps);
            } else {
                scrollLog(-steps * 2);
            }
        }
    }
}
