package App;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;

public class MainApp {
	
	private static String reqMethod;
	private static String reqPath;
	private static String rootDir;
	private static String ipAddress;
	private static String port;
	private static String status;
	private static int dirIndex;
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		getServerConfig();
		
		try {
			System.out.println(Integer.parseInt(port));
			ServerSocket server = new ServerSocket(Integer.parseInt(port));
			
			while(true) {
                Socket client = server.accept();
                	
//                while (true) {
                	BufferedReader br = new BufferedReader(new InputStreamReader(client.getInputStream()));
                	BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
                	
                	String req = "";
                	String line = br.readLine();
                	req += line + "\n";
                	
                	while (!line.isEmpty()) {
                		line = br.readLine();
                		req += line + "\n";
                	}
                	System.out.println(req);
//                	System.out.println("---");
                	parseReqPath(req);
                	parseReqMethod(req);
                	parseHost(req);
                	System.out.println(reqPath);
                	dirIndex = 0;
                	String fileContent = getFileContent();
                	String res = generateResponse(fileContent);
                	//write message to client
//                	System.out.println(res);
                	bw.write(res);
                	bw.flush();
//                }
                //close connection
                client.close();
                getServerConfig();
            }
		}
		catch (IOException e) {
            e.printStackTrace();
        }
	}
	
	public static void getServerConfig() {
		//
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
            ipAddress = m.group(2);
            port = m.group(3);
        }
	}
	
	public static String getFileMimeType(String filename) {
		
		FileInputStream fis;
		BufferedInputStream bis;
		String tmp = "";
		try {
			fis = new FileInputStream("ext2mime.txt");
			bis = new BufferedInputStream(fis);

			byte[] bRes = new byte[1024];
			int c = bis.read(bRes);
			
			while (c != -1) {
				tmp += (new String(bRes));
				c = bis.read(bRes);
			}
			
			bis.close();
			fis.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		String ext = "";
		try {
			ext = filename.substring(filename.indexOf('.'));			
		}
		catch (StringIndexOutOfBoundsException e) {
			return "";
		}
		String pattern = ext + " ([^\n]+)";
		Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(tmp);
        if (m.find()) {
            return m.group(1);
        }
        return "";
	}
	
	public static String generateDirectoryPage(File[] listDir) {
		//
		String fileContent = "";
		fileContent += "<!DOCTYPE html>\r\n" + 
				"<html>\r\n" + 
				"<head>\r\n" + 
				"<title>/" + reqPath + "</title>\r\n" + 
				"</head>\r\n" + 
				"<body>";
		for (File file : listDir) {
			fileContent += "<a href='" ;
			if (!reqPath.isEmpty()) fileContent += "/";
			fileContent += reqPath + "/" + file.getName() + "'>" + 
					reqPath + "/" + file.getName() + "</a></br>";
//			System.out.println("Path: " + reqPath);
		}
		fileContent += "</body>\r\n" + 
				"</html>";
		return fileContent;
	}
	
	public static File[] getListDir(File dir) {
		//
		File filesList[] = dir.listFiles();
		
		return filesList;
	}
	
	public static String generateResponse(String content) {
		//
		String fullPath = rootDir + reqPath;
		String mimeType = "";
		if (dirIndex == 1) fullPath += "\\index.html";
		
		if (dirIndex != 2) {
			Path path = Paths.get(fullPath);
			Path fileName = path.getFileName();
			mimeType = getFileMimeType(fileName.toString());			
		}
		else if (dirIndex == 2) {
			mimeType = "text/html";
		}
		
		if (mimeType.contains("image")) {
			byte[] fileContent;
			content = "";
			try {
				fileContent = FileUtils.readFileToByteArray(new File(fullPath));
				content = Base64.getEncoder().encodeToString(fileContent);
			} catch (IOException e) {
				e.printStackTrace();
			}
			System.out.println("disni yak");
			
			content = "<img src='data:" + mimeType + ";base64," + content + "'>";
			
			mimeType = "text/html";
		}

		String response = "HTTP/1.0 " + status + "\r\n";
		response += "Content-Type: " + mimeType + "\r\n";
		response += "Content-Length: " + content.length() + "\r\n";
		response += "\r\n";
		
		return response + content;
	}
	
	public static String getFileContent() {
		//
		String fullPath = rootDir + reqPath;
		String fileContent = "";
		
		File f = new File(fullPath);
		// File found
		if (f.isFile()) {			
			FileInputStream fis;
			BufferedInputStream bis;
			try {
				fis = new FileInputStream(fullPath);
				
				bis = new BufferedInputStream(fis);
				byte[] bRes = new byte[1024];
				int c = bis.read(bRes);
				
				while (c != -1) {
					fileContent += (new String(bRes));
					c = bis.read(bRes);
				}
				
				bis.close();
				fis.close();
			} catch (FileNotFoundException e) {
				status = "404 Not Found";
				return "File Not Found";
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		else if (f.exists()) {
			// Check index.html
			File f2 = new File(rootDir + reqPath + "\\index.html");
			if (f2.exists()) {
				FileInputStream fis;
				BufferedInputStream bis;
				dirIndex = 1;
				try {
					fullPath += "\\index.html";
//					System.out.println(fullPath);
					fis = new FileInputStream(fullPath);
					
					bis = new BufferedInputStream(fis);
					byte[] bRes = new byte[1024];
					int c = bis.read(bRes);
					
					while (c != -1) {
						fileContent += (new String(bRes));
						c = bis.read(bRes);
					}
					
					bis.close();
					fis.close();
				} catch (FileNotFoundException e) {
					status = "404 Not Found";
					return "File Not Found";
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			// No index.html
			else {
				dirIndex = 2;
				fileContent = generateDirectoryPage(getListDir(f));				
			}
		}
		// Not Found
		else {
//			System.out.println("ganemu");
			status = "404 Not Found";
			return "File Not Found";
		}
		// Success
		status = "200 OK";
		return fileContent;
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
