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
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicLong
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
comment|/**  * @version $Revision: 1.10 $  */
end_comment

begin_class
specifier|public
class|class
name|JDBCMessageStore
extends|extends
name|AbstractMessageStore
block|{
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
name|AtomicLong
name|lastStoreSequenceId
init|=
operator|new
name|AtomicLong
argument_list|(
operator|-
literal|1
argument_list|)
decl_stmt|;
specifier|protected
name|ActiveMQMessageAudit
name|audit
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
block|}
specifier|public
name|void
name|addMessage
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
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
name|long
name|sequenceId
init|=
name|persistenceAdapter
operator|.
name|getNextSequenceId
argument_list|()
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
name|message
operator|.
name|getPriority
argument_list|()
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
name|getStoreSequenceIdForMessageId
argument_list|(
name|ack
operator|.
name|getLastMessageId
argument_list|()
argument_list|)
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
name|adapter
operator|.
name|doRecoverNextMessages
argument_list|(
name|c
argument_list|,
name|destination
argument_list|,
name|lastStoreSequenceId
operator|.
name|get
argument_list|()
argument_list|,
name|maxReturned
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
if|if
condition|(
name|listener
operator|.
name|hasSpace
argument_list|()
condition|)
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
name|listener
operator|.
name|recoverMessage
argument_list|(
name|msg
argument_list|)
expr_stmt|;
name|lastStoreSequenceId
operator|.
name|set
argument_list|(
name|sequenceId
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
literal|" resetBatch, existing last seqId: "
operator|+
name|lastStoreSequenceId
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|lastStoreSequenceId
operator|.
name|set
argument_list|(
operator|-
literal|1
argument_list|)
expr_stmt|;
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
name|long
name|storeSequenceId
init|=
operator|-
literal|1
decl_stmt|;
try|try
block|{
name|storeSequenceId
operator|=
name|getStoreSequenceIdForMessageId
argument_list|(
name|messageId
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ignoredAsAlreadyLogged
parameter_list|)
block|{
comment|// reset batch in effect with default -1 value
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
name|destination
operator|.
name|getPhysicalName
argument_list|()
operator|+
literal|" setBatch: new sequenceId: "
operator|+
name|storeSequenceId
operator|+
literal|",existing last seqId: "
operator|+
name|lastStoreSequenceId
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|lastStoreSequenceId
operator|.
name|set
argument_list|(
name|storeSequenceId
argument_list|)
expr_stmt|;
block|}
specifier|private
name|long
name|getStoreSequenceIdForMessageId
parameter_list|(
name|MessageId
name|messageId
parameter_list|)
throws|throws
name|IOException
block|{
name|long
name|result
init|=
operator|-
literal|1
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
name|getStoreSequenceId
argument_list|(
name|c
argument_list|,
name|destination
argument_list|,
name|messageId
argument_list|)
index|[
literal|0
index|]
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
literal|"Failed to get store sequenceId for messageId: "
operator|+
name|messageId
operator|+
literal|", on: "
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
name|adapter
operator|.
name|setPrioritizedMessages
argument_list|(
name|prioritizedMessages
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

