package search;

import java.io.*;
import java.util.*;
import java.util.List;
import java.util.Queue;
import java.util.LinkedList;
import java.util.Comparator;
import java.lang.Math;
import java.util.concurrent.TimeUnit;

public class Search {

    private static SearchGraphics g;

    public static void main(String[] args) {
	int Width = 0;
	int Height = 0;
	int StartX = 0;
	int StartY = 0;
	int GoalX = 0;
	int GoalY = 0;
	char[][] map = new char[0][0];

        if (args.length < 1) {
            System.out.println("map file name required\n");
            System.exit(0);
        }
        String inputfile = args[0];
        int algorithm = 0;
        if (args.length >= 2) {
            algorithm = Integer.valueOf(args[1]);
        }
        else {
            Scanner stdin = new Scanner(System.in);
            System.out.println("This is the new version. Enter algorithm number (0 = breadth first; 1 = lowest cost first; 2 = greedy best first; 3 = A* with euclidean distance; 4 = A* with manhattan distance):");
            algorithm = stdin.nextInt();
        }

	File infile = new File(args[0]);    

	//read in map
        try {
		Scanner fileScanner = new Scanner(infile);
        	Width = fileScanner.nextInt();
        	Height = fileScanner.nextInt();
        	StartX = fileScanner.nextInt();
        	StartY = fileScanner.nextInt();
        	GoalX = fileScanner.nextInt();
        	GoalY = fileScanner.nextInt();

        	map = new char[Width][Height];
        	for (int i = 0; i < Height; i++) {
			String n = fileScanner.next();
        		for (int j = 0; j < Width; j++) {
				map[j][i] = n.charAt(j);
            		}
        	} 
	} catch (FileNotFoundException e) {
	        System.out.println("file "+args[0]+" not found");
		System.exit(0);
	}

	//init graphics
	g = new SearchGraphics(Width, Height);

	//paint starting map
	paint(map, new char[Width][Height], StartX, GoalX, StartY, GoalY, new int[0], new int[0], 0);

	search(map, Height, Width, StartX, GoalX, StartY, GoalY, algorithm);
	
    }

    private static void search(char[][] map, int Height, int Width, int StartX, int GoalX, int StartY, int GoalY, int algorithm) {

	//on map2, c = closed, o = open, x = unvisited
	char[][] map2 = new char[Width][Height];
	for (int i = 0; i < Width; i++) {
		//System.out.println(String.valueOf(i));
		for (int j = 0; j < Height; j++) {
			//System.out.println("\t"+String.valueOf(j));
			if (map[i][j] == 'W') {
				map2[i][j] = 'c';
			}
			else {
				map2[i][j] = 'x';
			}
		}
	}

	Queue<Node> openlist;
	Comparator<Node> comparator;

	switch(algorithm) {
		case 1:	comparator = new NodeCostComparator();
			openlist = new PriorityQueue<Node>(11, comparator);
			break;
		case 2: comparator = new NodeDistanceComparator(GoalX, GoalY);
			openlist = new PriorityQueue<Node>(11, comparator);
			break;
		case 3: comparator = new AStarEuclidean(GoalX, GoalY);
			openlist = new PriorityQueue<Node>(11, comparator);
			break;
		case 4: comparator = new AStarManhattan(GoalX, GoalY);
			openlist = new PriorityQueue<Node>(11, comparator);
			break;
		default: openlist = new LinkedList<Node>();
			break;
	}

	//add starting node to list
	openlist.add(new Node(StartX, StartY, squarecost(map[StartX][StartY])));
	map2[StartX][StartY] = 'o';

	Node current;
	while(openlist.peek() != null) {
		//Delay to create animated effect
		try {
		    TimeUnit.MILLISECONDS.sleep(10);
		}
		catch (InterruptedException e) {
		    System.out.println("error");
		}

		//Paint map
		paint(map, map2, StartX, GoalX, StartY, GoalY, new int[0], new int[0], 0);

		//pop off next node from open list
		current = openlist.remove();

		//if goal found, stop search and print map
		if (current.x == GoalX && current.y == GoalY) {
			System.out.print("length: " + current.path.size());
			System.out.print("\ncost: " + current.cost);

			int len = current.path.size()+1;
			int[] xpath = new int[len];
			int[] ypath = new int[len];
			for (int i = 0; i < current.path.size(); i++) {
				xpath[i] = current.path.get(i).x;
				ypath[i] = current.path.get(i).y;
			}
			xpath[current.path.size()] = GoalX;
			ypath[current.path.size()] = GoalY;

			paint(map, map2, StartX, GoalX, StartY, GoalY, xpath, ypath, len);
			
			return;
		}

		//stop program if more than a million nodes are on the open list (~ 1 gig memory)
		if(openlist.size() > 1000000) {
			paint(map, map2, StartX, GoalX, StartY, GoalY, new int[0], new int[0], 0);

			System.out.println("ran out of space!");

			return;
		}

		if (algorithm > 0) {
			//up
			if (current.y-1 >= 0 && map2[current.x][current.y-1] != 'c') {
				openlist.add(new Node(current.x, current.y-1, squarecost(map[current.x][current.y-1]), current));
				map2[current.x][current.y-1] = 'o';
			}
	
			//down
			if (current.y+1 < Height && map2[current.x][current.y+1] != 'c') {
				openlist.add(new Node(current.x, current.y+1, squarecost(map[current.x][current.y+1]), current));
				map2[current.x][current.y+1] = 'o';
			}
	
			//left
			if (current.x-1 >= 0 && map2[current.x-1][current.y] != 'c') {
				openlist.add(new Node(current.x-1, current.y, squarecost(map[current.x-1][current.y]), current));
				map2[current.x-1][current.y] = 'o';
			}
	
			//right
			if (current.x+1 < Width && map2[current.x+1][current.y] != 'c') {
				openlist.add(new Node(current.x+1, current.y, squarecost(map[current.x+1][current.y]), current));
				map2[current.x+1][current.y] = 'o';
			}
		
			map2[current.x][current.y] = 'c';
		}
		else {
			//no need to visit nodes twice in breadth-first search

			//up
			if (current.y-1 >= 0 && map2[current.x][current.y-1] == 'x') {
				openlist.add(new Node(current.x, current.y-1, squarecost(map[current.x][current.y-1]), current));
				map2[current.x][current.y-1] = 'o';
			}
	
			//down
			if (current.y+1 < Height && map2[current.x][current.y+1] == 'x') {
				openlist.add(new Node(current.x, current.y+1, squarecost(map[current.x][current.y+1]), current));
				map2[current.x][current.y+1] = 'o';
			}
	
			//left
			if (current.x-1 >= 0 && map2[current.x-1][current.y] == 'x') {
				openlist.add(new Node(current.x-1, current.y, squarecost(map[current.x-1][current.y]), current));
				map2[current.x-1][current.y] = 'o';
			}
	
			//right
			if (current.x+1 < Width && map2[current.x+1][current.y] == 'x') {
				openlist.add(new Node(current.x+1, current.y, squarecost(map[current.x+1][current.y]), current));
				map2[current.x+1][current.y] = 'o';
			}
		
			map2[current.x][current.y] = 'c';
		}
 	}
	System.out.println("path not found!");
	return;
    }

