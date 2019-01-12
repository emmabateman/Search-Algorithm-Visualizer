package search;

import java.io.*;

import java.util.*;
import java.util.List;
import java.util.Queue;
import java.util.LinkedList;
import java.util.Comparator;
import java.util.concurrent.TimeUnit;

import java.lang.Math;

/**
 * Main class.
 */
public class Search {
  private static SearchGraphics graphics;
  
  /**
   * Main function.
   * 
   * @param args Command line arguments.
   */
  public static void main(String[] args) {
    int width = 0;
    int height = 0;
    int startX = 0;
    int startY = 0;
    int goalX = 0;
    int goalY = 0;
    char[][] map = new char[0][0];

    if (args.length < 1) {
      System.out.println("map file name required\n");
      System.exit(0);
    }

    String inputFile = args[0];
    int algorithm = 0;
    if (args.length >= 2) {
      algorithm = Integer.valueOf(args[1]);
    }
    else {
      Scanner stdin = new Scanner(System.in);
      System.out.println("Enter algorithm number."
          + " 0 = breadth first; 1 = lowest cost first; 2 = greedy best first;"
          + " 3 = A* with euclidean distance; 4 = A* with manhattan distance.");
      algorithm = stdin.nextInt();
    }

    File infile = new File(args[0]);  

    //read in map
    try {
       Scanner fileScanner = new Scanner(infile);
       width = fileScanner.nextInt();
       height = fileScanner.nextInt();
       startX = fileScanner.nextInt();
       startY = fileScanner.nextInt();
       goalX = fileScanner.nextInt();
       goalY = fileScanner.nextInt();

       map = new char[width][height];
       for (int i = 0; i < height; i++) {
            String nextLine = fileScanner.next();
          for (int j = 0; j < width; j++) {
                map[j][i] = nextLine.charAt(j);
            }
       } 
    } catch (FileNotFoundException e) {
        System.out.println("file " + args[0] + " not found");
        System.exit(0);
    }

    //init graphics
    graphics = new SearchGraphics(width, height);

    //paint starting map
    paint(map, new char[width][height], startX, goalX, startY, goalY, new int[0], new int[0], 0);

    search(map, height, width, startX, goalX, startY, goalY, algorithm);
    
  }

