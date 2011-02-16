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
name|transport
operator|.
name|stomp
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
name|HashMap
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
name|Destination
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|JMSException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|MessageListener
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|MessageProducer
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
name|ActiveMQMessage
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

begin_comment
comment|/**  * Implementations of this interface are used to map back and forth from Stomp  * to ActiveMQ. There are several standard mappings which are semantically the  * same, the inner class, Helper, provides functions to copy those properties  * from one to the other  */
end_comment

begin_interface
specifier|public
interface|interface
name|FrameTranslator
block|{
name|ActiveMQMessage
name|convertFrame
parameter_list|(
name|ProtocolConverter
name|converter
parameter_list|,
name|StompFrame
name|frame
parameter_list|)
throws|throws
name|JMSException
throws|,
name|ProtocolException
function_decl|;
name|StompFrame
name|convertMessage
parameter_list|(
name|ProtocolConverter
name|converter
parameter_list|,
name|ActiveMQMessage
name|message
parameter_list|)
throws|throws
name|IOException
throws|,
name|JMSException
function_decl|;
name|String
name|convertDestination
parameter_list|(
name|ProtocolConverter
name|converter
parameter_list|,
name|Destination
name|d
parameter_list|)
function_decl|;
name|ActiveMQDestination
name|convertDestination
parameter_list|(
name|ProtocolConverter
name|converter
parameter_list|,
name|String
name|name
parameter_list|)
throws|throws
name|ProtocolException
function_decl|;
comment|/**      * Helper class which holds commonly needed functions used when implementing      * FrameTranslators      */
specifier|static
specifier|final
class|class
name|Helper
block|{
specifier|private
name|Helper
parameter_list|()
block|{         }
specifier|public
specifier|static
name|void
name|copyStandardHeadersFromMessageToFrame
parameter_list|(
name|ProtocolConverter
name|converter
parameter_list|,
name|ActiveMQMessage
name|message
parameter_list|,
name|StompFrame
name|command
parameter_list|,
name|FrameTranslator
name|ft
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|headers
init|=
name|command
operator|.
name|getHeaders
argument_list|()
decl_stmt|;
name|headers
operator|.
name|put
argument_list|(
name|Stomp
operator|.
name|Headers
operator|.
name|Message
operator|.
name|DESTINATION
argument_list|,
name|ft
operator|.
name|convertDestination
argument_list|(
name|converter
argument_list|,
name|message
operator|.
name|getDestination
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|headers
operator|.
name|put
argument_list|(
name|Stomp
operator|.
name|Headers
operator|.
name|Message
operator|.
name|MESSAGE_ID
argument_list|,
name|message
operator|.
name|getJMSMessageID
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|message
operator|.
name|getJMSCorrelationID
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|headers
operator|.
name|put
argument_list|(
name|Stomp
operator|.
name|Headers
operator|.
name|Message
operator|.
name|CORRELATION_ID
argument_list|,
name|message
operator|.
name|getJMSCorrelationID
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|headers
operator|.
name|put
argument_list|(
name|Stomp
operator|.
name|Headers
operator|.
name|Message
operator|.
name|EXPIRATION_TIME
argument_list|,
literal|""
operator|+
name|message
operator|.
name|getJMSExpiration
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|message
operator|.
name|getJMSRedelivered
argument_list|()
condition|)
block|{
name|headers
operator|.
name|put
argument_list|(
name|Stomp
operator|.
name|Headers
operator|.
name|Message
operator|.
name|REDELIVERED
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
block|}
name|headers
operator|.
name|put
argument_list|(
name|Stomp
operator|.
name|Headers
operator|.
name|Message
operator|.
name|PRORITY
argument_list|,
literal|""
operator|+
name|message
operator|.
name|getJMSPriority
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|message
operator|.
name|getJMSReplyTo
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|headers
operator|.
name|put
argument_list|(
name|Stomp
operator|.
name|Headers
operator|.
name|Message
operator|.
name|REPLY_TO
argument_list|,
name|ft
operator|.
name|convertDestination
argument_list|(
name|converter
argument_list|,
name|message
operator|.
name|getJMSReplyTo
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|headers
operator|.
name|put
argument_list|(
name|Stomp
operator|.
name|Headers
operator|.
name|Message
operator|.
name|TIMESTAMP
argument_list|,
literal|""
operator|+
name|message
operator|.
name|getJMSTimestamp
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|message
operator|.
name|getJMSType
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|headers
operator|.
name|put
argument_list|(
name|Stomp
operator|.
name|Headers
operator|.
name|Message
operator|.
name|TYPE
argument_list|,
name|message
operator|.
name|getJMSType
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|message
operator|.
name|getUserID
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|headers
operator|.
name|put
argument_list|(
name|Stomp
operator|.
name|Headers
operator|.
name|Message
operator|.
name|USERID
argument_list|,
name|message
operator|.
name|getUserID
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|message
operator|.
name|getOriginalDestination
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|headers
operator|.
name|put
argument_list|(
name|Stomp
operator|.
name|Headers
operator|.
name|Message
operator|.
name|ORIGINAL_DESTINATION
argument_list|,
name|ft
operator|.
name|convertDestination
argument_list|(
name|converter
argument_list|,
name|message
operator|.
name|getOriginalDestination
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// now lets add all the message headers
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|properties
init|=
name|message
operator|.
name|getProperties
argument_list|()
decl_stmt|;
if|if
condition|(
name|properties
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|prop
range|:
name|properties
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|headers
operator|.
name|put
argument_list|(
name|prop
operator|.
name|getKey
argument_list|()
argument_list|,
literal|""
operator|+
name|prop
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|public
specifier|static
name|void
name|copyStandardHeadersFromFrameToMessage
parameter_list|(
name|ProtocolConverter
name|converter
parameter_list|,
name|StompFrame
name|command
parameter_list|,
name|ActiveMQMessage
name|msg
parameter_list|,
name|FrameTranslator
name|ft
parameter_list|)
throws|throws
name|ProtocolException
throws|,
name|JMSException
block|{
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|headers
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|(
name|command
operator|.
name|getHeaders
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|String
name|destination
init|=
name|headers
operator|.
name|remove
argument_list|(
name|Stomp
operator|.
name|Headers
operator|.
name|Send
operator|.
name|DESTINATION
argument_list|)
decl_stmt|;
name|msg
operator|.
name|setDestination
argument_list|(
name|ft
operator|.
name|convertDestination
argument_list|(
name|converter
argument_list|,
name|destination
argument_list|)
argument_list|)
expr_stmt|;
comment|// the standard JMS headers
name|msg
operator|.
name|setJMSCorrelationID
argument_list|(
name|headers
operator|.
name|remove
argument_list|(
name|Stomp
operator|.
name|Headers
operator|.
name|Send
operator|.
name|CORRELATION_ID
argument_list|)
argument_list|)
expr_stmt|;
name|Object
name|o
init|=
name|headers
operator|.
name|remove
argument_list|(
name|Stomp
operator|.
name|Headers
operator|.
name|Send
operator|.
name|EXPIRATION_TIME
argument_list|)
decl_stmt|;
if|if
condition|(
name|o
operator|!=
literal|null
condition|)
block|{
name|msg
operator|.
name|setJMSExpiration
argument_list|(
name|Long
operator|.
name|parseLong
argument_list|(
operator|(
name|String
operator|)
name|o
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|o
operator|=
name|headers
operator|.
name|remove
argument_list|(
name|Stomp
operator|.
name|Headers
operator|.
name|Send
operator|.
name|PRIORITY
argument_list|)
expr_stmt|;
if|if
condition|(
name|o
operator|!=
literal|null
condition|)
block|{
name|msg
operator|.
name|setJMSPriority
argument_list|(
name|Integer
operator|.
name|parseInt
argument_list|(
operator|(
name|String
operator|)
name|o
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|msg
operator|.
name|setJMSPriority
argument_list|(
name|javax
operator|.
name|jms
operator|.
name|Message
operator|.
name|DEFAULT_PRIORITY
argument_list|)
expr_stmt|;
block|}
name|o
operator|=
name|headers
operator|.
name|remove
argument_list|(
name|Stomp
operator|.
name|Headers
operator|.
name|Send
operator|.
name|TYPE
argument_list|)
expr_stmt|;
if|if
condition|(
name|o
operator|!=
literal|null
condition|)
block|{
name|msg
operator|.
name|setJMSType
argument_list|(
operator|(
name|String
operator|)
name|o
argument_list|)
expr_stmt|;
block|}
name|o
operator|=
name|headers
operator|.
name|remove
argument_list|(
name|Stomp
operator|.
name|Headers
operator|.
name|Send
operator|.
name|REPLY_TO
argument_list|)
expr_stmt|;
if|if
condition|(
name|o
operator|!=
literal|null
condition|)
block|{
name|msg
operator|.
name|setJMSReplyTo
argument_list|(
name|ft
operator|.
name|convertDestination
argument_list|(
name|converter
argument_list|,
operator|(
name|String
operator|)
name|o
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|o
operator|=
name|headers
operator|.
name|remove
argument_list|(
name|Stomp
operator|.
name|Headers
operator|.
name|Send
operator|.
name|PERSISTENT
argument_list|)
expr_stmt|;
if|if
condition|(
name|o
operator|!=
literal|null
condition|)
block|{
name|msg
operator|.
name|setPersistent
argument_list|(
literal|"true"
operator|.
name|equals
argument_list|(
name|o
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// Stomp specific headers
name|headers
operator|.
name|remove
argument_list|(
name|Stomp
operator|.
name|Headers
operator|.
name|RECEIPT_REQUESTED
argument_list|)
expr_stmt|;
comment|// Since we take the rest of the header and put them in properties which could then
comment|// be sent back to a STOMP consumer we need to sanitize anything which could be in
comment|// Stomp.Headers.Message and might get passed through to the consumer
name|headers
operator|.
name|remove
argument_list|(
name|Stomp
operator|.
name|Headers
operator|.
name|Message
operator|.
name|MESSAGE_ID
argument_list|)
expr_stmt|;
name|headers
operator|.
name|remove
argument_list|(
name|Stomp
operator|.
name|Headers
operator|.
name|Message
operator|.
name|TIMESTAMP
argument_list|)
expr_stmt|;
name|headers
operator|.
name|remove
argument_list|(
name|Stomp
operator|.
name|Headers
operator|.
name|Message
operator|.
name|REDELIVERED
argument_list|)
expr_stmt|;
name|headers
operator|.
name|remove
argument_list|(
name|Stomp
operator|.
name|Headers
operator|.
name|Message
operator|.
name|SUBSCRIPTION
argument_list|)
expr_stmt|;
name|headers
operator|.
name|remove
argument_list|(
name|Stomp
operator|.
name|Headers
operator|.
name|Message
operator|.
name|USERID
argument_list|)
expr_stmt|;
comment|// now the general headers
name|msg
operator|.
name|setProperties
argument_list|(
name|headers
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_interface

end_unit

