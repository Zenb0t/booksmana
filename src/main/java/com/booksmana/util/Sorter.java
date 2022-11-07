package com.booksmana.util;

import java.util.Comparator;

import com.booksmana.data.Book;
import com.booksmana.data.Customer;

public class Sorter {

	public static class ByJoinedDateAscending implements Comparator<Customer> {

		@Override
		public int compare(Customer c1, Customer c2) {

			return c1.getJoinedDate().compareTo(c2.getJoinedDate());
		}
	}

	public static class ByJoinedDateDescending implements Comparator<Customer> {

		@Override
		public int compare(Customer c1, Customer c2) {

			return c2.getJoinedDate().compareTo(c1.getJoinedDate());
		}
	}

	public static class byAuthor implements Comparator<Book> {

		@Override
		public int compare(Book b1, Book b2) {

			return b1.getAuthors().compareTo(b2.getAuthors());
		}
	}

	public static class byAuthorDesc implements Comparator<Book> {

		@Override
		public int compare(Book b1, Book b2) {

			return b2.getAuthors().compareTo(b1.getAuthors());
		}
	}

}
