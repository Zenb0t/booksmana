package com.booksmana.io;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.LinkedList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.booksmana.data.Book;

public class BookReport {
	public static final String HORIZONTAL_LINE = "--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------";
	public static final String HEADER_FORMAT = "%3s. %-12s %-40s %-4s %-36s %-8s %-13s %-15s";
	public static final String BOOK_FORMAT = "%08d %-12s %-40s %4d %-40s %6.3f %13d %-60s";
	public static final int TEXT_FIELD_MAX = 40;
	public static final String ELLIPSIS = "...";

	private static final Logger LOG = LogManager.getLogger();

	/**
	 * private constructor to prevent instantiation
	 */
	private BookReport() {
	}

	public static void write(LinkedList<Book> bookList, String filePath) {

		LOG.debug("Opening Streams");
		FileOutputStream fileOut;
		try {
			fileOut = new FileOutputStream(filePath);
			PrintStream printOut = new PrintStream(fileOut);

			// printing file

			printOut.println("Book Report");
			printOut.println(HORIZONTAL_LINE);
			printOut.format(HEADER_FORMAT, "Book_ID", "ISBN", "Authors ", "Year", "Original Title", "Average_Rating",
					"Rating_Count", "Image_URL").println();
			printOut.println(HORIZONTAL_LINE);

			for (Book book : bookList) {

				String author = book.getAuthors();
				String title = book.getOriginalTitle();
				LOG.debug(String.format("Check lenght of title(%d) and author(%d) and truncate accordingly if exceeds max field size", title.length(), author.length()));
				title = (title.length() > TEXT_FIELD_MAX)
						? title.substring(0, TEXT_FIELD_MAX - ELLIPSIS.length()).concat(ELLIPSIS)
						: title;
				author = (author.length() > TEXT_FIELD_MAX)
						? author.substring(0, TEXT_FIELD_MAX - ELLIPSIS.length()).concat(ELLIPSIS)
						: author;

				printOut.format(BOOK_FORMAT, book.getBookId(), book.getIsbn(), author,
						book.getOriginalPublicationYear(), title, book.getAverageRating(), book.getRatingsCount(),
						book.getImageUrl());
				printOut.println();
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
