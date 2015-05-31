package com.appdynamics.sample.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.apache.log4j.Logger;

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
		String accessToken = "[Stubbed Interface] The access token will be provided here....";

		return accessToken;
	}

	@GET
	@Path("/payment/history/{history}")
	@Produces("text/plain")
	@Consumes("text/plain")
	public String getPaymentHistory(@PathParam("history") String history) throws PayPalRESTException {
		return "[Stubbed Interface]  Account history will be provided here...";
	}

	@GET
	@Path("/payment/paypal/create/{accessToken}")
	@Produces("text/plain")
	@Consumes("text/plain")
	public String createPaypalPayment(@PathParam("accessToken") String accessToken)
			throws PayPalRESTException
	{
		return "[Stubbed Interface]  Submitting a PayPal based payment will be added here";
	}

	@GET 
	@Path("/card/credit/create/{accessToken}/{cardNumber}/{cardType}")
	@Produces("text/plain")
	@Consumes("text/plain")
	public String createCreditCard(
			@PathParam("accessToken") String accessToken,
			@PathParam("cardNumber") String cardNumber,
			@PathParam("cardType") String cardType) throws PayPalRESTException 
	{
		return "[Stubbed Interface]  Adding a credit card will be added here...";
	}



	@GET
	@Path("/payment/credit/create/{accessToken}")
	@Produces("text/plain")
	@Consumes("text/plain")
	public String createPayment(@PathParam("accessToken") String accessToken) throws PayPalRESTException 
	{
		return "[Stubbed Interface]  Submitting a credit card based payment will be added here";
	}

	private CreditCard getCreditCard(Address address) {
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
		creditCard.setNumber("5500005555555559");
		creditCard.setType("mastercard");

		return creditCard;
	}

	private Address getAddress() {
		// ###Address
		// Base Address object used as shipping or billing
		// address in a payment. [Optional]
		Address address = new Address();
		address.setCity("Johnstown");
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
