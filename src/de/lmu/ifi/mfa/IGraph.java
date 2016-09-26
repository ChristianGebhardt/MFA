package de.lmu.ifi.mfa;

import java.util.LinkedList;

/**
 *  The <tt>IGraph</tt> interface represents a directed graph that can be used to implement
 *  a flow network.
 *  It supports the following two primary functionalities: create and manipulate the directed graph,
 *  evaluate and manipulate the flow on the network.
 *  <p>
 *  The interface provides methods to add and remove vertices or edges.
 *  Furthermore, it contains auxiliary functions that allow an easy implementation of
 *  Dinic's and Goldberg-Tarjan's maximum flow algorithms..
 *  <p>
 *  Additionally, the interface provides methods to query data about the current
 *  state of the flow network in an integer based representation.
 *  <p>
 *  For additional information, see <a href="https://github.com/ChristianGebhardt/mfa">MFA</a>
 *  by Christian Gebhardt on Github.
 *  
 *
 * @author  Christian Gebhardt
 * @version 1.0.1
 * @since   2016-09-03
 */
interface IGraph {

	/*********************************************************
	 * Vertex
	 *********************************************************/
	/**
	 * Add new vertex to the graph. Nothing happens, when the vertex already exists.
	 * 
	 * @param vertexId the vertex identifier to be added.
	 * @return true when the vertex was added to the graph, otherwise false.
	 */
	boolean addVertex(int vertexId);
	
	/**
	 * Remove a vertex from the graph. Nothing happens, when the vertex does not exists.
	 * 
	 * @param vertexId the vertex identifier to be removed.
	 * @return true when the vertex is removed from the graph, otherwise false.
	 */
	boolean removeVertex(int vertexId);
	
	/**
	 * Checks the existence of a vertex.
	 * 
	 * @param vertexId the vertex identifier that is searched for.
	 * @return true when the vertex is in the graph, otherwise false.
	 */
	boolean containsVertex(int vertexId);

	/*********************************************************
	 * Edge
	 *********************************************************/
	/**
	 * Add an edge to the graph of the flow network. When the edge already exists, nothing happens.
	 * 
	 * @param vertexId1 the start vertex identifier of the edge. When the vertex not already exists, it is added to the graph.
	 * @param vertexId2 the end vertex identifier of the edge. When the vertex not already exists, it is added to the graph.
	 * @param capacity the maximum flow capacity of the edge.
	 * @return true when the add operation was successful, otherwise false.
	 */
	boolean addEdge(int vertexId1, int vertexId2, int capacity);
	
	/**
	 * Remove an edge from the graph of the flow network. The adjacent
	 * vertices are untouched. When the edge does not exists, nothing happens. 
	 * 
	 * @param vertexId1 the start vertex identifier of the edge.
	 * @param vertexId2 the end vertex identifier of the edge.
	 * @return true when the remove operation was successful, otherwise false.
	 */
	boolean removeEdge(int vertexId1, int vertexId2);
	
	/*********************************************************
	 * Auxiliary functions (for maximum flow algorithms)
	 *********************************************************/
	/**
	 * Reset the flow on the whole graph. The method sets the flow to zero for
	 * each individual edge.
	 * 
	 * @return true when it was successful for every edge, otherwise false.
	 */
	boolean resetFlow();
	
	/**
	 * Reset the excess on the whole graph. The method sets the excess to zero for
	 * all vertices except the start vertex. The start vertex excess is set to infinity.
	 * 
	 * @param startVertexId the start vertex identifier.
	 * @return true when the operation was successful, otherwise false.
	 */
	boolean resetExcess(int startVertexId);
	
	/**
	 * Initialize the valid labeling on the whole graph. The method sets the labels to zero for
	 * all vertices except the start vertex. The start vertex label is set to n.
	 * 
	 * @param startVertexId the start vertex identifier.
	 * @return true when the initialization was successful, otherwise false.
	 */
	boolean initializeLabels(int startVertexId);
	
	/**
	 * Build the residual graph of the directed graph. The method adds all edges in an adjacent list to its
	 * end vertex as residual edge-
	 */
	void buildResidualGraph();
	
