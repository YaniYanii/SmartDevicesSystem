package com.smartdevicessystem;

import com.smartdevicessystem.projectUtils.reqHandler.RequestHandler;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONObject;

import java.io.IOException;

public class RegisterIOTServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public RegisterIOTServlet() {
        super();
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        JSONObject json = RequestHandler.parseJsonRequest(request);
        json.put("command", "REGISTER IOT");

        request.setAttribute("newJson", json);

        RequestDispatcher dispatcher = request.getRequestDispatcher("/gateWayServlet");
        dispatcher.forward(request, response);
    }
}