# BusinessCardParser
Takes a block of text in String format and returns the name, telephone number, and email address from it
This code uses Regular expressions to parse a block of text obtained from a business card into the name, telephone number, and email address of the card’s owner. The block is first broken up by newline characters and each line is placed into an array of Strings. Using regular expressions, each line in the array is then categorized as containing a possible name, a possible phone number, or a possible email address. Each line can only be put into one of these categories, so if, for example, email and telephone number were both provided on the same line, the line would be placed in only the email or telephone number category, depending on which was provided first. Further detail about how the lines were split can be found in each category’s subsection.

# Phone:
Telephone numbers are parsed using a regular expression that searches for 10 consecutive digits, possibly grouped in 3 digit, 3 digit, 4 digit blocks, with the first block possibly preceded by a 1 (for a total of 11 digits), and possibly enclosed by parenthesis. In addition, all blocks (including between the preceding 1 and the first block of 3 digits) can be separated by one or zero spaces, one or zero dashes, or one of each. As currently constructed, the function will not consider non U.S. numbers or any telephone numbers that include extensions. If needed, this functionality can be added by adding to the front and/or back of the regular expression as desired. The following patterns will be recognized, all of which can be preceded by a 1, a 1 followed by a dash, or a 1 followed by a space, and all of which can have zero or more spaces separating each block:

					xxxyyyzzzz		(xxx)yyyzzz
					xxxyyy-zzzz		(xxx)yyy-zzzz													xxx-yyyzzzz		(xxx)-yyyzzzz
					xxx-yyy-zzzz		(xxx)-yyy-zzzz			
					xxx yyy zzzz		(xxx) yyy zzzz
					

# Email:
Email addresses are parsed using a regular expression that searches for the following pattern:

							a@b.c

where a, b, and c represent strings of indeterminate length composed of any non-space characters. In addition to the full email address being stored, the function also stores the user name of the email address (the part preceding the @) as a global variable in order to determine the name of the person (explained further below). Using a similar tactic, one could potentially store the domain of the email address if they wanted to determine the company name. However, as currently constructed, this code lacks that functionality.


# Name: 
This function must be called after the email parsing function as this relies on the local name of the email address to check for a match. Possible name data is determined by using a regular expression to capture any lines that do not include any digits or symbols. After all lines possibly containing a name are captured, the function iterates through each line and compares it to the email’s user name to essentially solve a variation of the Longest Common Substring Problem. The function uses a Suffix Tree to find the line that matches most closely to the email’s local name. 
This function assumes that the local name of the person’s email address has some relation to their first or last name. The functionality will fail if provided an email address containing a local name unrelated to the person’s actual name, or a local name containing only initials. If faced with a scenario in which the longest common substring only contains zero or one matches, the function will throw an exception rather than take a guess at the correct name.
If the email address is of the aforementioned unsupported type, one possible solution could be to use each line as the input to a Google search and then check the first link for a company web page (company name), a link to glassdoor.com (job title), or a facebook/linkedin profile (name!). At any rate, this is not supported in the current version. 

# Testing:
Testing is done in the Driver file. An array of strings is created with various instances of possible business cards. The driver then iterates through the array printing the test number followed by the name, phone number, and email address for each test. Additional tests can be added by adding to the array.
 
 # Exceptions and Possible Errors/Unknown Behavior:
To avoid unknown behavior, the email and phone number functions will throw an illegal argument exception if, when finished with the parsing function, the program has more or less than one line to choose from. The name function will throw an illegal argument exception if the longest matching substring has fewer than 3 consecutive matching letters. Requiring any more than 3 matches would risk failing to match an acutal name if it were only two letters. However, this means that if the user name does not match the actual name, and any unrelated line happens to match 3 or more consecutive characters, the function could assign the name incorrectly. If accuracy is paramount at the expense of failing to match shorter names, the required number of matches can be increased in the parseName function. 
