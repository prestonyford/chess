import java.net.*;
import java.io.*;

import chess.dataModel.request.RegisterRequest;
import chess.dataModel.response.RegisterResponse;
import com.google.gson.Gson;
import exception.ResponseException;

public class ServerFacade {
    private final String serverUrl;

    public ServerFacade(String url) throws URISyntaxException, MalformedURLException {
        this.serverUrl = url;
    }

    public RegisterResponse register(RegisterRequest request) throws ResponseException {
        return makeRequest("POST", "/user", request, RegisterResponse.class);
    }

    private <T> T makeRequest(String method, String path, Object request, Class<T> responseClass) throws ResponseException {
        try {
            HttpURLConnection http = (HttpURLConnection) new URI(serverUrl + path).toURL().openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);

            writeBody(http, request);
            http.connect();
            throwIfNotSuccessful(http);
            return readBody(http, responseClass);
        } catch (Exception ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }


    private static void writeBody(HttpURLConnection http, Object request) throws IOException {
        if (request != null) {
            http.addRequestProperty("Content-Type", "application/json");
            String reqData = new Gson().toJson(request);
            try (OutputStream reqBody = http.getOutputStream()) {
                reqBody.write(reqData.getBytes());
            }
        }
    }

    private void throwIfNotSuccessful(HttpURLConnection http) throws IOException, ResponseException {
        var status = http.getResponseCode();
        if (!isSuccessful(status)) {
            throw new ResponseException(status, "failure: " + status);
        }
    }

    private static <T> T readBody(HttpURLConnection http, Class<T> responseClass) throws IOException {
        T response = null;
        if (http.getContentLength() < 0) {
            try (InputStream respBody = http.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(respBody);
                if (responseClass != null) {
                    response = new Gson().fromJson(reader, responseClass);
                }
            }
        }
        return response;
    }


    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }
}
