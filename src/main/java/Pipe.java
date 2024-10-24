import java.awt.*;

public class Pipe {
    int x;
    int y = 0;
    int height = 512;
    int width = 64;
    Image img;
    boolean passed = false;

    public Pipe(Image img, int startingX){
        this.img = img;
        x = startingX;
    }
}
