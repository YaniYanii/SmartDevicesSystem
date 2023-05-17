package com.smartdevicessystem;

import com.smartdevicessystem.projectUtils.reqHandler.RequestHandler;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONObject;

import java.io.IOException;
@WebServlet(name = "RegisterProductServlet", value = "/registerProduct")
public class RegisterProductServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    public RegisterProductServlet() {
        super();
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, ServletException, IOException {
        JSONObject json = RequestHandler.parseJsonRequest(request);
        json.put("command", "REGISTER PRODUCT");

        request.setAttribute("newJson", json);

        RequestDispatcher dispatcher = request.getRequestDispatcher("/gateWayServlet");
        dispatcher.forward(request, response);
    }
}
