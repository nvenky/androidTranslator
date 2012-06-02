import java.io.BufferedWriter;
import java.io.FileWriter;

import com.google.android.c2dm.C2DMessaging;


public class C2DMMessagingTest {

	public static void main(String[] args){
		try {
			FileWriter fstream = new FileWriter("/Users/nvenky/out.txt");
			BufferedWriter out = new BufferedWriter(fstream);
		  	out.write("Hello");
			C2DMessaging messaging = C2DMessaging.class.newInstance();
			out.write(messaging.toString());
		} catch (Exception e) {		
			e.printStackTrace();			
			System.out.println("Exception occurred" + e.getMessage());
		}
	}
}
