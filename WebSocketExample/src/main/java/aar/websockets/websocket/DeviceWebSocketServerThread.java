package aar.websockets.websocket;

import java.io.IOException;
import java.io.StringReader;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.spi.JsonProvider;
import javax.websocket.Session;

import aar.websockets.model.Exchange;

public class DeviceWebSocketServerThread extends Thread {

	private final Set<Session> sessions;
	private static HttpService httpService = new HttpService();

	public DeviceWebSocketServerThread(Set<Session> sessions) {
		this.sessions = sessions;
	}

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

	@Override
	public void run() {

		while (true) {
			try {
				JsonProvider provider = JsonProvider.provider();
				String result = null;

				result = httpService.getExchangeApi();

				if (result != null && result != "") {

					Exchange exchange = new Exchange();

					int a = 0;
					String[] resultAllEchange = result.split(",");

					while (a < resultAllEchange.length) {

						String idPos = resultAllEchange[a + 4];
						String[] idNum = idPos.split(":");
						String id = idNum[1];
						int idNumero = Integer.parseInt(id);
						a = a + 9;

						String exchangeComplete = httpService.getExchangeApiId(idNumero);

						String[] resultAEchange = exchangeComplete.split(",");

						int i = 0;
						String pos1 = resultAEchange[i];
						String pos2 = resultAEchange[i + 1];
						String pos3 = resultAEchange[i + 2];
						String pos4 = resultAEchange[i + 3];
						String pos5 = resultAEchange[i + 4];
						String pos6 = resultAEchange[i + 5];
						String pos7 = resultAEchange[i + 6];
						String pos8 = resultAEchange[i + 7];
						String pos9 = resultAEchange[i + 8];

						String finalResult = pos1 + "," + pos2 + "," + pos3 + "," + pos4 + "," + pos5 + "," + pos6 + ","
								+ pos7 + "," + pos8 + "," + pos9;

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

						JsonObject addMessage = provider.createObjectBuilder().add("action", "update")
								.add("id", exchange.getId()).add("crypto", exchange.getCrypto())
								.add("coin", exchange.getCoin()).add("price", exchange.getPrice())
								.add("timeStamp", exchange.getTimeStamp()).add("priceMax", exchange.getPriceMax())
								.add("timePriceMax", exchange.getTimePriceMax()).build();
						sendToAllConnectedSessions(addMessage);
					}
				}
				Thread.sleep(5000);

			} catch (InterruptedException ex) {
				Thread.currentThread().interrupt();
			}
		}
	}
}