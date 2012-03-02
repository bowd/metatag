package edu.man.datamining;
/**
 * Hello world!
 *
 */
public class App {
    public static void main( String[] args ) {
        MetaTag mm = new MetaTag("localhost", 8066);
        try {
          System.out.println( mm.getSemTypes("penicillin", true, -600) );
        } catch (Exception e) {
          System.err.println( "Exception while getting semtypes for phrase." );
          System.err.println( e.getMessage() );
        }
    }
}
