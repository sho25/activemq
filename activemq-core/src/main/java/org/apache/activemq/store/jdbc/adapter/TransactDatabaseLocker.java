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
operator|.
name|adapter
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
name|DefaultDatabaseLocker
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
comment|/**  * Represents an exclusive lock on a database to avoid multiple brokers running  * against the same logical database.  *   * @org.apache.xbean.XBean element="transact-database-locker"  * @version $Revision$  */
end_comment

begin_class
specifier|public
class|class
name|TransactDatabaseLocker
extends|extends
name|DefaultDatabaseLocker
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
name|TransactDatabaseLocker
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
name|TransactDatabaseLocker
parameter_list|()
block|{     }
specifier|public
name|TransactDatabaseLocker
parameter_list|(
name|JDBCPersistenceAdapter
name|persistenceAdapter
parameter_list|)
throws|throws
name|IOException
block|{
name|setPersistenceAdapter
argument_list|(
name|persistenceAdapter
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
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
if|if
condition|(
name|statement
operator|.
name|getMetaData
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|ResultSet
name|rs
init|=
name|statement
operator|.
name|executeQuery
argument_list|()
decl_stmt|;
comment|// if not already locked the statement below blocks until lock acquired
name|rs
operator|.
name|next
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|statement
operator|.
name|execute
argument_list|()
expr_stmt|;
block|}
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
block|}
end_class

end_unit

