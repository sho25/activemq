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
name|network
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
name|ArrayList
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
name|filter
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
name|transport
operator|.
name|Transport
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
comment|/**  * Consolidates subscriptions  *   * @version $Revision: 1.1 $  */
end_comment

begin_class
specifier|public
class|class
name|ConduitBridge
extends|extends
name|DemandForwardingBridge
block|{
specifier|private
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|ConduitBridge
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**      * Constructor      *       * @param localBroker      * @param remoteBroker      */
specifier|public
name|ConduitBridge
parameter_list|(
name|NetworkBridgeConfiguration
name|configuration
parameter_list|,
name|Transport
name|localBroker
parameter_list|,
name|Transport
name|remoteBroker
parameter_list|)
block|{
name|super
argument_list|(
name|configuration
argument_list|,
name|localBroker
argument_list|,
name|remoteBroker
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|DemandSubscription
name|createDemandSubscription
parameter_list|(
name|ConsumerInfo
name|info
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|addToAlreadyInterestedConsumers
argument_list|(
name|info
argument_list|)
condition|)
block|{
return|return
literal|null
return|;
comment|// don't want this subscription added
block|}
comment|//add our original id to ourselves
name|info
operator|.
name|addNetworkConsumerId
argument_list|(
name|info
operator|.
name|getConsumerId
argument_list|()
argument_list|)
expr_stmt|;
name|info
operator|.
name|setSelector
argument_list|(
literal|null
argument_list|)
expr_stmt|;
return|return
name|doCreateDemandSubscription
argument_list|(
name|info
argument_list|)
return|;
block|}
specifier|protected
name|boolean
name|addToAlreadyInterestedConsumers
parameter_list|(
name|ConsumerInfo
name|info
parameter_list|)
block|{
comment|// search through existing subscriptions and see if we have a match
name|boolean
name|matched
init|=
literal|false
decl_stmt|;
name|DestinationFilter
name|filter
init|=
name|DestinationFilter
operator|.
name|parseFilter
argument_list|(
name|info
operator|.
name|getDestination
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|Iterator
name|i
init|=
name|subscriptionMapByLocalId
operator|.
name|values
argument_list|()
operator|.
name|iterator
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|DemandSubscription
name|ds
init|=
operator|(
name|DemandSubscription
operator|)
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|filter
operator|.
name|matches
argument_list|(
name|ds
operator|.
name|getLocalInfo
argument_list|()
operator|.
name|getDestination
argument_list|()
argument_list|)
condition|)
block|{
comment|// add the interest in the subscription
comment|// ds.add(ds.getRemoteInfo().getConsumerId());
name|ds
operator|.
name|add
argument_list|(
name|info
operator|.
name|getConsumerId
argument_list|()
argument_list|)
expr_stmt|;
name|matched
operator|=
literal|true
expr_stmt|;
comment|// continue - we want interest to any existing
comment|// DemandSubscriptions
block|}
block|}
return|return
name|matched
return|;
block|}
specifier|protected
name|void
name|removeDemandSubscription
parameter_list|(
name|ConsumerId
name|id
parameter_list|)
throws|throws
name|IOException
block|{
name|List
argument_list|<
name|DemandSubscription
argument_list|>
name|tmpList
init|=
operator|new
name|ArrayList
argument_list|<
name|DemandSubscription
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|Iterator
name|i
init|=
name|subscriptionMapByLocalId
operator|.
name|values
argument_list|()
operator|.
name|iterator
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|DemandSubscription
name|ds
init|=
operator|(
name|DemandSubscription
operator|)
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
name|ds
operator|.
name|remove
argument_list|(
name|id
argument_list|)
expr_stmt|;
if|if
condition|(
name|ds
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|tmpList
operator|.
name|add
argument_list|(
name|ds
argument_list|)
expr_stmt|;
block|}
block|}
for|for
control|(
name|Iterator
argument_list|<
name|DemandSubscription
argument_list|>
name|i
init|=
name|tmpList
operator|.
name|iterator
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|DemandSubscription
name|ds
init|=
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
name|subscriptionMapByLocalId
operator|.
name|remove
argument_list|(
name|ds
operator|.
name|getRemoteInfo
argument_list|()
operator|.
name|getConsumerId
argument_list|()
argument_list|)
expr_stmt|;
name|removeSubscription
argument_list|(
name|ds
argument_list|)
expr_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isTraceEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|trace
argument_list|(
literal|"removing sub on "
operator|+
name|localBroker
operator|+
literal|" from "
operator|+
name|remoteBrokerName
operator|+
literal|" :  "
operator|+
name|ds
operator|.
name|getRemoteInfo
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

