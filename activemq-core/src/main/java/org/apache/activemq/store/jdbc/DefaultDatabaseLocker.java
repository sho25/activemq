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
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|Connection
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
name|SQLException
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
name|util
operator|.
name|Handler
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
comment|/**  * Represents an exclusive lock on a database to avoid multiple brokers running  * against the same logical database.  *   * @version $Revision: $  */
end_comment

begin_class
specifier|public
class|class
name|DefaultDatabaseLocker
implements|implements
name|DatabaseLocker
block|{
specifier|public
specifier|static
specifier|final
name|long
name|DEFAULT_LOCK_ACQUIRE_SLEEP_INTERVAL
init|=
literal|1000
decl_stmt|;
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
name|DefaultDatabaseLocker
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|DataSource
name|dataSource
decl_stmt|;
specifier|private
specifier|final
name|Statements
name|statements
decl_stmt|;
specifier|private
name|long
name|lockAcquireSleepInterval
init|=
name|DEFAULT_LOCK_ACQUIRE_SLEEP_INTERVAL
decl_stmt|;
specifier|private
name|Connection
name|connection
decl_stmt|;
specifier|private
name|boolean
name|stopping
decl_stmt|;
specifier|private
name|Handler
argument_list|<
name|Exception
argument_list|>
name|exceptionHandler
decl_stmt|;
specifier|public
name|DefaultDatabaseLocker
parameter_list|(
name|JDBCPersistenceAdapter
name|persistenceAdapter
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|(
name|persistenceAdapter
operator|.
name|getLockDataSource
argument_list|()
argument_list|,
name|persistenceAdapter
operator|.
name|getStatements
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|DefaultDatabaseLocker
parameter_list|(
name|DataSource
name|dataSource
parameter_list|,
name|Statements
name|statements
parameter_list|)
block|{
name|this
operator|.
name|dataSource
operator|=
name|dataSource
expr_stmt|;
name|this
operator|.
name|statements
operator|=
name|statements
expr_stmt|;
block|}
specifier|public
name|void
name|start
parameter_list|()
throws|throws
name|Exception
block|{
name|stopping
operator|=
literal|false
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Attempting to acquire the exclusive lock to become the Master broker"
argument_list|)
expr_stmt|;
name|PreparedStatement
name|statement
init|=
literal|null
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
try|try
block|{
name|connection
operator|=
name|dataSource
operator|.
name|getConnection
argument_list|()
expr_stmt|;
name|connection
operator|.
name|setAutoCommit
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|String
name|sql
init|=
name|statements
operator|.
name|getLockCreateStatement
argument_list|()
decl_stmt|;
name|statement
operator|=
name|connection
operator|.
name|prepareStatement
argument_list|(
name|sql
argument_list|)
expr_stmt|;
name|statement
operator|.
name|execute
argument_list|()
expr_stmt|;
break|break;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
if|if
condition|(
name|stopping
condition|)
block|{
throw|throw
operator|new
name|Exception
argument_list|(
literal|"Cannot start broker as being asked to shut down. Interrupted attempt to acquire lock: "
operator|+
name|e
argument_list|,
name|e
argument_list|)
throw|;
block|}
if|if
condition|(
name|exceptionHandler
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|exceptionHandler
operator|.
name|handle
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|handlerException
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"The exception handler "
operator|+
name|exceptionHandler
operator|.
name|getClass
argument_list|()
operator|.
name|getCanonicalName
argument_list|()
operator|+
literal|" threw this exception: "
operator|+
name|handlerException
operator|+
literal|" while trying to handle this excpetion: "
operator|+
name|e
argument_list|,
name|handlerException
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Failed to acquire lock: "
operator|+
name|e
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
if|if
condition|(
literal|null
operator|!=
name|statement
condition|)
block|{
try|try
block|{
name|statement
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SQLException
name|e1
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Caught while closing statement: "
operator|+
name|e1
argument_list|,
name|e1
argument_list|)
expr_stmt|;
block|}
name|statement
operator|=
literal|null
expr_stmt|;
block|}
block|}
name|LOG
operator|.
name|debug
argument_list|(
literal|"Sleeping for "
operator|+
name|lockAcquireSleepInterval
operator|+
literal|" milli(s) before trying again to get the lock..."
argument_list|)
expr_stmt|;
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
name|lockAcquireSleepInterval
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ie
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Master lock retry sleep interrupted"
argument_list|,
name|ie
argument_list|)
expr_stmt|;
block|}
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Becoming the master on dataSource: "
operator|+
name|dataSource
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|stop
parameter_list|()
throws|throws
name|Exception
block|{
name|stopping
operator|=
literal|true
expr_stmt|;
if|if
condition|(
name|connection
operator|!=
literal|null
operator|&&
operator|!
name|connection
operator|.
name|isClosed
argument_list|()
condition|)
block|{
try|try
block|{
name|connection
operator|.
name|rollback
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SQLException
name|sqle
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Exception while rollbacking the connection on shutdown"
argument_list|,
name|sqle
argument_list|)
expr_stmt|;
block|}
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|boolean
name|keepAlive
parameter_list|()
block|{
name|PreparedStatement
name|statement
init|=
literal|null
decl_stmt|;
name|boolean
name|result
init|=
literal|false
decl_stmt|;
try|try
block|{
name|statement
operator|=
name|connection
operator|.
name|prepareStatement
argument_list|(
name|statements
operator|.
name|getLockUpdateStatement
argument_list|()
argument_list|)
expr_stmt|;
name|statement
operator|.
name|setLong
argument_list|(
literal|1
argument_list|,
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|)
expr_stmt|;
name|int
name|rows
init|=
name|statement
operator|.
name|executeUpdate
argument_list|()
decl_stmt|;
if|if
condition|(
name|rows
operator|==
literal|1
condition|)
block|{
name|result
operator|=
literal|true
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Failed to update database lock: "
operator|+
name|e
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|statement
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|statement
operator|.
name|close
argument_list|()
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
name|error
argument_list|(
literal|"Failed to close statement"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|result
return|;
block|}
specifier|public
name|long
name|getLockAcquireSleepInterval
parameter_list|()
block|{
return|return
name|lockAcquireSleepInterval
return|;
block|}
specifier|public
name|void
name|setLockAcquireSleepInterval
parameter_list|(
name|long
name|lockAcquireSleepInterval
parameter_list|)
block|{
name|this
operator|.
name|lockAcquireSleepInterval
operator|=
name|lockAcquireSleepInterval
expr_stmt|;
block|}
specifier|public
name|Handler
name|getExceptionHandler
parameter_list|()
block|{
return|return
name|exceptionHandler
return|;
block|}
specifier|public
name|void
name|setExceptionHandler
parameter_list|(
name|Handler
name|exceptionHandler
parameter_list|)
block|{
name|this
operator|.
name|exceptionHandler
operator|=
name|exceptionHandler
expr_stmt|;
block|}
block|}
end_class

end_unit

