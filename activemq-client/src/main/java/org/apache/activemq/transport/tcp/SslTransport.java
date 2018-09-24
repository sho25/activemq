begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|Socket
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|SocketException
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
name|UnknownHostException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|cert
operator|.
name|X509Certificate
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
name|javax
operator|.
name|net
operator|.
name|ssl
operator|.
name|SSLParameters
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
name|SSLPeerUnverifiedException
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
name|SSLSession
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
name|SSLSocket
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
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|command
operator|.
name|ConnectionInfo
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
name|wireformat
operator|.
name|WireFormat
import|;
end_import

begin_comment
comment|/**  * A Transport class that uses SSL and client-side certificate authentication.  * Client-side certificate authentication must be enabled through the  * constructor. By default, this class will have the same client authentication  * behavior as the socket it is passed. This class will set ConnectionInfo's  * transportContext to the SSL certificates of the client. NOTE: Accessor method  * for needClientAuth was not provided on purpose. This is because  * needClientAuth's value must be set before the socket is connected. Otherwise,  * unexpected situations may occur.  */
end_comment

begin_class
specifier|public
class|class
name|SslTransport
extends|extends
name|TcpTransport
block|{
comment|/**      * Default to null as there are different defaults between server and client, initialiseSocket      * for more details      */
specifier|private
name|Boolean
name|verifyHostName
init|=
literal|null
decl_stmt|;
comment|/**      * Connect to a remote node such as a Broker.      *      * @param wireFormat The WireFormat to be used.      * @param socketFactory The socket factory to be used. Forcing SSLSockets      *                for obvious reasons.      * @param remoteLocation The remote location.      * @param localLocation The local location.      * @param needClientAuth If set to true, the underlying socket will need      *                client certificate authentication.      * @throws UnknownHostException If TcpTransport throws.      * @throws IOException If TcpTransport throws.      */
annotation|@
name|SuppressWarnings
argument_list|(
block|{
literal|"unchecked"
block|,
literal|"rawtypes"
block|}
argument_list|)
specifier|public
name|SslTransport
parameter_list|(
name|WireFormat
name|wireFormat
parameter_list|,
name|SSLSocketFactory
name|socketFactory
parameter_list|,
name|URI
name|remoteLocation
parameter_list|,
name|URI
name|localLocation
parameter_list|,
name|boolean
name|needClientAuth
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|wireFormat
argument_list|,
name|socketFactory
argument_list|,
name|remoteLocation
argument_list|,
name|localLocation
argument_list|)
expr_stmt|;
if|if
condition|(
name|this
operator|.
name|socket
operator|!=
literal|null
condition|)
block|{
operator|(
operator|(
name|SSLSocket
operator|)
name|this
operator|.
name|socket
operator|)
operator|.
name|setNeedClientAuth
argument_list|(
name|needClientAuth
argument_list|)
expr_stmt|;
comment|// Lets try to configure the SSL SNI field.  Handy in case your using
comment|// a single proxy to route to different messaging apps.
comment|// On java 1.7 it seems like it can only be configured via reflection.
comment|// TODO: find out if this will work on java 1.8
name|HashMap
name|props
init|=
operator|new
name|HashMap
argument_list|()
decl_stmt|;
name|props
operator|.
name|put
argument_list|(
literal|"host"
argument_list|,
name|remoteLocation
operator|.
name|getHost
argument_list|()
argument_list|)
expr_stmt|;
name|IntrospectionSupport
operator|.
name|setProperties
argument_list|(
name|this
operator|.
name|socket
argument_list|,
name|props
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|protected
name|void
name|initialiseSocket
parameter_list|(
name|Socket
name|sock
parameter_list|)
throws|throws
name|SocketException
throws|,
name|IllegalArgumentException
block|{
comment|/**          * This needs to default to null because this transport class is used for both a server transport          * and a client connection and we have different defaults for both.          * If we default it to a value it might override the transport server setting          * that was configured inside TcpTransportServer (which sets a default to false for server side)          *          * The idea here is that if this is a server transport then verifyHostName will be set by the setter          * and not be null as TcpTransportServer will set a default value of false (or a user will set it          * using transport.verifyHostName) but if this is a client connection the value will be null by default          * and will stay null if the user uses socket.verifyHostName to set the value or doesn't use the setter          * If it is null then we can check socketOptions for the value and if not set there then we can          * just set a default of true as this will be a client          *          * Unfortunately we have to do this to stay consistent because every other SSL option on the client          * side can be configured using socket. but this particular option isn't actually part of the socket          * so it makes it tricky from a user standpoint. For consistency sake I think it makes sense to allow          * using the socket. prefix that has been established so users do not get confused (as well as          * allow using no prefix which just calls the setter directly)          *          * Because of this there are actually two ways a client can configure this value, the client can either use          * socket.verifyHostName=<value> as mentioned or just simply use verifyHostName=<value> without using the socket.          * prefix and that will also work as the value will be set using the setter on the transport          *          * example server transport config:          *  ssl://localhost:61616?transport.verifyHostName=true          *          * example from client:          *  ssl://localhost:61616?verifyHostName=true          *                  OR          *  ssl://localhost:61616?socket.verifyHostName=true          *          */
if|if
condition|(
name|verifyHostName
operator|==
literal|null
condition|)
block|{
comment|//Check to see if the user included the value as part of socket options and if so then use that value
if|if
condition|(
name|socketOptions
operator|!=
literal|null
operator|&&
name|socketOptions
operator|.
name|containsKey
argument_list|(
literal|"verifyHostName"
argument_list|)
condition|)
block|{
name|verifyHostName
operator|=
name|Boolean
operator|.
name|parseBoolean
argument_list|(
name|socketOptions
operator|.
name|get
argument_list|(
literal|"verifyHostName"
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|socketOptions
operator|.
name|remove
argument_list|(
literal|"verifyHostName"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|//If null and not set then this is a client so default to true
name|verifyHostName
operator|=
literal|true
expr_stmt|;
block|}
block|}
if|if
condition|(
name|verifyHostName
condition|)
block|{
name|SSLParameters
name|sslParams
init|=
operator|new
name|SSLParameters
argument_list|()
decl_stmt|;
name|sslParams
operator|.
name|setEndpointIdentificationAlgorithm
argument_list|(
literal|"HTTPS"
argument_list|)
expr_stmt|;
operator|(
operator|(
name|SSLSocket
operator|)
name|this
operator|.
name|socket
operator|)
operator|.
name|setSSLParameters
argument_list|(
name|sslParams
argument_list|)
expr_stmt|;
block|}
name|super
operator|.
name|initialiseSocket
argument_list|(
name|sock
argument_list|)
expr_stmt|;
block|}
comment|/**      * Initialize from a ServerSocket. No access to needClientAuth is given      * since it is already set within the provided socket.      *      * @param wireFormat The WireFormat to be used.      * @param socket The Socket to be used. Forcing SSL.      * @throws IOException If TcpTransport throws.      */
specifier|public
name|SslTransport
parameter_list|(
name|WireFormat
name|wireFormat
parameter_list|,
name|SSLSocket
name|socket
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|wireFormat
argument_list|,
name|socket
argument_list|)
expr_stmt|;
block|}
specifier|public
name|SslTransport
parameter_list|(
name|WireFormat
name|format
parameter_list|,
name|SSLSocket
name|socket
parameter_list|,
name|InitBuffer
name|initBuffer
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|format
argument_list|,
name|socket
argument_list|,
name|initBuffer
argument_list|)
expr_stmt|;
block|}
comment|/**      * Overriding in order to add the client's certificates to ConnectionInfo      * Commmands.      *      * @param command The Command coming in.      */
annotation|@
name|Override
specifier|public
name|void
name|doConsume
parameter_list|(
name|Object
name|command
parameter_list|)
block|{
comment|// The instanceof can be avoided, but that would require modifying the
comment|// Command clas tree and that would require too much effort right
comment|// now.
if|if
condition|(
name|command
operator|instanceof
name|ConnectionInfo
condition|)
block|{
name|ConnectionInfo
name|connectionInfo
init|=
operator|(
name|ConnectionInfo
operator|)
name|command
decl_stmt|;
name|connectionInfo
operator|.
name|setTransportContext
argument_list|(
name|getPeerCertificates
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|super
operator|.
name|doConsume
argument_list|(
name|command
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setVerifyHostName
parameter_list|(
name|Boolean
name|verifyHostName
parameter_list|)
block|{
name|this
operator|.
name|verifyHostName
operator|=
name|verifyHostName
expr_stmt|;
block|}
comment|/**      * @return peer certificate chain associated with the ssl socket      */
annotation|@
name|Override
specifier|public
name|X509Certificate
index|[]
name|getPeerCertificates
parameter_list|()
block|{
name|SSLSocket
name|sslSocket
init|=
operator|(
name|SSLSocket
operator|)
name|this
operator|.
name|socket
decl_stmt|;
name|SSLSession
name|sslSession
init|=
name|sslSocket
operator|.
name|getSession
argument_list|()
decl_stmt|;
name|X509Certificate
index|[]
name|clientCertChain
decl_stmt|;
try|try
block|{
name|clientCertChain
operator|=
operator|(
name|X509Certificate
index|[]
operator|)
name|sslSession
operator|.
name|getPeerCertificates
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SSLPeerUnverifiedException
name|e
parameter_list|)
block|{
name|clientCertChain
operator|=
literal|null
expr_stmt|;
block|}
return|return
name|clientCertChain
return|;
block|}
comment|/**      * @return pretty print of 'this'      */
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"ssl://"
operator|+
name|socket
operator|.
name|getInetAddress
argument_list|()
operator|+
literal|":"
operator|+
name|socket
operator|.
name|getPort
argument_list|()
return|;
block|}
block|}
end_class

end_unit

