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
name|command
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
name|util
operator|.
name|JMSExceptionSupport
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
name|Arrays
import|;
end_import

begin_comment
comment|/**  * @openwire:marshaller code="91"  *  */
end_comment

begin_class
specifier|public
class|class
name|NetworkBridgeFilter
implements|implements
name|DataStructure
implements|,
name|BooleanExpression
block|{
specifier|public
specifier|static
specifier|final
name|byte
name|DATA_STRUCTURE_TYPE
init|=
name|CommandTypes
operator|.
name|NETWORK_BRIDGE_FILTER
decl_stmt|;
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|NetworkBridgeFilter
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|protected
name|BrokerId
name|networkBrokerId
decl_stmt|;
specifier|protected
name|int
name|messageTTL
decl_stmt|;
specifier|protected
name|int
name|consumerTTL
decl_stmt|;
specifier|transient
name|ConsumerInfo
name|consumerInfo
decl_stmt|;
specifier|public
name|NetworkBridgeFilter
parameter_list|()
block|{     }
specifier|public
name|NetworkBridgeFilter
parameter_list|(
name|ConsumerInfo
name|consumerInfo
parameter_list|,
name|BrokerId
name|networkBrokerId
parameter_list|,
name|int
name|messageTTL
parameter_list|,
name|int
name|consumerTTL
parameter_list|)
block|{
name|this
operator|.
name|networkBrokerId
operator|=
name|networkBrokerId
expr_stmt|;
name|this
operator|.
name|messageTTL
operator|=
name|messageTTL
expr_stmt|;
name|this
operator|.
name|consumerTTL
operator|=
name|consumerTTL
expr_stmt|;
name|this
operator|.
name|consumerInfo
operator|=
name|consumerInfo
expr_stmt|;
block|}
specifier|public
name|byte
name|getDataStructureType
parameter_list|()
block|{
return|return
name|DATA_STRUCTURE_TYPE
return|;
block|}
specifier|public
name|boolean
name|isMarshallAware
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
specifier|public
name|boolean
name|matches
parameter_list|(
name|MessageEvaluationContext
name|mec
parameter_list|)
throws|throws
name|JMSException
block|{
try|try
block|{
comment|// for Queues - the message can be acknowledged and dropped whilst
comment|// still
comment|// in the dispatch loop
comment|// so need to get the reference to it
name|Message
name|message
init|=
name|mec
operator|.
name|getMessage
argument_list|()
decl_stmt|;
return|return
name|message
operator|!=
literal|null
operator|&&
name|matchesForwardingFilter
argument_list|(
name|message
argument_list|,
name|mec
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
name|JMSExceptionSupport
operator|.
name|create
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
specifier|public
name|Object
name|evaluate
parameter_list|(
name|MessageEvaluationContext
name|message
parameter_list|)
throws|throws
name|JMSException
block|{
return|return
name|matches
argument_list|(
name|message
argument_list|)
condition|?
name|Boolean
operator|.
name|TRUE
else|:
name|Boolean
operator|.
name|FALSE
return|;
block|}
specifier|protected
name|boolean
name|matchesForwardingFilter
parameter_list|(
name|Message
name|message
parameter_list|,
name|MessageEvaluationContext
name|mec
parameter_list|)
block|{
if|if
condition|(
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
literal|"Message all ready routed once through target broker ("
operator|+
name|networkBrokerId
operator|+
literal|"), path: "
operator|+
name|Arrays
operator|.
name|toString
argument_list|(
name|message
operator|.
name|getBrokerPath
argument_list|()
argument_list|)
operator|+
literal|" - ignoring: "
operator|+
name|message
argument_list|)
expr_stmt|;
block|}
return|return
literal|false
return|;
block|}
name|int
name|hops
init|=
name|message
operator|.
name|getBrokerPath
argument_list|()
operator|==
literal|null
condition|?
literal|0
else|:
name|message
operator|.
name|getBrokerPath
argument_list|()
operator|.
name|length
decl_stmt|;
if|if
condition|(
name|messageTTL
operator|>
operator|-
literal|1
operator|&&
name|hops
operator|>=
name|messageTTL
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
literal|"Message restricted to "
operator|+
name|messageTTL
operator|+
literal|" network hops ignoring: "
operator|+
name|message
argument_list|)
expr_stmt|;
block|}
return|return
literal|false
return|;
block|}
if|if
condition|(
name|message
operator|.
name|isAdvisory
argument_list|()
condition|)
block|{
if|if
condition|(
name|consumerInfo
operator|!=
literal|null
operator|&&
name|consumerInfo
operator|.
name|isNetworkSubscription
argument_list|()
operator|&&
name|isAdvisoryInterpretedByNetworkBridge
argument_list|(
name|message
argument_list|)
condition|)
block|{
comment|// they will be interpreted by the bridge leading to dup commands
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
literal|"not propagating advisory to network sub: "
operator|+
name|consumerInfo
operator|.
name|getConsumerId
argument_list|()
operator|+
literal|", message: "
operator|+
name|message
argument_list|)
expr_stmt|;
block|}
return|return
literal|false
return|;
block|}
elseif|else
if|if
condition|(
name|message
operator|.
name|getDataStructure
argument_list|()
operator|!=
literal|null
operator|&&
name|message
operator|.
name|getDataStructure
argument_list|()
operator|.
name|getDataStructureType
argument_list|()
operator|==
name|CommandTypes
operator|.
name|CONSUMER_INFO
condition|)
block|{
name|ConsumerInfo
name|info
init|=
operator|(
name|ConsumerInfo
operator|)
name|message
operator|.
name|getDataStructure
argument_list|()
decl_stmt|;
name|hops
operator|=
name|info
operator|.
name|getBrokerPath
argument_list|()
operator|==
literal|null
condition|?
literal|0
else|:
name|info
operator|.
name|getBrokerPath
argument_list|()
operator|.
name|length
expr_stmt|;
if|if
condition|(
name|consumerTTL
operator|>
operator|-
literal|1
operator|&&
name|hops
operator|>=
name|consumerTTL
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
literal|"ConsumerInfo advisory restricted to "
operator|+
name|consumerTTL
operator|+
literal|" network hops ignoring: "
operator|+
name|message
argument_list|)
expr_stmt|;
block|}
return|return
literal|false
return|;
block|}
if|if
condition|(
name|contains
argument_list|(
name|info
operator|.
name|getBrokerPath
argument_list|()
argument_list|,
name|networkBrokerId
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|trace
argument_list|(
literal|"ConsumerInfo advisory all ready routed once through target broker ("
operator|+
name|networkBrokerId
operator|+
literal|"), path: "
operator|+
name|Arrays
operator|.
name|toString
argument_list|(
name|info
operator|.
name|getBrokerPath
argument_list|()
argument_list|)
operator|+
literal|" - ignoring: "
operator|+
name|message
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
block|}
block|}
return|return
literal|true
return|;
block|}
specifier|public
specifier|static
name|boolean
name|isAdvisoryInterpretedByNetworkBridge
parameter_list|(
name|Message
name|message
parameter_list|)
block|{
return|return
name|AdvisorySupport
operator|.
name|isConsumerAdvisoryTopic
argument_list|(
name|message
operator|.
name|getDestination
argument_list|()
argument_list|)
operator|||
name|AdvisorySupport
operator|.
name|isTempDestinationAdvisoryTopic
argument_list|(
name|message
operator|.
name|getDestination
argument_list|()
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|boolean
name|contains
parameter_list|(
name|BrokerId
index|[]
name|brokerPath
parameter_list|,
name|BrokerId
name|brokerId
parameter_list|)
block|{
if|if
condition|(
name|brokerPath
operator|!=
literal|null
operator|&&
name|brokerId
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|brokerPath
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|brokerId
operator|.
name|equals
argument_list|(
name|brokerPath
index|[
name|i
index|]
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
block|}
return|return
literal|false
return|;
block|}
comment|// keep for backward compat with older
comment|// wire formats
specifier|public
name|int
name|getNetworkTTL
parameter_list|()
block|{
return|return
name|messageTTL
return|;
block|}
specifier|public
name|void
name|setNetworkTTL
parameter_list|(
name|int
name|networkTTL
parameter_list|)
block|{
name|messageTTL
operator|=
name|networkTTL
expr_stmt|;
name|consumerTTL
operator|=
name|networkTTL
expr_stmt|;
block|}
comment|/**      * @openwire:property version=1 cache=true      */
specifier|public
name|BrokerId
name|getNetworkBrokerId
parameter_list|()
block|{
return|return
name|networkBrokerId
return|;
block|}
specifier|public
name|void
name|setNetworkBrokerId
parameter_list|(
name|BrokerId
name|remoteBrokerPath
parameter_list|)
block|{
name|this
operator|.
name|networkBrokerId
operator|=
name|remoteBrokerPath
expr_stmt|;
block|}
specifier|public
name|void
name|setMessageTTL
parameter_list|(
name|int
name|messageTTL
parameter_list|)
block|{
name|this
operator|.
name|messageTTL
operator|=
name|messageTTL
expr_stmt|;
block|}
comment|/**      * @openwire:property version=10      */
specifier|public
name|int
name|getMessageTTL
parameter_list|()
block|{
return|return
name|this
operator|.
name|messageTTL
return|;
block|}
specifier|public
name|void
name|setConsumerTTL
parameter_list|(
name|int
name|consumerTTL
parameter_list|)
block|{
name|this
operator|.
name|consumerTTL
operator|=
name|consumerTTL
expr_stmt|;
block|}
comment|/**      * @openwire:property version=10      */
specifier|public
name|int
name|getConsumerTTL
parameter_list|()
block|{
return|return
name|this
operator|.
name|consumerTTL
return|;
block|}
block|}
end_class

end_unit

