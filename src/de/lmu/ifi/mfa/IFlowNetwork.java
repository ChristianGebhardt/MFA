package de.lmu.ifi.mfa;

import java.io.File;
import java.util.LinkedList;

/**
 *  The <tt>IFlowNetwork</tt> interface represents a flow network consisting of
 *  a directed graph of vertices.
 *  It supports the following two primary functionalities: create and manipulate a flow
 *  network, evaluate the maximum flow on the network.
 *  <p>
 *  The interface provides methods to add and remove vertices or edges as well as
 *  methods to set source and sink vertex. It contains two methods that implement
 *  Dinic's and Goldberg-Tarjan's maximum flow algorithms to compute the maximum
 *  flow in O(n&sup2;m) and O(n&sup3;) time, respectively.
 *  <p>
 *  Additionally, there are methods to save and load the flow network.
 *  Furthermore, the interface provides methods to query data about the current
 *  state of the flow network in order to connect a graphical user interface.
 *  <p>
 *  For additional information, see <a href="https://github.com/ChristianGebhardt/mfa">MFA</a>
 *  by Christian Gebhardt on Github.
 *  
 *
 * @author  Christian Gebhardt
 * @version 1.0.1
 * @since   2016-09-03
 */
public interface IFlowNetwork {

	/*********************************************************
	 * Vertex
	 *********************************************************/
	/**
	 * Add a vertex to the graph of the flow network. When the vertex already
	 * exists, nothing happens.
	 * 
	 * @param vertexId the vertex identifier. It has to be non-negative
	 * 				   integer, otherwise it shows a failure message.
	 */
	void addVertex(int vertexId);
	/**
	 * Remove a vertex from the graph of the flow network. When the vertex has incoming
	 * and outgoing edges, all the adjacent edges are also removed.
	 * 
	 * @param vertexId the vertex identifier. It has to be non-negative
	 * 				   integer that exists in the graph, otherwise it shows a failure message.
	 */
	void removeVertex(int vertexId);

	/*********************************************************
	 * Edge
	 *********************************************************/
	/**
	 * Add an edge to the graph of the flow network. When the edge already exists, nothing happens.
	 * 
	 * @param vertexId1 the start vertex identifier of the edge. When the vertex not already exists, it is added to the graph.
	 * @param vertexId2 the end vertex identifier of the edge. When the vertex not already exists, it is added to the graph.
	 * @param capacity the maximum flow capacity of the edge. It has to be a positive integer, otherwise it shows a failure message.
	 */
	void addEdge(int vertexId1, int vertexId2, int capacity);
	
	/**
	 * Remove an edge from the graph of the flow network. The adjacent
	 * vertices are untouched. When the edge does not exists, nothing happens. 
	 * 
	 * @param vertexId1 the start vertex identifier of the edge.
	 * @param vertexId2 the end vertex identifier of the edge.
	 */
	void removeEdge(int vertexId1, int vertexId2);
	
	/*********************************************************
	 * Source/Sink
	 *********************************************************/ 
	/**
	 * Specify a vertex to be the source of the flow network. When the vertex does not
	 * exist, nothing happens.
	 * 
	 * @param sourceId the source vertex identifier. It has to be a valid identifier in the graph.
	 */
	void setSource(int sourceId);
	/**
	 * Return the identifier of the specified source vertex of the flow network. When the source is not specified
	 * it returns -1.
	 * 
	 * @return the source vertex identifier of the graph.
	 */
	int getSource();
	
	/**
	 * Specify a vertex to be the sink of the flow network. When the vertex does not
	 * exist, nothing happens.
	 * 
	 * @param sinkId the sink vertex identifier. It has to be a valid identifier in the graph.
	 */
	void setSink(int sinkId);
	
	/**
	 * Return the identifier of the specified sink vertex of the flow network. When the sink is not specified
	 * it returns -1.
	 * 
	 * @return the sink vertex identifier of the graph.
	 */
	int getSink();
	
	/*********************************************************
	 * Evaluate
	 *********************************************************/
	/**
	 * Calculate and return the maximum flow with Dinic's augmenting path algorithm.
	 * This method calculates one solution for the maximum flow in O(n²m) time and updates
	 * the flow in the network to this maximum flow.
	 * 
	 * @return the maximum flow of the flow network.
	 */
	int dinic();
	
	/**
	 * Calculate and return the maximum flow with Goldberg-Tarjan's push-relabel algorithm.
	 * This method calculates one solution for the maximum flow in O(n³) time and updates
	 * the flow in the network to this maximum flow.
	 * 
	 * @return the maximum flow of the flow network.
	 */
	int goldbergTarjan();
	
	/*********************************************************
	 * Control
	 *********************************************************/
	/**
	 * Reset the flow network to an empty network.This method removes all
	 * edges and vertices from the graph to reset the flow network
	 * to the initial state.
	 */
	void resetNetwork();
	
	/**
	 * Save the flow network to a file. This method saves the total topology of the
	 * flow network with the current state of the flow to the file system. Therefore,
	 * it uses a special file format <tt>*.mfa</tt>. When a failure arises, nothing happens.
	 * 
	 * @param file the file to save the flow network.
	 * @see java.io.File	
	 */
	void saveNetwork(File file);
	
	/**
	 * Load a flow network from a file. This method load a previously saved
	 * flow network with the current state of the flow from a special <tt>*.mfa</tt> file. 
	 * When the file is not found, or another failure arises, nothing happens.
	 * 
	 * @param file the file to load the flow network.
	 * @see java.io.File
	 */
	void loadNetwork(File file);
	
	/*********************************************************
	 * Output
	 *********************************************************/
	/**
	 * Return status information about the last operations. This method can be used
	 * from UIs to provide control information to the user. It returns success messages,
	 * failure notification and help information
	 * 
	 * @return message with status information about the last operations
	 */
	String getPrompt();
	
	/**
	 * Return a String with the current state of the flow network. This method can be used
	 * to display the flow network in text format. It contains the source and sink vertex identifiers the maximum flow
	 * and a list of all vertices with their adjacent lists of edges.
	 * 
	 * @return current state of the flow network in text format.
	 */
	String displayFlowNetwork();
	
	/**
	 * Return a list of all edges. This method adds all edges as four tuple of <tt>Integer[]</tt>
	 * (start vertex, end vertex, capacities, flows) to a <tt>LinkedList</tt>. 
	 * 
	 * @return list of all edges in the graph of the flow network.
	 */
	LinkedList<Integer[]> getGraphData();
	
	/**
	 * Return a list of all vertices. This method adds all vertex identifiers as <tt>Integer</tt>
	 * to a <tt>LinkedList</tt>. 
	 * 
	 * @return list of all vertices in the graph of the flow network.
	 */
	LinkedList<Integer> getVertexIndices();
	
	/*********************************************************
	 * Update/Draw
	 *********************************************************/
	/**
	 * Update the graph after a change. Set a flag that the status of the graph has changed and inform
	 * all components of the program about this change.
	 */
	void updateGraph();
	
	/**
	 * Return the status, if the flow network has changed. This method allows other program components
	 * to query the status of the flow network.
	 * 
	 * @return status of the flow network, if it has changed.
	 */
	boolean isUpdateGraph();
	
	/**
	 * Redraw the graph after a change. Set a flag that the topology of the graph has changed and inform
	 * all components of the program about this change.
	 */
	void drawGraph();
	
	/**
	 * Return the status, if the graph topology has changed. This method allows other program components
	 * to query, if they have to redraw the graph.
	 * 
	 * @return status of the graph topology, if it has changed.
	 */
	boolean isDrawGraph();
}
