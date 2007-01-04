begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *   * Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements. See the NOTICE  * file distributed with this work for additional information regarding copyright ownership. The ASF licenses this file  * to You under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the  * License. You may obtain a copy of the License at  *   * http://www.apache.org/licenses/LICENSE-2.0  *   * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on  * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the  * specific language governing permissions and limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|store
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
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|Service
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
name|command
operator|.
name|ActiveMQDestination
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
name|Message
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
name|MessageAck
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
name|MessageId
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
name|memory
operator|.
name|UsageManager
import|;
end_import

begin_comment
comment|/**  * Represents a message store which is used by the persistent   * implementations  *   * @version $Revision: 1.5 $  */
end_comment

begin_interface
specifier|public
interface|interface
name|MessageStore
extends|extends
name|Service
block|{
comment|/**      * Adds a message to the message store      *       * @param context context      * @param message       * @throws IOException       */
specifier|public
name|void
name|addMessage
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|Message
name|message
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * Looks up a message using either the String messageID or the messageNumber. Implementations are encouraged to fill      * in the missing key if its easy to do so.      *       * @param identity which contains either the messageID or the messageNumber      * @return the message or null if it does not exist      * @throws IOException       */
specifier|public
name|Message
name|getMessage
parameter_list|(
name|MessageId
name|identity
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * Removes a message from the message store.      *       * @param context       * @param ack the ack request that cause the message to be removed. It conatins the identity which contains the      *            messageID of the message that needs to be removed.      * @throws IOException       */
specifier|public
name|void
name|removeMessage
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|MessageAck
name|ack
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * Removes all the messages from the message store.      *       * @param context       * @throws IOException       */
specifier|public
name|void
name|removeAllMessages
parameter_list|(
name|ConnectionContext
name|context
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * Recover any messages to be delivered.      *       * @param container      * @throws Exception      */
specifier|public
name|void
name|recover
parameter_list|(
name|MessageRecoveryListener
name|container
parameter_list|)
throws|throws
name|Exception
function_decl|;
comment|/**      * The destination that the message store is holding messages for.      *       * @return the destination      */
specifier|public
name|ActiveMQDestination
name|getDestination
parameter_list|()
function_decl|;
comment|/**      * @param usageManager The UsageManager that is controlling the destination's memory usage.      */
specifier|public
name|void
name|setUsageManager
parameter_list|(
name|UsageManager
name|usageManager
parameter_list|)
function_decl|;
comment|/**      * @return the number of messages ready to deliver      * @throws IOException       *       */
specifier|public
name|int
name|getMessageCount
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**      * A hint to the Store to reset any batching state for the Destination      *       * @param nextToDispatch      *       */
specifier|public
name|void
name|resetBatching
parameter_list|()
function_decl|;
specifier|public
name|void
name|recoverNextMessages
parameter_list|(
name|int
name|maxReturned
parameter_list|,
name|MessageRecoveryListener
name|listener
parameter_list|)
throws|throws
name|Exception
function_decl|;
block|}
end_interface

end_unit

