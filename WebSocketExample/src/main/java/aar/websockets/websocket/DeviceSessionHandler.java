package aar.websockets.websocket;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.ApplicationScoped;
import java.util.HashSet;
import java.util.Set;
import javax.json.JsonObject;
import javax.json.spi.JsonProvider;
import javax.websocket.Session;

import java.net.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import javax.json.JsonReader;
import java.io.StringReader;
import javax.json.Json;

import aar.websockets.model.Exchange;

@ApplicationScoped
public class DeviceSessionHandler {

	private final Set<Session> sessions = new HashSet<>();
	private static HttpService httpService = new HttpService();
	private DeviceWebSocketServerThread thread = new DeviceWebSocketServerThread(sessions);
	private boolean createdThread = false;

	public void addSession(Session session) {
		sessions.add(session);
		thread = new DeviceWebSocketServerThread(sessions);
		JsonProvider provider = JsonProvider.provider();
		String result = null;

		result = httpService.getExchangeApi();

		if (result != null && result != "") {
			Exchange exchange = new Exchange();
			int i = 0;
			String[] result1 = result.split(",");

			while (result1.length > i) {
				String pos1 = result1[i];
				String pos2 = result1[i + 1];
				String pos3 = result1[i + 2];
				String pos4 = result1[i + 3];
				String pos5 = result1[i + 4];
				String pos6 = result1[i + 5];
				String pos7 = result1[i + 6];
				String pos8 = result1[i + 7];
				String pos9 = result1[i + 8];
				String finalResult = pos1 + "," + pos2 + "," + pos3 + "," + pos4 + "," + pos5 + "," + pos6 + "," + pos7
						+ "," + pos8 + "," + pos9;
				i = i + 9;

				try (JsonReader reader = Json.createReader(new StringReader(finalResult))) {
					JsonObject jsonMessage = reader.readObject();
					JsonObject innerJsonObject = jsonMessage.getJsonObject("coin");
					exchange.setCoin(innerJsonObject.getString("name"));
					JsonObject innerJsonObjectCrypto = jsonMessage.getJsonObject("crypto");
					exchange.setCrypto(innerJsonObjectCrypto.getString("name"));
					exchange.setId(jsonMessage.getJsonNumber("id").intValue());
					exchange.setPrice(jsonMessage.getJsonNumber("price").longValue());
					exchange.setTimeStamp(jsonMessage.getString("timeStamp"));
					exchange.setPriceMax(jsonMessage.getJsonNumber("priceMax").longValue());
					exchange.setTimePriceMax(jsonMessage.getString("timePriceMax"));
				}

				JsonObject addMessage = provider.createObjectBuilder().add("action", "add").add("id", exchange.getId())
						.add("crypto", exchange.getCrypto()).add("coin", exchange.getCoin())
						.add("price", exchange.getPrice()).add("timeStamp", exchange.getTimeStamp())
						.add("priceMax", exchange.getPriceMax()).add("timePriceMax", exchange.getTimePriceMax())
						.build();
				sendToSession(session, addMessage);
			}
		}
		if (!createdThread) {
			thread.start();
			createdThread = true;
		}
	}

	public void removeSession(Session session) {
		sessions.remove(session);
	}

	public void addExchange(Exchange exchange, Session session) {

		StringBuilder response = new StringBuilder();

		try {
			URL url = new URL("http://localhost:8080/RestWSExample/rest/exchange-rates");
			HttpURLConnection api = (HttpURLConnection) url.openConnection();

			String inputString = "crypto=" + exchange.getCrypto() + "&coin=" + exchange.getCoin();

			api.setRequestMethod("POST");
			api.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			api.setRequestProperty("Content-Length", String.valueOf(inputString.length()));
			api.setDoOutput(true);

			try (OutputStream outputStream = api.getOutputStream()) {
				byte[] input = inputString.getBytes("UTF-8");
				outputStream.write(input, 0, input.length);
			}

			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(api.getInputStream()));

			String responseLine = null;
			while ((responseLine = bufferedReader.readLine()) != null) {
				response.append(responseLine.trim());

			}

		} catch (MalformedURLException e) {
			Logger.getLogger(DeviceWebSocketServer.class.getName()).log(Level.SEVERE, null, e);
		} catch (IOException e) {
			Logger.getLogger(DeviceWebSocketServer.class.getName()).log(Level.SEVERE, null, e);
		}

