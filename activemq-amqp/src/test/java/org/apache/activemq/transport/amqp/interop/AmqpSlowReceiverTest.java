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
name|transport
operator|.
name|amqp
operator|.
name|interop
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
name|assertNotNull
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
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|fail
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|UndeclaredThrowableException
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
name|management
operator|.
name|InstanceNotFoundException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|ObjectName
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|openmbean
operator|.
name|CompositeData
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|openmbean
operator|.
name|TabularData
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
name|jmx
operator|.
name|AbortSlowConsumerStrategyViewMBean
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
name|jmx
operator|.
name|QueueViewMBean
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
name|AbortSlowConsumerStrategy
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
name|transport
operator|.
name|amqp
operator|.
name|client
operator|.
name|AmqpClient
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
name|amqp
operator|.
name|client
operator|.
name|AmqpClientTestSupport
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
name|amqp
operator|.
name|client
operator|.
name|AmqpConnection
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
name|amqp
operator|.
name|client
operator|.
name|AmqpMessage
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
name|amqp
operator|.
name|client
operator|.
name|AmqpReceiver
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
name|amqp
operator|.
name|client
operator|.
name|AmqpSession
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
name|Test
import|;
end_import

begin_comment
comment|/**  * Test the handling of consumer abort when the AbortSlowConsumerStrategy is used.  */
end_comment

