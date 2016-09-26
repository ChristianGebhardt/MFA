package de.lmu.ifi.mfa;

import java.io.Serializable;

/**
 *  The <tt>Edge</tt> class represents a edge in a directed graph from one vertex to another.
 *  It supports mainly the functionality to manipulate and push the flow in the edge.
 *  <p>
 *  The class provides methods to set and change the flow directly.
 *  Furthermore, it contains auxiliary functions that allow to push preflows forward and backward.
 *  <p>
 *  Additionally, the class provides a method to query data about the current
 *  state of the edge as String.
 *  <p>
 *  For additional information, see <a href="https://github.com/ChristianGebhardt/mfa">MFA</a>
 *  by Christian Gebhardt on Github.
 *  
 *
 * @author  Christian Gebhardt
 * @version 1.0.1
 * @since   2016-09-03
 */
class Edge implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Vertex startVertex;
	private Vertex endVertex;
	private int capacity;
	private int flow;
	private boolean blocked;
	
	/**
	 * Create a new directed edge between two vertices in a directed graph with specific capacity.
	 * The flow on the edge is initially set to zero.
	 * 
	 * @param startVertex the start vertex where the edge starts.
	 * @param endVertex the end vertex where the edge ends.
	 * @param capacity the maximum flow capacity on the edge. It has to be a positive integer.
	 */
	protected Edge(Vertex startVertex, Vertex endVertex, int capacity) {
		this.startVertex = startVertex;
		this.endVertex = endVertex;
		this.capacity = capacity;
		this.flow = 0;
		this.blocked = false;
	}
	
	/**
	 * Get the start vertex of the edge. This vertex normally has the edge in it edge list.
	 * 
	 * @return the start vertex of the edge.
	 */
	protected Vertex getStartVertex() {
		return startVertex;
	}
	
	/**
	 * Get the end vertex of the edge. This vertex normally has the edge in it residual edge list.
	 * 
	 * @return the end vertex of the edge.
	 */
	protected Vertex getEndVertex() {
		return endVertex;
	}
	
	/**
	 * Get the maximum flow capacity on the edge.
	 * 
	 * @return the maximum flow capacity.
	 */
	protected int getCapacity() {
		return capacity;
	}
	
	/**
	 * Get the current flow on the edge. The flow is initially zero.
	 * 
	 * @return the current flow.
	 */
	protected int getFlow() {
		return flow;
	}
	
	/**
	 * Set the flow to a specific value. The flow has to be a non-negative integer that is not
	 * larger than the maximum flow capacity.
	 * 
	 * @param flow the specified flow on the edge.
	 * @return true when the operation was successful, otherwise false.
	 */
	protected boolean setFlow(int flow) {
		if (flow > this.capacity || flow < 0) {
			return false;
		} else {
			this.flow = flow;
			return true;
		}
	}
	
	/*********************************************
	 * Auxiliary functions
	 *********************************************/
	/**
	 * Return the blocking status of the edge, if it is blocked or not.
	 * 
	 * @return true when the edge is blocked, otherwise false.
	 */
	protected boolean isBlocked() {
		return blocked;
	}
	
	/**
	 * Set the blocking status of the edge to be blocked or not blocked.
	 * 
	 * @param blocked the blocking status of the edge.
	 */
	protected void setBlocked(boolean blocked) {
		this.blocked = blocked;
	}
	
	/**
	 * Push flow forward through the edge from the start vertex to the end vertex.
	 * This method only pushes the excess forward by increasing the flow when the start vertex
	 * has positive excess. It increases the flow by the maximal possible amount.
	 * 
	 * @return the end vertex when it becomes active through the push operation, otherwise null.
	 */
	protected Vertex pushFlowForward() {	//to push in edge direction
		int previousExcess = endVertex.getExcess();
		int deltaFlow = 0;
		if(this.getStartVertex().getExcess() == -1) {	//initial push start vertex
			deltaFlow =capacity-flow;
			flow = capacity;
		} else {
			if (capacity-flow <= startVertex.getExcess()) {	//saturating push
				deltaFlow = capacity-flow;
			} else {										//non-saturating push
				deltaFlow =startVertex.getExcess();
				startVertex.setPreviousEdge();
			}
			flow += deltaFlow;
			startVertex.changeExcess(-deltaFlow);
		}
		endVertex.changeExcess(deltaFlow);
		if (previousExcess == 0 && deltaFlow > 0) {
			endVertex.setDead(false);
			return endVertex;
		}
		else {
			return null;
		}
	}
	
	/**
	 * Push flow backward through the edge from the end vertex to the start vertex.
	 * This method only pushes the excess backward by decreasing the flow when the end vertex
	 * has positive excess. It decreases the flow by the maximal possible amount.
	 * 
	 * @return the start vertex when it becomes active through the push operation, otherwise null.
	 */
	protected Vertex pushFlowBackward() {	//to push in reverse edge direction
		int previousExcess = startVertex.getExcess();
		int deltaFlow = 0;
		if (flow <= endVertex.getExcess()) {	//saturating push
			deltaFlow = flow;
		} else {										//non-saturating push
			deltaFlow =endVertex.getExcess();
			startVertex.setPreviousEdge();
		}
		flow -= deltaFlow;
		if(startVertex.getExcess() >= 0) {	//avoid excess change on start vertex
			startVertex.changeExcess(deltaFlow);
		}
		endVertex.changeExcess(-deltaFlow);
		if (previousExcess == 0 && deltaFlow > 0) {
			startVertex.setDead(false);
			return startVertex;
		}
		else {
			return null;
		}
	}
	
	/*********************************************
	 * Get information about the edge and its flow
	 *********************************************/
	/**
	 * Return the basic edge information as a String. This method creates a string that
	 * contains the start vertex identifier, the end vertex identifier,
	 * the capacity and the current flow of the edge.
	 * 
	 * @return the String representing the basic information of the edge.
	 */
	public String edgeToString() {
		return "("+startVertex.id()+","+endVertex.id()+",c:"+capacity+",f:"+flow+")";
	}
}
