begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *   * Copyright 2005-2006 The Apache Software Foundation  *   * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with  * the License. You may obtain a copy of the License at  *   * http://www.apache.org/licenses/LICENSE-2.0  *   * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on  * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the  * specific language governing permissions and limitations under the License.  */
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
name|kahadaptor
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
name|kaha
operator|.
name|MapContainer
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
comment|/**  * An implementation of {@link org.apache.activemq.store.MessageStore} which uses a JPS Container  *   * @version $Revision: 1.7 $  */
end_comment

begin_class
specifier|public
class|class
name|KahaMessageStore
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
name|MapContainer
name|messageContainer
decl_stmt|;
specifier|public
name|KahaMessageStore
parameter_list|(
name|MapContainer
name|container
parameter_list|,
name|ActiveMQDestination
name|destination
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|messageContainer
operator|=
name|container
expr_stmt|;
name|this
operator|.
name|destination
operator|=
name|destination
expr_stmt|;
block|}
specifier|public
name|Object
name|getId
parameter_list|()
block|{
return|return
name|messageContainer
operator|.
name|getId
argument_list|()
return|;
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
name|messageContainer
operator|.
name|put
argument_list|(
name|message
operator|.
name|getMessageId
argument_list|()
operator|.
name|toString
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
name|messageContainer
operator|.
name|put
argument_list|(
name|messageId
operator|.
name|toString
argument_list|()
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
name|messageContainer
operator|.
name|get
argument_list|(
name|identity
operator|.
name|toString
argument_list|()
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
name|messageContainer
operator|.
name|get
argument_list|(
name|identity
operator|.
name|toString
argument_list|()
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
name|messageContainer
operator|.
name|remove
argument_list|(
name|ack
operator|.
name|getLastMessageId
argument_list|()
operator|.
name|toString
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
name|messageContainer
operator|.
name|remove
argument_list|(
name|msgId
operator|.
name|toString
argument_list|()
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
name|Exception
block|{
for|for
control|(
name|Iterator
name|iter
init|=
name|messageContainer
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
name|listener
operator|.
name|finished
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|start
parameter_list|()
block|{}
specifier|public
name|void
name|stop
parameter_list|()
block|{}
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
name|messageContainer
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
name|messageContainer
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
comment|/**      * @param usageManager The UsageManager that is controlling the destination's memory usage.      */
specifier|public
name|void
name|setUsageManager
parameter_list|(
name|UsageManager
name|usageManager
parameter_list|)
block|{     }
block|}
end_class

end_unit

