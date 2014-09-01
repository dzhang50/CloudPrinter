package com.hackathon.cloudprinter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.HashMap;

import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class GlobalInfo {
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Key key;

	@Persistent(defaultFetchGroup = "true", serialized = "true")
    public HashMap<String, String> users = new HashMap<String, String>();

	
	public static void putInfo(GlobalInfo inf) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		
		// Only one instance of GlobalInfo for all users
		Key key = KeyFactory.createKey("GlobalInfo", "global");
		inf.key = key;
		System.out.println("GlobalInfo key: "+key);
		try {
			pm.makePersistent(inf);
		} finally {
			pm.close();
		}
	}
	
	public static GlobalInfo getInfo() {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		
		Key key = KeyFactory.createKey("GlobalInfo", "global");
		int createNew = 0;
		System.out.println("GlobalInfo key: "+key);
		GlobalInfo inf = null;
		try {
			GlobalInfo tmp = pm.getObjectById(GlobalInfo.class, key);
			inf = pm.detachCopy(tmp);
		}
		// If doesn't exist, create new one
		catch (JDOObjectNotFoundException e) {
			createNew = 1;
		} finally {
			if(createNew == 0) {
				pm.close();
			}
		}

		if (createNew == 1) {
			System.out.println("Creating new GlobalInfo");
			inf = new GlobalInfo();
			inf.key = key;
			try {
				pm.makePersistent(inf);
			} finally {
				pm.close();
			}
		}
		return inf;
	}
	
	  
    public static byte[] toByteArray(Object obj) {
    	ByteArrayOutputStream bos = new ByteArrayOutputStream();
    	ObjectOutput out;
    	byte[] b = null; 
		try {
			out = new ObjectOutputStream(bos);
	    	out.writeObject(obj);
	    	b = bos.toByteArray();
	    	out.close();
	    	bos.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}   
		
    	return b;
    }
    
    public static Object fromByteArray(byte[] b) {
    	ByteArrayInputStream bis = new ByteArrayInputStream(b);
    	ObjectInput in;
    	Object o = null;
		try {
			in = new ObjectInputStream(bis);
	    	o = in.readObject();
	    	bis.close();
	    	in.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
    	return o;
    }
    
}
