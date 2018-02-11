package Book;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

@Path("/Book")
public class Book {
	Class className;
	List<Object> listArgs;
	JSONObject response;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String listenRequest()
	{
		String response= "Hello";
		return response;
	}
	
	
	// Kushagra
	@GET  //change it to POST later?
	@Path("{query}")
	@Consumes(MediaType.TEXT_HTML)
	public String testHello(@PathParam("query") String query) throws IOException {
		System.out.println("Request Received");
		
		String jarPath = "/home/harshit/Documents/IAS/Test1.jar";
		
		try {
			JarFile jarFile = new JarFile(jarPath);
	        Enumeration<JarEntry> e = jarFile.entries();
	        URL[] urls = { new URL("jar:file:" + jarPath+"!/") };
	        URLClassLoader cl = URLClassLoader.newInstance(urls);
	        Class<?> c = null;
	        
	        while (e.hasMoreElements()) {
	            JarEntry je = e.nextElement();
	            if(je.isDirectory() || !je.getName().endsWith(".class") || je.getName().contains("Service")) {
	                continue;
	            }
	            String className = je.getName().substring(0,je.getName().length()-6);
	            className = className.replace('/', '.');
	            System.out.println(className);
	            c = cl.loadClass(className);
	            Object o = c.newInstance();
	        }
	
			
			final byte[] tempbytes = Base64.getDecoder().decode(query);
			String decStr = new String(tempbytes);
		
//			JSONObject obj = new JSONObject();
//			obj.put("serviceName", "Test1");
//			obj.put("methodName","add");
//			obj.put("arguments",);
		
			JSONObject obj = new JSONObject(decStr);
			//JSONArray obj = new JSONArray(decStr);
			String serviceName = obj.getString("serviceName");
			String methodName = obj.getString("methodName");
			listArgs = new ArrayList<>();
			
			//JSONObject argsJson = obj.getJSONObject("arguments");
			JSONArray argsJson = obj.getJSONArray("arguments");
			//Iterator iter = argsJson.keys();
			for(int count = 0; count < argsJson.length(); count++) {
				listArgs.add(argsJson.get(count));
			}
			
//			listArgs.add((Object)2);
//			listArgs.add((Object)4);
//			while(iter.hasNext()) {
//				String key = (String)iter.next();
//				Object value = argsJson.get(key); //argsJson.getString(key);
//				listArgs.add(value);			
//			}
			
//			response = processRequest("Test1", "add",c);
			response = processRequest(serviceName, methodName, c);
			String encStr = Base64.getEncoder().encodeToString(response.toString().getBytes());
			return encStr;
			
		} catch (JSONException e1) {
			e1.printStackTrace();
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		} catch (InstantiationException e1) {
			e1.printStackTrace();
		} catch (IllegalAccessException e1) {
			e1.printStackTrace();
		} 
		
		
		return "ERROR: Not Found";
		
	}


	private JSONObject processRequest(String serviceName, String methodName,  Class<?> c) {
		JSONObject resObj = null;
		try {
			//className = Class.forName(serviceName);
			 Object o = c.newInstance();
			 
			Class[] partypes = new Class[2];
			partypes[0] = String.class;
			partypes[1] = List.class;
			
			Object[] argToRun = new Object[2];
			argToRun[0] = methodName;
			argToRun[1] = listArgs;
			
			Method method = o.getClass().getMethod("run", partypes);
			
			
		//	Method method = className.getMethod("run", partypes);
			Object returnVal = method.invoke(o, argToRun);
			
			resObj = new JSONObject();
			resObj.put("result", returnVal);
			
			
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}	
		
		return resObj;
	}	
	
}
