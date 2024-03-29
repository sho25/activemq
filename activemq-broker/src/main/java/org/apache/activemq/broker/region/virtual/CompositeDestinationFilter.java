begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|util
operator|.
name|Collection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
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
name|concurrent
operator|.
name|CountDownLatch
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
name|AtomicReference
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

begin_comment
comment|/**  * Represents a composite {@link Destination} where send()s are replicated to  * each Destination instance.  */
end_comment

begin_class
specifier|public
class|class
name|CompositeDestinationFilter
extends|extends
name|DestinationFilter
block|{
specifier|private
name|Collection
name|forwardDestinations
decl_stmt|;
specifier|private
name|boolean
name|forwardOnly
decl_stmt|;
specifier|private
name|boolean
name|concurrentSend
init|=
literal|false
decl_stmt|;
specifier|public
name|CompositeDestinationFilter
parameter_list|(
name|Destination
name|next
parameter_list|,
name|Collection
name|forwardDestinations
parameter_list|,
name|boolean
name|forwardOnly
parameter_list|,
name|boolean
name|concurrentSend
parameter_list|)
block|{
name|super
argument_list|(
name|next
argument_list|)
expr_stmt|;
name|this
operator|.
name|forwardDestinations
operator|=
name|forwardDestinations
expr_stmt|;
name|this
operator|.
name|forwardOnly
operator|=
name|forwardOnly
expr_stmt|;
name|this
operator|.
name|concurrentSend
operator|=
name|concurrentSend
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|send
parameter_list|(
specifier|final
name|ProducerBrokerExchange
name|context
parameter_list|,
specifier|final
name|Message
name|message
parameter_list|)
throws|throws
name|Exception
block|{
name|MessageEvaluationContext
name|messageContext
init|=
literal|null
decl_stmt|;
name|Collection
argument_list|<
name|ActiveMQDestination
argument_list|>
name|matchingDestinations
init|=
operator|new
name|LinkedList
argument_list|<
name|ActiveMQDestination
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|Iterator
name|iter
init|=
name|forwardDestinations
operator|.
name|iterator
argument_list|()
init|;
name|iter
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|ActiveMQDestination
name|destination
init|=
literal|null
decl_stmt|;
name|Object
name|value
init|=
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|value
operator|instanceof
name|FilteredDestination
condition|)
block|{
name|FilteredDestination
name|filteredDestination
init|=
operator|(
name|FilteredDestination
operator|)
name|value
decl_stmt|;
if|if
condition|(
name|messageContext
operator|==
literal|null
condition|)
block|{
name|messageContext
operator|=
operator|new
name|NonCachedMessageEvaluationContext
argument_list|()
expr_stmt|;
name|messageContext
operator|.
name|setMessageReference
argument_list|(
name|message
argument_list|)
expr_stmt|;
block|}
name|messageContext
operator|.
name|setDestination
argument_list|(
name|filteredDestination
operator|.
name|getDestination
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|filteredDestination
operator|.
name|matches
argument_list|(
name|messageContext
argument_list|)
condition|)
block|{
name|destination
operator|=
name|filteredDestination
operator|.
name|getDestination
argument_list|()
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|value
operator|instanceof
name|ActiveMQDestination
condition|)
block|{
name|destination
operator|=
operator|(
name|ActiveMQDestination
operator|)
name|value
expr_stmt|;
block|}
if|if
condition|(
name|destination
operator|==
literal|null
condition|)
block|{
continue|continue;
block|}
name|matchingDestinations
operator|.
name|add
argument_list|(
name|destination
argument_list|)
expr_stmt|;
block|}
specifier|final
name|CountDownLatch
name|concurrent
init|=
operator|new
name|CountDownLatch
argument_list|(
name|concurrentSend
condition|?
name|matchingDestinations
operator|.
name|size
argument_list|()
else|:
literal|0
argument_list|)
decl_stmt|;
specifier|final
name|AtomicReference
argument_list|<
name|Exception
argument_list|>
name|exceptionAtomicReference
init|=
operator|new
name|AtomicReference
argument_list|<
name|Exception
argument_list|>
argument_list|()
decl_stmt|;
specifier|final
name|BrokerService
name|brokerService
init|=
name|context
operator|.
name|getConnectionContext
argument_list|()
operator|.
name|getBroker
argument_list|()
operator|.
name|getBrokerService
argument_list|()
decl_stmt|;
for|for
control|(
specifier|final
name|ActiveMQDestination
name|destination
range|:
name|matchingDestinations
control|)
block|{
if|if
condition|(
name|concurrent
operator|.
name|getCount
argument_list|()
operator|>
literal|0
condition|)
block|{
name|brokerService
operator|.
name|getTaskRunnerFactory
argument_list|()
operator|.
name|execute
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
if|if
condition|(
name|exceptionAtomicReference
operator|.
name|get
argument_list|()
operator|==
literal|null
condition|)
block|{
name|doForward
argument_list|(
name|context
operator|.
name|copy
argument_list|()
argument_list|,
name|message
argument_list|,
name|brokerService
operator|.
name|getRegionBroker
argument_list|()
argument_list|,
name|destination
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
name|exceptionAtomicReference
operator|.
name|set
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|concurrent
operator|.
name|countDown
argument_list|()
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|doForward
argument_list|(
name|context
argument_list|,
name|message
argument_list|,
name|brokerService
operator|.
name|getRegionBroker
argument_list|()
argument_list|,
name|destination
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
operator|!
name|forwardOnly
condition|)
block|{
name|super
operator|.
name|send
argument_list|(
name|context
argument_list|,
name|message
argument_list|)
expr_stmt|;
block|}
name|concurrent
operator|.
name|await
argument_list|()
expr_stmt|;
if|if
condition|(
name|exceptionAtomicReference
operator|.
name|get
argument_list|()
operator|!=
literal|null
condition|)
block|{
throw|throw
name|exceptionAtomicReference
operator|.
name|get
argument_list|()
throw|;
block|}
block|}
specifier|private
name|void
name|doForward
parameter_list|(
name|ProducerBrokerExchange
name|context
parameter_list|,
name|Message
name|message
parameter_list|,
name|Broker
name|regionBroker
parameter_list|,
name|ActiveMQDestination
name|destination
parameter_list|)
throws|throws
name|Exception
block|{
name|Message
name|forwardedMessage
init|=
name|message
operator|.
name|copy
argument_list|()
decl_stmt|;
name|forwardedMessage
operator|.
name|setMemoryUsage
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|forwardedMessage
operator|.
name|setOriginalDestination
argument_list|(
name|message
operator|.
name|getDestination
argument_list|()
argument_list|)
expr_stmt|;
name|forwardedMessage
operator|.
name|setDestination
argument_list|(
name|destination
argument_list|)
expr_stmt|;
comment|// Send it back through the region broker for routing.
name|context
operator|.
name|setMutable
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|regionBroker
operator|.
name|send
argument_list|(
name|context
argument_list|,
name|forwardedMessage
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

