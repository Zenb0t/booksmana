package a01169959.book;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import a01169959.book.BookStore;
import a01169959.data.Book;
import a01169959.data.Customer;
import a01169959.data.Purchase;
import a01169959.io.BookReader;
import a01169959.io.CustomerReader;
import a01169959.io.PurchasesReader;
import a01169959.util.ApplicationException;

public class Controller {


	private static LinkedList<Customer> customersList;
	private static LinkedList<Book> bookList;
	private static LinkedList<Purchase> purchaseList;
	
	private static Controller controller = new Controller();

	private Controller() {

	}

	public static Controller getInstance() {
		return controller;
	}

	public void initializeController(LinkedList<Customer> cList, LinkedList<Book> bList, LinkedList<Purchase> pList) {
		setBookList(bList);
		setCustomersList(cList);
		setPurchaseList(pList);
		
	}
	
	public void reloadLists() throws SQLException, ApplicationException{
		setCustomersList(CustomerReader.load(BookStore.customerDao));
		setBookList(BookReader.load(BookStore.bookDAO));
		setPurchaseList(PurchasesReader.load(BookStore.purchaseDAO));
	}

	public  LinkedList<Customer> getCustomersList() {
		return customersList;
	}

	public  LinkedList<Book> getBookList() {
		return bookList;
	}

	public  LinkedList<Purchase> getPurchaseList() {
		return purchaseList;
	}

	private static void setCustomersList(LinkedList<Customer> customersList) {
		Controller.customersList = customersList;
	}

	private static void setBookList(LinkedList<Book> bookList) {
		Controller.bookList = bookList;
	}

	private static void setPurchaseList(LinkedList<Purchase> purchaseList) {
		Controller.purchaseList = purchaseList;
	}

	public  void dropAll() throws SQLException {

		BookStore.customerDao.dropTable();
		BookStore.bookDAO.dropTable();
		BookStore.purchaseDAO.dropTable();

	}
	
	public HashMap<Long, int[]> indexList() {

			HashMap<Long, int[]> indexMap = new HashMap<Long, int[]>();

			for (Purchase p : purchaseList) {
				int cusIndex = 0;
				int bookIndex = 0;

				for (Customer c : customersList) {
					if (c.getId() == p.getCustomerId()) {
						cusIndex = customersList.indexOf(c);

					}
				}
				for (Book b : bookList) {
					if (b.getBookId() == p.getBookId()) {
						bookIndex = bookList.indexOf(b);

					}
				}
				int[] index = new int[] { cusIndex, bookIndex };
				indexMap.put(p.getId(), index);

			}

			return indexMap;
	}


	@SuppressWarnings("rawtypes")
	public  int count(List list) {
		return list.size();
	}
	
	public double getTotal(LinkedList<Purchase> list) {
		double total = 0;
		for (Purchase p : list) {
			total += p.getPrice();
		}
		return total;
	}
}
