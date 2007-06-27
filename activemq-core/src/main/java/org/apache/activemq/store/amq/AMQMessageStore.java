begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *   * Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements. See the NOTICE  * file distributed with this work for additional information regarding copyright ownership. The ASF licenses this file  * to You under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the  * License. You may obtain a copy of the License at  *   * http://www.apache.org/licenses/LICENSE-2.0  *   * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on  * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the  * specific language governing permissions and limitations under the License.  */
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
name|amq
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
name|io
operator|.
name|InterruptedIOException
import|;
end_import

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
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
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
name|LinkedHashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
operator|.
name|Entry
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
name|CountDownLatch
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
name|AtomicReference
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
name|DataStructure
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
name|JournalQueueAck
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
name|kaha
operator|.
name|impl
operator|.
name|async
operator|.
name|Location
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
name|memory
operator|.
name|UsageManager
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
name|store
operator|.
name|MessageStore
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
name|PersistenceAdapter
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
name|ReferenceStore
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
name|ReferenceStore
operator|.
name|ReferenceData
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
name|thread
operator|.
name|Task
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
name|thread
operator|.
name|TaskRunner
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
name|transaction
operator|.
name|Synchronization
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
name|Callback
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
name|TransactionTemplate
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
comment|/**  * A MessageStore that uses a Journal to store it's messages.  *   * @version $Revision: 1.14 $  */
end_comment