	/*********************************************************
	 * Dinic algorithm functions
	 *********************************************************/
	/**
	 * Build a layered network of a flow network. This method assigns the layer
	 * number to each vertex in the graph. The layered network is built in place withing
	 * the original graph.
	 * 
	 * @param startVertexId the start vertex identifier, where from the layered network is built up. 
	 * @param endVertexId the end vertex identifier, which defines the highest layer in the network.
	 * @return the number of layers in the network.
	 */
	int buildLayeredNetwork(int startVertexId, int endVertexId);
	
	/**
	 * Search an augmenting path in a graph by using a layered network. This method uses depth-first
	 * search to find an augmenting path in a flow graph. Vertices are marked as dead during the search to
	 * successively. When the search is successful, the augmenting path is stored in an auxiliary variable.
	 * 
	 * @param sourceVertexId the source vertex identifier from where to search an augmenting path.
	 * @param sinkVertexId the sink vertex identifier,which defines the termination condition for the search.
	 * @return the success status, if an augmenting path was found.
	 */
	boolean searchAugmentingPath(int sourceVertexId, int sinkVertexId);
	
	/**
	 * Updates the flow along an augmenting path. When an augmenting path is found and stored in an auxiliary
	 * variable, this function updates the flow along this augmenting path. It increasing the flow by the maximum
	 * allowed amount.
	 * 
	 * @return the amount of the flow increment.
	 */
	int updateMinFlowIncrement();

	/*********************************************************
	 * Goldberg-Tarjan algorithm functions
	 *********************************************************/
	/**
	 * Initial push to start the push-relabel algorithm. This method pushes the maximum flow from the source
	 * vertex to all its neighbors. The neighbors are added to the queue of active vertices, except of the sink vertex.
	 * 
	 * @param sourceVertexId the source vertex identifier, from where the initial push is performed.
	 * @param sinkVertexId the sink vertex identifier to except the sink from the active vertices.
	 * @return the number of active vertices in the queue.
	 */
	int initialPush(int sourceVertexId, int sinkVertexId);
	
	/**
	 * Perform a discharge operation on the first vertex in the queue. This method applies push- and relabel-operations
	 * on the first active vertex. All vertices that become active during this operations are added to the rear of the queue.
	 * When the current vertex is relabeled without becoming inactive, it is also added to the rear of the queue.
	 * 
	 * @return the number of active vertices in the queue.
	 */
	int dischargeQueue();
	
	/*********************************************************
	 * Get information about the graph and its flow
	 *********************************************************/
	/**
	 * Calculate and return the outgoing flow of a vertex. This method iterates through all outgoing edges and sums up
	 * the total outgoing flow.
	 * 
	 * @param vertexId the vertex identifier from which the outgoing flow is calculated.
	 * @return the outgoing flow of the vertex.
	 */
	int getOutFlow(int vertexId);
	
	/**
	 * Calculate and return the incoming flow of a vertex. This method iterates through all incominig edges and sums up
	 * the total ingoing flow.
	 * 
	 * @param vertexId the vertex identifier from which the incoming flow is calculated.
	 * @return the incoming flow of the vertex.
	 */
	int getInFlow(int vertexId);
	
	/**
	 * Get the graph data in form of a text based format. This method returns a String that contains a  list of
	 *  all adjacent lists.
	 * Each adjacent list is represented through the edges of the form <tt>(start vertex id,end vertex id,c:capacity,f:flow)</tt>
	 * 
	 * @return the list of all edges as integer array.
	 */
	String graphToString();
	
	/**
	 * Get the graph data in form of an edge list. This method returns a list of all edges as integer array.
	 * Each edge is represented through a four tuple <tt>[start vertex id, end vertex id, capacity, flow]</tt>
	 * 
	 * @return the list of all edges as integer array.
	 */
	LinkedList<Integer[]> getGraphData();
	
	/**
	 * Get the graph data in form of an vertex list. This method returns a list of all vertices as integer.
	 * Each vertex is represented through its identifier as integer.
	 * 
	 * @return the list of all vertex identifiers in the graph.
	 */
	LinkedList<Integer> getVertexIndices();
}
