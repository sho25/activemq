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
name|util
operator|.
name|concurrent
operator|.
name|CountDownLatch
import|;
end_import

begin_import
import|import
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
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicLong
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|ConnectionFactory
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
name|javax
operator|.
name|jms
operator|.
name|MessageConsumer
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|MessageProducer
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|Session
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|TextMessage
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|Topic
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
name|TransportConnector
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
name|region
operator|.
name|policy
operator|.
name|PolicyEntry
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
name|region
operator|.
name|policy
operator|.
name|PolicyMap
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
name|ActiveMQDestination
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
name|ActiveMQQueue
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
name|ActiveMQTopic
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
comment|// see: https://issues.apache.org/activemq/browse/AMQ-2668
end_comment

begin_class
specifier|public
class|class
name|PerDestinationStoreLimitTest
extends|extends
name|JmsTestSupport
block|{
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|PerDestinationStoreLimitTest
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|final
name|String
name|oneKb
init|=
operator|new
name|String
argument_list|(
operator|new
name|byte
index|[
literal|1024
index|]
argument_list|)
decl_stmt|;
name|ActiveMQDestination
name|queueDest
init|=
operator|new
name|ActiveMQQueue
argument_list|(
literal|"PerDestinationStoreLimitTest.Queue"
argument_list|)
decl_stmt|;
name|ActiveMQDestination
name|topicDest
init|=
operator|new
name|ActiveMQTopic
argument_list|(
literal|"PerDestinationStoreLimitTest.Topic"
argument_list|)
decl_stmt|;
specifier|protected
name|TransportConnector
name|connector
decl_stmt|;
specifier|protected
name|ActiveMQConnection
name|connection
decl_stmt|;
specifier|public
name|void
name|testDLQAfterBlockTopic
parameter_list|()
throws|throws
name|Exception
block|{
name|doTestDLQAfterBlock
argument_list|(
name|topicDest
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testDLQAfterBlockQueue
parameter_list|()
throws|throws
name|Exception
block|{
name|doTestDLQAfterBlock
argument_list|(
name|queueDest
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|doTestDLQAfterBlock
parameter_list|(
name|ActiveMQDestination
name|destination
parameter_list|)
throws|throws
name|Exception
block|{
name|ActiveMQConnectionFactory
name|factory
init|=
operator|(
name|ActiveMQConnectionFactory
operator|)
name|createConnectionFactory
argument_list|()
decl_stmt|;
name|RedeliveryPolicy
name|redeliveryPolicy
init|=
operator|new
name|RedeliveryPolicy
argument_list|()
decl_stmt|;
comment|// Immediately sent to the DLQ on rollback, no redelivery
name|redeliveryPolicy
operator|.
name|setMaximumRedeliveries
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|factory
operator|.
name|setRedeliveryPolicy
argument_list|(
name|redeliveryPolicy
argument_list|)
expr_stmt|;
comment|// Separate connection for consumer so it will not be blocked by filler thread
comment|// sending when it blocks
name|connection
operator|=
operator|(
name|ActiveMQConnection
operator|)
name|factory
operator|.
name|createConnection
argument_list|()
expr_stmt|;
name|connections
operator|.
name|add
argument_list|(
name|connection
argument_list|)
expr_stmt|;
name|connection
operator|.
name|setClientID
argument_list|(
literal|"someId"
argument_list|)
expr_stmt|;
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
specifier|final
name|Session
name|consumerSession
init|=
name|connection
operator|.
name|createSession
argument_list|(
literal|true
argument_list|,
name|Session
operator|.
name|SESSION_TRANSACTED
argument_list|)
decl_stmt|;
name|MessageConsumer
name|consumer
init|=
name|destination
operator|.
name|isQueue
argument_list|()
condition|?
name|consumerSession
operator|.
name|createConsumer
argument_list|(
name|destination
argument_list|)
else|:
name|consumerSession
operator|.
name|createDurableSubscriber
argument_list|(
operator|(
name|Topic
operator|)
name|destination
argument_list|,
literal|"Durable"
argument_list|)
decl_stmt|;
name|connection
operator|=
operator|(
name|ActiveMQConnection
operator|)
name|factory
operator|.
name|createConnection
argument_list|()
expr_stmt|;
name|connections
operator|.
name|add
argument_list|(
name|connection
argument_list|)
expr_stmt|;
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
specifier|final
name|Session
name|session
init|=
name|connection
operator|.
name|createSession
argument_list|(
literal|true
argument_list|,
name|Session
operator|.
name|SESSION_TRANSACTED
argument_list|)
decl_stmt|;
specifier|final
name|MessageProducer
name|producer
init|=
name|session
operator|.
name|createProducer
argument_list|(
name|destination
argument_list|)
decl_stmt|;
specifier|final
name|AtomicBoolean
name|done
init|=
operator|new
name|AtomicBoolean
argument_list|(
literal|true
argument_list|)
decl_stmt|;
specifier|final
name|AtomicBoolean
name|keepGoing
init|=
operator|new
name|AtomicBoolean
argument_list|(
literal|true
argument_list|)
decl_stmt|;
specifier|final
name|CountDownLatch
name|fillerStarted
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
decl_stmt|;
specifier|final
name|AtomicLong
name|sent
init|=
operator|new
name|AtomicLong
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|Thread
name|thread
init|=
operator|new
name|Thread
argument_list|(
literal|"Filler"
argument_list|)
block|{
name|int
name|i
decl_stmt|;
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
while|while
condition|(
name|keepGoing
operator|.
name|get
argument_list|()
condition|)
block|{
name|done
operator|.
name|set
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|fillerStarted
operator|.
name|countDown
argument_list|()
expr_stmt|;
try|try
block|{
name|producer
operator|.
name|send
argument_list|(
name|session
operator|.
name|createTextMessage
argument_list|(
name|oneKb
operator|+
operator|++
name|i
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|i
operator|%
literal|10
operator|==
literal|0
condition|)
block|{
name|session
operator|.
name|commit
argument_list|()
expr_stmt|;
name|sent
operator|.
name|getAndAdd
argument_list|(
literal|10
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"committed/sent: "
operator|+
name|sent
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"sent: "
operator|+
name|i
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|JMSException
name|e
parameter_list|)
block|{                     }
block|}
block|}
block|}
decl_stmt|;
name|thread
operator|.
name|start
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
literal|"filler started.."
argument_list|,
name|fillerStarted
operator|.
name|await
argument_list|(
literal|20
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
argument_list|)
expr_stmt|;
name|waitForBlocked
argument_list|(
name|done
argument_list|)
expr_stmt|;
comment|// consume and rollback some so message gets to DLQ
name|connection
operator|=
operator|(
name|ActiveMQConnection
operator|)
name|factory
operator|.
name|createConnection
argument_list|()
expr_stmt|;
name|connections
operator|.
name|add
argument_list|(
name|connection
argument_list|)
expr_stmt|;
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
name|TextMessage
name|msg
decl_stmt|;
name|int
name|received
init|=
literal|0
decl_stmt|;
for|for
control|(
init|;
name|received
operator|<
name|sent
operator|.
name|get
argument_list|()
condition|;
operator|++
name|received
control|)
block|{
name|msg
operator|=
operator|(
name|TextMessage
operator|)
name|consumer
operator|.
name|receive
argument_list|(
literal|4000
argument_list|)
expr_stmt|;
if|if
condition|(
name|msg
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"received null on count: "
operator|+
name|received
argument_list|)
expr_stmt|;
break|break;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"received: "
operator|+
name|received
operator|+
literal|", msg: "
operator|+
name|msg
operator|.
name|getJMSMessageID
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|received
operator|%
literal|5
operator|==
literal|0
condition|)
block|{
if|if
condition|(
name|received
operator|%
literal|3
operator|==
literal|0
condition|)
block|{
comment|// force the use of the DLQ which will use some more store
name|LOG
operator|.
name|info
argument_list|(
literal|"rollback on : "
operator|+
name|received
argument_list|)
expr_stmt|;
name|consumerSession
operator|.
name|rollback
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"commit on : "
operator|+
name|received
argument_list|)
expr_stmt|;
name|consumerSession
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
block|}
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Done:: sent: "
operator|+
name|sent
operator|.
name|get
argument_list|()
operator|+
literal|", received: "
operator|+
name|received
argument_list|)
expr_stmt|;
name|keepGoing
operator|.
name|set
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"some were sent:"
argument_list|,
name|sent
operator|.
name|get
argument_list|()
operator|>
literal|0
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"received what was committed"
argument_list|,
name|sent
operator|.
name|get
argument_list|()
argument_list|,
name|received
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|waitForBlocked
parameter_list|(
specifier|final
name|AtomicBoolean
name|done
parameter_list|)
throws|throws
name|InterruptedException
block|{
while|while
condition|(
literal|true
condition|)
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
comment|// the producer is blocked once the done flag stays true
if|if
condition|(
name|done
operator|.
name|get
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Blocked...."
argument_list|)
expr_stmt|;
break|break;
block|}
name|done
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
specifier|protected
name|BrokerService
name|createBroker
parameter_list|()
throws|throws
name|Exception
block|{
name|BrokerService
name|service
init|=
operator|new
name|BrokerService
argument_list|()
decl_stmt|;
name|service
operator|.
name|setDeleteAllMessagesOnStartup
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|service
operator|.
name|setUseJmx
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|service
operator|.
name|getSystemUsage
argument_list|()
operator|.
name|getStoreUsage
argument_list|()
operator|.
name|setLimit
argument_list|(
literal|200
operator|*
literal|1024
argument_list|)
expr_stmt|;
comment|// allow destination to use 50% of store, leaving 50% for DLQ.
name|PolicyMap
name|policyMap
init|=
operator|new
name|PolicyMap
argument_list|()
decl_stmt|;
name|PolicyEntry
name|policy
init|=
operator|new
name|PolicyEntry
argument_list|()
decl_stmt|;
name|policy
operator|.
name|setStoreUsageHighWaterMark
argument_list|(
literal|50
argument_list|)
expr_stmt|;
name|policyMap
operator|.
name|put
argument_list|(
name|queueDest
argument_list|,
name|policy
argument_list|)
expr_stmt|;
name|policyMap
operator|.
name|put
argument_list|(
name|topicDest
argument_list|,
name|policy
argument_list|)
expr_stmt|;
name|service
operator|.
name|setDestinationPolicy
argument_list|(
name|policyMap
argument_list|)
expr_stmt|;
name|connector
operator|=
name|service
operator|.
name|addConnector
argument_list|(
literal|"tcp://localhost:0"
argument_list|)
expr_stmt|;
return|return
name|service
return|;
block|}
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|setAutoFail
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
block|}
specifier|protected
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|connection
operator|!=
literal|null
condition|)
block|{
name|TcpTransport
name|t
init|=
operator|(
name|TcpTransport
operator|)
name|connection
operator|.
name|getTransport
argument_list|()
operator|.
name|narrow
argument_list|(
name|TcpTransport
operator|.
name|class
argument_list|)
decl_stmt|;
name|t
operator|.
name|getTransportListener
argument_list|()
operator|.
name|onException
argument_list|(
operator|new
name|IOException
argument_list|(
literal|"Disposed."
argument_list|)
argument_list|)
expr_stmt|;
name|connection
operator|.
name|getTransport
argument_list|()
operator|.
name|stop
argument_list|()
expr_stmt|;
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
block|}
specifier|protected
name|ConnectionFactory
name|createConnectionFactory
parameter_list|()
throws|throws
name|Exception
block|{
return|return
operator|new
name|ActiveMQConnectionFactory
argument_list|(
name|connector
operator|.
name|getConnectUri
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit

