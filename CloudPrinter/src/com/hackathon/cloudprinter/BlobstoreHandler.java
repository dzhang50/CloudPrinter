package com.hackathon.cloudprinter;

import java.nio.ByteBuffer;

import com.google.appengine.api.blobstore.BlobInfo;
import com.google.appengine.api.blobstore.BlobInfoFactory;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.files.AppEngineFile;
import com.google.appengine.api.files.FileService;
import com.google.appengine.api.files.FileServiceFactory;
import com.google.appengine.api.files.FileWriteChannel;

public class BlobstoreHandler {
	
    public static void deleteObj(String user) {
		GlobalInfo gInf = GlobalInfo.getInfo();

		// If doesn't exist, don't do anything
		if(!gInf.users.containsKey(user)) {
			System.out.println("deleteStockInfo: key doesn't exist, IGNORING");
			return;
		}
		
		String key = gInf.users.get(user); // returns an array of keys
		
		// Delete datastore entries
		BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService(); 
		AppEngineFile file = new AppEngineFile(key);
		
		FileService fileService = FileServiceFactory.getFileService();
		
		BlobKey blobKey = fileService.getBlobKey(file);
		try {
			System.out.println("deleteStockInfo: deleting blobStore...");
			blobstoreService.delete(blobKey);
		} catch (Exception e) {
			System.out.println("ERROR: Can't delete user "+user+" & key "+key);
		}
		
		// Remove from GlobalInfo
		System.out.println("deleteStockInfo: removing from stock hashmap...");
		gInf.users.remove(user);
		GlobalInfo.putInfo(gInf);
    }
    
	// Create new blob and overwrite old blob (not a modify operation, slow)
	public static void putObj(String user, Object obj) {
		GlobalInfo gInf = GlobalInfo.getInfo();
		
		// If stock already exists, delete
		deleteObj(user);
		
		try {
			FileService fileService = FileServiceFactory.getFileService();
			AppEngineFile file = fileService.createNewBlobFile("application/x-java-serialized-object");
			
			boolean lock = true;
			FileWriteChannel writeChannel = fileService.openWriteChannel(file, lock);
			
			byte[] tmp = GlobalInfo.toByteArray(obj);
			System.out.println("putBlobStockInfo writing: "+tmp.length+" bytes");
			writeChannel.write(ByteBuffer.wrap(tmp));
			writeChannel.closeFinally();
			System.out.println("User: "+user+", file: "+file.getFullPath());
			gInf.users.put(user, file.getFullPath());
		}
		catch(Exception e) {
			// do nothing
			System.out.println("putBlobStockInfo: FAILED writing to blobStore");
		}
		
		GlobalInfo.putInfo(gInf);
	}

	public static Object getObj(String user) {
		GlobalInfo gInf = GlobalInfo.getInfo();
		System.out.println(gInf.users.values());
		String key = gInf.users.get(user);
		System.out.println(user+": Trying datastore key "+key);
		
		if(key != null) {
			AppEngineFile file = new AppEngineFile(key);
			FileService fileService = FileServiceFactory.getFileService();
			
			BlobKey blobKey = fileService.getBlobKey(file);
			BlobstoreService blobStoreService = BlobstoreServiceFactory.getBlobstoreService();
			
			BlobInfoFactory blobInfoFactory = new BlobInfoFactory(DatastoreServiceFactory.getDatastoreService());
			BlobInfo blobInfo = blobInfoFactory.loadBlobInfo(blobKey);
			int blobSize = (int) blobInfo.getSize();
			Object inf = (Object)GlobalInfo.fromByteArray(blobStoreService.fetchData(blobKey, 0, blobSize));
		
			return inf;
		}
		else {
			return null;
		}
	}
}
