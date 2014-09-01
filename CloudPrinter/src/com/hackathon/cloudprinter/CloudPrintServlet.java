package com.hackathon.cloudprinter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

@SuppressWarnings("serial")
public class CloudPrintServlet extends HttpServlet {
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		resp.setContentType("text/plain");
		resp.getWriter().println("Hello, world");
		String tmp = req.getParameter("tmp");
		System.out.println(tmp);
	}
	
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
	//public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		String user = req.getParameter("user");
		String zip = req.getParameter("zip");
		
		String url = "http://i.wxbug.net/REST/Direct/GetForecast.ashx?zip="+zip+"&nf=1&ht=t&l=en&c=US&api_key=trkcjkyzw4murdjpw77wu8r2";
		
		System.out.println("Opening "+url);

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
		for(String entry : weatherData) {
			weatherJson += entry;
		}

		WeatherbugContainer weather = new Gson().fromJson(weatherJson, WeatherbugContainer.class);
		resp.setContentType("text/plain");
		resp.getWriter().println("Testing");
		resp.getWriter().println(weatherData);
		resp.getWriter().println("Parsed Json:");
		resp.getWriter().println(weather.forecastList.get(0).dayPred);
	}
}
