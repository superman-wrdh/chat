package cn.hc.robot.http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.Header;
import org.apache.http.util.EncodingUtils;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import java.io.StringReader;
import java.nio.charset.Charset;

/**
 * response wrapper
 */
public final class Response {
    private static final Log logger = LogFactory.getLog(Response.class);

    // gson instance
    // only serialize exposed properties
    private static Gson defaultGson = new GsonBuilder()
            .excludeFieldsWithoutExposeAnnotation()
            .enableComplexMapKeySerialization().create();

    public enum DeserializeType {
        Json, Xml
    }

    /**
     * constructor
     *
     * internal only
     */
    Response() {
    }

    private Header[] headers;
    private int statusCode;
    private byte[] body;
    private String mimeType;
    private Charset charset;
    private long contentLength;
    private String contentString;

    /**
     * get headers
     */
    public Header[] getHeaders() {
        return headers;
    }

    /**
     * get status code
     */
    public int getStatusCode() {
        return statusCode;
    }

    /**
     * set headers
     */
    void setHeaders(Header[] headers) {
        this.headers = headers;
    }

    /**
     * set status code
     */
    void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    /**
     * set body
     */
    void setBody(byte[] body) {
        this.body = body;
    }

    /**
     * get content type
     */
    public String getMimeType() {
        return mimeType;
    }

    /**
     * set content type
     */
    void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    /**
     * get content encoding
     */
    public Charset getContentCharset() {
        return charset;
    }

    /**
     * set content encoding
     */
    public void setCharset(Charset charset) {
        this.charset = charset;
    }

    /**
     * get content length
     */
    public long getContentLength() {
        return contentLength;
    }

    /**
     * set content length
     */
    void setContentLength(long contentLength) {
        this.contentLength = contentLength;
    }

    /**
     * get bytes content
     */
    public byte[] bytes() {
        return body;
    }

    /**
     * get string content
     */
    public String string() {
        if(contentString == null && body!=null)
            contentString = EncodingUtils.getString(body, charset == null ? Request.UTF_8 : charset.name());
        return contentString;
    }

    /**
     * deserialize content to object
     */
    public <T> T deserialize(Class<T> clz, DeserializeType type) {
        if(type == DeserializeType.Json)
            return defaultGson.fromJson(string(), clz);
        else if(type == DeserializeType.Xml){
            try {
                return (T) JAXBContext.newInstance(clz).createUnmarshaller().unmarshal(new StringReader(string()));
            } catch (JAXBException e) {
                logger.error(e);
                return null;
            }
        } else {
            return null;
        }
    }
}