import java.util.regex.Pattern;
import java.util.regex.Matcher;

class Driver {
  public static void main(String[] args) {
 	
    String[] tests = {
    "ASYMMETRIK LTD\nMike Smith, Jr.\nSenior Software Engineer\n(520)555-1234\nmsmith@asymmetrik.com",
    "Foobar Technologies\nAnalytic Developer\nLisa Haung\n1234 Sentry Road\nColumbia, MD 12345\nPhone: 410-555-1234\nFax: 410-555-4321\nlisa.haung@foobartech.com",
    "Arthur Wilson\nSoftware Engineer\nDecision & Security Technologies\nABC Technologies\n123 North 11th Street\nSuite 229\nArlington, VA 22209\nTel: +1 (703) 555-1259\nFax: +1 (703) 555-1200\nawilson@abctech.com",
    "Bugs Bunny, Esq.\nWarner Bros Production Studios\nWorkplace Comp Claims Investigator\n444 797 3310\nb.bunny@wbros.co",
    "Dr. Elmer Fudd\nTheoretical Physicist\n Acme Labs\n(520)7780955\nelmer@AcmeLabs.org",
    "Wile E. Cayote\nAcme Pyrotechnics\nDemolitioins Tester\n1-888-371-0093\nWECayote@acmePyro.boom",
    "1-800-325-6689\nnoName@yahoo.com\n Blah",
    "Donald Duck\n569 5567853",
    "Marvin the Martian\nPilot\nMarvin@MartianAirlines.com"};
    
    
    BusinessCardParser parser = new BusinessCardParser();
    ContactInfo info;
    int numTests = tests.length;
    for(int i = 0; i < numTests; i++){
      info = parser.getContactInfo(tests[i]);
      System.out.println("Test Number: "+i+"\nName: "+info.getName()+"\nPhone Number: "+info.getPhoneNumber()+"\nEmail Address: "+info.getEmailAddress());
    }
  }
}
    