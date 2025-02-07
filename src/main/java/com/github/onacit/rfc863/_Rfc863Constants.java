package com.github.onacit.rfc863;

import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.UnknownHostException;

@Slf4j
final class _Rfc863Constants {

    static final InetAddress HOST;

    static {
        InetAddress host = null;
        for (final var h : new String[]{"::", "0.0.0.0"}) {
            try {
                host = InetAddress.getByName(h);
                break;
            } catch (final UnknownHostException uhe) {
                log.error("failed to get host address for '{}'", h, uhe);
            }
        }
        if (host == null) {
            throw new RuntimeException("failed to get host address");
        }
        HOST = host;
    }

    static final int PORT = 9 + 20000;

    static final SocketAddress SERVER_ENDPOINT_TO_BIND = new InetSocketAddress(HOST, PORT);

    static final SocketAddress SERVER_ENDPOINT_TO_CONNECT;

    static {
        try {
//            SERVER_ENDPOINT_TO_CONNECT = new InetSocketAddress(InetAddress.getByName("::1"), PORT);
            SERVER_ENDPOINT_TO_CONNECT = new InetSocketAddress(InetAddress.getLocalHost(), PORT);
        } catch (final UnknownHostException uhe) {
            throw new RuntimeException("failed to get local host address", uhe);
        }
    }


    // -----------------------------------------------------------------------------------------------------------------
    static final int UDP_BUF_LEN = 0xFFFF - (4 + 4 + 4 + 4 + 4);

    // -----------------------------------------------------------------------------------------------------------------
    private _Rfc863Constants() {
        throw new AssertionError("instantiation is not allowed");
    }
}
