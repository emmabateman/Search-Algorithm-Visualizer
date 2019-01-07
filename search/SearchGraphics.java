//graphics classes for printing map

package search;

import java.awt.Graphics;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.lang.Math;
import java.util.Arrays;

public class SearchGraphics extends JPanel {

    private int squaresize = 1;
    private int wide = 0;
    private int high = 0;
    int[][] red = new int[0][0];
    int[][] green = new int[0][0];
    int[][] blue = new int[0][0];

    JFrame frame;

    public SearchGraphics() {
        frame = new JFrame("...");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 500);
        frame.setVisible(true);
    }

    public SearchGraphics(int w, int h) {
	frame = new JFrame("...");
	wide = w;
	high = h;
	squaresize = (int)Math.floor(Math.min(450.0/w, 450.0/h));
	red = new int[w][h];
	green = new int[w][h];
	blue = new int[w][h];
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 500);
        frame.setVisible(true);
    }

    public void setsquare(int x, int y, char terrain, char visited) {
	switch(terrain) {
		case 'R': red[x][y] = 255;
			green[x][y] = 200;
			blue[x][y] = 100;
			break;
		case 'f': red[x][y] = 100;
			green[x][y] = 255;
			blue[x][y] = 100;
			break;
		case 'F': red[x][y] = 50;
			green[x][y] = 200;
			blue[x][y] = 50;
			break;
		case 'h': red[x][y] = 0;
			green[x][y] = 155;
			blue[x][y] = 50;
			break;
		case 'r': red[x][y] = 155;
			green[x][y] = 155;
			blue[x][y] = 255;
			break;
		case 'M': red[x][y] = 200;
			green[x][y] = 200;
			blue[x][y] = 200;
			break;
		case 'W': red[x][y] = 0;
			green[x][y] = 0;
			blue[x][y] = 255;
			break;
		default: red[x][y] = 0;
			green[x][y] = 0;
			blue[x][y] = 0;
			break;
	}
	switch(visited) {
		case 'c': red[x][y] = (red[x][y]+100)/3;
			green[x][y] = (green[x][y]+100)/3;
			blue[x][y] = (blue[x][y]+100)/3;
			break;
		case 'o': red[x][y] = (red[x][y]+255+255)/3;
			green[x][y] = green[x][y]/3;
			blue[x][y] = blue[x][y]/3;
			break;
	}
    }

    public void draw(int x1, int x2, int y1, int y2, int[] x, int[] y, int length) {
	DrawPanel panel;
	panel = new DrawPanel(red, green, blue, wide, high, squaresize, x1, x2, y1, y2, x, y, length);
	frame.setContentPane(panel);
	frame.revalidate();

    }

    public int width() {
	return wide;
    }
    public int height() {
	return high;
    }
}

class Pts {

    int x1, x2, y1, y2, size;

    public Pts(int x1, int x2, int y1, int y2, int size) {
	this.x1 = x1;
	this.y1 = y1;
	this.x2 = x2;
	this.y2 = y2;
	this.size = size;
    }

    public void draw(Graphics g) {
	g.setColor(Color.BLACK);
	g.fillOval(x1*size+size/2-10, y1*size+size/2-10, 20, 20);
	g.setColor(Color.WHITE);
	g.fillOval(x2*size+size/2-10, y2*size+size/2-10, 20, 20);
    }
}

class Path {
    int[] x;
    int[] y;
    int length;
    int size;

    public Path(int[] x, int[] y, int length, int size) {
	this.x = x;
	this.y = y;
	this.length = length;
	this.size = size;
    }

    public void draw(Graphics g) {
	g.setColor(Color.BLACK);
	for (int i = 0; i < length-1; i++) {
		g.drawLine(x[i]*size+size/2, y[i]*size+size/2, x[i+1]*size+size/2, y[i+1]*size+size/2);
	}
    }
}

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

    public DrawPanel(int[][] r, int[][]g, int[][]b, int width, int height, int squaresize, int startx, int goalx, int starty, int goaly, int[] xpath, int[] ypath, int len) {
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
	size = squaresize;
	x1 = startx;
	x2 = goalx;
	y1 = starty;
	y2 = goaly;

	length = len;
	x = new int[len];
	y = new int[len];
	for (int i = 0; i < len; i++) {
		x[i] = xpath[i];
		y[i] = ypath[i];
	}		
    }


    public void paint(Graphics g) {
	for (int i = 0; i < w; i++) {
		for (int j = 0; j < h; j++) {
			g.setColor(new Color(red[i][j], green[i][j], blue[i][j]));
			g.fillRect(i*size, j*size, size, size);
		}
	}

	Pts pts = new Pts(x1, x2, y1, y2, size);
	pts.draw(g);

	if (length > 0) {
		Path path = new Path(x, y, length, size);
		path.draw(g);
	}
    }
}
