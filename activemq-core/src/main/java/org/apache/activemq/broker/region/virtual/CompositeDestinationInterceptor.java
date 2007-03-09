begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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

begin_comment
comment|/**  * Represents a composite {@link Destination} where send()s are replicated to  * each Destination instance.  *  * @version $Revision$  */
end_comment

begin_class
specifier|public
class|class
name|CompositeDestinationInterceptor
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
name|copyMessage
decl_stmt|;
specifier|public
name|CompositeDestinationInterceptor
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
name|copyMessage
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
name|copyMessage
operator|=
name|copyMessage
expr_stmt|;
block|}
specifier|public
name|void
name|send
parameter_list|(
name|ProducerBrokerExchange
name|context
parameter_list|,
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
name|MessageEvaluationContext
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
if|if
condition|(
name|copyMessage
condition|)
block|{
name|message
operator|=
name|message
operator|.
name|copy
argument_list|()
expr_stmt|;
name|message
operator|.
name|setDestination
argument_list|(
name|destination
argument_list|)
expr_stmt|;
block|}
name|send
argument_list|(
name|context
argument_list|,
name|message
argument_list|,
name|destination
argument_list|)
expr_stmt|;
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
block|}
block|}
end_class

end_unit

