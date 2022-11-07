package com.booksmana.io;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.booksmana.data.Customer;

public class CustomerReport {

	public static final String HORIZONTAL_LINE = "--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------";
	public static final String HEADER_FORMAT = "%4s. %-6s %-12s %-12s %-40s %-25s %-12s %-15s %-40s%s%n";
	public static final String CUSTOMER_FORMAT = "%4d. %06d %-12s %-12s %-40s %-25s %-12s %-15s %-40s %-12s";
	public static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("MMM dd yyyy");

	private static final Logger LOG = LogManager.getLogger();

	/**
	 * private constructor to prevent instantiation
	 */
	private CustomerReport() {
	}

	/**
	 * Write the report.
	 * 
	 * @param customers the customers
	 */
	public static void write(LinkedList<Customer> customers, String filePath) {

		

		LOG.debug("Opening Streams");
		FileOutputStream fileOut;
		try {
			fileOut = new FileOutputStream(filePath);
			PrintStream printOut = new PrintStream(fileOut);

			// printing file
			
			printOut.println("Customers Report");
			printOut.println(HORIZONTAL_LINE);
			printOut.format(HEADER_FORMAT, "#", "ID", "First name", "Last name", "Street", "City", "Postal Code", "Phone",
					"Email", "Join Date");
			printOut.println(HORIZONTAL_LINE);
			int i = 0;
			for (Customer customer : customers) {
				LocalDate date = customer.getJoinedDate();
				printOut.format(CUSTOMER_FORMAT, ++i, customer.getId(), customer.getFirstName(), customer.getLastName(),
						customer.getStreet(), customer.getCity(), customer.getPostalCode(), customer.getPhone(),
						customer.getEmailAddress(), DATE_FORMAT.format(date)).println();;
			//	printOut.println();
			}
			fileOut.close();
			printOut.close();
			LOG.debug("Closed Streams");

		} catch (FileNotFoundException e) {
			LOG.error(e.getStackTrace().toString(), e.getMessage());
		} catch (IOException e) {
			LOG.error(e.getStackTrace().toString(), e.getMessage());
		}
	}
}
