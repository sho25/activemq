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
name|network
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
name|atomic
operator|.
name|AtomicInteger
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
name|DeliveryMode
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
name|MessageListener
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
name|Queue
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
name|ActiveMQConnection
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
name|ActiveMQConnectionFactory
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
name|DestinationDoesNotExistException
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
name|AbstractDeadLetterStrategy
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
name|SharedDeadLetterStrategy
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
name|transport
operator|.
name|TransportFilter
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
name|activemq
operator|.
name|xbean
operator|.
name|BrokerFactoryBean
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
name|org
operator|.
name|springframework
operator|.
name|context
operator|.
name|support
operator|.
name|AbstractApplicationContext
import|;
end_import

begin_import
import|import
name|org
operator|.
name|springframework
operator|.
name|core
operator|.
name|io
operator|.
name|ClassPathResource
import|;
end_import

begin_import
import|import
name|org
operator|.
name|springframework
operator|.
name|core
operator|.
name|io
operator|.
name|Resource
import|;
end_import

begin_class
specifier|public
class|class
name|NetworkFailoverTest
extends|extends
name|TestCase
block|{
specifier|protected
specifier|static
specifier|final
name|int
name|MESSAGE_COUNT
init|=
literal|10
decl_stmt|;
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
name|NetworkFailoverTest
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|protected
name|AbstractApplicationContext
name|context
decl_stmt|;
specifier|protected
name|Connection
name|localConnection
decl_stmt|;
specifier|protected
name|Connection
name|remoteConnection
decl_stmt|;
specifier|protected
name|BrokerService
name|localBroker
decl_stmt|;
specifier|protected
name|BrokerService
name|remoteBroker
decl_stmt|;
specifier|protected
name|Session
name|localSession
decl_stmt|;
specifier|protected
name|Session
name|remoteSession
decl_stmt|;
specifier|protected
name|ActiveMQQueue
name|included
init|=
operator|new
name|ActiveMQQueue
argument_list|(
literal|"include.test.foo"
argument_list|)
decl_stmt|;
specifier|private
name|AtomicInteger
name|replyToNonExistDest
init|=
operator|new
name|AtomicInteger
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|private
name|AtomicInteger
name|roundTripComplete
init|=
operator|new
name|AtomicInteger
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|private
name|AtomicInteger
name|remoteDLQCount
init|=
operator|new
name|AtomicInteger
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|public
name|void
name|testRequestReply
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|MessageProducer
name|remoteProducer
init|=
name|remoteSession
operator|.
name|createProducer
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|MessageConsumer
name|remoteConsumer
init|=
name|remoteSession
operator|.
name|createConsumer
argument_list|(
name|included
argument_list|)
decl_stmt|;
name|remoteConsumer
operator|.
name|setMessageListener
argument_list|(
operator|new
name|MessageListener
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onMessage
parameter_list|(
name|Message
name|msg
parameter_list|)
block|{
specifier|final
name|TextMessage
name|textMsg
init|=
operator|(
name|TextMessage
operator|)
name|msg
decl_stmt|;
try|try
block|{
name|String
name|payload
init|=
literal|"REPLY: "
operator|+
name|textMsg
operator|.
name|getText
argument_list|()
operator|+
literal|", "
operator|+
name|textMsg
operator|.
name|getJMSMessageID
argument_list|()
decl_stmt|;
name|Destination
name|replyTo
decl_stmt|;
name|replyTo
operator|=
name|msg
operator|.
name|getJMSReplyTo
argument_list|()
expr_stmt|;
name|textMsg
operator|.
name|clearBody
argument_list|()
expr_stmt|;
name|textMsg
operator|.
name|setText
argument_list|(
name|payload
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"*** Sending response: {}"
argument_list|,
name|textMsg
operator|.
name|getText
argument_list|()
argument_list|)
expr_stmt|;
name|remoteProducer
operator|.
name|send
argument_list|(
name|replyTo
argument_list|,
name|textMsg
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"replied with: "
operator|+
name|textMsg
operator|.
name|getJMSMessageID
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|DestinationDoesNotExistException
name|expected
parameter_list|)
block|{
comment|// been removed but not yet recreated
name|replyToNonExistDest
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
try|try
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"NED: "
operator|+
name|textMsg
operator|.
name|getJMSMessageID
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|JMSException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
empty_stmt|;
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
literal|"*** Responder listener caught exception: "
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
name|Queue
name|tempQueue
init|=
name|localSession
operator|.
name|createTemporaryQueue
argument_list|()
decl_stmt|;
name|MessageProducer
name|requestProducer
init|=
name|localSession
operator|.
name|createProducer
argument_list|(
name|included
argument_list|)
decl_stmt|;
name|requestProducer
operator|.
name|setDeliveryMode
argument_list|(
name|DeliveryMode
operator|.
name|NON_PERSISTENT
argument_list|)
expr_stmt|;
name|MessageConsumer
name|requestConsumer
init|=
name|localSession
operator|.
name|createConsumer
argument_list|(
name|tempQueue
argument_list|)
decl_stmt|;
comment|// track remote dlq for forward failures
name|MessageConsumer
name|dlqconsumer
init|=
name|remoteSession
operator|.
name|createConsumer
argument_list|(
operator|new
name|ActiveMQQueue
argument_list|(
name|SharedDeadLetterStrategy
operator|.
name|DEFAULT_DEAD_LETTER_QUEUE_NAME
argument_list|)
argument_list|)
decl_stmt|;
name|dlqconsumer
operator|.
name|setMessageListener
argument_list|(
operator|new
name|MessageListener
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onMessage
parameter_list|(
name|Message
name|message
parameter_list|)
block|{
try|try
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"dlq "
operator|+
name|message
operator|.
name|getJMSMessageID
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|JMSException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
name|remoteDLQCount
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
comment|// allow for consumer infos to perculate arround
name|Thread
operator|.
name|sleep
argument_list|(
literal|2000
argument_list|)
expr_stmt|;
name|long
name|done
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|+
operator|(
name|MESSAGE_COUNT
operator|*
literal|6000
operator|)
decl_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|MESSAGE_COUNT
operator|>
name|roundTripComplete
operator|.
name|get
argument_list|()
operator|+
name|remoteDLQCount
operator|.
name|get
argument_list|()
operator|+
name|replyToNonExistDest
operator|.
name|get
argument_list|()
operator|&&
name|done
operator|>
name|System
operator|.
name|currentTimeMillis
argument_list|()
condition|)
block|{
if|if
condition|(
name|i
operator|<
name|MESSAGE_COUNT
condition|)
block|{
name|String
name|payload
init|=
literal|"test msg "
operator|+
name|i
decl_stmt|;
name|i
operator|++
expr_stmt|;
name|TextMessage
name|msg
init|=
name|localSession
operator|.
name|createTextMessage
argument_list|(
name|payload
argument_list|)
decl_stmt|;
name|msg
operator|.
name|setJMSReplyTo
argument_list|(
name|tempQueue
argument_list|)
expr_stmt|;
name|requestProducer
operator|.
name|send
argument_list|(
name|msg
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Sent: "
operator|+
name|msg
operator|.
name|getJMSMessageID
argument_list|()
operator|+
literal|", Failing over"
argument_list|)
expr_stmt|;
operator|(
call|(
name|FailoverTransport
call|)
argument_list|(
call|(
name|TransportFilter
call|)
argument_list|(
call|(
name|TransportFilter
call|)
argument_list|(
operator|(
name|ActiveMQConnection
operator|)
name|localConnection
argument_list|)
operator|.
name|getTransport
argument_list|()
argument_list|)
operator|.
name|getNext
argument_list|()
argument_list|)
operator|.
name|getNext
argument_list|()
operator|)
operator|.
name|handleTransportFailure
argument_list|(
operator|new
name|IOException
argument_list|(
literal|"Forcing failover from test"
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|TextMessage
name|result
init|=
operator|(
name|TextMessage
operator|)
name|requestConsumer
operator|.
name|receive
argument_list|(
literal|5000
argument_list|)
decl_stmt|;
if|if
condition|(
name|result
operator|!=
literal|null
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Got reply: "
operator|+
name|result
operator|.
name|getJMSMessageID
argument_list|()
operator|+
literal|", "
operator|+
name|result
operator|.
name|getText
argument_list|()
argument_list|)
expr_stmt|;
name|roundTripComplete
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
block|}
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"complete: "
operator|+
name|roundTripComplete
operator|.
name|get
argument_list|()
operator|+
literal|", remoteDLQCount: "
operator|+
name|remoteDLQCount
operator|.
name|get
argument_list|()
operator|+
literal|", replyToNonExistDest: "
operator|+
name|replyToNonExistDest
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"complete:"
operator|+
name|roundTripComplete
operator|.
name|get
argument_list|()
operator|+
literal|", remoteDLQCount: "
operator|+
name|remoteDLQCount
operator|.
name|get
argument_list|()
operator|+
literal|", replyToNonExistDest: "
operator|+
name|replyToNonExistDest
operator|.
name|get
argument_list|()
argument_list|,
name|MESSAGE_COUNT
argument_list|,
name|roundTripComplete
operator|.
name|get
argument_list|()
operator|+
name|remoteDLQCount
operator|.
name|get
argument_list|()
operator|+
name|replyToNonExistDest
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|doSetUp
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|doTearDown
argument_list|()
expr_stmt|;
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
specifier|protected
name|void
name|doTearDown
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|localConnection
operator|.
name|close
argument_list|()
expr_stmt|;
name|remoteConnection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{}
try|try
block|{
name|localBroker
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{}
try|try
block|{
name|remoteBroker
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{}
block|}
specifier|protected
name|void
name|doSetUp
parameter_list|(
name|boolean
name|deleteAllMessages
parameter_list|)
throws|throws
name|Exception
block|{
name|remoteBroker
operator|=
name|createRemoteBroker
argument_list|()
expr_stmt|;
name|remoteBroker
operator|.
name|setDeleteAllMessagesOnStartup
argument_list|(
name|deleteAllMessages
argument_list|)
expr_stmt|;
name|remoteBroker
operator|.
name|setCacheTempDestinations
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|remoteBroker
operator|.
name|start
argument_list|()
expr_stmt|;
name|localBroker
operator|=
name|createLocalBroker
argument_list|()
expr_stmt|;
name|localBroker
operator|.
name|setDeleteAllMessagesOnStartup
argument_list|(
name|deleteAllMessages
argument_list|)
expr_stmt|;
name|localBroker
operator|.
name|setCacheTempDestinations
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|localBroker
operator|.
name|start
argument_list|()
expr_stmt|;
name|String
name|localURI
init|=
literal|"tcp://localhost:61616"
decl_stmt|;
name|String
name|remoteURI
init|=
literal|"tcp://localhost:61617"
decl_stmt|;
name|ActiveMQConnectionFactory
name|fac
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
literal|"failover:("
operator|+
name|localURI
operator|+
literal|","
operator|+
name|remoteURI
operator|+
literal|")?randomize=false&backup=false&trackMessages=true"
argument_list|)
decl_stmt|;
name|localConnection
operator|=
name|fac
operator|.
name|createConnection
argument_list|()
expr_stmt|;
name|localConnection
operator|.
name|setClientID
argument_list|(
literal|"local"
argument_list|)
expr_stmt|;
name|localConnection
operator|.
name|start
argument_list|()
expr_stmt|;
name|fac
operator|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
literal|"failover:("
operator|+
name|remoteURI
operator|+
literal|","
operator|+
name|localURI
operator|+
literal|")?randomize=false&backup=false&trackMessages=true"
argument_list|)
expr_stmt|;
name|fac
operator|.
name|setWatchTopicAdvisories
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|remoteConnection
operator|=
name|fac
operator|.
name|createConnection
argument_list|()
expr_stmt|;
name|remoteConnection
operator|.
name|setClientID
argument_list|(
literal|"remote"
argument_list|)
expr_stmt|;
name|remoteConnection
operator|.
name|start
argument_list|()
expr_stmt|;
name|localSession
operator|=
name|localConnection
operator|.
name|createSession
argument_list|(
literal|false
argument_list|,
name|Session
operator|.
name|AUTO_ACKNOWLEDGE
argument_list|)
expr_stmt|;
name|remoteSession
operator|=
name|remoteConnection
operator|.
name|createSession
argument_list|(
literal|false
argument_list|,
name|Session
operator|.
name|AUTO_ACKNOWLEDGE
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|String
name|getRemoteBrokerURI
parameter_list|()
block|{
return|return
literal|"org/apache/activemq/network/remoteBroker.xml"
return|;
block|}
specifier|protected
name|String
name|getLocalBrokerURI
parameter_list|()
block|{
return|return
literal|"org/apache/activemq/network/localBroker.xml"
return|;
block|}
specifier|protected
name|BrokerService
name|createBroker
parameter_list|(
name|String
name|uri
parameter_list|)
throws|throws
name|Exception
block|{
name|Resource
name|resource
init|=
operator|new
name|ClassPathResource
argument_list|(
name|uri
argument_list|)
decl_stmt|;
name|BrokerFactoryBean
name|factory
init|=
operator|new
name|BrokerFactoryBean
argument_list|(
name|resource
argument_list|)
decl_stmt|;
name|resource
operator|=
operator|new
name|ClassPathResource
argument_list|(
name|uri
argument_list|)
expr_stmt|;
name|factory
operator|=
operator|new
name|BrokerFactoryBean
argument_list|(
name|resource
argument_list|)
expr_stmt|;
name|factory
operator|.
name|afterPropertiesSet
argument_list|()
expr_stmt|;
name|BrokerService
name|result
init|=
name|factory
operator|.
name|getBroker
argument_list|()
decl_stmt|;
return|return
name|result
return|;
block|}
specifier|protected
name|BrokerService
name|createLocalBroker
parameter_list|()
throws|throws
name|Exception
block|{
return|return
name|createBroker
argument_list|(
name|getLocalBrokerURI
argument_list|()
argument_list|)
return|;
block|}
specifier|protected
name|BrokerService
name|createRemoteBroker
parameter_list|()
throws|throws
name|Exception
block|{
return|return
name|createBroker
argument_list|(
name|getRemoteBrokerURI
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit
