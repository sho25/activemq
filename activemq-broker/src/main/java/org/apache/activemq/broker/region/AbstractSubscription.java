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
name|java
operator|.
name|util
operator|.
name|Collections
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
name|concurrent
operator|.
name|CopyOnWriteArrayList
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
name|atomic
operator|.
name|AtomicLong
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
name|javax
operator|.
name|jms
operator|.
name|JMSException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|ObjectName
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
name|filter
operator|.
name|LogicExpression
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
name|NoLocalExpression
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
specifier|abstract
class|class
name|AbstractSubscription
implements|implements
name|Subscription
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
name|AbstractSubscription
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|protected
name|Broker
name|broker
decl_stmt|;
specifier|protected
name|ConnectionContext
name|context
decl_stmt|;
specifier|protected
name|ConsumerInfo
name|info
decl_stmt|;
specifier|protected
specifier|final
name|DestinationFilter
name|destinationFilter
decl_stmt|;
specifier|protected
specifier|final
name|CopyOnWriteArrayList
argument_list|<
name|Destination
argument_list|>
name|destinations
init|=
operator|new
name|CopyOnWriteArrayList
argument_list|<
name|Destination
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
name|BooleanExpression
name|selectorExpression
decl_stmt|;
specifier|private
name|ObjectName
name|objectName
decl_stmt|;
specifier|private
name|int
name|cursorMemoryHighWaterMark
init|=
literal|70
decl_stmt|;
specifier|private
name|boolean
name|slowConsumer
decl_stmt|;
specifier|private
name|long
name|lastAckTime
decl_stmt|;
specifier|private
specifier|final
name|SubscriptionStatistics
name|subscriptionStatistics
init|=
operator|new
name|SubscriptionStatistics
argument_list|()
decl_stmt|;
specifier|public
name|AbstractSubscription
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
name|this
operator|.
name|broker
operator|=
name|broker
expr_stmt|;
name|this
operator|.
name|context
operator|=
name|context
expr_stmt|;
name|this
operator|.
name|info
operator|=
name|info
expr_stmt|;
name|this
operator|.
name|destinationFilter
operator|=
name|DestinationFilter
operator|.
name|parseFilter
argument_list|(
name|info
operator|.
name|getDestination
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|selectorExpression
operator|=
name|parseSelector
argument_list|(
name|info
argument_list|)
expr_stmt|;
name|this
operator|.
name|lastAckTime
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
expr_stmt|;
block|}
specifier|private
specifier|static
name|BooleanExpression
name|parseSelector
parameter_list|(
name|ConsumerInfo
name|info
parameter_list|)
throws|throws
name|InvalidSelectorException
block|{
name|BooleanExpression
name|rc
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|info
operator|.
name|getSelector
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|rc
operator|=
name|SelectorParser
operator|.
name|parse
argument_list|(
name|info
operator|.
name|getSelector
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|info
operator|.
name|isNoLocal
argument_list|()
condition|)
block|{
if|if
condition|(
name|rc
operator|==
literal|null
condition|)
block|{
name|rc
operator|=
operator|new
name|NoLocalExpression
argument_list|(
name|info
operator|.
name|getConsumerId
argument_list|()
operator|.
name|getConnectionId
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|rc
operator|=
name|LogicExpression
operator|.
name|createAND
argument_list|(
operator|new
name|NoLocalExpression
argument_list|(
name|info
operator|.
name|getConsumerId
argument_list|()
operator|.
name|getConnectionId
argument_list|()
argument_list|)
argument_list|,
name|rc
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|info
operator|.
name|getAdditionalPredicate
argument_list|()
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|rc
operator|==
literal|null
condition|)
block|{
name|rc
operator|=
name|info
operator|.
name|getAdditionalPredicate
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|rc
operator|=
name|LogicExpression
operator|.
name|createAND
argument_list|(
name|info
operator|.
name|getAdditionalPredicate
argument_list|()
argument_list|,
name|rc
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|rc
return|;
block|}
annotation|@
name|Override
specifier|public
specifier|synchronized
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
parameter_list|)
throws|throws
name|Exception
block|{
name|this
operator|.
name|lastAckTime
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
expr_stmt|;
name|subscriptionStatistics
operator|.
name|getConsumedCount
argument_list|()
operator|.
name|increment
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|matches
parameter_list|(
name|MessageReference
name|node
parameter_list|,
name|MessageEvaluationContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|ConsumerId
name|targetConsumerId
init|=
name|node
operator|.
name|getTargetConsumerId
argument_list|()
decl_stmt|;
if|if
condition|(
name|targetConsumerId
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
operator|!
name|targetConsumerId
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
return|return
literal|false
return|;
block|}
block|}
try|try
block|{
return|return
operator|(
name|selectorExpression
operator|==
literal|null
operator|||
name|selectorExpression
operator|.
name|matches
argument_list|(
name|context
argument_list|)
operator|)
operator|&&
name|this
operator|.
name|context
operator|.
name|isAllowedToConsume
argument_list|(
name|node
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|JMSException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Selector failed to evaluate: {}"
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isWildcard
parameter_list|()
block|{
return|return
name|destinationFilter
operator|.
name|isWildcard
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|matches
parameter_list|(
name|ActiveMQDestination
name|destination
parameter_list|)
block|{
return|return
name|destinationFilter
operator|.
name|matches
argument_list|(
name|destination
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|add
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|Destination
name|destination
parameter_list|)
throws|throws
name|Exception
block|{
name|destinations
operator|.
name|add
argument_list|(
name|destination
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|List
argument_list|<
name|MessageReference
argument_list|>
name|remove
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|Destination
name|destination
parameter_list|)
throws|throws
name|Exception
block|{
name|destinations
operator|.
name|remove
argument_list|(
name|destination
argument_list|)
expr_stmt|;
return|return
name|Collections
operator|.
name|EMPTY_LIST
return|;
block|}
annotation|@
name|Override
specifier|public
name|ConsumerInfo
name|getConsumerInfo
parameter_list|()
block|{
return|return
name|info
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|gc
parameter_list|()
block|{     }
annotation|@
name|Override
specifier|public
name|ConnectionContext
name|getContext
parameter_list|()
block|{
return|return
name|context
return|;
block|}
specifier|public
name|ConsumerInfo
name|getInfo
parameter_list|()
block|{
return|return
name|info
return|;
block|}
specifier|public
name|BooleanExpression
name|getSelectorExpression
parameter_list|()
block|{
return|return
name|selectorExpression
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getSelector
parameter_list|()
block|{
return|return
name|info
operator|.
name|getSelector
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setSelector
parameter_list|(
name|String
name|selector
parameter_list|)
throws|throws
name|InvalidSelectorException
block|{
name|ConsumerInfo
name|copy
init|=
name|info
operator|.
name|copy
argument_list|()
decl_stmt|;
name|copy
operator|.
name|setSelector
argument_list|(
name|selector
argument_list|)
expr_stmt|;
name|BooleanExpression
name|newSelector
init|=
name|parseSelector
argument_list|(
name|copy
argument_list|)
decl_stmt|;
comment|// its valid so lets actually update it now
name|info
operator|.
name|setSelector
argument_list|(
name|selector
argument_list|)
expr_stmt|;
name|this
operator|.
name|selectorExpression
operator|=
name|newSelector
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|ObjectName
name|getObjectName
parameter_list|()
block|{
return|return
name|objectName
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setObjectName
parameter_list|(
name|ObjectName
name|objectName
parameter_list|)
block|{
name|this
operator|.
name|objectName
operator|=
name|objectName
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|getPrefetchSize
parameter_list|()
block|{
return|return
name|info
operator|.
name|getPrefetchSize
argument_list|()
return|;
block|}
specifier|public
name|void
name|setPrefetchSize
parameter_list|(
name|int
name|newSize
parameter_list|)
block|{
name|info
operator|.
name|setPrefetchSize
argument_list|(
name|newSize
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isRecoveryRequired
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isSlowConsumer
parameter_list|()
block|{
return|return
name|slowConsumer
return|;
block|}
specifier|public
name|void
name|setSlowConsumer
parameter_list|(
name|boolean
name|val
parameter_list|)
block|{
name|slowConsumer
operator|=
name|val
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|addRecoveredMessage
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|MessageReference
name|message
parameter_list|)
throws|throws
name|Exception
block|{
name|boolean
name|result
init|=
literal|false
decl_stmt|;
name|MessageEvaluationContext
name|msgContext
init|=
name|context
operator|.
name|getMessageEvaluationContext
argument_list|()
decl_stmt|;
try|try
block|{
name|Destination
name|regionDestination
init|=
operator|(
name|Destination
operator|)
name|message
operator|.
name|getRegionDestination
argument_list|()
decl_stmt|;
name|msgContext
operator|.
name|setDestination
argument_list|(
name|regionDestination
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
name|result
operator|=
name|matches
argument_list|(
name|message
argument_list|,
name|msgContext
argument_list|)
expr_stmt|;
if|if
condition|(
name|result
condition|)
block|{
name|doAddRecoveredMessage
argument_list|(
name|message
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|msgContext
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
annotation|@
name|Override
specifier|public
name|ActiveMQDestination
name|getActiveMQDestination
parameter_list|()
block|{
return|return
name|info
operator|!=
literal|null
condition|?
name|info
operator|.
name|getDestination
argument_list|()
else|:
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isBrowser
parameter_list|()
block|{
return|return
name|info
operator|!=
literal|null
operator|&&
name|info
operator|.
name|isBrowser
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|getInFlightUsage
parameter_list|()
block|{
if|if
condition|(
name|info
operator|.
name|getPrefetchSize
argument_list|()
operator|>
literal|0
condition|)
block|{
return|return
operator|(
name|getInFlightSize
argument_list|()
operator|*
literal|100
operator|)
operator|/
name|info
operator|.
name|getPrefetchSize
argument_list|()
return|;
block|}
return|return
name|Integer
operator|.
name|MAX_VALUE
return|;
block|}
comment|/**      * Add a destination      * @param destination      */
specifier|public
name|void
name|addDestination
parameter_list|(
name|Destination
name|destination
parameter_list|)
block|{      }
comment|/**      * Remove a destination      * @param destination      */
specifier|public
name|void
name|removeDestination
parameter_list|(
name|Destination
name|destination
parameter_list|)
block|{      }
annotation|@
name|Override
specifier|public
name|int
name|getCursorMemoryHighWaterMark
parameter_list|()
block|{
return|return
name|this
operator|.
name|cursorMemoryHighWaterMark
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setCursorMemoryHighWaterMark
parameter_list|(
name|int
name|cursorMemoryHighWaterMark
parameter_list|)
block|{
name|this
operator|.
name|cursorMemoryHighWaterMark
operator|=
name|cursorMemoryHighWaterMark
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|countBeforeFull
parameter_list|()
block|{
return|return
name|getDispatchedQueueSize
argument_list|()
operator|-
name|info
operator|.
name|getPrefetchSize
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|unmatched
parameter_list|(
name|MessageReference
name|node
parameter_list|)
throws|throws
name|IOException
block|{
comment|// only durable topic subs have something to do here
block|}
specifier|protected
name|void
name|doAddRecoveredMessage
parameter_list|(
name|MessageReference
name|message
parameter_list|)
throws|throws
name|Exception
block|{
name|add
argument_list|(
name|message
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getTimeOfLastMessageAck
parameter_list|()
block|{
return|return
name|lastAckTime
return|;
block|}
specifier|public
name|void
name|setTimeOfLastMessageAck
parameter_list|(
name|long
name|value
parameter_list|)
block|{
name|this
operator|.
name|lastAckTime
operator|=
name|value
expr_stmt|;
block|}
specifier|public
name|long
name|getConsumedCount
parameter_list|()
block|{
return|return
name|subscriptionStatistics
operator|.
name|getConsumedCount
argument_list|()
operator|.
name|getCount
argument_list|()
return|;
block|}
specifier|public
name|void
name|incrementConsumedCount
parameter_list|()
block|{
name|subscriptionStatistics
operator|.
name|getConsumedCount
argument_list|()
operator|.
name|increment
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|resetConsumedCount
parameter_list|()
block|{
name|subscriptionStatistics
operator|.
name|getConsumedCount
argument_list|()
operator|.
name|reset
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|SubscriptionStatistics
name|getSubscriptionStatistics
parameter_list|()
block|{
return|return
name|subscriptionStatistics
return|;
block|}
block|}
end_class

end_unit

