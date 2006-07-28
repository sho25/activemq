begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
comment|/** 	 * Retrieve a message from the destination's queue. 	 *  	 * @param messageId the message id of the message to retreive 	 * @return A CompositeData object which is a JMX version of the messages 	 * @throws OpenDataException 	 */
specifier|public
name|CompositeData
name|getMessage
parameter_list|(
name|String
name|messageId
parameter_list|)
throws|throws
name|OpenDataException
function_decl|;
comment|/**      * Removes a message from the queue.  If the message has allready been dispatched       * to another consumer, the message cannot be delted and this method will return       * false.      *       * @param messageId       * @return true if the message was found and could be succesfully deleted.      */
specifier|public
name|boolean
name|removeMessage
parameter_list|(
name|String
name|messageId
parameter_list|)
function_decl|;
comment|/**      * Emptys out all the messages in the queue.      */
specifier|public
name|void
name|purge
parameter_list|()
function_decl|;
comment|/**      * Copys a given message to another destination.      *       * @param messageId      * @param destinationName      * @return true if the message was found and was successfuly copied to the other destination.      * @throws Exception      */
specifier|public
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
block|}
end_interface

end_unit