begin_class
specifier|public
class|class
name|AmqpSlowReceiverTest
extends|extends
name|AmqpClientTestSupport
block|{
specifier|private
specifier|final
name|long
name|DEFAULT_CHECK_PERIOD
init|=
literal|1000
decl_stmt|;
specifier|private
specifier|final
name|long
name|DEFAULT_MAX_SLOW_DURATION
init|=
literal|3000
decl_stmt|;
specifier|private
name|AbortSlowConsumerStrategy
name|strategy
decl_stmt|;
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60
operator|*
literal|1000
argument_list|)
specifier|public
name|void
name|testSlowConsumerIsAborted
parameter_list|()
throws|throws
name|Exception
block|{
name|AmqpClient
name|client
init|=
name|createAmqpClient
argument_list|()
decl_stmt|;
name|AmqpConnection
name|connection
init|=
name|client
operator|.
name|connect
argument_list|()
decl_stmt|;
name|AmqpSession
name|session
init|=
name|connection
operator|.
name|createSession
argument_list|()
decl_stmt|;
specifier|final
name|AmqpReceiver
name|receiver
init|=
name|session
operator|.
name|createReceiver
argument_list|(
literal|"queue://"
operator|+
name|getTestName
argument_list|()
argument_list|)
decl_stmt|;
name|receiver
operator|.
name|flow
argument_list|(
literal|100
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|brokerService
operator|.
name|getAdminView
argument_list|()
operator|.
name|getQueueSubscribers
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
name|sendMessages
argument_list|(
name|getTestName
argument_list|()
argument_list|,
literal|100
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|AmqpMessage
name|message
init|=
name|receiver
operator|.
name|receive
argument_list|(
literal|5
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|message
operator|.
name|accept
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Receiver should be closed"
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
return|return
name|receiver
operator|.
name|isClosed
argument_list|()
return|;
block|}
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|brokerService
operator|.
name|getAdminView
argument_list|()
operator|.
name|getQueueSubscribers
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSlowConsumerIsAbortedViaJmx
parameter_list|()
throws|throws
name|Exception
block|{
name|strategy
operator|.
name|setMaxSlowDuration
argument_list|(
literal|60
operator|*
literal|1000
argument_list|)
expr_stmt|;
comment|// so jmx does the abort
name|AmqpClient
name|client
init|=
name|createAmqpClient
argument_list|()
decl_stmt|;
name|AmqpConnection
name|connection
init|=
name|client
operator|.
name|connect
argument_list|()
decl_stmt|;
name|AmqpSession
name|session
init|=
name|connection
operator|.
name|createSession
argument_list|()
decl_stmt|;
specifier|final
name|AmqpReceiver
name|receiver
init|=
name|session
operator|.
name|createReceiver
argument_list|(
literal|"queue://"
operator|+
name|getTestName
argument_list|()
argument_list|)
decl_stmt|;
name|receiver
operator|.
name|flow
argument_list|(
literal|100
argument_list|)
expr_stmt|;
name|sendMessages
argument_list|(
name|getTestName
argument_list|()
argument_list|,
literal|100
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|AmqpMessage
name|message
init|=
name|receiver
operator|.
name|receive
argument_list|(
literal|5
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|message
operator|.
name|accept
argument_list|()
expr_stmt|;
name|QueueViewMBean
name|queue
init|=
name|getProxyToQueue
argument_list|(
name|getTestName
argument_list|()
argument_list|)
decl_stmt|;
name|ObjectName
name|slowConsumerPolicyMBeanName
init|=
name|queue
operator|.
name|getSlowConsumerStrategy
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|slowConsumerPolicyMBeanName
argument_list|)
expr_stmt|;
name|AbortSlowConsumerStrategyViewMBean
name|abortPolicy
init|=
operator|(
name|AbortSlowConsumerStrategyViewMBean
operator|)
name|brokerService
operator|.
name|getManagementContext
argument_list|()
operator|.
name|newProxyInstance
argument_list|(
name|slowConsumerPolicyMBeanName
argument_list|,
name|AbortSlowConsumerStrategyViewMBean
operator|.
name|class
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|TimeUnit
operator|.
name|SECONDS
operator|.
name|sleep
argument_list|(
literal|3
argument_list|)
expr_stmt|;
name|TabularData
name|slowOnes
init|=
name|abortPolicy
operator|.
name|getSlowConsumers
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"one slow consumers"
argument_list|,
literal|1
argument_list|,
name|slowOnes
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"slow ones:"
operator|+
name|slowOnes
argument_list|)
expr_stmt|;
name|CompositeData
name|slowOne
init|=
operator|(
name|CompositeData
operator|)
name|slowOnes
operator|.
name|values
argument_list|()
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Slow one: "
operator|+
name|slowOne
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"we have an object name"
argument_list|,
name|slowOne
operator|.
name|get
argument_list|(
literal|"subscription"
argument_list|)
operator|instanceof
name|ObjectName
argument_list|)
expr_stmt|;
name|abortPolicy
operator|.
name|abortConsumer
argument_list|(
operator|(
name|ObjectName
operator|)
name|slowOne
operator|.
name|get
argument_list|(
literal|"subscription"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Receiver should be closed"
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
return|return
name|receiver
operator|.
name|isClosed
argument_list|()
return|;
block|}
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|slowOnes
operator|=
name|abortPolicy
operator|.
name|getSlowConsumers
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"no slow consumers left"
argument_list|,
literal|0
argument_list|,
name|slowOnes
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// verify mbean gone with destination
name|brokerService
operator|.
name|getAdminView
argument_list|()
operator|.
name|removeQueue
argument_list|(
name|getTestName
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
name|abortPolicy
operator|.
name|getSlowConsumers
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"expect not found post destination removal"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UndeclaredThrowableException
name|expected
parameter_list|)
block|{
name|assertTrue
argument_list|(
literal|"correct exception: "
operator|+
name|expected
operator|.
name|getCause
argument_list|()
argument_list|,
name|expected
operator|.
name|getCause
argument_list|()
operator|instanceof
name|InstanceNotFoundException
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|protected
name|boolean
name|isUseOpenWireConnector
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|performAdditionalConfiguration
parameter_list|(
name|BrokerService
name|brokerService
parameter_list|)
throws|throws
name|Exception
block|{
name|strategy
operator|=
operator|new
name|AbortSlowConsumerStrategy
argument_list|()
expr_stmt|;
name|strategy
operator|.
name|setAbortConnection
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|strategy
operator|.
name|setCheckPeriod
argument_list|(
name|DEFAULT_CHECK_PERIOD
argument_list|)
expr_stmt|;
name|strategy
operator|.
name|setMaxSlowDuration
argument_list|(
name|DEFAULT_MAX_SLOW_DURATION
argument_list|)
expr_stmt|;
name|PolicyEntry
name|policy
init|=
operator|new
name|PolicyEntry
argument_list|()
decl_stmt|;
name|policy
operator|.
name|setSlowConsumerStrategy
argument_list|(
name|strategy
argument_list|)
expr_stmt|;
name|policy
operator|.
name|setQueuePrefetch
argument_list|(
literal|10
argument_list|)
expr_stmt|;
name|policy
operator|.
name|setTopicPrefetch
argument_list|(
literal|10
argument_list|)
expr_stmt|;
name|PolicyMap
name|pMap
init|=
operator|new
name|PolicyMap
argument_list|()
decl_stmt|;
name|pMap
operator|.
name|setDefaultEntry
argument_list|(
name|policy
argument_list|)
expr_stmt|;
name|brokerService
operator|.
name|setDestinationPolicy
argument_list|(
name|pMap
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit
