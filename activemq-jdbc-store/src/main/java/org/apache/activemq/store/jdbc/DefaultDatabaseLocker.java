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
name|java
operator|.
name|sql
operator|.
name|SQLFeatureNotSupportedException
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
name|activemq
operator|.
name|util
operator|.
name|ServiceStopper
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
comment|/**  * Represents an exclusive lock on a database to avoid multiple brokers running  * against the same logical database.  *   * @org.apache.xbean.XBean element="database-locker"  *   */
end_comment

begin_class
specifier|public
class|class
name|DefaultDatabaseLocker
extends|extends
name|AbstractJDBCLocker
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
name|DefaultDatabaseLocker
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|protected
specifier|volatile
name|PreparedStatement
name|lockCreateStatement
decl_stmt|;
specifier|protected
specifier|volatile
name|PreparedStatement
name|lockUpdateStatement
decl_stmt|;
specifier|protected
specifier|volatile
name|Connection
name|connection
decl_stmt|;
specifier|protected
name|Handler
argument_list|<
name|Exception
argument_list|>
name|exceptionHandler
decl_stmt|;
specifier|public
name|void
name|doStart
parameter_list|()
throws|throws
name|Exception
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Attempting to acquire the exclusive lock to become the Master broker"
argument_list|)
expr_stmt|;
name|String
name|sql
init|=
name|getStatements
argument_list|()
operator|.
name|getLockCreateStatement
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Locking Query is "
operator|+
name|sql
argument_list|)
expr_stmt|;
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
name|lockCreateStatement
operator|=
name|connection
operator|.
name|prepareStatement
argument_list|(
name|sql
argument_list|)
expr_stmt|;
name|lockCreateStatement
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
try|try
block|{
if|if
condition|(
name|isStopping
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|Exception
argument_list|(
literal|"Cannot start broker as being asked to shut down. "
operator|+
literal|"Interrupted attempt to acquire lock: "
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
literal|" while trying to handle this exception: "
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
name|debug
argument_list|(
literal|"Lock failure: "
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
comment|// Let's make sure the database connection is properly
comment|// closed when an error occurs so that we're not leaking
comment|// connections
if|if
condition|(
literal|null
operator|!=
name|connection
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
name|e1
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Caught exception during rollback on connection: "
operator|+
name|e1
argument_list|,
name|e1
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|connection
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
name|debug
argument_list|(
literal|"Caught exception while closing connection: "
operator|+
name|e1
argument_list|,
name|e1
argument_list|)
expr_stmt|;
block|}
name|connection
operator|=
literal|null
expr_stmt|;
block|}
block|}
block|}
finally|finally
block|{
if|if
condition|(
literal|null
operator|!=
name|lockCreateStatement
condition|)
block|{
try|try
block|{
name|lockCreateStatement
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
name|debug
argument_list|(
literal|"Caught while closing statement: "
operator|+
name|e1
argument_list|,
name|e1
argument_list|)
expr_stmt|;
block|}
name|lockCreateStatement
operator|=
literal|null
expr_stmt|;
block|}
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Failed to acquire lock.  Sleeping for "
operator|+
name|lockAcquireSleepInterval
operator|+
literal|" milli(s) before trying again..."
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
name|doStop
parameter_list|(
name|ServiceStopper
name|stopper
parameter_list|)
throws|throws
name|Exception
block|{
try|try
block|{
if|if
condition|(
name|lockCreateStatement
operator|!=
literal|null
condition|)
block|{
name|lockCreateStatement
operator|.
name|cancel
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|SQLFeatureNotSupportedException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Failed to cancel locking query on dataSource"
operator|+
name|dataSource
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
try|try
block|{
if|if
condition|(
name|lockUpdateStatement
operator|!=
literal|null
condition|)
block|{
name|lockUpdateStatement
operator|.
name|cancel
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|SQLFeatureNotSupportedException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Failed to cancel locking query on dataSource"
operator|+
name|dataSource
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
comment|// when the connection is closed from an outside source (lost TCP
comment|// connection, db server, etc) and this connection is managed by a pool
comment|// it is important to close the connection so that we don't leak
comment|// connections
if|if
condition|(
name|connection
operator|!=
literal|null
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
name|debug
argument_list|(
literal|"Exception while rollbacking the connection on shutdown. This exception is ignored."
argument_list|,
name|sqle
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
try|try
block|{
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SQLException
name|ignored
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Exception while closing connection on shutdown. This exception is ignored."
argument_list|,
name|ignored
argument_list|)
expr_stmt|;
block|}
name|lockCreateStatement
operator|=
literal|null
expr_stmt|;
block|}
block|}
block|}
specifier|public
name|boolean
name|keepAlive
parameter_list|()
throws|throws
name|IOException
block|{
name|boolean
name|result
init|=
literal|false
decl_stmt|;
try|try
block|{
name|lockUpdateStatement
operator|=
name|connection
operator|.
name|prepareStatement
argument_list|(
name|getStatements
argument_list|()
operator|.
name|getLockUpdateStatement
argument_list|()
argument_list|)
expr_stmt|;
name|lockUpdateStatement
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
name|setQueryTimeout
argument_list|(
name|lockUpdateStatement
argument_list|)
expr_stmt|;
name|int
name|rows
init|=
name|lockUpdateStatement
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
name|lockUpdateStatement
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|lockUpdateStatement
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
name|lockUpdateStatement
operator|=
literal|null
expr_stmt|;
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

