package de.lmu.ifi.mfa_gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import de.lmu.ifi.mfa.IFlowNetwork;

/**
 *  The <tt>MFAController</tt> serves as a controller for its view {@link MFAView}.
 *  They build an exchangeable controller-view unit in the package <tt>mfa_gui</tt>,
 *  which can be used in an MVC-architecture for a maximum flow algorithm program.
 *  Therefore, they use an implementation of the {@link IFlowNetwork} interface as
 *  data model.
 *  <p>
 *  For additional information about the program, see <a href="https://github.com/ChristianGebhardt/mfa">MFA</a>
 *  by Christian Gebhardt on Github.
 *  
 *
 * @author  Christian Gebhardt
 * @version 1.0.1
 * @since   2016-09-03
 */
public class MFAController {

	//main variables
    private MFAView ctrlView;
    private IFlowNetwork ctrlModel;

    /**
     * The constructor creates an instance of <tt>MFAController</tt> and
     * constructs the MVC-architecture. Therefore the constructor adds action listener
     * to swing object of the view. This action listeners manipulate the model according to
     * the program logic.
     * 
     * @param ctrlView the view of the program. 
     * @param ctrlModel the model for the flow network of the program.
     */
    public MFAController(MFAView ctrlView, IFlowNetwork ctrlModel){
        this.ctrlModel = ctrlModel;
        this.ctrlView = ctrlView;

        addListeners();
    }
    
    //add all listeners to to the several buttons
    private void addListeners(){
        this.ctrlView.setAddVertexListener(new AddVertexListener());
        this.ctrlView.setRemoveVertexListener(new RemoveVertexListener());
        this.ctrlView.setAddEdgeListener(new AddEdgeListener());
        this.ctrlView.setRemoveEdgeListener(new RemoveEdgeListener());
        this.ctrlView.setSourceListener(new SourceListener());
        this.ctrlView.setSinkListener(new SinkListener());
        this.ctrlView.setDinicListener(new DinicListener());
        this.ctrlView.setGoldbergListener(new GoldbergListener());
        this.ctrlView.setResetListener(new ResetListener());
        this.ctrlView.setSaveListener(new SaveListener());
        this.ctrlView.setLoadListener(new LoadListener());
    }
    
