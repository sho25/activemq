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
name|assertEquals
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertFalse
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertTrue
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
name|ExecutorService
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
name|Executors
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
name|Queue
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
specifier|public
class|class
name|AMQ6293Test
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
name|AMQ6293Test
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|BrokerService
name|brokerService
decl_stmt|;
specifier|private
name|String
name|connectionUri
decl_stmt|;
specifier|private
specifier|final
name|ExecutorService
name|service
init|=
name|Executors
operator|.
name|newFixedThreadPool
argument_list|(
literal|6
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|ActiveMQQueue
name|queue
init|=
operator|new
name|ActiveMQQueue
argument_list|(
literal|"test"
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|int
name|numMessages
init|=
literal|10000
decl_stmt|;
specifier|private
name|Connection
name|connection
decl_stmt|;
specifier|private
name|Session
name|session
decl_stmt|;
specifier|private
specifier|final
name|AtomicBoolean
name|isException
init|=
operator|new
name|AtomicBoolean
argument_list|()
decl_stmt|;
annotation|@
name|Before
specifier|public
name|void
name|before
parameter_list|()
throws|throws
name|Exception
block|{
name|brokerService
operator|=
operator|new
name|BrokerService
argument_list|()
expr_stmt|;
name|TransportConnector
name|connector
init|=
name|brokerService
operator|.
name|addConnector
argument_list|(
literal|"tcp://localhost:0"
argument_list|)
decl_stmt|;
name|connectionUri
operator|=
name|connector
operator|.
name|getPublishableConnectString
argument_list|()
expr_stmt|;
name|brokerService
operator|.
name|setPersistent
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|brokerService
operator|.
name|getManagementContext
argument_list|()
operator|.
name|setCreateConnector
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|PolicyMap
name|policyMap
init|=
operator|new
name|PolicyMap
argument_list|()
decl_stmt|;
name|PolicyEntry
name|entry
init|=
operator|new
name|PolicyEntry
argument_list|()
decl_stmt|;
name|policyMap
operator|.
name|setDefaultEntry
argument_list|(
name|entry
argument_list|)
expr_stmt|;
name|brokerService
operator|.
name|setDestinationPolicy
argument_list|(
name|policyMap
argument_list|)
expr_stmt|;
name|entry
operator|.
name|setQueuePrefetch
argument_list|(
literal|100
argument_list|)
expr_stmt|;
name|brokerService
operator|.
name|start
argument_list|()
expr_stmt|;
name|brokerService
operator|.
name|waitUntilStarted
argument_list|()
expr_stmt|;
specifier|final
name|ActiveMQConnectionFactory
name|factory
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
name|connectionUri
argument_list|)
decl_stmt|;
name|connection
operator|=
name|factory
operator|.
name|createConnection
argument_list|()
expr_stmt|;
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
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
block|}
annotation|@
name|After
specifier|public
name|void
name|after
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
name|connection
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|brokerService
operator|!=
literal|null
condition|)
block|{
name|brokerService
operator|.
name|stop
argument_list|()
expr_stmt|;
name|brokerService
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
literal|90000
argument_list|)
specifier|public
name|void
name|testDestinationStatisticsOnPurge
parameter_list|()
throws|throws
name|Exception
block|{
comment|//send messages to the store
name|sendTestMessages
argument_list|(
name|numMessages
argument_list|)
expr_stmt|;
comment|//Start up 5 consumers
specifier|final
name|Queue
name|regionQueue
init|=
operator|(
name|Queue
operator|)
name|brokerService
operator|.
name|getRegionBroker
argument_list|()
operator|.
name|getDestinationMap
argument_list|()
operator|.
name|get
argument_list|(
name|queue
argument_list|)
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
literal|5
condition|;
name|i
operator|++
control|)
block|{
name|service
operator|.
name|submit
argument_list|(
operator|new
name|TestConsumer
argument_list|(
name|session
operator|.
name|createConsumer
argument_list|(
name|queue
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|//Start a purge task at the same time as the consumers
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|1
condition|;
name|i
operator|++
control|)
block|{
name|service
operator|.
name|submit
argument_list|(
operator|new
name|Runnable
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
name|regionQueue
operator|.
name|purge
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|isException
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|warn
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
block|}
name|service
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Took too long to shutdown service"
argument_list|,
name|service
operator|.
name|awaitTermination
argument_list|(
literal|1
argument_list|,
name|TimeUnit
operator|.
name|MINUTES
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"Exception encountered"
argument_list|,
name|isException
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
comment|//Verify dequeue and message counts
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|regionQueue
operator|.
name|getDestinationStatistics
argument_list|()
operator|.
name|getMessages
argument_list|()
operator|.
name|getCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|numMessages
argument_list|,
name|regionQueue
operator|.
name|getDestinationStatistics
argument_list|()
operator|.
name|getDequeues
argument_list|()
operator|.
name|getCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|sendTestMessages
parameter_list|(
name|int
name|numMessages
parameter_list|)
throws|throws
name|JMSException
block|{
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
specifier|final
name|TextMessage
name|textMessage
init|=
name|session
operator|.
name|createTextMessage
argument_list|()
decl_stmt|;
name|textMessage
operator|.
name|setText
argument_list|(
literal|"Message"
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<=
name|numMessages
condition|;
name|i
operator|++
control|)
block|{
name|producer
operator|.
name|send
argument_list|(
name|textMessage
argument_list|)
expr_stmt|;
if|if
condition|(
name|i
operator|%
literal|1000
operator|==
literal|0
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Sent {} messages"
argument_list|,
name|i
argument_list|)
expr_stmt|;
name|session
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
block|}
name|session
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
specifier|private
class|class
name|TestConsumer
implements|implements
name|Runnable
block|{
specifier|private
specifier|final
name|MessageConsumer
name|consumer
decl_stmt|;
specifier|public
name|TestConsumer
parameter_list|(
specifier|final
name|MessageConsumer
name|consumer
parameter_list|)
throws|throws
name|JMSException
block|{
name|this
operator|.
name|consumer
operator|=
name|consumer
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|int
name|i
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|consumer
operator|.
name|receive
argument_list|(
literal|1000
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|i
operator|++
expr_stmt|;
if|if
condition|(
name|i
operator|%
literal|1000
operator|==
literal|0
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Received {} messages"
argument_list|,
name|i
argument_list|)
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
name|isException
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|warn
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
empty_stmt|;
block|}
end_class

end_unit

