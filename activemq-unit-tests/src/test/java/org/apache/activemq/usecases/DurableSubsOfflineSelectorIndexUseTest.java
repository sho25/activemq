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
name|MessageListener
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
name|PersistenceAdapter
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
name|KahaDBStore
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
name|DurableSubsOfflineSelectorIndexUseTest
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
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|DurableSubsOfflineSelectorIndexUseTest
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
name|int
name|messageCount
init|=
literal|400
decl_stmt|;
specifier|private
name|BrokerService
name|broker
decl_stmt|;
specifier|private
name|ActiveMQTopic
name|topic
decl_stmt|;
specifier|private
name|List
argument_list|<
name|Throwable
argument_list|>
name|exceptions
init|=
operator|new
name|ArrayList
argument_list|<
name|Throwable
argument_list|>
argument_list|()
decl_stmt|;
specifier|protected
name|ActiveMQConnectionFactory
name|createConnectionFactory
parameter_list|()
throws|throws
name|Exception
block|{
name|ActiveMQConnectionFactory
name|connectionFactory
init|=
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
decl_stmt|;
name|connectionFactory
operator|.
name|setWatchTopicAdvisories
argument_list|(
literal|false
argument_list|)
expr_stmt|;
return|return
name|connectionFactory
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
literal|"id"
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
name|DurableSubsOfflineSelectorIndexUseTest
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
name|exceptions
operator|.
name|clear
argument_list|()
expr_stmt|;
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
name|broker
operator|.
name|setAdvisorySupport
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|broker
operator|.
name|addConnector
argument_list|(
literal|"tcp://0.0.0.0:0"
argument_list|)
expr_stmt|;
name|setDefaultPersistenceAdapter
argument_list|(
name|broker
argument_list|)
expr_stmt|;
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
name|initCombosForTestIndexPageUsage
parameter_list|()
block|{
name|addCombinationValues
argument_list|(
literal|"messageCount"
argument_list|,
operator|new
name|Integer
index|[]
block|{
literal|890
block|,
literal|900
block|,
literal|400
block|}
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testIndexPageUsage
parameter_list|()
throws|throws
name|Exception
block|{
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
literal|"true"
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
literal|"false"
argument_list|,
literal|"filter = 'false'"
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
specifier|final
name|Connection
name|sendCon
init|=
name|createConnection
argument_list|(
literal|"send"
argument_list|)
decl_stmt|;
specifier|final
name|Session
name|sendSession
init|=
name|sendCon
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
name|MessageProducer
name|producer
init|=
name|sendSession
operator|.
name|createProducer
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|Thread
name|sendThread
init|=
operator|new
name|Thread
argument_list|()
block|{
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|messageCount
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
name|Message
name|message
init|=
name|sendSession
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
if|if
condition|(
name|i
operator|>
literal|0
operator|&&
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
literal|"Sent:"
operator|+
name|i
argument_list|)
expr_stmt|;
block|}
block|}
name|sendSession
operator|.
name|close
argument_list|()
expr_stmt|;
name|sendCon
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|exceptions
operator|.
name|add
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
decl_stmt|;
name|sendThread
operator|.
name|start
argument_list|()
expr_stmt|;
name|sendThread
operator|.
name|join
argument_list|()
expr_stmt|;
comment|// settle with sent messages
name|TimeUnit
operator|.
name|SECONDS
operator|.
name|sleep
argument_list|(
literal|4
argument_list|)
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
name|consumerTrue
init|=
name|session
operator|.
name|createDurableSubscriber
argument_list|(
name|topic
argument_list|,
literal|"true"
argument_list|,
literal|"filter = 'true'"
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|Listener
name|listenerT
init|=
operator|new
name|Listener
argument_list|()
decl_stmt|;
name|consumerTrue
operator|.
name|setMessageListener
argument_list|(
name|listenerT
argument_list|)
expr_stmt|;
name|waitFor
argument_list|(
name|listenerT
argument_list|,
name|messageCount
operator|/
literal|2
argument_list|)
expr_stmt|;
name|MessageConsumer
name|consumerFalse
init|=
name|session
operator|.
name|createDurableSubscriber
argument_list|(
name|topic
argument_list|,
literal|"false"
argument_list|,
literal|"filter = 'false'"
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|Listener
name|listenerF
init|=
operator|new
name|Listener
argument_list|()
decl_stmt|;
name|consumerFalse
operator|.
name|setMessageListener
argument_list|(
name|listenerF
argument_list|)
expr_stmt|;
name|waitFor
argument_list|(
name|listenerF
argument_list|,
name|messageCount
operator|/
literal|2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|messageCount
operator|/
literal|2
argument_list|,
name|listenerT
operator|.
name|count
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|messageCount
operator|/
literal|2
argument_list|,
name|listenerF
operator|.
name|count
argument_list|)
expr_stmt|;
name|consumerTrue
operator|.
name|close
argument_list|()
expr_stmt|;
name|session
operator|.
name|unsubscribe
argument_list|(
literal|"true"
argument_list|)
expr_stmt|;
name|consumerFalse
operator|.
name|close
argument_list|()
expr_stmt|;
name|session
operator|.
name|unsubscribe
argument_list|(
literal|"false"
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
name|PersistenceAdapter
name|persistenceAdapter
init|=
name|broker
operator|.
name|getPersistenceAdapter
argument_list|()
decl_stmt|;
if|if
condition|(
name|persistenceAdapter
operator|instanceof
name|KahaDBStore
condition|)
block|{
specifier|final
name|KahaDBStore
name|store
init|=
operator|(
operator|(
name|KahaDBPersistenceAdapter
operator|)
name|persistenceAdapter
operator|)
operator|.
name|getStore
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Store page count: "
operator|+
name|store
operator|.
name|getPageFile
argument_list|()
operator|.
name|getPageCount
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Store free page count: "
operator|+
name|store
operator|.
name|getPageFile
argument_list|()
operator|.
name|getFreePageCount
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Store page in-use: "
operator|+
operator|(
name|store
operator|.
name|getPageFile
argument_list|()
operator|.
name|getPageCount
argument_list|()
operator|-
name|store
operator|.
name|getPageFile
argument_list|()
operator|.
name|getFreePageCount
argument_list|()
operator|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"no leak of pages, always use just 10"
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
literal|10
operator|==
name|store
operator|.
name|getPageFile
argument_list|()
operator|.
name|getPageCount
argument_list|()
operator|-
name|store
operator|.
name|getPageFile
argument_list|()
operator|.
name|getFreePageCount
argument_list|()
return|;
block|}
block|}
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
operator|.
name|toMillis
argument_list|(
literal|10
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|waitFor
parameter_list|(
specifier|final
name|Listener
name|listener
parameter_list|,
specifier|final
name|int
name|count
parameter_list|)
throws|throws
name|Exception
block|{
name|assertTrue
argument_list|(
literal|"got all messages on time"
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
name|listener
operator|.
name|count
operator|==
name|count
return|;
block|}
block|}
argument_list|,
name|TimeUnit
operator|.
name|MINUTES
operator|.
name|toMillis
argument_list|(
literal|10
argument_list|)
argument_list|)
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
name|String
name|id
init|=
literal|null
decl_stmt|;
name|Listener
parameter_list|()
block|{         }
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
if|if
condition|(
name|id
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|LOG
operator|.
name|info
argument_list|(
name|id
operator|+
literal|", "
operator|+
name|message
operator|.
name|getJMSMessageID
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ignored
parameter_list|)
block|{                 }
block|}
block|}
block|}
block|}
end_class

end_unit

