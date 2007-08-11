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
name|memory
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
name|Collections
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

begin_comment
comment|/**  * An implementation of {@link org.apache.activemq.store.MessageStore} which  * uses a  *   * @version $Revision: 1.7 $  */
end_comment

begin_class
specifier|public
class|class
name|MemoryMessageStore
implements|implements
name|MessageStore
block|{
specifier|protected
specifier|final
name|ActiveMQDestination
name|destination
decl_stmt|;
specifier|protected
specifier|final
name|Map
argument_list|<
name|MessageId
argument_list|,
name|Message
argument_list|>
name|messageTable
decl_stmt|;
specifier|protected
name|MessageId
name|lastBatchId
decl_stmt|;
specifier|public
name|MemoryMessageStore
parameter_list|(
name|ActiveMQDestination
name|destination
parameter_list|)
block|{
name|this
argument_list|(
name|destination
argument_list|,
operator|new
name|LinkedHashMap
argument_list|<
name|MessageId
argument_list|,
name|Message
argument_list|>
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|MemoryMessageStore
parameter_list|(
name|ActiveMQDestination
name|destination
parameter_list|,
name|Map
argument_list|<
name|MessageId
argument_list|,
name|Message
argument_list|>
name|messageTable
parameter_list|)
block|{
name|this
operator|.
name|destination
operator|=
name|destination
expr_stmt|;
name|this
operator|.
name|messageTable
operator|=
name|Collections
operator|.
name|synchronizedMap
argument_list|(
name|messageTable
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|synchronized
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
synchronized|synchronized
init|(
name|messageTable
init|)
block|{
name|messageTable
operator|.
name|put
argument_list|(
name|message
operator|.
name|getMessageId
argument_list|()
argument_list|,
name|message
argument_list|)
expr_stmt|;
block|}
block|}
comment|// public void addMessageReference(ConnectionContext context,MessageId
comment|// messageId,long expirationTime,String messageRef)
comment|// throws IOException{
comment|// synchronized(messageTable){
comment|// messageTable.put(messageId,messageRef);
comment|// }
comment|// }
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
return|return
name|messageTable
operator|.
name|get
argument_list|(
name|identity
argument_list|)
return|;
block|}
comment|// public String getMessageReference(MessageId identity) throws IOException{
comment|// return (String)messageTable.get(identity);
comment|// }
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
name|removeMessage
argument_list|(
name|ack
operator|.
name|getLastMessageId
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|removeMessage
parameter_list|(
name|MessageId
name|msgId
parameter_list|)
throws|throws
name|IOException
block|{
synchronized|synchronized
init|(
name|messageTable
init|)
block|{
name|messageTable
operator|.
name|remove
argument_list|(
name|msgId
argument_list|)
expr_stmt|;
if|if
condition|(
operator|(
name|lastBatchId
operator|!=
literal|null
operator|&&
name|lastBatchId
operator|.
name|equals
argument_list|(
name|msgId
argument_list|)
operator|)
operator|||
name|messageTable
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|lastBatchId
operator|=
literal|null
expr_stmt|;
block|}
block|}
block|}
specifier|public
name|void
name|recover
parameter_list|(
name|MessageRecoveryListener
name|listener
parameter_list|)
throws|throws
name|Exception
block|{
comment|// the message table is a synchronizedMap - so just have to synchronize
comment|// here
synchronized|synchronized
init|(
name|messageTable
init|)
block|{
for|for
control|(
name|Iterator
argument_list|<
name|Message
argument_list|>
name|iter
init|=
name|messageTable
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
name|Object
name|msg
init|=
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|msg
operator|.
name|getClass
argument_list|()
operator|==
name|MessageId
operator|.
name|class
condition|)
block|{
name|listener
operator|.
name|recoverMessageReference
argument_list|(
operator|(
name|MessageId
operator|)
name|msg
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|listener
operator|.
name|recoverMessage
argument_list|(
operator|(
name|Message
operator|)
name|msg
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
specifier|public
name|void
name|start
parameter_list|()
block|{     }
specifier|public
name|void
name|stop
parameter_list|()
block|{     }
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
synchronized|synchronized
init|(
name|messageTable
init|)
block|{
name|messageTable
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
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
name|delete
parameter_list|()
block|{
synchronized|synchronized
init|(
name|messageTable
init|)
block|{
name|messageTable
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**      * @param usageManager The UsageManager that is controlling the      *                destination's memory usage.      */
specifier|public
name|void
name|setUsageManager
parameter_list|(
name|UsageManager
name|usageManager
parameter_list|)
block|{     }
specifier|public
name|int
name|getMessageCount
parameter_list|()
block|{
return|return
name|messageTable
operator|.
name|size
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
synchronized|synchronized
init|(
name|messageTable
init|)
block|{
name|boolean
name|pastLackBatch
init|=
name|lastBatchId
operator|==
literal|null
decl_stmt|;
name|int
name|count
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Iterator
name|iter
init|=
name|messageTable
operator|.
name|entrySet
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
name|Map
operator|.
name|Entry
name|entry
init|=
operator|(
name|Entry
operator|)
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|pastLackBatch
condition|)
block|{
name|count
operator|++
expr_stmt|;
name|Object
name|msg
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|lastBatchId
operator|=
operator|(
name|MessageId
operator|)
name|entry
operator|.
name|getKey
argument_list|()
expr_stmt|;
if|if
condition|(
name|msg
operator|.
name|getClass
argument_list|()
operator|==
name|MessageId
operator|.
name|class
condition|)
block|{
name|listener
operator|.
name|recoverMessageReference
argument_list|(
operator|(
name|MessageId
operator|)
name|msg
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|listener
operator|.
name|recoverMessage
argument_list|(
operator|(
name|Message
operator|)
name|msg
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|pastLackBatch
operator|=
name|entry
operator|.
name|getKey
argument_list|()
operator|.
name|equals
argument_list|(
name|lastBatchId
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
specifier|public
name|void
name|resetBatching
parameter_list|()
block|{
name|lastBatchId
operator|=
literal|null
expr_stmt|;
block|}
block|}
end_class

end_unit

