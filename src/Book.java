import java.time.LocalDate;

public class Book
{
	private LocalDate m_dateCompleted;
	private String m_sTitle;
	private String m_sSeriesTitle;
	private String m_sAuthor;
	private int m_nPages;
	private String m_sEdition;
	private int m_nMyRating;
	private String m_sGoodReadsLink;
	private String m_sBookIdentifier;
	private boolean m_bIsCollection;
	private String m_sCollectionTitle;
	private LocalDate m_dateStarted;
	private String m_sCoverImageName;

	private String m_sReader;

	Book(	String sReader, LocalDate dateCompleted,
			String sTitle, String sSeriesTitle, String sAuthor,
			int nPages, String sEdition, int nMyRating,
			String sGoodReadsLink, String sBookIdentifier,
			boolean bIsCollection, String sCollectionTitle,
			LocalDate dateStarted, String sCoverImageName )
	{
		m_sReader = sReader;
		m_dateCompleted = dateCompleted;
		m_sTitle = sTitle;
		m_sSeriesTitle = sSeriesTitle;
		m_sAuthor = sAuthor;
		m_nPages = nPages;
		m_sEdition = sEdition;
		m_nMyRating = nMyRating;
		m_sGoodReadsLink = sGoodReadsLink;
		m_sBookIdentifier = sBookIdentifier;
		m_bIsCollection = bIsCollection;
		m_sCollectionTitle = sCollectionTitle;
		m_dateStarted = dateStarted;
		m_sCoverImageName = sCoverImageName;
	}

	String GetReader() { return m_sReader; }
	LocalDate GetDateCompleted() { return m_dateCompleted; }
	String GetTitle() { return m_sTitle; }
	String GetSeriesTitle() { return m_sSeriesTitle; }
	String GetAuthor() { return m_sAuthor; }
	int GetPages() { return m_nPages; }
	String GetEdition() { return m_sEdition; }
	int GetMyRating() { return m_nMyRating; }
	String GetGoodReadsLink() { return m_sGoodReadsLink; }
	String GetBookIdentifier() { return m_sBookIdentifier; }
	boolean IsCollection() { return m_bIsCollection; }
	String GetCollectionTitle() { return m_sCollectionTitle; }
	LocalDate GetDateStarted() { return m_dateStarted; }
	String GetCoverImageName() { return m_sCoverImageName; }
}
