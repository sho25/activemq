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
name|pool
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
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|ActiveMQMessageProducer
import|;
end_import

begin_comment
comment|/**  * A pooled {@link MessageProducer}  */
end_comment

begin_class
specifier|public
class|class
name|PooledProducer
implements|implements
name|MessageProducer
block|{
specifier|private
specifier|final
name|ActiveMQMessageProducer
name|messageProducer
decl_stmt|;
specifier|private
specifier|final
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
name|PooledProducer
parameter_list|(
name|ActiveMQMessageProducer
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
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|JMSException
block|{     }
annotation|@
name|Override
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
name|send
argument_list|(
name|destination
argument_list|,
name|message
argument_list|,
name|getDeliveryMode
argument_list|()
argument_list|,
name|getPriority
argument_list|()
argument_list|,
name|getTimeToLive
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
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
name|send
argument_list|(
name|destination
argument_list|,
name|message
argument_list|,
name|getDeliveryMode
argument_list|()
argument_list|,
name|getPriority
argument_list|()
argument_list|,
name|getTimeToLive
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
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
annotation|@
name|Override
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
name|ActiveMQMessageProducer
name|messageProducer
init|=
name|getMessageProducer
argument_list|()
decl_stmt|;
comment|// just in case let only one thread send at once
synchronized|synchronized
init|(
name|messageProducer
init|)
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
block|}
annotation|@
name|Override
specifier|public
name|Destination
name|getDestination
parameter_list|()
block|{
return|return
name|destination
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|getDeliveryMode
parameter_list|()
block|{
return|return
name|deliveryMode
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setDeliveryMode
parameter_list|(
name|int
name|deliveryMode
parameter_list|)
block|{
name|this
operator|.
name|deliveryMode
operator|=
name|deliveryMode
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|getDisableMessageID
parameter_list|()
block|{
return|return
name|disableMessageID
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setDisableMessageID
parameter_list|(
name|boolean
name|disableMessageID
parameter_list|)
block|{
name|this
operator|.
name|disableMessageID
operator|=
name|disableMessageID
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|getDisableMessageTimestamp
parameter_list|()
block|{
return|return
name|disableMessageTimestamp
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setDisableMessageTimestamp
parameter_list|(
name|boolean
name|disableMessageTimestamp
parameter_list|)
block|{
name|this
operator|.
name|disableMessageTimestamp
operator|=
name|disableMessageTimestamp
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|getPriority
parameter_list|()
block|{
return|return
name|priority
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setPriority
parameter_list|(
name|int
name|priority
parameter_list|)
block|{
name|this
operator|.
name|priority
operator|=
name|priority
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getTimeToLive
parameter_list|()
block|{
return|return
name|timeToLive
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setTimeToLive
parameter_list|(
name|long
name|timeToLive
parameter_list|)
block|{
name|this
operator|.
name|timeToLive
operator|=
name|timeToLive
expr_stmt|;
block|}
comment|// Implementation methods
comment|// -------------------------------------------------------------------------
specifier|protected
name|ActiveMQMessageProducer
name|getMessageProducer
parameter_list|()
block|{
return|return
name|messageProducer
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"PooledProducer { "
operator|+
name|messageProducer
operator|+
literal|" }"
return|;
block|}
block|}
end_class

end_unit

