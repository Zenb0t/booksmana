package com.booksmana.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.apache.logging.log4j.Logger;

import com.booksmana.book.Controller;
import com.booksmana.data.Book;
import com.booksmana.data.Customer;
import com.booksmana.data.Purchase;
import net.miginfocom.swing.MigLayout;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.JList;

@SuppressWarnings("serial")
public class PurchaseListDialog extends JDialog {

	private final JPanel contentPanel = new JPanel();

	private final DefaultListModel<Purchase> listModel;
	private JList<Purchase> listView;

	private static Logger LOG;
	private Controller controller = Controller.getInstance();

	/**
	 * Launch the application.
	 */
	public static void callPurchaseListDialog(List<Purchase> list) {
		try {
			PurchaseListDialog dialog = new PurchaseListDialog(list);
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			LOG.error(e.getMessage());
		}
	}

	/**
	 * Create the dialog.
	 */
	public PurchaseListDialog(List<Purchase> list) {

		listModel = new DefaultListModel<Purchase>();
		for (Purchase p : list) {
			listModel.addElement(p);
		}
		listView = new JList<Purchase>(listModel);
		listView.setCellRenderer(new PurchaseRenderer());

		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new MigLayout("", "[grow]", "[grow]"));
		{
			JScrollPane scrollPane = new JScrollPane();
			contentPanel.add(scrollPane, "cell 0 0,grow");
			{
				scrollPane.setViewportView(listView);
			}
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						PurchaseListDialog.this.dispose();
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						PurchaseListDialog.this.dispose();
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
	}

	class PurchaseRenderer extends JLabel implements ListCellRenderer<Purchase> {

		protected PurchaseRenderer() {
			setOpaque(true);
		}

		@Override
		public Component getListCellRendererComponent(JList<? extends Purchase> list, Purchase purchase, int index,
				boolean isSelected, boolean cellHasFocus) {
			int[] indexArray = controller.indexList().get(purchase.getId());
			Customer c = controller.getCustomersList().get(indexArray[0]);
			Book b = controller.getBookList().get(indexArray[1]);
			setText(String.format("%s %s | %s | $%.2f ", c.getFirstName() , c.getLastName(), b.getOriginalTitle(), purchase.getPrice()));

			if (isSelected) {
				setBackground(list.getSelectionBackground());
				setForeground(list.getSelectionForeground());
			} else {
				setBackground(list.getBackground());
				setForeground(list.getForeground());
			}

			return this;
		}

	}

}
