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
name|java
operator|.
name|io
operator|.
name|IOException
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
name|MalformedObjectNameException
import|;
end_import

begin_interface
specifier|public
interface|interface
name|DestinationViewMBean
block|{
comment|/**      * Returns the name of this destination      */
name|String
name|getName
parameter_list|()
function_decl|;
comment|/**      * Resets the managment counters.      */
name|void
name|resetStatistics
parameter_list|()
function_decl|;
comment|/**      * Returns the number of messages that have been sent to the destination.      *       * @return The number of messages that have been sent to the destination.      */
name|long
name|getEnqueueCount
parameter_list|()
function_decl|;
comment|/**      * Returns the number of messages that have been delivered (potentially not      * acknowledged) to consumers.      *       * @return The number of messages that have been delivered (potentially not      *         acknowledged) to consumers.      */
name|long
name|getDispatchCount
parameter_list|()
function_decl|;
comment|/**      * Returns the number of messages that have been acknowledged from the      * destination.      *       * @return The number of messages that have been acknowledged from the      *         destination.      */
name|long
name|getDequeueCount
parameter_list|()
function_decl|;
comment|/**      * Returns the number of messages that have been dispatched but not      * acknowledged      *       * @return The number of messages that have been dispatched but not      * acknowledged      */
name|long
name|getInFlightCount
parameter_list|()
function_decl|;
comment|/**      * Returns the number of consumers subscribed this destination.      *       * @return The number of consumers subscribed this destination.      */
name|long
name|getConsumerCount
parameter_list|()
function_decl|;
comment|/**      * @return the number of producers publishing to the destination      */
name|long
name|getProducerCount
parameter_list|()
function_decl|;
comment|/**      * Returns the number of messages in this destination which are yet to be      * consumed      *       * @return Returns the number of messages in this destination which are yet      *         to be consumed      */
name|long
name|getQueueSize
parameter_list|()
function_decl|;
comment|/**      * @return An array of all the messages in the destination's queue.      */
name|CompositeData
index|[]
name|browse
parameter_list|()
throws|throws
name|OpenDataException
function_decl|;
comment|/**      * @return A list of all the messages in the destination's queue.      */
name|TabularData
name|browseAsTable
parameter_list|()
throws|throws
name|OpenDataException
function_decl|;
comment|/**      * @return An array of all the messages in the destination's queue.      * @throws InvalidSelectorException      */
name|CompositeData
index|[]
name|browse
parameter_list|(
name|String
name|selector
parameter_list|)
throws|throws
name|OpenDataException
throws|,
name|InvalidSelectorException
function_decl|;
comment|/**      * @return A list of all the messages in the destination's queue.      * @throws InvalidSelectorException      */
name|TabularData
name|browseAsTable
parameter_list|(
name|String
name|selector
parameter_list|)
throws|throws
name|OpenDataException
throws|,
name|InvalidSelectorException
function_decl|;
comment|/**      * Sends a TextMesage to the destination.      *       * @param body the text to send      * @return the message id of the message sent.      * @throws Exception      */
name|String
name|sendTextMessage
parameter_list|(
name|String
name|body
parameter_list|)
throws|throws
name|Exception
function_decl|;
comment|/**      * Sends a TextMesage to the destination.      *       * @param headers the message headers and properties to set. Can only      *                container Strings maped to primitive types.      * @param body the text to send      * @return the message id of the message sent.      * @throws Exception      */
name|String
name|sendTextMessage
parameter_list|(
name|Map
name|headers
parameter_list|,
name|String
name|body
parameter_list|)
throws|throws
name|Exception
function_decl|;
comment|/**      * Sends a TextMesage to the destination.      * @param body the text to send      * @param user      * @param password      * @return      * @throws Exception      */
name|String
name|sendTextMessage
parameter_list|(
name|String
name|body
parameter_list|,
name|String
name|user
parameter_list|,
name|String
name|password
parameter_list|)
throws|throws
name|Exception
function_decl|;
comment|/**      *       * @param headers the message headers and properties to set. Can only      *                container Strings maped to primitive types.      * @param body the text to send      * @param user      * @param password      * @return      * @throws Exception      */
name|String
name|sendTextMessage
parameter_list|(
name|Map
name|headers
parameter_list|,
name|String
name|body
parameter_list|,
name|String
name|user
parameter_list|,
name|String
name|password
parameter_list|)
throws|throws
name|Exception
function_decl|;
comment|/**      * @return the percentage of amount of memory used      */
name|int
name|getMemoryPercentUsage
parameter_list|()
function_decl|;
comment|/**      * @return the amount of memory allocated to this destination      */
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
comment|/**      * @return the portion of memory from the broker memory limit for this destination      */
name|float
name|getMemoryUsagePortion
parameter_list|()
function_decl|;
comment|/**      * set the portion of memory from the broker memory limit for this destination      * @param value      */
name|void
name|setMemoryUsagePortion
parameter_list|(
name|float
name|value
parameter_list|)
function_decl|;
comment|/**      * Browses the current destination returning a list of messages      */
name|List
name|browseMessages
parameter_list|()
throws|throws
name|InvalidSelectorException
function_decl|;
comment|/**      * Browses the current destination with the given selector returning a list      * of messages      */
name|List
name|browseMessages
parameter_list|(
name|String
name|selector
parameter_list|)
throws|throws
name|InvalidSelectorException
function_decl|;
comment|/**      * @return longest time a message is held by a destination      */
name|long
name|getMaxEnqueueTime
parameter_list|()
function_decl|;
comment|/**      * @return shortest time a message is held by a destination      */
name|long
name|getMinEnqueueTime
parameter_list|()
function_decl|;
comment|/**      * @return average time a message is held by a destination      */
name|double
name|getAverageEnqueueTime
parameter_list|()
function_decl|;
comment|/**      * @return the producerFlowControl      */
name|boolean
name|isProducerFlowControl
parameter_list|()
function_decl|;
comment|/**      * @param producerFlowControl the producerFlowControl to set      */
specifier|public
name|void
name|setProducerFlowControl
parameter_list|(
name|boolean
name|producerFlowControl
parameter_list|)
function_decl|;
comment|/**      * @return the maxProducersToAudit      */
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
name|int
name|maxProducersToAudit
parameter_list|)
function_decl|;
comment|/**      * @return the maxAuditDepth      */
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
name|int
name|maxAuditDepth
parameter_list|)
function_decl|;
comment|/**      * @return the maximum number of message to be paged into the       * destination      */
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
name|int
name|pageSize
parameter_list|)
function_decl|;
comment|/**      * @return true if caching is enabled of for the destination      */
specifier|public
name|boolean
name|isUseCache
parameter_list|()
function_decl|;
comment|/**      * @param value      * enable/disable caching on the destination      */
specifier|public
name|void
name|setUseCache
parameter_list|(
name|boolean
name|value
parameter_list|)
function_decl|;
comment|/**      * Returns all the current subscription MBeans matching this destination      *       * @return the names of the subscriptions for this destination      */
name|ObjectName
index|[]
name|getSubscriptions
parameter_list|()
throws|throws
name|IOException
throws|,
name|MalformedObjectNameException
function_decl|;
block|}
end_interface

end_unit

