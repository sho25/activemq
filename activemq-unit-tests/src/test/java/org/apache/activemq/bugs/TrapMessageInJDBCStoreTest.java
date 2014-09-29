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
import|import
name|junit
operator|.
name|framework
operator|.
name|TestCase
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
name|command
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
name|activemq
operator|.
name|store
operator|.
name|jdbc
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
name|activemq
operator|.
name|util
operator|.
name|ByteSequence
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
name|IOHelper
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|derby
operator|.
name|jdbc
operator|.
name|EmbeddedDataSource
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
name|*
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
name|sql
operator|.
name|PreparedStatement
import|;
end_import

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
name|Executor
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
name|ScheduledExecutorService
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

begin_comment
comment|/**  * Test to demostrate a message trapped in the JDBC store and not  * delivered to consumer  *  * The test throws issues the commit to the DB but throws  * an exception back to the broker. This scenario could happen when a network  * cable is disconnected - message is committed to DB but broker does not know.  *  *  */
end_comment

begin_class
specifier|public
class|class
name|TrapMessageInJDBCStoreTest
extends|extends
name|TestCase
block|{
specifier|private
specifier|static
specifier|final
name|String
name|MY_TEST_Q
init|=
literal|"MY_TEST_Q"
decl_stmt|;
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
name|TrapMessageInJDBCStoreTest
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|String
name|transportUrl
init|=
literal|"tcp://127.0.0.1:0"
decl_stmt|;
specifier|private
name|BrokerService
name|broker
decl_stmt|;
specifier|private
name|TestTransactionContext
name|testTransactionContext
decl_stmt|;
specifier|private
name|TestJDBCPersistenceAdapter
name|jdbc
decl_stmt|;
specifier|protected
name|BrokerService
name|createBroker
parameter_list|(
name|boolean
name|withJMX
parameter_list|)
throws|throws
name|Exception
block|{
name|BrokerService
name|broker
init|=
operator|new
name|BrokerService
argument_list|()
decl_stmt|;
name|broker
operator|.
name|setUseJmx
argument_list|(
name|withJMX
argument_list|)
expr_stmt|;
name|EmbeddedDataSource
name|embeddedDataSource
init|=
operator|(
name|EmbeddedDataSource
operator|)
name|DataSourceServiceSupport
operator|.
name|createDataSource
argument_list|(
name|IOHelper
operator|.
name|getDefaultDataDirectory
argument_list|()
argument_list|)
decl_stmt|;
name|embeddedDataSource
operator|.
name|setCreateDatabase
argument_list|(
literal|"create"
argument_list|)
expr_stmt|;
comment|//wire in a TestTransactionContext (wrapper to TransactionContext) that has an executeBatch()
comment|// method that can be configured to throw a SQL exception on demand
name|jdbc
operator|=
operator|new
name|TestJDBCPersistenceAdapter
argument_list|()
expr_stmt|;
name|jdbc
operator|.
name|setDataSource
argument_list|(
name|embeddedDataSource
argument_list|)
expr_stmt|;
name|testTransactionContext
operator|=
operator|new
name|TestTransactionContext
argument_list|(
name|jdbc
argument_list|)
expr_stmt|;
name|jdbc
operator|.
name|setLockKeepAlivePeriod
argument_list|(
literal|1000l
argument_list|)
expr_stmt|;
name|LeaseDatabaseLocker
name|leaseDatabaseLocker
init|=
operator|new
name|LeaseDatabaseLocker
argument_list|()
decl_stmt|;
name|leaseDatabaseLocker
operator|.
name|setLockAcquireSleepInterval
argument_list|(
literal|2000l
argument_list|)
expr_stmt|;
name|jdbc
operator|.
name|setLocker
argument_list|(
name|leaseDatabaseLocker
argument_list|)
expr_stmt|;
name|broker
operator|.
name|setPersistenceAdapter
argument_list|(
name|jdbc
argument_list|)
expr_stmt|;
name|broker
operator|.
name|setIoExceptionHandler
argument_list|(
operator|new
name|JDBCIOExceptionHandler
argument_list|()
argument_list|)
expr_stmt|;
name|transportUrl
operator|=
name|broker
operator|.
name|addConnector
argument_list|(
name|transportUrl
argument_list|)
operator|.
name|getPublishableConnectString
argument_list|()
expr_stmt|;
return|return
name|broker
return|;
block|}
comment|/**      *      * sends 3 messages to the queue. When the second message is being committed to the JDBCStore, $      * it throws a dummy SQL exception - the message has been committed to the embedded DB before the exception      * is thrown      *      * Excepted correct outcome: receive 3 messages and the DB should contain no messages      *      * @throws Exception      */
specifier|public
name|void
name|testDBCommitException
parameter_list|()
throws|throws
name|Exception
block|{
name|broker
operator|=
name|this
operator|.
name|createBroker
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|broker
operator|.
name|deleteAllMessages
argument_list|()
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
name|LOG
operator|.
name|info
argument_list|(
literal|"***Broker started..."
argument_list|)
expr_stmt|;
comment|// failover but timeout in 5 seconds so the test does not hang
name|String
name|failoverTransportURL
init|=
literal|"failover:("
operator|+
name|transportUrl
operator|+
literal|")?timeout=5000"
decl_stmt|;
name|sendMessage
argument_list|(
name|MY_TEST_Q
argument_list|,
name|failoverTransportURL
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|TextMessage
argument_list|>
name|consumedMessages
init|=
name|consumeMessages
argument_list|(
name|MY_TEST_Q
argument_list|,
name|failoverTransportURL
argument_list|)
decl_stmt|;
comment|//check db contents
name|ArrayList
argument_list|<
name|Long
argument_list|>
name|dbSeq
init|=
name|dbMessageCount
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"*** db contains message seq "
operator|+
name|dbSeq
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"number of messages in DB after test"
argument_list|,
literal|0
argument_list|,
name|dbSeq
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"number of consumed messages"
argument_list|,
literal|3
argument_list|,
name|consumedMessages
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
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
specifier|public
name|List
argument_list|<
name|TextMessage
argument_list|>
name|consumeMessages
parameter_list|(
name|String
name|queue
parameter_list|,
name|String
name|transportURL
parameter_list|)
throws|throws
name|JMSException
block|{
name|Connection
name|connection
init|=
literal|null
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"*** consumeMessages() called ..."
argument_list|)
expr_stmt|;
try|try
block|{
name|ActiveMQConnectionFactory
name|factory
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
name|transportURL
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
name|Destination
name|destination
init|=
name|session
operator|.
name|createQueue
argument_list|(
name|queue
argument_list|)
decl_stmt|;
name|ArrayList
argument_list|<
name|TextMessage
argument_list|>
name|consumedMessages
init|=
operator|new
name|ArrayList
argument_list|<
name|TextMessage
argument_list|>
argument_list|()
decl_stmt|;
name|MessageConsumer
name|messageConsumer
init|=
name|session
operator|.
name|createConsumer
argument_list|(
name|destination
argument_list|)
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|TextMessage
name|textMessage
init|=
operator|(
name|TextMessage
operator|)
name|messageConsumer
operator|.
name|receive
argument_list|(
literal|100
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"*** consumed Messages :"
operator|+
name|textMessage
argument_list|)
expr_stmt|;
if|if
condition|(
name|textMessage
operator|==
literal|null
condition|)
block|{
return|return
name|consumedMessages
return|;
block|}
name|consumedMessages
operator|.
name|add
argument_list|(
name|textMessage
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
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
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
specifier|public
name|void
name|sendMessage
parameter_list|(
name|String
name|queue
parameter_list|,
name|String
name|transportURL
parameter_list|)
throws|throws
name|JMSException
block|{
name|Connection
name|connection
init|=
literal|null
decl_stmt|;
try|try
block|{
name|ActiveMQConnectionFactory
name|factory
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
name|transportURL
argument_list|)
decl_stmt|;
name|connection
operator|=
name|factory
operator|.
name|createConnection
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
name|Destination
name|destination
init|=
name|session
operator|.
name|createQueue
argument_list|(
name|queue
argument_list|)
decl_stmt|;
name|MessageProducer
name|producer
init|=
name|session
operator|.
name|createProducer
argument_list|(
name|destination
argument_list|)
decl_stmt|;
name|producer
operator|.
name|setDeliveryMode
argument_list|(
name|DeliveryMode
operator|.
name|PERSISTENT
argument_list|)
expr_stmt|;
name|TextMessage
name|m
init|=
name|session
operator|.
name|createTextMessage
argument_list|(
literal|"1"
argument_list|)
decl_stmt|;
name|testTransactionContext
operator|.
name|throwSQLException
operator|=
literal|false
expr_stmt|;
name|jdbc
operator|.
name|throwSQLException
operator|=
literal|false
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"*** send message 1 to broker..."
argument_list|)
expr_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|m
argument_list|)
expr_stmt|;
name|testTransactionContext
operator|.
name|throwSQLException
operator|=
literal|true
expr_stmt|;
name|jdbc
operator|.
name|throwSQLException
operator|=
literal|true
expr_stmt|;
comment|// trigger SQL exception in transactionContext
name|LOG
operator|.
name|debug
argument_list|(
literal|"***  send message 2 to broker"
argument_list|)
expr_stmt|;
name|m
operator|.
name|setText
argument_list|(
literal|"2"
argument_list|)
expr_stmt|;
comment|// need to reset the flag in a seperate thread during the send
name|ScheduledExecutorService
name|executor
init|=
name|Executors
operator|.
name|newSingleThreadScheduledExecutor
argument_list|()
decl_stmt|;
name|executor
operator|.
name|schedule
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
name|testTransactionContext
operator|.
name|throwSQLException
operator|=
literal|false
expr_stmt|;
name|jdbc
operator|.
name|throwSQLException
operator|=
literal|false
expr_stmt|;
block|}
block|}
argument_list|,
literal|2
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
expr_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|m
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"***  send  message 3 to broker"
argument_list|)
expr_stmt|;
name|m
operator|.
name|setText
argument_list|(
literal|"3"
argument_list|)
expr_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|m
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"*** Finished sending messages to broker"
argument_list|)
expr_stmt|;
block|}
finally|finally
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
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
comment|/**      *  query the DB to see what messages are left in the store      * @return      * @throws SQLException      * @throws IOException      */
specifier|private
name|ArrayList
argument_list|<
name|Long
argument_list|>
name|dbMessageCount
parameter_list|()
throws|throws
name|SQLException
throws|,
name|IOException
block|{
name|java
operator|.
name|sql
operator|.
name|Connection
name|conn
init|=
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
name|getDataSource
argument_list|()
operator|.
name|getConnection
argument_list|()
decl_stmt|;
name|PreparedStatement
name|statement
init|=
name|conn
operator|.
name|prepareStatement
argument_list|(
literal|"SELECT MSGID_SEQ FROM ACTIVEMQ_MSGS"
argument_list|)
decl_stmt|;
try|try
block|{
name|ResultSet
name|result
init|=
name|statement
operator|.
name|executeQuery
argument_list|()
decl_stmt|;
name|ArrayList
argument_list|<
name|Long
argument_list|>
name|dbSeq
init|=
operator|new
name|ArrayList
argument_list|<
name|Long
argument_list|>
argument_list|()
decl_stmt|;
while|while
condition|(
name|result
operator|.
name|next
argument_list|()
condition|)
block|{
name|dbSeq
operator|.
name|add
argument_list|(
name|result
operator|.
name|getLong
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|dbSeq
return|;
block|}
finally|finally
block|{
name|statement
operator|.
name|close
argument_list|()
expr_stmt|;
name|conn
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
comment|/*      * Mock classes used for testing 	 */
specifier|public
class|class
name|TestJDBCPersistenceAdapter
extends|extends
name|JDBCPersistenceAdapter
block|{
specifier|public
name|boolean
name|throwSQLException
decl_stmt|;
specifier|public
name|TransactionContext
name|getTransactionContext
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|testTransactionContext
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|checkpoint
parameter_list|(
name|boolean
name|sync
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|throwSQLException
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"checkpoint failed"
argument_list|)
throw|;
block|}
name|super
operator|.
name|checkpoint
argument_list|(
name|sync
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
class|class
name|TestTransactionContext
extends|extends
name|TransactionContext
block|{
specifier|public
name|boolean
name|throwSQLException
decl_stmt|;
specifier|public
name|TestTransactionContext
parameter_list|(
name|JDBCPersistenceAdapter
name|jdbcPersistenceAdapter
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|jdbcPersistenceAdapter
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|executeBatch
parameter_list|()
throws|throws
name|SQLException
block|{
comment|//call
name|super
operator|.
name|executeBatch
argument_list|()
expr_stmt|;
if|if
condition|(
name|throwSQLException
condition|)
block|{
throw|throw
operator|new
name|SQLException
argument_list|(
literal|"TEST SQL EXCEPTION from executeBatch after super. execution"
argument_list|)
throw|;
block|}
block|}
block|}
block|}
end_class

end_unit

