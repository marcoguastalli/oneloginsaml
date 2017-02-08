package servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet(name = "simpleJson", displayName = "Simple Json", urlPatterns = { "/simpleJson" }, loadOnStartup = 1)
public class SimpleJson extends HttpServlet {

    private static final Logger LOG = LoggerFactory.getLogger(SimpleJson.class);

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            LOG.debug("Creating JSON");

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("object1", "A");
            jsonObject.put("object2", "B");
            jsonObject.put("object3", "C");

            response.setContentType("application/json");
            PrintWriter out = response.getWriter();
            out.println(jsonObject.toString());

        } catch (JSONException e) {
            LOG.error("Error!", e);
            getServletContext().getRequestDispatcher("/error.jsp").forward(request, response);
        }

    }
}
