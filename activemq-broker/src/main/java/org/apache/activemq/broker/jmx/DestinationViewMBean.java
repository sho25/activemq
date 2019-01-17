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
name|IOException
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
name|Map
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|InvalidSelectorException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|MalformedObjectNameException
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
name|OpenDataException
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

begin_interface
specifier|public
interface|interface
name|DestinationViewMBean
block|{
comment|/**      * Returns the name of this destination      */
annotation|@
name|MBeanInfo
argument_list|(
literal|"Name of this destination."
argument_list|)
name|String
name|getName
parameter_list|()
function_decl|;
comment|/**      * Resets the management counters.      */
annotation|@
name|MBeanInfo
argument_list|(
literal|"Resets statistics."
argument_list|)
name|void
name|resetStatistics
parameter_list|()
function_decl|;
comment|/**      * Returns the number of messages that have been sent to the destination.      *      * @return The number of messages that have been sent to the destination.      */
annotation|@
name|MBeanInfo
argument_list|(
literal|"Number of messages that have been sent to the destination."
argument_list|)
name|long
name|getEnqueueCount
parameter_list|()
function_decl|;
comment|/**      * Returns the number of messages that have been delivered (potentially not      * acknowledged) to consumers.      *      * @return The number of messages that have been delivered (potentially not      *         acknowledged) to consumers.      */
annotation|@
name|MBeanInfo
argument_list|(
literal|"Number of messages that has been delivered to consumers, including those not acknowledged"
argument_list|)
name|long
name|getDispatchCount
parameter_list|()
function_decl|;
comment|/**      * Returns the number of messages that have been acknowledged from the      * destination.      *      * @return The number of messages that have been acknowledged from the      *         destination.      */
annotation|@
name|MBeanInfo
argument_list|(
literal|"Number of messages that has been acknowledged (and removed) from the destination."
argument_list|)
name|long
name|getDequeueCount
parameter_list|()
function_decl|;
comment|/**      * Returns the number of messages that have been acknowledged by network subscriptions from the      * destination.      *      * @return The number of messages that have been acknowledged by network subscriptions from the      *         destination.      */
annotation|@
name|MBeanInfo
argument_list|(
literal|"Number of messages that have been forwarded (to a networked broker) from the destination."
argument_list|)
name|long
name|getForwardCount
parameter_list|()
function_decl|;
comment|/**      * Returns the number of messages that have been dispatched but not      * acknowledged      *      * @return The number of messages that have been dispatched but not      * acknowledged      */
annotation|@
name|MBeanInfo
argument_list|(
literal|"Number of messages that have been dispatched to, but not acknowledged by, consumers."
argument_list|)
name|long
name|getInFlightCount
parameter_list|()
function_decl|;
comment|/**      * Returns the number of messages that have expired      *      * @return The number of messages that have expired      */
annotation|@
name|MBeanInfo
argument_list|(
literal|"Number of messages that have been expired."
argument_list|)
name|long
name|getExpiredCount
parameter_list|()
function_decl|;
comment|/**      * Returns the number of consumers subscribed this destination.      *      * @return The number of consumers subscribed this destination.      */
annotation|@
name|MBeanInfo
argument_list|(
literal|"Number of consumers subscribed to this destination."
argument_list|)
name|long
name|getConsumerCount
parameter_list|()
function_decl|;
comment|/**      * @return the number of producers publishing to the destination      */
annotation|@
name|MBeanInfo
argument_list|(
literal|"Number of producers attached to this destination"
argument_list|)
name|long
name|getProducerCount
parameter_list|()
function_decl|;
comment|/**      * Returns the number of messages in this destination which are yet to be      * consumed      *      * @return Returns the number of messages in this destination which are yet      *         to be consumed      */
annotation|@
name|MBeanInfo
argument_list|(
literal|"Number of messages on this destination, including any that have been dispatched but not acknowledged"
argument_list|)
name|long
name|getQueueSize
parameter_list|()
function_decl|;
comment|/**      * Returns the memory size of all messages in this destination's store      *      * @return Returns the memory size of all messages in this destination's store      */
annotation|@
name|MBeanInfo
argument_list|(
literal|"The memory size of all messages in this destination's store."
argument_list|)
name|long
name|getStoreMessageSize
parameter_list|()
function_decl|;
comment|/**      * @return An array of all the messages in the destination's queue.      */
annotation|@
name|MBeanInfo
argument_list|(
literal|"An array of all messages in the destination. Not HTML friendly."
argument_list|)
name|CompositeData
index|[]
name|browse
parameter_list|()
throws|throws
name|OpenDataException
function_decl|;
comment|/**      * @return A list of all the messages in the destination's queue.      */
annotation|@
name|MBeanInfo
argument_list|(
literal|"A list of all messages in the destination. Not HTML friendly."
argument_list|)
name|TabularData
name|browseAsTable
parameter_list|()
throws|throws
name|OpenDataException
function_decl|;
comment|/**      * @return An array of all the messages in the destination's queue.      * @throws InvalidSelectorException      */
annotation|@
name|MBeanInfo
argument_list|(
literal|"An array of all messages in the destination based on an SQL-92 selection on the message headers or XPATH on the body. Not HTML friendly."
argument_list|)
name|CompositeData
index|[]
name|browse
parameter_list|(
annotation|@
name|MBeanInfo
argument_list|(
literal|"selector"
argument_list|)
name|String
name|selector
parameter_list|)
throws|throws
name|OpenDataException
throws|,
name|InvalidSelectorException
function_decl|;
comment|/**      * @return A list of all the messages in the destination's queue.      * @throws InvalidSelectorException      */
annotation|@
name|MBeanInfo
argument_list|(
literal|"A list of all messages in the destination based on an SQL-92 selection on the message headers or XPATH on the body. Not HTML friendly."
argument_list|)
name|TabularData
name|browseAsTable
parameter_list|(
annotation|@
name|MBeanInfo
argument_list|(
literal|"selector"
argument_list|)
name|String
name|selector
parameter_list|)
throws|throws
name|OpenDataException
throws|,
name|InvalidSelectorException
function_decl|;
comment|/**      * Sends a TextMesage to the destination.      *      * @param body the text to send      * @return the message id of the message sent.      * @throws Exception      */
annotation|@
name|MBeanInfo
argument_list|(
literal|"Sends a TextMessage to the destination."
argument_list|)
name|String
name|sendTextMessage
parameter_list|(
annotation|@
name|MBeanInfo
argument_list|(
literal|"body"
argument_list|)
name|String
name|body
parameter_list|)
throws|throws
name|Exception
function_decl|;
comment|/**      * Sends a TextMessage to the destination.      *      * @param properties the message properties to set as a comma sep name=value list. Can only      *                contain Strings maped to primitive types or JMS properties. eg: body=hi,JMSReplyTo=Queue2      * @return the message id of the message sent.      * @throws Exception      */
annotation|@
name|MBeanInfo
argument_list|(
literal|"Sends a TextMessage to the destination."
argument_list|)
specifier|public
name|String
name|sendTextMessageWithProperties
parameter_list|(
name|String
name|properties
parameter_list|)
throws|throws
name|Exception
function_decl|;
comment|/**      * Sends a TextMesage to the destination.      *      * @param headers the message headers and properties to set. Can only      *                container Strings maped to primitive types.      * @param body the text to send      * @return the message id of the message sent.      * @throws Exception      */
annotation|@
name|MBeanInfo
argument_list|(
literal|"Sends a TextMessage to the destination."
argument_list|)
name|String
name|sendTextMessage
parameter_list|(
annotation|@
name|MBeanInfo
argument_list|(
literal|"headers"
argument_list|)
name|Map
argument_list|<
name|?
argument_list|,
name|?
argument_list|>
name|headers
parameter_list|,
annotation|@
name|MBeanInfo
argument_list|(
literal|"body"
argument_list|)
name|String
name|body
parameter_list|)
throws|throws
name|Exception
function_decl|;
comment|/**      * Sends a TextMesage to the destination.      * @param body the text to send      * @param user      * @param password      * @return a string value      * @throws Exception      */
annotation|@
name|MBeanInfo
argument_list|(
literal|"Sends a TextMessage to a password-protected destination."
argument_list|)
name|String
name|sendTextMessage
parameter_list|(
annotation|@
name|MBeanInfo
argument_list|(
literal|"body"
argument_list|)
name|String
name|body
parameter_list|,
annotation|@
name|MBeanInfo
argument_list|(
literal|"user"
argument_list|)
name|String
name|user
parameter_list|,
annotation|@
name|MBeanInfo
argument_list|(
literal|"password"
argument_list|)
name|String
name|password
parameter_list|)
throws|throws
name|Exception
function_decl|;
comment|/**      *      * @param headers the message headers and properties to set. Can only      *                container Strings maped to primitive types.      * @param body the text to send      * @param user      * @param password      *      * @return a string value      *      * @throws Exception      */
annotation|@
name|MBeanInfo
argument_list|(
literal|"Sends a TextMessage to a password-protected destination."
argument_list|)
name|String
name|sendTextMessage
parameter_list|(
annotation|@
name|MBeanInfo
argument_list|(
literal|"headers"
argument_list|)
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|headers
parameter_list|,
annotation|@
name|MBeanInfo
argument_list|(
literal|"body"
argument_list|)
name|String
name|body
parameter_list|,
annotation|@
name|MBeanInfo
argument_list|(
literal|"user"
argument_list|)
name|String
name|user
parameter_list|,
annotation|@
name|MBeanInfo
argument_list|(
literal|"password"
argument_list|)
name|String
name|password
parameter_list|)
throws|throws
name|Exception
function_decl|;
comment|/**      * @return the percentage of amount of memory used      */
annotation|@
name|MBeanInfo
argument_list|(
literal|"The percentage of the memory limit used"
argument_list|)
name|int
name|getMemoryPercentUsage
parameter_list|()
function_decl|;
comment|/**      * @return the amount of memory currently used by this destination      */
annotation|@
name|MBeanInfo
argument_list|(
literal|"Memory used by undelivered messages in bytes"
argument_list|)
name|long
name|getMemoryUsageByteCount
parameter_list|()
function_decl|;
comment|/**      * @return the amount of memory allocated to this destination      */
annotation|@
name|MBeanInfo
argument_list|(
literal|"Memory limit, in bytes, used by undelivered messages before paging to temporary storage."
argument_list|)
name|long
name|getMemoryLimit
parameter_list|()
function_decl|;
comment|/**      * set the amount of memory allocated to this destination      * @param limit      */
name|void
name|setMemoryLimit
parameter_list|(
name|long
name|limit
parameter_list|)
function_decl|;
comment|/**      * @return the percentage of amount of temp usage used      */
annotation|@
name|MBeanInfo
argument_list|(
literal|"The percentage of the temp usage limit used"
argument_list|)
name|int
name|getTempUsagePercentUsage
parameter_list|()
function_decl|;
comment|/**      * @return the amount of temp usage allocated to this destination      */
annotation|@
name|MBeanInfo
argument_list|(
literal|"Temp usage limit, in bytes, assigned to this destination."
argument_list|)
name|long
name|getTempUsageLimit
parameter_list|()
function_decl|;
comment|/**      * set the amount of temp usage allocated to this destination      * @param limit the amount of temp usage allocated to this destination      */
name|void
name|setTempUsageLimit
parameter_list|(
name|long
name|limit
parameter_list|)
function_decl|;
comment|/**      * @return the portion of memory from the broker memory limit for this destination      */
annotation|@
name|MBeanInfo
argument_list|(
literal|"Portion of memory from the broker memory limit for this destination"
argument_list|)
name|float
name|getMemoryUsagePortion
parameter_list|()
function_decl|;
comment|/**      * set the portion of memory from the broker memory limit for this destination      * @param value      */
name|void
name|setMemoryUsagePortion
parameter_list|(
annotation|@
name|MBeanInfo
argument_list|(
literal|"bytes"
argument_list|)
name|float
name|value
parameter_list|)
function_decl|;
comment|/**      * Browses the current destination returning a list of messages      */
annotation|@
name|MBeanInfo
argument_list|(
literal|"A list of all messages in the destination. Not HTML friendly."
argument_list|)
name|List
argument_list|<
name|?
argument_list|>
name|browseMessages
parameter_list|()
throws|throws
name|InvalidSelectorException
function_decl|;
comment|/**      * Browses the current destination with the given selector returning a list      * of messages      */
annotation|@
name|MBeanInfo
argument_list|(
literal|"A list of all messages in the destination based on an SQL-92 selection on the message headers or XPATH on the body. Not HTML friendly."
argument_list|)
name|List
argument_list|<
name|?
argument_list|>
name|browseMessages
parameter_list|(
name|String
name|selector
parameter_list|)
throws|throws
name|InvalidSelectorException
function_decl|;
comment|/**      * @return longest time a message is held by a destination      */
annotation|@
name|MBeanInfo
argument_list|(
literal|"The longest time a message was held on this destination"
argument_list|)
name|long
name|getMaxEnqueueTime
parameter_list|()
function_decl|;
comment|/**      * @return shortest time a message is held by a destination      */
annotation|@
name|MBeanInfo
argument_list|(
literal|"The shortest time a message was held on this destination"
argument_list|)
name|long
name|getMinEnqueueTime
parameter_list|()
function_decl|;
annotation|@
name|MBeanInfo
argument_list|(
literal|"Average time a message was held on this destination."
argument_list|)
name|double
name|getAverageEnqueueTime
parameter_list|()
function_decl|;
annotation|@
name|MBeanInfo
argument_list|(
literal|"Average message size on this destination"
argument_list|)
name|long
name|getAverageMessageSize
parameter_list|()
function_decl|;
annotation|@
name|MBeanInfo
argument_list|(
literal|"Max message size on this destination"
argument_list|)
specifier|public
name|long
name|getMaxMessageSize
parameter_list|()
function_decl|;
annotation|@
name|MBeanInfo
argument_list|(
literal|"Min message size on this destination"
argument_list|)
specifier|public
name|long
name|getMinMessageSize
parameter_list|()
function_decl|;
comment|/**      * @return the producerFlowControl      */
annotation|@
name|MBeanInfo
argument_list|(
literal|"Flow control is enabled for producers"
argument_list|)
name|boolean
name|isProducerFlowControl
parameter_list|()
function_decl|;
comment|/**      * @param producerFlowControl the producerFlowControl to set      */
specifier|public
name|void
name|setProducerFlowControl
parameter_list|(
annotation|@
name|MBeanInfo
argument_list|(
literal|"producerFlowControl"
argument_list|)
name|boolean
name|producerFlowControl
parameter_list|)
function_decl|;
comment|/**      * @return if we treat consumers as alwaysRetroactive      */
annotation|@
name|MBeanInfo
argument_list|(
literal|"Always treat consumers as retroactive"
argument_list|)
name|boolean
name|isAlwaysRetroactive
parameter_list|()
function_decl|;
comment|/**      * @param alwaysRetroactive set as always retroActive      */
specifier|public
name|void
name|setAlwaysRetroactive
parameter_list|(
annotation|@
name|MBeanInfo
argument_list|(
literal|"alwaysRetroactive"
argument_list|)
name|boolean
name|alwaysRetroactive
parameter_list|)
function_decl|;
comment|/**      * Set's the interval at which warnings about producers being blocked by      * resource usage will be triggered. Values of 0 or less will disable      * warnings      *      * @param blockedProducerWarningInterval the interval at which warning about      *            blocked producers will be triggered.      */
specifier|public
name|void
name|setBlockedProducerWarningInterval
parameter_list|(
annotation|@
name|MBeanInfo
argument_list|(
literal|"blockedProducerWarningInterval"
argument_list|)
name|long
name|blockedProducerWarningInterval
parameter_list|)
function_decl|;
comment|/**      *      * @return the interval at which warning about blocked producers will be      *         triggered.      */
annotation|@
name|MBeanInfo
argument_list|(
literal|"Blocked Producer Warning Interval"
argument_list|)
specifier|public
name|long
name|getBlockedProducerWarningInterval
parameter_list|()
function_decl|;
comment|/**      * @return the maxProducersToAudit      */
annotation|@
name|MBeanInfo
argument_list|(
literal|"Maximum number of producers to audit"
argument_list|)
specifier|public
name|int
name|getMaxProducersToAudit
parameter_list|()
function_decl|;
comment|/**      * @param maxProducersToAudit the maxProducersToAudit to set      */
specifier|public
name|void
name|setMaxProducersToAudit
parameter_list|(
annotation|@
name|MBeanInfo
argument_list|(
literal|"maxProducersToAudit"
argument_list|)
name|int
name|maxProducersToAudit
parameter_list|)
function_decl|;
comment|/**      * @return the maxAuditDepth      */
annotation|@
name|MBeanInfo
argument_list|(
literal|"Max audit depth"
argument_list|)
specifier|public
name|int
name|getMaxAuditDepth
parameter_list|()
function_decl|;
comment|/**      * @param maxAuditDepth the maxAuditDepth to set      */
specifier|public
name|void
name|setMaxAuditDepth
parameter_list|(
annotation|@
name|MBeanInfo
argument_list|(
literal|"maxAuditDepth"
argument_list|)
name|int
name|maxAuditDepth
parameter_list|)
function_decl|;
comment|/**      * @return the maximum number of message to be paged into the      * destination      */
annotation|@
name|MBeanInfo
argument_list|(
literal|"Maximum number of messages to be paged in"
argument_list|)
specifier|public
name|int
name|getMaxPageSize
parameter_list|()
function_decl|;
comment|/**      * @param pageSize      * Set the maximum number of messages to page into the destination      */
specifier|public
name|void
name|setMaxPageSize
parameter_list|(
annotation|@
name|MBeanInfo
argument_list|(
literal|"pageSize"
argument_list|)
name|int
name|pageSize
parameter_list|)
function_decl|;
comment|/**      * @return true if caching is allowed of for the destination      */
annotation|@
name|MBeanInfo
argument_list|(
literal|"Caching is allowed"
argument_list|)
specifier|public
name|boolean
name|isUseCache
parameter_list|()
function_decl|;
comment|/**      * @return true if prioritized messages are enabled for the destination      */
annotation|@
name|MBeanInfo
argument_list|(
literal|"Prioritized messages is enabled"
argument_list|)
specifier|public
name|boolean
name|isPrioritizedMessages
parameter_list|()
function_decl|;
comment|/**      * @param value      * enable/disable caching on the destination      */
specifier|public
name|void
name|setUseCache
parameter_list|(
annotation|@
name|MBeanInfo
argument_list|(
literal|"cache"
argument_list|)
name|boolean
name|value
parameter_list|)
function_decl|;
comment|/**      * Returns all the current subscription MBeans matching this destination      *      * @return the names of the subscriptions for this destination      */
annotation|@
name|MBeanInfo
argument_list|(
literal|"Subscription MBeans matching this destination"
argument_list|)
name|ObjectName
index|[]
name|getSubscriptions
parameter_list|()
throws|throws
name|IOException
throws|,
name|MalformedObjectNameException
function_decl|;
comment|/**      * Returns the slow consumer strategy MBean for this destination      *      * @return the name of the slow consumer handler MBean for this destination      */
annotation|@
name|MBeanInfo
argument_list|(
literal|"Optional slowConsumer handler MBean for this destination"
argument_list|)
name|ObjectName
name|getSlowConsumerStrategy
parameter_list|()
throws|throws
name|IOException
throws|,
name|MalformedObjectNameException
function_decl|;
comment|/**      * @return A string of destination options, name value pairs as URL queryString.      */
annotation|@
name|MBeanInfo
argument_list|(
literal|"Destination options as name value pairs in a URL queryString"
argument_list|)
name|String
name|getOptions
parameter_list|()
function_decl|;
comment|/**      * @return true if this is dead letter queue      */
annotation|@
name|MBeanInfo
argument_list|(
literal|"Dead Letter Queue"
argument_list|)
name|boolean
name|isDLQ
parameter_list|()
function_decl|;
comment|/**      * @param value      * enable/disable the DLQ flag      */
name|void
name|setDLQ
parameter_list|(
name|boolean
name|value
parameter_list|)
function_decl|;
annotation|@
name|MBeanInfo
argument_list|(
literal|"Number of messages blocked for flow control"
argument_list|)
name|long
name|getBlockedSends
parameter_list|()
function_decl|;
annotation|@
name|MBeanInfo
argument_list|(
literal|"Average time (ms) messages have been blocked by flow control"
argument_list|)
name|double
name|getAverageBlockedTime
parameter_list|()
function_decl|;
annotation|@
name|MBeanInfo
argument_list|(
literal|"Total time (ms) messages have been blocked by flow control"
argument_list|)
name|long
name|getTotalBlockedTime
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

