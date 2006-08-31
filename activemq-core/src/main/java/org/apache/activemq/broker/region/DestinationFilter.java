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
name|broker
operator|.
name|region
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
name|Broker
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
name|broker
operator|.
name|region
operator|.
name|policy
operator|.
name|DeadLetterStrategy
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
name|memory
operator|.
name|UsageManager
import|;
end_import

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
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_comment
comment|/**  *   * @version $Revision$  */
end_comment

begin_class
specifier|public
class|class
name|DestinationFilter
implements|implements
name|Destination
block|{
specifier|private
name|Destination
name|next
decl_stmt|;
specifier|public
name|DestinationFilter
parameter_list|(
name|Destination
name|next
parameter_list|)
block|{
name|this
operator|.
name|next
operator|=
name|next
expr_stmt|;
block|}
specifier|public
name|void
name|acknowledge
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|Subscription
name|sub
parameter_list|,
name|MessageAck
name|ack
parameter_list|,
name|MessageReference
name|node
parameter_list|)
throws|throws
name|IOException
block|{
name|next
operator|.
name|acknowledge
argument_list|(
name|context
argument_list|,
name|sub
argument_list|,
name|ack
argument_list|,
name|node
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|addSubscription
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|Subscription
name|sub
parameter_list|)
throws|throws
name|Exception
block|{
name|next
operator|.
name|addSubscription
argument_list|(
name|context
argument_list|,
name|sub
argument_list|)
expr_stmt|;
block|}
specifier|public
name|Message
index|[]
name|browse
parameter_list|()
block|{
return|return
name|next
operator|.
name|browse
argument_list|()
return|;
block|}
specifier|public
name|void
name|dispose
parameter_list|(
name|ConnectionContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|next
operator|.
name|dispose
argument_list|(
name|context
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|gc
parameter_list|()
block|{
name|next
operator|.
name|gc
argument_list|()
expr_stmt|;
block|}
specifier|public
name|ActiveMQDestination
name|getActiveMQDestination
parameter_list|()
block|{
return|return
name|next
operator|.
name|getActiveMQDestination
argument_list|()
return|;
block|}
specifier|public
name|long
name|getConsumerCount
parameter_list|()
block|{
return|return
name|next
operator|.
name|getConsumerCount
argument_list|()
return|;
block|}
specifier|public
name|DeadLetterStrategy
name|getDeadLetterStrategy
parameter_list|()
block|{
return|return
name|next
operator|.
name|getDeadLetterStrategy
argument_list|()
return|;
block|}
specifier|public
name|long
name|getDequeueCount
parameter_list|()
block|{
return|return
name|next
operator|.
name|getDequeueCount
argument_list|()
return|;
block|}
specifier|public
name|DestinationStatistics
name|getDestinationStatistics
parameter_list|()
block|{
return|return
name|next
operator|.
name|getDestinationStatistics
argument_list|()
return|;
block|}
specifier|public
name|long
name|getEnqueueCount
parameter_list|()
block|{
return|return
name|next
operator|.
name|getEnqueueCount
argument_list|()
return|;
block|}
specifier|public
name|long
name|getMemoryLimit
parameter_list|()
block|{
return|return
name|next
operator|.
name|getMemoryLimit
argument_list|()
return|;
block|}
specifier|public
name|int
name|getMemoryPercentageUsed
parameter_list|()
block|{
return|return
name|next
operator|.
name|getMemoryPercentageUsed
argument_list|()
return|;
block|}
specifier|public
name|long
name|getMessagesCached
parameter_list|()
block|{
return|return
name|next
operator|.
name|getMessagesCached
argument_list|()
return|;
block|}
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|next
operator|.
name|getName
argument_list|()
return|;
block|}
specifier|public
name|long
name|getQueueSize
parameter_list|()
block|{
return|return
name|next
operator|.
name|getQueueSize
argument_list|()
return|;
block|}
specifier|public
name|UsageManager
name|getUsageManager
parameter_list|()
block|{
return|return
name|next
operator|.
name|getUsageManager
argument_list|()
return|;
block|}
specifier|public
name|boolean
name|lock
parameter_list|(
name|MessageReference
name|node
parameter_list|,
name|LockOwner
name|lockOwner
parameter_list|)
block|{
return|return
name|next
operator|.
name|lock
argument_list|(
name|node
argument_list|,
name|lockOwner
argument_list|)
return|;
block|}
specifier|public
name|void
name|removeSubscription
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|Subscription
name|sub
parameter_list|)
throws|throws
name|Exception
block|{
name|next
operator|.
name|removeSubscription
argument_list|(
name|context
argument_list|,
name|sub
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|resetStatistics
parameter_list|()
block|{
name|next
operator|.
name|resetStatistics
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|send
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|Message
name|messageSend
parameter_list|)
throws|throws
name|Exception
block|{
name|next
operator|.
name|send
argument_list|(
name|context
argument_list|,
name|messageSend
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setMemoryLimit
parameter_list|(
name|long
name|limit
parameter_list|)
block|{
name|next
operator|.
name|setMemoryLimit
argument_list|(
name|limit
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
name|next
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
name|next
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
comment|/**      * Sends a message to the given destination which may be a wildcard      */
specifier|protected
name|void
name|send
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|Message
name|message
parameter_list|,
name|ActiveMQDestination
name|destination
parameter_list|)
throws|throws
name|Exception
block|{
name|Broker
name|broker
init|=
name|context
operator|.
name|getBroker
argument_list|()
decl_stmt|;
name|Set
name|destinations
init|=
name|broker
operator|.
name|getDestinations
argument_list|(
name|destination
argument_list|)
decl_stmt|;
for|for
control|(
name|Iterator
name|iter
init|=
name|destinations
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
name|Destination
name|dest
init|=
operator|(
name|Destination
operator|)
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
name|dest
operator|.
name|send
argument_list|(
name|context
argument_list|,
name|message
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

