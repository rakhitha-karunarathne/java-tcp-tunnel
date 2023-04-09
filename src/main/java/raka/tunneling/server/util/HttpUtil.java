package raka.tunneling.server.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.HashMap;
import org.apache.hc.client5.http.fluent.Request;
import org.apache.hc.client5.http.impl.async.CloseableHttpAsyncClient;
import org.apache.hc.client5.http.impl.async.HttpAsyncClients;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.io.support.ClassicRequestBuilder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import raka.tunneling.server.dto.Response;

public class HttpUtil {
	ObjectMapper mapper = new ObjectMapper();

	/*
	public<T extends Response> T get(String url, HashMap<String,String> params, Class<T> responseClass) throws URISyntaxException, IOException, InterruptedException  {
		String finalUrl = getFinalUrl(url, params);
		
		HttpClient client = HttpClient.newHttpClient();
	    HttpRequest request = HttpRequest.newBuilder().uri(new URI(finalUrl)).GET().build();
	    HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
	    if(response.statusCode() == 200)
	    	return decodeResponse(responseClass, response);
		else
	    	throw new TunnelException(response.statusCode() + "");
	}
	*/

	public <T extends Response> T  post(String url, HashMap<String,String> params, Object payload, Class<T> responseClass) throws URISyntaxException, IOException, InterruptedException {
		String finalUrl = getFinalUrl(url, params);
		
		String response = Request.post(finalUrl)
		.bodyString(mapper.writeValueAsString(payload), ContentType.APPLICATION_JSON)
	    .execute().returnContent().asString();

		return decodeResponse(responseClass, response);


		/*
		HttpClient client = HttpClient.newHttpClient();
	    HttpRequest request = HttpRequest.newBuilder().
	    		uri(new URI(finalUrl))
	    		.POST(BodyPublishers.ofString(mapper.writeValueAsString(payload)))
	    		.header("Content-Type", "application/json")
	    		.build();
	    
	    HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
	    if(response.statusCode() == 200)
	    	return decodeResponse(responseClass, response);
	    else
	    	throw new TunnelException(response.statusCode() + "");
	    	*/
	}
	
	private String getFinalUrl(String url, HashMap<String,String> params) throws UnsupportedEncodingException {
		if(params == null || params.size() == 0)
			return url;
		
		StringBuilder sb = new StringBuilder();
		sb.append(url);
		
		if(url.indexOf("?") < 0) {
			sb.append("?");
		}

		for (String k : params.keySet()) {
			if(sb.charAt(sb.length()-1) != '?')
				sb.append("&");
			
			sb.append(URLEncoder.encode(k, "UTF-8"));
			sb.append("=");
			sb.append(URLEncoder.encode(params.get(k), "UTF-8"));
		}
		return sb.toString();
	}
	
	private <T extends Response> T decodeResponse(Class<T> responseClass, String response)
			throws JsonProcessingException, JsonMappingException {
		T t = mapper.readValue(response, responseClass) ;
		t.checkError();
		return t;
		
	}
}
