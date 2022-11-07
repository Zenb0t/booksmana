package com.booksmana.ui;

import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.apache.logging.log4j.Logger;

import com.booksmana.book.BookStore;
import com.booksmana.book.Controller;
import com.booksmana.data.Customer;
import com.booksmana.util.ApplicationException;
import net.miginfocom.swing.MigLayout;
import javax.swing.JLabel;
import javax.swing.JTextField;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.time.LocalDate;
import java.awt.event.ActionEvent;

/**
 * @author Felipe Mendes Ribeiro
 *
 */
@SuppressWarnings("serial")
public class CustomerDialog extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private JTextField idField;
	private JTextField fNameField;
	private JTextField streetField;
	private JTextField cityField;
	private JTextField lNameField;
	private JTextField postalCodeField;
	private JTextField phoneField;
	private JTextField emailField;
	private JTextField joinDateField;

	private static Logger LOG;

	/**
	 * Launch the application.
	 */
	public static void callCustomerDialog(Customer c) {
		try {
			CustomerDialog dialog = new CustomerDialog(c);
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			LOG.error(e.getMessage());
		}
	}

	/**
	 * Create the dialog.
	 */
	public CustomerDialog(Customer cus) {
		setBounds(100, 100, 450, 330);
		getContentPane().setLayout(new MigLayout("", "[438px]", "[230px][35px]"));
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, "cell 0 0,grow");
		contentPanel.setLayout(new MigLayout("", "[78.00][grow]", "[][][][][][][][][]"));
		{
			JLabel labelId = new JLabel("ID");
			contentPanel.add(labelId, "cell 0 0,alignx trailing");
		}
		{
			idField = new JTextField();
			idField.setEditable(false);
			contentPanel.add(idField, "cell 1 0,growx");
			idField.setColumns(10);
			idField.setText(String.valueOf(cus.getId()));
		}
		{
			JLabel lblFirstName = new JLabel("First Name");
			contentPanel.add(lblFirstName, "cell 0 1,alignx trailing");
		}
		{
			fNameField = new JTextField();
			contentPanel.add(fNameField, "cell 1 1,growx");
			fNameField.setColumns(10);
			fNameField.setText(cus.getFirstName());
		}
		{
			JLabel lblLastName = new JLabel("Last Name");
			contentPanel.add(lblLastName, "cell 0 2,alignx trailing");
		}
		{
			lNameField = new JTextField();
			contentPanel.add(lNameField, "cell 1 2,growx");
			lNameField.setColumns(10);
			lNameField.setText(cus.getLastName());
		}
		{
			JLabel lblStreet = new JLabel("Street");
			contentPanel.add(lblStreet, "cell 0 3,alignx trailing");
		}
		{
			streetField = new JTextField();
			contentPanel.add(streetField, "cell 1 3,growx");
			streetField.setColumns(10);
			streetField.setText(cus.getStreet());
		}
		{
			JLabel lblCity = new JLabel("City");
			contentPanel.add(lblCity, "cell 0 4,alignx trailing");
		}
		{
			cityField = new JTextField();
			contentPanel.add(cityField, "cell 1 4,growx");
			cityField.setColumns(10);
			cityField.setText(cus.getCity());
		}
		{
			JLabel lblPostalCode = new JLabel("Postal Code");
			contentPanel.add(lblPostalCode, "cell 0 5,alignx right");
		}
		{
			postalCodeField = new JTextField();
			contentPanel.add(postalCodeField, "cell 1 5,growx");
			postalCodeField.setColumns(10);
			postalCodeField.setText(cus.getPostalCode());
		}
		{
			JLabel lblPhone = new JLabel("Phone");
			contentPanel.add(lblPhone, "cell 0 6,alignx trailing");
		}
		{
			phoneField = new JTextField();
			contentPanel.add(phoneField, "cell 1 6,growx");
			phoneField.setColumns(10);
			phoneField.setText(cus.getPhone());
		}
		{
			JLabel lblEmail = new JLabel("Email");
			contentPanel.add(lblEmail, "cell 0 7,alignx trailing");
		}
		{
			emailField = new JTextField();
			emailField.setText("");
			contentPanel.add(emailField, "cell 1 7,growx,aligny baseline");
			emailField.setColumns(10);
			emailField.setText(cus.getEmailAddress());
		}
		{
			JLabel lblJoinDate = new JLabel("Join Date");
			contentPanel.add(lblJoinDate, "cell 0 8,alignx trailing");
		}
		{
			joinDateField = new JTextField();
			contentPanel.add(joinDateField, "cell 1 8,growx");
			joinDateField.setColumns(10);
			joinDateField.setText(cus.getJoinedDate().toString());
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, "cell 0 1,growx,aligny top");
			{
				JButton okButton = new JButton("OK");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						Customer c = new Customer.Builder(Long.parseLong(idField.getText()), phoneField.getText())
								.setFirstName(fNameField.getText()).setLastName(lNameField.getText())
								.setStreet(streetField.getText()).setCity(cityField.getText())
								.setPostalCode(postalCodeField.getText()).setEmailAddress(emailField.getText())
								.setJoinedDate(LocalDate.parse(joinDateField.getText())).build();
						try {
							if (!sameCustomer(c, cus)) {
								BookStore.customerDao.update(c);
								Controller controller = Controller.getInstance();
								controller.reloadLists();
							}
						} catch (SQLException | ApplicationException e1) {
							LOG.error(e1.getMessage());
						}

						CustomerDialog.this.dispose();
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
						CustomerDialog.this.dispose();
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
	}

	private boolean sameCustomer(Customer c1, Customer c2) {
		if (c1.getFirstName().equals(c2.getFirstName()) && c1.getLastName().equals(c2.getLastName())
				&& c1.getCity().equals(c2.getCity()) && c1.getStreet().equals(c2.getStreet())
				&& c1.getId() == c2.getId() && c1.getPhone().equals(c2.getPhone())
				&& c1.getPostalCode().equals(c2.getPostalCode()) && c1.getJoinedDate().equals(c2.getJoinedDate())
				&& c1.getEmailAddress().equals(c2.getEmailAddress())

		) {
			return true;
		} else {
			return false;
		}
	}

}
