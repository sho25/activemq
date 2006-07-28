begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|memory
operator|.
name|list
package|;
end_package

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
name|HashMap
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
name|Set
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
name|ActiveMQMessage
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
name|filter
operator|.
name|DestinationMap
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
name|buffer
operator|.
name|MessageBuffer
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
name|buffer
operator|.
name|MessageQueue
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
name|buffer
operator|.
name|OrderBasedMessageBuffer
import|;
end_import

begin_comment
comment|/**  * An implementation of {@link MessageList} which maintains a separate message  * list for each destination to reduce contention on the list and to speed up  * recovery times by only recovering the interested topics.  *   * @version $Revision: 1.1 $  */
end_comment

begin_class
specifier|public
class|class
name|DestinationBasedMessageList
implements|implements
name|MessageList
block|{
specifier|private
name|MessageBuffer
name|messageBuffer
decl_stmt|;
specifier|private
name|Map
name|queueIndex
init|=
operator|new
name|HashMap
argument_list|()
decl_stmt|;
specifier|private
name|DestinationMap
name|subscriptionIndex
init|=
operator|new
name|DestinationMap
argument_list|()
decl_stmt|;
specifier|private
name|Object
name|lock
init|=
operator|new
name|Object
argument_list|()
decl_stmt|;
specifier|public
name|DestinationBasedMessageList
parameter_list|(
name|int
name|maximumSize
parameter_list|)
block|{
name|this
argument_list|(
operator|new
name|OrderBasedMessageBuffer
argument_list|(
name|maximumSize
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|DestinationBasedMessageList
parameter_list|(
name|MessageBuffer
name|buffer
parameter_list|)
block|{
name|messageBuffer
operator|=
name|buffer
expr_stmt|;
block|}
specifier|public
name|void
name|add
parameter_list|(
name|MessageReference
name|node
parameter_list|)
block|{
name|ActiveMQMessage
name|message
init|=
operator|(
name|ActiveMQMessage
operator|)
name|node
operator|.
name|getMessageHardRef
argument_list|()
decl_stmt|;
name|ActiveMQDestination
name|destination
init|=
name|message
operator|.
name|getDestination
argument_list|()
decl_stmt|;
name|MessageQueue
name|queue
init|=
literal|null
decl_stmt|;
synchronized|synchronized
init|(
name|lock
init|)
block|{
name|queue
operator|=
operator|(
name|MessageQueue
operator|)
name|queueIndex
operator|.
name|get
argument_list|(
name|destination
argument_list|)
expr_stmt|;
if|if
condition|(
name|queue
operator|==
literal|null
condition|)
block|{
name|queue
operator|=
name|messageBuffer
operator|.
name|createMessageQueue
argument_list|()
expr_stmt|;
name|queueIndex
operator|.
name|put
argument_list|(
name|destination
argument_list|,
name|queue
argument_list|)
expr_stmt|;
name|subscriptionIndex
operator|.
name|put
argument_list|(
name|destination
argument_list|,
name|queue
argument_list|)
expr_stmt|;
block|}
block|}
name|queue
operator|.
name|add
argument_list|(
name|node
argument_list|)
expr_stmt|;
block|}
specifier|public
name|List
name|getMessages
parameter_list|(
name|Subscription
name|sub
parameter_list|)
block|{
return|return
name|getMessages
argument_list|(
name|sub
operator|.
name|getConsumerInfo
argument_list|()
operator|.
name|getDestination
argument_list|()
argument_list|)
return|;
block|}
specifier|protected
name|List
name|getMessages
parameter_list|(
name|ActiveMQDestination
name|destination
parameter_list|)
block|{
name|Set
name|set
init|=
literal|null
decl_stmt|;
synchronized|synchronized
init|(
name|lock
init|)
block|{
name|set
operator|=
name|subscriptionIndex
operator|.
name|get
argument_list|(
name|destination
argument_list|)
expr_stmt|;
block|}
name|List
name|answer
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
for|for
control|(
name|Iterator
name|iter
init|=
name|set
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
name|MessageQueue
name|queue
init|=
operator|(
name|MessageQueue
operator|)
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
name|queue
operator|.
name|appendMessages
argument_list|(
name|answer
argument_list|)
expr_stmt|;
block|}
return|return
name|answer
return|;
block|}
specifier|public
name|Message
index|[]
name|browse
parameter_list|(
name|ActiveMQDestination
name|destination
parameter_list|)
block|{
name|List
name|result
init|=
name|getMessages
argument_list|(
name|destination
argument_list|)
decl_stmt|;
return|return
operator|(
name|Message
index|[]
operator|)
name|result
operator|.
name|toArray
argument_list|(
operator|new
name|Message
index|[
name|result
operator|.
name|size
argument_list|()
index|]
argument_list|)
return|;
block|}
specifier|public
name|void
name|clear
parameter_list|()
block|{
name|messageBuffer
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

