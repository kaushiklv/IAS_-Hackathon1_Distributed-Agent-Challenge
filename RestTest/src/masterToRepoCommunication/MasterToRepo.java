package masterToRepoCommunication;


import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.json.JSONObject;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

@Path("/serviceMapping")
public class MasterToRepo {
	
	@GET
	@Path("/{serviceName}/{methodName}/{var: .+}")
	@Produces(MediaType.APPLICATION_JSON)
	public String testHello(@PathParam("serviceName") String serviceName, @PathParam("methodName") String methodName, @PathParam("var") String var) {
		
		
		JSONObject object = new JSONObject();
		object.put("ServiceName",methodName);
		
		String encStr = Base64.getEncoder().encodeToString(object.toString().getBytes());
		
		String serviceMapIp ="192.168.1.104";
		String serviceMapPort = "8080";
		
		Client client = new Client();
		WebResource webResource = client
				   .resource("http://"+ serviceMapIp + ":" + serviceMapPort + "/test1/updateRepoMapping/getMapping/"+ encStr);
		
		ClientResponse response = webResource.accept("application/json")
                .get(ClientResponse.class);
		
		System.out.println(response);
		String output = response.getEntity(String.class);

		System.out.println(output);
		final byte[] tempbytes = Base64.getDecoder().decode(output);
		String decStr = new String(tempbytes);
		
		
		System.out.println(decStr);
		
		
		
		JSONObject receivedAddress = new JSONObject(decStr);
		
		String statusCode = receivedAddress.getString("status");
		String error = "{\"result\" : \"Service Unavailable\"}";
		
		
		
		if(statusCode == "failure")
			return error;
		
		
		//Now calling the remote slave
		
		String [] paramList = var.split("/");
	//	List<String> paramList = new ArrayList<String>();
		
	//	paramList.add("2");
	//	paramList.add("3");
		
		JSONObject queryToRemote = new JSONObject();
		queryToRemote.put("serviceName",serviceName);
		queryToRemote.put("methodName",methodName);
		queryToRemote.put("arguments", paramList);
		
		String remoteIp = receivedAddress.getString("ip");
		String remotePort = receivedAddress.getString("port");
		
//		String remoteIp = "192.168.1.103";
//		String remotePort = "8501";
		String queryRemoteString = Base64.getEncoder().encodeToString(queryToRemote.toString().getBytes());
		
		
		
		Client clientRemote= new Client();
		WebResource webResourceFromRemote = clientRemote
				   .resource("http://"+ remoteIp + ":" + remotePort + "/D-Agent-2/Book/"+ queryRemoteString);
		
		ClientResponse responseFromRemote = webResourceFromRemote.accept("text/html")
                .get(ClientResponse.class);
		
		System.out.println(responseFromRemote);

		String outputFromRemote = responseFromRemote.getEntity(String.class);
		
		final byte[] tempbytesFromRemote = Base64.getDecoder().decode(outputFromRemote);
		String decStrFromRemote = new String(tempbytesFromRemote);
		
		JSONObject receivedRemote = new JSONObject(decStrFromRemote);
		
		
		String finalOutput = "{\"result :"  + receivedRemote.get("result") + "}";
		
		System.out.println(finalOutput);
		return finalOutput;
		
//		return "Final";
		
	}
}
