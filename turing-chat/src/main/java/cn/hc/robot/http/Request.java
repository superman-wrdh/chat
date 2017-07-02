package cn.hc.robot.http;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.cookie.*;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.impl.cookie.BestMatchSpecFactory;
import org.apache.http.impl.cookie.BrowserCompatSpec;
import org.apache.http.impl.cookie.BrowserCompatSpecFactory;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Http request wrapper based on Apache HTTP components
 */
public final class Request {

    public static String GET = "GET";
    public static String POST = "POST";
    public static String PUT = "PUT";
    public static String PATCH = "PATCH";
    public static String DELETE = "DELETE";
    public static String HEAD = "HEAD";
    public static String TRACE = "TRACE";
    public static String OPTIONS = "OPTIONS";

    public static String UTF_8 = "UTF-8";

    /**
     * constructor
     */
    public Request() {
    }

    /**
     * constructor
     */
    public Request(String url) {
        this();
        this.url = url;
    }

    private String url;
    private String method;
    private List<NameValuePair> queryParameters;
    private List<NameValuePair> formData;
    private List<Cookie> cookies;
    private List<Header> headers;
    private ContentType contentType;
    private String contentEncoding;
    private boolean chunked;
    private boolean gzipCompressed;
    private HttpHost proxy;
    private EntityBuilder entityBuilder;
    private boolean ignoreCookieCheck;

    /**
     * set url
     */
    public Request url(String url) {
        this.url = url;
        return this;
    }

    /**
     * get url
     */
    public String url() {
        return this.url;
    }

    /**
     * set method
     */
    public Request method(String method) {
        this.method = method;
        return this;
    }

    /**
     * get method
     */
    public String method() {
        return this.method;
    }

    /**
     * set content type
     */
    public Request contentType(ContentType contentType) {
        this.contentType = contentType;
        return this;
    }

    /**
     * get content type
     */
    public ContentType contentType() {
        return this.contentType;
    }

    /**
     * set chunked
     */
    public Request chunked() {
        chunked = true;
        return this;
    }

    /**
     * is chunked
     */
    public boolean isChunked() {
        return chunked;
    }

    /**
     * set gzip compressed
     */
    public Request gzipCompressed() {
        gzipCompressed = true;
        return this;
    }

    /**
     * is gzip compressed
     */
    public boolean isGzipCompressed() {
        return gzipCompressed;
    }

    /**
     * set ignore cookie check
     */
    public Request ignoreCookieCheck() {
        ignoreCookieCheck = true;
        return this;
    }

    /**
     * is ignore cookie check
     */
    public boolean isIgnoreCookieCheck() {
        return ignoreCookieCheck;
    }

    /**
     * set proxy
     */
    public Request proxy(HttpHost proxy) {
        this.proxy = proxy;
        return this;
    }

    /**
     * set proxy
     */
    public Request proxy(String host, int port, String schema) {
        return proxy(new HttpHost(host, port, schema));
    }

    /**
     * set proxy
     */
    public Request proxy(String host, int port) {
        return proxy(new HttpHost(host, port));
    }

    /**
     * set proxy
     */
    public Request proxy(String host) {
        return proxy(new HttpHost(host));
    }

    /**
     * get proxy
     */
    public HttpHost proxy() {
        return this.proxy;
    }

    /**
     * set body
     */
    public Request body(InputStream inputStream) {
        formData = null;
        if(entityBuilder==null)
            entityBuilder = EntityBuilder.create();
        entityBuilder.setStream(inputStream);
        return this;
    }

    /**
     * set body
     */
    public Request body(String content) {
        return body(content, UTF_8);
    }

    /**
     * set body
     */
    public Request body(String content, String contentEncoding) {
        formData = null;
        if(entityBuilder == null)
            entityBuilder = EntityBuilder.create();
        entityBuilder.setText(content);
        entityBuilder.setContentEncoding(contentEncoding);
        return this;
    }

    /**
     * set body
     */
    public Request body(byte[] bytes) {
        formData = null;
        entityBuilder.setBinary(bytes);
        return this;
    }

    /**
     * set file
     */
    public Request file(File file) {
        formData = null;
        entityBuilder.setFile(file);
        return this;
    }

    /**
     * add query parameter
     */
    public Request query(NameValuePair parameter) {
        if(this.queryParameters == null)
            this.queryParameters = new ArrayList<NameValuePair>();
        this.queryParameters.add(parameter);

        return this;
    }

