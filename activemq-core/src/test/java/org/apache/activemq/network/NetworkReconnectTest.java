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
name|Iterator
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
name|advisory
operator|.
name|ConsumerEvent
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
name|advisory
operator|.
name|ConsumerEventSource
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
name|advisory
operator|.
name|ConsumerListener
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
name|ActiveMQQueue
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

begin_comment
comment|/**  * These test cases are used to verifiy that network connections get re  * established in all broker restart scenarios.  *   * @author chirino  */
end_comment

begin_class
specifier|public
class|class
name|NetworkReconnectTest
extends|extends
name|TestCase
block|{
specifier|protected
specifier|static
specifier|final
name|Log
name|log
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|NetworkReconnectTest
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|BrokerService
name|producerBroker
decl_stmt|;
specifier|private
name|BrokerService
name|consumerBroker
decl_stmt|;
specifier|private
name|ActiveMQConnectionFactory
name|producerConnectionFactory
decl_stmt|;
specifier|private
name|ActiveMQConnectionFactory
name|consumerConnectionFactory
decl_stmt|;
specifier|private
name|Destination
name|destination
decl_stmt|;
specifier|private
name|ArrayList
name|connections
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
specifier|public
name|void
name|testMultipleProducerBrokerRestarts
parameter_list|()
throws|throws
name|Exception
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
literal|10
condition|;
name|i
operator|++
control|)
block|{
name|testWithProducerBrokerRestart
argument_list|()
expr_stmt|;
name|disposeConsumerConnections
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|testWithoutRestarts
parameter_list|()
throws|throws
name|Exception
block|{
name|startProducerBroker
argument_list|()
expr_stmt|;
name|startConsumerBroker
argument_list|()
expr_stmt|;
name|MessageConsumer
name|consumer
init|=
name|createConsumer
argument_list|()
decl_stmt|;
name|AtomicInteger
name|counter
init|=
name|createConsumerCounter
argument_list|(
name|producerConnectionFactory
argument_list|)
decl_stmt|;
name|waitForConsumerToArrive
argument_list|(
name|counter
argument_list|)
expr_stmt|;
name|String
name|messageId
init|=
name|sendMessage
argument_list|()
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
name|assertEquals
argument_list|(
name|messageId
argument_list|,
name|message
operator|.
name|getJMSMessageID
argument_list|()
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|consumer
operator|.
name|receiveNoWait
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testWithProducerBrokerRestart
parameter_list|()
throws|throws
name|Exception
block|{
name|startProducerBroker
argument_list|()
expr_stmt|;
name|startConsumerBroker
argument_list|()
expr_stmt|;
name|MessageConsumer
name|consumer
init|=
name|createConsumer
argument_list|()
decl_stmt|;
name|AtomicInteger
name|counter
init|=
name|createConsumerCounter
argument_list|(
name|producerConnectionFactory
argument_list|)
decl_stmt|;
name|waitForConsumerToArrive
argument_list|(
name|counter
argument_list|)
expr_stmt|;
name|String
name|messageId
init|=
name|sendMessage
argument_list|()
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
name|assertEquals
argument_list|(
name|messageId
argument_list|,
name|message
operator|.
name|getJMSMessageID
argument_list|()
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|consumer
operator|.
name|receiveNoWait
argument_list|()
argument_list|)
expr_stmt|;
comment|// Restart the first broker...
name|stopProducerBroker
argument_list|()
expr_stmt|;
name|startProducerBroker
argument_list|()
expr_stmt|;
name|counter
operator|=
name|createConsumerCounter
argument_list|(
name|producerConnectionFactory
argument_list|)
expr_stmt|;
name|waitForConsumerToArrive
argument_list|(
name|counter
argument_list|)
expr_stmt|;
name|messageId
operator|=
name|sendMessage
argument_list|()
expr_stmt|;
name|message
operator|=
name|consumer
operator|.
name|receive
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|messageId
argument_list|,
name|message
operator|.
name|getJMSMessageID
argument_list|()
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|consumer
operator|.
name|receiveNoWait
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testWithConsumerBrokerRestart
parameter_list|()
throws|throws
name|Exception
block|{
name|startProducerBroker
argument_list|()
expr_stmt|;
name|startConsumerBroker
argument_list|()
expr_stmt|;
name|MessageConsumer
name|consumer
init|=
name|createConsumer
argument_list|()
decl_stmt|;
name|AtomicInteger
name|counter
init|=
name|createConsumerCounter
argument_list|(
name|producerConnectionFactory
argument_list|)
decl_stmt|;
name|waitForConsumerToArrive
argument_list|(
name|counter
argument_list|)
expr_stmt|;
name|String
name|messageId
init|=
name|sendMessage
argument_list|()
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
name|assertEquals
argument_list|(
name|messageId
argument_list|,
name|message
operator|.
name|getJMSMessageID
argument_list|()
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|consumer
operator|.
name|receiveNoWait
argument_list|()
argument_list|)
expr_stmt|;
comment|// Restart the first broker...
name|stopConsumerBroker
argument_list|()
expr_stmt|;
name|waitForConsumerToLeave
argument_list|(
name|counter
argument_list|)
expr_stmt|;
name|startConsumerBroker
argument_list|()
expr_stmt|;
name|consumer
operator|=
name|createConsumer
argument_list|()
expr_stmt|;
name|waitForConsumerToArrive
argument_list|(
name|counter
argument_list|)
expr_stmt|;
name|messageId
operator|=
name|sendMessage
argument_list|()
expr_stmt|;
name|message
operator|=
name|consumer
operator|.
name|receive
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|messageId
argument_list|,
name|message
operator|.
name|getJMSMessageID
argument_list|()
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|consumer
operator|.
name|receiveNoWait
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testWithConsumerBrokerStartDelay
parameter_list|()
throws|throws
name|Exception
block|{
name|startConsumerBroker
argument_list|()
expr_stmt|;
name|MessageConsumer
name|consumer
init|=
name|createConsumer
argument_list|()
decl_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
operator|*
literal|5
argument_list|)
expr_stmt|;
name|startProducerBroker
argument_list|()
expr_stmt|;
name|AtomicInteger
name|counter
init|=
name|createConsumerCounter
argument_list|(
name|producerConnectionFactory
argument_list|)
decl_stmt|;
name|waitForConsumerToArrive
argument_list|(
name|counter
argument_list|)
expr_stmt|;
name|String
name|messageId
init|=
name|sendMessage
argument_list|()
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
name|assertEquals
argument_list|(
name|messageId
argument_list|,
name|message
operator|.
name|getJMSMessageID
argument_list|()
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|consumer
operator|.
name|receiveNoWait
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testWithProducerBrokerStartDelay
parameter_list|()
throws|throws
name|Exception
block|{
name|startProducerBroker
argument_list|()
expr_stmt|;
name|AtomicInteger
name|counter
init|=
name|createConsumerCounter
argument_list|(
name|producerConnectionFactory
argument_list|)
decl_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
operator|*
literal|5
argument_list|)
expr_stmt|;
name|startConsumerBroker
argument_list|()
expr_stmt|;
name|MessageConsumer
name|consumer
init|=
name|createConsumer
argument_list|()
decl_stmt|;
name|waitForConsumerToArrive
argument_list|(
name|counter
argument_list|)
expr_stmt|;
name|String
name|messageId
init|=
name|sendMessage
argument_list|()
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
name|assertEquals
argument_list|(
name|messageId
argument_list|,
name|message
operator|.
name|getJMSMessageID
argument_list|()
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|consumer
operator|.
name|receiveNoWait
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|log
operator|.
name|info
argument_list|(
literal|"==============================================================================="
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Running Test Case: "
operator|+
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"==============================================================================="
argument_list|)
expr_stmt|;
name|producerConnectionFactory
operator|=
name|createProducerConnectionFactory
argument_list|()
expr_stmt|;
name|consumerConnectionFactory
operator|=
name|createConsumerConnectionFactory
argument_list|()
expr_stmt|;
name|destination
operator|=
operator|new
name|ActiveMQQueue
argument_list|(
literal|"RECONNECT.TEST.QUEUE"
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
name|disposeConsumerConnections
argument_list|()
expr_stmt|;
try|try
block|{
name|stopProducerBroker
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{         }
try|try
block|{
name|stopConsumerBroker
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{         }
block|}
specifier|protected
name|void
name|disposeConsumerConnections
parameter_list|()
block|{
for|for
control|(
name|Iterator
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
name|connection
init|=
operator|(
name|Connection
operator|)
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
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
name|Throwable
name|ignore
parameter_list|)
block|{             }
block|}
block|}
specifier|protected
name|void
name|startProducerBroker
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|producerBroker
operator|==
literal|null
condition|)
block|{
name|producerBroker
operator|=
name|createFirstBroker
argument_list|()
expr_stmt|;
name|producerBroker
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
block|}
specifier|protected
name|void
name|stopProducerBroker
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|producerBroker
operator|!=
literal|null
condition|)
block|{
name|producerBroker
operator|.
name|stop
argument_list|()
expr_stmt|;
name|producerBroker
operator|=
literal|null
expr_stmt|;
block|}
block|}
specifier|protected
name|void
name|startConsumerBroker
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|consumerBroker
operator|==
literal|null
condition|)
block|{
name|consumerBroker
operator|=
name|createSecondBroker
argument_list|()
expr_stmt|;
name|consumerBroker
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
block|}
specifier|protected
name|void
name|stopConsumerBroker
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|consumerBroker
operator|!=
literal|null
condition|)
block|{
name|consumerBroker
operator|.
name|stop
argument_list|()
expr_stmt|;
name|consumerBroker
operator|=
literal|null
expr_stmt|;
block|}
block|}
specifier|protected
name|BrokerService
name|createFirstBroker
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
literal|"xbean:org/apache/activemq/network/reconnect-broker1.xml"
argument_list|)
argument_list|)
return|;
block|}
specifier|protected
name|BrokerService
name|createSecondBroker
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
literal|"xbean:org/apache/activemq/network/reconnect-broker2.xml"
argument_list|)
argument_list|)
return|;
block|}
specifier|protected
name|ActiveMQConnectionFactory
name|createProducerConnectionFactory
parameter_list|()
block|{
return|return
operator|new
name|ActiveMQConnectionFactory
argument_list|(
literal|"vm://broker1"
argument_list|)
return|;
block|}
specifier|protected
name|ActiveMQConnectionFactory
name|createConsumerConnectionFactory
parameter_list|()
block|{
return|return
operator|new
name|ActiveMQConnectionFactory
argument_list|(
literal|"vm://broker2"
argument_list|)
return|;
block|}
specifier|protected
name|String
name|sendMessage
parameter_list|()
throws|throws
name|JMSException
block|{
name|Connection
name|connection
init|=
literal|null
decl_stmt|;
try|try
block|{
name|connection
operator|=
name|producerConnectionFactory
operator|.
name|createConnection
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
name|Message
name|message
init|=
name|session
operator|.
name|createMessage
argument_list|()
decl_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|message
argument_list|)
expr_stmt|;
return|return
name|message
operator|.
name|getJMSMessageID
argument_list|()
return|;
block|}
finally|finally
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
name|Throwable
name|ignore
parameter_list|)
block|{             }
block|}
block|}
specifier|protected
name|MessageConsumer
name|createConsumer
parameter_list|()
throws|throws
name|JMSException
block|{
name|Connection
name|connection
init|=
name|consumerConnectionFactory
operator|.
name|createConnection
argument_list|()
decl_stmt|;
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
return|return
name|session
operator|.
name|createConsumer
argument_list|(
name|destination
argument_list|)
return|;
block|}
specifier|protected
name|AtomicInteger
name|createConsumerCounter
parameter_list|(
name|ActiveMQConnectionFactory
name|cf
parameter_list|)
throws|throws
name|Exception
block|{
specifier|final
name|AtomicInteger
name|rc
init|=
operator|new
name|AtomicInteger
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|Connection
name|connection
init|=
name|cf
operator|.
name|createConnection
argument_list|()
decl_stmt|;
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
name|ConsumerEventSource
name|source
init|=
operator|new
name|ConsumerEventSource
argument_list|(
name|connection
argument_list|,
name|destination
argument_list|)
decl_stmt|;
name|source
operator|.
name|setConsumerListener
argument_list|(
operator|new
name|ConsumerListener
argument_list|()
block|{
specifier|public
name|void
name|onConsumerEvent
parameter_list|(
name|ConsumerEvent
name|event
parameter_list|)
block|{
name|rc
operator|.
name|set
argument_list|(
name|event
operator|.
name|getConsumerCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|source
operator|.
name|start
argument_list|()
expr_stmt|;
return|return
name|rc
return|;
block|}
specifier|protected
name|void
name|waitForConsumerToArrive
parameter_list|(
name|AtomicInteger
name|consumerCounter
parameter_list|)
throws|throws
name|InterruptedException
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
literal|100
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|consumerCounter
operator|.
name|get
argument_list|()
operator|>
literal|0
condition|)
block|{
return|return;
block|}
name|Thread
operator|.
name|sleep
argument_list|(
literal|100
argument_list|)
expr_stmt|;
block|}
name|fail
argument_list|(
literal|"The consumer did not arrive."
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|waitForConsumerToLeave
parameter_list|(
name|AtomicInteger
name|consumerCounter
parameter_list|)
throws|throws
name|InterruptedException
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
literal|100
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|consumerCounter
operator|.
name|get
argument_list|()
operator|==
literal|0
condition|)
block|{
return|return;
block|}
name|Thread
operator|.
name|sleep
argument_list|(
literal|100
argument_list|)
expr_stmt|;
block|}
name|fail
argument_list|(
literal|"The consumer did not leave."
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

