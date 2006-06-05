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
name|ConnectionFactory
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
name|JMSException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Properties
import|;
end_import

begin_class
specifier|public
class|class
name|JmsClientSupport
extends|extends
name|JmsFactorySupport
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
name|JmsClientSupport
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|PREFIX_CONFIG_CLIENT
init|=
literal|"client."
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|SESSION_AUTO_ACKNOWLEDGE
init|=
literal|"autoAck"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|SESSION_CLIENT_ACKNOWLEDGE
init|=
literal|"clientAck"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|SESSION_DUPS_OK_ACKNOWLEDGE
init|=
literal|"dupsAck"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|SESSION_TRANSACTED
init|=
literal|"transacted"
decl_stmt|;
specifier|protected
name|Properties
name|clientSettings
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
specifier|protected
name|Connection
name|jmsConnection
decl_stmt|;
specifier|protected
name|Session
name|jmsSession
decl_stmt|;
comment|// Client settings
specifier|protected
name|String
name|spiClass
decl_stmt|;
specifier|protected
name|boolean
name|sessTransacted
init|=
literal|false
decl_stmt|;
specifier|protected
name|String
name|sessAckMode
init|=
name|SESSION_AUTO_ACKNOWLEDGE
decl_stmt|;
specifier|protected
name|String
name|destName
init|=
literal|"TEST.FOO"
decl_stmt|;
specifier|protected
name|int
name|destCount
init|=
literal|1
decl_stmt|;
specifier|protected
name|int
name|destIndex
init|=
literal|0
decl_stmt|;
specifier|protected
name|boolean
name|destComposite
init|=
literal|false
decl_stmt|;
specifier|public
name|ConnectionFactory
name|createConnectionFactory
parameter_list|()
throws|throws
name|JMSException
block|{
return|return
name|super
operator|.
name|createConnectionFactory
argument_list|(
name|getSpiClass
argument_list|()
argument_list|)
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
name|jmsConnection
operator|=
name|createConnectionFactory
argument_list|()
operator|.
name|createConnection
argument_list|()
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
name|int
name|ackMode
decl_stmt|;
if|if
condition|(
name|getSessAckMode
argument_list|()
operator|.
name|equalsIgnoreCase
argument_list|(
name|SESSION_AUTO_ACKNOWLEDGE
argument_list|)
condition|)
block|{
name|ackMode
operator|=
name|Session
operator|.
name|AUTO_ACKNOWLEDGE
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|getSessAckMode
argument_list|()
operator|.
name|equalsIgnoreCase
argument_list|(
name|SESSION_CLIENT_ACKNOWLEDGE
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
name|getSessAckMode
argument_list|()
operator|.
name|equalsIgnoreCase
argument_list|(
name|SESSION_DUPS_OK_ACKNOWLEDGE
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
name|getSessAckMode
argument_list|()
operator|.
name|equalsIgnoreCase
argument_list|(
name|SESSION_TRANSACTED
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
else|else
block|{
name|ackMode
operator|=
name|Session
operator|.
name|AUTO_ACKNOWLEDGE
expr_stmt|;
block|}
name|jmsSession
operator|=
name|getConnection
argument_list|()
operator|.
name|createSession
argument_list|(
name|isSessTransacted
argument_list|()
argument_list|,
name|ackMode
argument_list|)
expr_stmt|;
block|}
return|return
name|jmsSession
return|;
block|}
specifier|public
name|Destination
index|[]
name|createDestination
parameter_list|()
throws|throws
name|JMSException
block|{
if|if
condition|(
name|isDestComposite
argument_list|()
condition|)
block|{
return|return
operator|new
name|Destination
index|[]
block|{
name|createCompositeDestination
argument_list|(
name|getDestName
argument_list|()
argument_list|,
name|getDestCount
argument_list|()
argument_list|)
block|}
return|;
block|}
else|else
block|{
name|Destination
index|[]
name|dest
init|=
operator|new
name|Destination
index|[
name|getDestCount
argument_list|()
index|]
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
name|getDestCount
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|dest
index|[
name|i
index|]
operator|=
name|createDestination
argument_list|(
name|getDestName
argument_list|()
operator|+
literal|"."
operator|+
operator|(
name|getDestIndex
argument_list|()
operator|+
name|i
operator|)
argument_list|)
expr_stmt|;
block|}
return|return
name|dest
return|;
block|}
block|}
specifier|public
name|Destination
name|createDestination
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|JMSException
block|{
if|if
condition|(
name|name
operator|.
name|startsWith
argument_list|(
literal|"queue://"
argument_list|)
condition|)
block|{
return|return
name|getSession
argument_list|()
operator|.
name|createQueue
argument_list|(
name|name
operator|.
name|substring
argument_list|(
literal|"queue://"
operator|.
name|length
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|name
operator|.
name|startsWith
argument_list|(
literal|"topic://"
argument_list|)
condition|)
block|{
return|return
name|getSession
argument_list|()
operator|.
name|createTopic
argument_list|(
name|name
operator|.
name|substring
argument_list|(
literal|"topic://"
operator|.
name|length
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|getSession
argument_list|()
operator|.
name|createTopic
argument_list|(
name|name
argument_list|)
return|;
block|}
block|}
specifier|public
name|Destination
name|createCompositeDestination
parameter_list|(
name|String
name|name
parameter_list|,
name|int
name|count
parameter_list|)
throws|throws
name|JMSException
block|{
name|String
name|compDestName
init|=
literal|""
decl_stmt|;
name|String
name|simpleName
decl_stmt|;
if|if
condition|(
name|name
operator|.
name|startsWith
argument_list|(
literal|"queue://"
argument_list|)
condition|)
block|{
name|simpleName
operator|=
name|name
operator|.
name|substring
argument_list|(
literal|"queue://"
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|name
operator|.
name|startsWith
argument_list|(
literal|"topic://"
argument_list|)
condition|)
block|{
name|simpleName
operator|=
name|name
operator|.
name|substring
argument_list|(
literal|"topic://"
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|simpleName
operator|=
name|name
expr_stmt|;
block|}
name|int
name|i
decl_stmt|;
name|compDestName
operator|=
name|name
operator|+
literal|".0,"
expr_stmt|;
comment|// First destination
for|for
control|(
name|i
operator|=
literal|1
init|;
name|i
operator|<
name|count
operator|-
literal|1
condition|;
name|i
operator|++
control|)
block|{
name|compDestName
operator|+=
operator|(
name|simpleName
operator|+
literal|"."
operator|+
name|i
operator|+
literal|","
operator|)
expr_stmt|;
block|}
name|compDestName
operator|+=
operator|(
name|simpleName
operator|+
literal|"."
operator|+
name|i
operator|)
expr_stmt|;
comment|// Last destination (minus the comma)
return|return
name|createDestination
argument_list|(
name|compDestName
argument_list|)
return|;
block|}
specifier|public
name|String
name|getSpiClass
parameter_list|()
block|{
return|return
name|spiClass
return|;
block|}
specifier|public
name|void
name|setSpiClass
parameter_list|(
name|String
name|spiClass
parameter_list|)
block|{
name|this
operator|.
name|spiClass
operator|=
name|spiClass
expr_stmt|;
block|}
specifier|public
name|boolean
name|isSessTransacted
parameter_list|()
block|{
return|return
name|sessTransacted
return|;
block|}
specifier|public
name|void
name|setSessTransacted
parameter_list|(
name|boolean
name|sessTransacted
parameter_list|)
block|{
name|this
operator|.
name|sessTransacted
operator|=
name|sessTransacted
expr_stmt|;
block|}
specifier|public
name|String
name|getSessAckMode
parameter_list|()
block|{
return|return
name|sessAckMode
return|;
block|}
specifier|public
name|void
name|setSessAckMode
parameter_list|(
name|String
name|sessAckMode
parameter_list|)
block|{
name|this
operator|.
name|sessAckMode
operator|=
name|sessAckMode
expr_stmt|;
block|}
specifier|public
name|String
name|getDestName
parameter_list|()
block|{
return|return
name|destName
return|;
block|}
specifier|public
name|void
name|setDestName
parameter_list|(
name|String
name|destName
parameter_list|)
block|{
name|this
operator|.
name|destName
operator|=
name|destName
expr_stmt|;
block|}
specifier|public
name|int
name|getDestCount
parameter_list|()
block|{
return|return
name|destCount
return|;
block|}
specifier|public
name|void
name|setDestCount
parameter_list|(
name|int
name|destCount
parameter_list|)
block|{
name|this
operator|.
name|destCount
operator|=
name|destCount
expr_stmt|;
block|}
specifier|public
name|int
name|getDestIndex
parameter_list|()
block|{
return|return
name|destIndex
return|;
block|}
specifier|public
name|void
name|setDestIndex
parameter_list|(
name|int
name|destIndex
parameter_list|)
block|{
name|this
operator|.
name|destIndex
operator|=
name|destIndex
expr_stmt|;
block|}
specifier|public
name|boolean
name|isDestComposite
parameter_list|()
block|{
return|return
name|destComposite
return|;
block|}
specifier|public
name|void
name|setDestComposite
parameter_list|(
name|boolean
name|destComposite
parameter_list|)
block|{
name|this
operator|.
name|destComposite
operator|=
name|destComposite
expr_stmt|;
block|}
specifier|public
name|Properties
name|getClientSettings
parameter_list|()
block|{
return|return
name|clientSettings
return|;
block|}
specifier|public
name|void
name|setClientSettings
parameter_list|(
name|Properties
name|clientSettings
parameter_list|)
block|{
name|this
operator|.
name|clientSettings
operator|=
name|clientSettings
expr_stmt|;
name|ReflectionUtil
operator|.
name|configureClass
argument_list|(
name|this
argument_list|,
name|clientSettings
argument_list|)
expr_stmt|;
block|}
specifier|public
name|Properties
name|getSettings
parameter_list|()
block|{
name|Properties
name|allSettings
init|=
operator|new
name|Properties
argument_list|(
name|clientSettings
argument_list|)
decl_stmt|;
name|allSettings
operator|.
name|putAll
argument_list|(
name|super
operator|.
name|getSettings
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|allSettings
return|;
block|}
specifier|public
name|void
name|setSettings
parameter_list|(
name|Properties
name|settings
parameter_list|)
block|{
name|super
operator|.
name|setSettings
argument_list|(
name|settings
argument_list|)
expr_stmt|;
name|ReflectionUtil
operator|.
name|configureClass
argument_list|(
name|this
argument_list|,
name|clientSettings
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setProperty
parameter_list|(
name|String
name|key
parameter_list|,
name|String
name|value
parameter_list|)
block|{
if|if
condition|(
name|key
operator|.
name|startsWith
argument_list|(
name|PREFIX_CONFIG_CLIENT
argument_list|)
condition|)
block|{
name|clientSettings
operator|.
name|setProperty
argument_list|(
name|key
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|super
operator|.
name|setProperty
argument_list|(
name|key
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