    private static int squarecost(char c) {
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

    public static void paint(char[][] map, char[][] map2, int x1, int x2, int y1, int y2, int[] x, int[] y, int length) {
	for (int i = 0; i < g.height(); i++) {
		for (int j = 0; j < g.width(); j++) {
			g.setsquare(j, i, map[j][i], map2[j][i]);
		}
	}
	g.draw(x1, x2, y1, y2, x, y, length);
    }
}

class Node {
    public int x;
    public int y;
    public int cost; //path cost
    public int movementcost;
    public List<Node> path;
 
    public Node(int x, int y, int cost) {
	this.x = x;
	this.y = y;
	this.cost = cost;
	movementcost = cost;
	path = new LinkedList<Node>();
    }
    public Node(int x, int y, int cost, Node parent) {
	this.x = x;
	this.y = y;
	movementcost = cost;
	this.cost = cost + parent.cost;
	path = new LinkedList<Node>();
	path.addAll(parent.path);
	path.add(parent);
    }
}

class NodeCostComparator implements Comparator<Node> {

    @Override
    public int compare(Node x, Node y) {
	if (x.cost < y.cost)
		return -1;
	if (x.cost > y.cost)
		return 1;
	return 0;
    }
}

class NodeDistanceComparator implements Comparator<Node> {

    private int goalx;
    private int goaly;

    public NodeDistanceComparator(int x, int y) {
	goalx = x;
	goaly = y;
    }

    @Override
    public int compare(Node x, Node y) {
	if (Math.abs(x.x-goalx)+Math.abs(x.y-goaly) < Math.abs(y.x-goalx)+Math.abs(y.y-goaly))
		return -1;
	if (Math.abs(x.x-goalx)+Math.abs(x.y-goaly) > Math.abs(y.x-goalx)+Math.abs(y.y-goaly))
		return 1;
	return 0;
    }
}

class AStarEuclidean implements Comparator<Node> {

    private int goalx;
    private int goaly;

    public AStarEuclidean(int x, int y) {
	goalx = x;
	goaly = y;
    }

    @Override
    public int compare(Node x, Node y) {
	if (x.movementcost+Math.sqrt(Math.pow(x.x-goalx, 2)+Math.pow(x.y-goaly, 2)) < y.movementcost+Math.sqrt(Math.pow(y.x-goalx, 2)+Math.pow(y.y-goaly, 2)))
		return -1;
	if (x.movementcost+Math.sqrt(Math.pow(x.x-goalx, 2)+Math.pow(x.y-goaly, 2)) > y.movementcost+Math.sqrt(Math.pow(y.x-goalx, 2)+Math.pow(y.y-goaly, 2)))
		return 1;
	return 0;
    }
}

class AStarManhattan implements Comparator<Node> {

    private int goalx;
    private int goaly;

    public AStarManhattan(int x, int y) {
	goalx = x;
	goaly = y;
    }

    @Override
    public int compare(Node x, Node y) {
	int val1 = x.movementcost+Math.abs(x.x-goalx)+Math.abs(x.y-goaly);
	int val2 = y.movementcost+Math.abs(y.x-goalx)+Math.abs(y.y-goaly);
	if (val1 < val2)
		return -1;
	if (val1 > val2)
		return 1;
	return 0;
    }
}
