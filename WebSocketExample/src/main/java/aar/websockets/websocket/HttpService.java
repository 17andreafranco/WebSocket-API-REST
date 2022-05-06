package aar.websockets.websocket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HttpService {

	public String getExchangeApi () {
		String result = null;

		try {
			
			URL url = new URL("http://localhost:8080/RestWSExample/rest/exchange-rates");
			HttpURLConnection api = (HttpURLConnection) url.openConnection();

			api.setRequestMethod("GET");
			api.setDoOutput(true);

			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(api.getInputStream()));

			String responseLine = null;
			StringBuilder response = new StringBuilder();
			while ((responseLine = bufferedReader.readLine()) != null) {
				response.append(responseLine.trim());
			}

			result = response.toString().replaceAll("[\\[\\]]", "");

		} catch (MalformedURLException e) {
			Logger.getLogger(DeviceWebSocketServer.class.getName()).log(Level.SEVERE, null, e);
		} catch (IOException e) {
			Logger.getLogger(DeviceWebSocketServer.class.getName()).log(Level.SEVERE, null, e);
		}

		return result;
	}
	
	public String getExchangeApiId (int id) {
		String response = null;
		try {
			
			URL url = new URL("http://localhost:8080/RestWSExample/rest/exchange-rates/current/"+id);
			HttpURLConnection api = (HttpURLConnection) url.openConnection();

			api.setRequestMethod("GET");
			api.setDoOutput(true);

			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(api.getInputStream()));
		
			response = bufferedReader.readLine().toString();
		} catch (MalformedURLException e) {
			Logger.getLogger(DeviceWebSocketServer.class.getName()).log(Level.SEVERE, null, e);
		} catch (IOException e) {
			Logger.getLogger(DeviceWebSocketServer.class.getName()).log(Level.SEVERE, null, e);
		}

		return response;
	}
}