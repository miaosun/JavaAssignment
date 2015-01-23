import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ToolTipManager;
import javax.swing.border.TitledBorder;


public class OperationPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	/**
	 * The button labeled "LU pivot"
	 */
	protected JButton luButton;

	/**
	 * The button labeled "Inverse"
	 */
	protected JButton inverseButton;

	/**
	 * The button labeled "Clear"
	 */
	protected JButton clearButton;

	/**
	 * The center area for matrix and vector input
	 */
	protected JPanel centerPanel;

	/**
	 * The button panel for holding lu_pivot, inverse and clear buttons. 
	 */
	protected JPanel bottomPanel;

	/**
	 * A reference to the parent frame
	 */
	protected MainFrame parentFrame;

	/**
	 * The textarea for matrix insertion
	 */
	protected JTextArea matrixTextArea;

	/**
	 * The textarea for vector insertion
	 */
	protected JTextArea vectorTextArea;

	public OperationPanel(MainFrame parentFrame) {

		this.parentFrame = parentFrame;
		this.setLayout(new BorderLayout());

		//
		// The bottomPanel holds the bottoms for operation
		//
		luButton = new JButton("LU Pivot");
		luButton.setToolTipText("Get LU pivot result");
		inverseButton = new JButton("Inverse");
		inverseButton.setToolTipText("Get matrix inversion result");
		clearButton = new JButton("Clear");
		clearButton.setToolTipText("Clears all the contents");
		ToolTipManager.sharedInstance().setInitialDelay(5);

		bottomPanel = new JPanel();
		bottomPanel.add(luButton);
		bottomPanel.add(inverseButton);
		bottomPanel.add(clearButton);

		this.add(bottomPanel, BorderLayout.SOUTH);
		
		//
		// Action Listeners for the buttons
		//
		luButton.addActionListener(new LUActionListener());
		inverseButton.addActionListener(new InverseActionListener());
		clearButton.addActionListener(new ClearActionListener());

		//
		// Center panel where allows user insert matrix and vector
		//
		centerPanel = new JPanel();
		centerPanel.setLayout(new FlowLayout());

		JLabel matrixLabel = new JLabel("A = ");
		JLabel vectorLabel = new JLabel("  b = ");
		matrixTextArea = new JTextArea(14,36);
		vectorTextArea = new JTextArea(14,20);
		JScrollPane matrixPane = new JScrollPane(matrixTextArea);
		TitledBorder matrixBorder = BorderFactory.createTitledBorder("Matrix:");
		matrixBorder.setTitleColor(Color.BLACK);
		matrixPane.setBorder(matrixBorder);
		JScrollPane vectorPane = new JScrollPane(vectorTextArea);
		TitledBorder vectorBorder = BorderFactory.createTitledBorder("Vector:");
		vectorBorder.setTitleColor(Color.BLACK);
		vectorPane.setBorder(vectorBorder);

		centerPanel.add(matrixLabel);
		centerPanel.add(matrixPane);
		centerPanel.add(vectorLabel);
		centerPanel.add(vectorPane);

		this.add(centerPanel, BorderLayout.CENTER);
		this.setPreferredSize(this.getPreferredSize());

	}

	public class LUActionListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			String res = "LU Decomposition with scaled partial pivoting\nOriginal matrix (Doolittle factorisation)\n";
			Matrix matrix = getMatrix();
			res += matrix.toString();
			Vector vector = getVector();
			res += "\nOriginal vector\n" + vector.toString();

			//error message for size incompatibility
			if(matrix.ncols != vector.size())
			{
				JOptionPane.showMessageDialog(parentFrame, "ERROR\nmatrix and vector size not match for operations");
			}

			if(matrix.CalcDeterminant() == 0)
				res += "\nSingular matrix";

			else
			{
				Vector x = new Vector(vector.size());
				Matrix p = new Matrix(matrix.nrows, matrix.ncols);
				matrix.reorder(matrix, matrix.nrows, p);

				Matrix pa = new Matrix(matrix.nrows, matrix.ncols);
				pa = new Matrix(p.mult(matrix));

				Matrix lower = new Matrix(matrix.nrows, matrix.ncols);
				Matrix upper = new Matrix(matrix.nrows, matrix.ncols);
				matrix.lu_fact(pa, lower, upper, matrix.nrows);

				res += "\nLower matrix\n" + lower.toString();
				res += "\nUpper matrix\n" + upper.toString();

				x = matrix.lu_solve(lower, upper, p.mult(vector));

				res += "\nSolution\n" + x.toString();
				res += "\nDeterminant = " + matrix.CalcDeterminant() + "\n";
			}

			parentFrame.resultTextArea.setText(res);
		}
	}

	public class InverseActionListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			String res = "Matrix Inversion\n";

			Matrix matrix = getMatrix();
			res += "\nOriginal matrix\n" + matrix.toString();

			if(matrix.nrows != matrix.ncols)
				JOptionPane.showMessageDialog(parentFrame, "ERROR\nmatrix needs to be square for operations");

			//Vector vector = getVector();
			//res += "\nOriginal vector\n" + vector.toString();

			if(matrix.CalcDeterminant() == 0)
				res += "\nSingular matrix";

			else
			{
				//Vector x = new Vector(vector.size());
				Matrix p = new Matrix(matrix.nrows, matrix.ncols);
				matrix.reorder(matrix, matrix.nrows, p);

				Matrix pa = new Matrix(matrix.nrows, matrix.ncols);
				pa = new Matrix(p.mult(matrix));

				Matrix lower = new Matrix(matrix.nrows, matrix.ncols);
				Matrix upper = new Matrix(matrix.nrows, matrix.ncols);
				matrix.lu_fact(pa, lower, upper, matrix.nrows);
				res += "\nLower matrix\n" + lower.toString();
				res += "\nUpper matrix\n" + upper.toString();

				Matrix inverse = new Matrix(matrix.inverse(lower, upper));
				res += "\nInverse matrix\n" + inverse.toString();

				res += "\nDeterminant = " + matrix.CalcDeterminant() + "\n";
				res += "\nPivot array\n"; //TODO: + pivot array
			}
			
			parentFrame.resultTextArea.setText(res);
		}
	}

	/**
	 *  When the "Clear" button is pressed, we will:
	 *
	 *  <pre>
	 *  
	 *  Clear the content in matrix, vector and result text area
	 *  
	 *
	 *  </pre>
	 */
	public class ClearActionListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			matrixTextArea.setText("");
			vectorTextArea.setText("");
			parentFrame.resultTextArea.setText("");
		}

	}

	/**
	 *  When the function is called, the app will:
	 *
	 *  <pre>
	 *
	 *  1.  Read the content in vector text area and store the data in a string - vector_text
	 *  2.  Verify if it is a vector, if it has more than 1 line then it is not a vector, result in a exception
	 *  3.  If it is a vector then split it by any space and extract the user desired input number
	 *  4.  Organize all the extracted numbers in a Vector
	 *  
	 *  @return The intended vector input by user
	 *
	 *  </pre>
	 */
	public Vector getVector() {
		String vector_text = vectorTextArea.getText();
		String[] vector_aux = vector_text.trim().split("\n");
		if(vector_aux.length > 1)
			throw new IllegalArgumentException("vector not in right format");

		int n = vector_aux[0].trim().split("\\s+").length;
		Vector vector = new Vector(n);
		String[] vector_number = vector_aux[0].trim().split("\\s+");
		for(int i=0; i<vector_number.length; i++)
		{
			vector.set(i, Double.parseDouble(vector_number[i]));
		}
		return vector;
	}

	/**
	 *  When the function is called, the app will:
	 *
	 *  <pre>
	 *
	 *  1.  Read the content in matrix text area and store the data in a string - matrix_text
	 *  2.  Split the matrix_text string and store the data line by line in a string array - matrix_aux
	 *  3.  Split each line by any space and extract the user desired input number
	 *  4.  Organize all the extracted numbers in a Matrix
	 *  
	 *  @return The intended matrix input by user
	 *
	 *  </pre>
	 */
	public Matrix getMatrix() {
		String matrix_text = matrixTextArea.getText();	
		String[] matrix_aux = matrix_text.trim().split("\n");
		int ncols = matrix_aux[0].trim().split("\\s+").length;
		if(matrix_aux.length > 1)
		{
			for(int i=1; i<matrix_aux.length; i++)
				if(ncols != matrix_aux[i].trim().split("\\s+").length)
					throw new IllegalArgumentException("matrix not in right format");
		}

		Matrix matrix = new Matrix(matrix_aux.length, ncols);
		for(int i=0; i<matrix_aux.length; i++)
		{
			String[] matrix_number = matrix_aux[i].trim().split("\\s+");
			for(int j=0; j<matrix_number.length; j++)
			{
				matrix.set(i, j, Double.parseDouble(matrix_number[j]));
			}
		}
		return matrix;
	}
}
