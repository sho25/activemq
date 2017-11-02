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
name|apache
operator|.
name|activemq
operator|.
name|usage
operator|.
name|SystemUsage
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
name|DefaultTestAppender
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|log4j
operator|.
name|Appender
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|log4j
operator|.
name|Level
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|log4j
operator|.
name|spi
operator|.
name|LoggingEvent
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|*
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
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
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicBoolean
import|;
end_import

begin_class
specifier|public
class|class
name|UsageBlockedDispatchTest
extends|extends
name|TestSupport
block|{
specifier|private
specifier|static
specifier|final
name|int
name|MESSAGES_COUNT
init|=
literal|100
decl_stmt|;
specifier|private
specifier|static
name|byte
index|[]
name|buf
init|=
operator|new
name|byte
index|[
literal|2
operator|*
literal|1024
index|]
decl_stmt|;
specifier|private
name|BrokerService
name|broker
decl_stmt|;
specifier|protected
name|long
name|messageReceiveTimeout
init|=
literal|4000L
decl_stmt|;
specifier|private
name|String
name|connectionUri
decl_stmt|;
annotation|@
name|Override
specifier|public
name|void
name|setUp
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
name|setDataDirectory
argument_list|(
literal|"target"
operator|+
name|File
operator|.
name|separator
operator|+
literal|"activemq-data"
argument_list|)
expr_stmt|;
name|broker
operator|.
name|setPersistent
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|broker
operator|.
name|setUseJmx
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|broker
operator|.
name|setAdvisorySupport
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|broker
operator|.
name|setDeleteAllMessagesOnStartup
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|setDefaultPersistenceAdapter
argument_list|(
name|broker
argument_list|)
expr_stmt|;
name|SystemUsage
name|sysUsage
init|=
name|broker
operator|.
name|getSystemUsage
argument_list|()
decl_stmt|;
name|sysUsage
operator|.
name|getMemoryUsage
argument_list|()
operator|.
name|setLimit
argument_list|(
literal|100
operator|*
literal|1024
argument_list|)
expr_stmt|;
name|PolicyEntry
name|defaultPolicy
init|=
operator|new
name|PolicyEntry
argument_list|()
decl_stmt|;
name|defaultPolicy
operator|.
name|setProducerFlowControl
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|defaultPolicy
operator|.
name|setCursorMemoryHighWaterMark
argument_list|(
literal|100
argument_list|)
expr_stmt|;
name|defaultPolicy
operator|.
name|setMemoryLimit
argument_list|(
literal|50
operator|*
literal|1024
argument_list|)
expr_stmt|;
name|PolicyMap
name|policyMap
init|=
operator|new
name|PolicyMap
argument_list|()
decl_stmt|;
name|policyMap
operator|.
name|setDefaultEntry
argument_list|(
name|defaultPolicy
argument_list|)
expr_stmt|;
name|broker
operator|.
name|setDestinationPolicy
argument_list|(
name|policyMap
argument_list|)
expr_stmt|;
name|broker
operator|.
name|setSystemUsage
argument_list|(
name|sysUsage
argument_list|)
expr_stmt|;
name|broker
operator|.
name|addConnector
argument_list|(
literal|"tcp://localhost:0"
argument_list|)
operator|.
name|setName
argument_list|(
literal|"Default"
argument_list|)
expr_stmt|;
name|broker
operator|.
name|start
argument_list|()
expr_stmt|;
name|connectionUri
operator|=
name|broker
operator|.
name|getTransportConnectors
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getPublishableConnectString
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
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
block|}
block|}
specifier|public
name|void
name|testFillMemToBlockConsumer
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
name|connectionUri
argument_list|)
decl_stmt|;
name|ActiveMQPrefetchPolicy
name|prefetch
init|=
operator|new
name|ActiveMQPrefetchPolicy
argument_list|()
decl_stmt|;
name|prefetch
operator|.
name|setTopicPrefetch
argument_list|(
literal|10
argument_list|)
expr_stmt|;
name|factory
operator|.
name|setPrefetchPolicy
argument_list|(
name|prefetch
argument_list|)
expr_stmt|;
specifier|final
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
name|producerSession
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
name|producerSession
operator|.
name|createProducer
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|BytesMessage
name|message
init|=
name|producerSession
operator|.
name|createBytesMessage
argument_list|()
decl_stmt|;
name|message
operator|.
name|writeBytes
argument_list|(
name|buf
argument_list|)
expr_stmt|;
name|int
name|numFillers
init|=
literal|4
decl_stmt|;
name|ArrayList
argument_list|<
name|ActiveMQQueue
argument_list|>
name|fillers
init|=
operator|new
name|ArrayList
argument_list|<
name|ActiveMQQueue
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
name|numFillers
condition|;
name|i
operator|++
control|)
block|{
name|fillers
operator|.
name|add
argument_list|(
operator|new
name|ActiveMQQueue
argument_list|(
literal|"Q"
operator|+
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// fill cache and consume all memory
for|for
control|(
name|int
name|idx
init|=
literal|0
init|;
name|idx
operator|<
name|MESSAGES_COUNT
condition|;
operator|++
name|idx
control|)
block|{
for|for
control|(
name|ActiveMQQueue
name|q
range|:
name|fillers
control|)
block|{
name|producer
operator|.
name|send
argument_list|(
name|q
argument_list|,
name|message
argument_list|)
expr_stmt|;
block|}
block|}
name|ActiveMQQueue
name|willGetAPage
init|=
operator|new
name|ActiveMQQueue
argument_list|(
literal|"Q"
operator|+
name|numFillers
operator|++
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|idx
init|=
literal|0
init|;
name|idx
operator|<
name|MESSAGES_COUNT
condition|;
operator|++
name|idx
control|)
block|{
name|producer
operator|.
name|send
argument_list|(
name|willGetAPage
argument_list|,
name|message
argument_list|)
expr_stmt|;
block|}
name|ActiveMQQueue
name|shouldBeStuckForDispatch
init|=
operator|new
name|ActiveMQQueue
argument_list|(
literal|"Q"
operator|+
name|numFillers
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|idx
init|=
literal|0
init|;
name|idx
operator|<
name|MESSAGES_COUNT
condition|;
operator|++
name|idx
control|)
block|{
name|producer
operator|.
name|send
argument_list|(
name|shouldBeStuckForDispatch
argument_list|,
name|message
argument_list|)
expr_stmt|;
block|}
name|Connection
name|consumerConnection
init|=
name|factory
operator|.
name|createConnection
argument_list|()
decl_stmt|;
name|consumerConnection
operator|.
name|start
argument_list|()
expr_stmt|;
name|Session
name|consumerSession
init|=
name|consumerConnection
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
name|consumerSession
operator|.
name|createConsumer
argument_list|(
name|willGetAPage
argument_list|)
decl_stmt|;
name|Message
name|m
init|=
name|consumer
operator|.
name|receive
argument_list|(
name|messageReceiveTimeout
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"got a message"
argument_list|,
name|m
argument_list|)
expr_stmt|;
specifier|final
name|AtomicBoolean
name|gotExpectedLogEvent
init|=
operator|new
name|AtomicBoolean
argument_list|(
literal|false
argument_list|)
decl_stmt|;
name|Appender
name|appender
init|=
operator|new
name|DefaultTestAppender
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|doAppend
parameter_list|(
name|LoggingEvent
name|event
parameter_list|)
block|{
if|if
condition|(
name|event
operator|.
name|getLevel
argument_list|()
operator|==
name|Level
operator|.
name|WARN
operator|&&
name|event
operator|.
name|getRenderedMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"cursor blocked"
argument_list|)
condition|)
block|{
name|gotExpectedLogEvent
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
block|}
decl_stmt|;
try|try
block|{
name|org
operator|.
name|apache
operator|.
name|log4j
operator|.
name|Logger
operator|.
name|getLogger
argument_list|(
name|Queue
operator|.
name|class
argument_list|)
operator|.
name|addAppender
argument_list|(
name|appender
argument_list|)
expr_stmt|;
name|MessageConsumer
name|noDispatchConsumer
init|=
name|consumerSession
operator|.
name|createConsumer
argument_list|(
name|shouldBeStuckForDispatch
argument_list|)
decl_stmt|;
name|m
operator|=
name|noDispatchConsumer
operator|.
name|receive
argument_list|(
name|messageReceiveTimeout
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
literal|"did not get a message"
argument_list|,
name|m
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Got the new warning about the blocked cursor"
argument_list|,
name|gotExpectedLogEvent
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|org
operator|.
name|apache
operator|.
name|log4j
operator|.
name|Logger
operator|.
name|getLogger
argument_list|(
name|Queue
operator|.
name|class
argument_list|)
operator|.
name|removeAppender
argument_list|(
name|appender
argument_list|)
expr_stmt|;
name|org
operator|.
name|apache
operator|.
name|log4j
operator|.
name|Logger
operator|.
name|getRootLogger
argument_list|()
operator|.
name|removeAppender
argument_list|(
name|appender
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

