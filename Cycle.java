/**
 * Author: Yutong Wu
 * Email: yw6228@rit.edu
 * Description: This program constructs a graph based on the file input, then does a BFS and determine
 * whether the graph is bipartite and whether it contains a cycle
 */

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

public class Cycle {

    /**
     * This method reads from a given input file and construct a graph based on the file.
     * @param filename The input file
     * @return An array holding the constructed graph
     */
    public static Node[] readInput(String filename) throws IOException {
        BufferedReader br = null;

        br = new BufferedReader(new FileReader(filename));
        String line = br.readLine();
        int numNode = Integer.parseInt(line);
        Node[] nodeList = new Node[numNode + 1];
        for(int i = 1; i <= numNode; i++){
            //Initialize the nodes in the graph, no edges are connecting the nodes yet
            nodeList[i] = new Node(i);
        }
        line = br.readLine();
        int lineNum = 1;
        while(line != null){
            String content[] = line.split(":");
            if(!content[1].equals("")){
                String[] edges = content[1].split(" ");
                for(String edge : edges){
                    if(edge.equals("")){
                        continue;
                    }
                    int edgeNum = Integer.parseInt(edge);
                    nodeList[lineNum].addEdge(nodeList[edgeNum]);
                }
            }
            line = br.readLine();
            lineNum++;
        }
        br.close();
        return nodeList;

    }

    /**
     * This method displays the graph in the format of "Node[num]: adj"
     * @param nodeList The list containing the graph
     */
    public static void nodeListToString(Node nodeList[]){
        System.out.println(nodeList.length - 1);
        for(int i = 1; i < nodeList.length; i++){
            System.out.println(nodeList[i].toString());
        }
    }

    /**
     * This function construct the BFS tree(s) according to the graph, it finds a node which has not been
     * assigned a layer, and run the BFS() function on that node.
     * @param nodeList The graph
     */
    public static void findBFS(Node nodeList[]){
        int componentNum = 1;
        for(int i = 1; i < nodeList.length; i++){
            if(nodeList[i].layer == -1){
                BFS(nodeList, componentNum, nodeList[i].id);
                componentNum++;
            }
        }
    }

    /**
     * This method constructs one component based on a starting node, and also finds whether the
     * componenet is bipartite along the way. It then display the result of the BFS and bipartite check
     * to the standard output
     * @param nodeList The graph
     * @param componentNum The number of the component(for display purposes)
     * @param startNode The index of the starting node
     */
    public static void BFS(Node nodeList[], int componentNum, int startNode){
        System.out.println("\nconnected component "+componentNum+":");
        int parent[] = new int[nodeList.length];
        Arrays.fill(parent, -1);
        Queue<Integer> queue = new LinkedList<Integer>();
        queue.add(startNode);
        nodeList[startNode].layer = 0;
        boolean bipartite = true;
        boolean cycle = false;
        while(!queue.isEmpty()){
            int currNodeNum = queue.poll();
            System.out.print(currNodeNum+"("+nodeList[currNodeNum].layer+") ");
            for(Node adj: nodeList[currNodeNum].adj){
                if(adj.layer == -1){
                    parent[adj.id] = currNodeNum;
                    queue.add(adj.id);
                    adj.layer = nodeList[currNodeNum].layer + 1;
                }else if(cycle && !bipartite){
                    //If there is already a cycle and it has been determined that the graph is not bipartite
                    //there is no need to check whether the graph is bipartite or contains a cycle
                    continue;
                } else if(adj.layer == nodeList[currNodeNum].layer){
                    //Checks whether the graph is bipartite based on the fact that in a bipartite graph, two
                    //nodes from the same layer cannot be connected
                    //Also if two nodes in the same layer are connected, the graph will contain an
                    //odd length cycle based on Theorem 3.15, so cycle exists
                    bipartite = false;
                    cycle = true;
                }else if(parent[adj.id] != currNodeNum && parent[currNodeNum] != adj.id){
                    //Check whether the graph contains a cycle, if this adj is not the parent of the
                    //current node, or if the current node is not the parent of this adj, then this
                    //two nodes forms a cycle
                    cycle = true;
                }
            }
        }
        if(bipartite){
            System.out.println("\nbipartite");
        }else{
            System.out.println("\nnot bipartite");
        }
        if(cycle){
            System.out.println("cycle exists");
        }else{
            System.out.println("acyclic");
        }
    }

    public static void main(String args[]) throws IOException {
        if(args.length < 1){
            System.out.println("Usage: Graph input");
        }else{
            String input = args[0];
            Node[] nodeList = readInput(input);
            nodeListToString(nodeList);
            findBFS(nodeList);
        }
    }
}
