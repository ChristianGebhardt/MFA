package de.lmu.ifi.mfa;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.Observable;

/**
 *  The <tt>FlowNetwork</tt> class is an implementation a flow network interface {@link IFlowNetwork}.
 *  It supports the following two primary functionalities: create and manipulate a flow
 *  network, evaluate the maximum flow on the network.
 *  <p>
 *  The whole functionality is based only on non-negative integer values as identifiers.
 *  The internal data structure uses protected classes to represent a graph, vertex, or edge.
 *  <p>
 *  The class implements methods to add and remove vertices or edges as well as
 *  methods to set source and sink vertex. It contains two methods that implement
 *  Dinic's and Goldberg-Tarjan's maximum flow algorithms to compute the maximum
 *  flow in O(n²m) and O(n³) time, respectively.
 *  <p>
 *  Additionally, there are methods to save and load the flow network.
 *  Furthermore, the interface provides methods to query data about the current
 *  state of the flow network in order to connect a graphical user interface.
 *  <p>
 *  For additional information, see <a href="https://github.com/ChristianGebhardt/mfa">MFA project</a>
 *  by Christian Gebhardt on Github.
 *  
 *
 * @author  Christian Gebhardt
 * @version 1.0.1
 * @since   2016-09-03
 */
public class FlowNetwork extends Observable implements IFlowNetwork, Serializable {

	//constant values
	private static final long serialVersionUID = 1L;
	private static final String NEWLINE = System.getProperty("line.separator");
	
	//main variables
	private int sourceId;
	private int sinkId;
	private Graph graph;
	private int maxFlow;
	//variables for view
	private String prompt;
	private boolean drawGraph;
	private boolean updateGraph;
	
	/**
     * The constructor creates an empty flow network. Therefore, it creates an empty directed graph
     * as internal data structure and it assigns default values to all variables.
	 */
	public FlowNetwork() {
		this.sourceId = -1;
		this.sinkId = -1;
		this.graph = new Graph();
		this.maxFlow = 0;
		this.prompt = "";
		this.drawGraph = false;
		this.updateGraph = false;
	}
	
	//commented in interface
	public void setSource(int sourceId) {
		setSource(sourceId,true);
	}
	/**
	 * Specify a vertex to be the source of the flow network. When the vertex does not
	 * exist, nothing happens.
	 * 
	 * @param sourceId the source vertex identifier. It has to be a valid identifier in the graph.
	 * @param draw flag to update and redraw the graph.
	 */
	public void setSource(int sourceId, boolean draw) {
		if (sourceId >= 0) {
			if (sourceId != this.sinkId) {
				this.sourceId = sourceId;
				graph.addVertex(sourceId);
				prompt = "Source vertex set to be vertex "+sourceId+".";
			} else {
				this.sourceId = sourceId;
				graph.addVertex(sourceId);
				this.sinkId = -1;
				prompt = "Source vertex set to be vertex "+sourceId+"."+NEWLINE+"Source and sink vertex have to be different.";
			}
		} else {
			prompt = "Source identifier has to be a valid vertex label."+NEWLINE+"(use a non-negative integer: 0,1,2,3,...)";
		}
		updateGraph = draw;
		drawGraph = draw;
		setChanged();
	    notifyObservers();	
	}
	
	//commented in interface
	public int getSource() {
		return sourceId;
	}
	
	//commented in interface
	public void setSink(int sinkId) {
		setSink(sinkId,true);
	}
	/**
	 * Specify a vertex to be the sink of the flow network. When the vertex does not
	 * exist, nothing happens.
	 * 
	 * @param sinkId the sink vertex identifier. It has to be a valid identifier in the graph.
	 * @param draw flag to update and redraw the graph.
	 */
	public void setSink(int sinkId, boolean draw) {
		if (sinkId >= 0) {
			if (sinkId != this.sourceId) {
				this.sinkId = sinkId;
				graph.addVertex(sinkId);
				prompt = "Sink vertex set to be vertex "+sinkId+".";
			} else {
				this.sinkId = sinkId;
				graph.addVertex(sinkId);
				this.sourceId = -1;
				prompt = "Sink vertex set to be vertex "+sinkId+"."+NEWLINE+"Source and sink vertex have to be different.";
			}
		} else {
			prompt = "Sink identifier has to be a valid vertex label."+NEWLINE+"(use a non-negative integer: 0,1,2,3,...)";
		}
		updateGraph = draw;
		drawGraph = draw;
		setChanged();
	    notifyObservers();	
	}
	
