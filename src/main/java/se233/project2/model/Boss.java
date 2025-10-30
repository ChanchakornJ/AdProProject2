package se233.project2.model;


import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.image.Image;
import se233.project2.Launcher;


public class Boss extends Pane {
    double x = 500, y = 300, w = 120, h = 120;
    double speed = 2;
    int hp = 20;
    boolean alive = true;
    private Image bossImage;



    public Boss(double x, double y, double w, double h, double speed, int hp, String imgName){
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        this.speed = speed;
        this.hp = hp;

        this.bossImage = new Image(Launcher.class.getResourceAsStream(imgName));
        ImageView sprite = new ImageView(bossImage);
        sprite.setFitWidth(w);
        sprite.setFitHeight(h);
        getChildren().add(sprite);
        setTranslateX(x);
        setTranslateY(y);
    }

    void update() {
        x += speed;
        if (x < 400 || x > 700) speed *= -1; // patrol
    }
    public Rectangle2D getHitBox() {
        return new Rectangle2D(getTranslateX(), getTranslateY(), w-10, h);
    }


    void draw(GraphicsContext gc) {
        gc.setFill(Color.RED);
        gc.fillRect(x, y - h, w, h);
        gc.setFill(Color.WHITE);
        gc.fillText("Boss HP: " + hp, x, y - h - 10);
    }
    public void takeDamage(){
        hp--;
        if (hp <=0){
            die();
        }
    }
    public void die() {
        alive = false;
        ImageView explosion = new ImageView(new Image(Launcher.class.getResourceAsStream("assets/Stage1Pathway.png")));
        explosion.setX(400);
        explosion.setY(300);
        ((Pane)getParent()).getChildren().add(explosion);
    }



}
