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
name|amqp
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|BytesMessage
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
name|MapMessage
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|Message
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|ObjectMessage
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|Queue
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|StreamMessage
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|TemporaryQueue
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|TemporaryTopic
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|TextMessage
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|Topic
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
name|ActiveMQBytesMessage
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
name|ActiveMQMapMessage
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
name|ActiveMQObjectMessage
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
name|ActiveMQQueue
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
name|ActiveMQStreamMessage
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
name|ActiveMQTempQueue
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
name|ActiveMQTempTopic
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
name|ActiveMQTextMessage
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
name|ActiveMQTopic
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
name|transport
operator|.
name|amqp
operator|.
name|message
operator|.
name|JMSVendor
import|;
end_import

begin_class
specifier|public
class|class
name|ActiveMQJMSVendor
extends|extends
name|JMSVendor
block|{
specifier|final
specifier|public
specifier|static
name|ActiveMQJMSVendor
name|INSTANCE
init|=
operator|new
name|ActiveMQJMSVendor
argument_list|()
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|PREFIX_MARKER
init|=
literal|"://"
decl_stmt|;
specifier|private
name|ActiveMQJMSVendor
parameter_list|()
block|{     }
annotation|@
name|Override
specifier|public
name|BytesMessage
name|createBytesMessage
parameter_list|()
block|{
return|return
operator|new
name|ActiveMQBytesMessage
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|StreamMessage
name|createStreamMessage
parameter_list|()
block|{
return|return
operator|new
name|ActiveMQStreamMessage
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|Message
name|createMessage
parameter_list|()
block|{
return|return
operator|new
name|ActiveMQMessage
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|TextMessage
name|createTextMessage
parameter_list|()
block|{
return|return
operator|new
name|ActiveMQTextMessage
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|ObjectMessage
name|createObjectMessage
parameter_list|()
block|{
return|return
operator|new
name|ActiveMQObjectMessage
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|MapMessage
name|createMapMessage
parameter_list|()
block|{
return|return
operator|new
name|ActiveMQMapMessage
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|Destination
name|createDestination
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|super
operator|.
name|createDestination
argument_list|(
name|name
argument_list|,
name|Destination
operator|.
name|class
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
parameter_list|<
name|T
extends|extends
name|Destination
parameter_list|>
name|T
name|createDestination
parameter_list|(
name|String
name|name
parameter_list|,
name|Class
argument_list|<
name|T
argument_list|>
name|kind
parameter_list|)
block|{
name|String
name|destinationName
init|=
name|name
decl_stmt|;
name|int
name|prefixEnd
init|=
name|name
operator|.
name|lastIndexOf
argument_list|(
name|PREFIX_MARKER
argument_list|)
decl_stmt|;
if|if
condition|(
name|prefixEnd
operator|>=
literal|0
condition|)
block|{
name|destinationName
operator|=
name|name
operator|.
name|substring
argument_list|(
name|prefixEnd
operator|+
name|PREFIX_MARKER
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|kind
operator|==
name|Queue
operator|.
name|class
condition|)
block|{
return|return
name|kind
operator|.
name|cast
argument_list|(
operator|new
name|ActiveMQQueue
argument_list|(
name|destinationName
argument_list|)
argument_list|)
return|;
block|}
if|if
condition|(
name|kind
operator|==
name|Topic
operator|.
name|class
condition|)
block|{
return|return
name|kind
operator|.
name|cast
argument_list|(
operator|new
name|ActiveMQTopic
argument_list|(
name|destinationName
argument_list|)
argument_list|)
return|;
block|}
if|if
condition|(
name|kind
operator|==
name|TemporaryQueue
operator|.
name|class
condition|)
block|{
return|return
name|kind
operator|.
name|cast
argument_list|(
operator|new
name|ActiveMQTempQueue
argument_list|(
name|destinationName
argument_list|)
argument_list|)
return|;
block|}
if|if
condition|(
name|kind
operator|==
name|TemporaryTopic
operator|.
name|class
condition|)
block|{
return|return
name|kind
operator|.
name|cast
argument_list|(
operator|new
name|ActiveMQTempTopic
argument_list|(
name|destinationName
argument_list|)
argument_list|)
return|;
block|}
return|return
name|kind
operator|.
name|cast
argument_list|(
name|ActiveMQDestination
operator|.
name|createDestination
argument_list|(
name|name
argument_list|,
name|ActiveMQDestination
operator|.
name|QUEUE_TYPE
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setJMSXUserID
parameter_list|(
name|Message
name|msg
parameter_list|,
name|String
name|value
parameter_list|)
block|{
operator|(
operator|(
name|ActiveMQMessage
operator|)
name|msg
operator|)
operator|.
name|setUserID
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setJMSXGroupID
parameter_list|(
name|Message
name|msg
parameter_list|,
name|String
name|value
parameter_list|)
block|{
operator|(
operator|(
name|ActiveMQMessage
operator|)
name|msg
operator|)
operator|.
name|setGroupID
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setJMSXGroupSequence
parameter_list|(
name|Message
name|msg
parameter_list|,
name|int
name|value
parameter_list|)
block|{
operator|(
operator|(
name|ActiveMQMessage
operator|)
name|msg
operator|)
operator|.
name|setGroupSequence
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setJMSXDeliveryCount
parameter_list|(
name|Message
name|msg
parameter_list|,
name|long
name|value
parameter_list|)
block|{
operator|(
operator|(
name|ActiveMQMessage
operator|)
name|msg
operator|)
operator|.
name|setRedeliveryCounter
argument_list|(
operator|(
name|int
operator|)
name|value
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toAddress
parameter_list|(
name|Destination
name|dest
parameter_list|)
block|{
return|return
operator|(
operator|(
name|ActiveMQDestination
operator|)
name|dest
operator|)
operator|.
name|getQualifiedName
argument_list|()
return|;
block|}
block|}
end_class

end_unit

