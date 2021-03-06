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
name|Map
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
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|command
operator|.
name|ConsumerInfo
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
name|MessageDispatch
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
name|TransactionId
import|;
end_import

begin_class
specifier|public
class|class
name|StompQueueBrowserSubscription
extends|extends
name|StompSubscription
block|{
specifier|public
name|StompQueueBrowserSubscription
parameter_list|(
name|ProtocolConverter
name|stompTransport
parameter_list|,
name|String
name|subscriptionId
parameter_list|,
name|ConsumerInfo
name|consumerInfo
parameter_list|,
name|String
name|transformation
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|StompAckEntry
argument_list|>
name|pendingAcks
parameter_list|)
block|{
name|super
argument_list|(
name|stompTransport
argument_list|,
name|subscriptionId
argument_list|,
name|consumerInfo
argument_list|,
name|transformation
argument_list|,
name|pendingAcks
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
name|void
name|onMessageDispatch
parameter_list|(
name|MessageDispatch
name|md
parameter_list|)
throws|throws
name|IOException
throws|,
name|JMSException
block|{
if|if
condition|(
name|md
operator|.
name|getMessage
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|super
operator|.
name|onMessageDispatch
argument_list|(
name|md
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|StompFrame
name|browseDone
init|=
operator|new
name|StompFrame
argument_list|(
name|Stomp
operator|.
name|Responses
operator|.
name|MESSAGE
argument_list|)
decl_stmt|;
name|browseDone
operator|.
name|getHeaders
argument_list|()
operator|.
name|put
argument_list|(
name|Stomp
operator|.
name|Headers
operator|.
name|Message
operator|.
name|SUBSCRIPTION
argument_list|,
name|this
operator|.
name|getSubscriptionId
argument_list|()
argument_list|)
expr_stmt|;
name|browseDone
operator|.
name|getHeaders
argument_list|()
operator|.
name|put
argument_list|(
name|Stomp
operator|.
name|Headers
operator|.
name|Message
operator|.
name|BROWSER
argument_list|,
literal|"end"
argument_list|)
expr_stmt|;
name|browseDone
operator|.
name|getHeaders
argument_list|()
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
name|protocolConverter
operator|.
name|findTranslator
argument_list|(
literal|null
argument_list|)
operator|.
name|convertDestination
argument_list|(
name|protocolConverter
argument_list|,
name|this
operator|.
name|destination
argument_list|)
argument_list|)
expr_stmt|;
name|browseDone
operator|.
name|getHeaders
argument_list|()
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
literal|"0"
argument_list|)
expr_stmt|;
name|protocolConverter
operator|.
name|sendToStomp
argument_list|(
name|browseDone
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|MessageAck
name|onStompMessageNack
parameter_list|(
name|String
name|messageId
parameter_list|,
name|TransactionId
name|transactionId
parameter_list|)
throws|throws
name|ProtocolException
block|{
throw|throw
operator|new
name|ProtocolException
argument_list|(
literal|"Cannot Nack a message on a Queue Browser Subscription."
argument_list|)
throw|;
block|}
block|}
end_class

end_unit

