package com.github.onacit.rfc864;

import com.github.onacit.__Utils;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.Socket;

@Slf4j
class Rfc864Tcp1Client {

    public static void main(final String... args) throws IOException {
        try (var client = new Socket()) {
            assert !client.isConnected();
            client.connect(_Constants.SERVER_ENDPOINT);
            assert client.isConnected();
            log.debug("connected to {} through {}", client.getRemoteSocketAddress(), client.getLocalSocketAddress());
            client.shutdownOutput(); // IOException
            {
//                client.setSoTimeout(1024); // SocketException
            }
            __Utils.readQuitAndClose(true, client);
            for (int r; (r = client.getInputStream().read()) != -1; ) {
                System.out.print((char) r);
            } // end-of-for-loop
        } // end-of-try-with-resources
    }
}
