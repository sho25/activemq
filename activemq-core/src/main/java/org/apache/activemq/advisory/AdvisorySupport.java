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
name|advisory
package|;
end_package

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
name|ActiveMQTopic
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

begin_class
specifier|public
class|class
name|AdvisorySupport
block|{
specifier|public
specifier|static
specifier|final
name|String
name|ADVISORY_TOPIC_PREFIX
init|=
literal|"ActiveMQ.Advisory."
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|ActiveMQTopic
name|CONNECTION_ADVISORY_TOPIC
init|=
operator|new
name|ActiveMQTopic
argument_list|(
name|ADVISORY_TOPIC_PREFIX
operator|+
literal|"Connection"
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|ActiveMQTopic
name|QUEUE_ADVISORY_TOPIC
init|=
operator|new
name|ActiveMQTopic
argument_list|(
name|ADVISORY_TOPIC_PREFIX
operator|+
literal|"Queue"
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|ActiveMQTopic
name|TOPIC_ADVISORY_TOPIC
init|=
operator|new
name|ActiveMQTopic
argument_list|(
name|ADVISORY_TOPIC_PREFIX
operator|+
literal|"Topic"
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|ActiveMQTopic
name|TEMP_QUEUE_ADVISORY_TOPIC
init|=
operator|new
name|ActiveMQTopic
argument_list|(
name|ADVISORY_TOPIC_PREFIX
operator|+
literal|"TempQueue"
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|ActiveMQTopic
name|TEMP_TOPIC_ADVISORY_TOPIC
init|=
operator|new
name|ActiveMQTopic
argument_list|(
name|ADVISORY_TOPIC_PREFIX
operator|+
literal|"TempTopic"
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|PRODUCER_ADVISORY_TOPIC_PREFIX
init|=
name|ADVISORY_TOPIC_PREFIX
operator|+
literal|"Producer."
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|QUEUE_PRODUCER_ADVISORY_TOPIC_PREFIX
init|=
name|PRODUCER_ADVISORY_TOPIC_PREFIX
operator|+
literal|"Queue."
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|TOPIC_PRODUCER_ADVISORY_TOPIC_PREFIX
init|=
name|PRODUCER_ADVISORY_TOPIC_PREFIX
operator|+
literal|"Topic."
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|CONSUMER_ADVISORY_TOPIC_PREFIX
init|=
name|ADVISORY_TOPIC_PREFIX
operator|+
literal|"Consumer."
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|QUEUE_CONSUMER_ADVISORY_TOPIC_PREFIX
init|=
name|CONSUMER_ADVISORY_TOPIC_PREFIX
operator|+
literal|"Queue."
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|TOPIC_CONSUMER_ADVISORY_TOPIC_PREFIX
init|=
name|CONSUMER_ADVISORY_TOPIC_PREFIX
operator|+
literal|"Topic."
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|EXPIRED_TOPIC_MESSAGES_TOPIC_PREFIX
init|=
name|ADVISORY_TOPIC_PREFIX
operator|+
literal|"Expired.Topic."
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|EXPIRED_QUEUE_MESSAGES_TOPIC_PREFIX
init|=
name|ADVISORY_TOPIC_PREFIX
operator|+
literal|"Expired.Queue."
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|NO_TOPIC_CONSUMERS_TOPIC_PREFIX
init|=
name|ADVISORY_TOPIC_PREFIX
operator|+
literal|"NoConsumer.Topic."
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|NO_QUEUE_CONSUMERS_TOPIC_PREFIX
init|=
name|ADVISORY_TOPIC_PREFIX
operator|+
literal|"NoConsumer.Queue."
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|AGENT_TOPIC
init|=
literal|"ActiveMQ.Agent"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|ADIVSORY_MESSAGE_TYPE
init|=
literal|"Advisory"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|ActiveMQTopic
name|TEMP_DESTINATION_COMPOSITE_ADVISORY_TOPIC
init|=
operator|new
name|ActiveMQTopic
argument_list|(
name|TEMP_QUEUE_ADVISORY_TOPIC
operator|+
literal|","
operator|+
name|TEMP_TOPIC_ADVISORY_TOPIC
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|ActiveMQTopic
name|AGENT_TOPIC_DESTINATION
init|=
operator|new
name|ActiveMQTopic
argument_list|(
name|AGENT_TOPIC
argument_list|)
decl_stmt|;
specifier|public
specifier|static
name|ActiveMQTopic
name|getConnectionAdvisoryTopic
parameter_list|()
block|{
return|return
name|CONNECTION_ADVISORY_TOPIC
return|;
block|}
specifier|public
specifier|static
name|ActiveMQTopic
name|getConsumerAdvisoryTopic
parameter_list|(
name|ActiveMQDestination
name|destination
parameter_list|)
block|{
if|if
condition|(
name|destination
operator|.
name|isQueue
argument_list|()
condition|)
return|return
operator|new
name|ActiveMQTopic
argument_list|(
name|QUEUE_CONSUMER_ADVISORY_TOPIC_PREFIX
operator|+
name|destination
operator|.
name|getPhysicalName
argument_list|()
argument_list|)
return|;
else|else
return|return
operator|new
name|ActiveMQTopic
argument_list|(
name|TOPIC_CONSUMER_ADVISORY_TOPIC_PREFIX
operator|+
name|destination
operator|.
name|getPhysicalName
argument_list|()
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|ActiveMQTopic
name|getProducerAdvisoryTopic
parameter_list|(
name|ActiveMQDestination
name|destination
parameter_list|)
block|{
if|if
condition|(
name|destination
operator|.
name|isQueue
argument_list|()
condition|)
return|return
operator|new
name|ActiveMQTopic
argument_list|(
name|QUEUE_PRODUCER_ADVISORY_TOPIC_PREFIX
operator|+
name|destination
operator|.
name|getPhysicalName
argument_list|()
argument_list|)
return|;
else|else
return|return
operator|new
name|ActiveMQTopic
argument_list|(
name|TOPIC_PRODUCER_ADVISORY_TOPIC_PREFIX
operator|+
name|destination
operator|.
name|getPhysicalName
argument_list|()
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|ActiveMQTopic
name|getExpiredMessageTopic
parameter_list|(
name|ActiveMQDestination
name|destination
parameter_list|)
block|{
if|if
condition|(
name|destination
operator|.
name|isQueue
argument_list|()
condition|)
block|{
return|return
name|getExpiredQueueMessageAdvisoryTopic
argument_list|(
name|destination
argument_list|)
return|;
block|}
return|return
name|getExpiredTopicMessageAdvisoryTopic
argument_list|(
name|destination
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|ActiveMQTopic
name|getExpiredTopicMessageAdvisoryTopic
parameter_list|(
name|ActiveMQDestination
name|destination
parameter_list|)
block|{
name|String
name|name
init|=
name|EXPIRED_TOPIC_MESSAGES_TOPIC_PREFIX
operator|+
name|destination
operator|.
name|getPhysicalName
argument_list|()
decl_stmt|;
return|return
operator|new
name|ActiveMQTopic
argument_list|(
name|name
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|ActiveMQTopic
name|getExpiredQueueMessageAdvisoryTopic
parameter_list|(
name|ActiveMQDestination
name|destination
parameter_list|)
block|{
name|String
name|name
init|=
name|EXPIRED_QUEUE_MESSAGES_TOPIC_PREFIX
operator|+
name|destination
operator|.
name|getPhysicalName
argument_list|()
decl_stmt|;
return|return
operator|new
name|ActiveMQTopic
argument_list|(
name|name
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|ActiveMQTopic
name|getNoTopicConsumersAdvisoryTopic
parameter_list|(
name|ActiveMQDestination
name|destination
parameter_list|)
block|{
name|String
name|name
init|=
name|NO_TOPIC_CONSUMERS_TOPIC_PREFIX
operator|+
name|destination
operator|.
name|getPhysicalName
argument_list|()
decl_stmt|;
return|return
operator|new
name|ActiveMQTopic
argument_list|(
name|name
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|ActiveMQTopic
name|getNoQueueConsumersAdvisoryTopic
parameter_list|(
name|ActiveMQDestination
name|destination
parameter_list|)
block|{
name|String
name|name
init|=
name|NO_QUEUE_CONSUMERS_TOPIC_PREFIX
operator|+
name|destination
operator|.
name|getPhysicalName
argument_list|()
decl_stmt|;
return|return
operator|new
name|ActiveMQTopic
argument_list|(
name|name
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|ActiveMQTopic
name|getDestinationAdvisoryTopic
parameter_list|(
name|ActiveMQDestination
name|destination
parameter_list|)
block|{
switch|switch
condition|(
name|destination
operator|.
name|getDestinationType
argument_list|()
condition|)
block|{
case|case
name|ActiveMQDestination
operator|.
name|QUEUE_TYPE
case|:
return|return
name|QUEUE_ADVISORY_TOPIC
return|;
case|case
name|ActiveMQDestination
operator|.
name|TOPIC_TYPE
case|:
return|return
name|TOPIC_ADVISORY_TOPIC
return|;
case|case
name|ActiveMQDestination
operator|.
name|TEMP_QUEUE_TYPE
case|:
return|return
name|TEMP_QUEUE_ADVISORY_TOPIC
return|;
case|case
name|ActiveMQDestination
operator|.
name|TEMP_TOPIC_TYPE
case|:
return|return
name|TEMP_TOPIC_ADVISORY_TOPIC
return|;
block|}
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Unknown destination type: "
operator|+
name|destination
operator|.
name|getDestinationType
argument_list|()
argument_list|)
throw|;
block|}
specifier|public
specifier|static
name|boolean
name|isDestinationAdvisoryTopic
parameter_list|(
name|ActiveMQDestination
name|destination
parameter_list|)
block|{
if|if
condition|(
name|destination
operator|.
name|isComposite
argument_list|()
condition|)
block|{
name|ActiveMQDestination
index|[]
name|compositeDestinations
init|=
name|destination
operator|.
name|getCompositeDestinations
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|compositeDestinations
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|isDestinationAdvisoryTopic
argument_list|(
name|compositeDestinations
index|[
name|i
index|]
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
else|else
block|{
return|return
name|destination
operator|.
name|equals
argument_list|(
name|TEMP_QUEUE_ADVISORY_TOPIC
argument_list|)
operator|||
name|destination
operator|.
name|equals
argument_list|(
name|TEMP_TOPIC_ADVISORY_TOPIC
argument_list|)
operator|||
name|destination
operator|.
name|equals
argument_list|(
name|QUEUE_ADVISORY_TOPIC
argument_list|)
operator|||
name|destination
operator|.
name|equals
argument_list|(
name|TOPIC_ADVISORY_TOPIC
argument_list|)
return|;
block|}
block|}
specifier|public
specifier|static
name|boolean
name|isAdvisoryTopic
parameter_list|(
name|ActiveMQDestination
name|destination
parameter_list|)
block|{
if|if
condition|(
name|destination
operator|.
name|isComposite
argument_list|()
condition|)
block|{
name|ActiveMQDestination
index|[]
name|compositeDestinations
init|=
name|destination
operator|.
name|getCompositeDestinations
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|compositeDestinations
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|isAdvisoryTopic
argument_list|(
name|compositeDestinations
index|[
name|i
index|]
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
else|else
block|{
return|return
name|destination
operator|.
name|isTopic
argument_list|()
operator|&&
name|destination
operator|.
name|getPhysicalName
argument_list|()
operator|.
name|startsWith
argument_list|(
name|ADVISORY_TOPIC_PREFIX
argument_list|)
return|;
block|}
block|}
specifier|public
specifier|static
name|boolean
name|isConnectionAdvisoryTopic
parameter_list|(
name|ActiveMQDestination
name|destination
parameter_list|)
block|{
if|if
condition|(
name|destination
operator|.
name|isComposite
argument_list|()
condition|)
block|{
name|ActiveMQDestination
index|[]
name|compositeDestinations
init|=
name|destination
operator|.
name|getCompositeDestinations
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|compositeDestinations
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|isConnectionAdvisoryTopic
argument_list|(
name|compositeDestinations
index|[
name|i
index|]
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
else|else
block|{
return|return
name|destination
operator|.
name|equals
argument_list|(
name|CONNECTION_ADVISORY_TOPIC
argument_list|)
return|;
block|}
block|}
specifier|public
specifier|static
name|boolean
name|isProducerAdvisoryTopic
parameter_list|(
name|ActiveMQDestination
name|destination
parameter_list|)
block|{
if|if
condition|(
name|destination
operator|.
name|isComposite
argument_list|()
condition|)
block|{
name|ActiveMQDestination
index|[]
name|compositeDestinations
init|=
name|destination
operator|.
name|getCompositeDestinations
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|compositeDestinations
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|isProducerAdvisoryTopic
argument_list|(
name|compositeDestinations
index|[
name|i
index|]
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
else|else
block|{
return|return
name|destination
operator|.
name|isTopic
argument_list|()
operator|&&
name|destination
operator|.
name|getPhysicalName
argument_list|()
operator|.
name|startsWith
argument_list|(
name|PRODUCER_ADVISORY_TOPIC_PREFIX
argument_list|)
return|;
block|}
block|}
specifier|public
specifier|static
name|boolean
name|isConsumerAdvisoryTopic
parameter_list|(
name|ActiveMQDestination
name|destination
parameter_list|)
block|{
if|if
condition|(
name|destination
operator|.
name|isComposite
argument_list|()
condition|)
block|{
name|ActiveMQDestination
index|[]
name|compositeDestinations
init|=
name|destination
operator|.
name|getCompositeDestinations
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|compositeDestinations
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|isConsumerAdvisoryTopic
argument_list|(
name|compositeDestinations
index|[
name|i
index|]
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
else|else
block|{
return|return
name|destination
operator|.
name|isTopic
argument_list|()
operator|&&
name|destination
operator|.
name|getPhysicalName
argument_list|()
operator|.
name|startsWith
argument_list|(
name|CONSUMER_ADVISORY_TOPIC_PREFIX
argument_list|)
return|;
block|}
block|}
comment|/**      * Returns the agent topic which is used to send commands to the broker      */
specifier|public
specifier|static
name|Destination
name|getAgentDestination
parameter_list|()
block|{
return|return
name|AGENT_TOPIC_DESTINATION
return|;
block|}
block|}
end_class

end_unit

