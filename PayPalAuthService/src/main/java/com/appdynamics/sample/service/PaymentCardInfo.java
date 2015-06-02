package com.appdynamics.sample.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PaymentCardInfo {

	public class PaymentCard {
		private String cardNumber = null;
		private String cardType = null;
		private String cardCity = null;
		
		public String getCardNumber() {
			return cardNumber;
		}

		public void setCardNumber(String cardNumber) {
			this.cardNumber = cardNumber;
		}

		public String getCardType() {
			return cardType;
		}

		public void setCardType(String cardType) {
			this.cardType = cardType;
		}

		public String getCardCity() {
			return cardCity;
		}

		public void setCardCity(String cardCity) {
			this.cardCity = cardCity;
		}
		
		public PaymentCard(String number, String type, String city) {
			this.cardNumber = number;
			this.cardType = type;
			this.cardCity = city;
		}
	}

	private List<PaymentCard> cards = new ArrayList<PaymentCard> ();

	private static PaymentCardInfo instance = null;

	public static PaymentCardInfo instance() {
		if (instance == null) {
			instance = new PaymentCardInfo();
		}

		instance.addCards();

		return instance;
	}

	

	public PaymentCardInfo() {
		// TODO Auto-generated constructor stub
	}

	protected List<PaymentCard> getCards() {
		return cards;
	}

	private void addCards() {

		cards = new ArrayList<PaymentCard> ();

		cards.add(createCard("6011111111111117", "discover", "Austin"));
		cards.add(createCard("5555555555554444", "mastercard", "Houston"));
		cards.add(createCard("5105105105105100", "visa", "Dallas"));

	}

	private PaymentCard createCard(String number, String type, String city) {
		return new PaymentCard(number, type, city);
	}

	public PaymentCard getCard() {

		/** 
		 * intentially create a rand outsid our bounds, so we'll default to having
		 * more visa cards from dallas
		 */
		int index = new Random().nextInt(cards.size()+3);

		PaymentCard card = null;

		try {
			card = cards.get(index);
		}
		catch (Exception e) {

		}
		finally {
			if (card == null) {
				card = createCard("5105105105105100", "visa", "dallas");
			}
		}
		
		return card;
	}
}
