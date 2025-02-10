package com.github.onacit.rfc863;

import lombok.extern.slf4j.Slf4j;

import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;

@Slf4j
class Rfc863Udp3Server {

    public static void main(final String... args) throws Exception {
        try (var selector = Selector.open();
             var server = DatagramChannel.open()) {
            server.setOption(StandardSocketOptions.SO_REUSEADDR, Boolean.TRUE);
            server.setOption(StandardSocketOptions.SO_REUSEPORT, Boolean.TRUE);
            server.bind(_Rfc863Constants.SERVER_ENDPOINT_TO_BIND);
            log.info("bound to {}", server.getLocalAddress());
            server.configureBlocking(false);
            final var serverKey = server.register(selector, SelectionKey.OP_READ);
            _Rfc863Utils.readQuitAndCall(() -> {
                serverKey.cancel();
                selector.wakeup();
                return null;
            });
            final var buffer = ByteBuffer.allocate(_Rfc863Constants.UDP_BUF_LEN);
            while (serverKey.isValid()) {
                final var count = selector.select(0);
                if (count == 0) {
                    continue;
                }
                final var keys = selector.selectedKeys();
                assert keys.size() == 1 && keys.contains(serverKey);
                for (final var i = keys.iterator(); i.hasNext(); i.remove()) {
                    final var key = i.next();
                    assert key.isReadable();
                    final var channel = key.channel();
                    assert channel == server;
                    final var address = ((DatagramChannel) channel).receive(buffer.clear());
                    log.debug("discarding 0x{} byte(s) received from {}", String.format("%1$04x", buffer.position()),
                              address);
                }
            }
        }
    }
}
