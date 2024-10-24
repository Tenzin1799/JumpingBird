import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

public class FlappyBird2 extends JPanel implements KeyListener {
    // Board
    private Image bgImg;
    private int boardWidth = 360;
    private int boardHeight = 640;

    // Starter Values
    private boolean gameStarted = false;
    private boolean gameOver = false;
    private final int JUMP_VALUE = -15;       // at some point maybe make jump values change, so this can be utilized when resetting the stats
    private final int PIPE_SPEED_VALUE = -5;    // Same for this, definitely for this
    private final int STARTING_SCORE = 0;
    private final int BIRD_X = boardWidth/3;
    private final int BIRD_Y = boardHeight/2;
    private final int STARTING_GAP = boardHeight/4;
    private final String FONT_NAME = "SansSerif";
    private final int TEXT_FONT_SIZE = 15;
    private final int TITLE_FONT_SIZE = 25;

    // Bird
    private Image birdImg;
    private int startingBirdX = BIRD_X;
    private int startingBirdY = BIRD_Y;
    private int birdHeight = 24;
    private int birdWidth = 34;
    private Bird bird;

    // Title Card
    private final int TITLE_X = boardWidth/4;
    private final int TITLE_Y = boardHeight/4;
    private final int TITLE_X_SHADOW = TITLE_X - 2;
    private final int TITLE_Y_SHADOW = TITLE_Y + 1;
    private final int TITLE_TEXT_X = boardWidth/4;
    private final int TITLE_TEXT_Y = boardHeight/3;
    private int titleTextY = TITLE_TEXT_Y;
    private final int TITLE_TEXT_UPPER_LIMIT = TITLE_TEXT_Y - 2;
    private final int TITLE_TEXT_LOWER_LIMIT = TITLE_TEXT_Y + 2;
    private boolean titleTextFloating = false;
    private int titleTextMovement = 1;
    private int birdListX = boardWidth/2 - birdWidth/2;
    private int birdListY = boardHeight/3 + 30;
    private ArrayList<Bird> skinList = new ArrayList<>();
    private ArrayList<Image> skinImageList = new ArrayList<>();
    private int birdSelection = 0;  // User chooses which bird they want to play as
    private Image rightArrow;
    private Image leftArrow;
    private int leftArrowX = (birdListX - birdWidth/2) - birdWidth*2;
    private int rightArrowX = birdListX + birdWidth*2;
    private int arrowY = birdListY;
    private final int ARROW_WIDTH = 50;
    private final int ARROW_HEIGHT = 50;


    // Scores
    private double currentScore = STARTING_SCORE;
    private int topScore = 0;


    // Pipe
    private Image topPipeImg;
    private Image bottomPipeImg;
    private ArrayList<Pipe> pipesList = new ArrayList<>();
    private int pipeSpeed = PIPE_SPEED_VALUE;
    private int gap = STARTING_GAP;
    private int pipeStartingX = boardWidth;

    // Movements
    private int gravity = 1;
    private int jump = 0;
    private int titleTextGravity = 1;
    private int titleTextFloat = -1;

    // Timers
    private Timer pipesLoop;
    private Timer gameLoop;
    private Timer titleLoop;

