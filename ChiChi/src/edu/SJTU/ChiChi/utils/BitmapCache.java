package edu.SJTU.ChiChi.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.util.LruCache;

@SuppressLint("NewApi")
public class BitmapCache{
	
	private static boolean MemoryCacheInited = false;
	private static boolean DiskCacheInited = false;
	
	public static BitmapMemoryCache mMemoryCache;
	public static BitmapDiskCache mDiskCache;
	
	Context context;

	public BitmapCache(Context context){
		this.context = context;
		InitMemoryCache();
		InitDiskCache();
	}
	
	public Bitmap get(String key) {
		key = urltokey(key);
		Bitmap res = mMemoryCache.get(key);
		if (res != null) return res;
		res = mDiskCache.get(key);
		if (res != null) {
			mMemoryCache.add(key, res);
			return res;
		}
		return null;
	}
	
	public void put(String key, Bitmap bitmap) {
		key = urltokey(key);
		mMemoryCache.add(key, bitmap);
		mDiskCache.add(key, bitmap);
	}
	
	public void putOnlyDisk(String key, Bitmap bitmap) {
		key = urltokey(key);
		mDiskCache.add(key, bitmap);
	}

	public String urltokey(String url){
		//TODO
		String key = url.replaceAll("[^a-z0-9_-]", "");
		return key;
	}
	
	private void InitMemoryCache() {
		if(MemoryCacheInited) return;
		mMemoryCache = new BitmapMemoryCache();
		MemoryCacheInited = true;
	}
	
	private void InitDiskCache() {
		if(DiskCacheInited) return; 
		mDiskCache = new BitmapDiskCache(context);
		MemoryCacheInited = true;
	}
	

	
	public static class BitmapDiskCache extends DiskLruImageCache{
		
		final static String DISK_CACHE_SUBDIR = "img";
		final static int APP_VERSION = 1;
		final static int DISK_CACHE_SIZE = 1024 * 1024 * 10; //10MB
	    final static CompressFormat COMPRESSFORMART = CompressFormat.JPEG;
	    final static int COMPRESS_QUALITY = 70;
		
		private final static Object mDiskCacheLock = new Object();
		
		public BitmapDiskCache(Context context){
			super(context, DISK_CACHE_SUBDIR, DISK_CACHE_SIZE, COMPRESSFORMART, COMPRESS_QUALITY);
		}
		
		public void add(String key, Bitmap bitmap) {
		    synchronized (mDiskCacheLock) {
		    	super.put(key, bitmap);
		    }
		}
		
		public Bitmap get(String key){
			synchronized (mDiskCacheLock) {
				return super.getBitmap(key);
			}
		}
	}
	
	public static class BitmapMemoryCache extends LruCache<String, Bitmap> {

		final static int maxMemory = (int) (Runtime.getRuntime().maxMemory()/1024);
		final static int cacheSize = maxMemory / 8;
		
		public BitmapMemoryCache(int cacheSize) {
			super(cacheSize);
		}
		
		public BitmapMemoryCache(){
			this(cacheSize);
		}
	
	    @Override
	    protected int sizeOf(String key, Bitmap bitmap) {
	        // The cache size will be measured in kilobytes rather than
	        // number of items.
	        return bitmap.getByteCount() / 1024;
	    }
	    
	    public void add(String key, Bitmap bitmap) {
	    	if (get(key) == null) {
	    		put(key, bitmap);
	    	}
	    }
	}
}