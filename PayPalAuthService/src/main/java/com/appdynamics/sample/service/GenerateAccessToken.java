package com.appdynamics.sample.service;

import com.paypal.base.ConfigManager;
import com.paypal.base.rest.OAuthTokenCredential;
import com.paypal.base.rest.PayPalRESTException;

public class GenerateAccessToken {

	public static String getAccessToken() throws PayPalRESTException {

		// ###AccessToken
		// Retrieve the access token from
		// OAuthTokenCredential by passing in
		// ClientID and ClientSecret
		String clientID = ConfigManager.getInstance().getValue("clientID");
		String clientSecret = ConfigManager.getInstance().getValue(
				"clientSecret");

		return new OAuthTokenCredential(clientID, clientSecret).getAccessToken();
	}
}
