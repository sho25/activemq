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
name|SQLException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedList
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
name|ActiveMQMessageAudit
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
name|MessageId
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
name|XATransactionId
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
name|AbstractMessageStore
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
name|IndexListener
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
name|MessageRecoveryListener
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
name|ByteSequence
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
name|ByteSequenceData
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
comment|/**  *   */
end_comment

begin_class
specifier|public
class|class
name|JDBCMessageStore
extends|extends
name|AbstractMessageStore
block|{
class|class
name|Duration
block|{
specifier|static
specifier|final
name|int
name|LIMIT
init|=
literal|100
decl_stmt|;
specifier|final
name|long
name|start
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
specifier|final
name|String
name|name
decl_stmt|;
name|Duration
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
block|}
name|void
name|end
parameter_list|()
block|{
name|end
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
name|void
name|end
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
name|long
name|duration
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|start
decl_stmt|;
if|if
condition|(
name|duration
operator|>
name|LIMIT
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
name|name
operator|+
literal|" took a long time: "
operator|+
name|duration
operator|+
literal|"ms "
operator|+
name|o
argument_list|)
expr_stmt|;
block|}
block|}
block|}
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
name|JDBCMessageStore
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|protected
specifier|final
name|WireFormat
name|wireFormat
decl_stmt|;
specifier|protected
specifier|final
name|JDBCAdapter
name|adapter
decl_stmt|;
specifier|protected
specifier|final
name|JDBCPersistenceAdapter
name|persistenceAdapter
decl_stmt|;
specifier|protected
name|ActiveMQMessageAudit
name|audit
decl_stmt|;
specifier|protected
specifier|final
name|LinkedList
argument_list|<
name|Long
argument_list|>
name|pendingAdditions
init|=
operator|new
name|LinkedList
argument_list|<
name|Long
argument_list|>
argument_list|()
decl_stmt|;
specifier|final
name|long
index|[]
name|perPriorityLastRecovered
init|=
operator|new
name|long
index|[
literal|10
index|]
decl_stmt|;
specifier|public
name|JDBCMessageStore
parameter_list|(
name|JDBCPersistenceAdapter
name|persistenceAdapter
parameter_list|,
name|JDBCAdapter
name|adapter
parameter_list|,
name|WireFormat
name|wireFormat
parameter_list|,
name|ActiveMQDestination
name|destination
parameter_list|,
name|ActiveMQMessageAudit
name|audit
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|destination
argument_list|)
expr_stmt|;
name|this
operator|.
name|persistenceAdapter
operator|=
name|persistenceAdapter
expr_stmt|;
name|this
operator|.
name|adapter
operator|=
name|adapter
expr_stmt|;
name|this
operator|.
name|wireFormat
operator|=
name|wireFormat
expr_stmt|;
name|this
operator|.
name|audit
operator|=
name|audit
expr_stmt|;
if|if
condition|(
name|destination
operator|.
name|isQueue
argument_list|()
operator|&&
name|persistenceAdapter
operator|.
name|getBrokerService
argument_list|()
operator|.
name|shouldRecordVirtualDestination
argument_list|(
name|destination
argument_list|)
condition|)
block|{
name|recordDestinationCreation
argument_list|(
name|destination
argument_list|)
expr_stmt|;
block|}
name|resetBatching
argument_list|()
expr_stmt|;
block|}
specifier|private
name|void
name|recordDestinationCreation
parameter_list|(
name|ActiveMQDestination
name|destination
parameter_list|)
throws|throws
name|IOException
block|{
name|TransactionContext
name|c
init|=
name|persistenceAdapter
operator|.
name|getTransactionContext
argument_list|()
decl_stmt|;
try|try
block|{
name|c
operator|=
name|persistenceAdapter
operator|.
name|getTransactionContext
argument_list|()
expr_stmt|;
if|if
condition|(
name|adapter
operator|.
name|doGetLastAckedDurableSubscriberMessageId
argument_list|(
name|c
argument_list|,
name|destination
argument_list|,
name|destination
operator|.
name|getQualifiedName
argument_list|()
argument_list|,
name|destination
operator|.
name|getQualifiedName
argument_list|()
argument_list|)
operator|<
literal|0
condition|)
block|{
name|adapter
operator|.
name|doRecordDestination
argument_list|(
name|c
argument_list|,
name|destination
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
literal|"Failed to record destination: "
operator|+
name|destination
operator|+
literal|". Reason: "
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
name|addMessage
parameter_list|(
specifier|final
name|ConnectionContext
name|context
parameter_list|,
specifier|final
name|Message
name|message
parameter_list|)
throws|throws
name|IOException
block|{
name|MessageId
name|messageId
init|=
name|message
operator|.
name|getMessageId
argument_list|()
decl_stmt|;
if|if
condition|(
name|audit
operator|!=
literal|null
operator|&&
name|audit
operator|.
name|isDuplicate
argument_list|(
name|message
argument_list|)
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
name|destination
operator|.
name|getPhysicalName
argument_list|()
operator|+
literal|" ignoring duplicated (add) message, already stored: "
operator|+
name|messageId
argument_list|)
expr_stmt|;
block|}
return|return;
block|}
comment|// if xaXid present - this is a prepare - so we don't yet have an outcome
specifier|final
name|XATransactionId
name|xaXid
init|=
name|context
operator|!=
literal|null
condition|?
name|context
operator|.
name|getXid
argument_list|()
else|:
literal|null
decl_stmt|;
comment|// Serialize the Message..
name|byte
name|data
index|[]
decl_stmt|;
try|try
block|{
name|ByteSequence
name|packet
init|=
name|wireFormat
operator|.
name|marshal
argument_list|(
name|message
argument_list|)
decl_stmt|;
name|data
operator|=
name|ByteSequenceData
operator|.
name|toByteArray
argument_list|(
name|packet
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
name|IOExceptionSupport
operator|.
name|create
argument_list|(
literal|"Failed to broker message: "
operator|+
name|messageId
operator|+
literal|" in container: "
operator|+
name|e
argument_list|,
name|e
argument_list|)
throw|;
block|}
comment|// Get a connection and insert the message into the DB.
name|TransactionContext
name|c
init|=
name|persistenceAdapter
operator|.
name|getTransactionContext
argument_list|(
name|context
argument_list|)
decl_stmt|;
name|long
name|sequenceId
decl_stmt|;
synchronized|synchronized
init|(
name|pendingAdditions
init|)
block|{
name|sequenceId
operator|=
name|persistenceAdapter
operator|.
name|getNextSequenceId
argument_list|()
expr_stmt|;
specifier|final
name|long
name|sequence
init|=
name|sequenceId
decl_stmt|;
name|message
operator|.
name|getMessageId
argument_list|()
operator|.
name|setEntryLocator
argument_list|(
name|sequence
argument_list|)
expr_stmt|;
if|if
condition|(
name|xaXid
operator|==
literal|null
condition|)
block|{
name|pendingAdditions
operator|.
name|add
argument_list|(
name|sequence
argument_list|)
expr_stmt|;
name|c
operator|.
name|onCompletion
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
comment|// jdbc close or jms commit - while futureOrSequenceLong==null ordered
comment|// work will remain pending on the Queue
name|message
operator|.
name|getMessageId
argument_list|()
operator|.
name|setFutureOrSequenceLong
argument_list|(
name|sequence
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
if|if
condition|(
name|indexListener
operator|!=
literal|null
condition|)
block|{
name|indexListener
operator|.
name|onAdd
argument_list|(
operator|new
name|IndexListener
operator|.
name|MessageContext
argument_list|(
name|context
argument_list|,
name|message
argument_list|,
operator|new
name|Runnable
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
comment|// cursor add complete
synchronized|synchronized
init|(
name|pendingAdditions
init|)
block|{
name|pendingAdditions
operator|.
name|remove
argument_list|(
name|sequence
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|pendingAdditions
operator|.
name|remove
argument_list|(
name|sequence
argument_list|)
expr_stmt|;
block|}
block|}
block|}
try|try
block|{
name|adapter
operator|.
name|doAddMessage
argument_list|(
name|c
argument_list|,
name|sequenceId
argument_list|,
name|messageId
argument_list|,
name|destination
argument_list|,
name|data
argument_list|,
name|message
operator|.
name|getExpiration
argument_list|()
argument_list|,
name|this
operator|.
name|isPrioritizedMessages
argument_list|()
condition|?
name|message
operator|.
name|getPriority
argument_list|()
else|:
literal|0
argument_list|,
name|xaXid
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
literal|"Failed to broker message: "
operator|+
name|messageId
operator|+
literal|" in container: "
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
if|if
condition|(
name|xaXid
operator|==
literal|null
condition|)
block|{
name|onAdd
argument_list|(
name|message
argument_list|,
name|sequenceId
argument_list|,
name|message
operator|.
name|getPriority
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|// jdbc commit order is random with concurrent connections - limit scan to lowest pending
specifier|private
name|long
name|minPendingSequeunceId
parameter_list|()
block|{
synchronized|synchronized
init|(
name|pendingAdditions
init|)
block|{
if|if
condition|(
operator|!
name|pendingAdditions
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|pendingAdditions
operator|.
name|get
argument_list|(
literal|0
argument_list|)
return|;
block|}
else|else
block|{
comment|// nothing pending, ensure scan is limited to current state
return|return
name|persistenceAdapter
operator|.
name|sequenceGenerator
operator|.
name|getLastSequenceId
argument_list|()
operator|+
literal|1
return|;
block|}
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|updateMessage
parameter_list|(
name|Message
name|message
parameter_list|)
throws|throws
name|IOException
block|{
name|TransactionContext
name|c
init|=
name|persistenceAdapter
operator|.
name|getTransactionContext
argument_list|()
decl_stmt|;
try|try
block|{
name|adapter
operator|.
name|doUpdateMessage
argument_list|(
name|c
argument_list|,
name|destination
argument_list|,
name|message
operator|.
name|getMessageId
argument_list|()
argument_list|,
name|ByteSequenceData
operator|.
name|toByteArray
argument_list|(
name|wireFormat
operator|.
name|marshal
argument_list|(
name|message
argument_list|)
argument_list|)
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
literal|"Failed to update message: "
operator|+
name|message
operator|.
name|getMessageId
argument_list|()
operator|+
literal|" in container: "
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
specifier|protected
name|void
name|onAdd
parameter_list|(
name|Message
name|message
parameter_list|,
name|long
name|sequenceId
parameter_list|,
name|byte
name|priority
parameter_list|)
block|{}
specifier|public
name|void
name|addMessageReference
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|MessageId
name|messageId
parameter_list|,
name|long
name|expirationTime
parameter_list|,
name|String
name|messageRef
parameter_list|)
throws|throws
name|IOException
block|{
comment|// Get a connection and insert the message into the DB.
name|TransactionContext
name|c
init|=
name|persistenceAdapter
operator|.
name|getTransactionContext
argument_list|(
name|context
argument_list|)
decl_stmt|;
try|try
block|{
name|adapter
operator|.
name|doAddMessageReference
argument_list|(
name|c
argument_list|,
name|persistenceAdapter
operator|.
name|getNextSequenceId
argument_list|()
argument_list|,
name|messageId
argument_list|,
name|destination
argument_list|,
name|expirationTime
argument_list|,
name|messageRef
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
literal|"Failed to broker message: "
operator|+
name|messageId
operator|+
literal|" in container: "
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
name|Message
name|getMessage
parameter_list|(
name|MessageId
name|messageId
parameter_list|)
throws|throws
name|IOException
block|{
comment|// Get a connection and pull the message out of the DB
name|TransactionContext
name|c
init|=
name|persistenceAdapter
operator|.
name|getTransactionContext
argument_list|()
decl_stmt|;
try|try
block|{
name|byte
name|data
index|[]
init|=
name|adapter
operator|.
name|doGetMessage
argument_list|(
name|c
argument_list|,
name|messageId
argument_list|)
decl_stmt|;
if|if
condition|(
name|data
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|Message
name|answer
init|=
operator|(
name|Message
operator|)
name|wireFormat
operator|.
name|unmarshal
argument_list|(
operator|new
name|ByteSequence
argument_list|(
name|data
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|answer
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
name|IOExceptionSupport
operator|.
name|create
argument_list|(
literal|"Failed to broker message: "
operator|+
name|messageId
operator|+
literal|" in container: "
operator|+
name|e
argument_list|,
name|e
argument_list|)
throw|;
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
literal|"Failed to broker message: "
operator|+
name|messageId
operator|+
literal|" in container: "
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
name|String
name|getMessageReference
parameter_list|(
name|MessageId
name|messageId
parameter_list|)
throws|throws
name|IOException
block|{
name|long
name|id
init|=
name|messageId
operator|.
name|getBrokerSequenceId
argument_list|()
decl_stmt|;
comment|// Get a connection and pull the message out of the DB
name|TransactionContext
name|c
init|=
name|persistenceAdapter
operator|.
name|getTransactionContext
argument_list|()
decl_stmt|;
try|try
block|{
return|return
name|adapter
operator|.
name|doGetMessageReference
argument_list|(
name|c
argument_list|,
name|id
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
name|IOExceptionSupport
operator|.
name|create
argument_list|(
literal|"Failed to broker message: "
operator|+
name|messageId
operator|+
literal|" in container: "
operator|+
name|e
argument_list|,
name|e
argument_list|)
throw|;
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
literal|"Failed to broker message: "
operator|+
name|messageId
operator|+
literal|" in container: "
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
name|removeMessage
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|MessageAck
name|ack
parameter_list|)
throws|throws
name|IOException
block|{
name|long
name|seq
init|=
name|ack
operator|.
name|getLastMessageId
argument_list|()
operator|.
name|getFutureOrSequenceLong
argument_list|()
operator|!=
literal|null
condition|?
operator|(
name|Long
operator|)
name|ack
operator|.
name|getLastMessageId
argument_list|()
operator|.
name|getFutureOrSequenceLong
argument_list|()
else|:
name|persistenceAdapter
operator|.
name|getStoreSequenceIdForMessageId
argument_list|(
name|context
argument_list|,
name|ack
operator|.
name|getLastMessageId
argument_list|()
argument_list|,
name|destination
argument_list|)
index|[
literal|0
index|]
decl_stmt|;
comment|// Get a connection and remove the message from the DB
name|TransactionContext
name|c
init|=
name|persistenceAdapter
operator|.
name|getTransactionContext
argument_list|(
name|context
argument_list|)
decl_stmt|;
try|try
block|{
name|adapter
operator|.
name|doRemoveMessage
argument_list|(
name|c
argument_list|,
name|seq
argument_list|,
name|context
operator|!=
literal|null
condition|?
name|context
operator|.
name|getXid
argument_list|()
else|:
literal|null
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
literal|"Failed to broker message: "
operator|+
name|ack
operator|.
name|getLastMessageId
argument_list|()
operator|+
literal|" in container: "
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
name|recover
parameter_list|(
specifier|final
name|MessageRecoveryListener
name|listener
parameter_list|)
throws|throws
name|Exception
block|{
comment|// Get all the Message ids out of the database.
name|TransactionContext
name|c
init|=
name|persistenceAdapter
operator|.
name|getTransactionContext
argument_list|()
decl_stmt|;
try|try
block|{
name|c
operator|=
name|persistenceAdapter
operator|.
name|getTransactionContext
argument_list|()
expr_stmt|;
name|adapter
operator|.
name|doRecover
argument_list|(
name|c
argument_list|,
name|destination
argument_list|,
operator|new
name|JDBCMessageRecoveryListener
argument_list|()
block|{
specifier|public
name|boolean
name|recoverMessage
parameter_list|(
name|long
name|sequenceId
parameter_list|,
name|byte
index|[]
name|data
parameter_list|)
throws|throws
name|Exception
block|{
name|Message
name|msg
init|=
operator|(
name|Message
operator|)
name|wireFormat
operator|.
name|unmarshal
argument_list|(
operator|new
name|ByteSequence
argument_list|(
name|data
argument_list|)
argument_list|)
decl_stmt|;
name|msg
operator|.
name|getMessageId
argument_list|()
operator|.
name|setBrokerSequenceId
argument_list|(
name|sequenceId
argument_list|)
expr_stmt|;
return|return
name|listener
operator|.
name|recoverMessage
argument_list|(
name|msg
argument_list|)
return|;
block|}
specifier|public
name|boolean
name|recoverMessageReference
parameter_list|(
name|String
name|reference
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|listener
operator|.
name|recoverMessageReference
argument_list|(
operator|new
name|MessageId
argument_list|(
name|reference
argument_list|)
argument_list|)
return|;
block|}
block|}
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
literal|"Failed to recover container. Reason: "
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
comment|/**      * @see org.apache.activemq.store.MessageStore#removeAllMessages(ConnectionContext)      */
specifier|public
name|void
name|removeAllMessages
parameter_list|(
name|ConnectionContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
comment|// Get a connection and remove the message from the DB
name|TransactionContext
name|c
init|=
name|persistenceAdapter
operator|.
name|getTransactionContext
argument_list|(
name|context
argument_list|)
decl_stmt|;
try|try
block|{
name|adapter
operator|.
name|doRemoveAllMessages
argument_list|(
name|c
argument_list|,
name|destination
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
literal|"Failed to broker remove all messages: "
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
annotation|@
name|Override
specifier|public
name|int
name|getMessageCount
parameter_list|()
throws|throws
name|IOException
block|{
name|int
name|result
init|=
literal|0
decl_stmt|;
name|TransactionContext
name|c
init|=
name|persistenceAdapter
operator|.
name|getTransactionContext
argument_list|()
decl_stmt|;
try|try
block|{
name|result
operator|=
name|adapter
operator|.
name|doGetMessageCount
argument_list|(
name|c
argument_list|,
name|destination
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
literal|"Failed to get Message Count: "
operator|+
name|destination
operator|+
literal|". Reason: "
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
return|return
name|result
return|;
block|}
comment|/**      * @param maxReturned      * @param listener      * @throws Exception      * @see org.apache.activemq.store.MessageStore#recoverNextMessages(int,      *      org.apache.activemq.store.MessageRecoveryListener)      */
specifier|public
name|void
name|recoverNextMessages
parameter_list|(
name|int
name|maxReturned
parameter_list|,
specifier|final
name|MessageRecoveryListener
name|listener
parameter_list|)
throws|throws
name|Exception
block|{
name|TransactionContext
name|c
init|=
name|persistenceAdapter
operator|.
name|getTransactionContext
argument_list|()
decl_stmt|;
try|try
block|{
if|if
condition|(
name|LOG
operator|.
name|isTraceEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|trace
argument_list|(
name|this
operator|+
literal|" recoverNext lastRecovered:"
operator|+
name|Arrays
operator|.
name|toString
argument_list|(
name|perPriorityLastRecovered
argument_list|)
operator|+
literal|", minPending:"
operator|+
name|minPendingSequeunceId
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|adapter
operator|.
name|doRecoverNextMessages
argument_list|(
name|c
argument_list|,
name|destination
argument_list|,
name|perPriorityLastRecovered
argument_list|,
name|minPendingSequeunceId
argument_list|()
argument_list|,
name|maxReturned
argument_list|,
name|isPrioritizedMessages
argument_list|()
argument_list|,
operator|new
name|JDBCMessageRecoveryListener
argument_list|()
block|{
specifier|public
name|boolean
name|recoverMessage
parameter_list|(
name|long
name|sequenceId
parameter_list|,
name|byte
index|[]
name|data
parameter_list|)
throws|throws
name|Exception
block|{
name|Message
name|msg
init|=
operator|(
name|Message
operator|)
name|wireFormat
operator|.
name|unmarshal
argument_list|(
operator|new
name|ByteSequence
argument_list|(
name|data
argument_list|)
argument_list|)
decl_stmt|;
name|msg
operator|.
name|getMessageId
argument_list|()
operator|.
name|setBrokerSequenceId
argument_list|(
name|sequenceId
argument_list|)
expr_stmt|;
name|msg
operator|.
name|getMessageId
argument_list|()
operator|.
name|setFutureOrSequenceLong
argument_list|(
name|sequenceId
argument_list|)
expr_stmt|;
name|listener
operator|.
name|recoverMessage
argument_list|(
name|msg
argument_list|)
expr_stmt|;
name|trackLastRecovered
argument_list|(
name|sequenceId
argument_list|,
name|msg
operator|.
name|getPriority
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
specifier|public
name|boolean
name|recoverMessageReference
parameter_list|(
name|String
name|reference
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|listener
operator|.
name|hasSpace
argument_list|()
condition|)
block|{
name|listener
operator|.
name|recoverMessageReference
argument_list|(
operator|new
name|MessageId
argument_list|(
name|reference
argument_list|)
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
block|}
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
specifier|private
name|void
name|trackLastRecovered
parameter_list|(
name|long
name|sequenceId
parameter_list|,
name|int
name|priority
parameter_list|)
block|{
name|perPriorityLastRecovered
index|[
name|isPrioritizedMessages
argument_list|()
condition|?
name|priority
else|:
literal|0
index|]
operator|=
name|sequenceId
expr_stmt|;
block|}
comment|/**      * @see org.apache.activemq.store.MessageStore#resetBatching()      */
specifier|public
name|void
name|resetBatching
parameter_list|()
block|{
if|if
condition|(
name|LOG
operator|.
name|isTraceEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|trace
argument_list|(
name|this
operator|+
literal|" resetBatching. last recovered: "
operator|+
name|Arrays
operator|.
name|toString
argument_list|(
name|perPriorityLastRecovered
argument_list|)
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|perPriorityLastRecovered
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|perPriorityLastRecovered
index|[
name|i
index|]
operator|=
operator|-
literal|1
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|setBatch
parameter_list|(
name|MessageId
name|messageId
parameter_list|)
block|{
try|try
block|{
name|long
index|[]
name|storedValues
init|=
name|persistenceAdapter
operator|.
name|getStoreSequenceIdForMessageId
argument_list|(
literal|null
argument_list|,
name|messageId
argument_list|,
name|destination
argument_list|)
decl_stmt|;
name|trackLastRecovered
argument_list|(
name|storedValues
index|[
literal|0
index|]
argument_list|,
operator|(
name|int
operator|)
name|storedValues
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ignoredAsAlreadyLogged
parameter_list|)
block|{
name|resetBatching
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|LOG
operator|.
name|isTraceEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|trace
argument_list|(
name|this
operator|+
literal|" setBatch: new last recovered: "
operator|+
name|Arrays
operator|.
name|toString
argument_list|(
name|perPriorityLastRecovered
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|setPrioritizedMessages
parameter_list|(
name|boolean
name|prioritizedMessages
parameter_list|)
block|{
name|super
operator|.
name|setPrioritizedMessages
argument_list|(
name|prioritizedMessages
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|destination
operator|.
name|getPhysicalName
argument_list|()
operator|+
literal|",pendingSize:"
operator|+
name|pendingAdditions
operator|.
name|size
argument_list|()
return|;
block|}
block|}
end_class

end_unit

