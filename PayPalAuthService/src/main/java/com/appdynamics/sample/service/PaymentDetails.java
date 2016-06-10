package com.appdynamics.sample.service;

import java.util.Random;

public class PaymentDetails {

	private int shipping = 0;
	
	private int subTotal = 0;

	private int tax = 0;

	int paymentTotal = 0;

	public PaymentDetails() {
		paymentTotal = new Integer(new Random().nextInt(100) + 50);
		tax = (int) (.08 * paymentTotal);
		shipping = 5;
		
		/** subTotal + tax + shipping MUST == paymentTotal */
		subTotal = (paymentTotal - tax - shipping);
	}

	public int getPaymentTotal() {
		return paymentTotal;
	}

	public int getShipping() {
		return shipping;
	}

	public int getSubTotal() {
		return subTotal;
	}

	public int getTax() {
		return tax;
	}

	public void setPaymentTotal(int paymentTotal) {
		this.paymentTotal = paymentTotal;
	}
	public void setShipping(int shipping) {
		this.shipping = shipping;
	}
	
	public void setSubTotal(int subTotal) {
		this.subTotal = subTotal;
	}
	
	public void setTax(int tax) {
		this.tax = tax;
	}
}
