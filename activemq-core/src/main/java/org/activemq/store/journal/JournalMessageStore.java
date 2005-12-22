begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Copyright 2004 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|activemq
operator|.
name|store
operator|.
name|journal
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
name|org
operator|.
name|activeio
operator|.
name|journal
operator|.
name|RecordLocation
import|;
end_import

begin_import
import|import
name|org
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
name|JournalMessageStore
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
name|JournalMessageStore
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|protected
specifier|final
name|JournalPersistenceAdapter
name|peristenceAdapter
decl_stmt|;
specifier|protected
specifier|final
name|JournalTransactionStore
name|transactionStore
decl_stmt|;
specifier|protected
specifier|final
name|MessageStore
name|longTermStore
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
name|messages
init|=
operator|new
name|LinkedHashMap
argument_list|()
decl_stmt|;
specifier|private
name|ArrayList
name|messageAcks
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
comment|/** A MessageStore that we can use to retrieve messages quickly. */
specifier|private
name|LinkedHashMap
name|cpAddedMessageIds
decl_stmt|;
specifier|protected
name|RecordLocation
name|lastLocation
decl_stmt|;
specifier|protected
name|HashSet
name|inFlightTxLocations
init|=
operator|new
name|HashSet
argument_list|()
decl_stmt|;
specifier|public
name|JournalMessageStore
parameter_list|(
name|JournalPersistenceAdapter
name|adapter
parameter_list|,
name|MessageStore
name|checkpointStore
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
name|longTermStore
operator|=
name|checkpointStore
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
block|}
comment|/**      * Not synchronized since the Journal has better throughput if you increase      * the number of concurrent writes that it is doing.      */
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
name|boolean
name|debug
init|=
name|log
operator|.
name|isDebugEnabled
argument_list|()
decl_stmt|;
name|message
operator|.
name|incrementReferenceCount
argument_list|()
expr_stmt|;
specifier|final
name|RecordLocation
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
name|JournalMessageStore
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
name|JournalMessageStore
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
name|message
operator|.
name|decrementReferenceCount
argument_list|()
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|addMessage
parameter_list|(
specifier|final
name|Message
name|message
parameter_list|,
specifier|final
name|RecordLocation
name|location
parameter_list|)
block|{
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
name|message
operator|.
name|getMessageId
argument_list|()
decl_stmt|;
name|messages
operator|.
name|put
argument_list|(
name|id
argument_list|,
name|message
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|replayAddMessage
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|Message
name|message
parameter_list|)
block|{
try|try
block|{
comment|// Only add the message if it has not already been added.
name|Message
name|t
init|=
name|longTermStore
operator|.
name|getMessage
argument_list|(
name|message
operator|.
name|getMessageId
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|t
operator|==
literal|null
condition|)
block|{
name|longTermStore
operator|.
name|addMessage
argument_list|(
name|context
argument_list|,
name|message
argument_list|)
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
literal|"Could not replay add for message '"
operator|+
name|message
operator|.
name|getMessageId
argument_list|()
operator|+
literal|"'.  Message may have already been added. reason: "
operator|+
name|e
argument_list|)
expr_stmt|;
block|}
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
specifier|final
name|boolean
name|debug
init|=
name|log
operator|.
name|isDebugEnabled
argument_list|()
decl_stmt|;
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
name|RecordLocation
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
name|JournalMessageStore
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
name|JournalMessageStore
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
specifier|private
name|void
name|removeMessage
parameter_list|(
specifier|final
name|MessageAck
name|ack
parameter_list|,
specifier|final
name|RecordLocation
name|location
parameter_list|)
block|{
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
name|Message
name|message
init|=
operator|(
name|Message
operator|)
name|messages
operator|.
name|remove
argument_list|(
name|id
argument_list|)
decl_stmt|;
if|if
condition|(
name|message
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
else|else
block|{
name|message
operator|.
name|decrementReferenceCount
argument_list|()
expr_stmt|;
block|}
block|}
block|}
specifier|public
name|void
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
name|Message
name|t
init|=
name|longTermStore
operator|.
name|getMessage
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
name|longTermStore
operator|.
name|removeMessage
argument_list|(
name|context
argument_list|,
name|messageAck
argument_list|)
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
block|}
comment|/**      * @return      * @throws IOException      */
specifier|public
name|RecordLocation
name|checkpoint
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|checkpoint
argument_list|(
literal|null
argument_list|)
return|;
block|}
comment|/**      * @return      * @throws IOException      */
specifier|public
name|RecordLocation
name|checkpoint
parameter_list|(
specifier|final
name|Callback
name|postCheckpointTest
parameter_list|)
throws|throws
name|IOException
block|{
name|RecordLocation
name|rc
decl_stmt|;
specifier|final
name|ArrayList
name|cpRemovedMessageLocations
decl_stmt|;
specifier|final
name|ArrayList
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
argument_list|()
expr_stmt|;
name|this
operator|.
name|messageAcks
operator|=
operator|new
name|ArrayList
argument_list|()
expr_stmt|;
block|}
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
name|Throwable
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
name|iterator
init|=
name|cpAddedMessageIds
operator|.
name|values
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
name|Message
name|message
init|=
operator|(
name|Message
operator|)
name|iterator
operator|.
name|next
argument_list|()
decl_stmt|;
try|try
block|{
name|longTermStore
operator|.
name|addMessage
argument_list|(
name|context
argument_list|,
name|message
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
operator|+=
name|message
operator|.
name|getSize
argument_list|()
expr_stmt|;
name|iterator
operator|.
name|remove
argument_list|()
expr_stmt|;
name|message
operator|.
name|decrementReferenceCount
argument_list|()
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
name|iterator
operator|=
name|cpRemovedMessageLocations
operator|.
name|iterator
argument_list|()
expr_stmt|;
while|while
condition|(
name|iterator
operator|.
name|hasNext
argument_list|()
condition|)
block|{
try|try
block|{
name|MessageAck
name|ack
init|=
operator|(
name|MessageAck
operator|)
name|iterator
operator|.
name|next
argument_list|()
decl_stmt|;
name|longTermStore
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
name|debug
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
if|if
condition|(
name|postCheckpointTest
operator|!=
literal|null
condition|)
block|{
name|postCheckpointTest
operator|.
name|execute
argument_list|()
expr_stmt|;
block|}
block|}
block|}
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
operator|(
name|RecordLocation
operator|)
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
name|Message
name|answer
init|=
literal|null
decl_stmt|;
synchronized|synchronized
init|(
name|this
init|)
block|{
comment|// Do we have a still have it in the journal?
name|answer
operator|=
operator|(
name|Message
operator|)
name|messages
operator|.
name|get
argument_list|(
name|identity
argument_list|)
expr_stmt|;
if|if
condition|(
name|answer
operator|==
literal|null
operator|&&
name|cpAddedMessageIds
operator|!=
literal|null
condition|)
name|answer
operator|=
operator|(
name|Message
operator|)
name|cpAddedMessageIds
operator|.
name|get
argument_list|(
name|identity
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|answer
operator|!=
literal|null
condition|)
block|{
return|return
name|answer
return|;
block|}
comment|// If all else fails try the long term message store.
return|return
name|longTermStore
operator|.
name|getMessage
argument_list|(
name|identity
argument_list|)
return|;
block|}
comment|/**      * Replays the checkpointStore first as those messages are the oldest ones,      * then messages are replayed from the transaction log and then the cache is      * updated.      *       * @param listener      * @throws Throwable       */
specifier|public
name|void
name|recover
parameter_list|(
specifier|final
name|MessageRecoveryListener
name|listener
parameter_list|)
throws|throws
name|Throwable
block|{
name|peristenceAdapter
operator|.
name|checkpoint
argument_list|(
literal|true
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|longTermStore
operator|.
name|recover
argument_list|(
name|listener
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|start
parameter_list|()
throws|throws
name|IOException
block|{
name|longTermStore
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|stop
parameter_list|(
name|long
name|timeout
parameter_list|)
throws|throws
name|IOException
block|{
name|longTermStore
operator|.
name|stop
argument_list|(
name|timeout
argument_list|)
expr_stmt|;
block|}
comment|/**      * @return Returns the longTermStore.      */
specifier|public
name|MessageStore
name|getLongTermMessageStore
parameter_list|()
block|{
return|return
name|longTermStore
return|;
block|}
comment|/**      * @see org.activemq.store.MessageStore#removeAllMessages(ConnectionContext)      */
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
name|peristenceAdapter
operator|.
name|checkpoint
argument_list|(
literal|true
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|longTermStore
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
block|}
end_class

end_unit

