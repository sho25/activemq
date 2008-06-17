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
name|pool
package|;
end_package

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
name|Session
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|transaction
operator|.
name|RollbackException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|transaction
operator|.
name|Status
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|transaction
operator|.
name|SystemException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|transaction
operator|.
name|TransactionManager
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|transaction
operator|.
name|xa
operator|.
name|XAResource
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
name|ActiveMQConnection
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
name|pool
operator|.
name|ObjectPoolFactory
import|;
end_import

begin_comment
comment|/**  * An XA-aware connection pool.  When a session is created and an xa transaction is active,  * the session will automatically be enlisted in the current transaction.  *   * @author gnodet  */
end_comment

begin_class
specifier|public
class|class
name|XaConnectionPool
extends|extends
name|ConnectionPool
block|{
specifier|private
name|TransactionManager
name|transactionManager
decl_stmt|;
specifier|public
name|XaConnectionPool
parameter_list|(
name|ActiveMQConnection
name|connection
parameter_list|,
name|ObjectPoolFactory
name|poolFactory
parameter_list|,
name|TransactionManager
name|transactionManager
parameter_list|)
block|{
name|super
argument_list|(
name|connection
argument_list|,
name|poolFactory
argument_list|)
expr_stmt|;
name|this
operator|.
name|transactionManager
operator|=
name|transactionManager
expr_stmt|;
block|}
specifier|public
name|Session
name|createSession
parameter_list|(
name|boolean
name|transacted
parameter_list|,
name|int
name|ackMode
parameter_list|)
throws|throws
name|JMSException
block|{
try|try
block|{
name|boolean
name|isXa
init|=
operator|(
name|transactionManager
operator|!=
literal|null
operator|&&
name|transactionManager
operator|.
name|getStatus
argument_list|()
operator|!=
name|Status
operator|.
name|STATUS_NO_TRANSACTION
operator|)
decl_stmt|;
if|if
condition|(
name|isXa
condition|)
block|{
name|transacted
operator|=
literal|true
expr_stmt|;
name|ackMode
operator|=
name|Session
operator|.
name|SESSION_TRANSACTED
expr_stmt|;
block|}
name|PooledSession
name|session
init|=
operator|(
name|PooledSession
operator|)
name|super
operator|.
name|createSession
argument_list|(
name|transacted
argument_list|,
name|ackMode
argument_list|)
decl_stmt|;
if|if
condition|(
name|isXa
condition|)
block|{
name|session
operator|.
name|setIgnoreClose
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|transactionManager
operator|.
name|getTransaction
argument_list|()
operator|.
name|registerSynchronization
argument_list|(
operator|new
name|Synchronization
argument_list|(
name|session
argument_list|)
argument_list|)
expr_stmt|;
name|incrementReferenceCount
argument_list|()
expr_stmt|;
name|transactionManager
operator|.
name|getTransaction
argument_list|()
operator|.
name|enlistResource
argument_list|(
name|createXaResource
argument_list|(
name|session
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|session
return|;
block|}
catch|catch
parameter_list|(
name|RollbackException
name|e
parameter_list|)
block|{
specifier|final
name|JMSException
name|jmsException
init|=
operator|new
name|JMSException
argument_list|(
literal|"Rollback Exception"
argument_list|)
decl_stmt|;
name|jmsException
operator|.
name|initCause
argument_list|(
name|e
argument_list|)
expr_stmt|;
throw|throw
name|jmsException
throw|;
block|}
catch|catch
parameter_list|(
name|SystemException
name|e
parameter_list|)
block|{
specifier|final
name|JMSException
name|jmsException
init|=
operator|new
name|JMSException
argument_list|(
literal|"System Exception"
argument_list|)
decl_stmt|;
name|jmsException
operator|.
name|initCause
argument_list|(
name|e
argument_list|)
expr_stmt|;
throw|throw
name|jmsException
throw|;
block|}
block|}
specifier|protected
name|XAResource
name|createXaResource
parameter_list|(
name|PooledSession
name|session
parameter_list|)
throws|throws
name|JMSException
block|{
return|return
name|session
operator|.
name|getSession
argument_list|()
operator|.
name|getTransactionContext
argument_list|()
return|;
block|}
specifier|protected
class|class
name|Synchronization
implements|implements
name|javax
operator|.
name|transaction
operator|.
name|Synchronization
block|{
specifier|private
specifier|final
name|PooledSession
name|session
decl_stmt|;
specifier|private
name|Synchronization
parameter_list|(
name|PooledSession
name|session
parameter_list|)
block|{
name|this
operator|.
name|session
operator|=
name|session
expr_stmt|;
block|}
specifier|public
name|void
name|beforeCompletion
parameter_list|()
block|{         }
specifier|public
name|void
name|afterCompletion
parameter_list|(
name|int
name|status
parameter_list|)
block|{
try|try
block|{
comment|// This will return session to the pool.
name|session
operator|.
name|setIgnoreClose
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|session
operator|.
name|close
argument_list|()
expr_stmt|;
name|decrementReferenceCount
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|JMSException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
block|}
end_class

end_unit

