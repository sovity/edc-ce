package de.sovity.edc.ext.brokerserver.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.net.URI;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UrlUtils {


    /**
     * Returns everything before the URLs path.
     * <p>
     * Example: http://www.example.com/path/to/my/file.html -> http://www.example.com
     * Example 2: http://www.example.com:9000/path/to/my/file.html -> http://www.example.com:9000
     *
     * @param url url
     * @return protocol, host, port
     */
    public static String getEverythingBeforeThePath(String url) {
        var uri = URI.create(url);
        String scheme = uri.getScheme(); // "http"
        String authority = uri.getAuthority(); // "www.example.com"
        int port = uri.getPort(); // -1 (no port specified)
        String everythingBeforePath = scheme + "://" + authority;
        if (port != -1) {
            everythingBeforePath += ":" + port;
        }
        return everythingBeforePath;
    }
}
