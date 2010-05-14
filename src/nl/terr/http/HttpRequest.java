/**
 * HTTP request class
 * 
 * Inspired by blog post from Sander Borgman (http://sanderborgman.nl/android/android-httprequest-class/)
 */
package nl.terr.http;

import java.io.IOException;

import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.auth.AuthScheme;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.AuthState;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HttpContext;

public class HttpRequest {

    /**
     * HTTP GET request
     *
     * @param client
     * @param url
     * @return
     * @throws IOException
     * @throws ClientProtocolException
     * @throws Exception
     */
    public static String get(DefaultHttpClient client, String url)
        throws ClientProtocolException, IOException {

        // Initialize HTTP request
        HttpGet httpGet = new HttpGet(url);

        // Execute request
        ResponseHandler<String> responseHandler = new BasicResponseHandler();
        String response = client.execute(httpGet, responseHandler);

        return response;

    }

    /**
     * HTTP GET request with Basic authentication
     *
     * @param client
     * @param url
     * @param username
     * @param password
     * @return
     * @throws ClientProtocolException
     * @throws IOException
     * @throws HttpResponseException
     */
    public static String get(DefaultHttpClient  client, String url, String username, String password)
        throws ClientProtocolException, IOException, HttpResponseException {

        // Initialize HTTP request
        HttpGet httpGet = new HttpGet(url);

        // Execute request
        UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(username, password);
        client.getCredentialsProvider().setCredentials(
                new AuthScope(null, 443),
                credentials
        );

        // Generate Basic scheme object and stick it to the local
        // execution context
        BasicHttpContext localcontext = new BasicHttpContext();
        BasicScheme basicAuth = new BasicScheme();
        localcontext.setAttribute("preemptive-auth", basicAuth);

        // Add the 'first request interceptor'
        client.addRequestInterceptor(new PreemptiveAuth(), 0);

        // Execute request
        ResponseHandler<String> responseHandler = new BasicResponseHandler();
        String response = client.execute(httpGet, responseHandler);

        return response;
    }

    static class PreemptiveAuth implements HttpRequestInterceptor {

        public void process(
                final HttpRequest request,
                final HttpContext context) throws HttpException, IOException {

            AuthState authState = (AuthState) context.getAttribute(
                    ClientContext.TARGET_AUTH_STATE);

            // If no auth scheme avaialble yet, try to initialize it preemptively
            if (authState.getAuthScheme() == null) {
                AuthScheme authScheme = (AuthScheme) context.getAttribute(
                        "preemptive-auth");
                CredentialsProvider credsProvider = (CredentialsProvider) context.getAttribute(
                        ClientContext.CREDS_PROVIDER);
                HttpHost targetHost = (HttpHost) context.getAttribute(
                        ExecutionContext.HTTP_TARGET_HOST);
                if (authScheme != null) {
                    Credentials creds = credsProvider.getCredentials(
                            new AuthScope(
                                    targetHost.getHostName(),
                                    targetHost.getPort()));
                    if (creds == null) {
                        throw new HttpException("No credentials for preemptive authentication");
                    }
                    authState.setAuthScheme(authScheme);
                    authState.setCredentials(creds);
                }
            }

        }

        @Override
        public void process(org.apache.http.HttpRequest request,
                HttpContext context) throws HttpException, IOException {
            // TODO Auto-generated method stub

        }

    }

}