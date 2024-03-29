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
name|ra
package|;
end_package

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
name|Message
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
name|QueueSender
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
name|javax
operator|.
name|jms
operator|.
name|TopicPublisher
import|;
end_import

begin_comment
comment|/**  * An implementation of {@link MessageProducer} which uses the ActiveMQ JCA ResourceAdapter's  * current thread's JMS {@link javax.jms.Session} to send messages.  *  *   */
end_comment

begin_class
specifier|public
class|class
name|InboundMessageProducerProxy
implements|implements
name|MessageProducer
implements|,
name|QueueSender
implements|,
name|TopicPublisher
block|{
specifier|private
name|MessageProducer
name|messageProducer
decl_stmt|;
specifier|private
name|Destination
name|destination
decl_stmt|;
specifier|private
name|int
name|deliveryMode
decl_stmt|;
specifier|private
name|boolean
name|disableMessageID
decl_stmt|;
specifier|private
name|boolean
name|disableMessageTimestamp
decl_stmt|;
specifier|private
name|int
name|priority
decl_stmt|;
specifier|private
name|long
name|timeToLive
decl_stmt|;
specifier|public
name|InboundMessageProducerProxy
parameter_list|(
name|MessageProducer
name|messageProducer
parameter_list|,
name|Destination
name|destination
parameter_list|)
throws|throws
name|JMSException
block|{
name|this
operator|.
name|messageProducer
operator|=
name|messageProducer
expr_stmt|;
name|this
operator|.
name|destination
operator|=
name|destination
expr_stmt|;
name|this
operator|.
name|deliveryMode
operator|=
name|messageProducer
operator|.
name|getDeliveryMode
argument_list|()
expr_stmt|;
name|this
operator|.
name|disableMessageID
operator|=
name|messageProducer
operator|.
name|getDisableMessageID
argument_list|()
expr_stmt|;
name|this
operator|.
name|disableMessageTimestamp
operator|=
name|messageProducer
operator|.
name|getDisableMessageTimestamp
argument_list|()
expr_stmt|;
name|this
operator|.
name|priority
operator|=
name|messageProducer
operator|.
name|getPriority
argument_list|()
expr_stmt|;
name|this
operator|.
name|timeToLive
operator|=
name|messageProducer
operator|.
name|getTimeToLive
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|JMSException
block|{
comment|// do nothing as we just go back into the pool
comment|// though lets reset the various settings which may have been changed
name|messageProducer
operator|.
name|setDeliveryMode
argument_list|(
name|deliveryMode
argument_list|)
expr_stmt|;
name|messageProducer
operator|.
name|setDisableMessageID
argument_list|(
name|disableMessageID
argument_list|)
expr_stmt|;
name|messageProducer
operator|.
name|setDisableMessageTimestamp
argument_list|(
name|disableMessageTimestamp
argument_list|)
expr_stmt|;
name|messageProducer
operator|.
name|setPriority
argument_list|(
name|priority
argument_list|)
expr_stmt|;
name|messageProducer
operator|.
name|setTimeToLive
argument_list|(
name|timeToLive
argument_list|)
expr_stmt|;
block|}
specifier|public
name|Destination
name|getDestination
parameter_list|()
throws|throws
name|JMSException
block|{
return|return
name|destination
return|;
block|}
specifier|public
name|int
name|getDeliveryMode
parameter_list|()
throws|throws
name|JMSException
block|{
return|return
name|messageProducer
operator|.
name|getDeliveryMode
argument_list|()
return|;
block|}
specifier|public
name|boolean
name|getDisableMessageID
parameter_list|()
throws|throws
name|JMSException
block|{
return|return
name|messageProducer
operator|.
name|getDisableMessageID
argument_list|()
return|;
block|}
specifier|public
name|boolean
name|getDisableMessageTimestamp
parameter_list|()
throws|throws
name|JMSException
block|{
return|return
name|messageProducer
operator|.
name|getDisableMessageTimestamp
argument_list|()
return|;
block|}
specifier|public
name|int
name|getPriority
parameter_list|()
throws|throws
name|JMSException
block|{
return|return
name|messageProducer
operator|.
name|getPriority
argument_list|()
return|;
block|}
specifier|public
name|long
name|getTimeToLive
parameter_list|()
throws|throws
name|JMSException
block|{
return|return
name|messageProducer
operator|.
name|getTimeToLive
argument_list|()
return|;
block|}
specifier|public
name|void
name|send
parameter_list|(
name|Destination
name|destination
parameter_list|,
name|Message
name|message
parameter_list|)
throws|throws
name|JMSException
block|{
if|if
condition|(
name|destination
operator|==
literal|null
condition|)
block|{
name|destination
operator|=
name|this
operator|.
name|destination
expr_stmt|;
block|}
name|messageProducer
operator|.
name|send
argument_list|(
name|destination
argument_list|,
name|message
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|send
parameter_list|(
name|Destination
name|destination
parameter_list|,
name|Message
name|message
parameter_list|,
name|int
name|deliveryMode
parameter_list|,
name|int
name|priority
parameter_list|,
name|long
name|timeToLive
parameter_list|)
throws|throws
name|JMSException
block|{
if|if
condition|(
name|destination
operator|==
literal|null
condition|)
block|{
name|destination
operator|=
name|this
operator|.
name|destination
expr_stmt|;
block|}
name|messageProducer
operator|.
name|send
argument_list|(
name|destination
argument_list|,
name|message
argument_list|,
name|deliveryMode
argument_list|,
name|priority
argument_list|,
name|timeToLive
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|send
parameter_list|(
name|Message
name|message
parameter_list|)
throws|throws
name|JMSException
block|{
name|messageProducer
operator|.
name|send
argument_list|(
name|destination
argument_list|,
name|message
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|send
parameter_list|(
name|Message
name|message
parameter_list|,
name|int
name|deliveryMode
parameter_list|,
name|int
name|priority
parameter_list|,
name|long
name|timeToLive
parameter_list|)
throws|throws
name|JMSException
block|{
name|messageProducer
operator|.
name|send
argument_list|(
name|destination
argument_list|,
name|message
argument_list|,
name|deliveryMode
argument_list|,
name|priority
argument_list|,
name|timeToLive
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setDeliveryMode
parameter_list|(
name|int
name|i
parameter_list|)
throws|throws
name|JMSException
block|{
name|messageProducer
operator|.
name|setDeliveryMode
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setDisableMessageID
parameter_list|(
name|boolean
name|b
parameter_list|)
throws|throws
name|JMSException
block|{
name|messageProducer
operator|.
name|setDisableMessageID
argument_list|(
name|b
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setDisableMessageTimestamp
parameter_list|(
name|boolean
name|b
parameter_list|)
throws|throws
name|JMSException
block|{
name|messageProducer
operator|.
name|setDisableMessageTimestamp
argument_list|(
name|b
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setPriority
parameter_list|(
name|int
name|i
parameter_list|)
throws|throws
name|JMSException
block|{
name|messageProducer
operator|.
name|setPriority
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setTimeToLive
parameter_list|(
name|long
name|l
parameter_list|)
throws|throws
name|JMSException
block|{
name|messageProducer
operator|.
name|setTimeToLive
argument_list|(
name|l
argument_list|)
expr_stmt|;
block|}
specifier|public
name|Queue
name|getQueue
parameter_list|()
throws|throws
name|JMSException
block|{
return|return
operator|(
name|Queue
operator|)
name|messageProducer
operator|.
name|getDestination
argument_list|()
return|;
block|}
specifier|public
name|void
name|send
parameter_list|(
name|Queue
name|arg0
parameter_list|,
name|Message
name|arg1
parameter_list|)
throws|throws
name|JMSException
block|{
name|messageProducer
operator|.
name|send
argument_list|(
name|arg0
argument_list|,
name|arg1
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|send
parameter_list|(
name|Queue
name|arg0
parameter_list|,
name|Message
name|arg1
parameter_list|,
name|int
name|arg2
parameter_list|,
name|int
name|arg3
parameter_list|,
name|long
name|arg4
parameter_list|)
throws|throws
name|JMSException
block|{
name|messageProducer
operator|.
name|send
argument_list|(
name|arg0
argument_list|,
name|arg1
argument_list|,
name|arg2
argument_list|,
name|arg3
argument_list|,
name|arg4
argument_list|)
expr_stmt|;
block|}
specifier|public
name|Topic
name|getTopic
parameter_list|()
throws|throws
name|JMSException
block|{
return|return
operator|(
name|Topic
operator|)
name|messageProducer
operator|.
name|getDestination
argument_list|()
return|;
block|}
specifier|public
name|void
name|publish
parameter_list|(
name|Message
name|arg0
parameter_list|)
throws|throws
name|JMSException
block|{
name|messageProducer
operator|.
name|send
argument_list|(
name|arg0
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|publish
parameter_list|(
name|Message
name|arg0
parameter_list|,
name|int
name|arg1
parameter_list|,
name|int
name|arg2
parameter_list|,
name|long
name|arg3
parameter_list|)
throws|throws
name|JMSException
block|{
name|messageProducer
operator|.
name|send
argument_list|(
name|arg0
argument_list|,
name|arg1
argument_list|,
name|arg2
argument_list|,
name|arg3
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|publish
parameter_list|(
name|Topic
name|arg0
parameter_list|,
name|Message
name|arg1
parameter_list|)
throws|throws
name|JMSException
block|{
name|messageProducer
operator|.
name|send
argument_list|(
name|arg0
argument_list|,
name|arg1
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|publish
parameter_list|(
name|Topic
name|arg0
parameter_list|,
name|Message
name|arg1
parameter_list|,
name|int
name|arg2
parameter_list|,
name|int
name|arg3
parameter_list|,
name|long
name|arg4
parameter_list|)
throws|throws
name|JMSException
block|{
name|messageProducer
operator|.
name|send
argument_list|(
name|arg0
argument_list|,
name|arg1
argument_list|,
name|arg2
argument_list|,
name|arg3
argument_list|,
name|arg4
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

