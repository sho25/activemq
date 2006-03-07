begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Copyright 2005-2006 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
comment|/** 	 * Resets the managment counters. 	 */
specifier|public
name|void
name|resetStatistics
parameter_list|()
function_decl|;
comment|/**      * @return The number of messages that have been sent to the destination.      */
specifier|public
name|long
name|getEnqueueCount
parameter_list|()
function_decl|;
comment|/**      * @return The number of messages that have been received from the destination.      */
specifier|public
name|long
name|getDequeueCount
parameter_list|()
function_decl|;
comment|/**      * @return The number of consmers subscribed to messages from this destination.      */
specifier|public
name|long
name|getConsumerCount
parameter_list|()
function_decl|;
comment|/**      * @return The number of messages being buffered by this destination      */
specifier|public
name|long
name|getQueueSize
parameter_list|()
function_decl|;
comment|/**      * @return An array of all the messages in the destination's queue.      */
specifier|public
name|CompositeData
index|[]
name|browse
parameter_list|()
throws|throws
name|OpenDataException
function_decl|;
comment|/**      * @return A list of all the messages in the destination's queue.      */
specifier|public
name|TabularData
name|browseAsTable
parameter_list|()
throws|throws
name|OpenDataException
function_decl|;
comment|/**      * Sends a TextMesage to the destination.      * @param body the text to send      * @return the message id of the message sent.      * @throws Exception      */
specifier|public
name|String
name|sendTextMessage
parameter_list|(
name|String
name|body
parameter_list|)
throws|throws
name|Exception
function_decl|;
comment|/**      * Sends a TextMesage to the destination.      * @param headers the message headers and properties to set.  Can only container Strings maped to primitive types.      * @param body the text to send      * @return the message id of the message sent.      * @throws Exception      */
specifier|public
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
block|}
end_interface

end_unit

