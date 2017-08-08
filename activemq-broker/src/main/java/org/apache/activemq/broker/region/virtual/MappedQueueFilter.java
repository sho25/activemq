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
operator|.
name|virtual
package|;
end_package

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
name|BaseDestination
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
name|DestinationFilter
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
name|IndirectMessageReference
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
name|RegionBroker
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
name|util
operator|.
name|SubscriptionKey
import|;
end_import

begin_comment
comment|/**  * Creates a mapped Queue that can recover messages from subscription recovery  * policy of its Virtual Topic.  */
end_comment

begin_class
specifier|public
class|class
name|MappedQueueFilter
extends|extends
name|DestinationFilter
block|{
specifier|private
specifier|final
name|ActiveMQDestination
name|virtualDestination
decl_stmt|;
specifier|public
name|MappedQueueFilter
parameter_list|(
name|ActiveMQDestination
name|virtualDestination
parameter_list|,
name|Destination
name|destination
parameter_list|)
block|{
name|super
argument_list|(
name|destination
argument_list|)
expr_stmt|;
name|this
operator|.
name|virtualDestination
operator|=
name|virtualDestination
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
specifier|synchronized
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
comment|// recover messages for first consumer only
name|boolean
name|noSubs
init|=
name|getConsumers
argument_list|()
operator|.
name|isEmpty
argument_list|()
decl_stmt|;
comment|// for virtual consumer wildcard dests, only subscribe to exact match or non wildcard dests to ensure no duplicates
name|int
name|match
init|=
name|sub
operator|.
name|getActiveMQDestination
argument_list|()
operator|.
name|compareTo
argument_list|(
name|next
operator|.
name|getActiveMQDestination
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|match
operator|==
literal|0
operator|||
operator|(
operator|!
name|next
operator|.
name|getActiveMQDestination
argument_list|()
operator|.
name|isPattern
argument_list|()
operator|&&
name|match
operator|==
literal|1
operator|)
condition|)
block|{
name|super
operator|.
name|addSubscription
argument_list|(
name|context
argument_list|,
name|sub
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|noSubs
operator|&&
operator|!
name|getConsumers
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
comment|// new subscription added, recover retroactive messages
specifier|final
name|RegionBroker
name|regionBroker
init|=
operator|(
name|RegionBroker
operator|)
name|context
operator|.
name|getBroker
argument_list|()
operator|.
name|getAdaptor
argument_list|(
name|RegionBroker
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|final
name|Set
argument_list|<
name|Destination
argument_list|>
name|virtualDests
init|=
name|regionBroker
operator|.
name|getDestinations
argument_list|(
name|virtualDestination
argument_list|)
decl_stmt|;
specifier|final
name|ActiveMQDestination
name|newDestination
init|=
name|sub
operator|.
name|getActiveMQDestination
argument_list|()
decl_stmt|;
specifier|final
name|BaseDestination
name|regionDest
init|=
name|getBaseDestination
argument_list|(
operator|(
name|Destination
operator|)
name|regionBroker
operator|.
name|getDestinations
argument_list|(
name|newDestination
argument_list|)
operator|.
name|toArray
argument_list|()
index|[
literal|0
index|]
argument_list|)
decl_stmt|;
for|for
control|(
name|Destination
name|virtualDest
range|:
name|virtualDests
control|)
block|{
if|if
condition|(
name|virtualDest
operator|.
name|getActiveMQDestination
argument_list|()
operator|.
name|isTopic
argument_list|()
operator|&&
operator|(
name|virtualDest
operator|.
name|isAlwaysRetroactive
argument_list|()
operator|||
name|sub
operator|.
name|getConsumerInfo
argument_list|()
operator|.
name|isRetroactive
argument_list|()
operator|)
condition|)
block|{
name|Topic
name|topic
init|=
operator|(
name|Topic
operator|)
name|getBaseDestination
argument_list|(
name|virtualDest
argument_list|)
decl_stmt|;
if|if
condition|(
name|topic
operator|!=
literal|null
condition|)
block|{
comment|// re-use browse() to get recovered messages
specifier|final
name|Message
index|[]
name|messages
init|=
name|topic
operator|.
name|getSubscriptionRecoveryPolicy
argument_list|()
operator|.
name|browse
argument_list|(
name|topic
operator|.
name|getActiveMQDestination
argument_list|()
argument_list|)
decl_stmt|;
comment|// add recovered messages to subscription
for|for
control|(
name|Message
name|message
range|:
name|messages
control|)
block|{
specifier|final
name|Message
name|copy
init|=
name|message
operator|.
name|copy
argument_list|()
decl_stmt|;
name|copy
operator|.
name|setOriginalDestination
argument_list|(
name|message
operator|.
name|getDestination
argument_list|()
argument_list|)
expr_stmt|;
name|copy
operator|.
name|setDestination
argument_list|(
name|newDestination
argument_list|)
expr_stmt|;
name|copy
operator|.
name|setRegionDestination
argument_list|(
name|regionDest
argument_list|)
expr_stmt|;
name|sub
operator|.
name|addRecoveredMessage
argument_list|(
name|context
argument_list|,
name|newDestination
operator|.
name|isQueue
argument_list|()
condition|?
operator|new
name|IndirectMessageReference
argument_list|(
name|copy
argument_list|)
else|:
name|copy
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
block|}
specifier|private
name|BaseDestination
name|getBaseDestination
parameter_list|(
name|Destination
name|virtualDest
parameter_list|)
block|{
if|if
condition|(
name|virtualDest
operator|instanceof
name|BaseDestination
condition|)
block|{
return|return
operator|(
name|BaseDestination
operator|)
name|virtualDest
return|;
block|}
elseif|else
if|if
condition|(
name|virtualDest
operator|instanceof
name|DestinationFilter
condition|)
block|{
return|return
operator|(
operator|(
name|DestinationFilter
operator|)
name|virtualDest
operator|)
operator|.
name|getAdaptor
argument_list|(
name|BaseDestination
operator|.
name|class
argument_list|)
return|;
block|}
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
specifier|synchronized
name|void
name|removeSubscription
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|Subscription
name|sub
parameter_list|,
name|long
name|lastDeliveredSequenceId
parameter_list|)
throws|throws
name|Exception
block|{
name|super
operator|.
name|removeSubscription
argument_list|(
name|context
argument_list|,
name|sub
argument_list|,
name|lastDeliveredSequenceId
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
specifier|synchronized
name|void
name|deleteSubscription
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|SubscriptionKey
name|key
parameter_list|)
throws|throws
name|Exception
block|{
name|super
operator|.
name|deleteSubscription
argument_list|(
name|context
argument_list|,
name|key
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"MappedQueueFilter["
operator|+
name|virtualDestination
operator|+
literal|", "
operator|+
name|next
operator|+
literal|"]"
return|;
block|}
block|}
end_class

end_unit

