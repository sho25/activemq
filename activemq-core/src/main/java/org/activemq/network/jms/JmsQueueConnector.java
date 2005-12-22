begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Copyright 2004 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
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
name|Queue
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|QueueConnection
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|QueueConnectionFactory
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|QueueSession
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
name|naming
operator|.
name|NamingException
import|;
end_import

begin_comment
comment|/**  * A Bridge to other JMS Queue providers  *   * @org.xbean.XBean  *  * @version $Revision: 1.1.1.1 $  */
end_comment

begin_class
specifier|public
class|class
name|JmsQueueConnector
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
name|JmsQueueConnector
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|String
name|outboundQueueConnectionFactoryName
decl_stmt|;
specifier|private
name|String
name|localConnectionFactoryName
decl_stmt|;
specifier|private
name|QueueConnectionFactory
name|outboundQueueConnectionFactory
decl_stmt|;
specifier|private
name|QueueConnectionFactory
name|localQueueConnectionFactory
decl_stmt|;
specifier|private
name|QueueConnection
name|outboundQueueConnection
decl_stmt|;
specifier|private
name|QueueConnection
name|localQueueConnection
decl_stmt|;
specifier|private
name|InboundQueueBridge
index|[]
name|inboundQueueBridges
decl_stmt|;
specifier|private
name|OutboundQueueBridge
index|[]
name|outboundQueueBridges
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
name|initializeForeignQueueConnection
argument_list|()
expr_stmt|;
name|initializeLocalQueueConnection
argument_list|()
expr_stmt|;
name|initializeInboundQueueBridges
argument_list|()
expr_stmt|;
name|initializeOutboundQueueBridges
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
comment|/**      * @return Returns the inboundQueueBridges.      */
specifier|public
name|InboundQueueBridge
index|[]
name|getInboundQueueBridges
parameter_list|()
block|{
return|return
name|inboundQueueBridges
return|;
block|}
comment|/**      * @param inboundQueueBridges      *            The inboundQueueBridges to set.      */
specifier|public
name|void
name|setInboundQueueBridges
parameter_list|(
name|InboundQueueBridge
index|[]
name|inboundQueueBridges
parameter_list|)
block|{
name|this
operator|.
name|inboundQueueBridges
operator|=
name|inboundQueueBridges
expr_stmt|;
block|}
comment|/**      * @return Returns the outboundQueueBridges.      */
specifier|public
name|OutboundQueueBridge
index|[]
name|getOutboundQueueBridges
parameter_list|()
block|{
return|return
name|outboundQueueBridges
return|;
block|}
comment|/**      * @param outboundQueueBridges      *            The outboundQueueBridges to set.      */
specifier|public
name|void
name|setOutboundQueueBridges
parameter_list|(
name|OutboundQueueBridge
index|[]
name|outboundQueueBridges
parameter_list|)
block|{
name|this
operator|.
name|outboundQueueBridges
operator|=
name|outboundQueueBridges
expr_stmt|;
block|}
comment|/**      * @return Returns the localQueueConnectionFactory.      */
specifier|public
name|QueueConnectionFactory
name|getLocalQueueConnectionFactory
parameter_list|()
block|{
return|return
name|localQueueConnectionFactory
return|;
block|}
comment|/**      * @param localQueueConnectionFactory      *            The localQueueConnectionFactory to set.      */
specifier|public
name|void
name|setLocalQueueConnectionFactory
parameter_list|(
name|QueueConnectionFactory
name|localConnectionFactory
parameter_list|)
block|{
name|this
operator|.
name|localQueueConnectionFactory
operator|=
name|localConnectionFactory
expr_stmt|;
block|}
comment|/**      * @return Returns the outboundQueueConnectionFactory.      */
specifier|public
name|QueueConnectionFactory
name|getOutboundQueueConnectionFactory
parameter_list|()
block|{
return|return
name|outboundQueueConnectionFactory
return|;
block|}
comment|/**      * @return Returns the outboundQueueConnectionFactoryName.      */
specifier|public
name|String
name|getOutboundQueueConnectionFactoryName
parameter_list|()
block|{
return|return
name|outboundQueueConnectionFactoryName
return|;
block|}
comment|/**      * @param outboundQueueConnectionFactoryName      *            The outboundQueueConnectionFactoryName to set.      */
specifier|public
name|void
name|setOutboundQueueConnectionFactoryName
parameter_list|(
name|String
name|foreignQueueConnectionFactoryName
parameter_list|)
block|{
name|this
operator|.
name|outboundQueueConnectionFactoryName
operator|=
name|foreignQueueConnectionFactoryName
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
comment|/**      * @return Returns the localQueueConnection.      */
specifier|public
name|QueueConnection
name|getLocalQueueConnection
parameter_list|()
block|{
return|return
name|localQueueConnection
return|;
block|}
comment|/**      * @param localQueueConnection      *            The localQueueConnection to set.      */
specifier|public
name|void
name|setLocalQueueConnection
parameter_list|(
name|QueueConnection
name|localQueueConnection
parameter_list|)
block|{
name|this
operator|.
name|localQueueConnection
operator|=
name|localQueueConnection
expr_stmt|;
block|}
comment|/**      * @return Returns the outboundQueueConnection.      */
specifier|public
name|QueueConnection
name|getOutboundQueueConnection
parameter_list|()
block|{
return|return
name|outboundQueueConnection
return|;
block|}
comment|/**      * @param outboundQueueConnection      *            The outboundQueueConnection to set.      */
specifier|public
name|void
name|setOutboundQueueConnection
parameter_list|(
name|QueueConnection
name|foreignQueueConnection
parameter_list|)
block|{
name|this
operator|.
name|outboundQueueConnection
operator|=
name|foreignQueueConnection
expr_stmt|;
block|}
comment|/**      * @param outboundQueueConnectionFactory      *            The outboundQueueConnectionFactory to set.      */
specifier|public
name|void
name|setOutboundQueueConnectionFactory
parameter_list|(
name|QueueConnectionFactory
name|foreignQueueConnectionFactory
parameter_list|)
block|{
name|this
operator|.
name|outboundQueueConnectionFactory
operator|=
name|foreignQueueConnectionFactory
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
name|temporaryQueueCacheSize
parameter_list|)
block|{
name|this
operator|.
name|replyToDestinationCacheSize
operator|=
name|temporaryQueueCacheSize
expr_stmt|;
block|}
specifier|protected
name|void
name|initializeForeignQueueConnection
parameter_list|()
throws|throws
name|NamingException
throws|,
name|JMSException
block|{
if|if
condition|(
name|outboundQueueConnection
operator|==
literal|null
condition|)
block|{
comment|// get the connection factories
if|if
condition|(
name|outboundQueueConnectionFactory
operator|==
literal|null
condition|)
block|{
comment|// look it up from JNDI
if|if
condition|(
name|outboundQueueConnectionFactoryName
operator|!=
literal|null
condition|)
block|{
name|outboundQueueConnectionFactory
operator|=
operator|(
name|QueueConnectionFactory
operator|)
name|jndiTemplate
operator|.
name|lookup
argument_list|(
name|outboundQueueConnectionFactoryName
argument_list|,
name|QueueConnectionFactory
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
name|outboundQueueConnection
operator|=
name|outboundQueueConnectionFactory
operator|.
name|createQueueConnection
argument_list|(
name|outboundUsername
argument_list|,
name|outboundPassword
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|outboundQueueConnection
operator|=
name|outboundQueueConnectionFactory
operator|.
name|createQueueConnection
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
name|outboundQueueConnection
operator|=
name|outboundQueueConnectionFactory
operator|.
name|createQueueConnection
argument_list|(
name|outboundUsername
argument_list|,
name|outboundPassword
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|outboundQueueConnection
operator|=
name|outboundQueueConnectionFactory
operator|.
name|createQueueConnection
argument_list|()
expr_stmt|;
block|}
block|}
block|}
name|outboundQueueConnection
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
specifier|protected
name|void
name|initializeLocalQueueConnection
parameter_list|()
throws|throws
name|NamingException
throws|,
name|JMSException
block|{
if|if
condition|(
name|localQueueConnection
operator|==
literal|null
condition|)
block|{
comment|// get the connection factories
if|if
condition|(
name|localQueueConnectionFactory
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
name|localQueueConnectionFactory
operator|=
operator|(
name|QueueConnectionFactory
operator|)
name|jndiTemplate
operator|.
name|lookup
argument_list|(
name|localConnectionFactoryName
argument_list|,
name|QueueConnectionFactory
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
name|localQueueConnection
operator|=
name|localQueueConnectionFactory
operator|.
name|createQueueConnection
argument_list|(
name|localUsername
argument_list|,
name|localPassword
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|localQueueConnection
operator|=
name|localQueueConnectionFactory
operator|.
name|createQueueConnection
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
name|localQueueConnection
operator|=
name|embeddedConnectionFactory
operator|.
name|createQueueConnection
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
name|localQueueConnection
operator|=
name|localQueueConnectionFactory
operator|.
name|createQueueConnection
argument_list|(
name|localUsername
argument_list|,
name|localPassword
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|localQueueConnection
operator|=
name|localQueueConnectionFactory
operator|.
name|createQueueConnection
argument_list|()
expr_stmt|;
block|}
block|}
block|}
name|localQueueConnection
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
specifier|protected
name|void
name|initializeInboundQueueBridges
parameter_list|()
throws|throws
name|JMSException
block|{
if|if
condition|(
name|inboundQueueBridges
operator|!=
literal|null
condition|)
block|{
name|QueueSession
name|outboundSession
init|=
name|outboundQueueConnection
operator|.
name|createQueueSession
argument_list|(
literal|false
argument_list|,
name|Session
operator|.
name|AUTO_ACKNOWLEDGE
argument_list|)
decl_stmt|;
name|QueueSession
name|localSession
init|=
name|localQueueConnection
operator|.
name|createQueueSession
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
name|inboundQueueBridges
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|InboundQueueBridge
name|bridge
init|=
name|inboundQueueBridges
index|[
name|i
index|]
decl_stmt|;
name|String
name|queueName
init|=
name|bridge
operator|.
name|getInboundQueueName
argument_list|()
decl_stmt|;
name|Queue
name|activemqQueue
init|=
name|createActiveMQQueue
argument_list|(
name|localSession
argument_list|,
name|queueName
argument_list|)
decl_stmt|;
name|Queue
name|foreignQueue
init|=
name|createForeignQueue
argument_list|(
name|outboundSession
argument_list|,
name|queueName
argument_list|)
decl_stmt|;
name|bridge
operator|.
name|setConsumerQueue
argument_list|(
name|foreignQueue
argument_list|)
expr_stmt|;
name|bridge
operator|.
name|setProducerQueue
argument_list|(
name|activemqQueue
argument_list|)
expr_stmt|;
name|bridge
operator|.
name|setProducerConnection
argument_list|(
name|localQueueConnection
argument_list|)
expr_stmt|;
name|bridge
operator|.
name|setConsumerConnection
argument_list|(
name|outboundQueueConnection
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
name|getJmsMessageConvertor
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|bridge
operator|.
name|setJmsQueueConnector
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
name|initializeOutboundQueueBridges
parameter_list|()
throws|throws
name|JMSException
block|{
if|if
condition|(
name|outboundQueueBridges
operator|!=
literal|null
condition|)
block|{
name|QueueSession
name|outboundSession
init|=
name|outboundQueueConnection
operator|.
name|createQueueSession
argument_list|(
literal|false
argument_list|,
name|Session
operator|.
name|AUTO_ACKNOWLEDGE
argument_list|)
decl_stmt|;
name|QueueSession
name|localSession
init|=
name|localQueueConnection
operator|.
name|createQueueSession
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
name|outboundQueueBridges
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|OutboundQueueBridge
name|bridge
init|=
name|outboundQueueBridges
index|[
name|i
index|]
decl_stmt|;
name|String
name|queueName
init|=
name|bridge
operator|.
name|getOutboundQueueName
argument_list|()
decl_stmt|;
name|Queue
name|activemqQueue
init|=
name|createActiveMQQueue
argument_list|(
name|localSession
argument_list|,
name|queueName
argument_list|)
decl_stmt|;
name|Queue
name|foreignQueue
init|=
name|createForeignQueue
argument_list|(
name|outboundSession
argument_list|,
name|queueName
argument_list|)
decl_stmt|;
name|bridge
operator|.
name|setConsumerQueue
argument_list|(
name|activemqQueue
argument_list|)
expr_stmt|;
name|bridge
operator|.
name|setProducerQueue
argument_list|(
name|foreignQueue
argument_list|)
expr_stmt|;
name|bridge
operator|.
name|setProducerConnection
argument_list|(
name|outboundQueueConnection
argument_list|)
expr_stmt|;
name|bridge
operator|.
name|setConsumerConnection
argument_list|(
name|localQueueConnection
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
name|getJmsMessageConvertor
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|bridge
operator|.
name|setJmsQueueConnector
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
name|createReplyToQueueBridge
parameter_list|(
name|Queue
name|queue
parameter_list|,
name|QueueConnection
name|consumerConnection
parameter_list|,
name|QueueConnection
name|producerConnection
parameter_list|)
block|{
name|OutboundQueueBridge
name|bridge
init|=
operator|(
name|OutboundQueueBridge
operator|)
name|replyToBridges
operator|.
name|get
argument_list|(
name|queue
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
name|OutboundQueueBridge
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
name|QueueSession
name|localSession
init|=
name|localQueueConnection
operator|.
name|createQueueSession
argument_list|(
literal|false
argument_list|,
name|Session
operator|.
name|AUTO_ACKNOWLEDGE
argument_list|)
decl_stmt|;
name|Queue
name|localQueue
init|=
name|localSession
operator|.
name|createTemporaryQueue
argument_list|()
decl_stmt|;
name|localSession
operator|.
name|close
argument_list|()
expr_stmt|;
name|bridge
operator|.
name|setConsumerQueue
argument_list|(
name|localQueue
argument_list|)
expr_stmt|;
name|bridge
operator|.
name|setProducerQueue
argument_list|(
name|queue
argument_list|)
expr_stmt|;
name|bridge
operator|.
name|setProducerConnection
argument_list|(
name|outboundQueueConnection
argument_list|)
expr_stmt|;
name|bridge
operator|.
name|setConsumerConnection
argument_list|(
name|localQueueConnection
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
name|getJmsMessageConvertor
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|bridge
operator|.
name|setJmsQueueConnector
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
name|queue
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
literal|"Failed to create replyTo bridge for queue: "
operator|+
name|queue
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
name|queue
argument_list|,
name|bridge
argument_list|)
expr_stmt|;
block|}
return|return
name|bridge
operator|.
name|getConsumerQueue
argument_list|()
return|;
block|}
specifier|protected
name|Queue
name|createActiveMQQueue
parameter_list|(
name|QueueSession
name|session
parameter_list|,
name|String
name|queueName
parameter_list|)
throws|throws
name|JMSException
block|{
return|return
name|session
operator|.
name|createQueue
argument_list|(
name|queueName
argument_list|)
return|;
block|}
specifier|protected
name|Queue
name|createForeignQueue
parameter_list|(
name|QueueSession
name|session
parameter_list|,
name|String
name|queueName
parameter_list|)
throws|throws
name|JMSException
block|{
name|Queue
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
name|createQueue
argument_list|(
name|queueName
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|JMSException
name|e
parameter_list|)
block|{
comment|//look-up the Queue
try|try
block|{
name|result
operator|=
operator|(
name|Queue
operator|)
name|jndiTemplate
operator|.
name|lookup
argument_list|(
name|queueName
argument_list|,
name|Queue
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
literal|"Failed to look-up Queue for name: "
operator|+
name|queueName
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

