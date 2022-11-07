package com.booksmana.ui;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.border.EmptyBorder;

import org.apache.logging.log4j.Logger;

import com.booksmana.book.BookStore;
import com.booksmana.book.Controller;
import com.booksmana.data.Book;
import com.booksmana.data.Customer;
import com.booksmana.data.Purchase;
import com.booksmana.util.ApplicationException;
import com.booksmana.util.Sorter;
import com.booksmana.util.Validator;
import net.miginfocom.swing.MigLayout;
import java.awt.event.KeyEvent;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.LinkedList;
import java.awt.event.InputEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JCheckBoxMenuItem;

/**
 * @author Felipe Mendes Ribeiro
 *
 */
@SuppressWarnings("serial")
public class MainFrame extends JFrame {

	private static Logger LOG;

	private JPanel contentPane;
	private Controller controller;

	private boolean isFiltered;

	protected LinkedList<Purchase> filteredPurchaseList;

	/**
	 * Launch the application.
	 */
	public static void createUI() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainFrame frame = new MainFrame();
					frame.setVisible(true);
				} catch (Exception e) {
					LOG.error(e.getMessage());
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public MainFrame() {
		setTitle("Books 2");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		setSize(500, 350);
		setLocationRelativeTo(null);

		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new MigLayout("", "[]", "[]"));
		controller = Controller.getInstance();

		createMenus();
	}

	public void createMenus() {
		JMenuBar menuBar = new JMenuBar();

		JMenu fileMenu = new JMenu("File");
		fileMenu.setMnemonic('F');

		JMenuItem dropItem = new JMenuItem("Drop");
		dropItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, InputEvent.CTRL_DOWN_MASK));
		dropItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				try {

					int option = JOptionPane.showConfirmDialog(MainFrame.this,
							"Are you sure? This will drop all tables from the database.");
					if (option == JOptionPane.YES_OPTION) {
						controller.dropAll();
					}
				} catch (SQLException e1) {
					LOG.error(e1.getMessage());
				}
				System.exit(0);
			}
		});

		JMenuItem quitItem = new JMenuItem("Quit");
		quitItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.CTRL_DOWN_MASK));
		quitItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});

		JMenu booksMenu = new JMenu("Books");
		booksMenu.setMnemonic('B');

		JMenuItem booksCountItem = new JMenuItem("Count");
		booksCountItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				int count;
				try {
					count = controller.count(BookStore.bookDAO.getBookIds());
					JOptionPane.showMessageDialog(MainFrame.this, String.format("Total count of books: %d ", count));
				} catch (SQLException | ApplicationException e1) {
					LOG.error(e1.getMessage());
				}

			}
		});

		JCheckBoxMenuItem byAuthorChkBox = new JCheckBoxMenuItem("By Author");

		JCheckBoxMenuItem byDescendingOrderBooksChkBox = new JCheckBoxMenuItem("Descending");

		JMenuItem bookListItem = new JMenuItem("List");
		// Do action later
		bookListItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				LinkedList<Book> list = controller.getBookList();

				if (byAuthorChkBox.getState()) {

					if (byDescendingOrderBooksChkBox.getState()) {

						list.sort(new Sorter.byAuthorDesc());
					} else {
						list.sort(new Sorter.byAuthor());

					}
				}

				BookListDialog.callBookListDialog(list);

			}
		});
		;

		JMenu customersMenu = new JMenu("Customers");
		booksMenu.setMnemonic('C');

		JMenuItem customerCountItem = new JMenuItem("Count");
		customerCountItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				int count;
				try {
					count = controller.count(BookStore.customerDao.getIds());
					JOptionPane.showMessageDialog(MainFrame.this,
							String.format("Total count of customers: %d ", count));
				} catch (SQLException | ApplicationException e1) {
					LOG.error(e1.getMessage());
				}

			}
		});

		JCheckBoxMenuItem byJoinDateChkBox = new JCheckBoxMenuItem("By Join Date");

		JMenuItem customerListItem = new JMenuItem("List");
		customerListItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				LinkedList<Customer> list = controller.getCustomersList();

				if (byJoinDateChkBox.getState()) {

					list.sort(new Sorter.ByJoinedDateAscending());
				}

				CustomerListDialog.callCustomerListDialog(list);

			}
		});
		;

		JMenu purchasesMenu = new JMenu("Purchases");
		booksMenu.setMnemonic('P');

		JMenuItem totalItem = new JMenuItem("Total");
		totalItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				double total;
				if (!isFiltered) {
					total = controller.getTotal(controller.getPurchaseList());

				} else {
					total = controller.getTotal(filteredPurchaseList);
				}
				JOptionPane.showMessageDialog(MainFrame.this, String.format("Total purchases is: $%.2f ", total));

			}
		});

		JCheckBoxMenuItem byLastNameChkBox = new JCheckBoxMenuItem("By Last Name");

		JCheckBoxMenuItem byTitleChkBox = new JCheckBoxMenuItem("by Title");

		JCheckBoxMenuItem byDescendingOrderPurchasesChkBox = new JCheckBoxMenuItem("Descending");

		JMenuItem filterCustomerItem = new JMenuItem("Filter by Customer Id");
		filterCustomerItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String input = JOptionPane.showInputDialog(MainFrame.this, "Input a valid Customer ID to filter: ");

				if (input == null || input.isEmpty()) {
					isFiltered = false;

				} else if (Validator.isNumeric(input)) {
					filteredPurchaseList = new LinkedList<Purchase>();

					for (Purchase p : controller.getPurchaseList()) {
						if (p.getCustomerId() == Long.parseLong(input)) {
							filteredPurchaseList.add(p);
						}

					}
					if (filteredPurchaseList != null) {
						isFiltered = true;
					}
				} else if (!Validator.isNumeric(input)) {
					JOptionPane.showMessageDialog(MainFrame.this, "Input invalid.", "Warning ",
							JOptionPane.WARNING_MESSAGE);
					isFiltered = false;
				}

			}
		});

		JMenuItem listPurchasesItem = new JMenuItem("List");
		listPurchasesItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				LinkedList<Purchase> list = controller.getPurchaseList();
				if (isFiltered) {
					list = filteredPurchaseList;
				}

				if (byTitleChkBox.getState()) {

					list.sort(new Comparator<Purchase>() {
						@Override
						public int compare(Purchase p1, Purchase p2) {

							Book b1 = controller.getBookList().get(controller.indexList().get(p1.getId())[1]);
							Book b2 = controller.getBookList().get(controller.indexList().get(p2.getId())[1]);

							if (byDescendingOrderPurchasesChkBox.getState()) {
								return b2.getOriginalTitle().compareTo(b1.getOriginalTitle());
							}

							return b1.getOriginalTitle().compareTo(b2.getOriginalTitle());
						}
					});

				} else if (byLastNameChkBox.getState()) {

					list.sort(new Comparator<Purchase>() {
						@Override
						public int compare(Purchase p1, Purchase p2) {

							Customer c1 = controller.getCustomersList().get(controller.indexList().get(p1.getId())[0]);
							Customer c2 = controller.getCustomersList().get(controller.indexList().get(p2.getId())[0]);

							String name1 = String.format("%s %s", c1.getLastName(), c1.getFirstName());
							String name2 = String.format("%s %s", c2.getLastName(), c2.getFirstName());

							if (byDescendingOrderPurchasesChkBox.getState()) {
								return name2.compareTo(name1);
							}
							return name1.compareTo(name2);
						}

					});

				}

				PurchaseListDialog.callPurchaseListDialog(list);

			}
		});
		;

		JMenu helpMenu = new JMenu("Help");

		JMenuItem aboutItem = new JMenuItem("About");
		aboutItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0));

		aboutItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(MainFrame.this, "Books 2 \n By Felipe Mendes Ribeiro \n A01169959");
			}
		});

		menuBar.add(fileMenu);

		fileMenu.add(dropItem);
		fileMenu.add(quitItem);

		menuBar.add(booksMenu);
		booksMenu.add(booksCountItem);
		booksMenu.add(byAuthorChkBox);
		booksMenu.add(byDescendingOrderBooksChkBox);
		booksMenu.add(bookListItem);

		menuBar.add(customersMenu);
		customersMenu.add(customerCountItem);
		customersMenu.add(byJoinDateChkBox);
		customersMenu.add(customerListItem);

		menuBar.add(purchasesMenu);
		purchasesMenu.add(totalItem);
		purchasesMenu.add(byTitleChkBox);
		purchasesMenu.add(byLastNameChkBox);
		purchasesMenu.add(byDescendingOrderPurchasesChkBox);
		purchasesMenu.add(filterCustomerItem);
		purchasesMenu.add(listPurchasesItem);

		menuBar.add(helpMenu);
		helpMenu.add(aboutItem);
		setJMenuBar(menuBar);
	}

}
