package de.lmu.ifi.mfa;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.LinkedList;
import java.util.ListIterator;

/**
 *  The <tt>Graph</tt> class is an implementation a graph interface {@link IGraph}.
 *  It supports the following two primary functionalities: create and manipulate a graph
 *  morphology, provide auxiliary functions for maximum flow algorithms.
 *  <p>
 *  The whole parameter and return values are based only on integer values as vertex identifiers.
 *  The internal data structure uses protected classes of vertices and edges.
 *  <p>
 *  Furthermore, the interface provides methods to query data about the current
 *  state of the graph in order to make the graph data available.
 *  <p>
 *  For additional information, see <a href="https://github.com/ChristianGebhardt/mfa">MFA project</a>
 *  by Christian Gebhardt on Github.
 *  
 *
 * @author  Christian Gebhardt
 * @version 1.0.1
 * @since   2016-09-03
 */
class Graph implements IGraph, Serializable {

	//constant values
	private static final long serialVersionUID = 1L;
	private static final String NEWLINE = System.getProperty("line.separator");
	
	//representation of the graph: list of vertices, where each vertex contains a list of adjacent vertices
	//the map is used to have a connection from the external integer representation to the internal object representation
	private Map<Integer,Vertex> vertices;
	
	//auxiliary variable for Dinic algorithm
	private LinkedList<Edge> augmentingPath;
	//auxiliary variable for Goldberg-Tarjan algorithm
	private LinkedList<Vertex>  queue;
	private Vertex startVertex;
	private Vertex endVertex;
	
	/**
	 * Create a new graph by using an empty vertex list.
	 */
	public Graph() {
		this.vertices = new LinkedHashMap<Integer,Vertex>();
	}
	
	//commented in interface
	public boolean addVertex(int id) {
		if (!vertices.containsKey(id)) {
			vertices.put(id,new Vertex(id));
			return true;
		} else {			
			return false;
		}	
	}
	
	//commented in interface
	public boolean removeVertex(int id) {
		if (vertices.containsKey(id)) {
			boolean success = true;
			success = success && vertices.get(id).removeAllEdges();
			success = success && vertices.get(id).removeAllResEdges();
			vertices.remove(id);
			return success;		
		} else {
			return false;
		}	
	}
	
	//commented in interface
	public boolean containsVertex(int id) {
		return vertices.containsKey(id);
	}
	
	//commented in interface
	public boolean addEdge(int vertexId1, int vertexId2, int capacity) {
		this.addVertex(vertexId1);
		this.addVertex(vertexId2);
		Vertex startVertex = vertices.get(vertexId1);
		Vertex endVertex = vertices.get(vertexId2);
		boolean success= startVertex.addEdge(endVertex,capacity);
		success = success & endVertex.addResEdge(startVertex);
		return success;
	}
	
