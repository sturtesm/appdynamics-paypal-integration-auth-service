package com.appdynamics.sample.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.apache.log4j.Logger;

import com.appdynamics.sample.service.PaymentCardInfo.PaymentCard;
import com.appdynamics.sample.service.PaymentDetails;
import com.paypal.api.payments.Address;
import com.paypal.api.payments.Amount;
import com.paypal.api.payments.CreditCard;
import com.paypal.api.payments.Details;
import com.paypal.api.payments.FundingInstrument;
import com.paypal.api.payments.Item;
import com.paypal.api.payments.ItemList;
import com.paypal.api.payments.Payer;
import com.paypal.api.payments.Payment;
import com.paypal.api.payments.PaymentHistory;
import com.paypal.api.payments.Transaction;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;


@Path("/paypal")
public class PaypalServices {

	Logger logger = Logger.getLogger(PaypalServices.class);

	public PaypalServices() {

	}

	@GET
	@Path("/auth")
	@Produces("text/plain")
	public String authenticate(){
		String accessToken = null;

		try {

			logger.info("Processing Request to Generate PayPal authenticate Token");

			// Retrieve the access token from
			// OAuthTokenCredential by passing in
			accessToken = GenerateAccessToken.getAccessToken();

			logger.info("PayPal Access Token=" + accessToken);

		}catch (Exception e) {
			e.printStackTrace();
		}

		return accessToken;
	}

	@GET
	@Path("/payment/history/{history}")
	@Produces("text/plain")
	@Consumes("text/plain")
	public PaymentHistory getPaymentHistory(@PathParam("history") String history) throws PayPalRESTException {

		Map<String, String> containerMap = new HashMap<String, String>();
		Integer historyLength = 10;

		if (history == null || history.trim().length() == 0) {
			historyLength = new Integer(10);
		}
		else {
			try {
				historyLength = new Integer(history);
			}
			catch (Exception e) {
				historyLength = 10;
			}
		}

		containerMap.put("count", historyLength.toString());

		try {

			// ###AccessToken
			// Retrieve the access token from
			// OAuthTokenCredential by passing in
			// It is not mandatory to generate Access Token on a per call basis.
			// Typically the access token can be generated once and
			// reused within the expiry window
			String accessToken = GenerateAccessToken.getAccessToken();

			// ###Retrieve
			// Retrieve the PaymentHistory object by calling the
			// static `get` method
			// on the Payment class, and pass the
			// AccessToken and a ContainerMap object that contains
			// query parameters for paginations and filtering.
			// Refer the API documentation
			// for valid values for keys

			logger.info("Got access token: " + accessToken);

			PaymentHistory paymentHistory = Payment.list(accessToken,
					containerMap);

			logger.info("Payment history retrieved: " + paymentHistory);

			return paymentHistory;

		} catch (PayPalRESTException e) {
			e.printStackTrace();

			throw e;
		}
	}

	@GET
	@Path("/payment/paypal/create/{accessToken}")
	@Produces("text/plain")
	@Consumes("text/plain")
	public Payment createPaypalPayment(@PathParam("accessToken") String accessToken)
			throws PayPalRESTException
			{
		Payment createdPayment = null;

		// ###AccessToken
		// Retrieve the access token from
		// OAuthTokenCredential by passing in
		// ClientID and ClientSecret
		APIContext apiContext = null;

		if (accessToken == null || accessToken.trim().length() == 0) {
			accessToken = GenerateAccessToken.getAccessToken();
		}

		// ### Api Context
		// Pass in a `ApiContext` object to authenticate
		// the call and to send a unique request id
		// (that ensures idempotency). The SDK generates
		// a request id if you do not pass one explicitly.
		apiContext = new APIContext(accessToken);

		// ###Details
		// Let's you specify details of a payment amount.
		Details details = new Details();
		details.setShipping("1");
		details.setSubtotal("5");
		details.setTax("1");

		// ###Amount
		// Let's you specify a payment amount.
		Amount amount = new Amount();
		amount.setCurrency("USD");
		// Total must be equal to sum of shipping, tax and subtotal.
		amount.setTotal("7");
		amount.setDetails(details);

		// ###Transaction
		// A transaction defines the contract of a
		// payment - what is the payment for and who
		// is fulfilling it. Transaction is created with
		// a `Payee` and `Amount` types
		Transaction transaction = new Transaction();
		transaction.setAmount(amount);
		transaction
		.setDescription("This is the payment transaction description.");

		// ### Items
		Item item = new Item();
		item.setName("Ground Coffee 40 oz").setQuantity("1").setCurrency("USD").setPrice("5");
		ItemList itemList = new ItemList();
		List<Item> items = new ArrayList<Item>();
		items.add(item);
		itemList.setItems(items);

		transaction.setItemList(itemList);

		// The Payment creation API requires a list of
		// Transaction; add the created `Transaction`
		// to a List
		List<Transaction> transactions = new ArrayList<Transaction>();
		transactions.add(transaction);

		// ###Payer
		// A resource representing a Payer that funds a payment
		// Payment Method
		// as 'paypal'
		Payer payer = new Payer();
		payer.setPaymentMethod("paypal");

		// ###Payment
		// A Payment Resource; create one using
		// the above types and intent as 'sale'
		Payment payment = new Payment();
		payment.setIntent("sale");
		payment.setPayer(payer);
		payment.setTransactions(transactions);

		// Create a payment by posting to the APIService
		// using a valid AccessToken
		// The return object contains the status;
		try {
			createdPayment = payment.create(apiContext);
			logger.info("Created payment with id = "
					+ createdPayment.getId() + " and status = "
					+ createdPayment.getState());
		} catch (PayPalRESTException e) {
			e.printStackTrace();
		}
		return createdPayment;
			}

