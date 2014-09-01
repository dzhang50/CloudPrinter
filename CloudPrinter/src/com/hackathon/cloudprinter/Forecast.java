package com.hackathon.cloudprinter;

import java.io.Serializable;
import java.util.Date;

public class Forecast implements Serializable {
	public Forecast() {};
	public long dateTime;
	public String dayDesc;
	public String dayIcon;
	public String dayPred;
	public String dayTitle;
	public boolean hasDay;
	public boolean hasNight;
	public String high;
	public String hourly;
	public String low;
	public String nightDesc;
	public String nightIcon;
	public String nightPred;
	public String nightTitle;
	public String title;
}