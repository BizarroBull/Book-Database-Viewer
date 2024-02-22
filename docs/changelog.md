# Bull's Book Database Viewer Changelog



## v1.0.0 - 2024-02-08

### Added

- read directory for book cover images from an ini file
- read db location, user, and password from an ini file

### Changed

- cleaned up UI spacing


## v0.0.13 - 2024-01-24

### Changed

- remove image from add book tab
- remove column chooser options
- remove reader radio buttons on new add book tab
- allow editing all table data except #, Title, and Date Completed columns
- update button to update all columns
- search to use the ignore checkboxes
- importing should only require a title and completed date
- don't try to open images when filename is an empty string
- search with blank years should ignore year

### Added

- import+adding & import+updating book complete status bar messages added


### Fixed

- collection title not saved when updating from search tab


## v0.0.12 - 2024-01-18

### Added

- import tab to allow updating from duplicates list
- import tab to update cover image
- import tab to update all fields except title and date completed
- show "TRUE" or "FALSE" for is collection column


## v0.0.11 - 2024-01-16

### Added

- search to use author
- search to use title
- search to use date
- import tab to allow adding new entries to database
- import button to start by clearing both tables
- search tab to show all cover images
- search tab image to fill panel
- import tab to show full tables
- new book to save edition string
- new book to save collection boolean
- new book to require collection title if and only if applicable


## v0.0.10 - 2023-12-20

### Added

- main search table to show all columns
- database updated for all columns
- import tab to list duplicates
- import tab to list new entries
- import tab Import button to open a CSV file and parse
- import tab browse to open a dialog
- add import tab


## v0.09 - 2023-11-27

### Changed

- rename to Bull's Book Database Viewer
- redesign search tab
- table columns reordered

### Added

- search summary to display books, pages, 5-stars
- status bar to display some text after searching


All notable changes to this project are documented in this file.
The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/).