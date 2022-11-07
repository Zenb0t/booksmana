package com.booksmana.io;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.LinkedList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.booksmana.book.BookOptions;
import com.booksmana.data.Book;
import com.booksmana.data.Customer;
import com.booksmana.data.Purchase;

public class PurchaseReport {

	public static final String HORIZONTAL_LINE = "----------------------------------------------------------------------------------------------------------------------------------------------";
	public static final String HEADER_FORMAT = "%-24s %-80s %-2s";
	public static final String PURCHASE_FORMAT = "%-24s %-80s $%.2f";

	private static final Logger LOG = LogManager.getLogger();

	/**
	 * private constructor to prevent instantiation
	 */
	private PurchaseReport() {
	}

	public static void write(LinkedList<Purchase> purchaseList, String filePath, LinkedList<Book> bookList,
			LinkedList<Customer> customersList, HashMap<Long, int[]> indexList) {

		LOG.debug("Opening Streams");

		FileOutputStream fileOut;
		double total = 0;
		double globalTotal = 0;
		int cusRef = 0;
		try {
			fileOut = new FileOutputStream(filePath);
			PrintStream printOut = new PrintStream(fileOut);

			// printing file

			printOut.println("Purchase Report");
			printOut.println(HORIZONTAL_LINE);
			printOut.format(HEADER_FORMAT, "Name", "Title", "Price").println();
			printOut.println(HORIZONTAL_LINE);

			// Change to iterator so have control of first and last element
			for (Purchase purchase : purchaseList) {
				LOG.debug("Inside for each Purchase");
				LOG.debug(purchase.toString());
				LOG.debug(indexList == null);

				int cusIndex = indexList.get(purchase.getId())[0];
				int bookIndex = indexList.get(purchase.getId())[1];
				
				globalTotal += purchase.getPrice();
				LOG.debug(String.format("cusIndex %d bookIndex %d", cusIndex, bookIndex));
				Customer customer = customersList.get(cusIndex);
				Book book = bookList.get(bookIndex);

				String name = String.format("%s %s", customer.getFirstName(), customer.getLastName());
				String title = book.getOriginalTitle();
				LOG.debug(name + " " + title + " " + purchase.getPrice());

				if (cusIndex != cusRef && cusRef != 0) {
					printOut.println();
					printOut.format("Value of purchases: $%.2f \n", total);
					printOut.println(HORIZONTAL_LINE);
					LOG.debug(String.format("Value of purchases: $%.2f \n", total));
					total = 0;
					cusRef = cusIndex;
					LOG.debug("Total now is : " + total);
					// Print the total, next purchase it's a different customer
				}
				printOut.format(PURCHASE_FORMAT, name, title, purchase.getPrice());
				printOut.println();

				if (BookOptions.isTotalOptionSet() && !BookOptions.isByTitleOptionSet()) {
					LOG.debug(String.format("Customer Reference is equal zero? %b", cusRef == 0));
					if (cusRef == 0) {
						cusRef = cusIndex;
						LOG.debug(String.format("Customer Reference is %d", cusRef));
					}
					if (cusRef == cusIndex) {
						LOG.debug(String.format("Old total is %f", total));
						total += purchase.getPrice();
						LOG.debug(String.format("New total  is %f", total));
					}

				}
			}

			if (BookOptions.isTotalOptionSet() && cusRef != 0) {
				printOut.println();
				printOut.format("Value of purchases: $%.2f \n", total);
				printOut.println(HORIZONTAL_LINE);
			}
			
			if (BookOptions.isTotalOptionSet()) {
				printOut.println();
				printOut.format("Total value of purchases: $%.2f \n", globalTotal);
				printOut.println(HORIZONTAL_LINE);
			}
			
			
			fileOut.close();
			printOut.close();
			LOG.debug("Closed Streams");

		} catch (

		FileNotFoundException e) {
			LOG.error(e.getMessage(), e);
		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
		}
	}

}
