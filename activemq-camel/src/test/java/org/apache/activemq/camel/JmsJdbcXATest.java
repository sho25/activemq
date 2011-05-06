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
name|camel
package|;
end_package

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|ResultSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|SQLException
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
name|BrokerPlugin
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
name|BrokerPluginSupport
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
name|ConnectionContext
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
name|TransactionId
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
name|apache
operator|.
name|camel
operator|.
name|spring
operator|.
name|SpringTestSupport
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
name|dbcp
operator|.
name|BasicDataSource
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
name|org
operator|.
name|springframework
operator|.
name|context
operator|.
name|support
operator|.
name|AbstractXmlApplicationContext
import|;
end_import

begin_import
import|import
name|org
operator|.
name|springframework
operator|.
name|context
operator|.
name|support
operator|.
name|ClassPathXmlApplicationContext
import|;
end_import

begin_comment
comment|/**  *  shows broker 'once only delivery' and recovery with XA  */
end_comment

begin_class
specifier|public
class|class
name|JmsJdbcXATest
extends|extends
name|SpringTestSupport
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
name|JmsJdbcXATest
operator|.
name|class
argument_list|)
decl_stmt|;
name|BrokerService
name|broker
init|=
literal|null
decl_stmt|;
name|int
name|messageCount
decl_stmt|;
specifier|public
name|java
operator|.
name|sql
operator|.
name|Connection
name|initDb
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|createStatement
init|=
literal|"CREATE TABLE SCP_INPUT_MESSAGES ("
operator|+
literal|"id int NOT NULL GENERATED ALWAYS AS IDENTITY, "
operator|+
literal|"messageId varchar(96) NOT NULL, "
operator|+
literal|"messageCorrelationId varchar(96) NOT NULL, "
operator|+
literal|"messageContent varchar(2048) NOT NULL, "
operator|+
literal|"PRIMARY KEY (id) )"
decl_stmt|;
name|java
operator|.
name|sql
operator|.
name|Connection
name|conn
init|=
name|getJDBCConnection
argument_list|()
decl_stmt|;
try|try
block|{
name|conn
operator|.
name|createStatement
argument_list|()
operator|.
name|execute
argument_list|(
name|createStatement
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SQLException
name|alreadyExists
parameter_list|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"ex on create tables"
argument_list|,
name|alreadyExists
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|conn
operator|.
name|createStatement
argument_list|()
operator|.
name|execute
argument_list|(
literal|"DELETE FROM SCP_INPUT_MESSAGES"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SQLException
name|ex
parameter_list|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"ex on create delete all"
argument_list|,
name|ex
argument_list|)
expr_stmt|;
block|}
return|return
name|conn
return|;
block|}
specifier|private
name|java
operator|.
name|sql
operator|.
name|Connection
name|getJDBCConnection
parameter_list|()
throws|throws
name|Exception
block|{
name|BasicDataSource
name|dataSource
init|=
name|getMandatoryBean
argument_list|(
name|BasicDataSource
operator|.
name|class
argument_list|,
literal|"managedDataSourceWithRecovery"
argument_list|)
decl_stmt|;
return|return
name|dataSource
operator|.
name|getConnection
argument_list|()
return|;
block|}
specifier|private
name|int
name|dumpDb
parameter_list|(
name|java
operator|.
name|sql
operator|.
name|Connection
name|jdbcConn
parameter_list|)
throws|throws
name|Exception
block|{
name|int
name|count
init|=
literal|0
decl_stmt|;
name|ResultSet
name|resultSet
init|=
name|jdbcConn
operator|.
name|createStatement
argument_list|()
operator|.
name|executeQuery
argument_list|(
literal|"SELECT * FROM SCP_INPUT_MESSAGES"
argument_list|)
decl_stmt|;
while|while
condition|(
name|resultSet
operator|.
name|next
argument_list|()
condition|)
block|{
name|count
operator|++
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"message - seq:"
operator|+
name|resultSet
operator|.
name|getInt
argument_list|(
literal|1
argument_list|)
operator|+
literal|", id: "
operator|+
name|resultSet
operator|.
name|getString
argument_list|(
literal|2
argument_list|)
operator|+
literal|", corr: "
operator|+
name|resultSet
operator|.
name|getString
argument_list|(
literal|3
argument_list|)
operator|+
literal|", content: "
operator|+
name|resultSet
operator|.
name|getString
argument_list|(
literal|4
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|count
return|;
block|}
specifier|public
name|void
name|testRecoveryCommit
parameter_list|()
throws|throws
name|Exception
block|{
name|java
operator|.
name|sql
operator|.
name|Connection
name|jdbcConn
init|=
name|initDb
argument_list|()
decl_stmt|;
name|sendJMSMessageToKickOffRoute
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"waiting for route to kick in, it will kill the broker on first 2pc commit"
argument_list|)
expr_stmt|;
comment|// will be stopped by the plugin on first 2pc commit
name|broker
operator|.
name|waitUntilStopped
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"message in db, commit to db worked"
argument_list|,
literal|1
argument_list|,
name|dumpDb
argument_list|(
name|jdbcConn
argument_list|)
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Broker stopped, restarting..."
argument_list|)
expr_stmt|;
name|broker
operator|=
name|createBroker
argument_list|(
literal|false
argument_list|)
expr_stmt|;
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
name|assertEquals
argument_list|(
literal|"pending transactions"
argument_list|,
literal|1
argument_list|,
name|broker
operator|.
name|getBroker
argument_list|()
operator|.
name|getPreparedTransactions
argument_list|(
literal|null
argument_list|)
operator|.
name|length
argument_list|)
expr_stmt|;
comment|// TM stays actively committing first message ack which won't get redelivered - xa once only delivery
name|LOG
operator|.
name|info
argument_list|(
literal|"waiting for recovery to complete"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"recovery complete in time"
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
name|getBroker
argument_list|()
operator|.
name|getPreparedTransactions
argument_list|(
literal|null
argument_list|)
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
comment|// verify recovery complete
name|assertEquals
argument_list|(
literal|"recovery complete"
argument_list|,
literal|0
argument_list|,
name|broker
operator|.
name|getBroker
argument_list|()
operator|.
name|getPreparedTransactions
argument_list|(
literal|null
argument_list|)
operator|.
name|length
argument_list|)
expr_stmt|;
specifier|final
name|java
operator|.
name|sql
operator|.
name|Connection
name|freshConnection
init|=
name|getJDBCConnection
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"did not get replay"
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
literal|1
operator|==
name|dumpDb
argument_list|(
name|freshConnection
argument_list|)
return|;
block|}
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"still one message in db"
argument_list|,
literal|1
argument_list|,
name|dumpDb
argument_list|(
name|freshConnection
argument_list|)
argument_list|)
expr_stmt|;
comment|// let once complete ok
name|sendJMSMessageToKickOffRoute
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
literal|"got second message"
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
literal|2
operator|==
name|dumpDb
argument_list|(
name|freshConnection
argument_list|)
return|;
block|}
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"two messages in db"
argument_list|,
literal|2
argument_list|,
name|dumpDb
argument_list|(
name|freshConnection
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|sendJMSMessageToKickOffRoute
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
literal|"vm://testXA"
argument_list|)
decl_stmt|;
name|factory
operator|.
name|setWatchTopicAdvisories
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|Connection
name|connection
init|=
name|factory
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
name|producer
init|=
name|session
operator|.
name|createProducer
argument_list|(
operator|new
name|ActiveMQQueue
argument_list|(
literal|"scp_transacted"
argument_list|)
argument_list|)
decl_stmt|;
name|TextMessage
name|message
init|=
name|session
operator|.
name|createTextMessage
argument_list|(
literal|"Some Text, messageCount:"
operator|+
name|messageCount
operator|++
argument_list|)
decl_stmt|;
name|message
operator|.
name|setJMSCorrelationID
argument_list|(
literal|"pleaseCorrelate"
argument_list|)
expr_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
specifier|private
name|BrokerService
name|createBroker
parameter_list|(
name|boolean
name|deleteAllMessages
parameter_list|)
throws|throws
name|Exception
block|{
name|BrokerService
name|brokerService
init|=
operator|new
name|BrokerService
argument_list|()
decl_stmt|;
name|brokerService
operator|.
name|setDeleteAllMessagesOnStartup
argument_list|(
name|deleteAllMessages
argument_list|)
expr_stmt|;
name|brokerService
operator|.
name|setBrokerName
argument_list|(
literal|"testXA"
argument_list|)
expr_stmt|;
name|brokerService
operator|.
name|setAdvisorySupport
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|brokerService
operator|.
name|setUseJmx
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|brokerService
operator|.
name|setDataDirectory
argument_list|(
literal|"target/data"
argument_list|)
expr_stmt|;
name|brokerService
operator|.
name|addConnector
argument_list|(
literal|"tcp://0.0.0.0:61616"
argument_list|)
expr_stmt|;
return|return
name|brokerService
return|;
block|}
annotation|@
name|Override
specifier|protected
name|AbstractXmlApplicationContext
name|createApplicationContext
parameter_list|()
block|{
name|deleteDirectory
argument_list|(
literal|"target/data/howl"
argument_list|)
expr_stmt|;
comment|// make broker available to recovery processing on app context start
try|try
block|{
name|broker
operator|=
name|createBroker
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|broker
operator|.
name|setPlugins
argument_list|(
operator|new
name|BrokerPlugin
index|[]
block|{
operator|new
name|BrokerPluginSupport
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|commitTransaction
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|TransactionId
name|xid
parameter_list|,
name|boolean
name|onePhase
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|onePhase
condition|)
block|{
name|super
operator|.
name|commitTransaction
argument_list|(
name|context
argument_list|,
name|xid
argument_list|,
name|onePhase
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// die before doing the commit
comment|// so commit will hang as if reply is lost
name|context
operator|.
name|setDontSendReponse
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|Executors
operator|.
name|newSingleThreadExecutor
argument_list|()
operator|.
name|execute
argument_list|(
operator|new
name|Runnable
argument_list|()
block|{
specifier|public
name|void
name|run
parameter_list|()
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Stopping broker post commit..."
argument_list|)
expr_stmt|;
try|try
block|{
name|broker
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

begin_empty_stmt
unit|)
empty_stmt|;
end_empty_stmt

begin_expr_stmt
name|broker
operator|.
name|start
argument_list|()
expr_stmt|;
end_expr_stmt

begin_expr_stmt
unit|} catch
operator|(
name|Exception
name|e
operator|)
block|{
throw|throw
argument_list|new
name|RuntimeException
argument_list|(
literal|"Failed to start broker"
argument_list|,
name|e
argument_list|)
block|;         }
end_expr_stmt

begin_return
return|return
operator|new
name|ClassPathXmlApplicationContext
argument_list|(
literal|"org/apache/activemq/camel/jmsXajdbc.xml"
argument_list|)
return|;
end_return

unit|} }
end_unit

