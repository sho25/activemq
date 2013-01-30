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
name|BrokerId
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
name|NetworkBridgeFilter
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
comment|/**  * implement conditional behavior for queue consumers, allows replaying back to  * origin if no consumers are present on the local broker after a configurable  * delay, irrespective of the networkTTL Also allows rate limiting of messages  * through the network, useful for static includes  *  * @org.apache.xbean.XBean  */
end_comment

begin_class
specifier|public
class|class
name|ConditionalNetworkBridgeFilterFactory
implements|implements
name|NetworkBridgeFilterFactory
block|{
name|boolean
name|replayWhenNoConsumers
init|=
literal|false
decl_stmt|;
name|int
name|replayDelay
init|=
literal|0
decl_stmt|;
name|int
name|rateLimit
init|=
literal|0
decl_stmt|;
name|int
name|rateDuration
init|=
literal|1000
decl_stmt|;
annotation|@
name|Override
specifier|public
name|NetworkBridgeFilter
name|create
parameter_list|(
name|ConsumerInfo
name|info
parameter_list|,
name|BrokerId
index|[]
name|remoteBrokerPath
parameter_list|,
name|int
name|networkTimeToLive
parameter_list|)
block|{
name|ConditionalNetworkBridgeFilter
name|filter
init|=
operator|new
name|ConditionalNetworkBridgeFilter
argument_list|()
decl_stmt|;
name|filter
operator|.
name|setNetworkBrokerId
argument_list|(
name|remoteBrokerPath
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|filter
operator|.
name|setNetworkTTL
argument_list|(
name|networkTimeToLive
argument_list|)
expr_stmt|;
name|filter
operator|.
name|setAllowReplayWhenNoConsumers
argument_list|(
name|isReplayWhenNoConsumers
argument_list|()
argument_list|)
expr_stmt|;
name|filter
operator|.
name|setRateLimit
argument_list|(
name|getRateLimit
argument_list|()
argument_list|)
expr_stmt|;
name|filter
operator|.
name|setRateDuration
argument_list|(
name|getRateDuration
argument_list|()
argument_list|)
expr_stmt|;
name|filter
operator|.
name|setReplayDelay
argument_list|(
name|getReplayDelay
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|filter
return|;
block|}
specifier|public
name|void
name|setReplayWhenNoConsumers
parameter_list|(
name|boolean
name|replayWhenNoConsumers
parameter_list|)
block|{
name|this
operator|.
name|replayWhenNoConsumers
operator|=
name|replayWhenNoConsumers
expr_stmt|;
block|}
specifier|public
name|boolean
name|isReplayWhenNoConsumers
parameter_list|()
block|{
return|return
name|replayWhenNoConsumers
return|;
block|}
specifier|public
name|void
name|setRateLimit
parameter_list|(
name|int
name|rateLimit
parameter_list|)
block|{
name|this
operator|.
name|rateLimit
operator|=
name|rateLimit
expr_stmt|;
block|}
specifier|public
name|int
name|getRateLimit
parameter_list|()
block|{
return|return
name|rateLimit
return|;
block|}
specifier|public
name|int
name|getRateDuration
parameter_list|()
block|{
return|return
name|rateDuration
return|;
block|}
specifier|public
name|void
name|setRateDuration
parameter_list|(
name|int
name|rateDuration
parameter_list|)
block|{
name|this
operator|.
name|rateDuration
operator|=
name|rateDuration
expr_stmt|;
block|}
specifier|public
name|int
name|getReplayDelay
parameter_list|()
block|{
return|return
name|replayDelay
return|;
block|}
specifier|public
name|void
name|setReplayDelay
parameter_list|(
name|int
name|replayDelay
parameter_list|)
block|{
name|this
operator|.
name|replayDelay
operator|=
name|replayDelay
expr_stmt|;
block|}
specifier|private
specifier|static
class|class
name|ConditionalNetworkBridgeFilter
extends|extends
name|NetworkBridgeFilter
block|{
specifier|final
specifier|static
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|ConditionalNetworkBridgeFilter
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|int
name|rateLimit
decl_stmt|;
specifier|private
name|int
name|rateDuration
init|=
literal|1000
decl_stmt|;
specifier|private
name|boolean
name|allowReplayWhenNoConsumers
init|=
literal|true
decl_stmt|;
specifier|private
name|int
name|replayDelay
init|=
literal|1000
decl_stmt|;
specifier|private
name|int
name|matchCount
decl_stmt|;
specifier|private
name|long
name|rateDurationEnd
decl_stmt|;
annotation|@
name|Override
specifier|protected
name|boolean
name|matchesForwardingFilter
parameter_list|(
name|Message
name|message
parameter_list|,
specifier|final
name|MessageEvaluationContext
name|mec
parameter_list|)
block|{
name|boolean
name|match
init|=
literal|true
decl_stmt|;
if|if
condition|(
name|mec
operator|.
name|getDestination
argument_list|()
operator|.
name|isQueue
argument_list|()
operator|&&
name|contains
argument_list|(
name|message
operator|.
name|getBrokerPath
argument_list|()
argument_list|,
name|networkBrokerId
argument_list|)
condition|)
block|{
comment|// potential replay back to origin
name|match
operator|=
name|allowReplayWhenNoConsumers
operator|&&
name|hasNoLocalConsumers
argument_list|(
name|message
argument_list|,
name|mec
argument_list|)
operator|&&
name|hasNotJustArrived
argument_list|(
name|message
argument_list|)
expr_stmt|;
if|if
condition|(
name|match
operator|&&
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
literal|"Replaying  ["
operator|+
name|message
operator|.
name|getMessageId
argument_list|()
operator|+
literal|"] for ["
operator|+
name|message
operator|.
name|getDestination
argument_list|()
operator|+
literal|"] back to origin in the absence of a local consumer"
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// use existing filter logic for topics and non replays
name|match
operator|=
name|super
operator|.
name|matchesForwardingFilter
argument_list|(
name|message
argument_list|,
name|mec
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|match
operator|&&
name|rateLimitExceeded
argument_list|()
condition|)
block|{
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
literal|"Throttled network consumer rejecting ["
operator|+
name|message
operator|.
name|getMessageId
argument_list|()
operator|+
literal|"] for ["
operator|+
name|message
operator|.
name|getDestination
argument_list|()
operator|+
literal|" "
operator|+
name|matchCount
operator|+
literal|">"
operator|+
name|rateLimit
operator|+
literal|"/"
operator|+
name|rateDuration
argument_list|)
expr_stmt|;
block|}
name|match
operator|=
literal|false
expr_stmt|;
block|}
return|return
name|match
return|;
block|}
specifier|private
name|boolean
name|hasNotJustArrived
parameter_list|(
name|Message
name|message
parameter_list|)
block|{
return|return
name|replayDelay
operator|==
literal|0
operator|||
operator|(
name|message
operator|.
name|getBrokerInTime
argument_list|()
operator|+
name|replayDelay
operator|<
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|)
return|;
block|}
specifier|private
name|boolean
name|hasNoLocalConsumers
parameter_list|(
specifier|final
name|Message
name|message
parameter_list|,
specifier|final
name|MessageEvaluationContext
name|mec
parameter_list|)
block|{
name|Destination
name|regionDestination
init|=
operator|(
name|Destination
operator|)
name|mec
operator|.
name|getMessageReference
argument_list|()
operator|.
name|getRegionDestination
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|Subscription
argument_list|>
name|consumers
init|=
name|regionDestination
operator|.
name|getConsumers
argument_list|()
decl_stmt|;
for|for
control|(
name|Subscription
name|sub
range|:
name|consumers
control|)
block|{
if|if
condition|(
operator|!
name|sub
operator|.
name|getConsumerInfo
argument_list|()
operator|.
name|isNetworkSubscription
argument_list|()
operator|&&
operator|!
name|sub
operator|.
name|getConsumerInfo
argument_list|()
operator|.
name|isBrowser
argument_list|()
condition|)
block|{
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
literal|"Not replaying ["
operator|+
name|message
operator|.
name|getMessageId
argument_list|()
operator|+
literal|"] for ["
operator|+
name|message
operator|.
name|getDestination
argument_list|()
operator|+
literal|"] to origin due to existing local consumer: "
operator|+
name|sub
operator|.
name|getConsumerInfo
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
literal|false
return|;
block|}
block|}
return|return
literal|true
return|;
block|}
specifier|private
name|boolean
name|rateLimitExceeded
parameter_list|()
block|{
if|if
condition|(
name|rateLimit
operator|==
literal|0
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|rateDurationEnd
operator|<
name|System
operator|.
name|currentTimeMillis
argument_list|()
condition|)
block|{
name|rateDurationEnd
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|+
name|rateDuration
expr_stmt|;
name|matchCount
operator|=
literal|0
expr_stmt|;
block|}
return|return
operator|++
name|matchCount
operator|>
name|rateLimit
return|;
block|}
specifier|public
name|void
name|setReplayDelay
parameter_list|(
name|int
name|replayDelay
parameter_list|)
block|{
name|this
operator|.
name|replayDelay
operator|=
name|replayDelay
expr_stmt|;
block|}
specifier|public
name|void
name|setRateLimit
parameter_list|(
name|int
name|rateLimit
parameter_list|)
block|{
name|this
operator|.
name|rateLimit
operator|=
name|rateLimit
expr_stmt|;
block|}
specifier|public
name|void
name|setRateDuration
parameter_list|(
name|int
name|rateDuration
parameter_list|)
block|{
name|this
operator|.
name|rateDuration
operator|=
name|rateDuration
expr_stmt|;
block|}
specifier|public
name|void
name|setAllowReplayWhenNoConsumers
parameter_list|(
name|boolean
name|allowReplayWhenNoConsumers
parameter_list|)
block|{
name|this
operator|.
name|allowReplayWhenNoConsumers
operator|=
name|allowReplayWhenNoConsumers
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

