/******************************************************************************
 *  Compilation:  javac DepthFirstDirectedPaths.java
 *  Execution:    java DepthFirstDirectedPaths digraph.txt s
 *  Dependencies: Digraph.java Stack.java
 *  Data files:   http://algs4.cs.princeton.edu/42digraph/tinyDG.txt
 *                http://algs4.cs.princeton.edu/42digraph/mediumDG.txt
 *                http://algs4.cs.princeton.edu/42digraph/largeDG.txt
 *
 *  Determine reachability in a digraph from a given vertex using
 *  depth-first search.
 *  Runs in O(E + V) time.
 *
 *  % java DepthFirstDirectedPaths tinyDG.txt 3
 *  3 to 0:  3-5-4-2-0
 *  3 to 1:  3-5-4-2-0-1
 *  3 to 2:  3-5-4-2
 *  3 to 3:  3
 *  3 to 4:  3-5-4
 *  3 to 5:  3-5
 *  3 to 6:  not connected
 *  3 to 7:  not connected
 *  3 to 8:  not connected
 *  3 to 9:  not connected
 *  3 to 10:  not connected
 *  3 to 11:  not connected
 *  3 to 12:  not connected
 *
 ******************************************************************************/

package edu.princeton.cs.algs4;
import java.io.File;

/**
 *  The {@code DepthFirstDirectedPaths} class represents a data type for finding
 *  directed paths from a source vertex <em>s</em> to every
 *  other vertex in the digraph.
 *  <p>
 *  This implementation uses depth-first search.
 *  The constructor takes time proportional to <em>V</em> + <em>E</em>,
 *  where <em>V</em> is the number of vertices and <em>E</em> is the number of edges.
 *  It uses extra space (not including the graph) proportional to <em>V</em>.
 *  <p>
 *  For additional documentation,  
 *  see <a href="http://algs4.cs.princeton.edu/42digraph">Section 4.2</a> of  
 *  <i>Algorithms, 4th Edition</i> by Robert Sedgewick and Kevin Wayne. 
 *
 *  @author Robert Sedgewick
 *  @author Kevin Wayne
 */
public class DepthFirstDirectedPaths {
    private boolean[] marked;  // marked[v] = true if v is reachable from s
    private int[] edgeTo;      // edgeTo[v] = last edge on path from s to v
    private final int s;       // source vertex
    public static Stack revPostOrder = new Stack();


    /**
     * Computes a directed path from {@code s} to every other vertex in digraph {@code G}.
     * @param G the digraph
     * @param s the source vertex
     */
    public DepthFirstDirectedPaths(Digraph G, int s) {
        marked = new boolean[G.V()];
        edgeTo = new int[G.V()];
        this.s = s;
        dfs(G, s);
    }

    private void dfs(Digraph G, int v) {
        marked[v] = true;
        //Her skjer preorder, naar vi besoeker naboene
        //Preorder: print the nodes in the order in which DFS arrives at them.
       // System.out.println(v);

        for (int w : G.adj(v)) {
            if (!marked[w]) {
                edgeTo[w] = v;

                dfs(G, w);
            }
        }

        //Her er postorder (etter naboene er besoekt)
        //Postorder: print the nodes in the order in which DFS leaves them.
        //System.out.println(v);

        //Reverse Postorder
        revPostOrder.push(v);
    }

    /**
     * Is there a directed path from the source vertex {@code s} to vertex {@code v}?
     * @param v the vertex
     * @return {@code true} if there is a directed path from the source
     *   vertex {@code s} to vertex {@code v}, {@code false} otherwise
     */
    public boolean hasPathTo(int v) {
        return marked[v];
    }

    
    /**
     * Returns a directed path from the source vertex {@code s} to vertex {@code v}, or
     * {@code null} if no such path.
     * @param v the vertex
     * @return the sequence of vertices on a directed path from the source vertex
     *   {@code s} to vertex {@code v}, as an Iterable
     */
    public Iterable<Integer> pathTo(int v) {
        if (!hasPathTo(v)) return null;
        Stack<Integer> path = new Stack<Integer>();
        for (int x = v; x != s; x = edgeTo[x])
            path.push(x);
        path.push(s);
        return path;
    }

    /**
     * Unit tests the {@code DepthFirstDirectedPaths} data type.
     *
     * @param args the command-line arguments
     */
    public static void main(String[] args) {


        File file = new File("tinyDAG.txt");

        In in = new In(file);
        Digraph G = new Digraph(in);


        int s = 6;

        DepthFirstDirectedPaths dfs = new DepthFirstDirectedPaths(G, s);
        while (!revPostOrder.isEmpty()){
            System.out.println(revPostOrder.pop());
        }

/*
        System.out.println("Order which arrives target: ");
        for (int v = 0; v < G.V(); v++) {
            dfs = new DepthFirstDirectedPaths(G, v);
            if (dfs.hasPathTo(s)) {
                StdOut.printf("%d to %d:  ", v, s);
                for (int x : dfs.pathTo(s)) {
                    if (x == s) StdOut.print(x);
                    else StdOut.print(x + "-");
                }
                StdOut.println();
            }
        }
        dfs = new DepthFirstDirectedPaths(G, s);

        System.out.println("Order which leaves target: ");
        for (int v = 0; v < G.V(); v++) {
            if (dfs.hasPathTo(v)) {
                StdOut.printf("%d to %d:  ", s, v);
                for (int x : dfs.pathTo(v)) {
                    if (x == s) StdOut.print(x);
                    else        StdOut.print("-" + x);
                }
                StdOut.println();
            }
        }
*/
    }

}

/******************************************************************************
 *  Copyright 2002-2016, Robert Sedgewick and Kevin Wayne.
 *
 *  This file is part of algs4.jar, which accompanies the textbook
 *
 *      Algorithms, 4th edition by Robert Sedgewick and Kevin Wayne,
 *      Addison-Wesley Professional, 2011, ISBN 0-321-57351-X.
 *      http://algs4.cs.princeton.edu
 *
 *
 *  algs4.jar is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  algs4.jar is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with algs4.jar.  If not, see http://www.gnu.org/licenses.
 ******************************************************************************/
