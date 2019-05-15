import java.io.*;
import java.net.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.*;

public class WebWorker extends Thread {
    private String urlString;
    private final int rowToUpdate;
    private WebFrame frame ;

    public WebWorker(String urlString, int rowToUpdate, WebFrame frame) {
        this.urlString = urlString;
        this.rowToUpdate = rowToUpdate;
        this.frame = frame ;
    }
    public void run(){
        String downloadResult = download();
        frame.rowFinishedStatus(downloadResult ,  rowToUpdate , 1 );
    }

private String download() {
    InputStream input = null;
    StringBuilder contents = null;
    try {
        URL url = new URL(urlString);
        URLConnection connection = url.openConnection();

        // Set connect() to throw an IOException
        // if connection does not succeed in this many msecs.
        connection.setConnectTimeout(5000);

        connection.connect();
        input = connection.getInputStream();

        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
        //download starts here after reader opens input
       long start =  System.currentTimeMillis();
        char[] array = new char[1000];
        int len;
        int downloadSize = 0 ;
        contents = new StringBuilder(1000);
        while ((len = reader.read(array, 0, array.length)) > 0) {
                if(interrupted()){
                    return "interupted";
                }
            contents.append(array, 0, len);
            Thread.sleep(100);
            downloadSize+=len;
        }
        long end = System.currentTimeMillis();
         DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
         Date date = new Date();
        return dateFormat.format(date) + "  " + (end-start)+ "ms  " +downloadSize+" bytes" ;
        // Successful download if we get here

    }
    // Otherwise control jumps to a catch...
    catch (MalformedURLException ignored) { return "error";
    } catch (InterruptedException exception) {
        return "interupted";
        // YOUR CODE HERE
        // deal with interruption
    } catch (IOException ignored) {
        return "error";
    }
    // "finally" clause, to close the input stream
    // in any case

    finally {
        try {
            if (input != null) input.close();
        } catch (IOException ignored) {
        }

    }

}
	
}
