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
name|broker
operator|.
name|region
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
name|usage
operator|.
name|SystemUsage
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_class
specifier|public
class|class
name|QueueSubscription
extends|extends
name|PrefetchSubscription
implements|implements
name|LockOwner
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|QueueSubscription
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
name|QueueSubscription
parameter_list|(
name|Broker
name|broker
parameter_list|,
name|SystemUsage
name|usageManager
parameter_list|,
name|ConnectionContext
name|context
parameter_list|,
name|ConsumerInfo
name|info
parameter_list|)
throws|throws
name|JMSException
block|{
name|super
argument_list|(
name|broker
argument_list|,
name|usageManager
argument_list|,
name|context
argument_list|,
name|info
argument_list|)
expr_stmt|;
block|}
comment|/**      * In the queue case, mark the node as dropped and then a gc cycle will      * remove it from the queue.      *      * @throws IOException      */
annotation|@
name|Override
specifier|protected
name|void
name|acknowledge
parameter_list|(
specifier|final
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
name|this
operator|.
name|setTimeOfLastMessageAck
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|Destination
name|q
init|=
operator|(
name|Destination
operator|)
name|n
operator|.
name|getRegionDestination
argument_list|()
decl_stmt|;
specifier|final
name|QueueMessageReference
name|node
init|=
operator|(
name|QueueMessageReference
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
name|q
decl_stmt|;
name|queue
operator|.
name|removeMessage
argument_list|(
name|context
argument_list|,
name|this
argument_list|,
name|node
argument_list|,
name|ack
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|boolean
name|canDispatch
parameter_list|(
name|MessageReference
name|n
parameter_list|)
throws|throws
name|IOException
block|{
name|boolean
name|result
init|=
literal|true
decl_stmt|;
name|QueueMessageReference
name|node
init|=
operator|(
name|QueueMessageReference
operator|)
name|n
decl_stmt|;
if|if
condition|(
name|node
operator|.
name|isAcked
argument_list|()
operator|||
name|node
operator|.
name|isDropped
argument_list|()
condition|)
block|{
name|result
operator|=
literal|false
expr_stmt|;
block|}
name|result
operator|=
name|result
operator|&&
operator|(
name|isBrowser
argument_list|()
operator|||
name|node
operator|.
name|lock
argument_list|(
name|this
argument_list|)
operator|)
expr_stmt|;
return|return
name|result
return|;
block|}
annotation|@
name|Override
specifier|public
specifier|synchronized
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
name|prefetchExtension
operator|+
literal|", pending="
operator|+
name|getPendingQueueSize
argument_list|()
operator|+
literal|", prefetch="
operator|+
name|getPrefetchSize
argument_list|()
operator|+
literal|", prefetchExtension="
operator|+
name|prefetchExtension
operator|.
name|get
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|getLockPriority
parameter_list|()
block|{
return|return
name|info
operator|.
name|getPriority
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isLockExclusive
parameter_list|()
block|{
return|return
name|info
operator|.
name|isExclusive
argument_list|()
return|;
block|}
comment|/**      */
annotation|@
name|Override
specifier|public
name|void
name|destroy
parameter_list|()
block|{
name|setSlowConsumer
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|boolean
name|isDropped
parameter_list|(
name|MessageReference
name|node
parameter_list|)
block|{
name|boolean
name|result
init|=
literal|false
decl_stmt|;
if|if
condition|(
name|node
operator|instanceof
name|IndirectMessageReference
condition|)
block|{
name|QueueMessageReference
name|qmr
init|=
operator|(
name|QueueMessageReference
operator|)
name|node
decl_stmt|;
name|result
operator|=
name|qmr
operator|.
name|isDropped
argument_list|()
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
block|}
end_class

end_unit

