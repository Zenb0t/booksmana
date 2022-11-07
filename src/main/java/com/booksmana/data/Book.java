package com.booksmana.data;

/**
 * @author Felipe Mendes Ribeiro
 *
 */
public class Book   {

	public static final int FIELD_COUNT = 8;

	private long bookId;
	private String isbn;
	private String authors;
	private int originalPublicationYear;
	private String originalTitle;
	private double averageRating;
	private int ratingsCount;
	private String imageUrl;

	public static class Builder {

		private long bookId;
		private String isbn;
		private String authors;
		private int originalPublicationYear;
		private String originalTitle;
		private double averageRating;
		private int ratingsCount;
		private String imageUrl;

		public Builder(long bookId, String isbn) {
			this.bookId = bookId;
			this.isbn = isbn;
		}

		public Builder setAuthors(String authors) {
			this.authors = authors;
			return this;
		}

		public Builder setOriginalPublicationYear(int originalPublicationYear) {
			this.originalPublicationYear = originalPublicationYear;
			return this;
		}

		public Builder setOriginalTitle(String originalTitle) {
			this.originalTitle = originalTitle;
			return this;
		}

		public Builder setAverageRating(double averageRating) {
			this.averageRating = averageRating;
			return this;
		}

		public Builder setRatingsCount(int ratingsCount) {
			this.ratingsCount = ratingsCount;
			return this;
		}

		public Builder setImageUrl(String imageUrl) {
			this.imageUrl = imageUrl;
			return this;
		}

		public Book build() {
			return new Book(this);
		}
	}

	private Book(Builder builder) {
		bookId = builder.bookId;
		isbn = builder.isbn;
		authors = builder.authors;
		originalPublicationYear = builder.originalPublicationYear;
		originalTitle = builder.originalTitle;
		averageRating = builder.averageRating;
		ratingsCount = builder.ratingsCount;
		imageUrl = builder.imageUrl;
	}

	public static int getFieldCount() {
		return FIELD_COUNT;
	}

	public long getBookId() {
		return bookId;
	}

	public String getIsbn() {
		return isbn;
	}

	public String getAuthors() {
		return authors;
	}

	public int getOriginalPublicationYear() {
		return originalPublicationYear;
	}

	public String getOriginalTitle() {
		return originalTitle;
	}

	public double getAverageRating() {
		return averageRating;
	}

	public int getRatingsCount() {
		return ratingsCount;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	@Override
	public String toString() {
		return "Book [bookId=" + bookId + ", isbn=" + isbn + ", authors=" + authors + ", originalPublicationYear="
				+ originalPublicationYear + ", originalTitle=" + originalTitle + ", averageRating=" + averageRating
				+ ", ratingsCount=" + ratingsCount + ", imageUrl=" + imageUrl + "]";
	}
	
	

}
