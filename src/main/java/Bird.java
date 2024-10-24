import java.awt.*;

public class Bird {
    int x, y, height, width;
    Image img;
    public Bird(Image img, int startingBirdX, int startingBirdY, int birdHeight, int birdWidth){
        this.img = img;
        x = startingBirdX;
        y = startingBirdY;
        height = birdHeight;
        width = birdWidth;
    }
}
