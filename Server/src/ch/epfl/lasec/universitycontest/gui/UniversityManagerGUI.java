package ch.epfl.lasec.universitycontest.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import ch.epfl.lasec.universitycontest.NotConnectDBException;
import ch.epfl.lasec.universitycontest.ServerManager;

public class UniversityManagerGUI extends JPanel{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Table of the universities with their score
	 */
	private JTable uniTable;
	
	/**
	 * Button to add a university. Show a dialog window
	 */
	private JButton addUniButton = new JButton("New University");
	
	/**
	 * Server manager interface
	 */
	private ServerManager sm = new ServerManager();
	
	
	public UniversityManagerGUI() {
		this.setLayout(new BorderLayout());
		setUniTable();
		setAddButton();
		
		setMargin();
	}
	
	private void setMargin() {
		JPanel [] p = new JPanel [3];
		
		for (int i = 0; i < 3 ; ++i) {
			p[i] = new JPanel();
			p[i].add(Box.createRigidArea(new Dimension(50,50)));
		}
		
		this.add(p[0],BorderLayout.NORTH);
		this.add(p[1],BorderLayout.EAST);
		this.add(p[2],BorderLayout.WEST);
		
	}

	private void setUniTable(){
		String [] title = {"University","Score"};
		Object[][] data = null;
		try {
			data = sm.getUniScoreTable();
		} catch (Exception e) {
			//TODO: show error msg
		}
		
		uniTable = new JTable(new UniTableModel(data,title));
		uniTable.setRowHeight(30);
		
		JPanel center = new JPanel();
		center.setLayout(new BoxLayout(center, BoxLayout.PAGE_AXIS));
		
		center.add(new JScrollPane(uniTable),BorderLayout.CENTER);
		center.add(Box.createRigidArea(new Dimension(50,50)));
		
		this.add(center);

	}
	
	private void setAddButton() {
		addUniButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				String uniName = JOptionPane.showInputDialog(null,
						"Name of the university", "Add university",
						JOptionPane.QUESTION_MESSAGE);
				
				try {
					sm.addUni(uniName);
					
					Object[] addData = { uniName, 0 };

					((UniTableModel) uniTable.getModel()).addRow(addData);
					
				} catch (NotConnectDBException e1) {
					
					JOptionPane.showMessageDialog(null,"Error: you can not connect to the DB",
							"DataBase Error", JOptionPane.ERROR_MESSAGE);
					
				}

				
				

			}
		});
		this.add(addUniButton,BorderLayout.SOUTH);
	}
	
	class UniTableModel extends AbstractTableModel {
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
		private Object[][] data;
		private String[] title;
		
		public UniTableModel(Object[][] data, String[] title){
			this.data = data;
			this.title = title;
		}
		
		public String getColumnName(int col) {
			  return this.title[col];
		}

		@Override
		public int getRowCount() {
			return this.data.length;
		}

		@Override
		public int getColumnCount() {
			return this.title.length;
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			return this.data[rowIndex][columnIndex];
		}
		
		@SuppressWarnings({ "unchecked", "rawtypes" })
		public Class getColumnClass(int columnIndex){
			return this.data[0][columnIndex].getClass();
		}
		
		public void removeRow(int position) {

			int index = 0, index2 = 0, nbRow = this.getRowCount() - 1, 
				nbCol = this.getColumnCount();
			Object temp[][] = new Object[nbRow][nbCol];

			for (Object[] value : this.data) {
				if (index != position) {
					temp[index2++] = value;
				}
				index++;
			}
			this.data = temp;
			temp = null;

			this.fireTableDataChanged();
		}
		
		public void addRow(Object[] data){
			int indice = 0, nbRow = this.getRowCount(), nbCol = this.getColumnCount();
			
			Object temp[][] = this.data;
			this.data = new Object[nbRow+1][nbCol];
			
			for(Object[] value : temp)
				this.data[indice++] = value;
			
				
			this.data[indice] = data;
			temp = null;
			
			this.fireTableDataChanged();
		}

		
	}
	
}
