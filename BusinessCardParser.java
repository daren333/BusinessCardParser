import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.ArrayList;
import java.util.Iterator;
import java.lang.StringBuilder;

class BusinessCardParser {

  String local;
  
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
    int len = lines.length;
    Pattern namePattern = Pattern.compile("^[^\\d\\!\\@\\#\\$\\%\\&\\+]*$");
  	Pattern phonePattern = Pattern.compile("(1?\\-?\\s*\\(?\\d{3}\\-?\\s*\\)?\\s*\\d{3}\\-?\\s*\\d{4})");
  	Pattern emailPattern = Pattern.compile("((\\S+)@(\\S+).\\S+)");
    
    
    for(int i = 0; i < len; i++){
    	String line = lines[i];
      	
      	Matcher matcher = namePattern.matcher(line);
        if(matcher.find()){
          //System.out.println(line);
          possibleNameData.add(line);
        }
      	matcher = phonePattern.matcher(line);
      	if(matcher.find()){
          //System.out.println(line);
          possiblePhoneData.add(line);
        }
      	matcher = emailPattern.matcher(line);
      	if(matcher.find()){
           //System.out.println(line);
           possibleEmailData.add(line);
        }   
    }
    return;
  }
  
  private String parsePhoneNumber(ArrayList<String> possiblePhoneData){
    StringBuilder phoneNumber = new StringBuilder();
    
    //check for fax numbers
    Pattern faxNumbers = Pattern.compile("[F|f]+(ax)?:?\\s*");
   	//create shadow to avoid removing items while iterating through list
    ArrayList<String> shadow = new ArrayList<String>(possiblePhoneData);
    for(String line : shadow){
      Matcher matcher = faxNumbers.matcher(line);
      if(matcher.find()){
        possiblePhoneData.remove(line);
      }
    }
    if(possiblePhoneData.size() > 1 || possiblePhoneData.size() < 1){
      throw new IllegalArgumentException("Multiple phone numbers provided or ambiguous phone number input");
    }
    String line = possiblePhoneData.get(0);
   	int len = line.length();
   	for(int i = 0; i < len; i++) {
      if(line.charAt(i) > 47 && line.charAt(i) < 58){
        phoneNumber.append(line.charAt(i));
      }
    }
    return phoneNumber.toString();
  }
  
  /*This function is somewhat superfluous as email parsing could be accomplished in the
  	groupLinesByType function. However, I've included it here in the event that further scrutiny
    is wanted in the future and because everyone else got their own function so I didn't want emails
    to feel left out*/
  private String parseEmailAddress(ArrayList<String> possibleEmailData){
  	Pattern emailPattern = Pattern.compile("((\\S+)@(\\S+).\\S+)");
    
    if(possibleEmailData.size() > 1 || possibleEmailData.size() < 1){
      throw new IllegalArgumentException("Multiple email addresses provided or ambiguous email input");
    }
    Matcher matcher = emailPattern.matcher(possibleEmailData.get(0));
    if(matcher.find()){
      String emailAddress = matcher.group(1);
      local = matcher.group(2);
      return emailAddress;
    }
    else{
      throw new IllegalArgumentException("Unable to determine email address");
    }
  }

  private String parseName(ArrayList<String> possibleNameData){
    String targetWord = this.local, longestMatch = null;
    int maxMatches = 0, targetLen = targetWord.length();
    
    for(String line: possibleNameData){
      int lineLen = line.length();
      int[][] matchCounter = new int[lineLen][targetLen];
      for(int i = 0; i < (lineLen - 1); i++){
        for(int j = 0; j < (targetLen - 1); j++){
          if(line.charAt(i) == targetWord.charAt(j)){
            if(i == 0 || j == 0){
              matchCounter[i][j] = 1;
            }
            else{
               matchCounter[i][j] = matchCounter[i-1][j-1]+1;
            }
            if(matchCounter[i][j] > maxMatches){
              maxMatches = matchCounter[i][j];
              longestMatch = line;
            }
          }
          else{
            matchCounter[i][j] = 0;
          }
        }
      }
    }
    /*if nothing has matched or only one letter has matched, throw an exception rather 
    than guessing at the correct line*/
    if(maxMatches < 2){
      throw new IllegalArgumentException("Unable to determine name");
    }
    return longestMatch;
  }
 }
