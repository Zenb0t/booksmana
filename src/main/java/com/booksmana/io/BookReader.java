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

import com.booksmana.data.Book;
import com.booksmana.data.BookDAO;
import com.booksmana.util.ApplicationException;

public class BookReader {

	private static final Logger LOG = LogManager.getLogger();

	public static LinkedList<Book> read(String path) throws ApplicationException {
		LOG.info("Parsing data and creating book objects");
		File file = new File(path);
		FileReader in;
		Iterable<CSVRecord> records;

		try {
			in = new FileReader(file);
			records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(in);
		} catch (IOException e) {
			throw new ApplicationException(e);
		}

		LinkedList<Book> bookList = new LinkedList<Book>();

		LOG.debug("Reading " + file.getAbsolutePath());

		for (CSVRecord record : records) {

			String bookId = record.get("book_id");
			String isbn = record.get("isbn");
			String authors = record.get("authors");
			String originalPublicationYear = record.get("original_publication_year");
			String originalTitle = record.get("original_title");
			String averageRating = record.get("average_rating");
			String ratingsCount = record.get("ratings_count");
			String imageUrl = record.get("image_url");

			Book book = new Book.Builder(Long.parseLong(bookId), isbn).setAuthors(authors)
					.setOriginalPublicationYear(Integer.parseInt(originalPublicationYear))
					.setOriginalTitle(originalTitle).setAverageRating(Double.parseDouble(averageRating))
					.setRatingsCount(Integer.parseInt(ratingsCount)).setImageUrl(imageUrl).build();

			bookList.add(book);
		}

		return bookList;

	}
	
	public static LinkedList<Book> load (BookDAO bDao )throws SQLException , ApplicationException {
		LOG.info("Loading books from the database");
		LinkedList<Book> bookList = new LinkedList<Book>();
		Book book = null;
		
		for (String s : bDao.getBookIds()) {
			book = bDao.getBook(s);
			bookList.add(book);
			
		}
		return bookList;
	}

}
