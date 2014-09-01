package com.hackathon.cloudprinter;

import java.io.Serializable;
import java.util.List;


public class WeatherbugContainer implements Serializable  {
	
	public WeatherbugContainer() {};
	public List<Forecast> forecastList;
	public List<Integer> notifications;
	public String temperatureUnits;
}


