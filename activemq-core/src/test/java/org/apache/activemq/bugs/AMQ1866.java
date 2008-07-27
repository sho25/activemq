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
name|store
operator|.
name|amq
operator|.
name|AMQPersistenceAdapter
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
comment|/**  * This is a test case for the issue reported at:  * https://issues.apache.org/activemq/browse/AMQ-1866  *   * If you have a JMS producer sending messages to multiple fast consumers and   * one slow consumer, eventually all consumers will run as slow as   * the slowest consumer.    */
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
name|AtomicBoolean
name|shutdown
init|=
operator|new
name|AtomicBoolean
argument_list|()
decl_stmt|;
specifier|private
name|ActiveMQQueue
name|destination
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
name|AMQPersistenceAdapter
name|adaptor
init|=
operator|new
name|AMQPersistenceAdapter
argument_list|()
decl_stmt|;
name|adaptor
operator|.
name|setIndexBinSize
argument_list|(
literal|4096
argument_list|)
expr_stmt|;
name|brokerService
operator|.
name|setPersistenceAdapter
argument_list|(
name|adaptor
argument_list|)
expr_stmt|;
name|brokerService
operator|.
name|deleteAllMessages
argument_list|()
expr_stmt|;
comment|// A small max page size makes this issue occur faster.
name|PolicyMap
name|policyMap
init|=
operator|new
name|PolicyMap
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
name|setMaxPageSize
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|policyMap
operator|.
name|put
argument_list|(
operator|new
name|ActiveMQQueue
argument_list|(
literal|">"
argument_list|)
argument_list|,
name|pe
argument_list|)
expr_stmt|;
name|brokerService
operator|.
name|setDestinationPolicy
argument_list|(
name|policyMap
argument_list|)
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
name|destination
operator|=
operator|new
name|ActiveMQQueue
argument_list|(
name|getName
argument_list|()
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
name|interrupt
argument_list|()
expr_stmt|;
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
name|Exception
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
name|Exception
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
name|Exception
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
name|Exception
block|{
comment|// Preload the queue.
name|produce
argument_list|(
literal|20000
argument_list|)
expr_stmt|;
name|Thread
name|producer
init|=
operator|new
name|Thread
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
while|while
condition|(
operator|!
name|shutdown
operator|.
name|get
argument_list|()
condition|)
block|{
name|produce
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{                 }
block|}
block|}
decl_stmt|;
name|threads
operator|.
name|add
argument_list|(
name|producer
argument_list|)
expr_stmt|;
name|producer
operator|.
name|start
argument_list|()
expr_stmt|;
comment|// This is the slow consumer.
name|ConsumerThread
name|c1
init|=
operator|new
name|ConsumerThread
argument_list|(
literal|"Consumer-1"
argument_list|)
decl_stmt|;
name|threads
operator|.
name|add
argument_list|(
name|c1
argument_list|)
expr_stmt|;
name|c1
operator|.
name|start
argument_list|()
expr_stmt|;
comment|// Wait a bit so that the slow consumer gets assigned most of the messages.
name|Thread
operator|.
name|sleep
argument_list|(
literal|500
argument_list|)
expr_stmt|;
name|ConsumerThread
name|c2
init|=
operator|new
name|ConsumerThread
argument_list|(
literal|"Consumer-2"
argument_list|)
decl_stmt|;
name|threads
operator|.
name|add
argument_list|(
name|c2
argument_list|)
expr_stmt|;
name|c2
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
literal|"c1: "
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
literal|3
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
name|void
name|produce
parameter_list|(
name|int
name|count
parameter_list|)
throws|throws
name|Exception
block|{
name|Connection
name|connection
init|=
literal|null
decl_stmt|;
try|try
block|{
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
name|destination
argument_list|)
decl_stmt|;
name|connection
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
name|count
condition|;
name|i
operator|++
control|)
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
block|}
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
block|{             }
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
name|destination
argument_list|)
decl_stmt|;
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
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
literal|1000
operator|*
literal|1000
expr_stmt|;
block|}
else|else
block|{
name|sleepingTime
operator|=
literal|1
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
block|{             }
finally|finally
block|{
name|log
operator|.
name|debug
argument_list|(
name|getName
argument_list|()
operator|+
literal|": is stopping"
argument_list|)
expr_stmt|;
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