    public FlappyBird2(){
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setRequestFocusEnabled(true);
        addKeyListener(this);
        bgImg = new ImageIcon(getClass().getResource("/bg.png")).getImage();
        createListOfSkins();
        bird = skinList.get(birdSelection);

        leftArrow = new ImageIcon(getClass().getResource("/leftArrow.png")).getImage();
        rightArrow = new ImageIcon(getClass().getResource("/rightArrow.png")).getImage();

        gameLoop = new Timer(1000 / 60, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                move();
                updateScore();
                repaint();
            }
        });
        gameLoop.start();

        topPipeImg = new ImageIcon(getClass().getResource("/topPipe.png")).getImage();
        bottomPipeImg = new ImageIcon(getClass().getResource("/bottomPipe.png")).getImage();
        pipesLoop = new Timer(1500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                placePipes();
            }
        });

        titleLoop = new Timer(1000 / 25, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                moveTitle();
            }
        });
        titleLoop.start();
    }

    private void createListOfSkins(){
        skinImageList.add(new ImageIcon(getClass().getResource("/yellowBird.png")).getImage());
        skinImageList.add(new ImageIcon(getClass().getResource("/greenBird.png")).getImage());
        skinImageList.add(new ImageIcon(getClass().getResource("/ghostBird.png")).getImage());
        skinImageList.add(new ImageIcon(getClass().getResource("/blackBird.png")).getImage());
        skinImageList.add(new ImageIcon(getClass().getResource("/theBat.png")).getImage());
        skinImageList.add(new ImageIcon(getClass().getResource("/superDude.png")).getImage());

        for(int i = 0; i < skinImageList.size(); i++){
            skinList.add(new Bird(skinImageList.get(i), startingBirdX, startingBirdY, birdHeight, birdWidth));
        }
    }

    private void updateScore(){
        double pipesPassed = 0;
        for(int i = 0; i < pipesList.size(); i++){
            Pipe pipe = pipesList.get(i);
            if(pipe.passed == true){
                pipesPassed += .5;
            }
        }
        currentScore = pipesPassed;
    }

    private void placePipes(){
        if(gameStarted && !gameOver) {
            Pipe topPipe = new Pipe(topPipeImg, pipeStartingX);
            topPipe.y = (int) (0 - boardHeight / 2 + Math.random() * boardHeight / 4);

            Pipe bottomPipe = new Pipe(bottomPipeImg, pipeStartingX);
            bottomPipe.y = topPipe.height + topPipe.y + gap;

            pipesList.add(topPipe);
            pipesList.add(bottomPipe);
        }
    }

    private void collisions(Pipe pipe){
        boolean isBirdInPipeXBounds = (bird.x + bird.width > pipe.x && bird.x +bird.width < pipe.x + pipe.width) ||
                (bird.x > pipe.x && bird.x < pipe.x+pipe.width);

        if(isBirdInPipeXBounds){
            // If the bird is touching the bottom pipe
            if(bird.y > pipe.y + pipe.height){
                if(bird.y + bird.height > pipe.y + pipe.height + gap){
                    endGame();
                }
            }
            // If the bird is touching the top pipe
            if(bird.y < pipe.y - gap){
                endGame();
            }
        }
    }

    private void move(){
        if(gameStarted && !gameOver) {
            // Move bird
            bird.y += jump;
            jump += gravity;

            // Move pipe
            for (int i = 0; i < pipesList.size(); i++) {
                Pipe pipe = pipesList.get(i);
                pipe.x += pipeSpeed;
                // Detect Collisions
                collisions(pipe);
                // Detect passing a pipe
                if(bird.x > pipe.x + pipe.width){
                    pipe.passed = true;
                }
            }
            // Detect Game Overs
            if(bird.y + bird.height >= boardHeight){
                endGame();
            }
        }
    }

    private void moveTitle(){
        // Floating title card text
        titleTextY += titleTextMovement;
      if(titleTextFloating){
          titleTextMovement = Math.max(titleTextMovement+titleTextFloat, -2);
      } else {
          titleTextMovement = Math.min(titleTextMovement+titleTextGravity, 2);
      }
      if(titleTextY > TITLE_TEXT_LOWER_LIMIT){
          titleTextFloating = true;
      }
      if(titleTextY < TITLE_TEXT_UPPER_LIMIT){
          titleTextFloating = false;
      }

    }

    private void endGame(){
        if(currentScore > topScore){
            topScore = (int)currentScore;
        }
        gameOver = true;
        pipesLoop.stop();
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        draw(g);
    }

    private void draw(Graphics g){
        // Draw background
        g.drawImage(bgImg, 0, 0, boardWidth, boardHeight, null);
        setFont(g,FONT_NAME, Font.BOLD, TEXT_FONT_SIZE, Color.white);
        if(gameStarted){
            drawPipesAndBird(g);
            drawScoreAndPressPHint(g);
        } else {
            drawTitleScreen(g);
            drawSkinSelector(g);
        }
    }

    private void drawScoreAndPressPHint(Graphics g){
        g.drawString("Score: " + (int)(currentScore) + "    High Score: " + topScore, boardWidth/10, boardHeight/10);
        g.drawString("Press P for Main Menu ",boardWidth/10, boardHeight/20);
    }

    private void drawPipesAndBird(Graphics g){
        // Draw bird
        g.drawImage(bird.img, bird.x, bird.y, bird.width, bird.height, null);
        // Draw pipe
        for(int i = 0; i < pipesList.size(); i++){
            Pipe pipe = pipesList.get(i);
            g.drawImage(pipe.img, pipe.x, pipe.y, pipe.width, pipe.height, null);
        }
    }

    private void drawSkinSelector(Graphics g){
        if(birdSelection != 0) {
            g.drawImage(leftArrow, leftArrowX, arrowY, ARROW_WIDTH, ARROW_HEIGHT, null);
        }
        if(birdSelection != skinList.size()-1) {
            g.drawImage(rightArrow, rightArrowX, arrowY, ARROW_WIDTH, ARROW_HEIGHT, null);
        }
        // Draw list of skins
        g.drawImage(skinList.get(birdSelection).img, birdListX, birdListY, bird.width, bird.height, null);
    }

    private void drawTitleScreen(Graphics g){
        // Draw title shadow
        setFont(g,FONT_NAME, Font.BOLD, Font.ITALIC, TITLE_FONT_SIZE, Color.black);
        g.drawString("JUMPING BIRD", TITLE_X_SHADOW, TITLE_Y_SHADOW);
        // Draw title
        setFont(g,FONT_NAME, Font.BOLD, Font.ITALIC, TITLE_FONT_SIZE, new Color(255, 204, 0));
        g.drawString("JUMPING BIRD", TITLE_X, TITLE_Y);
        setFont(g,FONT_NAME, Font.BOLD, TEXT_FONT_SIZE, Color.white);
        g.drawString("Press Space to Start!", TITLE_TEXT_X, titleTextY);
    }

    private void setFont(Graphics g, String fontName, int fontStyle, int fontSize, Color color){
        g.setFont(new Font(fontName, fontStyle, fontSize));
        g.setColor(color);
    }

    private void setFont(Graphics g, String fontName, int fontStyle, int fontStyle2, int fontSize, Color color){
        g.setFont(new Font(fontName, fontStyle | fontStyle2, fontSize));
        g.setColor(color);
    }

    private void resetValues(){
        pipesList.clear();
        bird.y = BIRD_Y;
        bird.x = BIRD_X;
    }


    @Override
    public void keyPressed(KeyEvent e) {
        if(!gameStarted && e.getKeyCode() == KeyEvent.VK_SPACE){
            resetValues();
            gameStarted = true;
            pipesLoop.start();
            titleLoop.stop();
            bird = skinList.get(birdSelection);
        }
        if(!gameStarted && e.getKeyCode() == KeyEvent.VK_LEFT
                && birdSelection != 0){
            birdSelection--;
        }
        if(!gameStarted && e.getKeyCode() == KeyEvent.VK_RIGHT
                && birdSelection != skinList.size()-1){
            birdSelection++;
        }
        if(gameOver && e.getKeyCode() == KeyEvent.VK_SPACE){
            resetValues();
            gameOver = false;
            pipesLoop.start();
        }
        if(e.getKeyCode() == KeyEvent.VK_P){
            gameStarted = false;
        }
        if(e.getKeyCode() == KeyEvent.VK_SPACE){
            jump = JUMP_VALUE;
        }
    }
    @Override
    public void keyReleased(KeyEvent e) {

    }
    @Override
    public void keyTyped(KeyEvent e) {

    }

}