	@GET 
	@Path("/card/credit/create/{accessToken}/{cardNumber}/{cardType}")
	@Produces("text/plain")
	@Consumes("text/plain")
	public CreditCard createCreditCard(
			@PathParam("accessToken") String accessToken,
			@PathParam("cardNumber") String cardNumber,
			@PathParam("cardType") String cardType) throws PayPalRESTException {

		if (cardType == null) {
			cardType = "visa";
		}

		if (accessToken == null) {
			// ###AccessToken
			// Retrieve the access token from
			// OAuthTokenCredential by passing in
			// ClientID and ClientSecret
			// It is not mandatory to generate Access Token on a per call basis.
			// Typically the access token can be generated once and
			// reused within the expiry window
			accessToken = GenerateAccessToken.getAccessToken();
		}


		// ###CreditCard
		// A resource representing a credit card that can be
		// used to fund a payment.
		CreditCard creditCard = new CreditCard();
		creditCard.setExpireMonth(11);
		creditCard.setExpireYear(2018);
		creditCard.setNumber(cardNumber);
		creditCard.setType(cardType);

		try {

			// ### Api Context
			// Pass in a `ApiContext` object to authenticate 
			// the call and to send a unique request id 
			// (that ensures idempotency). The SDK generates
			// a request id if you do not pass one explicitly. 
			APIContext apiContext = new APIContext(accessToken);
			// Use this variant if you want to pass in a request id  
			// that is meaningful in your application, ideally 
			// a order id.
			/* 
			 * String requestId = Long.toString(System.nanoTime();
			 * APIContext apiContext = new APIContext(accessToken, requestId ));
			 */

			// ###Save
			// Creates the credit card as a resource
			// in the PayPal vault. The response contains
			// an 'id' that you can use to refer to it
			// in the future payments.
			CreditCard createdCreditCard = creditCard.create(apiContext);

			return createdCreditCard;

		} catch (PayPalRESTException e) {
			e.printStackTrace();

			throw e;
		}
	}



