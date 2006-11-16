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
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Enumeration
import|;
end_import

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
name|MessageEOFException
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

begin_comment
comment|/**  * A helper class for converting normal JMS interfaces into ActiveMQ specific ones.  *   * @version $Revision: 1.1 $  */
end_comment

begin_class
specifier|public
class|class
name|ActiveMQMessageTransformation
block|{
comment|/**      * Creates a an available JMS message from another provider. 	 *  	 * @param destination -  Destination to be converted into ActiveMQ's implementation. 	 * @return ActiveMQDestination - ActiveMQ's implementation of the destination. 	 * @throws JMSException if an error occurs 	 */
specifier|public
specifier|static
name|ActiveMQDestination
name|transformDestination
parameter_list|(
name|Destination
name|destination
parameter_list|)
throws|throws
name|JMSException
block|{
name|ActiveMQDestination
name|activeMQDestination
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|destination
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|destination
operator|instanceof
name|ActiveMQDestination
condition|)
block|{
return|return
operator|(
name|ActiveMQDestination
operator|)
name|destination
return|;
block|}
else|else
block|{
if|if
condition|(
name|destination
operator|instanceof
name|TemporaryQueue
condition|)
block|{
name|activeMQDestination
operator|=
operator|new
name|ActiveMQTempQueue
argument_list|(
operator|(
operator|(
name|Queue
operator|)
name|destination
operator|)
operator|.
name|getQueueName
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|destination
operator|instanceof
name|TemporaryTopic
condition|)
block|{
name|activeMQDestination
operator|=
operator|new
name|ActiveMQTempTopic
argument_list|(
operator|(
operator|(
name|Topic
operator|)
name|destination
operator|)
operator|.
name|getTopicName
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|destination
operator|instanceof
name|Queue
condition|)
block|{
name|activeMQDestination
operator|=
operator|new
name|ActiveMQQueue
argument_list|(
operator|(
operator|(
name|Queue
operator|)
name|destination
operator|)
operator|.
name|getQueueName
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|destination
operator|instanceof
name|Topic
condition|)
block|{
name|activeMQDestination
operator|=
operator|new
name|ActiveMQTopic
argument_list|(
operator|(
operator|(
name|Topic
operator|)
name|destination
operator|)
operator|.
name|getTopicName
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|activeMQDestination
return|;
block|}
comment|/**      * Creates a fast shallow copy of the current ActiveMQMessage or creates a whole new      * message instance from an available JMS message from another provider.      *      * @param message - Message to be converted into ActiveMQ's implementation.      * @param connection       * @return ActiveMQMessage -  ActiveMQ's implementation object of the message.      * @throws JMSException if an error occurs      */
specifier|public
specifier|static
specifier|final
name|ActiveMQMessage
name|transformMessage
parameter_list|(
name|Message
name|message
parameter_list|,
name|ActiveMQConnection
name|connection
parameter_list|)
throws|throws
name|JMSException
block|{
if|if
condition|(
name|message
operator|instanceof
name|ActiveMQMessage
condition|)
block|{
return|return
operator|(
name|ActiveMQMessage
operator|)
name|message
return|;
block|}
else|else
block|{
name|ActiveMQMessage
name|activeMessage
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|message
operator|instanceof
name|BytesMessage
condition|)
block|{
name|BytesMessage
name|bytesMsg
init|=
operator|(
name|BytesMessage
operator|)
name|message
decl_stmt|;
name|bytesMsg
operator|.
name|reset
argument_list|()
expr_stmt|;
name|ActiveMQBytesMessage
name|msg
init|=
operator|new
name|ActiveMQBytesMessage
argument_list|()
decl_stmt|;
name|msg
operator|.
name|setConnection
argument_list|(
name|connection
argument_list|)
expr_stmt|;
try|try
block|{
for|for
control|(
init|;
condition|;
control|)
block|{
comment|// Reads a byte from the message stream until the stream
comment|// is empty
name|msg
operator|.
name|writeByte
argument_list|(
name|bytesMsg
operator|.
name|readByte
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|MessageEOFException
name|e
parameter_list|)
block|{
comment|// if an end of message stream as expected
block|}
catch|catch
parameter_list|(
name|JMSException
name|e
parameter_list|)
block|{                 }
name|activeMessage
operator|=
name|msg
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|message
operator|instanceof
name|MapMessage
condition|)
block|{
name|MapMessage
name|mapMsg
init|=
operator|(
name|MapMessage
operator|)
name|message
decl_stmt|;
name|ActiveMQMapMessage
name|msg
init|=
operator|new
name|ActiveMQMapMessage
argument_list|()
decl_stmt|;
name|msg
operator|.
name|setConnection
argument_list|(
name|connection
argument_list|)
expr_stmt|;
name|Enumeration
name|iter
init|=
name|mapMsg
operator|.
name|getMapNames
argument_list|()
decl_stmt|;
while|while
condition|(
name|iter
operator|.
name|hasMoreElements
argument_list|()
condition|)
block|{
name|String
name|name
init|=
name|iter
operator|.
name|nextElement
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
name|msg
operator|.
name|setObject
argument_list|(
name|name
argument_list|,
name|mapMsg
operator|.
name|getObject
argument_list|(
name|name
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|activeMessage
operator|=
name|msg
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|message
operator|instanceof
name|ObjectMessage
condition|)
block|{
name|ObjectMessage
name|objMsg
init|=
operator|(
name|ObjectMessage
operator|)
name|message
decl_stmt|;
name|ActiveMQObjectMessage
name|msg
init|=
operator|new
name|ActiveMQObjectMessage
argument_list|()
decl_stmt|;
name|msg
operator|.
name|setConnection
argument_list|(
name|connection
argument_list|)
expr_stmt|;
name|msg
operator|.
name|setObject
argument_list|(
name|objMsg
operator|.
name|getObject
argument_list|()
argument_list|)
expr_stmt|;
name|msg
operator|.
name|storeContent
argument_list|()
expr_stmt|;
name|activeMessage
operator|=
name|msg
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|message
operator|instanceof
name|StreamMessage
condition|)
block|{
name|StreamMessage
name|streamMessage
init|=
operator|(
name|StreamMessage
operator|)
name|message
decl_stmt|;
name|streamMessage
operator|.
name|reset
argument_list|()
expr_stmt|;
name|ActiveMQStreamMessage
name|msg
init|=
operator|new
name|ActiveMQStreamMessage
argument_list|()
decl_stmt|;
name|msg
operator|.
name|setConnection
argument_list|(
name|connection
argument_list|)
expr_stmt|;
name|Object
name|obj
init|=
literal|null
decl_stmt|;
try|try
block|{
while|while
condition|(
operator|(
name|obj
operator|=
name|streamMessage
operator|.
name|readObject
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
name|msg
operator|.
name|writeObject
argument_list|(
name|obj
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|MessageEOFException
name|e
parameter_list|)
block|{
comment|// if an end of message stream as expected
block|}
catch|catch
parameter_list|(
name|JMSException
name|e
parameter_list|)
block|{                 }
name|activeMessage
operator|=
name|msg
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|message
operator|instanceof
name|TextMessage
condition|)
block|{
name|TextMessage
name|textMsg
init|=
operator|(
name|TextMessage
operator|)
name|message
decl_stmt|;
name|ActiveMQTextMessage
name|msg
init|=
operator|new
name|ActiveMQTextMessage
argument_list|()
decl_stmt|;
name|msg
operator|.
name|setConnection
argument_list|(
name|connection
argument_list|)
expr_stmt|;
name|msg
operator|.
name|setText
argument_list|(
name|textMsg
operator|.
name|getText
argument_list|()
argument_list|)
expr_stmt|;
name|activeMessage
operator|=
name|msg
expr_stmt|;
block|}
else|else
block|{
name|activeMessage
operator|=
operator|new
name|ActiveMQMessage
argument_list|()
expr_stmt|;
name|activeMessage
operator|.
name|setConnection
argument_list|(
name|connection
argument_list|)
expr_stmt|;
block|}
name|copyProperties
argument_list|(
name|message
argument_list|,
name|activeMessage
argument_list|)
expr_stmt|;
return|return
name|activeMessage
return|;
block|}
block|}
comment|/**      * Copies the standard JMS and user defined properties from the givem message to the specified message      *      * @param fromMessage the message to take the properties from      * @param toMesage the message to add the properties to      * @throws JMSException      */
specifier|public
specifier|static
name|void
name|copyProperties
parameter_list|(
name|Message
name|fromMessage
parameter_list|,
name|Message
name|toMesage
parameter_list|)
throws|throws
name|JMSException
block|{
name|toMesage
operator|.
name|setJMSMessageID
argument_list|(
name|fromMessage
operator|.
name|getJMSMessageID
argument_list|()
argument_list|)
expr_stmt|;
name|toMesage
operator|.
name|setJMSCorrelationID
argument_list|(
name|fromMessage
operator|.
name|getJMSCorrelationID
argument_list|()
argument_list|)
expr_stmt|;
name|toMesage
operator|.
name|setJMSReplyTo
argument_list|(
name|transformDestination
argument_list|(
name|fromMessage
operator|.
name|getJMSReplyTo
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|toMesage
operator|.
name|setJMSDestination
argument_list|(
name|transformDestination
argument_list|(
name|fromMessage
operator|.
name|getJMSDestination
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|toMesage
operator|.
name|setJMSDeliveryMode
argument_list|(
name|fromMessage
operator|.
name|getJMSDeliveryMode
argument_list|()
argument_list|)
expr_stmt|;
name|toMesage
operator|.
name|setJMSRedelivered
argument_list|(
name|fromMessage
operator|.
name|getJMSRedelivered
argument_list|()
argument_list|)
expr_stmt|;
name|toMesage
operator|.
name|setJMSType
argument_list|(
name|fromMessage
operator|.
name|getJMSType
argument_list|()
argument_list|)
expr_stmt|;
name|toMesage
operator|.
name|setJMSExpiration
argument_list|(
name|fromMessage
operator|.
name|getJMSExpiration
argument_list|()
argument_list|)
expr_stmt|;
name|toMesage
operator|.
name|setJMSPriority
argument_list|(
name|fromMessage
operator|.
name|getJMSPriority
argument_list|()
argument_list|)
expr_stmt|;
name|toMesage
operator|.
name|setJMSTimestamp
argument_list|(
name|fromMessage
operator|.
name|getJMSTimestamp
argument_list|()
argument_list|)
expr_stmt|;
name|Enumeration
name|propertyNames
init|=
name|fromMessage
operator|.
name|getPropertyNames
argument_list|()
decl_stmt|;
while|while
condition|(
name|propertyNames
operator|.
name|hasMoreElements
argument_list|()
condition|)
block|{
name|String
name|name
init|=
name|propertyNames
operator|.
name|nextElement
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
name|Object
name|obj
init|=
name|fromMessage
operator|.
name|getObjectProperty
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|toMesage
operator|.
name|setObjectProperty
argument_list|(
name|name
argument_list|,
name|obj
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

