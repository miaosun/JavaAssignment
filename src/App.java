import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 *  This class is the main driver for the app. <p>
 *
 *  It creates the Main Frame which contains menu, the operation and result panels.
 *  It then displays the GUI frame to the user.
 *
 *  @author Miao Sun
 */
public class App {

	/**
	 *  Main method to create the app and display the GUI frame.
	 */
	public static void main(String[] args) {

		SwingUtilities.invokeLater(new Runnable() {

			public void run() {		
				JFrame frame = new MainFrame("Matrix Operations");
				frame.setSize(800, 1000);
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.setVisible(true);
			}	
		});
	}
}