    //Inner listener classes implementing the interface ActionListener
    /**
     * Action Listener for the "Add Vertex" button.
     * 
     * @author  Christian Gebhardt
	 * @version 1.0.1
	 * @since   2016-09-03
     */
    class AddVertexListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
        	int vertexId = ctrlView.getAddVertexId();
        	ctrlModel.addVertex(vertexId);
        }
    }
    /**
     * Action Listener for the "Remove Vertex" button.
     * 
     * @author  Christian Gebhardt
	 * @version 1.0.1
	 * @since   2016-09-03
     */
    class RemoveVertexListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
        	int vertexId = ctrlView.getRemoveVertexId();
        	ctrlModel.removeVertex(vertexId);
        }
    }
    /**
     * Action Listener for the "Add Edge" button.
     * 
     * @author  Christian Gebhardt
	 * @version 1.0.1
	 * @since   2016-09-03
     */
    class AddEdgeListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            int startEdgeId = ctrlView.getAddEdgeId1();
            int endEdgeId = ctrlView.getAddEdgeId2();
            int capEdge = ctrlView.getAddEdgeCap();
        	ctrlModel.addEdge(startEdgeId,endEdgeId,capEdge);
        }
    }
    /**
     * Action Listener for the "Remove Edge" button.
     * 
     * @author  Christian Gebhardt
	 * @version 1.0.1
	 * @since   2016-09-03
     */
    class RemoveEdgeListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
        	int startEdgeId = ctrlView.getRemoveEdgeId1();
            int endEdgeId = ctrlView.getRemoveEdgeId2();
        	ctrlModel.removeEdge(startEdgeId,endEdgeId);
        }
    }
    /**
     * Action Listener for the "Set Source" button.
     * 
     * @author  Christian Gebhardt
	 * @version 1.0.1
	 * @since   2016-09-03
     */
    class SourceListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
        	int sourceId = ctrlView.getSourceId();
        	ctrlModel.setSource(sourceId);
        }
    }
    /**
     * Action Listener for the "Set Sink" button.
     * 
     * @author  Christian Gebhardt
	 * @version 1.0.1
	 * @since   2016-09-03
     */
    class SinkListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
        	int sinkId = ctrlView.getSinkId();
        	ctrlModel.setSink(sinkId);
        }
    }
    /**
     * Action Listener for the "Dinic" button.
     * 
     * @author  Christian Gebhardt
	 * @version 1.0.1
	 * @since   2016-09-03
     */
    class DinicListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
        	ctrlModel.dinic();
        }
    }
    /**
     * Action Listener for the "Goldberg-Tarjan" button.
     * 
     * @author  Christian Gebhardt
	 * @version 1.0.1
	 * @since   2016-09-03
     */
    class GoldbergListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
        	ctrlModel.goldbergTarjan();
        }
    }
    /**
     * Action Listener for the "Reset Flow Network" button.
     * 
     * @author  Christian Gebhardt
	 * @version 1.0.1
	 * @since   2016-09-03
     */
    class ResetListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
        	ctrlModel.resetNetwork();
        }
    }
    /**
     * Action Listener for the "Save Flow Network" button.
     * 
     * @author  Christian Gebhardt
	 * @version 1.0.1
	 * @since   2016-09-03
     */
    class SaveListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
        	JFileChooser fileChooser = new JFileChooser();
        	// add filter
        	FileNameExtensionFilter mfaFilter = new FileNameExtensionFilter("mfa files (*.mfa)", "mfa");
        	fileChooser.addChoosableFileFilter(mfaFilter);
        	fileChooser.setFileFilter(mfaFilter);
        	fileChooser.setSelectedFile(new File("*.mfa"));
        	if (fileChooser.showSaveDialog(ctrlView) == JFileChooser.APPROVE_OPTION) {
        	  File file = fileChooser.getSelectedFile();
        	  if (!file.toString().endsWith(".mfa")) {
        	        String filename = file.toString()+".mfa";
        	        file = new File(filename);
        	  }
        	  ctrlModel.saveNetwork(file);
        	}
        }
    }
    /**
     * Action Listener for the "Load Flow Network" button.
     * 
     * @author  Christian Gebhardt
	 * @version 1.0.1
	 * @since   2016-09-03
     */
    class LoadListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
        	int example = loadExample();
        	if (example == 0) {	//load example network from the internal resources
        		try {
	        		File file = getFile();
	        		ctrlModel.loadNetwork(file);
        		} catch (Exception ex) {}
        	} else if (example == 1) {	//load network from the file system (type: *.mfa)
	        	JFileChooser fileChooser = new JFileChooser();
	        	// add file filter
	        	FileNameExtensionFilter mfaFilter = new FileNameExtensionFilter("mfa files (*.mfa)", "mfa");
	        	fileChooser.addChoosableFileFilter(mfaFilter);
	        	fileChooser.setFileFilter(mfaFilter);
	        	if (fileChooser.showOpenDialog(ctrlView) == JFileChooser.APPROVE_OPTION) {
	        	  File file = fileChooser.getSelectedFile();
	        	  ctrlModel.loadNetwork(file);
	        	}
        	} else {}
        }
    }

    //Option dialog to load demo example or file from file system
    private int loadExample() {
    	Object[] options = {"Load example", "Load file"};
    	int response = JOptionPane.showOptionDialog(ctrlView,
    	    "Would you like to load a standard example or an external file?",
    	    "Choose Source",
    	    JOptionPane.YES_NO_OPTION,
    	    JOptionPane.PLAIN_MESSAGE,
    	    null,
    	    options,
    	    options[1]);
    	return response;
    }
    
    //Load example from thesis from the internal resources
    private File getFile() {
    	File file = null;
        String resource = "/resources/example.mfa";
        URL res = getClass().getResource(resource);
        if (res.toString().startsWith("jar:")) {
            try {	//read file from jar package (see: http://stackoverflow.com/questions/941754/how-to-get-a-path-to-a-resource-in-a-java-jar-file)
                InputStream input = getClass().getResourceAsStream(resource);
                file = File.createTempFile("example", ".tmp");
                OutputStream out = new FileOutputStream(file);
                int read;
                byte[] bytes = new byte[1024];
                while ((read = input.read(bytes)) != -1) {
                    out.write(bytes, 0, read);
                }
                out.close();
                file.deleteOnExit();
            } catch (IOException ex) {
            	ex.printStackTrace();
            }
        } else {
            //this will work in your IDE, but not from a JAR
            file = new File(res.getFile());
        }
        
        if (file != null && !file.exists()) {
            throw new RuntimeException("Error: File " + file + " not found!");
        }
        return file;
    }
}
