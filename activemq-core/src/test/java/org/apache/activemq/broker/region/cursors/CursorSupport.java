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
name|broker
operator|.
name|region
operator|.
name|cursors
package|;
end_package

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
name|Properties
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
name|ConnectionFactory
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
name|Test
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
name|CombinationTestSupport
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
name|BrokerTest
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
name|Queue
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
comment|/**  * @version $Revision: 1.3 $  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|CursorSupport
extends|extends
name|CombinationTestSupport
block|{
specifier|public
name|int
name|MESSAGE_COUNT
init|=
literal|500
decl_stmt|;
specifier|public
name|int
name|PREFETCH_SIZE
init|=
literal|50
decl_stmt|;
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
name|CursorSupport
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|protected
name|BrokerService
name|broker
decl_stmt|;
specifier|protected
name|String
name|bindAddress
init|=
literal|"tcp://localhost:60706"
decl_stmt|;
specifier|protected
specifier|abstract
name|Destination
name|getDestination
parameter_list|(
name|Session
name|session
parameter_list|)
throws|throws
name|JMSException
function_decl|;
specifier|protected
specifier|abstract
name|MessageConsumer
name|getConsumer
parameter_list|(
name|Connection
name|connection
parameter_list|)
throws|throws
name|Exception
function_decl|;
specifier|protected
specifier|abstract
name|void
name|configureBroker
parameter_list|(
name|BrokerService
name|answer
parameter_list|)
throws|throws
name|Exception
function_decl|;
specifier|public
name|void
name|testSendFirstThenConsume
parameter_list|()
throws|throws
name|Exception
block|{
name|ConnectionFactory
name|factory
init|=
name|createConnectionFactory
argument_list|()
decl_stmt|;
name|Connection
name|consumerConnection
init|=
name|getConsumerConnection
argument_list|(
name|factory
argument_list|)
decl_stmt|;
name|MessageConsumer
name|consumer
init|=
name|getConsumer
argument_list|(
name|consumerConnection
argument_list|)
decl_stmt|;
name|consumerConnection
operator|.
name|close
argument_list|()
expr_stmt|;
name|Connection
name|producerConnection
init|=
name|factory
operator|.
name|createConnection
argument_list|()
decl_stmt|;
name|producerConnection
operator|.
name|start
argument_list|()
expr_stmt|;
name|Session
name|session
init|=
name|producerConnection
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
name|MessageProducer
name|producer
init|=
name|session
operator|.
name|createProducer
argument_list|(
name|getDestination
argument_list|(
name|session
argument_list|)
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Message
argument_list|>
name|senderList
init|=
operator|new
name|ArrayList
argument_list|<
name|Message
argument_list|>
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
name|MESSAGE_COUNT
condition|;
name|i
operator|++
control|)
block|{
name|Message
name|msg
init|=
name|session
operator|.
name|createTextMessage
argument_list|(
literal|"test"
operator|+
name|i
argument_list|)
decl_stmt|;
name|senderList
operator|.
name|add
argument_list|(
name|msg
argument_list|)
expr_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|msg
argument_list|)
expr_stmt|;
block|}
name|producerConnection
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// now consume the messages
name|consumerConnection
operator|=
name|getConsumerConnection
argument_list|(
name|factory
argument_list|)
expr_stmt|;
comment|// create durable subs
name|consumer
operator|=
name|getConsumer
argument_list|(
name|consumerConnection
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|Message
argument_list|>
name|consumerList
init|=
operator|new
name|ArrayList
argument_list|<
name|Message
argument_list|>
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
name|MESSAGE_COUNT
condition|;
name|i
operator|++
control|)
block|{
name|Message
name|msg
init|=
name|consumer
operator|.
name|receive
argument_list|(
literal|1000
operator|*
literal|5
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"Message "
operator|+
name|i
operator|+
literal|" was missing."
argument_list|,
name|msg
argument_list|)
expr_stmt|;
name|consumerList
operator|.
name|add
argument_list|(
name|msg
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|senderList
argument_list|,
name|consumerList
argument_list|)
expr_stmt|;
name|consumerConnection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|initCombosForTestSendWhilstConsume
parameter_list|()
block|{
name|addCombinationValues
argument_list|(
literal|"MESSAGE_COUNT"
argument_list|,
operator|new
name|Object
index|[]
block|{
name|Integer
operator|.
name|valueOf
argument_list|(
literal|400
argument_list|)
block|,
name|Integer
operator|.
name|valueOf
argument_list|(
literal|500
argument_list|)
block|}
argument_list|)
expr_stmt|;
name|addCombinationValues
argument_list|(
literal|"PREFETCH_SIZE"
argument_list|,
operator|new
name|Object
index|[]
block|{
name|Integer
operator|.
name|valueOf
argument_list|(
literal|100
argument_list|)
block|,
name|Integer
operator|.
name|valueOf
argument_list|(
literal|50
argument_list|)
block|}
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testSendWhilstConsume
parameter_list|()
throws|throws
name|Exception
block|{
name|ConnectionFactory
name|factory
init|=
name|createConnectionFactory
argument_list|()
decl_stmt|;
name|Connection
name|consumerConnection
init|=
name|getConsumerConnection
argument_list|(
name|factory
argument_list|)
decl_stmt|;
comment|// create durable subs
name|MessageConsumer
name|consumer
init|=
name|getConsumer
argument_list|(
name|consumerConnection
argument_list|)
decl_stmt|;
name|consumerConnection
operator|.
name|close
argument_list|()
expr_stmt|;
name|Connection
name|producerConnection
init|=
name|factory
operator|.
name|createConnection
argument_list|()
decl_stmt|;
name|producerConnection
operator|.
name|start
argument_list|()
expr_stmt|;
name|Session
name|session
init|=
name|producerConnection
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
name|MessageProducer
name|producer
init|=
name|session
operator|.
name|createProducer
argument_list|(
name|getDestination
argument_list|(
name|session
argument_list|)
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|TextMessage
argument_list|>
name|senderList
init|=
operator|new
name|ArrayList
argument_list|<
name|TextMessage
argument_list|>
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
name|MESSAGE_COUNT
operator|/
literal|10
condition|;
name|i
operator|++
control|)
block|{
name|TextMessage
name|msg
init|=
name|session
operator|.
name|createTextMessage
argument_list|(
literal|"test"
operator|+
name|i
argument_list|)
decl_stmt|;
name|senderList
operator|.
name|add
argument_list|(
name|msg
argument_list|)
expr_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|msg
argument_list|)
expr_stmt|;
block|}
comment|// now consume the messages
name|consumerConnection
operator|=
name|getConsumerConnection
argument_list|(
name|factory
argument_list|)
expr_stmt|;
comment|// create durable subs
name|consumer
operator|=
name|getConsumer
argument_list|(
name|consumerConnection
argument_list|)
expr_stmt|;
specifier|final
name|List
argument_list|<
name|Message
argument_list|>
name|consumerList
init|=
operator|new
name|ArrayList
argument_list|<
name|Message
argument_list|>
argument_list|()
decl_stmt|;
specifier|final
name|CountDownLatch
name|latch
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|consumer
operator|.
name|setMessageListener
argument_list|(
operator|new
name|MessageListener
argument_list|()
block|{
specifier|public
name|void
name|onMessage
parameter_list|(
name|Message
name|msg
parameter_list|)
block|{
try|try
block|{
comment|// sleep to act as a slow consumer
comment|// which will force a mix of direct and polled dispatching
comment|// using the cursor on the broker
name|Thread
operator|.
name|sleep
argument_list|(
literal|50
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// TODO Auto-generated catch block
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
name|consumerList
operator|.
name|add
argument_list|(
name|msg
argument_list|)
expr_stmt|;
if|if
condition|(
name|consumerList
operator|.
name|size
argument_list|()
operator|==
name|MESSAGE_COUNT
condition|)
block|{
name|latch
operator|.
name|countDown
argument_list|()
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
name|MESSAGE_COUNT
operator|/
literal|10
init|;
name|i
operator|<
name|MESSAGE_COUNT
condition|;
name|i
operator|++
control|)
block|{
name|TextMessage
name|msg
init|=
name|session
operator|.
name|createTextMessage
argument_list|(
literal|"test"
operator|+
name|i
argument_list|)
decl_stmt|;
name|senderList
operator|.
name|add
argument_list|(
name|msg
argument_list|)
expr_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|msg
argument_list|)
expr_stmt|;
block|}
name|latch
operator|.
name|await
argument_list|(
literal|300000
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
expr_stmt|;
name|producerConnection
operator|.
name|close
argument_list|()
expr_stmt|;
name|consumerConnection
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Still dipatching - count down latch not sprung"
argument_list|,
name|latch
operator|.
name|getCount
argument_list|()
argument_list|,
literal|0
argument_list|)
expr_stmt|;
comment|//assertEquals("cosumerList - expected: " + MESSAGE_COUNT + " but was: " + consumerList.size(), consumerList.size(), senderList.size());
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|senderList
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|Message
name|sent
init|=
name|senderList
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|Message
name|consumed
init|=
name|consumerList
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|sent
operator|.
name|equals
argument_list|(
name|consumed
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"BAD MATCH AT POS "
operator|+
name|i
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|error
argument_list|(
name|sent
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|error
argument_list|(
name|consumed
argument_list|)
expr_stmt|;
comment|/*                  * log.error("\n\n\n\n\n"); for (int j = 0; j<                  * consumerList.size(); j++) { log.error(consumerList.get(j)); }                  */
block|}
name|assertEquals
argument_list|(
literal|"This should be the same at pos "
operator|+
name|i
operator|+
literal|" in the list"
argument_list|,
name|sent
operator|.
name|getJMSMessageID
argument_list|()
argument_list|,
name|consumed
operator|.
name|getJMSMessageID
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
specifier|protected
name|Connection
name|getConsumerConnection
parameter_list|(
name|ConnectionFactory
name|fac
parameter_list|)
throws|throws
name|JMSException
block|{
name|Connection
name|connection
init|=
name|fac
operator|.
name|createConnection
argument_list|()
decl_stmt|;
name|connection
operator|.
name|setClientID
argument_list|(
literal|"testConsumer"
argument_list|)
expr_stmt|;
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
return|return
name|connection
return|;
block|}
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|broker
operator|==
literal|null
condition|)
block|{
name|broker
operator|=
name|createBroker
argument_list|()
expr_stmt|;
block|}
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
specifier|protected
name|ActiveMQConnectionFactory
name|createConnectionFactory
parameter_list|()
throws|throws
name|Exception
block|{
name|ActiveMQConnectionFactory
name|cf
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
name|bindAddress
argument_list|)
decl_stmt|;
name|Properties
name|props
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
name|props
operator|.
name|setProperty
argument_list|(
literal|"prefetchPolicy.durableTopicPrefetch"
argument_list|,
literal|""
operator|+
name|PREFETCH_SIZE
argument_list|)
expr_stmt|;
name|props
operator|.
name|setProperty
argument_list|(
literal|"prefetchPolicy.optimizeDurableTopicPrefetch"
argument_list|,
literal|""
operator|+
name|PREFETCH_SIZE
argument_list|)
expr_stmt|;
name|props
operator|.
name|setProperty
argument_list|(
literal|"prefetchPolicy.queuePrefetch"
argument_list|,
literal|""
operator|+
name|PREFETCH_SIZE
argument_list|)
expr_stmt|;
name|cf
operator|.
name|setProperties
argument_list|(
name|props
argument_list|)
expr_stmt|;
return|return
name|cf
return|;
block|}
specifier|protected
name|BrokerService
name|createBroker
parameter_list|()
throws|throws
name|Exception
block|{
name|BrokerService
name|answer
init|=
operator|new
name|BrokerService
argument_list|()
decl_stmt|;
name|configureBroker
argument_list|(
name|answer
argument_list|)
expr_stmt|;
name|answer
operator|.
name|start
argument_list|()
expr_stmt|;
return|return
name|answer
return|;
block|}
block|}
end_class

end_unit

