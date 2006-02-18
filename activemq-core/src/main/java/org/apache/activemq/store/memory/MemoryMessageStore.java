begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Copyright 2005-2006 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
comment|/**  * An implementation of {@link org.apache.activemq.store.MessageStore} which uses a  *  * @version $Revision: 1.7 $  */
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
name|messageTable
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
name|messageTable
operator|.
name|put
argument_list|(
name|messageId
argument_list|,
name|messageRef
argument_list|)
expr_stmt|;
block|}
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
operator|(
name|Message
operator|)
name|messageTable
operator|.
name|get
argument_list|(
name|identity
argument_list|)
return|;
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
return|return
operator|(
name|String
operator|)
name|messageTable
operator|.
name|get
argument_list|(
name|identity
argument_list|)
return|;
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
name|messageTable
operator|.
name|remove
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
name|messageTable
operator|.
name|remove
argument_list|(
name|msgId
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|recover
parameter_list|(
name|MessageRecoveryListener
name|listener
parameter_list|)
throws|throws
name|Throwable
block|{
comment|// the message table is a synchronizedMap - so just have to synchronize here
synchronized|synchronized
init|(
name|messageTable
init|)
block|{
for|for
control|(
name|Iterator
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
operator|(
name|Object
operator|)
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
name|String
operator|.
name|class
condition|)
block|{
name|listener
operator|.
name|recoverMessageReference
argument_list|(
operator|(
name|String
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
throws|throws
name|IOException
block|{     }
specifier|public
name|void
name|stop
parameter_list|(
name|long
name|timeout
parameter_list|)
throws|throws
name|IOException
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
name|messageTable
operator|.
name|clear
argument_list|()
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
name|delete
parameter_list|()
block|{
name|messageTable
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

