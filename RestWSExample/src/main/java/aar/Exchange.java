package aar;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Exchange implements Serializable {

	// serialVersionUID obligatorio en objetos serializables
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private int id;

	@Column(nullable = false)
	private Currencies crypto;

	@Column(nullable = false)
	private Currencies coin;

	@Column(nullable = false)
	private float price;

	@Column(nullable = false)
	private String timeStamp;

	@Column(nullable = false)
	private float priceMax;

	@Column(nullable = false)
	private String timePriceMax;

	public int getId() {
		return id;
	}

	public Currencies getCrypto() {
		return crypto;
	}

	public Currencies getCoin() {
		return coin;
	}

	public float getPrice() {
		return price;
	}

	public String getTimeStamp() {
		return timeStamp;
	}

	public float getPriceMax() {
		return priceMax;
	}

	public String getTimePriceMax() {
		return timePriceMax;
	}
	
	public void setId(int id) {
		this.id = id;
	}

	public void setCrypto(Currencies crypto) {
		this.crypto = crypto;
	}

	public void setCoin(Currencies coin) {
		this.coin = coin;
	}

	public void setPrice(float price) {
		this.price = price;
	}

	public void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
	}

	public void setPriceMax(float priceMax) {
		this.priceMax = priceMax;
	}

	public void setTimePriceMax(String timePriceMax) {
		this.timePriceMax = timePriceMax;
	}

	public Exchange() {
	}

	public Exchange(Currencies crypto, Currencies coin, float price, String timeStamp, float priceMax,
			String timePriceMax) {
		this.crypto = crypto;
		this.coin = coin;
		this.price = price;
		this.timeStamp = timeStamp;
		this.priceMax = priceMax;
		this.timePriceMax = timePriceMax;
	}
}
