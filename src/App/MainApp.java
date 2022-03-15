package App;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainApp {
	
	private static String reqMethod;
	private static String reqPath;
	private static String rootDir;
	private static String IpAddress;
	private static String port;
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
	}
	
	public static void getServerConfig() {
		FileInputStream fis;
        BufferedInputStream bis;
        String tmp = "";
        try {
            fis = new FileInputStream("config.txt");
            bis = new BufferedInputStream(fis);
            
            byte[] c;
            c = new byte[bis.available()];
            bis.read(c);
            tmp = new String(c);
            
            bis.close();
            fis.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        String pattern = "rootDir=([^\n]+)\nip=([^\n]+)\nport=([^\n]+)";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(tmp);
        if (m.find()) {
            rootDir = m.group(1);
            IpAddress = m.group(2);
            port = m.group(3);
        }
	}
	
//	public static String generateDirectoryPage(List<String> listDir) {
//		//
//	}
//	
//	public static String getListDir() {
//		//
//	}
//	
//	public static String generateResponseHeader(int statusCode, String statusMsg, String contentType, int contentLength) {
//		//
//	}
//	
//	public static String getFileContent() {
//		//
//	}
//	
//	public static void parseReqMethod(String reqHeader) {
//		//
//	}
//	
//	public static void parseReqPath(String reqHeader) {
//		//
//	}
	
}
