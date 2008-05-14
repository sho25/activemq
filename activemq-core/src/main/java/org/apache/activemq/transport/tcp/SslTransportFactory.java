begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|tcp
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URI
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URISyntaxException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|UnknownHostException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|KeyManagementException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|NoSuchAlgorithmException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|SecureRandom
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|net
operator|.
name|ServerSocketFactory
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|net
operator|.
name|SocketFactory
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|net
operator|.
name|ssl
operator|.
name|KeyManager
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|net
operator|.
name|ssl
operator|.
name|SSLContext
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|net
operator|.
name|ssl
operator|.
name|SSLServerSocketFactory
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|net
operator|.
name|ssl
operator|.
name|SSLSocketFactory
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|net
operator|.
name|ssl
operator|.
name|TrustManager
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|broker
operator|.
name|BrokerService
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|broker
operator|.
name|BrokerServiceAware
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|broker
operator|.
name|SslContext
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|openwire
operator|.
name|OpenWireFormat
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|transport
operator|.
name|InactivityMonitor
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|transport
operator|.
name|Transport
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|transport
operator|.
name|TransportLoggerFactory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|transport
operator|.
name|TransportServer
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|transport
operator|.
name|WireFormatNegotiator
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|util
operator|.
name|IOExceptionSupport
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|util
operator|.
name|IntrospectionSupport
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|util
operator|.
name|URISupport
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|wireformat
operator|.
name|WireFormat
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|Log
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|LogFactory
import|;
end_import

begin_comment
comment|/**  * An implementation of the TcpTransportFactory using SSL. The major  * contribution from this class is that it is aware of SslTransportServer and  * SslTransport classes. All Transports and TransportServers created from this  * factory will have their needClientAuth option set to false.  *   * @author sepandm@gmail.com (Sepand)  * @author David Martin Clavo david(dot)martin(dot)clavo(at)gmail.com (logging improvement modifications)  * @version $Revision$  */
end_comment

