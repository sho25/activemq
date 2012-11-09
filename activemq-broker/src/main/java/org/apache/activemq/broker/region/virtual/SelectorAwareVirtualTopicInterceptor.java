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
name|BooleanExpression
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
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|plugin
operator|.
name|SubQueueSelectorCacheBroker
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
name|selector
operator|.
name|SelectorParser
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
name|LRUCache
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
name|SelectorAwareVirtualTopicInterceptor
extends|extends
name|VirtualTopicInterceptor
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
name|SelectorAwareVirtualTopicInterceptor
operator|.
name|class
argument_list|)
decl_stmt|;
name|LRUCache
argument_list|<
name|String
argument_list|,
name|BooleanExpression
argument_list|>
name|expressionCache
init|=
operator|new
name|LRUCache
argument_list|<
name|String
argument_list|,
name|BooleanExpression
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
name|SubQueueSelectorCacheBroker
name|selectorCachePlugin
decl_stmt|;
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
name|broker
argument_list|,
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
specifier|final
name|Broker
name|broker
parameter_list|,
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
if|if
condition|(
name|matches
operator|==
literal|false
operator|&&
name|subs
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
name|matches
operator|=
name|tryMatchingCachedSubs
argument_list|(
name|broker
argument_list|,
name|dest
argument_list|,
name|msgContext
argument_list|)
expr_stmt|;
block|}
return|return
name|matches
return|;
block|}
specifier|private
name|boolean
name|tryMatchingCachedSubs
parameter_list|(
specifier|final
name|Broker
name|broker
parameter_list|,
name|Destination
name|dest
parameter_list|,
name|MessageEvaluationContext
name|msgContext
parameter_list|)
block|{
name|boolean
name|matches
init|=
literal|false
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"No active consumer match found. Will try cache if configured..."
argument_list|)
expr_stmt|;
comment|//retrieve the specific plugin class and lookup the selector for the destination.
specifier|final
name|SubQueueSelectorCacheBroker
name|cache
init|=
name|getSubQueueSelectorCacheBrokerPlugin
argument_list|(
name|broker
argument_list|)
decl_stmt|;
if|if
condition|(
name|cache
operator|!=
literal|null
condition|)
block|{
specifier|final
name|String
name|selector
init|=
name|cache
operator|.
name|getSelector
argument_list|(
name|dest
operator|.
name|getActiveMQDestination
argument_list|()
operator|.
name|getQualifiedName
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|selector
operator|!=
literal|null
condition|)
block|{
try|try
block|{
specifier|final
name|BooleanExpression
name|expression
init|=
name|getExpression
argument_list|(
name|selector
argument_list|)
decl_stmt|;
name|matches
operator|=
name|expression
operator|.
name|matches
argument_list|(
name|msgContext
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|matches
return|;
block|}
specifier|private
name|BooleanExpression
name|getExpression
parameter_list|(
name|String
name|selector
parameter_list|)
throws|throws
name|Exception
block|{
name|BooleanExpression
name|result
decl_stmt|;
synchronized|synchronized
init|(
name|expressionCache
init|)
block|{
name|result
operator|=
name|expressionCache
operator|.
name|get
argument_list|(
name|selector
argument_list|)
expr_stmt|;
if|if
condition|(
name|result
operator|==
literal|null
condition|)
block|{
name|result
operator|=
name|compileSelector
argument_list|(
name|selector
argument_list|)
expr_stmt|;
name|expressionCache
operator|.
name|put
argument_list|(
name|selector
argument_list|,
name|result
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|result
return|;
block|}
comment|/**      * @return The SubQueueSelectorCacheBroker instance or null if no such broker is available.      */
specifier|private
name|SubQueueSelectorCacheBroker
name|getSubQueueSelectorCacheBrokerPlugin
parameter_list|(
specifier|final
name|Broker
name|broker
parameter_list|)
block|{
if|if
condition|(
name|selectorCachePlugin
operator|==
literal|null
condition|)
block|{
name|selectorCachePlugin
operator|=
operator|(
name|SubQueueSelectorCacheBroker
operator|)
name|broker
operator|.
name|getAdaptor
argument_list|(
name|SubQueueSelectorCacheBroker
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
comment|//if
return|return
name|selectorCachePlugin
return|;
block|}
comment|/**      * Pre-compile the JMS selector.      *      * @param selectorExpression The non-null JMS selector expression.      */
specifier|private
name|BooleanExpression
name|compileSelector
parameter_list|(
specifier|final
name|String
name|selectorExpression
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|SelectorParser
operator|.
name|parse
argument_list|(
name|selectorExpression
argument_list|)
return|;
block|}
block|}
end_class

end_unit
