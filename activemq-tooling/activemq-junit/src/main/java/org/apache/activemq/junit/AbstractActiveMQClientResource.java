begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|junit
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Serializable
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URI
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
name|BytesMessage
import|;
end_import

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
name|Session
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
name|TextMessage
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
name|ActiveMQConnectionFactory
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
name|junit
operator|.
name|rules
operator|.
name|ExternalResource
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

begin_class
specifier|public
specifier|abstract
class|class
name|AbstractActiveMQClientResource
extends|extends
name|ExternalResource
block|{
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|this
operator|.
name|getClass
argument_list|()
argument_list|)
decl_stmt|;
name|ActiveMQConnectionFactory
name|connectionFactory
decl_stmt|;
name|Connection
name|connection
decl_stmt|;
name|Session
name|session
decl_stmt|;
name|ActiveMQDestination
name|destination
decl_stmt|;
specifier|public
name|AbstractActiveMQClientResource
parameter_list|(
name|ActiveMQConnectionFactory
name|connectionFactory
parameter_list|)
block|{
name|this
operator|.
name|connectionFactory
operator|=
name|connectionFactory
expr_stmt|;
block|}
specifier|public
name|AbstractActiveMQClientResource
parameter_list|(
name|URI
name|brokerURI
parameter_list|)
block|{
name|this
argument_list|(
operator|new
name|ActiveMQConnectionFactory
argument_list|(
name|brokerURI
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|AbstractActiveMQClientResource
parameter_list|(
name|EmbeddedActiveMQBroker
name|embeddedActiveMQBroker
parameter_list|)
block|{
name|this
argument_list|(
name|embeddedActiveMQBroker
operator|.
name|createConnectionFactory
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|AbstractActiveMQClientResource
parameter_list|(
name|URI
name|brokerURI
parameter_list|,
name|String
name|userName
parameter_list|,
name|String
name|password
parameter_list|)
block|{
name|this
argument_list|(
operator|new
name|ActiveMQConnectionFactory
argument_list|(
name|userName
argument_list|,
name|password
argument_list|,
name|brokerURI
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|AbstractActiveMQClientResource
parameter_list|(
name|String
name|destinationName
parameter_list|,
name|ActiveMQConnectionFactory
name|connectionFactory
parameter_list|)
block|{
name|this
argument_list|(
name|connectionFactory
argument_list|)
expr_stmt|;
name|destination
operator|=
name|createDestination
argument_list|(
name|destinationName
argument_list|)
expr_stmt|;
block|}
specifier|public
name|AbstractActiveMQClientResource
parameter_list|(
name|String
name|destinationName
parameter_list|,
name|URI
name|brokerURI
parameter_list|)
block|{
name|this
argument_list|(
name|destinationName
argument_list|,
operator|new
name|ActiveMQConnectionFactory
argument_list|(
name|brokerURI
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|AbstractActiveMQClientResource
parameter_list|(
name|String
name|destinationName
parameter_list|,
name|EmbeddedActiveMQBroker
name|embeddedActiveMQBroker
parameter_list|)
block|{
name|this
argument_list|(
name|destinationName
argument_list|,
name|embeddedActiveMQBroker
operator|.
name|createConnectionFactory
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|AbstractActiveMQClientResource
parameter_list|(
name|String
name|destinationName
parameter_list|,
name|URI
name|brokerURI
parameter_list|,
name|String
name|userName
parameter_list|,
name|String
name|password
parameter_list|)
block|{
name|this
argument_list|(
name|destinationName
argument_list|,
operator|new
name|ActiveMQConnectionFactory
argument_list|(
name|userName
argument_list|,
name|password
argument_list|,
name|brokerURI
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|static
name|void
name|setMessageProperties
parameter_list|(
name|Message
name|message
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|properties
parameter_list|)
throws|throws
name|JMSException
block|{
if|if
condition|(
name|properties
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|property
range|:
name|properties
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|message
operator|.
name|setObjectProperty
argument_list|(
name|property
operator|.
name|getKey
argument_list|()
argument_list|,
name|property
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|public
name|String
name|getClientId
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
specifier|public
name|String
name|getDestinationName
parameter_list|()
block|{
return|return
operator|(
name|destination
operator|!=
literal|null
operator|)
condition|?
name|destination
operator|.
name|toString
argument_list|()
else|:
literal|null
return|;
block|}
specifier|public
specifier|abstract
name|byte
name|getDestinationType
parameter_list|()
function_decl|;
specifier|protected
specifier|abstract
name|void
name|createClient
parameter_list|()
throws|throws
name|JMSException
function_decl|;
comment|/**      * Start the Client      *<p/>      * Invoked by JUnit to setup the resource      */
annotation|@
name|Override
specifier|protected
name|void
name|before
parameter_list|()
throws|throws
name|Throwable
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Starting {}: {}"
argument_list|,
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
argument_list|,
name|connectionFactory
operator|.
name|getBrokerURL
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|start
argument_list|()
expr_stmt|;
name|super
operator|.
name|before
argument_list|()
expr_stmt|;
block|}
comment|/**      * Stop the Client      *<p/>      * Invoked by JUnit to tear down the resource      */
annotation|@
name|Override
specifier|protected
name|void
name|after
parameter_list|()
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Stopping {}: {}"
argument_list|,
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
argument_list|,
name|connectionFactory
operator|.
name|getBrokerURL
argument_list|()
argument_list|)
expr_stmt|;
name|super
operator|.
name|after
argument_list|()
expr_stmt|;
name|this
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|start
parameter_list|()
block|{
try|try
block|{
try|try
block|{
name|connection
operator|=
name|connectionFactory
operator|.
name|createConnection
argument_list|()
expr_stmt|;
name|String
name|clientId
init|=
name|getClientId
argument_list|()
decl_stmt|;
if|if
condition|(
name|clientId
operator|!=
literal|null
condition|)
block|{
name|connection
operator|.
name|setClientID
argument_list|(
name|clientId
argument_list|)
expr_stmt|;
block|}
name|session
operator|=
name|connection
operator|.
name|createSession
argument_list|(
literal|false
argument_list|,
name|Session
operator|.
name|AUTO_ACKNOWLEDGE
argument_list|)
expr_stmt|;
name|createClient
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|JMSException
name|jmsEx
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Producer initialization failed"
operator|+
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
argument_list|,
name|jmsEx
argument_list|)
throw|;
block|}
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|JMSException
name|jmsEx
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Producer failed to start"
argument_list|,
name|jmsEx
argument_list|)
throw|;
block|}
name|log
operator|.
name|info
argument_list|(
literal|"Ready to produce messages to {}"
argument_list|,
name|connectionFactory
operator|.
name|getBrokerURL
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|stop
parameter_list|()
block|{
try|try
block|{
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|JMSException
name|jmsEx
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Exception encountered closing JMS Connection"
argument_list|,
name|jmsEx
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|String
name|getBrokerURL
parameter_list|()
block|{
return|return
name|connectionFactory
operator|.
name|getBrokerURL
argument_list|()
return|;
block|}
specifier|protected
name|ActiveMQDestination
name|createDestination
parameter_list|(
name|String
name|destinationName
parameter_list|)
block|{
if|if
condition|(
name|destinationName
operator|!=
literal|null
condition|)
block|{
return|return
name|ActiveMQDestination
operator|.
name|createDestination
argument_list|(
name|destinationName
argument_list|,
name|getDestinationType
argument_list|()
argument_list|)
return|;
block|}
return|return
literal|null
return|;
block|}
specifier|public
name|BytesMessage
name|createBytesMessage
parameter_list|()
throws|throws
name|JMSException
block|{
return|return
name|session
operator|.
name|createBytesMessage
argument_list|()
return|;
block|}
specifier|public
name|TextMessage
name|createTextMessage
parameter_list|()
throws|throws
name|JMSException
block|{
return|return
name|session
operator|.
name|createTextMessage
argument_list|()
return|;
block|}
specifier|public
name|MapMessage
name|createMapMessage
parameter_list|()
throws|throws
name|JMSException
block|{
return|return
name|session
operator|.
name|createMapMessage
argument_list|()
return|;
block|}
specifier|public
name|ObjectMessage
name|createObjectMessage
parameter_list|()
throws|throws
name|JMSException
block|{
return|return
name|session
operator|.
name|createObjectMessage
argument_list|()
return|;
block|}
specifier|public
name|StreamMessage
name|createStreamMessage
parameter_list|()
throws|throws
name|JMSException
block|{
return|return
name|session
operator|.
name|createStreamMessage
argument_list|()
return|;
block|}
specifier|public
name|BytesMessage
name|createMessage
parameter_list|(
name|byte
index|[]
name|body
parameter_list|)
throws|throws
name|JMSException
block|{
return|return
name|this
operator|.
name|createMessage
argument_list|(
name|body
argument_list|,
literal|null
argument_list|)
return|;
block|}
specifier|public
name|TextMessage
name|createMessage
parameter_list|(
name|String
name|body
parameter_list|)
throws|throws
name|JMSException
block|{
return|return
name|this
operator|.
name|createMessage
argument_list|(
name|body
argument_list|,
literal|null
argument_list|)
return|;
block|}
specifier|public
name|MapMessage
name|createMessage
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|body
parameter_list|)
throws|throws
name|JMSException
block|{
return|return
name|this
operator|.
name|createMessage
argument_list|(
name|body
argument_list|,
literal|null
argument_list|)
return|;
block|}
specifier|public
name|ObjectMessage
name|createMessage
parameter_list|(
name|Serializable
name|body
parameter_list|)
throws|throws
name|JMSException
block|{
return|return
name|this
operator|.
name|createMessage
argument_list|(
name|body
argument_list|,
literal|null
argument_list|)
return|;
block|}
specifier|public
name|BytesMessage
name|createMessage
parameter_list|(
name|byte
index|[]
name|body
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|properties
parameter_list|)
throws|throws
name|JMSException
block|{
name|BytesMessage
name|message
init|=
name|this
operator|.
name|createBytesMessage
argument_list|()
decl_stmt|;
if|if
condition|(
name|body
operator|!=
literal|null
condition|)
block|{
name|message
operator|.
name|writeBytes
argument_list|(
name|body
argument_list|)
expr_stmt|;
block|}
name|setMessageProperties
argument_list|(
name|message
argument_list|,
name|properties
argument_list|)
expr_stmt|;
return|return
name|message
return|;
block|}
specifier|public
name|TextMessage
name|createMessage
parameter_list|(
name|String
name|body
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|properties
parameter_list|)
throws|throws
name|JMSException
block|{
name|TextMessage
name|message
init|=
name|this
operator|.
name|createTextMessage
argument_list|()
decl_stmt|;
if|if
condition|(
name|body
operator|!=
literal|null
condition|)
block|{
name|message
operator|.
name|setText
argument_list|(
name|body
argument_list|)
expr_stmt|;
block|}
name|setMessageProperties
argument_list|(
name|message
argument_list|,
name|properties
argument_list|)
expr_stmt|;
return|return
name|message
return|;
block|}
specifier|public
name|MapMessage
name|createMessage
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|body
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|properties
parameter_list|)
throws|throws
name|JMSException
block|{
name|MapMessage
name|message
init|=
name|this
operator|.
name|createMapMessage
argument_list|()
decl_stmt|;
if|if
condition|(
name|body
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|entry
range|:
name|body
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|message
operator|.
name|setObject
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|setMessageProperties
argument_list|(
name|message
argument_list|,
name|properties
argument_list|)
expr_stmt|;
return|return
name|message
return|;
block|}
specifier|public
name|ObjectMessage
name|createMessage
parameter_list|(
name|Serializable
name|body
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|properties
parameter_list|)
throws|throws
name|JMSException
block|{
name|ObjectMessage
name|message
init|=
name|this
operator|.
name|createObjectMessage
argument_list|()
decl_stmt|;
if|if
condition|(
name|body
operator|!=
literal|null
condition|)
block|{
name|message
operator|.
name|setObject
argument_list|(
name|body
argument_list|)
expr_stmt|;
block|}
name|setMessageProperties
argument_list|(
name|message
argument_list|,
name|properties
argument_list|)
expr_stmt|;
return|return
name|message
return|;
block|}
block|}
end_class

end_unit

