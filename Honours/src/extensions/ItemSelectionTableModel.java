package extensions;

import java.awt.Color;
import java.util.ArrayList;

import javax.swing.table.DefaultTableModel;

import extensions.Hierarchy.ButtonRenderer;

public class ItemSelectionTableModel extends DefaultTableModel {

	private static final long serialVersionUID = 1L;
	ArrayList<Color> rowColors = new ArrayList<Color>();

	public ItemSelectionTableModel(Object[][] data, String[] columns) {
		super(data, columns);
	}

	public ItemSelectionTableModel() {
		super();
		addColumn("Selection");
		addColumn("Item");
		addColumn("Remove");
	}

	public void addRow(Object[] rowData, Color bc) {
		this.addRow(rowData);
		rowColors.add(bc);
		this.setRowColour(this.getRowCount() - 1, bc);
	}

	public void setRowColour(int row, Color c) {
		rowColors.set(row, c);
		fireTableRowsUpdated(row, row);
	}

	public Color getRowColour(int row) {
		return rowColors.get(row);
	}

	public boolean isCellEditable(int row, int column) {
		if (column == 0)
			return true;
		if (column == 2)
			return true;
		return false;
	}

	public Class<?> getColumnClass(int columnIndex) {
		if (columnIndex == 0)
			return Boolean.class;
		if (columnIndex == 2)
			return ButtonRenderer.class;
		else
			return Object.class;
		// return getValueAt(0, columnIndex).getClass();
	}
}
