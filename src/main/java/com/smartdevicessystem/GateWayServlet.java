package com.smartdevicessystem;

import com.smartdevicessystem.projectUtils.reqHandler.RequestHandler;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

import javax.management.RuntimeErrorException;

import org.json.JSONObject;

@WebServlet(name = "GateWayServlet", value = "/gateWayServlet")
public class GateWayServlet extends HttpServlet {
    private RequestHandler reqHandler = RequestHandler.RequestHandlerInstance();

    /**
     * @see HttpServlet#HttpServlet()
     */
    public GateWayServlet() {
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

