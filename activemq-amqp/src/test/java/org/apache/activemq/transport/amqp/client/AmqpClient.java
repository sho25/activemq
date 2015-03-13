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
name|amqp
operator|.
name|client
package|;
end_package

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
name|util
operator|.
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|transport
operator|.
name|amqp
operator|.
name|client
operator|.
name|util
operator|.
name|ClientTcpTransport
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|qpid
operator|.
name|proton
operator|.
name|amqp
operator|.
name|Symbol
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_comment
comment|/**  * Connection instance used to connect to the Broker using Proton as  * the AMQP protocol handler.  */
end_comment

begin_class
specifier|public
class|class
name|AmqpClient
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|AmqpClient
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|String
name|username
decl_stmt|;
specifier|private
specifier|final
name|String
name|password
decl_stmt|;
specifier|private
specifier|final
name|URI
name|remoteURI
decl_stmt|;
specifier|private
name|AmqpStateInspector
name|stateInspector
init|=
operator|new
name|AmqpStateInspector
argument_list|()
decl_stmt|;
specifier|private
name|List
argument_list|<
name|Symbol
argument_list|>
name|offeredCapabilities
init|=
name|Collections
operator|.
name|emptyList
argument_list|()
decl_stmt|;
specifier|private
name|Map
argument_list|<
name|Symbol
argument_list|,
name|Object
argument_list|>
name|offeredProperties
init|=
name|Collections
operator|.
name|emptyMap
argument_list|()
decl_stmt|;
comment|/**      * Creates an AmqpClient instance which can be used as a factory for connections.      *      * @param remoteURI      *        The address of the remote peer to connect to.      * @param username      *	      The user name to use when authenticating the client.      * @param password      *		  The password to use when authenticating the client.      */
specifier|public
name|AmqpClient
parameter_list|(
name|URI
name|remoteURI
parameter_list|,
name|String
name|username
parameter_list|,
name|String
name|password
parameter_list|)
block|{
name|this
operator|.
name|remoteURI
operator|=
name|remoteURI
expr_stmt|;
name|this
operator|.
name|password
operator|=
name|password
expr_stmt|;
name|this
operator|.
name|username
operator|=
name|username
expr_stmt|;
block|}
comment|/**      * Creates a connection with the broker at the given location, this method initiates a      * connect attempt immediately and will fail if the remote peer cannot be reached.      *      * @returns a new connection object used to interact with the connected peer.      *      * @throws Exception if an error occurs attempting to connect to the Broker.      */
specifier|public
name|AmqpConnection
name|connect
parameter_list|()
throws|throws
name|Exception
block|{
name|AmqpConnection
name|connection
init|=
name|createConnection
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Attempting to create new connection to peer: {}"
argument_list|,
name|remoteURI
argument_list|)
expr_stmt|;
name|connection
operator|.
name|connect
argument_list|()
expr_stmt|;
return|return
name|connection
return|;
block|}
comment|/**      * Creates a connection object using the configured values for user, password, remote URI      * etc.  This method does not immediately initiate a connection to the remote leaving that      * to the caller which provides a connection object that can have additional configuration      * changes applied before the<code>connect</code> method is invoked.      *      * @returns a new connection object used to interact with the connected peer.      *      * @throws Exception if an error occurs attempting to connect to the Broker.      */
specifier|public
name|AmqpConnection
name|createConnection
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|username
operator|==
literal|null
operator|&&
name|password
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Password must be null if user name value is null"
argument_list|)
throw|;
block|}
name|ClientTcpTransport
name|transport
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|remoteURI
operator|.
name|getScheme
argument_list|()
operator|.
name|equals
argument_list|(
literal|"tcp"
argument_list|)
condition|)
block|{
name|transport
operator|=
operator|new
name|ClientTcpTransport
argument_list|(
name|remoteURI
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Client only support TCP currently."
argument_list|)
throw|;
block|}
name|AmqpConnection
name|connection
init|=
operator|new
name|AmqpConnection
argument_list|(
name|transport
argument_list|,
name|username
argument_list|,
name|password
argument_list|)
decl_stmt|;
name|connection
operator|.
name|setOfferedCapabilities
argument_list|(
name|getOfferedCapabilities
argument_list|()
argument_list|)
expr_stmt|;
name|connection
operator|.
name|setOfferedProperties
argument_list|(
name|getOfferedProperties
argument_list|()
argument_list|)
expr_stmt|;
name|connection
operator|.
name|setStateInspector
argument_list|(
name|getStateInspector
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|connection
return|;
block|}
comment|/**      * @return the user name value given when connect was called, always null before connect.      */
specifier|public
name|String
name|getUsername
parameter_list|()
block|{
return|return
name|username
return|;
block|}
comment|/**      * @return the password value given when connect was called, always null before connect.      */
specifier|public
name|String
name|getPassword
parameter_list|()
block|{
return|return
name|password
return|;
block|}
comment|/**      * @return the currently set address to use to connect to the AMQP peer.      */
specifier|public
name|URI
name|getRemoteURI
parameter_list|()
block|{
return|return
name|remoteURI
return|;
block|}
comment|/**      * Sets the offered capabilities that should be used when a new connection attempt      * is made.      *      * @param offeredCapabilities      *        the list of capabilities to offer when connecting.      */
specifier|public
name|void
name|setOfferedCapabilities
parameter_list|(
name|List
argument_list|<
name|Symbol
argument_list|>
name|offeredCapabilities
parameter_list|)
block|{
if|if
condition|(
name|offeredCapabilities
operator|!=
literal|null
condition|)
block|{
name|offeredCapabilities
operator|=
name|Collections
operator|.
name|emptyList
argument_list|()
expr_stmt|;
block|}
name|this
operator|.
name|offeredCapabilities
operator|=
name|offeredCapabilities
expr_stmt|;
block|}
comment|/**      * @return an unmodifiable view of the currently set offered capabilities      */
specifier|public
name|List
argument_list|<
name|Symbol
argument_list|>
name|getOfferedCapabilities
parameter_list|()
block|{
return|return
name|Collections
operator|.
name|unmodifiableList
argument_list|(
name|offeredCapabilities
argument_list|)
return|;
block|}
comment|/**      * Sets the offered connection properties that should be used when a new connection      * attempt is made.      *      * @param connectionProperties      *        the map of properties to offer when connecting.      */
specifier|public
name|void
name|setOfferedProperties
parameter_list|(
name|Map
argument_list|<
name|Symbol
argument_list|,
name|Object
argument_list|>
name|offeredProperties
parameter_list|)
block|{
if|if
condition|(
name|offeredProperties
operator|!=
literal|null
condition|)
block|{
name|offeredProperties
operator|=
name|Collections
operator|.
name|emptyMap
argument_list|()
expr_stmt|;
block|}
name|this
operator|.
name|offeredProperties
operator|=
name|offeredProperties
expr_stmt|;
block|}
comment|/**      * @return an unmodifiable view of the currently set connection properties.      */
specifier|public
name|Map
argument_list|<
name|Symbol
argument_list|,
name|Object
argument_list|>
name|getOfferedProperties
parameter_list|()
block|{
return|return
name|Collections
operator|.
name|unmodifiableMap
argument_list|(
name|offeredProperties
argument_list|)
return|;
block|}
comment|/**      * @return the currently set state inspector used to check state after various events.      */
specifier|public
name|AmqpStateInspector
name|getStateInspector
parameter_list|()
block|{
return|return
name|stateInspector
return|;
block|}
comment|/**      * Sets the state inspector used to check that the AMQP resource is valid after      * specific lifecycle events such as open and close.      *      * @param stateInspector      *        the new state inspector to use.      */
specifier|public
name|void
name|setStateInspector
parameter_list|(
name|AmqpStateInspector
name|stateInspector
parameter_list|)
block|{
if|if
condition|(
name|stateInspector
operator|==
literal|null
condition|)
block|{
name|stateInspector
operator|=
operator|new
name|AmqpStateInspector
argument_list|()
expr_stmt|;
block|}
name|this
operator|.
name|stateInspector
operator|=
name|stateInspector
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"AmqpClient: "
operator|+
name|getRemoteURI
argument_list|()
operator|.
name|getHost
argument_list|()
operator|+
literal|":"
operator|+
name|getRemoteURI
argument_list|()
operator|.
name|getPort
argument_list|()
return|;
block|}
comment|/**      * Creates an anonymous connection with the broker at the given location.      *      * @param broker      *        the address of the remote broker instance.      *      * @returns a new connection object used to interact with the connected peer.      *      * @throws Exception if an error occurs attempting to connect to the Broker.      */
specifier|public
specifier|static
name|AmqpConnection
name|connect
parameter_list|(
name|URI
name|broker
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|connect
argument_list|(
name|broker
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
return|;
block|}
comment|/**      * Creates a connection with the broker at the given location.      *      * @param broker      *        the address of the remote broker instance.      * @param username      *        the user name to use to connect to the broker or null for anonymous.      * @param password      *        the password to use to connect to the broker, must be null if user name is null.      *      * @returns a new connection object used to interact with the connected peer.      *      * @throws Exception if an error occurs attempting to connect to the Broker.      */
specifier|public
specifier|static
name|AmqpConnection
name|connect
parameter_list|(
name|URI
name|broker
parameter_list|,
name|String
name|username
parameter_list|,
name|String
name|password
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|username
operator|==
literal|null
operator|&&
name|password
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Password must be null if user name value is null"
argument_list|)
throw|;
block|}
name|AmqpClient
name|client
init|=
operator|new
name|AmqpClient
argument_list|(
name|broker
argument_list|,
name|username
argument_list|,
name|password
argument_list|)
decl_stmt|;
return|return
name|client
operator|.
name|connect
argument_list|()
return|;
block|}
block|}
end_class

end_unit

