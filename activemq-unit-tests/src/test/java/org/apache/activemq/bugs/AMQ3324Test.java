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
name|TemporaryQueue
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|Topic
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
name|AdvisorySupport
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
name|MirroredQueue
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
name|AMQ3324Test
block|{
specifier|private
specifier|static
specifier|final
specifier|transient
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|AMQ3324Test
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|bindAddress
init|=
literal|"tcp://0.0.0.0:0"
decl_stmt|;
specifier|private
name|BrokerService
name|broker
decl_stmt|;
specifier|private
name|ActiveMQConnectionFactory
name|cf
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|MESSAGE_COUNT
init|=
literal|100
decl_stmt|;
annotation|@
name|Before
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|broker
operator|=
name|this
operator|.
name|createBroker
argument_list|()
expr_stmt|;
name|String
name|address
init|=
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
decl_stmt|;
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
name|cf
operator|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
name|address
argument_list|)
expr_stmt|;
block|}
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
specifier|public
name|void
name|testTempMessageConsumedAdvisoryConnectionClose
parameter_list|()
throws|throws
name|Exception
block|{
name|Connection
name|connection
init|=
name|cf
operator|.
name|createConnection
argument_list|()
decl_stmt|;
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
specifier|final
name|TemporaryQueue
name|queue
init|=
name|session
operator|.
name|createTemporaryQueue
argument_list|()
decl_stmt|;
name|MessageConsumer
name|consumer
init|=
name|session
operator|.
name|createConsumer
argument_list|(
name|queue
argument_list|)
decl_stmt|;
specifier|final
name|Topic
name|advisoryTopic
init|=
name|AdvisorySupport
operator|.
name|getMessageConsumedAdvisoryTopic
argument_list|(
operator|(
name|ActiveMQDestination
operator|)
name|queue
argument_list|)
decl_stmt|;
name|MessageConsumer
name|advisoryConsumer
init|=
name|session
operator|.
name|createConsumer
argument_list|(
name|advisoryTopic
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
comment|// send lots of messages to the tempQueue
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
name|BytesMessage
name|m
init|=
name|session
operator|.
name|createBytesMessage
argument_list|()
decl_stmt|;
name|m
operator|.
name|writeBytes
argument_list|(
operator|new
name|byte
index|[
literal|1024
index|]
argument_list|)
expr_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|m
argument_list|)
expr_stmt|;
block|}
comment|// consume one message from tempQueue
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
name|assertNotNull
argument_list|(
name|msg
argument_list|)
expr_stmt|;
comment|// check one advisory message has produced on the advisoryTopic
name|Message
name|advCmsg
init|=
name|advisoryConsumer
operator|.
name|receive
argument_list|(
literal|5000
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|advCmsg
argument_list|)
expr_stmt|;
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Connection closed, destinations should now become inactive."
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"The destination "
operator|+
name|advisoryTopic
operator|+
literal|"was not removed. "
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
name|broker
operator|.
name|getAdminView
argument_list|()
operator|.
name|getTopics
argument_list|()
operator|.
name|length
operator|==
literal|0
return|;
block|}
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"The destination "
operator|+
name|queue
operator|+
literal|" was not removed. "
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
name|broker
operator|.
name|getAdminView
argument_list|()
operator|.
name|getTemporaryQueues
argument_list|()
operator|.
name|length
operator|==
literal|0
return|;
block|}
block|}
argument_list|)
argument_list|)
expr_stmt|;
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
name|answer
operator|.
name|setUseMirroredQueues
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|answer
operator|.
name|setPersistent
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|answer
operator|.
name|setSchedulePeriodForDestinationPurge
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
name|PolicyEntry
name|entry
init|=
operator|new
name|PolicyEntry
argument_list|()
decl_stmt|;
name|entry
operator|.
name|setGcInactiveDestinations
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|entry
operator|.
name|setInactiveTimoutBeforeGC
argument_list|(
literal|2000
argument_list|)
expr_stmt|;
name|entry
operator|.
name|setProducerFlowControl
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|entry
operator|.
name|setAdvisoryForConsumed
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|entry
operator|.
name|setAdvisoryForFastProducers
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|entry
operator|.
name|setAdvisoryForDelivery
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|PolicyMap
name|map
init|=
operator|new
name|PolicyMap
argument_list|()
decl_stmt|;
name|map
operator|.
name|setDefaultEntry
argument_list|(
name|entry
argument_list|)
expr_stmt|;
name|MirroredQueue
name|mirrorQ
init|=
operator|new
name|MirroredQueue
argument_list|()
decl_stmt|;
name|mirrorQ
operator|.
name|setCopyMessage
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|DestinationInterceptor
index|[]
name|destinationInterceptors
init|=
operator|new
name|DestinationInterceptor
index|[]
block|{
name|mirrorQ
block|}
decl_stmt|;
name|answer
operator|.
name|setDestinationInterceptors
argument_list|(
name|destinationInterceptors
argument_list|)
expr_stmt|;
name|answer
operator|.
name|setDestinationPolicy
argument_list|(
name|map
argument_list|)
expr_stmt|;
name|answer
operator|.
name|addConnector
argument_list|(
name|bindAddress
argument_list|)
expr_stmt|;
return|return
name|answer
return|;
block|}
block|}
end_class

end_unit
