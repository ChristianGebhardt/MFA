package de.lmu.ifi.mfa_gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.model.mxGraphModel;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;

import de.lmu.ifi.mfa.IFlowNetwork;

/**
 *  The <tt>MFAView</tt> class is the view of a maximum flow algorithm program. 
 *  It is connected to a controller {@link MFAController}, that evaluates and forwards
 *  the user input to the flow network model {@link IFlowNetwork}. 
 *  The view and the controller build an exchangeable controller-view unit in the package <tt>mfa_gui</tt>.
 *  <p>
 *  The <tt>MFAView</tt> class is an implementation of {@link Observer}, which has to be attached to the model.
 *  This allows the retrieve update information and query the current status of the flow network from the model
 *  <p>
 *  For additional information about the program, see <a href="https://github.com/ChristianGebhardt/mfa">MFA</a>
 *  by Christian Gebhardt on Github.
 *  
 *
 * @author  Christian Gebhardt
 * @version 1.0.1
 * @since   2016-09-03
 */
public class MFAView extends JFrame implements Observer, ActionListener {

	//constant values
	private static final long serialVersionUID = 1L;
	private static final String NEWLINE = System.getProperty("line.separator");

	//main variables
	private IFlowNetwork myFlowNet = null;

	//complex swing components
	private     JSplitPane  splitPaneH;
	private     JSplitPane  splitPaneV;
	private     JPanel      panel1a;
	private     JPanel      panel1b;
	private     JPanel      panel2;

	//vertex
	private JTextField txtAddVertexId;
	private JTextField txtRemoveVertexId;
	private JButton cmdAddV;
	private JButton cmdRemoveV;
	//edge
	private JTextField txtAddEdgeId1;
	private JTextField txtAddEdgeId2;
	private JTextField txtAddEdgeCap;
	private JTextField txtRemoveEdgeId1;
	private JTextField txtRemoveEdgeId2;
	private JButton cmdAddE;
	private JButton cmdRemoveE;
	
	//source/sink
	private JTextField txtSourceId;
	private JTextField txtSinkId;
	private JButton cmdSetSource;
	private JButton cmdSetSink;
	
	//evaluate
	private JButton cmdDinic;
	private JButton cmdGoldberg;
	
	//control
	private JButton cmdReset;
	private JButton cmdSave;
	private JButton cmdLoad;
	
	//output
	private JTextArea txtPrompt;
	private JTextArea txtDisplay;
	
	//mxGraph
	private mxGraph graph;
	private mxGraphComponent graphComponent;
	private Map<Integer,Object> vertices;
	
	//menu
	JMenuBar menuBar;
	JMenu info;
	JMenuItem help;
    JMenuItem about;
	
	//screen constants
	private static final int MIN_WIDTH = 1200;
	private static final int MIN_HEIGHT = 800;
	
    /**
     * The constructor uses the flow network {@link IFlowNetwork} as observable to create a
     * view for this model. Therefore, the constructor packs all necessary swing
     * object on the a frame displays it as view of the program.
     *  
     * @param flowNet the model for the flow network of the program.
     */
    public MFAView(IFlowNetwork flowNet) {
    	//Model
    	this.myFlowNet = flowNet;
    	
    	//GUI
    	setTitle( "Maximum Flow Algorithm" );
        setBackground(Color.gray);
        
        //MenuBar
        menuBar = new JMenuBar();
        info = new JMenu("Help");
        menuBar.add(info);
        //"help" menu item
        URL urlHelp = MFAView.class.getResource("/resources/questionmark16.png");	//works only for images in JAR
		ImageIcon helpIcon = new ImageIcon(urlHelp);
        help = new JMenuItem("Help Contents", helpIcon);
        help.addActionListener(this);
        //"about" menu item
        URL urlAbout = MFAView.class.getResource("/resources/lmu16.gif");
		ImageIcon aboutIcon = new ImageIcon(urlAbout);
        about = new JMenuItem("About MFA",aboutIcon);
        about.addActionListener(this);
        info.add(help);
        info.add(about);
        add(menuBar, BorderLayout.NORTH);
        
        //Screen size
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        if (dim.getWidth()<MIN_WIDTH || dim.getHeight()<MIN_HEIGHT) {
        	setExtendedState(java.awt.Frame.MAXIMIZED_BOTH);
        	setMinimumSize(dim);
        } else {
        	setMinimumSize(new Dimension(MIN_WIDTH, MIN_HEIGHT));
        }
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);       
        //change graph location on resize
        addComponentListener(new SizeListener());
        
