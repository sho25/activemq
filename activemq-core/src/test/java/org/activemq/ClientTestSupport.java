begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** *<a href="http://activemq.org">ActiveMQ: The Open Source Message Fabric</a> * * Copyright 2005 (C) LogicBlaze, Inc. http://www.logicblaze.com * * Licensed under the Apache License, Version 2.0 (the "License"); * you may not use this file except in compliance with the License. * You may obtain a copy of the License at * * http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. * **/
end_comment

begin_package
package|package
name|org
operator|.
name|activemq
package|;
end_package

begin_import
import|import
name|edu
operator|.
name|emory
operator|.
name|mathcs
operator|.
name|backport
operator|.
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeUnit
import|;
end_import

begin_import
import|import
name|edu
operator|.
name|emory
operator|.
name|mathcs
operator|.
name|backport
operator|.
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicBoolean
import|;
end_import

begin_import
import|import
name|org
operator|.
name|activemq
operator|.
name|broker
operator|.
name|Broker
import|;
end_import

begin_import
import|import
name|org
operator|.
name|activemq
operator|.
name|broker
operator|.
name|BrokerFactory
import|;
end_import

begin_import
import|import
name|org
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
name|activemq
operator|.
name|broker
operator|.
name|StubConnection
import|;
end_import

begin_import
import|import
name|org
operator|.
name|activemq
operator|.
name|broker
operator|.
name|TransportConnector
import|;
end_import

begin_import
import|import
name|org
operator|.
name|activemq
operator|.
name|command
operator|.
name|ActiveMQDestination
import|;
end_import

begin_import
import|import
name|org
operator|.
name|activemq
operator|.
name|command
operator|.
name|ConnectionId
import|;
end_import

begin_import
import|import
name|org
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
name|activemq
operator|.
name|command
operator|.
name|ConsumerInfo
import|;
end_import

begin_import
import|import
name|org
operator|.
name|activemq
operator|.
name|command
operator|.
name|Message
import|;
end_import

begin_import
import|import
name|org
operator|.
name|activemq
operator|.
name|command
operator|.
name|MessageAck
import|;
end_import

begin_import
import|import
name|org
operator|.
name|activemq
operator|.
name|command
operator|.
name|MessageDispatch
import|;
end_import

begin_import
import|import
name|org
operator|.
name|activemq
operator|.
name|command
operator|.
name|RemoveInfo
import|;
end_import

begin_import
import|import
name|org
operator|.
name|activemq
operator|.
name|command
operator|.
name|SessionInfo
import|;
end_import

begin_import
import|import
name|org
operator|.
name|activemq
operator|.
name|transport
operator|.
name|TransportFactory
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|JMSException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

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
name|junit
operator|.
name|framework
operator|.
name|TestCase
import|;
end_import

