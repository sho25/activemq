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
name|jmx
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
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
name|lang
operator|.
name|reflect
operator|.
name|InvocationTargetException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Method
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|MalformedURLException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URI
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URL
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
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|NoSuchElementException
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
name|AtomicInteger
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
name|ActiveMQConnectionMetaData
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
name|TransportConnector
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
name|ConsumerId
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
name|RemoveSubscriptionInfo
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
name|network
operator|.
name|NetworkConnector
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
name|BrokerSupport
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
comment|/**  *  */
end_comment

begin_class
specifier|public
class|class
name|BrokerView
implements|implements
name|BrokerViewMBean
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
name|BrokerView
operator|.
name|class
argument_list|)
decl_stmt|;
name|ManagedRegionBroker
name|broker
decl_stmt|;
specifier|private
specifier|final
name|BrokerService
name|brokerService
decl_stmt|;
specifier|private
specifier|final
name|AtomicInteger
name|sessionIdCounter
init|=
operator|new
name|AtomicInteger
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|private
name|ObjectName
name|jmsJobScheduler
decl_stmt|;
specifier|public
name|BrokerView
parameter_list|(
name|BrokerService
name|brokerService
parameter_list|,
name|ManagedRegionBroker
name|managedBroker
parameter_list|)
throws|throws
name|Exception
block|{
name|this
operator|.
name|brokerService
operator|=
name|brokerService
expr_stmt|;
name|this
operator|.
name|broker
operator|=
name|managedBroker
expr_stmt|;
block|}
specifier|public
name|ManagedRegionBroker
name|getBroker
parameter_list|()
block|{
return|return
name|broker
return|;
block|}
specifier|public
name|void
name|setBroker
parameter_list|(
name|ManagedRegionBroker
name|broker
parameter_list|)
block|{
name|this
operator|.
name|broker
operator|=
name|broker
expr_stmt|;
block|}
specifier|public
name|String
name|getBrokerId
parameter_list|()
block|{
return|return
name|safeGetBroker
argument_list|()
operator|.
name|getBrokerId
argument_list|()
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|public
name|String
name|getBrokerName
parameter_list|()
block|{
return|return
name|safeGetBroker
argument_list|()
operator|.
name|getBrokerName
argument_list|()
return|;
block|}
specifier|public
name|String
name|getBrokerVersion
parameter_list|()
block|{
return|return
name|ActiveMQConnectionMetaData
operator|.
name|PROVIDER_VERSION
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getUptime
parameter_list|()
block|{
return|return
name|brokerService
operator|.
name|getUptime
argument_list|()
return|;
block|}
specifier|public
name|void
name|gc
parameter_list|()
throws|throws
name|Exception
block|{
name|brokerService
operator|.
name|getBroker
argument_list|()
operator|.
name|gc
argument_list|()
expr_stmt|;
try|try
block|{
name|brokerService
operator|.
name|getPersistenceAdapter
argument_list|()
operator|.
name|checkpoint
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Failed to checkpoint persistence adapter on gc request, reason:"
operator|+
name|e
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|start
parameter_list|()
throws|throws
name|Exception
block|{
name|brokerService
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|stop
parameter_list|()
throws|throws
name|Exception
block|{
name|brokerService
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|stopGracefully
parameter_list|(
name|String
name|connectorName
parameter_list|,
name|String
name|queueName
parameter_list|,
name|long
name|timeout
parameter_list|,
name|long
name|pollInterval
parameter_list|)
throws|throws
name|Exception
block|{
name|brokerService
operator|.
name|stopGracefully
argument_list|(
name|connectorName
argument_list|,
name|queueName
argument_list|,
name|timeout
argument_list|,
name|pollInterval
argument_list|)
expr_stmt|;
block|}
specifier|public
name|long
name|getTotalEnqueueCount
parameter_list|()
block|{
return|return
name|safeGetBroker
argument_list|()
operator|.
name|getDestinationStatistics
argument_list|()
operator|.
name|getEnqueues
argument_list|()
operator|.
name|getCount
argument_list|()
return|;
block|}
specifier|public
name|long
name|getTotalDequeueCount
parameter_list|()
block|{
return|return
name|safeGetBroker
argument_list|()
operator|.
name|getDestinationStatistics
argument_list|()
operator|.
name|getDequeues
argument_list|()
operator|.
name|getCount
argument_list|()
return|;
block|}
specifier|public
name|long
name|getTotalConsumerCount
parameter_list|()
block|{
return|return
name|safeGetBroker
argument_list|()
operator|.
name|getDestinationStatistics
argument_list|()
operator|.
name|getConsumers
argument_list|()
operator|.
name|getCount
argument_list|()
return|;
block|}
specifier|public
name|long
name|getTotalProducerCount
parameter_list|()
block|{
return|return
name|safeGetBroker
argument_list|()
operator|.
name|getDestinationStatistics
argument_list|()
operator|.
name|getProducers
argument_list|()
operator|.
name|getCount
argument_list|()
return|;
block|}
specifier|public
name|long
name|getTotalMessageCount
parameter_list|()
block|{
return|return
name|safeGetBroker
argument_list|()
operator|.
name|getDestinationStatistics
argument_list|()
operator|.
name|getMessages
argument_list|()
operator|.
name|getCount
argument_list|()
return|;
block|}
specifier|public
name|long
name|getTotalMessagesCached
parameter_list|()
block|{
return|return
name|safeGetBroker
argument_list|()
operator|.
name|getDestinationStatistics
argument_list|()
operator|.
name|getMessagesCached
argument_list|()
operator|.
name|getCount
argument_list|()
return|;
block|}
specifier|public
name|int
name|getMemoryPercentUsage
parameter_list|()
block|{
return|return
name|brokerService
operator|.
name|getSystemUsage
argument_list|()
operator|.
name|getMemoryUsage
argument_list|()
operator|.
name|getPercentUsage
argument_list|()
return|;
block|}
specifier|public
name|long
name|getMemoryLimit
parameter_list|()
block|{
return|return
name|brokerService
operator|.
name|getSystemUsage
argument_list|()
operator|.
name|getMemoryUsage
argument_list|()
operator|.
name|getLimit
argument_list|()
return|;
block|}
specifier|public
name|void
name|setMemoryLimit
parameter_list|(
name|long
name|limit
parameter_list|)
block|{
name|brokerService
operator|.
name|getSystemUsage
argument_list|()
operator|.
name|getMemoryUsage
argument_list|()
operator|.
name|setLimit
argument_list|(
name|limit
argument_list|)
expr_stmt|;
block|}
specifier|public
name|long
name|getStoreLimit
parameter_list|()
block|{
return|return
name|brokerService
operator|.
name|getSystemUsage
argument_list|()
operator|.
name|getStoreUsage
argument_list|()
operator|.
name|getLimit
argument_list|()
return|;
block|}
specifier|public
name|int
name|getStorePercentUsage
parameter_list|()
block|{
return|return
name|brokerService
operator|.
name|getSystemUsage
argument_list|()
operator|.
name|getStoreUsage
argument_list|()
operator|.
name|getPercentUsage
argument_list|()
return|;
block|}
specifier|public
name|long
name|getTempLimit
parameter_list|()
block|{
return|return
name|brokerService
operator|.
name|getSystemUsage
argument_list|()
operator|.
name|getTempUsage
argument_list|()
operator|.
name|getLimit
argument_list|()
return|;
block|}
specifier|public
name|int
name|getTempPercentUsage
parameter_list|()
block|{
return|return
name|brokerService
operator|.
name|getSystemUsage
argument_list|()
operator|.
name|getTempUsage
argument_list|()
operator|.
name|getPercentUsage
argument_list|()
return|;
block|}
specifier|public
name|void
name|setStoreLimit
parameter_list|(
name|long
name|limit
parameter_list|)
block|{
name|brokerService
operator|.
name|getSystemUsage
argument_list|()
operator|.
name|getStoreUsage
argument_list|()
operator|.
name|setLimit
argument_list|(
name|limit
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setTempLimit
parameter_list|(
name|long
name|limit
parameter_list|)
block|{
name|brokerService
operator|.
name|getSystemUsage
argument_list|()
operator|.
name|getTempUsage
argument_list|()
operator|.
name|setLimit
argument_list|(
name|limit
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|resetStatistics
parameter_list|()
block|{
name|safeGetBroker
argument_list|()
operator|.
name|getDestinationStatistics
argument_list|()
operator|.
name|reset
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|enableStatistics
parameter_list|()
block|{
name|safeGetBroker
argument_list|()
operator|.
name|getDestinationStatistics
argument_list|()
operator|.
name|setEnabled
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|disableStatistics
parameter_list|()
block|{
name|safeGetBroker
argument_list|()
operator|.
name|getDestinationStatistics
argument_list|()
operator|.
name|setEnabled
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
specifier|public
name|boolean
name|isStatisticsEnabled
parameter_list|()
block|{
return|return
name|safeGetBroker
argument_list|()
operator|.
name|getDestinationStatistics
argument_list|()
operator|.
name|isEnabled
argument_list|()
return|;
block|}
specifier|public
name|boolean
name|isPersistent
parameter_list|()
block|{
return|return
name|brokerService
operator|.
name|isPersistent
argument_list|()
return|;
block|}
specifier|public
name|void
name|terminateJVM
parameter_list|(
name|int
name|exitCode
parameter_list|)
block|{
name|System
operator|.
name|exit
argument_list|(
name|exitCode
argument_list|)
expr_stmt|;
block|}
specifier|public
name|ObjectName
index|[]
name|getTopics
parameter_list|()
block|{
return|return
name|safeGetBroker
argument_list|()
operator|.
name|getTopics
argument_list|()
return|;
block|}
specifier|public
name|ObjectName
index|[]
name|getQueues
parameter_list|()
block|{
return|return
name|safeGetBroker
argument_list|()
operator|.
name|getQueues
argument_list|()
return|;
block|}
specifier|public
name|ObjectName
index|[]
name|getTemporaryTopics
parameter_list|()
block|{
return|return
name|safeGetBroker
argument_list|()
operator|.
name|getTemporaryTopics
argument_list|()
return|;
block|}
specifier|public
name|ObjectName
index|[]
name|getTemporaryQueues
parameter_list|()
block|{
return|return
name|safeGetBroker
argument_list|()
operator|.
name|getTemporaryQueues
argument_list|()
return|;
block|}
specifier|public
name|ObjectName
index|[]
name|getTopicSubscribers
parameter_list|()
block|{
return|return
name|safeGetBroker
argument_list|()
operator|.
name|getTopicSubscribers
argument_list|()
return|;
block|}
specifier|public
name|ObjectName
index|[]
name|getDurableTopicSubscribers
parameter_list|()
block|{
return|return
name|safeGetBroker
argument_list|()
operator|.
name|getDurableTopicSubscribers
argument_list|()
return|;
block|}
specifier|public
name|ObjectName
index|[]
name|getQueueSubscribers
parameter_list|()
block|{
return|return
name|safeGetBroker
argument_list|()
operator|.
name|getQueueSubscribers
argument_list|()
return|;
block|}
specifier|public
name|ObjectName
index|[]
name|getTemporaryTopicSubscribers
parameter_list|()
block|{
return|return
name|safeGetBroker
argument_list|()
operator|.
name|getTemporaryTopicSubscribers
argument_list|()
return|;
block|}
specifier|public
name|ObjectName
index|[]
name|getTemporaryQueueSubscribers
parameter_list|()
block|{
return|return
name|safeGetBroker
argument_list|()
operator|.
name|getTemporaryQueueSubscribers
argument_list|()
return|;
block|}
specifier|public
name|ObjectName
index|[]
name|getInactiveDurableTopicSubscribers
parameter_list|()
block|{
return|return
name|safeGetBroker
argument_list|()
operator|.
name|getInactiveDurableTopicSubscribers
argument_list|()
return|;
block|}
specifier|public
name|ObjectName
index|[]
name|getTopicProducers
parameter_list|()
block|{
return|return
name|safeGetBroker
argument_list|()
operator|.
name|getTopicProducers
argument_list|()
return|;
block|}
specifier|public
name|ObjectName
index|[]
name|getQueueProducers
parameter_list|()
block|{
return|return
name|safeGetBroker
argument_list|()
operator|.
name|getQueueProducers
argument_list|()
return|;
block|}
specifier|public
name|ObjectName
index|[]
name|getTemporaryTopicProducers
parameter_list|()
block|{
return|return
name|safeGetBroker
argument_list|()
operator|.
name|getTemporaryTopicProducers
argument_list|()
return|;
block|}
specifier|public
name|ObjectName
index|[]
name|getTemporaryQueueProducers
parameter_list|()
block|{
return|return
name|safeGetBroker
argument_list|()
operator|.
name|getTemporaryQueueProducers
argument_list|()
return|;
block|}
specifier|public
name|ObjectName
index|[]
name|getDynamicDestinationProducers
parameter_list|()
block|{
return|return
name|safeGetBroker
argument_list|()
operator|.
name|getDynamicDestinationProducers
argument_list|()
return|;
block|}
specifier|public
name|String
name|addConnector
parameter_list|(
name|String
name|discoveryAddress
parameter_list|)
throws|throws
name|Exception
block|{
name|TransportConnector
name|connector
init|=
name|brokerService
operator|.
name|addConnector
argument_list|(
name|discoveryAddress
argument_list|)
decl_stmt|;
if|if
condition|(
name|connector
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|NoSuchElementException
argument_list|(
literal|"Not connector matched the given name: "
operator|+
name|discoveryAddress
argument_list|)
throw|;
block|}
name|connector
operator|.
name|start
argument_list|()
expr_stmt|;
return|return
name|connector
operator|.
name|getName
argument_list|()
return|;
block|}
specifier|public
name|String
name|addNetworkConnector
parameter_list|(
name|String
name|discoveryAddress
parameter_list|)
throws|throws
name|Exception
block|{
name|NetworkConnector
name|connector
init|=
name|brokerService
operator|.
name|addNetworkConnector
argument_list|(
name|discoveryAddress
argument_list|)
decl_stmt|;
if|if
condition|(
name|connector
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|NoSuchElementException
argument_list|(
literal|"Not connector matched the given name: "
operator|+
name|discoveryAddress
argument_list|)
throw|;
block|}
name|connector
operator|.
name|start
argument_list|()
expr_stmt|;
return|return
name|connector
operator|.
name|getName
argument_list|()
return|;
block|}
specifier|public
name|boolean
name|removeConnector
parameter_list|(
name|String
name|connectorName
parameter_list|)
throws|throws
name|Exception
block|{
name|TransportConnector
name|connector
init|=
name|brokerService
operator|.
name|getConnectorByName
argument_list|(
name|connectorName
argument_list|)
decl_stmt|;
if|if
condition|(
name|connector
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|NoSuchElementException
argument_list|(
literal|"Not connector matched the given name: "
operator|+
name|connectorName
argument_list|)
throw|;
block|}
name|connector
operator|.
name|stop
argument_list|()
expr_stmt|;
return|return
name|brokerService
operator|.
name|removeConnector
argument_list|(
name|connector
argument_list|)
return|;
block|}
specifier|public
name|boolean
name|removeNetworkConnector
parameter_list|(
name|String
name|connectorName
parameter_list|)
throws|throws
name|Exception
block|{
name|NetworkConnector
name|connector
init|=
name|brokerService
operator|.
name|getNetworkConnectorByName
argument_list|(
name|connectorName
argument_list|)
decl_stmt|;
if|if
condition|(
name|connector
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|NoSuchElementException
argument_list|(
literal|"Not connector matched the given name: "
operator|+
name|connectorName
argument_list|)
throw|;
block|}
name|connector
operator|.
name|stop
argument_list|()
expr_stmt|;
return|return
name|brokerService
operator|.
name|removeNetworkConnector
argument_list|(
name|connector
argument_list|)
return|;
block|}
specifier|public
name|void
name|addTopic
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|Exception
block|{
name|safeGetBroker
argument_list|()
operator|.
name|getContextBroker
argument_list|()
operator|.
name|addDestination
argument_list|(
name|BrokerSupport
operator|.
name|getConnectionContext
argument_list|(
name|safeGetBroker
argument_list|()
operator|.
name|getContextBroker
argument_list|()
argument_list|)
argument_list|,
operator|new
name|ActiveMQTopic
argument_list|(
name|name
argument_list|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|addQueue
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|Exception
block|{
name|safeGetBroker
argument_list|()
operator|.
name|getContextBroker
argument_list|()
operator|.
name|addDestination
argument_list|(
name|BrokerSupport
operator|.
name|getConnectionContext
argument_list|(
name|safeGetBroker
argument_list|()
operator|.
name|getContextBroker
argument_list|()
argument_list|)
argument_list|,
operator|new
name|ActiveMQQueue
argument_list|(
name|name
argument_list|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|removeTopic
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|Exception
block|{
name|safeGetBroker
argument_list|()
operator|.
name|getContextBroker
argument_list|()
operator|.
name|removeDestination
argument_list|(
name|BrokerSupport
operator|.
name|getConnectionContext
argument_list|(
name|safeGetBroker
argument_list|()
operator|.
name|getContextBroker
argument_list|()
argument_list|)
argument_list|,
operator|new
name|ActiveMQTopic
argument_list|(
name|name
argument_list|)
argument_list|,
literal|1000
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|removeQueue
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|Exception
block|{
name|safeGetBroker
argument_list|()
operator|.
name|getContextBroker
argument_list|()
operator|.
name|removeDestination
argument_list|(
name|BrokerSupport
operator|.
name|getConnectionContext
argument_list|(
name|safeGetBroker
argument_list|()
operator|.
name|getContextBroker
argument_list|()
argument_list|)
argument_list|,
operator|new
name|ActiveMQQueue
argument_list|(
name|name
argument_list|)
argument_list|,
literal|1000
argument_list|)
expr_stmt|;
block|}
specifier|public
name|ObjectName
name|createDurableSubscriber
parameter_list|(
name|String
name|clientId
parameter_list|,
name|String
name|subscriberName
parameter_list|,
name|String
name|topicName
parameter_list|,
name|String
name|selector
parameter_list|)
throws|throws
name|Exception
block|{
name|ConnectionContext
name|context
init|=
operator|new
name|ConnectionContext
argument_list|()
decl_stmt|;
name|context
operator|.
name|setBroker
argument_list|(
name|safeGetBroker
argument_list|()
argument_list|)
expr_stmt|;
name|context
operator|.
name|setClientId
argument_list|(
name|clientId
argument_list|)
expr_stmt|;
name|ConsumerInfo
name|info
init|=
operator|new
name|ConsumerInfo
argument_list|()
decl_stmt|;
name|ConsumerId
name|consumerId
init|=
operator|new
name|ConsumerId
argument_list|()
decl_stmt|;
name|consumerId
operator|.
name|setConnectionId
argument_list|(
name|clientId
argument_list|)
expr_stmt|;
name|consumerId
operator|.
name|setSessionId
argument_list|(
name|sessionIdCounter
operator|.
name|incrementAndGet
argument_list|()
argument_list|)
expr_stmt|;
name|consumerId
operator|.
name|setValue
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|info
operator|.
name|setConsumerId
argument_list|(
name|consumerId
argument_list|)
expr_stmt|;
name|info
operator|.
name|setDestination
argument_list|(
operator|new
name|ActiveMQTopic
argument_list|(
name|topicName
argument_list|)
argument_list|)
expr_stmt|;
name|info
operator|.
name|setSubscriptionName
argument_list|(
name|subscriberName
argument_list|)
expr_stmt|;
name|info
operator|.
name|setSelector
argument_list|(
name|selector
argument_list|)
expr_stmt|;
name|Subscription
name|subscription
init|=
name|safeGetBroker
argument_list|()
operator|.
name|addConsumer
argument_list|(
name|context
argument_list|,
name|info
argument_list|)
decl_stmt|;
name|safeGetBroker
argument_list|()
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
name|subscription
operator|!=
literal|null
condition|)
block|{
return|return
name|subscription
operator|.
name|getObjectName
argument_list|()
return|;
block|}
return|return
literal|null
return|;
block|}
specifier|public
name|void
name|destroyDurableSubscriber
parameter_list|(
name|String
name|clientId
parameter_list|,
name|String
name|subscriberName
parameter_list|)
throws|throws
name|Exception
block|{
name|RemoveSubscriptionInfo
name|info
init|=
operator|new
name|RemoveSubscriptionInfo
argument_list|()
decl_stmt|;
name|info
operator|.
name|setClientId
argument_list|(
name|clientId
argument_list|)
expr_stmt|;
name|info
operator|.
name|setSubscriptionName
argument_list|(
name|subscriberName
argument_list|)
expr_stmt|;
name|ConnectionContext
name|context
init|=
operator|new
name|ConnectionContext
argument_list|()
decl_stmt|;
name|context
operator|.
name|setBroker
argument_list|(
name|safeGetBroker
argument_list|()
argument_list|)
expr_stmt|;
name|context
operator|.
name|setClientId
argument_list|(
name|clientId
argument_list|)
expr_stmt|;
name|safeGetBroker
argument_list|()
operator|.
name|removeSubscription
argument_list|(
name|context
argument_list|,
name|info
argument_list|)
expr_stmt|;
block|}
comment|//  doc comment inherited from BrokerViewMBean
specifier|public
name|void
name|reloadLog4jProperties
parameter_list|()
throws|throws
name|Throwable
block|{
comment|// Avoid a direct dependency on log4j.. use reflection.
try|try
block|{
name|ClassLoader
name|cl
init|=
name|getClass
argument_list|()
operator|.
name|getClassLoader
argument_list|()
decl_stmt|;
name|Class
argument_list|<
name|?
argument_list|>
name|logManagerClass
init|=
name|cl
operator|.
name|loadClass
argument_list|(
literal|"org.apache.log4j.LogManager"
argument_list|)
decl_stmt|;
name|Method
name|resetConfiguration
init|=
name|logManagerClass
operator|.
name|getMethod
argument_list|(
literal|"resetConfiguration"
argument_list|,
operator|new
name|Class
index|[]
block|{}
argument_list|)
decl_stmt|;
name|resetConfiguration
operator|.
name|invoke
argument_list|(
literal|null
argument_list|,
operator|new
name|Object
index|[]
block|{}
argument_list|)
expr_stmt|;
name|String
name|configurationOptionStr
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"log4j.configuration"
argument_list|)
decl_stmt|;
name|URL
name|log4jprops
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|configurationOptionStr
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|log4jprops
operator|=
operator|new
name|URL
argument_list|(
name|configurationOptionStr
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|MalformedURLException
name|ex
parameter_list|)
block|{
name|log4jprops
operator|=
name|cl
operator|.
name|getResource
argument_list|(
literal|"log4j.properties"
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|log4jprops
operator|=
name|cl
operator|.
name|getResource
argument_list|(
literal|"log4j.properties"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|log4jprops
operator|!=
literal|null
condition|)
block|{
name|Class
argument_list|<
name|?
argument_list|>
name|propertyConfiguratorClass
init|=
name|cl
operator|.
name|loadClass
argument_list|(
literal|"org.apache.log4j.PropertyConfigurator"
argument_list|)
decl_stmt|;
name|Method
name|configure
init|=
name|propertyConfiguratorClass
operator|.
name|getMethod
argument_list|(
literal|"configure"
argument_list|,
operator|new
name|Class
index|[]
block|{
name|URL
operator|.
name|class
block|}
argument_list|)
decl_stmt|;
name|configure
operator|.
name|invoke
argument_list|(
literal|null
argument_list|,
operator|new
name|Object
index|[]
block|{
name|log4jprops
block|}
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|InvocationTargetException
name|e
parameter_list|)
block|{
throw|throw
name|e
operator|.
name|getTargetException
argument_list|()
throw|;
block|}
block|}
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|getTransportConnectors
parameter_list|()
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|answer
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
try|try
block|{
for|for
control|(
name|TransportConnector
name|connector
range|:
name|brokerService
operator|.
name|getTransportConnectors
argument_list|()
control|)
block|{
name|answer
operator|.
name|put
argument_list|(
name|connector
operator|.
name|getName
argument_list|()
argument_list|,
name|connector
operator|.
name|getConnectUri
argument_list|()
operator|.
name|toString
argument_list|()
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
name|debug
argument_list|(
literal|"Failed to read URI to build transport connectors map"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
return|return
name|answer
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getTransportConnectorByType
parameter_list|(
name|String
name|type
parameter_list|)
block|{
return|return
name|brokerService
operator|.
name|getTransportConnectorURIsAsMap
argument_list|()
operator|.
name|get
argument_list|(
name|type
argument_list|)
return|;
block|}
annotation|@
name|Deprecated
comment|/**      * @deprecated use {@link #getTransportConnectors()} or {@link #getTransportConnectorByType(String)}      */
specifier|public
name|String
name|getOpenWireURL
parameter_list|()
block|{
name|String
name|answer
init|=
name|brokerService
operator|.
name|getTransportConnectorURIsAsMap
argument_list|()
operator|.
name|get
argument_list|(
literal|"tcp"
argument_list|)
decl_stmt|;
return|return
name|answer
operator|!=
literal|null
condition|?
name|answer
else|:
literal|""
return|;
block|}
annotation|@
name|Deprecated
comment|/**      * @deprecated use {@link #getTransportConnectors()} or {@link #getTransportConnectorByType(String)}      */
specifier|public
name|String
name|getStompURL
parameter_list|()
block|{
name|String
name|answer
init|=
name|brokerService
operator|.
name|getTransportConnectorURIsAsMap
argument_list|()
operator|.
name|get
argument_list|(
literal|"stomp"
argument_list|)
decl_stmt|;
return|return
name|answer
operator|!=
literal|null
condition|?
name|answer
else|:
literal|""
return|;
block|}
annotation|@
name|Deprecated
comment|/**      * @deprecated use {@link #getTransportConnectors()} or {@link #getTransportConnectorByType(String)}      */
specifier|public
name|String
name|getSslURL
parameter_list|()
block|{
name|String
name|answer
init|=
name|brokerService
operator|.
name|getTransportConnectorURIsAsMap
argument_list|()
operator|.
name|get
argument_list|(
literal|"ssl"
argument_list|)
decl_stmt|;
return|return
name|answer
operator|!=
literal|null
condition|?
name|answer
else|:
literal|""
return|;
block|}
annotation|@
name|Deprecated
comment|/**      * @deprecated use {@link #getTransportConnectors()} or {@link #getTransportConnectorByType(String)}      */
specifier|public
name|String
name|getStompSslURL
parameter_list|()
block|{
name|String
name|answer
init|=
name|brokerService
operator|.
name|getTransportConnectorURIsAsMap
argument_list|()
operator|.
name|get
argument_list|(
literal|"stomp+ssl"
argument_list|)
decl_stmt|;
return|return
name|answer
operator|!=
literal|null
condition|?
name|answer
else|:
literal|""
return|;
block|}
specifier|public
name|String
name|getVMURL
parameter_list|()
block|{
name|URI
name|answer
init|=
name|brokerService
operator|.
name|getVmConnectorURI
argument_list|()
decl_stmt|;
return|return
name|answer
operator|!=
literal|null
condition|?
name|answer
operator|.
name|toString
argument_list|()
else|:
literal|""
return|;
block|}
specifier|public
name|String
name|getDataDirectory
parameter_list|()
block|{
name|File
name|file
init|=
name|brokerService
operator|.
name|getDataDirectoryFile
argument_list|()
decl_stmt|;
try|try
block|{
return|return
name|file
operator|!=
literal|null
condition|?
name|file
operator|.
name|getCanonicalPath
argument_list|()
else|:
literal|""
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
return|return
literal|""
return|;
block|}
block|}
specifier|public
name|ObjectName
name|getJMSJobScheduler
parameter_list|()
block|{
return|return
name|this
operator|.
name|jmsJobScheduler
return|;
block|}
specifier|public
name|void
name|setJMSJobScheduler
parameter_list|(
name|ObjectName
name|name
parameter_list|)
block|{
name|this
operator|.
name|jmsJobScheduler
operator|=
name|name
expr_stmt|;
block|}
specifier|private
name|ManagedRegionBroker
name|safeGetBroker
parameter_list|()
block|{
if|if
condition|(
name|broker
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Broker is not yet started."
argument_list|)
throw|;
block|}
return|return
name|broker
return|;
block|}
block|}
end_class

end_unit

