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
name|web
operator|.
name|controller
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
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
name|servlet
operator|.
name|http
operator|.
name|HttpServletRequest
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|http
operator|.
name|HttpServletResponse
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
name|web
operator|.
name|BrokerFacade
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
name|web
operator|.
name|DestinationFacade
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
name|web
operator|.
name|WebClient
import|;
end_import

begin_import
import|import
name|org
operator|.
name|springframework
operator|.
name|web
operator|.
name|servlet
operator|.
name|ModelAndView
import|;
end_import

begin_import
import|import
name|org
operator|.
name|springframework
operator|.
name|web
operator|.
name|servlet
operator|.
name|mvc
operator|.
name|Controller
import|;
end_import

begin_comment
comment|/**  * Sends a message  *   * @version $Revision$  */
end_comment

begin_class
specifier|public
class|class
name|SendMessage
extends|extends
name|DestinationFacade
implements|implements
name|Controller
block|{
specifier|private
name|String
name|jmsText
decl_stmt|;
specifier|private
name|boolean
name|jmsPersistent
decl_stmt|;
specifier|private
name|int
name|jmsPriority
decl_stmt|;
specifier|private
name|int
name|jmsTimeToLive
init|=
operator|-
literal|1
decl_stmt|;
specifier|private
name|String
name|jmsCorrelationID
decl_stmt|;
specifier|private
name|String
name|jmsReplyTo
decl_stmt|;
specifier|private
name|String
name|jmsType
decl_stmt|;
specifier|private
name|int
name|jmsMessageCount
init|=
literal|1
decl_stmt|;
specifier|private
name|String
name|jmsMessageCountHeader
init|=
literal|"JMSXMessageNumber"
decl_stmt|;
specifier|private
name|boolean
name|redirectToBrowse
decl_stmt|;
specifier|public
name|SendMessage
parameter_list|(
name|BrokerFacade
name|brokerFacade
parameter_list|)
block|{
name|super
argument_list|(
name|brokerFacade
argument_list|)
expr_stmt|;
block|}
specifier|public
name|ModelAndView
name|handleRequest
parameter_list|(
name|HttpServletRequest
name|request
parameter_list|,
name|HttpServletResponse
name|response
parameter_list|)
throws|throws
name|Exception
block|{
name|WebClient
name|client
init|=
name|WebClient
operator|.
name|getWebClient
argument_list|(
name|request
argument_list|)
decl_stmt|;
name|ActiveMQDestination
name|dest
init|=
name|createDestination
argument_list|()
decl_stmt|;
name|sendMessages
argument_list|(
name|request
argument_list|,
name|client
argument_list|,
name|dest
argument_list|)
expr_stmt|;
if|if
condition|(
name|redirectToBrowse
condition|)
block|{
if|if
condition|(
name|isQueue
argument_list|()
condition|)
block|{
return|return
operator|new
name|ModelAndView
argument_list|(
literal|"redirect:browse.jsp?destination="
operator|+
name|getJMSDestination
argument_list|()
argument_list|)
return|;
block|}
block|}
return|return
name|redirectToBrowseView
argument_list|()
return|;
block|}
specifier|protected
name|void
name|sendMessages
parameter_list|(
name|HttpServletRequest
name|request
parameter_list|,
name|WebClient
name|client
parameter_list|,
name|ActiveMQDestination
name|dest
parameter_list|)
throws|throws
name|JMSException
block|{
if|if
condition|(
name|jmsMessageCount
operator|<=
literal|1
condition|)
block|{
name|jmsMessageCount
operator|=
literal|1
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|jmsMessageCount
condition|;
name|i
operator|++
control|)
block|{
name|Message
name|message
init|=
name|createMessage
argument_list|(
name|client
argument_list|,
name|request
argument_list|)
decl_stmt|;
name|appendHeaders
argument_list|(
name|message
argument_list|,
name|request
argument_list|)
expr_stmt|;
if|if
condition|(
name|jmsMessageCount
operator|>
literal|1
condition|)
block|{
name|message
operator|.
name|setIntProperty
argument_list|(
name|jmsMessageCountHeader
argument_list|,
name|i
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
name|client
operator|.
name|send
argument_list|(
name|dest
argument_list|,
name|message
argument_list|,
name|jmsPersistent
argument_list|,
name|jmsPriority
argument_list|,
name|jmsTimeToLive
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Properties
comment|// -------------------------------------------------------------------------
specifier|public
name|String
name|getJMSCorrelationID
parameter_list|()
block|{
return|return
name|jmsCorrelationID
return|;
block|}
specifier|public
name|void
name|setJMSCorrelationID
parameter_list|(
name|String
name|correlationID
parameter_list|)
block|{
name|jmsCorrelationID
operator|=
name|correlationID
expr_stmt|;
block|}
specifier|public
name|String
name|getJMSReplyTo
parameter_list|()
block|{
return|return
name|jmsReplyTo
return|;
block|}
specifier|public
name|void
name|setJMSReplyTo
parameter_list|(
name|String
name|replyTo
parameter_list|)
block|{
name|jmsReplyTo
operator|=
name|replyTo
expr_stmt|;
block|}
specifier|public
name|String
name|getJMSType
parameter_list|()
block|{
return|return
name|jmsType
return|;
block|}
specifier|public
name|void
name|setJMSType
parameter_list|(
name|String
name|type
parameter_list|)
block|{
name|jmsType
operator|=
name|type
expr_stmt|;
block|}
specifier|public
name|boolean
name|isJMSPersistent
parameter_list|()
block|{
return|return
name|jmsPersistent
return|;
block|}
specifier|public
name|void
name|setJMSPersistent
parameter_list|(
name|boolean
name|persistent
parameter_list|)
block|{
name|this
operator|.
name|jmsPersistent
operator|=
name|persistent
expr_stmt|;
block|}
specifier|public
name|int
name|getJMSPriority
parameter_list|()
block|{
return|return
name|jmsPriority
return|;
block|}
specifier|public
name|void
name|setJMSPriority
parameter_list|(
name|int
name|priority
parameter_list|)
block|{
name|this
operator|.
name|jmsPriority
operator|=
name|priority
expr_stmt|;
block|}
specifier|public
name|String
name|getJMSText
parameter_list|()
block|{
return|return
name|jmsText
return|;
block|}
specifier|public
name|void
name|setJMSText
parameter_list|(
name|String
name|text
parameter_list|)
block|{
name|this
operator|.
name|jmsText
operator|=
name|text
expr_stmt|;
block|}
specifier|public
name|int
name|getJMSTimeToLive
parameter_list|()
block|{
return|return
name|jmsTimeToLive
return|;
block|}
specifier|public
name|void
name|setJMSTimeToLive
parameter_list|(
name|int
name|timeToLive
parameter_list|)
block|{
name|this
operator|.
name|jmsTimeToLive
operator|=
name|timeToLive
expr_stmt|;
block|}
specifier|public
name|int
name|getJMSMessageCount
parameter_list|()
block|{
return|return
name|jmsMessageCount
return|;
block|}
specifier|public
name|void
name|setJMSMessageCount
parameter_list|(
name|int
name|copies
parameter_list|)
block|{
name|jmsMessageCount
operator|=
name|copies
expr_stmt|;
block|}
specifier|public
name|String
name|getJMSMessageCountHeader
parameter_list|()
block|{
return|return
name|jmsMessageCountHeader
return|;
block|}
specifier|public
name|void
name|setJMSMessageCountHeader
parameter_list|(
name|String
name|messageCountHeader
parameter_list|)
block|{
name|jmsMessageCountHeader
operator|=
name|messageCountHeader
expr_stmt|;
block|}
comment|// Implementation methods
comment|// -------------------------------------------------------------------------
specifier|protected
name|Message
name|createMessage
parameter_list|(
name|WebClient
name|client
parameter_list|,
name|HttpServletRequest
name|request
parameter_list|)
throws|throws
name|JMSException
block|{
if|if
condition|(
name|jmsText
operator|!=
literal|null
condition|)
block|{
return|return
name|client
operator|.
name|getSession
argument_list|()
operator|.
name|createTextMessage
argument_list|(
name|jmsText
argument_list|)
return|;
block|}
comment|// TODO create Bytes message from request body...
return|return
name|client
operator|.
name|getSession
argument_list|()
operator|.
name|createMessage
argument_list|()
return|;
block|}
specifier|protected
name|void
name|appendHeaders
parameter_list|(
name|Message
name|message
parameter_list|,
name|HttpServletRequest
name|request
parameter_list|)
throws|throws
name|JMSException
block|{
name|message
operator|.
name|setJMSCorrelationID
argument_list|(
name|jmsCorrelationID
argument_list|)
expr_stmt|;
if|if
condition|(
name|jmsReplyTo
operator|!=
literal|null
operator|&&
name|jmsReplyTo
operator|.
name|trim
argument_list|()
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|message
operator|.
name|setJMSReplyTo
argument_list|(
name|ActiveMQDestination
operator|.
name|createDestination
argument_list|(
name|jmsReplyTo
argument_list|,
name|ActiveMQDestination
operator|.
name|QUEUE_TYPE
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|message
operator|.
name|setJMSType
argument_list|(
name|jmsType
argument_list|)
expr_stmt|;
comment|// now lets add all of the parameters
name|Map
name|map
init|=
name|request
operator|.
name|getParameterMap
argument_list|()
decl_stmt|;
if|if
condition|(
name|map
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|Iterator
name|iter
init|=
name|map
operator|.
name|entrySet
argument_list|()
operator|.
name|iterator
argument_list|()
init|;
name|iter
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|Map
operator|.
name|Entry
name|entry
init|=
operator|(
name|Map
operator|.
name|Entry
operator|)
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
name|String
name|name
init|=
operator|(
name|String
operator|)
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|Object
name|value
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|isValidPropertyName
argument_list|(
name|name
argument_list|)
condition|)
block|{
if|if
condition|(
name|value
operator|instanceof
name|String
index|[]
condition|)
block|{
name|String
index|[]
name|array
init|=
operator|(
name|String
index|[]
operator|)
name|value
decl_stmt|;
if|if
condition|(
name|array
operator|.
name|length
operator|>
literal|0
condition|)
block|{
name|value
operator|=
name|array
index|[
literal|0
index|]
expr_stmt|;
block|}
else|else
block|{
name|value
operator|=
literal|null
expr_stmt|;
block|}
block|}
if|if
condition|(
name|value
operator|instanceof
name|String
condition|)
block|{
name|String
name|text
init|=
name|value
operator|.
name|toString
argument_list|()
operator|.
name|trim
argument_list|()
decl_stmt|;
if|if
condition|(
name|text
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
name|value
operator|=
literal|null
expr_stmt|;
block|}
else|else
block|{
name|value
operator|=
name|text
expr_stmt|;
block|}
block|}
if|if
condition|(
name|value
operator|!=
literal|null
condition|)
block|{
name|message
operator|.
name|setObjectProperty
argument_list|(
name|name
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
specifier|protected
name|boolean
name|isValidPropertyName
parameter_list|(
name|String
name|name
parameter_list|)
block|{
comment|// allow JMSX extensions or non JMS properties
return|return
name|name
operator|.
name|startsWith
argument_list|(
literal|"JMSX"
argument_list|)
operator|||
operator|!
name|name
operator|.
name|startsWith
argument_list|(
literal|"JMS"
argument_list|)
return|;
block|}
block|}
end_class

end_unit

