package game.gui;

import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.displays.Printable;

import java.util.Objects;

/**
 * The engine {@link Display} the graphical game hands to the world.
 *
 * <p>It forwards printed text and key requests to a {@link GameBridge} in
 * place of the console. The map characters the engine prints are not kept,
 * because the interface draws the map from the real game state; the first
 * character of each map only signals that a new turn has begun.</p>
 */
public final class GuiDisplay extends Display {

    private final GameBridge bridge;
    private boolean drawingMap;

    /**
     * Creates a display backed by a bridge.
     *
     * @param bridge the bridge that receives output and supplies input
     */
    public GuiDisplay(GameBridge bridge) {
        this.bridge = Objects.requireNonNull(bridge);
    }

    @Override
    public void print(Printable printable) {
        if (!drawingMap) {
            drawingMap = true;
            bridge.mapDrawStarted();
        }
    }

    @Override
    public void endLine() {
        if (!drawingMap) {
            bridge.printed("");
        }
    }

    @Override
    public void print(String text) {
        drawingMap = false;
        bridge.printed(text);
    }

    @Override
    public void println(String text) {
        drawingMap = false;
        bridge.printed(text);
    }

    @Override
    public char readChar() {
        drawingMap = false;
        return bridge.nextKey();
    }
}
