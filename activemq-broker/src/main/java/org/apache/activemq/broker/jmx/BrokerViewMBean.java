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
name|util
operator|.
name|Map
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
name|Service
import|;
end_import

begin_comment
comment|/**  * @author David Martin Clavo david(dot)martin(dot)clavo(at)gmail.com (for the reloadLog4jProperties method)  *  */
end_comment

begin_interface
specifier|public
interface|interface
name|BrokerViewMBean
extends|extends
name|Service
block|{
comment|/**      * @return The unique id of the broker.      */
annotation|@
name|MBeanInfo
argument_list|(
literal|"The unique id of the broker."
argument_list|)
name|String
name|getBrokerId
parameter_list|()
function_decl|;
comment|/**      * @return The name of the broker.      */
annotation|@
name|MBeanInfo
argument_list|(
literal|"The name of the broker."
argument_list|)
name|String
name|getBrokerName
parameter_list|()
function_decl|;
comment|/**      * @return The name of the broker.      */
annotation|@
name|MBeanInfo
argument_list|(
literal|"The version of the broker."
argument_list|)
name|String
name|getBrokerVersion
parameter_list|()
function_decl|;
comment|/**      * @return Uptime of the broker.      */
annotation|@
name|MBeanInfo
argument_list|(
literal|"Uptime of the broker."
argument_list|)
name|String
name|getUptime
parameter_list|()
function_decl|;
comment|/**      * @return The current number of active connections on this Broker.      */
name|int
name|getCurrentConnectionsCount
parameter_list|()
function_decl|;
comment|/**      * @return The total number of connections serviced since this Broker was started.      */
name|long
name|getTotalConnectionsCount
parameter_list|()
function_decl|;
comment|/**      * The Broker will flush it's caches so that the garbage collector can      * reclaim more memory.      *      * @throws Exception      */
annotation|@
name|MBeanInfo
argument_list|(
literal|"Runs the Garbage Collector."
argument_list|)
name|void
name|gc
parameter_list|()
throws|throws
name|Exception
function_decl|;
annotation|@
name|MBeanInfo
argument_list|(
literal|"Reset all broker statistics."
argument_list|)
name|void
name|resetStatistics
parameter_list|()
function_decl|;
annotation|@
name|MBeanInfo
argument_list|(
literal|"Enable broker statistics."
argument_list|)
name|void
name|enableStatistics
parameter_list|()
function_decl|;
annotation|@
name|MBeanInfo
argument_list|(
literal|"Disable broker statistics."
argument_list|)
name|void
name|disableStatistics
parameter_list|()
function_decl|;
annotation|@
name|MBeanInfo
argument_list|(
literal|"Broker statistics enabled."
argument_list|)
name|boolean
name|isStatisticsEnabled
parameter_list|()
function_decl|;
annotation|@
name|MBeanInfo
argument_list|(
literal|"Number of messages that have been sent to the broker."
argument_list|)
name|long
name|getTotalEnqueueCount
parameter_list|()
function_decl|;
annotation|@
name|MBeanInfo
argument_list|(
literal|"Number of messages that have been acknowledged on the broker."
argument_list|)
name|long
name|getTotalDequeueCount
parameter_list|()
function_decl|;
annotation|@
name|MBeanInfo
argument_list|(
literal|"Number of message consumers subscribed to destinations on the broker."
argument_list|)
name|long
name|getTotalConsumerCount
parameter_list|()
function_decl|;
annotation|@
name|MBeanInfo
argument_list|(
literal|"Number of message producers active on destinations on the broker."
argument_list|)
name|long
name|getTotalProducerCount
parameter_list|()
function_decl|;
annotation|@
name|MBeanInfo
argument_list|(
literal|"Number of unacknowledged messages on the broker."
argument_list|)
name|long
name|getTotalMessageCount
parameter_list|()
function_decl|;
annotation|@
name|MBeanInfo
argument_list|(
literal|"Average message size on this broker"
argument_list|)
name|long
name|getAverageMessageSize
parameter_list|()
function_decl|;
annotation|@
name|MBeanInfo
argument_list|(
literal|"Max message size on this broker"
argument_list|)
specifier|public
name|long
name|getMaxMessageSize
parameter_list|()
function_decl|;
annotation|@
name|MBeanInfo
argument_list|(
literal|"Min message size on this broker"
argument_list|)
specifier|public
name|long
name|getMinMessageSize
parameter_list|()
function_decl|;
annotation|@
name|MBeanInfo
argument_list|(
literal|"Percent of memory limit used."
argument_list|)
name|int
name|getMemoryPercentUsage
parameter_list|()
function_decl|;
annotation|@
name|MBeanInfo
argument_list|(
literal|"Memory limit, in bytes, used for holding undelivered messages before paging to temporary storage."
argument_list|)
name|long
name|getMemoryLimit
parameter_list|()
function_decl|;
name|void
name|setMemoryLimit
parameter_list|(
annotation|@
name|MBeanInfo
argument_list|(
literal|"bytes"
argument_list|)
name|long
name|limit
parameter_list|)
function_decl|;
annotation|@
name|MBeanInfo
argument_list|(
literal|"Percent of store limit used."
argument_list|)
name|int
name|getStorePercentUsage
parameter_list|()
function_decl|;
annotation|@
name|MBeanInfo
argument_list|(
literal|"Disk limit, in bytes, used for persistent messages before producers are blocked."
argument_list|)
name|long
name|getStoreLimit
parameter_list|()
function_decl|;
name|void
name|setStoreLimit
parameter_list|(
annotation|@
name|MBeanInfo
argument_list|(
literal|"bytes"
argument_list|)
name|long
name|limit
parameter_list|)
function_decl|;
annotation|@
name|MBeanInfo
argument_list|(
literal|"Percent of temp limit used."
argument_list|)
name|int
name|getTempPercentUsage
parameter_list|()
function_decl|;
annotation|@
name|MBeanInfo
argument_list|(
literal|"Disk limit, in bytes, used for non-persistent messages and temporary data before producers are blocked."
argument_list|)
name|long
name|getTempLimit
parameter_list|()
function_decl|;
name|void
name|setTempLimit
parameter_list|(
annotation|@
name|MBeanInfo
argument_list|(
literal|"bytes"
argument_list|)
name|long
name|limit
parameter_list|)
function_decl|;
annotation|@
name|MBeanInfo
argument_list|(
literal|"Percent of job store limit used."
argument_list|)
name|int
name|getJobSchedulerStorePercentUsage
parameter_list|()
function_decl|;
annotation|@
name|MBeanInfo
argument_list|(
literal|"Disk limit, in bytes, used for scheduled messages before producers are blocked."
argument_list|)
name|long
name|getJobSchedulerStoreLimit
parameter_list|()
function_decl|;
name|void
name|setJobSchedulerStoreLimit
parameter_list|(
annotation|@
name|MBeanInfo
argument_list|(
literal|"bytes"
argument_list|)
name|long
name|limit
parameter_list|)
function_decl|;
annotation|@
name|MBeanInfo
argument_list|(
literal|"Messages are synchronized to disk."
argument_list|)
name|boolean
name|isPersistent
parameter_list|()
function_decl|;
annotation|@
name|MBeanInfo
argument_list|(
literal|"Slave broker."
argument_list|)
name|boolean
name|isSlave
parameter_list|()
function_decl|;
comment|/**      * Shuts down the JVM.      *      * @param exitCode the exit code that will be reported by the JVM process      *                when it exits.      */
annotation|@
name|MBeanInfo
argument_list|(
literal|"Shuts down the JVM."
argument_list|)
name|void
name|terminateJVM
parameter_list|(
annotation|@
name|MBeanInfo
argument_list|(
literal|"exitCode"
argument_list|)
name|int
name|exitCode
parameter_list|)
function_decl|;
comment|/**      * Stop the broker and all it's components.      */
annotation|@
name|Override
annotation|@
name|MBeanInfo
argument_list|(
literal|"Stop the broker and all its components."
argument_list|)
name|void
name|stop
parameter_list|()
throws|throws
name|Exception
function_decl|;
comment|/**      * Restart the broker and all it's components.      */
annotation|@
name|MBeanInfo
argument_list|(
literal|"Restart the broker and all its components."
argument_list|)
name|void
name|restart
parameter_list|()
throws|throws
name|Exception
function_decl|;
annotation|@
name|MBeanInfo
argument_list|(
literal|"Poll for queues matching queueName are empty before stopping"
argument_list|)
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
function_decl|;
annotation|@
name|MBeanInfo
argument_list|(
literal|"Topics (broadcasted 'queues'); generally system information."
argument_list|)
name|ObjectName
index|[]
name|getTopics
parameter_list|()
function_decl|;
annotation|@
name|MBeanInfo
argument_list|(
literal|"Standard Queues containing AIE messages."
argument_list|)
name|ObjectName
index|[]
name|getQueues
parameter_list|()
function_decl|;
annotation|@
name|MBeanInfo
argument_list|(
literal|"Temporary Topics; generally unused."
argument_list|)
name|ObjectName
index|[]
name|getTemporaryTopics
parameter_list|()
function_decl|;
annotation|@
name|MBeanInfo
argument_list|(
literal|"Temporary Queues; generally temporary message response holders."
argument_list|)
name|ObjectName
index|[]
name|getTemporaryQueues
parameter_list|()
function_decl|;
annotation|@
name|MBeanInfo
argument_list|(
literal|"Topic Subscribers"
argument_list|)
name|ObjectName
index|[]
name|getTopicSubscribers
parameter_list|()
function_decl|;
annotation|@
name|MBeanInfo
argument_list|(
literal|"Durable (persistent) topic subscribers"
argument_list|)
name|ObjectName
index|[]
name|getDurableTopicSubscribers
parameter_list|()
function_decl|;
annotation|@
name|MBeanInfo
argument_list|(
literal|"Inactive (disconnected persistent) topic subscribers"
argument_list|)
name|ObjectName
index|[]
name|getInactiveDurableTopicSubscribers
parameter_list|()
function_decl|;
annotation|@
name|MBeanInfo
argument_list|(
literal|"Queue Subscribers."
argument_list|)
name|ObjectName
index|[]
name|getQueueSubscribers
parameter_list|()
function_decl|;
annotation|@
name|MBeanInfo
argument_list|(
literal|"Temporary Topic Subscribers."
argument_list|)
name|ObjectName
index|[]
name|getTemporaryTopicSubscribers
parameter_list|()
function_decl|;
annotation|@
name|MBeanInfo
argument_list|(
literal|"Temporary Queue Subscribers."
argument_list|)
name|ObjectName
index|[]
name|getTemporaryQueueSubscribers
parameter_list|()
function_decl|;
annotation|@
name|MBeanInfo
argument_list|(
literal|"Topic Producers."
argument_list|)
specifier|public
name|ObjectName
index|[]
name|getTopicProducers
parameter_list|()
function_decl|;
annotation|@
name|MBeanInfo
argument_list|(
literal|"Queue Producers."
argument_list|)
specifier|public
name|ObjectName
index|[]
name|getQueueProducers
parameter_list|()
function_decl|;
annotation|@
name|MBeanInfo
argument_list|(
literal|"Temporary Topic Producers."
argument_list|)
specifier|public
name|ObjectName
index|[]
name|getTemporaryTopicProducers
parameter_list|()
function_decl|;
annotation|@
name|MBeanInfo
argument_list|(
literal|"Temporary Queue Producers."
argument_list|)
specifier|public
name|ObjectName
index|[]
name|getTemporaryQueueProducers
parameter_list|()
function_decl|;
annotation|@
name|MBeanInfo
argument_list|(
literal|"Dynamic Destination Producers."
argument_list|)
specifier|public
name|ObjectName
index|[]
name|getDynamicDestinationProducers
parameter_list|()
function_decl|;
annotation|@
name|MBeanInfo
argument_list|(
literal|"Adds a Connector to the broker."
argument_list|)
name|String
name|addConnector
parameter_list|(
annotation|@
name|MBeanInfo
argument_list|(
literal|"discoveryAddress"
argument_list|)
name|String
name|discoveryAddress
parameter_list|)
throws|throws
name|Exception
function_decl|;
annotation|@
name|MBeanInfo
argument_list|(
literal|"Adds a Network Connector to the broker."
argument_list|)
name|String
name|addNetworkConnector
parameter_list|(
annotation|@
name|MBeanInfo
argument_list|(
literal|"discoveryAddress"
argument_list|)
name|String
name|discoveryAddress
parameter_list|)
throws|throws
name|Exception
function_decl|;
annotation|@
name|MBeanInfo
argument_list|(
literal|"Removes a Connector from the broker."
argument_list|)
name|boolean
name|removeConnector
parameter_list|(
annotation|@
name|MBeanInfo
argument_list|(
literal|"connectorName"
argument_list|)
name|String
name|connectorName
parameter_list|)
throws|throws
name|Exception
function_decl|;
annotation|@
name|MBeanInfo
argument_list|(
literal|"Removes a Network Connector from the broker."
argument_list|)
name|boolean
name|removeNetworkConnector
parameter_list|(
annotation|@
name|MBeanInfo
argument_list|(
literal|"connectorName"
argument_list|)
name|String
name|connectorName
parameter_list|)
throws|throws
name|Exception
function_decl|;
comment|/**      * Adds a Topic destination to the broker.      *      * @param name The name of the Topic      * @throws Exception      */
annotation|@
name|MBeanInfo
argument_list|(
literal|"Adds a Topic destination to the broker."
argument_list|)
name|void
name|addTopic
parameter_list|(
annotation|@
name|MBeanInfo
argument_list|(
literal|"name"
argument_list|)
name|String
name|name
parameter_list|)
throws|throws
name|Exception
function_decl|;
comment|/**      * Adds a Queue destination to the broker.      *      * @param name The name of the Queue      * @throws Exception      */
annotation|@
name|MBeanInfo
argument_list|(
literal|"Adds a Queue destination to the broker."
argument_list|)
name|void
name|addQueue
parameter_list|(
annotation|@
name|MBeanInfo
argument_list|(
literal|"name"
argument_list|)
name|String
name|name
parameter_list|)
throws|throws
name|Exception
function_decl|;
comment|/**      * Removes a Topic destination from the broker.      *      * @param name The name of the Topic      * @throws Exception      */
annotation|@
name|MBeanInfo
argument_list|(
literal|"Removes a Topic destination from the broker."
argument_list|)
name|void
name|removeTopic
parameter_list|(
annotation|@
name|MBeanInfo
argument_list|(
literal|"name"
argument_list|)
name|String
name|name
parameter_list|)
throws|throws
name|Exception
function_decl|;
comment|/**      * Removes a Queue destination from the broker.      *      * @param name The name of the Queue      * @throws Exception      */
annotation|@
name|MBeanInfo
argument_list|(
literal|"Removes a Queue destination from the broker."
argument_list|)
name|void
name|removeQueue
parameter_list|(
annotation|@
name|MBeanInfo
argument_list|(
literal|"name"
argument_list|)
name|String
name|name
parameter_list|)
throws|throws
name|Exception
function_decl|;
comment|/**      * Creates a new durable topic subscriber      *      * @param clientId the JMS client ID      * @param subscriberName the durable subscriber name      * @param topicName the name of the topic to subscribe to      * @param selector a selector or null      * @return the object name of the MBean registered in JMX      */
annotation|@
name|MBeanInfo
argument_list|(
name|value
operator|=
literal|"Creates a new durable topic subscriber."
argument_list|)
name|ObjectName
name|createDurableSubscriber
parameter_list|(
annotation|@
name|MBeanInfo
argument_list|(
literal|"clientId"
argument_list|)
name|String
name|clientId
parameter_list|,
annotation|@
name|MBeanInfo
argument_list|(
literal|"subscriberName"
argument_list|)
name|String
name|subscriberName
parameter_list|,
annotation|@
name|MBeanInfo
argument_list|(
literal|"topicName"
argument_list|)
name|String
name|topicName
parameter_list|,
annotation|@
name|MBeanInfo
argument_list|(
literal|"selector"
argument_list|)
name|String
name|selector
parameter_list|)
throws|throws
name|Exception
function_decl|;
comment|/**      * Destroys a durable subscriber      *      * @param clientId the JMS client ID      * @param subscriberName the durable subscriber name      */
annotation|@
name|MBeanInfo
argument_list|(
name|value
operator|=
literal|"Destroys a durable subscriber."
argument_list|)
name|void
name|destroyDurableSubscriber
parameter_list|(
annotation|@
name|MBeanInfo
argument_list|(
literal|"clientId"
argument_list|)
name|String
name|clientId
parameter_list|,
annotation|@
name|MBeanInfo
argument_list|(
literal|"subscriberName"
argument_list|)
name|String
name|subscriberName
parameter_list|)
throws|throws
name|Exception
function_decl|;
comment|/**      * Reloads log4j.properties from the classpath.      * This methods calls org.apache.activemq.transport.TransportLoggerControl.reloadLog4jProperties      * @throws Throwable      */
annotation|@
name|MBeanInfo
argument_list|(
name|value
operator|=
literal|"Reloads log4j.properties from the classpath."
argument_list|)
specifier|public
name|void
name|reloadLog4jProperties
parameter_list|()
throws|throws
name|Throwable
function_decl|;
comment|/**      * @deprecated use {@link #getTransportConnectors()} or {@link #getTransportConnectorByType(String)}      */
annotation|@
name|Deprecated
annotation|@
name|MBeanInfo
argument_list|(
literal|"The url of the openwire connector - deprecated, use getTransportConnectors or getTransportConnectorByType instead"
argument_list|)
name|String
name|getOpenWireURL
parameter_list|()
function_decl|;
comment|/**      * @deprecated use {@link #getTransportConnectors()} or {@link #getTransportConnectorByType(String)}      */
annotation|@
name|Deprecated
annotation|@
name|MBeanInfo
argument_list|(
literal|"The url of the stomp connector - deprecated, use getTransportConnectors or getTransportConnectorByType instead"
argument_list|)
name|String
name|getStompURL
parameter_list|()
function_decl|;
comment|/**      * @deprecated use {@link #getTransportConnectors()} or {@link #getTransportConnectorByType(String)}      */
annotation|@
name|Deprecated
annotation|@
name|MBeanInfo
argument_list|(
literal|"The url of the SSL connector - deprecated, use getTransportConnectors or getTransportConnectorByType instead"
argument_list|)
name|String
name|getSslURL
parameter_list|()
function_decl|;
comment|/**      * @deprecated use {@link #getTransportConnectors()} or {@link #getTransportConnectorByType(String)}      */
annotation|@
name|Deprecated
annotation|@
name|MBeanInfo
argument_list|(
literal|"The url of the Stomp SSL connector - deprecated, use getTransportConnectors or getTransportConnectorByType instead"
argument_list|)
name|String
name|getStompSslURL
parameter_list|()
function_decl|;
annotation|@
name|MBeanInfo
argument_list|(
literal|"The url of the VM connector"
argument_list|)
name|String
name|getVMURL
parameter_list|()
function_decl|;
annotation|@
name|MBeanInfo
argument_list|(
literal|"The map of all defined transport connectors, with transport name as a key"
argument_list|)
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|getTransportConnectors
parameter_list|()
function_decl|;
annotation|@
name|MBeanInfo
argument_list|(
literal|"The url of transport connector by it's type; e.g. tcp, stomp, ssl, etc."
argument_list|)
name|String
name|getTransportConnectorByType
parameter_list|(
name|String
name|type
parameter_list|)
function_decl|;
annotation|@
name|MBeanInfo
argument_list|(
literal|"The location of the data directory"
argument_list|)
specifier|public
name|String
name|getDataDirectory
parameter_list|()
function_decl|;
annotation|@
name|MBeanInfo
argument_list|(
literal|"JMSJobScheduler"
argument_list|)
name|ObjectName
name|getJMSJobScheduler
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