begin_class
specifier|public
class|class
name|AMQMessageStore
implements|implements
name|MessageStore
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
name|AMQMessageStore
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|protected
specifier|final
name|AMQPersistenceAdapter
name|peristenceAdapter
decl_stmt|;
specifier|protected
specifier|final
name|AMQTransactionStore
name|transactionStore
decl_stmt|;
specifier|protected
specifier|final
name|ReferenceStore
name|referenceStore
decl_stmt|;
specifier|protected
specifier|final
name|ActiveMQDestination
name|destination
decl_stmt|;
specifier|protected
specifier|final
name|TransactionTemplate
name|transactionTemplate
decl_stmt|;
specifier|private
name|LinkedHashMap
argument_list|<
name|MessageId
argument_list|,
name|ReferenceData
argument_list|>
name|messages
init|=
operator|new
name|LinkedHashMap
argument_list|<
name|MessageId
argument_list|,
name|ReferenceData
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
name|ArrayList
argument_list|<
name|MessageAck
argument_list|>
name|messageAcks
init|=
operator|new
name|ArrayList
argument_list|<
name|MessageAck
argument_list|>
argument_list|()
decl_stmt|;
comment|/** A MessageStore that we can use to retrieve messages quickly. */
specifier|private
name|LinkedHashMap
argument_list|<
name|MessageId
argument_list|,
name|ReferenceData
argument_list|>
name|cpAddedMessageIds
decl_stmt|;
specifier|protected
name|Location
name|lastLocation
decl_stmt|;
specifier|protected
name|Location
name|lastWrittenLocation
decl_stmt|;
specifier|protected
name|HashSet
argument_list|<
name|Location
argument_list|>
name|inFlightTxLocations
init|=
operator|new
name|HashSet
argument_list|<
name|Location
argument_list|>
argument_list|()
decl_stmt|;
specifier|protected
specifier|final
name|TaskRunner
name|asyncWriteTask
decl_stmt|;
specifier|protected
name|CountDownLatch
name|flushLatch
decl_stmt|;
specifier|private
specifier|final
name|boolean
name|debug
init|=
name|log
operator|.
name|isDebugEnabled
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|AtomicReference
argument_list|<
name|Location
argument_list|>
name|mark
init|=
operator|new
name|AtomicReference
argument_list|<
name|Location
argument_list|>
argument_list|()
decl_stmt|;
specifier|public
name|AMQMessageStore
parameter_list|(
name|AMQPersistenceAdapter
name|adapter
parameter_list|,
name|ReferenceStore
name|referenceStore
parameter_list|,
name|ActiveMQDestination
name|destination
parameter_list|)
block|{
name|this
operator|.
name|peristenceAdapter
operator|=
name|adapter
expr_stmt|;
name|this
operator|.
name|transactionStore
operator|=
name|adapter
operator|.
name|getTransactionStore
argument_list|()
expr_stmt|;
name|this
operator|.
name|referenceStore
operator|=
name|referenceStore
expr_stmt|;
name|this
operator|.
name|destination
operator|=
name|destination
expr_stmt|;
name|this
operator|.
name|transactionTemplate
operator|=
operator|new
name|TransactionTemplate
argument_list|(
name|adapter
argument_list|,
operator|new
name|ConnectionContext
argument_list|()
argument_list|)
expr_stmt|;
name|asyncWriteTask
operator|=
name|adapter
operator|.
name|getTaskRunnerFactory
argument_list|()
operator|.
name|createTaskRunner
argument_list|(
operator|new
name|Task
argument_list|()
block|{
specifier|public
name|boolean
name|iterate
parameter_list|()
block|{
name|asyncWrite
argument_list|()
expr_stmt|;
return|return
literal|false
return|;
block|}
block|}
argument_list|,
literal|"Checkpoint: "
operator|+
name|destination
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setUsageManager
parameter_list|(
name|UsageManager
name|usageManager
parameter_list|)
block|{
name|referenceStore
operator|.
name|setUsageManager
argument_list|(
name|usageManager
argument_list|)
expr_stmt|;
block|}
comment|/**      * Not synchronized since the Journal has better throughput if you increase the number of concurrent writes that it      * is doing.      */
specifier|public
name|void
name|addMessage
parameter_list|(
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
specifier|final
name|MessageId
name|id
init|=
name|message
operator|.
name|getMessageId
argument_list|()
decl_stmt|;
specifier|final
name|Location
name|location
init|=
name|peristenceAdapter
operator|.
name|writeCommand
argument_list|(
name|message
argument_list|,
name|message
operator|.
name|isResponseRequired
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|context
operator|.
name|isInTransaction
argument_list|()
condition|)
block|{
if|if
condition|(
name|debug
condition|)
name|log
operator|.
name|debug
argument_list|(
literal|"Journalled message add for: "
operator|+
name|id
operator|+
literal|", at: "
operator|+
name|location
argument_list|)
expr_stmt|;
name|addMessage
argument_list|(
name|message
argument_list|,
name|location
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|debug
condition|)
name|log
operator|.
name|debug
argument_list|(
literal|"Journalled transacted message add for: "
operator|+
name|id
operator|+
literal|", at: "
operator|+
name|location
argument_list|)
expr_stmt|;
synchronized|synchronized
init|(
name|this
init|)
block|{
name|inFlightTxLocations
operator|.
name|add
argument_list|(
name|location
argument_list|)
expr_stmt|;
block|}
name|transactionStore
operator|.
name|addMessage
argument_list|(
name|this
argument_list|,
name|message
argument_list|,
name|location
argument_list|)
expr_stmt|;
name|context
operator|.
name|getTransaction
argument_list|()
operator|.
name|addSynchronization
argument_list|(
operator|new
name|Synchronization
argument_list|()
block|{
specifier|public
name|void
name|afterCommit
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|debug
condition|)
name|log
operator|.
name|debug
argument_list|(
literal|"Transacted message add commit for: "
operator|+
name|id
operator|+
literal|", at: "
operator|+
name|location
argument_list|)
expr_stmt|;
synchronized|synchronized
init|(
name|AMQMessageStore
operator|.
name|this
init|)
block|{
name|inFlightTxLocations
operator|.
name|remove
argument_list|(
name|location
argument_list|)
expr_stmt|;
name|addMessage
argument_list|(
name|message
argument_list|,
name|location
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|afterRollback
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|debug
condition|)
name|log
operator|.
name|debug
argument_list|(
literal|"Transacted message add rollback for: "
operator|+
name|id
operator|+
literal|", at: "
operator|+
name|location
argument_list|)
expr_stmt|;
synchronized|synchronized
init|(
name|AMQMessageStore
operator|.
name|this
init|)
block|{
name|inFlightTxLocations
operator|.
name|remove
argument_list|(
name|location
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
block|}
block|}
name|void
name|addMessage
parameter_list|(
specifier|final
name|Message
name|message
parameter_list|,
specifier|final
name|Location
name|location
parameter_list|)
throws|throws
name|InterruptedIOException
block|{
name|ReferenceData
name|data
init|=
operator|new
name|ReferenceData
argument_list|()
decl_stmt|;
name|data
operator|.
name|setExpiration
argument_list|(
name|message
operator|.
name|getExpiration
argument_list|()
argument_list|)
expr_stmt|;
name|data
operator|.
name|setFileId
argument_list|(
name|location
operator|.
name|getDataFileId
argument_list|()
argument_list|)
expr_stmt|;
name|data
operator|.
name|setOffset
argument_list|(
name|location
operator|.
name|getOffset
argument_list|()
argument_list|)
expr_stmt|;
synchronized|synchronized
init|(
name|this
init|)
block|{
name|lastLocation
operator|=
name|location
expr_stmt|;
name|messages
operator|.
name|put
argument_list|(
name|message
operator|.
name|getMessageId
argument_list|()
argument_list|,
name|data
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|asyncWriteTask
operator|.
name|wakeup
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|InterruptedIOException
argument_list|()
throw|;
block|}
block|}
specifier|public
name|boolean
name|replayAddMessage
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|Message
name|message
parameter_list|,
name|Location
name|location
parameter_list|)
block|{
name|MessageId
name|id
init|=
name|message
operator|.
name|getMessageId
argument_list|()
decl_stmt|;
try|try
block|{
comment|// Only add the message if it has not already been added.
name|ReferenceData
name|data
init|=
name|referenceStore
operator|.
name|getMessageReference
argument_list|(
name|id
argument_list|)
decl_stmt|;
if|if
condition|(
name|data
operator|==
literal|null
condition|)
block|{
name|data
operator|=
operator|new
name|ReferenceData
argument_list|()
expr_stmt|;
name|data
operator|.
name|setExpiration
argument_list|(
name|message
operator|.
name|getExpiration
argument_list|()
argument_list|)
expr_stmt|;
name|data
operator|.
name|setFileId
argument_list|(
name|location
operator|.
name|getDataFileId
argument_list|()
argument_list|)
expr_stmt|;
name|data
operator|.
name|setOffset
argument_list|(
name|location
operator|.
name|getOffset
argument_list|()
argument_list|)
expr_stmt|;
name|referenceStore
operator|.
name|addMessageReference
argument_list|(
name|context
argument_list|,
name|id
argument_list|,
name|data
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
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
literal|"Could not replay add for message '"
operator|+
name|id
operator|+
literal|"'.  Message may have already been added. reason: "
operator|+
name|e
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
return|return
literal|false
return|;
block|}
comment|/**      */
specifier|public
name|void
name|removeMessage
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
specifier|final
name|MessageAck
name|ack
parameter_list|)
throws|throws
name|IOException
block|{
name|JournalQueueAck
name|remove
init|=
operator|new
name|JournalQueueAck
argument_list|()
decl_stmt|;
name|remove
operator|.
name|setDestination
argument_list|(
name|destination
argument_list|)
expr_stmt|;
name|remove
operator|.
name|setMessageAck
argument_list|(
name|ack
argument_list|)
expr_stmt|;
specifier|final
name|Location
name|location
init|=
name|peristenceAdapter
operator|.
name|writeCommand
argument_list|(
name|remove
argument_list|,
name|ack
operator|.
name|isResponseRequired
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|context
operator|.
name|isInTransaction
argument_list|()
condition|)
block|{
if|if
condition|(
name|debug
condition|)
name|log
operator|.
name|debug
argument_list|(
literal|"Journalled message remove for: "
operator|+
name|ack
operator|.
name|getLastMessageId
argument_list|()
operator|+
literal|", at: "
operator|+
name|location
argument_list|)
expr_stmt|;
name|removeMessage
argument_list|(
name|ack
argument_list|,
name|location
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|debug
condition|)
name|log
operator|.
name|debug
argument_list|(
literal|"Journalled transacted message remove for: "
operator|+
name|ack
operator|.
name|getLastMessageId
argument_list|()
operator|+
literal|", at: "
operator|+
name|location
argument_list|)
expr_stmt|;
synchronized|synchronized
init|(
name|this
init|)
block|{
name|inFlightTxLocations
operator|.
name|add
argument_list|(
name|location
argument_list|)
expr_stmt|;
block|}
name|transactionStore
operator|.
name|removeMessage
argument_list|(
name|this
argument_list|,
name|ack
argument_list|,
name|location
argument_list|)
expr_stmt|;
name|context
operator|.
name|getTransaction
argument_list|()
operator|.
name|addSynchronization
argument_list|(
operator|new
name|Synchronization
argument_list|()
block|{
specifier|public
name|void
name|afterCommit
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|debug
condition|)
name|log
operator|.
name|debug
argument_list|(
literal|"Transacted message remove commit for: "
operator|+
name|ack
operator|.
name|getLastMessageId
argument_list|()
operator|+
literal|", at: "
operator|+
name|location
argument_list|)
expr_stmt|;
synchronized|synchronized
init|(
name|AMQMessageStore
operator|.
name|this
init|)
block|{
name|inFlightTxLocations
operator|.
name|remove
argument_list|(
name|location
argument_list|)
expr_stmt|;
name|removeMessage
argument_list|(
name|ack
argument_list|,
name|location
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|afterRollback
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|debug
condition|)
name|log
operator|.
name|debug
argument_list|(
literal|"Transacted message remove rollback for: "
operator|+
name|ack
operator|.
name|getLastMessageId
argument_list|()
operator|+
literal|", at: "
operator|+
name|location
argument_list|)
expr_stmt|;
synchronized|synchronized
init|(
name|AMQMessageStore
operator|.
name|this
init|)
block|{
name|inFlightTxLocations
operator|.
name|remove
argument_list|(
name|location
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
block|}
block|}
specifier|final
name|void
name|removeMessage
parameter_list|(
specifier|final
name|MessageAck
name|ack
parameter_list|,
specifier|final
name|Location
name|location
parameter_list|)
throws|throws
name|InterruptedIOException
block|{
name|ReferenceData
name|data
decl_stmt|;
synchronized|synchronized
init|(
name|this
init|)
block|{
name|lastLocation
operator|=
name|location
expr_stmt|;
name|MessageId
name|id
init|=
name|ack
operator|.
name|getLastMessageId
argument_list|()
decl_stmt|;
name|data
operator|=
name|messages
operator|.
name|remove
argument_list|(
name|id
argument_list|)
expr_stmt|;
if|if
condition|(
name|data
operator|==
literal|null
condition|)
block|{
name|messageAcks
operator|.
name|add
argument_list|(
name|ack
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|data
operator|==
literal|null
condition|)
block|{
try|try
block|{
name|asyncWriteTask
operator|.
name|wakeup
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|InterruptedIOException
argument_list|()
throw|;
block|}
block|}
block|}
specifier|public
name|boolean
name|replayRemoveMessage
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|MessageAck
name|messageAck
parameter_list|)
block|{
try|try
block|{
comment|// Only remove the message if it has not already been removed.
name|ReferenceData
name|t
init|=
name|referenceStore
operator|.
name|getMessageReference
argument_list|(
name|messageAck
operator|.
name|getLastMessageId
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|t
operator|!=
literal|null
condition|)
block|{
name|referenceStore
operator|.
name|removeMessage
argument_list|(
name|context
argument_list|,
name|messageAck
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
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
literal|"Could not replay acknowledge for message '"
operator|+
name|messageAck
operator|.
name|getLastMessageId
argument_list|()
operator|+
literal|"'.  Message may have already been acknowledged. reason: "
operator|+
name|e
argument_list|)
expr_stmt|;
block|}
return|return
literal|false
return|;
block|}
comment|/**      * Waits till the lastest data has landed on the referenceStore      *       * @throws InterruptedIOException      */
specifier|public
name|void
name|flush
parameter_list|()
throws|throws
name|InterruptedIOException
block|{
if|if
condition|(
name|log
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"flush starting ..."
argument_list|)
expr_stmt|;
block|}
name|CountDownLatch
name|countDown
decl_stmt|;
synchronized|synchronized
init|(
name|this
init|)
block|{
if|if
condition|(
name|lastWrittenLocation
operator|==
name|lastLocation
condition|)
block|{
return|return;
block|}
if|if
condition|(
name|flushLatch
operator|==
literal|null
condition|)
block|{
name|flushLatch
operator|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
name|countDown
operator|=
name|flushLatch
expr_stmt|;
block|}
try|try
block|{
name|asyncWriteTask
operator|.
name|wakeup
argument_list|()
expr_stmt|;
name|countDown
operator|.
name|await
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|InterruptedIOException
argument_list|()
throw|;
block|}
if|if
condition|(
name|log
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"flush finished"
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * @return      * @throws IOException      */
name|void
name|asyncWrite
parameter_list|()
block|{
try|try
block|{
name|CountDownLatch
name|countDown
decl_stmt|;
synchronized|synchronized
init|(
name|this
init|)
block|{
name|countDown
operator|=
name|flushLatch
expr_stmt|;
name|flushLatch
operator|=
literal|null
expr_stmt|;
block|}
name|mark
operator|.
name|set
argument_list|(
name|doAsyncWrite
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|countDown
operator|!=
literal|null
condition|)
block|{
name|countDown
operator|.
name|countDown
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Checkpoint failed: "
operator|+
name|e
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * @return      * @throws IOException      */
specifier|protected
name|Location
name|doAsyncWrite
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|ArrayList
argument_list|<
name|MessageAck
argument_list|>
name|cpRemovedMessageLocations
decl_stmt|;
specifier|final
name|ArrayList
argument_list|<
name|Location
argument_list|>
name|cpActiveJournalLocations
decl_stmt|;
specifier|final
name|int
name|maxCheckpointMessageAddSize
init|=
name|peristenceAdapter
operator|.
name|getMaxCheckpointMessageAddSize
argument_list|()
decl_stmt|;
specifier|final
name|Location
name|lastLocation
decl_stmt|;
comment|// swap out the message hash maps..
synchronized|synchronized
init|(
name|this
init|)
block|{
name|cpAddedMessageIds
operator|=
name|this
operator|.
name|messages
expr_stmt|;
name|cpRemovedMessageLocations
operator|=
name|this
operator|.
name|messageAcks
expr_stmt|;
name|cpActiveJournalLocations
operator|=
operator|new
name|ArrayList
argument_list|<
name|Location
argument_list|>
argument_list|(
name|inFlightTxLocations
argument_list|)
expr_stmt|;
name|this
operator|.
name|messages
operator|=
operator|new
name|LinkedHashMap
argument_list|<
name|MessageId
argument_list|,
name|ReferenceData
argument_list|>
argument_list|()
expr_stmt|;
name|this
operator|.
name|messageAcks
operator|=
operator|new
name|ArrayList
argument_list|<
name|MessageAck
argument_list|>
argument_list|()
expr_stmt|;
name|lastLocation
operator|=
name|this
operator|.
name|lastLocation
expr_stmt|;
block|}
if|if
condition|(
name|log
operator|.
name|isDebugEnabled
argument_list|()
condition|)
name|log
operator|.
name|debug
argument_list|(
literal|"Doing batch update... adding: "
operator|+
name|cpAddedMessageIds
operator|.
name|size
argument_list|()
operator|+
literal|" removing: "
operator|+
name|cpRemovedMessageLocations
operator|.
name|size
argument_list|()
operator|+
literal|" "
argument_list|)
expr_stmt|;
name|transactionTemplate
operator|.
name|run
argument_list|(
operator|new
name|Callback
argument_list|()
block|{
specifier|public
name|void
name|execute
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|size
init|=
literal|0
decl_stmt|;
name|PersistenceAdapter
name|persitanceAdapter
init|=
name|transactionTemplate
operator|.
name|getPersistenceAdapter
argument_list|()
decl_stmt|;
name|ConnectionContext
name|context
init|=
name|transactionTemplate
operator|.
name|getContext
argument_list|()
decl_stmt|;
comment|// Checkpoint the added messages.
name|Iterator
argument_list|<
name|Entry
argument_list|<
name|MessageId
argument_list|,
name|ReferenceData
argument_list|>
argument_list|>
name|iterator
init|=
name|cpAddedMessageIds
operator|.
name|entrySet
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|iterator
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|Entry
argument_list|<
name|MessageId
argument_list|,
name|ReferenceData
argument_list|>
name|entry
init|=
name|iterator
operator|.
name|next
argument_list|()
decl_stmt|;
try|try
block|{
name|referenceStore
operator|.
name|addMessageReference
argument_list|(
name|context
argument_list|,
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
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
literal|"Message could not be added to long term store: "
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
name|size
operator|++
expr_stmt|;
comment|// Commit the batch if it's getting too big
if|if
condition|(
name|size
operator|>=
name|maxCheckpointMessageAddSize
condition|)
block|{
name|persitanceAdapter
operator|.
name|commitTransaction
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|persitanceAdapter
operator|.
name|beginTransaction
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|size
operator|=
literal|0
expr_stmt|;
block|}
block|}
name|persitanceAdapter
operator|.
name|commitTransaction
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|persitanceAdapter
operator|.
name|beginTransaction
argument_list|(
name|context
argument_list|)
expr_stmt|;
comment|// Checkpoint the removed messages.
for|for
control|(
name|MessageAck
name|ack
range|:
name|cpRemovedMessageLocations
control|)
block|{
try|try
block|{
name|referenceStore
operator|.
name|removeMessage
argument_list|(
name|transactionTemplate
operator|.
name|getContext
argument_list|()
argument_list|,
name|ack
argument_list|)
expr_stmt|;
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
literal|"Message could not be removed from long term store: "
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
block|}
block|}
block|}
argument_list|)
expr_stmt|;
name|log
operator|.
name|debug
argument_list|(
literal|"Batch update done."
argument_list|)
expr_stmt|;
synchronized|synchronized
init|(
name|this
init|)
block|{
name|cpAddedMessageIds
operator|=
literal|null
expr_stmt|;
name|lastWrittenLocation
operator|=
name|lastLocation
expr_stmt|;
block|}
if|if
condition|(
name|cpActiveJournalLocations
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|Collections
operator|.
name|sort
argument_list|(
name|cpActiveJournalLocations
argument_list|)
expr_stmt|;
return|return
name|cpActiveJournalLocations
operator|.
name|get
argument_list|(
literal|0
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|lastLocation
return|;
block|}
block|}
comment|/**      *       */
specifier|public
name|Message
name|getMessage
parameter_list|(
name|MessageId
name|identity
parameter_list|)
throws|throws
name|IOException
block|{
name|ReferenceData
name|data
init|=
literal|null
decl_stmt|;
synchronized|synchronized
init|(
name|this
init|)
block|{
comment|// Is it still in flight???
name|data
operator|=
name|messages
operator|.
name|get
argument_list|(
name|identity
argument_list|)
expr_stmt|;
if|if
condition|(
name|data
operator|==
literal|null
operator|&&
name|cpAddedMessageIds
operator|!=
literal|null
condition|)
block|{
name|data
operator|=
name|cpAddedMessageIds
operator|.
name|get
argument_list|(
name|identity
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|data
operator|==
literal|null
condition|)
block|{
name|data
operator|=
name|referenceStore
operator|.
name|getMessageReference
argument_list|(
name|identity
argument_list|)
expr_stmt|;
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
block|}
name|Location
name|location
init|=
operator|new
name|Location
argument_list|()
decl_stmt|;
name|location
operator|.
name|setDataFileId
argument_list|(
name|data
operator|.
name|getFileId
argument_list|()
argument_list|)
expr_stmt|;
name|location
operator|.
name|setOffset
argument_list|(
name|data
operator|.
name|getOffset
argument_list|()
argument_list|)
expr_stmt|;
name|DataStructure
name|rc
init|=
name|peristenceAdapter
operator|.
name|readCommand
argument_list|(
name|location
argument_list|)
decl_stmt|;
try|try
block|{
return|return
operator|(
name|Message
operator|)
name|rc
return|;
block|}
catch|catch
parameter_list|(
name|ClassCastException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Could not read message "
operator|+
name|identity
operator|+
literal|" at location "
operator|+
name|location
operator|+
literal|", expected a message, but got: "
operator|+
name|rc
argument_list|)
throw|;
block|}
block|}
comment|/**      * Replays the referenceStore first as those messages are the oldest ones, then messages are replayed from the      * transaction log and then the cache is updated.      *       * @param listener      * @throws Exception      */
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
name|flush
argument_list|()
expr_stmt|;
name|referenceStore
operator|.
name|recover
argument_list|(
operator|new
name|RecoveryListenerAdapter
argument_list|(
name|this
argument_list|,
name|listener
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|start
parameter_list|()
throws|throws
name|Exception
block|{
name|referenceStore
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|stop
parameter_list|()
throws|throws
name|Exception
block|{
name|flush
argument_list|()
expr_stmt|;
name|asyncWriteTask
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|referenceStore
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
comment|/**      * @return Returns the longTermStore.      */
specifier|public
name|ReferenceStore
name|getReferenceStore
parameter_list|()
block|{
return|return
name|referenceStore
return|;
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
name|flush
argument_list|()
expr_stmt|;
name|referenceStore
operator|.
name|removeAllMessages
argument_list|(
name|context
argument_list|)
expr_stmt|;
block|}
specifier|public
name|ActiveMQDestination
name|getDestination
parameter_list|()
block|{
return|return
name|destination
return|;
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
throw|throw
operator|new
name|IOException
argument_list|(
literal|"The journal does not support message references."
argument_list|)
throw|;
block|}
specifier|public
name|String
name|getMessageReference
parameter_list|(
name|MessageId
name|identity
parameter_list|)
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"The journal does not support message references."
argument_list|)
throw|;
block|}
comment|/**      * @return      * @throws IOException      * @see org.apache.activemq.store.MessageStore#getMessageCount()      */
specifier|public
name|int
name|getMessageCount
parameter_list|()
throws|throws
name|IOException
block|{
name|flush
argument_list|()
expr_stmt|;
return|return
name|referenceStore
operator|.
name|getMessageCount
argument_list|()
return|;
block|}
specifier|public
name|void
name|recoverNextMessages
parameter_list|(
name|int
name|maxReturned
parameter_list|,
name|MessageRecoveryListener
name|listener
parameter_list|)
throws|throws
name|Exception
block|{
comment|/*         RecoveryListenerAdapter recoveryListener=new RecoveryListenerAdapter(this,listener);         if(referenceStore.supportsExternalBatchControl()){             synchronized(this){                 referenceStore.recoverNextMessages(maxReturned,recoveryListener);                 if(recoveryListener.size()==0&&recoveryListener.hasSpace()){                     // check for inflight messages                     int count=0;                     Iterator<Entry<MessageId,ReferenceData>> iterator=messages.entrySet().iterator();                     while(iterator.hasNext()&&count<maxReturned&&recoveryListener.hasSpace()){                         Entry<MessageId,ReferenceData> entry=iterator.next();                         ReferenceData data=entry.getValue();                         Message message=getMessage(data);                         recoveryListener.recoverMessage(message);                         count++;                     }                     referenceStore.setBatch(recoveryListener.getLastRecoveredMessageId());                 }             }         }else{             flush();             referenceStore.recoverNextMessages(maxReturned,recoveryListener);         }         */
name|RecoveryListenerAdapter
name|recoveryListener
init|=
operator|new
name|RecoveryListenerAdapter
argument_list|(
name|this
argument_list|,
name|listener
argument_list|)
decl_stmt|;
name|referenceStore
operator|.
name|recoverNextMessages
argument_list|(
name|maxReturned
argument_list|,
name|recoveryListener
argument_list|)
expr_stmt|;
if|if
condition|(
name|recoveryListener
operator|.
name|size
argument_list|()
operator|==
literal|0
operator|&&
name|recoveryListener
operator|.
name|hasSpace
argument_list|()
condition|)
block|{
name|flush
argument_list|()
expr_stmt|;
name|referenceStore
operator|.
name|recoverNextMessages
argument_list|(
name|maxReturned
argument_list|,
name|recoveryListener
argument_list|)
expr_stmt|;
block|}
block|}
name|Message
name|getMessage
parameter_list|(
name|ReferenceData
name|data
parameter_list|)
throws|throws
name|IOException
block|{
name|Location
name|location
init|=
operator|new
name|Location
argument_list|()
decl_stmt|;
name|location
operator|.
name|setDataFileId
argument_list|(
name|data
operator|.
name|getFileId
argument_list|()
argument_list|)
expr_stmt|;
name|location
operator|.
name|setOffset
argument_list|(
name|data
operator|.
name|getOffset
argument_list|()
argument_list|)
expr_stmt|;
name|DataStructure
name|rc
init|=
name|peristenceAdapter
operator|.
name|readCommand
argument_list|(
name|location
argument_list|)
decl_stmt|;
try|try
block|{
return|return
operator|(
name|Message
operator|)
name|rc
return|;
block|}
catch|catch
parameter_list|(
name|ClassCastException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Could not read message  at location "
operator|+
name|location
operator|+
literal|", expected a message, but got: "
operator|+
name|rc
argument_list|)
throw|;
block|}
block|}
specifier|public
name|void
name|resetBatching
parameter_list|()
block|{
name|referenceStore
operator|.
name|resetBatching
argument_list|()
expr_stmt|;
block|}
specifier|public
name|Location
name|getMark
parameter_list|()
block|{
return|return
name|mark
operator|.
name|get
argument_list|()
return|;
block|}
block|}
end_class

end_unit

