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
name|auto
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
name|util
operator|.
name|Set
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
name|SSLServerSocket
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
name|transport
operator|.
name|tcp
operator|.
name|SslTransportFactory
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
name|tcp
operator|.
name|TcpTransport
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
name|tcp
operator|.
name|TcpTransportFactory
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
comment|/**  *  An SSL TransportServer.  *  *  Allows for client certificate authentication (refer to setNeedClientAuth for  *      details).  *  NOTE: Client certificate authentication is disabled by default.  *  */
end_comment

begin_class
specifier|public
class|class
name|AutoSslTransportServer
extends|extends
name|AutoTcpTransportServer
block|{
comment|// Specifies if sockets created from this server should needClientAuth.
specifier|private
name|boolean
name|needClientAuth
decl_stmt|;
comment|// Specifies if sockets created from this server should wantClientAuth.
specifier|private
name|boolean
name|wantClientAuth
decl_stmt|;
specifier|public
name|AutoSslTransportServer
parameter_list|(
name|SslTransportFactory
name|transportFactory
parameter_list|,
name|URI
name|location
parameter_list|,
name|SSLServerSocketFactory
name|serverSocketFactory
parameter_list|,
name|BrokerService
name|brokerService
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|enabledProtocols
parameter_list|)
throws|throws
name|IOException
throws|,
name|URISyntaxException
block|{
name|super
argument_list|(
name|transportFactory
argument_list|,
name|location
argument_list|,
name|serverSocketFactory
argument_list|,
name|brokerService
argument_list|,
name|enabledProtocols
argument_list|)
expr_stmt|;
comment|// TODO Auto-generated constructor stub
block|}
comment|/**      * Sets whether client authentication should be required      * Must be called before {@link #bind()}      * Note: Calling this method clears the wantClientAuth flag      * in the underlying implementation.      */
specifier|public
name|void
name|setNeedClientAuth
parameter_list|(
name|boolean
name|needAuth
parameter_list|)
block|{
name|this
operator|.
name|needClientAuth
operator|=
name|needAuth
expr_stmt|;
block|}
comment|/**      * Returns whether client authentication should be required.      */
specifier|public
name|boolean
name|getNeedClientAuth
parameter_list|()
block|{
return|return
name|this
operator|.
name|needClientAuth
return|;
block|}
comment|/**      * Returns whether client authentication should be requested.      */
specifier|public
name|boolean
name|getWantClientAuth
parameter_list|()
block|{
return|return
name|this
operator|.
name|wantClientAuth
return|;
block|}
comment|/**      * Sets whether client authentication should be requested.      * Must be called before {@link #bind()}      * Note: Calling this method clears the needClientAuth flag      * in the underlying implementation.      */
specifier|public
name|void
name|setWantClientAuth
parameter_list|(
name|boolean
name|wantAuth
parameter_list|)
block|{
name|this
operator|.
name|wantClientAuth
operator|=
name|wantAuth
expr_stmt|;
block|}
comment|/**      * Binds this socket to the previously specified URI.      *      * Overridden to allow for proper handling of needClientAuth.      *      * @throws IOException passed up from TcpTransportServer.      */
annotation|@
name|Override
specifier|public
name|void
name|bind
parameter_list|()
throws|throws
name|IOException
block|{
name|super
operator|.
name|bind
argument_list|()
expr_stmt|;
if|if
condition|(
name|needClientAuth
condition|)
block|{
operator|(
operator|(
name|SSLServerSocket
operator|)
name|this
operator|.
name|serverSocket
operator|)
operator|.
name|setNeedClientAuth
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|wantClientAuth
condition|)
block|{
operator|(
operator|(
name|SSLServerSocket
operator|)
name|this
operator|.
name|serverSocket
operator|)
operator|.
name|setWantClientAuth
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Used to create Transports for this server.      *      * Overridden to allow the use of SslTransports (instead of TcpTransports).      *      * @param socket The incoming socket that will be wrapped into the new Transport.      * @param format The WireFormat being used.      * @return The newly return (SSL) Transport.      * @throws IOException      */
annotation|@
name|Override
specifier|protected
name|TcpTransport
name|createTransport
parameter_list|(
name|Socket
name|socket
parameter_list|,
name|WireFormat
name|format
parameter_list|,
name|TcpTransportFactory
name|detectedTransportFactory
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|detectedTransportFactory
operator|.
name|createTransport
argument_list|(
name|format
argument_list|,
name|socket
argument_list|,
name|this
operator|.
name|initBuffer
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isSslServer
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
block|}
end_class

end_unit

