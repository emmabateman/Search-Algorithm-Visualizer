//graphics classes for printing map

package search;

import java.awt.Graphics;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.JFrame;
import javax.swing.JPanel;

import java.util.Arrays;

import java.lang.Math;

/**
 * Graphics handling.
 */
public class SearchGraphics extends JPanel {

  private int squareSize = 1;
  private int wide = 0;
  private int high = 0;
  int[][] red = new int[0][0];
  int[][] green = new int[0][0];
  int[][] blue = new int[0][0];

  JFrame frame;

  /**
   * Constructor without parameters. Initializes JFrame.
   */
  public SearchGraphics() {
    frame = new JFrame("...");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setSize(500, 500);
    frame.setVisible(true);
  }

  /**
   * Constructor with parameters. Initalizes JFrame and initializes rgb maps to proper size.
   *
   * @param w Width of the map.
   * @param h Height of the map.
   */
  public SearchGraphics(int w, int h) {
    frame = new JFrame("...");
    wide = w;
    high = h;
    squareSize = (int)Math.floor(Math.min(450.0/w, 450.0/h));
    red = new int[w][h];
    green = new int[w][h];
    blue = new int[w][h];
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setSize(500, 500);
    frame.setVisible(true);
  }

  /**
   * Set the color of a given square on the map.
   * The color is based on the type of terrain
   * and whether the spot is visited, unvisited, or current.
   *
   * @param x X-coordinate of square.
   * @param y Y-coordinate of square.
   * @param terrain Type of terrain at square.
   * @param visited Visited status at square.
   */
  public void setSquare(int x, int y, char terrain, char visited) {
    switch(terrain) {
        case 'R':
            red[x][y] = 255;
            green[x][y] = 200;
            blue[x][y] = 100;
            break;
        case 'f':
            red[x][y] = 100;
            green[x][y] = 255;
            blue[x][y] = 100;
            break;
        case 'F':
            red[x][y] = 50;
            green[x][y] = 200;
            blue[x][y] = 50;
            break;
        case 'h':
            red[x][y] = 0;
            green[x][y] = 155;
            blue[x][y] = 50;
            break;
        case 'r':
            red[x][y] = 155;
            green[x][y] = 155;
            blue[x][y] = 255;
            break;
        case 'M':
            red[x][y] = 200;
            green[x][y] = 200;
            blue[x][y] = 200;
            break;
        case 'W':
            red[x][y] = 0;
            green[x][y] = 0;
            blue[x][y] = 255;
            break;
        default:
            red[x][y] = 0;
            green[x][y] = 0;
            blue[x][y] = 0;
            break;
    }
    switch(visited) {
        case 'c':
            red[x][y] = (red[x][y] + 100) / 3;
            green[x][y] = (green[x][y] + 100) / 3;
            blue[x][y] = (blue[x][y] + 100) / 3;
            break;
        case 'o':
            red[x][y] = (red[x][y] + 255 + 255) / 3;
            green[x][y] = green[x][y] / 3;
            blue[x][y] = blue[x][y] / 3;
            break;
    }
  }

  /**
   * Draw map.
   *
   * @param x1 X-coordinate of starting point.
   * @param x2 X-coordinate of ending point.
   * @param y1 Y-coordinate of starting point
   * @param y2 Y-coordinate of ending point.
   * @param x List of x-coordinates of the path.
   * @param y List of y-coordinates of the path. 
   * @param length Length of the path.
   */
  public void draw(int x1, int x2, int y1, int y2, int[] x, int[] y, int length) {
    DrawPanel panel;
    panel = new DrawPanel(red, green, blue, wide, high, squareSize, x1, x2, y1, y2, x, y, length);
    frame.setContentPane(panel);
    frame.revalidate();

  }

  /**
   * @return width
   */
  public int getWidth() {
    return wide;
  }

  /**
   * @return height
   */
  public int getHeight() {
    return high;
  }
}

/**
 * Handles drawing points to show start and goal.
 */
class Pts {

  int x1, x2, y1, y2, size;

