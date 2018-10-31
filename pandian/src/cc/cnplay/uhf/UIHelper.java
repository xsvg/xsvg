package cc.cnplay.uhf;

import android.content.Context;
import android.widget.Toast;



public class UIHelper {
   
	public static String token = "token";
	public static String host = "192.168.0.100";
	public static final String DB_XML = "cnplay";
	public static final String DB_XML_TOKEN = "token";
	public static final String DB_XML_HOST = "host";
	
	/**
	 * 弹出Toast消息
	 * 
	 * @param msg
	 */
	public static void ToastMessage(Context cont, String msg) {
		Toast.makeText(cont, msg, Toast.LENGTH_SHORT).show();
	}

	public static void ToastMessage(Context cont, int msg) {
		Toast.makeText(cont, msg, Toast.LENGTH_SHORT).show();
	}

	public static void ToastMessage(Context cont, String msg, int time) {
		Toast.makeText(cont, msg, time).show();
	}

  

}
