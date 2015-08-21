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

begin_comment
comment|/**  *   */
end_comment

begin_interface
specifier|public
interface|interface
name|DurableSubscriptionViewMBean
extends|extends
name|SubscriptionViewMBean
block|{
comment|/**      * @return name of the durable subscription name      */
annotation|@
name|MBeanInfo
argument_list|(
literal|"The subscription name."
argument_list|)
name|String
name|getSubscriptionName
parameter_list|()
function_decl|;
comment|/**      * Browse messages for this durable subscriber      *       * @return messages      * @throws OpenDataException      */
annotation|@
name|MBeanInfo
argument_list|(
literal|"Browse the composite data array of pending messages in this subscription"
argument_list|)
name|CompositeData
index|[]
name|browse
parameter_list|()
throws|throws
name|OpenDataException
function_decl|;
comment|/**      * Browse messages for this durable subscriber      *       * @return messages      * @throws OpenDataException      */
annotation|@
name|MBeanInfo
argument_list|(
literal|"Browse the tabular data of pending messages in this subscription"
argument_list|)
name|TabularData
name|browseAsTable
parameter_list|()
throws|throws
name|OpenDataException
function_decl|;
comment|/**      * Destroys the durable subscription so that messages will no longer be      * stored for this subscription      */
annotation|@
name|MBeanInfo
argument_list|(
literal|"Destroy or delete this subscription"
argument_list|)
name|void
name|destroy
parameter_list|()
throws|throws
name|Exception
function_decl|;
comment|/**      * @return true if the message cursor has memory space available      * to page in more messages      */
annotation|@
name|MBeanInfo
argument_list|(
literal|"The subscription has space for more messages in memory"
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
literal|"The subscription cursor is full"
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
literal|"The subscription cursor has messages in memory"
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
literal|"The subscription cursor memory usage bytes"
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
literal|"The subscription cursor memory usage %"
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
literal|"The subscription cursor size or message count"
argument_list|)
specifier|public
name|int
name|cursorSize
parameter_list|()
function_decl|;
comment|/**      * Removes a message from the durable subscription.      *      * @param messageId      * @throws Exception      */
annotation|@
name|MBeanInfo
argument_list|(
literal|"Remove a message from the subscription by JMS message ID."
argument_list|)
specifier|public
name|void
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
block|}
end_interface

end_unit

