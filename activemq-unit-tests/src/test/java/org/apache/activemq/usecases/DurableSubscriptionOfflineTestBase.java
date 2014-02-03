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
name|TestSupport
operator|.
name|PersistenceAdapterChoice
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
name|disk
operator|.
name|journal
operator|.
name|Journal
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
name|leveldb
operator|.
name|LevelDBPersistenceAdapter
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
name|memory
operator|.
name|MemoryPersistenceAdapter
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
name|Rule
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|rules
operator|.
name|TestName
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
name|Destination
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
name|java
operator|.
name|io
operator|.
name|IOException
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
name|assertTrue
import|;
end_import

begin_class
specifier|public
specifier|abstract
class|class
name|DurableSubscriptionOfflineTestBase
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
name|DurableSubscriptionOfflineTestBase
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
name|boolean
name|usePrioritySupport
init|=
name|Boolean
operator|.
name|TRUE
decl_stmt|;
specifier|public
name|int
name|journalMaxFileLength
init|=
name|Journal
operator|.
name|DEFAULT_MAX_FILE_LENGTH
decl_stmt|;
specifier|public
name|boolean
name|keepDurableSubsActive
init|=
literal|true
decl_stmt|;
specifier|protected
name|BrokerService
name|broker
decl_stmt|;
specifier|protected
name|ActiveMQTopic
name|topic
decl_stmt|;
specifier|protected
specifier|final
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
name|connectionFactory
decl_stmt|;
specifier|protected
name|boolean
name|isTopic
init|=
literal|true
decl_stmt|;
specifier|public
name|PersistenceAdapterChoice
name|defaultPersistenceAdapter
init|=
name|PersistenceAdapterChoice
operator|.
name|KahaDB
decl_stmt|;
annotation|@
name|Rule
specifier|public
name|TestName
name|testName
init|=
operator|new
name|TestName
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
name|ConnectionFactory
name|connectionFactory1
init|=
name|createConnectionFactory
argument_list|()
decl_stmt|;
name|Connection
name|connection
init|=
name|connectionFactory1
operator|.
name|createConnection
argument_list|()
decl_stmt|;
name|connection
operator|.
name|setClientID
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
return|return
name|connection
return|;
block|}
specifier|public
name|ActiveMQConnectionFactory
name|getConnectionFactory
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|connectionFactory
operator|==
literal|null
condition|)
block|{
name|connectionFactory
operator|=
name|createConnectionFactory
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Should have created a connection factory!"
argument_list|,
name|connectionFactory
operator|!=
literal|null
argument_list|)
expr_stmt|;
block|}
return|return
name|connectionFactory
return|;
block|}
annotation|@
name|Before
specifier|public
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
name|destroyBroker
argument_list|()
expr_stmt|;
block|}
specifier|protected
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
specifier|protected
name|void
name|createBroker
parameter_list|(
name|boolean
name|deleteAllMessages
parameter_list|)
throws|throws
name|Exception
block|{
name|String
name|currentTestName
init|=
name|getName
argument_list|(
literal|true
argument_list|)
decl_stmt|;
name|broker
operator|=
name|BrokerFactory
operator|.
name|createBroker
argument_list|(
literal|"broker:(vm://"
operator|+
name|currentTestName
operator|+
literal|")"
argument_list|)
expr_stmt|;
name|broker
operator|.
name|setBrokerName
argument_list|(
name|currentTestName
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
name|setKeepDurableSubsActive
argument_list|(
name|keepDurableSubsActive
argument_list|)
expr_stmt|;
name|broker
operator|.
name|addConnector
argument_list|(
literal|"tcp://0.0.0.0:0"
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
elseif|else
if|if
condition|(
name|broker
operator|.
name|getPersistenceAdapter
argument_list|()
operator|instanceof
name|KahaDBPersistenceAdapter
condition|)
block|{
comment|// have lots of journal files
operator|(
operator|(
name|KahaDBPersistenceAdapter
operator|)
name|broker
operator|.
name|getPersistenceAdapter
argument_list|()
operator|)
operator|.
name|setJournalMaxFileLength
argument_list|(
name|journalMaxFileLength
argument_list|)
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
specifier|protected
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
specifier|protected
name|Destination
name|createDestination
parameter_list|(
name|String
name|subject
parameter_list|)
block|{
if|if
condition|(
name|isTopic
condition|)
block|{
return|return
operator|new
name|ActiveMQTopic
argument_list|(
name|subject
argument_list|)
return|;
block|}
else|else
block|{
return|return
operator|new
name|ActiveMQQueue
argument_list|(
name|subject
argument_list|)
return|;
block|}
block|}
specifier|protected
name|Destination
name|createDestination
parameter_list|()
block|{
return|return
name|createDestination
argument_list|(
name|getDestinationString
argument_list|()
argument_list|)
return|;
block|}
comment|/**      * Returns the name of the destination used in this test case      */
specifier|protected
name|String
name|getDestinationString
parameter_list|()
block|{
return|return
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|"."
operator|+
name|getName
argument_list|(
literal|true
argument_list|)
return|;
block|}
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|getName
argument_list|(
literal|false
argument_list|)
return|;
block|}
specifier|protected
name|String
name|getName
parameter_list|(
name|boolean
name|original
parameter_list|)
block|{
name|String
name|currentTestName
init|=
name|testName
operator|.
name|getMethodName
argument_list|()
decl_stmt|;
name|currentTestName
operator|=
name|currentTestName
operator|.
name|replace
argument_list|(
literal|"["
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|currentTestName
operator|=
name|currentTestName
operator|.
name|replace
argument_list|(
literal|"]"
argument_list|,
literal|""
argument_list|)
expr_stmt|;
return|return
name|currentTestName
return|;
block|}
specifier|public
name|PersistenceAdapter
name|setDefaultPersistenceAdapter
parameter_list|(
name|BrokerService
name|broker
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|setPersistenceAdapter
argument_list|(
name|broker
argument_list|,
name|defaultPersistenceAdapter
argument_list|)
return|;
block|}
specifier|public
name|PersistenceAdapter
name|setPersistenceAdapter
parameter_list|(
name|BrokerService
name|broker
parameter_list|,
name|PersistenceAdapterChoice
name|choice
parameter_list|)
throws|throws
name|IOException
block|{
name|PersistenceAdapter
name|adapter
init|=
literal|null
decl_stmt|;
switch|switch
condition|(
name|choice
condition|)
block|{
case|case
name|JDBC
case|:
name|LOG
operator|.
name|debug
argument_list|(
literal|">>>> setPersistenceAdapter to JDBC "
argument_list|)
expr_stmt|;
name|adapter
operator|=
operator|new
name|JDBCPersistenceAdapter
argument_list|()
expr_stmt|;
break|break;
case|case
name|KahaDB
case|:
name|LOG
operator|.
name|debug
argument_list|(
literal|">>>> setPersistenceAdapter to KahaDB "
argument_list|)
expr_stmt|;
name|adapter
operator|=
operator|new
name|KahaDBPersistenceAdapter
argument_list|()
expr_stmt|;
break|break;
case|case
name|LevelDB
case|:
name|LOG
operator|.
name|debug
argument_list|(
literal|">>>> setPersistenceAdapter to LevelDB "
argument_list|)
expr_stmt|;
name|adapter
operator|=
operator|new
name|LevelDBPersistenceAdapter
argument_list|()
expr_stmt|;
break|break;
case|case
name|MEM
case|:
name|LOG
operator|.
name|debug
argument_list|(
literal|">>>> setPersistenceAdapter to MEM "
argument_list|)
expr_stmt|;
name|adapter
operator|=
operator|new
name|MemoryPersistenceAdapter
argument_list|()
expr_stmt|;
break|break;
block|}
name|broker
operator|.
name|setPersistenceAdapter
argument_list|(
name|adapter
argument_list|)
expr_stmt|;
return|return
name|adapter
return|;
block|}
block|}
end_class

begin_class
class|class
name|DurableSubscriptionOfflineTestListener
implements|implements
name|MessageListener
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
name|DurableSubscriptionOfflineTestListener
operator|.
name|class
argument_list|)
decl_stmt|;
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
name|DurableSubscriptionOfflineTestListener
parameter_list|()
block|{}
name|DurableSubscriptionOfflineTestListener
parameter_list|(
name|String
name|id
parameter_list|)
block|{
name|this
operator|.
name|id
operator|=
name|id
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|onMessage
parameter_list|(
name|javax
operator|.
name|jms
operator|.
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
block|{}
block|}
block|}
block|}
end_class

end_unit
