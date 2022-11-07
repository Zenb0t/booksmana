package com.booksmana.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.booksmana.db.Dao;
import com.booksmana.db.Database;
import com.booksmana.db.DbConstants;
import com.booksmana.util.ApplicationException;

public class BookDAO extends Dao{
	
	public static final String TABLE_NAME = DbConstants.BOOK_TABLE_NAME;

	private static final Logger LOG = LogManager.getLogger();

	public BookDAO(Database database) {
		super(database, TABLE_NAME);
	}
	

	@Override
	public void create() throws SQLException {
		String sql = String.format("create table %s(" // 1
				+ "%s INT, " // 2
				+ "%s VARCHAR(12), " // 3
				+ "%s VARCHAR(100), " // 4
				+ "%s INT, " // 5
				+ "%s VARCHAR(100), " // 6
				+ "%s FLOAT, " // 7
				+ "%s INT, " // 8
				+ "%s VARCHAR(100), " // 9
				+ "primary key (%s) )", // 10
				tableName, // 1
				Fields.BOOK_ID.getName(), // 2
				Fields.ISBN.getName(), // 3
				Fields.AUTHOR.getName(), // 4
				Fields.ORIGINAL_PUB_YEAR.getName(), // 5
				Fields.ORIGINAL_TITLE.getName(), // 6
				Fields.AVERAGE_RATING.getName(), // 7
				Fields.RATINGS_COUNT.getName(), // 8
				Fields.IMG_URL.getName(), // 9
				Fields.BOOK_ID.getName()); // 10

		LOG.debug(sql);
		super.create(sql);
	}
	
	@SuppressWarnings("static-access")
	public void add(Book book) throws SQLException {

		PreparedStatement statement = null;
		try {
			Connection connection = database.getConnection();
			statement = connection.prepareStatement("insert into "+ tableName+" values(?,?,?,?,?,?,?,?)");
			statement.setLong(1, book.getBookId());
			statement.setString(2, book.getIsbn());
			statement.setString(3, book.getAuthors());
			statement.setInt(4, book.getOriginalPublicationYear());
			statement.setString(5, book.getOriginalTitle());
			statement.setDouble(6, book.getAverageRating());
			statement.setInt(7, book.getRatingsCount());
			statement.setString(8, book.getImageUrl());

			
			
//			String sql = String.format("insert into "+tableName+"%s values(" // 1 tableName
//					+ "'%s', " // 2 BOOK ID
//					+ "'%s', " // 3 ISBN
//					+ "'%s', " // 4 AUTHOR
//					+ "'%s', " // 5 ORIGINAL_PUB_YEAR
//					+ "'%s', " // 6 ORIGINAL_TITLE
//					+ "'%s', " // 7 AVERAGE_RATING Code
//					+ "'%s', " // 8 RATINGS_COUNT
//					+ "'%s')", // 9 IMG_URL
//					book.getBookId(), // 2
//					book.getIsbn(), // 3
//					book.getAuthors(), // 4
//					book.getOriginalPublicationYear(), //5
//					book.getOriginalTitle(), // 6
//					book.getAverageRating(), // 7
//					book.getRatingsCount(), // 8
//					book.getImageUrl());// 9
//			LOG.debug(sql);
			statement.executeUpdate();
		} finally {
			close(statement);
		}
	}

	@SuppressWarnings("static-access")
	public Book getBook(String bookId) throws SQLException, ApplicationException {
		Connection connection;
		Statement statement = null;
		Book book = null;

		try {
			connection = database.getConnection();
			statement = connection.createStatement();
			// Execute a statement
			String sql = String.format("SELECT * FROM %s WHERE %s = '%s'", tableName, Fields.BOOK_ID.getName(), bookId);
			LOG.debug(sql);
			ResultSet resultSet = statement.executeQuery(sql);

			// get the book
			// throw an exception if we get more than one result
			int count = 0;
			while (resultSet.next()) {
				count++;
				if (count > 1) {
					throw new ApplicationException(String.format("Expected one result, got %d", count));
				}

				book = new Book.Builder(Integer.parseInt(resultSet.getString(Fields.BOOK_ID.getName())),
						resultSet.getString(Fields.ISBN.getName()))
								.setAuthors(resultSet.getString(Fields.AUTHOR.getName()))
								.setAverageRating(resultSet.getFloat(Fields.AVERAGE_RATING.getName()))
								.setRatingsCount(resultSet.getInt(Fields.RATINGS_COUNT.getName()))
								.setOriginalPublicationYear(resultSet.getInt(Fields.ORIGINAL_PUB_YEAR.getName()))
								.setOriginalTitle(resultSet.getString(Fields.ORIGINAL_TITLE.getName()))
								.setImageUrl(resultSet.getString(Fields.IMG_URL.getName()))
								.build();
								
								

			}
		} finally {
			close(statement);
		}

		return book;
	}

