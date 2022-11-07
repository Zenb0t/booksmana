package com.booksmana.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.booksmana.db.Dao;
import com.booksmana.db.Database;
import com.booksmana.util.ApplicationException;
import com.booksmana.db.DbConstants;
import com.booksmana.util.Validator;

public class CustomerDAO extends Dao {

	public static final String TABLE_NAME = DbConstants.CUSTOMER_TABLE_NAME;

	private static final Logger LOG = LogManager.getLogger();

	public CustomerDAO(Database database) {
		super(database, TABLE_NAME);
	}

	@Override
	public void create() throws SQLException {
		String sql = String.format("create table %s(" // 1
				+ "%s INT, " // 2
				+ "%s VARCHAR(50), " // 3
				+ "%s VARCHAR(50), " // 4
				+ "%s VARCHAR(50), " // 5
				+ "%s VARCHAR(50), " // 6
				+ "%s VARCHAR(50), " // 7
				+ "%s VARCHAR(50), " // 8
				+ "%s VARCHAR(50), " // 9
				+ "%s DATE, " // 10
				+ "primary key (%s) )", // 11
				tableName, // 1
				Fields.ID.getName(), // 2
				Fields.FIRST_NAME.getName(), // 3
				Fields.LAST_NAME.getName(), // 4
				Fields.STREET.getName(), // 5
				Fields.CITY.getName(), // 6
				Fields.POSTAL_CODE.getName(), // 7
				Fields.PHONE.getName(), // 8
				Fields.EMAIL.getName(), // 9
				Fields.JOIN_DATE.getName(), // 10
				Fields.ID.getName() // 11
			//	Fields.LAST_NAME.getName() //11
				); // 

		LOG.debug(sql);
		super.create(sql);
	}

	@SuppressWarnings("static-access")
	public void add(Customer customer) throws SQLException {

		PreparedStatement prepStmt = null;
		try {
			Connection connection = database.getConnection();
			prepStmt = connection.prepareStatement("insert into " + tableName + " values(?,?,?,?,?,?,?,?,?)");

//			prepStmt.setString(1,tableName);
			prepStmt.setLong(1, customer.getId());
			prepStmt.setString(2, customer.getFirstName());
			prepStmt.setString(3, customer.getLastName());
			prepStmt.setString(4, customer.getStreet());
			prepStmt.setString(5, customer.getCity());
			prepStmt.setString(6, customer.getPostalCode());
			prepStmt.setString(7, customer.getPhone());
			prepStmt.setString(8, customer.getEmailAddress());
			prepStmt.setString(9, customer.getJoinedDate().toString());

			String sql = String.format("insert into %s values(" // 1 tableName
					+ "'%s', " // 2 Customer Id
					+ "'%s', " // 3 FirstName
					+ "'%s', " // 4 LastName
					+ "'%s', " // 5 Street
					+ "'%s', " // 6 City
					+ "'%s', " // 7 Postal Code
					+ "'%s', " // 8 Phone
					+ "'%s', " // 9 Email
					+ "'%s')", // 10 Join date
					tableName, // 1
					customer.getId(), // 2
					customer.getFirstName(), // 3
					customer.getLastName(), // 4
					customer.getStreet(), // 5
					customer.getCity(), // 5
					customer.getPostalCode(), // 7
					customer.getPhone(), // 8
					customer.getEmailAddress(), // 9
					customer.getJoinedDate());// 10
			LOG.debug(sql);
			prepStmt.executeUpdate();
		} finally {
			close(prepStmt);
		}
	}

	@SuppressWarnings("static-access")
	public Customer getCustomer(String cusId) throws SQLException, ApplicationException {
		Connection connection;
		Statement statement = null;
		Customer customer = null;

		try {
			connection = database.getConnection();
			statement = connection.createStatement();
			// Execute a statement
			String sql = String.format("SELECT * FROM %s WHERE %s = '%s'", tableName, Fields.ID.getName(), cusId);
			LOG.debug(sql);
			ResultSet resultSet = statement.executeQuery(sql);

			// get the Customer
			// throw an exception if we get more than one result
			int count = 0;
			while (resultSet.next()) {
				count++;
				if (count > 1) {
					throw new ApplicationException(String.format("Expected one result, got %d", count));
				}
				String emailAddress = Validator.validateEmail(resultSet.getString(Fields.EMAIL.getName()));
				LocalDate joinDate = LocalDate.parse(resultSet.getString(Fields.JOIN_DATE.getName()));

				customer = new Customer.Builder(Integer.parseInt(resultSet.getString(Fields.ID.getName())),
						resultSet.getString(Fields.PHONE.getName()))
								.setFirstName(resultSet.getString(Fields.FIRST_NAME.getName()))
								.setLastName(resultSet.getString(Fields.LAST_NAME.getName()))
								.setStreet(resultSet.getString(Fields.STREET.getName()))
								.setCity(resultSet.getString(Fields.CITY.getName()))
								.setPostalCode(resultSet.getString(Fields.POSTAL_CODE.getName()))
								.setEmailAddress(emailAddress).setJoinedDate(joinDate).build();

			}
		} finally {
			close(statement);
		}

		return customer;
	}

	@SuppressWarnings("static-access")
	public List<String> getIds() throws SQLException, ApplicationException {
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

				String cusId = resultSet.getString(Fields.ID.getName());
				list.add(cusId);
			}
		} finally {
			close(statement);
		}

		return list;
	}

	@SuppressWarnings("static-access")
	public void update(Customer customer) throws SQLException {
		Connection connection;
		Statement statement = null;
		try {
			connection = database.getConnection();
			statement = connection.createStatement();
			String sql = String.format(
					"UPDATE %s set %s='%s', %s='%s', %s='%s', %s='%s', %s='%s', %s='%s', %s='%s', %s='%s', %s='%s' WHERE %s='%s'",
					tableName, //
					Fields.ID.getName(), customer.getId(), //
					Fields.FIRST_NAME.getName(), customer.getFirstName(), //
					Fields.LAST_NAME.getName(), customer.getLastName(), //
					Fields.STREET.getName(), customer.getStreet(), //
					Fields.CITY.getName(), customer.getCity(), //
					Fields.POSTAL_CODE.getName(), customer.getPostalCode(), //
					Fields.PHONE.getName(), customer.getPhone(), //
					Fields.EMAIL.getName(), customer.getEmailAddress(), //
					Fields.JOIN_DATE.getName(), customer.getJoinedDate(), //
					Fields.ID.getName(), customer.getId());
			LOG.debug(sql);
			int rowcount = statement.executeUpdate(sql);
			LOG.info(String.format("Updated %d rows", rowcount));

		} finally {
			close(statement);
		}
	}

	@SuppressWarnings("static-access")
	public void delete(Customer customer) throws SQLException {
		Connection connection;
		Statement statement = null;
		try {
			connection = database.getConnection();
			statement = connection.createStatement();
			// Execute a statement
			String sql = String.format("DELETE from %s WHERE %s='%s'", tableName, Fields.ID.getName(),
					customer.getId());
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

		ID("customerId", "INT", -1, 2), FIRST_NAME("firstName", "VARCHAR", 12, 3),
		LAST_NAME("lastName", "VARCHAR", 12, 4), STREET("street", "VARCHAR", 25, 5), CITY("city", "VARCHAR", 12, 6),
		POSTAL_CODE("zipCode", "VARCHAR", 12, 7), PHONE("phone", "VARCHAR", 15, 8), EMAIL("email", "VARCHAR", 25, 9),
		JOIN_DATE("joinedDate", "DATE", -1, 10);

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