begin_class
specifier|public
class|class
name|SslTransportFactory
extends|extends
name|TcpTransportFactory
implements|implements
name|BrokerServiceAware
block|{
comment|// The log this uses.,
specifier|private
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|SslTransportFactory
operator|.
name|class
argument_list|)
decl_stmt|;
comment|// The context used to creat ssl sockets.
specifier|private
name|SSLContext
name|sslContext
decl_stmt|;
comment|/**      * Constructor. Nothing special.      */
specifier|public
name|SslTransportFactory
parameter_list|()
block|{     }
comment|/**      * Overriding to use SslTransportServer and allow for proper reflection.      */
specifier|public
name|TransportServer
name|doBind
parameter_list|(
specifier|final
name|URI
name|location
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|options
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|(
name|URISupport
operator|.
name|parseParamters
argument_list|(
name|location
argument_list|)
argument_list|)
decl_stmt|;
name|ServerSocketFactory
name|serverSocketFactory
init|=
name|createServerSocketFactory
argument_list|()
decl_stmt|;
name|SslTransportServer
name|server
init|=
operator|new
name|SslTransportServer
argument_list|(
name|this
argument_list|,
name|location
argument_list|,
operator|(
name|SSLServerSocketFactory
operator|)
name|serverSocketFactory
argument_list|)
decl_stmt|;
name|server
operator|.
name|setWireFormatFactory
argument_list|(
name|createWireFormatFactory
argument_list|(
name|options
argument_list|)
argument_list|)
expr_stmt|;
name|IntrospectionSupport
operator|.
name|setProperties
argument_list|(
name|server
argument_list|,
name|options
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|transportOptions
init|=
name|IntrospectionSupport
operator|.
name|extractProperties
argument_list|(
name|options
argument_list|,
literal|"transport."
argument_list|)
decl_stmt|;
name|server
operator|.
name|setTransportOption
argument_list|(
name|transportOptions
argument_list|)
expr_stmt|;
name|server
operator|.
name|bind
argument_list|()
expr_stmt|;
return|return
name|server
return|;
block|}
catch|catch
parameter_list|(
name|URISyntaxException
name|e
parameter_list|)
block|{
throw|throw
name|IOExceptionSupport
operator|.
name|create
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
comment|/**      * Overriding to allow for proper configuration through reflection.      */
specifier|public
name|Transport
name|compositeConfigure
parameter_list|(
name|Transport
name|transport
parameter_list|,
name|WireFormat
name|format
parameter_list|,
name|Map
name|options
parameter_list|)
block|{
name|SslTransport
name|sslTransport
init|=
operator|(
name|SslTransport
operator|)
name|transport
operator|.
name|narrow
argument_list|(
name|SslTransport
operator|.
name|class
argument_list|)
decl_stmt|;
name|IntrospectionSupport
operator|.
name|setProperties
argument_list|(
name|sslTransport
argument_list|,
name|options
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|socketOptions
init|=
name|IntrospectionSupport
operator|.
name|extractProperties
argument_list|(
name|options
argument_list|,
literal|"socket."
argument_list|)
decl_stmt|;
name|sslTransport
operator|.
name|setSocketOptions
argument_list|(
name|socketOptions
argument_list|)
expr_stmt|;
if|if
condition|(
name|sslTransport
operator|.
name|isTrace
argument_list|()
condition|)
block|{
try|try
block|{
name|transport
operator|=
name|TransportLoggerFactory
operator|.
name|getInstance
argument_list|()
operator|.
name|createTransportLogger
argument_list|(
name|transport
argument_list|,
name|sslTransport
operator|.
name|getLogWriterName
argument_list|()
argument_list|,
name|sslTransport
operator|.
name|isDynamicManagement
argument_list|()
argument_list|,
name|sslTransport
operator|.
name|isStartLogging
argument_list|()
argument_list|,
name|sslTransport
operator|.
name|getJmxPort
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Could not create TransportLogger object for: "
operator|+
name|sslTransport
operator|.
name|getLogWriterName
argument_list|()
operator|+
literal|", reason: "
operator|+
name|e
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
name|transport
operator|=
operator|new
name|InactivityMonitor
argument_list|(
name|transport
argument_list|)
expr_stmt|;
comment|// Only need the WireFormatNegotiator if using openwire
if|if
condition|(
name|format
operator|instanceof
name|OpenWireFormat
condition|)
block|{
name|transport
operator|=
operator|new
name|WireFormatNegotiator
argument_list|(
name|transport
argument_list|,
operator|(
name|OpenWireFormat
operator|)
name|format
argument_list|,
name|sslTransport
operator|.
name|getMinmumWireFormatVersion
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|transport
return|;
block|}
comment|/**      * Overriding to use SslTransports.      */
specifier|protected
name|Transport
name|createTransport
parameter_list|(
name|URI
name|location
parameter_list|,
name|WireFormat
name|wf
parameter_list|)
throws|throws
name|UnknownHostException
throws|,
name|IOException
block|{
name|URI
name|localLocation
init|=
literal|null
decl_stmt|;
name|String
name|path
init|=
name|location
operator|.
name|getPath
argument_list|()
decl_stmt|;
comment|// see if the path is a local URI location
if|if
condition|(
name|path
operator|!=
literal|null
operator|&&
name|path
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|int
name|localPortIndex
init|=
name|path
operator|.
name|indexOf
argument_list|(
literal|':'
argument_list|)
decl_stmt|;
try|try
block|{
name|Integer
operator|.
name|parseInt
argument_list|(
name|path
operator|.
name|substring
argument_list|(
name|localPortIndex
operator|+
literal|1
argument_list|,
name|path
operator|.
name|length
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|localString
init|=
name|location
operator|.
name|getScheme
argument_list|()
operator|+
literal|":/"
operator|+
name|path
decl_stmt|;
name|localLocation
operator|=
operator|new
name|URI
argument_list|(
name|localString
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"path isn't a valid local location for SslTransport to use"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
name|SocketFactory
name|socketFactory
init|=
name|createSocketFactory
argument_list|()
decl_stmt|;
return|return
operator|new
name|SslTransport
argument_list|(
name|wf
argument_list|,
operator|(
name|SSLSocketFactory
operator|)
name|socketFactory
argument_list|,
name|location
argument_list|,
name|localLocation
argument_list|,
literal|false
argument_list|)
return|;
block|}
comment|/**      * Sets the key and trust managers used in constructed socket factories.      * Passes given arguments to SSLContext.init(...).      *       * @param km The sources of authentication keys or null.      * @param tm The sources of peer authentication trust decisions or null.      * @param random The source of randomness for this generator or null.      */
specifier|public
name|void
name|setKeyAndTrustManagers
parameter_list|(
name|KeyManager
index|[]
name|km
parameter_list|,
name|TrustManager
index|[]
name|tm
parameter_list|,
name|SecureRandom
name|random
parameter_list|)
throws|throws
name|KeyManagementException
block|{
comment|// Killing old context and making a new one just to be safe.
try|try
block|{
name|sslContext
operator|=
name|SSLContext
operator|.
name|getInstance
argument_list|(
literal|"TLS"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NoSuchAlgorithmException
name|e
parameter_list|)
block|{
comment|// This should not happen unless this class is improperly modified.
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Unknown SSL algorithm encountered."
argument_list|,
name|e
argument_list|)
throw|;
block|}
name|sslContext
operator|.
name|init
argument_list|(
name|km
argument_list|,
name|tm
argument_list|,
name|random
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setBrokerService
parameter_list|(
name|BrokerService
name|brokerService
parameter_list|)
block|{
name|SslContext
name|c
init|=
name|brokerService
operator|.
name|getSslContext
argument_list|()
decl_stmt|;
if|if
condition|(
name|sslContext
operator|==
literal|null
operator|&&
name|c
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|setKeyAndTrustManagers
argument_list|(
name|c
operator|.
name|getKeyManagersAsArray
argument_list|()
argument_list|,
name|c
operator|.
name|getTrustManagersAsArray
argument_list|()
argument_list|,
name|c
operator|.
name|getSecureRandom
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|KeyManagementException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
comment|/**      * Creates a new SSL ServerSocketFactory. The given factory will use      * user-provided key and trust managers (if the user provided them).      *       * @return Newly created (Ssl)ServerSocketFactory.      */
specifier|protected
name|ServerSocketFactory
name|createServerSocketFactory
parameter_list|()
block|{
if|if
condition|(
name|sslContext
operator|==
literal|null
condition|)
block|{
return|return
name|SSLServerSocketFactory
operator|.
name|getDefault
argument_list|()
return|;
block|}
else|else
block|{
return|return
name|sslContext
operator|.
name|getServerSocketFactory
argument_list|()
return|;
block|}
block|}
comment|/**      * Creates a new SSL SocketFactory. The given factory will use user-provided      * key and trust managers (if the user provided them).      *       * @return Newly created (Ssl)SocketFactory.      */
specifier|protected
name|SocketFactory
name|createSocketFactory
parameter_list|()
block|{
if|if
condition|(
name|sslContext
operator|==
literal|null
condition|)
block|{
return|return
name|SSLSocketFactory
operator|.
name|getDefault
argument_list|()
return|;
block|}
else|else
block|{
return|return
name|sslContext
operator|.
name|getSocketFactory
argument_list|()
return|;
block|}
block|}
block|}
end_class

end_unit

