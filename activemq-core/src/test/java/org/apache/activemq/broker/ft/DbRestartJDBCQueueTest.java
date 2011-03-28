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
name|broker
operator|.
name|ft
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|CountDownLatch
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
name|Destination
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|ExceptionListener
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
name|JmsTopicSendReceiveWithTwoConnectionsTest
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
name|DataSourceSupport
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
name|util
operator|.
name|DefaultIOExceptionHandler
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

begin_class
specifier|public
class|class
name|DbRestartJDBCQueueTest
extends|extends
name|JmsTopicSendReceiveWithTwoConnectionsTest
implements|implements
name|ExceptionListener
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
name|DbRestartJDBCQueueTest
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
name|boolean
name|transactedSends
init|=
literal|false
decl_stmt|;
specifier|public
name|int
name|failureCount
init|=
literal|25
decl_stmt|;
comment|// or 20 for even tx batch boundary
name|int
name|inflightMessageCount
init|=
literal|0
decl_stmt|;
name|EmbeddedDataSource
name|sharedDs
decl_stmt|;
name|BrokerService
name|broker
decl_stmt|;
specifier|final
name|CountDownLatch
name|restartDBLatch
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
decl_stmt|;
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|setAutoFail
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|topic
operator|=
literal|false
expr_stmt|;
name|verbose
operator|=
literal|true
expr_stmt|;
comment|// startup db
name|sharedDs
operator|=
operator|(
name|EmbeddedDataSource
operator|)
operator|new
name|DataSourceSupport
argument_list|()
operator|.
name|getDataSource
argument_list|()
expr_stmt|;
name|broker
operator|=
operator|new
name|BrokerService
argument_list|()
expr_stmt|;
name|DefaultIOExceptionHandler
name|handler
init|=
operator|new
name|DefaultIOExceptionHandler
argument_list|()
decl_stmt|;
name|handler
operator|.
name|setIgnoreSQLExceptions
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|handler
operator|.
name|setStopStartConnectors
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|broker
operator|.
name|setIoExceptionHandler
argument_list|(
name|handler
argument_list|)
expr_stmt|;
name|broker
operator|.
name|addConnector
argument_list|(
literal|"tcp://localhost:0"
argument_list|)
expr_stmt|;
name|broker
operator|.
name|setUseJmx
argument_list|(
literal|false
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
name|setDeleteAllMessagesOnStartup
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|JDBCPersistenceAdapter
name|persistenceAdapter
init|=
operator|new
name|JDBCPersistenceAdapter
argument_list|()
decl_stmt|;
name|persistenceAdapter
operator|.
name|setDataSource
argument_list|(
name|sharedDs
argument_list|)
expr_stmt|;
name|persistenceAdapter
operator|.
name|setUseDatabaseLock
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|persistenceAdapter
operator|.
name|setLockKeepAlivePeriod
argument_list|(
literal|500
argument_list|)
expr_stmt|;
name|persistenceAdapter
operator|.
name|setLockAcquireSleepInterval
argument_list|(
literal|500
argument_list|)
expr_stmt|;
name|broker
operator|.
name|setPersistenceAdapter
argument_list|(
name|persistenceAdapter
argument_list|)
expr_stmt|;
name|broker
operator|.
name|start
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
name|broker
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
specifier|protected
name|Session
name|createSendSession
parameter_list|(
name|Connection
name|sendConnection
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|transactedSends
condition|)
block|{
return|return
name|sendConnection
operator|.
name|createSession
argument_list|(
literal|true
argument_list|,
name|Session
operator|.
name|SESSION_TRANSACTED
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|sendConnection
operator|.
name|createSession
argument_list|(
literal|false
argument_list|,
name|Session
operator|.
name|AUTO_ACKNOWLEDGE
argument_list|)
return|;
block|}
block|}
specifier|protected
name|ActiveMQConnectionFactory
name|createConnectionFactory
parameter_list|()
throws|throws
name|Exception
block|{
name|ActiveMQConnectionFactory
name|f
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
literal|"failover://"
operator|+
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
argument_list|)
decl_stmt|;
name|f
operator|.
name|setExceptionListener
argument_list|(
name|this
argument_list|)
expr_stmt|;
return|return
name|f
return|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|messageSent
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
operator|++
name|inflightMessageCount
operator|==
name|failureCount
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"STOPPING DB!@!!!!"
argument_list|)
expr_stmt|;
specifier|final
name|EmbeddedDataSource
name|ds
init|=
name|sharedDs
decl_stmt|;
name|ds
operator|.
name|setShutdownDatabase
argument_list|(
literal|"shutdown"
argument_list|)
expr_stmt|;
try|try
block|{
name|ds
operator|.
name|getConnection
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ignored
parameter_list|)
block|{             }
name|LOG
operator|.
name|info
argument_list|(
literal|"DB STOPPED!@!!!!"
argument_list|)
expr_stmt|;
name|Thread
name|dbRestartThread
init|=
operator|new
name|Thread
argument_list|(
literal|"db-re-start-thread"
argument_list|)
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
literal|"Sleeping for 10 seconds before allowing db restart"
argument_list|)
expr_stmt|;
try|try
block|{
name|restartDBLatch
operator|.
name|await
argument_list|(
literal|10
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
name|ds
operator|.
name|setShutdownDatabase
argument_list|(
literal|"false"
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"DB RESTARTED!@!!!!"
argument_list|)
expr_stmt|;
block|}
block|}
decl_stmt|;
name|dbRestartThread
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
block|}
specifier|protected
name|void
name|sendToProducer
parameter_list|(
name|MessageProducer
name|producer
parameter_list|,
name|Destination
name|producerDestination
parameter_list|,
name|Message
name|message
parameter_list|)
throws|throws
name|JMSException
block|{
block|{
comment|// do some retries as db failures filter back to the client until broker sees
comment|// db lock failure and shuts down
name|boolean
name|sent
init|=
literal|false
decl_stmt|;
do|do
block|{
try|try
block|{
name|producer
operator|.
name|send
argument_list|(
name|producerDestination
argument_list|,
name|message
argument_list|)
expr_stmt|;
if|if
condition|(
name|transactedSends
operator|&&
operator|(
operator|(
name|inflightMessageCount
operator|+
literal|1
operator|)
operator|%
literal|10
operator|==
literal|0
operator|||
operator|(
name|inflightMessageCount
operator|+
literal|1
operator|)
operator|>=
name|messageCount
operator|)
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"committing on send: "
operator|+
name|inflightMessageCount
operator|+
literal|" message: "
operator|+
name|message
argument_list|)
expr_stmt|;
name|session
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
name|sent
operator|=
literal|true
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|JMSException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Exception on producer send:"
argument_list|,
name|e
argument_list|)
expr_stmt|;
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|2000
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ignored
parameter_list|)
block|{                     }
block|}
block|}
do|while
condition|(
operator|!
name|sent
condition|)
do|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|onException
parameter_list|(
name|JMSException
name|exception
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"exception on connection: "
argument_list|,
name|exception
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

