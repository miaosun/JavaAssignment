import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;

/*
 * The Main Frame of the Application
 * Consists of 3 parts: 
 * 1. upper side is a menu which consists 3 sub menus: File (Save, Load and Exit), Look and Feel(several themes) and About
 * 2. middle side is the Operation Panel   @see OperationPanel
 * 3. lower part is the result panel, for showing operational results 
 */
public class MainFrame extends JFrame {

	private static final long serialVersionUID = 1L;

	/**
	 * The Panel for operations (input matrix and vector data), 
	 */
	protected OperationPanel operationPanel;

	/**
	 * The textarea for displaying matrix operation result
	 */
	protected JTextArea resultTextArea;

	/*
	 *  MainFrame constructor
	 *  @param title  the application's title
	 */
	public MainFrame(String title) {

		setTitle(title);

		Container container = this.getContentPane();
		container.setLayout(new BorderLayout());

		//
		// Operation panel, where user input matrix and vector data and carry out operations
		//
		operationPanel = new OperationPanel(this);
		container.add(operationPanel, BorderLayout.CENTER);

		//
		// Result text area for displaying operation result
		//
		resultTextArea = new JTextArea(40,30);
		resultTextArea.setEditable(false);
		JScrollPane resultPane = new JScrollPane(resultTextArea);

		TitledBorder resultBorder = BorderFactory.createTitledBorder("Result:");
		resultBorder.setTitleColor(Color.BLACK);
		resultPane.setBorder(resultBorder);
		container.add(resultPane, BorderLayout.SOUTH);

		// Menu bar
		JMenuBar menuBar = new JMenuBar();

		//
		// File Menu, option with load data from file or save data to file, exit program
		//
		JMenu fileMenu = new JMenu("File");
		JMenuItem loadMenuItem = new JMenuItem("Load");
		JMenuItem saveMenuItem = new JMenuItem("Save");
		JMenuItem exitMenuItem = new JMenuItem("Exit");
		fileMenu.add(loadMenuItem);
		fileMenu.add(saveMenuItem);
		fileMenu.add(exitMenuItem);

		//
		//action listener for file menu options
		//
		loadMenuItem.addActionListener(new LoadActionListener());
		saveMenuItem.addActionListener(new SaveActionListener());
		exitMenuItem.addActionListener(new ExitActionListener());

		menuBar.add(fileMenu);

		//
		// Option menu item with options to change look and feel of the program
		//
		setupLookAndFeelMenu(menuBar);

		//
		// Help Menu, with "About" menu item
		//
		JMenu helpMenu = new JMenu("Help");
		JMenuItem aboutMenuItem = new JMenuItem("About");
		aboutMenuItem.addActionListener(new AboutActionListener());
		helpMenu.add(aboutMenuItem);

		menuBar.add(helpMenu);

		this.setJMenuBar(menuBar);

		this.pack();

	}

