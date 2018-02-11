import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.*;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javax.ws.rs.core.MediaType;

import org.json.JSONObject;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.*;
import java.util.List;

public class JarListener {

	public static void main(String[] args) throws InterruptedException, IOException {
		// TODO Auto-generated method stub
		      
		String jarPath = "/home/kaushik/Documents/IAS";
		Path myDir = Paths.get(jarPath);       

		
		InetAddress addr = InetAddress.getLocalHost();
		String ip = addr.getHostAddress();
		System.out.println(ip);
		
        try {
        	boolean valid = true;
        	do {
	           WatchService watcher = myDir.getFileSystem().newWatchService();
	           myDir.register(watcher, StandardWatchEventKinds.ENTRY_CREATE);
	
	           WatchKey watckKey = watcher.take();
	
	           List<WatchEvent<?>> events = watckKey.pollEvents();
	           for (WatchEvent<?> event : events) {
	                if (event.kind() == StandardWatchEventKinds.ENTRY_CREATE) {	                	
	                    System.out.println("Created: " + event.context().toString());
	                    URL url = new URL("jar:file:"+ jarPath + "/" + event.context().toString() + "!/services.txt");
	            		InputStream in = url.openStream();
	            		BufferedReader input = new BufferedReader(new InputStreamReader(in));
	            		ArrayList<String> serviceNames = new ArrayList<String>();
	            		String line = input.readLine();
	            		serviceNames.add(line);
	            		while(line != null) {
	            			System.out.println(line);	            			
	            			line = input.readLine();
	            			if(line != null)
	            				serviceNames.add(line);
	            		}
	            		updateServiceRepo(serviceNames);
	            		
	                }
	           }
	           valid = watckKey.reset();
        	}while(valid);
           
        } catch (Exception e) {
            System.out.println("Error: " + e.toString());
        }		
		
	}
	
	
	public static void updateServiceRepo(List<String> services)
	{
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("ip", "192.168.1.103");
		jsonObject.put("port", "8501");
		jsonObject.put("services", services);
				
		
		  Client client = new Client();
		  String encStr = Base64.getEncoder().encodeToString(jsonObject.toString().getBytes());
		  
		  System.out.println(encStr);
			
		
		  WebResource service = client.resource("http://192.168.1.104:8080/test1/updateRepoMapping/" + encStr);
		  ClientResponse response = service.accept("application/json")
	                .get(ClientResponse.class);
				
				System.out.println(response.getEntity(String.class));
		
	}
	
	
	

}