	//commented in interface
	public boolean removeEdge(int vertexId1, int vertexId2) {
		Vertex startVertex = vertices.get(vertexId1);
		Vertex endVertex = vertices.get(vertexId2);
		if (startVertex != null && endVertex != null) {
			boolean success = startVertex.removeEdge(endVertex);
			if (success) {
				endVertex.removeResEdge(startVertex);
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
	
	//commented in interface
	public boolean resetFlow() {
		for (Map.Entry<Integer, Vertex> entry : vertices.entrySet())
        {
			if (!entry.getValue().resetFlow()) {
				return false;
			}
        }
		return true;
	}
	
	//commented in interface
	public boolean resetExcess(int startVertexId) {
		for (Map.Entry<Integer, Vertex> entry : vertices.entrySet())
        {
			entry.getValue().resetExcess();
			if (entry.getValue().id() == startVertexId) {
				entry.getValue().setExcess(-1);
			}
        }
		return true;
	}
	
	//commented in interface
	public boolean initializeLabels(int startVertexId) {
		for (Map.Entry<Integer, Vertex> entry : vertices.entrySet())
        {
			entry.getValue().resetLabel();
			if (entry.getValue().id() == startVertexId) {
				entry.getValue().setLabel(this.vertices.size());
			}
        }
		return true;
	}
	
	//commented in interface
	public void buildResidualGraph() {
		for (Map.Entry<Integer, Vertex> entry : vertices.entrySet())
        {
			entry.getValue().clearResNeighbors();
			entry.getValue().setDead(false);
        }
		for (Map.Entry<Integer, Vertex> entry : vertices.entrySet())
        {
			entry.getValue().addEdgesToResGraph();
        }
	}
	
	/*********************************************************
	 * Dinic algorithm functions
	 *********************************************************/
	//commented in interface
	public int buildLayeredNetwork(int startVertexId, int endVertexId) {
		//initialize layer number, blocking flag, edge iterator, and dead end flag
		for (Map.Entry<Integer, Vertex> entry : vertices.entrySet()) {
			entry.getValue().resetLayer();
			entry.getValue().resetBlockings();
			entry.getValue().resetEdge();
			entry.getValue().setDead(false);
        }
		//initialize auxiliary variables
		int layerNbr = 0;
		Vertex startVertex = vertices.get(startVertexId);
		Vertex endVertex = vertices.get(endVertexId);
		Vertex tempVertex = null;
		LinkedList<Edge> tempEdges = null;
		LinkedList<Vertex> currentLayer = new LinkedList<Vertex>();
		LinkedList<Vertex> nextLayer = new LinkedList<Vertex>();
		//create layer 0 with start vertex
		startVertex.setLayer(0);	
		currentLayer.add(startVertex);
		//loop over all layers
		while(!currentLayer.isEmpty()) {
			layerNbr++;
			while(!currentLayer.isEmpty()) {
				tempVertex = currentLayer.removeFirst();
				tempEdges = tempVertex.getAllEdges();		//check all edges
				ListIterator<Edge> listIterator = tempEdges.listIterator();
				while (listIterator.hasNext()) {
					Edge checkEdge = listIterator.next();
					if (checkEdge.getCapacity() > checkEdge.getFlow()) {
						Vertex checkVertex = checkEdge.getEndVertex();
						if (checkVertex.getLayer() == -1) {
							checkEdge.setBlocked(false);
							checkVertex.setLayer(layerNbr);
							checkVertex.setDead(false);
							nextLayer.add(checkVertex);	
						}
					}
				}
				tempEdges = tempVertex.getAllResEdges();	//check all residual edges
				listIterator = tempEdges.listIterator();
				while (listIterator.hasNext()) {
					Edge checkEdge = listIterator.next();
					if (0 < checkEdge.getFlow()) {
						Vertex checkVertex = checkEdge.getStartVertex();
						if (checkVertex.getLayer() == -1) {
							checkEdge.setBlocked(false);
							checkVertex.setLayer(layerNbr);
							checkVertex.setDead(false);
							nextLayer.add(checkVertex);	
						}
					}
				}
			}
			//check termination condition
			if (nextLayer.contains(endVertex)) {
				ListIterator<Vertex> listIterator = nextLayer.listIterator();
				while (listIterator.hasNext()) {
					listIterator.next().resetLayer();
				}
				endVertex.setLayer(layerNbr);
				return layerNbr;
			} else {
				currentLayer = nextLayer;
				nextLayer = new LinkedList<Vertex>();
			}
		}
		return -1;
	}

	//commented in interface
	public boolean searchAugmentingPath(int startVertexId, int endVertexId) {
		//initialize auxiliary variables
		augmentingPath = new LinkedList<Edge>();
		Vertex activeVertex = vertices.get(startVertexId);
		Vertex startVertex = vertices.get(startVertexId);
		Vertex endVertex = vertices.get(endVertexId);
		//loop over vertices until end vertex is reached
		while(activeVertex != endVertex) {
			if(!activeVertex.isDead()) {
				//one step forward
				Edge newEdge = activeVertex.getNextEdge();
				if (newEdge == null) {
					augmentingPath = null;
					return false;
				} else {
				}
				if(activeVertex == newEdge.getStartVertex()) {			//normal edge
					if (newEdge.getEndVertex().getLayer() == activeVertex.getLayer()+1 && newEdge.getCapacity()>newEdge.getFlow()) {
						activeVertex = newEdge.getEndVertex();
						augmentingPath.add(newEdge);
					}
				} else if(activeVertex == newEdge.getEndVertex() && 0<newEdge.getFlow()) {	//residual edge
					if (newEdge.getStartVertex().getLayer() == activeVertex.getLayer()+1) {
						activeVertex = newEdge.getStartVertex();
						augmentingPath.add(newEdge);
					}
				} else {
					//Do nothing
				}
			} else {
				//check termination condition (no augmenting path in network)
				if (activeVertex == startVertex) {
					augmentingPath = null;
					return false;
				}
				//one step back
				Edge lastEdge = augmentingPath.removeLast();
				lastEdge.setBlocked(true);
				if(activeVertex == lastEdge.getEndVertex()) {			//normal edge
					activeVertex = lastEdge.getStartVertex();
				} else {												//residual edge
					activeVertex = lastEdge.getEndVertex();
				}
			}
		}
		return true;
	}
	
	//commented in interface
	public int updateMinFlowIncrement() {
		int deltaFlow = 0;
		if (augmentingPath.size()<1) {
			return 0;
		}
		//initialize auxiliary variables
		ListIterator<Edge> listIterator = augmentingPath.listIterator();
		Edge currentEdge = listIterator.next();
		Vertex startVertex = currentEdge.getStartVertex();
		Vertex endVertex = currentEdge.getEndVertex();
		deltaFlow = currentEdge.getCapacity()-currentEdge.getFlow();
		//loop over all edges in augmenting path
		while (listIterator.hasNext()) {
			currentEdge = listIterator.next();
			startVertex = endVertex;
			if (startVertex == currentEdge.getStartVertex()) {		//normal edge
				endVertex = currentEdge.getEndVertex();
				if (deltaFlow > currentEdge.getCapacity()-currentEdge.getFlow()) {
					deltaFlow = currentEdge.getCapacity()-currentEdge.getFlow();
				}
			} else {												//residual edge
				endVertex = currentEdge.getStartVertex();
				if (deltaFlow > currentEdge.getFlow()) {
					deltaFlow = currentEdge.getFlow();
				}
			}
		}
		
		//Block edges, reset augmenting path and update flow
		listIterator = augmentingPath.listIterator();
		//first edge
		currentEdge = listIterator.next();
		startVertex = currentEdge.getStartVertex();
		endVertex = currentEdge.getEndVertex();
		if (deltaFlow == currentEdge.getCapacity()-currentEdge.getFlow()) { //only normal edge possible
			currentEdge.setBlocked(true);
		} else {
			startVertex.setPreviousEdge();
		}
		currentEdge.setFlow(currentEdge.getFlow()+deltaFlow);
		//loop over all edges in augmenting path
		while (listIterator.hasNext()) {
			currentEdge = listIterator.next();
			startVertex = endVertex;
			if (startVertex == currentEdge.getStartVertex()) {		//normal edge
				endVertex = currentEdge.getEndVertex();
				if (deltaFlow == currentEdge.getCapacity()-currentEdge.getFlow()) {
					currentEdge.setBlocked(true);
				} else {
					currentEdge.getStartVertex().setPreviousEdge();
				}
				currentEdge.setFlow(currentEdge.getFlow()+deltaFlow);
			} else {												//residual edge
				endVertex = currentEdge.getStartVertex();
				if (deltaFlow == currentEdge.getFlow()) {
					currentEdge.setBlocked(true);
				} else {
					currentEdge.getEndVertex().setPreviousEdge();
				}
				currentEdge.setFlow(currentEdge.getFlow()-deltaFlow);
			}
		}
		return deltaFlow;
	}
	
	
	/*********************************************************
	 * Goldberg-Tarjan algorithm functions
	 *********************************************************/
	//commented in interface
	public int initialPush(int startVertexId, int endVertexId) {
		startVertex = vertices.get(startVertexId);
		endVertex = vertices.get(endVertexId);
		queue = new LinkedList<Vertex>();
		LinkedList<Edge> startEdges = startVertex.getAllEdges();
		ListIterator<Edge> listIterator = startEdges.listIterator();
		//loop over all outgoing edges of the source
		while (listIterator.hasNext()) {
			Vertex newVertex = listIterator.next().pushFlowForward();
			if (newVertex != null && newVertex != startVertex && newVertex != endVertex) {
				queue.add(newVertex);
			}
		}
		//return queue length
		return queue.size();
	}
	
	//commented in interface
	public int dischargeQueue() {
		//discharge head vertex
		Vertex headVertex = queue.removeFirst();
		headVertex.resetEdge();
		while (headVertex.getExcess()>0 && !headVertex.labelIncreased()) {
			Vertex newVertex = headVertex.push_relabel();
			if (newVertex != null && newVertex != startVertex && newVertex != endVertex) {	//add new vertex to queue
				queue.add(newVertex);
			}
		}
		if (headVertex.getExcess() > 0) {	//vertex still active
			headVertex.resetIncreasedLabel();
			queue.add(headVertex);
		}
		//return queue length
		return queue.size();
	}
	
	//commented in interface
	public int getOutFlow(int vertexId) {
		return vertices.get(vertexId).getOutFlow();
	}
	//commented in interface
	public int getInFlow(int vertexId) {
		return vertices.get(vertexId).getInFlow();
	}

	//commented in interface
	public String graphToString() {
		StringBuilder s = new StringBuilder();
		for (Map.Entry<Integer, Vertex> entry : vertices.entrySet())
        {
			s.append(entry.getValue().vertexToString()+NEWLINE);
        }
		return s.toString();
	}
	
	//commented in interface
	public LinkedList<Integer[]> getGraphData() {
		LinkedList<Integer[]> graphEdges = new LinkedList<Integer[]>();
		//iterate over vertices
		for (Map.Entry<Integer, Vertex> entry : vertices.entrySet())
        {
			graphEdges.addAll(entry.getValue().getEdgeData());
        }
		
		return graphEdges;
	}
	
	//commented in interface
	public LinkedList<Integer> getVertexIndices() {
		LinkedList<Integer> veritecsIds = new LinkedList<Integer>();
		//iterate over vertices
		for (Map.Entry<Integer, Vertex> entry : vertices.entrySet())
        {
			veritecsIds.add(entry.getValue().id());
        }
		
		return veritecsIds;
	}
}
