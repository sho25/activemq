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
name|bugs
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
name|Random
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
name|ActiveMQPrefetchPolicy
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
comment|/**  * This is a test case for the issue reported at:  * https://issues.apache.org/activemq/browse/AMQ-1866  *   * If you have a JMS producer sending messages to multiple consumers and   * you have a low prefetch, eventually all consumers will run as slow as   * the slowest consumer.    */
end_comment

begin_class
specifier|public
class|class
name|AMQ1866
extends|extends
name|TestCase
block|{
specifier|private
specifier|static
specifier|final
name|Log
name|log
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|ConsumerThread
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|BrokerService
name|brokerService
decl_stmt|;
specifier|private
name|ArrayList
argument_list|<
name|Thread
argument_list|>
name|threads
init|=
operator|new
name|ArrayList
argument_list|<
name|Thread
argument_list|>
argument_list|()
decl_stmt|;
name|String
name|ACTIVEMQ_BROKER_BIND
init|=
literal|"tcp://localhost:61616"
decl_stmt|;
name|String
name|ACTIVEMQ_BROKER_URI
init|=
literal|"tcp://localhost:61616"
decl_stmt|;
name|String
name|REQUEST_QUEUE
init|=
literal|"provider.queue"
decl_stmt|;
name|AtomicBoolean
name|shutdown
init|=
operator|new
name|AtomicBoolean
argument_list|()
decl_stmt|;
annotation|@
name|Override
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Start an embedded broker up.
name|brokerService
operator|=
operator|new
name|BrokerService
argument_list|()
expr_stmt|;
name|brokerService
operator|.
name|addConnector
argument_list|(
name|ACTIVEMQ_BROKER_BIND
argument_list|)
expr_stmt|;
name|brokerService
operator|.
name|start
argument_list|()
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
comment|// Stop any running threads.
name|shutdown
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
for|for
control|(
name|Thread
name|t
range|:
name|threads
control|)
block|{
name|t
operator|.
name|join
argument_list|()
expr_stmt|;
block|}
name|brokerService
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
comment|// Failing
specifier|public
name|void
name|testConsumerSlowDownPrefetch0
parameter_list|()
throws|throws
name|InterruptedException
block|{
name|ACTIVEMQ_BROKER_URI
operator|=
literal|"tcp://localhost:61616?jms.prefetchPolicy.queuePrefetch=0"
expr_stmt|;
name|doTestConsumerSlowDown
argument_list|()
expr_stmt|;
block|}
comment|// Failing
specifier|public
name|void
name|testConsumerSlowDownPrefetch10
parameter_list|()
throws|throws
name|InterruptedException
block|{
name|ACTIVEMQ_BROKER_URI
operator|=
literal|"tcp://localhost:61616?jms.prefetchPolicy.queuePrefetch=10"
expr_stmt|;
name|doTestConsumerSlowDown
argument_list|()
expr_stmt|;
block|}
comment|// Passing
specifier|public
name|void
name|testConsumerSlowDownDefaultPrefetch
parameter_list|()
throws|throws
name|InterruptedException
block|{
name|ACTIVEMQ_BROKER_URI
operator|=
literal|"tcp://localhost:61616"
expr_stmt|;
name|doTestConsumerSlowDown
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|doTestConsumerSlowDown
parameter_list|()
throws|throws
name|InterruptedException
block|{
name|ConsumerThread
name|c1
init|=
operator|new
name|ConsumerThread
argument_list|(
literal|"Consumer-1"
argument_list|)
decl_stmt|;
name|ConsumerThread
name|c2
init|=
operator|new
name|ConsumerThread
argument_list|(
literal|"Consumer-2"
argument_list|)
decl_stmt|;
name|ProducerThread
name|p1
init|=
operator|new
name|ProducerThread
argument_list|(
literal|"Producer-1"
argument_list|)
decl_stmt|;
name|threads
operator|.
name|add
argument_list|(
name|c1
argument_list|)
expr_stmt|;
name|threads
operator|.
name|add
argument_list|(
name|c2
argument_list|)
expr_stmt|;
name|threads
operator|.
name|add
argument_list|(
name|p1
argument_list|)
expr_stmt|;
name|c1
operator|.
name|start
argument_list|()
expr_stmt|;
name|c2
operator|.
name|start
argument_list|()
expr_stmt|;
name|p1
operator|.
name|start
argument_list|()
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
literal|30
condition|;
name|i
operator|++
control|)
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
name|long
name|p1Counter
init|=
name|p1
operator|.
name|counter
operator|.
name|getAndSet
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|long
name|c1Counter
init|=
name|c1
operator|.
name|counter
operator|.
name|getAndSet
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|long
name|c2Counter
init|=
name|c2
operator|.
name|counter
operator|.
name|getAndSet
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"p1: "
operator|+
name|p1Counter
operator|+
literal|", c1: "
operator|+
name|c1Counter
operator|+
literal|", c2: "
operator|+
name|c2Counter
argument_list|)
expr_stmt|;
comment|// Once message have been flowing for a few seconds, start asserting that c2 always gets messages.  It should be receiving about 100 / sec
if|if
condition|(
name|i
operator|>
literal|2
condition|)
block|{
name|assertTrue
argument_list|(
literal|"Consumer 2 should be receiving new messages every second."
argument_list|,
name|c2Counter
operator|>
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|public
class|class
name|ProducerThread
extends|extends
name|Thread
block|{
specifier|final
name|AtomicLong
name|counter
init|=
operator|new
name|AtomicLong
argument_list|()
decl_stmt|;
specifier|public
name|ProducerThread
parameter_list|(
name|String
name|threadId
parameter_list|)
block|{
name|super
argument_list|(
name|threadId
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|run
parameter_list|()
block|{
name|Connection
name|connection
init|=
literal|null
decl_stmt|;
try|try
block|{
name|log
operator|.
name|debug
argument_list|(
name|getName
argument_list|()
operator|+
literal|": is running"
argument_list|)
expr_stmt|;
name|ActiveMQConnectionFactory
name|factory
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
name|ACTIVEMQ_BROKER_URI
argument_list|)
decl_stmt|;
name|factory
operator|.
name|setDispatchAsync
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|Destination
name|requestDestination
init|=
operator|new
name|ActiveMQQueue
argument_list|(
name|REQUEST_QUEUE
argument_list|)
decl_stmt|;
name|connection
operator|=
name|factory
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
name|requestDestination
argument_list|)
decl_stmt|;
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
while|while
condition|(
operator|!
name|shutdown
operator|.
name|get
argument_list|()
condition|)
block|{
name|producer
operator|.
name|send
argument_list|(
name|session
operator|.
name|createTextMessage
argument_list|(
name|getName
argument_list|()
operator|+
literal|" Message "
operator|+
operator|(
operator|++
name|i
operator|)
argument_list|)
argument_list|)
expr_stmt|;
name|counter
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|10
argument_list|)
expr_stmt|;
block|}
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
name|e
parameter_list|)
block|{                 }
block|}
block|}
block|}
specifier|public
class|class
name|ConsumerThread
extends|extends
name|Thread
block|{
specifier|final
name|AtomicLong
name|counter
init|=
operator|new
name|AtomicLong
argument_list|()
decl_stmt|;
specifier|public
name|ConsumerThread
parameter_list|(
name|String
name|threadId
parameter_list|)
block|{
name|super
argument_list|(
name|threadId
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|run
parameter_list|()
block|{
name|Connection
name|connection
init|=
literal|null
decl_stmt|;
try|try
block|{
name|log
operator|.
name|debug
argument_list|(
name|getName
argument_list|()
operator|+
literal|": is running"
argument_list|)
expr_stmt|;
name|ActiveMQConnectionFactory
name|factory
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
name|ACTIVEMQ_BROKER_URI
argument_list|)
decl_stmt|;
name|factory
operator|.
name|setDispatchAsync
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|Destination
name|requestDestination
init|=
operator|new
name|ActiveMQQueue
argument_list|(
name|REQUEST_QUEUE
argument_list|)
decl_stmt|;
name|connection
operator|=
name|factory
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
name|MessageConsumer
name|consumer
init|=
name|session
operator|.
name|createConsumer
argument_list|(
name|requestDestination
argument_list|)
decl_stmt|;
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
while|while
condition|(
operator|!
name|shutdown
operator|.
name|get
argument_list|()
condition|)
block|{
name|TextMessage
name|msg
init|=
operator|(
name|TextMessage
operator|)
name|consumer
operator|.
name|receive
argument_list|(
literal|1000
argument_list|)
decl_stmt|;
if|if
condition|(
name|msg
operator|!=
literal|null
condition|)
block|{
name|int
name|sleepingTime
decl_stmt|;
if|if
condition|(
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
literal|"Consumer-1"
argument_list|)
condition|)
block|{
name|sleepingTime
operator|=
literal|10
operator|*
literal|1000
expr_stmt|;
block|}
else|else
block|{
name|sleepingTime
operator|=
literal|10
expr_stmt|;
block|}
name|Thread
operator|.
name|sleep
argument_list|(
name|sleepingTime
argument_list|)
expr_stmt|;
name|counter
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
block|}
block|}
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
name|e
parameter_list|)
block|{                 }
block|}
block|}
block|}
block|}
end_class

end_unit

