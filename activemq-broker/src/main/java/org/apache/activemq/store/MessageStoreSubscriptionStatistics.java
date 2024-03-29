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
name|util
operator|.
name|concurrent
operator|.
name|ConcurrentHashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ConcurrentMap
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
name|management
operator|.
name|CountStatisticImpl
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
name|management
operator|.
name|SizeStatisticImpl
import|;
end_import

begin_class
specifier|public
class|class
name|MessageStoreSubscriptionStatistics
extends|extends
name|AbstractMessageStoreStatistics
block|{
specifier|private
specifier|final
name|ConcurrentMap
argument_list|<
name|String
argument_list|,
name|SubscriptionStatistics
argument_list|>
name|subStatistics
init|=
operator|new
name|ConcurrentHashMap
argument_list|<>
argument_list|()
decl_stmt|;
comment|/**      * @param enabled      * @param countDescription      * @param sizeDescription      */
specifier|public
name|MessageStoreSubscriptionStatistics
parameter_list|(
name|boolean
name|enabled
parameter_list|)
block|{
name|super
argument_list|(
name|enabled
argument_list|,
literal|"The number of messages or this subscription in the message store"
argument_list|,
literal|"Size of messages contained by this subscription in the message store"
argument_list|)
expr_stmt|;
block|}
comment|/**      * Total count for all subscriptions      */
annotation|@
name|Override
specifier|public
name|CountStatisticImpl
name|getMessageCount
parameter_list|()
block|{
return|return
name|this
operator|.
name|messageCount
return|;
block|}
comment|/**      * Total size for all subscriptions      */
annotation|@
name|Override
specifier|public
name|SizeStatisticImpl
name|getMessageSize
parameter_list|()
block|{
return|return
name|this
operator|.
name|messageSize
return|;
block|}
specifier|public
name|CountStatisticImpl
name|getMessageCount
parameter_list|(
name|String
name|subKey
parameter_list|)
block|{
return|return
name|getOrInitStatistics
argument_list|(
name|subKey
argument_list|)
operator|.
name|getMessageCount
argument_list|()
return|;
block|}
specifier|public
name|SizeStatisticImpl
name|getMessageSize
parameter_list|(
name|String
name|subKey
parameter_list|)
block|{
return|return
name|getOrInitStatistics
argument_list|(
name|subKey
argument_list|)
operator|.
name|getMessageSize
argument_list|()
return|;
block|}
specifier|public
name|void
name|removeSubscription
parameter_list|(
name|String
name|subKey
parameter_list|)
block|{
name|SubscriptionStatistics
name|subStats
init|=
name|subStatistics
operator|.
name|remove
argument_list|(
name|subKey
argument_list|)
decl_stmt|;
comment|//Subtract from the parent
if|if
condition|(
name|subStats
operator|!=
literal|null
condition|)
block|{
name|getMessageCount
argument_list|()
operator|.
name|subtract
argument_list|(
name|subStats
operator|.
name|getMessageCount
argument_list|()
operator|.
name|getCount
argument_list|()
argument_list|)
expr_stmt|;
name|getMessageSize
argument_list|()
operator|.
name|addSize
argument_list|(
operator|-
name|subStats
operator|.
name|getMessageSize
argument_list|()
operator|.
name|getTotalSize
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|reset
parameter_list|()
block|{
name|super
operator|.
name|reset
argument_list|()
expr_stmt|;
name|subStatistics
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
specifier|private
name|SubscriptionStatistics
name|getOrInitStatistics
parameter_list|(
name|String
name|subKey
parameter_list|)
block|{
name|SubscriptionStatistics
name|subStats
init|=
name|subStatistics
operator|.
name|get
argument_list|(
name|subKey
argument_list|)
decl_stmt|;
if|if
condition|(
name|subStats
operator|==
literal|null
condition|)
block|{
specifier|final
name|SubscriptionStatistics
name|stats
init|=
operator|new
name|SubscriptionStatistics
argument_list|()
decl_stmt|;
name|subStats
operator|=
name|subStatistics
operator|.
name|putIfAbsent
argument_list|(
name|subKey
argument_list|,
name|stats
argument_list|)
expr_stmt|;
if|if
condition|(
name|subStats
operator|==
literal|null
condition|)
block|{
name|subStats
operator|=
name|stats
expr_stmt|;
block|}
block|}
return|return
name|subStats
return|;
block|}
specifier|private
class|class
name|SubscriptionStatistics
extends|extends
name|AbstractMessageStoreStatistics
block|{
specifier|public
name|SubscriptionStatistics
parameter_list|()
block|{
name|this
argument_list|(
name|MessageStoreSubscriptionStatistics
operator|.
name|this
operator|.
name|enabled
argument_list|)
expr_stmt|;
block|}
comment|/**          * @param enabled          * @param countDescription          * @param sizeDescription          */
specifier|public
name|SubscriptionStatistics
parameter_list|(
name|boolean
name|enabled
parameter_list|)
block|{
name|super
argument_list|(
name|enabled
argument_list|,
literal|"The number of messages or this subscription in the message store"
argument_list|,
literal|"Size of messages contained by this subscription in the message store"
argument_list|)
expr_stmt|;
name|this
operator|.
name|setParent
argument_list|(
name|MessageStoreSubscriptionStatistics
operator|.
name|this
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

