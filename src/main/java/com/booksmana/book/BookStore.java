package com.booksmana.book;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.time.Duration;
import java.time.Instant;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;

import org.apache.commons.cli.ParseException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.DefaultConfiguration;

import com.booksmana.data.Book;
import com.booksmana.data.BookDAO;
import com.booksmana.data.Customer;
import com.booksmana.data.CustomerDAO;
import com.booksmana.data.Purchase;
import com.booksmana.data.PurchaseDAO;
import com.booksmana.io.BookReader;
import com.booksmana.io.BookReport;
import com.booksmana.io.PurchasesReader;
import com.booksmana.ui.MainFrame;
import com.booksmana.io.CustomerReader;
import com.booksmana.io.CustomerReport;
import com.booksmana.db.Database;
import com.booksmana.db.DbConstants;
import com.booksmana.io.PurchaseReport;
import com.booksmana.util.ApplicationException;
import com.booksmana.util.Sorter;

/**
 * @author Felipe Mendes Ribeiro
 *
 */
public class BookStore    {

	private LinkedList<Customer> customersList;
	private LinkedList<Book> bookList;
	private LinkedList<Purchase> purchaseList;
	private HashMap<Long, int[]> indexList;

	private static final String LOG4J_CONFIG_FILENAME = "log4j2.xml";
	private static Logger LOG;
	private static final String PATH_CUSTOMER_FILE = "customers.dat";
	private static final String PATH_BOOK_FILE = "books500.csv";
	private static final String PATH_PURCHASES_FILE = "purchases.csv";

	private static final String CUSTOMERS_OUTPUT_FILENAME = "customerReport.txt";
	private static final String BOOK_OUTPUT_FILENAME = "bookReport.txt";
	private static final String PURCHASES_OUTPUT_FILENAME = "purchasesReport.txt";

	private static Database database;

	public static CustomerDAO customerDao;
	public static BookDAO bookDAO;
	public static PurchaseDAO purchaseDAO;

	static {
		configureLogging();

		LOG = LogManager.getLogger();
	}

	public BookStore() throws ApplicationException, ParseException {
		LOG.info("Created Bcmc");

		LOG.debug("Creating and initializing database and Daos objects");
		try {
			database = new Database();

			customerDao = new CustomerDAO(database);
			bookDAO = new BookDAO(database);
			purchaseDAO = new PurchaseDAO(database);
		} catch (IOException e) {
			throw new ApplicationException(e.getMessage());
		}

	}

	public static void main(String[] args) {
		Instant startTime = Instant.now();
		LOG.info(startTime);

		// start the bookStore System
		try {
			BookStore bookStore = new BookStore();
			bookStore.run();
			MainFrame.createUI();
			

		} catch (Exception e) {
			LOG.error(e.getMessage(), e);

		}

		Instant endTime = Instant.now();
		LOG.info(endTime);
		LOG.info(String.format("Duration: %d ms", Duration.between(startTime, endTime).toMillis()));
		LOG.info("BookS has stopped");

	}

	@SuppressWarnings("static-access")
	public void run() {
		

		LOG.debug("run()");
		// Check if file exists, if not read and create db tables. Then load from db.
		try {
			if (!database.tableExists(DbConstants.CUSTOMER_TABLE_NAME)) {
				LOG.info("Creating customer table");
				customerDao.create();
				customersList = CustomerReader.read(PATH_CUSTOMER_FILE);
				for (Customer c : customersList) {
					LOG.info(c.toString());
					customerDao.add(c);
				}
			
			}
			if (!database.tableExists(DbConstants.BOOK_TABLE_NAME)) {
				LOG.info("Creating book table");
				bookDAO.create();
				bookList = BookReader.read(PATH_BOOK_FILE);
				for (Book b : bookList) {
					LOG.info(b.toString());
					bookDAO.add(b);
				}
			}
			if (!database.tableExists(DbConstants.PURCHASE_TABLE_NAME)) {
				LOG.info("Creating purchase table");
				purchaseDAO.create();
				purchaseList = PurchasesReader.read(PATH_PURCHASES_FILE);
				for (Purchase p : purchaseList) {
					LOG.info(p.toString());
					purchaseDAO.add(p);
				}
			}
			
			customersList = CustomerReader.load(customerDao);
			bookList = BookReader.load(bookDAO);
			purchaseList = PurchasesReader.load(purchaseDAO);
			LOG.info("Loading finished");
			Controller controller = Controller.getInstance();
			controller.initializeController(customersList, bookList, purchaseList);
			LOG.info("Controller initialized");
			
			
			
		} catch (SQLException e) {
			LOG.error(String.format("SQLs Exception %s", e.getMessage()));
		} catch (ApplicationException e) {
			LOG.error(String.format("Application Exception %s", e.getMessage()));
		}

		// create method update indexList if needed, for each time DB is updated?
		// reflect the changes
		//createIndexList();
		// generateReports(); // no need to generate reports

	}

	

