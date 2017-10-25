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
name|virtual
package|;
end_package

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
name|DestinationInterceptor
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
name|broker
operator|.
name|region
operator|.
name|virtual
operator|.
name|VirtualDestination
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
name|virtual
operator|.
name|VirtualDestinationInterceptor
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
name|virtual
operator|.
name|VirtualTopic
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
name|java
operator|.
name|util
operator|.
name|Arrays
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

begin_class
annotation|@
name|RunWith
argument_list|(
name|Parameterized
operator|.
name|class
argument_list|)
specifier|public
class|class
name|VirtualTopicFlowControlDiscardTest
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
name|VirtualTopicFlowControlDiscardTest
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|final
name|String
name|payload
init|=
operator|new
name|String
argument_list|(
operator|new
name|byte
index|[
literal|155
index|]
argument_list|)
decl_stmt|;
name|int
name|numConsumers
init|=
literal|2
decl_stmt|;
name|int
name|total
init|=
literal|500
decl_stmt|;
annotation|@
name|Parameterized
operator|.
name|Parameter
argument_list|(
literal|0
argument_list|)
specifier|public
name|boolean
name|concurrentSend
decl_stmt|;
annotation|@
name|Parameterized
operator|.
name|Parameter
argument_list|(
literal|1
argument_list|)
specifier|public
name|boolean
name|transactedSend
decl_stmt|;
annotation|@
name|Parameterized
operator|.
name|Parameter
argument_list|(
literal|2
argument_list|)
specifier|public
name|boolean
name|sendFailGlobal
decl_stmt|;
annotation|@
name|Parameterized
operator|.
name|Parameter
argument_list|(
literal|3
argument_list|)
specifier|public
name|boolean
name|persistentBroker
decl_stmt|;
name|BrokerService
name|brokerService
decl_stmt|;
name|ConnectionFactory
name|connectionFactory
decl_stmt|;
annotation|@
name|Before
specifier|public
name|void
name|createBroker
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
name|brokerService
operator|.
name|setPersistent
argument_list|(
name|persistentBroker
argument_list|)
expr_stmt|;
name|brokerService
operator|.
name|setDeleteAllMessagesOnStartup
argument_list|(
literal|true
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
name|restrictedUsage
init|=
operator|new
name|PolicyEntry
argument_list|()
decl_stmt|;
name|restrictedUsage
operator|.
name|setCursorMemoryHighWaterMark
argument_list|(
literal|50
argument_list|)
expr_stmt|;
name|restrictedUsage
operator|.
name|setMemoryLimit
argument_list|(
literal|5000
argument_list|)
expr_stmt|;
name|restrictedUsage
operator|.
name|setCursorMemoryHighWaterMark
argument_list|(
literal|110
argument_list|)
expr_stmt|;
if|if
condition|(
name|sendFailGlobal
condition|)
block|{
name|brokerService
operator|.
name|getSystemUsage
argument_list|()
operator|.
name|setSendFailIfNoSpace
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|restrictedUsage
operator|.
name|setSendFailIfNoSpace
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|restrictedUsage
operator|.
name|setSendFailIfNoSpaceAfterTimeout
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
name|policyMap
operator|.
name|put
argument_list|(
operator|new
name|ActiveMQQueue
argument_list|(
literal|"Consumer.0.VirtualTopic.TEST"
argument_list|)
argument_list|,
name|restrictedUsage
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
name|start
argument_list|()
expr_stmt|;
for|for
control|(
name|DestinationInterceptor
name|destinationInterceptor
range|:
name|brokerService
operator|.
name|getDestinationInterceptors
argument_list|()
control|)
block|{
for|for
control|(
name|VirtualDestination
name|virtualDestination
range|:
operator|(
operator|(
name|VirtualDestinationInterceptor
operator|)
name|destinationInterceptor
operator|)
operator|.
name|getVirtualDestinations
argument_list|()
control|)
block|{
if|if
condition|(
name|virtualDestination
operator|instanceof
name|VirtualTopic
condition|)
block|{
operator|(
operator|(
name|VirtualTopic
operator|)
name|virtualDestination
operator|)
operator|.
name|setConcurrentSend
argument_list|(
name|concurrentSend
argument_list|)
expr_stmt|;
operator|(
operator|(
name|VirtualTopic
operator|)
name|virtualDestination
operator|)
operator|.
name|setTransactedSend
argument_list|(
name|transactedSend
argument_list|)
expr_stmt|;
operator|(
operator|(
name|VirtualTopic
operator|)
name|virtualDestination
operator|)
operator|.
name|setDropOnResourceLimit
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|ActiveMQConnectionFactory
name|activeMQConnectionFactory
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
literal|"vm://localhost"
argument_list|)
decl_stmt|;
name|ActiveMQPrefetchPolicy
name|zeroPrefetch
init|=
operator|new
name|ActiveMQPrefetchPolicy
argument_list|()
decl_stmt|;
name|zeroPrefetch
operator|.
name|setAll
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|activeMQConnectionFactory
operator|.
name|setPrefetchPolicy
argument_list|(
name|zeroPrefetch
argument_list|)
expr_stmt|;
name|connectionFactory
operator|=
name|activeMQConnectionFactory
expr_stmt|;
block|}
annotation|@
name|After
specifier|public
name|void
name|stopBroker
parameter_list|()
throws|throws
name|Exception
block|{
name|brokerService
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Parameterized
operator|.
name|Parameters
argument_list|(
name|name
operator|=
literal|"cS=#{0},tS=#{1},g=#{2},persist=#{3}"
argument_list|)
specifier|public
specifier|static
name|Iterable
argument_list|<
name|Object
index|[]
argument_list|>
name|parameters
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
name|Boolean
operator|.
name|TRUE
block|,
name|Boolean
operator|.
name|TRUE
block|,
name|Boolean
operator|.
name|TRUE
block|,
name|Boolean
operator|.
name|TRUE
block|}
block|,
block|{
name|Boolean
operator|.
name|TRUE
block|,
name|Boolean
operator|.
name|TRUE
block|,
name|Boolean
operator|.
name|TRUE
block|,
name|Boolean
operator|.
name|FALSE
block|}
block|,
block|{
name|Boolean
operator|.
name|FALSE
block|,
name|Boolean
operator|.
name|TRUE
block|,
name|Boolean
operator|.
name|TRUE
block|,
name|Boolean
operator|.
name|FALSE
block|}
block|,
block|{
name|Boolean
operator|.
name|TRUE
block|,
name|Boolean
operator|.
name|FALSE
block|,
name|Boolean
operator|.
name|TRUE
block|,
name|Boolean
operator|.
name|FALSE
block|}
block|,
block|{
name|Boolean
operator|.
name|FALSE
block|,
name|Boolean
operator|.
name|FALSE
block|,
name|Boolean
operator|.
name|TRUE
block|,
name|Boolean
operator|.
name|FALSE
block|}
block|,
block|{
name|Boolean
operator|.
name|FALSE
block|,
name|Boolean
operator|.
name|FALSE
block|,
name|Boolean
operator|.
name|FALSE
block|,
name|Boolean
operator|.
name|FALSE
block|}
block|,
block|{
name|Boolean
operator|.
name|TRUE
block|,
name|Boolean
operator|.
name|TRUE
block|,
name|Boolean
operator|.
name|FALSE
block|,
name|Boolean
operator|.
name|FALSE
block|}
block|,
block|{
name|Boolean
operator|.
name|FALSE
block|,
name|Boolean
operator|.
name|FALSE
block|,
name|Boolean
operator|.
name|FALSE
block|,
name|Boolean
operator|.
name|TRUE
block|}
block|,          }
argument_list|)
return|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testFanoutWithResourceException
parameter_list|()
throws|throws
name|Exception
block|{
name|Connection
name|connection1
init|=
name|connectionFactory
operator|.
name|createConnection
argument_list|()
decl_stmt|;
name|connection1
operator|.
name|start
argument_list|()
expr_stmt|;
name|Session
name|session
init|=
name|connection1
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
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numConsumers
condition|;
name|i
operator|++
control|)
block|{
name|session
operator|.
name|createConsumer
argument_list|(
operator|new
name|ActiveMQQueue
argument_list|(
literal|"Consumer."
operator|+
name|i
operator|+
literal|".VirtualTopic.TEST"
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|Connection
name|connection2
init|=
name|connectionFactory
operator|.
name|createConnection
argument_list|()
decl_stmt|;
name|connection2
operator|.
name|start
argument_list|()
expr_stmt|;
name|Session
name|producerSession
init|=
name|connection2
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
name|producerSession
operator|.
name|createProducer
argument_list|(
operator|new
name|ActiveMQTopic
argument_list|(
literal|"VirtualTopic.TEST"
argument_list|)
argument_list|)
decl_stmt|;
name|long
name|start
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Starting producer: "
operator|+
name|start
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
name|total
condition|;
name|i
operator|++
control|)
block|{
name|producer
operator|.
name|send
argument_list|(
name|producerSession
operator|.
name|createTextMessage
argument_list|(
name|payload
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Done producer, duration: "
operator|+
operator|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|start
operator|)
argument_list|)
expr_stmt|;
name|Destination
name|destination
init|=
name|brokerService
operator|.
name|getDestination
argument_list|(
operator|new
name|ActiveMQQueue
argument_list|(
literal|"Consumer.0.VirtualTopic.TEST"
argument_list|)
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Dest 0 size: "
operator|+
operator|(
name|destination
operator|.
name|getDestinationStatistics
argument_list|()
operator|.
name|getEnqueues
argument_list|()
operator|.
name|getCount
argument_list|()
operator|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"did not get all"
argument_list|,
operator|(
name|destination
operator|.
name|getDestinationStatistics
argument_list|()
operator|.
name|getEnqueues
argument_list|()
operator|.
name|getCount
argument_list|()
operator|<
name|total
operator|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"got all"
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
name|Destination
name|dest
init|=
name|brokerService
operator|.
name|getDestination
argument_list|(
operator|new
name|ActiveMQQueue
argument_list|(
literal|"Consumer.1.VirtualTopic.TEST"
argument_list|)
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Dest 1 size: "
operator|+
name|dest
operator|.
name|getDestinationStatistics
argument_list|()
operator|.
name|getEnqueues
argument_list|()
operator|.
name|getCount
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|total
operator|==
name|dest
operator|.
name|getDestinationStatistics
argument_list|()
operator|.
name|getEnqueues
argument_list|()
operator|.
name|getCount
argument_list|()
return|;
block|}
block|}
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
name|connection1
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
name|connection2
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
block|}
block|}
end_class

end_unit

