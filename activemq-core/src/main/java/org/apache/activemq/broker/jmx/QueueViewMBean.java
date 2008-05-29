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

begin_interface
specifier|public
interface|interface
name|QueueViewMBean
extends|extends
name|DestinationViewMBean
block|{
comment|/**      * Retrieve a message from the destination's queue.      *       * @param messageId the message id of the message to retrieve      * @return A CompositeData object which is a JMX version of the messages      * @throws OpenDataException      */
name|CompositeData
name|getMessage
parameter_list|(
name|String
name|messageId
parameter_list|)
throws|throws
name|OpenDataException
function_decl|;
comment|/**      * Removes a message from the queue. If the message has already been      * dispatched to another consumer, the message cannot be deleted and this      * method will return false.      *       * @param messageId      * @return true if the message was found and could be successfully deleted.      * @throws Exception      */
name|boolean
name|removeMessage
parameter_list|(
name|String
name|messageId
parameter_list|)
throws|throws
name|Exception
function_decl|;
comment|/**      * Removes the messages matching the given selector      *       * @return the number of messages removed      */
name|int
name|removeMatchingMessages
parameter_list|(
name|String
name|selector
parameter_list|)
throws|throws
name|Exception
function_decl|;
comment|/**      * Removes the messages matching the given selector up to the maximum number      * of matched messages      *       * @return the number of messages removed      */
name|int
name|removeMatchingMessages
parameter_list|(
name|String
name|selector
parameter_list|,
name|int
name|maximumMessages
parameter_list|)
throws|throws
name|Exception
function_decl|;
comment|/**      * Removes all of the messages in the queue.      *       * @throws Exception      */
name|void
name|purge
parameter_list|()
throws|throws
name|Exception
function_decl|;
comment|/**      * Copies a given message to another destination.      *       * @param messageId      * @param destinationName      * @return true if the message was found and was successfully copied to the      *         other destination.      * @throws Exception      */
name|boolean
name|copyMessageTo
parameter_list|(
name|String
name|messageId
parameter_list|,
name|String
name|destinationName
parameter_list|)
throws|throws
name|Exception
function_decl|;
comment|/**      * Copies the messages matching the given selector      *       * @return the number of messages copied      */
name|int
name|copyMatchingMessagesTo
parameter_list|(
name|String
name|selector
parameter_list|,
name|String
name|destinationName
parameter_list|)
throws|throws
name|Exception
function_decl|;
comment|/**      * Copies the messages matching the given selector up to the maximum number      * of matched messages      *       * @return the number of messages copied      */
name|int
name|copyMatchingMessagesTo
parameter_list|(
name|String
name|selector
parameter_list|,
name|String
name|destinationName
parameter_list|,
name|int
name|maximumMessages
parameter_list|)
throws|throws
name|Exception
function_decl|;
comment|/**      * Moves the message to another destination.      *       * @param messageId      * @param destinationName      * @return true if the message was found and was successfully copied to the      *         other destination.      * @throws Exception      */
name|boolean
name|moveMessageTo
parameter_list|(
name|String
name|messageId
parameter_list|,
name|String
name|destinationName
parameter_list|)
throws|throws
name|Exception
function_decl|;
comment|/**      * Moves the messages matching the given selector      *       * @return the number of messages removed      */
name|int
name|moveMatchingMessagesTo
parameter_list|(
name|String
name|selector
parameter_list|,
name|String
name|destinationName
parameter_list|)
throws|throws
name|Exception
function_decl|;
comment|/**      * Moves the messages matching the given selector up to the maximum number      * of matched messages      */
name|int
name|moveMatchingMessagesTo
parameter_list|(
name|String
name|selector
parameter_list|,
name|String
name|destinationName
parameter_list|,
name|int
name|maximumMessages
parameter_list|)
throws|throws
name|Exception
function_decl|;
comment|/**      * @return true if the message cursor has memory space available      * to page in more messages      */
specifier|public
name|boolean
name|doesCursorHaveSpace
parameter_list|()
function_decl|;
comment|/**      * @return true if the cursor has reached its memory limit for      * paged in messages      */
specifier|public
name|boolean
name|isCursorFull
parameter_list|()
function_decl|;
comment|/**      * @return true if the cursor has messages buffered to deliver      */
specifier|public
name|boolean
name|doesCursorHaveMessagesBuffered
parameter_list|()
function_decl|;
comment|/**      * @return the cursor memory usage in bytes      */
specifier|public
name|long
name|getCursorMemoryUsage
parameter_list|()
function_decl|;
comment|/**      * @return the cursor memory usage as a percentage      */
specifier|public
name|int
name|getCursorPercentUsage
parameter_list|()
function_decl|;
comment|/**      * @return the number of messages available to be paged in       * by the cursor      */
specifier|public
name|int
name|cursorSize
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

