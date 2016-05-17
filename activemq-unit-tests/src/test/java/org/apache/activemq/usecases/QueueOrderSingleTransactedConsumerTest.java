begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *<p/>  * http://www.apache.org/licenses/LICENSE-2.0  *<p/>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|TextMessage
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
name|List
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
name|assertEquals
import|;
end_import

begin_class
specifier|public
class|class
name|QueueOrderSingleTransactedConsumerTest
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
name|QueueOrderSingleTransactedConsumerTest
operator|.
name|class
argument_list|)
decl_stmt|;
name|BrokerService
name|broker
init|=
literal|null
decl_stmt|;
name|ActiveMQQueue
name|dest
init|=
operator|new
name|ActiveMQQueue
argument_list|(
literal|"Queue"
argument_list|)
decl_stmt|;
annotation|@
name|Test
specifier|public
name|void
name|testSingleConsumerTxRepeat
parameter_list|()
throws|throws
name|Exception
block|{
comment|// effect the broker sequence id that is region wide
name|ActiveMQQueue
name|dummyDest
init|=
operator|new
name|ActiveMQQueue
argument_list|(
literal|"AnotherQueue"
argument_list|)
decl_stmt|;
name|publishMessagesWithOrderProperty
argument_list|(
literal|10
argument_list|,
literal|0
argument_list|,
name|dest
argument_list|)
expr_stmt|;
name|publishMessagesWithOrderProperty
argument_list|(
literal|1
argument_list|,
literal|0
argument_list|,
name|dummyDest
argument_list|)
expr_stmt|;
name|publishMessagesWithOrderProperty
argument_list|(
literal|10
argument_list|,
literal|10
argument_list|,
name|dest
argument_list|)
expr_stmt|;
name|publishMessagesWithOrderProperty
argument_list|(
literal|1
argument_list|,
literal|0
argument_list|,
name|dummyDest
argument_list|)
expr_stmt|;
name|publishMessagesWithOrderProperty
argument_list|(
literal|10
argument_list|,
literal|20
argument_list|,
name|dest
argument_list|)
expr_stmt|;
name|publishMessagesWithOrderProperty
argument_list|(
literal|1
argument_list|,
literal|0
argument_list|,
name|dummyDest
argument_list|)
expr_stmt|;
name|publishMessagesWithOrderProperty
argument_list|(
literal|5
argument_list|,
literal|30
argument_list|,
name|dest
argument_list|)
expr_stmt|;
name|consumeVerifyOrderRollback
argument_list|(
literal|20
argument_list|)
expr_stmt|;
name|consumeVerifyOrderRollback
argument_list|(
literal|10
argument_list|)
expr_stmt|;
name|consumeVerifyOrderRollback
argument_list|(
literal|5
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSingleSessionXConsumerTxRepeat
parameter_list|()
throws|throws
name|Exception
block|{
name|publishMessagesWithOrderProperty
argument_list|(
literal|50
argument_list|)
expr_stmt|;
name|Connection
name|connection
init|=
name|getConnectionFactory
argument_list|()
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
literal|true
argument_list|,
name|Session
operator|.
name|SESSION_TRANSACTED
argument_list|)
decl_stmt|;
name|MessageConsumer
name|messageConsumer
init|=
name|consumeVerifyOrder
argument_list|(
name|session
argument_list|,
literal|20
argument_list|)
decl_stmt|;
name|messageConsumer
operator|.
name|close
argument_list|()
expr_stmt|;
name|session
operator|.
name|rollback
argument_list|()
expr_stmt|;
name|messageConsumer
operator|=
name|consumeVerifyOrder
argument_list|(
name|session
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|messageConsumer
operator|.
name|close
argument_list|()
expr_stmt|;
name|session
operator|.
name|rollback
argument_list|()
expr_stmt|;
name|messageConsumer
operator|=
name|consumeVerifyOrder
argument_list|(
name|session
argument_list|,
literal|5
argument_list|)
expr_stmt|;
name|messageConsumer
operator|.
name|close
argument_list|()
expr_stmt|;
name|session
operator|.
name|commit
argument_list|()
expr_stmt|;
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|tesXConsumerTxRepeat
parameter_list|()
throws|throws
name|Exception
block|{
name|publishMessagesWithOrderProperty
argument_list|(
literal|10
argument_list|)
expr_stmt|;
name|Connection
name|connection
init|=
name|getConnectionFactory
argument_list|()
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
literal|true
argument_list|,
name|Session
operator|.
name|SESSION_TRANSACTED
argument_list|)
decl_stmt|;
name|MessageConsumer
name|messageConsumer
init|=
name|consumeVerifyOrder
argument_list|(
name|session
argument_list|,
literal|6
argument_list|)
decl_stmt|;
name|messageConsumer
operator|.
name|close
argument_list|()
expr_stmt|;
name|messageConsumer
operator|=
name|consumeVerifyOrder
argument_list|(
name|session
argument_list|,
literal|4
argument_list|,
literal|6
argument_list|)
expr_stmt|;
comment|// rollback before close, so there are two consumers in the mix
name|session
operator|.
name|rollback
argument_list|()
expr_stmt|;
name|messageConsumer
operator|.
name|close
argument_list|()
expr_stmt|;
name|messageConsumer
operator|=
name|consumeVerifyOrder
argument_list|(
name|session
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|session
operator|.
name|commit
argument_list|()
expr_stmt|;
name|messageConsumer
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
annotation|@
name|Test
specifier|public
name|void
name|testSingleTxXConsumerTxRepeat
parameter_list|()
throws|throws
name|Exception
block|{
name|publishMessagesWithOrderProperty
argument_list|(
literal|10
argument_list|)
expr_stmt|;
name|Connection
name|connection
init|=
name|getConnectionFactory
argument_list|()
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
literal|true
argument_list|,
name|Session
operator|.
name|SESSION_TRANSACTED
argument_list|)
decl_stmt|;
name|MessageConsumer
name|messageConsumer
init|=
name|consumeVerifyOrder
argument_list|(
name|session
argument_list|,
literal|6
argument_list|)
decl_stmt|;
name|messageConsumer
operator|.
name|close
argument_list|()
expr_stmt|;
name|messageConsumer
operator|=
name|consumeVerifyOrder
argument_list|(
name|session
argument_list|,
literal|4
argument_list|,
literal|6
argument_list|)
expr_stmt|;
name|messageConsumer
operator|.
name|close
argument_list|()
expr_stmt|;
name|session
operator|.
name|rollback
argument_list|()
expr_stmt|;
name|messageConsumer
operator|=
name|consumeVerifyOrder
argument_list|(
name|session
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|session
operator|.
name|commit
argument_list|()
expr_stmt|;
name|messageConsumer
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
specifier|private
name|void
name|consumeVerifyOrderRollback
parameter_list|(
specifier|final
name|int
name|num
parameter_list|)
throws|throws
name|Exception
block|{
name|Connection
name|connection
init|=
name|getConnectionFactory
argument_list|()
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
literal|true
argument_list|,
name|Session
operator|.
name|SESSION_TRANSACTED
argument_list|)
decl_stmt|;
name|MessageConsumer
name|messageConsumer
init|=
name|consumeVerifyOrder
argument_list|(
name|session
argument_list|,
name|num
argument_list|)
decl_stmt|;
name|messageConsumer
operator|.
name|close
argument_list|()
expr_stmt|;
name|session
operator|.
name|rollback
argument_list|()
expr_stmt|;
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
specifier|private
name|MessageConsumer
name|consumeVerifyOrder
parameter_list|(
name|Session
name|session
parameter_list|,
specifier|final
name|int
name|num
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|consumeVerifyOrder
argument_list|(
name|session
argument_list|,
name|num
argument_list|,
literal|0
argument_list|)
return|;
block|}
specifier|private
name|MessageConsumer
name|consumeVerifyOrder
parameter_list|(
name|Session
name|session
parameter_list|,
specifier|final
name|int
name|num
parameter_list|,
specifier|final
name|int
name|base
parameter_list|)
throws|throws
name|Exception
block|{
name|MessageConsumer
name|messageConsumer
init|=
name|session
operator|.
name|createConsumer
argument_list|(
name|dest
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
name|num
condition|;
control|)
block|{
name|Message
name|message
init|=
name|messageConsumer
operator|.
name|receive
argument_list|(
literal|4000
argument_list|)
decl_stmt|;
if|if
condition|(
name|message
operator|!=
literal|null
condition|)
block|{
name|assertEquals
argument_list|(
name|i
operator|+
name|base
argument_list|,
name|message
operator|.
name|getIntProperty
argument_list|(
literal|"Order"
argument_list|)
argument_list|)
expr_stmt|;
name|i
operator|++
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Received:"
operator|+
name|message
operator|.
name|getJMSMessageID
argument_list|()
operator|+
literal|", Order: "
operator|+
name|message
operator|.
name|getIntProperty
argument_list|(
literal|"Order"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|messageConsumer
return|;
block|}
specifier|private
name|void
name|publishMessagesWithOrderProperty
parameter_list|(
name|int
name|num
parameter_list|)
throws|throws
name|Exception
block|{
name|publishMessagesWithOrderProperty
argument_list|(
name|num
argument_list|,
literal|0
argument_list|,
name|dest
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|publishMessagesWithOrderProperty
parameter_list|(
name|int
name|num
parameter_list|,
name|int
name|seqStart
parameter_list|,
name|ActiveMQQueue
name|destination
parameter_list|)
throws|throws
name|Exception
block|{
name|Connection
name|connection
init|=
name|getConnectionFactory
argument_list|()
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
name|MessageProducer
name|messageProducer
init|=
name|session
operator|.
name|createProducer
argument_list|(
name|destination
argument_list|)
decl_stmt|;
name|TextMessage
name|textMessage
init|=
name|session
operator|.
name|createTextMessage
argument_list|(
literal|"A"
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
name|num
condition|;
name|i
operator|++
control|)
block|{
name|textMessage
operator|.
name|setIntProperty
argument_list|(
literal|"Order"
argument_list|,
name|i
operator|+
name|seqStart
argument_list|)
expr_stmt|;
name|messageProducer
operator|.
name|send
argument_list|(
name|textMessage
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Before
specifier|public
name|void
name|startBroker
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
name|setDeleteAllMessagesOnStartup
argument_list|(
literal|true
argument_list|)
expr_stmt|;
comment|// add the policy entries
name|PolicyMap
name|policyMap
init|=
operator|new
name|PolicyMap
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|PolicyEntry
argument_list|>
name|entries
init|=
operator|new
name|ArrayList
argument_list|<
name|PolicyEntry
argument_list|>
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
name|setExpireMessagesPeriod
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|pe
operator|.
name|setQueuePrefetch
argument_list|(
literal|0
argument_list|)
expr_stmt|;
comment|// make incremental dispatch to the consumers explicit
name|pe
operator|.
name|setStrictOrderDispatch
argument_list|(
literal|true
argument_list|)
expr_stmt|;
comment|// force redeliveries back to the head of the queue
name|pe
operator|.
name|setQueue
argument_list|(
literal|">"
argument_list|)
expr_stmt|;
name|entries
operator|.
name|add
argument_list|(
name|pe
argument_list|)
expr_stmt|;
name|policyMap
operator|.
name|setPolicyEntries
argument_list|(
name|entries
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
name|addConnector
argument_list|(
literal|"tcp://0.0.0.0:0"
argument_list|)
expr_stmt|;
name|broker
operator|.
name|start
argument_list|()
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
specifier|private
name|ActiveMQConnectionFactory
name|getConnectionFactory
parameter_list|()
throws|throws
name|Exception
block|{
return|return
operator|new
name|ActiveMQConnectionFactory
argument_list|(
name|broker
operator|.
name|getTransportConnectorByScheme
argument_list|(
literal|"tcp"
argument_list|)
operator|.
name|getPublishableConnectString
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit

