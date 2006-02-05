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
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|Log
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|LogFactory
import|;
end_import

begin_comment
comment|/**  * A Bridge to other JMS Topic providers  *   * @org.xbean.XBean  *   * @version $Revision: 1.1.1.1 $  */
end_comment

begin_class
specifier|public
class|class
name|JmsTopicConnector
extends|extends
name|JmsConnector
block|{
specifier|private
specifier|static
specifier|final
name|Log
name|log
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|JmsTopicConnector
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
name|TopicConnection
name|outboundTopicConnection
decl_stmt|;
specifier|private
name|TopicConnection
name|localTopicConnection
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
specifier|private
name|String
name|outboundUsername
decl_stmt|;
specifier|private
name|String
name|outboundPassword
decl_stmt|;
specifier|private
name|String
name|localUsername
decl_stmt|;
specifier|private
name|String
name|localPassword
decl_stmt|;
specifier|public
name|boolean
name|init
parameter_list|()
block|{
name|boolean
name|result
init|=
name|super
operator|.
name|init
argument_list|()
decl_stmt|;
if|if
condition|(
name|result
condition|)
block|{
try|try
block|{
name|initializeForeignTopicConnection
argument_list|()
expr_stmt|;
name|initializeLocalTopicConnection
argument_list|()
expr_stmt|;
name|initializeInboundTopicBridges
argument_list|()
expr_stmt|;
name|initializeOutboundTopicBridges
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Failed to initialize the JMSConnector"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|result
return|;
block|}
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
comment|/**      * @param inboundTopicBridges      *            The inboundTopicBridges to set.      */
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
comment|/**      * @param outboundTopicBridges      *            The outboundTopicBridges to set.      */
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
comment|/**      * @param localTopicConnectionFactory      *            The localTopicConnectionFactory to set.      */
specifier|public
name|void
name|setLocalTopicConnectionFactory
parameter_list|(
name|TopicConnectionFactory
name|localConnectionFactory
parameter_list|)
block|{
name|this
operator|.
name|localTopicConnectionFactory
operator|=
name|localConnectionFactory
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
comment|/**      * @param outboundTopicConnectionFactoryName      *            The outboundTopicConnectionFactoryName to set.      */
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
comment|/**      * @param localConnectionFactoryName      *            The localConnectionFactoryName to set.      */
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
name|localTopicConnection
return|;
block|}
comment|/**      * @param localTopicConnection      *            The localTopicConnection to set.      */
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
name|localTopicConnection
operator|=
name|localTopicConnection
expr_stmt|;
block|}
comment|/**      * @return Returns the outboundTopicConnection.      */
specifier|public
name|TopicConnection
name|getOutboundTopicConnection
parameter_list|()
block|{
return|return
name|outboundTopicConnection
return|;
block|}
comment|/**      * @param outboundTopicConnection      *            The outboundTopicConnection to set.      */
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
name|outboundTopicConnection
operator|=
name|foreignTopicConnection
expr_stmt|;
block|}
comment|/**      * @param outboundTopicConnectionFactory      *            The outboundTopicConnectionFactory to set.      */
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
comment|/**      * @return Returns the outboundPassword.      */
specifier|public
name|String
name|getOutboundPassword
parameter_list|()
block|{
return|return
name|outboundPassword
return|;
block|}
comment|/**      * @param outboundPassword      *            The outboundPassword to set.      */
specifier|public
name|void
name|setOutboundPassword
parameter_list|(
name|String
name|foreignPassword
parameter_list|)
block|{
name|this
operator|.
name|outboundPassword
operator|=
name|foreignPassword
expr_stmt|;
block|}
comment|/**      * @return Returns the outboundUsername.      */
specifier|public
name|String
name|getOutboundUsername
parameter_list|()
block|{
return|return
name|outboundUsername
return|;
block|}
comment|/**      * @param outboundUsername      *            The outboundUsername to set.      */
specifier|public
name|void
name|setOutboundUsername
parameter_list|(
name|String
name|foreignUsername
parameter_list|)
block|{
name|this
operator|.
name|outboundUsername
operator|=
name|foreignUsername
expr_stmt|;
block|}
comment|/**      * @return Returns the localPassword.      */
specifier|public
name|String
name|getLocalPassword
parameter_list|()
block|{
return|return
name|localPassword
return|;
block|}
comment|/**      * @param localPassword      *            The localPassword to set.      */
specifier|public
name|void
name|setLocalPassword
parameter_list|(
name|String
name|localPassword
parameter_list|)
block|{
name|this
operator|.
name|localPassword
operator|=
name|localPassword
expr_stmt|;
block|}
comment|/**      * @return Returns the localUsername.      */
specifier|public
name|String
name|getLocalUsername
parameter_list|()
block|{
return|return
name|localUsername
return|;
block|}
comment|/**      * @param localUsername      *            The localUsername to set.      */
specifier|public
name|void
name|setLocalUsername
parameter_list|(
name|String
name|localUsername
parameter_list|)
block|{
name|this
operator|.
name|localUsername
operator|=
name|localUsername
expr_stmt|;
block|}
comment|/**      * @return Returns the replyToDestinationCacheSize.      */
specifier|public
name|int
name|getReplyToDestinationCacheSize
parameter_list|()
block|{
return|return
name|replyToDestinationCacheSize
return|;
block|}
comment|/**      * @param replyToDestinationCacheSize The replyToDestinationCacheSize to set.      */
specifier|public
name|void
name|setReplyToDestinationCacheSize
parameter_list|(
name|int
name|temporaryTopicCacheSize
parameter_list|)
block|{
name|this
operator|.
name|replyToDestinationCacheSize
operator|=
name|temporaryTopicCacheSize
expr_stmt|;
block|}
specifier|protected
name|void
name|initializeForeignTopicConnection
parameter_list|()
throws|throws
name|NamingException
throws|,
name|JMSException
block|{
if|if
condition|(
name|outboundTopicConnection
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
operator|(
name|TopicConnectionFactory
operator|)
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
name|outboundTopicConnection
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
name|outboundTopicConnection
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
literal|"Cannot create localConnection - no information"
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
name|outboundTopicConnection
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
name|outboundTopicConnection
operator|=
name|outboundTopicConnectionFactory
operator|.
name|createTopicConnection
argument_list|()
expr_stmt|;
block|}
block|}
block|}
name|outboundTopicConnection
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
specifier|protected
name|void
name|initializeLocalTopicConnection
parameter_list|()
throws|throws
name|NamingException
throws|,
name|JMSException
block|{
if|if
condition|(
name|localTopicConnection
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
operator|(
name|TopicConnectionFactory
operator|)
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
name|localTopicConnection
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
name|localTopicConnection
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
name|localTopicConnection
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
name|localTopicConnection
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
name|localTopicConnection
operator|=
name|localTopicConnectionFactory
operator|.
name|createTopicConnection
argument_list|()
expr_stmt|;
block|}
block|}
block|}
name|localTopicConnection
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
specifier|protected
name|void
name|initializeInboundTopicBridges
parameter_list|()
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
name|outboundTopicConnection
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
name|TopicSession
name|localSession
init|=
name|localTopicConnection
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
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|inboundTopicBridges
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|InboundTopicBridge
name|bridge
init|=
name|inboundTopicBridges
index|[
name|i
index|]
decl_stmt|;
name|String
name|topicName
init|=
name|bridge
operator|.
name|getInboundTopicName
argument_list|()
decl_stmt|;
name|Topic
name|activemqTopic
init|=
name|createActiveMQTopic
argument_list|(
name|localSession
argument_list|,
name|topicName
argument_list|)
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
name|setConsumerTopic
argument_list|(
name|foreignTopic
argument_list|)
expr_stmt|;
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
name|localTopicConnection
argument_list|)
expr_stmt|;
name|bridge
operator|.
name|setConsumerConnection
argument_list|(
name|outboundTopicConnection
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
name|setJmsTopicConnector
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
name|localSession
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
specifier|protected
name|void
name|initializeOutboundTopicBridges
parameter_list|()
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
name|outboundTopicConnection
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
name|TopicSession
name|localSession
init|=
name|localTopicConnection
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
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|outboundTopicBridges
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|OutboundTopicBridge
name|bridge
init|=
name|outboundTopicBridges
index|[
name|i
index|]
decl_stmt|;
name|String
name|topicName
init|=
name|bridge
operator|.
name|getOutboundTopicName
argument_list|()
decl_stmt|;
name|Topic
name|activemqTopic
init|=
name|createActiveMQTopic
argument_list|(
name|localSession
argument_list|,
name|topicName
argument_list|)
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
name|setConsumerTopic
argument_list|(
name|activemqTopic
argument_list|)
expr_stmt|;
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
name|outboundTopicConnection
argument_list|)
expr_stmt|;
name|bridge
operator|.
name|setConsumerConnection
argument_list|(
name|localTopicConnection
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
name|setJmsTopicConnector
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
name|localSession
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
specifier|protected
name|Destination
name|createReplyToTopicBridge
parameter_list|(
name|Topic
name|topic
parameter_list|,
name|TopicConnection
name|consumerConnection
parameter_list|,
name|TopicConnection
name|producerConnection
parameter_list|)
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
name|topic
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
comment|//we only handle replyTo destinations - inbound
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
name|localSession
init|=
name|localTopicConnection
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
name|localTopic
init|=
name|localSession
operator|.
name|createTemporaryTopic
argument_list|()
decl_stmt|;
name|localSession
operator|.
name|close
argument_list|()
expr_stmt|;
name|bridge
operator|.
name|setConsumerTopic
argument_list|(
name|localTopic
argument_list|)
expr_stmt|;
name|bridge
operator|.
name|setProducerTopic
argument_list|(
name|topic
argument_list|)
expr_stmt|;
name|bridge
operator|.
name|setProducerConnection
argument_list|(
name|outboundTopicConnection
argument_list|)
expr_stmt|;
name|bridge
operator|.
name|setConsumerConnection
argument_list|(
name|localTopicConnection
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
name|setJmsTopicConnector
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|bridge
operator|.
name|start
argument_list|()
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Created replyTo bridge for "
operator|+
name|topic
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Failed to create replyTo bridge for topic: "
operator|+
name|topic
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
name|topic
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
comment|//look-up the Topic
try|try
block|{
name|result
operator|=
operator|(
name|Topic
operator|)
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
name|log
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
return|return
name|result
return|;
block|}
block|}
end_class

end_unit

