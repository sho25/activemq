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
name|atomic
operator|.
name|AtomicBoolean
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
name|Connection
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
name|apache
operator|.
name|activemq
operator|.
name|command
operator|.
name|ConsumerControl
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
name|RemoveInfo
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
name|state
operator|.
name|CommandVisitor
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
name|thread
operator|.
name|Scheduler
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
name|InactivityIOException
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
comment|/**  * Abort slow consumers when they reach the configured threshold of slowness, default is slow for 30 seconds  *  * @org.apache.xbean.XBean  */
end_comment

begin_class
specifier|public
class|class
name|AbortSlowConsumerStrategy
implements|implements
name|SlowConsumerStrategy
implements|,
name|Runnable
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
name|AbortSlowConsumerStrategy
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|protected
name|String
name|name
init|=
literal|"AbortSlowConsumerStrategy@"
operator|+
name|hashCode
argument_list|()
decl_stmt|;
specifier|protected
name|Scheduler
name|scheduler
decl_stmt|;
specifier|protected
name|Broker
name|broker
decl_stmt|;
specifier|protected
specifier|final
name|AtomicBoolean
name|taskStarted
init|=
operator|new
name|AtomicBoolean
argument_list|(
literal|false
argument_list|)
decl_stmt|;
specifier|protected
specifier|final
name|Map
argument_list|<
name|Subscription
argument_list|,
name|SlowConsumerEntry
argument_list|>
name|slowConsumers
init|=
operator|new
name|ConcurrentHashMap
argument_list|<
name|Subscription
argument_list|,
name|SlowConsumerEntry
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
name|long
name|maxSlowCount
init|=
operator|-
literal|1
decl_stmt|;
specifier|private
name|long
name|maxSlowDuration
init|=
literal|30
operator|*
literal|1000
decl_stmt|;
specifier|private
name|long
name|checkPeriod
init|=
literal|30
operator|*
literal|1000
decl_stmt|;
specifier|private
name|boolean
name|abortConnection
init|=
literal|false
decl_stmt|;
specifier|private
name|boolean
name|ignoreNetworkConsumers
init|=
literal|true
decl_stmt|;
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
name|this
operator|.
name|scheduler
operator|=
name|broker
operator|.
name|getScheduler
argument_list|()
expr_stmt|;
name|this
operator|.
name|broker
operator|=
name|broker
expr_stmt|;
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
if|if
condition|(
name|maxSlowCount
operator|<
literal|0
operator|&&
name|maxSlowDuration
operator|<
literal|0
condition|)
block|{
comment|// nothing to do
name|LOG
operator|.
name|info
argument_list|(
literal|"no limits set, slowConsumer strategy has nothing to do"
argument_list|)
expr_stmt|;
return|return;
block|}
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
name|checkPeriod
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|slowConsumers
operator|.
name|containsKey
argument_list|(
name|subs
argument_list|)
condition|)
block|{
name|slowConsumers
operator|.
name|put
argument_list|(
name|subs
argument_list|,
operator|new
name|SlowConsumerEntry
argument_list|(
name|context
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|maxSlowCount
operator|>
literal|0
condition|)
block|{
name|slowConsumers
operator|.
name|get
argument_list|(
name|subs
argument_list|)
operator|.
name|slow
argument_list|()
expr_stmt|;
block|}
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
name|maxSlowDuration
operator|>
literal|0
condition|)
block|{
comment|// mark
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
name|Subscription
name|subscription
init|=
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
if|if
condition|(
name|isIgnoreNetworkSubscriptions
argument_list|()
operator|&&
name|subscription
operator|.
name|getConsumerInfo
argument_list|()
operator|.
name|isNetworkSubscription
argument_list|()
condition|)
block|{
if|if
condition|(
name|slowConsumers
operator|.
name|remove
argument_list|(
name|subscription
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"network sub: {} is no longer slow"
argument_list|,
name|subscription
operator|.
name|getConsumerInfo
argument_list|()
operator|.
name|getConsumerId
argument_list|()
argument_list|)
expr_stmt|;
block|}
continue|continue;
block|}
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
name|maxSlowDuration
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
name|checkPeriod
operator|>=
name|maxSlowDuration
operator|)
operator|||
name|maxSlowCount
operator|>
literal|0
operator|&&
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|slowCount
operator|>=
name|maxSlowCount
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
else|else
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"sub: "
operator|+
name|entry
operator|.
name|getKey
argument_list|()
operator|.
name|getConsumerInfo
argument_list|()
operator|.
name|getConsumerId
argument_list|()
operator|+
literal|" is no longer slow"
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
name|abortSubscription
argument_list|(
name|toAbort
argument_list|,
name|abortConnection
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|abortSubscription
parameter_list|(
name|Map
argument_list|<
name|Subscription
argument_list|,
name|SlowConsumerEntry
argument_list|>
name|toAbort
parameter_list|,
name|boolean
name|abortSubscriberConnection
parameter_list|)
block|{
name|Map
argument_list|<
name|Connection
argument_list|,
name|List
argument_list|<
name|Subscription
argument_list|>
argument_list|>
name|abortMap
init|=
operator|new
name|HashMap
argument_list|<
name|Connection
argument_list|,
name|List
argument_list|<
name|Subscription
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
specifier|final
name|Entry
argument_list|<
name|Subscription
argument_list|,
name|SlowConsumerEntry
argument_list|>
name|entry
range|:
name|toAbort
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|ConnectionContext
name|connectionContext
init|=
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|context
decl_stmt|;
if|if
condition|(
name|connectionContext
operator|==
literal|null
condition|)
block|{
continue|continue;
block|}
name|Connection
name|connection
init|=
name|connectionContext
operator|.
name|getConnection
argument_list|()
decl_stmt|;
if|if
condition|(
name|connection
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"slowConsumer abort ignored, no connection in context:"
operator|+
name|connectionContext
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|abortMap
operator|.
name|containsKey
argument_list|(
name|connection
argument_list|)
condition|)
block|{
name|abortMap
operator|.
name|put
argument_list|(
name|connection
argument_list|,
operator|new
name|ArrayList
argument_list|<
name|Subscription
argument_list|>
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|abortMap
operator|.
name|get
argument_list|(
name|connection
argument_list|)
operator|.
name|add
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|Entry
argument_list|<
name|Connection
argument_list|,
name|List
argument_list|<
name|Subscription
argument_list|>
argument_list|>
name|entry
range|:
name|abortMap
operator|.
name|entrySet
argument_list|()
control|)
block|{
specifier|final
name|Connection
name|connection
init|=
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|Subscription
argument_list|>
name|subscriptions
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|abortSubscriberConnection
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"aborting connection:{} with {} slow consumers"
argument_list|,
name|connection
operator|.
name|getConnectionId
argument_list|()
argument_list|,
name|subscriptions
operator|.
name|size
argument_list|()
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
for|for
control|(
name|Subscription
name|subscription
range|:
name|subscriptions
control|)
block|{
name|LOG
operator|.
name|trace
argument_list|(
literal|"Connection {} being aborted because of slow consumer: {} on destination: {}"
argument_list|,
operator|new
name|Object
index|[]
block|{
name|connection
operator|.
name|getConnectionId
argument_list|()
block|,
name|subscription
operator|.
name|getConsumerInfo
argument_list|()
operator|.
name|getConsumerId
argument_list|()
block|,
name|subscription
operator|.
name|getActiveMQDestination
argument_list|()
block|}
argument_list|)
expr_stmt|;
block|}
block|}
try|try
block|{
name|scheduler
operator|.
name|executeAfterDelay
argument_list|(
operator|new
name|Runnable
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
name|connection
operator|.
name|serviceException
argument_list|(
operator|new
name|InactivityIOException
argument_list|(
name|subscriptions
operator|.
name|size
argument_list|()
operator|+
literal|" Consumers was slow too often (>"
operator|+
name|maxSlowCount
operator|+
literal|") or too long (>"
operator|+
name|maxSlowDuration
operator|+
literal|"): "
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|,
literal|0l
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
name|info
argument_list|(
literal|"exception on aborting connection {} with {} slow consumers"
argument_list|,
name|connection
operator|.
name|getConnectionId
argument_list|()
argument_list|,
name|subscriptions
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// just abort each consumer
for|for
control|(
name|Subscription
name|subscription
range|:
name|subscriptions
control|)
block|{
specifier|final
name|Subscription
name|subToClose
init|=
name|subscription
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"aborting slow consumer: {} for destination:{}"
argument_list|,
name|subscription
operator|.
name|getConsumerInfo
argument_list|()
operator|.
name|getConsumerId
argument_list|()
argument_list|,
name|subscription
operator|.
name|getActiveMQDestination
argument_list|()
argument_list|)
expr_stmt|;
comment|// tell the remote consumer to close
try|try
block|{
name|ConsumerControl
name|stopConsumer
init|=
operator|new
name|ConsumerControl
argument_list|()
decl_stmt|;
name|stopConsumer
operator|.
name|setConsumerId
argument_list|(
name|subscription
operator|.
name|getConsumerInfo
argument_list|()
operator|.
name|getConsumerId
argument_list|()
argument_list|)
expr_stmt|;
name|stopConsumer
operator|.
name|setClose
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|connection
operator|.
name|dispatchAsync
argument_list|(
name|stopConsumer
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
name|info
argument_list|(
literal|"exception on aborting slow consumer: {}"
argument_list|,
name|subscription
operator|.
name|getConsumerInfo
argument_list|()
operator|.
name|getConsumerId
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
comment|// force a local remove in case remote is unresponsive
try|try
block|{
name|scheduler
operator|.
name|executeAfterDelay
argument_list|(
operator|new
name|Runnable
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|RemoveInfo
name|removeCommand
init|=
name|subToClose
operator|.
name|getConsumerInfo
argument_list|()
operator|.
name|createRemoveCommand
argument_list|()
decl_stmt|;
if|if
condition|(
name|connection
operator|instanceof
name|CommandVisitor
condition|)
block|{
comment|// avoid service exception handling and logging
name|removeCommand
operator|.
name|visit
argument_list|(
operator|(
name|CommandVisitor
operator|)
name|connection
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|connection
operator|.
name|service
argument_list|(
name|removeCommand
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IllegalStateException
name|ignoredAsRemoteHasDoneTheJob
parameter_list|)
block|{                                 }
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"exception on local remove of slow consumer: {}"
argument_list|,
name|subToClose
operator|.
name|getConsumerInfo
argument_list|()
operator|.
name|getConsumerId
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|,
literal|1000l
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
name|info
argument_list|(
literal|"exception on local remove of slow consumer: {}"
argument_list|,
name|subscription
operator|.
name|getConsumerInfo
argument_list|()
operator|.
name|getConsumerId
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
specifier|public
name|void
name|abortConsumer
parameter_list|(
name|Subscription
name|sub
parameter_list|,
name|boolean
name|abortSubscriberConnection
parameter_list|)
block|{
if|if
condition|(
name|sub
operator|!=
literal|null
condition|)
block|{
name|SlowConsumerEntry
name|entry
init|=
name|slowConsumers
operator|.
name|remove
argument_list|(
name|sub
argument_list|)
decl_stmt|;
if|if
condition|(
name|entry
operator|!=
literal|null
condition|)
block|{
name|Map
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
name|toAbort
operator|.
name|put
argument_list|(
name|sub
argument_list|,
name|entry
argument_list|)
expr_stmt|;
name|abortSubscription
argument_list|(
name|toAbort
argument_list|,
name|abortSubscriberConnection
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"cannot abort subscription as it no longer exists in the map of slow consumers: "
operator|+
name|sub
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|public
name|long
name|getMaxSlowCount
parameter_list|()
block|{
return|return
name|maxSlowCount
return|;
block|}
comment|/**      * number of times a subscription can be deemed slow before triggering abort      * effect depends on dispatch rate as slow determination is done on dispatch      */
specifier|public
name|void
name|setMaxSlowCount
parameter_list|(
name|long
name|maxSlowCount
parameter_list|)
block|{
name|this
operator|.
name|maxSlowCount
operator|=
name|maxSlowCount
expr_stmt|;
block|}
specifier|public
name|long
name|getMaxSlowDuration
parameter_list|()
block|{
return|return
name|maxSlowDuration
return|;
block|}
comment|/**      * time in milliseconds that a sub can remain slow before triggering      * an abort.      * @param maxSlowDuration      */
specifier|public
name|void
name|setMaxSlowDuration
parameter_list|(
name|long
name|maxSlowDuration
parameter_list|)
block|{
name|this
operator|.
name|maxSlowDuration
operator|=
name|maxSlowDuration
expr_stmt|;
block|}
specifier|public
name|long
name|getCheckPeriod
parameter_list|()
block|{
return|return
name|checkPeriod
return|;
block|}
comment|/**      * time in milliseconds between checks for slow subscriptions      * @param checkPeriod      */
specifier|public
name|void
name|setCheckPeriod
parameter_list|(
name|long
name|checkPeriod
parameter_list|)
block|{
name|this
operator|.
name|checkPeriod
operator|=
name|checkPeriod
expr_stmt|;
block|}
specifier|public
name|boolean
name|isAbortConnection
parameter_list|()
block|{
return|return
name|abortConnection
return|;
block|}
comment|/**      * abort the consumers connection rather than sending a stop command to the remote consumer      * @param abortConnection      */
specifier|public
name|void
name|setAbortConnection
parameter_list|(
name|boolean
name|abortConnection
parameter_list|)
block|{
name|this
operator|.
name|abortConnection
operator|=
name|abortConnection
expr_stmt|;
block|}
comment|/**      * Returns whether the strategy is configured to ignore subscriptions that are from a network      * connection.      *      * @return true if the strategy will ignore network connection subscriptions when looking      *         for slow consumers.      */
specifier|public
name|boolean
name|isIgnoreNetworkSubscriptions
parameter_list|()
block|{
return|return
name|ignoreNetworkConsumers
return|;
block|}
comment|/**      * Sets whether the strategy is configured to ignore consumers that are part of a network      * connection to another broker.      *      * When configured to not ignore idle consumers this strategy acts not only on consumers      * that are actually slow but also on any consumer that has not received any messages for      * the maxTimeSinceLastAck.  This allows for a way to evict idle consumers while also      * aborting slow consumers however for a network subscription this can create a lot of      * unnecessary churn and if the abort connection option is also enabled this can result      * in the entire network connection being torn down and rebuilt for no reason.      *      * @param ignoreNetworkConsumers      *      Should this strategy ignore subscriptions made by a network connector.      */
specifier|public
name|void
name|setIgnoreNetworkConsumers
parameter_list|(
name|boolean
name|ignoreNetworkConsumers
parameter_list|)
block|{
name|this
operator|.
name|ignoreNetworkConsumers
operator|=
name|ignoreNetworkConsumers
expr_stmt|;
block|}
specifier|public
name|void
name|setName
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
block|}
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|name
return|;
block|}
specifier|public
name|Map
argument_list|<
name|Subscription
argument_list|,
name|SlowConsumerEntry
argument_list|>
name|getSlowConsumers
parameter_list|()
block|{
return|return
name|slowConsumers
return|;
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
comment|// Not needed for this strategy.
block|}
block|}
end_class

end_unit

