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
name|state
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
name|Iterator
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
name|ConcurrentHashMap
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
name|Command
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
name|ConnectionId
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
name|ConnectionInfo
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
name|ConsumerId
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
name|ConsumerInfo
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
name|DestinationInfo
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
name|Message
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
name|MessageAck
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
name|ProducerId
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
name|ProducerInfo
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
name|Response
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
name|SessionId
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
name|SessionInfo
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
name|TransactionInfo
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
name|Transport
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

begin_comment
comment|/**  * Tracks the state of a connection so a newly established transport can   * be re-initialized to the state that was tracked.  *   * @version $Revision$  */
end_comment

begin_class
specifier|public
class|class
name|ConnectionStateTracker
extends|extends
name|CommandVisitorAdapter
block|{
specifier|private
specifier|final
specifier|static
name|Tracked
name|TRACKED_RESPONSE_MARKER
init|=
operator|new
name|Tracked
argument_list|(
literal|null
argument_list|)
decl_stmt|;
specifier|private
name|boolean
name|trackTransactions
init|=
literal|false
decl_stmt|;
specifier|private
name|boolean
name|restoreSessions
init|=
literal|true
decl_stmt|;
specifier|private
name|boolean
name|restoreConsumers
init|=
literal|true
decl_stmt|;
specifier|private
name|boolean
name|restoreProducers
init|=
literal|true
decl_stmt|;
specifier|private
name|boolean
name|restoreTransaction
init|=
literal|true
decl_stmt|;
specifier|protected
specifier|final
name|ConcurrentHashMap
name|connectionStates
init|=
operator|new
name|ConcurrentHashMap
argument_list|()
decl_stmt|;
specifier|private
class|class
name|RemoveTransactionAction
implements|implements
name|Runnable
block|{
specifier|private
specifier|final
name|TransactionInfo
name|info
decl_stmt|;
specifier|public
name|RemoveTransactionAction
parameter_list|(
name|TransactionInfo
name|info
parameter_list|)
block|{
name|this
operator|.
name|info
operator|=
name|info
expr_stmt|;
block|}
specifier|public
name|void
name|run
parameter_list|()
block|{
name|ConnectionId
name|connectionId
init|=
name|info
operator|.
name|getConnectionId
argument_list|()
decl_stmt|;
name|ConnectionState
name|cs
init|=
operator|(
name|ConnectionState
operator|)
name|connectionStates
operator|.
name|get
argument_list|(
name|connectionId
argument_list|)
decl_stmt|;
name|cs
operator|.
name|removeTransactionState
argument_list|(
name|info
operator|.
name|getTransactionId
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      *       *       * @param command      * @return null if the command is not state tracked.      * @throws IOException      */
specifier|public
name|Tracked
name|track
parameter_list|(
name|Command
name|command
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
return|return
operator|(
name|Tracked
operator|)
name|command
operator|.
name|visit
argument_list|(
name|this
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
name|e
throw|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
throw|throw
name|IOExceptionSupport
operator|.
name|create
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
specifier|public
name|void
name|restore
parameter_list|(
name|Transport
name|transport
parameter_list|)
throws|throws
name|IOException
block|{
comment|// Restore the connections.
for|for
control|(
name|Iterator
name|iter
init|=
name|connectionStates
operator|.
name|values
argument_list|()
operator|.
name|iterator
argument_list|()
init|;
name|iter
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|ConnectionState
name|connectionState
init|=
operator|(
name|ConnectionState
operator|)
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
name|transport
operator|.
name|oneway
argument_list|(
name|connectionState
operator|.
name|getInfo
argument_list|()
argument_list|)
expr_stmt|;
name|restoreTempDestinations
argument_list|(
name|transport
argument_list|,
name|connectionState
argument_list|)
expr_stmt|;
if|if
condition|(
name|restoreSessions
condition|)
name|restoreSessions
argument_list|(
name|transport
argument_list|,
name|connectionState
argument_list|)
expr_stmt|;
if|if
condition|(
name|restoreTransaction
condition|)
name|restoreTransactions
argument_list|(
name|transport
argument_list|,
name|connectionState
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|restoreTransactions
parameter_list|(
name|Transport
name|transport
parameter_list|,
name|ConnectionState
name|connectionState
parameter_list|)
throws|throws
name|IOException
block|{
for|for
control|(
name|Iterator
name|iter
init|=
name|connectionState
operator|.
name|getTransactionStates
argument_list|()
operator|.
name|iterator
argument_list|()
init|;
name|iter
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|TransactionState
name|transactionState
init|=
operator|(
name|TransactionState
operator|)
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
for|for
control|(
name|Iterator
name|iterator
init|=
name|transactionState
operator|.
name|getCommands
argument_list|()
operator|.
name|iterator
argument_list|()
init|;
name|iterator
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|Command
name|command
init|=
operator|(
name|Command
operator|)
name|iterator
operator|.
name|next
argument_list|()
decl_stmt|;
name|transport
operator|.
name|oneway
argument_list|(
name|command
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**      * @param transport      * @param connectionState      * @throws IOException      */
specifier|protected
name|void
name|restoreSessions
parameter_list|(
name|Transport
name|transport
parameter_list|,
name|ConnectionState
name|connectionState
parameter_list|)
throws|throws
name|IOException
block|{
comment|// Restore the connection's sessions
for|for
control|(
name|Iterator
name|iter2
init|=
name|connectionState
operator|.
name|getSessionStates
argument_list|()
operator|.
name|iterator
argument_list|()
init|;
name|iter2
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|SessionState
name|sessionState
init|=
operator|(
name|SessionState
operator|)
name|iter2
operator|.
name|next
argument_list|()
decl_stmt|;
name|transport
operator|.
name|oneway
argument_list|(
name|sessionState
operator|.
name|getInfo
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|restoreProducers
condition|)
name|restoreProducers
argument_list|(
name|transport
argument_list|,
name|sessionState
argument_list|)
expr_stmt|;
if|if
condition|(
name|restoreConsumers
condition|)
name|restoreConsumers
argument_list|(
name|transport
argument_list|,
name|sessionState
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * @param transport      * @param sessionState      * @throws IOException      */
specifier|protected
name|void
name|restoreConsumers
parameter_list|(
name|Transport
name|transport
parameter_list|,
name|SessionState
name|sessionState
parameter_list|)
throws|throws
name|IOException
block|{
comment|// Restore the session's consumers
for|for
control|(
name|Iterator
name|iter3
init|=
name|sessionState
operator|.
name|getConsumerStates
argument_list|()
operator|.
name|iterator
argument_list|()
init|;
name|iter3
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|ConsumerState
name|consumerState
init|=
operator|(
name|ConsumerState
operator|)
name|iter3
operator|.
name|next
argument_list|()
decl_stmt|;
name|transport
operator|.
name|oneway
argument_list|(
name|consumerState
operator|.
name|getInfo
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * @param transport      * @param sessionState      * @throws IOException      */
specifier|protected
name|void
name|restoreProducers
parameter_list|(
name|Transport
name|transport
parameter_list|,
name|SessionState
name|sessionState
parameter_list|)
throws|throws
name|IOException
block|{
comment|// Restore the session's producers
for|for
control|(
name|Iterator
name|iter3
init|=
name|sessionState
operator|.
name|getProducerStates
argument_list|()
operator|.
name|iterator
argument_list|()
init|;
name|iter3
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|ProducerState
name|producerState
init|=
operator|(
name|ProducerState
operator|)
name|iter3
operator|.
name|next
argument_list|()
decl_stmt|;
name|transport
operator|.
name|oneway
argument_list|(
name|producerState
operator|.
name|getInfo
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * @param transport      * @param connectionState      * @throws IOException      */
specifier|protected
name|void
name|restoreTempDestinations
parameter_list|(
name|Transport
name|transport
parameter_list|,
name|ConnectionState
name|connectionState
parameter_list|)
throws|throws
name|IOException
block|{
comment|// Restore the connection's temp destinations.
for|for
control|(
name|Iterator
name|iter2
init|=
name|connectionState
operator|.
name|getTempDesinations
argument_list|()
operator|.
name|iterator
argument_list|()
init|;
name|iter2
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|transport
operator|.
name|oneway
argument_list|(
operator|(
name|DestinationInfo
operator|)
name|iter2
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|Response
name|processAddDestination
parameter_list|(
name|DestinationInfo
name|info
parameter_list|)
block|{
if|if
condition|(
name|info
operator|!=
literal|null
condition|)
block|{
name|ConnectionState
name|cs
init|=
operator|(
name|ConnectionState
operator|)
name|connectionStates
operator|.
name|get
argument_list|(
name|info
operator|.
name|getConnectionId
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|cs
operator|!=
literal|null
operator|&&
name|info
operator|.
name|getDestination
argument_list|()
operator|.
name|isTemporary
argument_list|()
condition|)
block|{
name|cs
operator|.
name|addTempDestination
argument_list|(
name|info
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|TRACKED_RESPONSE_MARKER
return|;
block|}
specifier|public
name|Response
name|processRemoveDestination
parameter_list|(
name|DestinationInfo
name|info
parameter_list|)
block|{
if|if
condition|(
name|info
operator|!=
literal|null
condition|)
block|{
name|ConnectionState
name|cs
init|=
operator|(
name|ConnectionState
operator|)
name|connectionStates
operator|.
name|get
argument_list|(
name|info
operator|.
name|getConnectionId
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|cs
operator|!=
literal|null
operator|&&
name|info
operator|.
name|getDestination
argument_list|()
operator|.
name|isTemporary
argument_list|()
condition|)
block|{
name|cs
operator|.
name|removeTempDestination
argument_list|(
name|info
operator|.
name|getDestination
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|TRACKED_RESPONSE_MARKER
return|;
block|}
specifier|public
name|Response
name|processAddProducer
parameter_list|(
name|ProducerInfo
name|info
parameter_list|)
block|{
if|if
condition|(
name|info
operator|!=
literal|null
operator|&&
name|info
operator|.
name|getProducerId
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|SessionId
name|sessionId
init|=
name|info
operator|.
name|getProducerId
argument_list|()
operator|.
name|getParentId
argument_list|()
decl_stmt|;
if|if
condition|(
name|sessionId
operator|!=
literal|null
condition|)
block|{
name|ConnectionId
name|connectionId
init|=
name|sessionId
operator|.
name|getParentId
argument_list|()
decl_stmt|;
if|if
condition|(
name|connectionId
operator|!=
literal|null
condition|)
block|{
name|ConnectionState
name|cs
init|=
operator|(
name|ConnectionState
operator|)
name|connectionStates
operator|.
name|get
argument_list|(
name|connectionId
argument_list|)
decl_stmt|;
if|if
condition|(
name|cs
operator|!=
literal|null
condition|)
block|{
name|SessionState
name|ss
init|=
name|cs
operator|.
name|getSessionState
argument_list|(
name|sessionId
argument_list|)
decl_stmt|;
if|if
condition|(
name|ss
operator|!=
literal|null
condition|)
block|{
name|ss
operator|.
name|addProducer
argument_list|(
name|info
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
return|return
name|TRACKED_RESPONSE_MARKER
return|;
block|}
specifier|public
name|Response
name|processRemoveProducer
parameter_list|(
name|ProducerId
name|id
parameter_list|)
block|{
if|if
condition|(
name|id
operator|!=
literal|null
condition|)
block|{
name|SessionId
name|sessionId
init|=
name|id
operator|.
name|getParentId
argument_list|()
decl_stmt|;
if|if
condition|(
name|sessionId
operator|!=
literal|null
condition|)
block|{
name|ConnectionId
name|connectionId
init|=
name|sessionId
operator|.
name|getParentId
argument_list|()
decl_stmt|;
if|if
condition|(
name|connectionId
operator|!=
literal|null
condition|)
block|{
name|ConnectionState
name|cs
init|=
operator|(
name|ConnectionState
operator|)
name|connectionStates
operator|.
name|get
argument_list|(
name|connectionId
argument_list|)
decl_stmt|;
if|if
condition|(
name|cs
operator|!=
literal|null
condition|)
block|{
name|SessionState
name|ss
init|=
name|cs
operator|.
name|getSessionState
argument_list|(
name|sessionId
argument_list|)
decl_stmt|;
if|if
condition|(
name|ss
operator|!=
literal|null
condition|)
block|{
name|ss
operator|.
name|removeProducer
argument_list|(
name|id
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
return|return
name|TRACKED_RESPONSE_MARKER
return|;
block|}
specifier|public
name|Response
name|processAddConsumer
parameter_list|(
name|ConsumerInfo
name|info
parameter_list|)
block|{
if|if
condition|(
name|info
operator|!=
literal|null
condition|)
block|{
name|SessionId
name|sessionId
init|=
name|info
operator|.
name|getConsumerId
argument_list|()
operator|.
name|getParentId
argument_list|()
decl_stmt|;
if|if
condition|(
name|sessionId
operator|!=
literal|null
condition|)
block|{
name|ConnectionId
name|connectionId
init|=
name|sessionId
operator|.
name|getParentId
argument_list|()
decl_stmt|;
if|if
condition|(
name|connectionId
operator|!=
literal|null
condition|)
block|{
name|ConnectionState
name|cs
init|=
operator|(
name|ConnectionState
operator|)
name|connectionStates
operator|.
name|get
argument_list|(
name|connectionId
argument_list|)
decl_stmt|;
if|if
condition|(
name|cs
operator|!=
literal|null
condition|)
block|{
name|SessionState
name|ss
init|=
name|cs
operator|.
name|getSessionState
argument_list|(
name|sessionId
argument_list|)
decl_stmt|;
if|if
condition|(
name|ss
operator|!=
literal|null
condition|)
block|{
name|ss
operator|.
name|addConsumer
argument_list|(
name|info
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
return|return
name|TRACKED_RESPONSE_MARKER
return|;
block|}
specifier|public
name|Response
name|processRemoveConsumer
parameter_list|(
name|ConsumerId
name|id
parameter_list|)
block|{
if|if
condition|(
name|id
operator|!=
literal|null
condition|)
block|{
name|SessionId
name|sessionId
init|=
name|id
operator|.
name|getParentId
argument_list|()
decl_stmt|;
if|if
condition|(
name|sessionId
operator|!=
literal|null
condition|)
block|{
name|ConnectionId
name|connectionId
init|=
name|sessionId
operator|.
name|getParentId
argument_list|()
decl_stmt|;
if|if
condition|(
name|connectionId
operator|!=
literal|null
condition|)
block|{
name|ConnectionState
name|cs
init|=
operator|(
name|ConnectionState
operator|)
name|connectionStates
operator|.
name|get
argument_list|(
name|connectionId
argument_list|)
decl_stmt|;
if|if
condition|(
name|cs
operator|!=
literal|null
condition|)
block|{
name|SessionState
name|ss
init|=
name|cs
operator|.
name|getSessionState
argument_list|(
name|sessionId
argument_list|)
decl_stmt|;
if|if
condition|(
name|ss
operator|!=
literal|null
condition|)
block|{
name|ss
operator|.
name|removeConsumer
argument_list|(
name|id
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
return|return
name|TRACKED_RESPONSE_MARKER
return|;
block|}
specifier|public
name|Response
name|processAddSession
parameter_list|(
name|SessionInfo
name|info
parameter_list|)
block|{
if|if
condition|(
name|info
operator|!=
literal|null
condition|)
block|{
name|ConnectionId
name|connectionId
init|=
name|info
operator|.
name|getSessionId
argument_list|()
operator|.
name|getParentId
argument_list|()
decl_stmt|;
if|if
condition|(
name|connectionId
operator|!=
literal|null
condition|)
block|{
name|ConnectionState
name|cs
init|=
operator|(
name|ConnectionState
operator|)
name|connectionStates
operator|.
name|get
argument_list|(
name|connectionId
argument_list|)
decl_stmt|;
if|if
condition|(
name|cs
operator|!=
literal|null
condition|)
block|{
name|cs
operator|.
name|addSession
argument_list|(
name|info
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|TRACKED_RESPONSE_MARKER
return|;
block|}
specifier|public
name|Response
name|processRemoveSession
parameter_list|(
name|SessionId
name|id
parameter_list|)
block|{
if|if
condition|(
name|id
operator|!=
literal|null
condition|)
block|{
name|ConnectionId
name|connectionId
init|=
name|id
operator|.
name|getParentId
argument_list|()
decl_stmt|;
if|if
condition|(
name|connectionId
operator|!=
literal|null
condition|)
block|{
name|ConnectionState
name|cs
init|=
operator|(
name|ConnectionState
operator|)
name|connectionStates
operator|.
name|get
argument_list|(
name|connectionId
argument_list|)
decl_stmt|;
if|if
condition|(
name|cs
operator|!=
literal|null
condition|)
block|{
name|cs
operator|.
name|removeSession
argument_list|(
name|id
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|TRACKED_RESPONSE_MARKER
return|;
block|}
specifier|public
name|Response
name|processAddConnection
parameter_list|(
name|ConnectionInfo
name|info
parameter_list|)
block|{
if|if
condition|(
name|info
operator|!=
literal|null
condition|)
block|{
name|connectionStates
operator|.
name|put
argument_list|(
name|info
operator|.
name|getConnectionId
argument_list|()
argument_list|,
operator|new
name|ConnectionState
argument_list|(
name|info
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|TRACKED_RESPONSE_MARKER
return|;
block|}
specifier|public
name|Response
name|processRemoveConnection
parameter_list|(
name|ConnectionId
name|id
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|id
operator|!=
literal|null
condition|)
block|{
name|connectionStates
operator|.
name|remove
argument_list|(
name|id
argument_list|)
expr_stmt|;
block|}
return|return
name|TRACKED_RESPONSE_MARKER
return|;
block|}
specifier|public
name|Response
name|processMessage
parameter_list|(
name|Message
name|send
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|trackTransactions
operator|&&
name|send
operator|!=
literal|null
operator|&&
name|send
operator|.
name|getTransactionId
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|ConnectionId
name|connectionId
init|=
name|send
operator|.
name|getProducerId
argument_list|()
operator|.
name|getParentId
argument_list|()
operator|.
name|getParentId
argument_list|()
decl_stmt|;
if|if
condition|(
name|connectionId
operator|!=
literal|null
condition|)
block|{
name|ConnectionState
name|cs
init|=
operator|(
name|ConnectionState
operator|)
name|connectionStates
operator|.
name|get
argument_list|(
name|connectionId
argument_list|)
decl_stmt|;
if|if
condition|(
name|cs
operator|!=
literal|null
condition|)
block|{
name|TransactionState
name|transactionState
init|=
name|cs
operator|.
name|getTransactionState
argument_list|(
name|send
operator|.
name|getTransactionId
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|transactionState
operator|!=
literal|null
condition|)
block|{
name|transactionState
operator|.
name|addCommand
argument_list|(
name|send
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|TRACKED_RESPONSE_MARKER
return|;
block|}
return|return
literal|null
return|;
block|}
specifier|public
name|Response
name|processMessageAck
parameter_list|(
name|MessageAck
name|ack
parameter_list|)
block|{
if|if
condition|(
name|trackTransactions
operator|&&
name|ack
operator|!=
literal|null
operator|&&
name|ack
operator|.
name|getTransactionId
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|ConnectionId
name|connectionId
init|=
name|ack
operator|.
name|getConsumerId
argument_list|()
operator|.
name|getParentId
argument_list|()
operator|.
name|getParentId
argument_list|()
decl_stmt|;
if|if
condition|(
name|connectionId
operator|!=
literal|null
condition|)
block|{
name|ConnectionState
name|cs
init|=
operator|(
name|ConnectionState
operator|)
name|connectionStates
operator|.
name|get
argument_list|(
name|connectionId
argument_list|)
decl_stmt|;
if|if
condition|(
name|cs
operator|!=
literal|null
condition|)
block|{
name|TransactionState
name|transactionState
init|=
name|cs
operator|.
name|getTransactionState
argument_list|(
name|ack
operator|.
name|getTransactionId
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|transactionState
operator|!=
literal|null
condition|)
block|{
name|transactionState
operator|.
name|addCommand
argument_list|(
name|ack
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|TRACKED_RESPONSE_MARKER
return|;
block|}
return|return
literal|null
return|;
block|}
specifier|public
name|Response
name|processBeginTransaction
parameter_list|(
name|TransactionInfo
name|info
parameter_list|)
block|{
if|if
condition|(
name|trackTransactions
operator|&&
name|info
operator|!=
literal|null
operator|&&
name|info
operator|.
name|getTransactionId
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|ConnectionId
name|connectionId
init|=
name|info
operator|.
name|getConnectionId
argument_list|()
decl_stmt|;
if|if
condition|(
name|connectionId
operator|!=
literal|null
condition|)
block|{
name|ConnectionState
name|cs
init|=
operator|(
name|ConnectionState
operator|)
name|connectionStates
operator|.
name|get
argument_list|(
name|connectionId
argument_list|)
decl_stmt|;
if|if
condition|(
name|cs
operator|!=
literal|null
condition|)
block|{
name|cs
operator|.
name|addTransactionState
argument_list|(
name|info
operator|.
name|getTransactionId
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|TRACKED_RESPONSE_MARKER
return|;
block|}
return|return
literal|null
return|;
block|}
specifier|public
name|Response
name|processPrepareTransaction
parameter_list|(
name|TransactionInfo
name|info
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|trackTransactions
operator|&&
name|info
operator|!=
literal|null
condition|)
block|{
name|ConnectionId
name|connectionId
init|=
name|info
operator|.
name|getConnectionId
argument_list|()
decl_stmt|;
if|if
condition|(
name|connectionId
operator|!=
literal|null
condition|)
block|{
name|ConnectionState
name|cs
init|=
operator|(
name|ConnectionState
operator|)
name|connectionStates
operator|.
name|get
argument_list|(
name|connectionId
argument_list|)
decl_stmt|;
if|if
condition|(
name|cs
operator|!=
literal|null
condition|)
block|{
name|TransactionState
name|transactionState
init|=
name|cs
operator|.
name|getTransactionState
argument_list|(
name|info
operator|.
name|getTransactionId
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|transactionState
operator|!=
literal|null
condition|)
block|{
name|transactionState
operator|.
name|addCommand
argument_list|(
name|info
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|TRACKED_RESPONSE_MARKER
return|;
block|}
return|return
literal|null
return|;
block|}
specifier|public
name|Response
name|processCommitTransactionOnePhase
parameter_list|(
name|TransactionInfo
name|info
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|trackTransactions
operator|&&
name|info
operator|!=
literal|null
condition|)
block|{
name|ConnectionId
name|connectionId
init|=
name|info
operator|.
name|getConnectionId
argument_list|()
decl_stmt|;
if|if
condition|(
name|connectionId
operator|!=
literal|null
condition|)
block|{
name|ConnectionState
name|cs
init|=
operator|(
name|ConnectionState
operator|)
name|connectionStates
operator|.
name|get
argument_list|(
name|connectionId
argument_list|)
decl_stmt|;
if|if
condition|(
name|cs
operator|!=
literal|null
condition|)
block|{
name|TransactionState
name|transactionState
init|=
name|cs
operator|.
name|getTransactionState
argument_list|(
name|info
operator|.
name|getTransactionId
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|transactionState
operator|!=
literal|null
condition|)
block|{
name|transactionState
operator|.
name|addCommand
argument_list|(
name|info
argument_list|)
expr_stmt|;
return|return
operator|new
name|Tracked
argument_list|(
operator|new
name|RemoveTransactionAction
argument_list|(
name|info
argument_list|)
argument_list|)
return|;
block|}
block|}
block|}
block|}
return|return
literal|null
return|;
block|}
specifier|public
name|Response
name|processCommitTransactionTwoPhase
parameter_list|(
name|TransactionInfo
name|info
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|trackTransactions
operator|&&
name|info
operator|!=
literal|null
condition|)
block|{
name|ConnectionId
name|connectionId
init|=
name|info
operator|.
name|getConnectionId
argument_list|()
decl_stmt|;
if|if
condition|(
name|connectionId
operator|!=
literal|null
condition|)
block|{
name|ConnectionState
name|cs
init|=
operator|(
name|ConnectionState
operator|)
name|connectionStates
operator|.
name|get
argument_list|(
name|connectionId
argument_list|)
decl_stmt|;
if|if
condition|(
name|cs
operator|!=
literal|null
condition|)
block|{
name|TransactionState
name|transactionState
init|=
name|cs
operator|.
name|getTransactionState
argument_list|(
name|info
operator|.
name|getTransactionId
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|transactionState
operator|!=
literal|null
condition|)
block|{
name|transactionState
operator|.
name|addCommand
argument_list|(
name|info
argument_list|)
expr_stmt|;
return|return
operator|new
name|Tracked
argument_list|(
operator|new
name|RemoveTransactionAction
argument_list|(
name|info
argument_list|)
argument_list|)
return|;
block|}
block|}
block|}
block|}
return|return
literal|null
return|;
block|}
specifier|public
name|Response
name|processRollbackTransaction
parameter_list|(
name|TransactionInfo
name|info
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|trackTransactions
operator|&&
name|info
operator|!=
literal|null
condition|)
block|{
name|ConnectionId
name|connectionId
init|=
name|info
operator|.
name|getConnectionId
argument_list|()
decl_stmt|;
if|if
condition|(
name|connectionId
operator|!=
literal|null
condition|)
block|{
name|ConnectionState
name|cs
init|=
operator|(
name|ConnectionState
operator|)
name|connectionStates
operator|.
name|get
argument_list|(
name|connectionId
argument_list|)
decl_stmt|;
if|if
condition|(
name|cs
operator|!=
literal|null
condition|)
block|{
name|TransactionState
name|transactionState
init|=
name|cs
operator|.
name|getTransactionState
argument_list|(
name|info
operator|.
name|getTransactionId
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|transactionState
operator|!=
literal|null
condition|)
block|{
name|transactionState
operator|.
name|addCommand
argument_list|(
name|info
argument_list|)
expr_stmt|;
return|return
operator|new
name|Tracked
argument_list|(
operator|new
name|RemoveTransactionAction
argument_list|(
name|info
argument_list|)
argument_list|)
return|;
block|}
block|}
block|}
block|}
return|return
literal|null
return|;
block|}
specifier|public
name|Response
name|processEndTransaction
parameter_list|(
name|TransactionInfo
name|info
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|trackTransactions
operator|&&
name|info
operator|!=
literal|null
condition|)
block|{
name|ConnectionId
name|connectionId
init|=
name|info
operator|.
name|getConnectionId
argument_list|()
decl_stmt|;
if|if
condition|(
name|connectionId
operator|!=
literal|null
condition|)
block|{
name|ConnectionState
name|cs
init|=
operator|(
name|ConnectionState
operator|)
name|connectionStates
operator|.
name|get
argument_list|(
name|connectionId
argument_list|)
decl_stmt|;
if|if
condition|(
name|cs
operator|!=
literal|null
condition|)
block|{
name|TransactionState
name|transactionState
init|=
name|cs
operator|.
name|getTransactionState
argument_list|(
name|info
operator|.
name|getTransactionId
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|transactionState
operator|!=
literal|null
condition|)
block|{
name|transactionState
operator|.
name|addCommand
argument_list|(
name|info
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|TRACKED_RESPONSE_MARKER
return|;
block|}
return|return
literal|null
return|;
block|}
specifier|public
name|boolean
name|isRestoreConsumers
parameter_list|()
block|{
return|return
name|restoreConsumers
return|;
block|}
specifier|public
name|void
name|setRestoreConsumers
parameter_list|(
name|boolean
name|restoreConsumers
parameter_list|)
block|{
name|this
operator|.
name|restoreConsumers
operator|=
name|restoreConsumers
expr_stmt|;
block|}
specifier|public
name|boolean
name|isRestoreProducers
parameter_list|()
block|{
return|return
name|restoreProducers
return|;
block|}
specifier|public
name|void
name|setRestoreProducers
parameter_list|(
name|boolean
name|restoreProducers
parameter_list|)
block|{
name|this
operator|.
name|restoreProducers
operator|=
name|restoreProducers
expr_stmt|;
block|}
specifier|public
name|boolean
name|isRestoreSessions
parameter_list|()
block|{
return|return
name|restoreSessions
return|;
block|}
specifier|public
name|void
name|setRestoreSessions
parameter_list|(
name|boolean
name|restoreSessions
parameter_list|)
block|{
name|this
operator|.
name|restoreSessions
operator|=
name|restoreSessions
expr_stmt|;
block|}
specifier|public
name|boolean
name|isTrackTransactions
parameter_list|()
block|{
return|return
name|trackTransactions
return|;
block|}
specifier|public
name|void
name|setTrackTransactions
parameter_list|(
name|boolean
name|trackTransactions
parameter_list|)
block|{
name|this
operator|.
name|trackTransactions
operator|=
name|trackTransactions
expr_stmt|;
block|}
specifier|public
name|boolean
name|isRestoreTransaction
parameter_list|()
block|{
return|return
name|restoreTransaction
return|;
block|}
specifier|public
name|void
name|setRestoreTransaction
parameter_list|(
name|boolean
name|restoreTransaction
parameter_list|)
block|{
name|this
operator|.
name|restoreTransaction
operator|=
name|restoreTransaction
expr_stmt|;
block|}
block|}
end_class

end_unit

