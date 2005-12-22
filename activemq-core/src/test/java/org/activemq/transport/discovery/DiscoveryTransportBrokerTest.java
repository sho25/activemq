begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Copyright 2004 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|activemq
operator|.
name|transport
operator|.
name|discovery
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
name|javax
operator|.
name|jms
operator|.
name|DeliveryMode
import|;
end_import

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|Test
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
name|ActiveMQQueue
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
name|ProducerInfo
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
name|network
operator|.
name|NetworkTestSupport
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
name|Transport
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
name|org
operator|.
name|activemq
operator|.
name|transport
operator|.
name|failover
operator|.
name|FailoverTransport
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

begin_class
specifier|public
class|class
name|DiscoveryTransportBrokerTest
extends|extends
name|NetworkTestSupport
block|{
specifier|static
specifier|final
specifier|private
name|Log
name|log
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|DiscoveryTransportBrokerTest
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
name|void
name|testPublisherFailsOver
parameter_list|()
throws|throws
name|Throwable
block|{
name|ActiveMQDestination
name|destination
init|=
operator|new
name|ActiveMQQueue
argument_list|(
literal|"TEST"
argument_list|)
decl_stmt|;
name|int
name|deliveryMode
init|=
name|DeliveryMode
operator|.
name|NON_PERSISTENT
decl_stmt|;
comment|// Start a normal consumer on the local broker
name|StubConnection
name|connection1
init|=
name|createConnection
argument_list|()
decl_stmt|;
name|ConnectionInfo
name|connectionInfo1
init|=
name|createConnectionInfo
argument_list|()
decl_stmt|;
name|SessionInfo
name|sessionInfo1
init|=
name|createSessionInfo
argument_list|(
name|connectionInfo1
argument_list|)
decl_stmt|;
name|ConsumerInfo
name|consumerInfo1
init|=
name|createConsumerInfo
argument_list|(
name|sessionInfo1
argument_list|,
name|destination
argument_list|)
decl_stmt|;
name|connection1
operator|.
name|send
argument_list|(
name|connectionInfo1
argument_list|)
expr_stmt|;
name|connection1
operator|.
name|send
argument_list|(
name|sessionInfo1
argument_list|)
expr_stmt|;
name|connection1
operator|.
name|request
argument_list|(
name|consumerInfo1
argument_list|)
expr_stmt|;
comment|// Start a normal consumer on a remote broker
name|StubConnection
name|connection2
init|=
name|createRemoteConnection
argument_list|()
decl_stmt|;
name|ConnectionInfo
name|connectionInfo2
init|=
name|createConnectionInfo
argument_list|()
decl_stmt|;
name|SessionInfo
name|sessionInfo2
init|=
name|createSessionInfo
argument_list|(
name|connectionInfo2
argument_list|)
decl_stmt|;
name|ConsumerInfo
name|consumerInfo2
init|=
name|createConsumerInfo
argument_list|(
name|sessionInfo2
argument_list|,
name|destination
argument_list|)
decl_stmt|;
name|connection2
operator|.
name|send
argument_list|(
name|connectionInfo2
argument_list|)
expr_stmt|;
name|connection2
operator|.
name|send
argument_list|(
name|sessionInfo2
argument_list|)
expr_stmt|;
name|connection2
operator|.
name|request
argument_list|(
name|consumerInfo2
argument_list|)
expr_stmt|;
comment|// Start a failover publisher.
name|StubConnection
name|connection3
init|=
name|createFailoverConnection
argument_list|()
decl_stmt|;
name|ConnectionInfo
name|connectionInfo3
init|=
name|createConnectionInfo
argument_list|()
decl_stmt|;
name|SessionInfo
name|sessionInfo3
init|=
name|createSessionInfo
argument_list|(
name|connectionInfo3
argument_list|)
decl_stmt|;
name|ProducerInfo
name|producerInfo3
init|=
name|createProducerInfo
argument_list|(
name|sessionInfo3
argument_list|)
decl_stmt|;
name|connection3
operator|.
name|send
argument_list|(
name|connectionInfo3
argument_list|)
expr_stmt|;
name|connection3
operator|.
name|send
argument_list|(
name|sessionInfo3
argument_list|)
expr_stmt|;
name|connection3
operator|.
name|send
argument_list|(
name|producerInfo3
argument_list|)
expr_stmt|;
comment|// Send the message using the fail over publisher.
name|connection3
operator|.
name|request
argument_list|(
name|createMessage
argument_list|(
name|producerInfo3
argument_list|,
name|destination
argument_list|,
name|deliveryMode
argument_list|)
argument_list|)
expr_stmt|;
comment|// The message will be sent to one of the brokers.
name|FailoverTransport
name|ft
init|=
operator|(
name|FailoverTransport
operator|)
name|connection3
operator|.
name|getTransport
argument_list|()
operator|.
name|narrow
argument_list|(
name|FailoverTransport
operator|.
name|class
argument_list|)
decl_stmt|;
comment|// See which broker we were connected to.
name|StubConnection
name|connectionA
decl_stmt|;
name|StubConnection
name|connectionB
decl_stmt|;
name|TransportConnector
name|serverA
decl_stmt|;
if|if
condition|(
name|connector
operator|.
name|getServer
argument_list|()
operator|.
name|getConnectURI
argument_list|()
operator|.
name|equals
argument_list|(
name|ft
operator|.
name|getConnectedTransportURI
argument_list|()
argument_list|)
condition|)
block|{
name|connectionA
operator|=
name|connection1
expr_stmt|;
name|connectionB
operator|=
name|connection2
expr_stmt|;
name|serverA
operator|=
name|connector
expr_stmt|;
block|}
else|else
block|{
name|connectionA
operator|=
name|connection2
expr_stmt|;
name|connectionB
operator|=
name|connection1
expr_stmt|;
name|serverA
operator|=
name|remoteConnector
expr_stmt|;
block|}
name|assertNotNull
argument_list|(
name|receiveMessage
argument_list|(
name|connectionA
argument_list|)
argument_list|)
expr_stmt|;
name|assertNoMessagesLeft
argument_list|(
name|connectionB
argument_list|)
expr_stmt|;
comment|// Dispose the server so that it fails over to the other server.
name|log
operator|.
name|info
argument_list|(
literal|"Disconnecting active server"
argument_list|)
expr_stmt|;
name|serverA
operator|.
name|stop
argument_list|()
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Sending request that should failover"
argument_list|)
expr_stmt|;
name|connection3
operator|.
name|request
argument_list|(
name|createMessage
argument_list|(
name|producerInfo3
argument_list|,
name|destination
argument_list|,
name|deliveryMode
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|receiveMessage
argument_list|(
name|connectionB
argument_list|)
argument_list|)
expr_stmt|;
name|assertNoMessagesLeft
argument_list|(
name|connectionA
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|String
name|getLocalURI
parameter_list|()
block|{
return|return
literal|"tcp://localhost:0?wireFormat.tcpNoDelayEnabled=true"
return|;
block|}
specifier|protected
name|String
name|getRemoteURI
parameter_list|()
block|{
return|return
literal|"tcp://localhost:0?wireFormat.tcpNoDelayEnabled=true"
return|;
block|}
specifier|protected
name|TransportConnector
name|createConnector
parameter_list|()
throws|throws
name|Exception
throws|,
name|IOException
throws|,
name|URISyntaxException
block|{
name|TransportConnector
name|x
init|=
name|super
operator|.
name|createConnector
argument_list|()
decl_stmt|;
name|x
operator|.
name|setDiscoveryUri
argument_list|(
operator|new
name|URI
argument_list|(
literal|"multicast://default"
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|x
return|;
block|}
specifier|protected
name|TransportConnector
name|createRemoteConnector
parameter_list|()
throws|throws
name|Exception
throws|,
name|IOException
throws|,
name|URISyntaxException
block|{
name|TransportConnector
name|x
init|=
name|super
operator|.
name|createRemoteConnector
argument_list|()
decl_stmt|;
name|x
operator|.
name|setDiscoveryUri
argument_list|(
operator|new
name|URI
argument_list|(
literal|"multicast://default"
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|x
return|;
block|}
specifier|protected
name|StubConnection
name|createFailoverConnection
parameter_list|()
throws|throws
name|Exception
block|{
name|URI
name|failoverURI
init|=
operator|new
name|URI
argument_list|(
literal|"discovery:multicast://default"
argument_list|)
decl_stmt|;
name|Transport
name|transport
init|=
name|TransportFactory
operator|.
name|connect
argument_list|(
name|failoverURI
argument_list|)
decl_stmt|;
name|StubConnection
name|connection
init|=
operator|new
name|StubConnection
argument_list|(
name|transport
argument_list|)
decl_stmt|;
name|connections
operator|.
name|add
argument_list|(
name|connection
argument_list|)
expr_stmt|;
return|return
name|connection
return|;
block|}
specifier|public
specifier|static
name|Test
name|suite
parameter_list|()
block|{
return|return
name|suite
argument_list|(
name|DiscoveryTransportBrokerTest
operator|.
name|class
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
block|{
name|junit
operator|.
name|textui
operator|.
name|TestRunner
operator|.
name|run
argument_list|(
name|suite
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

