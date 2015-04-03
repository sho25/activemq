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
name|util
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
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|RedeliveryPolicy
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
name|ScheduledMessage
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
name|BrokerPluginSupport
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
name|MessageReference
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
name|policy
operator|.
name|RedeliveryPolicyMap
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
name|ActiveMQQueue
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
name|ActiveMQTopic
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
name|ProducerInfo
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
name|AnyDestination
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
name|ProducerState
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
comment|/**  * Replace regular DLQ handling with redelivery via a resend to the original destination  * after a delay  * A destination matching RedeliveryPolicy controls the quantity and delay for re-sends  * If there is no matching policy or an existing policy limit is exceeded by default  * regular DLQ processing resumes. This is controlled via sendToDlqIfMaxRetriesExceeded  * and fallbackToDeadLetter  *  * @org.apache.xbean.XBean element="redeliveryPlugin"  */
end_comment

begin_class
specifier|public
class|class
name|RedeliveryPlugin
extends|extends
name|BrokerPluginSupport
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
name|RedeliveryPlugin
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|REDELIVERY_DELAY
init|=
literal|"redeliveryDelay"
decl_stmt|;
name|RedeliveryPolicyMap
name|redeliveryPolicyMap
init|=
operator|new
name|RedeliveryPolicyMap
argument_list|()
decl_stmt|;
name|boolean
name|sendToDlqIfMaxRetriesExceeded
init|=
literal|true
decl_stmt|;
specifier|private
name|boolean
name|fallbackToDeadLetter
init|=
literal|true
decl_stmt|;
annotation|@
name|Override
specifier|public
name|Broker
name|installPlugin
parameter_list|(
name|Broker
name|broker
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
operator|!
name|broker
operator|.
name|getBrokerService
argument_list|()
operator|.
name|isSchedulerSupport
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"RedeliveryPlugin requires schedulerSupport=true on the broker"
argument_list|)
throw|;
block|}
name|validatePolicyDelay
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
return|return
name|super
operator|.
name|installPlugin
argument_list|(
name|broker
argument_list|)
return|;
block|}
comment|/*      * sending to dlq is called as part of a poison ack processing, before the message is acknowledged  and removed      * by the destination so a delay is vital to avoid resending before it has been consumed      */
specifier|private
name|void
name|validatePolicyDelay
parameter_list|(
name|long
name|limit
parameter_list|)
block|{
specifier|final
name|ActiveMQDestination
name|matchAll
init|=
operator|new
name|AnyDestination
argument_list|(
operator|new
name|ActiveMQDestination
index|[]
block|{
operator|new
name|ActiveMQQueue
argument_list|(
literal|">"
argument_list|)
block|,
operator|new
name|ActiveMQTopic
argument_list|(
literal|">"
argument_list|)
block|}
argument_list|)
decl_stmt|;
for|for
control|(
name|Object
name|entry
range|:
name|redeliveryPolicyMap
operator|.
name|get
argument_list|(
name|matchAll
argument_list|)
control|)
block|{
name|RedeliveryPolicy
name|redeliveryPolicy
init|=
operator|(
name|RedeliveryPolicy
operator|)
name|entry
decl_stmt|;
name|validateLimit
argument_list|(
name|limit
argument_list|,
name|redeliveryPolicy
argument_list|)
expr_stmt|;
block|}
name|RedeliveryPolicy
name|defaultEntry
init|=
name|redeliveryPolicyMap
operator|.
name|getDefaultEntry
argument_list|()
decl_stmt|;
if|if
condition|(
name|defaultEntry
operator|!=
literal|null
condition|)
block|{
name|validateLimit
argument_list|(
name|limit
argument_list|,
name|defaultEntry
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|validateLimit
parameter_list|(
name|long
name|limit
parameter_list|,
name|RedeliveryPolicy
name|redeliveryPolicy
parameter_list|)
block|{
if|if
condition|(
name|redeliveryPolicy
operator|.
name|getInitialRedeliveryDelay
argument_list|()
operator|<
name|limit
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"RedeliveryPolicy initialRedeliveryDelay must exceed: "
operator|+
name|limit
operator|+
literal|". "
operator|+
name|redeliveryPolicy
argument_list|)
throw|;
block|}
if|if
condition|(
name|redeliveryPolicy
operator|.
name|getRedeliveryDelay
argument_list|()
operator|<
name|limit
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"RedeliveryPolicy redeliveryDelay must exceed: "
operator|+
name|limit
operator|+
literal|". "
operator|+
name|redeliveryPolicy
argument_list|)
throw|;
block|}
block|}
specifier|public
name|RedeliveryPolicyMap
name|getRedeliveryPolicyMap
parameter_list|()
block|{
return|return
name|redeliveryPolicyMap
return|;
block|}
specifier|public
name|void
name|setRedeliveryPolicyMap
parameter_list|(
name|RedeliveryPolicyMap
name|redeliveryPolicyMap
parameter_list|)
block|{
name|this
operator|.
name|redeliveryPolicyMap
operator|=
name|redeliveryPolicyMap
expr_stmt|;
block|}
specifier|public
name|boolean
name|isSendToDlqIfMaxRetriesExceeded
parameter_list|()
block|{
return|return
name|sendToDlqIfMaxRetriesExceeded
return|;
block|}
comment|/**      * What to do if the maxretries on a matching redelivery policy is exceeded.      * when true, the region broker DLQ processing will be used via sendToDeadLetterQueue      * when false, there is no action      * @param sendToDlqIfMaxRetriesExceeded      */
specifier|public
name|void
name|setSendToDlqIfMaxRetriesExceeded
parameter_list|(
name|boolean
name|sendToDlqIfMaxRetriesExceeded
parameter_list|)
block|{
name|this
operator|.
name|sendToDlqIfMaxRetriesExceeded
operator|=
name|sendToDlqIfMaxRetriesExceeded
expr_stmt|;
block|}
specifier|public
name|boolean
name|isFallbackToDeadLetter
parameter_list|()
block|{
return|return
name|fallbackToDeadLetter
return|;
block|}
comment|/**      * What to do if there is no matching redelivery policy for a destination.      * when true, the region broker DLQ processing will be used via sendToDeadLetterQueue      * when false, there is no action      * @param fallbackToDeadLetter      */
specifier|public
name|void
name|setFallbackToDeadLetter
parameter_list|(
name|boolean
name|fallbackToDeadLetter
parameter_list|)
block|{
name|this
operator|.
name|fallbackToDeadLetter
operator|=
name|fallbackToDeadLetter
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|sendToDeadLetterQueue
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|MessageReference
name|messageReference
parameter_list|,
name|Subscription
name|subscription
parameter_list|,
name|Throwable
name|poisonCause
parameter_list|)
block|{
if|if
condition|(
name|messageReference
operator|.
name|isExpired
argument_list|()
condition|)
block|{
comment|// there are two uses of  sendToDeadLetterQueue, we are only interested in valid messages
return|return
name|super
operator|.
name|sendToDeadLetterQueue
argument_list|(
name|context
argument_list|,
name|messageReference
argument_list|,
name|subscription
argument_list|,
name|poisonCause
argument_list|)
return|;
block|}
else|else
block|{
try|try
block|{
name|Destination
name|regionDestination
init|=
operator|(
name|Destination
operator|)
name|messageReference
operator|.
name|getRegionDestination
argument_list|()
decl_stmt|;
specifier|final
name|RedeliveryPolicy
name|redeliveryPolicy
init|=
name|redeliveryPolicyMap
operator|.
name|getEntryFor
argument_list|(
name|regionDestination
operator|.
name|getActiveMQDestination
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|redeliveryPolicy
operator|!=
literal|null
condition|)
block|{
specifier|final
name|int
name|maximumRedeliveries
init|=
name|redeliveryPolicy
operator|.
name|getMaximumRedeliveries
argument_list|()
decl_stmt|;
name|int
name|redeliveryCount
init|=
name|messageReference
operator|.
name|getRedeliveryCounter
argument_list|()
decl_stmt|;
if|if
condition|(
name|RedeliveryPolicy
operator|.
name|NO_MAXIMUM_REDELIVERIES
operator|==
name|maximumRedeliveries
operator|||
name|redeliveryCount
operator|<
name|maximumRedeliveries
condition|)
block|{
name|long
name|delay
init|=
name|redeliveryPolicy
operator|.
name|getInitialRedeliveryDelay
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|redeliveryCount
condition|;
name|i
operator|++
control|)
block|{
name|delay
operator|=
name|redeliveryPolicy
operator|.
name|getNextRedeliveryDelay
argument_list|(
name|delay
argument_list|)
expr_stmt|;
block|}
name|scheduleRedelivery
argument_list|(
name|context
argument_list|,
name|messageReference
argument_list|,
name|delay
argument_list|,
operator|++
name|redeliveryCount
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|isSendToDlqIfMaxRetriesExceeded
argument_list|()
condition|)
block|{
return|return
name|super
operator|.
name|sendToDeadLetterQueue
argument_list|(
name|context
argument_list|,
name|messageReference
argument_list|,
name|subscription
argument_list|,
name|poisonCause
argument_list|)
return|;
block|}
else|else
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Discarding message that exceeds max redelivery count({}), {}"
argument_list|,
name|maximumRedeliveries
argument_list|,
name|messageReference
operator|.
name|getMessageId
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|isFallbackToDeadLetter
argument_list|()
condition|)
block|{
return|return
name|super
operator|.
name|sendToDeadLetterQueue
argument_list|(
name|context
argument_list|,
name|messageReference
argument_list|,
name|subscription
argument_list|,
name|poisonCause
argument_list|)
return|;
block|}
else|else
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Ignoring dlq request for: {}, RedeliveryPolicy not found (and no fallback) for: {}"
argument_list|,
name|messageReference
operator|.
name|getMessageId
argument_list|()
argument_list|,
name|regionDestination
operator|.
name|getActiveMQDestination
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
literal|false
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|exception
parameter_list|)
block|{
comment|// abort the ack, will be effective if client use transactions or individual ack with sync send
name|RuntimeException
name|toThrow
init|=
operator|new
name|RuntimeException
argument_list|(
literal|"Failed to schedule redelivery for: "
operator|+
name|messageReference
operator|.
name|getMessageId
argument_list|()
argument_list|,
name|exception
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|error
argument_list|(
name|toThrow
operator|.
name|toString
argument_list|()
argument_list|,
name|exception
argument_list|)
expr_stmt|;
throw|throw
name|toThrow
throw|;
block|}
block|}
block|}
specifier|private
name|void
name|scheduleRedelivery
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|MessageReference
name|messageReference
parameter_list|,
name|long
name|delay
parameter_list|,
name|int
name|redeliveryCount
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|LOG
operator|.
name|isTraceEnabled
argument_list|()
condition|)
block|{
name|Destination
name|regionDestination
init|=
operator|(
name|Destination
operator|)
name|messageReference
operator|.
name|getRegionDestination
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|trace
argument_list|(
literal|"redelivery #{} of: {} with delay: {}, dest: {}"
argument_list|,
operator|new
name|Object
index|[]
block|{
name|redeliveryCount
block|,
name|messageReference
operator|.
name|getMessageId
argument_list|()
block|,
name|delay
block|,
name|regionDestination
operator|.
name|getActiveMQDestination
argument_list|()
block|}
argument_list|)
expr_stmt|;
block|}
specifier|final
name|Message
name|old
init|=
name|messageReference
operator|.
name|getMessage
argument_list|()
decl_stmt|;
name|Message
name|message
init|=
name|old
operator|.
name|copy
argument_list|()
decl_stmt|;
name|message
operator|.
name|setTransactionId
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|message
operator|.
name|setMemoryUsage
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|message
operator|.
name|removeProperty
argument_list|(
name|ScheduledMessage
operator|.
name|AMQ_SCHEDULED_ID
argument_list|)
expr_stmt|;
name|message
operator|.
name|setProperty
argument_list|(
name|REDELIVERY_DELAY
argument_list|,
name|delay
argument_list|)
expr_stmt|;
name|message
operator|.
name|setProperty
argument_list|(
name|ScheduledMessage
operator|.
name|AMQ_SCHEDULED_DELAY
argument_list|,
name|delay
argument_list|)
expr_stmt|;
name|message
operator|.
name|setRedeliveryCounter
argument_list|(
name|redeliveryCount
argument_list|)
expr_stmt|;
name|boolean
name|originalFlowControl
init|=
name|context
operator|.
name|isProducerFlowControl
argument_list|()
decl_stmt|;
try|try
block|{
name|context
operator|.
name|setProducerFlowControl
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|ProducerInfo
name|info
init|=
operator|new
name|ProducerInfo
argument_list|()
decl_stmt|;
name|ProducerState
name|state
init|=
operator|new
name|ProducerState
argument_list|(
name|info
argument_list|)
decl_stmt|;
name|ProducerBrokerExchange
name|producerExchange
init|=
operator|new
name|ProducerBrokerExchange
argument_list|()
decl_stmt|;
name|producerExchange
operator|.
name|setProducerState
argument_list|(
name|state
argument_list|)
expr_stmt|;
name|producerExchange
operator|.
name|setMutable
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|producerExchange
operator|.
name|setConnectionContext
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|context
operator|.
name|getBroker
argument_list|()
operator|.
name|send
argument_list|(
name|producerExchange
argument_list|,
name|message
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|context
operator|.
name|setProducerFlowControl
argument_list|(
name|originalFlowControl
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

