import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.ArrayList;
import java.util.Iterator;
import java.lang.StringBuilder;

class BusinessCardParser {

  String userName;
  
  ContactInfo getContactInfo(String document){
		String emailAddress, phoneNumber, name;
		ArrayList<String> possibleNameData = new ArrayList<String>();
  	ArrayList<String> possiblePhoneData = new ArrayList<String>();
    ArrayList<String> possibleEmailData = new ArrayList<String>();
	
		//splitting document by newline; considering \r, \n, and \r\n to avoid issues with different systems
    String[] docByLine = document.split("\r?\n|\r");
    groupLinesByType(docByLine, possibleNameData, possiblePhoneData, possibleEmailData);
	  
    phoneNumber = parsePhoneNumber(possiblePhoneData);
    emailAddress = parseEmailAddress(possibleEmailData);
    name = parseName(possibleNameData);
	  
    return new ContactInfo(name, phoneNumber, emailAddress);
  }
  

  /*Matching lines possibly containing names, phone numbers, and email addresses using regexs.
  Lines with no digits or non-alphabetic symbols are considered possible names
  Lines with 10 consecutive digits, possibly preceded by a 1, a 1 , or a 1-, and potentially 
  grouped in (3) 3-4, (3)-3-4, or 3-3-4 are considered possible phone numbers
  lines with at least one non-space character, followed by an at sign, followed by at least 
  one non-space character, a period, and at least one more non-space character are considered 
  possible email addresses.*/
  
  private void groupLinesByType(String[] lines, ArrayList<String> possibleNameData, ArrayList<String> possiblePhoneData, ArrayList<String> possibleEmailData){
    Pattern namePattern = Pattern.compile("^[^\\d\\!\\@\\#\\$\\%\\&\\+]*$");
  	Pattern phonePattern = Pattern.compile("(1?\\s*\\-?\\s*\\(?\\s*\\d{3}\\s*\\-?\\s*\\)?\\s*\\-?\\d{3}\\s*\\-?\\s*\\d{4})");
  	Pattern emailPattern = Pattern.compile("((\\S+)@(\\S+).\\S+)");
		int len = lines.length;

    for(int i = 0; i < len; i++){
    	String line = lines[i];	
      Matcher matcher = namePattern.matcher(line);
      if(matcher.find()){
      	possibleNameData.add(line);
      }
     	matcher = phonePattern.matcher(line);
      if(matcher.find()){
      	possiblePhoneData.add(line);
      }
      matcher = emailPattern.matcher(line);
     	if(matcher.find()){
      	possibleEmailData.add(line);
      }   
    }
  }
  
  private String parsePhoneNumber(ArrayList<String> possiblePhoneData){
    StringBuilder phoneNumber = new StringBuilder();
    
    /*check for fax numbers, create shadow to avoid removing items while iterating 
		through list and then remove any lines containing "F", "f", "Fax", or "fax" */
    Pattern faxNumbers = Pattern.compile("[F|f]+(ax)?:?\\s*");
    ArrayList<String> shadow = new ArrayList<String>(possiblePhoneData);
    for(String line : shadow){
      Matcher matcher = faxNumbers.matcher(line);
      if(matcher.find()){
        possiblePhoneData.remove(line);
      }
    }
		
		//Throws exception unless there is exactly one possible phone number remaining to avoid unknown behavior
    if(possiblePhoneData.size() > 1 || possiblePhoneData.size() < 1){
      throw new IllegalArgumentException("Multiple phone numbers provided or ambiguous phone number input");
    }
		
    String line = possiblePhoneData.get(0);
   	int len = line.length();
		//Uses ascii value range to copy only digits
   	for(int i = 0; i < len; i++) {
      if(line.charAt(i) > 47 && line.charAt(i) < 58){
        phoneNumber.append(line.charAt(i));
      }
    }
    return phoneNumber.toString();
  }
  
  /*The parse email function is somewhat superfluous as email parsing could be accomplished in the
  	groupLinesByType function. However, I've included it here in the event that further scrutiny
    is wanted in the future and because everyone else got their own function so I didn't want emails
    to feel left out*/
	
  private String parseEmailAddress(ArrayList<String> possibleEmailData){
		Pattern emailPattern = Pattern.compile("((\\S+)@(\\S+).\\S+)");
    Matcher matcher = emailPattern.matcher(possibleEmailData.get(0));
		String emailAddress = null;
		
		//Throws exception unless there is exactly one possible email address to avoid unknown behavior
    if(possibleEmailData.size() > 1 || possibleEmailData.size() < 1){
      throw new IllegalArgumentException("Multiple email addresses provided or unable to determine email");
    }
		
    if(matcher.find()){
      emailAddress = matcher.group(1);
			/*Global variable userName assigned the portion of the email address
			preceding the @ sign for later use in parsing name data*/
      this.userName = matcher.group(2);
		}
    
		return emailAddress;
  }

	/*This function uses a Suffix Tree to iterate through each line possibly containing a name and
	matching each character with the email userName to find the longest matching substring. This assumes
	that company email addresses generally use a portion of the employees name as part of their email
	username. In the event that the username has nothing to do with the subject's name, UNKNOWN BEHAVIOR 
	MAY OCCUR if any of the lines happen to match three or more consecutive letters of the username.*/
	
  private String parseName(ArrayList<String> possibleNameData){
    String longestMatch = null;
    int userNameLen = userName.length(), maxMatches = 0;
    
    for(String line: possibleNameData){
      int lineLen = line.length();
      int[][] matchCounter = new int[lineLen][userNameLen];
      for(int i = 0; i < (lineLen - 1); i++){
        for(int k = 0; k < (userNameLen - 1); k++){
          if(line.charAt(i) == userName.charAt(k)){
            if(i == 0 || k == 0){
              matchCounter[i][k] = 1;
            }
            else{
               matchCounter[i][k] = matchCounter[i-1][k-1]+1;
            }
            if(matchCounter[i][k] > maxMatches){
              maxMatches = matchCounter[i][k];
              longestMatch = line;
            }
          }
          else{
            matchCounter[i][k] = 0;
          }
        }
      }
    }
		
    /*if fewer than three letters have matched, throw an exception rather 
    than guessing at the correct line*/
    if(maxMatches < 3){
      throw new IllegalArgumentException("Unable to determine name");
    }
		
    return longestMatch;
  }
 }