	//commented in interface
	public int getSink() {
		return sinkId;
	}
	
	//commented in interface
	public void addVertex(int vertexId) {
		addVertex(vertexId,true);
	}
	/**
	 * Add a vertex to the graph of the flow network. When the vertex already
	 * exists, nothing happens.
	 * 
	 * @param vertexId the vertex identifier. It has to be non-negative
	 * 				   integer, otherwise it shows a failure message.
	 * @param draw flag to update and redraw the graph.
	 */
	public void addVertex(int vertexId, boolean draw) {
		if (vertexId >= 0) {
			boolean success = graph.addVertex(vertexId);
			if (success) {
				prompt = "Vertex "+vertexId+" added to Graph.";
			} else {
				prompt = "Vertex "+vertexId+" already exists in Graph.";
			}
		} else {
			prompt = "Vertex identifier has to be a valid vertex label."+NEWLINE+"(use a non-negative integer: 0,1,2,3,...)";
		}
		updateGraph = draw;
		drawGraph = draw;
		setChanged();
	    notifyObservers();
	}
	
	//commented in interface
	public void removeVertex(int vertexId) {
		removeVertex(vertexId,true);
	}
	/**
	 * Remove a vertex from the graph of the flow network. When the vertex has incoming
	 * and outgoing edges, all the adjacent edges are also removed.
	 * 
	 * @param vertexId the vertex identifier. It has to be non-negative
	 * 				   integer that exists in the graph, otherwise it shows a failure message.
	 * @param draw flag to update and redraw the graph.
	 */
	public void removeVertex(int vertexId, boolean draw) {
		if (vertexId >= 0) {
			if (graph.containsVertex(vertexId)) {
				boolean success = graph.removeVertex(vertexId);
				if (success) {
					if(vertexId == sourceId)
						sourceId = -1;
					if(vertexId == sinkId)
						sinkId = -1;
					prompt = "Vertex "+vertexId+" removed from graph.";
				} else {
					prompt = "Vertex "+vertexId+" cannot be removed from graph completely.";
				}
			} else {
				prompt = "Graph does not contain vertex "+vertexId+".";
			}
		} else {
			prompt = "Vertex identifier has to be a valid vertex label."+NEWLINE+"(use a non-negative integer: 0,1,2,3,...)";
		}
		updateGraph = draw;
		drawGraph = draw;
		setChanged();
	    notifyObservers();
	}
	
	//commented in interface
	public void addEdge(int vertexId1, int vertexId2, int capacity) {
		addEdge(vertexId1,vertexId2,capacity,true);
	}
	/**
	 * Add an edge to the graph of the flow network. When the edge already exists, nothing happens.
	 * 
	 * @param vertexId1 the start vertex identifier of the edge. When the vertex not already exists, it is added to the graph.
	 * @param vertexId2 the end vertex identifier of the edge. When the vertex not already exists, it is added to the graph.
	 * @param capacity the maximum flow capacity of the edge. It has to be a positive integer, otherwise it shows a failure message.
	 * @param draw flag to update and redraw the graph.
	 */
	public void addEdge(int vertexId1, int vertexId2, int capacity, boolean draw) {
		if (vertexId1 >= 0 && vertexId2 >= 0 && capacity>=1) {
			if (vertexId1 != vertexId2) {
				boolean success = graph.addEdge(vertexId1, vertexId2, capacity);
				if (success) {
					prompt = "Edge ("+vertexId1+","+vertexId2+") added to Graph.";
				} else {
					prompt = "Edge ("+vertexId1+","+vertexId2+") not added to Graph.";
				}
			} else {
				prompt = "Vertex identifiers have to be different.";
			}
			
		} else if (vertexId1 < 0 || vertexId2 < 0) {
			prompt = "Vertex identifiers have to be a valid vertex label."+NEWLINE+"(use a non-negative integer: 0,1,2,3,...)";
		} else if (capacity < 1) {
			prompt = "Capacity has to be an integer larger than zero."+NEWLINE+"(use a positive integer: 1,2,3,4,...)";
		} else {
			prompt = "Adding edge failed.";
		}
		updateGraph = draw;
		drawGraph = draw;
		setChanged();
	    notifyObservers();
	}
	
