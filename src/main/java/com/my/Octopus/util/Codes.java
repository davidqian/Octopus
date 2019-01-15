package com.my.Octopus.util;

public class Codes {
    //websocket server codes
    public static final int NETCONNECT = 1;
    public static final int NETCLOSED = 2;

    //gateway codes
    public static final int GATEWAY_BEGIN = 0;
    public static final int GATEWAY_INIT_REQ = 10;
    public static final int GATEWAY_INIT_RES = 11;
    public static final int GATEWAY_HEARTBEAT = 12;
    public static final int GATEWAY_END = 20;
    public static final int GATEWAY_SENDTO = 20;
    public static final int GATEWAY_BROADCAST = 21;
    public static final int GATEWAY_DATA = 22;

    //gateway status code
    public static final int SEND_OK = 0; //发送数据正常
    public static final int SEND_ERROR = 1; //发送数据错误
}
