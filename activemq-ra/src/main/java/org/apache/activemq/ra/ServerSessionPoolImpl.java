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
name|ra
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
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
name|atomic
operator|.
name|AtomicBoolean
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
name|locks
operator|.
name|Lock
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
name|locks
operator|.
name|ReentrantLock
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
name|ServerSession
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|ServerSessionPool
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
name|resource
operator|.
name|spi
operator|.
name|UnavailableException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|resource
operator|.
name|spi
operator|.
name|endpoint
operator|.
name|MessageEndpoint
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
name|ActiveMQSession
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
name|MessageDispatch
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
comment|/**  *  $Date$  */
end_comment

begin_class
specifier|public
class|class
name|ServerSessionPoolImpl
implements|implements
name|ServerSessionPool
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
name|ServerSessionPoolImpl
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|ActiveMQEndpointWorker
name|activeMQAsfEndpointWorker
decl_stmt|;
specifier|private
specifier|final
name|int
name|maxSessions
decl_stmt|;
specifier|private
specifier|final
name|List
argument_list|<
name|ServerSessionImpl
argument_list|>
name|idleSessions
init|=
operator|new
name|ArrayList
argument_list|<
name|ServerSessionImpl
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|List
argument_list|<
name|ServerSessionImpl
argument_list|>
name|activeSessions
init|=
operator|new
name|ArrayList
argument_list|<
name|ServerSessionImpl
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|Lock
name|sessionLock
init|=
operator|new
name|ReentrantLock
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|AtomicBoolean
name|closing
init|=
operator|new
name|AtomicBoolean
argument_list|(
literal|false
argument_list|)
decl_stmt|;
specifier|public
name|ServerSessionPoolImpl
parameter_list|(
name|ActiveMQEndpointWorker
name|activeMQAsfEndpointWorker
parameter_list|,
name|int
name|maxSessions
parameter_list|)
block|{
name|this
operator|.
name|activeMQAsfEndpointWorker
operator|=
name|activeMQAsfEndpointWorker
expr_stmt|;
name|this
operator|.
name|maxSessions
operator|=
name|maxSessions
expr_stmt|;
block|}
specifier|private
name|ServerSessionImpl
name|createServerSessionImpl
parameter_list|()
throws|throws
name|JMSException
block|{
name|MessageActivationSpec
name|activationSpec
init|=
name|activeMQAsfEndpointWorker
operator|.
name|endpointActivationKey
operator|.
name|getActivationSpec
argument_list|()
decl_stmt|;
name|int
name|acknowledge
init|=
operator|(
name|activeMQAsfEndpointWorker
operator|.
name|transacted
operator|)
condition|?
name|Session
operator|.
name|SESSION_TRANSACTED
else|:
name|activationSpec
operator|.
name|getAcknowledgeModeForSession
argument_list|()
decl_stmt|;
specifier|final
name|ActiveMQConnection
name|connection
init|=
name|activeMQAsfEndpointWorker
operator|.
name|getConnection
argument_list|()
decl_stmt|;
if|if
condition|(
name|connection
operator|==
literal|null
condition|)
block|{
comment|// redispatch of pending prefetched messages after disconnect can have a null connection
return|return
literal|null
return|;
block|}
specifier|final
name|ActiveMQSession
name|session
init|=
operator|(
name|ActiveMQSession
operator|)
name|connection
operator|.
name|createSession
argument_list|(
name|activeMQAsfEndpointWorker
operator|.
name|transacted
argument_list|,
name|acknowledge
argument_list|)
decl_stmt|;
name|MessageEndpoint
name|endpoint
decl_stmt|;
try|try
block|{
name|int
name|batchSize
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|activationSpec
operator|.
name|getEnableBatchBooleanValue
argument_list|()
condition|)
block|{
name|batchSize
operator|=
name|activationSpec
operator|.
name|getMaxMessagesPerBatchIntValue
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|activationSpec
operator|.
name|isUseRAManagedTransactionEnabled
argument_list|()
condition|)
block|{
comment|// The RA will manage the transaction commit.
name|endpoint
operator|=
name|createEndpoint
argument_list|(
literal|null
argument_list|)
expr_stmt|;
return|return
operator|new
name|ServerSessionImpl
argument_list|(
name|this
argument_list|,
name|session
argument_list|,
name|activeMQAsfEndpointWorker
operator|.
name|workManager
argument_list|,
name|endpoint
argument_list|,
literal|true
argument_list|,
name|batchSize
argument_list|)
return|;
block|}
else|else
block|{
comment|// Give the container an object to manage to transaction with.
name|endpoint
operator|=
name|createEndpoint
argument_list|(
operator|new
name|LocalAndXATransaction
argument_list|(
name|session
operator|.
name|getTransactionContext
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
return|return
operator|new
name|ServerSessionImpl
argument_list|(
name|this
argument_list|,
name|session
argument_list|,
name|activeMQAsfEndpointWorker
operator|.
name|workManager
argument_list|,
name|endpoint
argument_list|,
literal|false
argument_list|,
name|batchSize
argument_list|)
return|;
block|}
block|}
catch|catch
parameter_list|(
name|UnavailableException
name|e
parameter_list|)
block|{
comment|// The container could be limiting us on the number of endpoints
comment|// that are being created.
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Could not create an endpoint."
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
name|session
operator|.
name|close
argument_list|()
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
specifier|private
name|MessageEndpoint
name|createEndpoint
parameter_list|(
name|LocalAndXATransaction
name|txResourceProxy
parameter_list|)
throws|throws
name|UnavailableException
block|{
name|MessageEndpoint
name|endpoint
decl_stmt|;
name|endpoint
operator|=
name|activeMQAsfEndpointWorker
operator|.
name|endpointFactory
operator|.
name|createEndpoint
argument_list|(
name|txResourceProxy
argument_list|)
expr_stmt|;
name|MessageEndpointProxy
name|endpointProxy
init|=
operator|new
name|MessageEndpointProxy
argument_list|(
name|endpoint
argument_list|)
decl_stmt|;
return|return
name|endpointProxy
return|;
block|}
comment|/**      */
annotation|@
name|Override
specifier|public
name|ServerSession
name|getServerSession
parameter_list|()
throws|throws
name|JMSException
block|{
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"ServerSession requested."
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|closing
operator|.
name|get
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|JMSException
argument_list|(
literal|"Session Pool Shutting Down."
argument_list|)
throw|;
block|}
name|ServerSessionImpl
name|ss
init|=
literal|null
decl_stmt|;
name|sessionLock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|ss
operator|=
name|getExistingServerSession
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|sessionLock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|ss
operator|!=
literal|null
condition|)
block|{
return|return
name|ss
return|;
block|}
name|ss
operator|=
name|createServerSessionImpl
argument_list|()
expr_stmt|;
name|sessionLock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
comment|// We may not be able to create a session due to the container
comment|// restricting us.
if|if
condition|(
name|ss
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|activeSessions
operator|.
name|isEmpty
argument_list|()
operator|&&
name|idleSessions
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|JMSException
argument_list|(
literal|"Endpoint factory did not allow creation of any endpoints."
argument_list|)
throw|;
block|}
name|ss
operator|=
name|getExistingServerSession
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|activeSessions
operator|.
name|add
argument_list|(
name|ss
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|sessionLock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Created a new session: "
operator|+
name|ss
argument_list|)
expr_stmt|;
block|}
return|return
name|ss
return|;
block|}
comment|/**      * Must be called with sessionLock held.      * Returns an idle session if one exists or an active session if no more      * sessions can be created.  Sessions can not be created if force is true      * or activeSessions>= maxSessions.      * @param force do not check activeSessions>= maxSessions, return an active connection anyway.      * @return an already existing session.      */
specifier|private
name|ServerSessionImpl
name|getExistingServerSession
parameter_list|(
name|boolean
name|force
parameter_list|)
block|{
name|ServerSessionImpl
name|ss
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|idleSessions
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|ss
operator|=
name|idleSessions
operator|.
name|remove
argument_list|(
name|idleSessions
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|ss
operator|!=
literal|null
condition|)
block|{
name|activeSessions
operator|.
name|add
argument_list|(
name|ss
argument_list|)
expr_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Using idle session: "
operator|+
name|ss
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|force
operator|||
name|activeSessions
operator|.
name|size
argument_list|()
operator|>=
name|maxSessions
condition|)
block|{
comment|// If we are at the upper limit
comment|// then reuse the already created sessions..
comment|// This is going to queue up messages into a session for
comment|// processing.
name|ss
operator|=
name|getExistingActiveServerSession
argument_list|()
expr_stmt|;
block|}
return|return
name|ss
return|;
block|}
comment|/**      * Must be called with sessionLock held.      * Returns the first session from activeSessions, shifting it to last.      * @return session      */
specifier|private
name|ServerSessionImpl
name|getExistingActiveServerSession
parameter_list|()
block|{
name|ServerSessionImpl
name|ss
init|=
literal|null
decl_stmt|;
if|if
condition|(
operator|!
name|activeSessions
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
if|if
condition|(
name|activeSessions
operator|.
name|size
argument_list|()
operator|>
literal|1
condition|)
block|{
comment|// round robin
name|ss
operator|=
name|activeSessions
operator|.
name|remove
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|activeSessions
operator|.
name|add
argument_list|(
name|ss
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|ss
operator|=
name|activeSessions
operator|.
name|get
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Reusing an active session: "
operator|+
name|ss
argument_list|)
expr_stmt|;
block|}
return|return
name|ss
return|;
block|}
specifier|public
name|void
name|returnToPool
parameter_list|(
name|ServerSessionImpl
name|ss
parameter_list|)
block|{
name|sessionLock
operator|.
name|lock
argument_list|()
expr_stmt|;
name|activeSessions
operator|.
name|remove
argument_list|(
name|ss
argument_list|)
expr_stmt|;
try|try
block|{
comment|// make sure we only return non-stale sessions to the pool
if|if
condition|(
name|ss
operator|.
name|isStale
argument_list|()
condition|)
block|{
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Discarding stale ServerSession to be returned to pool: "
operator|+
name|ss
argument_list|)
expr_stmt|;
block|}
name|ss
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"ServerSession returned to pool: "
operator|+
name|ss
argument_list|)
expr_stmt|;
block|}
name|idleSessions
operator|.
name|add
argument_list|(
name|ss
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|sessionLock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
synchronized|synchronized
init|(
name|closing
init|)
block|{
name|closing
operator|.
name|notify
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|removeFromPool
parameter_list|(
name|ServerSessionImpl
name|ss
parameter_list|)
block|{
name|sessionLock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|activeSessions
operator|.
name|remove
argument_list|(
name|ss
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|sessionLock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
try|try
block|{
name|ActiveMQSession
name|session
init|=
operator|(
name|ActiveMQSession
operator|)
name|ss
operator|.
name|getSession
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|MessageDispatch
argument_list|>
name|l
init|=
name|session
operator|.
name|getUnconsumedMessages
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|isClosing
argument_list|()
operator|&&
operator|!
name|l
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|ActiveMQConnection
name|connection
init|=
name|activeMQAsfEndpointWorker
operator|.
name|getConnection
argument_list|()
decl_stmt|;
if|if
condition|(
name|connection
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|Iterator
argument_list|<
name|MessageDispatch
argument_list|>
name|i
init|=
name|l
operator|.
name|iterator
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|MessageDispatch
name|md
init|=
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|connection
operator|.
name|hasDispatcher
argument_list|(
name|md
operator|.
name|getConsumerId
argument_list|()
argument_list|)
condition|)
block|{
name|dispatchToSession
argument_list|(
name|md
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|trace
argument_list|(
literal|"on remove of {} redispatch of {}"
argument_list|,
name|session
argument_list|,
name|md
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|trace
argument_list|(
literal|"on remove not redispatching {}, dispatcher no longer present on {}"
argument_list|,
name|md
argument_list|,
name|session
operator|.
name|getConnection
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
else|else
block|{
name|LOG
operator|.
name|trace
argument_list|(
literal|"on remove of {} not redispatching while disconnected"
argument_list|,
name|session
argument_list|)
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Error redispatching unconsumed messages from stale server session {}"
argument_list|,
name|ss
argument_list|,
name|t
argument_list|)
expr_stmt|;
block|}
name|ss
operator|.
name|close
argument_list|()
expr_stmt|;
synchronized|synchronized
init|(
name|closing
init|)
block|{
name|closing
operator|.
name|notify
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**      * @param messageDispatch      *            the message to dispatch      * @throws JMSException      */
specifier|private
name|void
name|dispatchToSession
parameter_list|(
name|MessageDispatch
name|messageDispatch
parameter_list|)
throws|throws
name|JMSException
block|{
name|ServerSession
name|serverSession
init|=
name|getServerSession
argument_list|()
decl_stmt|;
name|Session
name|s
init|=
name|serverSession
operator|.
name|getSession
argument_list|()
decl_stmt|;
name|ActiveMQSession
name|session
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|s
operator|instanceof
name|ActiveMQSession
condition|)
block|{
name|session
operator|=
operator|(
name|ActiveMQSession
operator|)
name|s
expr_stmt|;
block|}
else|else
block|{
name|activeMQAsfEndpointWorker
operator|.
name|getConnection
argument_list|()
operator|.
name|onAsyncException
argument_list|(
operator|new
name|JMSException
argument_list|(
literal|"Session pool provided an invalid session type: "
operator|+
name|s
operator|.
name|getClass
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|session
operator|.
name|dispatch
argument_list|(
name|messageDispatch
argument_list|)
expr_stmt|;
name|serverSession
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|close
parameter_list|()
block|{
name|closing
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"{} close"
argument_list|,
name|this
argument_list|)
expr_stmt|;
name|int
name|activeCount
init|=
name|closeSessions
argument_list|()
decl_stmt|;
comment|// we may have to wait erroneously 250ms if an
comment|// active session is removed during our wait and we
comment|// are not notified
while|while
condition|(
name|activeCount
operator|>
literal|0
condition|)
block|{
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Active Sessions = "
operator|+
name|activeCount
argument_list|)
expr_stmt|;
block|}
try|try
block|{
synchronized|synchronized
init|(
name|closing
init|)
block|{
name|closing
operator|.
name|wait
argument_list|(
literal|250
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|interrupt
argument_list|()
expr_stmt|;
return|return;
block|}
name|activeCount
operator|=
name|closeSessions
argument_list|()
expr_stmt|;
block|}
block|}
specifier|protected
name|int
name|closeSessions
parameter_list|()
block|{
name|sessionLock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|List
argument_list|<
name|ServerSessionImpl
argument_list|>
name|alreadyClosedServerSessions
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|activeSessions
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|ServerSessionImpl
name|ss
range|:
name|activeSessions
control|)
block|{
try|try
block|{
name|ActiveMQSession
name|session
init|=
operator|(
name|ActiveMQSession
operator|)
name|ss
operator|.
name|getSession
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|session
operator|.
name|isClosed
argument_list|()
condition|)
block|{
name|session
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Session {} already closed"
argument_list|,
name|session
argument_list|)
expr_stmt|;
name|alreadyClosedServerSessions
operator|.
name|add
argument_list|(
name|ss
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|JMSException
name|ignored
parameter_list|)
block|{
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Failed to close active running server session {}, reason:{}"
argument_list|,
name|ss
argument_list|,
name|ignored
operator|.
name|toString
argument_list|()
argument_list|,
name|ignored
argument_list|)
expr_stmt|;
block|}
block|}
block|}
for|for
control|(
name|ServerSessionImpl
name|ss
range|:
name|alreadyClosedServerSessions
control|)
block|{
name|removeFromPool
argument_list|(
name|ss
argument_list|)
expr_stmt|;
block|}
name|alreadyClosedServerSessions
operator|.
name|clear
argument_list|()
expr_stmt|;
for|for
control|(
name|ServerSessionImpl
name|ss
range|:
name|idleSessions
control|)
block|{
name|ss
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|idleSessions
operator|.
name|clear
argument_list|()
expr_stmt|;
return|return
name|activeSessions
operator|.
name|size
argument_list|()
return|;
block|}
finally|finally
block|{
name|sessionLock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**      * @return Returns the closing.      */
specifier|public
name|boolean
name|isClosing
parameter_list|()
block|{
return|return
name|closing
operator|.
name|get
argument_list|()
return|;
block|}
comment|/**      * @param closing The closing to set.      */
specifier|public
name|void
name|setClosing
parameter_list|(
name|boolean
name|closing
parameter_list|)
block|{
name|this
operator|.
name|closing
operator|.
name|set
argument_list|(
name|closing
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

