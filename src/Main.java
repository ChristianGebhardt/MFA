import javax.swing.UIManager;

import de.lmu.ifi.mfa.FlowNetwork;
import de.lmu.ifi.mfa_gui.MFAController;
import de.lmu.ifi.mfa_gui.MFAView;

/**
 *  The <tt>Main</tt> class contains the static function <tt>main</tt>
 *  to launch a maximum flow algorithm program.
 *  It uses the following two components: the network flow model, which is an
 *  implementation of {@link FlowNetwork}, and a graphical user interfaces
 *  comprised of a controller {@link MFAController} and its view {@link MFAView}.
 *  <p>
 *  The whole program is an implementation of the MVC-model.
 *  To launch the program, the class has only three static field variables for the
 *  three parts and the <tt>main</tt> function that instantiates and connects them.
 *  <p>
 *  For additional information about the program, see <a href="https://github.com/ChristianGebhardt/mfa">MFA</a>
 *  by Christian Gebhardt on Github.
 *  
 *
 * @author  Christian Gebhardt
 * @version 1.0.1
 * @since   2016-09-03
 */
public class Main {
	/**
	 * The model of the flow network.
	 */
	static FlowNetwork myNetwork;
	/**
	 * The controller of the view.
	 */
	static MFAController myController;
	/**
	 * The view of the program.
	 */
	static MFAView myView;
	
	/**
	 * Start the program. This method instantiate the MVC-components and connects them.
	 * 
	 * @param args unused
	 */
	public static void main(String[] args) {
		//create model
		myNetwork = new FlowNetwork();		
		//create view
		try {
	        UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");	//windows appearance
	    } catch (Exception evt) {}
	    myView = new MFAView(myNetwork);
		//create controller
		myController =new MFAController(myView, myNetwork);
		//connect observable <-> observer
		myNetwork.addObserver(myView);
	}
	
}
