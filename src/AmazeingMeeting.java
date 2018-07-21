import edu.princeton.cs.algs4.BreadthFirstPaths;
import edu.princeton.cs.algs4.Graph;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;

import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class AmazeingMeeting {
    public static int n; // dimension of maze
    public static int m;
    public static int[][] inputMaze;
    public boolean[][] north;     // is there a wall to north of cell i, j
    public boolean[][] east;
    public boolean[][] south;
    public boolean[][] west;
    private boolean[][] visited;
    private boolean done = false;

    public  Graph G = new Graph(n*m); //Using a Graph to represent possible paths in the maze

    private Queue<Integer> queue = new LinkedList<>(); //Queue used for Breadth-First search
    private int paths; //Number of new paths each visited node/tile has
    private int currentCounter = 1; //Counter for depthtracking
    private int counter = 0; //As above
    private boolean newDepth = true; //Boolean for increasing depth/distance
    private boolean currentDepth = true; //Boolean for keeping track of depth
    private int pathTracker = 1; //How many current paths we are discovering with BFS
    private int drawPause = 30; //Draw delay for visual
    private int distance; //Distance variable
    private int endX; //End coordinate
    private int endY;
    private ArrayList<Point> too = new ArrayList(); //Point(x,y( of visited node/tile
    private ArrayList distanceTo = new ArrayList(); //distance from start point ArrayList

    //Initializing maze
    public AmazeingMeeting(int n, int m) {
        this.n = n;
        this.m = m;
        StdDraw.setXscale(0, n + 2);
        StdDraw.setYscale(0, m + 2);
        init();
        generate();
    }

    private void init() {
        distance = 0;
        // initialize border cells as already visited
        visited = new boolean[n + 2][m + 2];
        for (int x = 0; x < n + 2; x++) {
            visited[x][0] = true;
            visited[x][m + 1] = true;
        }
        for (int y = 0; y < m + 2; y++) {
            visited[0][y] = true;
            visited[n + 1][y] = true;
        }

        // initialize all walls as present
        // creating a n*m grid of only walls
        // (we remove walls when we read in maze from txt file)
        north = new boolean[n + 2][m + 2];
        east = new boolean[n + 2][m + 2];
        south = new boolean[n + 2][m + 2];
        west = new boolean[n + 2][m + 2];
        for (int x = 0; x < n + 2; x++) {
            for (int y = 0; y < m + 2; y++) {
                north[x][y] = true;
                east[x][y] = true;
                south[x][y] = true;
                west[x][y] = true;
            }
        }
    }

/**
 * Generate maze:
 * We generate the maze by removing walls between cells which have 1's and have adjacent cells with 1's
 * We do this by using the Graph G with n*m vertices, and creating edges for all such cells.
 * We also set the respective boarders (e.g. south) to false.
 * Each vertex V in G get a value between 0 and 599 corresponding to which index it has in the inputmaze int[][]
 * For example the vertex representing inputmaze[0][4] has value 4 while inputmaze[2][4] has value 64
 * (since it would be at position 64 if we laid out the 2x2 matrix in a 1x1 array.
 */
    public void generate() {

        int index = 0;
        for(int i = 0; i<n;i++) {
            for (int j = 0; j < m; j++) {
                index++;
                //System.out.println(index);
                if (inputMaze[i][j] == 1) {

                    if (j > 0 && inputMaze[i][j - 1] == 1) {
                        G.addEdge(index, index - 1);
                        south[i + 1][j + 1] = false;
                    }
                    if (j < m - 1 && inputMaze[i][j + 1] == 1) {
                        G.addEdge(index, index + 1);
                        north[i + 1][j + 1] = false;
                    }
                    if (i > 0 && inputMaze[i - 1][j] == 1) {
                        G.addEdge(index - m, index);
                        west[i + 1][j + 1] = false;
                    }
                    if (i < n - 1 && inputMaze[i + 1][j] == 1) {
                        G.addEdge(index + m, index);
                        east[i + 1][j + 1] = false;
                    }
                }
            }
        }
    }
    //OLD METHOD for generate() without using Graph
/*    private void generate() {

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                if (inputMaze[i][j] == 1) {
                    if (i > 0 && inputMaze[i - 1][j] == 1) west[i + 1][j + 1] = false;
                    if (i < n - 1 && inputMaze[i + 1][j] == 1) east[i + 1][j + 1] = false;
                    if (j < m - 1 && inputMaze[i][j + 1] == 1) north[i + 1][j + 1] = false;
                    if (j > 0 && inputMaze[i][j - 1] == 1) south[i + 1][j + 1] = false;
                }
            }
        }
    }*/


/**
 * SOLVE MAZE WITH BREADTH FIRST SEARCH
 * */

    private void solve(int x, int y, String color) {

        if (done || visited[x][y]) return;

        //Marking the current node/tile as visited so we don't use it again
        visited[x][y] = true;


//COLORS
        if(color=="blue") StdDraw.setPenColor(StdDraw.BLUE);
        else if(color=="red") StdDraw.setPenColor(StdDraw.RED);
//Draw current position in chosen color
        StdDraw.filledCircle(x + 0.5, y + 0.5, 0.25);
        StdDraw.show();
        StdDraw.pause(drawPause);

//Success criteria: check if we reached the middle +/- 1 slot
        success(x,y);
        if(done) {
            endX=x;
            endY=y;
            return;
        }

//BREADTH FIRST SEARCH
        while (!queue.isEmpty() && !done){
        //Set current node/tile as the one which was next in the PQ
            x = queue.remove();
            y = queue.remove();

            //Reset number of adjacent paths for the current tile
            paths=0;

            //Check which direction is not blocked and add those to the PQ
            if (!north[x][y] && !visited[x][y+1]){
                //Instead of calling Solve(x,y+1) like Depth-First Search does
                //We add them to a PQ and solve later, this is BFS
                queue.add(x);
                queue.add(y+1);
                //We keep track of the tiles and distances in separate ArrayLists
                //(which we will use later for backtracking the shortest path
                too.add(new Point(x,y+1));
                distanceTo.add(distance);
                paths++; //For each new path we +1 to path (to keep track of depth)
            }
            if (!east[x][y] && !visited[x+1][y]){
                queue.add(x+1);
                queue.add(y);
                too.add(new Point(x+1,y));
                distanceTo.add(distance);
                paths++;
            }
            if (!south[x][y] && !visited[x][y-1]){
                queue.add(x);
                queue.add(y-1);
                too.add(new Point(x,y-1));
                distanceTo.add(distance);
                paths++;
            }
            if (!west[x][y] && !visited[x-1][y]){
                queue.add(x-1);
                queue.add(y);
                too.add(new Point(x-1,y));
                distanceTo.add(distance);
                paths++;
            }

    //DEPTH TRACKING:
        //To keep track of the distance to starting point we need to
        //keep track of how many paths the BFS is working on each iteration
        //e.g. if we reach a cross section we get an additional paths to explore
        //then pathtracker++. If we reach a dead end then pathtracker--.
        //(if we have 3 new paths then pathtracker +=2

            if(paths == 2) {
                pathTracker+=1;
                counter=pathTracker;
            }
            if(paths == 3) {
                pathTracker+=2;
                counter=pathTracker;
            }
            if(paths == 0) {
                pathTracker-=1;
                counter=pathTracker;
            }
        //If we have discovered all tracks in a current depth, we add a new layer (new depth)
            if (counter==pathTracker && !currentDepth) {
                newDepth=true;
                currentCounter = counter; //To keep track of the paths we are discovering
                currentCounter--;
            }
        //As long as we are on the current depth level, we don't add a new depth
            else if (currentCounter<pathTracker) {
                newDepth=false;
                currentCounter--;
            }
        //If we reach a dead end but we're still on current depth we must now create a new depth
            if(paths == 0 && currentDepth) {
                newDepth=false;
                currentCounter=counter;
                currentCounter--;
            }
        //As long as we discover paths in current depth we do not add new layers
            if (currentCounter>0) currentDepth=true;

        //If we only follow one path (as we do initially until we reach a intersection)
            if(pathTracker==1) {
                currentDepth=false;
                newDepth=true;

         //If we add a new depth, we increase the distance from starting point with 1
            }
            if(newDepth) {
                distance++;
                //And we start then working on a new layer (unless we have only 1 path
                //as is the case initially (until we reach a intersection
                if(pathTracker>1) currentDepth=true;
            }

            //If we have discovered all new tiles in the current depth, we add a new layer
            if (currentCounter==0) {
                counter=pathTracker;
                currentDepth=false;
            }
            /*
            System.out.println("Adjacent paths: " + paths + " Number of paths: " +pathTracker);
            System.out.println("New depth: " + newDepth);
            System.out.println("current depth: " +currentDepth+ ", currentcounter: " + currentCounter);
            System.out.println("***********************");
            */

            //Recursive call for the next tile in the PQ
            solve(x, y, color);
            }
        if (done) return;
    }

    // solve the maze starting from the start state
    public void solve() {
        for (int x = 1; x <= n; x++)
            for (int y = 1; y <= m; y++)
                visited[x][y] = false;

        //First we solve for for lower left entrance of the maze
            queue.add(1);
            queue.add(2);
            too.add(new Point(1,2));
            distanceTo.add(0);
            solve(1, 2, "blue");
            backTrack(); //Method for backtracking and finding shortest path

            resetMaze(); //Reset the maze and arrays to initial conditions

        //Solve for upper right entrance of maze
            queue.add(20);
            queue.add(29);
            too.add(new Point(20,29));
            distanceTo.add(0);
            solve(20, 29, "red");
            backTrack();


        //Draw a heart in the middle
            StdDraw.setPenColor(StdDraw.RED);
            double[] xs = { endX-1,  endX-0.5, endX, endX-0.5 };
            double[] ys = {  endY+0.25, endY+0.25, endY+0.25, endY-0.25 };
            StdDraw.filledPolygon(xs, ys);

            // circles
            StdDraw.filledCircle(endX-0.25, endY+0.5, 0.5 / Math.sqrt(2));
            StdDraw.filledCircle(endX-0.75,endY+0.5, 0.5 / Math.sqrt(2));
            StdDraw.show();
    }

    public void backTrack(){
        int N = too.toArray().length;
        int x1 = (int)too.get(N-1).getX();
        int y1 = (int)too.get(N-1).getY();

        //System.out.println(distanceTo.get(too.indexOf(new Point(10,15))));
        int minDist = (int)distanceTo.get(N-1);
        for(int i=N-2; i>-1; i--){
            int x2=(int)too.get(i).getX();
            int y2=(int)too.get(i).getY();
            int minDist2 = (int)distanceTo.get(i);

            if ((x1==x2+1 && y1 == y2 && minDist2<=minDist && visited[x2][y2]) ||
                    (x1==x2 && y1 == y2+1 && minDist2<=minDist && visited[x2][y2]) ||
                    (x1==x2-1 && y1 == y2 && minDist2<=minDist && visited[x2][y2]) ||
                    (x1==x2 && y1 == y2-1 && minDist2<=minDist && visited[x2][y2])
                    ){
                StdDraw.setPenColor(StdDraw.BLACK);

                if(x1>x2) StdDraw.filledRectangle(x1, y1 + 0.5, 0.5,0.15);
                if(x1<x2) StdDraw.filledRectangle(x1+1, y1 + 0.5, 0.5, 0.15);
                if(y1>y2) StdDraw.filledRectangle(x1+0.5, y1, 0.15,0.5);
                if(y1<y2) StdDraw.filledRectangle(x1+0.5, y1 + 1, 0.15,0.5);
                StdDraw.show();
                x1=x2;
                y1=y2;
                //System.out.println(x1 +", " + y1);
            }
        }
    }

    public void solveBFS(){
        int mid = 285;

        System.out.println("Breadth-First Search. Midpoint vertex: " + mid + ". Start points: 2 and 599");

        BreadthFirstPaths bfs = new BreadthFirstPaths(G, mid);
        for (int v = 0; v < G.V(); v++) {
            if (bfs.hasPathTo(v) && v == 2 || v == 599) { //Adding criteria v==2 or 599 which are the starting points
                StdOut.printf("%d to %d (%d):  ", mid, v, bfs.distTo(v));
                for (int x : bfs.pathTo(v)) {
                    if (x == mid) StdOut.print(x);
                    else        StdOut.print("-" + x);
                }
                StdOut.println();
            }
        }
    }

//Set maze and arrays to initial conditions
    private void resetMaze(){
        distance=-1;
        queue.clear();
        done=false;
        too.clear();
        distanceTo.clear();
        //Reset visited nodes
        for (int x = 0; x < n+2; x++) {
            for (int y = 0; y < m+2; y++) {
                visited[x][y] = false;
            }
        }
    }
//If reached the middle
    private void success(int x, int y){

        if (x == n/2 && y == m/2) done = true;
        else if (x == n/2+1 && y == m/2) done = true;
        else if (x == n/2+1 && y == m/2+1)done = true;
        else if (x == n/2 && y == m/2+1)done = true;
        else if (x == n/2-1 && y == m/2)done = true;
        else if (x == n/2-1 && y == m/2-1)done = true;
        else if (x == n/2 && y == m/2-1)done = true;
        }



// draw the maze
    public void draw() {
        //Drawing the walls in the maze
        StdDraw.setPenColor(StdDraw.BLACK);
        for (int x = 1; x <= n; x++) {
            for (int y = 1; y <= m; y++) {
                if (south[x][y]) StdDraw.line(x, y, x+1, y);
                if (north[x][y]) StdDraw.line(x, y+1, x+1, y+1);
                if (west[x][y])  StdDraw.line(x, y, x, y+1);
                if (east[x][y])  StdDraw.line(x+1, y, x+1, y+1);
            }
        }
        StdDraw.show();
        StdDraw.pause(1000);
    }


    public static void main(String[] args) throws IOException{


    //20x30 input maze from text file
        n = 20;
        m = 30;
        File fileIn = new File("simple_maze_20x30.txt");
        String line;
        inputMaze = new int[n][m];

        BufferedReader br = new BufferedReader(new FileReader(fileIn));
        int linenumber = 0;
        while ((line = br.readLine()) != null){
            for(int i = 0; i < line.length(); i++){
                inputMaze[linenumber][i] = Integer.parseInt(Character.toString(line.charAt(i)));
            }
            linenumber++;
        }

    //Printing the maze to console for visual help
        for(int i = 0; i < inputMaze.length; i++){
            for(int j = 0; j < inputMaze[i].length; j++){
                if(inputMaze[i][j]>0) {
                    System.out.print("\033[31m "+inputMaze[i][j]);
                }
                else {
                    System.out.print("\033[0m "+inputMaze[i][j]);
                }
            }
            System.out.println("");
        }

//VISUAL REPRESENTATION OF SOLUTION
//Creating the maze in the maze class and solving it
        AmazeingMeeting maze = new AmazeingMeeting(n, m);
        StdDraw.enableDoubleBuffering();
        maze.draw();
        maze.solve();

//Solving using BreadthFirstPaths.java from algs4-data.zip
//Each vertex has a value representing a set of coordinates
//See generate() method for explanation
        maze.solveBFS();
    }
}