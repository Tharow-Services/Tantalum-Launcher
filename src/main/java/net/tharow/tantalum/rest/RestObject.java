

package net.tharow.tantalum.rest;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import net.tharow.tantalum.launchercore.TantalumConstants;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import java.nio.charset.StandardCharsets;

public class RestObject {
    private static final Gson gson = new Gson();

    private String error;

    public boolean hasError() {
        return error != null;
    }

    public String getError() {
        return error;
    }

    public static <T extends RestObject> @NotNull T getRestObject(Class<T> restObject, String url) throws RestfulAPIException {
        try {
            URLConnection conn = new URL(url).openConnection();
            conn.setRequestProperty("User-Agent", TantalumConstants.getUserAgent());
            conn.setConnectTimeout(15000);
            conn.setReadTimeout(15000);
            try (InputStream stream = conn.getInputStream()) {
                String data = IOUtils.toString(stream, StandardCharsets.UTF_8);

                T result = gson.fromJson(data, restObject);

                if (result == null) {
                    throw new RestfulAPIException("Unable to access URL [" + url + "]");
                }

                if (result.hasError()) {
                    throw new RestfulAPIException("Error in response: " + result.getError());
                }

                return result;
            }
        } catch (SocketTimeoutException e) {
            throw new RestfulAPIException("Timed out accessing URL [" + url + "]", e);
        } catch (MalformedURLException e) {
            throw new RestfulAPIException("Invalid URL [" + url + "]", e);
        } catch (JsonParseException e) {
            throw new RestfulAPIException("Error parsing response JSON at URL [" + url + "]", e);
        } catch (IOException e) {
            throw new RestfulAPIException("Error accessing URL [" + url + "]", e);
        }
    }
}
