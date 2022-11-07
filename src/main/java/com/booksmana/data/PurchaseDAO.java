package com.booksmana.data;

import java.sql.Connection;
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

public class PurchaseDAO extends Dao {
	
	public static final String TABLE_NAME = DbConstants.PURCHASE_TABLE_NAME;

	private static final Logger LOG = LogManager.getLogger();

	public PurchaseDAO(Database database) {
		super(database, TABLE_NAME);
	}
	
//	private long id;
//	private long customerId;
//	private long bookId;
//	private double price;

	@Override
	public void create() throws SQLException {
		LOG.debug("Creating " + TABLE_NAME);
		String sql = String.format("create table %s(" // 1
				+ "%s INT, " // 2
				+ "%s INT, " // 3
				+ "%s INT, " // 4
				+ "%s MONEY, " // 5
				+ "PRIMARY KEY (%s)) ",// 6
				tableName, // 1
				Fields.ID.getName(), // 2
				Fields.CUSTOMER_ID.getName(), // 3
				Fields.BOOK_ID.getName(), // 4
				Fields.PRICE.getName(), // 5
				Fields.ID.getName()); // 6
//12

		LOG.debug(sql);
		super.create(sql);
	}

	@SuppressWarnings("static-access")
	public void add(Purchase purchase) throws SQLException {

		Statement statement = null;
		try {
			Connection connection = database.getConnection();
			statement = connection.createStatement();
			String sql = String.format("insert into %s values(" // 1 tableName
					+ "'%s', " // 2 Purchase Id
					+ "'%s', " // 3 Customer Id
					+ "'%s', " // 4 Book Id
					+ "'%s') ", // 5 Price
					tableName, // 1
					purchase.getId(), // 2
					purchase.getCustomerId(), // 3
					purchase.getBookId(), // 4
					purchase.getPrice());// 5
			LOG.debug(sql);
			statement.executeUpdate(sql);
		} finally {
			close(statement);
		}
	}

	@SuppressWarnings("static-access")
	public Purchase getPurchase(String purId) throws SQLException, ApplicationException {
		Connection connection;
		Statement statement = null;
		Purchase purchase = null;

		try {
			connection = database.getConnection();
			statement = connection.createStatement();
			// Execute a statement
			String sql = String.format("SELECT * FROM %s WHERE %s = '%s'", tableName, Fields.ID.getName(), purId);
			LOG.debug(sql);
			ResultSet resultSet = statement.executeQuery(sql);

			// get the Purchase
			// throw an exception if we get more than one result
			int count = 0;
			while (resultSet.next()) {
				count++;
				if (count > 1) {
					throw new ApplicationException(String.format("Expected one result, got %d", count));
				}

				purchase = new Purchase.Builder(Integer.parseInt(resultSet.getString(Fields.ID.getName())),Integer.parseInt(resultSet.getString(Fields.CUSTOMER_ID.getName())),Integer.parseInt(resultSet.getString(Fields.BOOK_ID.getName()))
						)
								.setPrice(Double.parseDouble(resultSet.getString(Fields.PRICE.getName())))
								.build();

			}
		} finally {
			close(statement);
		}

		return purchase;
	}

	@SuppressWarnings("static-access")
	public List<String> getPurchaseIds() throws SQLException , ApplicationException {
	
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

				String id = resultSet.getString(Fields.ID.getName());
				list.add(id);
			}
			}finally {
				close(statement);
			}
		
		return list;
	}


	@SuppressWarnings("static-access")
	public void update(Purchase purchase) throws SQLException {
		Connection connection;
		Statement statement = null;
		try {
			connection = database.getConnection();
			statement = connection.createStatement();
			String sql = String.format("UPDATE %s set %s='%s', %s='%s', %s='%s', %s='%s', WHERE %s='%s'",
					tableName, //
					Fields.ID.getName(), purchase.getId(), //
					Fields.CUSTOMER_ID.getName(), purchase.getCustomerId(), //
					Fields.BOOK_ID.getName(), purchase.getBookId(), //
					Fields.PRICE.getName(), purchase.getPrice(), //			
					Fields.ID.getName(), purchase.getId());
			LOG.debug(sql);
			int rowcount = statement.executeUpdate(sql);
			LOG.info(String.format("Updated %d rows", rowcount));
			
		}finally {
			close(statement);
		}
	}

	@SuppressWarnings("static-access")
	public void delete(Purchase purchase) throws SQLException {
		Connection connection;
		Statement statement = null;
		try {
			connection = database.getConnection();
			statement = connection.createStatement();
			// Execute a statement
			String sql = String.format("DELETE from %s WHERE %s='%s'", tableName, Fields.ID.getName(),
					purchase.getId());
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

		ID("purchaseId", "INT", -1, 2), CUSTOMER_ID("customerId", "INT", -1, 3),
		BOOK_ID("bookId", "INT", -1, 4), PRICE("price", "MONEYR", -1, 5);

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