	//commented in interface
	public void removeEdge(int vertexId1, int vertexId2) {
		removeEdge(vertexId1,vertexId2,true);
	}
	/**
	 * Remove an edge from the graph of the flow network. The adjacent
	 * vertices are untouched. When the edge does not exists, nothing happens. 
	 * 
	 * @param vertexId1 the start vertex identifier of the edge.
	 * @param vertexId2 the end vertex identifier of the edge.
	 * @param draw flag to update and redraw the graph.
	 */
	public void removeEdge(int vertexId1, int vertexId2, boolean draw) {
		if (vertexId1 >= 0 && vertexId2 >= 0) {
			boolean success = graph.removeEdge(vertexId1, vertexId2);
			if (success) {
				prompt = "Edge ("+vertexId1+","+vertexId2+") removed from Graph.";
			} else {
				prompt = "Edge ("+vertexId1+","+vertexId2+") not removed from Graph.";
			}
			
		} else {
			prompt = "Vertex identifiers have to be a valid vertex label."+NEWLINE+"(use a non-negative integer: 0,1,2,3,...)";
		}
		updateGraph = draw;
		drawGraph = draw;
		setChanged();
	    notifyObservers();
	}
	
	//commented in interface
	public int dinic() {
		maxFlow = 0;
		if (this.getSource()>=0 && this.getSink()>=0) {
			//initialize
			graph.resetFlow();
			graph.buildResidualGraph();
			int distance = graph.buildLayeredNetwork(sourceId, sinkId);
			int deltaFlow = 0;
			//main loop
			while (distance > 0) {
				boolean pathFound = graph.searchAugmentingPath(sourceId, sinkId);
				if(pathFound) {
					deltaFlow = graph.updateMinFlowIncrement();
					maxFlow += deltaFlow;
				} else {
					distance = graph.buildLayeredNetwork(sourceId, sinkId);
				}
				try {
					Thread.sleep(1);
				} catch (Exception ex) {}
			}
			//return output to observers
			this.prompt = "Dinic: maximum flow F="+maxFlow+".";
			updateGraph = true;
			drawGraph = false;
		} else {
			this.prompt = "Dinic: maximum flow F="+maxFlow+"."+NEWLINE+"(No valid source or sink)";
			updateGraph = false;
			drawGraph = false;
		}
		setChanged();
	    notifyObservers();
		return maxFlow;
	}
	
	//commented in interface
	public int goldbergTarjan() {
		maxFlow = 0;
		if (this.getSource()>=0 && this.getSink()>=0) {
			//initialize
			graph.resetFlow();
			graph.buildResidualGraph();
			graph.resetExcess(sourceId);
			graph.initializeLabels(sourceId);
			int queueLength = graph.initialPush(sourceId, sinkId);
			//main loop
			while (queueLength>0) {
				queueLength = graph.dischargeQueue();
			}
			maxFlow = graph.getOutFlow(sourceId)-graph.getInFlow(sourceId);
			//return output to observers
			this.prompt = "Goldberg-Tarjan: maximum flow F="+maxFlow+".";
			updateGraph = true;
			drawGraph = false;
		} else {
			this.prompt = "Goldberg-Tarjan: maximum flow F="+maxFlow+"."+NEWLINE+"(No valid source or sink)";
			updateGraph = false;
			drawGraph = false;
		}
		setChanged();
	    notifyObservers();
		return maxFlow;
	}
	
