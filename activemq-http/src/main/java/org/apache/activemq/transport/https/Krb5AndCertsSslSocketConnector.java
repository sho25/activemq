begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements. See the NOTICE file distributed with this  * work for additional information regarding copyright ownership. The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|transport
operator|.
name|https
package|;
end_package

begin_comment
comment|/**  * Extend Jetty's {@link SslSocketConnector} to optionally also provide  * Kerberos5ized SSL sockets. The only change in behavior from superclass is  * that we no longer honor requests to turn off NeedAuthentication when running  * with Kerberos support.  */
end_comment

begin_class
specifier|public
class|class
name|Krb5AndCertsSslSocketConnector
block|{
comment|//
comment|//extends SslSocketConnector {
comment|//    public static final List<String> KRB5_CIPHER_SUITES = Collections.unmodifiableList(Collections.singletonList("TLS_KRB5_WITH_3DES_EDE_CBC_SHA"));
comment|//    static {
comment|//        System.setProperty("https.cipherSuites", KRB5_CIPHER_SUITES.get(0));
comment|//    }
comment|//
comment|//    private static final Logger LOG = LoggerFactory.getLogger(Krb5AndCertsSslSocketConnector.class);
comment|//
comment|//    private static final String REMOTE_PRINCIPAL = "remote_principal";
comment|//
comment|//    public enum MODE {
comment|//        KRB, CERTS, BOTH
comment|//    } // Support Kerberos, certificates or both?
comment|//
comment|//    private boolean useKrb;
comment|//    private boolean useCerts;
comment|//
comment|//    public Krb5AndCertsSslSocketConnector() {
comment|//        // By default, stick to cert based authentication
comment|//        super();
comment|//        useKrb = false;
comment|//        useCerts = true;
comment|//        setPasswords();
comment|//    }
comment|//    public Krb5AndCertsSslSocketConnector(SslContextFactory f, String auth) {
comment|//        // By default, stick to cert based authentication
comment|//        super(f);
comment|//        useKrb = false;
comment|//        useCerts = true;
comment|//        setPasswords();
comment|//        setMode(auth);
comment|//    }
comment|//
comment|//    public static boolean isKrb(String mode) {
comment|//        return mode == MODE.KRB.toString() || mode == MODE.BOTH.toString();
comment|//    }
comment|//
comment|//    public void setMode(String mode) {
comment|//        useKrb = mode == MODE.KRB.toString() || mode == MODE.BOTH.toString();
comment|//        useCerts = mode == MODE.CERTS.toString() || mode == MODE.BOTH.toString();
comment|//        logIfDebug("useKerb = " + useKrb + ", useCerts = " + useCerts);
comment|//    }
comment|//
comment|//    // If not using Certs, set passwords to random gibberish or else
comment|//    // Jetty will actually prompt the user for some.
comment|//    private void setPasswords() {
comment|//        if (!useCerts) {
comment|//            Random r = new Random();
comment|//            System.setProperty("jetty.ssl.password", String.valueOf(r.nextLong()));
comment|//            System.setProperty("jetty.ssl.keypassword", String.valueOf(r.nextLong()));
comment|//        }
comment|//    }
comment|//
comment|//    @Override
comment|//    public SslContextFactory getSslContextFactory() {
comment|//        final SslContextFactory factory = super.getSslContextFactory();
comment|//
comment|//        if (useCerts) {
comment|//            return factory;
comment|//        }
comment|//
comment|//        try {
comment|//            SSLContext context = factory.getProvider() == null ? SSLContext.getInstance(factory.getProtocol()) : SSLContext.getInstance(factory.getProtocol(),
comment|//                factory.getProvider());
comment|//            context.init(null, null, null);
comment|//            factory.setSslContext(context);
comment|//        } catch (NoSuchAlgorithmException e) {
comment|//        } catch (NoSuchProviderException e) {
comment|//        } catch (KeyManagementException e) {
comment|//        }
comment|//
comment|//        return factory;
comment|//    }
comment|//
comment|//    /*
comment|//     * (non-Javadoc)
comment|//     *
comment|//     * @see
comment|//     * org.mortbay.jetty.security.SslSocketConnector#newServerSocket(java.lang
comment|//     * .String, int, int)
comment|//     */
comment|//    @Override
comment|//    protected ServerSocket newServerSocket(String host, int port, int backlog) throws IOException {
comment|//        logIfDebug("Creating new KrbServerSocket for: " + host);
comment|//        SSLServerSocket ss = null;
comment|//
comment|//        if (useCerts) // Get the server socket from the SSL super impl
comment|//            ss = (SSLServerSocket) super.newServerSocket(host, port, backlog);
comment|//        else { // Create a default server socket
comment|//            try {
comment|//                ss = (SSLServerSocket) super.newServerSocket(host, port, backlog);
comment|//            } catch (Exception e) {
comment|//                LOG.warn("Could not create KRB5 Listener", e);
comment|//                throw new IOException("Could not create KRB5 Listener: " + e.toString());
comment|//            }
comment|//        }
comment|//
comment|//        // Add Kerberos ciphers to this socket server if needed.
comment|//        if (useKrb) {
comment|//            ss.setNeedClientAuth(true);
comment|//            String[] combined;
comment|//            if (useCerts) { // combine the cipher suites
comment|//                String[] certs = ss.getEnabledCipherSuites();
comment|//                combined = new String[certs.length + KRB5_CIPHER_SUITES.size()];
comment|//                System.arraycopy(certs, 0, combined, 0, certs.length);
comment|//                System.arraycopy(KRB5_CIPHER_SUITES.toArray(new String[0]), 0, combined, certs.length, KRB5_CIPHER_SUITES.size());
comment|//            } else { // Just enable Kerberos auth
comment|//                combined = KRB5_CIPHER_SUITES.toArray(new String[0]);
comment|//            }
comment|//
comment|//            ss.setEnabledCipherSuites(combined);
comment|//        }
comment|//        return ss;
comment|//    };
comment|//
comment|//    @Override
comment|//    public void customize(EndPoint endpoint, Request request) throws IOException {
comment|//        if (useKrb) { // Add Kerberos-specific info
comment|//            SSLSocket sslSocket = (SSLSocket) endpoint.getTransport();
comment|//            Principal remotePrincipal = sslSocket.getSession().getPeerPrincipal();
comment|//            logIfDebug("Remote principal = " + remotePrincipal);
comment|//            request.setScheme(HttpSchemes.HTTPS);
comment|//            request.setAttribute(REMOTE_PRINCIPAL, remotePrincipal);
comment|//
comment|//            if (!useCerts) { // Add extra info that would have been added by
comment|//                             // super
comment|//                String cipherSuite = sslSocket.getSession().getCipherSuite();
comment|//                Integer keySize = Integer.valueOf(ServletSSL.deduceKeyLength(cipherSuite));
comment|//                ;
comment|//
comment|//                request.setAttribute("javax.servlet.request.cipher_suite", cipherSuite);
comment|//                request.setAttribute("javax.servlet.request.key_size", keySize);
comment|//            }
comment|//        }
comment|//
comment|//        if (useCerts)
comment|//            super.customize(endpoint, request);
comment|//    }
comment|//
comment|//    private void logIfDebug(String s) {
comment|//        if (LOG.isDebugEnabled())
comment|//            LOG.debug(s);
comment|//    }
block|}
end_class

end_unit

