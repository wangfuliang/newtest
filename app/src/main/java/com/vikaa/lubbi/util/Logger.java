package com.vikaa.lubbi.util;
import android.util.Log;
/**
 * @author Donal Tong 
 * momoka
 * 2012-12-27
 */
public class Logger {
	/**
	 * log tag
	 */
	private String tag = "LeGou";
	/**
	 * debug or not
	 */
	private static volatile boolean debug = true;
 
	private static Logger instance = new Logger();
 
	private Logger() {
 
	}
 
	public static Logger getLogger() {
		return instance;
	}
	
	/**
	 * 获取函数名称
	 */
	private String getFunctionName() {
		StackTraceElement[] sts = Thread.currentThread().getStackTrace();
 
		if (sts == null) {
			return null;
		}
 
		for (StackTraceElement st : sts) {
			if (st.isNativeMethod()) {
				continue;
			}
 
			if (st.getClassName().equals(Thread.class.getName())) {
				continue;
			}
 
			if (st.getClassName().equals(this.getClass().getName())) {
				continue;
			}
 
			return "[" + Thread.currentThread().getName() + "(" + Thread.currentThread().getId()
					+ "): " + st.getFileName() + ":" + st.getLineNumber() + "]";
		}
 
		return null;
	}
 
	private String createMessage(String msg) {
		String functionName = getFunctionName();
		String message = (functionName == null ? msg : (functionName + " - " + msg));
		return message;
	}
 
	/**
	 * log.i
	 */
	public void info(String msg) {
		if (debug) {
			String message = createMessage(msg);
			Log.i(tag, message);
		}
	}
	public static void i(String msg){
		instance.info(msg);
	}
	public static void i(Exception e){
		instance.info(e!=null?e.toString():"null");
	}
	/**
	 * log.v
	 */
	public void verbose(String msg) {
		if (debug) {
			String message = createMessage(msg);
			Log.v(tag, message);
		}
	}
	public void v(String msg){
		instance.verbose(msg);
	}
	public void v(Exception e){
		instance.verbose(e!=null?e.toString():"null");
	}
	/**
	 * log.d
	 */
	public void debug(String msg) {
		if (debug) {
			String message = createMessage(msg);
			Log.d(tag, message);
		}
	}
	public static void d(String msg){
		instance.debug(msg);
	}
	public static void d(Exception e){
		instance.debug(e!=null?e.toString():"null");
	}
	/**
	 * log.e
	 */
	public void error(String msg) {
		if (debug) {
			String message = createMessage(msg);
			Log.e(tag, message);
		}
	}
	/**
	 * log.e
	 * @description 	
	 * @param msg
	 */
	public static void e(String msg){
		instance.error(msg);
	}
	/**
	 * log.error 
	 */
	public void error(Exception e){
		if(debug){
			StringBuffer sb = new StringBuffer();
	        String name = getFunctionName();
	        StackTraceElement[] sts = e.getStackTrace();
 
	        if (name != null) {
                sb.append(name+" - "+e+"\r\n");
            } else {
                sb.append(e+"\r\n");
            }
	        if (sts != null && sts.length > 0) {
	            for (StackTraceElement st:sts) {
	                if (st != null) {
	                    sb.append("[ "+st.getFileName()+":"+st.getLineNumber()+" ]\r\n");
	                }
	            }
	        }
	        Log.e(tag,sb.toString());
		}
	}
	public static void e(Exception e){
		instance.error(e);
	}
	/**
	 * log.warn
	 */
	public void warn(String msg) {
		if (debug) {
			String message = createMessage(msg);
			Log.w(tag, message);
		}
	}
	/**
	 * log.w
	 */
	public static void w(String msg){
		instance.warn(msg);
	}
	public static void w(Exception e){
		instance.warn(e!=null?e.toString():"null");
	}
	public void setTag(String tag) {
		this.tag = tag;
	}
 
	/**
	 * set debug
	 */
	public static void setDebug(boolean d) {
		debug = d;
	}
}