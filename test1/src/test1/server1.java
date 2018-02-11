package test1;

import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;

import org.json.JSONArray;
import org.json.JSONObject;

@Path("/updateRepoMapping")
public class server1{
	

	
	public static HashMap<String, String > Repository_map = new HashMap<String, String>();
	
	@GET
	//@Produces(MediaType.TEXT_HTML)
	@Path("{query}")
	@Consumes(MediaType.TEXT_HTML)
	public String updateRepoMapping(@PathParam("query") String query ) {
		
		
		System.out.println(query);
		
		final byte[] tempbytes = Base64.getDecoder().decode(query);
		String services = new String(tempbytes);
		System.out.println(services);
		
		
		JSONObject newServices = new JSONObject(services);
		String ip = newServices.getString("ip");
		String port = newServices.getString("port");
		
		JSONArray arr = (JSONArray) newServices.get("services");
		System.out.println(arr);
		List<String> list_of_functions = new ArrayList<>();
		
		for(int i = 0; i < arr.length(); i++) {
			
			list_of_functions.add(arr.getString(i));
			
		}
		
		System.out.println(newServices.toString());
		
		for (int i = 0; i < list_of_functions.size(); i++) {
		Repository_map.put(list_of_functions.get(i),ip+"#"+port);
		}
		
		for(String key : Repository_map.keySet())
		{
			System.out.println(key + "--" + Repository_map.get(key));
		}
		
		
		JSONObject object = new JSONObject();
		object.put("status", "success");
		System.out.println(list_of_functions.get(0));
		System.out.println(list_of_functions.get(1));
		return "success";
	
		
}	
	
	@GET
	@Path("/getMapping/{query}")
	@Consumes(MediaType.TEXT_HTML)
	public String getRepoMapping(@PathParam("query") String query ) {
		
		
		System.out.println(query);
		
		final byte[] tempbytes = Base64.getDecoder().decode(query);
		String services = new String(tempbytes);
		System.out.println(services);
		
		JSONObject newServices = new JSONObject(services);
		String s_name = newServices.getString("ServiceName");
		System.out.println("got method name as " + s_name);
		JSONObject Jobject = new JSONObject();
		int flag=1;
		for(String key : Repository_map.keySet())
		{
			if(key.equals(s_name))
			{
				System.out.println("found key :" + key);
				String s =  Repository_map.get(s_name);
				   int index = s.indexOf("#");
				   String ip = s.substring(0, index);
				   System.out.println(ip);
				   String port = s.substring(index+1);
				   System.out.println(port);
				   Jobject.put("status","success");
				   Jobject.put("ip",ip);
				   Jobject.put("port",port);
				   System.out.println(s);
					flag=0;
					break;

			}
			else
				System.out.println("not found" + key);
		}
		
		if(flag==1)
		{
			Jobject.put("status","failure");
			Jobject.put("ip","NULL");
			Jobject.put("port","NULL");
		}
			
		
		
	String encStr = Base64.getEncoder().encodeToString
			(Jobject.toString().getBytes());
		
	 System.out.println(encStr);
		return encStr;

}
}