  /**
   * Search for a path to the goal
   * 
   * @param map 2D terrain being traversed.
   * @param height Height of the map.
   * @param width Width of the map.
   * @param startX X-coordinate of the starting point.
   * @param goalX X-coordinate of the goal point.
   * @param startY Y-coordinate of the starting point.
   * @param goalY Y-coordinate of the goal point.
   * @param algorithm Integer representing algorithm choice.
   */
  private static void search(char[][] map, int height, int width,
      int startX, int goalX, int startY, int goalY,
      int algorithm) {

    //on map2, c = closed, o = open, x = unvisited
    char[][] map2 = new char[width][height];
    for (int i = 0; i < width; i++) {
        for (int j = 0; j < height; j++) {
            if (map[i][j] == 'W') {
              map2[i][j] = 'c';
            }
            else {
              map2[i][j] = 'x';
            }
        }
    }

    Queue<Node> openList;
    Comparator<Node> comparator;

    switch(algorithm) {
        case 1:
            comparator = new NodeCostComparator();
            openList = new PriorityQueue<Node>(11, comparator);
            break;
        case 2:
            comparator = new NodeDistanceComparator(goalX, goalY);
            openList = new PriorityQueue<Node>(11, comparator);
            break;
        case 3:
            comparator = new AStarEuclidean(goalX, goalY);
            openList = new PriorityQueue<Node>(11, comparator);
            break;
        case 4:
            comparator = new AStarManhattan(goalX, goalY);
            openList = new PriorityQueue<Node>(11, comparator);
            break;
        default:
            openList = new LinkedList<Node>();
            break;
    }

    //add starting node to list
    openList.add(new Node(startX, startY, squareCost(map[startX][startY])));
    map2[startX][startY] = 'o';

    Node current;

    while(openList.peek() != null) {
        //Delay to create animated effect
        try {
          TimeUnit.MILLISECONDS.sleep(10);
        }
        catch (InterruptedException e) {
          System.out.println("error");
        }

        //Paint map
        paint(map, map2, startX, goalX, startY, goalY, new int[0], new int[0], 0);

        //pop off next node from open list
        current = openList.remove();

        //if goal found, stop search and print map
        if ((current.x == goalX) && (current.y == goalY)) {
          System.out.print("length: " + current.path.size());
          System.out.println("\ncost: " + current.cost);

          int len = current.path.size() + 1;
          int[] xpath = new int[len];
          int[] ypath = new int[len];
          for (int i = 0; i < current.path.size(); i++) {
            xpath[i] = current.path.get(i).x;
            ypath[i] = current.path.get(i).y;
          }
          xpath[current.path.size()] = goalX;
          ypath[current.path.size()] = goalY;

          paint(map, map2, startX, goalX, startY, goalY, xpath, ypath, len);
            
          return;
        }

        //stop program if more than a million nodes are on the open list (~ 1 gig memory)
        if(openList.size() > 1000000) {
          paint(map, map2, startX, goalX, startY, goalY, new int[0], new int[0], 0);

          System.out.println("ran out of space!");

          return;
        }

        if (algorithm > 1) {
          //up
          if ((current.y - 1 >= 0) && (map2[current.x][current.y - 1] != 'c')) {
            Node newNode = new Node(current.x, current.y - 1, squareCost(map[current.x][current.y - 1]), current);
            openList.add(newNode);
            map2[current.x][current.y - 1] = 'o';
          }
    
          //down
          if ((current.y + 1 < height) && (map2[current.x][current.y + 1] != 'c')) {
            Node newNode = new Node(current.x, current.y + 1, squareCost(map[current.x][current.y + 1]), current);
            openList.add(newNode);
            map2[current.x][current.y + 1] = 'o';
          }
    
          //left
          if ((current.x - 1 >= 0) && (map2[current.x - 1][current.y] != 'c')) {
            Node newNode = new Node(current.x - 1, current.y, squareCost(map[current.x - 1][current.y]), current);
            openList.add(newNode);
            map2[current.x - 1][current.y] = 'o';
          }
    
          //right
          if (current.x + 1 < width && map2[current.x + 1][current.y] != 'c') {
            Node newNode = new Node(current.x + 1, current.y, squareCost(map[current.x + 1][current.y]), current);
            openList.add(newNode);
            map2[current.x + 1][current.y] = 'o';
          }
        
          map2[current.x][current.y] = 'c';
        }
        else {
          //no need to visit nodes twice in breadth-first or lowest-cost-first search

          //up
          if ((current.y - 1 >= 0) && (map2[current.x][current.y - 1] == 'x')) {
            Node newNode = new Node(current.x, current.y - 1, squareCost(map[current.x][current.y - 1]), current);
            openList.add(newNode);
            map2[current.x][current.y - 1] = 'o';
          }
    
          //down
          if ((current.y + 1 < height) && (map2[current.x][current.y + 1] == 'x')) {
            Node newNode = new Node(current.x, current.y + 1, squareCost(map[current.x][current.y + 1]), current);
            openList.add(newNode);
            map2[current.x][current.y + 1] = 'o';
          }
    
          //left
          if ((current.x - 1 >= 0) && (map2[current.x - 1][current.y] == 'x')) {
            Node newNode = new Node(current.x - 1, current.y, squareCost(map[current.x - 1][current.y]), current);
            openList.add(newNode);
            map2[current.x - 1][current.y] = 'o';
          }
    
          //right
          if ((current.x + 1 < width) && (map2[current.x + 1][current.y] == 'x')) {
            Node newNode = new Node(current.x + 1, current.y, squareCost(map[current.x + 1][current.y]), current);
            openList.add(newNode);
            map2[current.x + 1][current.y] = 'o';
          }
        
          map2[current.x][current.y] = 'c';
        }
    }
    System.out.println("path not found!");
    return;
  }

  /**
   * Get the cost to traverse a square.
   * 
   * @param c Character representing terrain type.
   * @return cost
   */
  private static int squareCost(char c) {
    int result = 0;
    switch(c) {
        case 'R': result = 1;
            break;
        case 'f': result = 2;
            break;
        case 'F': result = 4;
            break;
        case 'h': result = 5;
            break;
        case 'r': result = 7;
            break;
        case 'M': result = 10;
            break;
        case 'W': result = 10000;
            break;
    }
    return result;
  }