	//commented in interface
	public void resetNetwork() {
		this.sourceId = -1;
		this.sinkId = -1;
		this.graph = new Graph();
		this.maxFlow = 0;
		this.prompt = "Flow network reset.";
		updateGraph = true;
		drawGraph = true;
		setChanged();
	    notifyObservers();
	}
	
	//commented in interface
	public void loadNetwork(File file) {
		try {
            FileInputStream fis = new FileInputStream(file);
            ObjectInputStream ois = new ObjectInputStream(fis);
            FlowNetwork loadNetwork = (FlowNetwork)ois.readObject();
            ois.close();
            fis.close();
            //updated the variables with the loaded data
            this.sourceId = loadNetwork.sourceId;
    		this.sinkId = loadNetwork.sinkId;
    		this.graph = loadNetwork.graph;
    		this.maxFlow = loadNetwork.maxFlow;
            this.prompt = "Flow network loaded ("+file.getName()+").";
		} catch (FileNotFoundException ex) {
        	this.prompt = "Flow network not loaded (FileNotFoundException).";
        } catch (IOException e) {
        	this.prompt = "Flow network not loaded (IOException).";
        } catch (ClassNotFoundException e) {
        	this.prompt = "Flow network not loaded (ClassNotFoundException).";
        }    
		updateGraph = true;
		drawGraph = true;
		setChanged();
	    notifyObservers();
	}
	
	//commented in interface
	public void saveNetwork(File file) {
		try {
            FileOutputStream fos = new FileOutputStream(file);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(this);
            oos.close();
            fos.close();
            this.prompt = "Flow network saved ("+file.getName()+").";
        } catch (FileNotFoundException ex) {
        	this.prompt = "Flow network not saved (FileNotFoundException).";
        } catch (IOException ex) {
        	this.prompt = "Flow network not saved (IOException).";
        }    
		updateGraph = false;
		drawGraph = false;
		setChanged();
	    notifyObservers();
	}
	
	//commented in interface
	public String getPrompt() {
		return prompt;
	}
	
	//commented in interface
	public String displayFlowNetwork() {
		StringBuilder s = new StringBuilder();
		//build string with basic information (source, sink, total flow)
		s.append("FLOW NETWORK" + NEWLINE);
		s.append("============" + NEWLINE);
		if (this.sourceId >= 0) {
			s.append("Source vertex: " + this.sourceId + " (green)" + NEWLINE);
		} else {
			s.append("Source vertex: -" + NEWLINE);
		}
		if (this.sinkId >= 0) {
			s.append("Sink vertex: " + this.sinkId + " (red)" + NEWLINE);
		} else {
			s.append("Sink vertex: -" + NEWLINE);
		}
		if (this.maxFlow > 0) {
			s.append("Maximum flow: " + this.maxFlow + NEWLINE);
		} else {
			s.append("Maximum flow: -" + NEWLINE);
		}
        s.append("---------------------------------------------------------------"+NEWLINE);
        //append string representation of the graph as adjacent lists
        s.append(graph.graphToString());
        
        return s.toString();
	}

	//commented in interface
	public LinkedList<Integer[]> getGraphData() {
		return graph.getGraphData();
	}
	
	//commented in interface
	public LinkedList<Integer> getVertexIndices() {
		return graph.getVertexIndices();
	}
	
	//commented in interface
	public void updateGraph() {
		updateGraph = true;
		drawGraph = false;
		setChanged();
	    notifyObservers();
	}
	
	//commented in interface
	public boolean isUpdateGraph() {
		return updateGraph;
	}
	
	//commented in interface
	public void drawGraph() {
		updateGraph = true;
		drawGraph = true;
		setChanged();
	    notifyObservers();
	}
	
	//commented in interface
	public boolean isDrawGraph() {
		return drawGraph;
	}
    
}
