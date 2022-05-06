package aar.websockets.websocket;

import java.io.StringReader;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import javax.enterprise.context.ApplicationScoped;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;

import aar.websockets.model.Exchange;

@ApplicationScoped
@ServerEndpoint("/actions")
public class DeviceWebSocketServer {

	private static DeviceSessionHandler sessionHandler = new DeviceSessionHandler();

	public DeviceWebSocketServer() {
		System.out.println("class loaded " + this.getClass());
	}

	@OnOpen
	public void onOpen(Session session) {
		sessionHandler.addSession(session);
		System.out.println("cliente suscrito, sesion activa");
	}

	@OnClose
	public void onClose(Session session) {
		sessionHandler.removeSession(session);
		System.out.println("cliente cierra conexión, sesion eliminada");
	}

	@OnError
	public void onError(Throwable error) {
		Logger.getLogger(DeviceWebSocketServer.class.getName()).log(Level.SEVERE, null, error);
	}

	@OnMessage
	public void onMessage(String message, Session session) {
		try (JsonReader reader = Json.createReader(new StringReader(message))) {
			JsonObject jsonMessage = reader.readObject();

			if ("add".equals(jsonMessage.getString("action"))) {
				Exchange exchange = new Exchange();
				if (!sessionHandler.exchangeExists(jsonMessage.getString("coin"), jsonMessage.getString("crypto"))) {
					if (!jsonMessage.getString("crypto").equalsIgnoreCase(jsonMessage.getString("coin"))) {
						//if(sessionHandler.getAllCurrencies(jsonMessage.getString("crypto")) && sessionHandler.getAllCurrencies(jsonMessage.getString("coin"))) {
							exchange.setCrypto(jsonMessage.getString("crypto"));
							exchange.setCoin(jsonMessage.getString("coin"));
							sessionHandler.addExchange(exchange, session);
						//}
					}
				}
			}

			if ("remove".equals(jsonMessage.getString("action"))) {
				int id = (int) jsonMessage.getInt("id");
				sessionHandler.removeExchange(id);
			}
		}
	}
}