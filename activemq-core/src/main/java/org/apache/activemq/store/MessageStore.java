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
name|activeio
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

begin_comment
comment|/**  * Represents a message store which is used by the persistent {@link org.apache.activemq.service.MessageContainer}  * implementations  *  * @version $Revision: 1.5 $  */
end_comment

begin_interface
specifier|public
interface|interface
name|MessageStore
extends|extends
name|Service
block|{
comment|/**      * Adds a message to the message store      * @param context TODO      */
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
comment|/**      * Adds a message reference to the message store      * @param context TODO      * @param messageId TODO      * @param expirationTime TODO      */
specifier|public
name|void
name|addMessageReference
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|MessageId
name|messageId
parameter_list|,
name|long
name|expirationTime
parameter_list|,
name|String
name|messageRef
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * Looks up a message using either the String messageID or      * the messageNumber. Implementations are encouraged to fill in the missing      * key if its easy to do so.      * @param identity which contains either the messageID or the messageNumber      * @return the message or null if it does not exist      */
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
comment|/**      * Looks up a message using either the String messageID or      * the messageNumber. Implementations are encouraged to fill in the missing      * key if its easy to do so.      * @param identity which contains either the messageID or the messageNumber      * @return the message or null if it does not exist      */
specifier|public
name|String
name|getMessageReference
parameter_list|(
name|MessageId
name|identity
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * Removes a message from the message store.      * @param context TODO      * @param ack the ack request that cause the message to be removed.  It conatins       *   the identity which contains the messageID of the message that needs to be removed.      */
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
comment|/**      * Removes all the messages from the message store.      * @param context TODO      */
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
comment|/**      * Recover any messages to be delivered.      *      * @param container      * @throws Exception       */
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
comment|/**      * The destination that the message store is holding messages for.      * @return      */
specifier|public
name|ActiveMQDestination
name|getDestination
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

