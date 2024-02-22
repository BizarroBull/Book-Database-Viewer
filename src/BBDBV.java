import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JScrollPane;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.table.*;
import javax.swing.table.DefaultTableModel;

import org.ini4j.Wini;

import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.ButtonGroup;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.SwingConstants;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

import javax.swing.ImageIcon;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import java.time.LocalDate;  
import java.util.Calendar;
import java.awt.FlowLayout;
import javax.swing.border.LineBorder;
import java.awt.Color;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;


public class BBDBV extends JFrame
{
	private static final long serialVersionUID = 1L;

	private final String m_sTitle = "Bull's Book Database Viewer v1.0.0";

	private final static String m_sINIFilename = "BBDBV.ini";

	private final String m_sReaderRyan		= "Ryan";
	// private String m_sReaderAlexandra	= "Alexandra";
	// private String m_sReaderOwen			= "Owen";
	// private String m_sReaderTessa		= "Tessa";
	// private String m_sReaderDillon		= "Dillon";

	public class MyTableModel extends DefaultTableModel
	{
		private static final long serialVersionUID = 1L;

	    @Override 
	    public boolean isCellEditable(int row, int column)
	    {
			if(	m_nColumnNumNumber == column ||
				m_nColumnNumDateCompleted == column ||
				m_nColumnNumTitle == column )
			{
				return false;
			}
			else
			{
				return true;
			}
	    }
	}

	private final String m_sColumnNameNumber = "#";
	private final String m_sColumnNameDateCompleted = "Date Completed";
	private final String m_sColumnNameTitle = "Title";
	private final String m_sColumnNameSeriesTitle = "Series Title";
	private final String m_sColumnNameAuthor = "Author";
	private final String m_sColumnNamePages = "Pages";
	private final String m_sColumnNameEdition = "Edition";
	private final String m_sColumnNameMyRating = "MyRating";
	private final String m_sColumnNameGoodReadsLink = "Good Reads Link";
	private final String m_sColumnNameBookIdentifier = "Book Identifer";
	private final String m_sColumnNameIsCollection = "Is Collection?";
	private final String m_sColumnNameCollectionTitle = "Collection Title";
	private final String m_sColumnNameDateStarted = "Date Started";
	private final String m_sColumnNameBookCoverImage = "Book Cover Image";

	private final int m_nColumnNumNumber = 0;
	private final int m_nColumnNumDateCompleted = 1;
	private final int m_nColumnNumTitle = 2;
	private final int m_nColumnNumSeriesTitle = 3;
	private final int m_nColumnNumAuthor = 4;
	private final int m_nColumnNumPages = 5;
	private final int m_nColumnNumEdition = 6;
	private final int m_nColumnNumMyRating = 7;
	private final int m_nColumnNumGoodReadsLink = 8;
	private final int m_nColumnNumBookIdentifier = 9;
	private final int m_nColumnNumIsCollection = 10;
	private final int m_nColumnNumCollectionTitle = 11;
	private final int m_nColumnNumDateStarted = 12;
	private final int m_nColumnNumCoverImageName = 13;

	private int m_nColumnWidthNumber = 5;
	private int m_nColumnWidthDateCompleted = 20;
	private int m_nColumnWidthTitle = 100;
	private int m_nColumnWidthDateStarted = 20;

	private static DatabasePostgreSQL m_DBBooks = new DatabasePostgreSQL();
	private static JLabel m_lbl_StatusBar;

	private int m_nBookListCoverWidth = 258;
	private int m_nBookListCoverHeight = 273;
	private static String m_sBookCoverDirectory;

	private JPanel contentPane;
	private JScrollPane m_ScrollPane;
	private JTable m_TableResults;
    private DefaultTableModel m_TableModel;
	private JTextField textField_DateStarted;
	private JTextField textField_DateCompleted;
	private JTextField textField_Title;
	private JTextField textField_Author;
	private JTextField textField_GoodReadsLink;
	private final ButtonGroup buttonGroup_Edition = new ButtonGroup();
	private JTextField textField_SearchDateCompletedStart;
	private JTextField textField_SearchDateCompletedEnd;
	private JTextField textField_SearchAuthor;
	private JTextField textField_SearchTitle;
	private JCheckBox chckbx_SelectBookIdentifier;

	private enum Column {
	    NUMBER, DATESTARTED, DATECOMPLETED, TITLE, AUTHOR, EDITION,
	    PAGES, MYRATING, GOODREADSLINK, BOOKIDENTIFIER
	}

	private JSpinner spinner_Pages;
	private JPanel panel_BookListCover;
	private JTextField textField_CoverImageName;
	private JLabel m_picLabel;
	private JTextField textField_SearchTotalBooks;
	private JTextField textField_SearchTotalPages;
	private JTextField textField_Search5StarRatings;
	private JTextField textField_FileName;
	
