begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *   * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements. See the NOTICE file distributed with this  * work for additional information regarding copyright ownership. The ASF  * licenses this file to You under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *   * http://www.apache.org/licenses/LICENSE-2.0  *   * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
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
name|cursors
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
name|LinkedList
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|JMSException
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
name|Destination
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
name|TopicMessageStore
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
comment|/**  * perist pending messages pending message (messages awaiting disptach to a  * consumer) cursor  *   * @version $Revision$  */
end_comment

begin_class
class|class
name|TopicStorePrefetch
extends|extends
name|AbstractPendingMessageCursor
implements|implements
name|MessageRecoveryListener
block|{
specifier|static
specifier|private
specifier|final
name|Log
name|log
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|TopicStorePrefetch
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|TopicMessageStore
name|store
decl_stmt|;
specifier|private
specifier|final
name|LinkedList
name|batchList
init|=
operator|new
name|LinkedList
argument_list|()
decl_stmt|;
specifier|private
name|String
name|clientId
decl_stmt|;
specifier|private
name|String
name|subscriberName
decl_stmt|;
specifier|private
name|Destination
name|regionDestination
decl_stmt|;
comment|/**      * @param topic      * @param clientId      * @param subscriberName      * @throws IOException      */
specifier|public
name|TopicStorePrefetch
parameter_list|(
name|Topic
name|topic
parameter_list|,
name|String
name|clientId
parameter_list|,
name|String
name|subscriberName
parameter_list|)
block|{
name|this
operator|.
name|regionDestination
operator|=
name|topic
expr_stmt|;
name|this
operator|.
name|store
operator|=
operator|(
name|TopicMessageStore
operator|)
name|topic
operator|.
name|getMessageStore
argument_list|()
expr_stmt|;
name|this
operator|.
name|clientId
operator|=
name|clientId
expr_stmt|;
name|this
operator|.
name|subscriberName
operator|=
name|subscriberName
expr_stmt|;
block|}
specifier|public
name|void
name|start
parameter_list|()
throws|throws
name|Exception
block|{     }
specifier|public
name|void
name|stop
parameter_list|()
throws|throws
name|Exception
block|{
name|store
operator|.
name|resetBatching
argument_list|(
name|clientId
argument_list|,
name|subscriberName
argument_list|)
expr_stmt|;
block|}
comment|/**      * @return true if there are no pending messages      */
specifier|public
name|boolean
name|isEmpty
parameter_list|()
block|{
return|return
name|batchList
operator|.
name|isEmpty
argument_list|()
return|;
block|}
specifier|public
specifier|synchronized
name|int
name|size
parameter_list|()
block|{
try|try
block|{
return|return
name|store
operator|.
name|getMessageCount
argument_list|(
name|clientId
argument_list|,
name|subscriberName
argument_list|)
return|;
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
name|this
operator|+
literal|" Failed to get the outstanding message count from the store"
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
specifier|public
specifier|synchronized
name|void
name|addMessageLast
parameter_list|(
name|MessageReference
name|node
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|node
operator|!=
literal|null
condition|)
block|{
name|node
operator|.
name|decrementReferenceCount
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
specifier|synchronized
name|boolean
name|hasNext
parameter_list|()
block|{
if|if
condition|(
name|isEmpty
argument_list|()
condition|)
block|{
try|try
block|{
name|fillBatch
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Failed to fill batch"
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
return|return
operator|!
name|isEmpty
argument_list|()
return|;
block|}
specifier|public
specifier|synchronized
name|MessageReference
name|next
parameter_list|()
block|{
name|Message
name|result
init|=
operator|(
name|Message
operator|)
name|batchList
operator|.
name|removeFirst
argument_list|()
decl_stmt|;
name|result
operator|.
name|setRegionDestination
argument_list|(
name|regionDestination
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
specifier|public
name|void
name|reset
parameter_list|()
block|{     }
comment|// MessageRecoveryListener implementation
specifier|public
name|void
name|finished
parameter_list|()
block|{     }
specifier|public
name|void
name|recoverMessage
parameter_list|(
name|Message
name|message
parameter_list|)
throws|throws
name|Exception
block|{
name|message
operator|.
name|setRegionDestination
argument_list|(
name|regionDestination
argument_list|)
expr_stmt|;
name|message
operator|.
name|incrementReferenceCount
argument_list|()
expr_stmt|;
name|batchList
operator|.
name|addLast
argument_list|(
name|message
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|recoverMessageReference
parameter_list|(
name|String
name|messageReference
parameter_list|)
throws|throws
name|Exception
block|{
comment|// shouldn't get called
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Not supported"
argument_list|)
throw|;
block|}
comment|// implementation
specifier|protected
name|void
name|fillBatch
parameter_list|()
throws|throws
name|Exception
block|{
name|store
operator|.
name|recoverNextMessages
argument_list|(
name|clientId
argument_list|,
name|subscriberName
argument_list|,
name|maxBatchSize
argument_list|,
name|this
argument_list|)
expr_stmt|;
comment|// this will add more messages to the batch list
if|if
condition|(
operator|!
name|batchList
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|Message
name|message
init|=
operator|(
name|Message
operator|)
name|batchList
operator|.
name|getLast
argument_list|()
decl_stmt|;
block|}
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"TopicStorePrefetch"
operator|+
name|System
operator|.
name|identityHashCode
argument_list|(
name|this
argument_list|)
operator|+
literal|"("
operator|+
name|clientId
operator|+
literal|","
operator|+
name|subscriberName
operator|+
literal|")"
return|;
block|}
block|}
end_class

end_unit