        //Program icon (LMU logo)
        URL url = MFAView.class.getResource("/resources/lmu.gif");
        ImageIcon icon = new ImageIcon(url);
        setIconImage(icon.getImage());
        
        //top panel (deepest layer)
        JPanel topPanel = new JPanel();
        topPanel.setLayout( new BorderLayout() );
        getContentPane().add( topPanel );

        // Create the panels
        createPanel1a();
        createPanel1b();
        createPanel2();

        // Create a splitter pane
        splitPaneH = new JSplitPane( JSplitPane.HORIZONTAL_SPLIT );	//left: input+output  - right: display
        topPanel.add( splitPaneH, BorderLayout.CENTER );

        splitPaneV = new JSplitPane( JSplitPane.VERTICAL_SPLIT );	//top: input  - bottom: output
        splitPaneV.setLeftComponent(panel1a);
        splitPaneV.setRightComponent(panel1b);

        splitPaneH.setLeftComponent(splitPaneV);
        splitPaneH.setRightComponent(panel2);
        
        //finally pack and show view
        pack();
	    setLocationRelativeTo(null);	//set view in the middle of the screen
	    setVisible(true);
    }

    //Input Field Panel
    private void createPanel1a(){
        panel1a = new JPanel();
        panel1a.setLayout(new BorderLayout());
        panel1a.setPreferredSize(new Dimension( 500, 500));

        // Create Sub-Panels
        JPanel inputMask = new JPanel();
        inputMask.setLayout(new BoxLayout(inputMask, BoxLayout.PAGE_AXIS));
        inputMask.setOpaque(true);
        inputMask.setBackground(Color.WHITE);
        inputMask.add(Box.createRigidArea(new Dimension(0,25)));
        //Vertex
        JPanel vertexPanel = new JPanel();
        vertexPanel.setLayout(new FlowLayout());
        vertexPanel.setOpaque(true);
        vertexPanel.setBackground(Color.WHITE);
        vertexPanel.setBorder(BorderFactory.createTitledBorder("Vertex"));
        txtAddVertexId = new JTextField(3);						//add vertex id text field
        txtAddVertexId.setToolTipText("vertex-ID (add)");
        vertexPanel.add(txtAddVertexId);
        cmdAddV = new JButton("Add Vertex");					//add vertex button
        cmdAddV.setToolTipText("add vertex-ID to flow network");
        vertexPanel.add(cmdAddV);
        vertexPanel.add(Box.createRigidArea(new Dimension(10,0)));
        txtRemoveVertexId = new JTextField(3);					//remove vertex id text field
        txtRemoveVertexId.setToolTipText("vertex ID (remove)");
        vertexPanel.add(txtRemoveVertexId);
        cmdRemoveV = new JButton("Remove Vertex");				//remove vertex button
        cmdRemoveV.setToolTipText("remove vertex-ID from flow network");
        vertexPanel.add(cmdRemoveV);
        inputMask.add(vertexPanel);
        inputMask.add(Box.createRigidArea(new Dimension(0,10)));
        //Edge
        JPanel edgePanel = new JPanel();
        edgePanel.setLayout(new FlowLayout());
        edgePanel.setOpaque(true);
        edgePanel.setBackground(Color.WHITE);
        edgePanel.setBorder(BorderFactory.createTitledBorder("Edge"));
        txtAddEdgeId1 = new JTextField(3);						//add edge id 1 text field
        txtAddEdgeId1.setToolTipText("start-vertex ID (add edge)");
        edgePanel.add(txtAddEdgeId1);
        txtAddEdgeId2 = new JTextField(3);						//add edge id 2 text field
        txtAddEdgeId2.setToolTipText("end-vertex ID (add edge)");
        edgePanel.add(txtAddEdgeId2);
        txtAddEdgeCap = new JTextField(3);						//add edge capacity text field
        txtAddEdgeCap.setToolTipText("capacity (add edge)");
        edgePanel.add(txtAddEdgeCap);
        cmdAddE = new JButton("Add Edge");						//add edge button
        cmdAddE.setToolTipText("add edge to flow network");
        edgePanel.add(cmdAddE);
        edgePanel.add(Box.createRigidArea(new Dimension(10,0)));
        txtRemoveEdgeId1 = new JTextField(3);						//remove edge id 1 text field
        txtRemoveEdgeId1.setToolTipText("start-vertex ID (remove edge)");
        edgePanel.add(txtRemoveEdgeId1);
        txtRemoveEdgeId2 = new JTextField(3);						//remove edge id 2 text field
        txtRemoveEdgeId2.setToolTipText("end-vertex ID (remove edge)");
        edgePanel.add(txtRemoveEdgeId2);
        cmdRemoveE = new JButton("Remove Edge");					//remove edge button
        cmdRemoveE.setToolTipText("remove edge from flow network");
        edgePanel.add(cmdRemoveE);
        inputMask.add(edgePanel);
        inputMask.add(Box.createRigidArea(new Dimension(0,10)));
        //Source/Sink
        JPanel source_sink = new JPanel();
        source_sink.setLayout(new FlowLayout());
        source_sink.setOpaque(true);
        source_sink.setBackground(Color.WHITE);
        source_sink.setBorder(BorderFactory.createTitledBorder("Source / Sink"));
        txtSourceId = new JTextField(3);						//set source id text field
        txtSourceId.setToolTipText("source-vertex ID");
        source_sink.add(txtSourceId);
        cmdSetSource = new JButton("Set Source");				//set source button
        cmdSetSource.setToolTipText("set source of flow network");
        source_sink.add(cmdSetSource);
        source_sink.add(Box.createRigidArea(new Dimension(10,0)));
        txtSinkId = new JTextField(3);							//set sink id text field
        txtSinkId.setToolTipText("sink-vertex ID");
        source_sink.add(txtSinkId);
        cmdSetSink = new JButton("Set Sink");					//set sink button
        cmdSetSink.setToolTipText("set sink of flow network");
        source_sink.add(cmdSetSink);
        inputMask.add(source_sink);
        inputMask.add(Box.createRigidArea(new Dimension(0,25)));
        //Evaluate
        JPanel evaluate = new JPanel();
        evaluate.setLayout(new FlowLayout());
        evaluate.setOpaque(true);
        evaluate.setBackground(Color.WHITE);
        evaluate.setBorder(BorderFactory.createTitledBorder("Evaluate"));
        cmdDinic = new JButton("Dinic");						//evaluate Dinic button
        cmdDinic.setToolTipText("calculate maximum flow with Dinic algorithm");
        evaluate.add(cmdDinic);
        evaluate.add(Box.createRigidArea(new Dimension(10,0)));
        cmdGoldberg = new JButton("Goldberg-Tarjan");			//evaluate Goldberg-Tarjan button
        cmdGoldberg.setToolTipText("calculate maximum flow with Goldberg-Tarjan algorithm");
        evaluate.add(cmdGoldberg);
        inputMask.add(evaluate);
        inputMask.add(Box.createRigidArea(new Dimension(0,25)));
        //Control
        JPanel control = new JPanel();
        control.setLayout(new FlowLayout());
        control.setOpaque(true);
        control.setBackground(Color.WHITE);
        control.setBorder(BorderFactory.createTitledBorder("Control"));
        cmdReset = new JButton("Reset Flow Network");			//reset flow network button
        cmdReset.setToolTipText("reset flow network to empty network");
        control.add(cmdReset);
        control.add(Box.createRigidArea(new Dimension(10,0)));
        cmdSave = new JButton("Save Flow Network");				//save flow network button
        cmdSave.setToolTipText("save current flow network to file");
        control.add(cmdSave);
        control.add(Box.createRigidArea(new Dimension(10,0)));
        cmdLoad = new JButton("Load Flow Network");				//load flow network button
        cmdLoad.setToolTipText("load flow network from demo example or from file");
        control.add(cmdLoad);
        inputMask.add(control);
        inputMask.add(Box.createRigidArea(new Dimension(0,25)));
        
        // Add everything
        panel1a.add(new JLabel( "Input:"), BorderLayout.NORTH);
        panel1a.add(inputMask,BorderLayout.CENTER);
    }

    //Output field
    private void createPanel1b(){
        panel1b = new JPanel();
        panel1b.setLayout(new BorderLayout());
        panel1b.setPreferredSize(new Dimension(500, 100));      
        
        //Add everything
        panel1b.add(new JLabel( "Output:"), BorderLayout.NORTH);
        txtPrompt  = new JTextArea(5,40);
        panel1b.add(txtPrompt, BorderLayout.CENTER);
    }

    //Create the display field (text field and graphical network representation)
    private void createPanel2(){
        panel2 = new JPanel();
        panel2.setLayout(new BorderLayout());
        panel2.setPreferredSize( new Dimension(600, 700));
        panel2.setMinimumSize( new Dimension(600, 600));

        // Create Sub-Panels
        JPanel outputMask = new JPanel();
        outputMask.setLayout(new BoxLayout(outputMask, BoxLayout.PAGE_AXIS));
        outputMask.setOpaque(true);
        outputMask.setBackground(Color.WHITE);
        outputMask.add(Box.createRigidArea(new Dimension(0,25)));
        //Text area  
        txtDisplay = new JTextArea();
        txtDisplay.setText(helpMessage());
        JScrollPane txtOutputPanel = new JScrollPane(txtDisplay);	//initial help (might be removed again)
        txtOutputPanel.setPreferredSize(new Dimension(600, 250));
        outputMask.add(txtOutputPanel);
        outputMask.add(Box.createRigidArea(new Dimension(0,10)));
        //Graphic area
        JPanel graphPanel = new JPanel();
        graphPanel.setLayout(new BorderLayout());
        graphPanel.setOpaque(true);
        graphPanel.setBackground(Color.WHITE);
        graphPanel.setBorder(BorderFactory.createTitledBorder("Visualization"));
        //Graph (
        graph = new mxGraph();
        graphComponent = new mxGraphComponent(graph);
        graphComponent.setPreferredSize(new Dimension(600, 350));
        graphComponent.setEnabled(false);		//no moving of vertices/edge
        graphComponent.setConnectable(false);	//no connection of vertices
        graphPanel.add(graphComponent, BorderLayout.CENTER);
        this.revalidate();		//repaint all the components of the container (important when the graph is initialized with some components)
        this.repaint();			//repaint the container itself
        outputMask.add(graphPanel);
        outputMask.add(Box.createRigidArea(new Dimension(0,25)));
        
        //Add everything
        panel2.add( new JLabel( "Display:" ), BorderLayout.NORTH );
        panel2.add(outputMask, BorderLayout.CENTER);
    }

    //rearrange the graph morphology automatically by using a predefined layout
    private void morphGraph(mxGraph graph, mxGraphComponent graphComponent) {
	    // define layout
    	mxHierarchicalLayout layout = new  mxHierarchicalLayout(graph);
    	layout.setOrientation(SwingConstants.WEST);
	    // layout using morphing
	    graph.getModel().beginUpdate();
	    try {
	            layout.execute(graph.getDefaultParent());
	    } finally {
	    	//template changed from https://jgraph.github.io/mxgraph/docs/js-api/files/util/mxMorphing-js.html
	    	graph.getModel().endUpdate();	    	
	    	
	    	//get the size of layout
	        double widthLayout = graphComponent.getLayoutAreaSize().getWidth();
	        double heightLayout = graphComponent.getLayoutAreaSize().getHeight();
	        //we need to determine the size of the graph
	        double width = graph.getGraphBounds().getWidth();
	        double height = graph.getGraphBounds().getHeight();
	        double leftCorner = (widthLayout - width)/2 > 0 ? (widthLayout - width)/2 : 0;	// max(0, (widthLayout - width)/2)
	        double topCorner = (heightLayout - height)/2 > 0 ? (heightLayout - height)/2 : 0;	// max(0, (heightLayout - height)/2)
	        //set new geometry to set the graph in the center
	        graph.getModel().setGeometry(graph.getDefaultParent(), 
	                new mxGeometry(leftCorner, topCorner,
	                        widthLayout, heightLayout));
	    }
	
	}
   
   /**
    * Update the output message as well as the textual and graphical network representation.
    * This method is called by the model (observable), when the status or the morphology of
    * the flow network has changed.
    */
   public void update(Observable obs, Object obj) {
      if (obs == myFlowNet)
      {
         if (myFlowNet.isUpdateGraph()) {
	         this.txtDisplay.setText(myFlowNet.displayFlowNetwork());
	         this.txtDisplay.setSelectionStart(0);	//Go to the top of text area
	         this.txtDisplay.setSelectionEnd(0); 
	         this.updateGraph();
         }
         //display graph
         if (myFlowNet.isDrawGraph()) {
	         this.drawGraph();
         }
         this.txtPrompt.setText(myFlowNet.getPrompt());
      }
   }
   
   //draw graph in panel new
   private void drawGraph() {
	   LinkedList<Integer[]> edges = myFlowNet.getGraphData();
	   LinkedList<Integer> verticeIds = myFlowNet.getVertexIndices();
	   int startId = myFlowNet.getSource();
	   int endId = myFlowNet.getSink();
	   vertices = new HashMap<Integer,Object>();
	   
	   //remove all graph components first
	   graph.removeCells(graph.getChildCells(graph.getDefaultParent()));
	   
	   graph.getModel().beginUpdate();
	   try {
		   //add vertices with edges
		   Object parent = graph.getDefaultParent();
		   ListIterator<Integer[]> listIterator = edges.listIterator();
			while (listIterator.hasNext()) {
				Integer[] nextEdge = listIterator.next();
				if (nextEdge.length > 0 && !vertices.containsKey(nextEdge[0])) {	//add start vertex of edge
					Object a;
					if (startId == nextEdge[0]) {	//check for source or sink vertex
						a = graph.insertVertex(parent, nextEdge[0]+"", nextEdge[0]+"", 0, 0, 50, 30,"fillColor=green");
					} else if (endId == nextEdge[0]) {
						a = graph.insertVertex(parent, nextEdge[0]+"", nextEdge[0]+"", 0, 0, 50, 30,"fillColor=red");
					} else {
						a = graph.insertVertex(parent, nextEdge[0]+"", nextEdge[0]+"", 0, 0, 50, 30);
					}
					vertices.put(nextEdge[0], a);
				}
				if (nextEdge.length > 0 && !vertices.containsKey(nextEdge[1])) {	//add end vertex of edge
					Object a;
					if (endId == nextEdge[1]) {		//check for source or sink vertex
						a = graph.insertVertex(parent, nextEdge[1]+"", nextEdge[1]+"", 0, 0, 50, 30,"fillColor=red");
					} else if (startId == nextEdge[1]) {
						a = graph.insertVertex(parent, nextEdge[1]+"", nextEdge[1]+"", 0, 0, 50, 30,"fillColor=green");
					} else {
						a = graph.insertVertex(parent, nextEdge[1]+"", nextEdge[1]+"", 0, 0, 50, 30);
					}
					vertices.put(nextEdge[1], a);
				}
			}
			//add vertices without edges
			ListIterator<Integer> listIteratorV = verticeIds.listIterator();
			Object a;
			while (listIteratorV.hasNext()) {
				Integer vId = listIteratorV.next();
				if (!vertices.containsKey(vId)) {
					if (endId == vId) {		//check for source or sink vertex
						a = graph.insertVertex(parent, vId+"", vId+"", 0, 0, 50, 30,"fillColor=red");
					} else if (startId == vId) {
						a = graph.insertVertex(parent, vId+"", vId+"", 0, 0, 50, 30,"fillColor=green");
					} else {
						a = graph.insertVertex(parent, vId+"", vId+"", 0, 0, 50, 30);
					}
					vertices.put(vId, a);
				}
			}
			//add edges;
		    listIterator = edges.listIterator();
			while (listIterator.hasNext()) {
				Integer[] nextEdge = listIterator.next();
				graph.insertEdge(parent, null, nextEdge[3]+"/"+nextEdge[2], vertices.get(nextEdge[0]), vertices.get(nextEdge[1]));
			}
	   } finally {
		   graph.getModel().endUpdate();
	   }
	   //rearrange components
	   morphGraph(graph, graphComponent);
   }
   
   //update edges and edge labels in graph (without redrawing the graph totally)
   private void updateGraph() {
	   if (vertices != null) {
		   LinkedList<Integer[]> edges = myFlowNet.getGraphData();
		   graph.getModel().beginUpdate();
		   try {
			   //remove all edges and add again
			   Object parent = graph.getDefaultParent();
			   ListIterator<Integer[]> listIterator = edges.listIterator();
				while (listIterator.hasNext()) {
					Integer[] nextEdge = listIterator.next();
					if (nextEdge.length > 0 && vertices.containsKey(nextEdge[0]) && vertices.containsKey(nextEdge[1])) {
						Object[] edgeList = graph.getEdgesBetween(vertices.get(nextEdge[0]), vertices.get(nextEdge[1]),true); //true: only edges in one direction
						if (edgeList.length > 0) {
							((mxGraphModel) graph.getModel()).remove(edgeList[0]);
							graph.removeCells(edgeList);
							graph.insertEdge(parent, null, nextEdge[3]+"/"+nextEdge[2], vertices.get(nextEdge[0]), vertices.get(nextEdge[1]));
						} else {
							//Do nothing
						}
					}
				}
	
		   } finally {
			   graph.getModel().endUpdate();
		   }
		   //rearrange components
		   morphGraph(graph, graphComponent);
	   } else {
		   //Do nothing
	   }
   }
   
   //add action listener
   protected void setAddVertexListener(ActionListener l){
       this.cmdAddV.addActionListener(l);
   }
   protected void setRemoveVertexListener(ActionListener l){
       this.cmdRemoveV.addActionListener(l);
   }
   protected void setAddEdgeListener(ActionListener l){
       this.cmdAddE.addActionListener(l);
   }
   protected void setRemoveEdgeListener(ActionListener l){
       this.cmdRemoveE.addActionListener(l);
   }
   protected void setSourceListener(ActionListener l){
       this.cmdSetSource.addActionListener(l);
   }
   protected void setSinkListener(ActionListener l){
       this.cmdSetSink.addActionListener(l);
   }
   protected void setDinicListener(ActionListener l){
       this.cmdDinic.addActionListener(l);
   }
   protected void setGoldbergListener(ActionListener l){
       this.cmdGoldberg.addActionListener(l);
   }
   protected void setResetListener(ActionListener l){
       this.cmdReset.addActionListener(l);
   }
   protected void setSaveListener(ActionListener l){
       this.cmdSave.addActionListener(l);
   }
   protected void setLoadListener(ActionListener l){
       this.cmdLoad.addActionListener(l);
   }
   
   
   //return input values from text fields
   protected int getAddVertexId() {
	   try {
		   return Integer.parseInt(this.txtAddVertexId.getText());
	   } catch (Exception evt) {
		   return -1;
	   }
   }
   protected int getRemoveVertexId() {
	   try {
		   return Integer.parseInt(this.txtRemoveVertexId.getText());
	   } catch (Exception evt) {
		   return -1;
	   }
   }
   protected int getAddEdgeId1() {
	   try {
		   return Integer.parseInt(this.txtAddEdgeId1.getText());
	   } catch (Exception evt) {
		   return -1;
	   }
   }
   protected int getAddEdgeId2() {
	   try {
		   return Integer.parseInt(this.txtAddEdgeId2.getText());
	   } catch (Exception evt) {
		   return -1;
	   }
   }
   protected int getAddEdgeCap() {
	   try {
		   return Integer.parseInt(this.txtAddEdgeCap.getText());
	   } catch (Exception evt) {
		   return -1;
	   }
   }
   protected int getRemoveEdgeId1() {
	   try {
		   return Integer.parseInt(this.txtRemoveEdgeId1.getText());
	   } catch (Exception evt) {
		   return -1;
	   }
   }
   protected int getRemoveEdgeId2() {
	   try {
		   return Integer.parseInt(this.txtRemoveEdgeId2.getText());
	   } catch (Exception evt) {
		   return -1;
	   }
   }
   protected int getSourceId() {
	   try {
		   return Integer.parseInt(this.txtSourceId.getText());
	   } catch (Exception evt) {
		   return -1;
	   }
   }
   protected int getSinkId() {
	   try {
		   return Integer.parseInt(this.txtSinkId.getText());
	   } catch (Exception evt) {
		   return -1;
	   }
   }

	/**
	 * Action listener for the menu items. This method opens the windows for the help and about
	 * message, when the related menu item is pressed.
	 * 
	 * @param object the action event of the menu item.
	 */
    @Override
	public void actionPerformed(ActionEvent object) {
		if (object.getSource() == help){	//show help window
	        final JDialog frame = new JDialog(this, "Help Contents", true);
	        JTextArea helpText = new JTextArea(10,100);
	        String helpMsg = helpMessage();
	        helpText.setText(helpMsg);
	        helpText.setEditable(false);
	        frame.getContentPane().add(helpText);
	        frame.pack();
	        frame.setLocationRelativeTo(null);
	        frame.setVisible(true);
	   }
	   if (object.getSource() == about){	//show about window
	        final JDialog frame = new JDialog(this, "About MFA", true);
	        JTextArea helpText = new JTextArea(10,80);
	        String aboutMsg = aboutMessage();
	        helpText.setText(aboutMsg);
	        helpText.setEditable(false);
	        frame.getContentPane().add(helpText);
	        frame.pack();
	        frame.setLocationRelativeTo(null);
	        frame.setVisible(true);
	   }	
	}
    
    //checks the size of the frames and adjust the position of the visualized graph
    private class SizeListener implements ComponentListener{
        public void componentHidden(ComponentEvent arg0) {
        }
        public void componentMoved(ComponentEvent arg0) {   
        }
        //recalculate the center of the frame and reset the graph position
        public void componentResized(ComponentEvent arg0) {
            //get the size of layout
	        double widthLayout = graphComponent.getLayoutAreaSize().getWidth();
	        double heightLayout = graphComponent.getLayoutAreaSize().getHeight();
	        //we need to determine the size of the graph
	        double width = graph.getGraphBounds().getWidth();
	        double height = graph.getGraphBounds().getHeight();
	        double leftCorner = (widthLayout - width)/2 > 0 ? (widthLayout - width)/2 : 0;	// max(0, (widthLayout - width)/2)
	        double topCorner = (heightLayout - height)/2 > 0 ? (heightLayout - height)/2 : 0;	// max(0, (heightLayout - height)/2)
	        //set new geometry to set the graph in the center
	        graph.getModel().setGeometry(graph.getDefaultParent(), 
	                new mxGeometry(leftCorner, topCorner,
	                        widthLayout, heightLayout));

        }
        public void componentShown(ComponentEvent arg0) {

        }
    }
   
	//create help message to show in the help window (and at the program start)
	private String helpMessage() {
		StringBuilder s = new StringBuilder();
		s.append("Getting Started" + NEWLINE);
		s.append("===============" + NEWLINE + NEWLINE);
		s.append("1. Create directed graph with <<Add Vertex>> and <<Add Edge>>" + NEWLINE);
		s.append("   > insert vertex identifier and edge capacity in text fields beside button and press button" + NEWLINE);
		s.append("   > correct graph with <<Remove Vertex>> and <<Remove Edge>> if necessary" + NEWLINE);
		s.append("2. Set source and sink vertex" + NEWLINE);
		s.append("   > insert vertex identifier in text fields and press button" + NEWLINE);
		s.append("3. Calculate maximum flow with <<Dinic>> or <<Goldberg-Tarjan>>" + NEWLINE);
		s.append("   > press respective button" + NEWLINE + NEWLINE);
		
		s.append("Further Features" + NEWLINE);
		s.append("================" + NEWLINE + NEWLINE);
		s.append("- Click on <<Reset Flow Network>> to set flow network to initial empty state" + NEWLINE);
		s.append("- Save current flow network to file system with <<Save Flow Network>>" + NEWLINE);
		s.append("- Load old flow network from demo example or file system with <<Load Flow Network>>" + NEWLINE);
		
		return s.toString();
	}
	
	//create about message to show in the about window
	private String aboutMessage() {
		StringBuilder s = new StringBuilder();
		s.append("Maximum Flow Algorithm Application" + NEWLINE + NEWLINE);
		s.append("Version: Ringberg Release (1.0.1)" + NEWLINE);
		s.append("Build id: 20160831-0100" + NEWLINE);
		s.append("(c) Copyright Chrisitan Gebhardt 2016. All rights reserved." + NEWLINE);
		s.append("This product includes software developed by other open source projects" + NEWLINE);
		s.append("including the  'Apache Software Foundation' and " + NEWLINE + "the 'JGraphX Swing Component - Java Graph Visualization Library'." + NEWLINE + NEWLINE);
		s.append("------------------------------------------------------------------------" + NEWLINE + NEWLINE);
		
		s.append("Contact Information" + NEWLINE + NEWLINE);
		s.append("Christian Gebhardt" + NEWLINE);
		s.append("Ludwigs-Maximilians-Universität München" + NEWLINE);
		s.append("Lenggrieser Str. 6" + NEWLINE);
		s.append("81371 München" + NEWLINE);
		s.append("Germany" + NEWLINE);
		s.append("Phone: +49 (0) 89 2180 3704" + NEWLINE);
		s.append("Email: gebhardt.christian@campus.lmu.de" + NEWLINE);

		return s.toString();
	}
}