	private File m_ImportFile;
	private JTable m_TableImportNew;
    private DefaultTableModel m_TableImportNewModel;
	private JTable m_TableImportDuplicate;
    private DefaultTableModel m_TableImportDuplicateModel;
    private JTextField textField_Series;
    private JTextField textField_BookIdentifier;
    private JTextField textField_CollectionTitle;
    private final ButtonGroup buttonGroup_CollectionDetail = new ButtonGroup();
    private JRadioButton rdbtn_NotACollection;
    private JRadioButton rdbtn_Collection;
    private JRadioButton rdbtn_CollectedStory;
    private JCheckBox chckbx_IgnoreCollectedStories;
    private JCheckBox chckbx_IgnoreCollections;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try
				{
					BBDBV frame = new BBDBV();
					frame.setVisible(true);

					// Read the values from the ini file.

					Wini ini = new Wini(new File( m_sINIFilename ));
					String sURL = ini.get( "login", "url" );
					String sUser = ini.get( "login", "user" );
					String sPassword = ini.get( "login", "password" );
					m_sBookCoverDirectory = ini.get( "images", "directory" );

					m_DBBooks.connect( sURL, sUser, sPassword );

					m_lbl_StatusBar.setText( "Connected to Bull's Book Database ");
				}
				catch (Exception e)
				{
					m_lbl_StatusBar.setText( "Error: Couldn't connect to Bull's Book Database ");

					e.printStackTrace();
				}
			}
		});
	}

	// ------------------------------------------------------------------------
	// TableShowColumn
	// ------------------------------------------------------------------------
	void TableShowColumn( Column col )
	{
		int nColumnMove = 0;

		switch( col )
		{
		case NUMBER:
			m_TableModel.addColumn( m_sColumnNameNumber );
			break;
		case DATESTARTED:
			m_TableModel.addColumn( m_sColumnNameDateStarted );
			break;
		case DATECOMPLETED:
			m_TableModel.addColumn( m_sColumnNameDateCompleted );
			break;
		case TITLE:
			m_TableModel.addColumn( m_sColumnNameTitle );
			m_TableResults.moveColumn( m_TableResults.getColumnCount()-1, m_TableResults.getColumnCount()-2);
			break;
		case AUTHOR:
			m_TableModel.addColumn( m_sColumnNameAuthor );
			break;
		case EDITION:
			m_TableModel.addColumn( m_sColumnNameEdition );
			break;
		case PAGES:
			m_TableModel.addColumn( m_sColumnNamePages );
			break;
		case MYRATING:
			m_TableModel.addColumn( m_sColumnNameMyRating );
			break;
		case GOODREADSLINK:
			m_TableModel.addColumn( m_sColumnNameGoodReadsLink );
			if( chckbx_SelectBookIdentifier.isSelected() )
				nColumnMove++;
			m_TableResults.moveColumn( m_TableResults.getColumnCount()-1, m_TableResults.getColumnCount()-1-nColumnMove);
			break;
		case BOOKIDENTIFIER:
			m_TableModel.addColumn( m_sColumnNameBookIdentifier );
			break;
		}
	}

	// ------------------------------------------------------------------------
	// TableHideColumn
	// ------------------------------------------------------------------------
	void TableHideColumn( Column col )
	{
		String sColumnName = "";

		// Get the name of the column we want to hide.

		switch( col )
		{
		case NUMBER:
			sColumnName = m_sColumnNameNumber;
			break;
		case DATESTARTED:
			sColumnName = m_sColumnNameDateStarted;
			break;
		case DATECOMPLETED:
			sColumnName = m_sColumnNameDateCompleted;
			break;
		case TITLE:
			sColumnName = m_sColumnNameTitle;
			break;
		case AUTHOR:
			sColumnName = m_sColumnNameAuthor;
			break;
		case EDITION:
			sColumnName = m_sColumnNameEdition;
			break;
		case PAGES:
			sColumnName = m_sColumnNamePages;
			break;
		case MYRATING:
			sColumnName = m_sColumnNameMyRating;
			break;
		case GOODREADSLINK:
			sColumnName = m_sColumnNameGoodReadsLink;
			break;
		case BOOKIDENTIFIER:
			sColumnName = m_sColumnNameBookIdentifier;
			break;
		}

		// Find the column with the name we want.
		TableColumnModel tblCols = m_TableResults.getColumnModel();
        for( int i = 0; i < tblCols.getColumnCount(); i++ )
        {
         	if( tblCols.getColumn(i).getHeaderValue() == sColumnName )
        	{
    			System.out.println( "found column" );
       		//b 			m_TableModel.addColumn( sColumnNameBookIdentifier );

        		// Remove the column.
        		tblCols.removeColumn( tblCols.getColumn( i ) );
        		break;
        	}
        }

		
	}

	// ------------------------------------------------------------------------
	// GetScaledImage
	// ------------------------------------------------------------------------

	private ImageIcon GetScaledImage( String sFilename, int nMaxWidth, int nMaxHeight )
	{
	    int newHeight = 0, newWidth = 0;        // Variables for the new height and width
	    int priorHeight = 0, priorWidth = 0;
	    BufferedImage image = null;
	    ImageIcon sizeImage;

	    try
	    {
	    	image = ImageIO.read( new File( sFilename ) );        // get the image

		    sizeImage = new ImageIcon(image);
	
		    if(sizeImage != null)
		    {
		        priorHeight = sizeImage.getIconHeight(); 
		        priorWidth = sizeImage.getIconWidth();
		    }
	
		    // Calculate the correct new height and width
		    if((float)priorHeight/(float)priorWidth > (float)nMaxHeight/(float)nMaxWidth)
		    {
		        newHeight = nMaxHeight;
		        newWidth = (int)(((float)priorWidth/(float)priorHeight)*(float)newHeight);
		    }
		    else 
		    {
		        newWidth = nMaxWidth;
		        newHeight = (int)(((float)priorHeight/(float)priorWidth)*(float)newWidth);
		    }
	
	
		    // Resize the image
	
		    // 1. Create a new Buffered Image and Graphic2D object
		    BufferedImage resizedImg = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
		    Graphics2D g2 = resizedImg.createGraphics();
	
		    // 2. Use the Graphic object to draw a new image to the image in the buffer
		    g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		    g2.drawImage(image, 0, 0, newWidth, newHeight, null);
		    g2.dispose();
	
		    // 3. Convert the buffered image into an ImageIcon for return
		    return (new ImageIcon(resizedImg));
	    }
	    catch (Exception e)
	    {
            // e.printStackTrace();
 	    	System.err.println( "Couldn't open image " + sFilename );

	    	return null;
	    }
	}

	// ------------------------------------------------------------------------
	// ExportTable
	//
	// Export the data in m_Table to a comma separated value text file.
	// Filename will be data_[current date]_[current time].csv
	// ------------------------------------------------------------------------
	void ExportTable()
	{
		try
		{
			FileWriter myWriter = new FileWriter("filename.txt");
			myWriter.write("Files in Java might be tricky, but it is fun enough!");
			myWriter.close();
			System.out.println("Successfully wrote to the file.");
		}
		catch ( IOException e )
		{
			System.out.println("An error occurred.");
			e.printStackTrace();
		}
	}

	/**
	 * Create the frame.
	 */
	public BBDBV()
	{
		setTitle( m_sTitle );
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1600, 900);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		// Generate our date strings.

		Calendar cal = Calendar.getInstance();
		String sYearStart = cal.get( Calendar.YEAR ) + "-01-01";
		String sYearEnd = cal.get( Calendar.YEAR ) + "-12-31";
		String sCurrentDate = cal.get( Calendar.YEAR ) + "-";

		if( ( cal.get( Calendar.MONTH ) + 1 ) < 10 )
			sCurrentDate += "0";
		sCurrentDate += ( cal.get( Calendar.MONTH ) + 1 ) + "-";

		if( cal.get( Calendar.DAY_OF_MONTH ) < 10 )
			sCurrentDate += "0";
		sCurrentDate += cal.get( Calendar.DAY_OF_MONTH );

		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBounds(0, 0, 1584, 835);
		contentPane.add(tabbedPane);

		m_TableModel = new MyTableModel();
		m_TableModel.addColumn( m_sColumnNameNumber );
		m_TableModel.addColumn( m_sColumnNameDateCompleted );
		m_TableModel.addColumn( m_sColumnNameTitle );
		m_TableModel.addColumn( m_sColumnNameSeriesTitle );
		m_TableModel.addColumn( m_sColumnNameAuthor );
		m_TableModel.addColumn( m_sColumnNamePages );
		m_TableModel.addColumn( m_sColumnNameEdition );
		m_TableModel.addColumn( m_sColumnNameMyRating );
		m_TableModel.addColumn( m_sColumnNameGoodReadsLink );
		m_TableModel.addColumn( m_sColumnNameBookIdentifier );
		m_TableModel.addColumn( m_sColumnNameIsCollection );
		m_TableModel.addColumn( m_sColumnNameCollectionTitle );
		m_TableModel.addColumn( m_sColumnNameDateStarted );
		m_TableModel.addColumn( m_sColumnNameBookCoverImage );
		
		JPanel panel_BookList = new JPanel();
		tabbedPane.addTab("Book List", null, panel_BookList, null);
		panel_BookList.setLayout(null);

		m_ScrollPane = new JScrollPane();
		m_ScrollPane.setEnabled(false);
		m_ScrollPane.setBounds(10, 11, 1559, 468);
		panel_BookList.add( m_ScrollPane );
		m_TableResults = new JTable( m_TableModel );
		m_TableResults.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e)
			{
				if( MouseEvent.BUTTON1 == e.getButton() )
				{
					// Get the book cover image name for the row that was selected.
					int nRow = m_TableResults.getSelectedRow();
					if( nRow >= 0 )
					{
						Object tableValue;
						String sYearCompleted = "";
						String sCoverImageName;

						// Remove the previous image.

						panel_BookListCover.remove( m_picLabel );

						tableValue = m_TableModel.getValueAt( nRow, m_nColumnNumDateCompleted );
						if( null != tableValue )
						{
							sYearCompleted = tableValue.toString().substring(0, 4);
						}

						tableValue = m_TableModel.getValueAt( nRow, m_nColumnNumCoverImageName );
						if( null != tableValue && !tableValue.toString().equals( "" ) )
						{
							sCoverImageName = m_sBookCoverDirectory + File.separator + sYearCompleted + File.separator + tableValue.toString();
							ImageIcon imageIcon = GetScaledImage( sCoverImageName, m_nBookListCoverWidth, m_nBookListCoverHeight );
							m_picLabel = new JLabel( imageIcon );
							panel_BookListCover.add( m_picLabel );
						}

						// Repaint.

						panel_BookListCover.revalidate();
						panel_BookListCover.repaint();
					}
				}
			}
		});
		
		m_TableResults.getColumnModel().getColumn( m_nColumnNumNumber ).setPreferredWidth( m_nColumnWidthNumber );
		m_TableResults.getColumnModel().getColumn( m_nColumnNumDateCompleted ).setPreferredWidth( m_nColumnWidthDateCompleted );
		m_TableResults.getColumnModel().getColumn( m_nColumnNumTitle ).setPreferredWidth( m_nColumnWidthTitle );
		m_TableResults.getColumnModel().getColumn( m_nColumnNumSeriesTitle ).setPreferredWidth( m_nColumnWidthTitle );
		m_TableResults.getColumnModel().getColumn( m_nColumnNumDateStarted ).setPreferredWidth( m_nColumnWidthDateStarted );

		m_ScrollPane.setViewportView( m_TableResults );

		JButton btn_Search = new JButton("Search");
		btn_Search.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				// Clear the existing table.

				m_TableModel.setRowCount( 0 );
				m_lbl_StatusBar.setText( "" );

				String sSearchDateCompletedStart = textField_SearchDateCompletedStart.getText();
				String sSearchDateCompletedEnd = textField_SearchDateCompletedEnd.getText();
				String sSearchAuthor = textField_SearchAuthor.getText();
				String sSearchTitle = textField_SearchTitle.getText();

				int nNumber = 0;
				int nTotalPages = 0;
				int nTotal5StarRatings = 0;

				try
				{
					ResultSet rs = m_DBBooks.Search(	m_sReaderRyan, sSearchDateCompletedStart, sSearchDateCompletedEnd,
														sSearchAuthor, sSearchTitle,
														chckbx_IgnoreCollections.isSelected(), chckbx_IgnoreCollectedStories.isSelected() );

					if( null != rs )
					{
						while( rs.next() )
						{
							nNumber++;

							String sDateStarted = "";
							LocalDate dateStarted = rs.getObject( "Date Started", LocalDate.class );
							if( null != dateStarted )
								sDateStarted = dateStarted.toString();
	
							int nPages = rs.getInt( "Pages" );
							nTotalPages += nPages;

							int nRating = rs.getInt( "My Rating");
							if( 5 == nRating )
								nTotal5StarRatings++;

							String sIsCollection = "FALSE";
							if( rs.getBoolean( "IsCollection" ) )
								sIsCollection = "TRUE";

							String sDateCompleted = "";
							LocalDate dateCompleted = rs.getObject( "Date Completed", LocalDate.class );
							if( null != dateCompleted )
								sDateCompleted = dateCompleted.toString();
	
							String[] item = {
									String.valueOf( nNumber ),
									sDateCompleted,
									rs.getString("Title"),
									rs.getString( "Series Title" ),
									rs.getString("Author"),
									String.valueOf( nPages ),
									rs.getString("Edition"),
									String.valueOf( nRating ),
									rs.getString("Good Reads Link"),
									rs.getString("Book Identifier"),
									sIsCollection,
									rs.getString("Collection Title"),
									sDateStarted,
									rs.getString("Cover Image Name")
									};
	
							m_TableModel.insertRow( nNumber-1, item );
						}
					}

					m_lbl_StatusBar.setText( "Search complete." );
				}
				catch (SQLException exc)
				{
					m_lbl_StatusBar.setText( "Search error." );
					System.err.println(exc.getMessage());
				}

				// Display the search summary result.

				textField_SearchTotalBooks.setText( Integer.toString( nNumber ) );
				textField_SearchTotalPages.setText( Integer.toString( nTotalPages ) );
				textField_Search5StarRatings.setText( Integer.toString( nTotal5StarRatings ) );
			}
		});
		btn_Search.setBounds(1480, 773, 89, 23);
		panel_BookList.add(btn_Search);
		
		JButton btn_Export = new JButton("Export");
		btn_Export.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				ExportTable();
			}
		});
		btn_Export.setBounds(1200, 773, 89, 23);
		panel_BookList.add(btn_Export);

		JButton btn_UpdateSelectedRow = new JButton("Update Selected Row");
		btn_UpdateSelectedRow.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				int nRow = m_TableResults.getSelectedRow();
				if( nRow >= 0 )
				{
					String sReader = m_sReaderRyan;

					String sDateCompleted = m_TableModel.getValueAt( nRow, m_nColumnNumDateCompleted ).toString();
					LocalDate dateCompleted = null;
					if( false == sDateCompleted.isEmpty() )
						dateCompleted = LocalDate.parse( sDateCompleted );

					String sTitle = m_TableModel.getValueAt( nRow, m_nColumnNumTitle ).toString();
					String sSeriesTitle = m_TableModel.getValueAt( nRow, m_nColumnNumSeriesTitle ).toString();
					String sAuthor = m_TableModel.getValueAt( nRow, m_nColumnNumAuthor ).toString();
					int nPages = Integer.parseInt( m_TableModel.getValueAt( nRow, m_nColumnNumPages ).toString() );
					String sEdition = m_TableModel.getValueAt( nRow, m_nColumnNumEdition ).toString();
					int nMyRating = Integer.parseInt( m_TableModel.getValueAt( nRow, m_nColumnNumMyRating ).toString() );
					String sGoodReadsLink = m_TableModel.getValueAt( nRow, m_nColumnNumGoodReadsLink ).toString();
					String sBookIdentifier = m_TableModel.getValueAt( nRow, m_nColumnNumBookIdentifier ).toString();

					boolean bIsCollection = false;
					if( m_TableModel.getValueAt( nRow, m_nColumnNumIsCollection ).toString().equals( "TRUE" ) )
						bIsCollection = true;

					String sCollectionTitle = m_TableModel.getValueAt( nRow, m_nColumnNumCollectionTitle ).toString();

					String sDateStarted = m_TableModel.getValueAt( nRow, m_nColumnNumDateStarted ).toString();
					LocalDate dateStarted = null;
					if( false == sDateStarted.isEmpty() )
						dateStarted = LocalDate.parse( sDateStarted );

					String sCoverImageName = m_TableModel.getValueAt( nRow, m_nColumnNumCoverImageName ).toString();

					Book bookToUpdate = new Book(	sReader, dateCompleted, sTitle, sSeriesTitle,
													sAuthor, nPages, sEdition, nMyRating,
													sGoodReadsLink, sBookIdentifier,
													bIsCollection, sCollectionTitle,
													dateStarted, sCoverImageName );

					m_DBBooks.UpdateBook( bookToUpdate );
					m_lbl_StatusBar.setText( "Update complete." );

				}
				else
				{
					System.out.println( "no row selected" );
				}

			}
		});
		btn_UpdateSelectedRow.setBounds(1299, 773, 171, 23);
		panel_BookList.add(btn_UpdateSelectedRow);
							
		JPanel panel_4 = new JPanel();
		panel_4.setBorder(new TitledBorder(null, "Search Parameters", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_4.setBounds(1200, 507, 369, 255);
		panel_BookList.add(panel_4);
		panel_4.setLayout(null);
		
		JLabel lbl_SearchDateCompletedStart = new JLabel("Date Completed, Start:");
		lbl_SearchDateCompletedStart.setBounds(10, 29, 130, 14);
		panel_4.add(lbl_SearchDateCompletedStart);
		lbl_SearchDateCompletedStart.setHorizontalAlignment(SwingConstants.RIGHT);
		
		textField_SearchDateCompletedStart = new JTextField();
		textField_SearchDateCompletedStart.setBounds(157, 25, 86, 20);
		panel_4.add(textField_SearchDateCompletedStart);
		textField_SearchDateCompletedStart.setColumns(10);
		//int nCurrentYear = java.time.LocalDate.getYear();
		//String sYearStart = String.valueOf( nCurrentYear ) + "-01-01";
		//String sYearEnd = String.valueOf( nCurrentYear ) + "-12-31";

		textField_SearchDateCompletedStart.setText( sYearStart );
		
		JLabel lbl_SearchDateCompletedEnd = new JLabel("Date Completed, End:");
		lbl_SearchDateCompletedEnd.setBounds(10, 56, 130, 14);
		panel_4.add(lbl_SearchDateCompletedEnd);
		lbl_SearchDateCompletedEnd.setHorizontalAlignment(SwingConstants.RIGHT);
		
		textField_SearchDateCompletedEnd = new JTextField();
		textField_SearchDateCompletedEnd.setBounds(157, 52, 86, 20);
		panel_4.add(textField_SearchDateCompletedEnd);
		textField_SearchDateCompletedEnd.setColumns(10);
		textField_SearchDateCompletedEnd.setText( sYearEnd );
		
		JLabel lbl_SearchAuthor = new JLabel("Author Search:");
		lbl_SearchAuthor.setBounds(10, 82, 130, 14);
		panel_4.add(lbl_SearchAuthor);
		lbl_SearchAuthor.setHorizontalAlignment(SwingConstants.RIGHT);
		
		textField_SearchAuthor = new JTextField();
		textField_SearchAuthor.setBounds(157, 78, 86, 20);
		panel_4.add(textField_SearchAuthor);
		textField_SearchAuthor.setColumns(10);
		
		JLabel lbl_SearchTitle = new JLabel("Title Search:");
		lbl_SearchTitle.setBounds(10, 108, 130, 14);
		panel_4.add(lbl_SearchTitle);
		lbl_SearchTitle.setHorizontalAlignment(SwingConstants.RIGHT);
		
		textField_SearchTitle = new JTextField();
		textField_SearchTitle.setBounds(157, 105, 86, 20);
		panel_4.add(textField_SearchTitle);
		textField_SearchTitle.setColumns(10);
		
		chckbx_IgnoreCollections = new JCheckBox("Ignore Collections");
		chckbx_IgnoreCollections.setBounds(10, 129, 233, 23);
		panel_4.add(chckbx_IgnoreCollections);
		
		chckbx_IgnoreCollectedStories = new JCheckBox("Ignore Collected Stories");
		chckbx_IgnoreCollectedStories.setSelected(true);
		chckbx_IgnoreCollectedStories.setBounds(10, 155, 233, 23);
		panel_4.add(chckbx_IgnoreCollectedStories);
		
		JPanel panel_6 = new JPanel();
		panel_6.setBorder(new TitledBorder(null, "Selected Book Cover", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_6.setBounds(10, 490, 278, 306);
		panel_BookList.add(panel_6);
		panel_6.setLayout(null);
																			
		panel_BookListCover = new JPanel();
		panel_BookListCover.setBounds(10, 22, 258, 273);
		panel_6.add(panel_BookListCover);
		panel_BookListCover.setBorder(null);


		m_picLabel = new JLabel( "" );
		panel_BookListCover.add( m_picLabel );
		
		JPanel panel_7 = new JPanel();
		panel_7.setBorder(new TitledBorder(null, "Search Summary", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_7.setBounds(311, 490, 348, 306);
		panel_BookList.add(panel_7);
		panel_7.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("Total Books:");
		lblNewLabel.setBounds(24, 46, 111, 14);
		panel_7.add(lblNewLabel);
		
		JLabel label = new JLabel("Total Pages:");
		label.setBounds(24, 71, 111, 14);
		panel_7.add(label);
		
		JLabel lblNewLabel_4 = new JLabel("5 star ratings:");
		lblNewLabel_4.setBounds(24, 96, 111, 14);
		panel_7.add(lblNewLabel_4);
		
		textField_SearchTotalBooks = new JTextField();
		textField_SearchTotalBooks.setEditable(false);
		textField_SearchTotalBooks.setBounds(153, 43, 86, 20);
		panel_7.add(textField_SearchTotalBooks);
		textField_SearchTotalBooks.setColumns(10);
		
		textField_SearchTotalPages = new JTextField();
		textField_SearchTotalPages.setEditable(false);
		textField_SearchTotalPages.setBounds(153, 68, 86, 20);
		panel_7.add(textField_SearchTotalPages);
		textField_SearchTotalPages.setColumns(10);
		
		textField_Search5StarRatings = new JTextField();
		textField_Search5StarRatings.setEditable(false);
		textField_Search5StarRatings.setBounds(153, 93, 86, 20);
		panel_7.add(textField_Search5StarRatings);
		textField_Search5StarRatings.setColumns(10);

		JPanel panel_NewBook = new JPanel();
		tabbedPane.addTab("New Book", null, panel_NewBook, null);
		panel_NewBook.setLayout(null);
		
		textField_DateStarted = new JTextField();
		textField_DateStarted.setBounds(170, 234, 237, 20);
		panel_NewBook.add(textField_DateStarted);
		textField_DateStarted.setColumns(10);
		textField_DateStarted.setText( sCurrentDate );
		
		JLabel lbl_DateStarted = new JLabel("Date Started:");
		lbl_DateStarted.setHorizontalAlignment(SwingConstants.RIGHT);
		lbl_DateStarted.setBounds(61, 237, 99, 14);
		panel_NewBook.add(lbl_DateStarted);
		
		JLabel lbl_DateCompleted = new JLabel("Date Completed:");
		lbl_DateCompleted.setHorizontalAlignment(SwingConstants.RIGHT);
		lbl_DateCompleted.setBounds(61, 11, 99, 14);
		panel_NewBook.add(lbl_DateCompleted);
		
		textField_DateCompleted = new JTextField();
		textField_DateCompleted.setBounds(170, 8, 237, 20);
		panel_NewBook.add(textField_DateCompleted);
		textField_DateCompleted.setColumns(10);
		textField_DateCompleted.setText( sCurrentDate );
		
		JLabel lblNewLabel_1 = new JLabel("Title:");
		lblNewLabel_1.setHorizontalAlignment(SwingConstants.RIGHT);
		lblNewLabel_1.setBounds(61, 36, 99, 14);
		panel_NewBook.add(lblNewLabel_1);
		
		textField_Title = new JTextField();
		textField_Title.setBounds(170, 33, 237, 20);
		panel_NewBook.add(textField_Title);
		textField_Title.setColumns(10);
		
		JLabel lblNewLabel_2 = new JLabel("Author:");
		lblNewLabel_2.setHorizontalAlignment(SwingConstants.RIGHT);
		lblNewLabel_2.setBounds(63, 86, 97, 14);
		panel_NewBook.add(lblNewLabel_2);
		
		textField_Author = new JTextField();
		textField_Author.setBounds(170, 83, 237, 20);
		panel_NewBook.add(textField_Author);
		textField_Author.setColumns(10);
		
		JLabel lbl_Pages = new JLabel("Pages:");
		lbl_Pages.setHorizontalAlignment(SwingConstants.RIGHT);
		lbl_Pages.setBounds(63, 111, 97, 14);
		panel_NewBook.add(lbl_Pages);
		
		JLabel lbl_MyRating = new JLabel("My Rating:");
		lbl_MyRating.setHorizontalAlignment(SwingConstants.RIGHT);
		lbl_MyRating.setBounds(61, 136, 99, 14);
		panel_NewBook.add(lbl_MyRating);
		
		spinner_Pages = new JSpinner();
		spinner_Pages.setBounds(170, 108, 86, 20);
		panel_NewBook.add(spinner_Pages);
		
		textField_GoodReadsLink = new JTextField();
		textField_GoodReadsLink.setText("https://www.goodreads.com/book/show/");
		textField_GoodReadsLink.setBounds(170, 158, 237, 20);
		panel_NewBook.add(textField_GoodReadsLink);
		textField_GoodReadsLink.setColumns(10);
		
		JLabel lbl_GoodReadsLink = new JLabel("Good Reads Link:");
		lbl_GoodReadsLink.setHorizontalAlignment(SwingConstants.RIGHT);
		lbl_GoodReadsLink.setBounds(61, 161, 99, 14);
		panel_NewBook.add(lbl_GoodReadsLink);
		
		JPanel panel_1 = new JPanel();
		FlowLayout flowLayout_1 = (FlowLayout) panel_1.getLayout();
		flowLayout_1.setAlignment(FlowLayout.LEFT);
		panel_1.setBorder(new TitledBorder(null, "Edition", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_1.setBounds(10, 344, 119, 209);
		panel_NewBook.add(panel_1);
		
		JRadioButton rdbtn_Kindle = new JRadioButton("Kindle");
		panel_1.add(rdbtn_Kindle);
		rdbtn_Kindle.setSelected(true);
		buttonGroup_Edition.add(rdbtn_Kindle);
		
		JRadioButton rdbtn_eBook = new JRadioButton("eBook");
		panel_1.add(rdbtn_eBook);
		buttonGroup_Edition.add(rdbtn_eBook);
		
		JRadioButton rdbtn_Paperback = new JRadioButton("Paperback");
		panel_1.add(rdbtn_Paperback);
		buttonGroup_Edition.add(rdbtn_Paperback);
		
		JRadioButton rdbtn_Hardcover = new JRadioButton("Hardcover");
		panel_1.add(rdbtn_Hardcover);
		buttonGroup_Edition.add(rdbtn_Hardcover);
		
		JCheckBox chckbx_Library = new JCheckBox("Library book?");
		panel_1.add(chckbx_Library);
		
		JButton btn_Save = new JButton("Save");
		btn_Save.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				String sReader = m_sReaderRyan;

				String sDateCompleted = textField_DateCompleted.getText();
				LocalDate dateCompleted = null;
				if( false == sDateCompleted.isEmpty() )
					dateCompleted = LocalDate.parse( sDateCompleted );

				String sTitle = textField_Title.getText();
				String sSeriesTitle = textField_Series.getText();
				String sAuthor = textField_Author.getText();

				int nPages = (Integer)spinner_Pages.getValue();

				String sEdition = "";

				if( rdbtn_Kindle.isSelected() )
					sEdition = "Kindle";
				else if( rdbtn_eBook.isSelected() )
					sEdition = "eBook";
				else if( rdbtn_Paperback.isSelected() )
					sEdition = "Paperback";
				else if( rdbtn_Hardcover.isSelected() )
					sEdition = "Hardcover";

				if( chckbx_Library.isSelected() )
					sEdition += ", Library";

				int nMyRating = (Integer)spinner_Pages.getValue();
				String sGoodReadsLink = textField_GoodReadsLink.getText();
				String sBookIdentifier = textField_BookIdentifier.getText();
				boolean bIsCollection = rdbtn_Collection.isSelected();
				String sCollectionTitle = textField_CollectionTitle.getText();

				String sDateStarted = textField_DateStarted.getText();
				LocalDate dateStarted = null;
				if( false == sDateStarted.isEmpty() )
					dateStarted = LocalDate.parse( sDateStarted );

				String sCoverImageName = textField_CoverImageName.getText();

				// Check the data before adding the book.

				if( sTitle.isEmpty() )
				{
					m_lbl_StatusBar.setText( "Error adding book: Title cannot be blank." );
				}
				else if( rdbtn_CollectedStory.isSelected() && sCollectionTitle.isEmpty() )
				{
					// This is a collected story but there is no collection title.
					
					m_lbl_StatusBar.setText( "Error adding book: This is a collected story but there is no collection title." );
				}
				else
				{
					Book latestBook = new Book( sReader, dateCompleted, sTitle, sSeriesTitle,
												sAuthor, nPages, sEdition, nMyRating,
												sGoodReadsLink, sBookIdentifier,
												bIsCollection, sCollectionTitle, dateStarted, sCoverImageName );
	
					m_DBBooks.AddBook( latestBook );

					m_lbl_StatusBar.setText( "Adding book complete." );

				}
			}
		});

		btn_Save.setBounds(318, 530, 89, 23);
		panel_NewBook.add(btn_Save);

		JPanel panel_5 = new JPanel();
		FlowLayout flowLayout = (FlowLayout) panel_5.getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		panel_5.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, new Color(255, 255, 255), new Color(160, 160, 160)), "Collection Details", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		panel_5.setBounds(153, 344, 138, 209);
		panel_NewBook.add(panel_5);
		
		rdbtn_NotACollection = new JRadioButton("Not a collection");
		buttonGroup_CollectionDetail.add(rdbtn_NotACollection);
		rdbtn_NotACollection.setSelected(true);
		panel_5.add(rdbtn_NotACollection);
		
		rdbtn_Collection = new JRadioButton("Collection");
		buttonGroup_CollectionDetail.add(rdbtn_Collection);
		panel_5.add(rdbtn_Collection);
		
		rdbtn_CollectedStory = new JRadioButton("Collected Story");
		rdbtn_CollectedStory.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				if( rdbtn_CollectedStory.isSelected() )
				{
					textField_CollectionTitle.setEditable( true );
				}
				else
				{
					textField_CollectionTitle.setText( "" );
					textField_CollectionTitle.setEditable( false );
				}
			}
		});
		buttonGroup_CollectionDetail.add(rdbtn_CollectedStory);
		panel_5.add(rdbtn_CollectedStory);
		
		JLabel lbl_CoverImageName = new JLabel("Cover Image Name:");
		lbl_CoverImageName.setHorizontalAlignment(SwingConstants.RIGHT);
		lbl_CoverImageName.setBounds(10, 262, 150, 14);
		panel_NewBook.add(lbl_CoverImageName);
		
		textField_CoverImageName = new JTextField();
		textField_CoverImageName.setColumns(10);
		textField_CoverImageName.setBounds(170, 259, 237, 20);
		panel_NewBook.add(textField_CoverImageName);
		
		JSpinner spinner_MyRating = new JSpinner();
		spinner_MyRating.setModel(new SpinnerNumberModel(1, 1, 5, 1));
		spinner_MyRating.setBounds(170, 133, 86, 20);
		panel_NewBook.add(spinner_MyRating);
		
		JLabel lblNewLabel_7 = new JLabel("Collection Title:");
		lblNewLabel_7.setHorizontalAlignment(SwingConstants.RIGHT);
		lblNewLabel_7.setBounds(49, 212, 111, 14);
		panel_NewBook.add(lblNewLabel_7);
		
		JLabel lblNewLabel_8 = new JLabel("Series Title:");
		lblNewLabel_8.setHorizontalAlignment(SwingConstants.RIGHT);
		lblNewLabel_8.setBounds(63, 61, 97, 14);
		panel_NewBook.add(lblNewLabel_8);
		
		textField_Series = new JTextField();
		textField_Series.setBounds(170, 58, 237, 20);
		panel_NewBook.add(textField_Series);
		textField_Series.setColumns(10);
		
		JLabel lblNewLabel_3 = new JLabel("Book Identifier:");
		lblNewLabel_3.setHorizontalAlignment(SwingConstants.RIGHT);
		lblNewLabel_3.setBounds(49, 186, 111, 14);
		panel_NewBook.add(lblNewLabel_3);
		
		textField_BookIdentifier = new JTextField();
		textField_BookIdentifier.setBounds(170, 183, 237, 20);
		panel_NewBook.add(textField_BookIdentifier);
		textField_BookIdentifier.setColumns(10);
		
		textField_CollectionTitle = new JTextField();
		textField_CollectionTitle.setEditable(false);
		textField_CollectionTitle.setBounds(170, 209, 237, 20);
		panel_NewBook.add(textField_CollectionTitle);
		textField_CollectionTitle.setColumns(10);
		
		JPanel panel_Import = new JPanel();
		tabbedPane.addTab("Import", null, panel_Import, null);
		panel_Import.setLayout(null);
		
		JButton btnNewButton_Browse = new JButton("Browse");
		btnNewButton_Browse.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				JFileChooser fileChooser = new JFileChooser();
				FileNameExtensionFilter filter = new FileNameExtensionFilter( "TSV FILES", "tsv" );
				fileChooser.setFileFilter( filter );
				int nOption = fileChooser.showOpenDialog( panel_Import );
				if( JFileChooser.APPROVE_OPTION == nOption )
				{
					m_ImportFile = fileChooser.getSelectedFile();
					textField_FileName.setText( m_ImportFile.getName() );
				}
			}
		});
		btnNewButton_Browse.setBounds(10, 39, 89, 23);
		panel_Import.add(btnNewButton_Browse);
		
		JLabel lblNewLabel_5 = new JLabel("File name:");
		lblNewLabel_5.setBounds(10, 14, 104, 14);
		panel_Import.add(lblNewLabel_5);
		
		textField_FileName = new JTextField();
		textField_FileName.setBounds(124, 11, 187, 20);
		panel_Import.add(textField_FileName);
		textField_FileName.setColumns(10);
		
		JButton btnNewButton_Import = new JButton("Import");
		btnNewButton_Import.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				// Start by clearing the tables.

				m_TableImportNewModel.setRowCount( 0 );
				m_TableImportDuplicateModel.setRowCount( 0 );

				if( m_ImportFile == null )
				{
					m_lbl_StatusBar.setText( "Error: No file selected." );
				}
				else
				{
					BookList bl = new BookList();
					int nEntries = bl.ReadFile( m_ImportFile.getAbsolutePath() );

					// We look at all the entries and add them to the appropriate table.

					int nNewBooks = 0;
					int nDuplicateBooks = 0;

					// We skip entry 0 which is the column headers so i starts at 1.

					for( int i=1; i<nEntries; i++ )
					{
						Book book = bl.GetBookFromList( i );

						if( null == book )
							continue;

						String sTitle = book.GetTitle();

						String sDateStarted = "";
						LocalDate dateStarted = book.GetDateStarted();
						if( null != dateStarted )
							sDateStarted = dateStarted.toString();

						int nPages = book.GetPages();

						int nRating = book.GetMyRating();

						String sDateCompleted = "";
						LocalDate dateCompleted = book.GetDateCompleted();
						if( null != dateCompleted )
							sDateCompleted = dateCompleted.toString();

						String sIsCollection = "FALSE";
						if( book.IsCollection() )
							sIsCollection = "TRUE";

						String[] item = {
								String.valueOf(i), 
								sDateCompleted,
								sTitle,
								book.GetSeriesTitle(),
								book.GetAuthor(),
								String.valueOf( nPages ),
								book.GetEdition(),
								String.valueOf( nRating ),
								book.GetGoodReadsLink(),
								book.GetBookIdentifier(),
								sIsCollection,
								book.GetCollectionTitle(),
								sDateStarted,
								book.GetCoverImageName()
							};

						// We see if a book with this title and completed date already exist in the database.

						boolean bDuplicate = false;

						try
						{
							ResultSet rs = m_DBBooks.SearchByTitle( sTitle );

							if( null != rs )
							{
								while( rs.next() )
								{
									String sTitleDB = rs.getString( "Title" );
									String sDateCompletedDB = "";
									LocalDate dateCompletedDB = rs.getObject( "Date Completed", LocalDate.class );
									if( null != dateCompletedDB )
										sDateCompletedDB = dateCompletedDB.toString();

									if( sTitle.equals( sTitleDB ) && sDateCompleted.equals( sDateCompletedDB ) )
									{
										// There is an entry in the database with this title and date completed.
										// We consider this a duplicate.

										bDuplicate = true;
										break;
									}
								}
							}
						}
						catch (SQLException exc)
						{
							m_lbl_StatusBar.setText( "Search error while importing." );
							System.err.println(exc.getMessage());
							return;
						}

						if( !bDuplicate )
						{
							m_TableImportNewModel.insertRow( nNewBooks, item );
							nNewBooks++;
						}
						else
						{
							m_TableImportDuplicateModel.insertRow( nDuplicateBooks, item );
							nDuplicateBooks++;
						}
					}

					m_lbl_StatusBar.setText( "Importing file complete." );
				}
			}
		});
		btnNewButton_Import.setBounds(222, 39, 89, 23);
		panel_Import.add(btnNewButton_Import);
		
		JLabel lblNewLabel_6 = new JLabel("New Books Found:");
		lblNewLabel_6.setBounds(10, 93, 147, 14);
		panel_Import.add(lblNewLabel_6);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 118, 1559, 300);
		panel_Import.add(scrollPane);

		// Create our table for imported books that are new.

		m_TableImportNewModel = new DefaultTableModel();
		m_TableImportNewModel.addColumn( m_sColumnNameNumber );
		m_TableImportNewModel.addColumn( m_sColumnNameDateCompleted );
		m_TableImportNewModel.addColumn( m_sColumnNameTitle );
		m_TableImportNewModel.addColumn( m_sColumnNameSeriesTitle );
		m_TableImportNewModel.addColumn( m_sColumnNameAuthor );
		m_TableImportNewModel.addColumn( m_sColumnNamePages );
		m_TableImportNewModel.addColumn( m_sColumnNameEdition );
		m_TableImportNewModel.addColumn( m_sColumnNameMyRating );
		m_TableImportNewModel.addColumn( m_sColumnNameGoodReadsLink );
		m_TableImportNewModel.addColumn( m_sColumnNameBookIdentifier );
		m_TableImportNewModel.addColumn( m_sColumnNameIsCollection );
		m_TableImportNewModel.addColumn( m_sColumnNameCollectionTitle );
		m_TableImportNewModel.addColumn( m_sColumnNameDateStarted );
		m_TableImportNewModel.addColumn( m_sColumnNameBookCoverImage );

		m_TableImportNew = new JTable( m_TableImportNewModel );
		scrollPane.setViewportView( m_TableImportNew );

		JLabel lblNewLabel_6_1 = new JLabel("Duplicate Books Found:");
		lblNewLabel_6_1.setBounds(10, 471, 147, 14);
		panel_Import.add(lblNewLabel_6_1);
		
		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(10, 496, 1559, 250);
		panel_Import.add(scrollPane_1);
		
		// Create our table for imported books that are duplicates.

		m_TableImportDuplicateModel = new DefaultTableModel();
		m_TableImportDuplicateModel.addColumn( m_sColumnNameNumber );
		m_TableImportDuplicateModel.addColumn( m_sColumnNameDateCompleted );
		m_TableImportDuplicateModel.addColumn( m_sColumnNameTitle );
		m_TableImportDuplicateModel.addColumn( m_sColumnNameSeriesTitle );
		m_TableImportDuplicateModel.addColumn( m_sColumnNameAuthor );
		m_TableImportDuplicateModel.addColumn( m_sColumnNamePages );
		m_TableImportDuplicateModel.addColumn( m_sColumnNameEdition );
		m_TableImportDuplicateModel.addColumn( m_sColumnNameMyRating );
		m_TableImportDuplicateModel.addColumn( m_sColumnNameGoodReadsLink );
		m_TableImportDuplicateModel.addColumn( m_sColumnNameBookIdentifier );
		m_TableImportDuplicateModel.addColumn( m_sColumnNameIsCollection );
		m_TableImportDuplicateModel.addColumn( m_sColumnNameCollectionTitle );
		m_TableImportDuplicateModel.addColumn( m_sColumnNameDateStarted );
		m_TableImportDuplicateModel.addColumn( m_sColumnNameBookCoverImage );

		m_TableImportDuplicate = new JTable( m_TableImportDuplicateModel );
		scrollPane_1.setViewportView( m_TableImportDuplicate );
		
		JButton btnNewButton = new JButton("Add Books");
		btnNewButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				int nRows = m_TableImportNewModel.getRowCount();

				for( int i=0; i<nRows; i++ )
				{
					// Create a book object from the data in this row of the table.

					Object tableValue;
					String sReader = m_sReaderRyan;
					String sDateCompleted = "";
					LocalDate dateCompleted = null;
					String sTitle = "";
					String sSeriesTitle = "";
					String sAuthor = "";
					int nPages = 0;
					String sEdition = "";
					int nMyRating = 0;
					String sGoodReadsLink = "";
					String sBookIdentifier = "";
					boolean bIsCollection = false;
					String sCollectionTitle = "";
					String sDateStarted = "";
					LocalDate dateStarted = null;
					String sCoverImageName = "";

					tableValue = m_TableImportNewModel.getValueAt( i, m_nColumnNumDateCompleted );
					if( null != tableValue )
					{
						sDateCompleted = tableValue.toString();
						if( false == sDateCompleted.isEmpty() )
							dateCompleted = LocalDate.parse( sDateCompleted );
					}

					tableValue = m_TableImportNewModel.getValueAt( i, m_nColumnNumTitle );
					if( null != tableValue )
					{
						sTitle = tableValue.toString();
					}

					tableValue = m_TableImportNewModel.getValueAt( i, m_nColumnNumSeriesTitle );
					if( null != tableValue )
					{
						sSeriesTitle = tableValue.toString();
					}

					tableValue = m_TableImportNewModel.getValueAt( i, m_nColumnNumAuthor );
					if( null != tableValue )
					{
						sAuthor = tableValue.toString();
					}

					tableValue = m_TableImportNewModel.getValueAt( i, m_nColumnNumPages );
					if( null != tableValue )
					{
						nPages = Integer.parseInt( tableValue.toString() );
					}

					tableValue = m_TableImportNewModel.getValueAt( i, m_nColumnNumEdition );
					if( null != tableValue )
					{
						sEdition = tableValue.toString();
					}

					tableValue = m_TableImportNewModel.getValueAt( i, m_nColumnNumMyRating );
					if( null != tableValue )
					{
						nMyRating = Integer.parseInt( tableValue.toString() );
					}

					tableValue = m_TableImportNewModel.getValueAt( i, m_nColumnNumGoodReadsLink );
					if( null != tableValue )
					{
						sGoodReadsLink = tableValue.toString();
					}

					tableValue = m_TableImportNewModel.getValueAt( i, m_nColumnNumBookIdentifier );
					if( null != tableValue )
					{
						sBookIdentifier = tableValue.toString();
					}

					tableValue = m_TableImportNewModel.getValueAt( i, m_nColumnNumIsCollection );
					if( null != tableValue )
					{
						if( tableValue.toString().equals( "TRUE" ) )
							bIsCollection = true;
					}

					tableValue = m_TableImportNewModel.getValueAt( i, m_nColumnNumCollectionTitle );
					if( null != tableValue )
					{
						sCollectionTitle = tableValue.toString();
					}

					tableValue = m_TableImportNewModel.getValueAt( i, m_nColumnNumDateStarted );
					if( null != tableValue )
					{
						sDateStarted = tableValue.toString();
						if( false == sDateStarted.isEmpty() )
							dateStarted = LocalDate.parse( sDateStarted );
					}

					tableValue = m_TableImportNewModel.getValueAt( i, m_nColumnNumCoverImageName );
					if( null != tableValue )
					{
						sCoverImageName = tableValue.toString();
					}

					// Check the data before adding the book.

					if( sTitle.isEmpty() )
					{
						m_lbl_StatusBar.setText( "Error adding book: Title cannot be blank." );
					}
					else
					{
						Book latestBook = new Book( sReader, dateCompleted, sTitle, sSeriesTitle,
													sAuthor, nPages, sEdition, nMyRating,
													sGoodReadsLink, sBookIdentifier,
													bIsCollection, sCollectionTitle, dateStarted, sCoverImageName );

						m_DBBooks.AddBook( latestBook );
					}
				}

				m_lbl_StatusBar.setText( "Importing, Adding complete." );
			}
		});
		btnNewButton.setBounds(10, 429, 150, 23);
		panel_Import.add(btnNewButton);
		
		JButton btnNewButton_1 = new JButton("Update");
		btnNewButton_1.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				int nRows = m_TableImportDuplicateModel.getRowCount();

				for( int i=0; i<nRows; i++ )
				{
					// Create a book object from the data in this row of the table.

					Object tableValue;
					String sReader = m_sReaderRyan;
					String sDateCompleted = "";
					LocalDate dateCompleted = null;
					String sTitle = "";
					String sSeriesTitle = "";
					String sAuthor = "";
					int nPages = 0;
					String sEdition = "";
					int nMyRating = 0;
					String sGoodReadsLink = "";
					String sBookIdentifier = "";
					boolean bIsCollection = false;
					String sCollectionTitle = "";
					String sDateStarted = "";
					LocalDate dateStarted = null;
					String sCoverImageName = "";

					tableValue = m_TableImportDuplicateModel.getValueAt( i, m_nColumnNumDateCompleted );
					if( null != tableValue )
					{
						sDateCompleted = tableValue.toString();
						if( false == sDateCompleted.isEmpty() )
							dateCompleted = LocalDate.parse( sDateCompleted );
					}

					tableValue = m_TableImportDuplicateModel.getValueAt( i, m_nColumnNumTitle );
					if( null != tableValue )
					{
						sTitle = tableValue.toString();
					}

					tableValue = m_TableImportDuplicateModel.getValueAt( i, m_nColumnNumSeriesTitle );
					if( null != tableValue )
					{
						sSeriesTitle = tableValue.toString();
					}

					tableValue = m_TableImportDuplicateModel.getValueAt( i, m_nColumnNumAuthor );
					if( null != tableValue )
					{
						sAuthor = tableValue.toString();
					}

					tableValue = m_TableImportDuplicateModel.getValueAt( i, m_nColumnNumPages );
					if( null != tableValue )
					{
						nPages = Integer.parseInt( tableValue.toString() );
					}

					tableValue = m_TableImportDuplicateModel.getValueAt( i, m_nColumnNumEdition );
					if( null != tableValue )
					{
						sEdition = tableValue.toString();
					}

					tableValue = m_TableImportDuplicateModel.getValueAt( i, m_nColumnNumMyRating );
					if( null != tableValue )
					{
						nMyRating = Integer.parseInt( tableValue.toString() );
					}

					tableValue = m_TableImportDuplicateModel.getValueAt( i, m_nColumnNumGoodReadsLink );
					if( null != tableValue )
					{
						sGoodReadsLink = tableValue.toString();
					}

					tableValue = m_TableImportDuplicateModel.getValueAt( i, m_nColumnNumBookIdentifier );
					if( null != tableValue )
					{
						sBookIdentifier = tableValue.toString();
					}

					tableValue = m_TableImportDuplicateModel.getValueAt( i, m_nColumnNumIsCollection );
					if( null != tableValue )
					{
						if( tableValue.toString().equals( "TRUE" ) )
							bIsCollection = true;
					}

					tableValue = m_TableImportDuplicateModel.getValueAt( i, m_nColumnNumCollectionTitle );
					if( null != tableValue )
					{
						sCollectionTitle = tableValue.toString();
					}

					tableValue = m_TableImportDuplicateModel.getValueAt( i, m_nColumnNumDateStarted );
					if( null != tableValue )
					{
						sDateStarted = tableValue.toString();
						if( false == sDateStarted.isEmpty() )
							dateStarted = LocalDate.parse( sDateStarted );
					}

					tableValue = m_TableImportDuplicateModel.getValueAt( i, m_nColumnNumCoverImageName );
					if( null != tableValue )
					{
						sCoverImageName = tableValue.toString();
					}

					// Check the data before adding the book.

					if( sTitle.isEmpty() )
					{
						m_lbl_StatusBar.setText( "Error updating book: Title cannot be blank." );
					}
					else
					{
						Book latestBook = new Book( sReader, dateCompleted, sTitle, sSeriesTitle,
													sAuthor, nPages, sEdition, nMyRating,
													sGoodReadsLink, sBookIdentifier,
													bIsCollection, sCollectionTitle, dateStarted, sCoverImageName );
		
						m_DBBooks.UpdateBook( latestBook );
					}
				}

				m_lbl_StatusBar.setText( "Importing, Updating complete." );
			}
		});
		btnNewButton_1.setBounds(10, 757, 150, 23);
		panel_Import.add(btnNewButton_1);
		
		JPanel panel_StatusBar = new JPanel();
		panel_StatusBar.setBorder(new LineBorder(new Color(0, 0, 0)));
		FlowLayout fl_panel_StatusBar = (FlowLayout) panel_StatusBar.getLayout();
		fl_panel_StatusBar.setAlignment(FlowLayout.LEFT);
		panel_StatusBar.setBounds(0, 835, 1584, 26);
		contentPane.add(panel_StatusBar);
		
		m_lbl_StatusBar = new JLabel("");
		m_lbl_StatusBar.setHorizontalAlignment(SwingConstants.LEFT);
		panel_StatusBar.add(m_lbl_StatusBar);
	}
}