		try (JsonReader reader = Json.createReader(new StringReader(response.toString()))) {
			JsonObject jsonMessage = reader.readObject();
			exchange.setId(jsonMessage.getJsonNumber("id").intValue());
			exchange.setPrice(jsonMessage.getJsonNumber("price").longValue());
			exchange.setTimeStamp(jsonMessage.getString("timeStamp"));
			exchange.setPriceMax(jsonMessage.getJsonNumber("priceMax").longValue());
			exchange.setTimePriceMax(jsonMessage.getString("timePriceMax"));
		}

		JsonProvider provider = JsonProvider.provider();
		JsonObject addMessage = provider.createObjectBuilder().add("action", "add").add("id", exchange.getId())
				.add("crypto", exchange.getCrypto()).add("coin", exchange.getCoin()).add("price", exchange.getPrice())
				.add("timeStamp", exchange.getTimeStamp()).add("priceMax", exchange.getPriceMax())
				.add("timePriceMax", exchange.getTimePriceMax()).build();
		sendToAllConnectedSessions(addMessage);
	}

	public void removeExchange(int id) {
		Exchange exchange = getExchangeById(id);

		if (exchange != null) {
			try {
				URL url = new URL("http://localhost:8080/RestWSExample/rest/exchange-rates/" + id);
				HttpURLConnection api = (HttpURLConnection) url.openConnection();

				api.setDoOutput(true);
				api.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
				api.setRequestMethod("DELETE");
				api.connect();

				BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(api.getInputStream()));

			} catch (MalformedURLException e) {
				Logger.getLogger(DeviceWebSocketServer.class.getName()).log(Level.SEVERE, null, e);
			} catch (IOException e) {
				Logger.getLogger(DeviceWebSocketServer.class.getName()).log(Level.SEVERE, null, e);
			}

			JsonProvider provider = JsonProvider.provider();
			JsonObject removeMessage = provider.createObjectBuilder().add("action", "remove").add("id", id).build();

			sendToAllConnectedSessions(removeMessage);
		}
	}

	private Exchange getExchangeById(int id) {
		StringBuilder response = new StringBuilder();
		JsonProvider provider = JsonProvider.provider();

		try {
			URL url = new URL("http://localhost:8080/RestWSExample/rest/exchange-rates/current/" + id);
			HttpURLConnection api = (HttpURLConnection) url.openConnection();

			api.setRequestMethod("GET");
			api.setDoOutput(true);

			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(api.getInputStream()));

			String responseLine = null;
			while ((responseLine = bufferedReader.readLine()) != null) {
				response.append(responseLine.trim());
			}

		} catch (MalformedURLException e) {
			Logger.getLogger(DeviceWebSocketServer.class.getName()).log(Level.SEVERE, null, e);
		} catch (IOException e) {
			Logger.getLogger(DeviceWebSocketServer.class.getName()).log(Level.SEVERE, null, e);
		} catch (IllegalArgumentException e) {
			Logger.getLogger(DeviceWebSocketServer.class.getName()).log(Level.SEVERE, null, e);
		}

		Exchange exchange = new Exchange();

		try (JsonReader reader = Json.createReader(new StringReader(response.toString()))) {
			JsonObject jsonMessage = reader.readObject();
			JsonObject innerJsonObject = jsonMessage.getJsonObject("coin");
			exchange.setCoin(innerJsonObject.getString("name"));
			JsonObject innerJsonObjectCrypto = jsonMessage.getJsonObject("crypto");
			exchange.setCrypto(innerJsonObjectCrypto.getString("name"));
			exchange.setId(jsonMessage.getJsonNumber("id").intValue());
			exchange.setPrice(jsonMessage.getJsonNumber("price").longValue());
			exchange.setTimeStamp(jsonMessage.getString("timeStamp"));
			exchange.setPriceMax(jsonMessage.getJsonNumber("priceMax").longValue());
			exchange.setTimePriceMax(jsonMessage.getString("timePriceMax"));
		}
		return exchange;
	}

	public void getAllExchanges() {
		JsonProvider provider = JsonProvider.provider();
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

		Exchange exchange = new Exchange();

		int i = 0;
		String[] result1 = result.split(",");

		while (result1.length > i) {
			String pos1 = result1[i];
			String pos2 = result1[i + 1];
			String pos3 = result1[i + 2];
			String pos4 = result1[i + 3];
			String pos5 = result1[i + 4];
			String pos6 = result1[i + 5];
			String pos7 = result1[i + 6];
			String pos8 = result1[i + 7];
			String pos9 = result1[i + 8];
			String finalResult = pos1 + "," + pos2 + "," + pos3 + "," + pos4 + "," + pos5 + "," + pos6 + "," + pos7
					+ "," + pos8 + "," + pos9;
			i = i + 9;

			try (JsonReader reader = Json.createReader(new StringReader(finalResult))) {
				JsonObject jsonMessage = reader.readObject();
				JsonObject innerJsonObject = jsonMessage.getJsonObject("coin");
				exchange.setCoin(innerJsonObject.getString("name"));
				JsonObject innerJsonObjectCrypto = jsonMessage.getJsonObject("crypto");
				exchange.setCrypto(innerJsonObjectCrypto.getString("name"));
				exchange.setId(jsonMessage.getJsonNumber("id").intValue());
				exchange.setPrice(jsonMessage.getJsonNumber("price").longValue());
				exchange.setTimeStamp(jsonMessage.getString("timeStamp"));
				exchange.setPriceMax(jsonMessage.getJsonNumber("priceMax").longValue());
				exchange.setTimePriceMax(jsonMessage.getString("timePriceMax"));
			}

			JsonObject addMessage = provider.createObjectBuilder().add("action", "add").add("id", exchange.getId())
					.add("crypto", exchange.getCrypto()).add("coin", exchange.getCoin())
					.add("price", exchange.getPrice()).add("timeStamp", exchange.getTimeStamp())
					.add("priceMax", exchange.getPriceMax()).add("timePriceMax", exchange.getTimePriceMax()).build();
			sendToAllConnectedSessions(addMessage);
		}
	}

	public boolean exchangeExists(String coin, String crypto) {
		boolean exchangeExist = false;
		String result = null;

		result = httpService.getExchangeApi();

		String[] resultAllEchange = result.split(",");

		int a = 0;

		while (a < resultAllEchange.length && exchangeExist == false) {

			String posCoin = resultAllEchange[a + 1];
			String posCrypto = resultAllEchange[a + 3];

			String[] nameCoin = posCoin.split(":");
			String nCoin = nameCoin[1];

			String[] nameCrypro = posCrypto.split(":");
			String nCrypto = nameCrypro[1];

			String coinExchange = removeCharacter(nCoin);
			String cryptoExchange = removeCharacter(nCrypto);

			if (coinExchange.equalsIgnoreCase(coin) && cryptoExchange.equalsIgnoreCase(crypto)) {
				exchangeExist = true;
				return exchangeExist;
			}
			a = a + 9;
		}
		return exchangeExist;
	}

	public static String removeCharacter(String currencie) {

		StringBuilder sb = new StringBuilder(currencie);

		sb.deleteCharAt(currencie.length() - 1);
		sb.deleteCharAt(currencie.length() - 2);
		sb.deleteCharAt(0);

		return sb.toString();
	}
	
	/*public boolean getAllCurrencies(String coin) {
		String result = null;
		boolean exists = false;

		try {
			URL url = new URL("http://localhost:8080/RestWSExample/rest/currencies");
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

		int i = 0;
		String[] currency1 = result.split(",");

		while (currency1.length > i && exists==false) {
			String currency = currency1[i+1];
			String[] nameCoin = currency.split(":");
			String nameCurrency = nameCoin[1];
			String coinExchange = removeCharacter(nameCurrency);

			if (coin.equalsIgnoreCase(coinExchange)) {
				exists = true;
			}
			i=i+3;
			
		}
		return exists;
	}*/

	private void sendToAllConnectedSessions(JsonObject message) {
		for (Session session : sessions) {
			sendToSession(session, message);
		}
	}

	private void sendToSession(Session session, JsonObject message) {
		try {
			session.getBasicRemote().sendText(message.toString());
		} catch (IOException ex) {
			sessions.remove(session);
			Logger.getLogger(DeviceSessionHandler.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
}