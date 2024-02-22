import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class DatabasePostgreSQL
{
	Connection m_Conn = null;

	// ------------------------------------------------------------------------
	// connect
	// ------------------------------------------------------------------------

	//
	// Connect to the PostgreSQL database
	//
	// @return a Connection object
	//

	public Connection connect( String sURL, String sUser, String sPassword )
	{
		try
		{
			m_Conn = DriverManager.getConnection( sURL, sUser, sPassword );
			System.out.println( "Connected to the PostgreSQL server successfully." );
		}
		catch (SQLException e)
		{
			System.out.println( e.getMessage() );
		}

		return m_Conn;
	}

	// ------------------------------------------------------------------------
	// Search
	// ------------------------------------------------------------------------

	public ResultSet Search( String sReader, String sDateCompletedStart, String sDateCompletedEnd, String sAuthor, String sTitle, boolean bIgnoreCollections, boolean bIgnoreCollectedStories )
	{
		LocalDate dateCompletedStart = null;
		LocalDate dateCompletedEnd = null;
		ResultSet rs = null;

		String sSQL = "SELECT "
				
						+ "\"Reader\", \"Date Completed\", \"Title\", \"Series Title\", "
						+ "\"Author\", \"Pages\", \"Edition\", "
						+ "\"My Rating\", \"Good Reads Link\", \"Book Identifier\", "
						+ "\"IsCollection\", \"Collection Title\", "
						+ "\"Date Started\", \"Cover Image Name\" "

						+ "FROM \"BooksRead\" "
						+ "WHERE \"Reader\" = ? "
							+ "AND \"Author\" LIKE ? "
							+ "AND \"Title\" LIKE ? ";

		if( bIgnoreCollections )
		{
			// Ignore collections by requiring the is collection boolean is false.
			// Use coalesce to treat a null as FALSE.

			sSQL += "AND coalesce( \"IsCollection\", FALSE) = FALSE ";
		}

		if( bIgnoreCollectedStories )
		{
			// Ignore collected stories by requiring the collection title is empty.
			// Use coalesce to treat a null as an empty string.

			sSQL += "AND coalesce( \"Collection Title\", '') = '' ";
		}

		if( !sDateCompletedStart.isEmpty() )
		{
			sSQL += "AND \"Date Completed\" >= ? ";
			dateCompletedStart = LocalDate.parse( sDateCompletedStart );
		}

		if( !sDateCompletedEnd.isEmpty() )
		{
			sSQL += "AND \"Date Completed\" <= ? ";
			dateCompletedEnd = LocalDate.parse( sDateCompletedEnd );
		}

		sSQL += "ORDER BY \"Date Completed\" ";

		if( null == m_Conn )
			return null;

		try
		{
			int nIndex = 4;
			PreparedStatement pstmt = m_Conn.prepareStatement( sSQL );

			pstmt.setString( 1, sReader );
			pstmt.setString( 2, "%" + sAuthor + "%" );
			pstmt.setString( 3, "%" + sTitle + "%" );

			if( !sDateCompletedStart.isEmpty() )
			{
				pstmt.setObject( nIndex, dateCompletedStart );
				nIndex++;
			}

			if( !sDateCompletedEnd.isEmpty() )
			{
				pstmt.setObject( nIndex, dateCompletedEnd );
			}

			rs = pstmt.executeQuery();
		}
		catch (SQLException e)
		{
			System.out.print( "Search: " );
			System.out.println( e.getMessage() );
		}

		return rs;
	}

	// ------------------------------------------------------------------------
	// Search
	// ------------------------------------------------------------------------

	public ResultSet SearchByTitle( String sTitle )
	{
		ResultSet rs = null;

		String SQL = "SELECT "

						+ "\"Reader\", \"Date Completed\", \"Title\", \"Series Title\", "
						+ "\"Author\", \"Pages\", \"Edition\", "
						+ "\"My Rating\", \"Good Reads Link\", \"Book Identifier\", "
						+ "\"IsCollection\", \"Collection Title\", "
						+ "\"Date Started\", \"Cover Image Name\" "

						+ "FROM \"BooksRead\" "
						+ "WHERE \"Title\" = ? "
						+ "ORDER BY \"Date Completed\"";

		if( null == m_Conn )
			return null;

		try
		{
			PreparedStatement pstmt = m_Conn.prepareStatement(SQL);
			
			pstmt.setString( 1, sTitle );
			rs = pstmt.executeQuery();
		}
		catch (SQLException e)
		{
			System.err.print( "SearchByTitle: " );
			System.err.println( e.getMessage() );
		}

		return rs;
	}

	// ------------------------------------------------------------------------
	// AddBook
	// ------------------------------------------------------------------------

	void AddBook( Book book )
	{
		String SQL = "INSERT INTO \"BooksRead\"( "
						+ "\"Reader\", \"Date Completed\", \"Title\", \"Series Title\", "
						+ "\"Author\", \"Pages\", \"Edition\", "
						+ "\"My Rating\", \"Good Reads Link\", \"Book Identifier\", "
						+ "\"IsCollection\", \"Collection Title\", "
						+ "\"Date Started\", \"Cover Image Name\" ) "

						+ "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

		try
		{
			if( null == m_Conn )
				return;

			PreparedStatement pstmt = m_Conn.prepareStatement(SQL);

			LocalDate dateStarted = book.GetDateStarted();
			LocalDate dateCompleted = book.GetDateCompleted();

			pstmt.setString( 1, book.GetReader() );
			pstmt.setObject( 2, dateCompleted );
			pstmt.setString( 3, book.GetTitle() );
			pstmt.setString( 4, book.GetSeriesTitle() );
			pstmt.setString( 5, book.GetAuthor() );
			pstmt.setInt( 6, book.GetPages() );
			pstmt.setString( 7, book.GetEdition() );
			pstmt.setInt( 8, book.GetMyRating() );
			pstmt.setString( 9, book.GetGoodReadsLink() );
			pstmt.setString( 10, book.GetBookIdentifier() );
			pstmt.setBoolean( 11, book.IsCollection() );
			pstmt.setString( 12, book.GetCollectionTitle() );
			pstmt.setObject( 13, dateStarted );
			pstmt.setString( 14, book.GetCoverImageName() );

			pstmt.executeUpdate();
		}
		catch (SQLException e)
		{
			System.err.print( "AddBook: " );
			System.err.println( e.getMessage() );
		}
	}

	// ------------------------------------------------------------------------
	// UpdateBook
	// ------------------------------------------------------------------------

	void UpdateBook( Book book )
	{
		String sSQL;
		PreparedStatement pstmt;

		String sTitle = book.GetTitle();
		LocalDate dateCompleted = book.GetDateCompleted();

		try
		{
			if( null == m_Conn )
				return;

			sSQL = "UPDATE \"BooksRead\""
					+ " SET \"Series Title\" = ? "
					+ " WHERE \"Title\" = ? "
						+ "AND \"Date Completed\" = ?";
			pstmt = m_Conn.prepareStatement( sSQL );
			pstmt.setString( 1, book.GetSeriesTitle() );
			pstmt.setString( 2, sTitle );
			pstmt.setObject( 3, dateCompleted );
			pstmt.executeUpdate();

			sSQL = "UPDATE \"BooksRead\""
					+ " SET \"Author\" = ? "
					+ " WHERE \"Title\" = ? "
						+ "AND \"Date Completed\" = ?";
			pstmt = m_Conn.prepareStatement( sSQL );
			pstmt.setString( 1, book.GetAuthor() );
			pstmt.setString( 2, sTitle );
			pstmt.setObject( 3, dateCompleted );
			pstmt.executeUpdate();

			sSQL = "UPDATE \"BooksRead\""
					+ " SET \"Pages\" = ? "
					+ " WHERE \"Title\" = ? "
						+ "AND \"Date Completed\" = ?";
			pstmt = m_Conn.prepareStatement( sSQL );
			pstmt.setInt( 1, book.GetPages() );
			pstmt.setString( 2, sTitle );
			pstmt.setObject( 3, dateCompleted );
			pstmt.executeUpdate();

			sSQL = "UPDATE \"BooksRead\""
					+ " SET \"Edition\" = ? "
					+ " WHERE \"Title\" = ? "
						+ "AND \"Date Completed\" = ?";
			pstmt = m_Conn.prepareStatement( sSQL );
			pstmt.setString( 1, book.GetEdition() );
			pstmt.setString( 2, sTitle );
			pstmt.setObject( 3, dateCompleted );
			pstmt.executeUpdate();

			sSQL = "UPDATE \"BooksRead\""
					+ " SET \"My Rating\" = ? "
					+ " WHERE \"Title\" = ? "
						+ "AND \"Date Completed\" = ?";
			pstmt = m_Conn.prepareStatement( sSQL );
			pstmt.setInt( 1, book.GetMyRating() );
			pstmt.setString( 2, sTitle );
			pstmt.setObject( 3, dateCompleted );
			pstmt.executeUpdate();

			sSQL = "UPDATE \"BooksRead\""
					+ " SET \"Good Reads Link\" = ? "
					+ " WHERE \"Title\" = ? "
						+ "AND \"Date Completed\" = ?";
			pstmt = m_Conn.prepareStatement( sSQL );
			pstmt.setString( 1, book.GetGoodReadsLink() );
			pstmt.setString( 2, sTitle );
			pstmt.setObject( 3, dateCompleted );
			pstmt.executeUpdate();

			sSQL = "UPDATE \"BooksRead\""
					+ " SET \"Book Identifier\" = ? "
					+ " WHERE \"Title\" = ? "
						+ "AND \"Date Completed\" = ?";
			pstmt = m_Conn.prepareStatement( sSQL );
			pstmt.setString( 1, book.GetBookIdentifier() );
			pstmt.setString( 2, sTitle );
			pstmt.setObject( 3, dateCompleted );
			pstmt.executeUpdate();

			sSQL = "UPDATE \"BooksRead\""
					+ " SET \"IsCollection\" = ? "
					+ " WHERE \"Title\" = ? "
						+ "AND \"Date Completed\" = ?";
			pstmt = m_Conn.prepareStatement( sSQL );
			pstmt.setBoolean( 1, book.IsCollection() );
			pstmt.setString( 2, sTitle );
			pstmt.setObject( 3, dateCompleted );
			pstmt.executeUpdate();

			sSQL = "UPDATE \"BooksRead\""
					+ " SET \"Collection Title\" = ? "
					+ " WHERE \"Title\" = ? "
						+ "AND \"Date Completed\" = ?";
			pstmt = m_Conn.prepareStatement( sSQL );
			pstmt.setString( 1, book.GetCollectionTitle() );
			pstmt.setString( 2, sTitle );
			pstmt.setObject( 3, dateCompleted );
			pstmt.executeUpdate();

			sSQL = "UPDATE \"BooksRead\""
					+ " SET \"Date Started\" = ? "
					+ " WHERE \"Title\" = ? "
						+ "AND \"Date Completed\" = ?";
			pstmt = m_Conn.prepareStatement( sSQL );
			pstmt.setObject( 1, book.GetDateStarted() );
			pstmt.setString( 2, sTitle );
			pstmt.setObject( 3, dateCompleted );
			pstmt.executeUpdate();

			sSQL = "UPDATE \"BooksRead\""
			+ " SET \"Cover Image Name\" = ? "
			+ " WHERE \"Title\" = ? "
				+ "AND \"Date Completed\" = ?";
			pstmt = m_Conn.prepareStatement( sSQL );
			pstmt.setString( 1, book.GetCoverImageName() );
			pstmt.setString( 2, sTitle );
			pstmt.setObject( 3, dateCompleted );
			pstmt.executeUpdate();
		}
		catch (SQLException e)
		{
			System.err.print( "UpdateBook: " );
			System.err.println( e.getMessage() );
		}
	}
}
