package org.apache.tools.ant.util.java15;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.SocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.ListIterator;
import org.apache.tools.ant.BuildException;

/* JADX INFO: loaded from: classes3.dex */
public class ProxyDiagnostics {
    public static final String DEFAULT_DESTINATION = "http://ant.apache.org/";
    private URI destURI;
    private String destination;

    public ProxyDiagnostics(String str) {
        this.destination = str;
        try {
            this.destURI = new URI(str);
        } catch (URISyntaxException e) {
            throw new BuildException(e);
        }
    }

    public ProxyDiagnostics() {
        this(DEFAULT_DESTINATION);
    }

    public String toString() {
        List<Proxy> listSelect = ProxySelector.getDefault().select(this.destURI);
        StringBuffer stringBuffer = new StringBuffer();
        ListIterator<Proxy> listIterator = listSelect.listIterator();
        while (listIterator.hasNext()) {
            Proxy next = listIterator.next();
            SocketAddress socketAddressAddress = next.address();
            if (socketAddressAddress == null) {
                stringBuffer.append("Direct connection\n");
            } else {
                stringBuffer.append(next.toString());
                if (socketAddressAddress instanceof InetSocketAddress) {
                    InetSocketAddress inetSocketAddress = (InetSocketAddress) socketAddressAddress;
                    stringBuffer.append(' ');
                    stringBuffer.append(inetSocketAddress.getHostName());
                    stringBuffer.append(':');
                    stringBuffer.append(inetSocketAddress.getPort());
                    if (inetSocketAddress.isUnresolved()) {
                        stringBuffer.append(" [unresolved]");
                    } else {
                        InetAddress address = inetSocketAddress.getAddress();
                        stringBuffer.append(" [");
                        stringBuffer.append(address.getHostAddress());
                        stringBuffer.append(']');
                    }
                }
                stringBuffer.append('\n');
            }
        }
        return stringBuffer.toString();
    }
}
