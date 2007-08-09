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
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
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
name|atomic
operator|.
name|AtomicBoolean
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
name|activemq
operator|.
name|transport
operator|.
name|TransportListener
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
comment|/**  * Holds a real JMS connection along with the session pools associated with it.  *   * @version $Revision$  */
end_comment

begin_class
specifier|public
class|class
name|ConnectionPool
block|{
specifier|private
name|TransactionManager
name|transactionManager
decl_stmt|;
specifier|private
name|ActiveMQConnection
name|connection
decl_stmt|;
specifier|private
name|Map
name|cache
decl_stmt|;
specifier|private
name|AtomicBoolean
name|started
init|=
operator|new
name|AtomicBoolean
argument_list|(
literal|false
argument_list|)
decl_stmt|;
specifier|private
name|int
name|referenceCount
decl_stmt|;
specifier|private
name|ObjectPoolFactory
name|poolFactory
decl_stmt|;
specifier|private
name|long
name|lastUsed
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
specifier|private
name|boolean
name|hasFailed
decl_stmt|;
specifier|private
name|boolean
name|hasExpired
decl_stmt|;
specifier|private
name|int
name|idleTimeout
init|=
literal|30
operator|*
literal|1000
decl_stmt|;
specifier|public
name|ConnectionPool
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
name|this
argument_list|(
name|connection
argument_list|,
operator|new
name|HashMap
argument_list|()
argument_list|,
name|poolFactory
argument_list|,
name|transactionManager
argument_list|)
expr_stmt|;
comment|// Add a transport Listener so that we can notice if this connection
comment|// should be expired due to
comment|// a connection failure.
name|connection
operator|.
name|addTransportListener
argument_list|(
operator|new
name|TransportListener
argument_list|()
block|{
specifier|public
name|void
name|onCommand
parameter_list|(
name|Object
name|command
parameter_list|)
block|{             }
specifier|public
name|void
name|onException
parameter_list|(
name|IOException
name|error
parameter_list|)
block|{
synchronized|synchronized
init|(
name|ConnectionPool
operator|.
name|this
init|)
block|{
name|hasFailed
operator|=
literal|true
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|transportInterupted
parameter_list|()
block|{             }
specifier|public
name|void
name|transportResumed
parameter_list|()
block|{             }
block|}
argument_list|)
expr_stmt|;
block|}
specifier|public
name|ConnectionPool
parameter_list|(
name|ActiveMQConnection
name|connection
parameter_list|,
name|Map
name|cache
parameter_list|,
name|ObjectPoolFactory
name|poolFactory
parameter_list|,
name|TransactionManager
name|transactionManager
parameter_list|)
block|{
name|this
operator|.
name|connection
operator|=
name|connection
expr_stmt|;
name|this
operator|.
name|cache
operator|=
name|cache
expr_stmt|;
name|this
operator|.
name|poolFactory
operator|=
name|poolFactory
expr_stmt|;
name|this
operator|.
name|transactionManager
operator|=
name|transactionManager
expr_stmt|;
block|}
specifier|public
name|void
name|start
parameter_list|()
throws|throws
name|JMSException
block|{
if|if
condition|(
name|started
operator|.
name|compareAndSet
argument_list|(
literal|false
argument_list|,
literal|true
argument_list|)
condition|)
block|{
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
block|}
specifier|synchronized
specifier|public
name|ActiveMQConnection
name|getConnection
parameter_list|()
block|{
return|return
name|connection
return|;
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
name|SessionKey
name|key
init|=
operator|new
name|SessionKey
argument_list|(
name|transacted
argument_list|,
name|ackMode
argument_list|)
decl_stmt|;
name|SessionPool
name|pool
init|=
operator|(
name|SessionPool
operator|)
name|cache
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|pool
operator|==
literal|null
condition|)
block|{
name|pool
operator|=
operator|new
name|SessionPool
argument_list|(
name|this
argument_list|,
name|key
argument_list|,
name|poolFactory
operator|.
name|createPool
argument_list|()
argument_list|)
expr_stmt|;
name|cache
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|pool
argument_list|)
expr_stmt|;
block|}
name|PooledSession
name|session
init|=
name|pool
operator|.
name|borrowSession
argument_list|()
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
specifier|synchronized
specifier|public
name|void
name|close
parameter_list|()
block|{
if|if
condition|(
name|connection
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|Iterator
name|i
init|=
name|cache
operator|.
name|values
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|i
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|SessionPool
name|pool
init|=
operator|(
name|SessionPool
operator|)
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
name|i
operator|.
name|remove
argument_list|()
expr_stmt|;
try|try
block|{
name|pool
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{                     }
block|}
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
name|Exception
name|e
parameter_list|)
block|{                 }
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
specifier|synchronized
specifier|public
name|void
name|incrementReferenceCount
parameter_list|()
block|{
name|referenceCount
operator|++
expr_stmt|;
name|lastUsed
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
expr_stmt|;
block|}
specifier|synchronized
specifier|public
name|void
name|decrementReferenceCount
parameter_list|()
block|{
name|referenceCount
operator|--
expr_stmt|;
name|lastUsed
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
expr_stmt|;
if|if
condition|(
name|referenceCount
operator|==
literal|0
condition|)
block|{
name|expiredCheck
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**      * @return true if this connection has expired.      */
specifier|synchronized
specifier|public
name|boolean
name|expiredCheck
parameter_list|()
block|{
if|if
condition|(
name|connection
operator|==
literal|null
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
name|hasExpired
condition|)
block|{
if|if
condition|(
name|referenceCount
operator|==
literal|0
condition|)
block|{
name|close
argument_list|()
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
if|if
condition|(
name|hasFailed
operator|||
operator|(
name|idleTimeout
operator|>
literal|0
operator|&&
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|>
name|lastUsed
operator|+
name|idleTimeout
operator|)
condition|)
block|{
name|hasExpired
operator|=
literal|true
expr_stmt|;
if|if
condition|(
name|referenceCount
operator|==
literal|0
condition|)
block|{
name|close
argument_list|()
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
specifier|public
name|int
name|getIdleTimeout
parameter_list|()
block|{
return|return
name|idleTimeout
return|;
block|}
specifier|public
name|void
name|setIdleTimeout
parameter_list|(
name|int
name|idleTimeout
parameter_list|)
block|{
name|this
operator|.
name|idleTimeout
operator|=
name|idleTimeout
expr_stmt|;
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
specifier|protected
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

