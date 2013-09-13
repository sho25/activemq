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
name|plugin
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
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
comment|/**  */
end_comment

begin_class
specifier|public
class|class
name|DiscardingDLQBroker
extends|extends
name|BrokerFilter
block|{
specifier|public
specifier|static
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|DiscardingDLQBroker
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|boolean
name|dropTemporaryTopics
init|=
literal|true
decl_stmt|;
specifier|private
name|boolean
name|dropTemporaryQueues
init|=
literal|true
decl_stmt|;
specifier|private
name|boolean
name|dropAll
init|=
literal|true
decl_stmt|;
specifier|private
name|Pattern
index|[]
name|destFilter
decl_stmt|;
specifier|private
name|int
name|reportInterval
init|=
literal|1000
decl_stmt|;
specifier|private
name|long
name|dropCount
init|=
literal|0
decl_stmt|;
specifier|public
name|DiscardingDLQBroker
parameter_list|(
name|Broker
name|next
parameter_list|)
block|{
name|super
argument_list|(
name|next
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|sendToDeadLetterQueue
parameter_list|(
name|ConnectionContext
name|ctx
parameter_list|,
name|MessageReference
name|msgRef
parameter_list|,
name|Subscription
name|subscription
parameter_list|,
name|Throwable
name|poisonCause
parameter_list|)
block|{
name|log
operator|.
name|trace
argument_list|(
literal|"Discarding DLQ BrokerFilter[pass through] - skipping message: {}"
argument_list|,
operator|(
name|msgRef
operator|!=
literal|null
condition|?
name|msgRef
operator|.
name|getMessage
argument_list|()
else|:
literal|null
operator|)
argument_list|)
expr_stmt|;
name|boolean
name|dropped
init|=
literal|true
decl_stmt|;
name|Message
name|msg
init|=
literal|null
decl_stmt|;
name|ActiveMQDestination
name|dest
init|=
literal|null
decl_stmt|;
name|String
name|destName
init|=
literal|null
decl_stmt|;
name|msg
operator|=
name|msgRef
operator|.
name|getMessage
argument_list|()
expr_stmt|;
name|dest
operator|=
name|msg
operator|.
name|getDestination
argument_list|()
expr_stmt|;
name|destName
operator|=
name|dest
operator|.
name|getPhysicalName
argument_list|()
expr_stmt|;
if|if
condition|(
name|dest
operator|==
literal|null
operator|||
name|destName
operator|==
literal|null
condition|)
block|{
comment|// do nothing, no need to forward it
name|skipMessage
argument_list|(
literal|"NULL DESTINATION"
argument_list|,
name|msgRef
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|dropAll
condition|)
block|{
comment|// do nothing
name|skipMessage
argument_list|(
literal|"dropAll"
argument_list|,
name|msgRef
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|dropTemporaryTopics
operator|&&
name|dest
operator|.
name|isTemporary
argument_list|()
operator|&&
name|dest
operator|.
name|isTopic
argument_list|()
condition|)
block|{
comment|// do nothing
name|skipMessage
argument_list|(
literal|"dropTemporaryTopics"
argument_list|,
name|msgRef
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|dropTemporaryQueues
operator|&&
name|dest
operator|.
name|isTemporary
argument_list|()
operator|&&
name|dest
operator|.
name|isQueue
argument_list|()
condition|)
block|{
comment|// do nothing
name|skipMessage
argument_list|(
literal|"dropTemporaryQueues"
argument_list|,
name|msgRef
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|destFilter
operator|!=
literal|null
operator|&&
name|matches
argument_list|(
name|destName
argument_list|)
condition|)
block|{
comment|// do nothing
name|skipMessage
argument_list|(
literal|"dropOnly"
argument_list|,
name|msgRef
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|dropped
operator|=
literal|false
expr_stmt|;
return|return
name|next
operator|.
name|sendToDeadLetterQueue
argument_list|(
name|ctx
argument_list|,
name|msgRef
argument_list|,
name|subscription
argument_list|,
name|poisonCause
argument_list|)
return|;
block|}
if|if
condition|(
name|dropped
operator|&&
name|getReportInterval
argument_list|()
operator|>
literal|0
condition|)
block|{
if|if
condition|(
operator|(
operator|++
name|dropCount
operator|)
operator|%
name|getReportInterval
argument_list|()
operator|==
literal|0
condition|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Total of {} messages were discarded, since their destination was the dead letter queue"
argument_list|,
name|dropCount
argument_list|)
expr_stmt|;
block|}
block|}
return|return
literal|false
return|;
block|}
specifier|public
name|boolean
name|matches
parameter_list|(
name|String
name|destName
parameter_list|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|destFilter
operator|!=
literal|null
operator|&&
name|i
operator|<
name|destFilter
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|destFilter
index|[
name|i
index|]
operator|!=
literal|null
operator|&&
name|destFilter
index|[
name|i
index|]
operator|.
name|matcher
argument_list|(
name|destName
argument_list|)
operator|.
name|matches
argument_list|()
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
specifier|private
name|void
name|skipMessage
parameter_list|(
name|String
name|prefix
parameter_list|,
name|MessageReference
name|msgRef
parameter_list|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Discarding DLQ BrokerFilter[{}] - skipping message: {}"
argument_list|,
name|prefix
argument_list|,
operator|(
name|msgRef
operator|!=
literal|null
condition|?
name|msgRef
operator|.
name|getMessage
argument_list|()
else|:
literal|null
operator|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setDropTemporaryTopics
parameter_list|(
name|boolean
name|dropTemporaryTopics
parameter_list|)
block|{
name|this
operator|.
name|dropTemporaryTopics
operator|=
name|dropTemporaryTopics
expr_stmt|;
block|}
specifier|public
name|void
name|setDropTemporaryQueues
parameter_list|(
name|boolean
name|dropTemporaryQueues
parameter_list|)
block|{
name|this
operator|.
name|dropTemporaryQueues
operator|=
name|dropTemporaryQueues
expr_stmt|;
block|}
specifier|public
name|void
name|setDropAll
parameter_list|(
name|boolean
name|dropAll
parameter_list|)
block|{
name|this
operator|.
name|dropAll
operator|=
name|dropAll
expr_stmt|;
block|}
specifier|public
name|void
name|setDestFilter
parameter_list|(
name|Pattern
index|[]
name|destFilter
parameter_list|)
block|{
name|this
operator|.
name|destFilter
operator|=
name|destFilter
expr_stmt|;
block|}
specifier|public
name|void
name|setReportInterval
parameter_list|(
name|int
name|reportInterval
parameter_list|)
block|{
name|this
operator|.
name|reportInterval
operator|=
name|reportInterval
expr_stmt|;
block|}
specifier|public
name|boolean
name|isDropTemporaryTopics
parameter_list|()
block|{
return|return
name|dropTemporaryTopics
return|;
block|}
specifier|public
name|boolean
name|isDropTemporaryQueues
parameter_list|()
block|{
return|return
name|dropTemporaryQueues
return|;
block|}
specifier|public
name|boolean
name|isDropAll
parameter_list|()
block|{
return|return
name|dropAll
return|;
block|}
specifier|public
name|Pattern
index|[]
name|getDestFilter
parameter_list|()
block|{
return|return
name|destFilter
return|;
block|}
specifier|public
name|int
name|getReportInterval
parameter_list|()
block|{
return|return
name|reportInterval
return|;
block|}
block|}
end_class

end_unit

