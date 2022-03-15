package App;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
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
	
	public static String generateDirectoryPage(List<String> listDir) {
		//
	}
	
	public static String getListDir() {
		//
	}
	
	public static String generateResponseHeader(int statusCode, String statusMsg, String contentType, int contentLength) {
		//
	}
	
	public static String getFileContent() {
		//
	}
	
	public static String getReqHeader(String req) {
		//
		String header = req.split("\r\n\r\n")[0];
        return header;
	}
	
	public static void parseReqPath(String reqHeader) {
		//
		String pattern = "GET ([^\\s]+)";
		Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(reqHeader);
        if (m.find()) {
            reqPath = m.group(1);
            reqPath = reqPath.substring(1);
        }
        if (reqPath.equals("\\")) {
        	reqPath = "";
        }
 
        try {
			reqPath = URLDecoder.decode(reqPath, StandardCharsets.UTF_8.name());
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
 
	public static void parseReqMethod(String reqHeader) {
		//
		String pattern = "([^\\s]+)";
		Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(reqHeader);
        if (m.find()) {
            reqMethod = m.group(1);
        }
	}
 
	public static void parseHost(String reqHeader) {
		//
		String host = "";
		String pattern = "Host: ([^\n]+)";
		Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(reqHeader);
        if (m.find()) {
            host = m.group(1);
        }
 
        // Ambil config rootdir virtual host
        if (!host.equals("localhost")) {
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
 
        	pattern = "VirtualHost: " + host + " ([^\n]+)";
        	r = Pattern.compile(pattern);
        	m = r.matcher(tmp);
        	if (m.find()) {
        		rootDir = m.group(1);
        	}
        }
	}
	
	
	
}
