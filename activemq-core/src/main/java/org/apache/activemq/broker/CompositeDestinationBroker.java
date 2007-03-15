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
name|ProducerInfo
import|;
end_import

begin_comment
comment|/**  * This broker filter handles composite destinations.  *   * If a broker operation is invoked using a composite destination, this filter  * repeats the operation using each destination of the composite.  *   * HRC: I think this filter is dangerous to use to with the consumer operations.  Multiple  * Subscription objects will be associated with a single JMS consumer each having a   * different idea of what the current pre-fetch dispatch size is.    *  * If this is used, then the client has to expect many more messages to be dispatched   * than the pre-fetch setting allows.  *   * @version $Revision: 1.8 $  */
end_comment

begin_class
specifier|public
class|class
name|CompositeDestinationBroker
extends|extends
name|BrokerFilter
block|{
specifier|public
name|CompositeDestinationBroker
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
comment|/**      * A producer may register to send to multiple destinations via a composite destination.      */
specifier|public
name|void
name|addProducer
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|ProducerInfo
name|info
parameter_list|)
throws|throws
name|Exception
block|{
comment|// The destination may be null.
name|ActiveMQDestination
name|destination
init|=
name|info
operator|.
name|getDestination
argument_list|()
decl_stmt|;
if|if
condition|(
name|destination
operator|!=
literal|null
operator|&&
name|destination
operator|.
name|isComposite
argument_list|()
condition|)
block|{
name|ActiveMQDestination
index|[]
name|destinations
init|=
name|destination
operator|.
name|getCompositeDestinations
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
name|destinations
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|ProducerInfo
name|copy
init|=
name|info
operator|.
name|copy
argument_list|()
decl_stmt|;
name|copy
operator|.
name|setDestination
argument_list|(
name|destinations
index|[
name|i
index|]
argument_list|)
expr_stmt|;
name|next
operator|.
name|addProducer
argument_list|(
name|context
argument_list|,
name|copy
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|next
operator|.
name|addProducer
argument_list|(
name|context
argument_list|,
name|info
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * A producer may de-register from sending to multiple destinations via a composite destination.      */
specifier|public
name|void
name|removeProducer
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|ProducerInfo
name|info
parameter_list|)
throws|throws
name|Exception
block|{
comment|// The destination may be null.
name|ActiveMQDestination
name|destination
init|=
name|info
operator|.
name|getDestination
argument_list|()
decl_stmt|;
if|if
condition|(
name|destination
operator|!=
literal|null
operator|&&
name|destination
operator|.
name|isComposite
argument_list|()
condition|)
block|{
name|ActiveMQDestination
index|[]
name|destinations
init|=
name|destination
operator|.
name|getCompositeDestinations
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
name|destinations
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|ProducerInfo
name|copy
init|=
name|info
operator|.
name|copy
argument_list|()
decl_stmt|;
name|copy
operator|.
name|setDestination
argument_list|(
name|destinations
index|[
name|i
index|]
argument_list|)
expr_stmt|;
name|next
operator|.
name|removeProducer
argument_list|(
name|context
argument_list|,
name|copy
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|next
operator|.
name|removeProducer
argument_list|(
name|context
argument_list|,
name|info
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      *       */
specifier|public
name|void
name|send
parameter_list|(
name|ProducerBrokerExchange
name|producerExchange
parameter_list|,
name|Message
name|message
parameter_list|)
throws|throws
name|Exception
block|{
name|ActiveMQDestination
name|destination
init|=
name|message
operator|.
name|getDestination
argument_list|()
decl_stmt|;
if|if
condition|(
name|destination
operator|.
name|isComposite
argument_list|()
condition|)
block|{
name|ActiveMQDestination
index|[]
name|destinations
init|=
name|destination
operator|.
name|getCompositeDestinations
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
name|destinations
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|i
operator|!=
literal|0
condition|)
block|{
name|message
operator|=
name|message
operator|.
name|copy
argument_list|()
expr_stmt|;
block|}
name|message
operator|.
name|setOriginalDestination
argument_list|(
name|destination
argument_list|)
expr_stmt|;
name|message
operator|.
name|setDestination
argument_list|(
name|destinations
index|[
name|i
index|]
argument_list|)
expr_stmt|;
name|next
operator|.
name|send
argument_list|(
name|producerExchange
argument_list|,
name|message
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|next
operator|.
name|send
argument_list|(
name|producerExchange
argument_list|,
name|message
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

