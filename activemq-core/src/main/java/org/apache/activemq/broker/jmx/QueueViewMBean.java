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
annotation|@
name|MBeanInfo
argument_list|(
literal|"View a message from the destination by JMS message ID."
argument_list|)
name|CompositeData
name|getMessage
parameter_list|(
annotation|@
name|MBeanInfo
argument_list|(
literal|"messageId"
argument_list|)
name|String
name|messageId
parameter_list|)
throws|throws
name|OpenDataException
function_decl|;
comment|/**      * Removes a message from the queue. If the message has already been      * dispatched to another consumer, the message cannot be deleted and this      * method will return false.      *       * @param messageId      * @return true if the message was found and could be successfully deleted.      * @throws Exception      */
annotation|@
name|MBeanInfo
argument_list|(
literal|"Remove a message from the destination by JMS message ID.  If the message has been dispatched, it cannot be deleted and false is returned."
argument_list|)
name|boolean
name|removeMessage
parameter_list|(
annotation|@
name|MBeanInfo
argument_list|(
literal|"messageId"
argument_list|)
name|String
name|messageId
parameter_list|)
throws|throws
name|Exception
function_decl|;
comment|/**      * Removes the messages matching the given selector      *       * @return the number of messages removed      */
annotation|@
name|MBeanInfo
argument_list|(
literal|"Removes messages from the destination based on an SQL-92 selection on the message headers or XPATH on the body."
argument_list|)
name|int
name|removeMatchingMessages
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
name|Exception
function_decl|;
comment|/**      * Removes the messages matching the given selector up to the maximum number      * of matched messages      *       * @return the number of messages removed      */
annotation|@
name|MBeanInfo
argument_list|(
literal|"Removes up to a specified number of messages from the destination based on an SQL-92 selection on the message headers or XPATH on the body."
argument_list|)
name|int
name|removeMatchingMessages
parameter_list|(
annotation|@
name|MBeanInfo
argument_list|(
literal|"selector"
argument_list|)
name|String
name|selector
parameter_list|,
annotation|@
name|MBeanInfo
argument_list|(
literal|"maximumMessages"
argument_list|)
name|int
name|maximumMessages
parameter_list|)
throws|throws
name|Exception
function_decl|;
comment|/**      * Removes all of the messages in the queue.      *       * @throws Exception      */
annotation|@
name|MBeanInfo
argument_list|(
literal|"Removes all of the messages in the queue."
argument_list|)
name|void
name|purge
parameter_list|()
throws|throws
name|Exception
function_decl|;
comment|/**      * Copies a given message to another destination.      *       * @param messageId      * @param destinationName      * @return true if the message was found and was successfully copied to the      *         other destination.      * @throws Exception      */
annotation|@
name|MBeanInfo
argument_list|(
literal|"Copies a message with the given JMS message ID into the specified destination."
argument_list|)
name|boolean
name|copyMessageTo
parameter_list|(
annotation|@
name|MBeanInfo
argument_list|(
literal|"messageId"
argument_list|)
name|String
name|messageId
parameter_list|,
annotation|@
name|MBeanInfo
argument_list|(
literal|"destinationName"
argument_list|)
name|String
name|destinationName
parameter_list|)
throws|throws
name|Exception
function_decl|;
comment|/**      * Copies the messages matching the given selector      *       * @return the number of messages copied      */
annotation|@
name|MBeanInfo
argument_list|(
literal|"Copies messages based on an SQL-92 selecton on the message headers or XPATH on the body into the specified destination."
argument_list|)
name|int
name|copyMatchingMessagesTo
parameter_list|(
annotation|@
name|MBeanInfo
argument_list|(
literal|"selector"
argument_list|)
name|String
name|selector
parameter_list|,
annotation|@
name|MBeanInfo
argument_list|(
literal|"destinationName"
argument_list|)
name|String
name|destinationName
parameter_list|)
throws|throws
name|Exception
function_decl|;
comment|/**      * Copies the messages matching the given selector up to the maximum number      * of matched messages      *       * @return the number of messages copied      */
annotation|@
name|MBeanInfo
argument_list|(
literal|"Copies up to a specified number of messages based on an SQL-92 selecton on the message headers or XPATH on the body into the specified destination."
argument_list|)
name|int
name|copyMatchingMessagesTo
parameter_list|(
annotation|@
name|MBeanInfo
argument_list|(
literal|"selector"
argument_list|)
name|String
name|selector
parameter_list|,
annotation|@
name|MBeanInfo
argument_list|(
literal|"destinationName"
argument_list|)
name|String
name|destinationName
parameter_list|,
annotation|@
name|MBeanInfo
argument_list|(
literal|"maximumMessages"
argument_list|)
name|int
name|maximumMessages
parameter_list|)
throws|throws
name|Exception
function_decl|;
comment|/**      * Moves the message to another destination.      *       * @param messageId      * @param destinationName      * @return true if the message was found and was successfully copied to the      *         other destination.      * @throws Exception      */
annotation|@
name|MBeanInfo
argument_list|(
literal|"Moves a message with the given JMS message ID into the specified destination."
argument_list|)
name|boolean
name|moveMessageTo
parameter_list|(
annotation|@
name|MBeanInfo
argument_list|(
literal|"messageId"
argument_list|)
name|String
name|messageId
parameter_list|,
annotation|@
name|MBeanInfo
argument_list|(
literal|"destinationName"
argument_list|)
name|String
name|destinationName
parameter_list|)
throws|throws
name|Exception
function_decl|;
comment|/**      * Moves a message back to its original destination      */
annotation|@
name|MBeanInfo
argument_list|(
literal|"Moves a message with the given JMS message back to its original destination"
argument_list|)
name|boolean
name|retryMessage
parameter_list|(
annotation|@
name|MBeanInfo
argument_list|(
literal|"messageId"
argument_list|)
name|String
name|messageId
parameter_list|)
throws|throws
name|Exception
function_decl|;
comment|/**      * Moves the messages matching the given selector      *       * @return the number of messages removed      */
annotation|@
name|MBeanInfo
argument_list|(
literal|"Moves messages based on an SQL-92 selecton on the message headers or XPATH on the body into the specified destination."
argument_list|)
name|int
name|moveMatchingMessagesTo
parameter_list|(
annotation|@
name|MBeanInfo
argument_list|(
literal|"selector"
argument_list|)
name|String
name|selector
parameter_list|,
annotation|@
name|MBeanInfo
argument_list|(
literal|"destinationName"
argument_list|)
name|String
name|destinationName
parameter_list|)
throws|throws
name|Exception
function_decl|;
comment|/**      * Moves the messages matching the given selector up to the maximum number      * of matched messages      */
annotation|@
name|MBeanInfo
argument_list|(
literal|"Moves up to a specified number of messages based on an SQL-92 selecton on the message headers or XPATH on the body into the specified destination."
argument_list|)
name|int
name|moveMatchingMessagesTo
parameter_list|(
annotation|@
name|MBeanInfo
argument_list|(
literal|"selector"
argument_list|)
name|String
name|selector
parameter_list|,
annotation|@
name|MBeanInfo
argument_list|(
literal|"destinationName"
argument_list|)
name|String
name|destinationName
parameter_list|,
annotation|@
name|MBeanInfo
argument_list|(
literal|"maximumMessages"
argument_list|)
name|int
name|maximumMessages
parameter_list|)
throws|throws
name|Exception
function_decl|;
comment|/**      * @return true if the message cursor has memory space available      * to page in more messages      */
annotation|@
name|MBeanInfo
argument_list|(
literal|"Message cursor has memory space available"
argument_list|)
specifier|public
name|boolean
name|doesCursorHaveSpace
parameter_list|()
function_decl|;
comment|/**      * @return true if the cursor has reached its memory limit for      * paged in messages      */
annotation|@
name|MBeanInfo
argument_list|(
literal|"Message cusor has reached its memory limit for paged in messages"
argument_list|)
specifier|public
name|boolean
name|isCursorFull
parameter_list|()
function_decl|;
comment|/**      * @return true if the cursor has messages buffered to deliver      */
annotation|@
name|MBeanInfo
argument_list|(
literal|"Message cursor has buffered messages to deliver"
argument_list|)
specifier|public
name|boolean
name|doesCursorHaveMessagesBuffered
parameter_list|()
function_decl|;
comment|/**      * @return the cursor memory usage in bytes      */
annotation|@
name|MBeanInfo
argument_list|(
literal|"Message cursor memory usage, in bytes."
argument_list|)
specifier|public
name|long
name|getCursorMemoryUsage
parameter_list|()
function_decl|;
comment|/**      * @return the cursor memory usage as a percentage      */
annotation|@
name|MBeanInfo
argument_list|(
literal|"Percentage of memory limit used"
argument_list|)
specifier|public
name|int
name|getCursorPercentUsage
parameter_list|()
function_decl|;
comment|/**      * @return the number of messages available to be paged in       * by the cursor      */
annotation|@
name|MBeanInfo
argument_list|(
literal|"Number of messages available to be paged in by the cursor."
argument_list|)
specifier|public
name|int
name|cursorSize
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

