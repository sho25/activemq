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
name|web
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
name|ActiveMQQueue
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
name|TextMessage
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|ServletConfig
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|ServletException
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
name|HttpServlet
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
name|HttpSession
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|BufferedReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
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

begin_comment
comment|/**  * A useful base class for any JMS related servlet;  * there are various ways to map JMS operations to web requests  * so we put most of the common behaviour in a reusable base class.  *  * This servlet can be configured with the following init paramters<dl>  *<dt>topic</dt><dd>Set to 'true' if the servle should default to using topics rather than channels</dd>  *<dt>destination</dt><dd>The default destination to use if one is not specifiied</dd>  *<dt></dt><dd></dd>  *</dl>  * @version $Revision: 1.1.1.1 $  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|MessageServletSupport
extends|extends
name|HttpServlet
block|{
specifier|private
name|boolean
name|defaultTopicFlag
init|=
literal|true
decl_stmt|;
specifier|private
name|Destination
name|defaultDestination
decl_stmt|;
specifier|private
name|String
name|destinationParameter
init|=
literal|"destination"
decl_stmt|;
specifier|private
name|String
name|topicParameter
init|=
literal|"topic"
decl_stmt|;
specifier|private
name|String
name|bodyParameter
init|=
literal|"body"
decl_stmt|;
specifier|public
name|void
name|init
parameter_list|(
name|ServletConfig
name|servletConfig
parameter_list|)
throws|throws
name|ServletException
block|{
name|super
operator|.
name|init
argument_list|(
name|servletConfig
argument_list|)
expr_stmt|;
name|String
name|name
init|=
name|servletConfig
operator|.
name|getInitParameter
argument_list|(
literal|"topic"
argument_list|)
decl_stmt|;
if|if
condition|(
name|name
operator|!=
literal|null
condition|)
block|{
name|defaultTopicFlag
operator|=
name|asBoolean
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
name|log
argument_list|(
literal|"Defaulting to use topics: "
operator|+
name|defaultTopicFlag
argument_list|)
expr_stmt|;
name|name
operator|=
name|servletConfig
operator|.
name|getInitParameter
argument_list|(
literal|"destination"
argument_list|)
expr_stmt|;
if|if
condition|(
name|name
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|defaultTopicFlag
condition|)
block|{
name|defaultDestination
operator|=
operator|new
name|ActiveMQTopic
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|defaultDestination
operator|=
operator|new
name|ActiveMQQueue
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
block|}
comment|// lets check to see if there's a connection factory set
name|WebClient
operator|.
name|initContext
argument_list|(
name|getServletContext
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|WebClient
name|createWebClient
parameter_list|(
name|HttpServletRequest
name|request
parameter_list|)
block|{
return|return
operator|new
name|WebClient
argument_list|()
return|;
block|}
specifier|public
specifier|static
name|boolean
name|asBoolean
parameter_list|(
name|String
name|param
parameter_list|)
block|{
return|return
name|asBoolean
argument_list|(
name|param
argument_list|,
literal|false
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|boolean
name|asBoolean
parameter_list|(
name|String
name|param
parameter_list|,
name|boolean
name|defaultValue
parameter_list|)
block|{
if|if
condition|(
name|param
operator|==
literal|null
condition|)
block|{
return|return
name|defaultValue
return|;
block|}
else|else
block|{
return|return
name|param
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"true"
argument_list|)
return|;
block|}
block|}
comment|/**      * Helper method to get the client for the current session      *      * @param request is the current HTTP request      * @return the current client or a newly creates      */
specifier|protected
name|WebClient
name|getWebClient
parameter_list|(
name|HttpServletRequest
name|request
parameter_list|)
block|{
name|HttpSession
name|session
init|=
name|request
operator|.
name|getSession
argument_list|(
literal|true
argument_list|)
decl_stmt|;
name|WebClient
name|client
init|=
name|WebClient
operator|.
name|getWebClient
argument_list|(
name|session
argument_list|)
decl_stmt|;
if|if
condition|(
name|client
operator|==
literal|null
operator|||
name|client
operator|.
name|isClosed
argument_list|()
condition|)
block|{
name|client
operator|=
name|createWebClient
argument_list|(
name|request
argument_list|)
expr_stmt|;
name|session
operator|.
name|setAttribute
argument_list|(
name|WebClient
operator|.
name|webClientAttribute
argument_list|,
name|client
argument_list|)
expr_stmt|;
block|}
return|return
name|client
return|;
block|}
specifier|protected
name|void
name|appendParametersToMessage
parameter_list|(
name|HttpServletRequest
name|request
parameter_list|,
name|TextMessage
name|message
parameter_list|)
throws|throws
name|JMSException
block|{
name|Map
name|parameterMap
init|=
name|request
operator|.
name|getParameterMap
argument_list|()
decl_stmt|;
if|if
condition|(
name|parameterMap
operator|==
literal|null
condition|)
block|{
return|return;
block|}
name|Map
name|parameters
init|=
operator|new
name|HashMap
argument_list|(
name|parameterMap
argument_list|)
decl_stmt|;
name|String
name|correlationID
init|=
name|asString
argument_list|(
name|parameters
operator|.
name|remove
argument_list|(
literal|"JMSCorrelationID"
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|correlationID
operator|!=
literal|null
condition|)
block|{
name|message
operator|.
name|setJMSCorrelationID
argument_list|(
name|correlationID
argument_list|)
expr_stmt|;
block|}
name|Long
name|expiration
init|=
name|asLong
argument_list|(
name|parameters
operator|.
name|remove
argument_list|(
literal|"JMSExpiration"
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|expiration
operator|!=
literal|null
condition|)
block|{
name|message
operator|.
name|setJMSExpiration
argument_list|(
name|expiration
operator|.
name|longValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|Integer
name|priority
init|=
name|asInteger
argument_list|(
name|parameters
operator|.
name|remove
argument_list|(
literal|"JMSPriority"
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|expiration
operator|!=
literal|null
condition|)
block|{
name|message
operator|.
name|setJMSPriority
argument_list|(
name|priority
operator|.
name|intValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|Destination
name|replyTo
init|=
name|asDestination
argument_list|(
name|parameters
operator|.
name|remove
argument_list|(
literal|"JMSReplyTo"
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|replyTo
operator|!=
literal|null
condition|)
block|{
name|message
operator|.
name|setJMSReplyTo
argument_list|(
name|replyTo
argument_list|)
expr_stmt|;
block|}
name|String
name|type
init|=
operator|(
name|String
operator|)
name|asString
argument_list|(
name|parameters
operator|.
name|remove
argument_list|(
literal|"JMSType"
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|correlationID
operator|!=
literal|null
condition|)
block|{
name|message
operator|.
name|setJMSType
argument_list|(
name|type
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|Iterator
name|iter
init|=
name|parameters
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
if|if
condition|(
operator|!
name|destinationParameter
operator|.
name|equals
argument_list|(
name|name
argument_list|)
operator|&&
operator|!
name|topicParameter
operator|.
name|equals
argument_list|(
name|name
argument_list|)
operator|&&
operator|!
name|bodyParameter
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
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
name|value
operator|instanceof
name|Object
index|[]
condition|)
block|{
name|Object
index|[]
name|array
init|=
operator|(
name|Object
index|[]
operator|)
name|value
decl_stmt|;
if|if
condition|(
name|array
operator|.
name|length
operator|==
literal|1
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
name|log
argument_list|(
literal|"Can't use property: "
operator|+
name|name
operator|+
literal|" which is of type: "
operator|+
name|value
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|" value"
argument_list|)
expr_stmt|;
name|value
operator|=
literal|null
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|,
name|size
init|=
name|array
operator|.
name|length
init|;
name|i
operator|<
name|size
condition|;
name|i
operator|++
control|)
block|{
name|log
argument_list|(
literal|"value["
operator|+
name|i
operator|+
literal|"] = "
operator|+
name|array
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
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
specifier|protected
name|Destination
name|asDestination
parameter_list|(
name|Object
name|value
parameter_list|)
block|{
if|if
condition|(
name|value
operator|instanceof
name|Destination
condition|)
block|{
return|return
operator|(
name|Destination
operator|)
name|value
return|;
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
operator|(
name|String
operator|)
name|value
decl_stmt|;
return|return
name|ActiveMQDestination
operator|.
name|createDestination
argument_list|(
name|text
argument_list|,
name|ActiveMQDestination
operator|.
name|QUEUE_TYPE
argument_list|)
return|;
block|}
if|if
condition|(
name|value
operator|instanceof
name|String
index|[]
condition|)
block|{
name|String
name|text
init|=
operator|(
operator|(
name|String
index|[]
operator|)
name|value
operator|)
index|[
literal|0
index|]
decl_stmt|;
return|return
name|ActiveMQDestination
operator|.
name|createDestination
argument_list|(
name|text
argument_list|,
name|ActiveMQDestination
operator|.
name|QUEUE_TYPE
argument_list|)
return|;
block|}
return|return
literal|null
return|;
block|}
specifier|protected
name|Integer
name|asInteger
parameter_list|(
name|Object
name|value
parameter_list|)
block|{
if|if
condition|(
name|value
operator|instanceof
name|Integer
condition|)
block|{
return|return
operator|(
name|Integer
operator|)
name|value
return|;
block|}
if|if
condition|(
name|value
operator|instanceof
name|String
condition|)
block|{
return|return
name|Integer
operator|.
name|valueOf
argument_list|(
operator|(
name|String
operator|)
name|value
argument_list|)
return|;
block|}
if|if
condition|(
name|value
operator|instanceof
name|String
index|[]
condition|)
block|{
return|return
name|Integer
operator|.
name|valueOf
argument_list|(
operator|(
operator|(
name|String
index|[]
operator|)
name|value
operator|)
index|[
literal|0
index|]
argument_list|)
return|;
block|}
return|return
literal|null
return|;
block|}
specifier|protected
name|Long
name|asLong
parameter_list|(
name|Object
name|value
parameter_list|)
block|{
if|if
condition|(
name|value
operator|instanceof
name|Long
condition|)
block|{
return|return
operator|(
name|Long
operator|)
name|value
return|;
block|}
if|if
condition|(
name|value
operator|instanceof
name|String
condition|)
block|{
return|return
name|Long
operator|.
name|valueOf
argument_list|(
operator|(
name|String
operator|)
name|value
argument_list|)
return|;
block|}
if|if
condition|(
name|value
operator|instanceof
name|String
index|[]
condition|)
block|{
return|return
name|Long
operator|.
name|valueOf
argument_list|(
operator|(
operator|(
name|String
index|[]
operator|)
name|value
operator|)
index|[
literal|0
index|]
argument_list|)
return|;
block|}
return|return
literal|null
return|;
block|}
specifier|protected
name|String
name|asString
parameter_list|(
name|Object
name|value
parameter_list|)
block|{
if|if
condition|(
name|value
operator|instanceof
name|String
index|[]
condition|)
block|{
return|return
operator|(
operator|(
name|String
index|[]
operator|)
name|value
operator|)
index|[
literal|0
index|]
return|;
block|}
if|if
condition|(
name|value
operator|!=
literal|null
condition|)
block|{
return|return
name|value
operator|.
name|toString
argument_list|()
return|;
block|}
return|return
literal|null
return|;
block|}
comment|/**      * @return the destination to use for the current request      */
specifier|protected
name|Destination
name|getDestination
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
name|String
name|destinationName
init|=
name|request
operator|.
name|getParameter
argument_list|(
name|destinationParameter
argument_list|)
decl_stmt|;
if|if
condition|(
name|destinationName
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|defaultDestination
operator|==
literal|null
condition|)
block|{
return|return
name|getDestinationFromURI
argument_list|(
name|client
argument_list|,
name|request
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|defaultDestination
return|;
block|}
block|}
return|return
name|getDestination
argument_list|(
name|client
argument_list|,
name|request
argument_list|,
name|destinationName
argument_list|)
return|;
block|}
comment|/**      * @return the destination to use for the current request using the relative URI from      *         where this servlet was invoked as the destination name      */
specifier|protected
name|Destination
name|getDestinationFromURI
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
name|String
name|uri
init|=
name|request
operator|.
name|getPathInfo
argument_list|()
decl_stmt|;
if|if
condition|(
name|uri
operator|==
literal|null
condition|)
return|return
literal|null
return|;
comment|// replace URI separator with JMS destination separator
if|if
condition|(
name|uri
operator|.
name|startsWith
argument_list|(
literal|"/"
argument_list|)
condition|)
block|{
name|uri
operator|=
name|uri
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
expr_stmt|;
if|if
condition|(
name|uri
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
return|return
literal|null
return|;
block|}
name|uri
operator|=
name|uri
operator|.
name|replace
argument_list|(
literal|'/'
argument_list|,
literal|'.'
argument_list|)
expr_stmt|;
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"destination uri="
operator|+
name|uri
argument_list|)
expr_stmt|;
return|return
name|getDestination
argument_list|(
name|client
argument_list|,
name|request
argument_list|,
name|uri
argument_list|)
return|;
block|}
comment|/**      * @return the Destination object for the given destination name      */
specifier|protected
name|Destination
name|getDestination
parameter_list|(
name|WebClient
name|client
parameter_list|,
name|HttpServletRequest
name|request
parameter_list|,
name|String
name|destinationName
parameter_list|)
throws|throws
name|JMSException
block|{
comment|// TODO cache destinations ???
name|boolean
name|is_topic
init|=
name|defaultTopicFlag
decl_stmt|;
if|if
condition|(
name|destinationName
operator|.
name|startsWith
argument_list|(
literal|"topic://"
argument_list|)
condition|)
block|{
name|is_topic
operator|=
literal|true
expr_stmt|;
name|destinationName
operator|=
name|destinationName
operator|.
name|substring
argument_list|(
literal|8
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|destinationName
operator|.
name|startsWith
argument_list|(
literal|"channel://"
argument_list|)
condition|)
block|{
name|is_topic
operator|=
literal|true
expr_stmt|;
name|destinationName
operator|=
name|destinationName
operator|.
name|substring
argument_list|(
literal|10
argument_list|)
expr_stmt|;
block|}
else|else
name|is_topic
operator|=
name|isTopic
argument_list|(
name|request
argument_list|)
expr_stmt|;
if|if
condition|(
name|is_topic
condition|)
block|{
return|return
name|client
operator|.
name|getSession
argument_list|()
operator|.
name|createTopic
argument_list|(
name|destinationName
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|client
operator|.
name|getSession
argument_list|()
operator|.
name|createQueue
argument_list|(
name|destinationName
argument_list|)
return|;
block|}
block|}
comment|/**      * @return true if the current request is for a topic destination, else false if its for a queue      */
specifier|protected
name|boolean
name|isTopic
parameter_list|(
name|HttpServletRequest
name|request
parameter_list|)
block|{
name|boolean
name|aTopic
init|=
name|defaultTopicFlag
decl_stmt|;
name|String
name|aTopicText
init|=
name|request
operator|.
name|getParameter
argument_list|(
name|topicParameter
argument_list|)
decl_stmt|;
if|if
condition|(
name|aTopicText
operator|!=
literal|null
condition|)
block|{
name|aTopic
operator|=
name|asBoolean
argument_list|(
name|aTopicText
argument_list|)
expr_stmt|;
block|}
return|return
name|aTopic
return|;
block|}
specifier|protected
name|long
name|asLong
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|Long
operator|.
name|parseLong
argument_list|(
name|name
argument_list|)
return|;
block|}
comment|/**      * @return the text that was posted to the servlet which is used as the body      *         of the message to be sent      */
specifier|protected
name|String
name|getPostedMessageBody
parameter_list|(
name|HttpServletRequest
name|request
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|answer
init|=
name|request
operator|.
name|getParameter
argument_list|(
name|bodyParameter
argument_list|)
decl_stmt|;
if|if
condition|(
name|answer
operator|==
literal|null
operator|&&
literal|"text/xml"
operator|.
name|equals
argument_list|(
name|request
operator|.
name|getContentType
argument_list|()
argument_list|)
condition|)
block|{
comment|// lets read the message body instead
name|BufferedReader
name|reader
init|=
name|request
operator|.
name|getReader
argument_list|()
decl_stmt|;
name|StringBuffer
name|buffer
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|String
name|line
init|=
name|reader
operator|.
name|readLine
argument_list|()
decl_stmt|;
if|if
condition|(
name|line
operator|==
literal|null
condition|)
block|{
break|break;
block|}
name|buffer
operator|.
name|append
argument_list|(
name|line
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
block|}
return|return
name|buffer
operator|.
name|toString
argument_list|()
return|;
block|}
return|return
name|answer
return|;
block|}
block|}
end_class

end_unit

