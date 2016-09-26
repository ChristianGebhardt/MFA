package de.lmu.ifi.mfa;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.ListIterator;

/**
 *  The <tt>Vertex</tt> class represents a vertex in a directed graph.
 *  It supports the following two primary functionalities: create and manipulate the directed graph
 *  morphology through its adjacent list,
 *  evaluate and manipulate the flow in and out of the vertex.
 *  <p>
 *  The class provides methods to add and remove edges to and from its adjacent neighbor list.
 *  Furthermore, it contains auxiliary functions that allow an easy implementation of
 *  layered networks and preflows.
 *  <p>
 *  Additionally, the class provides methods to query data about the current
 *  state of the vertex and its neighbors in an integer based representation.
 *  <p>
 *  For additional information, see <a href="https://github.com/ChristianGebhardt/mfa">MFA</a>
 *  by Christian Gebhardt on Github.
 *  
 *
 * @author  Christian Gebhardt
 * @version 1.0.1
 * @since   2016-09-03
 */
class Vertex implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private int id;
	private int excess;
	private int label;
	private int layer;
	private LinkedList<Edge> neighbors;
	private LinkedList<Edge> resNeighbors;
	private int iteratorAugPath;
	private boolean deadEnd;
	private boolean increasedLabel;
	
	/**
	 * Create a new vertex by using an empty adjacent neighbor list (and residual neighbor list).
	 * 
	 * @param id the vertex identifier.
	 */
	protected Vertex(int id) {
		this.id = id;
		this.excess = 0;
		this.label = 0;
		this.layer = -1;
		neighbors = new LinkedList<Edge>();
		resNeighbors = new LinkedList<Edge>();
		this.iteratorAugPath = 0;
		this.deadEnd = false;
	}
	
	/*********************************************************
	 * Edges
	 *********************************************************/
	/**
	 * Add an edge to the neighbor list of the vertex. When the edge already exists, nothing happens.
	 * 
	 * @param endVertex the end vertex of the edge.
	 * @param capacity the maximum flow capacity of the edge.
	 * @return true when the add operation was successful, otherwise false.
	 */
	protected boolean addEdge(Vertex endVertex,int capacity) {
		if (!containsEdge(endVertex)) {
			neighbors.add(new Edge(this,endVertex,capacity));
			return true;
		} else {			
			return false;
		}
	}
	
	/**
	 * Add a residual edge to the residual neighbor list of the vertex. When the edge already exists, nothing happens.
	 * 
	 * @param startVertex the start vertex of the edge or the end Vertex of the residual edge.
	 * @return true when the add operation was successful, otherwise false.
	 */
	protected boolean addResEdge(Vertex startVertex) {
		if (startVertex.containsEdge(this)) {
			resNeighbors.add(startVertex.getEdge(this));
			return true;
		} else {			
			return false;
		}
	}
	
	/**
	 * Remove an edge from the neighbor list of the vertex. When the edge does not exist, nothing happens.
	 * 
	 * @param endVertex the end vertex of the edge.
	 * @return true when the remove operation was successful, otherwise false.
	 */
	protected boolean removeEdge(Vertex endVertex) {
		if (containsEdge(endVertex)) {
			Edge oldEdge = getEdge(endVertex);
			if (oldEdge != null) {
				neighbors.remove(oldEdge);
				return true;
			} else {
				return false;
			}
			
		} else {			
			return false;
		}
	}
	
	/**
	 * Remove a residual edge from the residual neighbor list of the vertex. When the edge does not exist, nothing happens.
	 * 
	 * @param startVertex the start vertex of the edge.
	 * @return true when the remove operation was successful, otherwise false.
	 */
	protected boolean removeResEdge(Vertex startVertex) {
		if (startVertex.containsEdge(this)) {
			Edge oldEdge = startVertex.getEdge(this);
			if (oldEdge != null) {
				resNeighbors.remove(oldEdge);
				return true;
			} else {
				return false;
			}
			
		} else {			
			return false;
		}
	}
	
	/**
	 * Remove all edges from the neighbor list of the vertex.
	 * 
	 * @return true when the remove operation was totally successful, otherwise false.
	 */
	protected boolean removeAllEdges() {
		ListIterator<Edge> listIterator = neighbors.listIterator();
		boolean success = true;
		while (listIterator.hasNext()) {
			success= success && listIterator.next().getEndVertex().removeResEdge(this);
		}
		return success;
	}
	
	/**
	 * Remove all residual edges from the residual neighbor list of the vertex.
	 * 
	 * @return true when the remove operation was totally successful, otherwise false.
	 */
	protected boolean removeAllResEdges() {
		ListIterator<Edge> listIterator = resNeighbors.listIterator();
		boolean success = true;
		while (listIterator.hasNext()) {
			success= success && listIterator.next().getStartVertex().removeEdge(this);
		}
		return success;
	}
	
	/**
	 * Check if an edge to a specific vertex exists.
	 *
	 * @param vertexId the end vertex identifier of the searched edge.
	 * @return true when the edge to the specified vertex exists, otherwise false.
	 */
	protected boolean containsEdge(int vertexId) {
		ListIterator<Edge> listIterator = neighbors.listIterator();
		while (listIterator.hasNext()) {
			if (listIterator.next().getEndVertex().id() == vertexId) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Check if an edge to a specific vertex exists.
	 *
	 * @param endVertex the end vertex of the searched edge.
	 * @return true when the edge to the specified vertex exists, otherwise false.
	 */
	protected boolean containsEdge(Vertex endVertex) {
		ListIterator<Edge> listIterator = neighbors.listIterator();
		while (listIterator.hasNext()) {
			if (listIterator.next().getEndVertex() == endVertex) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Return the edge to a specific vertex.
	 *
	 * @param endVertex the end vertex of the searched edge.
	 * @return the edge to the end vertex.
	 */
	protected Edge getEdge(Vertex endVertex) {
		ListIterator<Edge> listIterator = neighbors.listIterator();
		while (listIterator.hasNext()) {
			Edge nextEdge = listIterator.next();
			if (nextEdge.getEndVertex() == endVertex) {
				return nextEdge;
			}
		}
		return null;
	}
	
	/**
	 * Return all adjacent edges to this vertex.
	 *
	 * @return a list of all adjacent edges.
	 */
	protected LinkedList<Edge> getAllEdges() {
		return neighbors;
	}
	
	/**
	 * Return all adjacent residual edges to this vertex.
	 *
	 * @return a list of all adjacent residual edges.
	 */
	protected LinkedList<Edge> getAllResEdges() {
		return resNeighbors;
	}
	
	/**
	 * Reset the list of residual edges. This method deletes the additional information of
	 * a residual graph.
	 *
	 */
	protected void clearResNeighbors() {
		resNeighbors = new LinkedList<Edge>();
	}
	
	/**
	 * Set all the adjacent edges as residual edges of the end vertex. This method adds the additional information of
	 * a residual graph by setting up a bidirectional connection between the edge vertices.
	 *
	 */
	protected void addEdgesToResGraph() {
		ListIterator<Edge> listIterator = neighbors.listIterator();
		while (listIterator.hasNext()) {
			Edge nextEdge = listIterator.next();
			nextEdge.getEndVertex().addResEdge(nextEdge);
		}
	}
	
	/**
	 * Add a residual edge to the residual edge list. The edge has to be a regular edge in the directed graph.
	 * 
	 * @param resEdge the edge that is added to the residual edge list.
	 */
	protected void addResEdge(Edge resEdge) {
		resNeighbors.add(resEdge);
	}
	
	/**
	 * Return the vertex identifier.
	 *
	 * @return the identifier of this vertex.
	 */
	protected int id() {
		return this.id;
	}
	
	/*********************************************************
	 * Auxiliary functions
	 *********************************************************/
	/**
	 * Reset the flow on all outgoing edges in the neighbor edge list to zero.
	 * 
	 * @return true when the reset operation was totally successful, otherwise false.
	 */
	protected boolean resetFlow() {
		ListIterator<Edge> listIterator = neighbors.listIterator();
		while (listIterator.hasNext()) {
			Edge nextEdge = listIterator.next();
			if (!nextEdge.setFlow(0)) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Get the total outgoing flow of the vertex. This vertex sums up all outgoing flows in its edge list.
	 * 
	 * @return total outgoing flow of the vertex.
	 */
	protected int getOutFlow() {
		ListIterator<Edge> listIterator = neighbors.listIterator();
		int outFlow = 0;
		while (listIterator.hasNext()) {
			Edge nextEdge = listIterator.next();
			outFlow += nextEdge.getFlow();
		}
		return outFlow;
	}
	
	/**
	 * Get the total incoming flow into the vertex. This vertex sums up all incoming flows in its residual edge list.
	 * 
	 * @return total incoming flow of the vertex.
	 */
	protected int getInFlow() {
		ListIterator<Edge> listIterator = resNeighbors.listIterator();
		int inFlow = 0;
		while (listIterator.hasNext()) {
			Edge nextEdge = listIterator.next();
			inFlow += nextEdge.getFlow();
		}
		return inFlow;
	}
	
	/**
	 * Specify the layer number in the layered network.
	 * 
	 * @param layer the layer number in the layered network.
	 */
	protected void setLayer(int layer) {
		this.layer = layer;
	}
	
	/**
	 * Get the layer number in the layered network. The method returns -1 when the layered network is not created.
	 *
	 * @return the layer number in the layered network.
	 */
	protected int getLayer() {
		return this.layer;
	}
	
	/**
	 * Reset the layer number of  the layered network. This method deletes the additional information of
	 * a layered network. 
	 */
	protected void resetLayer() {
		this.setLayer(-1);
	}
	
	/**
	 * Specify the valid label of the vertex. The number should be between 0 and 2n.
	 * 
	 * @param label the valid label of the vertex.
	 */
	protected void setLabel(int label) {
		this.label = label;
	}
	
	/**
	 * Get the valid label of the vertex. The method returns 0 when the label was not changed before.
	 *
	 * @return the valid label of the vertex.
	 */
	protected int getLabel() {
		return this.label;
	}
	
	/**
	 * Reset the valid label of the vertex. This method deletes the additional information of
	 * a valid labeling. 
	 */
	protected void resetLabel() {
		this.setLabel(0);
	}
	
	/**
	 * Set the excess of the vertex. The number should be a non-negative integer.
	 * 
	 * @param excess the excess of the vertex.
	 */
	protected void setExcess(int excess) {
		this.excess = excess;
	}
	
	/**
	 * Get the excess of the vertex. The method returns 0 when the excess was not changed before.
	 *
	 * @return the excess of the vertex.
	 */
	protected int getExcess() {
		return this.excess;
	}
	
	/**
	 * Change the excess of the vertex. The method adds the specified excess to the current excess.
	 * To decrease the excess, the change has to be a negative integer. 
	 *
	 * @param deltaExcess the change of the excess.
	 */
	protected void changeExcess(int deltaExcess) {
		this.excess += deltaExcess;
		this.increasedLabel = false;
	}
	
	/**
	 * Reset the excess of the vertex. This method deletes the additional information of
	 * a current excess. 
	 */
	protected void resetExcess() {
		this.setExcess(0);
	}
	
	/*********************************************************
	 * Goldberg-Tarjan algorithm functions
	 *********************************************************/
	/**
	 * Relabel the vertex by increasing its valid label. This method should be applied when the vertex
	 * has a positive excess and no push-operation is possible. It sets the valid label one over the minimum
	 * of all neighboring vertices in the residual graph.
	 */
	protected void relabelVertex() {
		int newLabel = Integer.MAX_VALUE;
		ListIterator<Edge> listIterator = neighbors.listIterator();
		while (listIterator.hasNext()) {	//check normal edges
			Edge nextEdge = listIterator.next();
			if (nextEdge.getEndVertex().getLabel()+1 < newLabel && nextEdge.getCapacity()-nextEdge.getFlow() > 0) {
				newLabel = nextEdge.getEndVertex().getLabel()+1;
			}
		}
		listIterator = resNeighbors.listIterator();
		while (listIterator.hasNext()) {	//check residual edges
			Edge nextEdge = listIterator.next();
			if (nextEdge.getStartVertex().getLabel()+1 < newLabel && nextEdge.getFlow() > 0) {
				newLabel = nextEdge.getStartVertex().getLabel()+1;
			}
		}
		this.label = newLabel;
		this.increasedLabel = true;
	}
	
	/**
	 * Delete the information if the label was recently increased or not.
	 */
	protected void resetIncreasedLabel() {
		this.increasedLabel = false;
	}
	
	/**
	 * Get information if the valid label of the vertex was recently increased.
	 * 
	 * @return true when the valid label was recently increased, otherwise false.
	 */
	protected boolean labelIncreased() {
		return increasedLabel;
	}
	
	/**
	 * Execute one push-relabel operation on this vertex. The method tries to push its excess through
	 * the next edge in its edge or residual edge list. When a push operation is executed on an edge and the
	 * end vertex of the edge becomes active in this process, the method returns this vertex, otherwise it
	 * returns null. When there is no additional edge to push through, a relabel operation is applied.
	 * 
	 * @return the vertex that becomes active in the push-relabel operation, otherwise null.
	 */
	protected Vertex push_relabel() {
		Edge pushEdge = this.getNextEdge();
		Vertex newActiveVertex;
		if(pushEdge == null) {
			this.relabelVertex();
			this.setDead(false);
			newActiveVertex = null;
		} else {
			if (this == pushEdge.getStartVertex()) { //normal edge		
				if (pushEdge.getStartVertex().getLabel() == pushEdge.getEndVertex().getLabel()+1 && pushEdge.getCapacity()>pushEdge.getFlow()) {
					newActiveVertex = pushEdge.pushFlowForward();
				} else if (this.isDead()) {
					this.relabelVertex();
					this.setDead(false);
					newActiveVertex = null;
				} else {
					//do nothing
					newActiveVertex = null;
				}
			} else {	//residual edge
				if (pushEdge.getEndVertex().getLabel() == pushEdge.getStartVertex().getLabel()+1 && 0<pushEdge.getFlow()) {
					newActiveVertex = pushEdge.pushFlowBackward();
				} else if (this.isDead()) {
					this.relabelVertex();
					this.setDead(false);
					newActiveVertex = null;
				} else {
					//do nothing
					newActiveVertex = null;
				}
			}
		}
		return newActiveVertex;
	}
	
	/**
	 * Check if the vertex is a dead end in the layered network.
	 * 
	 * @return true when the vertex is a dead end, otherwise false.
	 */
	protected boolean isDead() {
		return this.deadEnd;
	}
	
	/**
	 * Specify if the vertex is a dead end or not in the layered network.
	 * 
	 * @param isDead the status (dead or not dead) of the vertex.
	 */
	protected void setDead(boolean isDead) {
		this.deadEnd = isDead;
	}
	
	/*********************************************************
	 * Edge iterator functions
	 *********************************************************/
	/**
	 * Get the next edge in the list of all outgoing edges in the residual graph. This method uses
	 * an internal iterator that iterates over all edges and residual edges. It returns the
	 * edge where the iterator is pointing to and moves the iterator to the next edge.
	 * 
	 * @return the next edge in the list of all outgoing edges in the residual graph. 
	 */
	protected Edge getNextEdge() {
		if (this.isDead()) {
			return null;
		} else {
			if (iteratorAugPath >= 0 && iteratorAugPath < neighbors.size()) {	//iterate in normal edges
				iteratorAugPath++;
				if (iteratorAugPath == neighbors.size() && resNeighbors.size() == 0) {
					this.setDead(true);
				}
				return neighbors.get(iteratorAugPath-1);
			} else if (iteratorAugPath == neighbors.size()) {	//last normal edge -> jump to residual edges
				iteratorAugPath = -1;	
				if (1 == resNeighbors.size()) {
					this.setDead(true);
				}
				if (0 == resNeighbors.size()) {
					this.setDead(true);
					return null;
				}
				return resNeighbors.get(-1*iteratorAugPath-1);
			} else if (-1*iteratorAugPath < resNeighbors.size()) {	//residual edge
				iteratorAugPath--;
				return resNeighbors.get(-1*iteratorAugPath-2);
			} else {											//last residual edge
				iteratorAugPath--;
				this.setDead(true);
				return resNeighbors.get(-1*iteratorAugPath-2);
			}		
		}
	}
	
	/**
	 * Set back the iterator to the previous edge. This method goes one step back with the internal
	 * iterator to the previous edge in the list of all outgoing edges in the residual graph.
	 */
	protected void setPreviousEdge() {
		if (!this.isDead()) {
			if (iteratorAugPath > 0 && iteratorAugPath <= neighbors.size()) {	//normal edge
				iteratorAugPath--;
			} else if (iteratorAugPath == -1) {
				iteratorAugPath = neighbors.size();
			} else if (iteratorAugPath < -1) {
				iteratorAugPath++;
			} else {
				//Do nothing
			}	
		} else {								//residual edge
			this.setDead(false);
			if (resNeighbors.size() > 0) {
				iteratorAugPath = -1*resNeighbors.size();
			} else if (resNeighbors.size() > 0) {
				iteratorAugPath = neighbors.size()-1;
			}
			
		}
	}
	
	/**
	 * Reset the internal edge iterator to the first edge in the list of all
	 * outgoing edges in the residual graph. 
	 */
	protected void resetEdge() {
		this.iteratorAugPath = 0;
	}
	
	/**
	 * Set all edges in the edge list to be initially blocked.
	 */
	protected void resetBlockings() {
		ListIterator<Edge> listIterator = neighbors.listIterator();
		while (listIterator.hasNext()) {
			listIterator.next().setBlocked(true);
		}
	}
	

	
	/*********************************************************
	 * Get information about the graph and its flow
	 *********************************************************/
	/**
	 * Return the basic vertex information as a String. This method creates a string that
	 * contains the vertex identifier and a list of all edges in the neighbor edge list.
	 * 
	 * @return the String representing the basic information of the vertex.
	 */
	public String vertexToString() {
		StringBuilder s = new StringBuilder();
		s.append("Vertex "+id+" (label "+this.label+"):  ");
		ListIterator<Edge> listIterator = neighbors.listIterator();
		while (listIterator.hasNext()) {
			Edge nextEdge = listIterator.next();
			if (nextEdge.getEndVertex() != null) {
				s.append(nextEdge.edgeToString()+"  ");
			}
		}

		return s.toString();
	}
	
	/**
	 * Get the basic data about the adjacent edges as a list of integer array.
	 * Each edge is represented through a four tuple <tt>[start vertex id, end vertex id, capacity, flow]</tt>
	 *
	 * @return the list of all edges as integer array.
	 */
	protected LinkedList<Integer[]> getEdgeData() {
		LinkedList<Integer[]> vertexEdges = new LinkedList<Integer[]>();
		//iterate over edges
		ListIterator<Edge> listIterator = neighbors.listIterator();
		while (listIterator.hasNext()) {
			Integer[] data = new Integer[4];
			Edge currEdge = listIterator.next();
			data[0] = currEdge.getStartVertex().id();
			data[1] = currEdge.getEndVertex().id();
			data[2] = currEdge.getCapacity();
			data[3] = currEdge.getFlow();
			vertexEdges.add(data);
		}		
		return vertexEdges;
	}
	
}
