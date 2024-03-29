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
name|bugs
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertNull
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Enumeration
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
name|ExceptionListener
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
name|Queue
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|QueueBrowser
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
name|broker
operator|.
name|Broker
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
name|BrokerFilter
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
name|BrokerPlugin
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
name|command
operator|.
name|MessageDispatch
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|After
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_class
specifier|public
class|class
name|MessageExpiryClientSideTest
block|{
specifier|private
name|ActiveMQConnection
name|connection
decl_stmt|;
specifier|private
name|BrokerService
name|broker
decl_stmt|;
specifier|private
specifier|volatile
name|Exception
name|connectionError
decl_stmt|;
annotation|@
name|Before
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|createBroker
argument_list|()
expr_stmt|;
name|connection
operator|=
name|createConnection
argument_list|()
expr_stmt|;
name|connection
operator|.
name|setExceptionListener
argument_list|(
operator|new
name|ExceptionListener
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onException
parameter_list|(
name|JMSException
name|exception
parameter_list|)
block|{
try|try
block|{
name|connectionError
operator|=
name|exception
expr_stmt|;
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|JMSException
name|e
parameter_list|)
block|{                 }
block|}
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
specifier|public
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
try|try
block|{
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{             }
block|}
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
name|broker
operator|.
name|waitUntilStopped
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**      * check if the pull request (prefetch=1) times out when the expiry occurs      * on the client side.      */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|30000
argument_list|)
specifier|public
name|void
name|testConsumerReceivePrefetchOneRedeliveryZero
parameter_list|()
throws|throws
name|Exception
block|{
name|connection
operator|.
name|getPrefetchPolicy
argument_list|()
operator|.
name|setQueuePrefetch
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
comment|// push message to queue
name|Session
name|session
init|=
name|connection
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
name|Queue
name|queue
init|=
name|session
operator|.
name|createQueue
argument_list|(
literal|"timeout.test"
argument_list|)
decl_stmt|;
name|MessageProducer
name|producer
init|=
name|session
operator|.
name|createProducer
argument_list|(
name|queue
argument_list|)
decl_stmt|;
name|TextMessage
name|textMessage
init|=
name|session
operator|.
name|createTextMessage
argument_list|(
literal|"test Message"
argument_list|)
decl_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|textMessage
argument_list|)
expr_stmt|;
name|session
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// try to consume message
name|session
operator|=
name|connection
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
name|MessageConsumer
name|consumer
init|=
name|session
operator|.
name|createConsumer
argument_list|(
name|queue
argument_list|)
decl_stmt|;
name|Message
name|message
init|=
name|consumer
operator|.
name|receive
argument_list|(
literal|1000
argument_list|)
decl_stmt|;
comment|// message should be null as it should have expired and the
comment|// consumer.receive(timeout) should return null.
name|assertNull
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|session
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertNull
argument_list|(
name|connectionError
argument_list|)
expr_stmt|;
block|}
comment|/**      * check if the pull request (prefetch=0) times out when the expiry occurs      * on the client side.      */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|30000
argument_list|)
specifier|public
name|void
name|testConsumerReceivePrefetchZeroRedeliveryZero
parameter_list|()
throws|throws
name|Exception
block|{
name|connection
operator|.
name|getPrefetchPolicy
argument_list|()
operator|.
name|setQueuePrefetch
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
comment|// push message to queue
name|Session
name|session
init|=
name|connection
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
name|Queue
name|queue
init|=
name|session
operator|.
name|createQueue
argument_list|(
literal|"timeout.test"
argument_list|)
decl_stmt|;
name|MessageProducer
name|producer
init|=
name|session
operator|.
name|createProducer
argument_list|(
name|queue
argument_list|)
decl_stmt|;
name|TextMessage
name|textMessage
init|=
name|session
operator|.
name|createTextMessage
argument_list|(
literal|"test Message"
argument_list|)
decl_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|textMessage
argument_list|)
expr_stmt|;
name|session
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// try to consume message
name|session
operator|=
name|connection
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
name|MessageConsumer
name|consumer
init|=
name|session
operator|.
name|createConsumer
argument_list|(
name|queue
argument_list|)
decl_stmt|;
name|Message
name|message
init|=
name|consumer
operator|.
name|receive
argument_list|(
literal|1000
argument_list|)
decl_stmt|;
comment|// message should be null as it should have expired and the
comment|// consumer.receive(timeout) should return null.
name|assertNull
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|session
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertNull
argument_list|(
name|connectionError
argument_list|)
expr_stmt|;
block|}
comment|/**      * check if the pull request (prefetch=0) times out when the expiry occurs      * on the client side.      */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|30000
argument_list|)
specifier|public
name|void
name|testQueueBrowserPrefetchZeroRedeliveryZero
parameter_list|()
throws|throws
name|Exception
block|{
name|connection
operator|.
name|getPrefetchPolicy
argument_list|()
operator|.
name|setQueuePrefetch
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
comment|// push message to queue
name|Session
name|session
init|=
name|connection
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
name|Queue
name|queue
init|=
name|session
operator|.
name|createQueue
argument_list|(
literal|"timeout.test"
argument_list|)
decl_stmt|;
name|MessageProducer
name|producer
init|=
name|session
operator|.
name|createProducer
argument_list|(
name|queue
argument_list|)
decl_stmt|;
name|TextMessage
name|textMessage
init|=
name|session
operator|.
name|createTextMessage
argument_list|(
literal|"test Message"
argument_list|)
decl_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|textMessage
argument_list|)
expr_stmt|;
name|session
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// try to consume message
name|session
operator|=
name|connection
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
name|QueueBrowser
name|browser
init|=
name|session
operator|.
name|createBrowser
argument_list|(
name|queue
argument_list|)
decl_stmt|;
name|Message
name|message
init|=
literal|null
decl_stmt|;
name|Enumeration
argument_list|<
name|?
argument_list|>
name|enumeration
init|=
name|browser
operator|.
name|getEnumeration
argument_list|()
decl_stmt|;
while|while
condition|(
name|enumeration
operator|.
name|hasMoreElements
argument_list|()
condition|)
block|{
name|message
operator|=
operator|(
name|Message
operator|)
name|enumeration
operator|.
name|nextElement
argument_list|()
expr_stmt|;
block|}
comment|// message should be null as it should have expired and the
comment|// consumer.receive(timeout) should return null.
name|assertNull
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|session
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertNull
argument_list|(
name|connectionError
argument_list|)
expr_stmt|;
block|}
comment|/**      * check if the browse with (prefetch=1) times out when the expiry occurs      * on the client side.      */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|30000
argument_list|)
specifier|public
name|void
name|testQueueBrowserPrefetchOneRedeliveryZero
parameter_list|()
throws|throws
name|Exception
block|{
name|connection
operator|.
name|getPrefetchPolicy
argument_list|()
operator|.
name|setQueuePrefetch
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
comment|// push message to queue
name|Session
name|session
init|=
name|connection
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
name|Queue
name|queue
init|=
name|session
operator|.
name|createQueue
argument_list|(
literal|"timeout.test"
argument_list|)
decl_stmt|;
name|MessageProducer
name|producer
init|=
name|session
operator|.
name|createProducer
argument_list|(
name|queue
argument_list|)
decl_stmt|;
name|TextMessage
name|textMessage
init|=
name|session
operator|.
name|createTextMessage
argument_list|(
literal|"test Message"
argument_list|)
decl_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|textMessage
argument_list|)
expr_stmt|;
name|session
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// try to consume message
name|session
operator|=
name|connection
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
name|QueueBrowser
name|browser
init|=
name|session
operator|.
name|createBrowser
argument_list|(
name|queue
argument_list|)
decl_stmt|;
name|Message
name|message
init|=
literal|null
decl_stmt|;
name|Enumeration
argument_list|<
name|?
argument_list|>
name|enumeration
init|=
name|browser
operator|.
name|getEnumeration
argument_list|()
decl_stmt|;
while|while
condition|(
name|enumeration
operator|.
name|hasMoreElements
argument_list|()
condition|)
block|{
name|message
operator|=
operator|(
name|Message
operator|)
name|enumeration
operator|.
name|nextElement
argument_list|()
expr_stmt|;
block|}
comment|// message should be null as it should have expired and the
comment|// consumer.receive(timeout) should return null.
name|assertNull
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|session
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertNull
argument_list|(
name|connectionError
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|createBroker
parameter_list|()
throws|throws
name|Exception
block|{
name|broker
operator|=
operator|new
name|BrokerService
argument_list|()
expr_stmt|;
name|broker
operator|.
name|setUseJmx
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|broker
operator|.
name|setPersistent
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|broker
operator|.
name|setAdvisorySupport
argument_list|(
literal|false
argument_list|)
expr_stmt|;
comment|// add a plugin to ensure the expiration happens on the client side rather
comment|// than broker side.
name|broker
operator|.
name|setPlugins
argument_list|(
operator|new
name|BrokerPlugin
index|[]
block|{
operator|new
name|BrokerPlugin
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Broker
name|installPlugin
parameter_list|(
name|Broker
name|broker
parameter_list|)
throws|throws
name|Exception
block|{
return|return
operator|new
name|BrokerFilter
argument_list|(
name|broker
argument_list|)
block|{
specifier|private
name|AtomicInteger
name|counter
init|=
operator|new
name|AtomicInteger
argument_list|()
decl_stmt|;
annotation|@
name|Override
specifier|public
name|void
name|preProcessDispatch
parameter_list|(
name|MessageDispatch
name|messageDispatch
parameter_list|)
block|{
if|if
condition|(
name|counter
operator|.
name|get
argument_list|()
operator|==
literal|0
operator|&&
name|messageDispatch
operator|.
name|getDestination
argument_list|()
operator|.
name|getPhysicalName
argument_list|()
operator|.
name|contains
argument_list|(
literal|"timeout.test"
argument_list|)
condition|)
block|{
comment|// Set the expiration to now
name|messageDispatch
operator|.
name|getMessage
argument_list|()
operator|.
name|setExpiration
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
literal|1000
argument_list|)
expr_stmt|;
name|counter
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
block|}
name|super
operator|.
name|preProcessDispatch
argument_list|(
name|messageDispatch
argument_list|)
argument_list|;
block|}
block|}
end_class

begin_empty_stmt
empty_stmt|;
end_empty_stmt

begin_empty_stmt
unit|}         } })
empty_stmt|;
end_empty_stmt

begin_expr_stmt
name|broker
operator|.
name|start
argument_list|()
expr_stmt|;
end_expr_stmt

begin_expr_stmt
name|broker
operator|.
name|waitUntilStarted
argument_list|()
expr_stmt|;
end_expr_stmt

begin_function
unit|}      protected
name|ActiveMQConnectionFactory
name|createConnectionFactory
parameter_list|()
throws|throws
name|Exception
block|{
return|return
operator|new
name|ActiveMQConnectionFactory
argument_list|(
literal|"vm://localhost"
argument_list|)
return|;
block|}
end_function

begin_function
specifier|protected
name|ActiveMQConnection
name|createConnection
parameter_list|()
throws|throws
name|Exception
block|{
return|return
operator|(
name|ActiveMQConnection
operator|)
name|createConnectionFactory
argument_list|()
operator|.
name|createConnection
argument_list|()
return|;
block|}
end_function

unit|}
end_unit

