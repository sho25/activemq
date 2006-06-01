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
name|tool
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
name|*
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
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_class
specifier|public
class|class
name|JmsConfigurableClientSupport
extends|extends
name|JmsBasicClientSupport
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
name|JmsConfigurableClientSupport
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|AMQ_SERVER
init|=
literal|"amq"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|AMQ_CONNECTION_FACTORY_CLASS
init|=
literal|"org.apache.activemq.ActiveMQConnectionFactory"
decl_stmt|;
specifier|private
name|String
name|serverType
init|=
literal|""
decl_stmt|;
specifier|private
name|String
name|factoryClass
init|=
literal|""
decl_stmt|;
specifier|private
name|Map
name|factorySettings
init|=
operator|new
name|HashMap
argument_list|()
decl_stmt|;
specifier|private
name|Map
name|connectionSettings
init|=
operator|new
name|HashMap
argument_list|()
decl_stmt|;
specifier|private
name|Map
name|sessionSettings
init|=
operator|new
name|HashMap
argument_list|()
decl_stmt|;
specifier|private
name|Map
name|queueSettings
init|=
operator|new
name|HashMap
argument_list|()
decl_stmt|;
specifier|private
name|Map
name|topicSettings
init|=
operator|new
name|HashMap
argument_list|()
decl_stmt|;
specifier|private
name|Map
name|consumerSettings
init|=
operator|new
name|HashMap
argument_list|()
decl_stmt|;
specifier|private
name|Map
name|producerSettings
init|=
operator|new
name|HashMap
argument_list|()
decl_stmt|;
specifier|private
name|Map
name|messageSettings
init|=
operator|new
name|HashMap
argument_list|()
decl_stmt|;
specifier|protected
name|ConnectionFactory
name|jmsFactory
init|=
literal|null
decl_stmt|;
specifier|protected
name|Connection
name|jmsConnection
init|=
literal|null
decl_stmt|;
specifier|protected
name|Session
name|jmsSession
init|=
literal|null
decl_stmt|;
specifier|protected
name|MessageProducer
name|jmsMessageProducer
init|=
literal|null
decl_stmt|;
specifier|protected
name|MessageConsumer
name|jmsMessageConsumer
init|=
literal|null
decl_stmt|;
specifier|public
name|ConnectionFactory
name|createConnectionFactory
parameter_list|(
name|String
name|url
parameter_list|)
block|{
name|jmsFactory
operator|=
name|super
operator|.
name|createConnectionFactory
argument_list|(
name|factoryClass
argument_list|,
name|url
argument_list|,
name|factorySettings
argument_list|)
expr_stmt|;
return|return
name|jmsFactory
return|;
block|}
specifier|public
name|ConnectionFactory
name|createConnectionFactory
parameter_list|(
name|String
name|clazz
parameter_list|,
name|String
name|url
parameter_list|)
block|{
name|factoryClass
operator|=
name|clazz
expr_stmt|;
name|jmsFactory
operator|=
name|super
operator|.
name|createConnectionFactory
argument_list|(
name|clazz
argument_list|,
name|url
argument_list|,
name|factorySettings
argument_list|)
expr_stmt|;
return|return
name|jmsFactory
return|;
block|}
specifier|public
name|ConnectionFactory
name|createConnectionFactory
parameter_list|(
name|String
name|clazz
parameter_list|,
name|String
name|url
parameter_list|,
name|Map
name|props
parameter_list|)
block|{
name|factoryClass
operator|=
name|clazz
expr_stmt|;
comment|// Add previous settings to current settings
name|props
operator|.
name|putAll
argument_list|(
name|factorySettings
argument_list|)
expr_stmt|;
name|jmsFactory
operator|=
name|super
operator|.
name|createConnectionFactory
argument_list|(
name|clazz
argument_list|,
name|url
argument_list|,
name|props
argument_list|)
expr_stmt|;
return|return
name|jmsFactory
return|;
block|}
specifier|public
name|ConnectionFactory
name|getConnectionFactory
parameter_list|()
block|{
return|return
name|jmsFactory
return|;
block|}
specifier|public
name|Connection
name|getConnection
parameter_list|()
throws|throws
name|JMSException
block|{
if|if
condition|(
name|jmsConnection
operator|==
literal|null
condition|)
block|{
comment|// Retrieve username and password parameter is they exist
name|String
name|username
init|=
operator|(
name|String
operator|)
name|connectionSettings
operator|.
name|get
argument_list|(
literal|"username"
argument_list|)
decl_stmt|;
name|String
name|password
init|=
operator|(
name|String
operator|)
name|connectionSettings
operator|.
name|get
argument_list|(
literal|"password"
argument_list|)
decl_stmt|;
if|if
condition|(
name|username
operator|==
literal|null
condition|)
block|{
name|username
operator|=
literal|""
expr_stmt|;
block|}
if|if
condition|(
name|password
operator|==
literal|null
condition|)
block|{
name|password
operator|=
literal|""
expr_stmt|;
block|}
name|jmsConnection
operator|=
name|getConnectionFactory
argument_list|()
operator|.
name|createConnection
argument_list|(
name|username
argument_list|,
name|password
argument_list|)
expr_stmt|;
name|configureJmsObject
argument_list|(
name|jmsConnection
argument_list|,
name|connectionSettings
argument_list|)
expr_stmt|;
block|}
return|return
name|jmsConnection
return|;
block|}
specifier|public
name|Session
name|getSession
parameter_list|()
throws|throws
name|JMSException
block|{
if|if
condition|(
name|jmsSession
operator|==
literal|null
condition|)
block|{
name|boolean
name|transacted
decl_stmt|;
comment|// Check if session is transacted
if|if
condition|(
name|sessionSettings
operator|.
name|get
argument_list|(
literal|"transacted"
argument_list|)
operator|!=
literal|null
operator|&&
operator|(
operator|(
name|String
operator|)
name|sessionSettings
operator|.
name|get
argument_list|(
literal|"transacted"
argument_list|)
operator|)
operator|.
name|equals
argument_list|(
literal|"true"
argument_list|)
condition|)
block|{
name|transacted
operator|=
literal|true
expr_stmt|;
block|}
else|else
block|{
name|transacted
operator|=
literal|false
expr_stmt|;
block|}
comment|// Check acknowledge type - default is AUTO_ACKNOWLEDGE
name|String
name|ackModeStr
init|=
operator|(
name|String
operator|)
name|sessionSettings
operator|.
name|get
argument_list|(
literal|"ackMode"
argument_list|)
decl_stmt|;
name|int
name|ackMode
init|=
name|Session
operator|.
name|AUTO_ACKNOWLEDGE
decl_stmt|;
if|if
condition|(
name|ackModeStr
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|ackModeStr
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"CLIENT_ACKNOWLEDGE"
argument_list|)
condition|)
block|{
name|ackMode
operator|=
name|Session
operator|.
name|CLIENT_ACKNOWLEDGE
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|ackModeStr
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"DUPS_OK_ACKNOWLEDGE"
argument_list|)
condition|)
block|{
name|ackMode
operator|=
name|Session
operator|.
name|DUPS_OK_ACKNOWLEDGE
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|ackModeStr
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"SESSION_TRANSACTED"
argument_list|)
condition|)
block|{
name|ackMode
operator|=
name|Session
operator|.
name|SESSION_TRANSACTED
expr_stmt|;
block|}
block|}
name|jmsSession
operator|=
name|getConnection
argument_list|()
operator|.
name|createSession
argument_list|(
name|transacted
argument_list|,
name|ackMode
argument_list|)
expr_stmt|;
name|configureJmsObject
argument_list|(
name|jmsSession
argument_list|,
name|sessionSettings
argument_list|)
expr_stmt|;
block|}
return|return
name|jmsSession
return|;
block|}
specifier|public
name|MessageProducer
name|createMessageProducer
parameter_list|(
name|Destination
name|dest
parameter_list|)
throws|throws
name|JMSException
block|{
name|jmsMessageProducer
operator|=
name|getSession
argument_list|()
operator|.
name|createProducer
argument_list|(
name|dest
argument_list|)
expr_stmt|;
name|configureJmsObject
argument_list|(
name|jmsMessageProducer
argument_list|,
name|producerSettings
argument_list|)
expr_stmt|;
return|return
name|jmsMessageProducer
return|;
block|}
specifier|public
name|MessageProducer
name|getMessageProducer
parameter_list|()
block|{
return|return
name|jmsMessageProducer
return|;
block|}
specifier|public
name|MessageConsumer
name|createMessageConsumer
parameter_list|(
name|Destination
name|dest
parameter_list|,
name|String
name|selector
parameter_list|,
name|boolean
name|noLocal
parameter_list|)
throws|throws
name|JMSException
block|{
name|jmsMessageConsumer
operator|=
name|getSession
argument_list|()
operator|.
name|createConsumer
argument_list|(
name|dest
argument_list|,
name|selector
argument_list|,
name|noLocal
argument_list|)
expr_stmt|;
name|configureJmsObject
argument_list|(
name|jmsMessageConsumer
argument_list|,
name|consumerSettings
argument_list|)
expr_stmt|;
return|return
name|jmsMessageConsumer
return|;
block|}
specifier|public
name|MessageConsumer
name|getMessageConsumer
parameter_list|()
block|{
return|return
name|jmsMessageConsumer
return|;
block|}
specifier|public
name|TopicSubscriber
name|createDurableSubscriber
parameter_list|(
name|Topic
name|dest
parameter_list|,
name|String
name|name
parameter_list|,
name|String
name|selector
parameter_list|,
name|boolean
name|noLocal
parameter_list|)
throws|throws
name|JMSException
block|{
name|jmsMessageConsumer
operator|=
name|getSession
argument_list|()
operator|.
name|createDurableSubscriber
argument_list|(
name|dest
argument_list|,
name|name
argument_list|,
name|selector
argument_list|,
name|noLocal
argument_list|)
expr_stmt|;
name|configureJmsObject
argument_list|(
name|jmsMessageConsumer
argument_list|,
name|consumerSettings
argument_list|)
expr_stmt|;
return|return
operator|(
name|TopicSubscriber
operator|)
name|jmsMessageConsumer
return|;
block|}
specifier|public
name|TopicSubscriber
name|getDurableSubscriber
parameter_list|()
block|{
return|return
operator|(
name|TopicSubscriber
operator|)
name|jmsMessageConsumer
return|;
block|}
specifier|public
name|TextMessage
name|createTextMessage
parameter_list|(
name|String
name|text
parameter_list|)
throws|throws
name|JMSException
block|{
name|TextMessage
name|msg
init|=
name|getSession
argument_list|()
operator|.
name|createTextMessage
argument_list|(
name|text
argument_list|)
decl_stmt|;
name|configureJmsObject
argument_list|(
name|msg
argument_list|,
name|messageSettings
argument_list|)
expr_stmt|;
return|return
name|msg
return|;
block|}
specifier|public
name|Queue
name|createQueue
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|JMSException
block|{
name|Queue
name|queue
init|=
name|getSession
argument_list|()
operator|.
name|createQueue
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|configureJmsObject
argument_list|(
name|queue
argument_list|,
name|queueSettings
argument_list|)
expr_stmt|;
return|return
name|queue
return|;
block|}
specifier|public
name|Topic
name|createTopic
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|JMSException
block|{
name|Topic
name|topic
init|=
name|getSession
argument_list|()
operator|.
name|createTopic
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|configureJmsObject
argument_list|(
name|topic
argument_list|,
name|topicSettings
argument_list|)
expr_stmt|;
return|return
name|topic
return|;
block|}
specifier|public
name|void
name|addConfigParam
parameter_list|(
name|String
name|key
parameter_list|,
name|Object
name|value
parameter_list|)
block|{
comment|// Simple mapping of JMS Server to connection factory class
if|if
condition|(
name|key
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"server"
argument_list|)
condition|)
block|{
name|serverType
operator|=
name|value
operator|.
name|toString
argument_list|()
expr_stmt|;
if|if
condition|(
name|serverType
operator|.
name|equalsIgnoreCase
argument_list|(
name|AMQ_SERVER
argument_list|)
condition|)
block|{
name|factoryClass
operator|=
name|AMQ_CONNECTION_FACTORY_CLASS
expr_stmt|;
block|}
comment|// Manually specify the connection factory class to use
block|}
elseif|else
if|if
condition|(
name|key
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"factoryClass"
argument_list|)
condition|)
block|{
name|factoryClass
operator|=
name|value
operator|.
name|toString
argument_list|()
expr_stmt|;
comment|// Connection factory specific settings
block|}
elseif|else
if|if
condition|(
name|key
operator|.
name|startsWith
argument_list|(
literal|"factory."
argument_list|)
condition|)
block|{
name|factorySettings
operator|.
name|put
argument_list|(
name|key
operator|.
name|substring
argument_list|(
literal|"factory."
operator|.
name|length
argument_list|()
argument_list|)
argument_list|,
name|value
argument_list|)
expr_stmt|;
comment|// Connection specific settings
block|}
elseif|else
if|if
condition|(
name|key
operator|.
name|startsWith
argument_list|(
literal|"connection."
argument_list|)
condition|)
block|{
name|connectionSettings
operator|.
name|put
argument_list|(
name|key
operator|.
name|substring
argument_list|(
literal|"session."
operator|.
name|length
argument_list|()
argument_list|)
argument_list|,
name|value
argument_list|)
expr_stmt|;
comment|// Session specific settings
block|}
elseif|else
if|if
condition|(
name|key
operator|.
name|startsWith
argument_list|(
literal|"session."
argument_list|)
condition|)
block|{
name|sessionSettings
operator|.
name|put
argument_list|(
name|key
operator|.
name|substring
argument_list|(
literal|"session."
operator|.
name|length
argument_list|()
argument_list|)
argument_list|,
name|value
argument_list|)
expr_stmt|;
comment|// Destination specific settings
block|}
elseif|else
if|if
condition|(
name|key
operator|.
name|startsWith
argument_list|(
literal|"dest."
argument_list|)
condition|)
block|{
name|queueSettings
operator|.
name|put
argument_list|(
name|key
operator|.
name|substring
argument_list|(
literal|"dest."
operator|.
name|length
argument_list|()
argument_list|)
argument_list|,
name|value
argument_list|)
expr_stmt|;
name|topicSettings
operator|.
name|put
argument_list|(
name|key
operator|.
name|substring
argument_list|(
literal|"dest."
operator|.
name|length
argument_list|()
argument_list|)
argument_list|,
name|value
argument_list|)
expr_stmt|;
comment|// Queue specific settings
block|}
elseif|else
if|if
condition|(
name|key
operator|.
name|startsWith
argument_list|(
literal|"queue."
argument_list|)
condition|)
block|{
name|queueSettings
operator|.
name|put
argument_list|(
name|key
operator|.
name|substring
argument_list|(
literal|"queue."
operator|.
name|length
argument_list|()
argument_list|)
argument_list|,
name|value
argument_list|)
expr_stmt|;
comment|// Topic specific settings
block|}
elseif|else
if|if
condition|(
name|key
operator|.
name|startsWith
argument_list|(
literal|"topic."
argument_list|)
condition|)
block|{
name|topicSettings
operator|.
name|put
argument_list|(
name|key
operator|.
name|substring
argument_list|(
literal|"topic."
operator|.
name|length
argument_list|()
argument_list|)
argument_list|,
name|value
argument_list|)
expr_stmt|;
comment|// Consumer specific settings
block|}
elseif|else
if|if
condition|(
name|key
operator|.
name|startsWith
argument_list|(
literal|"consumer."
argument_list|)
condition|)
block|{
name|consumerSettings
operator|.
name|put
argument_list|(
name|key
operator|.
name|substring
argument_list|(
literal|"consumer."
operator|.
name|length
argument_list|()
argument_list|)
argument_list|,
name|value
argument_list|)
expr_stmt|;
comment|// Producer specific settings
block|}
elseif|else
if|if
condition|(
name|key
operator|.
name|startsWith
argument_list|(
literal|"producer."
argument_list|)
condition|)
block|{
name|producerSettings
operator|.
name|put
argument_list|(
name|key
operator|.
name|substring
argument_list|(
literal|"producer."
operator|.
name|length
argument_list|()
argument_list|)
argument_list|,
name|value
argument_list|)
expr_stmt|;
comment|// Message specific settings
block|}
elseif|else
if|if
condition|(
name|key
operator|.
name|startsWith
argument_list|(
literal|"message."
argument_list|)
condition|)
block|{
name|messageSettings
operator|.
name|put
argument_list|(
name|key
operator|.
name|substring
argument_list|(
literal|"message."
operator|.
name|length
argument_list|()
argument_list|)
argument_list|,
name|value
argument_list|)
expr_stmt|;
comment|// Unknown settings
block|}
else|else
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Unknown setting: "
operator|+
name|key
operator|+
literal|" = "
operator|+
name|value
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|configureJmsObject
parameter_list|(
name|Object
name|jmsObject
parameter_list|,
name|Map
name|props
parameter_list|)
block|{
if|if
condition|(
name|props
operator|==
literal|null
operator|||
name|props
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return;
block|}
name|ReflectionUtil
operator|.
name|configureClass
argument_list|(
name|jmsObject
argument_list|,
name|props
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|configureJmsObject
parameter_list|(
name|Object
name|jmsObject
parameter_list|,
name|String
name|key
parameter_list|,
name|Object
name|val
parameter_list|)
block|{
if|if
condition|(
name|key
operator|==
literal|null
operator|||
name|key
operator|==
literal|""
operator|||
name|val
operator|==
literal|null
condition|)
block|{
return|return;
block|}
name|ReflectionUtil
operator|.
name|configureClass
argument_list|(
name|jmsObject
argument_list|,
name|key
argument_list|,
name|val
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

