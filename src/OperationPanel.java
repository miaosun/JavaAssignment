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

/*
 * The Operation Panel of the Application
 * Consists of 3 parts: 
 * 1. left upper side is a text area for user to input Matrix
 * 2. right upper side is a text area for user to input Vector
 * 3. lower part has 3 buttons: one for computing LU Pivot, one for Matrix inversion, last one for clearing the text areas. 
 */
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

	/*
	 * OperationPanel constructor
	 * @param parentFrame  the parent frame: main frame of the application
	 */
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

	/**
	 *  When the LU Pivot button is clicked, the app will:
	 *
	 *  <pre>
	 *
	 *  1.  Get the user input Matrix and Vector from Matrix and Vector text area
	 *  2.  Verify if the sizes of Matrix and Vector match for operation, if it is not, result in a exception
	 *  3.  Calculate its determinant, if it is <code>0</code>, it is a singular Matrix, ends the computation and show the result in result panel
	 *  4.  Compute the reordered Matrix
	 *  5.  Compute the lower and upper Matrix
	 *  6.  Solve the differential system
	 *  7.  Display all the information in the result panel @see {@link MainFrame#resultTextArea}
	 *
	 *  @see OperationPanel#getMatrix()
	 *  @see OperationPanel#getVector()
	 *  @see Matrix#toString()
	 *  @see Matrix#CalcDeterminant()
	 *  @see Matrix#Matrix(int, int)
	 *  @see Matrix#Matrix(Matrix)
	 *  @see Matrix#reorder(Matrix, int, Matrix)
	 *  @see Matrix#mult(Matrix)
	 *  @see Matrix#inverse(Matrix, Matrix)
	 *  @see Matrix#lu_fact(Matrix, Matrix, Matrix, int)
	 *  @see Matrix#lu_solve(Matrix, Matrix, Vector)
	 *  @see Vector#Vector(int)
	 *  @see Vector#size()
	 *  @see Vector#toString()
	 *  
     *  @exception IllegalArgumentException
     *              if the sizes user input Matrix and Vector are not match for operations 
     *              
	 *  </pre>
	 */
	public class LUActionListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			String res = "LU Decomposition with scaled partial pivoting\n\nOriginal matrix (Doolittle factorisation)\n";
			Matrix matrix = getMatrix();
			res += matrix.toString();
			Vector vector = getVector();
			res += "\nOriginal vector\n" + vector.toString();

			//error message for size incompatibility
			if(matrix.ncols != vector.size())
			{
				JOptionPane.showMessageDialog(parentFrame, "ERROR\nmatrix and vector size not match for operations");
				throw new IllegalArgumentException("ERROR\nmatrix and vector size not match for operations");
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

	/**
	 *  When the inverse button is clicked, the app will:
	 *
	 *  <pre>
	 *
	 *  1.  Get the user input Matrix from Matrix text area
	 *  2.  Verify if it is a square matrix, if it is not, result in a exception
	 *  3.  Calculate its determinant, if it is <code>0</code>, it is a singular Matrix, ends the computation and show the result in result panel
	 *  4.  Compute the lower and upper Matrix
	 *  5.  Compute the inversed Matrix
	 *  6.  Compute the pivot array
	 *  7.  Display all the information in the result panel  @see {@link MainFrame#resultTextArea}
	 *
	 *  @see OperationPanel#getMatrix()
	 *  @see Matrix#CalcDeterminant()
	 *  @see Matrix#toString()
	 *  @see Matrix#Matrix(int, int)
	 *  @see Matrix#Matrix(Matrix)
	 *  @see Matrix#reorder(Matrix, int, Matrix)
	 *  @see Matrix#mult(Matrix)
	 *  @see Matrix#inverse(Matrix, Matrix)
	 *  @see Matrix#lu_fact(Matrix, Matrix, Matrix, int)
     *  @exception IllegalArgumentException
     *              if user input Matrix is not a square Matrix
     *              
	 *  </pre>
	 */
	public class InverseActionListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			String res = "Matrix Inversion\n";

			Matrix matrix = getMatrix();
			res += "\nOriginal matrix\n" + matrix.toString();

			if(matrix.nrows != matrix.ncols)
			{
				JOptionPane.showMessageDialog(parentFrame, "ERROR\nmatrix needs to be square for operations");
				throw new IllegalArgumentException("ERROR\nmatrix needs to be square for operations");
			}

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
				
				res += "\nPivot array: "; 
				
				for(int i=0; i<p.ncols; i++)
				{
					for(int j=0; j<p.nrows; j++)
					{
						if(p.get(i, j) == 1)
							res += j+1 + " ";
					}
				}
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
	 *  @see Vector#Vector(int)
	 *  @see Vector#set(int, double)
     *  @exception IllegalArgumentException
     *              if user input Vector is not in right format
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
	 *  @see Matrix#Matrix(int, int)
	 *  @see Matrix#set(int, int, double)
     *  @exception IllegalArgumentException
     *              if user input Matrix is not in right format
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
