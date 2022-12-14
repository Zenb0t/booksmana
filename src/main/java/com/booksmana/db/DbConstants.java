package com.booksmana.db;


public interface DbConstants {

	String DB_PROPERTIES_FILENAME = "db.properties";
	String DB_DRIVER_KEY = "db.driver";
	String DB_URL_KEY = "db.url";
	String DB_USER_KEY = "db.user";
	String DB_PASSWORD_KEY = "db.password";
	String TABLE_ROOT = "ROOT";
	String CUSTOMER_TABLE_NAME = TABLE_ROOT + "Customers";
	String BOOK_TABLE_NAME = TABLE_ROOT + "Book";
	String PURCHASE_TABLE_NAME = TABLE_ROOT + "Purchase";

}
