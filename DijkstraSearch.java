/* 
 */ 

import java.io.*; 
import java.awt.*; 
import javax.swing.*; 
import trubgp.*;  // TRU Board Games Playground package 


public class DijkstraSearch 
{ 
     static Board board;  // Game board 
     static Graph graph; 
     static int SIZE = 20;
     static NodePriorityQueueVertex[] nodes;
     static StackVertex pathStack;
     static PriorityQueue<VertexString> queue;
     
     public static void main(String[] args) 
     { 
          // Creat a game board 
          create(); 
     } 
     // Create a new board 
     static void create()
     { 
          // Construct a new board 
          board = new Board(SIZE, SIZE, 40*SIZE, 25*SIZE, "Line", Color.WHITE);  // Line or NoLine 
          board.setTitle("Dijkstra's Algorithm"); 
          
          board.button1SetName("Read a graph data file"); 
          board.button1ClickEventListener(new BGPEventListener() { @Override public void clicked(int row, int col) { 
               read(); 
          }}); 
          
          board.button2SetName("Calculate shortest path StartNode|EndNode"); 
          board.button2ClickEventListener(new BGPEventListener() { @Override public void clicked(int row, int col) { 
               search(); 
          }}); 
          
          board.setText("graph.txt"); 
     } 
     
     
     static void read() 
     { 
          String fileName; 
          String line; 
          
          try { 
               fileName = board.getText(); 
               
               // FileReader reads text files in the default encoding. 
               FileReader fileReader = new FileReader(fileName); 
               
               // Always wrap FileReader in BufferedReader. 
               BufferedReader bufferedReader = new BufferedReader(fileReader); 
               
               String words[]; 
               
               // Read the number of nodes, and create an empty tree 
               
               while((line = bufferedReader.readLine()) != null) { 
                    line = line.trim(); 
                    if (line.length() == 0) 
                         continue; 
                    if (line.charAt(0) == '/' && line.charAt(1) == '/') 
                         continue; 
                    
                    graph = new Graph(Integer.parseInt(line));
                    nodes = new NodePriorityQueueVertex[(Integer.parseInt(line))];
                    
                    break; 
               } 
               
               // Read node contents and keep them in the graph 
               
               int id = 0; 
               while(id < graph.size()) { 
                    line = bufferedReader.readLine(); 
                    line = line.trim(); 
                    if (line.length() == 0) 
                         continue; 
                    if (line.charAt(0) == '/' && line.charAt(1) == '/') 
                         continue; 
                    
                    words = line.split("[ \t]+ ");  // Should be an integer(VertexString)
                    VertexString vs = new VertexString(Integer.parseInt(words[0]), words[1]);
                    
                    graph.keepVertex(Integer.parseInt(words[0]), vs);
                    NodePriorityQueueVertex node = new NodePriorityQueueVertex(vs);
                    nodes[id] = node;
                      
                    //System.out.println(nodes[id].getContent().getContent());
                    // Checking for what is in the array
                    //System.out.print(nodes[id].getContent()); // Gets the content of a vertex string from the array
                    //System.out.println(graph.find(nodes[id].getContent().getId())); // content from the graph 
                    //System.out.println(graph.find(vs.getContent()));
                    //System.out.println(" " + nodes[id].getId());
                    //System.out.println(id);
                    
                    //System.out.println();
                    
                    id++; 
               }
               
               // Read adjaceny information and keep it in the tree 
               
               String costs[]; 
               
               id = 0; 
               while(id < graph.size()) { 
                    line = bufferedReader.readLine(); 
                    line = line.trim(); 
                    if (line.length() == 0) 
                         continue; 
                    if (line.charAt(0) == '/' && line.charAt(1) == '/') 
                         continue; 
                    
                    costs = line.split("[ \t]+"); 
                    for(int i = 0; i < graph.size(); i++){
                         graph.setNeighbors(id, i, Double.parseDouble(costs[i]));//set the neighbours   
                    }
                    id++; 
               }    
               
               // Always close files. 
               bufferedReader.close();   
               
               // Display the tree on the board 
               displayGraph(); 
               
               JOptionPane.showMessageDialog(null, "Reading succesfull. Enter a starting and ending point"); 
               
               // Just for searching 
               board.setText("A D"); 
          } 
          catch(FileNotFoundException ex) { 
               JOptionPane.showMessageDialog(null, "File not found"); 
          } 
          catch(IOException ex) { 
               JOptionPane.showMessageDialog(null, "File i/o error"); 
          } 
     }
     
