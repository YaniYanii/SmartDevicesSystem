package com.smartdevicessystem.projectUtils;

import com.smartdevicessystem.projectUtils.reqHandler.RequestHandler;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

import javax.management.RuntimeErrorException;

import org.json.JSONObject;

/**
 * Servlet implementation class GateWayServalet
 */
public class GateWayServalet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private RequestHandler reqHandler = RequestHandler.RequestHandlerInstance();
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GateWayServalet() {
        super();
    }

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		JSONObject jsonReq = null;
		try {
			
		jsonReq =  (JSONObject)request.getAttribute("newJson");
		} catch (Exception e) {
			throw new RuntimeErrorException(null,e.toString());
		}
		reqHandler.addRequest(jsonReq);
	}
	
	protected void doPut(HttpServletRequest request, HttpServletResponse response){
		JSONObject jsonReq = null;
		try {
			
		jsonReq =  RequestHandler.parseJsonRequest(request);
		} catch (Exception e) {
			throw new RuntimeErrorException(null,e.toString());
		}
		reqHandler.addRequest(jsonReq);
	}
}
