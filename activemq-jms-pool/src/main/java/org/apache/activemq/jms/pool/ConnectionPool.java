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
name|jms
operator|.
name|pool
package|;
end_package

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
name|concurrent
operator|.
name|CopyOnWriteArrayList
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
name|*
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|IllegalStateException
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
name|KeyedPoolableObjectFactory
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
name|impl
operator|.
name|GenericKeyedObjectPool
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
name|impl
operator|.
name|GenericObjectPool
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
comment|/**  * Holds a real JMS connection along with the session pools associated with it.  *<p/>  * Instances of this class are shared amongst one or more PooledConnection object and must  * track the session objects that are loaned out for cleanup on close as well as ensuring  * that the temporary destinations of the managed Connection are purged when all references  * to this ConnectionPool are released.  */
end_comment

begin_class
specifier|public
class|class
name|ConnectionPool
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
name|ConnectionPool
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|protected
name|Connection
name|connection
decl_stmt|;
specifier|private
name|int
name|referenceCount
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
specifier|final
name|long
name|firstUsed
init|=
name|lastUsed
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
specifier|private
name|long
name|expiryTimeout
init|=
literal|0l
decl_stmt|;
specifier|private
name|boolean
name|useAnonymousProducers
init|=
literal|true
decl_stmt|;
specifier|private
specifier|final
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
specifier|final
name|GenericKeyedObjectPool
argument_list|<
name|SessionKey
argument_list|,
name|Session
argument_list|>
name|sessionPool
decl_stmt|;
specifier|private
specifier|final
name|List
argument_list|<
name|PooledSession
argument_list|>
name|loanedSessions
init|=
operator|new
name|CopyOnWriteArrayList
argument_list|<
name|PooledSession
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
name|boolean
name|reconnectOnException
decl_stmt|;
specifier|private
name|ExceptionListener
name|parentExceptionListener
decl_stmt|;
specifier|public
name|ConnectionPool
parameter_list|(
name|Connection
name|connection
parameter_list|)
block|{
name|this
operator|.
name|connection
operator|=
name|wrap
argument_list|(
name|connection
argument_list|)
expr_stmt|;
comment|// Create our internal Pool of session instances.
name|this
operator|.
name|sessionPool
operator|=
operator|new
name|GenericKeyedObjectPool
argument_list|<
name|SessionKey
argument_list|,
name|Session
argument_list|>
argument_list|(
operator|new
name|KeyedPoolableObjectFactory
argument_list|<
name|SessionKey
argument_list|,
name|Session
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|activateObject
parameter_list|(
name|SessionKey
name|key
parameter_list|,
name|Session
name|session
parameter_list|)
throws|throws
name|Exception
block|{                 }
annotation|@
name|Override
specifier|public
name|void
name|destroyObject
parameter_list|(
name|SessionKey
name|key
parameter_list|,
name|Session
name|session
parameter_list|)
throws|throws
name|Exception
block|{
name|session
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Session
name|makeObject
parameter_list|(
name|SessionKey
name|key
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|makeSession
argument_list|(
name|key
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|passivateObject
parameter_list|(
name|SessionKey
name|key
parameter_list|,
name|Session
name|session
parameter_list|)
throws|throws
name|Exception
block|{                 }
annotation|@
name|Override
specifier|public
name|boolean
name|validateObject
parameter_list|(
name|SessionKey
name|key
parameter_list|,
name|Session
name|session
parameter_list|)
block|{
return|return
literal|true
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
comment|// useful when external failure needs to force expiry
specifier|public
name|void
name|setHasExpired
parameter_list|(
name|boolean
name|val
parameter_list|)
block|{
name|hasExpired
operator|=
name|val
expr_stmt|;
block|}
specifier|protected
name|Session
name|makeSession
parameter_list|(
name|SessionKey
name|key
parameter_list|)
throws|throws
name|JMSException
block|{
return|return
name|connection
operator|.
name|createSession
argument_list|(
name|key
operator|.
name|isTransacted
argument_list|()
argument_list|,
name|key
operator|.
name|getAckMode
argument_list|()
argument_list|)
return|;
block|}
specifier|protected
name|Connection
name|wrap
parameter_list|(
name|Connection
name|connection
parameter_list|)
block|{
return|return
name|connection
return|;
block|}
specifier|protected
name|void
name|unWrap
parameter_list|(
name|Connection
name|connection
parameter_list|)
block|{     }
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
try|try
block|{
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|JMSException
name|e
parameter_list|)
block|{
name|started
operator|.
name|set
argument_list|(
literal|false
argument_list|)
expr_stmt|;
throw|throw
operator|(
name|e
operator|)
throw|;
block|}
block|}
block|}
specifier|public
specifier|synchronized
name|Connection
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
name|PooledSession
name|session
decl_stmt|;
try|try
block|{
name|session
operator|=
operator|new
name|PooledSession
argument_list|(
name|key
argument_list|,
name|sessionPool
operator|.
name|borrowObject
argument_list|(
name|key
argument_list|)
argument_list|,
name|sessionPool
argument_list|,
name|key
operator|.
name|isTransacted
argument_list|()
argument_list|,
name|useAnonymousProducers
argument_list|)
expr_stmt|;
name|session
operator|.
name|addSessionEventListener
argument_list|(
operator|new
name|PooledSessionEventListener
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onTemporaryTopicCreate
parameter_list|(
name|TemporaryTopic
name|tempTopic
parameter_list|)
block|{                 }
annotation|@
name|Override
specifier|public
name|void
name|onTemporaryQueueCreate
parameter_list|(
name|TemporaryQueue
name|tempQueue
parameter_list|)
block|{                 }
annotation|@
name|Override
specifier|public
name|void
name|onSessionClosed
parameter_list|(
name|PooledSession
name|session
parameter_list|)
block|{
name|ConnectionPool
operator|.
name|this
operator|.
name|loanedSessions
operator|.
name|remove
argument_list|(
name|session
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|this
operator|.
name|loanedSessions
operator|.
name|add
argument_list|(
name|session
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|IllegalStateException
name|illegalStateException
init|=
operator|new
name|IllegalStateException
argument_list|(
name|e
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|illegalStateException
operator|.
name|initCause
argument_list|(
name|e
argument_list|)
expr_stmt|;
throw|throw
name|illegalStateException
throw|;
block|}
return|return
name|session
return|;
block|}
specifier|public
specifier|synchronized
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
name|sessionPool
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
block|{             }
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
specifier|public
specifier|synchronized
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
specifier|public
specifier|synchronized
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
comment|// Loaned sessions are those that are active in the sessionPool and
comment|// have not been closed by the client before closing the connection.
comment|// These need to be closed so that all session's reflect the fact
comment|// that the parent Connection is closed.
for|for
control|(
name|PooledSession
name|session
range|:
name|this
operator|.
name|loanedSessions
control|)
block|{
try|try
block|{
name|session
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
block|}
name|this
operator|.
name|loanedSessions
operator|.
name|clear
argument_list|()
expr_stmt|;
name|unWrap
argument_list|(
name|getConnection
argument_list|()
argument_list|)
expr_stmt|;
name|expiredCheck
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**      * Determines if this Connection has expired.      *<p/>      * A ConnectionPool is considered expired when all references to it are released AND either      * the configured idleTimeout has elapsed OR the configured expiryTimeout has elapsed.      * Once a ConnectionPool is determined to have expired its underlying Connection is closed.      *      * @return true if this connection has expired.      */
specifier|public
specifier|synchronized
name|boolean
name|expiredCheck
parameter_list|()
block|{
name|boolean
name|expired
init|=
literal|false
decl_stmt|;
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
name|expired
operator|=
literal|true
expr_stmt|;
block|}
block|}
if|if
condition|(
name|expiryTimeout
operator|>
literal|0
operator|&&
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|>
name|firstUsed
operator|+
name|expiryTimeout
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
name|expired
operator|=
literal|true
expr_stmt|;
block|}
block|}
comment|// Only set hasExpired here is no references, as a Connection with references is by
comment|// definition not idle at this time.
if|if
condition|(
name|referenceCount
operator|==
literal|0
operator|&&
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
condition|)
block|{
name|hasExpired
operator|=
literal|true
expr_stmt|;
name|close
argument_list|()
expr_stmt|;
name|expired
operator|=
literal|true
expr_stmt|;
block|}
return|return
name|expired
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
specifier|public
name|void
name|setExpiryTimeout
parameter_list|(
name|long
name|expiryTimeout
parameter_list|)
block|{
name|this
operator|.
name|expiryTimeout
operator|=
name|expiryTimeout
expr_stmt|;
block|}
specifier|public
name|long
name|getExpiryTimeout
parameter_list|()
block|{
return|return
name|expiryTimeout
return|;
block|}
specifier|public
name|int
name|getMaximumActiveSessionPerConnection
parameter_list|()
block|{
return|return
name|this
operator|.
name|sessionPool
operator|.
name|getMaxActive
argument_list|()
return|;
block|}
specifier|public
name|void
name|setMaximumActiveSessionPerConnection
parameter_list|(
name|int
name|maximumActiveSessionPerConnection
parameter_list|)
block|{
name|this
operator|.
name|sessionPool
operator|.
name|setMaxActive
argument_list|(
name|maximumActiveSessionPerConnection
argument_list|)
expr_stmt|;
block|}
specifier|public
name|boolean
name|isUseAnonymousProducers
parameter_list|()
block|{
return|return
name|this
operator|.
name|useAnonymousProducers
return|;
block|}
specifier|public
name|void
name|setUseAnonymousProducers
parameter_list|(
name|boolean
name|value
parameter_list|)
block|{
name|this
operator|.
name|useAnonymousProducers
operator|=
name|value
expr_stmt|;
block|}
comment|/**      * @return the total number of Pooled session including idle sessions that are not      *          currently loaned out to any client.      */
specifier|public
name|int
name|getNumSessions
parameter_list|()
block|{
return|return
name|this
operator|.
name|sessionPool
operator|.
name|getNumIdle
argument_list|()
operator|+
name|this
operator|.
name|sessionPool
operator|.
name|getNumActive
argument_list|()
return|;
block|}
comment|/**      * @return the total number of Sessions that are in the Session pool but not loaned out.      */
specifier|public
name|int
name|getNumIdleSessions
parameter_list|()
block|{
return|return
name|this
operator|.
name|sessionPool
operator|.
name|getNumIdle
argument_list|()
return|;
block|}
comment|/**      * @return the total number of Session's that have been loaned to PooledConnection instances.      */
specifier|public
name|int
name|getNumActiveSessions
parameter_list|()
block|{
return|return
name|this
operator|.
name|sessionPool
operator|.
name|getNumActive
argument_list|()
return|;
block|}
comment|/**      * Configure whether the createSession method should block when there are no more idle sessions and the      * pool already contains the maximum number of active sessions.  If false the create method will fail      * and throw an exception.      *      * @param block      * 		Indicates whether blocking should be used to wait for more space to create a session.      */
specifier|public
name|void
name|setBlockIfSessionPoolIsFull
parameter_list|(
name|boolean
name|block
parameter_list|)
block|{
name|this
operator|.
name|sessionPool
operator|.
name|setWhenExhaustedAction
argument_list|(
operator|(
name|block
condition|?
name|GenericObjectPool
operator|.
name|WHEN_EXHAUSTED_BLOCK
else|:
name|GenericObjectPool
operator|.
name|WHEN_EXHAUSTED_FAIL
operator|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|boolean
name|isBlockIfSessionPoolIsFull
parameter_list|()
block|{
return|return
name|this
operator|.
name|sessionPool
operator|.
name|getWhenExhaustedAction
argument_list|()
operator|==
name|GenericObjectPool
operator|.
name|WHEN_EXHAUSTED_BLOCK
return|;
block|}
comment|/**      * Returns the timeout to use for blocking creating new sessions      *      * @return true if the pooled Connection createSession method will block when the limit is hit.      * @see #setBlockIfSessionPoolIsFull(boolean)      */
specifier|public
name|long
name|getBlockIfSessionPoolIsFullTimeout
parameter_list|()
block|{
return|return
name|this
operator|.
name|sessionPool
operator|.
name|getMaxWait
argument_list|()
return|;
block|}
comment|/**      * Controls the behavior of the internal session pool. By default the call to      * Connection.getSession() will block if the session pool is full.  This setting      * will affect how long it blocks and throws an exception after the timeout.      *      * The size of the session pool is controlled by the @see #maximumActive      * property.      *      * Whether or not the call to create session blocks is controlled by the @see #blockIfSessionPoolIsFull      * property      *      * @param blockIfSessionPoolIsFullTimeout - if blockIfSessionPoolIsFullTimeout is true,      *                                        then use this setting to configure how long to block before retry      */
specifier|public
name|void
name|setBlockIfSessionPoolIsFullTimeout
parameter_list|(
name|long
name|blockIfSessionPoolIsFullTimeout
parameter_list|)
block|{
name|this
operator|.
name|sessionPool
operator|.
name|setMaxWait
argument_list|(
name|blockIfSessionPoolIsFullTimeout
argument_list|)
expr_stmt|;
block|}
comment|/**      * @return true if the underlying connection will be renewed on JMSException, false otherwise      */
specifier|public
name|boolean
name|isReconnectOnException
parameter_list|()
block|{
return|return
name|reconnectOnException
return|;
block|}
comment|/**      * Controls weather the underlying connection should be reset (and renewed) on JMSException      *      * @param reconnectOnException      *          Boolean value that configures whether reconnect on exception should happen      */
specifier|public
name|void
name|setReconnectOnException
parameter_list|(
name|boolean
name|reconnectOnException
parameter_list|)
block|{
name|this
operator|.
name|reconnectOnException
operator|=
name|reconnectOnException
expr_stmt|;
try|try
block|{
if|if
condition|(
name|isReconnectOnException
argument_list|()
condition|)
block|{
if|if
condition|(
name|connection
operator|.
name|getExceptionListener
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|parentExceptionListener
operator|=
name|connection
operator|.
name|getExceptionListener
argument_list|()
expr_stmt|;
block|}
name|connection
operator|.
name|setExceptionListener
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|parentExceptionListener
operator|!=
literal|null
condition|)
block|{
name|connection
operator|.
name|setExceptionListener
argument_list|(
name|parentExceptionListener
argument_list|)
expr_stmt|;
block|}
name|parentExceptionListener
operator|=
literal|null
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|JMSException
name|jmse
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Cannot set reconnect exception listener"
argument_list|,
name|jmse
argument_list|)
expr_stmt|;
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
name|close
argument_list|()
expr_stmt|;
if|if
condition|(
name|parentExceptionListener
operator|!=
literal|null
condition|)
block|{
name|parentExceptionListener
operator|.
name|onException
argument_list|(
name|exception
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"ConnectionPool["
operator|+
name|connection
operator|+
literal|"]"
return|;
block|}
block|}
end_class

end_unit