begin_class
specifier|public
class|class
name|ClientTestSupport
extends|extends
name|TestCase
block|{
specifier|private
name|ActiveMQConnectionFactory
name|connFactory
decl_stmt|;
specifier|protected
name|BrokerService
name|broker
decl_stmt|;
specifier|private
name|String
name|brokerURL
init|=
literal|"vm://localhost?broker.persistent=false"
decl_stmt|;
specifier|protected
name|long
name|idGenerator
init|=
literal|0
decl_stmt|;
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|AtomicBoolean
name|connected
init|=
operator|new
name|AtomicBoolean
argument_list|(
literal|false
argument_list|)
decl_stmt|;
name|TransportConnector
name|connector
decl_stmt|;
comment|// Start up a broker with a tcp connector.
try|try
block|{
name|broker
operator|=
name|BrokerFactory
operator|.
name|createBroker
argument_list|(
operator|new
name|URI
argument_list|(
name|this
operator|.
name|brokerURL
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|brokerId
init|=
name|broker
operator|.
name|getBrokerName
argument_list|()
decl_stmt|;
name|connector
operator|=
operator|new
name|TransportConnector
argument_list|(
name|broker
operator|.
name|getBroker
argument_list|()
argument_list|,
name|TransportFactory
operator|.
name|bind
argument_list|(
name|brokerId
argument_list|,
operator|new
name|URI
argument_list|(
name|this
operator|.
name|brokerURL
argument_list|)
argument_list|)
argument_list|)
block|{
comment|// Hook into the connector so we can assert that the server accepted a connection.
specifier|protected
name|org
operator|.
name|activemq
operator|.
name|broker
operator|.
name|Connection
name|createConnection
parameter_list|(
name|org
operator|.
name|activemq
operator|.
name|transport
operator|.
name|Transport
name|transport
parameter_list|)
throws|throws
name|IOException
block|{
name|connected
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
return|return
name|super
operator|.
name|createConnection
argument_list|(
name|transport
argument_list|)
return|;
block|}
block|}
expr_stmt|;
name|connector
operator|.
name|start
argument_list|()
expr_stmt|;
name|broker
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|JMSException
argument_list|(
literal|"Error creating broker "
operator|+
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|URISyntaxException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|JMSException
argument_list|(
literal|"Error creating broker "
operator|+
name|e
argument_list|)
throw|;
block|}
name|URI
name|connectURI
decl_stmt|;
name|connectURI
operator|=
name|connector
operator|.
name|getServer
argument_list|()
operator|.
name|getConnectURI
argument_list|()
expr_stmt|;
comment|// This should create the connection.
name|connFactory
operator|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
name|connectURI
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
if|if
condition|(
name|broker
operator|!=
literal|null
condition|)
block|{
name|broker
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|ActiveMQConnectionFactory
name|getConnectionFactory
parameter_list|()
throws|throws
name|JMSException
block|{
if|if
condition|(
name|this
operator|.
name|connFactory
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|JMSException
argument_list|(
literal|"ActiveMQConnectionFactory is null "
argument_list|)
throw|;
block|}
return|return
name|this
operator|.
name|connFactory
return|;
block|}
comment|//Helper Classes
specifier|protected
name|ConnectionInfo
name|createConnectionInfo
parameter_list|()
throws|throws
name|Throwable
block|{
name|ConnectionInfo
name|info
init|=
operator|new
name|ConnectionInfo
argument_list|()
decl_stmt|;
name|info
operator|.
name|setConnectionId
argument_list|(
operator|new
name|ConnectionId
argument_list|(
literal|"connection:"
operator|+
operator|(
operator|++
name|idGenerator
operator|)
argument_list|)
argument_list|)
expr_stmt|;
name|info
operator|.
name|setClientId
argument_list|(
name|info
operator|.
name|getConnectionId
argument_list|()
operator|.
name|getConnectionId
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|info
return|;
block|}
specifier|protected
name|SessionInfo
name|createSessionInfo
parameter_list|(
name|ConnectionInfo
name|connectionInfo
parameter_list|)
throws|throws
name|Throwable
block|{
name|SessionInfo
name|info
init|=
operator|new
name|SessionInfo
argument_list|(
name|connectionInfo
argument_list|,
operator|++
name|idGenerator
argument_list|)
decl_stmt|;
return|return
name|info
return|;
block|}
specifier|protected
name|ConsumerInfo
name|createConsumerInfo
parameter_list|(
name|SessionInfo
name|sessionInfo
parameter_list|,
name|ActiveMQDestination
name|destination
parameter_list|)
throws|throws
name|Throwable
block|{
name|ConsumerInfo
name|info
init|=
operator|new
name|ConsumerInfo
argument_list|(
name|sessionInfo
argument_list|,
operator|++
name|idGenerator
argument_list|)
decl_stmt|;
name|info
operator|.
name|setBrowser
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|info
operator|.
name|setDestination
argument_list|(
name|destination
argument_list|)
expr_stmt|;
name|info
operator|.
name|setPrefetchSize
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
name|info
operator|.
name|setDispatchAsync
argument_list|(
literal|false
argument_list|)
expr_stmt|;
return|return
name|info
return|;
block|}
specifier|protected
name|RemoveInfo
name|closeConsumerInfo
parameter_list|(
name|ConsumerInfo
name|consumerInfo
parameter_list|)
block|{
return|return
name|consumerInfo
operator|.
name|createRemoveCommand
argument_list|()
return|;
block|}
specifier|protected
name|MessageAck
name|createAck
parameter_list|(
name|ConsumerInfo
name|consumerInfo
parameter_list|,
name|Message
name|msg
parameter_list|,
name|int
name|count
parameter_list|,
name|byte
name|ackType
parameter_list|)
block|{
name|MessageAck
name|ack
init|=
operator|new
name|MessageAck
argument_list|()
decl_stmt|;
name|ack
operator|.
name|setAckType
argument_list|(
name|ackType
argument_list|)
expr_stmt|;
name|ack
operator|.
name|setConsumerId
argument_list|(
name|consumerInfo
operator|.
name|getConsumerId
argument_list|()
argument_list|)
expr_stmt|;
name|ack
operator|.
name|setDestination
argument_list|(
name|msg
operator|.
name|getDestination
argument_list|()
argument_list|)
expr_stmt|;
name|ack
operator|.
name|setLastMessageId
argument_list|(
name|msg
operator|.
name|getMessageId
argument_list|()
argument_list|)
expr_stmt|;
name|ack
operator|.
name|setMessageCount
argument_list|(
name|count
argument_list|)
expr_stmt|;
return|return
name|ack
return|;
block|}
specifier|protected
name|Message
name|receiveMessage
parameter_list|(
name|StubConnection
name|connection
parameter_list|,
name|int
name|MAX_WAIT
parameter_list|)
throws|throws
name|InterruptedException
block|{
while|while
condition|(
literal|true
condition|)
block|{
name|Object
name|o
init|=
name|connection
operator|.
name|getDispatchQueue
argument_list|()
operator|.
name|poll
argument_list|(
name|MAX_WAIT
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
decl_stmt|;
if|if
condition|(
name|o
operator|==
literal|null
condition|)
return|return
literal|null
return|;
if|if
condition|(
name|o
operator|instanceof
name|MessageDispatch
condition|)
block|{
name|MessageDispatch
name|dispatch
init|=
operator|(
name|MessageDispatch
operator|)
name|o
decl_stmt|;
return|return
name|dispatch
operator|.
name|getMessage
argument_list|()
return|;
block|}
block|}
block|}
specifier|protected
name|Broker
name|getBroker
parameter_list|()
throws|throws
name|Exception
block|{
return|return
name|this
operator|.
name|broker
operator|!=
literal|null
condition|?
name|this
operator|.
name|broker
operator|.
name|getBroker
argument_list|()
else|:
literal|null
return|;
block|}
specifier|public
specifier|static
name|void
name|removeMessageStore
parameter_list|()
block|{
if|if
condition|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"activemq.store.dir"
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|recursiveDelete
argument_list|(
operator|new
name|File
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"activemq.store.dir"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"derby.system.home"
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|recursiveDelete
argument_list|(
operator|new
name|File
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"derby.system.home"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
specifier|static
name|void
name|recursiveDelete
parameter_list|(
name|File
name|f
parameter_list|)
block|{
if|if
condition|(
name|f
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
name|File
index|[]
name|files
init|=
name|f
operator|.
name|listFiles
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|files
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|recursiveDelete
argument_list|(
name|files
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
name|f
operator|.
name|delete
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

