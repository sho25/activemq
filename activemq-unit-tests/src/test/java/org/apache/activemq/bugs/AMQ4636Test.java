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
name|SQLException
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
name|DeliveryMode
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
name|JMSException
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
name|Topic
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|TopicSubscriber
import|;
end_import

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
name|store
operator|.
name|jdbc
operator|.
name|DataSourceServiceSupport
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
name|JDBCIOExceptionHandler
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
name|jdbc
operator|.
name|LeaseDatabaseLocker
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
name|TransactionContext
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

begin_comment
comment|/**  * Testing how the broker reacts when a SQL Exception is thrown from  * org.apache.activemq.store.jdbc.TransactionContext.executeBatch().  *<p/>  * see https://issues.apache.org/jira/browse/AMQ-4636  */
end_comment

begin_class
specifier|public
class|class
name|AMQ4636Test
extends|extends
name|TestCase
block|{
specifier|private
specifier|static
specifier|final
name|String
name|MY_TEST_TOPIC
init|=
literal|"MY_TEST_TOPIC"
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
name|AMQ4636Test
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|String
name|transportUrl
init|=
literal|"tcp://0.0.0.0:0"
decl_stmt|;
specifier|private
name|BrokerService
name|broker
decl_stmt|;
specifier|private
name|TestTransactionContext
name|testTransactionContext
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
name|JDBCPersistenceAdapter
name|jdbc
init|=
operator|new
name|TestJDBCPersistenceAdapter
argument_list|()
decl_stmt|;
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
comment|/**      * adding a TestTransactionContext (wrapper to TransactionContext) so an SQLException is triggered      * during TransactionContext.executeBatch() when called in the broker.      *<p/>      * Expectation: SQLException triggers a connection shutdown and failover should kick and try to redeliver the      * message. SQLException should NOT be returned to client      */
specifier|public
name|void
name|testProducerWithDBShutdown
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
comment|// failover but timeout in 1 seconds so the test does not hang
name|String
name|failoverTransportURL
init|=
literal|"failover:("
operator|+
name|transportUrl
operator|+
literal|")?timeout=1000"
decl_stmt|;
name|this
operator|.
name|createDurableConsumer
argument_list|(
name|MY_TEST_TOPIC
argument_list|,
name|failoverTransportURL
argument_list|)
expr_stmt|;
name|this
operator|.
name|sendMessage
argument_list|(
name|MY_TEST_TOPIC
argument_list|,
name|failoverTransportURL
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|createDurableConsumer
parameter_list|(
name|String
name|topic
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
name|info
argument_list|(
literal|"*** createDurableConsumer() called ..."
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
name|setClientID
argument_list|(
literal|"myconn1"
argument_list|)
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
name|createTopic
argument_list|(
name|topic
argument_list|)
decl_stmt|;
name|TopicSubscriber
name|topicSubscriber
init|=
name|session
operator|.
name|createDurableSubscriber
argument_list|(
operator|(
name|Topic
operator|)
name|destination
argument_list|,
literal|"MySub1"
argument_list|)
decl_stmt|;
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
name|topic
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
name|createTopic
argument_list|(
name|topic
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
name|Message
name|m
init|=
name|session
operator|.
name|createTextMessage
argument_list|(
literal|"testMessage"
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"*** send message to broker..."
argument_list|)
expr_stmt|;
comment|// trigger SQL exception in transactionContext
name|testTransactionContext
operator|.
name|throwSQLException
operator|=
literal|true
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
name|info
argument_list|(
literal|"*** Finished send message to broker"
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
comment|/*      * Mock classes used for testing 	 */
specifier|public
class|class
name|TestJDBCPersistenceAdapter
extends|extends
name|JDBCPersistenceAdapter
block|{
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
if|if
condition|(
name|throwSQLException
condition|)
block|{
comment|// only throw exception once
name|throwSQLException
operator|=
literal|false
expr_stmt|;
throw|throw
operator|new
name|SQLException
argument_list|(
literal|"TEST SQL EXCEPTION"
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
block|}
end_class

end_unit
