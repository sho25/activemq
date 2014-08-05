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
name|tool
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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
name|ConnectionFactory
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
name|Session
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
name|apache
operator|.
name|activemq
operator|.
name|tool
operator|.
name|properties
operator|.
name|JmsClientProperties
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
name|AbstractJmsClient
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
name|AbstractJmsClient
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|QUEUE_SCHEME
init|=
literal|"queue://"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|TOPIC_SCHEME
init|=
literal|"topic://"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|DESTINATION_SEPARATOR
init|=
literal|","
decl_stmt|;
specifier|protected
name|ConnectionFactory
name|factory
decl_stmt|;
specifier|protected
name|Connection
name|jmsConnection
decl_stmt|;
specifier|protected
name|Session
name|jmsSession
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
decl_stmt|;
specifier|protected
name|String
name|clientName
init|=
literal|""
decl_stmt|;
specifier|private
name|int
name|internalTxCounter
init|=
literal|0
decl_stmt|;
specifier|public
name|AbstractJmsClient
parameter_list|(
name|ConnectionFactory
name|factory
parameter_list|)
block|{
name|this
operator|.
name|factory
operator|=
name|factory
expr_stmt|;
block|}
specifier|public
specifier|abstract
name|JmsClientProperties
name|getClient
parameter_list|()
function_decl|;
specifier|public
specifier|abstract
name|void
name|setClient
parameter_list|(
name|JmsClientProperties
name|client
parameter_list|)
function_decl|;
specifier|public
name|ConnectionFactory
name|getFactory
parameter_list|()
block|{
return|return
name|factory
return|;
block|}
specifier|public
name|void
name|setFactory
parameter_list|(
name|ConnectionFactory
name|factory
parameter_list|)
block|{
name|this
operator|.
name|factory
operator|=
name|factory
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
name|String
name|getClientName
parameter_list|()
block|{
return|return
name|clientName
return|;
block|}
specifier|public
name|void
name|setClientName
parameter_list|(
name|String
name|clientName
parameter_list|)
block|{
name|this
operator|.
name|clientName
operator|=
name|clientName
expr_stmt|;
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
name|factory
operator|.
name|createConnection
argument_list|()
expr_stmt|;
name|jmsConnection
operator|.
name|setClientID
argument_list|(
name|getClientName
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Creating JMS Connection: Provider="
operator|+
name|getClient
argument_list|()
operator|.
name|getJmsProvider
argument_list|()
operator|+
literal|", JMS Spec="
operator|+
name|getClient
argument_list|()
operator|.
name|getJmsVersion
argument_list|()
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
name|int
name|ackMode
decl_stmt|;
if|if
condition|(
name|getClient
argument_list|()
operator|.
name|getSessAckMode
argument_list|()
operator|.
name|equalsIgnoreCase
argument_list|(
name|JmsClientProperties
operator|.
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
name|getClient
argument_list|()
operator|.
name|getSessAckMode
argument_list|()
operator|.
name|equalsIgnoreCase
argument_list|(
name|JmsClientProperties
operator|.
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
name|getClient
argument_list|()
operator|.
name|getSessAckMode
argument_list|()
operator|.
name|equalsIgnoreCase
argument_list|(
name|JmsClientProperties
operator|.
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
name|getClient
argument_list|()
operator|.
name|getSessAckMode
argument_list|()
operator|.
name|equalsIgnoreCase
argument_list|(
name|JmsClientProperties
operator|.
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
name|getClient
argument_list|()
operator|.
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
name|createDestinations
parameter_list|(
name|int
name|destCount
parameter_list|)
throws|throws
name|JMSException
block|{
specifier|final
name|String
name|destName
init|=
name|getClient
argument_list|()
operator|.
name|getDestName
argument_list|()
decl_stmt|;
name|ArrayList
argument_list|<
name|Destination
argument_list|>
name|destinations
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
if|if
condition|(
name|destName
operator|.
name|contains
argument_list|(
name|DESTINATION_SEPARATOR
argument_list|)
condition|)
block|{
if|if
condition|(
name|getClient
argument_list|()
operator|.
name|isDestComposite
argument_list|()
operator|&&
operator|(
name|destCount
operator|==
literal|1
operator|)
condition|)
block|{
comment|// user was explicit about which destinations to make composite
name|String
index|[]
name|simpleNames
init|=
name|mapToSimpleNames
argument_list|(
name|destName
operator|.
name|split
argument_list|(
name|DESTINATION_SEPARATOR
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|joinedSimpleNames
init|=
name|join
argument_list|(
name|simpleNames
argument_list|,
name|DESTINATION_SEPARATOR
argument_list|)
decl_stmt|;
comment|// use the type of the 1st destination for the Destination instance
name|byte
name|destinationType
init|=
name|getDestinationType
argument_list|(
name|destName
argument_list|)
decl_stmt|;
name|destinations
operator|.
name|add
argument_list|(
name|createCompositeDestination
argument_list|(
name|destinationType
argument_list|,
name|joinedSimpleNames
argument_list|,
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"User requested multiple destinations, splitting: {}"
argument_list|,
name|destName
argument_list|)
expr_stmt|;
comment|// either composite with multiple destinations to be suffixed
comment|// or multiple non-composite destinations
name|String
index|[]
name|destinationNames
init|=
name|destName
operator|.
name|split
argument_list|(
name|DESTINATION_SEPARATOR
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|splitDestName
range|:
name|destinationNames
control|)
block|{
name|addDestinations
argument_list|(
name|destinations
argument_list|,
name|splitDestName
argument_list|,
name|destCount
argument_list|)
expr_stmt|;
block|}
block|}
block|}
else|else
block|{
name|addDestinations
argument_list|(
name|destinations
argument_list|,
name|destName
argument_list|,
name|destCount
argument_list|)
expr_stmt|;
block|}
return|return
name|destinations
operator|.
name|toArray
argument_list|(
operator|new
name|Destination
index|[]
block|{}
argument_list|)
return|;
block|}
specifier|private
name|String
name|join
parameter_list|(
name|String
index|[]
name|stings
parameter_list|,
name|String
name|separator
parameter_list|)
block|{
name|StringBuffer
name|sb
init|=
operator|new
name|StringBuffer
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
name|stings
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|i
operator|>
literal|0
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|separator
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
name|stings
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|private
name|void
name|addDestinations
parameter_list|(
name|List
argument_list|<
name|Destination
argument_list|>
name|destinations
parameter_list|,
name|String
name|destName
parameter_list|,
name|int
name|destCount
parameter_list|)
throws|throws
name|JMSException
block|{
name|boolean
name|destComposite
init|=
name|getClient
argument_list|()
operator|.
name|isDestComposite
argument_list|()
decl_stmt|;
if|if
condition|(
operator|(
name|destComposite
operator|)
operator|&&
operator|(
name|destCount
operator|>
literal|1
operator|)
condition|)
block|{
name|destinations
operator|.
name|add
argument_list|(
name|createCompositeDestination
argument_list|(
name|destName
argument_list|,
name|destCount
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|destCount
condition|;
name|i
operator|++
control|)
block|{
name|destinations
operator|.
name|add
argument_list|(
name|createDestination
argument_list|(
name|withDestinationSuffix
argument_list|(
name|destName
argument_list|,
name|i
argument_list|,
name|destCount
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|private
name|String
name|withDestinationSuffix
parameter_list|(
name|String
name|name
parameter_list|,
name|int
name|destIndex
parameter_list|,
name|int
name|destCount
parameter_list|)
block|{
return|return
operator|(
name|destCount
operator|==
literal|1
operator|)
condition|?
name|name
else|:
name|name
operator|+
literal|"."
operator|+
name|destIndex
return|;
block|}
specifier|protected
name|Destination
name|createCompositeDestination
parameter_list|(
name|String
name|destName
parameter_list|,
name|int
name|destCount
parameter_list|)
throws|throws
name|JMSException
block|{
return|return
name|createCompositeDestination
argument_list|(
name|getDestinationType
argument_list|(
name|destName
argument_list|)
argument_list|,
name|destName
argument_list|,
name|destCount
argument_list|)
return|;
block|}
specifier|protected
name|Destination
name|createCompositeDestination
parameter_list|(
name|byte
name|destinationType
parameter_list|,
name|String
name|destName
parameter_list|,
name|int
name|destCount
parameter_list|)
throws|throws
name|JMSException
block|{
name|String
name|simpleName
init|=
name|getSimpleName
argument_list|(
name|destName
argument_list|)
decl_stmt|;
name|String
name|compDestName
init|=
literal|""
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
name|destCount
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|i
operator|>
literal|0
condition|)
block|{
name|compDestName
operator|+=
literal|","
expr_stmt|;
block|}
name|compDestName
operator|+=
name|withDestinationSuffix
argument_list|(
name|simpleName
argument_list|,
name|i
argument_list|,
name|destCount
argument_list|)
expr_stmt|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Creating composite destination: {}"
argument_list|,
name|compDestName
argument_list|)
expr_stmt|;
return|return
operator|(
name|destinationType
operator|==
name|ActiveMQDestination
operator|.
name|TOPIC_TYPE
operator|)
condition|?
name|getSession
argument_list|()
operator|.
name|createTopic
argument_list|(
name|compDestName
argument_list|)
else|:
name|getSession
argument_list|()
operator|.
name|createQueue
argument_list|(
name|compDestName
argument_list|)
return|;
block|}
specifier|private
name|String
index|[]
name|mapToSimpleNames
parameter_list|(
name|String
index|[]
name|destNames
parameter_list|)
block|{
assert|assert
operator|(
name|destNames
operator|!=
literal|null
operator|)
assert|;
name|String
index|[]
name|simpleNames
init|=
operator|new
name|String
index|[
name|destNames
operator|.
name|length
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
name|destNames
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|simpleNames
index|[
name|i
index|]
operator|=
name|getSimpleName
argument_list|(
name|destNames
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
return|return
name|simpleNames
return|;
block|}
specifier|private
name|String
name|getSimpleName
parameter_list|(
name|String
name|destName
parameter_list|)
block|{
name|String
name|simpleName
decl_stmt|;
if|if
condition|(
name|destName
operator|.
name|startsWith
argument_list|(
name|QUEUE_SCHEME
argument_list|)
condition|)
block|{
name|simpleName
operator|=
name|destName
operator|.
name|substring
argument_list|(
name|QUEUE_SCHEME
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|destName
operator|.
name|startsWith
argument_list|(
name|TOPIC_SCHEME
argument_list|)
condition|)
block|{
name|simpleName
operator|=
name|destName
operator|.
name|substring
argument_list|(
name|TOPIC_SCHEME
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
name|destName
expr_stmt|;
block|}
return|return
name|simpleName
return|;
block|}
specifier|private
name|byte
name|getDestinationType
parameter_list|(
name|String
name|destName
parameter_list|)
block|{
assert|assert
operator|(
name|destName
operator|!=
literal|null
operator|)
assert|;
if|if
condition|(
name|destName
operator|.
name|startsWith
argument_list|(
name|QUEUE_SCHEME
argument_list|)
condition|)
block|{
return|return
name|ActiveMQDestination
operator|.
name|QUEUE_TYPE
return|;
block|}
else|else
block|{
return|return
name|ActiveMQDestination
operator|.
name|TOPIC_TYPE
return|;
block|}
block|}
specifier|protected
name|Destination
name|createDestination
parameter_list|(
name|String
name|destName
parameter_list|)
throws|throws
name|JMSException
block|{
name|String
name|simpleName
init|=
name|getSimpleName
argument_list|(
name|destName
argument_list|)
decl_stmt|;
if|if
condition|(
name|getDestinationType
argument_list|(
name|destName
argument_list|)
operator|==
name|ActiveMQDestination
operator|.
name|QUEUE_TYPE
condition|)
block|{
return|return
name|getSession
argument_list|()
operator|.
name|createQueue
argument_list|(
name|simpleName
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
name|simpleName
argument_list|)
return|;
block|}
block|}
comment|/**      * Helper method that checks if session is      * transacted and whether to commit the tx based on commitAfterXMsgs      * property.      *      * @return true if transaction was committed.      * @throws JMSException in case the call to JMS Session.commit() fails.      */
specifier|public
name|boolean
name|commitTxIfNecessary
parameter_list|()
throws|throws
name|JMSException
block|{
name|internalTxCounter
operator|++
expr_stmt|;
if|if
condition|(
name|getClient
argument_list|()
operator|.
name|isSessTransacted
argument_list|()
condition|)
block|{
if|if
condition|(
operator|(
name|internalTxCounter
operator|%
name|getClient
argument_list|()
operator|.
name|getCommitAfterXMsgs
argument_list|()
operator|)
operator|==
literal|0
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Committing transaction."
argument_list|)
expr_stmt|;
name|internalTxCounter
operator|=
literal|0
expr_stmt|;
name|getSession
argument_list|()
operator|.
name|commit
argument_list|()
expr_stmt|;
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
block|}
end_class

end_unit

