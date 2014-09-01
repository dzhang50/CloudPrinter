package com.hackathon.cloudprinter;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.paypal.svcs.services.AdaptivePaymentsService;
import com.paypal.svcs.types.ap.PreapprovalRequest;
import com.paypal.svcs.types.ap.PreapprovalResponse;
import com.paypal.svcs.types.common.ClientDetailsType;
import com.paypal.svcs.types.common.RequestEnvelope;

@SuppressWarnings("serial")
public class PaypalServlet extends HttpServlet {
/*
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		resp.setContentType("text/plain");
		resp.getWriter().println("Hello, world");
	}
*/	

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		HttpSession session = request.getSession();
		session.setAttribute("url", request.getRequestURI());
		session.setAttribute(
				"relatedUrl",
				"<ul><li><a href='Preapproval'>Preapproval</a></li><li><a href='PreapprovalDetails'>PreapprovalDetails</a></li><li><a href='ConfirmPreapproval'>ConfirmPreapproval</a></li><li><a href='CancelPreapproval'>CancelPreapproval</a></li></ul>");
		RequestEnvelope requestEnvelope = new RequestEnvelope("en_US");
		PreapprovalRequest req = new PreapprovalRequest();

		ClientDetailsType clientDetails = new ClientDetailsType();
		if (request.getParameter("applicationID") != "")
			clientDetails.setApplicationId(request
					.getParameter("applicationID"));
		if (request.getParameter("customerID") != "")
			clientDetails.setCustomerId(request.getParameter("customerID"));
		if (request.getParameter("customerType") != "")
			clientDetails.setCustomerType(request.getParameter("customerType"));
		if (request.getParameter("deviceID") != "")
			clientDetails.setDeviceId(request.getParameter("deviceID"));
		if (request.getParameter("location") != "")
			clientDetails.setGeoLocation(request.getParameter("location"));
		if (request.getParameter("ipAddress") != "")
			clientDetails.setIpAddress(request.getParameter("ipAddress"));
		if (request.getParameter("model") != "")
			clientDetails.setModel(request.getParameter("model"));
		if (request.getParameter("partnerName") != "")
			clientDetails.setPartnerName(request.getParameter("partnerName"));
		req.setClientDetails(clientDetails);
		/*if (request.getParameter("dateOfMonth") != "")
			req.setDateOfMonth(Integer.parseInt(request
					.getParameter("dateOfMonth")));*/
		/*if (request.getParameter("dayOfWeek") != "")
			req.setDayOfWeek(DayOfWeek.fromValue(request
					.getParameter("dayOfWeek")));
		if (request.getParameter("displayMaxTotalAmount") != "")
			req.setDisplayMaxTotalAmount(Boolean.parseBoolean(request
					.getParameter("displayMaxTotalAmount")));
		if (request.getParameter("feesPayer") != "")
			req.setFeesPayer(request.getParameter("feesPayer"));
		if (request.getParameter("ipnNotificationURL") != "")
			req.setIpnNotificationUrl(request
					.getParameter("ipnNotificationURL"));
		if (request.getParameter("maxAmountPerPayment") != "")
			req.setMaxAmountPerPayment(Double.parseDouble(request
					.getParameter("maxAmountPerPayment")));
		if (request.getParameter("maxNumberOfPayments") != "")
			req.setMaxNumberOfPayments(Integer.parseInt(request
					.getParameter("maxNumberOfPayments")));
		if (request.getParameter("maxNumberOfPaymentsPerPeriod") != "")
			req.setMaxNumberOfPaymentsPerPeriod(Integer.parseInt(request
					.getParameter("maxNumberOfPaymentsPerPeriod")));*/
if (request.getParameter("totalAmountOfAllPayments") != "")
			req.setMaxTotalAmountOfAllPayments(Double.parseDouble(request
					.getParameter("totalAmountOfAllPayments")));
		if (request.getParameter("memo") != "")
			req.setMemo(request.getParameter("memo"));
		if (request.getParameter("paymentPeriod") != "")
			req.setPaymentPeriod(request.getParameter("paymentPeriod"));
		if (request.getParameter("pinType") != "")
			req.setPinType(request.getParameter("pinType"));
		if (request.getParameter("senderEmail") != "")
			req.setSenderEmail(request.getParameter("senderEmail"));
if (request.getParameter("cancelURL") != "")
			req.setCancelUrl(request.getParameter("cancelURL"));
if (request.getParameter("currencyCode") != "")
			req.setCurrencyCode(request.getParameter("currencyCode"));
if (request.getParameter("returnURL") != "")
			req.setReturnUrl(request.getParameter("returnURL"));
		req.setRequestEnvelope(requestEnvelope);
if (request.getParameter("startingDate") != "") //2012-11-03
			req.setStartingDate(request.getParameter("startingDate"));
if (request.getParameter("endingDate") != "")
			req.setEndingDate(request.getParameter("endingDate"));
		AdaptivePaymentsService service = new AdaptivePaymentsService(this
				.getServletContext().getRealPath("/")
				+ "/WEB-INF/sdk_config.properties");
		try {
			PreapprovalResponse resp = null;
			try {
				resp = service.preapproval(req);
			} catch (com.paypal.sdk.exceptions.OAuthException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			response.setContentType("text/html");
			if (resp != null) {
				//session.setAttribute("RESPONSE_OBJECT", resp);
				session.setAttribute("lastReq", service.getLastRequest());
				session.setAttribute("lastResp", service.getLastResponse());
				if (resp.getResponseEnvelope().getAck().toString()
						.equalsIgnoreCase("SUCCESS")) {
					Map<Object, Object> map = new LinkedHashMap<Object, Object>();
					map.put("Ack", resp.getResponseEnvelope().getAck());
					map.put("Correlation ID", resp.getResponseEnvelope()
							.getCorrelationId());
					map.put("Time Stamp", resp.getResponseEnvelope()
							.getTimestamp());
map.put("Preapproval Key", resp.getPreapprovalKey());
					map.put("Redirect URL",
							"<a href=https://www.sandbox.paypal.com/cgi-bin/webscr?cmd=_ap-preapproval&preapprovalkey="
									+ resp.getPreapprovalKey()
									+ ">Redirect to paypal</a>");
					session.setAttribute("map", map);
					/*
					PrintWriter out = new PrintWriter(response.getOutputStream());
					out.println("https://www.sandbox.paypal.com/cgi-bin/webscr?cmd=_ap-preapproval&preapprovalkey="
									+ resp.getPreapprovalKey());
					out.flush();*/
					response.sendRedirect("https://www.sandbox.paypal.com/cgi-bin/webscr?cmd=_ap-preapproval&preapprovalkey="
							+ resp.getPreapprovalKey());
				} else {
					//session.setAttribute("Error", resp.getError());
					System.out.println("Bad thing: "+resp.getError().get(0).getMessage());
					System.out.println("Error bad thing: "+resp.getError());
					response.sendRedirect("Error.jsp");
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}

}
