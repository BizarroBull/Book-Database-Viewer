import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

import javax.swing.JLabel;

class BookList
{
	// private final int INDEX_BOOKLIST_NUMBER =		0;
	private final int INDEX_BOOKLIST_DATECOMPLETED =	1;
	private final int INDEX_BOOKLIST_TITLE =			2;
	private final int INDEX_BOOKLIST_SERIESTITLE =		3;
	private final int INDEX_BOOKLIST_AUTHOR =			4;
	private final int INDEX_BOOKLIST_PAGES =			5;
	private final int INDEX_BOOKLIST_EDITION =			6;
	private final int INDEX_BOOKLIST_MYRATING =			7;
	private final int INDEX_BOOKLIST_GOODREADSLINK =	8;
	private final int INDEX_BOOKLIST_IDENTIFIER =		9;
	private final int INDEX_BOOKLIST_ISCOLLECTION =		10;
	private final int INDEX_BOOKLIST_COLLECTIONTITLE =	11;
	private final int INDEX_BOOKLIST_DATESTARTED =		12;
	private final int INDEX_BOOKLIST_IMAGE =			13;

	Path m_PathToTSVFile;
	List<String> m_ListLinesOfText;
	String m_LineOfText;

	JLabel m_LabelListener = null;

	// -------------------------------------------------------------------------

	public void AddLabelListener( JLabel lbl_statusbar )
	{
		m_LabelListener = lbl_statusbar;
	}

	// -------------------------------------------------------------------------

	int ReadFile( String sFilename )
	{
		// Read a file.
		// Return the number of entries found.

		try
		{
			m_PathToTSVFile = Paths.get( sFilename );

			if( !Files.exists(m_PathToTSVFile) )
			{
				if( null != m_LabelListener )
					m_LabelListener.setText( "Error: " + sFilename + " doesn't exist" );
			}
			else
			{
				if( null != m_LabelListener )
					m_LabelListener.setText( "Successfully read file " + sFilename );
			}
		}
		catch( InvalidPathException exc )
		{
			if( null != m_LabelListener )
				m_LabelListener.setText( "Error reading file " + sFilename );

			System.err.println( "ReadFile exception: " + exc.getMessage() );

			return 0;
		}

		// We look at each line of the file.

		int nEntries = 0;
		try
		{
			m_ListLinesOfText = Files.readAllLines( m_PathToTSVFile );

			for( int i=0; i<m_ListLinesOfText.size(); i++ )
			{
				String sBookInfo = m_ListLinesOfText.get(i);
				String [] pItemList = sBookInfo.split( "\t" );

				if( pItemList.length > INDEX_BOOKLIST_TITLE )
				{
					String sTitle = pItemList[ INDEX_BOOKLIST_TITLE ];
	
					if( !sTitle.isEmpty() )
					{
						// We found a valid book entry in the CSV file.

						nEntries++;
					}
				}
			}
		}
		catch( IOException exc )
		{
			System.err.println( "ReadFile exception: " + exc.getMessage() );
		}

		return nEntries;
	}

	// -------------------------------------------------------------------------

	Book GetBookFromList( int nEntry )
	{
		String sReader = "";
		LocalDate dateCompleted = null;
		String sTitle = "";
		String sAuthor = "";
		String sSeriesTitle = "";
		int nPages = 0;
		String sEdition = "";
		int nMyRating = 0;
		String sGoodReadsLink = "";
		String sBookIdentifier = "";
		boolean bIsCollection = false;
		String sCollectionTitle = "";
		LocalDate dateStarted = null;
		String sCoverImageName = "";

		try
		{
			m_ListLinesOfText = Files.readAllLines( m_PathToTSVFile );

			String sBookInfo = m_ListLinesOfText.get( nEntry );
			String [] pItemList = sBookInfo.split( "\t" );

			if( pItemList.length > INDEX_BOOKLIST_ISCOLLECTION )
			{
				try
				{
					dateCompleted = LocalDate.parse( pItemList[ INDEX_BOOKLIST_DATECOMPLETED ] );
				}
				catch( DateTimeParseException exc )
				{
					System.err.println( "Import error: invalid date completed for entry " + nEntry );
					return null;
				}

				sTitle = pItemList[ INDEX_BOOKLIST_TITLE ];

				if( sTitle.equals( "" ) )
				{
					System.err.println( "Import error: empty title for entry " + nEntry );
					return null;
				}

				sSeriesTitle = pItemList[ INDEX_BOOKLIST_SERIESTITLE ];
				sAuthor = pItemList[ INDEX_BOOKLIST_AUTHOR ];
				sEdition = pItemList[ INDEX_BOOKLIST_EDITION ];

				try
				{
					nPages = Integer.valueOf( pItemList[ INDEX_BOOKLIST_PAGES ] );
				}
				catch( NumberFormatException exc )
				{
					nPages = 0;
					System.err.println( "Import warning: invalid pages for entry " + nEntry + ", string was \"" + pItemList[ INDEX_BOOKLIST_PAGES ] + "\"" );
				}

				try
				{
					nMyRating = Integer.valueOf( pItemList[ INDEX_BOOKLIST_MYRATING ] );
				}
				catch( NumberFormatException exc )
				{
					System.err.println( "Import warning: invalid rating for entry " + nEntry + ", string was \"" + pItemList[ INDEX_BOOKLIST_MYRATING ] + "\"" );
					nMyRating = 0;
				}

				sGoodReadsLink = pItemList[ INDEX_BOOKLIST_GOODREADSLINK ];
				sBookIdentifier = pItemList[ INDEX_BOOKLIST_IDENTIFIER ];

				if( pItemList[ INDEX_BOOKLIST_ISCOLLECTION ].equals( "TRUE") )
					bIsCollection = true;

				if( pItemList.length > INDEX_BOOKLIST_COLLECTIONTITLE )
				{
					sCollectionTitle = pItemList[ INDEX_BOOKLIST_COLLECTIONTITLE ];

					if( pItemList.length > INDEX_BOOKLIST_DATESTARTED )
					{
						try
						{
							dateStarted = LocalDate.parse( pItemList[ INDEX_BOOKLIST_DATESTARTED ] );
						}
						catch( DateTimeParseException exc )
						{
							System.err.println( "Import warning: invalid date started for entry " + nEntry );
						}

						if( pItemList.length > INDEX_BOOKLIST_IMAGE )
						{
							sCoverImageName = pItemList[ INDEX_BOOKLIST_IMAGE ];
						}
					}
				}
			}
			else
			{
				System.err.println( "Import error: less columns than minimum of " + INDEX_BOOKLIST_IDENTIFIER + " for entry " + nEntry );
				return null;
			}
		}
		catch( IOException exc )
		{
			System.err.println( "GetBookFromList exception: " + exc.getMessage() );
		}

		Book book = new Book(	sReader, dateCompleted,
								sTitle, sSeriesTitle, sAuthor, nPages, sEdition,
								nMyRating, sGoodReadsLink, sBookIdentifier,
								bIsCollection, sCollectionTitle,
								dateStarted, sCoverImageName );

		return book;
	}

}
