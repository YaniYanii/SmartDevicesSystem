package com.smartdevicessystem.projectUtils.reqHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.Callable;

import com.smartdevicessystem.projectUtils.commandFactory.CommandFactory;
import jakarta.servlet.http.HttpServletRequest;
import iotInfrustructure.gateWay.threadPool.ThreadPool;
import org.json.JSONObject;

public class RequestHandler {
	
	private final int NUM_THREADS = 5;
	private final ThreadPool threadPool = new ThreadPool(NUM_THREADS);	
	
	private RequestHandler(){}
	
	/*---------Singleton implementation-----------*/
	public static RequestHandler RequestHandlerInstance(){
		return SingeltonInitClass.INSTANCE;
	}
	
	private static class SingeltonInitClass{
		private static RequestHandler INSTANCE = new RequestHandler();
	}
	/*-------------------------------------------*/
	
	public void addRequest(JSONObject json) {
		threadPool.submit(new CallableRefernce(json));
	}
	
	
	public static JSONObject parseJsonRequest(HttpServletRequest request) throws IOException {
	    StringBuilder stringBuilder = new StringBuilder();
	    BufferedReader reader = new BufferedReader(new InputStreamReader(request.getInputStream()));
	    String line;
	    while ((line = reader.readLine()) != null) {
	        stringBuilder.append(line);
	    }
	    String json= stringBuilder.toString();	    
	    return new JSONObject(json);
	}
	
	/**********************************************************/
	private static class CallableRefernce implements Callable<JSONObject>{
		private final JSONObject curJson;
		
		CallableRefernce(JSONObject json){
			curJson = json;	
			
	}
			@Override
			public JSONObject call() throws Exception {
				return CommandFactory.getCommandFactoryInstance().execute(curJson);
			}
	}
}
