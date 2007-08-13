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

begin_interface
specifier|public
interface|interface
name|BrokerViewMBean
extends|extends
name|Service
block|{
comment|/**      * @return The unique id of the broker.      */
name|String
name|getBrokerId
parameter_list|()
function_decl|;
comment|/**      * The Broker will fush it's caches so that the garbage collector can      * recalaim more memory.      *       * @throws Exception      */
name|void
name|gc
parameter_list|()
throws|throws
name|Exception
function_decl|;
name|void
name|resetStatistics
parameter_list|()
function_decl|;
name|void
name|enableStatistics
parameter_list|()
function_decl|;
name|void
name|disableStatistics
parameter_list|()
function_decl|;
name|boolean
name|isStatisticsEnabled
parameter_list|()
function_decl|;
name|long
name|getTotalEnqueueCount
parameter_list|()
function_decl|;
name|long
name|getTotalDequeueCount
parameter_list|()
function_decl|;
name|long
name|getTotalConsumerCount
parameter_list|()
function_decl|;
name|long
name|getTotalMessageCount
parameter_list|()
function_decl|;
name|int
name|getMemoryPercentageUsed
parameter_list|()
function_decl|;
name|long
name|getMemoryLimit
parameter_list|()
function_decl|;
name|void
name|setMemoryLimit
parameter_list|(
name|long
name|limit
parameter_list|)
function_decl|;
comment|/**      * Shuts down the JVM.      *       * @param exitCode the exit code that will be reported by the JVM process      *                when it exits.      */
name|void
name|terminateJVM
parameter_list|(
name|int
name|exitCode
parameter_list|)
function_decl|;
comment|/**      * Stop the broker and all it's components.      */
name|void
name|stop
parameter_list|()
throws|throws
name|Exception
function_decl|;
name|ObjectName
index|[]
name|getTopics
parameter_list|()
function_decl|;
name|ObjectName
index|[]
name|getQueues
parameter_list|()
function_decl|;
name|ObjectName
index|[]
name|getTemporaryTopics
parameter_list|()
function_decl|;
name|ObjectName
index|[]
name|getTemporaryQueues
parameter_list|()
function_decl|;
name|ObjectName
index|[]
name|getTopicSubscribers
parameter_list|()
function_decl|;
name|ObjectName
index|[]
name|getDurableTopicSubscribers
parameter_list|()
function_decl|;
name|ObjectName
index|[]
name|getInactiveDurableTopicSubscribers
parameter_list|()
function_decl|;
name|ObjectName
index|[]
name|getQueueSubscribers
parameter_list|()
function_decl|;
name|ObjectName
index|[]
name|getTemporaryTopicSubscribers
parameter_list|()
function_decl|;
name|ObjectName
index|[]
name|getTemporaryQueueSubscribers
parameter_list|()
function_decl|;
comment|/**      * Adds a Topic destination to the broker.      *       * @param name The name of the Topic      * @throws Exception      */
name|void
name|addTopic
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|Exception
function_decl|;
comment|/**      * Adds a Queue destination to the broker.      *       * @param name The name of the Queue      * @throws Exception      */
name|void
name|addQueue
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|Exception
function_decl|;
comment|/**      * Removes a Topic destination from the broker.      *       * @param name The name of the Topic      * @throws Exception      */
name|void
name|removeTopic
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|Exception
function_decl|;
comment|/**      * Removes a Queue destination from the broker.      *       * @param name The name of the Queue      * @throws Exception      */
name|void
name|removeQueue
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|Exception
function_decl|;
comment|/**      * Creates a new durable topic subscriber      *       * @param clientId the JMS client ID      * @param subscriberName the durable subscriber name      * @param topicName the name of the topic to subscribe to      * @param selector a selector or null      * @return the object name of the MBean registered in JMX      */
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
function_decl|;
comment|/**      * Destroys a durable subscriber      *       * @param clientId the JMS client ID      * @param subscriberName the durable subscriber name      */
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
function_decl|;
comment|/**      * Reloads log4j.properties from the classpath.      * This methods calls org.apache.activemq.transport.TransportLoggerControl.reloadLog4jProperties      * @throws Exception      */
specifier|public
name|void
name|reloadLog4jProperties
parameter_list|()
throws|throws
name|Exception
function_decl|;
block|}
end_interface

end_unit

