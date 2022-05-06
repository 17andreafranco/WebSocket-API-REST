package aar;

import java.time.LocalDateTime;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.FormParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/")
public class CurrenciesService {

	ExchangeratesDAO ExchangeratesDAO = new ExchangeratesDAO();

	Logger log = Logger.getLogger(CurrenciesService.class.getName());

	String CRYPTONATOR = "https://api.cryptonator.com/api/";

	DateTimeFormatter DATE = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

	@GET
	@Path("/currencies")
	@Produces(MediaType.APPLICATION_JSON)
	public List<Currencies> getCurrencies() {
		return ExchangeratesDAO.getAllCurrencies();
	}

	@POST
	@Path("/currencies")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes("application/x-www-form-urlencoded")
	public Response addCurrencies(@FormParam("name") String name) {
		if (!(ExchangeratesDAO.currenciesExist(name))) {
			try {
				ExchangeratesDAO.addCurrencies(name);
				log.log(Level.INFO, "Inserted currency " + name);

				return Response.status(200).entity(ExchangeratesDAO.getCurrencies(name)).build();
			} catch (Exception e) {
				return Response.status(400).build();
			}
		} else {
			return Response.status(400).build();
		}
	}

	@DELETE
	@Path("/currencies/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes("application/x-www-form-urlencoded")
	public Response removeCurrency(@PathParam("id") Integer id) {

		try {
			boolean deletedOK = ExchangeratesDAO.deleteExchangeMissingCurrency(id);
			if (deletedOK) {
				log.log(Level.INFO, "deleted currency " + id + " correctly ");
				return Response.status(200).entity(ExchangeratesDAO.getAllCurrencies()).build();
			}
			return Response.status(400).build();
		} catch (Exception e) {
			return Response.status(400).build();
		}
	}

	@GET
	@Path("/exchange-rates")
	@Produces(MediaType.APPLICATION_JSON)
	public List<Exchange> getExchange() {
		return ExchangeratesDAO.getAllExchange();
	}

	@GET
	@Path("/exchange-rates/current/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Exchange getExchangeById(@PathParam("id") Integer id) {
		String date = DATE.format(LocalDateTime.now());

		String cryptonator = ClientBuilder.newClient()
				.target(CRYPTONATOR + "ticker/" + ExchangeratesDAO.getExchange(id).getCrypto().getName() + "-"
						+ ExchangeratesDAO.getExchange(id).getCoin().getName())
				.request().accept(MediaType.APPLICATION_JSON).get(String.class);

		String[] cryptonatorS = cryptonator.split(",");
		String wholePrice = cryptonatorS[2];

		String[] priceS = wholePrice.split(":");
		String valuePriceS = priceS[1];

		String price = valuePriceS.replace("\"", "");

		Float valuePrice = Float.parseFloat(price);

		ExchangeratesDAO.priceMax(id, valuePrice, date);
		ExchangeratesDAO.changedPrice(id, valuePrice, date);

		return ExchangeratesDAO.getExchange(id);
	}

	@GET
	@Path("/exchange-rates/max/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Exchange getMaxExchangeById(@PathParam("id") Integer id) {
		return ExchangeratesDAO.getExchange(id);
	}

	@POST
	@Path("/exchange-rates")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes("application/x-www-form-urlencoded")
	public Response addExchange(@FormParam("coin") String coin, @FormParam("crypto") String crypto) {
		String date = DATE.format(LocalDateTime.now());

		if (ExchangeratesDAO.currenciesExist(coin) && ExchangeratesDAO.currenciesExist(crypto)) {
			if (!ExchangeratesDAO.exchangeExist(coin, crypto)) {
					try {
						String cryptonator = ClientBuilder.newClient()
								.target(CRYPTONATOR + "ticker/" + crypto + "-" + coin).request()
								.accept(MediaType.APPLICATION_JSON).get(String.class);

						String[] cryptonatorS = cryptonator.split(",");
						String wholePrice = cryptonatorS[2];

						String[] wholePriceS = wholePrice.split(":");
						String valuePriceCryptoS = wholePriceS[1];

						String price = valuePriceCryptoS.replace("\"", "");
						float valuePriceCrypto = Float.parseFloat(price);

						float valuePriceMax = valuePriceCrypto;
						String dateMax = date;

						Currencies cryptoC = ExchangeratesDAO.getCurrencies(crypto);
						Currencies coinC = ExchangeratesDAO.getCurrencies(coin);

						ExchangeratesDAO.addExchange(cryptoC, coinC, valuePriceCrypto, date, valuePriceMax, dateMax);

						log.log(Level.INFO, "Inserted exchange " + coin + "and" + crypto);

						return Response.status(200).entity(ExchangeratesDAO.getExchangeByName(coin, crypto)).build();

					} catch (Exception e) {
						return Response.status(400).build();
					}
			}
			return Response.status(400).build();
		} else {
			return Response.status(400).build();
		}
	}

	@DELETE
	@Path("/exchange-rates/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes("application/x-www-form-urlencoded")
	public Response removeExchange(@PathParam("id") Integer id) {
		try {
			boolean deletedOk = ExchangeratesDAO.deleteExchange(id);

			if (deletedOk == true)
				log.log(Level.INFO, "deleted exchange " + id + " correctly ");

			return Response.status(200).entity(ExchangeratesDAO.getAllExchange()).build();
		} catch (Exception e) {
			return Response.status(400).build();
		}
	}
}