	// Create method loadFiles to load from the DB

	@SuppressWarnings("unused")
	private void loadFiles() {
		// TODO
	}

	@SuppressWarnings("unused")
	private void readFiles() throws ApplicationException {
		LOG.info("Reading data files");

		LOG.debug("Reading " + PATH_CUSTOMER_FILE);

//		try {
		customersList = CustomerReader.read(PATH_CUSTOMER_FILE);
//		} catch (ApplicationException e) {
//			LOG.error(String.format("Error processing %s file in CustomerReader, msg: %s", PATH_CUSTOMER_FILE,
//					e.getMessage()));
//		}

		LOG.debug("Reading " + PATH_BOOK_FILE);

		try {
			bookList = BookReader.read(PATH_BOOK_FILE);
		} catch (ApplicationException e) {
			LOG.error(String.format("Error processing %s file in BookReader, msg: %s", PATH_BOOK_FILE, e.getMessage()));
		}

		LOG.debug("Reading " + PATH_PURCHASES_FILE);

		try {
			purchaseList = PurchasesReader.read(PATH_PURCHASES_FILE);

		} catch (ApplicationException e) {
			LOG.error(String.format("Error processing %s file in PurchasesReader, msg: %s", PATH_PURCHASES_FILE,
					e.getMessage()));
		}

	}

