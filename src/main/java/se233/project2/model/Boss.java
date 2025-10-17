package se233.project2.model;


import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

class Boss {
    double x = 500, y = 300, w = 120, h = 120;
    double speed = 2;
    int hp = 20;
    boolean alive = true;

    void update() {
        x += speed;
        if (x < 400 || x > 700) speed *= -1; // patrol
    }

    void draw(GraphicsContext gc) {
        gc.setFill(Color.RED);
        gc.fillRect(x, y - h, w, h);
        gc.setFill(Color.WHITE);
        gc.fillText("Boss HP: " + hp, x, y - h - 10);
    }
}
