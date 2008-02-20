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
name|store
operator|.
name|jdbc
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
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
name|SQLException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
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
name|ScheduledFuture
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
name|ScheduledThreadPoolExecutor
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
name|ThreadFactory
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
name|sql
operator|.
name|DataSource
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
name|BrokerServiceAware
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
name|openwire
operator|.
name|OpenWireFormat
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
name|MessageStore
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
name|TopicMessageStore
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
name|TransactionStore
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
name|adapter
operator|.
name|DefaultJDBCAdapter
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
name|MemoryTransactionStore
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
name|usage
operator|.
name|SystemUsage
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
name|FactoryFinder
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
name|IOExceptionSupport
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
name|wireformat
operator|.
name|WireFormat
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

begin_comment
comment|/**  * A {@link PersistenceAdapter} implementation using JDBC for persistence  * storage.  *   * This persistence adapter will correctly remember prepared XA transactions,  * but it will not keep track of local transaction commits so that operations  * performed against the Message store are done as a single uow.  *   * @org.apache.xbean.XBean element="jdbcPersistenceAdapter"  *   * @version $Revision: 1.9 $  */
end_comment

begin_class
specifier|public
class|class
name|JDBCPersistenceAdapter
extends|extends
name|DataSourceSupport
implements|implements
name|PersistenceAdapter
implements|,
name|BrokerServiceAware
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
name|JDBCPersistenceAdapter
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|static
name|FactoryFinder
name|factoryFinder
init|=
operator|new
name|FactoryFinder
argument_list|(
literal|"META-INF/services/org/apache/activemq/store/jdbc/"
argument_list|)
decl_stmt|;
specifier|private
name|WireFormat
name|wireFormat
init|=
operator|new
name|OpenWireFormat
argument_list|()
decl_stmt|;
specifier|private
name|BrokerService
name|brokerService
decl_stmt|;
specifier|private
name|Statements
name|statements
decl_stmt|;
specifier|private
name|JDBCAdapter
name|adapter
decl_stmt|;
specifier|private
name|MemoryTransactionStore
name|transactionStore
decl_stmt|;
specifier|private
name|ScheduledThreadPoolExecutor
name|clockDaemon
decl_stmt|;
specifier|private
name|ScheduledFuture
name|clockTicket
decl_stmt|;
specifier|private
name|int
name|cleanupPeriod
init|=
literal|1000
operator|*
literal|60
operator|*
literal|5
decl_stmt|;
specifier|private
name|boolean
name|useExternalMessageReferences
decl_stmt|;
specifier|private
name|boolean
name|useDatabaseLock
init|=
literal|true
decl_stmt|;
specifier|private
name|long
name|lockKeepAlivePeriod
init|=
literal|1000
operator|*
literal|30
decl_stmt|;
specifier|private
name|DatabaseLocker
name|databaseLocker
decl_stmt|;
specifier|private
name|boolean
name|createTablesOnStartup
init|=
literal|true
decl_stmt|;
specifier|public
name|JDBCPersistenceAdapter
parameter_list|()
block|{     }
specifier|public
name|JDBCPersistenceAdapter
parameter_list|(
name|DataSource
name|ds
parameter_list|,
name|WireFormat
name|wireFormat
parameter_list|)
block|{
name|super
argument_list|(
name|ds
argument_list|)
expr_stmt|;
name|this
operator|.
name|wireFormat
operator|=
name|wireFormat
expr_stmt|;
block|}
specifier|public
name|Set
argument_list|<
name|ActiveMQDestination
argument_list|>
name|getDestinations
parameter_list|()
block|{
comment|// Get a connection and insert the message into the DB.
name|TransactionContext
name|c
init|=
literal|null
decl_stmt|;
try|try
block|{
name|c
operator|=
name|getTransactionContext
argument_list|()
expr_stmt|;
return|return
name|getAdapter
argument_list|()
operator|.
name|doGetDestinations
argument_list|(
name|c
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
return|return
name|emptyDestinationSet
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|SQLException
name|e
parameter_list|)
block|{
name|JDBCPersistenceAdapter
operator|.
name|log
argument_list|(
literal|"JDBC Failure: "
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return
name|emptyDestinationSet
argument_list|()
return|;
block|}
finally|finally
block|{
if|if
condition|(
name|c
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|c
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{                 }
block|}
block|}
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
specifier|private
name|Set
argument_list|<
name|ActiveMQDestination
argument_list|>
name|emptyDestinationSet
parameter_list|()
block|{
return|return
name|Collections
operator|.
name|EMPTY_SET
return|;
block|}
specifier|public
name|MessageStore
name|createQueueMessageStore
parameter_list|(
name|ActiveMQQueue
name|destination
parameter_list|)
throws|throws
name|IOException
block|{
name|MessageStore
name|rc
init|=
operator|new
name|JDBCMessageStore
argument_list|(
name|this
argument_list|,
name|getAdapter
argument_list|()
argument_list|,
name|wireFormat
argument_list|,
name|destination
argument_list|)
decl_stmt|;
if|if
condition|(
name|transactionStore
operator|!=
literal|null
condition|)
block|{
name|rc
operator|=
name|transactionStore
operator|.
name|proxy
argument_list|(
name|rc
argument_list|)
expr_stmt|;
block|}
return|return
name|rc
return|;
block|}
specifier|public
name|TopicMessageStore
name|createTopicMessageStore
parameter_list|(
name|ActiveMQTopic
name|destination
parameter_list|)
throws|throws
name|IOException
block|{
name|TopicMessageStore
name|rc
init|=
operator|new
name|JDBCTopicMessageStore
argument_list|(
name|this
argument_list|,
name|getAdapter
argument_list|()
argument_list|,
name|wireFormat
argument_list|,
name|destination
argument_list|)
decl_stmt|;
if|if
condition|(
name|transactionStore
operator|!=
literal|null
condition|)
block|{
name|rc
operator|=
name|transactionStore
operator|.
name|proxy
argument_list|(
name|rc
argument_list|)
expr_stmt|;
block|}
return|return
name|rc
return|;
block|}
specifier|public
name|TransactionStore
name|createTransactionStore
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|transactionStore
operator|==
literal|null
condition|)
block|{
name|transactionStore
operator|=
operator|new
name|MemoryTransactionStore
argument_list|()
expr_stmt|;
block|}
return|return
name|this
operator|.
name|transactionStore
return|;
block|}
specifier|public
name|long
name|getLastMessageBrokerSequenceId
parameter_list|()
throws|throws
name|IOException
block|{
comment|// Get a connection and insert the message into the DB.
name|TransactionContext
name|c
init|=
name|getTransactionContext
argument_list|()
decl_stmt|;
try|try
block|{
return|return
name|getAdapter
argument_list|()
operator|.
name|doGetLastMessageBrokerSequenceId
argument_list|(
name|c
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|SQLException
name|e
parameter_list|)
block|{
name|JDBCPersistenceAdapter
operator|.
name|log
argument_list|(
literal|"JDBC Failure: "
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
name|IOExceptionSupport
operator|.
name|create
argument_list|(
literal|"Failed to get last broker message id: "
operator|+
name|e
argument_list|,
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
name|c
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|start
parameter_list|()
throws|throws
name|Exception
block|{
name|getAdapter
argument_list|()
operator|.
name|setUseExternalMessageReferences
argument_list|(
name|isUseExternalMessageReferences
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|isCreateTablesOnStartup
argument_list|()
condition|)
block|{
name|TransactionContext
name|transactionContext
init|=
name|getTransactionContext
argument_list|()
decl_stmt|;
name|transactionContext
operator|.
name|begin
argument_list|()
expr_stmt|;
try|try
block|{
try|try
block|{
name|getAdapter
argument_list|()
operator|.
name|doCreateTables
argument_list|(
name|transactionContext
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SQLException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Cannot create tables due to: "
operator|+
name|e
argument_list|)
expr_stmt|;
name|JDBCPersistenceAdapter
operator|.
name|log
argument_list|(
literal|"Failure Details: "
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|transactionContext
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
block|}
if|if
condition|(
name|isUseDatabaseLock
argument_list|()
condition|)
block|{
name|DatabaseLocker
name|service
init|=
name|getDatabaseLocker
argument_list|()
decl_stmt|;
if|if
condition|(
name|service
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"No databaseLocker configured for the JDBC Persistence Adapter"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|service
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
block|}
name|cleanup
argument_list|()
expr_stmt|;
comment|// Cleanup the db periodically.
if|if
condition|(
name|cleanupPeriod
operator|>
literal|0
condition|)
block|{
name|clockTicket
operator|=
name|getScheduledThreadPoolExecutor
argument_list|()
operator|.
name|scheduleAtFixedRate
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
name|cleanup
argument_list|()
expr_stmt|;
block|}
block|}
argument_list|,
name|cleanupPeriod
argument_list|,
name|cleanupPeriod
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
specifier|synchronized
name|void
name|stop
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|clockTicket
operator|!=
literal|null
condition|)
block|{
name|clockTicket
operator|.
name|cancel
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|clockTicket
operator|=
literal|null
expr_stmt|;
block|}
if|if
condition|(
name|clockDaemon
operator|!=
literal|null
condition|)
block|{
name|clockDaemon
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|clockDaemon
operator|=
literal|null
expr_stmt|;
block|}
name|DatabaseLocker
name|service
init|=
name|getDatabaseLocker
argument_list|()
decl_stmt|;
if|if
condition|(
name|service
operator|!=
literal|null
condition|)
block|{
name|service
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|cleanup
parameter_list|()
block|{
name|TransactionContext
name|c
init|=
literal|null
decl_stmt|;
try|try
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Cleaning up old messages."
argument_list|)
expr_stmt|;
name|c
operator|=
name|getTransactionContext
argument_list|()
expr_stmt|;
name|getAdapter
argument_list|()
operator|.
name|doDeleteOldMessages
argument_list|(
name|c
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Old message cleanup failed due to: "
operator|+
name|e
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SQLException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Old message cleanup failed due to: "
operator|+
name|e
argument_list|)
expr_stmt|;
name|JDBCPersistenceAdapter
operator|.
name|log
argument_list|(
literal|"Failure Details: "
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|c
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|c
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{                 }
block|}
name|LOG
operator|.
name|debug
argument_list|(
literal|"Cleanup done."
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|setScheduledThreadPoolExecutor
parameter_list|(
name|ScheduledThreadPoolExecutor
name|clockDaemon
parameter_list|)
block|{
name|this
operator|.
name|clockDaemon
operator|=
name|clockDaemon
expr_stmt|;
block|}
specifier|public
name|ScheduledThreadPoolExecutor
name|getScheduledThreadPoolExecutor
parameter_list|()
block|{
if|if
condition|(
name|clockDaemon
operator|==
literal|null
condition|)
block|{
name|clockDaemon
operator|=
operator|new
name|ScheduledThreadPoolExecutor
argument_list|(
literal|5
argument_list|,
operator|new
name|ThreadFactory
argument_list|()
block|{
specifier|public
name|Thread
name|newThread
parameter_list|(
name|Runnable
name|runnable
parameter_list|)
block|{
name|Thread
name|thread
init|=
operator|new
name|Thread
argument_list|(
name|runnable
argument_list|,
literal|"ActiveMQ Cleanup Timer"
argument_list|)
decl_stmt|;
name|thread
operator|.
name|setDaemon
argument_list|(
literal|true
argument_list|)
expr_stmt|;
return|return
name|thread
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
return|return
name|clockDaemon
return|;
block|}
specifier|public
name|JDBCAdapter
name|getAdapter
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|adapter
operator|==
literal|null
condition|)
block|{
name|setAdapter
argument_list|(
name|createAdapter
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|adapter
return|;
block|}
specifier|public
name|DatabaseLocker
name|getDatabaseLocker
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|databaseLocker
operator|==
literal|null
condition|)
block|{
name|databaseLocker
operator|=
name|createDatabaseLocker
argument_list|()
expr_stmt|;
if|if
condition|(
name|lockKeepAlivePeriod
operator|>
literal|0
condition|)
block|{
name|getScheduledThreadPoolExecutor
argument_list|()
operator|.
name|scheduleAtFixedRate
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
name|databaseLockKeepAlive
argument_list|()
expr_stmt|;
block|}
block|}
argument_list|,
name|lockKeepAlivePeriod
argument_list|,
name|lockKeepAlivePeriod
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|databaseLocker
return|;
block|}
comment|/**      * Sets the database locker strategy to use to lock the database on startup      */
specifier|public
name|void
name|setDatabaseLocker
parameter_list|(
name|DatabaseLocker
name|databaseLocker
parameter_list|)
block|{
name|this
operator|.
name|databaseLocker
operator|=
name|databaseLocker
expr_stmt|;
block|}
specifier|public
name|BrokerService
name|getBrokerService
parameter_list|()
block|{
return|return
name|brokerService
return|;
block|}
specifier|public
name|void
name|setBrokerService
parameter_list|(
name|BrokerService
name|brokerService
parameter_list|)
block|{
name|this
operator|.
name|brokerService
operator|=
name|brokerService
expr_stmt|;
block|}
comment|/**      * @throws IOException      */
specifier|protected
name|JDBCAdapter
name|createAdapter
parameter_list|()
throws|throws
name|IOException
block|{
name|JDBCAdapter
name|adapter
init|=
literal|null
decl_stmt|;
name|TransactionContext
name|c
init|=
name|getTransactionContext
argument_list|()
decl_stmt|;
try|try
block|{
try|try
block|{
comment|// Make the filename file system safe.
name|String
name|dirverName
init|=
name|c
operator|.
name|getConnection
argument_list|()
operator|.
name|getMetaData
argument_list|()
operator|.
name|getDriverName
argument_list|()
decl_stmt|;
name|dirverName
operator|=
name|dirverName
operator|.
name|replaceAll
argument_list|(
literal|"[^a-zA-Z0-9\\-]"
argument_list|,
literal|"_"
argument_list|)
operator|.
name|toLowerCase
argument_list|()
expr_stmt|;
try|try
block|{
name|adapter
operator|=
operator|(
name|DefaultJDBCAdapter
operator|)
name|factoryFinder
operator|.
name|newInstance
argument_list|(
name|dirverName
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Database driver recognized: ["
operator|+
name|dirverName
operator|+
literal|"]"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Database driver NOT recognized: ["
operator|+
name|dirverName
operator|+
literal|"].  Will use default JDBC implementation."
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|SQLException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"JDBC error occurred while trying to detect database type.  Will use default JDBC implementation: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
name|JDBCPersistenceAdapter
operator|.
name|log
argument_list|(
literal|"Failure Details: "
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
comment|// Use the default JDBC adapter if the
comment|// Database type is not recognized.
if|if
condition|(
name|adapter
operator|==
literal|null
condition|)
block|{
name|adapter
operator|=
operator|new
name|DefaultJDBCAdapter
argument_list|()
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|c
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
return|return
name|adapter
return|;
block|}
specifier|public
name|void
name|setAdapter
parameter_list|(
name|JDBCAdapter
name|adapter
parameter_list|)
block|{
name|this
operator|.
name|adapter
operator|=
name|adapter
expr_stmt|;
name|this
operator|.
name|adapter
operator|.
name|setStatements
argument_list|(
name|getStatements
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|WireFormat
name|getWireFormat
parameter_list|()
block|{
return|return
name|wireFormat
return|;
block|}
specifier|public
name|void
name|setWireFormat
parameter_list|(
name|WireFormat
name|wireFormat
parameter_list|)
block|{
name|this
operator|.
name|wireFormat
operator|=
name|wireFormat
expr_stmt|;
block|}
specifier|public
name|TransactionContext
name|getTransactionContext
parameter_list|(
name|ConnectionContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|context
operator|==
literal|null
condition|)
block|{
return|return
name|getTransactionContext
argument_list|()
return|;
block|}
else|else
block|{
name|TransactionContext
name|answer
init|=
operator|(
name|TransactionContext
operator|)
name|context
operator|.
name|getLongTermStoreContext
argument_list|()
decl_stmt|;
if|if
condition|(
name|answer
operator|==
literal|null
condition|)
block|{
name|answer
operator|=
operator|new
name|TransactionContext
argument_list|(
name|getDataSource
argument_list|()
argument_list|)
expr_stmt|;
name|context
operator|.
name|setLongTermStoreContext
argument_list|(
name|answer
argument_list|)
expr_stmt|;
block|}
return|return
name|answer
return|;
block|}
block|}
specifier|public
name|TransactionContext
name|getTransactionContext
parameter_list|()
throws|throws
name|IOException
block|{
return|return
operator|new
name|TransactionContext
argument_list|(
name|getDataSource
argument_list|()
argument_list|)
return|;
block|}
specifier|public
name|void
name|beginTransaction
parameter_list|(
name|ConnectionContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|TransactionContext
name|transactionContext
init|=
name|getTransactionContext
argument_list|(
name|context
argument_list|)
decl_stmt|;
name|transactionContext
operator|.
name|begin
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|commitTransaction
parameter_list|(
name|ConnectionContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|TransactionContext
name|transactionContext
init|=
name|getTransactionContext
argument_list|(
name|context
argument_list|)
decl_stmt|;
name|transactionContext
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|rollbackTransaction
parameter_list|(
name|ConnectionContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|TransactionContext
name|transactionContext
init|=
name|getTransactionContext
argument_list|(
name|context
argument_list|)
decl_stmt|;
name|transactionContext
operator|.
name|rollback
argument_list|()
expr_stmt|;
block|}
specifier|public
name|int
name|getCleanupPeriod
parameter_list|()
block|{
return|return
name|cleanupPeriod
return|;
block|}
comment|/**      * Sets the number of milliseconds until the database is attempted to be      * cleaned up for durable topics      */
specifier|public
name|void
name|setCleanupPeriod
parameter_list|(
name|int
name|cleanupPeriod
parameter_list|)
block|{
name|this
operator|.
name|cleanupPeriod
operator|=
name|cleanupPeriod
expr_stmt|;
block|}
specifier|public
name|void
name|deleteAllMessages
parameter_list|()
throws|throws
name|IOException
block|{
name|TransactionContext
name|c
init|=
name|getTransactionContext
argument_list|()
decl_stmt|;
try|try
block|{
name|getAdapter
argument_list|()
operator|.
name|doDropTables
argument_list|(
name|c
argument_list|)
expr_stmt|;
name|getAdapter
argument_list|()
operator|.
name|setUseExternalMessageReferences
argument_list|(
name|isUseExternalMessageReferences
argument_list|()
argument_list|)
expr_stmt|;
name|getAdapter
argument_list|()
operator|.
name|doCreateTables
argument_list|(
name|c
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SQLException
name|e
parameter_list|)
block|{
name|JDBCPersistenceAdapter
operator|.
name|log
argument_list|(
literal|"JDBC Failure: "
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
name|IOExceptionSupport
operator|.
name|create
argument_list|(
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
name|c
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|boolean
name|isUseExternalMessageReferences
parameter_list|()
block|{
return|return
name|useExternalMessageReferences
return|;
block|}
specifier|public
name|void
name|setUseExternalMessageReferences
parameter_list|(
name|boolean
name|useExternalMessageReferences
parameter_list|)
block|{
name|this
operator|.
name|useExternalMessageReferences
operator|=
name|useExternalMessageReferences
expr_stmt|;
block|}
specifier|public
name|boolean
name|isCreateTablesOnStartup
parameter_list|()
block|{
return|return
name|createTablesOnStartup
return|;
block|}
comment|/**      * Sets whether or not tables are created on startup      */
specifier|public
name|void
name|setCreateTablesOnStartup
parameter_list|(
name|boolean
name|createTablesOnStartup
parameter_list|)
block|{
name|this
operator|.
name|createTablesOnStartup
operator|=
name|createTablesOnStartup
expr_stmt|;
block|}
specifier|public
name|boolean
name|isUseDatabaseLock
parameter_list|()
block|{
return|return
name|useDatabaseLock
return|;
block|}
comment|/**      * Sets whether or not an exclusive database lock should be used to enable      * JDBC Master/Slave. Enabled by default.      */
specifier|public
name|void
name|setUseDatabaseLock
parameter_list|(
name|boolean
name|useDatabaseLock
parameter_list|)
block|{
name|this
operator|.
name|useDatabaseLock
operator|=
name|useDatabaseLock
expr_stmt|;
block|}
specifier|public
specifier|static
name|void
name|log
parameter_list|(
name|String
name|msg
parameter_list|,
name|SQLException
name|e
parameter_list|)
block|{
name|String
name|s
init|=
name|msg
operator|+
name|e
operator|.
name|getMessage
argument_list|()
decl_stmt|;
while|while
condition|(
name|e
operator|.
name|getNextException
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|e
operator|=
name|e
operator|.
name|getNextException
argument_list|()
expr_stmt|;
name|s
operator|+=
literal|", due to: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
expr_stmt|;
block|}
name|LOG
operator|.
name|debug
argument_list|(
name|s
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
specifier|public
name|Statements
name|getStatements
parameter_list|()
block|{
if|if
condition|(
name|statements
operator|==
literal|null
condition|)
block|{
name|statements
operator|=
operator|new
name|Statements
argument_list|()
expr_stmt|;
block|}
return|return
name|statements
return|;
block|}
specifier|public
name|void
name|setStatements
parameter_list|(
name|Statements
name|statements
parameter_list|)
block|{
name|this
operator|.
name|statements
operator|=
name|statements
expr_stmt|;
block|}
comment|/**      * @param usageManager The UsageManager that is controlling the      *                destination's memory usage.      */
specifier|public
name|void
name|setUsageManager
parameter_list|(
name|SystemUsage
name|usageManager
parameter_list|)
block|{     }
specifier|protected
name|void
name|databaseLockKeepAlive
parameter_list|()
block|{
name|boolean
name|stop
init|=
literal|false
decl_stmt|;
try|try
block|{
name|DatabaseLocker
name|locker
init|=
name|getDatabaseLocker
argument_list|()
decl_stmt|;
if|if
condition|(
name|locker
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
operator|!
name|locker
operator|.
name|keepAlive
argument_list|()
condition|)
block|{
name|stop
operator|=
literal|true
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Failed to get database when trying keepalive: "
operator|+
name|e
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|stop
condition|)
block|{
name|stopBroker
argument_list|()
expr_stmt|;
block|}
block|}
specifier|protected
name|void
name|stopBroker
parameter_list|()
block|{
comment|// we can no longer keep the lock so lets fail
name|LOG
operator|.
name|info
argument_list|(
literal|"No longer able to keep the exclusive lock so giving up being a master"
argument_list|)
expr_stmt|;
try|try
block|{
name|brokerService
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
name|LOG
operator|.
name|warn
argument_list|(
literal|"Failed to stop broker"
argument_list|)
expr_stmt|;
block|}
block|}
specifier|protected
name|DatabaseLocker
name|createDatabaseLocker
parameter_list|()
throws|throws
name|IOException
block|{
return|return
operator|new
name|DefaultDatabaseLocker
argument_list|(
name|getDataSource
argument_list|()
argument_list|,
name|getStatements
argument_list|()
argument_list|)
return|;
block|}
specifier|public
name|void
name|setBrokerName
parameter_list|(
name|String
name|brokerName
parameter_list|)
block|{     }
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"JDBCPersistenceAdaptor("
operator|+
name|super
operator|.
name|toString
argument_list|()
operator|+
literal|")"
return|;
block|}
specifier|public
name|void
name|setDirectory
parameter_list|(
name|File
name|dir
parameter_list|)
block|{     }
specifier|public
name|void
name|checkpoint
parameter_list|(
name|boolean
name|sync
parameter_list|)
throws|throws
name|IOException
block|{     }
specifier|public
name|long
name|size
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
specifier|public
name|long
name|getLockKeepAlivePeriod
parameter_list|()
block|{
return|return
name|lockKeepAlivePeriod
return|;
block|}
specifier|public
name|void
name|setLockKeepAlivePeriod
parameter_list|(
name|long
name|lockKeepAlivePeriod
parameter_list|)
block|{
name|this
operator|.
name|lockKeepAlivePeriod
operator|=
name|lockKeepAlivePeriod
expr_stmt|;
block|}
block|}
end_class

end_unit

