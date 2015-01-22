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
name|usecases
package|;
end_package

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
name|javax
operator|.
name|jms
operator|.
name|BytesMessage
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
name|TestSupport
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
name|Destination
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
name|util
operator|.
name|ConsumerThread
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
name|ProducerThread
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
name|Wait
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
name|Ignore
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

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runner
operator|.
name|RunWith
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runners
operator|.
name|Parameterized
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

begin_class
annotation|@
name|RunWith
argument_list|(
name|value
operator|=
name|Parameterized
operator|.
name|class
argument_list|)
specifier|public
class|class
name|MemoryLimitTest
extends|extends
name|TestSupport
block|{
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
name|MemoryLimitTest
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|final
name|byte
index|[]
name|payload
init|=
operator|new
name|byte
index|[
literal|10
operator|*
literal|1024
index|]
decl_stmt|;
comment|//10KB
specifier|protected
name|BrokerService
name|broker
decl_stmt|;
annotation|@
name|Parameterized
operator|.
name|Parameter
specifier|public
name|TestSupport
operator|.
name|PersistenceAdapterChoice
name|persistenceAdapterChoice
decl_stmt|;
annotation|@
name|Parameterized
operator|.
name|Parameters
argument_list|(
name|name
operator|=
literal|"store={0}"
argument_list|)
specifier|public
specifier|static
name|Iterable
argument_list|<
name|Object
index|[]
argument_list|>
name|getTestParameters
parameter_list|()
block|{
return|return
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|Object
index|[]
index|[]
block|{
block|{
name|TestSupport
operator|.
name|PersistenceAdapterChoice
operator|.
name|KahaDB
block|}
block|,
block|{
name|PersistenceAdapterChoice
operator|.
name|LevelDB
block|}
block|,
block|{
name|PersistenceAdapterChoice
operator|.
name|JDBC
block|}
block|}
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
name|BrokerService
name|broker
init|=
operator|new
name|BrokerService
argument_list|()
decl_stmt|;
name|broker
operator|.
name|getSystemUsage
argument_list|()
operator|.
name|getMemoryUsage
argument_list|()
operator|.
name|setLimit
argument_list|(
literal|1
operator|*
literal|1024
operator|*
literal|1024
argument_list|)
expr_stmt|;
comment|//1MB
name|broker
operator|.
name|deleteAllMessages
argument_list|()
expr_stmt|;
name|PolicyMap
name|policyMap
init|=
operator|new
name|PolicyMap
argument_list|()
decl_stmt|;
name|PolicyEntry
name|policyEntry
init|=
operator|new
name|PolicyEntry
argument_list|()
decl_stmt|;
name|policyEntry
operator|.
name|setProducerFlowControl
argument_list|(
literal|false
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
name|policyEntry
argument_list|)
expr_stmt|;
name|broker
operator|.
name|setDestinationPolicy
argument_list|(
name|policyMap
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Starting broker with persistenceAdapterChoice "
operator|+
name|persistenceAdapterChoice
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|setPersistenceAdapter
argument_list|(
name|broker
argument_list|,
name|persistenceAdapterChoice
argument_list|)
expr_stmt|;
name|broker
operator|.
name|getPersistenceAdapter
argument_list|()
operator|.
name|deleteAllMessages
argument_list|()
expr_stmt|;
return|return
name|broker
return|;
block|}
annotation|@
name|Override
annotation|@
name|Before
specifier|public
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
block|}
annotation|@
name|Override
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
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|120000
argument_list|)
specifier|public
name|void
name|testCursorBatch
parameter_list|()
throws|throws
name|Exception
block|{
name|ActiveMQConnectionFactory
name|factory
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
literal|"vm://localhost?jms.prefetchPolicy.all=10"
argument_list|)
decl_stmt|;
name|factory
operator|.
name|setOptimizeAcknowledge
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|Connection
name|conn
init|=
name|factory
operator|.
name|createConnection
argument_list|()
decl_stmt|;
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
name|CLIENT_ACKNOWLEDGE
argument_list|)
decl_stmt|;
name|Queue
name|queue
init|=
name|sess
operator|.
name|createQueue
argument_list|(
literal|"STORE"
argument_list|)
decl_stmt|;
specifier|final
name|ProducerThread
name|producer
init|=
operator|new
name|ProducerThread
argument_list|(
name|sess
argument_list|,
name|queue
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|Message
name|createMessage
parameter_list|(
name|int
name|i
parameter_list|)
throws|throws
name|Exception
block|{
name|BytesMessage
name|bytesMessage
init|=
name|sess
operator|.
name|createBytesMessage
argument_list|()
decl_stmt|;
name|bytesMessage
operator|.
name|writeBytes
argument_list|(
name|payload
argument_list|)
expr_stmt|;
return|return
name|bytesMessage
return|;
block|}
block|}
decl_stmt|;
name|producer
operator|.
name|setMessageCount
argument_list|(
literal|2000
argument_list|)
expr_stmt|;
name|producer
operator|.
name|start
argument_list|()
expr_stmt|;
name|producer
operator|.
name|join
argument_list|()
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
comment|// assert we didn't break high watermark (70%) usage
specifier|final
name|Destination
name|dest
init|=
name|broker
operator|.
name|getDestination
argument_list|(
operator|(
name|ActiveMQQueue
operator|)
name|queue
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Destination usage: "
operator|+
name|dest
operator|.
name|getMemoryUsage
argument_list|()
argument_list|)
expr_stmt|;
name|int
name|percentUsage
init|=
name|dest
operator|.
name|getMemoryUsage
argument_list|()
operator|.
name|getPercentUsage
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Should be less than 70% of limit but was: "
operator|+
name|percentUsage
argument_list|,
name|percentUsage
operator|<=
literal|71
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Broker usage: "
operator|+
name|broker
operator|.
name|getSystemUsage
argument_list|()
operator|.
name|getMemoryUsage
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|broker
operator|.
name|getSystemUsage
argument_list|()
operator|.
name|getMemoryUsage
argument_list|()
operator|.
name|getPercentUsage
argument_list|()
operator|<=
literal|71
argument_list|)
expr_stmt|;
comment|// consume one message
name|MessageConsumer
name|consumer
init|=
name|sess
operator|.
name|createConsumer
argument_list|(
name|queue
argument_list|)
decl_stmt|;
name|Message
name|msg
init|=
name|consumer
operator|.
name|receive
argument_list|(
literal|5000
argument_list|)
decl_stmt|;
name|msg
operator|.
name|acknowledge
argument_list|()
expr_stmt|;
comment|// this should free some space and allow us to get new batch of messages in the memory
comment|// exceeding the limit
name|assertTrue
argument_list|(
literal|"Limit is exceeded"
argument_list|,
name|Wait
operator|.
name|waitFor
argument_list|(
operator|new
name|Wait
operator|.
name|Condition
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|isSatisified
parameter_list|()
throws|throws
name|Exception
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Destination usage: "
operator|+
name|dest
operator|.
name|getMemoryUsage
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|dest
operator|.
name|getMemoryUsage
argument_list|()
operator|.
name|getPercentUsage
argument_list|()
operator|>=
literal|200
return|;
block|}
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Broker usage: "
operator|+
name|broker
operator|.
name|getSystemUsage
argument_list|()
operator|.
name|getMemoryUsage
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|broker
operator|.
name|getSystemUsage
argument_list|()
operator|.
name|getMemoryUsage
argument_list|()
operator|.
name|getPercentUsage
argument_list|()
operator|>=
literal|200
argument_list|)
expr_stmt|;
comment|// let's make sure we can consume all messages
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
literal|2000
condition|;
name|i
operator|++
control|)
block|{
name|msg
operator|=
name|consumer
operator|.
name|receive
argument_list|(
literal|5000
argument_list|)
expr_stmt|;
if|if
condition|(
name|msg
operator|==
literal|null
condition|)
block|{
name|dumpAllThreads
argument_list|(
literal|"NoMessage"
argument_list|)
expr_stmt|;
block|}
name|assertNotNull
argument_list|(
literal|"Didn't receive message "
operator|+
name|i
argument_list|,
name|msg
argument_list|)
expr_stmt|;
name|msg
operator|.
name|acknowledge
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**      * Handy test for manually checking what's going on      */
annotation|@
name|Ignore
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|120000
argument_list|)
specifier|public
name|void
name|testLimit
parameter_list|()
throws|throws
name|Exception
block|{
name|ActiveMQConnectionFactory
name|factory
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
literal|"vm://localhost?jms.prefetchPolicy.all=10"
argument_list|)
decl_stmt|;
name|factory
operator|.
name|setOptimizeAcknowledge
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|Connection
name|conn
init|=
name|factory
operator|.
name|createConnection
argument_list|()
decl_stmt|;
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
name|ProducerThread
name|producer
init|=
operator|new
name|ProducerThread
argument_list|(
name|sess
argument_list|,
name|sess
operator|.
name|createQueue
argument_list|(
literal|"STORE.1"
argument_list|)
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|Message
name|createMessage
parameter_list|(
name|int
name|i
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|sess
operator|.
name|createTextMessage
argument_list|(
name|payload
operator|+
literal|"::"
operator|+
name|i
argument_list|)
return|;
block|}
block|}
decl_stmt|;
name|producer
operator|.
name|setMessageCount
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
specifier|final
name|ProducerThread
name|producer2
init|=
operator|new
name|ProducerThread
argument_list|(
name|sess
argument_list|,
name|sess
operator|.
name|createQueue
argument_list|(
literal|"STORE.2"
argument_list|)
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|Message
name|createMessage
parameter_list|(
name|int
name|i
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|sess
operator|.
name|createTextMessage
argument_list|(
name|payload
operator|+
literal|"::"
operator|+
name|i
argument_list|)
return|;
block|}
block|}
decl_stmt|;
name|producer2
operator|.
name|setMessageCount
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
name|ConsumerThread
name|consumer
init|=
operator|new
name|ConsumerThread
argument_list|(
name|sess
argument_list|,
name|sess
operator|.
name|createQueue
argument_list|(
literal|"STORE.1"
argument_list|)
argument_list|)
decl_stmt|;
name|consumer
operator|.
name|setBreakOnNull
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|consumer
operator|.
name|setMessageCount
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
name|producer
operator|.
name|start
argument_list|()
expr_stmt|;
name|producer
operator|.
name|join
argument_list|()
expr_stmt|;
name|producer2
operator|.
name|start
argument_list|()
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|300
argument_list|)
expr_stmt|;
name|consumer
operator|.
name|start
argument_list|()
expr_stmt|;
name|consumer
operator|.
name|join
argument_list|()
expr_stmt|;
name|producer2
operator|.
name|join
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"consumer got all produced messages"
argument_list|,
name|producer
operator|.
name|getMessageCount
argument_list|()
argument_list|,
name|consumer
operator|.
name|getReceived
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

