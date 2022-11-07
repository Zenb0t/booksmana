package com.booksmana.io;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.LinkedList;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.booksmana.data.Purchase;
import com.booksmana.data.PurchaseDAO;
import com.booksmana.util.ApplicationException;

public class PurchasesReader {
	
	private static final Logger LOG = LogManager.getLogger();

	public static LinkedList<Purchase> read(String path) throws ApplicationException {
		LOG.info("Parsing data and creating purchase objects");
		File file = new File(path);
		FileReader in;
		Iterable<CSVRecord> records;

		try {
			in = new FileReader(file);
			records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(in);
		} catch (IOException e) {
			throw new ApplicationException(e);
		}

		LinkedList<Purchase> purchaseList = new LinkedList<Purchase>();


		LOG.debug("Reading " + file.getAbsolutePath());

		for (CSVRecord record : records) {

			Long id = Long.parseLong(record.get("id"));
			Long bookId = Long.parseLong(record.get("book_id"));
			Long customerId = Long.parseLong(record.get("customer_id"));
			Double price = Double.parseDouble(record.get("price"));


			Purchase purchase = new Purchase.Builder(id, customerId, bookId).setPrice(price).build();

			purchaseList.add(purchase);
		}

		return purchaseList;

	}
	
	public static LinkedList<Purchase> load (PurchaseDAO pDao)throws SQLException , ApplicationException {
		LOG.info("Loading the purchases from the database");
		LinkedList<Purchase> purchaseList = new LinkedList<Purchase>();
		Purchase pur = null;
		
		for (String s : pDao.getPurchaseIds()) {
			pur = pDao.getPurchase(s);
			purchaseList.add(pur);
			
		}
		return purchaseList;
	}
	
}
	
	
