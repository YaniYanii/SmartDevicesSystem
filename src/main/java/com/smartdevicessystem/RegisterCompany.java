package com.smartdevicessystem;
import com.smartdevicessystem.projectUtils.reqHandler.RequestHandler;
import jakarta.servlet.RequestDispatcher;
import org.json.JSONObject;
import java.io.*;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.WebServlet;

@WebServlet(name = "RegisterCompany", value = "/registerCompany")
public class RegisterCompany extends HttpServlet {

    public RegisterCompany() {
        super();
    }
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        JSONObject json = RequestHandler.parseJsonRequest(request);
        json.put("command", "REGISTER COMPANY");

        request.setAttribute("newJson", json);

        RequestDispatcher dispatcher = request.getRequestDispatcher("/gateWayServlet");
        dispatcher.forward(request, response);
    }
    /*TODO move register here and add k v (db name) for caching in register company command*/

}