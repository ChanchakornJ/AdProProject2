package se233.project2.model;

import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class BossSprite extends ImageView {
    private int cols = 1, rows = 1;
    private int startIndex = 0, endIndex = 0;
    private int currentIndex = 0;
    private int frameDelay = 6;
    private int frameCounter = 0;

    private int frameWidth, frameHeight;

    public BossSprite(Image img) {
        setImage(img);
    }

    public void configure(int cols, int rows, int start, int end, int delay) {
        this.cols = cols;
        this.rows = rows;
        this.startIndex = start;
        this.endIndex = end;
        this.frameDelay = delay;

        frameWidth = (int)(getImage().getWidth() / cols);
        frameHeight = (int)(getImage().getHeight() / rows);

        currentIndex = start;
        setViewportForIndex(currentIndex);
    }

    public void tick() {
        frameCounter++;
        if (frameCounter < frameDelay) return;
        frameCounter = 0;

        currentIndex++;
        if (currentIndex > endIndex) currentIndex = startIndex;

        setViewportForIndex(currentIndex);
    }

    private void setViewportForIndex(int index) {
        int col = index % cols;
        int row = index / cols;
        setViewport(new Rectangle2D(col * frameWidth, row * frameHeight, frameWidth, frameHeight));
    }
}
