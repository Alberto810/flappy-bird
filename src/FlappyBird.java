import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.*;

public class FlappyBird extends JPanel implements KeyListener, ActionListener {

    int LarguraBorda = 360;
    int AlturaBorda = 640;

    // IMAGEMS

    Image birdImage;
    Image backgroundImage;
    Image bottomPipeImage;
    Image topPipeImage;

    // PASSARO

    int birdX = LarguraBorda / 8;
    int birdY = AlturaBorda / 2;
    int birdWidth = 34;
    int birdHeight = 24;

    class Bird {
        int x = birdX;
        int y = birdY;
        int width = birdWidth;
        int height = birdHeight;
        Image img;

        Bird(Image img) {
            this.img = img;
        }
    }

    // CANOS

    int pipeX = LarguraBorda;
    int pipeY = 0;
    int pipeWidth = 64;
    int pipeHeight = 512;

    class Pipe {
        int x = pipeX;
        int y = pipeY;
        int width = pipeWidth;
        int height = pipeHeight;
        Image img;
        boolean Passed = false;

        Pipe(Image img) {
            this.img = img;
        }
    }

    // LÓGICA DO JOGO
    Bird bird;
    int velocitX = -4;
    int velocitY = 0;
    int gravity = 1;

    ArrayList<Pipe> pipes;
    Random random = new Random();

    Timer gameLoop;
    Timer placePipesTimer;

    boolean gameOver = false;
    boolean gameStarted = false;

    double counter = 0;

    FlappyBird() {
        setPreferredSize(new Dimension(LarguraBorda, AlturaBorda));
        setFocusable(true);
        addKeyListener(this);

        backgroundImage = new ImageIcon(getClass().getResource("./flappybirdbg.png")).getImage();
        birdImage = new ImageIcon(getClass().getResource("./flappybird.png")).getImage();
        topPipeImage = new ImageIcon(getClass().getResource("./toppipe.png")).getImage();
        bottomPipeImage = new ImageIcon(getClass().getResource("./bottompipe.png")).getImage();

        bird = new Bird(birdImage);

        pipes = new ArrayList<Pipe>();
        placePipesTimer = new Timer(1500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                placePipes();
            }
        });
        gameLoop = new Timer(1000 / 60, this);
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void placePipes() {
        int randomPypeY = (int) (pipeY - pipeHeight / 4 - Math.random() * pipeHeight / 2);
        int openingSpace = AlturaBorda / 4;
        Pipe topPipe = new Pipe(topPipeImage);
        topPipe.y = randomPypeY;
        pipes.add(topPipe);

        Pipe bottomPipe = new Pipe(bottomPipeImage);
        bottomPipe.y = topPipe.y + pipeHeight + openingSpace;
        pipes.add(bottomPipe);

    }

    public void draw(Graphics g) {
        g.drawImage(backgroundImage, 0, 0, LarguraBorda, AlturaBorda, null);

        g.drawImage(bird.img, bird.x, bird.y, bird.width, bird.height, null);

        for (int i = 0; i < pipes.size(); i++) {
            Pipe pipe = pipes.get(i);
            g.drawImage(pipe.img, pipe.x, pipe.y, pipe.width, pipe.height, null);
        }

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString("Score: " + (int) counter, 10, 20);

        if (!gameStarted && !gameOver) {
            g.setFont(new Font("Arial", Font.BOLD, 24));
            g.drawString("APERTE SPACE", 100, AlturaBorda / 2);
            g.drawString("PARA COMECAR", 100, AlturaBorda / 2 + 40);
        }

        if (gameOver) {
            g.setFont(new Font("Arial", Font.BOLD, 24));
            g.drawString("GAME OVER", 100, AlturaBorda / 2);
            g.drawString("SPACE PARA REINICIAR", 40, AlturaBorda / 2 + 40);
        }
    }

    public void move() {

        if (!gameStarted) {
            return;
        }

        velocitY += gravity;
        bird.y += velocitY;
        bird.y = Math.max(bird.y, 0);

        for (int i = 0; i < pipes.size(); i++) {
            Pipe pipe = pipes.get(i);
            pipe.x += velocitX;

            if (bird.y > AlturaBorda) {
                gameOver = true;
            }

            if (collision(bird, pipe)) {
                gameOver = true;
            }

            if (!pipe.Passed && bird.x > pipe.x + pipe.width) {
                pipe.Passed = true;
                counter += 0.5;

            }
        }
    }

    public boolean collision(Bird a, Pipe b) {
        return a.x < b.x + b.width &&
                a.x + a.width > b.x &&
                a.y < b.y + b.height &&
                a.y + a.height > b.y;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();

        if (gameOver) {
            placePipesTimer.stop();
            gameLoop.stop();
        }

    }

    @Override
    public void keyPressed(KeyEvent e) {

        if (e.getKeyCode() == KeyEvent.VK_SPACE) {

            if (!gameStarted) {
                gameStarted = true;
                placePipesTimer.start();
                gameLoop.start();
            }

            velocitY = -8;
        }

        if (gameOver) {
            bird.x = birdX;
            bird.y = birdY;
            velocitY = 0;

            pipes.clear();
            counter = 0;

            gameOver = false;
            gameStarted = false;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

}
