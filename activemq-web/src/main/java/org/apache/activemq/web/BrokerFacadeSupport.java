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
name|web
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
name|Collection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
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
name|List
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
name|InstanceNotFoundException
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
name|javax
operator|.
name|management
operator|.
name|QueryExp
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|openmbean
operator|.
name|CompositeData
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|openmbean
operator|.
name|TabularData
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
name|ConnectionViewMBean
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
name|ConnectorViewMBean
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
name|DestinationViewMBean
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
name|DurableSubscriptionViewMBean
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
name|JobSchedulerViewMBean
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
name|ManagementContext
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
name|NetworkBridgeViewMBean
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
name|NetworkConnectorViewMBean
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
name|ProducerViewMBean
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
name|QueueViewMBean
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
name|jmx
operator|.
name|TopicViewMBean
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
name|web
operator|.
name|util
operator|.
name|ExceptionUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|springframework
operator|.
name|util
operator|.
name|StringUtils
import|;
end_import

begin_comment
comment|/**  * A useful base class for an implementation of {@link BrokerFacade}  *  *  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|BrokerFacadeSupport
implements|implements
name|BrokerFacade
block|{
specifier|public
specifier|abstract
name|ManagementContext
name|getManagementContext
parameter_list|()
function_decl|;
specifier|public
specifier|abstract
name|Set
name|queryNames
parameter_list|(
name|ObjectName
name|name
parameter_list|,
name|QueryExp
name|query
parameter_list|)
throws|throws
name|Exception
function_decl|;
specifier|public
specifier|abstract
name|Object
name|newProxyInstance
parameter_list|(
name|ObjectName
name|objectName
parameter_list|,
name|Class
name|interfaceClass
parameter_list|,
name|boolean
name|notificationBroadcaster
parameter_list|)
throws|throws
name|Exception
function_decl|;
annotation|@
name|Override
specifier|public
name|Collection
argument_list|<
name|QueueViewMBean
argument_list|>
name|getQueues
parameter_list|()
throws|throws
name|Exception
block|{
name|BrokerViewMBean
name|broker
init|=
name|getBrokerAdmin
argument_list|()
decl_stmt|;
if|if
condition|(
name|broker
operator|==
literal|null
condition|)
block|{
return|return
name|Collections
operator|.
name|EMPTY_LIST
return|;
block|}
name|ObjectName
index|[]
name|queues
init|=
name|broker
operator|.
name|getQueues
argument_list|()
decl_stmt|;
return|return
name|getManagedObjects
argument_list|(
name|queues
argument_list|,
name|QueueViewMBean
operator|.
name|class
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Collection
argument_list|<
name|TopicViewMBean
argument_list|>
name|getTopics
parameter_list|()
throws|throws
name|Exception
block|{
name|BrokerViewMBean
name|broker
init|=
name|getBrokerAdmin
argument_list|()
decl_stmt|;
if|if
condition|(
name|broker
operator|==
literal|null
condition|)
block|{
return|return
name|Collections
operator|.
name|EMPTY_LIST
return|;
block|}
name|ObjectName
index|[]
name|topics
init|=
name|broker
operator|.
name|getTopics
argument_list|()
decl_stmt|;
return|return
name|getManagedObjects
argument_list|(
name|topics
argument_list|,
name|TopicViewMBean
operator|.
name|class
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Collection
argument_list|<
name|SubscriptionViewMBean
argument_list|>
name|getTopicSubscribers
parameter_list|(
name|String
name|topicName
parameter_list|)
throws|throws
name|Exception
block|{
name|String
name|brokerName
init|=
name|getBrokerName
argument_list|()
decl_stmt|;
name|topicName
operator|=
name|StringUtils
operator|.
name|replace
argument_list|(
name|topicName
argument_list|,
literal|"\""
argument_list|,
literal|"_"
argument_list|)
expr_stmt|;
name|ObjectName
name|query
init|=
operator|new
name|ObjectName
argument_list|(
literal|"org.apache.activemq:type=Broker,brokerName="
operator|+
name|brokerName
operator|+
literal|",destinationType=Topic,destinationName="
operator|+
name|topicName
operator|+
literal|",endpoint=Consumer,*"
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|ObjectName
argument_list|>
name|queryResult
init|=
name|queryNames
argument_list|(
name|query
argument_list|,
literal|null
argument_list|)
decl_stmt|;
return|return
name|getManagedObjects
argument_list|(
name|queryResult
operator|.
name|toArray
argument_list|(
operator|new
name|ObjectName
index|[
name|queryResult
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|,
name|SubscriptionViewMBean
operator|.
name|class
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Collection
argument_list|<
name|SubscriptionViewMBean
argument_list|>
name|getNonDurableTopicSubscribers
parameter_list|()
throws|throws
name|Exception
block|{
name|BrokerViewMBean
name|broker
init|=
name|getBrokerAdmin
argument_list|()
decl_stmt|;
if|if
condition|(
name|broker
operator|==
literal|null
condition|)
block|{
return|return
name|Collections
operator|.
name|EMPTY_LIST
return|;
block|}
name|ObjectName
index|[]
name|subscribers
init|=
name|broker
operator|.
name|getTopicSubscribers
argument_list|()
decl_stmt|;
return|return
name|getManagedObjects
argument_list|(
name|subscribers
argument_list|,
name|SubscriptionViewMBean
operator|.
name|class
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Collection
argument_list|<
name|DurableSubscriptionViewMBean
argument_list|>
name|getDurableTopicSubscribers
parameter_list|()
throws|throws
name|Exception
block|{
name|BrokerViewMBean
name|broker
init|=
name|getBrokerAdmin
argument_list|()
decl_stmt|;
if|if
condition|(
name|broker
operator|==
literal|null
condition|)
block|{
return|return
name|Collections
operator|.
name|EMPTY_LIST
return|;
block|}
name|ObjectName
index|[]
name|subscribers
init|=
name|broker
operator|.
name|getDurableTopicSubscribers
argument_list|()
decl_stmt|;
return|return
name|getManagedObjects
argument_list|(
name|subscribers
argument_list|,
name|DurableSubscriptionViewMBean
operator|.
name|class
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Collection
argument_list|<
name|DurableSubscriptionViewMBean
argument_list|>
name|getInactiveDurableTopicSubscribers
parameter_list|()
throws|throws
name|Exception
block|{
name|BrokerViewMBean
name|broker
init|=
name|getBrokerAdmin
argument_list|()
decl_stmt|;
if|if
condition|(
name|broker
operator|==
literal|null
condition|)
block|{
return|return
name|Collections
operator|.
name|EMPTY_LIST
return|;
block|}
name|ObjectName
index|[]
name|subscribers
init|=
name|broker
operator|.
name|getInactiveDurableTopicSubscribers
argument_list|()
decl_stmt|;
return|return
name|getManagedObjects
argument_list|(
name|subscribers
argument_list|,
name|DurableSubscriptionViewMBean
operator|.
name|class
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|QueueViewMBean
name|getQueue
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|Exception
block|{
return|return
operator|(
name|QueueViewMBean
operator|)
name|getDestinationByName
argument_list|(
name|getQueues
argument_list|()
argument_list|,
name|name
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|TopicViewMBean
name|getTopic
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|Exception
block|{
return|return
operator|(
name|TopicViewMBean
operator|)
name|getDestinationByName
argument_list|(
name|getTopics
argument_list|()
argument_list|,
name|name
argument_list|)
return|;
block|}
specifier|protected
name|DestinationViewMBean
name|getDestinationByName
parameter_list|(
name|Collection
argument_list|<
name|?
extends|extends
name|DestinationViewMBean
argument_list|>
name|collection
parameter_list|,
name|String
name|name
parameter_list|)
block|{
name|Iterator
argument_list|<
name|?
extends|extends
name|DestinationViewMBean
argument_list|>
name|iter
init|=
name|collection
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|iter
operator|.
name|hasNext
argument_list|()
condition|)
block|{
try|try
block|{
name|DestinationViewMBean
name|destinationViewMBean
init|=
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|name
operator|.
name|equals
argument_list|(
name|destinationViewMBean
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
return|return
name|destinationViewMBean
return|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
if|if
condition|(
operator|!
name|ExceptionUtils
operator|.
name|isRootCause
argument_list|(
name|ex
argument_list|,
name|InstanceNotFoundException
operator|.
name|class
argument_list|)
condition|)
block|{
comment|// Only throw if not an expected InstanceNotFoundException exception
throw|throw
name|ex
throw|;
block|}
block|}
block|}
return|return
literal|null
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
specifier|protected
parameter_list|<
name|T
parameter_list|>
name|Collection
argument_list|<
name|T
argument_list|>
name|getManagedObjects
parameter_list|(
name|ObjectName
index|[]
name|names
parameter_list|,
name|Class
argument_list|<
name|T
argument_list|>
name|type
parameter_list|)
throws|throws
name|Exception
block|{
name|List
argument_list|<
name|T
argument_list|>
name|answer
init|=
operator|new
name|ArrayList
argument_list|<
name|T
argument_list|>
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
name|names
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
name|names
index|[
name|i
index|]
decl_stmt|;
name|T
name|value
init|=
operator|(
name|T
operator|)
name|newProxyInstance
argument_list|(
name|name
argument_list|,
name|type
argument_list|,
literal|true
argument_list|)
decl_stmt|;
if|if
condition|(
name|value
operator|!=
literal|null
condition|)
block|{
name|answer
operator|.
name|add
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|answer
return|;
block|}
annotation|@
name|Override
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
specifier|public
name|Collection
argument_list|<
name|ConnectionViewMBean
argument_list|>
name|getConnections
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|brokerName
init|=
name|getBrokerName
argument_list|()
decl_stmt|;
name|ObjectName
name|query
init|=
operator|new
name|ObjectName
argument_list|(
literal|"org.apache.activemq:type=Broker,brokerName="
operator|+
name|brokerName
operator|+
literal|",connector=clientConnectors,connectorName=*,connectionName=*"
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|ObjectName
argument_list|>
name|queryResult
init|=
name|queryNames
argument_list|(
name|query
argument_list|,
literal|null
argument_list|)
decl_stmt|;
return|return
name|getManagedObjects
argument_list|(
name|queryResult
operator|.
name|toArray
argument_list|(
operator|new
name|ObjectName
index|[
name|queryResult
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|,
name|ConnectionViewMBean
operator|.
name|class
argument_list|)
return|;
block|}
annotation|@
name|Override
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
specifier|public
name|Collection
argument_list|<
name|String
argument_list|>
name|getConnections
parameter_list|(
name|String
name|connectorName
parameter_list|)
throws|throws
name|Exception
block|{
name|String
name|brokerName
init|=
name|getBrokerName
argument_list|()
decl_stmt|;
name|ObjectName
name|query
init|=
operator|new
name|ObjectName
argument_list|(
literal|"org.apache.activemq:type=Broker,brokerName="
operator|+
name|brokerName
operator|+
literal|",connector=clientConnectors,connectorName="
operator|+
name|connectorName
operator|+
literal|",connectionViewType=clientId"
operator|+
literal|",connectionName=*"
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|ObjectName
argument_list|>
name|queryResult
init|=
name|queryNames
argument_list|(
name|query
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|Collection
argument_list|<
name|String
argument_list|>
name|result
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|(
name|queryResult
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|ObjectName
name|on
range|:
name|queryResult
control|)
block|{
name|String
name|name
init|=
name|StringUtils
operator|.
name|replace
argument_list|(
name|on
operator|.
name|getKeyProperty
argument_list|(
literal|"connectionName"
argument_list|)
argument_list|,
literal|"_"
argument_list|,
literal|":"
argument_list|)
decl_stmt|;
name|result
operator|.
name|add
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
annotation|@
name|Override
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
specifier|public
name|ConnectionViewMBean
name|getConnection
parameter_list|(
name|String
name|connectionName
parameter_list|)
throws|throws
name|Exception
block|{
name|connectionName
operator|=
name|StringUtils
operator|.
name|replace
argument_list|(
name|connectionName
argument_list|,
literal|":"
argument_list|,
literal|"_"
argument_list|)
expr_stmt|;
name|String
name|brokerName
init|=
name|getBrokerName
argument_list|()
decl_stmt|;
name|ObjectName
name|query
init|=
operator|new
name|ObjectName
argument_list|(
literal|"org.apache.activemq:type=Broker,brokerName="
operator|+
name|brokerName
operator|+
literal|",connector=clientConnectors,*,connectionName="
operator|+
name|connectionName
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|ObjectName
argument_list|>
name|queryResult
init|=
name|queryNames
argument_list|(
name|query
argument_list|,
literal|null
argument_list|)
decl_stmt|;
if|if
condition|(
name|queryResult
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
return|return
literal|null
return|;
name|ObjectName
name|objectName
init|=
name|queryResult
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
decl_stmt|;
return|return
operator|(
name|ConnectionViewMBean
operator|)
name|newProxyInstance
argument_list|(
name|objectName
argument_list|,
name|ConnectionViewMBean
operator|.
name|class
argument_list|,
literal|true
argument_list|)
return|;
block|}
annotation|@
name|Override
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
specifier|public
name|Collection
argument_list|<
name|String
argument_list|>
name|getConnectors
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|brokerName
init|=
name|getBrokerName
argument_list|()
decl_stmt|;
name|ObjectName
name|query
init|=
operator|new
name|ObjectName
argument_list|(
literal|"org.apache.activemq:type=Broker,brokerName="
operator|+
name|brokerName
operator|+
literal|",connector=clientConnectors,connectorName=*"
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|ObjectName
argument_list|>
name|queryResult
init|=
name|queryNames
argument_list|(
name|query
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|Collection
argument_list|<
name|String
argument_list|>
name|result
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|(
name|queryResult
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|ObjectName
name|on
range|:
name|queryResult
control|)
name|result
operator|.
name|add
argument_list|(
name|on
operator|.
name|getKeyProperty
argument_list|(
literal|"connectorName"
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
annotation|@
name|Override
specifier|public
name|ConnectorViewMBean
name|getConnector
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|Exception
block|{
name|String
name|brokerName
init|=
name|getBrokerName
argument_list|()
decl_stmt|;
name|ObjectName
name|objectName
init|=
operator|new
name|ObjectName
argument_list|(
literal|"org.apache.activemq:type=Broker,brokerName="
operator|+
name|brokerName
operator|+
literal|",connector=clientConnectors,connectorName="
operator|+
name|name
argument_list|)
decl_stmt|;
return|return
operator|(
name|ConnectorViewMBean
operator|)
name|newProxyInstance
argument_list|(
name|objectName
argument_list|,
name|ConnectorViewMBean
operator|.
name|class
argument_list|,
literal|true
argument_list|)
return|;
block|}
annotation|@
name|Override
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
specifier|public
name|Collection
argument_list|<
name|NetworkConnectorViewMBean
argument_list|>
name|getNetworkConnectors
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|brokerName
init|=
name|getBrokerName
argument_list|()
decl_stmt|;
name|ObjectName
name|query
init|=
operator|new
name|ObjectName
argument_list|(
literal|"org.apache.activemq:type=Broker,brokerName="
operator|+
name|brokerName
operator|+
literal|",connector=networkConnectors,networkConnectorName=*"
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|ObjectName
argument_list|>
name|queryResult
init|=
name|queryNames
argument_list|(
name|query
argument_list|,
literal|null
argument_list|)
decl_stmt|;
return|return
name|getManagedObjects
argument_list|(
name|queryResult
operator|.
name|toArray
argument_list|(
operator|new
name|ObjectName
index|[
name|queryResult
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|,
name|NetworkConnectorViewMBean
operator|.
name|class
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Collection
argument_list|<
name|NetworkBridgeViewMBean
argument_list|>
name|getNetworkBridges
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|brokerName
init|=
name|getBrokerName
argument_list|()
decl_stmt|;
name|ObjectName
name|query
init|=
operator|new
name|ObjectName
argument_list|(
literal|"org.apache.activemq:type=Broker,brokerName="
operator|+
name|brokerName
operator|+
literal|",connector=*,networkConnectorName=*,networkBridge=*"
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|ObjectName
argument_list|>
name|queryResult
init|=
name|queryNames
argument_list|(
name|query
argument_list|,
literal|null
argument_list|)
decl_stmt|;
return|return
name|getManagedObjects
argument_list|(
name|queryResult
operator|.
name|toArray
argument_list|(
operator|new
name|ObjectName
index|[
name|queryResult
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|,
name|NetworkBridgeViewMBean
operator|.
name|class
argument_list|)
return|;
block|}
annotation|@
name|Override
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
specifier|public
name|Collection
argument_list|<
name|SubscriptionViewMBean
argument_list|>
name|getQueueConsumers
parameter_list|(
name|String
name|queueName
parameter_list|)
throws|throws
name|Exception
block|{
name|String
name|brokerName
init|=
name|getBrokerName
argument_list|()
decl_stmt|;
name|queueName
operator|=
name|StringUtils
operator|.
name|replace
argument_list|(
name|queueName
argument_list|,
literal|"\""
argument_list|,
literal|"_"
argument_list|)
expr_stmt|;
name|ObjectName
name|query
init|=
operator|new
name|ObjectName
argument_list|(
literal|"org.apache.activemq:type=Broker,brokerName="
operator|+
name|brokerName
operator|+
literal|",destinationType=Queue,destinationName="
operator|+
name|queueName
operator|+
literal|",endpoint=Consumer,*"
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|ObjectName
argument_list|>
name|queryResult
init|=
name|queryNames
argument_list|(
name|query
argument_list|,
literal|null
argument_list|)
decl_stmt|;
return|return
name|getManagedObjects
argument_list|(
name|queryResult
operator|.
name|toArray
argument_list|(
operator|new
name|ObjectName
index|[
name|queryResult
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|,
name|SubscriptionViewMBean
operator|.
name|class
argument_list|)
return|;
block|}
annotation|@
name|Override
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
specifier|public
name|Collection
argument_list|<
name|ProducerViewMBean
argument_list|>
name|getQueueProducers
parameter_list|(
name|String
name|queueName
parameter_list|)
throws|throws
name|Exception
block|{
name|String
name|brokerName
init|=
name|getBrokerName
argument_list|()
decl_stmt|;
name|queueName
operator|=
name|StringUtils
operator|.
name|replace
argument_list|(
name|queueName
argument_list|,
literal|"\""
argument_list|,
literal|"_"
argument_list|)
expr_stmt|;
name|ObjectName
name|query
init|=
operator|new
name|ObjectName
argument_list|(
literal|"org.apache.activemq:type=Broker,brokerName="
operator|+
name|brokerName
operator|+
literal|",destinationType=Queue,destinationName="
operator|+
name|queueName
operator|+
literal|",endpoint=Producer,*"
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|ObjectName
argument_list|>
name|queryResult
init|=
name|queryNames
argument_list|(
name|query
argument_list|,
literal|null
argument_list|)
decl_stmt|;
return|return
name|getManagedObjects
argument_list|(
name|queryResult
operator|.
name|toArray
argument_list|(
operator|new
name|ObjectName
index|[
name|queryResult
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|,
name|ProducerViewMBean
operator|.
name|class
argument_list|)
return|;
block|}
annotation|@
name|Override
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
specifier|public
name|Collection
argument_list|<
name|ProducerViewMBean
argument_list|>
name|getTopicProducers
parameter_list|(
name|String
name|topicName
parameter_list|)
throws|throws
name|Exception
block|{
name|String
name|brokerName
init|=
name|getBrokerName
argument_list|()
decl_stmt|;
name|topicName
operator|=
name|StringUtils
operator|.
name|replace
argument_list|(
name|topicName
argument_list|,
literal|"\""
argument_list|,
literal|"_"
argument_list|)
expr_stmt|;
name|ObjectName
name|query
init|=
operator|new
name|ObjectName
argument_list|(
literal|"org.apache.activemq:type=Broker,brokerName="
operator|+
name|brokerName
operator|+
literal|",destinationType=Topic,destinationName="
operator|+
name|topicName
operator|+
literal|",endpoint=Producer,*"
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|ObjectName
argument_list|>
name|queryResult
init|=
name|queryNames
argument_list|(
name|query
argument_list|,
literal|null
argument_list|)
decl_stmt|;
return|return
name|getManagedObjects
argument_list|(
name|queryResult
operator|.
name|toArray
argument_list|(
operator|new
name|ObjectName
index|[
name|queryResult
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|,
name|ProducerViewMBean
operator|.
name|class
argument_list|)
return|;
block|}
annotation|@
name|Override
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
specifier|public
name|Collection
argument_list|<
name|SubscriptionViewMBean
argument_list|>
name|getConsumersOnConnection
parameter_list|(
name|String
name|connectionName
parameter_list|)
throws|throws
name|Exception
block|{
name|connectionName
operator|=
name|StringUtils
operator|.
name|replace
argument_list|(
name|connectionName
argument_list|,
literal|":"
argument_list|,
literal|"_"
argument_list|)
expr_stmt|;
name|String
name|brokerName
init|=
name|getBrokerName
argument_list|()
decl_stmt|;
name|ObjectName
name|query
init|=
operator|new
name|ObjectName
argument_list|(
literal|"org.apache.activemq:type=Broker,brokerName="
operator|+
name|brokerName
operator|+
literal|",*,endpoint=Consumer,clientId="
operator|+
name|connectionName
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|ObjectName
argument_list|>
name|queryResult
init|=
name|queryNames
argument_list|(
name|query
argument_list|,
literal|null
argument_list|)
decl_stmt|;
return|return
name|getManagedObjects
argument_list|(
name|queryResult
operator|.
name|toArray
argument_list|(
operator|new
name|ObjectName
index|[
name|queryResult
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|,
name|SubscriptionViewMBean
operator|.
name|class
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|JobSchedulerViewMBean
name|getJobScheduler
parameter_list|()
throws|throws
name|Exception
block|{
name|ObjectName
name|name
init|=
name|getBrokerAdmin
argument_list|()
operator|.
name|getJMSJobScheduler
argument_list|()
decl_stmt|;
return|return
operator|(
name|JobSchedulerViewMBean
operator|)
name|newProxyInstance
argument_list|(
name|name
argument_list|,
name|JobSchedulerViewMBean
operator|.
name|class
argument_list|,
literal|true
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Collection
argument_list|<
name|JobFacade
argument_list|>
name|getScheduledJobs
parameter_list|()
throws|throws
name|Exception
block|{
name|JobSchedulerViewMBean
name|jobScheduler
init|=
name|getJobScheduler
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|JobFacade
argument_list|>
name|result
init|=
operator|new
name|ArrayList
argument_list|<
name|JobFacade
argument_list|>
argument_list|()
decl_stmt|;
name|TabularData
name|table
init|=
name|jobScheduler
operator|.
name|getAllJobs
argument_list|()
decl_stmt|;
for|for
control|(
name|Object
name|object
range|:
name|table
operator|.
name|values
argument_list|()
control|)
block|{
name|CompositeData
name|cd
init|=
operator|(
name|CompositeData
operator|)
name|object
decl_stmt|;
name|JobFacade
name|jf
init|=
operator|new
name|JobFacade
argument_list|(
name|cd
argument_list|)
decl_stmt|;
name|result
operator|.
name|add
argument_list|(
name|jf
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isJobSchedulerStarted
parameter_list|()
block|{
try|try
block|{
name|JobSchedulerViewMBean
name|jobScheduler
init|=
name|getJobScheduler
argument_list|()
decl_stmt|;
return|return
literal|true
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
block|}
block|}
end_class

end_unit

