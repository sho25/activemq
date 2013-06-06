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
name|scheduler
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
name|advisory
operator|.
name|AdvisorySupport
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
name|BrokerFilter
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
name|BrokerService
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
name|command
operator|.
name|MessageId
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
name|ProducerId
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
name|openwire
operator|.
name|OpenWireFormat
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
name|security
operator|.
name|SecurityContext
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
name|apache
operator|.
name|activemq
operator|.
name|transaction
operator|.
name|Synchronization
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
name|usage
operator|.
name|JobSchedulerUsage
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
name|usage
operator|.
name|SystemUsage
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
name|ByteSequence
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
name|IdGenerator
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
name|LongSequenceGenerator
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
name|TypeConversionSupport
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
name|wireformat
operator|.
name|WireFormat
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
name|SchedulerBroker
extends|extends
name|BrokerFilter
implements|implements
name|JobListener
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
name|SchedulerBroker
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|IdGenerator
name|ID_GENERATOR
init|=
operator|new
name|IdGenerator
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|LongSequenceGenerator
name|messageIdGenerator
init|=
operator|new
name|LongSequenceGenerator
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|AtomicBoolean
name|started
init|=
operator|new
name|AtomicBoolean
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|WireFormat
name|wireFormat
init|=
operator|new
name|OpenWireFormat
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|ConnectionContext
name|context
init|=
operator|new
name|ConnectionContext
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|ProducerId
name|producerId
init|=
operator|new
name|ProducerId
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|SystemUsage
name|systemUsage
decl_stmt|;
specifier|private
specifier|final
name|JobSchedulerStore
name|store
decl_stmt|;
specifier|private
name|JobScheduler
name|scheduler
decl_stmt|;
specifier|public
name|SchedulerBroker
parameter_list|(
name|BrokerService
name|brokerService
parameter_list|,
name|Broker
name|next
parameter_list|,
name|JobSchedulerStore
name|store
parameter_list|)
throws|throws
name|Exception
block|{
name|super
argument_list|(
name|next
argument_list|)
expr_stmt|;
name|this
operator|.
name|store
operator|=
name|store
expr_stmt|;
name|this
operator|.
name|producerId
operator|.
name|setConnectionId
argument_list|(
name|ID_GENERATOR
operator|.
name|generateId
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|context
operator|.
name|setSecurityContext
argument_list|(
name|SecurityContext
operator|.
name|BROKER_SECURITY_CONTEXT
argument_list|)
expr_stmt|;
name|this
operator|.
name|context
operator|.
name|setBroker
argument_list|(
name|next
argument_list|)
expr_stmt|;
name|this
operator|.
name|systemUsage
operator|=
name|brokerService
operator|.
name|getSystemUsage
argument_list|()
expr_stmt|;
name|wireFormat
operator|.
name|setVersion
argument_list|(
name|brokerService
operator|.
name|getStoreOpenWireVersion
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|synchronized
name|JobScheduler
name|getJobScheduler
parameter_list|()
throws|throws
name|Exception
block|{
return|return
operator|new
name|JobSchedulerFacade
argument_list|(
name|this
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|start
parameter_list|()
throws|throws
name|Exception
block|{
name|this
operator|.
name|started
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|getInternalScheduler
argument_list|()
expr_stmt|;
name|super
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|stop
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|this
operator|.
name|started
operator|.
name|compareAndSet
argument_list|(
literal|true
argument_list|,
literal|false
argument_list|)
condition|)
block|{
if|if
condition|(
name|this
operator|.
name|store
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|store
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|this
operator|.
name|scheduler
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|scheduler
operator|.
name|removeListener
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|this
operator|.
name|scheduler
operator|=
literal|null
expr_stmt|;
block|}
block|}
name|super
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|send
parameter_list|(
name|ProducerBrokerExchange
name|producerExchange
parameter_list|,
specifier|final
name|Message
name|messageSend
parameter_list|)
throws|throws
name|Exception
block|{
name|ConnectionContext
name|context
init|=
name|producerExchange
operator|.
name|getConnectionContext
argument_list|()
decl_stmt|;
specifier|final
name|String
name|jobId
init|=
operator|(
name|String
operator|)
name|messageSend
operator|.
name|getProperty
argument_list|(
name|ScheduledMessage
operator|.
name|AMQ_SCHEDULED_ID
argument_list|)
decl_stmt|;
specifier|final
name|Object
name|cronValue
init|=
name|messageSend
operator|.
name|getProperty
argument_list|(
name|ScheduledMessage
operator|.
name|AMQ_SCHEDULED_CRON
argument_list|)
decl_stmt|;
specifier|final
name|Object
name|periodValue
init|=
name|messageSend
operator|.
name|getProperty
argument_list|(
name|ScheduledMessage
operator|.
name|AMQ_SCHEDULED_PERIOD
argument_list|)
decl_stmt|;
specifier|final
name|Object
name|delayValue
init|=
name|messageSend
operator|.
name|getProperty
argument_list|(
name|ScheduledMessage
operator|.
name|AMQ_SCHEDULED_DELAY
argument_list|)
decl_stmt|;
name|String
name|physicalName
init|=
name|messageSend
operator|.
name|getDestination
argument_list|()
operator|.
name|getPhysicalName
argument_list|()
decl_stmt|;
name|boolean
name|schedularManage
init|=
name|physicalName
operator|.
name|regionMatches
argument_list|(
literal|true
argument_list|,
literal|0
argument_list|,
name|ScheduledMessage
operator|.
name|AMQ_SCHEDULER_MANAGEMENT_DESTINATION
argument_list|,
literal|0
argument_list|,
name|ScheduledMessage
operator|.
name|AMQ_SCHEDULER_MANAGEMENT_DESTINATION
operator|.
name|length
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|schedularManage
operator|==
literal|true
condition|)
block|{
name|JobScheduler
name|scheduler
init|=
name|getInternalScheduler
argument_list|()
decl_stmt|;
name|ActiveMQDestination
name|replyTo
init|=
name|messageSend
operator|.
name|getReplyTo
argument_list|()
decl_stmt|;
name|String
name|action
init|=
operator|(
name|String
operator|)
name|messageSend
operator|.
name|getProperty
argument_list|(
name|ScheduledMessage
operator|.
name|AMQ_SCHEDULER_ACTION
argument_list|)
decl_stmt|;
if|if
condition|(
name|action
operator|!=
literal|null
condition|)
block|{
name|Object
name|startTime
init|=
name|messageSend
operator|.
name|getProperty
argument_list|(
name|ScheduledMessage
operator|.
name|AMQ_SCHEDULER_ACTION_START_TIME
argument_list|)
decl_stmt|;
name|Object
name|endTime
init|=
name|messageSend
operator|.
name|getProperty
argument_list|(
name|ScheduledMessage
operator|.
name|AMQ_SCHEDULER_ACTION_END_TIME
argument_list|)
decl_stmt|;
if|if
condition|(
name|replyTo
operator|!=
literal|null
operator|&&
name|action
operator|.
name|equals
argument_list|(
name|ScheduledMessage
operator|.
name|AMQ_SCHEDULER_ACTION_BROWSE
argument_list|)
condition|)
block|{
if|if
condition|(
name|startTime
operator|!=
literal|null
operator|&&
name|endTime
operator|!=
literal|null
condition|)
block|{
name|long
name|start
init|=
operator|(
name|Long
operator|)
name|TypeConversionSupport
operator|.
name|convert
argument_list|(
name|startTime
argument_list|,
name|Long
operator|.
name|class
argument_list|)
decl_stmt|;
name|long
name|finish
init|=
operator|(
name|Long
operator|)
name|TypeConversionSupport
operator|.
name|convert
argument_list|(
name|endTime
argument_list|,
name|Long
operator|.
name|class
argument_list|)
decl_stmt|;
for|for
control|(
name|Job
name|job
range|:
name|scheduler
operator|.
name|getAllJobs
argument_list|(
name|start
argument_list|,
name|finish
argument_list|)
control|)
block|{
name|sendScheduledJob
argument_list|(
name|producerExchange
operator|.
name|getConnectionContext
argument_list|()
argument_list|,
name|job
argument_list|,
name|replyTo
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
for|for
control|(
name|Job
name|job
range|:
name|scheduler
operator|.
name|getAllJobs
argument_list|()
control|)
block|{
name|sendScheduledJob
argument_list|(
name|producerExchange
operator|.
name|getConnectionContext
argument_list|()
argument_list|,
name|job
argument_list|,
name|replyTo
argument_list|)
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|jobId
operator|!=
literal|null
operator|&&
name|action
operator|.
name|equals
argument_list|(
name|ScheduledMessage
operator|.
name|AMQ_SCHEDULER_ACTION_REMOVE
argument_list|)
condition|)
block|{
name|scheduler
operator|.
name|remove
argument_list|(
name|jobId
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|action
operator|.
name|equals
argument_list|(
name|ScheduledMessage
operator|.
name|AMQ_SCHEDULER_ACTION_REMOVEALL
argument_list|)
condition|)
block|{
if|if
condition|(
name|startTime
operator|!=
literal|null
operator|&&
name|endTime
operator|!=
literal|null
condition|)
block|{
name|long
name|start
init|=
operator|(
name|Long
operator|)
name|TypeConversionSupport
operator|.
name|convert
argument_list|(
name|startTime
argument_list|,
name|Long
operator|.
name|class
argument_list|)
decl_stmt|;
name|long
name|finish
init|=
operator|(
name|Long
operator|)
name|TypeConversionSupport
operator|.
name|convert
argument_list|(
name|endTime
argument_list|,
name|Long
operator|.
name|class
argument_list|)
decl_stmt|;
name|scheduler
operator|.
name|removeAllJobs
argument_list|(
name|start
argument_list|,
name|finish
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|scheduler
operator|.
name|removeAllJobs
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
elseif|else
if|if
condition|(
operator|(
name|cronValue
operator|!=
literal|null
operator|||
name|periodValue
operator|!=
literal|null
operator|||
name|delayValue
operator|!=
literal|null
operator|)
operator|&&
name|jobId
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|context
operator|.
name|isInTransaction
argument_list|()
condition|)
block|{
name|context
operator|.
name|getTransaction
argument_list|()
operator|.
name|addSynchronization
argument_list|(
operator|new
name|Synchronization
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|afterCommit
parameter_list|()
throws|throws
name|Exception
block|{
name|doSchedule
argument_list|(
name|messageSend
argument_list|,
name|cronValue
argument_list|,
name|periodValue
argument_list|,
name|delayValue
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|doSchedule
argument_list|(
name|messageSend
argument_list|,
name|cronValue
argument_list|,
name|periodValue
argument_list|,
name|delayValue
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|super
operator|.
name|send
argument_list|(
name|producerExchange
argument_list|,
name|messageSend
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|doSchedule
parameter_list|(
name|Message
name|messageSend
parameter_list|,
name|Object
name|cronValue
parameter_list|,
name|Object
name|periodValue
parameter_list|,
name|Object
name|delayValue
parameter_list|)
throws|throws
name|Exception
block|{
name|long
name|delay
init|=
literal|0
decl_stmt|;
name|long
name|period
init|=
literal|0
decl_stmt|;
name|int
name|repeat
init|=
literal|0
decl_stmt|;
name|String
name|cronEntry
init|=
literal|""
decl_stmt|;
comment|// clear transaction context
name|Message
name|msg
init|=
name|messageSend
operator|.
name|copy
argument_list|()
decl_stmt|;
name|msg
operator|.
name|setTransactionId
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|util
operator|.
name|ByteSequence
name|packet
init|=
name|wireFormat
operator|.
name|marshal
argument_list|(
name|msg
argument_list|)
decl_stmt|;
if|if
condition|(
name|cronValue
operator|!=
literal|null
condition|)
block|{
name|cronEntry
operator|=
name|cronValue
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|periodValue
operator|!=
literal|null
condition|)
block|{
name|period
operator|=
operator|(
name|Long
operator|)
name|TypeConversionSupport
operator|.
name|convert
argument_list|(
name|periodValue
argument_list|,
name|Long
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|delayValue
operator|!=
literal|null
condition|)
block|{
name|delay
operator|=
operator|(
name|Long
operator|)
name|TypeConversionSupport
operator|.
name|convert
argument_list|(
name|delayValue
argument_list|,
name|Long
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
name|Object
name|repeatValue
init|=
name|msg
operator|.
name|getProperty
argument_list|(
name|ScheduledMessage
operator|.
name|AMQ_SCHEDULED_REPEAT
argument_list|)
decl_stmt|;
if|if
condition|(
name|repeatValue
operator|!=
literal|null
condition|)
block|{
name|repeat
operator|=
operator|(
name|Integer
operator|)
name|TypeConversionSupport
operator|.
name|convert
argument_list|(
name|repeatValue
argument_list|,
name|Integer
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
name|getInternalScheduler
argument_list|()
operator|.
name|schedule
argument_list|(
name|msg
operator|.
name|getMessageId
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
operator|new
name|ByteSequence
argument_list|(
name|packet
operator|.
name|data
argument_list|,
name|packet
operator|.
name|offset
argument_list|,
name|packet
operator|.
name|length
argument_list|)
argument_list|,
name|cronEntry
argument_list|,
name|delay
argument_list|,
name|period
argument_list|,
name|repeat
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|scheduledJob
parameter_list|(
name|String
name|id
parameter_list|,
name|ByteSequence
name|job
parameter_list|)
block|{
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|util
operator|.
name|ByteSequence
name|packet
init|=
operator|new
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|util
operator|.
name|ByteSequence
argument_list|(
name|job
operator|.
name|getData
argument_list|()
argument_list|,
name|job
operator|.
name|getOffset
argument_list|()
argument_list|,
name|job
operator|.
name|getLength
argument_list|()
argument_list|)
decl_stmt|;
try|try
block|{
name|Message
name|messageSend
init|=
operator|(
name|Message
operator|)
name|this
operator|.
name|wireFormat
operator|.
name|unmarshal
argument_list|(
name|packet
argument_list|)
decl_stmt|;
name|messageSend
operator|.
name|setOriginalTransactionId
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|Object
name|repeatValue
init|=
name|messageSend
operator|.
name|getProperty
argument_list|(
name|ScheduledMessage
operator|.
name|AMQ_SCHEDULED_REPEAT
argument_list|)
decl_stmt|;
name|Object
name|cronValue
init|=
name|messageSend
operator|.
name|getProperty
argument_list|(
name|ScheduledMessage
operator|.
name|AMQ_SCHEDULED_CRON
argument_list|)
decl_stmt|;
name|String
name|cronStr
init|=
name|cronValue
operator|!=
literal|null
condition|?
name|cronValue
operator|.
name|toString
argument_list|()
else|:
literal|null
decl_stmt|;
name|int
name|repeat
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|repeatValue
operator|!=
literal|null
condition|)
block|{
name|repeat
operator|=
operator|(
name|Integer
operator|)
name|TypeConversionSupport
operator|.
name|convert
argument_list|(
name|repeatValue
argument_list|,
name|Integer
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
comment|// Check for room in the job scheduler store
if|if
condition|(
name|systemUsage
operator|.
name|getJobSchedulerUsage
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|JobSchedulerUsage
name|usage
init|=
name|systemUsage
operator|.
name|getJobSchedulerUsage
argument_list|()
decl_stmt|;
if|if
condition|(
name|usage
operator|.
name|isFull
argument_list|()
condition|)
block|{
specifier|final
name|String
name|logMessage
init|=
literal|"Job Scheduler Store is Full ("
operator|+
name|usage
operator|.
name|getPercentUsage
argument_list|()
operator|+
literal|"% of "
operator|+
name|usage
operator|.
name|getLimit
argument_list|()
operator|+
literal|"). Stopping producer ("
operator|+
name|messageSend
operator|.
name|getProducerId
argument_list|()
operator|+
literal|") to prevent flooding of the job scheduler store."
operator|+
literal|" See http://activemq.apache.org/producer-flow-control.html for more info"
decl_stmt|;
name|long
name|start
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|long
name|nextWarn
init|=
name|start
decl_stmt|;
while|while
condition|(
operator|!
name|usage
operator|.
name|waitForSpace
argument_list|(
literal|1000
argument_list|)
condition|)
block|{
if|if
condition|(
name|context
operator|.
name|getStopping
argument_list|()
operator|.
name|get
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Connection closed, send aborted."
argument_list|)
throw|;
block|}
name|long
name|now
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
if|if
condition|(
name|now
operator|>=
name|nextWarn
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|""
operator|+
name|usage
operator|+
literal|": "
operator|+
name|logMessage
operator|+
literal|" (blocking for: "
operator|+
operator|(
name|now
operator|-
name|start
operator|)
operator|/
literal|1000
operator|+
literal|"s)"
argument_list|)
expr_stmt|;
name|nextWarn
operator|=
name|now
operator|+
literal|30000l
expr_stmt|;
block|}
block|}
block|}
block|}
if|if
condition|(
name|repeat
operator|!=
literal|0
operator|||
name|cronStr
operator|!=
literal|null
operator|&&
name|cronStr
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
comment|// create a unique id - the original message could be sent
comment|// lots of times
name|messageSend
operator|.
name|setMessageId
argument_list|(
operator|new
name|MessageId
argument_list|(
name|this
operator|.
name|producerId
argument_list|,
name|this
operator|.
name|messageIdGenerator
operator|.
name|getNextSequenceId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// Add the jobId as a property
name|messageSend
operator|.
name|setProperty
argument_list|(
literal|"scheduledJobId"
argument_list|,
name|id
argument_list|)
expr_stmt|;
comment|// if this goes across a network - we don't want it rescheduled
name|messageSend
operator|.
name|removeProperty
argument_list|(
name|ScheduledMessage
operator|.
name|AMQ_SCHEDULED_PERIOD
argument_list|)
expr_stmt|;
name|messageSend
operator|.
name|removeProperty
argument_list|(
name|ScheduledMessage
operator|.
name|AMQ_SCHEDULED_DELAY
argument_list|)
expr_stmt|;
name|messageSend
operator|.
name|removeProperty
argument_list|(
name|ScheduledMessage
operator|.
name|AMQ_SCHEDULED_REPEAT
argument_list|)
expr_stmt|;
name|messageSend
operator|.
name|removeProperty
argument_list|(
name|ScheduledMessage
operator|.
name|AMQ_SCHEDULED_CRON
argument_list|)
expr_stmt|;
if|if
condition|(
name|messageSend
operator|.
name|getTimestamp
argument_list|()
operator|>
literal|0
operator|&&
name|messageSend
operator|.
name|getExpiration
argument_list|()
operator|>
literal|0
condition|)
block|{
name|long
name|oldExpiration
init|=
name|messageSend
operator|.
name|getExpiration
argument_list|()
decl_stmt|;
name|long
name|newTimeStamp
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|long
name|timeToLive
init|=
literal|0
decl_stmt|;
name|long
name|oldTimestamp
init|=
name|messageSend
operator|.
name|getTimestamp
argument_list|()
decl_stmt|;
if|if
condition|(
name|oldExpiration
operator|>
literal|0
condition|)
block|{
name|timeToLive
operator|=
name|oldExpiration
operator|-
name|oldTimestamp
expr_stmt|;
block|}
name|long
name|expiration
init|=
name|timeToLive
operator|+
name|newTimeStamp
decl_stmt|;
if|if
condition|(
name|expiration
operator|>
name|oldExpiration
condition|)
block|{
if|if
condition|(
name|timeToLive
operator|>
literal|0
operator|&&
name|expiration
operator|>
literal|0
condition|)
block|{
name|messageSend
operator|.
name|setExpiration
argument_list|(
name|expiration
argument_list|)
expr_stmt|;
block|}
name|messageSend
operator|.
name|setTimestamp
argument_list|(
name|newTimeStamp
argument_list|)
expr_stmt|;
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
literal|"Set message "
operator|+
name|messageSend
operator|.
name|getMessageId
argument_list|()
operator|+
literal|" timestamp from "
operator|+
name|oldTimestamp
operator|+
literal|" to "
operator|+
name|newTimeStamp
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|final
name|ProducerBrokerExchange
name|producerExchange
init|=
operator|new
name|ProducerBrokerExchange
argument_list|()
decl_stmt|;
name|producerExchange
operator|.
name|setConnectionContext
argument_list|(
name|context
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
name|setProducerState
argument_list|(
operator|new
name|ProducerState
argument_list|(
operator|new
name|ProducerInfo
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|super
operator|.
name|send
argument_list|(
name|producerExchange
argument_list|,
name|messageSend
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
literal|"Failed to send scheduled message "
operator|+
name|id
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
specifier|protected
specifier|synchronized
name|JobScheduler
name|getInternalScheduler
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|this
operator|.
name|started
operator|.
name|get
argument_list|()
condition|)
block|{
if|if
condition|(
name|this
operator|.
name|scheduler
operator|==
literal|null
condition|)
block|{
name|this
operator|.
name|scheduler
operator|=
name|store
operator|.
name|getJobScheduler
argument_list|(
literal|"JMS"
argument_list|)
expr_stmt|;
name|this
operator|.
name|scheduler
operator|.
name|addListener
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
return|return
name|this
operator|.
name|scheduler
return|;
block|}
return|return
literal|null
return|;
block|}
specifier|protected
name|void
name|sendScheduledJob
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|Job
name|job
parameter_list|,
name|ActiveMQDestination
name|replyTo
parameter_list|)
throws|throws
name|Exception
block|{
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|util
operator|.
name|ByteSequence
name|packet
init|=
operator|new
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|util
operator|.
name|ByteSequence
argument_list|(
name|job
operator|.
name|getPayload
argument_list|()
argument_list|)
decl_stmt|;
try|try
block|{
name|Message
name|msg
init|=
operator|(
name|Message
operator|)
name|this
operator|.
name|wireFormat
operator|.
name|unmarshal
argument_list|(
name|packet
argument_list|)
decl_stmt|;
name|msg
operator|.
name|setOriginalTransactionId
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|msg
operator|.
name|setPersistent
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|msg
operator|.
name|setType
argument_list|(
name|AdvisorySupport
operator|.
name|ADIVSORY_MESSAGE_TYPE
argument_list|)
expr_stmt|;
name|msg
operator|.
name|setMessageId
argument_list|(
operator|new
name|MessageId
argument_list|(
name|this
operator|.
name|producerId
argument_list|,
name|this
operator|.
name|messageIdGenerator
operator|.
name|getNextSequenceId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|msg
operator|.
name|setDestination
argument_list|(
name|replyTo
argument_list|)
expr_stmt|;
name|msg
operator|.
name|setResponseRequired
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|msg
operator|.
name|setProducerId
argument_list|(
name|this
operator|.
name|producerId
argument_list|)
expr_stmt|;
comment|// Add the jobId as a property
name|msg
operator|.
name|setProperty
argument_list|(
literal|"scheduledJobId"
argument_list|,
name|job
operator|.
name|getJobId
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|boolean
name|originalFlowControl
init|=
name|context
operator|.
name|isProducerFlowControl
argument_list|()
decl_stmt|;
specifier|final
name|ProducerBrokerExchange
name|producerExchange
init|=
operator|new
name|ProducerBrokerExchange
argument_list|()
decl_stmt|;
name|producerExchange
operator|.
name|setConnectionContext
argument_list|(
name|context
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
name|setProducerState
argument_list|(
operator|new
name|ProducerState
argument_list|(
operator|new
name|ProducerInfo
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
name|context
operator|.
name|setProducerFlowControl
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|this
operator|.
name|next
operator|.
name|send
argument_list|(
name|producerExchange
argument_list|,
name|msg
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
literal|"Failed to send scheduled message "
operator|+
name|job
operator|.
name|getJobId
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

