begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License. You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|junit
operator|.
name|framework
operator|.
name|TestCase
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
name|ActiveMQMessage
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
name|XATransactionId
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

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|Connection
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|Destination
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
name|Message
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
name|XAConnection
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|XASession
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|transaction
operator|.
name|xa
operator|.
name|XAResource
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|transaction
operator|.
name|xa
operator|.
name|Xid
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayOutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataOutputStream
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
name|util
operator|.
name|ArrayList
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

begin_class
specifier|public
class|class
name|XAConsumerTest
extends|extends
name|TestCase
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
name|XAConsumerTest
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|TEST_AMQ_BROKER_URI
init|=
literal|"tcp://localhost:0"
decl_stmt|;
specifier|private
name|String
name|brokerUri
decl_stmt|;
specifier|private
specifier|static
name|long
name|txGenerator
init|=
literal|21
decl_stmt|;
specifier|private
name|BrokerService
name|broker
decl_stmt|;
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|broker
operator|=
name|createBroker
argument_list|()
expr_stmt|;
name|broker
operator|.
name|start
argument_list|()
expr_stmt|;
name|broker
operator|.
name|waitUntilStarted
argument_list|()
expr_stmt|;
name|brokerUri
operator|=
name|broker
operator|.
name|getTransportConnectorByScheme
argument_list|(
literal|"tcp"
argument_list|)
operator|.
name|getPublishableConnectString
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
name|void
name|testPullRequestXAConsumer
parameter_list|()
throws|throws
name|Exception
block|{
name|ActiveMQXAConnectionFactory
name|activeMQConnectionFactory
init|=
operator|new
name|ActiveMQXAConnectionFactory
argument_list|(
literal|"admin"
argument_list|,
literal|"admin"
argument_list|,
name|brokerUri
operator|+
literal|"?trace=true&jms.prefetchPolicy.all=0"
argument_list|)
decl_stmt|;
name|XAConnection
name|connection
init|=
name|activeMQConnectionFactory
operator|.
name|createXAConnection
argument_list|()
decl_stmt|;
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
name|ActiveMQXAConnectionFactory
name|activeMQConnectionFactoryAutoAck
init|=
operator|new
name|ActiveMQXAConnectionFactory
argument_list|(
literal|"admin"
argument_list|,
literal|"admin"
argument_list|,
name|brokerUri
operator|+
literal|"?trace=true&jms.prefetchPolicy.all=0"
argument_list|)
decl_stmt|;
comment|// allow non xa use of connections
name|activeMQConnectionFactoryAutoAck
operator|.
name|setXaAckMode
argument_list|(
name|Session
operator|.
name|AUTO_ACKNOWLEDGE
argument_list|)
expr_stmt|;
name|Connection
name|autoAckConnection
init|=
name|activeMQConnectionFactoryAutoAck
operator|.
name|createConnection
argument_list|()
decl_stmt|;
name|autoAckConnection
operator|.
name|start
argument_list|()
expr_stmt|;
try|try
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|">>>INVOKE XA receive with PullRequest Consumer..."
argument_list|)
expr_stmt|;
name|XASession
name|xaSession
init|=
name|connection
operator|.
name|createXASession
argument_list|()
decl_stmt|;
name|XAResource
name|xaResource
init|=
name|xaSession
operator|.
name|getXAResource
argument_list|()
decl_stmt|;
name|Xid
name|xid
init|=
name|createXid
argument_list|()
decl_stmt|;
name|xaResource
operator|.
name|start
argument_list|(
name|xid
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|Destination
name|destination
init|=
name|xaSession
operator|.
name|createQueue
argument_list|(
literal|"TEST.T2"
argument_list|)
decl_stmt|;
specifier|final
name|MessageConsumer
name|messageConsumer
init|=
name|xaSession
operator|.
name|createConsumer
argument_list|(
name|destination
argument_list|)
decl_stmt|;
specifier|final
name|CountDownLatch
name|receiveThreadDone
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
decl_stmt|;
specifier|final
name|CountDownLatch
name|receiveLatch
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
decl_stmt|;
comment|// do a message receive
name|Thread
name|receiveThread
init|=
operator|new
name|Thread
argument_list|(
operator|new
name|Runnable
argument_list|()
block|{
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|messageConsumer
operator|.
name|receive
argument_list|(
literal|600000
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|JMSException
name|expected
parameter_list|)
block|{
name|receiveLatch
operator|.
name|countDown
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"got expected ex: "
argument_list|,
name|expected
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|receiveThreadDone
operator|.
name|countDown
argument_list|()
expr_stmt|;
block|}
block|}
block|}
argument_list|)
decl_stmt|;
name|receiveThread
operator|.
name|start
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|">>>simulate Transaction Rollback"
argument_list|)
expr_stmt|;
name|xaResource
operator|.
name|end
argument_list|(
name|xid
argument_list|,
name|XAResource
operator|.
name|TMFAIL
argument_list|)
expr_stmt|;
name|xaResource
operator|.
name|rollback
argument_list|(
name|xid
argument_list|)
expr_stmt|;
comment|// send a message after transaction is rolled back.
name|LOG
operator|.
name|info
argument_list|(
literal|">>>Sending message..."
argument_list|)
expr_stmt|;
name|Session
name|session
init|=
name|autoAckConnection
operator|.
name|createSession
argument_list|(
literal|false
argument_list|,
name|Session
operator|.
name|AUTO_ACKNOWLEDGE
argument_list|)
decl_stmt|;
name|Message
name|messageToSend
init|=
name|session
operator|.
name|createMessage
argument_list|()
decl_stmt|;
name|MessageProducer
name|messageProducer
init|=
name|session
operator|.
name|createProducer
argument_list|(
name|destination
argument_list|)
decl_stmt|;
name|messageProducer
operator|.
name|send
argument_list|(
name|messageToSend
argument_list|)
expr_stmt|;
name|receiveThreadDone
operator|.
name|await
argument_list|(
literal|30
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
expr_stmt|;
name|receiveLatch
operator|.
name|await
argument_list|(
literal|5
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
expr_stmt|;
comment|// consume with non transacted consumer to verify not autoacked
name|messageConsumer
operator|.
name|close
argument_list|()
expr_stmt|;
name|xaSession
operator|.
name|close
argument_list|()
expr_stmt|;
name|MessageConsumer
name|messageConsumer1
init|=
name|session
operator|.
name|createConsumer
argument_list|(
name|destination
argument_list|)
decl_stmt|;
name|javax
operator|.
name|jms
operator|.
name|Message
name|message
init|=
name|messageConsumer1
operator|.
name|receive
argument_list|(
literal|5000
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"Got message"
argument_list|,
name|message
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Got message on new session"
argument_list|,
name|message
argument_list|)
expr_stmt|;
name|message
operator|.
name|acknowledge
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|">>>Closing Connection"
argument_list|)
expr_stmt|;
if|if
condition|(
name|connection
operator|!=
literal|null
condition|)
block|{
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|autoAckConnection
operator|!=
literal|null
condition|)
block|{
name|autoAckConnection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
specifier|public
name|void
name|testPullRequestXAConsumerSingleConsumer
parameter_list|()
throws|throws
name|Exception
block|{
name|ActiveMQXAConnectionFactory
name|activeMQConnectionFactory
init|=
operator|new
name|ActiveMQXAConnectionFactory
argument_list|(
literal|"admin"
argument_list|,
literal|"admin"
argument_list|,
name|brokerUri
operator|+
literal|"?trace=true&jms.prefetchPolicy.all=0"
argument_list|)
decl_stmt|;
name|XAConnection
name|connection
init|=
name|activeMQConnectionFactory
operator|.
name|createXAConnection
argument_list|()
decl_stmt|;
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
try|try
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|">>>INVOKE XA receive with PullRequest Consumer..."
argument_list|)
expr_stmt|;
name|XASession
name|xaSession
init|=
name|connection
operator|.
name|createXASession
argument_list|()
decl_stmt|;
name|XAResource
name|xaResource
init|=
name|xaSession
operator|.
name|getXAResource
argument_list|()
decl_stmt|;
name|Xid
name|xid
init|=
name|createXid
argument_list|()
decl_stmt|;
name|xaResource
operator|.
name|start
argument_list|(
name|xid
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|Destination
name|destination
init|=
name|xaSession
operator|.
name|createQueue
argument_list|(
literal|"TEST.T2"
argument_list|)
decl_stmt|;
specifier|final
name|MessageConsumer
name|messageConsumer
init|=
name|xaSession
operator|.
name|createConsumer
argument_list|(
name|destination
argument_list|)
decl_stmt|;
specifier|final
name|CountDownLatch
name|receiveThreadDone
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
decl_stmt|;
specifier|final
name|CountDownLatch
name|receiveLatch
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
decl_stmt|;
comment|// do a message receive
name|Thread
name|receiveThread
init|=
operator|new
name|Thread
argument_list|(
operator|new
name|Runnable
argument_list|()
block|{
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|messageConsumer
operator|.
name|receive
argument_list|(
literal|600000
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|JMSException
name|expected
parameter_list|)
block|{
name|receiveLatch
operator|.
name|countDown
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"got expected ex: "
argument_list|,
name|expected
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|receiveThreadDone
operator|.
name|countDown
argument_list|()
expr_stmt|;
block|}
block|}
block|}
argument_list|)
decl_stmt|;
name|receiveThread
operator|.
name|start
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|">>>simulate Transaction Rollback"
argument_list|)
expr_stmt|;
name|xaResource
operator|.
name|end
argument_list|(
name|xid
argument_list|,
name|XAResource
operator|.
name|TMFAIL
argument_list|)
expr_stmt|;
name|xaResource
operator|.
name|rollback
argument_list|(
name|xid
argument_list|)
expr_stmt|;
block|{
name|XASession
name|xaSessionSend
init|=
name|connection
operator|.
name|createXASession
argument_list|()
decl_stmt|;
name|XAResource
name|xaResourceSend
init|=
name|xaSessionSend
operator|.
name|getXAResource
argument_list|()
decl_stmt|;
name|Xid
name|xidSend
init|=
name|createXid
argument_list|()
decl_stmt|;
name|xaResourceSend
operator|.
name|start
argument_list|(
name|xidSend
argument_list|,
literal|0
argument_list|)
expr_stmt|;
comment|// send a message after transaction is rolled back.
name|LOG
operator|.
name|info
argument_list|(
literal|">>>Sending message..."
argument_list|)
expr_stmt|;
name|ActiveMQMessage
name|messageToSend
init|=
operator|(
name|ActiveMQMessage
operator|)
name|xaSessionSend
operator|.
name|createMessage
argument_list|()
decl_stmt|;
name|messageToSend
operator|.
name|setTransactionId
argument_list|(
operator|new
name|XATransactionId
argument_list|(
name|xidSend
argument_list|)
argument_list|)
expr_stmt|;
name|MessageProducer
name|messageProducer
init|=
name|xaSessionSend
operator|.
name|createProducer
argument_list|(
name|destination
argument_list|)
decl_stmt|;
name|messageProducer
operator|.
name|send
argument_list|(
name|messageToSend
argument_list|)
expr_stmt|;
name|xaResourceSend
operator|.
name|end
argument_list|(
name|xidSend
argument_list|,
name|XAResource
operator|.
name|TMSUCCESS
argument_list|)
expr_stmt|;
name|xaResourceSend
operator|.
name|commit
argument_list|(
name|xidSend
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
name|receiveThreadDone
operator|.
name|await
argument_list|(
literal|30
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
expr_stmt|;
name|receiveLatch
operator|.
name|await
argument_list|(
literal|5
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
expr_stmt|;
comment|// after jms exception we need to close
name|messageConsumer
operator|.
name|close
argument_list|()
expr_stmt|;
name|MessageConsumer
name|messageConsumerTwo
init|=
name|xaSession
operator|.
name|createConsumer
argument_list|(
name|destination
argument_list|)
decl_stmt|;
name|Xid
name|xidReceiveOk
init|=
name|createXid
argument_list|()
decl_stmt|;
name|xaResource
operator|.
name|start
argument_list|(
name|xidReceiveOk
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|javax
operator|.
name|jms
operator|.
name|Message
name|message
init|=
name|messageConsumerTwo
operator|.
name|receive
argument_list|(
literal|10000
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"Got message"
argument_list|,
name|message
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Got message on new session"
argument_list|,
name|message
argument_list|)
expr_stmt|;
name|xaResource
operator|.
name|end
argument_list|(
name|xidReceiveOk
argument_list|,
name|XAResource
operator|.
name|TMSUCCESS
argument_list|)
expr_stmt|;
name|xaResource
operator|.
name|commit
argument_list|(
name|xidReceiveOk
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|">>>Closing Connection"
argument_list|)
expr_stmt|;
if|if
condition|(
name|connection
operator|!=
literal|null
condition|)
block|{
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
specifier|public
name|Xid
name|createXid
parameter_list|()
throws|throws
name|IOException
block|{
name|ByteArrayOutputStream
name|baos
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|DataOutputStream
name|os
init|=
operator|new
name|DataOutputStream
argument_list|(
name|baos
argument_list|)
decl_stmt|;
name|os
operator|.
name|writeLong
argument_list|(
operator|++
name|txGenerator
argument_list|)
expr_stmt|;
name|os
operator|.
name|close
argument_list|()
expr_stmt|;
specifier|final
name|byte
index|[]
name|bs
init|=
name|baos
operator|.
name|toByteArray
argument_list|()
decl_stmt|;
return|return
operator|new
name|Xid
argument_list|()
block|{
specifier|public
name|int
name|getFormatId
parameter_list|()
block|{
return|return
literal|86
return|;
block|}
specifier|public
name|byte
index|[]
name|getGlobalTransactionId
parameter_list|()
block|{
return|return
name|bs
return|;
block|}
specifier|public
name|byte
index|[]
name|getBranchQualifier
parameter_list|()
block|{
return|return
name|bs
return|;
block|}
block|}
return|;
block|}
specifier|private
name|BrokerService
name|createBroker
parameter_list|()
throws|throws
name|Exception
block|{
name|BrokerService
name|broker
init|=
operator|new
name|BrokerService
argument_list|()
decl_stmt|;
name|PolicyMap
name|policyMap
init|=
operator|new
name|PolicyMap
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|PolicyEntry
argument_list|>
name|entries
init|=
operator|new
name|ArrayList
argument_list|<
name|PolicyEntry
argument_list|>
argument_list|()
decl_stmt|;
name|PolicyEntry
name|pe
init|=
operator|new
name|PolicyEntry
argument_list|()
decl_stmt|;
name|pe
operator|.
name|setProducerFlowControl
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|pe
operator|.
name|setUseCache
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|pe
operator|.
name|setPrioritizedMessages
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|pe
operator|.
name|setExpireMessagesPeriod
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|pe
operator|.
name|setQueuePrefetch
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|pe
operator|.
name|setQueue
argument_list|(
literal|">"
argument_list|)
expr_stmt|;
name|entries
operator|.
name|add
argument_list|(
name|pe
argument_list|)
expr_stmt|;
name|policyMap
operator|.
name|setPolicyEntries
argument_list|(
name|entries
argument_list|)
expr_stmt|;
name|broker
operator|.
name|setDestinationPolicy
argument_list|(
name|policyMap
argument_list|)
expr_stmt|;
name|broker
operator|.
name|addConnector
argument_list|(
name|TEST_AMQ_BROKER_URI
argument_list|)
expr_stmt|;
name|broker
operator|.
name|deleteAllMessages
argument_list|()
expr_stmt|;
return|return
name|broker
return|;
block|}
block|}
end_class

end_unit