  /**
   * Constructor.
   *
   * @param x1 X-coordinate for start point.
   * @param x2 X-coordinate for goal point.
   * @param y1 Y-coordinate for start point.
   * @param y2 Y-coordinate for goal point.
   * @param size How large each square is.
   */
  public Pts(int x1, int x2, int y1, int y2, int size) {
    this.x1 = x1;
    this.y1 = y1;
    this.x2 = x2;
    this.y2 = y2;
    this.size = size;
  }

  /**
   * Draw ovals to show the points.
   *
   * @param graphics Graphics object to draw the ovals onto.
   */
  public void draw(Graphics graphics) {
    graphics.setColor(Color.BLACK);
    graphics.fillOval((x1 * size) + (size / 2) - 10, (y1 * size) + (size / 2) - 10, 20, 20);
    graphics.setColor(Color.WHITE);
    graphics.fillOval((x2 * size) + (size / 2) - 10, (y2 * size) + (size / 2) - 10, 20, 20);
  }
}

/**
 * Handles drawing path onto map.
 */
class Path {
  int[] x;
  int[] y;
  int length;
  int size;

  /**
   * Constructor.
   *
   * @param x[] X-coordinates of path.
   * @param y[] Y-coordinates of path.
   * @param length Length of the path.
   * @param size How large each square is.
   */
  public Path(int[] x, int[] y, int length, int size) {
    this.x = x;
    this.y = y;
    this.length = length;
    this.size = size;
  }

  /**
   * Draw line to show the path.
   *
   * @param graphics Graphics object to draw the path onto.
   */
  public void draw(Graphics graphics) {
    graphics.setColor(Color.BLACK);
    for (int i = 0; i < length - 1; i++) {
        graphics.drawLine((x[i] * size) + (size / 2), (y[i] * size) + (size / 2),
            (x[i+1] * size) + (size / 2), (y[i+1] * size) + (size / 2));
    }
  }
}

/**
 * Panel to show map.
 */
class DrawPanel extends JPanel {

  int[][] red;
  int[][] green;
  int[][] blue;
  int w = 0;
  int h = 0;
  int size;
  int x1, x2, y1, y2;

  int[] x;
  int[] y;
  int length = 0;

  /**
   * Constructor.
   *
   * @param r Matrix of red values.
   * @param g Matrix of green values.
   * @param b Matrix of blue values.
   * @param width Width of map.
   * @param height Height of map.
   * @param squareSize Size of each square.
   * @param startX X-coordinate of the start point.
   * @param goalX X-coordinate of the goal point.
   * @param startY Y-coordinate of the start point.
   * @param goalY Y-coordinate of the goal point.
   * @param xPath X-coordinates of the path.
   * @param yPath Y-coordinates of the path.
   * @param len Length of the path.
   */
  public DrawPanel(int[][] r, int[][]g, int[][]b,
      int width, int height, int squareSize,
      int startX, int goalX, int startY, int goalY,
      int[] xPath, int[] yPath, int len) {
    red = new int[width][height];
    green = new int[width][height];
    blue = new int[width][height];
    for (int i = 0; i < width; i++) {
        for (int j = 0; j < height; j++) {
            red[i][j] = r[i][j];
        }
    }
    for (int i = 0; i < width; i++) {
        for (int j = 0; j < height; j++) {
            green[i][j] = g[i][j];
        }
    }
    for (int i = 0; i < width; i++) {
        for (int j = 0; j < height; j++) {
            blue[i][j] = b[i][j];
        }
    }
    w = width;
    h = height;
    size = squareSize;
    x1 = startX;
    x2 = goalX;
    y1 = startY;
    y2 = goalY;
    length = len;
    x = new int[len];
    y = new int[len];
    for (int i = 0; i < len; i++) {
        x[i] = xPath[i];
        y[i] = yPath[i];
    }        
  }

  /**
   * Paints the components of the map onto the graphics object.
   * 
   * @param graphics Graphics object.
   */
  public void paint(Graphics graphics) {
    for (int i = 0; i < w; i++) {
        for (int j = 0; j < h; j++) {
            graphics.setColor(new Color(red[i][j], green[i][j], blue[i][j]));
            graphics.fillRect(i * size, j * size, size, size);
        }
    }

    Pts pts = new Pts(x1, x2, y1, y2, size);
    pts.draw(graphics);

    if (length > 0) {
        Path path = new Path(x, y, length, size);
        path.draw(graphics);
    }
  }
}