	@SuppressWarnings("static-access")
	public List<String> getBookIds() throws SQLException , ApplicationException {
		List<String> list = new LinkedList<String>();
		Connection connection;
		Statement statement = null;
		
		try {
			connection = database.getConnection();
			statement = connection.createStatement();
			// Execute a statement
			String sql = String.format("SELECT * FROM %s ", tableName);
			LOG.debug(sql);
			ResultSet resultSet = statement.executeQuery(sql);
		
			while (resultSet.next()) {

				String bookId = resultSet.getString(Fields.BOOK_ID.getName());
				list.add(bookId);
			}
			}finally {
				close(statement);
			}
		
		return list;
	}

	@SuppressWarnings("static-access")
	public void update(Book book) throws SQLException {
		Connection connection;
		Statement statement = null;
		try {
			connection = database.getConnection();
			statement = connection.createStatement();
			String sql = String.format("UPDATE %s set %s='%s', %s='%s', %s='%s', %s='%s', %s='%s', %s='%s', %s='%s', %s='%s' WHERE %s='%s'",
					tableName, //
					Fields.BOOK_ID.getName(), book.getBookId(), //
					Fields.ISBN.getName(), book.getIsbn(), //
					Fields.AUTHOR.getName(), book.getAuthors(), //
					Fields.ORIGINAL_PUB_YEAR.getName(), book.getOriginalPublicationYear(), //
					Fields.ORIGINAL_TITLE.getName(), book.getOriginalTitle(), //
					Fields.AVERAGE_RATING.getName(), book.getAverageRating(), //
					Fields.RATINGS_COUNT.getName(), book.getRatingsCount(), //
					Fields.IMG_URL.getName(), book.getImageUrl(), //				
					Fields.BOOK_ID.getName(), book.getBookId());
			LOG.debug(sql);
			int rowcount = statement.executeUpdate(sql);
			LOG.info(String.format("Updated %d rows", rowcount));
			
		}finally {
			close(statement);
		}
	}

	@SuppressWarnings("static-access")
	public void delete(Book book) throws SQLException {
		Connection connection;
		Statement statement = null;
		try {
			connection = database.getConnection();
			statement = connection.createStatement();
			// Execute a statement
			String sql = String.format("DELETE from %s WHERE %s='%s'", tableName, Fields.BOOK_ID.getName(),
					book.getBookId());
			LOG.debug(sql);
			int rowcount = statement.executeUpdate(sql);
			LOG.info(String.format("Deleted %d rows", rowcount));
		} finally {
			close(statement);
		}
	}

	@SuppressWarnings("static-access")
	public void dropTable() throws SQLException {
		Connection connection;
		Statement statement = null;
		try {
			connection = database.getConnection();
			statement = connection.createStatement();
			// Execute a statement
			String sql = String.format("DROP TABLE %s ", tableName);
			LOG.debug(sql);
			int rowcount = statement.executeUpdate(sql);
			LOG.info(String.format("Table %s Dropped", TABLE_NAME, rowcount));
		} finally {
			close(statement);
		}

	}

	public enum Fields {

		BOOK_ID("BookId", "INT", -1, 2), ISBN("ISBN", "CHAR", 8, 3),
		AUTHOR("Author", "VARCHAR", 40, 4), ORIGINAL_PUB_YEAR("originalPublicationYear", "INT", -1, 5),ORIGINAL_TITLE("originalTitle", "VARCHAR",100 , 6  ), AVERAGE_RATING("averageRating", "FLOAT", -1, 7),
		RATINGS_COUNT("ratingsCount", "INT", -1, 8), IMG_URL("imageUrl", "VARCHAR", 100, 9);

		private final String name;
		private final String type;
		private final int length;
		private final int column;

		Fields(String name, String type, int length, int column) {
			this.name = name;
			this.type = type;
			this.length = length;
			this.column = column;
		}

		public String getType() {
			return type;
		}

		public String getName() {
			return name;
		}

		public int getLength() {
			return length;
		}

		public int getColumn() {
			return column;
		}
	}

}
