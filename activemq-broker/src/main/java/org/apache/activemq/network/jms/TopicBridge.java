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
name|network
operator|.
name|jms
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|Connection
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
name|MessageConsumer
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
name|Session
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
name|TopicConnection
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

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|TopicSession
import|;
end_import

begin_comment
comment|/**  * A Destination bridge is used to bridge between to different JMS systems  *  *  */
end_comment

begin_class
class|class
name|TopicBridge
extends|extends
name|DestinationBridge
block|{
specifier|protected
name|Topic
name|consumerTopic
decl_stmt|;
specifier|protected
name|Topic
name|producerTopic
decl_stmt|;
specifier|protected
name|TopicSession
name|consumerSession
decl_stmt|;
specifier|protected
name|TopicSession
name|producerSession
decl_stmt|;
specifier|protected
name|String
name|consumerName
decl_stmt|;
specifier|protected
name|String
name|selector
decl_stmt|;
specifier|protected
name|TopicPublisher
name|producer
decl_stmt|;
specifier|protected
name|TopicConnection
name|consumerConnection
decl_stmt|;
specifier|protected
name|TopicConnection
name|producerConnection
decl_stmt|;
specifier|public
name|void
name|stop
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|stop
argument_list|()
expr_stmt|;
if|if
condition|(
name|consumerSession
operator|!=
literal|null
condition|)
block|{
name|consumerSession
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|producerSession
operator|!=
literal|null
condition|)
block|{
name|producerSession
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
specifier|protected
name|MessageConsumer
name|createConsumer
parameter_list|()
throws|throws
name|JMSException
block|{
comment|// set up the consumer
if|if
condition|(
name|consumerConnection
operator|==
literal|null
condition|)
return|return
literal|null
return|;
name|consumerSession
operator|=
name|consumerConnection
operator|.
name|createTopicSession
argument_list|(
literal|false
argument_list|,
name|Session
operator|.
name|CLIENT_ACKNOWLEDGE
argument_list|)
expr_stmt|;
name|MessageConsumer
name|consumer
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|consumerName
operator|!=
literal|null
operator|&&
name|consumerName
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
if|if
condition|(
name|selector
operator|!=
literal|null
operator|&&
name|selector
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|consumer
operator|=
name|consumerSession
operator|.
name|createDurableSubscriber
argument_list|(
name|consumerTopic
argument_list|,
name|consumerName
argument_list|,
name|selector
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|consumer
operator|=
name|consumerSession
operator|.
name|createDurableSubscriber
argument_list|(
name|consumerTopic
argument_list|,
name|consumerName
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
if|if
condition|(
name|selector
operator|!=
literal|null
operator|&&
name|selector
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|consumer
operator|=
name|consumerSession
operator|.
name|createSubscriber
argument_list|(
name|consumerTopic
argument_list|,
name|selector
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|consumer
operator|=
name|consumerSession
operator|.
name|createSubscriber
argument_list|(
name|consumerTopic
argument_list|)
expr_stmt|;
block|}
block|}
name|consumer
operator|.
name|setMessageListener
argument_list|(
name|this
argument_list|)
expr_stmt|;
return|return
name|consumer
return|;
block|}
specifier|protected
specifier|synchronized
name|MessageProducer
name|createProducer
parameter_list|()
throws|throws
name|JMSException
block|{
if|if
condition|(
name|producerConnection
operator|==
literal|null
condition|)
return|return
literal|null
return|;
name|producerSession
operator|=
name|producerConnection
operator|.
name|createTopicSession
argument_list|(
literal|false
argument_list|,
name|Session
operator|.
name|AUTO_ACKNOWLEDGE
argument_list|)
expr_stmt|;
name|producer
operator|=
name|producerSession
operator|.
name|createPublisher
argument_list|(
literal|null
argument_list|)
expr_stmt|;
return|return
name|producer
return|;
block|}
specifier|protected
specifier|synchronized
name|void
name|sendMessage
parameter_list|(
name|Message
name|message
parameter_list|)
throws|throws
name|JMSException
block|{
if|if
condition|(
name|producer
operator|==
literal|null
operator|&&
name|createProducer
argument_list|()
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|JMSException
argument_list|(
literal|"Producer for remote queue not available."
argument_list|)
throw|;
block|}
try|try
block|{
name|producer
operator|.
name|publish
argument_list|(
name|producerTopic
argument_list|,
name|message
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|JMSException
name|e
parameter_list|)
block|{
name|producer
operator|=
literal|null
expr_stmt|;
throw|throw
name|e
throw|;
block|}
block|}
comment|/**      * @return Returns the consumerConnection.      */
specifier|public
name|TopicConnection
name|getConsumerConnection
parameter_list|()
block|{
return|return
name|consumerConnection
return|;
block|}
comment|/**      * @param consumerConnection The consumerConnection to set.      */
specifier|public
name|void
name|setConsumerConnection
parameter_list|(
name|TopicConnection
name|consumerConnection
parameter_list|)
block|{
name|this
operator|.
name|consumerConnection
operator|=
name|consumerConnection
expr_stmt|;
if|if
condition|(
name|started
operator|.
name|get
argument_list|()
condition|)
block|{
try|try
block|{
name|createConsumer
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|jmsConnector
operator|.
name|handleConnectionFailure
argument_list|(
name|getConnnectionForConsumer
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**      * @return Returns the subscriptionName.      */
specifier|public
name|String
name|getConsumerName
parameter_list|()
block|{
return|return
name|consumerName
return|;
block|}
comment|/**      * @param subscriptionName The subscriptionName to set.      */
specifier|public
name|void
name|setConsumerName
parameter_list|(
name|String
name|consumerName
parameter_list|)
block|{
name|this
operator|.
name|consumerName
operator|=
name|consumerName
expr_stmt|;
block|}
comment|/**      * @return Returns the consumerTopic.      */
specifier|public
name|Topic
name|getConsumerTopic
parameter_list|()
block|{
return|return
name|consumerTopic
return|;
block|}
comment|/**      * @param consumerTopic The consumerTopic to set.      */
specifier|public
name|void
name|setConsumerTopic
parameter_list|(
name|Topic
name|consumerTopic
parameter_list|)
block|{
name|this
operator|.
name|consumerTopic
operator|=
name|consumerTopic
expr_stmt|;
block|}
comment|/**      * @return Returns the producerConnection.      */
specifier|public
name|TopicConnection
name|getProducerConnection
parameter_list|()
block|{
return|return
name|producerConnection
return|;
block|}
comment|/**      * @param producerConnection The producerConnection to set.      */
specifier|public
name|void
name|setProducerConnection
parameter_list|(
name|TopicConnection
name|producerConnection
parameter_list|)
block|{
name|this
operator|.
name|producerConnection
operator|=
name|producerConnection
expr_stmt|;
block|}
comment|/**      * @return Returns the producerTopic.      */
specifier|public
name|Topic
name|getProducerTopic
parameter_list|()
block|{
return|return
name|producerTopic
return|;
block|}
comment|/**      * @param producerTopic The producerTopic to set.      */
specifier|public
name|void
name|setProducerTopic
parameter_list|(
name|Topic
name|producerTopic
parameter_list|)
block|{
name|this
operator|.
name|producerTopic
operator|=
name|producerTopic
expr_stmt|;
block|}
comment|/**      * @return Returns the selector.      */
specifier|public
name|String
name|getSelector
parameter_list|()
block|{
return|return
name|selector
return|;
block|}
comment|/**      * @param selector The selector to set.      */
specifier|public
name|void
name|setSelector
parameter_list|(
name|String
name|selector
parameter_list|)
block|{
name|this
operator|.
name|selector
operator|=
name|selector
expr_stmt|;
block|}
specifier|protected
name|Connection
name|getConnnectionForConsumer
parameter_list|()
block|{
return|return
name|getConsumerConnection
argument_list|()
return|;
block|}
specifier|protected
name|Connection
name|getConnectionForProducer
parameter_list|()
block|{
return|return
name|getProducerConnection
argument_list|()
return|;
block|}
block|}
end_class

end_unit
