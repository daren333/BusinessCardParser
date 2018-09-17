
import java.util.regex.Pattern;
import java.util.regex.Matcher;

class ContactInfo {
	
  String name, phoneNumber, email;
  
  protected ContactInfo(String name, String phoneNumber, String email){
    
    this.name = name;
    this.phoneNumber = phoneNumber;
    this.email = email;
     
  }
  
  //returns the full name of the individual (eg. John Smith, Susan Malick)
  public String getName(){
    return this.name;
  }
  
  //returns the phone number formatted as a sequence of digits
  public String getPhoneNumber(){
    return this.phoneNumber;
  }
  
  //returns the email address
  public String getEmailAddress(){
    return this.email;
  }
  
}