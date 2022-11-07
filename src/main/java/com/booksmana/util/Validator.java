/**
 * Project: A01169959 Lab3
 * File: Validator.java
 */

package com.booksmana.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;


/**
 * @author Felipe Mendes Ribeiro
 *
 */
public class Validator {

	private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
	private static final String YYYYMMDD_PATTERN = "(20\\d{2})(\\d{2})(\\d{2})";
	
	private Validator() {
	}


	public static String validateEmail(String emailAddress) throws ApplicationException {
		if (!emailAddress.matches(EMAIL_PATTERN)) {
			throw new ApplicationException(String.format("Invalid email: %s", emailAddress));
		}

		return emailAddress;
	}

	public static LocalDate validateJoinedDate(String joinedDate) throws ApplicationException {
		LocalDate date = null;
		if (!joinedDate.matches(YYYYMMDD_PATTERN)) {
			throw new ApplicationException(String.format("Invalid joined date: %s", joinedDate));
		}
		try {
			date = LocalDate.parse(joinedDate, DateTimeFormatter.ofPattern("yyyyMMdd"));
		} catch (Exception e) {
			throw new ApplicationException(String.format("Invalid joined date: %s", joinedDate));
		}

		return date;
	}
	
	public static boolean isNumeric(String s) throws ApplicationException {
		boolean isNumber = false;
		try{
			Long.parseLong(s);
			isNumber = true;
		}catch (Exception e) {
			throw new ApplicationException(String.format("Invalid number: %s", s));
		}
		return isNumber;
	}
}