	@SuppressWarnings("unused")
	private void generateReports() throws FileNotFoundException {
		LOG.info("generating the reports");

		// Customer Option Logic

		if (BookOptions.isCustomersOptionSet()) {
			LOG.debug("generating the customer report");
			LinkedList<Customer> sortedCustomers = customersList;

			if (BookOptions.isByJoinDateOptionSet() && BookOptions.isDescendingOptionSet()) {
				sortedCustomers.sort(new Sorter.ByJoinedDateDescending());
				CustomerReport.write(sortedCustomers, CUSTOMERS_OUTPUT_FILENAME);
			} else if (BookOptions.isByJoinDateOptionSet()) {
				sortedCustomers.sort(new Sorter.ByJoinedDateAscending());
				CustomerReport.write(sortedCustomers, CUSTOMERS_OUTPUT_FILENAME);

			}
			CustomerReport.write(sortedCustomers, CUSTOMERS_OUTPUT_FILENAME);

			LOG.debug("Customer Report: " + BookOptions.isCustomersOptionSet());
			LOG.debug("Customer Join Date: " + BookOptions.isByJoinDateOptionSet());
			LOG.debug("Customer Join Date DESC: " + BookOptions.isDescendingOptionSet());

		}

		// Book Option Logic

		if (BookOptions.isBooksOptionSet()) {
			LOG.debug("generating the book report");
			LinkedList<Book> sortedBooks = null;

			if (BookOptions.isByAuthorOptionSet() && BookOptions.isDescendingOptionSet()) {
				sortedBooks = bookList;
				sortedBooks.sort(new Sorter.byAuthorDesc());
				BookReport.write(sortedBooks, BOOK_OUTPUT_FILENAME);
			} else if (BookOptions.isByAuthorOptionSet()) {
				sortedBooks = bookList;
				sortedBooks.sort(new Sorter.byAuthor());
				BookReport.write(sortedBooks, BOOK_OUTPUT_FILENAME);

			} else {
				BookReport.write(bookList, BOOK_OUTPUT_FILENAME);
			}

			LOG.debug("Book Report: " + BookOptions.isBooksOptionSet());
			LOG.debug("Book by author: " + BookOptions.isDescendingOptionSet());
			LOG.debug("DESC: " + BookOptions.isDescendingOptionSet());

		}

		// Purchase Option Logic

		if (BookOptions.isPurchasesOptionSet()) {

			LOG.debug("generating the purchases report");
			LOG.debug(String.format("Total is set to: %b", BookOptions.isTotalOptionSet()));
			LinkedList<Purchase> sortedPurchases = purchaseList;

			// If a customer ID is provided, filter the list

			if (BookOptions.getCustomerId() != null && !BookOptions.getCustomerId().isEmpty()) {
				LinkedList<Purchase> filteredList = new LinkedList<Purchase>();
				long cusId = Long.parseLong(BookOptions.getCustomerId());
				for (Purchase p : purchaseList) {
					if (p.getCustomerId() == cusId) {
						filteredList.add(p);
					}
				}
				sortedPurchases = filteredList;

			}

			if (BookOptions.isByLastnameOptionSet() && BookOptions.isDescendingOptionSet()) {
				sortedPurchases.sort(new Comparator<Purchase>() {
					@Override
					public int compare(Purchase p1, Purchase p2) {

						Customer c1 = customersList.get(indexList.get(p1.getId())[0]);
						Customer c2 = customersList.get(indexList.get(p2.getId())[0]);

						String name1 = String.format("%s %s", c1.getLastName(), c1.getFirstName());
						String name2 = String.format("%s %s", c2.getLastName(), c2.getFirstName());

						return name2.compareTo(name1);
					}
				});

			} else if (BookOptions.isByLastnameOptionSet()) {
				sortedPurchases.sort(new Comparator<Purchase>() {
					@Override
					public int compare(Purchase p1, Purchase p2) {

						Customer c1 = customersList.get(indexList.get(p1.getId())[0]);
						Customer c2 = customersList.get(indexList.get(p2.getId())[0]);

						String name1 = String.format("%s %s", c1.getLastName(), c1.getFirstName());
						String name2 = String.format("%s %s", c2.getLastName(), c2.getFirstName());

						return name1.compareTo(name2);
					}
				});

			} else if (BookOptions.isByTitleOptionSet() && BookOptions.isDescendingOptionSet()) {
				sortedPurchases.sort(new Comparator<Purchase>() {
					@Override
					public int compare(Purchase p1, Purchase p2) {

						Book b1 = bookList.get(indexList.get(p1.getId())[1]);
						Book b2 = bookList.get(indexList.get(p2.getId())[1]);
						return b2.getOriginalTitle().compareTo(b1.getOriginalTitle());
					}
				});

			} else if (BookOptions.isByTitleOptionSet()) {
				sortedPurchases.sort(new Comparator<Purchase>() {
					@Override
					public int compare(Purchase p1, Purchase p2) {

						Book b1 = bookList.get(indexList.get(p1.getId())[1]);
						Book b2 = bookList.get(indexList.get(p2.getId())[1]);
						return b1.getOriginalTitle().compareTo(b2.getOriginalTitle());
					}
				});

			}

			// After all the logic, pick the filtered and sorted list and print the report

			PurchaseReport.write(sortedPurchases, PURCHASES_OUTPUT_FILENAME, bookList, customersList, indexList);
			LOG.debug("Purchases Report: " + BookOptions.isPurchasesOptionSet());
			LOG.debug("Purchases by Book Title: " + BookOptions.isByTitleOptionSet());
			LOG.debug("Purchases By lastName: " + BookOptions.isByLastnameOptionSet());
			LOG.debug(" DESC: " + BookOptions.isDescendingOptionSet());
			LOG.debug("Total of purchases: " + BookOptions.isTotalOptionSet());
		}
	}

	private static void configureLogging() {
		ConfigurationSource source;
		try {
			source = new ConfigurationSource(new FileInputStream(LOG4J_CONFIG_FILENAME));
			Configurator.initialize(null, source);
		} catch (IOException e) {
			System.out.println(String.format(
					"WARNING! Can't find the log4j logging configuration file %s; using DefaultConfiguration for logging.",
					LOG4J_CONFIG_FILENAME));
			Configurator.initialize(new DefaultConfiguration());
		}
	}
}
