package cn.hc.robot.http;



/**
 * http exception
 */
public class HttpException extends Exception {
    public HttpException(String msg) {
        super(msg);
    }
    public HttpException(String msg, Throwable cause) {
        super(msg, cause);
    }
}