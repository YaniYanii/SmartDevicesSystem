package com.smartdevicessystem;
import com.smartdevicessystem.gateWay.reqHandler.RequestHandler;
import jakarta.servlet.RequestDispatcher;
import org.json.JSONObject;
import java.io.*;

import com.fasterxml.jackson.databind.util.JSONPObject;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

@WebServlet(name = "RegisterCompany", value = "/registerCompany")
public class RegisterCompany extends HttpServlet {
    private String message;

    public void init() {
        message = "Hello World!";
    }


    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        JSONObject json = RequestHandler.parseJsonRequest(request);
        json.put("command", "REGISTER COMPANY");

        request.setAttribute("newJson", json);

        RequestDispatcher dispatcher = request.getRequestDispatcher("/gateWayServalet");
        dispatcher.forward(request, response);
    }
}