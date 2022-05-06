package aar.websockets.model;

public class Exchange {

	private int id;
	private String crypto;
	private String coin;
	private float price;
	private String timeStamp;
	private float priceMax;
	private String timePriceMax;
	private String type;

	public int getId() {
		return id;
	}

	public String getCrypto() {
		return crypto;
	}

	public String getCoin() {
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
	
	public String getType() {
        return type;
    }

	public void setId(int id) {
		this.id = id;
	}

	public void setCrypto(String crypto) {
		this.crypto = crypto;
	}

	public void setCoin(String coin) {
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
	
    public void setType(String type) {
        this.type = type;
    }

	public Exchange() {
	}

	public Exchange(String crypto, String coin, float price, String timeStamp, float priceMax,
			String timePriceMax, String type) {
		this.crypto = crypto;
		this.coin = coin;
		this.price = price;
		this.timeStamp = timeStamp;
		this.priceMax = priceMax;
		this.timePriceMax = timePriceMax;
		this.type = type;
	}
}
