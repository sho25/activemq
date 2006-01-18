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
name|group
operator|.
name|MessageGroupMap
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
name|ConsumerId
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
name|ConsumerInfo
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
name|transaction
operator|.
name|Synchronization
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|InvalidSelectorException
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

begin_class
specifier|public
class|class
name|QueueSubscription
extends|extends
name|PrefetchSubscription
block|{
specifier|public
name|QueueSubscription
parameter_list|(
name|Broker
name|broker
parameter_list|,
name|ConnectionContext
name|context
parameter_list|,
name|ConsumerInfo
name|info
parameter_list|)
throws|throws
name|InvalidSelectorException
block|{
name|super
argument_list|(
name|broker
argument_list|,
name|context
argument_list|,
name|info
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|add
parameter_list|(
name|MessageReference
name|node
parameter_list|)
throws|throws
name|Throwable
block|{
name|super
operator|.
name|add
argument_list|(
name|node
argument_list|)
expr_stmt|;
block|}
comment|/**      * In the queue case, mark the node as dropped and then a gc cycle will remove it from       * the queue.      * @throws IOException       */
specifier|protected
name|void
name|acknowledge
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
specifier|final
name|MessageAck
name|ack
parameter_list|,
specifier|final
name|MessageReference
name|n
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|IndirectMessageReference
name|node
init|=
operator|(
name|IndirectMessageReference
operator|)
name|n
decl_stmt|;
specifier|final
name|Queue
name|queue
init|=
operator|(
name|Queue
operator|)
name|node
operator|.
name|getRegionDestination
argument_list|()
decl_stmt|;
name|queue
operator|.
name|acknowledge
argument_list|(
name|context
argument_list|,
name|this
argument_list|,
name|ack
argument_list|,
name|node
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|ack
operator|.
name|isInTransaction
argument_list|()
condition|)
block|{
name|node
operator|.
name|drop
argument_list|()
expr_stmt|;
name|queue
operator|.
name|dropEvent
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|node
operator|.
name|setAcked
argument_list|(
literal|true
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
name|Throwable
block|{
name|node
operator|.
name|drop
argument_list|()
expr_stmt|;
name|queue
operator|.
name|dropEvent
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|afterRollback
parameter_list|()
throws|throws
name|Throwable
block|{
name|node
operator|.
name|setAcked
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
block|}
specifier|protected
name|boolean
name|canDispatch
parameter_list|(
name|MessageReference
name|n
parameter_list|)
block|{
name|IndirectMessageReference
name|node
init|=
operator|(
name|IndirectMessageReference
operator|)
name|n
decl_stmt|;
if|if
condition|(
name|node
operator|.
name|isAcked
argument_list|()
condition|)
return|return
literal|false
return|;
comment|// Keep message groups together.
name|String
name|groupId
init|=
name|node
operator|.
name|getGroupID
argument_list|()
decl_stmt|;
name|int
name|sequence
init|=
name|node
operator|.
name|getGroupSequence
argument_list|()
decl_stmt|;
if|if
condition|(
name|groupId
operator|!=
literal|null
condition|)
block|{
name|MessageGroupMap
name|messageGroupOwners
init|=
operator|(
operator|(
name|Queue
operator|)
name|node
operator|.
name|getRegionDestination
argument_list|()
operator|)
operator|.
name|getMessageGroupOwners
argument_list|()
decl_stmt|;
comment|// If we can own the first, then no-one else should own the rest.
if|if
condition|(
name|sequence
operator|==
literal|0
condition|)
block|{
if|if
condition|(
name|node
operator|.
name|lock
argument_list|(
name|this
argument_list|)
condition|)
block|{
name|messageGroupOwners
operator|.
name|put
argument_list|(
name|groupId
argument_list|,
name|info
operator|.
name|getConsumerId
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
else|else
block|{
return|return
literal|false
return|;
block|}
block|}
comment|// Make sure that the previous owner is still valid, we may
comment|// need to become the new owner.
name|ConsumerId
name|groupOwner
decl_stmt|;
synchronized|synchronized
init|(
name|node
init|)
block|{
name|groupOwner
operator|=
name|messageGroupOwners
operator|.
name|get
argument_list|(
name|groupId
argument_list|)
expr_stmt|;
if|if
condition|(
name|groupOwner
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|node
operator|.
name|lock
argument_list|(
name|this
argument_list|)
condition|)
block|{
name|messageGroupOwners
operator|.
name|put
argument_list|(
name|groupId
argument_list|,
name|info
operator|.
name|getConsumerId
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
else|else
block|{
return|return
literal|false
return|;
block|}
block|}
block|}
if|if
condition|(
name|groupOwner
operator|.
name|equals
argument_list|(
name|info
operator|.
name|getConsumerId
argument_list|()
argument_list|)
condition|)
block|{
comment|// A group sequence< 1 is an end of group signal.
if|if
condition|(
name|sequence
operator|<
literal|0
condition|)
block|{
name|messageGroupOwners
operator|.
name|removeGroup
argument_list|(
name|groupId
argument_list|)
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
else|else
block|{
return|return
name|node
operator|.
name|lock
argument_list|(
name|this
argument_list|)
return|;
block|}
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"QueueSubscription:"
operator|+
literal|" consumer="
operator|+
name|info
operator|.
name|getConsumerId
argument_list|()
operator|+
literal|", destinations="
operator|+
name|destinations
operator|.
name|size
argument_list|()
operator|+
literal|", dispatched="
operator|+
name|dispatched
operator|.
name|size
argument_list|()
operator|+
literal|", delivered="
operator|+
name|this
operator|.
name|delivered
operator|+
literal|", matched="
operator|+
name|this
operator|.
name|matched
operator|.
name|size
argument_list|()
return|;
block|}
block|}
end_class

end_unit

