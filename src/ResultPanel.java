import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.TitledBorder;


public class ResultPanel extends JPanel {

	public ResultPanel() {
		JTextArea resultArea = new JTextArea(40,30);
		resultArea.setEditable(false);
		JScrollPane resultPane = new JScrollPane(resultArea);

		TitledBorder resultBorder = BorderFactory.createTitledBorder("Result:");
		resultBorder.setTitleColor(Color.BLACK);
		resultPane.setBorder(resultBorder);
	}
}
