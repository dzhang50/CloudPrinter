package com.hackathon.cloudprinter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

@SuppressWarnings("serial")
public class CloudPrinterServlet extends HttpServlet {

	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		String user = req.getParameter("user");
		String mode = req.getParameter("mode"); // sudoku, weather, checkout
		if (user == null) {
			return;
		}
		
		if(mode.equals("checkout")) {
			System.out.println("Doing checkout");
			ArrayList<String> receipt = (ArrayList<String>)BlobstoreHandler.getObj(user);
			if(receipt == null) {
				System.out.println(receipt);
				return;
			}
			String price = receipt.remove(0);
			String urlStr = "http://cloud-printer.appspot.com/pay?actionType=PAY&amount="+price+"&mail=seller_1351904893_biz@hotmail.com&preapprovalKey=PA-4JX53250W7122593R&cancelURL=http://google.com&returnURL=http://www.amazon.com/&currencyCode=USD&startingDate=2012-11-03Z&endingDate=2013-11-02Z";

			System.out.println("Opening " + urlStr);
			
			URL url = new URL(urlStr);
			URLConnection con = url.openConnection();
			con.setConnectTimeout(20000);
			con.setReadTimeout(20000);
			InputStream in = con.getInputStream();
			
			BufferedReader fileReader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
			String sCurrentLine;
			
			
			int numLines = receipt.size();
			System.out.println("Numlines: "+numLines+"\n"+receipt);
			resp.setContentType("text/plain");
			resp.getWriter().println(numLines);
			
			for(String line : receipt) {
				resp.getWriter().println(line);
			}
			// Clear database entry
			BlobstoreHandler.deleteObj(user);			
		}
		if(mode.equals("sudoku")) {
			String s = (String)BlobstoreHandler.getObj(user);
			resp.setContentType("text/plain");
			resp.getWriter().println(s);

			// Clear database entry
			BlobstoreHandler.deleteObj(user);
		}
		if(mode.equals("weather")) {
			WeatherbugContainer weather = (WeatherbugContainer) BlobstoreHandler
					.getObj(user);
			if (weather != null) {
				resp.setContentType("text/plain");
				Forecast f = weather.forecastList.get(0);
				if (f.hasDay) {
					resp.getWriter().println("day");
					resp.getWriter().println(f.dayTitle);
					resp.getWriter().println(f.dayDesc);
					resp.getWriter().println(f.dayPred);
				} else {
					resp.getWriter().println("night");
					resp.getWriter().println(f.nightTitle);
					resp.getWriter().println(f.nightDesc);
					resp.getWriter().println(f.nightPred);
				}
				resp.getWriter().println(f.high);
				resp.getWriter().println(f.low);
				resp.getWriter().println(weather.temperatureUnits);

				// Clear database entry
				BlobstoreHandler.deleteObj(user);
			}
		}	
	}
	
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		String mode = req.getParameter("mode"); // sudoku, weather, checkout
		String user = req.getParameter("user");
		//String zip = req.getParameter("zip");
		String la = req.getParameter("la");
		String lo = req.getParameter("lo");
		System.out.println("User: "+user+", mode: "+mode+", la: "+la+", lo: "+lo);
		
		if(mode.equals("checkout")) {
			if(user != null) {
				List<String> receipt = new ArrayList<String>();
				int iter = 0;
				double total = 0.0;
				String desc = req.getParameter("desc"+iter);
				String price = req.getParameter("price"+iter);
				receipt.add("123 Longhorn Way");
				receipt.add("Austin, TX 78705");
				receipt.add("");
				receipt.add("================================");
				while(desc != null) {
					int len = desc.length();
					String periods = "";
					for(int j = 0; j < 21-len; j++) {
						periods += ".";
					}
					receipt.add(desc+periods+" $"+price);
					total += Float.parseFloat(price);
					iter++;
					desc = req.getParameter("desc"+iter);
					price = req.getParameter("price"+iter);
					System.out.println("desc"+iter+": "+desc+", price: "+price);
				}
				receipt.add("================================");
				receipt.add("");
				receipt.add("SUBTOTAL............. "+String.format("%.2f", total));
				receipt.add("8.25% SALES TAX...... "+String.format("%.2f", (total*0.0825)));
				receipt.add("TOTAL................ "+String.format("%.2f", (total*1.0825)));

				receipt.add(0, String.format("%.2f", total*1.0825));
				System.out.println(receipt);
				BlobstoreHandler.putObj(user, receipt);
			}
		}
		if(mode.equals("sudoku")) {
			if (user != null) {
				String s = "sudoku";
				BlobstoreHandler.putObj(user, s);
			}			
		}
		if (mode.equals("weather")) {
			// String url =
			// "http://i.wxbug.net/REST/Direct/GetForecast.ashx?zip="+ zip +
			// "&nf=1&ht=t&l=en&c=US&api_key=trkcjkyzw4murdjpw77wu8r2";
			String url = "http://i.wxbug.net/REST/Direct/GetForecast.ashx?la="
					+ la + "&lo=" + lo
					+ "&nf=1&ht=t&l=en&c=US&api_key=trkcjkyzw4murdjpw77wu8r2";

			System.out.println("Opening " + url);

			InputStreamReader stream = new InputStreamReader(
					(new URL(url)).openStream());
			BufferedReader fileReader = new BufferedReader(stream);
			String sCurrentLine;
			List<String> weatherData = new ArrayList<String>();
			while ((sCurrentLine = fileReader.readLine()) != null) {
				// System.out.println(sCurrentLine);
				weatherData.add(sCurrentLine);
			}
			String weatherJson = "";
			for (String entry : weatherData) {
				weatherJson += entry;
			}

			WeatherbugContainer weather = new Gson().fromJson(weatherJson,
					WeatherbugContainer.class);
			resp.setContentType("text/plain");
			resp.getWriter().println("Testing");
			resp.getWriter().println(weatherData);
			resp.getWriter().println("Parsed Json:");
			resp.getWriter().println(weather.forecastList.get(0).dayPred);
			resp.getWriter().println(weather.forecastList.get(0).nightPred);

			if (user != null) {
				BlobstoreHandler.putObj(user, weather);
			}
		}
	}
}