	/**
	 *  GIVEN:  Look And Feel
	 *
	 *  Builds a menu based on the installed look and feels.  This method loops
	 *  through each installed look and feel and creates a menu item w/ the name
	 *  of the look and feel.  We also keep track of the class name by setting the
	 *  action command for the menu item.  This allows the associated listener to
	 *  determine the class name to load for the look and feel.  <p>  
	 *
	 *  More info on look and feels see the "LookAndFeelListener" at the bottom of the file.
	 *
	 */
	protected void setupLookAndFeelMenu(JMenuBar theMenuBar) {

		UIManager.LookAndFeelInfo[] lookAndFeelInfo = UIManager.getInstalledLookAndFeels();
		JMenu lookAndFeelMenu = new JMenu("Options");
		JMenuItem anItem = null;
		LookAndFeelListener myListener = new LookAndFeelListener();

		try {
			for (int i=0; i < lookAndFeelInfo.length; i++) {
				anItem = new JMenuItem(lookAndFeelInfo[i].getName() + " Look and Feel");
				anItem.setActionCommand(lookAndFeelInfo[i].getClassName());
				anItem.addActionListener(myListener);

				lookAndFeelMenu.add(anItem);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		theMenuBar.add(lookAndFeelMenu);
	}

	/**
	 *  Listener class to set the look and feel at run time.
	 *
	 *  Changing the look and feel is so simple :-)  Just call the
	 *  method:
	 *
	 *  <code>
	 *     UIManager.setLookAndFeel(<class name of look and feel>);
	 *  </code>
	 *
	 *
	 *  The available look and feels are:
	 *
	 * <pre>
	 *
	 *    NAME             CLASS NAME
	 *    --------         -----------
	 *    Metal            javax.swing.plaf.metal.MetalLookAndFeel
	 *    Windows          com.sun.java.swing.plaf.windows.WindowsLookAndFeel
	 *    Motif            com.sun.java.swing.plaf.motif.MotifLookAndFeel
	 *
	 *  </pre>
	 *
	 *  There is also an additional Mac look and feel that you can download
	 *  from Sun's web site.
	 *
	 */
	class LookAndFeelListener implements ActionListener {

		public void actionPerformed(ActionEvent event) {

			// get the class name to load
			String className = event.getActionCommand();		

			try {
				// set the look and feel
				UIManager.setLookAndFeel(className);

				// update our component tree w/ new look and feel
				SwingUtilities.updateComponentTreeUI(MainFrame.this);				
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 *  When the "Load" menu option is clicked, we will:
	 *
	 *  <pre>
	 *
	 *  1.  Load the data of matrix, vector and operation result from files.
	 *  2.  If any problems occur during the load then we'll report
	 *      them to the user.
	 *
	 *  </pre>
	 */
	public class LoadActionListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {

			try {
				String line="";
				BufferedReader matrixFromFile = new BufferedReader(new FileReader("matrix.db"));

				while ( (line = matrixFromFile.readLine()) != null )
					operationPanel.matrixTextArea.append(line+"\n");
				matrixFromFile.close();
				
				line="";
				BufferedReader vectorFromFile = new BufferedReader(new FileReader("vector.db"));

				while ( (line = vectorFromFile.readLine()) != null )
					operationPanel.vectorTextArea.append(line+"\n");
				vectorFromFile.close();	
				
				line="";
				BufferedReader resultFromFile = new BufferedReader(new FileReader("result.db"));

				while ( (line = resultFromFile.readLine()) != null )
					resultTextArea.append(line+"\n");
				resultFromFile.close();		
				
				JOptionPane.showMessageDialog(rootPane, "Load from file with success!");
			}
			catch(FileNotFoundException exc) {
				JOptionPane.showMessageDialog(rootPane, "Error: file not found!");
			} catch (IOException e1) {
				JOptionPane.showMessageDialog(rootPane, "Error: reading from file");
			}
			
		}
	}

	/**
	 *  When the "Save" menu option is clicked, we will:
	 *
	 *  <pre>
	 *
	 *  1.  Save the data of matrix, vector and operation result into files.
	 *  2.  If any problems occur during the save then we'll report
	 *      them to the user.
	 *
	 *  </pre>
	 */
	public class SaveActionListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {

			try {				
				FileWriter matrixWriter = new FileWriter("matrix.db", false);
				matrixWriter.write(operationPanel.matrixTextArea.getText());
				matrixWriter.close();

				FileWriter vectorWriter = new FileWriter("vector.db", false);
				vectorWriter.write(operationPanel.vectorTextArea.getText());
				vectorWriter.close();

				FileWriter resultWriter = new FileWriter("result.db", false);
				resultWriter.write(resultTextArea.getText());
				resultWriter.close();
				JOptionPane.showMessageDialog(rootPane, "Saved to file with success!");
			}
			catch (IOException e1) {
				JOptionPane.showMessageDialog(rootPane, "Error occured during saving!");
			}
		}
	}

	/**
	 *  When the "Exit" menu option is clicked, the application will be closed.
	 */
	public class ExitActionListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			setVisible(false);
			dispose();
			System.exit(0);
		}

	}

	/**
	 *  When the "About" menu option is clicked, some useful information will show up in a new dialog window.
	 */
	public class AboutActionListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			JOptionPane.showMessageDialog(rootPane, "Matrix operation ver 1.0\nThanks for using, enjoy!\nAny problem contact the author\nMiao Sun: m.sun@cranfield.ac.uk");
		}

	}

}



