  /**
   * Use SearchGraphics to paint the map.
   * 
   * @param map Map of terrain types.
   * @param map2 Map of which squares have been visited.
   * @param x1 X-coordinate of starting point.
   * @param x2 X-coordinate of ending point.
   * @param y1 Y-coordinate of starting point
   * @param y2 Y-coordinate of ending point.
   * @param x List of x-coordinates of the path.
   * @param y List of y-coordinates of the path. 
   * @param length Length of the path.
   */
  public static void paint(char[][] map, char[][] map2,
      int x1, int x2, int y1, int y2,
      int[] x, int[] y, int length) {

    for (int i = 0; i < graphics.getHeight(); i++) {
        for (int j = 0; j < graphics.getWidth(); j++) {
            graphics.setSquare(j, i, map[j][i], map2[j][i]);
        }
    }
    graphics.draw(x1, x2, y1, y2, x, y, length);
  }
}

/**
 * Search tree node.
 */
class Node {
  public int x;
  public int y;
  public int cost; //path cost
  public int movementCost;
  public List<Node> path;
 
  /**
   * Constructor without path.
   * 
   * @param x Current x value.
   * @param y Current y value.
   * @param cost Cost so far.
   */
  public Node(int x, int y, int cost) {
    this.x = x;
    this.y = y;
    this.cost = cost;
    movementCost = cost;
    path = new LinkedList<Node>();
  }
  
  /**
   * Constructor without path.
   * 
   * @param x Current x value.
   * @param y Current y value.
   * @param cost Cost so far.
   * @param parent Parent to inherit path from.
   */
  public Node(int x, int y, int cost, Node parent) {
    this.x = x;
    this.y = y;
    movementCost = cost;
    this.cost = cost + parent.cost;
    path = new LinkedList<Node>();
    path.addAll(parent.path);
    path.add(parent);
  }
}

/**
 * Compares costs between nodes
 */
class NodeCostComparator implements Comparator<Node> {

  @Override
  public int compare(Node x, Node y) {
    if (x.cost < y.cost) {
      return -1;
    }
    if (x.cost > y.cost) {
      return 1;
    }
    return 0;
  }
}

/**
 * Compares distances of nodes from the goal.
 */
class NodeDistanceComparator implements Comparator<Node> {

  private int goalX;
  private int goalY;

  public NodeDistanceComparator(int x, int y) {
    goalX = x;
    goalY = y;
  }

  @Override
  public int compare(Node x, Node y) {
    if (Math.abs(x.x - goalX) + Math.abs(x.y - goalY)
        < Math.abs(y.x - goalX) + Math.abs(y.y - goalY)) {
      return -1;
    }
    if (Math.abs(x.x - goalX) + Math.abs(x.y - goalY)
        > Math.abs(y.x - goalX) + Math.abs(y.y - goalY)) {
      return 1;
    }
    return 0;
  }
}

/**
 * Comparator that chooses between nodes using the A* algorithm with Euclidean distance.
*/
class AStarEuclidean implements Comparator<Node> {

  private int goalX;
  private int goalY;

  public AStarEuclidean(int x, int y) {
    goalX = x;
    goalY = y;
  }

  @Override
  public int compare(Node x, Node y) {
    if (x.movementCost + Math.sqrt(Math.pow(x.x - goalX, 2) + Math.pow(x.y - goalY, 2))
        < y.movementCost + Math.sqrt(Math.pow(y.x - goalX, 2) + Math.pow(y.y - goalY, 2))) {
      return -1;
    }
    if (x.movementCost + Math.sqrt(Math.pow(x.x - goalX, 2) + Math.pow(x.y - goalY, 2))
        > y.movementCost + Math.sqrt(Math.pow(y.x - goalX, 2) + Math.pow(y.y - goalY, 2))) {
      return 1;
    }
    return 0;
  }
}

/**
 * Comparator that chooses between nodes using the A* algorithm with Manhattan distance.
*/
class AStarManhattan implements Comparator<Node> {

  private int goalX;
  private int goalY;

  public AStarManhattan(int x, int y) {
    goalX = x;
    goalY = y;
  }

  @Override
  public int compare(Node x, Node y) {
    int val1 = x.movementCost + Math.abs(x.x - goalX) + Math.abs(x.y - goalY);
    int val2 = y.movementCost + Math.abs(y.x - goalX) + Math.abs(y.y - goalY);
    if (val1 < val2) {
      return -1;
    }
    if (val1 > val2) {
      return 1;
    }
    return 0;
  }
}
