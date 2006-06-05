begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Copyright 2005-2006 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|Statement
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
name|IOExceptionSupport
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
comment|/**  * Helps keep track of the current transaction/JDBC connection.  *   * @version $Revision: 1.2 $  */
end_comment

begin_class
specifier|public
class|class
name|TransactionContext
block|{
specifier|private
specifier|static
specifier|final
name|Log
name|log
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|TransactionContext
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
name|Connection
name|connection
decl_stmt|;
specifier|private
name|boolean
name|inTx
decl_stmt|;
specifier|private
name|PreparedStatement
name|addMessageStatement
decl_stmt|;
specifier|private
name|PreparedStatement
name|removedMessageStatement
decl_stmt|;
specifier|private
name|PreparedStatement
name|updateLastAckStatement
decl_stmt|;
specifier|public
name|TransactionContext
parameter_list|(
name|DataSource
name|dataSource
parameter_list|)
block|{
name|this
operator|.
name|dataSource
operator|=
name|dataSource
expr_stmt|;
block|}
specifier|public
name|Connection
name|getConnection
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|connection
operator|==
literal|null
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
operator|!
name|inTx
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
literal|"Could not get JDBC connection: "
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
try|try
block|{
name|connection
operator|.
name|setTransactionIsolation
argument_list|(
name|Connection
operator|.
name|TRANSACTION_READ_UNCOMMITTED
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{             }
block|}
return|return
name|connection
return|;
block|}
specifier|public
name|void
name|executeBatch
parameter_list|()
throws|throws
name|SQLException
block|{
try|try
block|{
name|executeBatch
argument_list|(
name|addMessageStatement
argument_list|,
literal|"Failed add a message"
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|addMessageStatement
operator|=
literal|null
expr_stmt|;
try|try
block|{
name|executeBatch
argument_list|(
name|removedMessageStatement
argument_list|,
literal|"Failed to remove a message"
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|removedMessageStatement
operator|=
literal|null
expr_stmt|;
try|try
block|{
name|executeBatch
argument_list|(
name|updateLastAckStatement
argument_list|,
literal|"Failed to ack a message"
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|updateLastAckStatement
operator|=
literal|null
expr_stmt|;
block|}
block|}
block|}
block|}
specifier|private
name|void
name|executeBatch
parameter_list|(
name|PreparedStatement
name|p
parameter_list|,
name|String
name|message
parameter_list|)
throws|throws
name|SQLException
block|{
if|if
condition|(
name|p
operator|==
literal|null
condition|)
return|return;
try|try
block|{
name|int
index|[]
name|rc
init|=
name|p
operator|.
name|executeBatch
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|rc
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|int
name|code
init|=
name|rc
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|code
operator|<
literal|0
operator|&&
name|code
operator|!=
name|Statement
operator|.
name|SUCCESS_NO_INFO
condition|)
block|{
throw|throw
operator|new
name|SQLException
argument_list|(
name|message
operator|+
literal|". Response code: "
operator|+
name|code
argument_list|)
throw|;
block|}
block|}
block|}
finally|finally
block|{
try|try
block|{
name|p
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
block|{ }
block|}
block|}
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|inTx
condition|)
block|{
try|try
block|{
comment|/**                  * we are not in a transaction so should not be committing ??                  * This was previously commented out - but had                  * adverse affects on testing - so it's back!                  *                   */
try|try
block|{
name|executeBatch
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|connection
operator|!=
literal|null
operator|&&
operator|!
name|connection
operator|.
name|getAutoCommit
argument_list|()
condition|)
block|{
name|connection
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
block|}
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
literal|"Error while closing connection: "
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
try|try
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
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Close failed: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|connection
operator|=
literal|null
expr_stmt|;
block|}
block|}
block|}
block|}
specifier|public
name|void
name|begin
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|inTx
condition|)
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Already started."
argument_list|)
throw|;
name|inTx
operator|=
literal|true
expr_stmt|;
name|connection
operator|=
name|getConnection
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|commit
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|inTx
condition|)
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Not started."
argument_list|)
throw|;
try|try
block|{
name|executeBatch
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
name|connection
operator|.
name|getAutoCommit
argument_list|()
condition|)
name|connection
operator|.
name|commit
argument_list|()
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
literal|"Commit failed: "
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
name|inTx
operator|=
literal|false
expr_stmt|;
name|close
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|rollback
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|inTx
condition|)
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Not started."
argument_list|)
throw|;
try|try
block|{
if|if
condition|(
name|addMessageStatement
operator|!=
literal|null
condition|)
block|{
name|addMessageStatement
operator|.
name|close
argument_list|()
expr_stmt|;
name|addMessageStatement
operator|=
literal|null
expr_stmt|;
block|}
if|if
condition|(
name|removedMessageStatement
operator|!=
literal|null
condition|)
block|{
name|removedMessageStatement
operator|.
name|close
argument_list|()
expr_stmt|;
name|removedMessageStatement
operator|=
literal|null
expr_stmt|;
block|}
if|if
condition|(
name|updateLastAckStatement
operator|!=
literal|null
condition|)
block|{
name|updateLastAckStatement
operator|.
name|close
argument_list|()
expr_stmt|;
name|updateLastAckStatement
operator|=
literal|null
expr_stmt|;
block|}
name|connection
operator|.
name|rollback
argument_list|()
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
literal|"Rollback failed: "
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
name|inTx
operator|=
literal|false
expr_stmt|;
name|close
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|PreparedStatement
name|getAddMessageStatement
parameter_list|()
block|{
return|return
name|addMessageStatement
return|;
block|}
specifier|public
name|void
name|setAddMessageStatement
parameter_list|(
name|PreparedStatement
name|addMessageStatement
parameter_list|)
block|{
name|this
operator|.
name|addMessageStatement
operator|=
name|addMessageStatement
expr_stmt|;
block|}
specifier|public
name|PreparedStatement
name|getUpdateLastAckStatement
parameter_list|()
block|{
return|return
name|updateLastAckStatement
return|;
block|}
specifier|public
name|void
name|setUpdateLastAckStatement
parameter_list|(
name|PreparedStatement
name|ackMessageStatement
parameter_list|)
block|{
name|this
operator|.
name|updateLastAckStatement
operator|=
name|ackMessageStatement
expr_stmt|;
block|}
specifier|public
name|PreparedStatement
name|getRemovedMessageStatement
parameter_list|()
block|{
return|return
name|removedMessageStatement
return|;
block|}
specifier|public
name|void
name|setRemovedMessageStatement
parameter_list|(
name|PreparedStatement
name|removedMessageStatement
parameter_list|)
block|{
name|this
operator|.
name|removedMessageStatement
operator|=
name|removedMessageStatement
expr_stmt|;
block|}
block|}
end_class

end_unit

