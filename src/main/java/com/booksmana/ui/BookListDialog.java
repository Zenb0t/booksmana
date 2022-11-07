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

import com.booksmana.data.Book;
import net.miginfocom.swing.MigLayout;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.JList;

@SuppressWarnings("serial")
public class BookListDialog extends JDialog {

	private final JPanel contentPanel = new JPanel();
	
	private final DefaultListModel<Book> listModel;
	private JList<Book> listView;
	
	private static Logger LOG;

	/**
	 * Launch the application.
	 */
	public static void callBookListDialog(List<Book> list) {
		try {
			BookListDialog dialog = new BookListDialog(list);
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			LOG.error(e.getMessage());
		}
	}

	/**
	 * Create the dialog.
	 */
	public BookListDialog(List<Book> list) {
		
		listModel = new DefaultListModel<Book>();
		for (Book b : list) {
			listModel.addElement(b);
		}
		listView = new JList<Book>(listModel);
		listView.setCellRenderer(new BookRenderer());

		
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
						BookListDialog.this.dispose();
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
						BookListDialog.this.dispose();
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
	}
	
	class BookRenderer extends JLabel implements ListCellRenderer<Book>{
		
		protected  BookRenderer() {
			setOpaque(true);
		}

		@Override
		public Component getListCellRendererComponent(JList<? extends Book> list, Book book, int index,
				boolean isSelected, boolean cellHasFocus) {

		        setText(String.format("%s | Authors:  %s", book.getOriginalTitle(), book.getAuthors()));
		        
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