     static void displayGraph() 
     { 
          for (int col = 1; col < graph.size() + 1; col++) { 
               board.cellContent(0, col, "" + (col - 1)); 
               board.cellBackgroundColor(0, col, Color.YELLOW); 
          } 
          
          for (int row = 1; row < graph.size() + 1; row++) { 
               board.cellContent(row, 0, "" + (row -1)); 
               board.cellBackgroundColor(row, 0, Color.YELLOW); 
               for (int col = 1; col < graph.size()+1; col++) { 
                    board.cellContent(row, col, "" + graph.cost(row-1, col-1)); 
                    board.cellBackgroundColor(row, col, Color.CYAN); 
               } 
          } 
          
          for (int row = 1; row < graph.size() + 1; row++) { 
               board.cellContent(row, graph.size()+2, graph.find(row-1).getContent()); 
               board.cellBackgroundColor(row, graph.size()+2, Color.CYAN); 
          } 
          
          for (int row = graph.size() + 1; row < SIZE; row++) { 
               for (int col = 0; col < SIZE; col++) { 
                    board.cellContent(row, col, ""); 
                    board.cellBackgroundColor(row, col, Color.WHITE); 
               } 
          } 
     }
     
     static void search() 
     { 
          // read the text from the text field 
          String line = board.getText().trim(); 
          if (line.length() == 0) return; 
          String words[] = line.split(" "); 
          if (words.length < 2) return; 
          
          String startNodeString = words[0]; 
          String endNodeString = words[1]; 
          
          queue = new PriorityQueue<VertexString>();
          
          
          graph.reset();
          
          int col = 0;
          VertexString startVertex = graph.find(startNodeString);
        
          
          startVertex.setParent(null);
          nodes[startVertex.getId()].setPriority(0);
          
          queue.addElement(nodes[startVertex.getId()]);
          
          while(!queue.isEmpty())
          {

               VertexString vertex = queue.removeMin();
               NodePriorityQueueVertex node = nodes[graph.find(vertex.getContent()).getId()];
               
               board.cellContent(SIZE-1, col, "" + vertex.getId() + ":" + vertex.getContent()); 
               board.cellBackgroundColor(SIZE-1, col, Color.CYAN); 
               col++; 
               

               if(node.getContent().getContent().equals(endNodeString)) { 
                    JOptionPane.showMessageDialog(null, "Path Found " + endNodeString + "\nPath: " + buildPath(node)); 
                    return; 
               } 
               
               boolean[] neighbors =  graph.getNeighbors(vertex.getId());
               

               for (int i = 0; i < neighbors.length; i++) { 

                    if(neighbors[i] && !graph.find(i).isVisited()) {

                         if(graph.find(i).isExpanded()) {

                              if((graph.cost(vertex.getId(), i) + node.getPriority()) < nodes[i].getPriority()) {
                                   nodes[i].setPriority(graph.cost(vertex.getId(), i) + node.getPriority());
                                   graph.find(i).setParent(vertex);
                                   graph.find(i).expanded();
                                   queue.update(nodes[i]);
                              }                              
                         }

                         else {
                              graph.find(i).expanded();
                              graph.find(i).setParent(graph.find(vertex.getId()));
                              nodes[i].setPriority(graph.cost(vertex.getId(), i) + node.getPriority());
                              queue.addElement(nodes[i]);
                         }
                    }
               }
               vertex.visited();
          }  
     }
     
     /* Builds a String out of the content of the Vertices 
      * in the path
      */ 
     static String buildPath(NodePriorityQueueVertex node) {
          pathStack = new StackVertex();
          VertexString target = node.getContent();
          String path = "";
          
          // Push every node into stack into stack
          while(target != null) {
               pathStack.push(target);
               target = (VertexString)target.getParent();
          }
          
          // Pop from stack and make a string of content
          while(!pathStack.isEmpty())
               path += pathStack.pop().getContent() + "  ";
          return path;
     }
}
