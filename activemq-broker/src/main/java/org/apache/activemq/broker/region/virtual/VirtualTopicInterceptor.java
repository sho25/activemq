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
name|virtual
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
name|broker
operator|.
name|region
operator|.
name|Topic
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
name|util
operator|.
name|LRUCache
import|;
end_import

begin_comment
comment|/**  * A Destination which implements<a href="http://activemq.org/site/virtual-destinations.html">Virtual Topic</a>  */
end_comment

begin_class
specifier|public
class|class
name|VirtualTopicInterceptor
extends|extends
name|DestinationFilter
block|{
specifier|private
specifier|final
name|String
name|prefix
decl_stmt|;
specifier|private
specifier|final
name|String
name|postfix
decl_stmt|;
specifier|private
specifier|final
name|boolean
name|local
decl_stmt|;
specifier|private
specifier|final
name|LRUCache
argument_list|<
name|ActiveMQDestination
argument_list|,
name|ActiveMQQueue
argument_list|>
name|cache
init|=
operator|new
name|LRUCache
argument_list|<
name|ActiveMQDestination
argument_list|,
name|ActiveMQQueue
argument_list|>
argument_list|()
decl_stmt|;
specifier|public
name|VirtualTopicInterceptor
parameter_list|(
name|Destination
name|next
parameter_list|,
name|String
name|prefix
parameter_list|,
name|String
name|postfix
parameter_list|,
name|boolean
name|local
parameter_list|)
block|{
name|super
argument_list|(
name|next
argument_list|)
expr_stmt|;
name|this
operator|.
name|prefix
operator|=
name|prefix
expr_stmt|;
name|this
operator|.
name|postfix
operator|=
name|postfix
expr_stmt|;
name|this
operator|.
name|local
operator|=
name|local
expr_stmt|;
block|}
specifier|public
name|Topic
name|getTopic
parameter_list|()
block|{
return|return
operator|(
name|Topic
operator|)
name|this
operator|.
name|next
return|;
block|}
annotation|@
name|Override
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
if|if
condition|(
operator|!
name|message
operator|.
name|isAdvisory
argument_list|()
operator|&&
operator|!
operator|(
name|local
operator|&&
name|message
operator|.
name|getBrokerPath
argument_list|()
operator|!=
literal|null
operator|)
condition|)
block|{
name|ActiveMQDestination
name|queueConsumers
init|=
name|getQueueConsumersWildcard
argument_list|(
name|message
operator|.
name|getDestination
argument_list|()
argument_list|)
decl_stmt|;
name|send
argument_list|(
name|context
argument_list|,
name|message
argument_list|,
name|queueConsumers
argument_list|)
expr_stmt|;
block|}
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
specifier|protected
name|ActiveMQDestination
name|getQueueConsumersWildcard
parameter_list|(
name|ActiveMQDestination
name|original
parameter_list|)
block|{
name|ActiveMQQueue
name|queue
decl_stmt|;
synchronized|synchronized
init|(
name|cache
init|)
block|{
name|queue
operator|=
name|cache
operator|.
name|get
argument_list|(
name|original
argument_list|)
expr_stmt|;
if|if
condition|(
name|queue
operator|==
literal|null
condition|)
block|{
name|queue
operator|=
operator|new
name|ActiveMQQueue
argument_list|(
name|prefix
operator|+
name|original
operator|.
name|getPhysicalName
argument_list|()
operator|+
name|postfix
argument_list|)
expr_stmt|;
name|cache
operator|.
name|put
argument_list|(
name|original
argument_list|,
name|queue
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|queue
return|;
block|}
block|}
end_class

end_unit

