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
name|broker
operator|.
name|region
operator|.
name|policy
package|;
end_package

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
name|broker
operator|.
name|region
operator|.
name|MessageReference
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
name|region
operator|.
name|Subscription
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
name|region
operator|.
name|Topic
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
name|filter
operator|.
name|MessageEvaluationContext
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
name|list
operator|.
name|DestinationBasedMessageList
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
name|list
operator|.
name|MessageList
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
name|list
operator|.
name|SimpleMessageList
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

begin_comment
comment|/**  * This implementation of {@link SubscriptionRecoveryPolicy} will keep a fixed  * amount of memory available in RAM for message history which is evicted in  * time order.  *   * @org.xbean.XBean  *   * @version $Revision$  */
end_comment

begin_class
specifier|public
class|class
name|FixedSizedSubscriptionRecoveryPolicy
implements|implements
name|SubscriptionRecoveryPolicy
block|{
specifier|private
name|MessageList
name|buffer
decl_stmt|;
specifier|private
name|int
name|maximumSize
init|=
literal|100
operator|*
literal|64
operator|*
literal|1024
decl_stmt|;
specifier|private
name|boolean
name|useSharedBuffer
init|=
literal|true
decl_stmt|;
specifier|public
name|boolean
name|add
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|MessageReference
name|message
parameter_list|)
throws|throws
name|Throwable
block|{
name|buffer
operator|.
name|add
argument_list|(
name|message
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
specifier|public
name|void
name|recover
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|Topic
name|topic
parameter_list|,
name|Subscription
name|sub
parameter_list|)
throws|throws
name|Throwable
block|{
comment|// Re-dispatch the messages from the buffer.
name|List
name|copy
init|=
name|buffer
operator|.
name|getMessages
argument_list|(
name|sub
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|copy
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|MessageEvaluationContext
name|msgContext
init|=
name|context
operator|.
name|getMessageEvaluationContext
argument_list|()
decl_stmt|;
try|try
block|{
for|for
control|(
name|Iterator
name|iter
init|=
name|copy
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
name|MessageReference
name|node
init|=
operator|(
name|MessageReference
operator|)
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
name|msgContext
operator|.
name|setDestination
argument_list|(
name|node
operator|.
name|getRegionDestination
argument_list|()
operator|.
name|getActiveMQDestination
argument_list|()
argument_list|)
expr_stmt|;
name|msgContext
operator|.
name|setMessageReference
argument_list|(
name|node
argument_list|)
expr_stmt|;
if|if
condition|(
name|sub
operator|.
name|matches
argument_list|(
name|node
argument_list|,
name|msgContext
argument_list|)
condition|)
block|{
name|sub
operator|.
name|add
argument_list|(
name|node
argument_list|)
expr_stmt|;
block|}
block|}
block|}
finally|finally
block|{
name|msgContext
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
block|}
block|}
specifier|public
name|void
name|start
parameter_list|()
throws|throws
name|Exception
block|{
name|buffer
operator|=
name|createMessageList
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
name|buffer
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
comment|// Properties
comment|// -------------------------------------------------------------------------
specifier|public
name|MessageList
name|getBuffer
parameter_list|()
block|{
return|return
name|buffer
return|;
block|}
specifier|public
name|void
name|setBuffer
parameter_list|(
name|MessageList
name|buffer
parameter_list|)
block|{
name|this
operator|.
name|buffer
operator|=
name|buffer
expr_stmt|;
block|}
specifier|public
name|int
name|getMaximumSize
parameter_list|()
block|{
return|return
name|maximumSize
return|;
block|}
comment|/**      * Sets the maximum amount of RAM in bytes that this buffer can hold in RAM      */
specifier|public
name|void
name|setMaximumSize
parameter_list|(
name|int
name|maximumSize
parameter_list|)
block|{
name|this
operator|.
name|maximumSize
operator|=
name|maximumSize
expr_stmt|;
block|}
specifier|public
name|boolean
name|isUseSharedBuffer
parameter_list|()
block|{
return|return
name|useSharedBuffer
return|;
block|}
specifier|public
name|void
name|setUseSharedBuffer
parameter_list|(
name|boolean
name|useSharedBuffer
parameter_list|)
block|{
name|this
operator|.
name|useSharedBuffer
operator|=
name|useSharedBuffer
expr_stmt|;
block|}
comment|// Implementation methods
comment|// -------------------------------------------------------------------------
specifier|protected
name|MessageList
name|createMessageList
parameter_list|()
block|{
if|if
condition|(
name|useSharedBuffer
condition|)
block|{
return|return
operator|new
name|SimpleMessageList
argument_list|(
name|maximumSize
argument_list|)
return|;
block|}
else|else
block|{
return|return
operator|new
name|DestinationBasedMessageList
argument_list|(
name|maximumSize
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

