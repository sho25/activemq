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
name|view
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
name|io
operator|.
name|PrintWriter
import|;
end_import

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
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
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
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|ObjectName
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
name|jmx
operator|.
name|BrokerViewMBean
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
name|jmx
operator|.
name|SubscriptionViewMBean
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
name|filter
operator|.
name|DestinationMapNode
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
specifier|public
class|class
name|ConnectionDotFileInterceptor
extends|extends
name|DotFileInterceptorSupport
block|{
specifier|protected
specifier|static
specifier|final
name|String
name|ID_SEPARATOR
init|=
literal|"_"
decl_stmt|;
specifier|private
specifier|final
name|boolean
name|redrawOnRemove
decl_stmt|;
specifier|private
name|boolean
name|clearProducerCacheAfterRender
decl_stmt|;
specifier|private
specifier|final
name|String
name|domain
init|=
literal|"org.apache.activemq"
decl_stmt|;
specifier|private
name|BrokerViewMBean
name|brokerView
decl_stmt|;
comment|// until we have some MBeans for producers, lets do it all ourselves
specifier|private
specifier|final
name|Map
argument_list|<
name|ProducerId
argument_list|,
name|ProducerInfo
argument_list|>
name|producers
init|=
operator|new
name|HashMap
argument_list|<
name|ProducerId
argument_list|,
name|ProducerInfo
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|Map
argument_list|<
name|ProducerId
argument_list|,
name|Set
argument_list|<
name|ActiveMQDestination
argument_list|>
argument_list|>
name|producerDestinations
init|=
operator|new
name|HashMap
argument_list|<
name|ProducerId
argument_list|,
name|Set
argument_list|<
name|ActiveMQDestination
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|Object
name|lock
init|=
operator|new
name|Object
argument_list|()
decl_stmt|;
specifier|public
name|ConnectionDotFileInterceptor
parameter_list|(
name|Broker
name|next
parameter_list|,
name|String
name|file
parameter_list|,
name|boolean
name|redrawOnRemove
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|next
argument_list|,
name|file
argument_list|)
expr_stmt|;
name|this
operator|.
name|redrawOnRemove
operator|=
name|redrawOnRemove
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Subscription
name|addConsumer
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|ConsumerInfo
name|info
parameter_list|)
throws|throws
name|Exception
block|{
name|Subscription
name|answer
init|=
name|super
operator|.
name|addConsumer
argument_list|(
name|context
argument_list|,
name|info
argument_list|)
decl_stmt|;
name|generateFile
argument_list|()
expr_stmt|;
return|return
name|answer
return|;
block|}
annotation|@
name|Override
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
name|super
operator|.
name|addProducer
argument_list|(
name|context
argument_list|,
name|info
argument_list|)
expr_stmt|;
name|ProducerId
name|producerId
init|=
name|info
operator|.
name|getProducerId
argument_list|()
decl_stmt|;
synchronized|synchronized
init|(
name|lock
init|)
block|{
name|producers
operator|.
name|put
argument_list|(
name|producerId
argument_list|,
name|info
argument_list|)
expr_stmt|;
block|}
name|generateFile
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|removeConsumer
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|ConsumerInfo
name|info
parameter_list|)
throws|throws
name|Exception
block|{
name|super
operator|.
name|removeConsumer
argument_list|(
name|context
argument_list|,
name|info
argument_list|)
expr_stmt|;
if|if
condition|(
name|redrawOnRemove
condition|)
block|{
name|generateFile
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
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
name|super
operator|.
name|removeProducer
argument_list|(
name|context
argument_list|,
name|info
argument_list|)
expr_stmt|;
name|ProducerId
name|producerId
init|=
name|info
operator|.
name|getProducerId
argument_list|()
decl_stmt|;
if|if
condition|(
name|redrawOnRemove
condition|)
block|{
synchronized|synchronized
init|(
name|lock
init|)
block|{
name|producerDestinations
operator|.
name|remove
argument_list|(
name|producerId
argument_list|)
expr_stmt|;
name|producers
operator|.
name|remove
argument_list|(
name|producerId
argument_list|)
expr_stmt|;
block|}
name|generateFile
argument_list|()
expr_stmt|;
block|}
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
name|Message
name|messageSend
parameter_list|)
throws|throws
name|Exception
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
name|ProducerId
name|producerId
init|=
name|messageSend
operator|.
name|getProducerId
argument_list|()
decl_stmt|;
name|ActiveMQDestination
name|destination
init|=
name|messageSend
operator|.
name|getDestination
argument_list|()
decl_stmt|;
synchronized|synchronized
init|(
name|lock
init|)
block|{
name|Set
argument_list|<
name|ActiveMQDestination
argument_list|>
name|destinations
init|=
name|producerDestinations
operator|.
name|get
argument_list|(
name|producerId
argument_list|)
decl_stmt|;
if|if
condition|(
name|destinations
operator|==
literal|null
condition|)
block|{
name|destinations
operator|=
operator|new
name|HashSet
argument_list|<
name|ActiveMQDestination
argument_list|>
argument_list|()
expr_stmt|;
block|}
name|producerDestinations
operator|.
name|put
argument_list|(
name|producerId
argument_list|,
name|destinations
argument_list|)
expr_stmt|;
name|destinations
operator|.
name|add
argument_list|(
name|destination
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|protected
name|void
name|generateFile
parameter_list|(
name|PrintWriter
name|writer
parameter_list|)
throws|throws
name|Exception
block|{
name|writer
operator|.
name|println
argument_list|(
literal|"digraph \"ActiveMQ Connections\" {"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|println
argument_list|()
expr_stmt|;
name|writer
operator|.
name|println
argument_list|(
literal|"label=\"ActiveMQ Broker: "
operator|+
name|getBrokerView
argument_list|()
operator|.
name|getBrokerId
argument_list|()
operator|+
literal|"\"];"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|println
argument_list|()
expr_stmt|;
name|writer
operator|.
name|println
argument_list|(
literal|"node [style = \"rounded,filled\", fillcolor = yellow, fontname=\"Helvetica-Oblique\"];"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|println
argument_list|()
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|clients
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|queues
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|topics
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|printSubscribers
argument_list|(
name|writer
argument_list|,
name|clients
argument_list|,
name|queues
argument_list|,
literal|"queue_"
argument_list|,
name|getBrokerView
argument_list|()
operator|.
name|getQueueSubscribers
argument_list|()
argument_list|)
expr_stmt|;
name|writer
operator|.
name|println
argument_list|()
expr_stmt|;
name|printSubscribers
argument_list|(
name|writer
argument_list|,
name|clients
argument_list|,
name|topics
argument_list|,
literal|"topic_"
argument_list|,
name|getBrokerView
argument_list|()
operator|.
name|getTopicSubscribers
argument_list|()
argument_list|)
expr_stmt|;
name|writer
operator|.
name|println
argument_list|()
expr_stmt|;
name|printProducers
argument_list|(
name|writer
argument_list|,
name|clients
argument_list|,
name|queues
argument_list|,
name|topics
argument_list|)
expr_stmt|;
name|writer
operator|.
name|println
argument_list|()
expr_stmt|;
name|writeLabels
argument_list|(
name|writer
argument_list|,
literal|"green"
argument_list|,
literal|"Client: "
argument_list|,
name|clients
argument_list|)
expr_stmt|;
name|writer
operator|.
name|println
argument_list|()
expr_stmt|;
name|writeLabels
argument_list|(
name|writer
argument_list|,
literal|"red"
argument_list|,
literal|"Queue: "
argument_list|,
name|queues
argument_list|)
expr_stmt|;
name|writeLabels
argument_list|(
name|writer
argument_list|,
literal|"blue"
argument_list|,
literal|"Topic: "
argument_list|,
name|topics
argument_list|)
expr_stmt|;
name|writer
operator|.
name|println
argument_list|(
literal|"}"
argument_list|)
expr_stmt|;
if|if
condition|(
name|clearProducerCacheAfterRender
condition|)
block|{
name|producerDestinations
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
block|}
specifier|protected
name|void
name|printProducers
parameter_list|(
name|PrintWriter
name|writer
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|clients
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|queues
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|topics
parameter_list|)
block|{
synchronized|synchronized
init|(
name|lock
init|)
block|{
for|for
control|(
name|Iterator
name|iter
init|=
name|producerDestinations
operator|.
name|entrySet
argument_list|()
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
name|Map
operator|.
name|Entry
name|entry
init|=
operator|(
name|Map
operator|.
name|Entry
operator|)
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
name|ProducerId
name|producerId
init|=
operator|(
name|ProducerId
operator|)
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|Set
name|destinationSet
init|=
operator|(
name|Set
operator|)
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|printProducers
argument_list|(
name|writer
argument_list|,
name|clients
argument_list|,
name|queues
argument_list|,
name|topics
argument_list|,
name|producerId
argument_list|,
name|destinationSet
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|protected
name|void
name|printProducers
parameter_list|(
name|PrintWriter
name|writer
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|clients
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|queues
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|topics
parameter_list|,
name|ProducerId
name|producerId
parameter_list|,
name|Set
name|destinationSet
parameter_list|)
block|{
for|for
control|(
name|Iterator
name|iter
init|=
name|destinationSet
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
operator|(
name|ActiveMQDestination
operator|)
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
comment|// TODO use clientId one day
name|String
name|clientId
init|=
name|producerId
operator|.
name|getConnectionId
argument_list|()
decl_stmt|;
name|String
name|safeClientId
init|=
name|asID
argument_list|(
name|clientId
argument_list|)
decl_stmt|;
name|clients
operator|.
name|put
argument_list|(
name|safeClientId
argument_list|,
name|clientId
argument_list|)
expr_stmt|;
name|String
name|physicalName
init|=
name|destination
operator|.
name|getPhysicalName
argument_list|()
decl_stmt|;
name|String
name|safeDestinationId
init|=
name|asID
argument_list|(
name|physicalName
argument_list|)
decl_stmt|;
if|if
condition|(
name|destination
operator|.
name|isTopic
argument_list|()
condition|)
block|{
name|safeDestinationId
operator|=
literal|"topic_"
operator|+
name|safeDestinationId
expr_stmt|;
name|topics
operator|.
name|put
argument_list|(
name|safeDestinationId
argument_list|,
name|physicalName
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|safeDestinationId
operator|=
literal|"queue_"
operator|+
name|safeDestinationId
expr_stmt|;
name|queues
operator|.
name|put
argument_list|(
name|safeDestinationId
argument_list|,
name|physicalName
argument_list|)
expr_stmt|;
block|}
name|String
name|safeProducerId
init|=
name|asID
argument_list|(
name|producerId
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
comment|// lets write out the links
name|writer
operator|.
name|print
argument_list|(
name|safeClientId
argument_list|)
expr_stmt|;
name|writer
operator|.
name|print
argument_list|(
literal|" -> "
argument_list|)
expr_stmt|;
name|writer
operator|.
name|print
argument_list|(
name|safeProducerId
argument_list|)
expr_stmt|;
name|writer
operator|.
name|println
argument_list|(
literal|";"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|print
argument_list|(
name|safeProducerId
argument_list|)
expr_stmt|;
name|writer
operator|.
name|print
argument_list|(
literal|" -> "
argument_list|)
expr_stmt|;
name|writer
operator|.
name|print
argument_list|(
name|safeDestinationId
argument_list|)
expr_stmt|;
name|writer
operator|.
name|println
argument_list|(
literal|";"
argument_list|)
expr_stmt|;
comment|// now lets write out the label
name|writer
operator|.
name|print
argument_list|(
name|safeProducerId
argument_list|)
expr_stmt|;
name|writer
operator|.
name|print
argument_list|(
literal|" [label = \""
argument_list|)
expr_stmt|;
name|String
name|label
init|=
literal|"Producer: "
operator|+
name|producerId
operator|.
name|getSessionId
argument_list|()
operator|+
literal|"-"
operator|+
name|producerId
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|writer
operator|.
name|print
argument_list|(
name|label
argument_list|)
expr_stmt|;
name|writer
operator|.
name|println
argument_list|(
literal|"\"];"
argument_list|)
expr_stmt|;
block|}
block|}
specifier|protected
name|void
name|printSubscribers
parameter_list|(
name|PrintWriter
name|writer
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|clients
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|destinations
parameter_list|,
name|String
name|type
parameter_list|,
name|ObjectName
index|[]
name|subscribers
parameter_list|)
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
name|subscribers
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|ObjectName
name|name
init|=
name|subscribers
index|[
name|i
index|]
decl_stmt|;
name|SubscriptionViewMBean
name|subscriber
init|=
operator|(
name|SubscriptionViewMBean
operator|)
name|getBrokerService
argument_list|()
operator|.
name|getManagementContext
argument_list|()
operator|.
name|newProxyInstance
argument_list|(
name|name
argument_list|,
name|SubscriptionViewMBean
operator|.
name|class
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|String
name|clientId
init|=
name|subscriber
operator|.
name|getClientId
argument_list|()
decl_stmt|;
name|String
name|safeClientId
init|=
name|asID
argument_list|(
name|clientId
argument_list|)
decl_stmt|;
name|clients
operator|.
name|put
argument_list|(
name|safeClientId
argument_list|,
name|clientId
argument_list|)
expr_stmt|;
name|String
name|destination
init|=
name|subscriber
operator|.
name|getDestinationName
argument_list|()
decl_stmt|;
name|String
name|safeDestinationId
init|=
name|type
operator|+
name|asID
argument_list|(
name|destination
argument_list|)
decl_stmt|;
name|destinations
operator|.
name|put
argument_list|(
name|safeDestinationId
argument_list|,
name|destination
argument_list|)
expr_stmt|;
name|String
name|selector
init|=
name|subscriber
operator|.
name|getSelector
argument_list|()
decl_stmt|;
comment|// lets write out the links
name|String
name|subscriberId
init|=
name|safeClientId
operator|+
literal|"_"
operator|+
name|subscriber
operator|.
name|getSessionId
argument_list|()
operator|+
literal|"_"
operator|+
name|subscriber
operator|.
name|getSubscriptionId
argument_list|()
decl_stmt|;
name|writer
operator|.
name|print
argument_list|(
name|subscriberId
argument_list|)
expr_stmt|;
name|writer
operator|.
name|print
argument_list|(
literal|" -> "
argument_list|)
expr_stmt|;
name|writer
operator|.
name|print
argument_list|(
name|safeClientId
argument_list|)
expr_stmt|;
name|writer
operator|.
name|println
argument_list|(
literal|";"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|print
argument_list|(
name|safeDestinationId
argument_list|)
expr_stmt|;
name|writer
operator|.
name|print
argument_list|(
literal|" -> "
argument_list|)
expr_stmt|;
name|writer
operator|.
name|print
argument_list|(
name|subscriberId
argument_list|)
expr_stmt|;
name|writer
operator|.
name|println
argument_list|(
literal|";"
argument_list|)
expr_stmt|;
comment|// now lets write out the label
name|writer
operator|.
name|print
argument_list|(
name|subscriberId
argument_list|)
expr_stmt|;
name|writer
operator|.
name|print
argument_list|(
literal|" [label = \""
argument_list|)
expr_stmt|;
name|String
name|label
init|=
literal|"Subscription: "
operator|+
name|subscriber
operator|.
name|getSessionId
argument_list|()
operator|+
literal|"-"
operator|+
name|subscriber
operator|.
name|getSubscriptionId
argument_list|()
decl_stmt|;
if|if
condition|(
name|selector
operator|!=
literal|null
operator|&&
name|selector
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|label
operator|=
name|label
operator|+
literal|"\\nSelector: "
operator|+
name|selector
expr_stmt|;
block|}
name|writer
operator|.
name|print
argument_list|(
name|label
argument_list|)
expr_stmt|;
name|writer
operator|.
name|println
argument_list|(
literal|"\"];"
argument_list|)
expr_stmt|;
block|}
block|}
specifier|protected
name|void
name|writeLabels
parameter_list|(
name|PrintWriter
name|writer
parameter_list|,
name|String
name|color
parameter_list|,
name|String
name|prefix
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|map
parameter_list|)
block|{
for|for
control|(
name|Iterator
name|iter
init|=
name|map
operator|.
name|entrySet
argument_list|()
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
name|Map
operator|.
name|Entry
name|entry
init|=
operator|(
name|Map
operator|.
name|Entry
operator|)
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
name|String
name|id
init|=
operator|(
name|String
operator|)
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|String
name|label
init|=
operator|(
name|String
operator|)
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|writer
operator|.
name|print
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|writer
operator|.
name|print
argument_list|(
literal|" [ fillcolor = "
argument_list|)
expr_stmt|;
name|writer
operator|.
name|print
argument_list|(
name|color
argument_list|)
expr_stmt|;
name|writer
operator|.
name|print
argument_list|(
literal|", label = \""
argument_list|)
expr_stmt|;
name|writer
operator|.
name|print
argument_list|(
name|prefix
argument_list|)
expr_stmt|;
name|writer
operator|.
name|print
argument_list|(
name|label
argument_list|)
expr_stmt|;
name|writer
operator|.
name|println
argument_list|(
literal|"\"];"
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Lets strip out any non supported characters      */
specifier|protected
name|String
name|asID
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|StringBuffer
name|buffer
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
name|int
name|size
init|=
name|name
operator|.
name|length
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
name|size
condition|;
name|i
operator|++
control|)
block|{
name|char
name|ch
init|=
name|name
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|Character
operator|.
name|isLetterOrDigit
argument_list|(
name|ch
argument_list|)
operator|||
name|ch
operator|==
literal|'_'
condition|)
block|{
name|buffer
operator|.
name|append
argument_list|(
name|ch
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|buffer
operator|.
name|append
argument_list|(
literal|'_'
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|buffer
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|protected
name|void
name|printNodes
parameter_list|(
name|PrintWriter
name|writer
parameter_list|,
name|DestinationMapNode
name|node
parameter_list|,
name|String
name|prefix
parameter_list|)
block|{
name|String
name|path
init|=
name|getPath
argument_list|(
name|node
argument_list|)
decl_stmt|;
name|writer
operator|.
name|print
argument_list|(
literal|"  "
argument_list|)
expr_stmt|;
name|writer
operator|.
name|print
argument_list|(
name|prefix
argument_list|)
expr_stmt|;
name|writer
operator|.
name|print
argument_list|(
name|ID_SEPARATOR
argument_list|)
expr_stmt|;
name|writer
operator|.
name|print
argument_list|(
name|path
argument_list|)
expr_stmt|;
name|String
name|label
init|=
name|path
decl_stmt|;
if|if
condition|(
name|prefix
operator|.
name|equals
argument_list|(
literal|"topic"
argument_list|)
condition|)
block|{
name|label
operator|=
literal|"Topics"
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|prefix
operator|.
name|equals
argument_list|(
literal|"queue"
argument_list|)
condition|)
block|{
name|label
operator|=
literal|"Queues"
expr_stmt|;
block|}
name|writer
operator|.
name|print
argument_list|(
literal|"[ label = \""
argument_list|)
expr_stmt|;
name|writer
operator|.
name|print
argument_list|(
name|label
argument_list|)
expr_stmt|;
name|writer
operator|.
name|println
argument_list|(
literal|"\" ];"
argument_list|)
expr_stmt|;
name|Collection
name|children
init|=
name|node
operator|.
name|getChildren
argument_list|()
decl_stmt|;
for|for
control|(
name|Iterator
name|iter
init|=
name|children
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
name|DestinationMapNode
name|child
init|=
operator|(
name|DestinationMapNode
operator|)
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
name|printNodes
argument_list|(
name|writer
argument_list|,
name|child
argument_list|,
name|prefix
operator|+
name|ID_SEPARATOR
operator|+
name|path
argument_list|)
expr_stmt|;
block|}
block|}
specifier|protected
name|void
name|printNodeLinks
parameter_list|(
name|PrintWriter
name|writer
parameter_list|,
name|DestinationMapNode
name|node
parameter_list|,
name|String
name|prefix
parameter_list|)
block|{
name|String
name|path
init|=
name|getPath
argument_list|(
name|node
argument_list|)
decl_stmt|;
name|Collection
name|children
init|=
name|node
operator|.
name|getChildren
argument_list|()
decl_stmt|;
for|for
control|(
name|Iterator
name|iter
init|=
name|children
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
name|DestinationMapNode
name|child
init|=
operator|(
name|DestinationMapNode
operator|)
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
name|writer
operator|.
name|print
argument_list|(
literal|"  "
argument_list|)
expr_stmt|;
name|writer
operator|.
name|print
argument_list|(
name|prefix
argument_list|)
expr_stmt|;
name|writer
operator|.
name|print
argument_list|(
name|ID_SEPARATOR
argument_list|)
expr_stmt|;
name|writer
operator|.
name|print
argument_list|(
name|path
argument_list|)
expr_stmt|;
name|writer
operator|.
name|print
argument_list|(
literal|" -> "
argument_list|)
expr_stmt|;
name|writer
operator|.
name|print
argument_list|(
name|prefix
argument_list|)
expr_stmt|;
name|writer
operator|.
name|print
argument_list|(
name|ID_SEPARATOR
argument_list|)
expr_stmt|;
name|writer
operator|.
name|print
argument_list|(
name|path
argument_list|)
expr_stmt|;
name|writer
operator|.
name|print
argument_list|(
name|ID_SEPARATOR
argument_list|)
expr_stmt|;
name|writer
operator|.
name|print
argument_list|(
name|getPath
argument_list|(
name|child
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|println
argument_list|(
literal|";"
argument_list|)
expr_stmt|;
name|printNodeLinks
argument_list|(
name|writer
argument_list|,
name|child
argument_list|,
name|prefix
operator|+
name|ID_SEPARATOR
operator|+
name|path
argument_list|)
expr_stmt|;
block|}
block|}
specifier|protected
name|String
name|getPath
parameter_list|(
name|DestinationMapNode
name|node
parameter_list|)
block|{
name|String
name|path
init|=
name|node
operator|.
name|getPath
argument_list|()
decl_stmt|;
if|if
condition|(
name|path
operator|.
name|equals
argument_list|(
literal|"*"
argument_list|)
condition|)
block|{
return|return
literal|"root"
return|;
block|}
return|return
name|path
return|;
block|}
name|BrokerViewMBean
name|getBrokerView
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|this
operator|.
name|brokerView
operator|==
literal|null
condition|)
block|{
name|ObjectName
name|brokerName
init|=
name|getBrokerService
argument_list|()
operator|.
name|getBrokerObjectName
argument_list|()
decl_stmt|;
name|this
operator|.
name|brokerView
operator|=
operator|(
name|BrokerViewMBean
operator|)
name|getBrokerService
argument_list|()
operator|.
name|getManagementContext
argument_list|()
operator|.
name|newProxyInstance
argument_list|(
name|brokerName
argument_list|,
name|BrokerViewMBean
operator|.
name|class
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
return|return
name|this
operator|.
name|brokerView
return|;
block|}
block|}
end_class

end_unit

