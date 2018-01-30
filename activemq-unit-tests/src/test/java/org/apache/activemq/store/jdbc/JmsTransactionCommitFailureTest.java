begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License. You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|store
operator|.
name|jdbc
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
name|Assert
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
name|sql
operator|.
name|DataSource
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
name|lang
operator|.
name|reflect
operator|.
name|Field
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
name|sql
operator|.
name|Statement
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
name|Properties
import|;
end_import

begin_class
specifier|public
class|class
name|JmsTransactionCommitFailureTest
block|{
specifier|private
specifier|static
specifier|final
name|Log
name|LOGGER
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|JmsTransactionCommitFailureTest
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|OUTPUT_DIR
init|=
literal|"target/"
operator|+
name|JmsTransactionCommitFailureTest
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
decl_stmt|;
specifier|private
name|Properties
name|originalSystemProps
decl_stmt|;
specifier|private
name|DataSource
name|dataSource
decl_stmt|;
specifier|private
name|CommitFailurePersistenceAdapter
name|persistenceAdapter
decl_stmt|;
specifier|private
name|BrokerService
name|broker
decl_stmt|;
specifier|private
name|ConnectionFactory
name|connectionFactory
decl_stmt|;
specifier|private
name|int
name|messageCounter
init|=
literal|1
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
name|originalSystemProps
operator|=
name|System
operator|.
name|getProperties
argument_list|()
expr_stmt|;
name|Properties
name|systemProps
init|=
operator|(
name|Properties
operator|)
name|originalSystemProps
operator|.
name|clone
argument_list|()
decl_stmt|;
name|systemProps
operator|.
name|setProperty
argument_list|(
literal|"derby.stream.error.file"
argument_list|,
name|OUTPUT_DIR
operator|+
literal|"/derby.log"
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperties
argument_list|(
name|systemProps
argument_list|)
expr_stmt|;
name|dataSource
operator|=
name|createDataSource
argument_list|()
expr_stmt|;
name|persistenceAdapter
operator|=
operator|new
name|CommitFailurePersistenceAdapter
argument_list|(
name|dataSource
argument_list|)
expr_stmt|;
name|broker
operator|=
name|createBroker
argument_list|(
name|persistenceAdapter
argument_list|)
expr_stmt|;
name|broker
operator|.
name|start
argument_list|()
expr_stmt|;
name|connectionFactory
operator|=
name|createConnectionFactory
argument_list|(
name|broker
operator|.
name|getBrokerName
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|private
name|DataSource
name|createDataSource
parameter_list|()
block|{
name|EmbeddedDataSource
name|dataSource
init|=
operator|new
name|EmbeddedDataSource
argument_list|()
decl_stmt|;
name|dataSource
operator|.
name|setDatabaseName
argument_list|(
name|OUTPUT_DIR
operator|+
literal|"/derby-db"
argument_list|)
expr_stmt|;
name|dataSource
operator|.
name|setCreateDatabase
argument_list|(
literal|"create"
argument_list|)
expr_stmt|;
return|return
name|dataSource
return|;
block|}
specifier|private
name|BrokerService
name|createBroker
parameter_list|(
name|PersistenceAdapter
name|persistenceAdapter
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|brokerName
init|=
name|JmsTransactionCommitFailureTest
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
decl_stmt|;
name|BrokerService
name|broker
init|=
operator|new
name|BrokerService
argument_list|()
decl_stmt|;
name|broker
operator|.
name|setDataDirectory
argument_list|(
name|OUTPUT_DIR
operator|+
literal|"/activemq"
argument_list|)
expr_stmt|;
name|broker
operator|.
name|setBrokerName
argument_list|(
name|brokerName
argument_list|)
expr_stmt|;
name|broker
operator|.
name|setDeleteAllMessagesOnStartup
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
name|setUseJmx
argument_list|(
literal|false
argument_list|)
expr_stmt|;
if|if
condition|(
name|persistenceAdapter
operator|!=
literal|null
condition|)
block|{
name|broker
operator|.
name|setPersistent
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|broker
operator|.
name|setPersistenceAdapter
argument_list|(
name|persistenceAdapter
argument_list|)
expr_stmt|;
block|}
return|return
name|broker
return|;
block|}
specifier|private
name|ConnectionFactory
name|createConnectionFactory
parameter_list|(
name|String
name|brokerName
parameter_list|)
block|{
name|ActiveMQConnectionFactory
name|factory
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
literal|"vm://"
operator|+
name|brokerName
argument_list|)
decl_stmt|;
name|factory
operator|.
name|setWatchTopicAdvisories
argument_list|(
literal|false
argument_list|)
expr_stmt|;
return|return
name|factory
return|;
block|}
specifier|private
name|void
name|stopDataSource
parameter_list|()
block|{
if|if
condition|(
name|dataSource
operator|instanceof
name|EmbeddedDataSource
condition|)
block|{
name|EmbeddedDataSource
name|derbyDataSource
init|=
operator|(
name|EmbeddedDataSource
operator|)
name|dataSource
decl_stmt|;
name|derbyDataSource
operator|.
name|setShutdownDatabase
argument_list|(
literal|"shutdown"
argument_list|)
expr_stmt|;
try|try
block|{
name|derbyDataSource
operator|.
name|getConnection
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SQLException
name|ignored
parameter_list|)
block|{             }
block|}
block|}
specifier|private
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
name|broker
operator|=
literal|null
expr_stmt|;
block|}
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
try|try
block|{
name|stopBroker
argument_list|()
expr_stmt|;
name|stopDataSource
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|System
operator|.
name|setProperties
argument_list|(
name|originalSystemProps
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testJmsTransactionCommitFailure
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|queueName
init|=
literal|"testJmsTransactionCommitFailure"
decl_stmt|;
comment|// Send 1.message
name|sendMessage
argument_list|(
name|queueName
argument_list|,
literal|1
argument_list|)
expr_stmt|;
comment|// Check message count directly in database
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1L
argument_list|,
name|getMessageCount
argument_list|()
argument_list|)
expr_stmt|;
comment|// Set failure flag on persistence adapter
name|persistenceAdapter
operator|.
name|setCommitFailureEnabled
argument_list|(
literal|true
argument_list|)
expr_stmt|;
comment|// Send 2.message and 3.message in one JMS transaction
try|try
block|{
name|LOGGER
operator|.
name|warn
argument_list|(
literal|"Attempt to send Message-2/Message-3 (first time)..."
argument_list|)
expr_stmt|;
name|sendMessage
argument_list|(
name|queueName
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|LOGGER
operator|.
name|warn
argument_list|(
literal|"Message-2/Message-3 successfuly sent (first time)"
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|JMSException
name|jmse
parameter_list|)
block|{
comment|// Expected - decrease message counter (I want to repeat message send)
name|LOGGER
operator|.
name|warn
argument_list|(
literal|"Attempt to send Message-2/Message-3 failed"
argument_list|,
name|jmse
argument_list|)
expr_stmt|;
name|messageCounter
operator|-=
literal|2
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1L
argument_list|,
name|getMessageCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// Reset failure flag on persistence adapter
name|persistenceAdapter
operator|.
name|setCommitFailureEnabled
argument_list|(
literal|false
argument_list|)
expr_stmt|;
comment|// Send 2.message again
name|LOGGER
operator|.
name|warn
argument_list|(
literal|"Attempt to send Message-2/Message-3 (second time)..."
argument_list|)
expr_stmt|;
name|sendMessage
argument_list|(
name|queueName
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|LOGGER
operator|.
name|warn
argument_list|(
literal|"Message-2/Message-3 successfuly sent (second time)"
argument_list|)
expr_stmt|;
name|int
name|expectedMessageCount
init|=
literal|3
decl_stmt|;
comment|// Check message count directly in database
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|3L
argument_list|,
name|getMessageCount
argument_list|()
argument_list|)
expr_stmt|;
comment|// Attempt to receive 3 (expected) messages
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<=
name|expectedMessageCount
condition|;
name|i
operator|++
control|)
block|{
name|Message
name|message
init|=
name|receiveMessage
argument_list|(
name|queueName
argument_list|,
literal|10000
argument_list|)
decl_stmt|;
name|LOGGER
operator|.
name|warn
argument_list|(
name|i
operator|+
literal|". Message received ("
operator|+
name|message
operator|+
literal|")"
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|message
operator|instanceof
name|TextMessage
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|i
argument_list|,
name|message
operator|.
name|getIntProperty
argument_list|(
literal|"MessageId"
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Message-"
operator|+
name|i
argument_list|,
operator|(
operator|(
name|TextMessage
operator|)
name|message
operator|)
operator|.
name|getText
argument_list|()
argument_list|)
expr_stmt|;
comment|// Check message count directly in database
comment|//Assert.assertEquals(expectedMessageCount - i, getMessageCount());
block|}
comment|// Check message count directly in database
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|getMessageCount
argument_list|()
argument_list|)
expr_stmt|;
comment|// No next message is expected
name|Assert
operator|.
name|assertNull
argument_list|(
name|receiveMessage
argument_list|(
name|queueName
argument_list|,
literal|4000
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testQueueMemoryLeak
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|queueName
init|=
literal|"testMemoryLeak"
decl_stmt|;
name|sendMessage
argument_list|(
name|queueName
argument_list|,
literal|1
argument_list|)
expr_stmt|;
comment|// Set failure flag on persistence adapter
name|persistenceAdapter
operator|.
name|setCommitFailureEnabled
argument_list|(
literal|true
argument_list|)
expr_stmt|;
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
literal|10
condition|;
name|i
operator|++
control|)
block|{
try|try
block|{
name|sendMessage
argument_list|(
name|queueName
argument_list|,
literal|2
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|JMSException
name|jmse
parameter_list|)
block|{
comment|// Expected
block|}
block|}
block|}
finally|finally
block|{
name|persistenceAdapter
operator|.
name|setCommitFailureEnabled
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
name|Destination
name|destination
init|=
name|broker
operator|.
name|getDestination
argument_list|(
operator|new
name|ActiveMQQueue
argument_list|(
name|queueName
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|destination
operator|instanceof
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
condition|)
block|{
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
name|queue
init|=
operator|(
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
operator|)
name|destination
decl_stmt|;
name|Field
name|listField
init|=
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
operator|.
name|class
operator|.
name|getDeclaredField
argument_list|(
literal|"indexOrderedCursorUpdates"
argument_list|)
decl_stmt|;
name|listField
operator|.
name|setAccessible
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|?
argument_list|>
name|list
init|=
operator|(
name|List
argument_list|<
name|?
argument_list|>
operator|)
name|listField
operator|.
name|get
argument_list|(
name|queue
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|list
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testQueueMemoryLeakNoTx
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|queueName
init|=
literal|"testMemoryLeak"
decl_stmt|;
name|sendMessage
argument_list|(
name|queueName
argument_list|,
literal|1
argument_list|)
expr_stmt|;
comment|// Set failure flag on persistence adapter
name|persistenceAdapter
operator|.
name|setCommitFailureEnabled
argument_list|(
literal|true
argument_list|)
expr_stmt|;
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
literal|10
condition|;
name|i
operator|++
control|)
block|{
try|try
block|{
name|sendMessage
argument_list|(
name|queueName
argument_list|,
literal|2
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|JMSException
name|jmse
parameter_list|)
block|{
comment|// Expected
block|}
block|}
block|}
finally|finally
block|{
name|persistenceAdapter
operator|.
name|setCommitFailureEnabled
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
name|Destination
name|destination
init|=
name|broker
operator|.
name|getDestination
argument_list|(
operator|new
name|ActiveMQQueue
argument_list|(
name|queueName
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|destination
operator|instanceof
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
condition|)
block|{
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
name|queue
init|=
operator|(
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
operator|)
name|destination
decl_stmt|;
name|Field
name|listField
init|=
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
operator|.
name|class
operator|.
name|getDeclaredField
argument_list|(
literal|"indexOrderedCursorUpdates"
argument_list|)
decl_stmt|;
name|listField
operator|.
name|setAccessible
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|?
argument_list|>
name|list
init|=
operator|(
name|List
argument_list|<
name|?
argument_list|>
operator|)
name|listField
operator|.
name|get
argument_list|(
name|queue
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|list
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|sendMessage
parameter_list|(
name|String
name|queueName
parameter_list|,
name|int
name|count
parameter_list|)
throws|throws
name|JMSException
block|{
name|sendMessage
argument_list|(
name|queueName
argument_list|,
name|count
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|sendMessage
parameter_list|(
name|String
name|queueName
parameter_list|,
name|int
name|count
parameter_list|,
name|boolean
name|transacted
parameter_list|)
throws|throws
name|JMSException
block|{
name|Connection
name|con
init|=
name|connectionFactory
operator|.
name|createConnection
argument_list|()
decl_stmt|;
try|try
block|{
name|Session
name|session
init|=
name|con
operator|.
name|createSession
argument_list|(
name|transacted
argument_list|,
name|transacted
condition|?
name|Session
operator|.
name|SESSION_TRANSACTED
else|:
name|Session
operator|.
name|AUTO_ACKNOWLEDGE
argument_list|)
decl_stmt|;
try|try
block|{
name|Queue
name|destination
init|=
name|session
operator|.
name|createQueue
argument_list|(
name|queueName
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
name|count
condition|;
name|i
operator|++
control|)
block|{
name|TextMessage
name|message
init|=
name|session
operator|.
name|createTextMessage
argument_list|()
decl_stmt|;
name|message
operator|.
name|setIntProperty
argument_list|(
literal|"MessageId"
argument_list|,
name|messageCounter
argument_list|)
expr_stmt|;
name|message
operator|.
name|setText
argument_list|(
literal|"Message-"
operator|+
name|messageCounter
operator|++
argument_list|)
expr_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|message
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|transacted
condition|)
block|{
name|session
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|producer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|session
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|con
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
name|Message
name|receiveMessage
parameter_list|(
name|String
name|queueName
parameter_list|,
name|long
name|receiveTimeout
parameter_list|)
throws|throws
name|JMSException
block|{
name|Message
name|message
init|=
literal|null
decl_stmt|;
name|Connection
name|con
init|=
name|connectionFactory
operator|.
name|createConnection
argument_list|()
decl_stmt|;
try|try
block|{
name|con
operator|.
name|start
argument_list|()
expr_stmt|;
try|try
block|{
name|Session
name|session
init|=
name|con
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
try|try
block|{
name|Queue
name|destination
init|=
name|session
operator|.
name|createQueue
argument_list|(
name|queueName
argument_list|)
decl_stmt|;
name|MessageConsumer
name|consumer
init|=
name|session
operator|.
name|createConsumer
argument_list|(
name|destination
argument_list|)
decl_stmt|;
try|try
block|{
name|message
operator|=
name|consumer
operator|.
name|receive
argument_list|(
name|receiveTimeout
argument_list|)
expr_stmt|;
name|session
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|consumer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|session
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|con
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|con
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
return|return
name|message
return|;
block|}
specifier|private
name|long
name|getMessageCount
parameter_list|()
throws|throws
name|SQLException
block|{
name|long
name|messageCount
init|=
operator|-
literal|1
decl_stmt|;
name|java
operator|.
name|sql
operator|.
name|Connection
name|con
init|=
name|dataSource
operator|.
name|getConnection
argument_list|()
decl_stmt|;
try|try
block|{
name|Statement
name|stmt
init|=
name|con
operator|.
name|createStatement
argument_list|()
decl_stmt|;
try|try
block|{
name|ResultSet
name|rs
init|=
name|stmt
operator|.
name|executeQuery
argument_list|(
literal|"select count(*) from activemq_msgs"
argument_list|)
decl_stmt|;
try|try
block|{
while|while
condition|(
name|rs
operator|.
name|next
argument_list|()
condition|)
name|messageCount
operator|=
name|rs
operator|.
name|getLong
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|rs
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|stmt
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|con
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
return|return
name|messageCount
return|;
block|}
specifier|private
specifier|static
class|class
name|CommitFailurePersistenceAdapter
extends|extends
name|JDBCPersistenceAdapter
block|{
specifier|private
name|boolean
name|isCommitFailureEnabled
decl_stmt|;
specifier|private
name|int
name|transactionIsolation
decl_stmt|;
specifier|public
name|CommitFailurePersistenceAdapter
parameter_list|(
name|DataSource
name|dataSource
parameter_list|)
block|{
name|setDataSource
argument_list|(
name|dataSource
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setCommitFailureEnabled
parameter_list|(
name|boolean
name|isCommitFailureEnabled
parameter_list|)
block|{
name|this
operator|.
name|isCommitFailureEnabled
operator|=
name|isCommitFailureEnabled
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setTransactionIsolation
parameter_list|(
name|int
name|transactionIsolation
parameter_list|)
block|{
name|super
operator|.
name|setTransactionIsolation
argument_list|(
name|transactionIsolation
argument_list|)
expr_stmt|;
name|this
operator|.
name|transactionIsolation
operator|=
name|transactionIsolation
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|TransactionContext
name|getTransactionContext
parameter_list|()
throws|throws
name|IOException
block|{
name|TransactionContext
name|answer
init|=
operator|new
name|TransactionContext
argument_list|(
name|this
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|void
name|executeBatch
parameter_list|()
throws|throws
name|SQLException
block|{
if|if
condition|(
name|isCommitFailureEnabled
condition|)
block|{
throw|throw
operator|new
name|SQLException
argument_list|(
literal|"Test commit failure exception"
argument_list|)
throw|;
block|}
name|super
operator|.
name|executeBatch
argument_list|()
expr_stmt|;
block|}
block|}
decl_stmt|;
if|if
condition|(
name|transactionIsolation
operator|>
literal|0
condition|)
block|{
name|answer
operator|.
name|setTransactionIsolation
argument_list|(
name|transactionIsolation
argument_list|)
expr_stmt|;
block|}
return|return
name|answer
return|;
block|}
block|}
block|}
end_class

end_unit

