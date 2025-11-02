package se233.project2.model;


import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class AnimatedSprite extends ImageView {
    private int frameDelay = 6;
    private int frameCounter = 0;

    private int totalColumns, frameWidth, frameHeight;
    private int startColumn = 0, startRow = 0;
    private int frameCount = 1;   // how many frames in current animation
    private int currentFrame = 0;

    public AnimatedSprite(Image image, int totalColumns, int frameWidth, int frameHeight) {
        this.setImage(image);
        this.totalColumns = totalColumns;
        this.frameWidth = frameWidth;
        this.frameHeight = frameHeight;

        setViewport(new Rectangle2D(0, 0, frameWidth, frameHeight));
    }

    /** Call this when switching animation */
    public void setAnimation(int startColumn, int startRow, int frameCount) {
        this.startColumn = startColumn;
        this.startRow = startRow;
        this.frameCount = frameCount;
        this.currentFrame = 0;   // reset frame
        updateViewport();
    }

    public void tick() {
        frameCounter++;
        if (frameCounter < frameDelay) return;
        frameCounter = 0;

        currentFrame = (currentFrame + 1) % frameCount;
        updateViewport();
    }

    private void updateViewport() {
        int col = (startColumn + currentFrame) % totalColumns;
        int row = startRow + (startColumn + currentFrame) / totalColumns;

        int x = col * frameWidth;
        int y = row * frameHeight;

        setViewport(new Rectangle2D(x, y, frameWidth, frameHeight));
    }
    public boolean isLastFrame() {
        return currentFrame == frameCount - 1;
    }


}
