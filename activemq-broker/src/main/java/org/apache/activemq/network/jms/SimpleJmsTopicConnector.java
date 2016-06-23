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
name|Destination
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|ExceptionListener
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
name|TopicConnectionFactory
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

begin_import
import|import
name|javax
operator|.
name|naming
operator|.
name|NamingException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_comment
comment|/**  * A Bridge to other JMS Topic providers  */
end_comment

begin_class
specifier|public
class|class
name|SimpleJmsTopicConnector
extends|extends
name|JmsConnector
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|SimpleJmsTopicConnector
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|String
name|outboundTopicConnectionFactoryName
decl_stmt|;
specifier|private
name|String
name|localConnectionFactoryName
decl_stmt|;
specifier|private
name|TopicConnectionFactory
name|outboundTopicConnectionFactory
decl_stmt|;
specifier|private
name|TopicConnectionFactory
name|localTopicConnectionFactory
decl_stmt|;
specifier|private
name|InboundTopicBridge
index|[]
name|inboundTopicBridges
decl_stmt|;
specifier|private
name|OutboundTopicBridge
index|[]
name|outboundTopicBridges
decl_stmt|;
comment|/**      * @return Returns the inboundTopicBridges.      */
specifier|public
name|InboundTopicBridge
index|[]
name|getInboundTopicBridges
parameter_list|()
block|{
return|return
name|inboundTopicBridges
return|;
block|}
comment|/**      * @param inboundTopicBridges The inboundTopicBridges to set.      */
specifier|public
name|void
name|setInboundTopicBridges
parameter_list|(
name|InboundTopicBridge
index|[]
name|inboundTopicBridges
parameter_list|)
block|{
name|this
operator|.
name|inboundTopicBridges
operator|=
name|inboundTopicBridges
expr_stmt|;
block|}
comment|/**      * @return Returns the outboundTopicBridges.      */
specifier|public
name|OutboundTopicBridge
index|[]
name|getOutboundTopicBridges
parameter_list|()
block|{
return|return
name|outboundTopicBridges
return|;
block|}
comment|/**      * @param outboundTopicBridges The outboundTopicBridges to set.      */
specifier|public
name|void
name|setOutboundTopicBridges
parameter_list|(
name|OutboundTopicBridge
index|[]
name|outboundTopicBridges
parameter_list|)
block|{
name|this
operator|.
name|outboundTopicBridges
operator|=
name|outboundTopicBridges
expr_stmt|;
block|}
comment|/**      * @return Returns the localTopicConnectionFactory.      */
specifier|public
name|TopicConnectionFactory
name|getLocalTopicConnectionFactory
parameter_list|()
block|{
return|return
name|localTopicConnectionFactory
return|;
block|}
comment|/**      * @param localTopicConnectionFactory The localTopicConnectionFactory to set.      */
specifier|public
name|void
name|setLocalTopicConnectionFactory
parameter_list|(
name|TopicConnectionFactory
name|localTopicConnectionFactory
parameter_list|)
block|{
name|this
operator|.
name|localTopicConnectionFactory
operator|=
name|localTopicConnectionFactory
expr_stmt|;
block|}
comment|/**      * @return Returns the outboundTopicConnectionFactory.      */
specifier|public
name|TopicConnectionFactory
name|getOutboundTopicConnectionFactory
parameter_list|()
block|{
return|return
name|outboundTopicConnectionFactory
return|;
block|}
comment|/**      * @return Returns the outboundTopicConnectionFactoryName.      */
specifier|public
name|String
name|getOutboundTopicConnectionFactoryName
parameter_list|()
block|{
return|return
name|outboundTopicConnectionFactoryName
return|;
block|}
comment|/**      * @param foreignTopicConnectionFactoryName The foreignTopicConnectionFactoryName to set.      */
specifier|public
name|void
name|setOutboundTopicConnectionFactoryName
parameter_list|(
name|String
name|foreignTopicConnectionFactoryName
parameter_list|)
block|{
name|this
operator|.
name|outboundTopicConnectionFactoryName
operator|=
name|foreignTopicConnectionFactoryName
expr_stmt|;
block|}
comment|/**      * @return Returns the localConnectionFactoryName.      */
specifier|public
name|String
name|getLocalConnectionFactoryName
parameter_list|()
block|{
return|return
name|localConnectionFactoryName
return|;
block|}
comment|/**      * @param localConnectionFactoryName The localConnectionFactoryName to set.      */
specifier|public
name|void
name|setLocalConnectionFactoryName
parameter_list|(
name|String
name|localConnectionFactoryName
parameter_list|)
block|{
name|this
operator|.
name|localConnectionFactoryName
operator|=
name|localConnectionFactoryName
expr_stmt|;
block|}
comment|/**      * @return Returns the localTopicConnection.      */
specifier|public
name|TopicConnection
name|getLocalTopicConnection
parameter_list|()
block|{
return|return
operator|(
name|TopicConnection
operator|)
name|localConnection
operator|.
name|get
argument_list|()
return|;
block|}
comment|/**      * @param localTopicConnection The localTopicConnection to set.      */
specifier|public
name|void
name|setLocalTopicConnection
parameter_list|(
name|TopicConnection
name|localTopicConnection
parameter_list|)
block|{
name|this
operator|.
name|localConnection
operator|.
name|set
argument_list|(
name|localTopicConnection
argument_list|)
expr_stmt|;
block|}
comment|/**      * @return Returns the outboundTopicConnection.      */
specifier|public
name|TopicConnection
name|getOutboundTopicConnection
parameter_list|()
block|{
return|return
operator|(
name|TopicConnection
operator|)
name|foreignConnection
operator|.
name|get
argument_list|()
return|;
block|}
comment|/**      * @param foreignTopicConnection The foreignTopicConnection to set.      */
specifier|public
name|void
name|setOutboundTopicConnection
parameter_list|(
name|TopicConnection
name|foreignTopicConnection
parameter_list|)
block|{
name|this
operator|.
name|foreignConnection
operator|.
name|set
argument_list|(
name|foreignTopicConnection
argument_list|)
expr_stmt|;
block|}
comment|/**      * @param foreignTopicConnectionFactory The foreignTopicConnectionFactory to set.      */
specifier|public
name|void
name|setOutboundTopicConnectionFactory
parameter_list|(
name|TopicConnectionFactory
name|foreignTopicConnectionFactory
parameter_list|)
block|{
name|this
operator|.
name|outboundTopicConnectionFactory
operator|=
name|foreignTopicConnectionFactory
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|initializeForeignConnection
parameter_list|()
throws|throws
name|NamingException
throws|,
name|JMSException
block|{
name|TopicConnection
name|newConnection
init|=
literal|null
decl_stmt|;
try|try
block|{
if|if
condition|(
name|foreignConnection
operator|.
name|get
argument_list|()
operator|==
literal|null
condition|)
block|{
comment|// get the connection factories
if|if
condition|(
name|outboundTopicConnectionFactory
operator|==
literal|null
condition|)
block|{
comment|// look it up from JNDI
if|if
condition|(
name|outboundTopicConnectionFactoryName
operator|!=
literal|null
condition|)
block|{
name|outboundTopicConnectionFactory
operator|=
name|jndiOutboundTemplate
operator|.
name|lookup
argument_list|(
name|outboundTopicConnectionFactoryName
argument_list|,
name|TopicConnectionFactory
operator|.
name|class
argument_list|)
expr_stmt|;
if|if
condition|(
name|outboundUsername
operator|!=
literal|null
condition|)
block|{
name|newConnection
operator|=
name|outboundTopicConnectionFactory
operator|.
name|createTopicConnection
argument_list|(
name|outboundUsername
argument_list|,
name|outboundPassword
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|newConnection
operator|=
name|outboundTopicConnectionFactory
operator|.
name|createTopicConnection
argument_list|()
expr_stmt|;
block|}
block|}
else|else
block|{
throw|throw
operator|new
name|JMSException
argument_list|(
literal|"Cannot create foreignConnection - no information"
argument_list|)
throw|;
block|}
block|}
else|else
block|{
if|if
condition|(
name|outboundUsername
operator|!=
literal|null
condition|)
block|{
name|newConnection
operator|=
name|outboundTopicConnectionFactory
operator|.
name|createTopicConnection
argument_list|(
name|outboundUsername
argument_list|,
name|outboundPassword
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|newConnection
operator|=
name|outboundTopicConnectionFactory
operator|.
name|createTopicConnection
argument_list|()
expr_stmt|;
block|}
block|}
block|}
else|else
block|{
comment|// Clear if for now in case something goes wrong during the init.
name|newConnection
operator|=
operator|(
name|TopicConnection
operator|)
name|foreignConnection
operator|.
name|getAndSet
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
comment|// Register for any async error notifications now so we can reset in the
comment|// case where there's not a lot of activity and a connection drops.
name|newConnection
operator|.
name|setExceptionListener
argument_list|(
operator|new
name|ExceptionListener
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onException
parameter_list|(
name|JMSException
name|exception
parameter_list|)
block|{
name|handleConnectionFailure
argument_list|(
name|foreignConnection
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
if|if
condition|(
name|outboundClientId
operator|!=
literal|null
operator|&&
name|outboundClientId
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|newConnection
operator|.
name|setClientID
argument_list|(
name|getOutboundClientId
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|newConnection
operator|.
name|start
argument_list|()
expr_stmt|;
name|outboundMessageConvertor
operator|.
name|setConnection
argument_list|(
name|newConnection
argument_list|)
expr_stmt|;
comment|// Configure the bridges with the new Outbound connection.
name|initializeInboundDestinationBridgesOutboundSide
argument_list|(
name|newConnection
argument_list|)
expr_stmt|;
name|initializeOutboundDestinationBridgesOutboundSide
argument_list|(
name|newConnection
argument_list|)
expr_stmt|;
comment|// At this point all looks good, so this our current connection now.
name|foreignConnection
operator|.
name|set
argument_list|(
name|newConnection
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
if|if
condition|(
name|newConnection
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|newConnection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ignore
parameter_list|)
block|{}
block|}
throw|throw
name|ex
throw|;
block|}
block|}
annotation|@
name|Override
specifier|protected
name|void
name|initializeLocalConnection
parameter_list|()
throws|throws
name|NamingException
throws|,
name|JMSException
block|{
name|TopicConnection
name|newConnection
init|=
literal|null
decl_stmt|;
try|try
block|{
if|if
condition|(
name|localConnection
operator|.
name|get
argument_list|()
operator|==
literal|null
condition|)
block|{
comment|// get the connection factories
if|if
condition|(
name|localTopicConnectionFactory
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|embeddedConnectionFactory
operator|==
literal|null
condition|)
block|{
comment|// look it up from JNDI
if|if
condition|(
name|localConnectionFactoryName
operator|!=
literal|null
condition|)
block|{
name|localTopicConnectionFactory
operator|=
name|jndiLocalTemplate
operator|.
name|lookup
argument_list|(
name|localConnectionFactoryName
argument_list|,
name|TopicConnectionFactory
operator|.
name|class
argument_list|)
expr_stmt|;
if|if
condition|(
name|localUsername
operator|!=
literal|null
condition|)
block|{
name|newConnection
operator|=
name|localTopicConnectionFactory
operator|.
name|createTopicConnection
argument_list|(
name|localUsername
argument_list|,
name|localPassword
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|newConnection
operator|=
name|localTopicConnectionFactory
operator|.
name|createTopicConnection
argument_list|()
expr_stmt|;
block|}
block|}
else|else
block|{
throw|throw
operator|new
name|JMSException
argument_list|(
literal|"Cannot create localConnection - no information"
argument_list|)
throw|;
block|}
block|}
else|else
block|{
name|newConnection
operator|=
name|embeddedConnectionFactory
operator|.
name|createTopicConnection
argument_list|()
expr_stmt|;
block|}
block|}
else|else
block|{
if|if
condition|(
name|localUsername
operator|!=
literal|null
condition|)
block|{
name|newConnection
operator|=
name|localTopicConnectionFactory
operator|.
name|createTopicConnection
argument_list|(
name|localUsername
argument_list|,
name|localPassword
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|newConnection
operator|=
name|localTopicConnectionFactory
operator|.
name|createTopicConnection
argument_list|()
expr_stmt|;
block|}
block|}
block|}
else|else
block|{
comment|// Clear if for now in case something goes wrong during the init.
name|newConnection
operator|=
operator|(
name|TopicConnection
operator|)
name|localConnection
operator|.
name|getAndSet
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
comment|// Register for any async error notifications now so we can reset in the
comment|// case where there's not a lot of activity and a connection drops.
name|newConnection
operator|.
name|setExceptionListener
argument_list|(
operator|new
name|ExceptionListener
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onException
parameter_list|(
name|JMSException
name|exception
parameter_list|)
block|{
name|handleConnectionFailure
argument_list|(
name|localConnection
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
if|if
condition|(
name|localClientId
operator|!=
literal|null
operator|&&
name|localClientId
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|newConnection
operator|.
name|setClientID
argument_list|(
name|getLocalClientId
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|newConnection
operator|.
name|start
argument_list|()
expr_stmt|;
name|inboundMessageConvertor
operator|.
name|setConnection
argument_list|(
name|newConnection
argument_list|)
expr_stmt|;
comment|// Configure the bridges with the new Local connection.
name|initializeInboundDestinationBridgesLocalSide
argument_list|(
name|newConnection
argument_list|)
expr_stmt|;
name|initializeOutboundDestinationBridgesLocalSide
argument_list|(
name|newConnection
argument_list|)
expr_stmt|;
comment|// At this point all looks good, so this our current connection now.
name|localConnection
operator|.
name|set
argument_list|(
name|newConnection
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
if|if
condition|(
name|newConnection
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|newConnection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ignore
parameter_list|)
block|{}
block|}
throw|throw
name|ex
throw|;
block|}
block|}
specifier|protected
name|void
name|initializeInboundDestinationBridgesOutboundSide
parameter_list|(
name|TopicConnection
name|connection
parameter_list|)
throws|throws
name|JMSException
block|{
if|if
condition|(
name|inboundTopicBridges
operator|!=
literal|null
condition|)
block|{
name|TopicSession
name|outboundSession
init|=
name|connection
operator|.
name|createTopicSession
argument_list|(
literal|false
argument_list|,
name|Session
operator|.
name|AUTO_ACKNOWLEDGE
argument_list|)
decl_stmt|;
for|for
control|(
name|InboundTopicBridge
name|bridge
range|:
name|inboundTopicBridges
control|)
block|{
name|String
name|TopicName
init|=
name|bridge
operator|.
name|getInboundTopicName
argument_list|()
decl_stmt|;
name|Topic
name|foreignTopic
init|=
name|createForeignTopic
argument_list|(
name|outboundSession
argument_list|,
name|TopicName
argument_list|)
decl_stmt|;
name|bridge
operator|.
name|setConsumer
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|bridge
operator|.
name|setConsumerTopic
argument_list|(
name|foreignTopic
argument_list|)
expr_stmt|;
name|bridge
operator|.
name|setConsumerConnection
argument_list|(
name|connection
argument_list|)
expr_stmt|;
name|bridge
operator|.
name|setJmsConnector
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|addInboundBridge
argument_list|(
name|bridge
argument_list|)
expr_stmt|;
block|}
name|outboundSession
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
specifier|protected
name|void
name|initializeInboundDestinationBridgesLocalSide
parameter_list|(
name|TopicConnection
name|connection
parameter_list|)
throws|throws
name|JMSException
block|{
if|if
condition|(
name|inboundTopicBridges
operator|!=
literal|null
condition|)
block|{
name|TopicSession
name|localSession
init|=
name|connection
operator|.
name|createTopicSession
argument_list|(
literal|false
argument_list|,
name|Session
operator|.
name|AUTO_ACKNOWLEDGE
argument_list|)
decl_stmt|;
for|for
control|(
name|InboundTopicBridge
name|bridge
range|:
name|inboundTopicBridges
control|)
block|{
name|String
name|localTopicName
init|=
name|bridge
operator|.
name|getLocalTopicName
argument_list|()
decl_stmt|;
name|Topic
name|activemqTopic
init|=
name|createActiveMQTopic
argument_list|(
name|localSession
argument_list|,
name|localTopicName
argument_list|)
decl_stmt|;
name|bridge
operator|.
name|setProducerTopic
argument_list|(
name|activemqTopic
argument_list|)
expr_stmt|;
name|bridge
operator|.
name|setProducerConnection
argument_list|(
name|connection
argument_list|)
expr_stmt|;
if|if
condition|(
name|bridge
operator|.
name|getJmsMessageConvertor
argument_list|()
operator|==
literal|null
condition|)
block|{
name|bridge
operator|.
name|setJmsMessageConvertor
argument_list|(
name|getInboundMessageConvertor
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|bridge
operator|.
name|setJmsConnector
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|addInboundBridge
argument_list|(
name|bridge
argument_list|)
expr_stmt|;
block|}
name|localSession
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
specifier|protected
name|void
name|initializeOutboundDestinationBridgesOutboundSide
parameter_list|(
name|TopicConnection
name|connection
parameter_list|)
throws|throws
name|JMSException
block|{
if|if
condition|(
name|outboundTopicBridges
operator|!=
literal|null
condition|)
block|{
name|TopicSession
name|outboundSession
init|=
name|connection
operator|.
name|createTopicSession
argument_list|(
literal|false
argument_list|,
name|Session
operator|.
name|AUTO_ACKNOWLEDGE
argument_list|)
decl_stmt|;
for|for
control|(
name|OutboundTopicBridge
name|bridge
range|:
name|outboundTopicBridges
control|)
block|{
name|String
name|topicName
init|=
name|bridge
operator|.
name|getOutboundTopicName
argument_list|()
decl_stmt|;
name|Topic
name|foreignTopic
init|=
name|createForeignTopic
argument_list|(
name|outboundSession
argument_list|,
name|topicName
argument_list|)
decl_stmt|;
name|bridge
operator|.
name|setProducerTopic
argument_list|(
name|foreignTopic
argument_list|)
expr_stmt|;
name|bridge
operator|.
name|setProducerConnection
argument_list|(
name|connection
argument_list|)
expr_stmt|;
if|if
condition|(
name|bridge
operator|.
name|getJmsMessageConvertor
argument_list|()
operator|==
literal|null
condition|)
block|{
name|bridge
operator|.
name|setJmsMessageConvertor
argument_list|(
name|getOutboundMessageConvertor
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|bridge
operator|.
name|setJmsConnector
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|addOutboundBridge
argument_list|(
name|bridge
argument_list|)
expr_stmt|;
block|}
name|outboundSession
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
specifier|protected
name|void
name|initializeOutboundDestinationBridgesLocalSide
parameter_list|(
name|TopicConnection
name|connection
parameter_list|)
throws|throws
name|JMSException
block|{
if|if
condition|(
name|outboundTopicBridges
operator|!=
literal|null
condition|)
block|{
name|TopicSession
name|localSession
init|=
name|connection
operator|.
name|createTopicSession
argument_list|(
literal|false
argument_list|,
name|Session
operator|.
name|AUTO_ACKNOWLEDGE
argument_list|)
decl_stmt|;
for|for
control|(
name|OutboundTopicBridge
name|bridge
range|:
name|outboundTopicBridges
control|)
block|{
name|String
name|localTopicName
init|=
name|bridge
operator|.
name|getLocalTopicName
argument_list|()
decl_stmt|;
name|Topic
name|activemqTopic
init|=
name|createActiveMQTopic
argument_list|(
name|localSession
argument_list|,
name|localTopicName
argument_list|)
decl_stmt|;
name|bridge
operator|.
name|setConsumer
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|bridge
operator|.
name|setConsumerTopic
argument_list|(
name|activemqTopic
argument_list|)
expr_stmt|;
name|bridge
operator|.
name|setConsumerConnection
argument_list|(
name|connection
argument_list|)
expr_stmt|;
name|bridge
operator|.
name|setJmsConnector
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|addOutboundBridge
argument_list|(
name|bridge
argument_list|)
expr_stmt|;
block|}
name|localSession
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|protected
name|Destination
name|createReplyToBridge
parameter_list|(
name|Destination
name|destination
parameter_list|,
name|Connection
name|replyToProducerConnection
parameter_list|,
name|Connection
name|replyToConsumerConnection
parameter_list|)
block|{
name|Topic
name|replyToProducerTopic
init|=
operator|(
name|Topic
operator|)
name|destination
decl_stmt|;
name|boolean
name|isInbound
init|=
name|replyToProducerConnection
operator|.
name|equals
argument_list|(
name|localConnection
operator|.
name|get
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|isInbound
condition|)
block|{
name|InboundTopicBridge
name|bridge
init|=
operator|(
name|InboundTopicBridge
operator|)
name|replyToBridges
operator|.
name|get
argument_list|(
name|replyToProducerTopic
argument_list|)
decl_stmt|;
if|if
condition|(
name|bridge
operator|==
literal|null
condition|)
block|{
name|bridge
operator|=
operator|new
name|InboundTopicBridge
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|Destination
name|processReplyToDestination
parameter_list|(
name|Destination
name|destination
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
block|}
expr_stmt|;
try|try
block|{
name|TopicSession
name|replyToConsumerSession
init|=
operator|(
operator|(
name|TopicConnection
operator|)
name|replyToConsumerConnection
operator|)
operator|.
name|createTopicSession
argument_list|(
literal|false
argument_list|,
name|Session
operator|.
name|AUTO_ACKNOWLEDGE
argument_list|)
decl_stmt|;
name|Topic
name|replyToConsumerTopic
init|=
name|replyToConsumerSession
operator|.
name|createTemporaryTopic
argument_list|()
decl_stmt|;
name|replyToConsumerSession
operator|.
name|close
argument_list|()
expr_stmt|;
name|bridge
operator|.
name|setConsumerTopic
argument_list|(
name|replyToConsumerTopic
argument_list|)
expr_stmt|;
name|bridge
operator|.
name|setProducerTopic
argument_list|(
name|replyToProducerTopic
argument_list|)
expr_stmt|;
name|bridge
operator|.
name|setProducerConnection
argument_list|(
operator|(
name|TopicConnection
operator|)
name|replyToProducerConnection
argument_list|)
expr_stmt|;
name|bridge
operator|.
name|setConsumerConnection
argument_list|(
operator|(
name|TopicConnection
operator|)
name|replyToConsumerConnection
argument_list|)
expr_stmt|;
name|bridge
operator|.
name|setDoHandleReplyTo
argument_list|(
literal|false
argument_list|)
expr_stmt|;
if|if
condition|(
name|bridge
operator|.
name|getJmsMessageConvertor
argument_list|()
operator|==
literal|null
condition|)
block|{
name|bridge
operator|.
name|setJmsMessageConvertor
argument_list|(
name|getInboundMessageConvertor
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|bridge
operator|.
name|setJmsConnector
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|bridge
operator|.
name|start
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Created replyTo bridge for {}"
argument_list|,
name|replyToProducerTopic
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Failed to create replyTo bridge for topic: {}"
argument_list|,
name|replyToProducerTopic
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
name|replyToBridges
operator|.
name|put
argument_list|(
name|replyToProducerTopic
argument_list|,
name|bridge
argument_list|)
expr_stmt|;
block|}
return|return
name|bridge
operator|.
name|getConsumerTopic
argument_list|()
return|;
block|}
else|else
block|{
name|OutboundTopicBridge
name|bridge
init|=
operator|(
name|OutboundTopicBridge
operator|)
name|replyToBridges
operator|.
name|get
argument_list|(
name|replyToProducerTopic
argument_list|)
decl_stmt|;
if|if
condition|(
name|bridge
operator|==
literal|null
condition|)
block|{
name|bridge
operator|=
operator|new
name|OutboundTopicBridge
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|Destination
name|processReplyToDestination
parameter_list|(
name|Destination
name|destination
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
block|}
expr_stmt|;
try|try
block|{
name|TopicSession
name|replyToConsumerSession
init|=
operator|(
operator|(
name|TopicConnection
operator|)
name|replyToConsumerConnection
operator|)
operator|.
name|createTopicSession
argument_list|(
literal|false
argument_list|,
name|Session
operator|.
name|AUTO_ACKNOWLEDGE
argument_list|)
decl_stmt|;
name|Topic
name|replyToConsumerTopic
init|=
name|replyToConsumerSession
operator|.
name|createTemporaryTopic
argument_list|()
decl_stmt|;
name|replyToConsumerSession
operator|.
name|close
argument_list|()
expr_stmt|;
name|bridge
operator|.
name|setConsumerTopic
argument_list|(
name|replyToConsumerTopic
argument_list|)
expr_stmt|;
name|bridge
operator|.
name|setProducerTopic
argument_list|(
name|replyToProducerTopic
argument_list|)
expr_stmt|;
name|bridge
operator|.
name|setProducerConnection
argument_list|(
operator|(
name|TopicConnection
operator|)
name|replyToProducerConnection
argument_list|)
expr_stmt|;
name|bridge
operator|.
name|setConsumerConnection
argument_list|(
operator|(
name|TopicConnection
operator|)
name|replyToConsumerConnection
argument_list|)
expr_stmt|;
name|bridge
operator|.
name|setDoHandleReplyTo
argument_list|(
literal|false
argument_list|)
expr_stmt|;
if|if
condition|(
name|bridge
operator|.
name|getJmsMessageConvertor
argument_list|()
operator|==
literal|null
condition|)
block|{
name|bridge
operator|.
name|setJmsMessageConvertor
argument_list|(
name|getOutboundMessageConvertor
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|bridge
operator|.
name|setJmsConnector
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|bridge
operator|.
name|start
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Created replyTo bridge for {}"
argument_list|,
name|replyToProducerTopic
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Failed to create replyTo bridge for topic: {}"
argument_list|,
name|replyToProducerTopic
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
name|replyToBridges
operator|.
name|put
argument_list|(
name|replyToProducerTopic
argument_list|,
name|bridge
argument_list|)
expr_stmt|;
block|}
return|return
name|bridge
operator|.
name|getConsumerTopic
argument_list|()
return|;
block|}
block|}
specifier|protected
name|Topic
name|createActiveMQTopic
parameter_list|(
name|TopicSession
name|session
parameter_list|,
name|String
name|topicName
parameter_list|)
throws|throws
name|JMSException
block|{
return|return
name|session
operator|.
name|createTopic
argument_list|(
name|topicName
argument_list|)
return|;
block|}
specifier|protected
name|Topic
name|createForeignTopic
parameter_list|(
name|TopicSession
name|session
parameter_list|,
name|String
name|topicName
parameter_list|)
throws|throws
name|JMSException
block|{
name|Topic
name|result
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|preferJndiDestinationLookup
condition|)
block|{
try|try
block|{
comment|// look-up the Queue
name|result
operator|=
name|jndiOutboundTemplate
operator|.
name|lookup
argument_list|(
name|topicName
argument_list|,
name|Topic
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NamingException
name|e
parameter_list|)
block|{
try|try
block|{
name|result
operator|=
name|session
operator|.
name|createTopic
argument_list|(
name|topicName
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|JMSException
name|e1
parameter_list|)
block|{
name|String
name|errStr
init|=
literal|"Failed to look-up or create Topic for name: "
operator|+
name|topicName
decl_stmt|;
name|LOG
operator|.
name|error
argument_list|(
name|errStr
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|JMSException
name|jmsEx
init|=
operator|new
name|JMSException
argument_list|(
name|errStr
argument_list|)
decl_stmt|;
name|jmsEx
operator|.
name|setLinkedException
argument_list|(
name|e1
argument_list|)
expr_stmt|;
throw|throw
name|jmsEx
throw|;
block|}
block|}
block|}
else|else
block|{
try|try
block|{
name|result
operator|=
name|session
operator|.
name|createTopic
argument_list|(
name|topicName
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|JMSException
name|e
parameter_list|)
block|{
comment|// look-up the Topic
try|try
block|{
name|result
operator|=
name|jndiOutboundTemplate
operator|.
name|lookup
argument_list|(
name|topicName
argument_list|,
name|Topic
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NamingException
name|e1
parameter_list|)
block|{
name|String
name|errStr
init|=
literal|"Failed to look-up Topic for name: "
operator|+
name|topicName
decl_stmt|;
name|LOG
operator|.
name|error
argument_list|(
name|errStr
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|JMSException
name|jmsEx
init|=
operator|new
name|JMSException
argument_list|(
name|errStr
argument_list|)
decl_stmt|;
name|jmsEx
operator|.
name|setLinkedException
argument_list|(
name|e1
argument_list|)
expr_stmt|;
throw|throw
name|jmsEx
throw|;
block|}
block|}
block|}
return|return
name|result
return|;
block|}
block|}
end_class

end_unit

