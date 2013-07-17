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
name|policy
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
name|LinkedList
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
operator|.
name|Entry
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

begin_comment
comment|/**  * Abort slow consumers when they reach the configured threshold of slowness,  *  * default is that a consumer that has not Ack'd a message for 30 seconds is slow.  *  * @org.apache.xbean.XBean  */
end_comment

begin_class
specifier|public
class|class
name|AbortSlowAckConsumerStrategy
extends|extends
name|AbortSlowConsumerStrategy
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
name|AbortSlowAckConsumerStrategy
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|List
argument_list|<
name|Destination
argument_list|>
name|destinations
init|=
operator|new
name|LinkedList
argument_list|<
name|Destination
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
name|long
name|maxTimeSinceLastAck
init|=
literal|30
operator|*
literal|1000
decl_stmt|;
specifier|private
name|boolean
name|ignoreIdleConsumers
init|=
literal|true
decl_stmt|;
specifier|public
name|AbortSlowAckConsumerStrategy
parameter_list|()
block|{
name|this
operator|.
name|name
operator|=
literal|"AbortSlowAckConsumerStrategy@"
operator|+
name|hashCode
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setBrokerService
parameter_list|(
name|Broker
name|broker
parameter_list|)
block|{
name|super
operator|.
name|setBrokerService
argument_list|(
name|broker
argument_list|)
expr_stmt|;
comment|// Task starts right away since we may not receive any slow consumer events.
if|if
condition|(
name|taskStarted
operator|.
name|compareAndSet
argument_list|(
literal|false
argument_list|,
literal|true
argument_list|)
condition|)
block|{
name|scheduler
operator|.
name|executePeriodically
argument_list|(
name|this
argument_list|,
name|getCheckPeriod
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|slowConsumer
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|Subscription
name|subs
parameter_list|)
block|{
comment|// Ignore these events, we just look at time since last Ack.
block|}
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
if|if
condition|(
name|maxTimeSinceLastAck
operator|<
literal|0
condition|)
block|{
comment|// nothing to do
name|LOG
operator|.
name|info
argument_list|(
literal|"no limit set, slowConsumer strategy has nothing to do"
argument_list|)
expr_stmt|;
return|return;
block|}
if|if
condition|(
name|getMaxSlowDuration
argument_list|()
operator|>
literal|0
condition|)
block|{
comment|// For subscriptions that are already slow we mark them again and check below if
comment|// they've exceeded their configured lifetime.
for|for
control|(
name|SlowConsumerEntry
name|entry
range|:
name|slowConsumers
operator|.
name|values
argument_list|()
control|)
block|{
name|entry
operator|.
name|mark
argument_list|()
expr_stmt|;
block|}
block|}
name|List
argument_list|<
name|Destination
argument_list|>
name|disposed
init|=
operator|new
name|ArrayList
argument_list|<
name|Destination
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|Destination
name|destination
range|:
name|destinations
control|)
block|{
if|if
condition|(
name|destination
operator|.
name|isDisposed
argument_list|()
condition|)
block|{
name|disposed
operator|.
name|add
argument_list|(
name|destination
argument_list|)
expr_stmt|;
continue|continue;
block|}
comment|// Not explicitly documented but this returns a stable copy.
name|List
argument_list|<
name|Subscription
argument_list|>
name|subscribers
init|=
name|destination
operator|.
name|getConsumers
argument_list|()
decl_stmt|;
name|updateSlowConsumersList
argument_list|(
name|subscribers
argument_list|)
expr_stmt|;
block|}
comment|// Clean up an disposed destinations to save space.
name|destinations
operator|.
name|removeAll
argument_list|(
name|disposed
argument_list|)
expr_stmt|;
name|abortAllQualifiedSlowConsumers
argument_list|()
expr_stmt|;
block|}
specifier|private
name|void
name|updateSlowConsumersList
parameter_list|(
name|List
argument_list|<
name|Subscription
argument_list|>
name|subscribers
parameter_list|)
block|{
for|for
control|(
name|Subscription
name|subscriber
range|:
name|subscribers
control|)
block|{
if|if
condition|(
name|isIgnoreIdleConsumers
argument_list|()
operator|&&
name|subscriber
operator|.
name|getDispatchedQueueSize
argument_list|()
operator|==
literal|0
condition|)
block|{
comment|// Not considered Idle so ensure its cleared from the list
if|if
condition|(
name|slowConsumers
operator|.
name|remove
argument_list|(
name|subscriber
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"sub: {} is no longer slow"
argument_list|,
name|subscriber
operator|.
name|getConsumerInfo
argument_list|()
operator|.
name|getConsumerId
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|long
name|lastAckTime
init|=
name|subscriber
operator|.
name|getTimeOfLastMessageAck
argument_list|()
decl_stmt|;
name|long
name|timeDelta
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|lastAckTime
decl_stmt|;
if|if
condition|(
name|timeDelta
operator|>
name|maxTimeSinceLastAck
condition|)
block|{
if|if
condition|(
operator|!
name|slowConsumers
operator|.
name|containsKey
argument_list|(
name|subscriber
argument_list|)
condition|)
block|{
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"sub: {} is now slow"
argument_list|,
name|subscriber
operator|.
name|getConsumerInfo
argument_list|()
operator|.
name|getConsumerId
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|slowConsumers
operator|.
name|put
argument_list|(
name|subscriber
argument_list|,
operator|new
name|SlowConsumerEntry
argument_list|(
name|subscriber
operator|.
name|getContext
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|getMaxSlowCount
argument_list|()
operator|>
literal|0
condition|)
block|{
name|slowConsumers
operator|.
name|get
argument_list|(
name|subscriber
argument_list|)
operator|.
name|slow
argument_list|()
expr_stmt|;
block|}
block|}
else|else
block|{
if|if
condition|(
name|slowConsumers
operator|.
name|remove
argument_list|(
name|subscriber
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"sub: {} is no longer slow"
argument_list|,
name|subscriber
operator|.
name|getConsumerInfo
argument_list|()
operator|.
name|getConsumerId
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
specifier|private
name|void
name|abortAllQualifiedSlowConsumers
parameter_list|()
block|{
name|HashMap
argument_list|<
name|Subscription
argument_list|,
name|SlowConsumerEntry
argument_list|>
name|toAbort
init|=
operator|new
name|HashMap
argument_list|<
name|Subscription
argument_list|,
name|SlowConsumerEntry
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|Entry
argument_list|<
name|Subscription
argument_list|,
name|SlowConsumerEntry
argument_list|>
name|entry
range|:
name|slowConsumers
operator|.
name|entrySet
argument_list|()
control|)
block|{
if|if
condition|(
name|entry
operator|.
name|getKey
argument_list|()
operator|.
name|isSlowConsumer
argument_list|()
condition|)
block|{
if|if
condition|(
name|getMaxSlowDuration
argument_list|()
operator|>
literal|0
operator|&&
operator|(
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|markCount
operator|*
name|getCheckPeriod
argument_list|()
operator|>
name|getMaxSlowDuration
argument_list|()
operator|)
operator|||
name|getMaxSlowCount
argument_list|()
operator|>
literal|0
operator|&&
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|slowCount
operator|>
name|getMaxSlowCount
argument_list|()
condition|)
block|{
name|toAbort
operator|.
name|put
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|slowConsumers
operator|.
name|remove
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|// Now if any subscriptions made it into the aborts list we can kick them.
name|abortSubscription
argument_list|(
name|toAbort
argument_list|,
name|isAbortConnection
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|addDestination
parameter_list|(
name|Destination
name|destination
parameter_list|)
block|{
name|this
operator|.
name|destinations
operator|.
name|add
argument_list|(
name|destination
argument_list|)
expr_stmt|;
block|}
comment|/**      * Gets the maximum time since last Ack before a subscription is considered to be slow.      *      * @return the maximum time since last Ack before the consumer is considered to be slow.      */
specifier|public
name|long
name|getMaxTimeSinceLastAck
parameter_list|()
block|{
return|return
name|maxTimeSinceLastAck
return|;
block|}
comment|/**      * Sets the maximum time since last Ack before a subscription is considered to be slow.      *      * @param maxTimeSinceLastAck      *      the maximum time since last Ack (mills) before the consumer is considered to be slow.      */
specifier|public
name|void
name|setMaxTimeSinceLastAck
parameter_list|(
name|long
name|maxTimeSinceLastAck
parameter_list|)
block|{
name|this
operator|.
name|maxTimeSinceLastAck
operator|=
name|maxTimeSinceLastAck
expr_stmt|;
block|}
comment|/**      * Returns whether the strategy is configured to ignore consumers that are simply idle, i.e      * consumers that have no pending acks (dispatch queue is empty).      *      * @return true if the strategy will ignore idle consumer when looking for slow consumers.      */
specifier|public
name|boolean
name|isIgnoreIdleConsumers
parameter_list|()
block|{
return|return
name|ignoreIdleConsumers
return|;
block|}
comment|/**      * Sets whether the strategy is configured to ignore consumers that are simply idle, i.e      * consumers that have no pending acks (dispatch queue is empty).      *      * When configured to not ignore idle consumers this strategy acks not only on consumers      * that are actually slow but also on any consumer that has not received any messages for      * the maxTimeSinceLastAck.  This allows for a way to evict idle consumers while also      * aborting slow consumers.      *      * @param ignoreIdleConsumers      *      Should this strategy ignore idle consumers or consider all consumers when checking      *      the last ack time verses the maxTimeSinceLastAck value.      */
specifier|public
name|void
name|setIgnoreIdleConsumers
parameter_list|(
name|boolean
name|ignoreIdleConsumers
parameter_list|)
block|{
name|this
operator|.
name|ignoreIdleConsumers
operator|=
name|ignoreIdleConsumers
expr_stmt|;
block|}
block|}
end_class

end_unit

