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
name|junit
operator|.
name|framework
operator|.
name|Test
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
name|store
operator|.
name|jdbc
operator|.
name|JDBCPersistenceAdapter
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
name|kahadb
operator|.
name|KahaDBPersistenceAdapter
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
name|io
operator|.
name|File
import|;
end_import

begin_class
specifier|public
class|class
name|DurableSubscriptionOfflineTest
extends|extends
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|TestSupport
block|{
specifier|private
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|DurableSubscriptionOfflineTest
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
name|Boolean
name|usePrioritySupport
init|=
name|Boolean
operator|.
name|TRUE
decl_stmt|;
specifier|private
name|BrokerService
name|broker
decl_stmt|;
specifier|private
name|ActiveMQTopic
name|topic
decl_stmt|;
specifier|protected
name|ActiveMQConnectionFactory
name|createConnectionFactory
parameter_list|()
throws|throws
name|Exception
block|{
return|return
operator|new
name|ActiveMQConnectionFactory
argument_list|(
literal|"vm://"
operator|+
name|getName
argument_list|(
literal|true
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|protected
name|Connection
name|createConnection
parameter_list|()
throws|throws
name|Exception
block|{
return|return
name|createConnection
argument_list|(
literal|"cliName"
argument_list|)
return|;
block|}
specifier|protected
name|Connection
name|createConnection
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|Exception
block|{
name|Connection
name|con
init|=
name|super
operator|.
name|createConnection
argument_list|()
decl_stmt|;
name|con
operator|.
name|setClientID
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|con
operator|.
name|start
argument_list|()
expr_stmt|;
return|return
name|con
return|;
block|}
specifier|public
specifier|static
name|Test
name|suite
parameter_list|()
block|{
return|return
name|suite
argument_list|(
name|DurableSubscriptionOfflineTest
operator|.
name|class
argument_list|)
return|;
block|}
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|topic
operator|=
operator|(
name|ActiveMQTopic
operator|)
name|createDestination
argument_list|()
expr_stmt|;
name|createBroker
argument_list|()
expr_stmt|;
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
block|}
specifier|protected
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
name|destroyBroker
argument_list|()
expr_stmt|;
block|}
specifier|private
name|void
name|createBroker
parameter_list|()
throws|throws
name|Exception
block|{
name|createBroker
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|createBroker
parameter_list|(
name|boolean
name|deleteAllMessages
parameter_list|)
throws|throws
name|Exception
block|{
name|broker
operator|=
name|BrokerFactory
operator|.
name|createBroker
argument_list|(
literal|"broker:(vm://"
operator|+
name|getName
argument_list|(
literal|true
argument_list|)
operator|+
literal|")"
argument_list|)
expr_stmt|;
name|broker
operator|.
name|setBrokerName
argument_list|(
name|getName
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|broker
operator|.
name|setDeleteAllMessagesOnStartup
argument_list|(
name|deleteAllMessages
argument_list|)
expr_stmt|;
name|broker
operator|.
name|getManagementContext
argument_list|()
operator|.
name|setCreateConnector
argument_list|(
literal|false
argument_list|)
expr_stmt|;
if|if
condition|(
name|usePrioritySupport
condition|)
block|{
name|PolicyEntry
name|policy
init|=
operator|new
name|PolicyEntry
argument_list|()
decl_stmt|;
name|policy
operator|.
name|setPrioritizedMessages
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
name|policyMap
operator|.
name|setDefaultEntry
argument_list|(
name|policy
argument_list|)
expr_stmt|;
name|broker
operator|.
name|setDestinationPolicy
argument_list|(
name|policyMap
argument_list|)
expr_stmt|;
block|}
name|setDefaultPersistenceAdapter
argument_list|(
name|broker
argument_list|)
expr_stmt|;
if|if
condition|(
name|broker
operator|.
name|getPersistenceAdapter
argument_list|()
operator|instanceof
name|JDBCPersistenceAdapter
condition|)
block|{
comment|// ensure it kicks in during tests
operator|(
operator|(
name|JDBCPersistenceAdapter
operator|)
name|broker
operator|.
name|getPersistenceAdapter
argument_list|()
operator|)
operator|.
name|setCleanupPeriod
argument_list|(
literal|2
operator|*
literal|1000
argument_list|)
expr_stmt|;
block|}
name|broker
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
specifier|private
name|void
name|destroyBroker
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
name|broker
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|initCombosForTestConsumeOnlyMatchedMessages
parameter_list|()
throws|throws
name|Exception
block|{
name|this
operator|.
name|addCombinationValues
argument_list|(
literal|"defaultPersistenceAdapter"
argument_list|,
operator|new
name|Object
index|[]
block|{
name|PersistenceAdapterChoice
operator|.
name|KahaDB
block|,
name|PersistenceAdapterChoice
operator|.
name|JDBC
block|}
argument_list|)
expr_stmt|;
name|this
operator|.
name|addCombinationValues
argument_list|(
literal|"usePrioritySupport"
argument_list|,
operator|new
name|Object
index|[]
block|{
name|Boolean
operator|.
name|TRUE
block|,
name|Boolean
operator|.
name|FALSE
block|}
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testConsumeOnlyMatchedMessages
parameter_list|()
throws|throws
name|Exception
block|{
comment|// create durable subscription
name|Connection
name|con
init|=
name|createConnection
argument_list|()
decl_stmt|;
name|Session
name|session
init|=
name|con
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
name|session
operator|.
name|createDurableSubscriber
argument_list|(
name|topic
argument_list|,
literal|"SubsId"
argument_list|,
literal|"filter = 'true'"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|session
operator|.
name|close
argument_list|()
expr_stmt|;
name|con
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// send messages
name|con
operator|=
name|createConnection
argument_list|()
expr_stmt|;
name|session
operator|=
name|con
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
name|MessageProducer
name|producer
init|=
name|session
operator|.
name|createProducer
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|int
name|sent
init|=
literal|0
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
literal|10
condition|;
name|i
operator|++
control|)
block|{
name|boolean
name|filter
init|=
name|i
operator|%
literal|2
operator|==
literal|1
decl_stmt|;
if|if
condition|(
name|filter
condition|)
name|sent
operator|++
expr_stmt|;
name|Message
name|message
init|=
name|session
operator|.
name|createMessage
argument_list|()
decl_stmt|;
name|message
operator|.
name|setStringProperty
argument_list|(
literal|"filter"
argument_list|,
name|filter
condition|?
literal|"true"
else|:
literal|"false"
argument_list|)
expr_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|topic
argument_list|,
name|message
argument_list|)
expr_stmt|;
block|}
name|session
operator|.
name|close
argument_list|()
expr_stmt|;
name|con
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// consume messages
name|con
operator|=
name|createConnection
argument_list|()
expr_stmt|;
name|session
operator|=
name|con
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
name|MessageConsumer
name|consumer
init|=
name|session
operator|.
name|createDurableSubscriber
argument_list|(
name|topic
argument_list|,
literal|"SubsId"
argument_list|,
literal|"filter = 'true'"
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|Listener
name|listener
init|=
operator|new
name|Listener
argument_list|()
decl_stmt|;
name|consumer
operator|.
name|setMessageListener
argument_list|(
name|listener
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|3
operator|*
literal|1000
argument_list|)
expr_stmt|;
name|session
operator|.
name|close
argument_list|()
expr_stmt|;
name|con
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|sent
argument_list|,
name|listener
operator|.
name|count
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testConsumeAllMatchedMessages
parameter_list|()
throws|throws
name|Exception
block|{
comment|// create durable subscription
name|Connection
name|con
init|=
name|createConnection
argument_list|()
decl_stmt|;
name|Session
name|session
init|=
name|con
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
name|session
operator|.
name|createDurableSubscriber
argument_list|(
name|topic
argument_list|,
literal|"SubsId"
argument_list|,
literal|"filter = 'true'"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|session
operator|.
name|close
argument_list|()
expr_stmt|;
name|con
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// send messages
name|con
operator|=
name|createConnection
argument_list|()
expr_stmt|;
name|session
operator|=
name|con
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
name|MessageProducer
name|producer
init|=
name|session
operator|.
name|createProducer
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|int
name|sent
init|=
literal|0
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
literal|10
condition|;
name|i
operator|++
control|)
block|{
name|sent
operator|++
expr_stmt|;
name|Message
name|message
init|=
name|session
operator|.
name|createMessage
argument_list|()
decl_stmt|;
name|message
operator|.
name|setStringProperty
argument_list|(
literal|"filter"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|topic
argument_list|,
name|message
argument_list|)
expr_stmt|;
block|}
name|Thread
operator|.
name|sleep
argument_list|(
literal|1
operator|*
literal|1000
argument_list|)
expr_stmt|;
name|session
operator|.
name|close
argument_list|()
expr_stmt|;
name|con
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// consume messages
name|con
operator|=
name|createConnection
argument_list|()
expr_stmt|;
name|session
operator|=
name|con
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
name|MessageConsumer
name|consumer
init|=
name|session
operator|.
name|createDurableSubscriber
argument_list|(
name|topic
argument_list|,
literal|"SubsId"
argument_list|,
literal|"filter = 'true'"
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|Listener
name|listener
init|=
operator|new
name|Listener
argument_list|()
decl_stmt|;
name|consumer
operator|.
name|setMessageListener
argument_list|(
name|listener
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|3
operator|*
literal|1000
argument_list|)
expr_stmt|;
name|session
operator|.
name|close
argument_list|()
expr_stmt|;
name|con
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|sent
argument_list|,
name|listener
operator|.
name|count
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testVerifyAllConsumedAreAcked
parameter_list|()
throws|throws
name|Exception
block|{
comment|// create durable subscription
name|Connection
name|con
init|=
name|createConnection
argument_list|()
decl_stmt|;
name|Session
name|session
init|=
name|con
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
name|session
operator|.
name|createDurableSubscriber
argument_list|(
name|topic
argument_list|,
literal|"SubsId"
argument_list|,
literal|"filter = 'true'"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|session
operator|.
name|close
argument_list|()
expr_stmt|;
name|con
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// send messages
name|con
operator|=
name|createConnection
argument_list|()
expr_stmt|;
name|session
operator|=
name|con
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
name|MessageProducer
name|producer
init|=
name|session
operator|.
name|createProducer
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|int
name|sent
init|=
literal|0
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
literal|10
condition|;
name|i
operator|++
control|)
block|{
name|sent
operator|++
expr_stmt|;
name|Message
name|message
init|=
name|session
operator|.
name|createMessage
argument_list|()
decl_stmt|;
name|message
operator|.
name|setStringProperty
argument_list|(
literal|"filter"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|topic
argument_list|,
name|message
argument_list|)
expr_stmt|;
block|}
name|Thread
operator|.
name|sleep
argument_list|(
literal|1
operator|*
literal|1000
argument_list|)
expr_stmt|;
name|session
operator|.
name|close
argument_list|()
expr_stmt|;
name|con
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// consume messages
name|con
operator|=
name|createConnection
argument_list|()
expr_stmt|;
name|session
operator|=
name|con
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
name|MessageConsumer
name|consumer
init|=
name|session
operator|.
name|createDurableSubscriber
argument_list|(
name|topic
argument_list|,
literal|"SubsId"
argument_list|,
literal|"filter = 'true'"
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|Listener
name|listener
init|=
operator|new
name|Listener
argument_list|()
decl_stmt|;
name|consumer
operator|.
name|setMessageListener
argument_list|(
name|listener
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|3
operator|*
literal|1000
argument_list|)
expr_stmt|;
name|session
operator|.
name|close
argument_list|()
expr_stmt|;
name|con
operator|.
name|close
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Consumed: "
operator|+
name|listener
operator|.
name|count
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|sent
argument_list|,
name|listener
operator|.
name|count
argument_list|)
expr_stmt|;
comment|// consume messages again, should not get any
name|con
operator|=
name|createConnection
argument_list|()
expr_stmt|;
name|session
operator|=
name|con
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
name|consumer
operator|=
name|session
operator|.
name|createDurableSubscriber
argument_list|(
name|topic
argument_list|,
literal|"SubsId"
argument_list|,
literal|"filter = 'true'"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|listener
operator|=
operator|new
name|Listener
argument_list|()
expr_stmt|;
name|consumer
operator|.
name|setMessageListener
argument_list|(
name|listener
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|3
operator|*
literal|1000
argument_list|)
expr_stmt|;
name|session
operator|.
name|close
argument_list|()
expr_stmt|;
name|con
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|listener
operator|.
name|count
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testTwoOfflineSubscriptionCanConsume
parameter_list|()
throws|throws
name|Exception
block|{
comment|// create durable subscription 1
name|Connection
name|con
init|=
name|createConnection
argument_list|(
literal|"cliId1"
argument_list|)
decl_stmt|;
name|Session
name|session
init|=
name|con
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
name|session
operator|.
name|createDurableSubscriber
argument_list|(
name|topic
argument_list|,
literal|"SubsId"
argument_list|,
literal|"filter = 'true'"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|session
operator|.
name|close
argument_list|()
expr_stmt|;
name|con
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// create durable subscription 2
name|Connection
name|con2
init|=
name|createConnection
argument_list|(
literal|"cliId2"
argument_list|)
decl_stmt|;
name|Session
name|session2
init|=
name|con2
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
name|consumer2
init|=
name|session2
operator|.
name|createDurableSubscriber
argument_list|(
name|topic
argument_list|,
literal|"SubsId"
argument_list|,
literal|"filter = 'true'"
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|Listener
name|listener2
init|=
operator|new
name|Listener
argument_list|()
decl_stmt|;
name|consumer2
operator|.
name|setMessageListener
argument_list|(
name|listener2
argument_list|)
expr_stmt|;
comment|// send messages
name|con
operator|=
name|createConnection
argument_list|()
expr_stmt|;
name|session
operator|=
name|con
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
name|MessageProducer
name|producer
init|=
name|session
operator|.
name|createProducer
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|int
name|sent
init|=
literal|0
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
literal|10
condition|;
name|i
operator|++
control|)
block|{
name|sent
operator|++
expr_stmt|;
name|Message
name|message
init|=
name|session
operator|.
name|createMessage
argument_list|()
decl_stmt|;
name|message
operator|.
name|setStringProperty
argument_list|(
literal|"filter"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|topic
argument_list|,
name|message
argument_list|)
expr_stmt|;
block|}
name|Thread
operator|.
name|sleep
argument_list|(
literal|1
operator|*
literal|1000
argument_list|)
expr_stmt|;
name|session
operator|.
name|close
argument_list|()
expr_stmt|;
name|con
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// test online subs
name|Thread
operator|.
name|sleep
argument_list|(
literal|3
operator|*
literal|1000
argument_list|)
expr_stmt|;
name|session2
operator|.
name|close
argument_list|()
expr_stmt|;
name|con2
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|sent
argument_list|,
name|listener2
operator|.
name|count
argument_list|)
expr_stmt|;
comment|// consume messages
name|con
operator|=
name|createConnection
argument_list|(
literal|"cliId1"
argument_list|)
expr_stmt|;
name|session
operator|=
name|con
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
name|MessageConsumer
name|consumer
init|=
name|session
operator|.
name|createDurableSubscriber
argument_list|(
name|topic
argument_list|,
literal|"SubsId"
argument_list|,
literal|"filter = 'true'"
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|Listener
name|listener
init|=
operator|new
name|Listener
argument_list|()
decl_stmt|;
name|consumer
operator|.
name|setMessageListener
argument_list|(
name|listener
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|3
operator|*
literal|1000
argument_list|)
expr_stmt|;
name|session
operator|.
name|close
argument_list|()
expr_stmt|;
name|con
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"offline consumer got all"
argument_list|,
name|sent
argument_list|,
name|listener
operator|.
name|count
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|initCombosForTestOfflineSubscriptionCanConsumeAfterOnlineSubs
parameter_list|()
throws|throws
name|Exception
block|{
name|this
operator|.
name|addCombinationValues
argument_list|(
literal|"defaultPersistenceAdapter"
argument_list|,
operator|new
name|Object
index|[]
block|{
name|PersistenceAdapterChoice
operator|.
name|KahaDB
block|,
name|PersistenceAdapterChoice
operator|.
name|JDBC
block|}
argument_list|)
expr_stmt|;
name|this
operator|.
name|addCombinationValues
argument_list|(
literal|"usePrioritySupport"
argument_list|,
operator|new
name|Object
index|[]
block|{
name|Boolean
operator|.
name|TRUE
block|,
name|Boolean
operator|.
name|FALSE
block|}
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testOfflineSubscriptionCanConsumeAfterOnlineSubs
parameter_list|()
throws|throws
name|Exception
block|{
name|Connection
name|con
init|=
name|createConnection
argument_list|(
literal|"offCli1"
argument_list|)
decl_stmt|;
name|Session
name|session
init|=
name|con
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
name|session
operator|.
name|createDurableSubscriber
argument_list|(
name|topic
argument_list|,
literal|"SubsId"
argument_list|,
literal|"filter = 'true'"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|session
operator|.
name|close
argument_list|()
expr_stmt|;
name|con
operator|.
name|close
argument_list|()
expr_stmt|;
name|con
operator|=
name|createConnection
argument_list|(
literal|"offCli2"
argument_list|)
expr_stmt|;
name|session
operator|=
name|con
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
name|session
operator|.
name|createDurableSubscriber
argument_list|(
name|topic
argument_list|,
literal|"SubsId"
argument_list|,
literal|"filter = 'true'"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|session
operator|.
name|close
argument_list|()
expr_stmt|;
name|con
operator|.
name|close
argument_list|()
expr_stmt|;
name|Connection
name|con2
init|=
name|createConnection
argument_list|(
literal|"onlineCli1"
argument_list|)
decl_stmt|;
name|Session
name|session2
init|=
name|con2
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
name|consumer2
init|=
name|session2
operator|.
name|createDurableSubscriber
argument_list|(
name|topic
argument_list|,
literal|"SubsId"
argument_list|,
literal|"filter = 'true'"
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|Listener
name|listener2
init|=
operator|new
name|Listener
argument_list|()
decl_stmt|;
name|consumer2
operator|.
name|setMessageListener
argument_list|(
name|listener2
argument_list|)
expr_stmt|;
comment|// send messages
name|con
operator|=
name|createConnection
argument_list|()
expr_stmt|;
name|session
operator|=
name|con
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
name|MessageProducer
name|producer
init|=
name|session
operator|.
name|createProducer
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|int
name|sent
init|=
literal|0
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
literal|10
condition|;
name|i
operator|++
control|)
block|{
name|sent
operator|++
expr_stmt|;
name|Message
name|message
init|=
name|session
operator|.
name|createMessage
argument_list|()
decl_stmt|;
name|message
operator|.
name|setStringProperty
argument_list|(
literal|"filter"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|topic
argument_list|,
name|message
argument_list|)
expr_stmt|;
block|}
name|Thread
operator|.
name|sleep
argument_list|(
literal|1
operator|*
literal|1000
argument_list|)
expr_stmt|;
name|session
operator|.
name|close
argument_list|()
expr_stmt|;
name|con
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// test online subs
name|Thread
operator|.
name|sleep
argument_list|(
literal|3
operator|*
literal|1000
argument_list|)
expr_stmt|;
name|session2
operator|.
name|close
argument_list|()
expr_stmt|;
name|con2
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|sent
argument_list|,
name|listener2
operator|.
name|count
argument_list|)
expr_stmt|;
comment|// restart broker
name|broker
operator|.
name|stop
argument_list|()
expr_stmt|;
name|createBroker
argument_list|(
literal|false
comment|/*deleteAllMessages*/
argument_list|)
expr_stmt|;
comment|// test offline
name|con
operator|=
name|createConnection
argument_list|(
literal|"offCli1"
argument_list|)
expr_stmt|;
name|session
operator|=
name|con
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
name|MessageConsumer
name|consumer
init|=
name|session
operator|.
name|createDurableSubscriber
argument_list|(
name|topic
argument_list|,
literal|"SubsId"
argument_list|,
literal|"filter = 'true'"
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|Connection
name|con3
init|=
name|createConnection
argument_list|(
literal|"offCli2"
argument_list|)
decl_stmt|;
name|Session
name|session3
init|=
name|con3
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
name|consumer3
init|=
name|session3
operator|.
name|createDurableSubscriber
argument_list|(
name|topic
argument_list|,
literal|"SubsId"
argument_list|,
literal|"filter = 'true'"
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|Listener
name|listener
init|=
operator|new
name|Listener
argument_list|()
decl_stmt|;
name|consumer
operator|.
name|setMessageListener
argument_list|(
name|listener
argument_list|)
expr_stmt|;
name|Listener
name|listener3
init|=
operator|new
name|Listener
argument_list|()
decl_stmt|;
name|consumer3
operator|.
name|setMessageListener
argument_list|(
name|listener3
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|3
operator|*
literal|1000
argument_list|)
expr_stmt|;
name|session
operator|.
name|close
argument_list|()
expr_stmt|;
name|con
operator|.
name|close
argument_list|()
expr_stmt|;
name|session3
operator|.
name|close
argument_list|()
expr_stmt|;
name|con3
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|sent
argument_list|,
name|listener
operator|.
name|count
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|sent
argument_list|,
name|listener3
operator|.
name|count
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|static
class|class
name|Listener
implements|implements
name|MessageListener
block|{
name|int
name|count
init|=
literal|0
decl_stmt|;
specifier|public
name|void
name|onMessage
parameter_list|(
name|Message
name|message
parameter_list|)
block|{
name|count
operator|++
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

