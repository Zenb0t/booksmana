package com.booksmana.data;

/**
 * @author Felipe Mendes Ribeiro
 *
 */
public class Purchase  {

	public static final int FIELD_COUNT = 4;
	private long id;
	private long customerId;
	private long bookId;
	private double price;

	public static class Builder {
		private long id;
		private long customerId;
		private long bookId;
		private double price;

		public Builder(long id, long customerId, long bookId) {
			this.id = id;
			this.bookId = bookId;
			this.customerId = customerId;

		}

		public Builder setPrice(double price) {
			this.price = price;
			return this;
		}

		public Purchase build() {
			return new Purchase(this);
		}
	}

	private Purchase(Builder builder) {
		id = builder.id;
		bookId = builder.bookId;
		customerId = builder.customerId;
		price = builder.price;
	}

	public static int getFieldCount() {
		return FIELD_COUNT;
	}

	public long getId() {
		return id;
	}

	public long getCustomerId() {
		return customerId;
	}

	public long getBookId() {
		return bookId;
	}

	public double getPrice() {
		return price;
	}

	@Override
	public String toString() {
		return "Purchase [id=" + id + ", customerId=" + customerId + ", bookId=" + bookId + ", price=" + price + "]";
	}

}