	@GET
	@Path("/payment/credit/create/{accessToken}")
	@Produces("text/plain")
	@Consumes("text/plain")
	public Payment createPayment(@PathParam("accessToken") String accessToken) 
			throws PayPalRESTException, Exception 
			{
		if (accessToken == null) {
			getAccessToken();
		}

		PaymentCardInfo cardInfo = PaymentCardInfo.instance();

		PaymentCard card = cardInfo.getCard();

		/** gets a random billing address */
		Address billingAddress = getAddress(card.getCardCity());


		PaymentDetails paymentDetails = new PaymentDetails();

		/** Payment Details */
		//   Let's you specify details of a payment amount.
		Details details = new Details();
		details.setShipping(new Integer(paymentDetails.getShipping()).toString());
		details.setSubtotal(new Integer(paymentDetails.getSubTotal()).toString());
		details.setTax(new Integer(paymentDetails.getTax()).toString());

		String msg = String.format("Payment Details, Shipping=%d, SubTotal=%d, Tax=%d.  Total=%d",
				paymentDetails.getShipping(), paymentDetails.getSubTotal(), 
				paymentDetails.getTax(), paymentDetails.getPaymentTotal());

		logger.info(msg);

		// ###Amount
		// Let's you specify a payment amount.
		Amount amount = new Amount();
		amount.setCurrency("USD");
		// Total must be equal to sum of shipping, tax and subtotal.
		amount.setTotal(new Integer(paymentDetails.getPaymentTotal()).toString());
		amount.setDetails(details);

		/** gets a new credit card */
		CreditCard creditCard = getCreditCard(card, billingAddress);

		// ###Transaction
		// A transaction defines the contract of a
		// payment - what is the payment for and who
		// is fulfilling it. Transaction is created with
		// a `Payee` and `Amount` types
		Transaction transaction = new Transaction();
		transaction.setAmount(amount);
		transaction
		.setDescription("This is the payment transaction description.");

		// The Payment creation API requires a list of
		// Transaction; add the created `Transaction`
		// to a List
		List<Transaction> transactions = new ArrayList<Transaction>();
		transactions.add(transaction);

		// ###FundingInstrument
		// A resource representing a Payeer's funding instrument.
		// Use a Payer ID (A unique identifier of the payer generated
		// and provided by the facilitator. This is required when
		// creating or using a tokenized funding instrument)
		// and the `CreditCardDetails`
		FundingInstrument fundingInstrument = new FundingInstrument();
		fundingInstrument.setCreditCard(creditCard);

		// The Payment creation API requires a list of
		// FundingInstrument; add the created `FundingInstrument`
		// to a List
		List<FundingInstrument> fundingInstrumentList = new ArrayList<FundingInstrument>();
		fundingInstrumentList.add(fundingInstrument);

		// ###Payer
		// A resource representing a Payer that funds a payment
		// Use the List of `FundingInstrument` and the Payment Method
		// as 'credit_card'
		Payer payer = new Payer();
		payer.setFundingInstruments(fundingInstrumentList);
		payer.setPaymentMethod("credit_card");

		// ###Payment
		// A Payment Resource; create one using
		// the above types and intent as 'sale'
		Payment payment = new Payment();
		payment.setIntent("sale");
		payment.setPayer(payer);
		payment.setTransactions(transactions);
		Payment createdPayment = null;
		try {

			// ### Api Context
			// Pass in a `ApiContext` object to authenticate
			// the call and to send a unique request id
			// (that ensures idempotency). The SDK generates
			// a request id if you do not pass one explicitly.
			APIContext apiContext = new APIContext(accessToken);
			// Use this variant if you want to pass in a request id
			// that is meaningful in your application, ideally
			// a order id.
			/*
			 * String requestId = Long.toString(System.nanoTime(); APIContext
			 * apiContext = new APIContext(accessToken, requestId ));
			 */

			// Create a payment by posting to the APIService
			// using a valid AccessToken
			// The return object contains the status;
			createdPayment = payment.create(apiContext);

			logger.info("Created payment with id = " + createdPayment.getId()
					+ " and status = " + createdPayment.getState());

		} catch (PayPalRESTException e) {
			e.printStackTrace();

			throw e;
		}
		return createdPayment;

	}

	private CreditCard getCreditCard(PaymentCard card, Address address) throws Exception {

		// ###CreditCard
		// A resource representing a credit card that can be
		// used to fund a payment.
		CreditCard creditCard = new CreditCard();
		creditCard.setBillingAddress(address);
		creditCard.setCvv2(111);
		creditCard.setExpireMonth(11);
		creditCard.setExpireYear(2018);
		creditCard.setFirstName("Joe");
		creditCard.setLastName("Shopper");
		creditCard.setNumber(card.getCardNumber());
		creditCard.setType(card.getCardType());

		return creditCard;
	}

	private Address getAddress(String city) {
		// ###Address
		// Base Address object used as shipping or billing
		// address in a payment. [Optional]
		Address address = new Address();
		address.setCity(city);
		address.setCountryCode("US");
		address.setLine1("52 N Main ST");
		address.setPostalCode("43210");
		address.setState("OH");

		return address;
	}

	private String getAccessToken() throws PayPalRESTException {
		// ###AccessToken
		// Retrieve the access token from
		// OAuthTokenCredential by passing in
		// ClientID and ClientSecret
		// It is not mandatory to generate Access Token on a per call basis.
		// Typically the access token can be generated once and
		// reused within the expiry window
		return GenerateAccessToken.getAccessToken();

	}
}
