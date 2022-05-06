package aar;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ExchangeratesDAO {

	Logger log = Logger.getLogger(ExchangeratesDAO.class.getName());

	final DatabaseService dataBase = new DatabaseService();

	public int addCurrencies(String name) {
		try {
			Currencies currencies = new Currencies(name);
			dataBase.insertCurrencies(currencies);
		} catch (Exception ex) {
			log.log(Level.SEVERE, null, ex);
		}
		return 1;
	}

	public int addExchange(Currencies crypto, Currencies coin, float price, String timeStamp, float priceMax,
			String timePriceMax) {
		try {
			Exchange exchange = new Exchange(crypto, coin, price, timeStamp, priceMax, timePriceMax);
			dataBase.insertExchange(exchange);
		} catch (Exception ex) {
			log.log(Level.SEVERE, null, ex);
		}
		return 1;
	}

	public boolean currenciesExist(String name) {
		boolean currrencyExists = false;
		List<Currencies> currenciesList = dataBase.findAllCurrencies();

		for (int i = 0; i < currenciesList.size() && currrencyExists == false; i++) {
			if (currenciesList.get(i).getName().equalsIgnoreCase(name)) {
				currrencyExists = true;
			}
		}
		return currrencyExists;
	}

	public Currencies getCurrencies(String name) {
		List<Currencies> currenciesList = dataBase.findAllCurrencies();

		for (int i = 0; i < currenciesList.size(); i++) {
			if (currenciesList.get(i).getName().equalsIgnoreCase(name)) {
				Currencies currrencyExist = currenciesList.get(i);
				return currrencyExist;
			}
		}
		return null;
	}

	public List<Currencies> getAllCurrencies() {
		try {
			return dataBase.findAllCurrencies();
		} catch (Exception ex) {
			log.log(Level.SEVERE, null, ex);
		}
		return null;
	}

	public List<Exchange> getAllExchange() {
		try {
			return dataBase.findAllExchange();
		} catch (Exception ex) {
			log.log(Level.SEVERE, null, ex);
		}
		return null;
	}

	public Currencies getCurrencies(Integer id) {
		return dataBase.readCurrencies(id);
	}

	public Exchange getExchange(Integer id) {
		return dataBase.readExchange(id);
	}

	public boolean deleteCurrencies(Integer id) {
		return dataBase.deleteCurrencies(id);
	}

	public boolean deleteExchange(Integer id) {
		return dataBase.deleteExchange(id);
	}

	public boolean deleteExchangeMissingCurrency(Integer idCurrency) {
		String cryptoName = getCurrencies(idCurrency).getName();
		String coinName = getCurrencies(idCurrency).getName();

		List<Exchange> exchangeList = dataBase.findAllExchange();

		for (int i = 0; i < exchangeList.size(); i++) {
			if (exchangeList.get(i).getCoin().getName().equalsIgnoreCase(coinName)
					|| exchangeList.get(i).getCrypto().getName().equalsIgnoreCase(cryptoName)) {
				int idExchange = exchangeList.get(i).getId();
				this.deleteExchange(idExchange);
			}
		}

		return dataBase.deleteCurrencies(idCurrency);
	}

	public boolean exchangeExist(String coin, String crypto) {
		List<Exchange> list = dataBase.findAllExchange();
		boolean find = false;
		for (int i = 0; i < list.size() && find == false; i++) {
			if (list.get(i).getCoin().getName().equalsIgnoreCase(coin)
					&& list.get(i).getCrypto().getName().equalsIgnoreCase(crypto)) {
				find = true;
			}
		}
		return find;
	}
	
	public Exchange getExchangeByName(String coin, String crypto) {
		List<Exchange> list = dataBase.findAllExchange();
		boolean find = false;
		Exchange exchange = null;
		for (int i = 0; i < list.size() && find == false; i++) {
			if (list.get(i).getCoin().getName().equalsIgnoreCase(coin)
					&& list.get(i).getCrypto().getName().equalsIgnoreCase(crypto)) {
				find = true;
				exchange = list.get(i);
			}
		}
		return exchange;
	}
	
	public void changedPrice(Integer id, float price, String time) {
		dataBase.updatePrice(id, price, time);
	}

	public void priceMax(Integer id, float price, String time) {
		dataBase.changePriceMax(id, price, time);
	}

}