    /**
     * add query parameter
     */
    public Request query(String name, String value) {
        return query(new BasicNameValuePair(name, value));
    }

    /**
     * add form parameter
     */
    public Request form(NameValuePair parameter) {
        if(this.formData == null)
            this.formData = new ArrayList<>();
        this.formData.add(parameter);
        return this;
    }

    /**
     * add form parameter
     */
    public Request form(String name, String value) {
        return form(new BasicNameValuePair(name, value));
    }

    /**
     * set header
     */
    public Request header(Header header) {
        if(this.headers == null)
            this.headers = new ArrayList<>();
        this.headers.add(header);
        return this;
    }

    /**
     * set header
     */
    public Request header(String name, String value) {
        return header(new BasicHeader(name, value));
    }

    /**
     * set cookie
     */
    public Request cookie(Cookie cookie) {
        if(this.cookies == null)
            this.cookies = new ArrayList<>();
        this.cookies.add(cookie);
        return this;
    }

    /**
     * set cookie
     */
    public Request cookie(String name, String value) {
        return cookie(new BasicClientCookie(name, value));
    }

    /**
     * execute request
     */
    public Response execute() {
        if(method == null)
            method = (formData == null && entityBuilder == null) ? GET : POST;
        RequestBuilder builder = RequestBuilder.create(this.method);

        builder.setUri(url);
        if(headers!=null && headers.size()>0) {
            for(Header header:headers)
                builder.addHeader(header);
        }
        if(queryParameters!=null && queryParameters.size()>0) {
            for(NameValuePair par:queryParameters)
                builder.addParameter(par);
        }

        if(formData!=null)
            entityBuilder = EntityBuilder.create();

        if(entityBuilder!=null) {
            if(contentType!=null)
                entityBuilder.setContentType(contentType);
            if(contentEncoding!=null)
                entityBuilder.setContentEncoding(contentEncoding);
            if(chunked)
                entityBuilder.chunked();
            if(gzipCompressed)
                entityBuilder.gzipCompress();
            if(formData!=null && formData.size()>0)
                entityBuilder.setParameters(formData);

            builder.setEntity(entityBuilder.build());
        }
        HttpUriRequest req = builder.build();

        HttpClientBuilder clientBuilder = HttpClientBuilder.create();

        if(cookies!=null && cookies.size()>0) {
            BasicCookieStore cookieStore = new BasicCookieStore();

            for(Cookie cookie : cookies)
                cookieStore.addCookie(cookie);

            clientBuilder.setDefaultCookieStore(cookieStore);
        }
        if(proxy!=null)
            clientBuilder.setProxy(proxy);

        if(ignoreCookieCheck) {
            Registry<CookieSpecProvider> cookieSpecRegistry = RegistryBuilder.<CookieSpecProvider>create()
                    .register(CookieSpecs.BEST_MATCH,
                            new BestMatchSpecFactory())
                    .register(CookieSpecs.BROWSER_COMPATIBILITY,
                            new BrowserCompatSpecFactory())
                    .register("simpleSpec", new SimpleCookieSpecProvider())
                    .build();

            RequestConfig requestConfig = RequestConfig.custom()
                    .setCookieSpec("simpleSpec")
                    .build();
            clientBuilder.setDefaultCookieSpecRegistry(cookieSpecRegistry);
            clientBuilder.setDefaultRequestConfig(requestConfig);
        }

        CloseableHttpClient httpClient = clientBuilder.build();
        try {
            try (CloseableHttpResponse resp = httpClient.execute(req)) {
                Response response = new Response();

                response.setHeaders(resp.getAllHeaders());
                response.setStatusCode(resp.getStatusLine().getStatusCode());

                HttpEntity entity = resp.getEntity();

                if(entity!=null) {
                    ContentType contentType = ContentType.getOrDefault(entity);
                    response.setCharset(contentType.getCharset());
                    response.setMimeType(contentType.getMimeType());
                    response.setContentLength(entity.getContentLength());
                    response.setBody(EntityUtils.toByteArray(entity));
                }

                return response;
            }
        } catch (IOException e) {
            //throw new HttpException("error to request : " + url, e);
            e.printStackTrace();
        } finally {
            try {
                httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private static class SimpleCookieSpecProvider implements CookieSpecProvider {
        public CookieSpec create(HttpContext context) {

            return new BrowserCompatSpec() {
                @Override
                public void validate(Cookie cookie, CookieOrigin origin)
                        throws MalformedCookieException {
                    // ignore cookie check
                }
            };
        }
    }
}