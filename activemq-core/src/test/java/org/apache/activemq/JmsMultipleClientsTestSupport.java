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
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
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
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
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
name|TopicSubscriber
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
name|BrokerFactory
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
name|util
operator|.
name|MessageIdList
import|;
end_import

begin_comment
comment|/**  * Test case support used to test multiple message comsumers and message  * producers connecting to a single broker.  *   * @version $Revision$  */
end_comment

begin_class
specifier|public
class|class
name|JmsMultipleClientsTestSupport
extends|extends
name|CombinationTestSupport
block|{
specifier|protected
name|Map
argument_list|<
name|MessageConsumer
argument_list|,
name|MessageIdList
argument_list|>
name|consumers
init|=
operator|new
name|HashMap
argument_list|<
name|MessageConsumer
argument_list|,
name|MessageIdList
argument_list|>
argument_list|()
decl_stmt|;
comment|// Map of consumer with messages
comment|// received
specifier|protected
name|int
name|consumerCount
init|=
literal|1
decl_stmt|;
specifier|protected
name|int
name|producerCount
init|=
literal|1
decl_stmt|;
specifier|protected
name|int
name|messageSize
init|=
literal|1024
decl_stmt|;
specifier|protected
name|boolean
name|useConcurrentSend
init|=
literal|true
decl_stmt|;
specifier|protected
name|boolean
name|durable
decl_stmt|;
specifier|protected
name|boolean
name|topic
decl_stmt|;
specifier|protected
name|boolean
name|persistent
decl_stmt|;
specifier|protected
name|BrokerService
name|broker
decl_stmt|;
specifier|protected
name|Destination
name|destination
decl_stmt|;
specifier|protected
name|List
argument_list|<
name|Connection
argument_list|>
name|connections
init|=
name|Collections
operator|.
name|synchronizedList
argument_list|(
operator|new
name|ArrayList
argument_list|<
name|Connection
argument_list|>
argument_list|()
argument_list|)
decl_stmt|;
specifier|protected
name|MessageIdList
name|allMessagesList
init|=
operator|new
name|MessageIdList
argument_list|()
decl_stmt|;
specifier|private
name|AtomicInteger
name|producerLock
decl_stmt|;
specifier|protected
name|void
name|startProducers
parameter_list|(
name|Destination
name|dest
parameter_list|,
name|int
name|msgCount
parameter_list|)
throws|throws
name|Exception
block|{
name|startProducers
argument_list|(
name|createConnectionFactory
argument_list|()
argument_list|,
name|dest
argument_list|,
name|msgCount
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|startProducers
parameter_list|(
specifier|final
name|ConnectionFactory
name|factory
parameter_list|,
specifier|final
name|Destination
name|dest
parameter_list|,
specifier|final
name|int
name|msgCount
parameter_list|)
throws|throws
name|Exception
block|{
comment|// Use concurrent send
if|if
condition|(
name|useConcurrentSend
condition|)
block|{
name|producerLock
operator|=
operator|new
name|AtomicInteger
argument_list|(
name|producerCount
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|producerCount
condition|;
name|i
operator|++
control|)
block|{
name|Thread
name|t
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
name|sendMessages
argument_list|(
name|factory
operator|.
name|createConnection
argument_list|()
argument_list|,
name|dest
argument_list|,
name|msgCount
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
synchronized|synchronized
init|(
name|producerLock
init|)
block|{
name|producerLock
operator|.
name|decrementAndGet
argument_list|()
expr_stmt|;
name|producerLock
operator|.
name|notifyAll
argument_list|()
expr_stmt|;
block|}
block|}
block|}
argument_list|)
decl_stmt|;
name|t
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
comment|// Wait for all producers to finish sending
synchronized|synchronized
init|(
name|producerLock
init|)
block|{
while|while
condition|(
name|producerLock
operator|.
name|get
argument_list|()
operator|!=
literal|0
condition|)
block|{
name|producerLock
operator|.
name|wait
argument_list|(
literal|2000
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Use serialized send
block|}
else|else
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|producerCount
condition|;
name|i
operator|++
control|)
block|{
name|sendMessages
argument_list|(
name|factory
operator|.
name|createConnection
argument_list|()
argument_list|,
name|dest
argument_list|,
name|msgCount
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|protected
name|void
name|sendMessages
parameter_list|(
name|Connection
name|connection
parameter_list|,
name|Destination
name|destination
parameter_list|,
name|int
name|count
parameter_list|)
throws|throws
name|Exception
block|{
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
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
name|producer
operator|.
name|setDeliveryMode
argument_list|(
name|persistent
condition|?
name|DeliveryMode
operator|.
name|PERSISTENT
else|:
name|DeliveryMode
operator|.
name|NON_PERSISTENT
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|count
condition|;
name|i
operator|++
control|)
block|{
name|TextMessage
name|msg
init|=
name|createTextMessage
argument_list|(
name|session
argument_list|,
literal|""
operator|+
name|i
argument_list|)
decl_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|msg
argument_list|)
expr_stmt|;
block|}
name|producer
operator|.
name|close
argument_list|()
expr_stmt|;
name|session
operator|.
name|close
argument_list|()
expr_stmt|;
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
specifier|protected
name|TextMessage
name|createTextMessage
parameter_list|(
name|Session
name|session
parameter_list|,
name|String
name|initText
parameter_list|)
throws|throws
name|Exception
block|{
name|TextMessage
name|msg
init|=
name|session
operator|.
name|createTextMessage
argument_list|()
decl_stmt|;
comment|// Pad message text
if|if
condition|(
name|initText
operator|.
name|length
argument_list|()
operator|<
name|messageSize
condition|)
block|{
name|char
index|[]
name|data
init|=
operator|new
name|char
index|[
name|messageSize
operator|-
name|initText
operator|.
name|length
argument_list|()
index|]
decl_stmt|;
name|Arrays
operator|.
name|fill
argument_list|(
name|data
argument_list|,
literal|'*'
argument_list|)
expr_stmt|;
name|String
name|str
init|=
operator|new
name|String
argument_list|(
name|data
argument_list|)
decl_stmt|;
name|msg
operator|.
name|setText
argument_list|(
name|initText
operator|+
name|str
argument_list|)
expr_stmt|;
comment|// Do not pad message text
block|}
else|else
block|{
name|msg
operator|.
name|setText
argument_list|(
name|initText
argument_list|)
expr_stmt|;
block|}
return|return
name|msg
return|;
block|}
specifier|protected
name|void
name|startConsumers
parameter_list|(
name|Destination
name|dest
parameter_list|)
throws|throws
name|Exception
block|{
name|startConsumers
argument_list|(
name|createConnectionFactory
argument_list|()
argument_list|,
name|dest
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|startConsumers
parameter_list|(
name|ConnectionFactory
name|factory
parameter_list|,
name|Destination
name|dest
parameter_list|)
throws|throws
name|Exception
block|{
name|MessageConsumer
name|consumer
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
name|consumerCount
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|durable
operator|&&
name|topic
condition|)
block|{
name|consumer
operator|=
name|createDurableSubscriber
argument_list|(
name|factory
operator|.
name|createConnection
argument_list|()
argument_list|,
name|dest
argument_list|,
literal|"consumer"
operator|+
operator|(
name|i
operator|+
literal|1
operator|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|consumer
operator|=
name|createMessageConsumer
argument_list|(
name|factory
operator|.
name|createConnection
argument_list|()
argument_list|,
name|dest
argument_list|)
expr_stmt|;
block|}
name|MessageIdList
name|list
init|=
operator|new
name|MessageIdList
argument_list|()
decl_stmt|;
name|list
operator|.
name|setParent
argument_list|(
name|allMessagesList
argument_list|)
expr_stmt|;
name|consumer
operator|.
name|setMessageListener
argument_list|(
name|list
argument_list|)
expr_stmt|;
name|consumers
operator|.
name|put
argument_list|(
name|consumer
argument_list|,
name|list
argument_list|)
expr_stmt|;
block|}
block|}
specifier|protected
name|MessageConsumer
name|createMessageConsumer
parameter_list|(
name|Connection
name|conn
parameter_list|,
name|Destination
name|dest
parameter_list|)
throws|throws
name|Exception
block|{
name|connections
operator|.
name|add
argument_list|(
name|conn
argument_list|)
expr_stmt|;
name|Session
name|sess
init|=
name|conn
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
specifier|final
name|MessageConsumer
name|consumer
init|=
name|sess
operator|.
name|createConsumer
argument_list|(
name|dest
argument_list|)
decl_stmt|;
name|conn
operator|.
name|start
argument_list|()
expr_stmt|;
return|return
name|consumer
return|;
block|}
specifier|protected
name|TopicSubscriber
name|createDurableSubscriber
parameter_list|(
name|Connection
name|conn
parameter_list|,
name|Destination
name|dest
parameter_list|,
name|String
name|name
parameter_list|)
throws|throws
name|Exception
block|{
name|conn
operator|.
name|setClientID
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|connections
operator|.
name|add
argument_list|(
name|conn
argument_list|)
expr_stmt|;
name|conn
operator|.
name|start
argument_list|()
expr_stmt|;
name|Session
name|sess
init|=
name|conn
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
specifier|final
name|TopicSubscriber
name|consumer
init|=
name|sess
operator|.
name|createDurableSubscriber
argument_list|(
operator|(
name|javax
operator|.
name|jms
operator|.
name|Topic
operator|)
name|dest
argument_list|,
name|name
argument_list|)
decl_stmt|;
return|return
name|consumer
return|;
block|}
specifier|protected
name|void
name|waitForAllMessagesToBeReceived
parameter_list|(
name|int
name|messageCount
parameter_list|)
throws|throws
name|Exception
block|{
name|allMessagesList
operator|.
name|waitForMessagesToArrive
argument_list|(
name|messageCount
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|ActiveMQDestination
name|createDestination
parameter_list|()
throws|throws
name|JMSException
block|{
name|String
name|name
init|=
literal|"."
operator|+
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|"."
operator|+
name|getName
argument_list|()
decl_stmt|;
if|if
condition|(
name|topic
condition|)
block|{
name|destination
operator|=
operator|new
name|ActiveMQTopic
argument_list|(
literal|"Topic"
operator|+
name|name
argument_list|)
expr_stmt|;
return|return
operator|(
name|ActiveMQDestination
operator|)
name|destination
return|;
block|}
else|else
block|{
name|destination
operator|=
operator|new
name|ActiveMQQueue
argument_list|(
literal|"Queue"
operator|+
name|name
argument_list|)
expr_stmt|;
return|return
operator|(
name|ActiveMQDestination
operator|)
name|destination
return|;
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
literal|"vm://localhost"
argument_list|)
return|;
block|}
specifier|protected
name|BrokerService
name|createBroker
parameter_list|()
throws|throws
name|Exception
block|{
return|return
name|BrokerFactory
operator|.
name|createBroker
argument_list|(
operator|new
name|URI
argument_list|(
literal|"broker://()/localhost?persistent=false&useJmx=true"
argument_list|)
argument_list|)
return|;
block|}
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
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
block|}
specifier|protected
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
for|for
control|(
name|Iterator
argument_list|<
name|Connection
argument_list|>
name|iter
init|=
name|connections
operator|.
name|iterator
argument_list|()
init|;
name|iter
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|Connection
name|conn
init|=
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
try|try
block|{
name|conn
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{             }
block|}
name|broker
operator|.
name|stop
argument_list|()
expr_stmt|;
name|allMessagesList
operator|.
name|flushMessages
argument_list|()
expr_stmt|;
name|consumers
operator|.
name|clear
argument_list|()
expr_stmt|;
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
comment|/*      * Some helpful assertions for multiple consumers.      */
specifier|protected
name|void
name|assertConsumerReceivedAtLeastXMessages
parameter_list|(
name|MessageConsumer
name|consumer
parameter_list|,
name|int
name|msgCount
parameter_list|)
block|{
name|MessageIdList
name|messageIdList
init|=
name|consumers
operator|.
name|get
argument_list|(
name|consumer
argument_list|)
decl_stmt|;
name|messageIdList
operator|.
name|assertAtLeastMessagesReceived
argument_list|(
name|msgCount
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|assertConsumerReceivedAtMostXMessages
parameter_list|(
name|MessageConsumer
name|consumer
parameter_list|,
name|int
name|msgCount
parameter_list|)
block|{
name|MessageIdList
name|messageIdList
init|=
name|consumers
operator|.
name|get
argument_list|(
name|consumer
argument_list|)
decl_stmt|;
name|messageIdList
operator|.
name|assertAtMostMessagesReceived
argument_list|(
name|msgCount
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|assertConsumerReceivedXMessages
parameter_list|(
name|MessageConsumer
name|consumer
parameter_list|,
name|int
name|msgCount
parameter_list|)
block|{
name|MessageIdList
name|messageIdList
init|=
name|consumers
operator|.
name|get
argument_list|(
name|consumer
argument_list|)
decl_stmt|;
name|messageIdList
operator|.
name|assertMessagesReceivedNoWait
argument_list|(
name|msgCount
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|assertEachConsumerReceivedAtLeastXMessages
parameter_list|(
name|int
name|msgCount
parameter_list|)
block|{
for|for
control|(
name|Iterator
argument_list|<
name|MessageConsumer
argument_list|>
name|i
init|=
name|consumers
operator|.
name|keySet
argument_list|()
operator|.
name|iterator
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|assertConsumerReceivedAtLeastXMessages
argument_list|(
name|i
operator|.
name|next
argument_list|()
argument_list|,
name|msgCount
argument_list|)
expr_stmt|;
block|}
block|}
specifier|protected
name|void
name|assertEachConsumerReceivedAtMostXMessages
parameter_list|(
name|int
name|msgCount
parameter_list|)
block|{
for|for
control|(
name|Iterator
argument_list|<
name|MessageConsumer
argument_list|>
name|i
init|=
name|consumers
operator|.
name|keySet
argument_list|()
operator|.
name|iterator
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|assertConsumerReceivedAtMostXMessages
argument_list|(
name|i
operator|.
name|next
argument_list|()
argument_list|,
name|msgCount
argument_list|)
expr_stmt|;
block|}
block|}
specifier|protected
name|void
name|assertEachConsumerReceivedXMessages
parameter_list|(
name|int
name|msgCount
parameter_list|)
block|{
for|for
control|(
name|Iterator
argument_list|<
name|MessageConsumer
argument_list|>
name|i
init|=
name|consumers
operator|.
name|keySet
argument_list|()
operator|.
name|iterator
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|assertConsumerReceivedXMessages
argument_list|(
name|i
operator|.
name|next
argument_list|()
argument_list|,
name|msgCount
argument_list|)
expr_stmt|;
block|}
block|}
specifier|protected
name|void
name|assertTotalMessagesReceived
parameter_list|(
name|int
name|msgCount
parameter_list|)
block|{
name|allMessagesList
operator|.
name|assertMessagesReceivedNoWait
argument_list|(
name|msgCount
argument_list|)
expr_stmt|;
comment|// now lets count the individual messages received
name|int
name|totalMsg
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Iterator
argument_list|<
name|MessageConsumer
argument_list|>
name|i
init|=
name|consumers
operator|.
name|keySet
argument_list|()
operator|.
name|iterator
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|MessageIdList
name|messageIdList
init|=
name|consumers
operator|.
name|get
argument_list|(
name|i
operator|.
name|next
argument_list|()
argument_list|)
decl_stmt|;
name|totalMsg
operator|+=
name|messageIdList
operator|.
name|getMessageCount
argument_list|()
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|"Total of consumers message count"
argument_list|,
name|msgCount
argument_list|,
name|totalMsg
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

