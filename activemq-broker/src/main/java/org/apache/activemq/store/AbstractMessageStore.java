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
name|usage
operator|.
name|MemoryUsage
import|;
end_import

begin_class
specifier|abstract
specifier|public
class|class
name|AbstractMessageStore
implements|implements
name|MessageStore
block|{
specifier|public
specifier|static
specifier|final
name|ListenableFuture
argument_list|<
name|Object
argument_list|>
name|FUTURE
decl_stmt|;
specifier|protected
specifier|final
name|ActiveMQDestination
name|destination
decl_stmt|;
specifier|protected
name|boolean
name|prioritizedMessages
decl_stmt|;
specifier|public
name|AbstractMessageStore
parameter_list|(
name|ActiveMQDestination
name|destination
parameter_list|)
block|{
name|this
operator|.
name|destination
operator|=
name|destination
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|dispose
parameter_list|(
name|ConnectionContext
name|context
parameter_list|)
block|{     }
annotation|@
name|Override
specifier|public
name|void
name|start
parameter_list|()
throws|throws
name|Exception
block|{     }
annotation|@
name|Override
specifier|public
name|void
name|stop
parameter_list|()
throws|throws
name|Exception
block|{     }
annotation|@
name|Override
specifier|public
name|ActiveMQDestination
name|getDestination
parameter_list|()
block|{
return|return
name|destination
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setMemoryUsage
parameter_list|(
name|MemoryUsage
name|memoryUsage
parameter_list|)
block|{     }
annotation|@
name|Override
specifier|public
name|void
name|setBatch
parameter_list|(
name|MessageId
name|messageId
parameter_list|)
throws|throws
name|IOException
throws|,
name|Exception
block|{     }
comment|/**      * flag to indicate if the store is empty      *      * @return true if the message count is 0      * @throws Exception      */
annotation|@
name|Override
specifier|public
name|boolean
name|isEmpty
parameter_list|()
throws|throws
name|Exception
block|{
return|return
name|getMessageCount
argument_list|()
operator|==
literal|0
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setPrioritizedMessages
parameter_list|(
name|boolean
name|prioritizedMessages
parameter_list|)
block|{
name|this
operator|.
name|prioritizedMessages
operator|=
name|prioritizedMessages
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isPrioritizedMessages
parameter_list|()
block|{
return|return
name|this
operator|.
name|prioritizedMessages
return|;
block|}
annotation|@
name|Override
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
parameter_list|,
specifier|final
name|boolean
name|canOptimizeHint
parameter_list|)
throws|throws
name|IOException
block|{
name|addMessage
argument_list|(
name|context
argument_list|,
name|message
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|ListenableFuture
argument_list|<
name|Object
argument_list|>
name|asyncAddQueueMessage
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
name|addMessage
argument_list|(
name|context
argument_list|,
name|message
argument_list|)
expr_stmt|;
return|return
name|FUTURE
return|;
block|}
annotation|@
name|Override
specifier|public
name|ListenableFuture
argument_list|<
name|Object
argument_list|>
name|asyncAddQueueMessage
parameter_list|(
specifier|final
name|ConnectionContext
name|context
parameter_list|,
specifier|final
name|Message
name|message
parameter_list|,
specifier|final
name|boolean
name|canOptimizeHint
parameter_list|)
throws|throws
name|IOException
block|{
name|addMessage
argument_list|(
name|context
argument_list|,
name|message
argument_list|,
name|canOptimizeHint
argument_list|)
expr_stmt|;
return|return
name|FUTURE
return|;
block|}
annotation|@
name|Override
specifier|public
name|ListenableFuture
argument_list|<
name|Object
argument_list|>
name|asyncAddTopicMessage
parameter_list|(
specifier|final
name|ConnectionContext
name|context
parameter_list|,
specifier|final
name|Message
name|message
parameter_list|,
specifier|final
name|boolean
name|canOptimizeHint
parameter_list|)
throws|throws
name|IOException
block|{
name|addMessage
argument_list|(
name|context
argument_list|,
name|message
argument_list|,
name|canOptimizeHint
argument_list|)
expr_stmt|;
return|return
name|FUTURE
return|;
block|}
annotation|@
name|Override
specifier|public
name|ListenableFuture
argument_list|<
name|Object
argument_list|>
name|asyncAddTopicMessage
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
name|addMessage
argument_list|(
name|context
argument_list|,
name|message
argument_list|)
expr_stmt|;
return|return
operator|new
name|InlineListenableFuture
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|removeAsyncMessage
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
name|context
argument_list|,
name|ack
argument_list|)
expr_stmt|;
block|}
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
throw|throw
operator|new
name|IOException
argument_list|(
literal|"update is not supported by: "
operator|+
name|this
argument_list|)
throw|;
block|}
static|static
block|{
name|FUTURE
operator|=
operator|new
name|InlineListenableFuture
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

