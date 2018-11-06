package cc.cnplay.uhf;

public class App {
	public static Login login = new Login();
	public static String PWD = "00000000";
	public static int PORT = 0;
	public static int CNTLEN = 4;
	
	public static String HOSTNAME = "192.168.0.107:8080";

	public static String url(String path) {
		return "http://" + login.getHostname() + path;
	}
}
