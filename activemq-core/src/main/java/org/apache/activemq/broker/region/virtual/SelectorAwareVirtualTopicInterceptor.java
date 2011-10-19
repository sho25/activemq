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
name|ProducerBrokerExchange
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
name|filter
operator|.
name|NonCachedMessageEvaluationContext
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
name|List
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

begin_class
specifier|public
class|class
name|SelectorAwareVirtualTopicInterceptor
extends|extends
name|VirtualTopicInterceptor
block|{
specifier|public
name|SelectorAwareVirtualTopicInterceptor
parameter_list|(
name|Destination
name|next
parameter_list|,
name|String
name|prefix
parameter_list|,
name|String
name|postfix
parameter_list|,
name|boolean
name|local
parameter_list|)
block|{
name|super
argument_list|(
name|next
argument_list|,
name|prefix
argument_list|,
name|postfix
argument_list|,
name|local
argument_list|)
expr_stmt|;
block|}
comment|/**      * Respect the selectors of the subscriptions to ensure only matched messages are dispatched to      * the virtual queues, hence there is no build up of unmatched messages on these destinations      */
annotation|@
name|Override
specifier|protected
name|void
name|send
parameter_list|(
name|ProducerBrokerExchange
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
name|getConnectionContext
argument_list|()
operator|.
name|getBroker
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|Destination
argument_list|>
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
name|Destination
name|dest
range|:
name|destinations
control|)
block|{
if|if
condition|(
name|matchesSomeConsumer
argument_list|(
name|message
argument_list|,
name|dest
argument_list|)
condition|)
block|{
name|dest
operator|.
name|send
argument_list|(
name|context
argument_list|,
name|message
operator|.
name|copy
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|private
name|boolean
name|matchesSomeConsumer
parameter_list|(
name|Message
name|message
parameter_list|,
name|Destination
name|dest
parameter_list|)
throws|throws
name|IOException
block|{
name|boolean
name|matches
init|=
literal|false
decl_stmt|;
name|MessageEvaluationContext
name|msgContext
init|=
operator|new
name|NonCachedMessageEvaluationContext
argument_list|()
decl_stmt|;
name|msgContext
operator|.
name|setDestination
argument_list|(
name|dest
operator|.
name|getActiveMQDestination
argument_list|()
argument_list|)
expr_stmt|;
name|msgContext
operator|.
name|setMessageReference
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|Subscription
argument_list|>
name|subs
init|=
name|dest
operator|.
name|getConsumers
argument_list|()
decl_stmt|;
for|for
control|(
name|Subscription
name|sub
range|:
name|subs
control|)
block|{
if|if
condition|(
name|sub
operator|.
name|matches
argument_list|(
name|message
argument_list|,
name|msgContext
argument_list|)
condition|)
block|{
name|matches
operator|=
literal|true
expr_stmt|;
break|break;
block|}
block|}
return|return
name|matches
return|;
block|}
block|}
end_class

end_unit

