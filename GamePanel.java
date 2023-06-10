import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Random;
import javax.swing.*;

public class GamePanel extends JPanel implements ActionListener {

  static final int SCREEN_WIDTH = 600;
  static final int SCREEN_HEIGHT = 600;
  static final int UNIT_SIZE = 20;
  static final int GAME_UNITS = (SCREEN_WIDTH * SCREEN_HEIGHT) / UNIT_SIZE;
  final int[] x = new int[GAME_UNITS];
  final int[] y = new int[GAME_UNITS];
  int delay = 120;
  int bodyParts = 2;
  int applesEaten = 0;
  int appleX;
  int appleY;
  boolean running = false;
  Timer timer;
  Random random = new Random();
  static final char[] directions = new char[] { 'U', 'D', 'L', 'R' };
  char direction = directions[random.nextInt(0, 3)];

  GamePanel() {
    this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
    this.setBackground(Color.black);
    this.setFocusable(true);
    this.addKeyListener(new MyKeyAdapter());
    startGame();
  }

  public void startGame() {
    newApple();
    running = true;
    timer = new Timer(delay, this);
    timer.start();
  }

  public void paintComponent(Graphics g) {
    super.paintComponent(g);
    draw(g);
  }

  public void draw(Graphics g) {
    if (running) {
      //Grid
      for (int i = 0; i < SCREEN_HEIGHT / UNIT_SIZE; i++) {
        g.drawLine(i * UNIT_SIZE, 0, i * UNIT_SIZE, SCREEN_HEIGHT);
        g.drawLine(0, i * UNIT_SIZE, SCREEN_WIDTH, i * UNIT_SIZE);
      }

      //Apple
      if (applesEaten % 50 != 0 || applesEaten == 0) {
        g.setColor(Color.RED);
        g.fillOval(appleX, appleY, UNIT_SIZE, UNIT_SIZE);
      } else {
        g.setColor(new Color(0xe0ae07));
        g.fillOval(appleX, appleY, UNIT_SIZE, UNIT_SIZE);
      }

      //Snake
      for (int i = 0; i < bodyParts; i++) {
        if (i == 0) {
          g.setColor(new Color(0x053d0a));
          g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
        } else if (i % 2 == 0) {
          g.setColor(new Color(0x06753e));
          g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
        } else {
          g.setColor(new Color(0x06750a));
          g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
        }
      }

      //Score
      g.setColor(Color.red);
      g.setFont(new Font("Ink Free", Font.BOLD, 30));
      g.drawString("Score : " + applesEaten, 0, UNIT_SIZE);
    } else {
      gameOver(g);
    }
  }

  public void newApple() {
    appleX = random.nextInt((int) (SCREEN_WIDTH / UNIT_SIZE)) * UNIT_SIZE;
    appleY = random.nextInt((int) (SCREEN_HEIGHT / UNIT_SIZE)) * UNIT_SIZE;
  }

  public void move() {
    for (int i = bodyParts; i > 0; i--) {
      x[i] = x[i - 1];
      y[i] = y[i - 1];
    }
    switch (direction) {
      case 'U' -> y[0] = (y[0] < 0) ? SCREEN_HEIGHT : (y[0] - UNIT_SIZE);
      case 'D' -> y[0] = (y[0] > SCREEN_HEIGHT) ? 0 : (y[0] + UNIT_SIZE);
      case 'R' -> x[0] = (x[0] > SCREEN_WIDTH) ? 0 : (x[0] + UNIT_SIZE);
      case 'L' -> x[0] = (x[0] < 0) ? SCREEN_WIDTH : (x[0] - UNIT_SIZE);
    }
  }

  public void checkApple() {
    if ((appleX == x[0]) && (appleY == y[0])) {
      bodyParts++;
      applesEaten += (applesEaten != 0 && applesEaten % 50 == 0) ? 10 : 1;
      delay += (delay < 10) ? 0 : -3;
      newApple();
    }
  }

  public void checkCollisions() {
    //Checks if Head collides with body
    for (int i = bodyParts; i > 0; i--) {
      if ((x[0] == x[i]) && (y[0] == y[i])) {
        running = false;
        break;
      }
    }

    //Game stops if collision occurs
    if (!running) {
      timer.stop();
    }
  }

  public void gameOver(Graphics g) {
    //Score
    g.setColor(Color.red);
    g.setFont(new Font("Ink Free", Font.BOLD, 30));
    FontMetrics scoreMetrics = getFontMetrics(g.getFont());
    g.drawString(
      "Final Score : " + applesEaten,
      (SCREEN_WIDTH - scoreMetrics.stringWidth("Final Game Over!!")) / 2,
      SCREEN_HEIGHT / 8
    );

    //Game-Over text
    g.setColor(Color.red);
    g.setFont(new Font("Ink Free", Font.BOLD, 75));
    FontMetrics gameOverMetrics = getFontMetrics(g.getFont());
    g.drawString(
      "Game Over!!",
      (SCREEN_WIDTH - gameOverMetrics.stringWidth("Game Over!!")) / 2,
      SCREEN_HEIGHT / 2
    );
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    if (running) {
      move();
      checkApple();
      checkCollisions();
    }
    repaint();
  }

  public class MyKeyAdapter extends KeyAdapter {

    @Override
    public void keyPressed(KeyEvent e) {
      switch (e.getKeyCode()) {
        case KeyEvent.VK_A, KeyEvent.VK_LEFT -> direction =
          direction != 'R' ? 'L' : direction;
        case KeyEvent.VK_D, KeyEvent.VK_RIGHT -> direction =
          direction != 'L' ? 'R' : direction;
        case KeyEvent.VK_W, KeyEvent.VK_UP -> direction =
          direction != 'D' ? 'U' : direction;
        case KeyEvent.VK_S, KeyEvent.VK_DOWN -> direction =
          direction != 'U' ? 'D' : direction;
      }
    }
  }
}
