package com.booksmana.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.apache.logging.log4j.Logger;

import com.booksmana.data.Customer;
import net.miginfocom.swing.MigLayout;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.JList;

@SuppressWarnings("serial")
public class CustomerListDialog extends JDialog {

	private final JPanel contentPanel = new JPanel();
	
	private final DefaultListModel<Customer> listModel;
	private JList<Customer> listView;
	
	private static Logger LOG;

	/**
	 * Launch the application.
	 */
	public static void callCustomerListDialog(List<Customer> list) {
		try {
			CustomerListDialog dialog = new CustomerListDialog(list);
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			LOG.error(e.getMessage());
		}
	}

	/**
	 * Create the dialog.
	 */
	public CustomerListDialog(List<Customer> list) {
		

		
		listModel = new DefaultListModel<Customer>();
		for (Customer c : list) {
			listModel.addElement(c);
		}
		listView = new JList<Customer>(listModel);
		listView.setCellRenderer(new BookRenderer());

		MouseListener mouseListener = new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() > 0) {
					CustomerDialog.callCustomerDialog(listView.getSelectedValue());
				}
			}
		};
		
		listView.addMouseListener(mouseListener);
		
		
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
						CustomerListDialog.this.dispose();
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
						CustomerListDialog.this.dispose();
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
	}
	
	class BookRenderer extends JLabel implements ListCellRenderer<Customer>{
		
		protected  BookRenderer() {
			setOpaque(true);
		}

		@Override
		public Component getListCellRendererComponent(JList<? extends Customer> list, Customer customer, int index,
				boolean isSelected, boolean cellHasFocus) {

		        setText(String.format("ID| %s | %s %s", customer.getId(), customer.getFirstName(), customer.getLastName()));
		        
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
