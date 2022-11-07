package com.booksmana.io;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.LinkedList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.booksmana.data.Customer;
import com.booksmana.data.CustomerDAO;
import com.booksmana.util.ApplicationException;
import com.booksmana.util.Validator;

public class CustomerReader {

	public static final String RECORD_DELIMITER = ":";
	public static final String FIELD_DELIMITER = "\\|";

	private static final Logger LOG = LogManager.getLogger();

	/**
	 * private constructor to prevent instantiation
	 */
	private CustomerReader() {
	}

	/**
	 * Read the customer input data.
	 * 
	 * @param data The input data.
	 * @return An array of customers.
	 */
	public static LinkedList<Customer> read(String path) throws ApplicationException {
		LOG.info("Parsing data and creating customer objects");

		LinkedList<Customer> customersList = new LinkedList<Customer>();
		
		try {
			FileReader fileReader = new FileReader(path);
			BufferedReader bufferedReader = new BufferedReader(fileReader);

			String s;
			LOG.debug("Reading values with buffered reader");
			while ((s = bufferedReader.readLine()) != null) {
				if (Character.isDigit(s.charAt(0))) {
					LOG.debug(s);
					Customer customer = parseString(s);
					customersList.add(customer);
				}
			}

			bufferedReader.close();
		} catch (IOException e) {
			throw new ApplicationException(e);
		}
		LOG.debug("Customer List size is: " + customersList.size());
		return customersList;

	}
	
	public static LinkedList<Customer> load (CustomerDAO cDao)throws SQLException , ApplicationException {
		LOG.info("Loading customers from database");
		LinkedList<Customer> customerList = new LinkedList<Customer>();
		Customer cus = null;
		
		for (String s : cDao.getIds()) {
			cus = cDao.getCustomer(s);
			customerList.add(cus);
			
		}
		return customerList;
	}

	private static Customer parseString(String row) throws ApplicationException {
		String[] elements = row.split(FIELD_DELIMITER);
		if (elements.length != Customer.FIELD_COUNT) {
			throw new ApplicationException(String.format("Expected %d but got %d: %s", Customer.FIELD_COUNT,
					elements.length, Arrays.toString(elements)));
		}

		int index = 0;
		long id = Integer.parseInt(elements[index++]);
		String firstName = elements[index++];
		String lastName = elements[index++];
		String street = elements[index++];
		String city = elements[index++];
		String postalCode = elements[index++];
		String phone = elements[index++];
		String emailAddress = Validator.validateEmail(elements[index++]);
		LocalDate joinDate = Validator.validateJoinedDate(elements[index]);
		Customer customer = null;

		customer = new Customer.Builder(id, phone).setFirstName(firstName).setLastName(lastName).setStreet(street)
				.setCity(city).setPostalCode(postalCode).setEmailAddress(emailAddress).setJoinedDate(joinDate).build();

		return customer;
	}
}
