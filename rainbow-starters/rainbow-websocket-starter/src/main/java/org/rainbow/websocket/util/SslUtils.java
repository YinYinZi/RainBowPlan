package org.rainbow.websocket.util;

import cn.hutool.core.util.StrUtil;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import org.springframework.util.ResourceUtils;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLException;
import javax.net.ssl.TrustManagerFactory;
import java.net.URL;
import java.security.KeyStore;

/**
 * @author K
 * @date 2021/3/4  17:02
 */
public final class SslUtils {

    public static SslContext createSslContext(String keyPassword, String keyStoreResource,
                                              String keyStoreType, String keyStorePassword,
                                              String trustStoreResource, String trustStoreType,
                                              String trustStorePassword) throws SSLException {
        SslContextBuilder sslContextBuilder = SslContextBuilder
                .forServer(getKeyManagerFactory(keyStoreType, keyStoreResource, keyPassword, keyStorePassword))
                .trustManager(getTrustManagerFactory(trustStoreType, trustStoreResource, trustStorePassword));
        return sslContextBuilder.build();
    }

    private static KeyManagerFactory getKeyManagerFactory(String type, String resource, String keyPassword, String keyStorePassword) {
        try {
            KeyStore store = loadKeyStore(type, resource, keyStorePassword);
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            char[] keyPasswordBytes = StrUtil.isNotEmpty(keyPassword) ? keyPassword.toCharArray() : null;
            if (keyPasswordBytes == null && StrUtil.isNotBlank(keyStorePassword)) {
                keyPasswordBytes = keyStorePassword.toCharArray();
            }
            keyManagerFactory.init(store, keyPasswordBytes);
            return keyManagerFactory;
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
    }

    private static TrustManagerFactory getTrustManagerFactory(String trustStoreType, String trustStoreResource, String trustStorePassword) {
        try {
            KeyStore store = loadKeyStore(trustStoreType, trustStoreResource, trustStorePassword);
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(store);
            return trustManagerFactory;
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
    }

    private static KeyStore loadKeyStore(String type, String resource, String password) throws Exception {
        type = (StrUtil.isEmpty(type)) ? "JKS" : type;
        if (StrUtil.isEmpty(resource)) {
            return null;
        }
        KeyStore store = KeyStore.getInstance(type);
        URL url = ResourceUtils.getURL(resource);
        store.load(url.openStream(), StrUtil.isEmpty(password) ? null : password.toCharArray());
        return store;
    }
}
