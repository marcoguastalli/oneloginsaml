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

import com.onelogin.AccountSettings;
import com.onelogin.saml.Response;

@WebServlet(name = "simpleJson", displayName = "Simple Json", urlPatterns = { "/simpleJson" }, loadOnStartup = 1)
public class SimpleJson extends HttpServlet {

    private static final Logger LOG = LoggerFactory.getLogger(SimpleJson.class);

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            String oktaAuthCert =
                    "MIIDmjCCAoKgAwIBAgIGAUtCNvhAMA0GCSqGSIb3DQEBBQUAMIGNMQswCQYDVQQGEwJVUzETMBEG A1UECAwKQ2FsaWZvcm5pYTEWMBQGA1UEBwwNU2FuIEZyYW5jaXNjbzENMAsGA1UECgwET2t0YTEU MBIGA1UECwwLU1NPUHJvdmlkZXIxDjAMBgNVBAMMBWFkb2JlMRwwGgYJKoZIhvcNAQkBFg1pbmZv QG9rdGEuY29tMB4XDTE1MDEzMTIyNTY1M1oXDTQ1MDEzMTIyNTc1M1owgY0xCzAJBgNVBAYTAlVT MRMwEQYDVQQIDApDYWxpZm9ybmlhMRYwFAYDVQQHDA1TYW4gRnJhbmNpc2NvMQ0wCwYDVQQKDARP a3RhMRQwEgYDVQQLDAtTU09Qcm92aWRlcjEOMAwGA1UEAwwFYWRvYmUxHDAaBgkqhkiG9w0BCQEW DWluZm9Ab2t0YS5jb20wggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQCaBYF/Qn7FJocZ T/sUQTLe/deYAgScCP/dqsebneeXueswEKoKe762M6cLkrQIjxM5b60EmmgFAr08PB0QxUjdZZly 8bHvZCCWBhZukFsRcP57Umzr0c3z+iPG8IFPw6nLQJ+a0XT0qCVNlYUXmbnzRZ/2dpKU2qQZuHkW 0xzcEBbft+RbOSug6X3oCZg96MWMA9HQraeO3QwuNTxor8WuFUPcoMAu4JJ7FndIsT54zB+9il6d 7aqmmSALhkCYCljcBjssNMBb0nw742bvHYK4/Kj6v8ow5e6GSzkyqw7Tua1KY0bU59YCWlAuufW0 aGfBj0RnLbF25ul7Pv1ibiohAgMBAAEwDQYJKoZIhvcNAQEFBQADggEBAB8cP0w5gHiqRrS05HaE t/BAP+bqpcBRO13GQgv2b0Quoiwi7MSLPW3m9LW7hLOUrMCJFlxRBYqnx/VGndUCUE/cG1Qy7coe LZFgtmy2GMeHfULtmTsZFbw94mc0/LZuj5b4k2JWpCkjjh+fLxGDOkp6JtYW6sIMtFsge9DqGnn9 DItXlSEYHKOz6WT0+VqfG4lknxMEH9SKPdoOUjxxMwjXwmCBumJazy2A3VbEztqK0Soenk+92Rka qZUbEFWm/yuaj2npVLu1e9dRZyXf1+xi+/M/3209UHIASLLd/xIYJeGhtnq9lrjoxpFriigf4+Xj LwkalrwruMc1vbw2XrQ=";

            String SAMLResponse = request.getParameter("SAMLResponse") != null ? request.getParameter("SAMLResponse") : "";

            AccountSettings accountSettings = new AccountSettings();
            accountSettings.setCertificate(oktaAuthCert);
            Response samlResponse = new Response(accountSettings, SAMLResponse, request.getRequestURL().toString());


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
        } catch (Exception e) {
            LOG.error("Error!", e);
            getServletContext().getRequestDispatcher("/error.jsp").forward(request, response);
        }

    }
}
