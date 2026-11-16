package game.gui;

import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 * Entry point for the graphical version of Stack Queue Valley.
 *
 * <p>This starts the same game as {@link game.Application}, shown in a window
 * instead of the console. The console application is unchanged and still
 * works; run whichever you prefer.</p>
 */
public final class GuiApplication {

    private GuiApplication() {
    }

    /**
     * Opens the game window and starts the game.
     *
     * @param args command-line arguments; none are used
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(GuiApplication::open);
    }

    private static void open() {
        SpriteSheet sheet = new SpriteSheet();
        GameRenderer renderer = new GameRenderer(sheet);

        GameSession[] session = new GameSession[1];
        GameCanvas canvas = new GameCanvas(renderer, key -> session[0].submit(key));
        session[0] = new GameSession(
                snapshot -> SwingUtilities.invokeLater(() -> canvas.show(snapshot)),
                failure -> {
                    failure.printStackTrace();
                    SwingUtilities.invokeLater(() -> canvas.showError(String.valueOf(failure)));
                });

        JFrame frame = new JFrame("Stack Queue Valley");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setIconImages(icons(sheet));
        frame.setContentPane(canvas);
        frame.pack();
        frame.setMinimumSize(new Dimension(800, 490));

        // Fit smaller screens: keep the 16:9 picture inside the usable area.
        Rectangle usable = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
        if (frame.getWidth() > usable.width || frame.getHeight() > usable.height) {
            double shrink = Math.min(usable.width / (double) frame.getWidth(),
                    usable.height / (double) frame.getHeight()) * 0.95;
            frame.setSize((int) (frame.getWidth() * shrink), (int) (frame.getHeight() * shrink));
        }
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        canvas.requestFocusInWindow();
        canvas.startAnimation();
        session[0].start();
    }

    private static List<Image> icons(SpriteSheet sheet) {
        List<Image> icons = new ArrayList<>();
        BufferedImage farmer = sheet.get("actor.farmer");
        for (int size : new int[] {16, 32, 48, 64}) {
            BufferedImage icon = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
            icon.createGraphics().drawImage(farmer, 0, 0, size, size, null);
            icons.add(icon);
        }
        return icons;
    }
}
