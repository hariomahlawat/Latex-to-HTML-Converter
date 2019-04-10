import java.io.File;
import java.io.Writer;
import java.io.FileWriter;
import java.io.OutputStreamWriter;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.lang.StringBuilder;

public class CustomMethods {

    /*------------------------------------------------------------------------------------------------------------------
    // Methods to take out the image width, height and name from the token.
    --------------------------------------------------------------------------------------------------------------------
    */
        public String extractImageWidth(String s){
            s= s.trim();
            int firstIndex = s.indexOf('=');
            int lastIndex = s.indexOf(',');

            return  s.substring(firstIndex+1,lastIndex);
        }

        public String extractImageHeight(String s){
            s= s.trim();
            int firstIndex = s.indexOf(',');
            s =s.substring(firstIndex);
             firstIndex = s.indexOf('=');
            int lastIndex = s.indexOf(']');

            return  s.substring(firstIndex+1,lastIndex);
        }


        public String extractImageName(String s){
            s= s.trim();
            int firstIndex = s.indexOf('{');
            int lastIndex = s.indexOf('}');

            return  s.substring(firstIndex+1,lastIndex);

        }

    /*------------------------------------------------------------------------------------------------------------------
    // Methods to assist mathematical conversion.
    --------------------------------------------------------------------------------------------------------------------
    */

        public String findTextBetweenBraces(String s){
            s= s.trim();
            int firstIndex = s.indexOf('{');
            int lastIndex = s.indexOf('}');

            return  s.substring(firstIndex+1,lastIndex);
        }
        public String extractFirstFracPart(String s){
            s= s.trim();
            int firstIndex = s.indexOf('}');
            s =s.substring(4,firstIndex);
            return  s.substring(2,s.length());
        }

        public String extractSecondFracPart(String s){
            s= s.trim();
            int firstIndex = s.indexOf('}');
            s =s.substring(firstIndex+2, s.length()-1);
            return  s;
        }

        //---------------------------------------------------------------------------------------------------------------

}