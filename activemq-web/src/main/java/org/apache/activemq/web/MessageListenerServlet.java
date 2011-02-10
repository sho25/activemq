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
package|;
end_package

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
name|io
operator|.
name|PrintWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|StringWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|*
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
name|Message
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|MessageConsumer
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
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|MessageAvailableConsumer
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
name|MessageAvailableListener
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

begin_import
import|import
name|org
operator|.
name|eclipse
operator|.
name|jetty
operator|.
name|continuation
operator|.
name|Continuation
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eclipse
operator|.
name|jetty
operator|.
name|continuation
operator|.
name|ContinuationSupport
import|;
end_import

begin_comment
comment|/**  * A servlet for sending and receiving messages to/from JMS destinations using  * HTTP POST for sending and HTTP GET for receiving.<p/> You can specify the  * destination and whether it is a topic or queue via configuration details on  * the servlet or as request parameters.<p/> For reading messages you can  * specify a readTimeout parameter to determine how long the servlet should  * block for. The servlet can be configured with the following init parameters:  *<dl>  *<dt>defaultReadTimeout</dt>  *<dd>The default time in ms to wait for messages. May be overridden by a  * request using the 'timeout' parameter</dd>  *<dt>maximumReadTimeout</dt>  *<dd>The maximum value a request may specify for the 'timeout' parameter</dd>  *<dt>maximumMessages</dt>  *<dd>maximum messages to send per response</dd>  *<dt></dt>  *<dd></dd>  *</dl>  *   * @version $Revision: 1.1.1.1 $  */
end_comment

begin_class
specifier|public
class|class
name|MessageListenerServlet
extends|extends
name|MessageServletSupport
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
name|MessageListenerServlet
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|String
name|readTimeoutParameter
init|=
literal|"timeout"
decl_stmt|;
specifier|private
name|long
name|defaultReadTimeout
init|=
operator|-
literal|1
decl_stmt|;
specifier|private
name|long
name|maximumReadTimeout
init|=
literal|25000
decl_stmt|;
specifier|private
name|int
name|maximumMessages
init|=
literal|100
decl_stmt|;
specifier|private
name|Timer
name|clientCleanupTimer
init|=
operator|new
name|Timer
argument_list|()
decl_stmt|;
specifier|private
name|HashMap
argument_list|<
name|String
argument_list|,
name|AjaxWebClient
argument_list|>
name|ajaxWebClients
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|AjaxWebClient
argument_list|>
argument_list|()
decl_stmt|;
specifier|public
name|void
name|init
parameter_list|()
throws|throws
name|ServletException
block|{
name|ServletConfig
name|servletConfig
init|=
name|getServletConfig
argument_list|()
decl_stmt|;
name|String
name|name
init|=
name|servletConfig
operator|.
name|getInitParameter
argument_list|(
literal|"defaultReadTimeout"
argument_list|)
decl_stmt|;
if|if
condition|(
name|name
operator|!=
literal|null
condition|)
block|{
name|defaultReadTimeout
operator|=
name|asLong
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
name|name
operator|=
name|servletConfig
operator|.
name|getInitParameter
argument_list|(
literal|"maximumReadTimeout"
argument_list|)
expr_stmt|;
if|if
condition|(
name|name
operator|!=
literal|null
condition|)
block|{
name|maximumReadTimeout
operator|=
name|asLong
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
name|name
operator|=
name|servletConfig
operator|.
name|getInitParameter
argument_list|(
literal|"maximumMessages"
argument_list|)
expr_stmt|;
if|if
condition|(
name|name
operator|!=
literal|null
condition|)
block|{
name|maximumMessages
operator|=
operator|(
name|int
operator|)
name|asLong
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
name|clientCleanupTimer
operator|.
name|schedule
argument_list|(
operator|new
name|ClientCleaner
argument_list|()
argument_list|,
literal|5000
argument_list|,
literal|60000
argument_list|)
expr_stmt|;
block|}
comment|/**      * Sends a message to a destination or manage subscriptions. If the the      * content type of the POST is      *<code>application/x-www-form-urlencoded</code>, then the form      * parameters "destination", "message" and "type" are used to pass a message      * or a subscription. If multiple messages or subscriptions are passed in a      * single post, then additional parameters are shortened to "dN", "mN" and      * "tN" where N is an index starting from 1. The type is either "send",      * "listen" or "unlisten". For send types, the message is the text of the      * TextMessage, otherwise it is the ID to be used for the subscription. If      * the content type is not<code>application/x-www-form-urlencoded</code>,      * then the body of the post is sent as the message to a destination that is      * derived from a query parameter, the URL or the default destination.      *       * @param request      * @param response      * @throws ServletException      * @throws IOException      */
specifier|protected
name|void
name|doPost
parameter_list|(
name|HttpServletRequest
name|request
parameter_list|,
name|HttpServletResponse
name|response
parameter_list|)
throws|throws
name|ServletException
throws|,
name|IOException
block|{
comment|// lets turn the HTTP post into a JMS Message
name|AjaxWebClient
name|client
init|=
name|getAjaxWebClient
argument_list|(
name|request
argument_list|)
decl_stmt|;
name|String
name|messageIds
init|=
literal|""
decl_stmt|;
synchronized|synchronized
init|(
name|client
init|)
block|{
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"POST client="
operator|+
name|client
operator|+
literal|" session="
operator|+
name|request
operator|.
name|getSession
argument_list|()
operator|.
name|getId
argument_list|()
operator|+
literal|" clientId="
operator|+
name|request
operator|.
name|getParameter
argument_list|(
literal|"clientId"
argument_list|)
operator|+
literal|" info="
operator|+
name|request
operator|.
name|getPathInfo
argument_list|()
operator|+
literal|" contentType="
operator|+
name|request
operator|.
name|getContentType
argument_list|()
argument_list|)
expr_stmt|;
comment|// dump(request.getParameterMap());
block|}
name|int
name|messages
init|=
literal|0
decl_stmt|;
comment|// loop until no more messages
while|while
condition|(
literal|true
condition|)
block|{
comment|// Get the message parameters. Multiple messages are encoded
comment|// with more compact parameter names.
name|String
name|destinationName
init|=
name|request
operator|.
name|getParameter
argument_list|(
name|messages
operator|==
literal|0
condition|?
literal|"destination"
else|:
operator|(
literal|"d"
operator|+
name|messages
operator|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|destinationName
operator|==
literal|null
condition|)
block|{
name|destinationName
operator|=
name|request
operator|.
name|getHeader
argument_list|(
literal|"destination"
argument_list|)
expr_stmt|;
block|}
name|String
name|message
init|=
name|request
operator|.
name|getParameter
argument_list|(
name|messages
operator|==
literal|0
condition|?
literal|"message"
else|:
operator|(
literal|"m"
operator|+
name|messages
operator|)
argument_list|)
decl_stmt|;
name|String
name|type
init|=
name|request
operator|.
name|getParameter
argument_list|(
name|messages
operator|==
literal|0
condition|?
literal|"type"
else|:
operator|(
literal|"t"
operator|+
name|messages
operator|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|destinationName
operator|==
literal|null
operator|||
name|message
operator|==
literal|null
operator|||
name|type
operator|==
literal|null
condition|)
block|{
break|break;
block|}
try|try
block|{
name|Destination
name|destination
init|=
name|getDestination
argument_list|(
name|client
argument_list|,
name|request
argument_list|,
name|destinationName
argument_list|)
decl_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
name|messages
operator|+
literal|" destination="
operator|+
name|destinationName
operator|+
literal|" message="
operator|+
name|message
operator|+
literal|" type="
operator|+
name|type
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
name|destination
operator|+
literal|" is a "
operator|+
name|destination
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|messages
operator|++
expr_stmt|;
if|if
condition|(
literal|"listen"
operator|.
name|equals
argument_list|(
name|type
argument_list|)
condition|)
block|{
name|AjaxListener
name|listener
init|=
name|client
operator|.
name|getListener
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|MessageAvailableConsumer
argument_list|,
name|String
argument_list|>
name|consumerIdMap
init|=
name|client
operator|.
name|getIdMap
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|MessageAvailableConsumer
argument_list|,
name|String
argument_list|>
name|consumerDestinationNameMap
init|=
name|client
operator|.
name|getDestinationNameMap
argument_list|()
decl_stmt|;
name|client
operator|.
name|closeConsumer
argument_list|(
name|destination
argument_list|)
expr_stmt|;
comment|// drop any existing
comment|// consumer.
name|MessageAvailableConsumer
name|consumer
init|=
operator|(
name|MessageAvailableConsumer
operator|)
name|client
operator|.
name|getConsumer
argument_list|(
name|destination
argument_list|,
name|request
operator|.
name|getHeader
argument_list|(
name|WebClient
operator|.
name|selectorName
argument_list|)
argument_list|)
decl_stmt|;
name|consumer
operator|.
name|setAvailableListener
argument_list|(
name|listener
argument_list|)
expr_stmt|;
name|consumerIdMap
operator|.
name|put
argument_list|(
name|consumer
argument_list|,
name|message
argument_list|)
expr_stmt|;
name|consumerDestinationNameMap
operator|.
name|put
argument_list|(
name|consumer
argument_list|,
name|destinationName
argument_list|)
expr_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Subscribed: "
operator|+
name|consumer
operator|+
literal|" to "
operator|+
name|destination
operator|+
literal|" id="
operator|+
name|message
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
literal|"unlisten"
operator|.
name|equals
argument_list|(
name|type
argument_list|)
condition|)
block|{
name|Map
argument_list|<
name|MessageAvailableConsumer
argument_list|,
name|String
argument_list|>
name|consumerIdMap
init|=
name|client
operator|.
name|getIdMap
argument_list|()
decl_stmt|;
name|Map
name|consumerDestinationNameMap
init|=
name|client
operator|.
name|getDestinationNameMap
argument_list|()
decl_stmt|;
name|MessageAvailableConsumer
name|consumer
init|=
operator|(
name|MessageAvailableConsumer
operator|)
name|client
operator|.
name|getConsumer
argument_list|(
name|destination
argument_list|,
name|request
operator|.
name|getHeader
argument_list|(
name|WebClient
operator|.
name|selectorName
argument_list|)
argument_list|)
decl_stmt|;
name|consumer
operator|.
name|setAvailableListener
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|consumerIdMap
operator|.
name|remove
argument_list|(
name|consumer
argument_list|)
expr_stmt|;
name|consumerDestinationNameMap
operator|.
name|remove
argument_list|(
name|consumer
argument_list|)
expr_stmt|;
name|client
operator|.
name|closeConsumer
argument_list|(
name|destination
argument_list|)
expr_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Unsubscribed: "
operator|+
name|consumer
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
literal|"send"
operator|.
name|equals
argument_list|(
name|type
argument_list|)
condition|)
block|{
name|TextMessage
name|text
init|=
name|client
operator|.
name|getSession
argument_list|()
operator|.
name|createTextMessage
argument_list|(
name|message
argument_list|)
decl_stmt|;
name|appendParametersToMessage
argument_list|(
name|request
argument_list|,
name|text
argument_list|)
expr_stmt|;
name|client
operator|.
name|send
argument_list|(
name|destination
argument_list|,
name|text
argument_list|)
expr_stmt|;
name|messageIds
operator|+=
name|text
operator|.
name|getJMSMessageID
argument_list|()
operator|+
literal|"\n"
expr_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Sent "
operator|+
name|message
operator|+
literal|" to "
operator|+
name|destination
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"unknown type "
operator|+
name|type
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|JMSException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"jms"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
literal|"true"
operator|.
name|equals
argument_list|(
name|request
operator|.
name|getParameter
argument_list|(
literal|"poll"
argument_list|)
argument_list|)
condition|)
block|{
try|try
block|{
comment|// TODO return message IDs
name|doMessages
argument_list|(
name|client
argument_list|,
name|request
argument_list|,
name|response
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|JMSException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ServletException
argument_list|(
literal|"JMS problem: "
operator|+
name|e
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
else|else
block|{
comment|// handle simple POST of a message
if|if
condition|(
name|request
operator|.
name|getContentLength
argument_list|()
operator|!=
literal|0
operator|&&
operator|(
name|request
operator|.
name|getContentType
argument_list|()
operator|==
literal|null
operator|||
operator|!
name|request
operator|.
name|getContentType
argument_list|()
operator|.
name|toLowerCase
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"application/x-www-form-urlencoded"
argument_list|)
operator|)
condition|)
block|{
try|try
block|{
name|Destination
name|destination
init|=
name|getDestination
argument_list|(
name|client
argument_list|,
name|request
argument_list|)
decl_stmt|;
name|String
name|body
init|=
name|getPostedMessageBody
argument_list|(
name|request
argument_list|)
decl_stmt|;
name|TextMessage
name|message
init|=
name|client
operator|.
name|getSession
argument_list|()
operator|.
name|createTextMessage
argument_list|(
name|body
argument_list|)
decl_stmt|;
name|appendParametersToMessage
argument_list|(
name|request
argument_list|,
name|message
argument_list|)
expr_stmt|;
name|client
operator|.
name|send
argument_list|(
name|destination
argument_list|,
name|message
argument_list|)
expr_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Sent to destination: "
operator|+
name|destination
operator|+
literal|" body: "
operator|+
name|body
argument_list|)
expr_stmt|;
block|}
name|messageIds
operator|+=
name|message
operator|.
name|getJMSMessageID
argument_list|()
operator|+
literal|"\n"
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|JMSException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ServletException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
name|response
operator|.
name|setContentType
argument_list|(
literal|"text/plain"
argument_list|)
expr_stmt|;
name|response
operator|.
name|setHeader
argument_list|(
literal|"Cache-Control"
argument_list|,
literal|"no-cache"
argument_list|)
expr_stmt|;
name|response
operator|.
name|getWriter
argument_list|()
operator|.
name|print
argument_list|(
name|messageIds
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Supports a HTTP DELETE to be equivlanent of consuming a singe message      * from a queue      */
specifier|protected
name|void
name|doGet
parameter_list|(
name|HttpServletRequest
name|request
parameter_list|,
name|HttpServletResponse
name|response
parameter_list|)
throws|throws
name|ServletException
throws|,
name|IOException
block|{
try|try
block|{
name|AjaxWebClient
name|client
init|=
name|getAjaxWebClient
argument_list|(
name|request
argument_list|)
decl_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"GET client="
operator|+
name|client
operator|+
literal|" session="
operator|+
name|request
operator|.
name|getSession
argument_list|()
operator|.
name|getId
argument_list|()
operator|+
literal|" clientId="
operator|+
name|request
operator|.
name|getParameter
argument_list|(
literal|"clientId"
argument_list|)
operator|+
literal|" uri="
operator|+
name|request
operator|.
name|getRequestURI
argument_list|()
operator|+
literal|" query="
operator|+
name|request
operator|.
name|getQueryString
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|doMessages
argument_list|(
name|client
argument_list|,
name|request
argument_list|,
name|response
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|JMSException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ServletException
argument_list|(
literal|"JMS problem: "
operator|+
name|e
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
comment|/**      * Reads a message from a destination up to some specific timeout period      *       * @param client The webclient      * @param request      * @param response      * @throws ServletException      * @throws IOException      */
specifier|protected
name|void
name|doMessages
parameter_list|(
name|AjaxWebClient
name|client
parameter_list|,
name|HttpServletRequest
name|request
parameter_list|,
name|HttpServletResponse
name|response
parameter_list|)
throws|throws
name|JMSException
throws|,
name|IOException
block|{
name|int
name|messages
init|=
literal|0
decl_stmt|;
comment|// This is a poll for any messages
name|long
name|timeout
init|=
name|getReadTimeout
argument_list|(
name|request
argument_list|)
decl_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"doMessage timeout="
operator|+
name|timeout
argument_list|)
expr_stmt|;
block|}
name|Message
name|message
init|=
literal|null
decl_stmt|;
comment|// this is non-null if we're resuming the continuation.
comment|// attributes set in AjaxListener
name|message
operator|=
operator|(
name|Message
operator|)
name|request
operator|.
name|getAttribute
argument_list|(
literal|"message"
argument_list|)
expr_stmt|;
synchronized|synchronized
init|(
name|client
init|)
block|{
name|List
name|consumers
init|=
name|client
operator|.
name|getConsumers
argument_list|()
decl_stmt|;
name|MessageAvailableConsumer
name|consumer
init|=
operator|(
name|MessageAvailableConsumer
operator|)
name|request
operator|.
name|getAttribute
argument_list|(
literal|"consumer"
argument_list|)
decl_stmt|;
if|if
condition|(
name|message
operator|==
literal|null
condition|)
block|{
comment|// Look for a message that is ready to go
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|message
operator|==
literal|null
operator|&&
name|i
operator|<
name|consumers
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|consumer
operator|=
operator|(
name|MessageAvailableConsumer
operator|)
name|consumers
operator|.
name|get
argument_list|(
name|i
argument_list|)
expr_stmt|;
if|if
condition|(
name|consumer
operator|.
name|getAvailableListener
argument_list|()
operator|==
literal|null
condition|)
block|{
continue|continue;
block|}
comment|// Look for any available messages
name|message
operator|=
name|consumer
operator|.
name|receive
argument_list|(
literal|10
argument_list|)
expr_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"received "
operator|+
name|message
operator|+
literal|" from "
operator|+
name|consumer
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|// prepare the response
name|response
operator|.
name|setContentType
argument_list|(
literal|"text/xml"
argument_list|)
expr_stmt|;
name|response
operator|.
name|setHeader
argument_list|(
literal|"Cache-Control"
argument_list|,
literal|"no-cache"
argument_list|)
expr_stmt|;
if|if
condition|(
name|message
operator|==
literal|null
operator|&&
name|client
operator|.
name|getListener
argument_list|()
operator|.
name|getUnconsumedMessages
argument_list|()
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
name|Continuation
name|continuation
init|=
name|ContinuationSupport
operator|.
name|getContinuation
argument_list|(
name|request
argument_list|)
decl_stmt|;
if|if
condition|(
name|continuation
operator|.
name|isExpired
argument_list|()
condition|)
block|{
name|response
operator|.
name|setStatus
argument_list|(
name|HttpServletResponse
operator|.
name|SC_OK
argument_list|)
expr_stmt|;
name|StringWriter
name|swriter
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
name|PrintWriter
name|writer
init|=
operator|new
name|PrintWriter
argument_list|(
name|swriter
argument_list|)
decl_stmt|;
name|writer
operator|.
name|println
argument_list|(
literal|"<ajax-response>"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|print
argument_list|(
literal|"</ajax-response>"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|flush
argument_list|()
expr_stmt|;
name|String
name|m
init|=
name|swriter
operator|.
name|toString
argument_list|()
decl_stmt|;
name|response
operator|.
name|getWriter
argument_list|()
operator|.
name|println
argument_list|(
name|m
argument_list|)
expr_stmt|;
return|return;
block|}
name|continuation
operator|.
name|setTimeout
argument_list|(
name|timeout
argument_list|)
expr_stmt|;
name|continuation
operator|.
name|suspend
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Suspending continuation "
operator|+
name|continuation
argument_list|)
expr_stmt|;
comment|// Fetch the listeners
name|AjaxListener
name|listener
init|=
name|client
operator|.
name|getListener
argument_list|()
decl_stmt|;
name|listener
operator|.
name|access
argument_list|()
expr_stmt|;
comment|// register this continuation with our listener.
name|listener
operator|.
name|setContinuation
argument_list|(
name|continuation
argument_list|)
expr_stmt|;
return|return;
block|}
name|StringWriter
name|swriter
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
name|PrintWriter
name|writer
init|=
operator|new
name|PrintWriter
argument_list|(
name|swriter
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|MessageAvailableConsumer
argument_list|,
name|String
argument_list|>
name|consumerIdMap
init|=
name|client
operator|.
name|getIdMap
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|MessageAvailableConsumer
argument_list|,
name|String
argument_list|>
name|consumerDestinationNameMap
init|=
name|client
operator|.
name|getDestinationNameMap
argument_list|()
decl_stmt|;
name|response
operator|.
name|setStatus
argument_list|(
name|HttpServletResponse
operator|.
name|SC_OK
argument_list|)
expr_stmt|;
name|writer
operator|.
name|println
argument_list|(
literal|"<ajax-response>"
argument_list|)
expr_stmt|;
comment|// Send any message we already have
if|if
condition|(
name|message
operator|!=
literal|null
condition|)
block|{
name|String
name|id
init|=
name|consumerIdMap
operator|.
name|get
argument_list|(
name|consumer
argument_list|)
decl_stmt|;
name|String
name|destinationName
init|=
name|consumerDestinationNameMap
operator|.
name|get
argument_list|(
name|consumer
argument_list|)
decl_stmt|;
name|writeMessageResponse
argument_list|(
name|writer
argument_list|,
name|message
argument_list|,
name|id
argument_list|,
name|destinationName
argument_list|)
expr_stmt|;
name|messages
operator|++
expr_stmt|;
block|}
comment|// Send the rest of the messages
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|consumers
operator|.
name|size
argument_list|()
operator|&&
name|messages
operator|<
name|maximumMessages
condition|;
name|i
operator|++
control|)
block|{
name|consumer
operator|=
operator|(
name|MessageAvailableConsumer
operator|)
name|consumers
operator|.
name|get
argument_list|(
name|i
argument_list|)
expr_stmt|;
if|if
condition|(
name|consumer
operator|.
name|getAvailableListener
argument_list|()
operator|==
literal|null
condition|)
block|{
continue|continue;
block|}
name|LinkedList
argument_list|<
name|Message
argument_list|>
name|unconsumedMessages
init|=
operator|(
operator|(
name|AjaxListener
operator|)
name|consumer
operator|.
name|getAvailableListener
argument_list|()
operator|)
operator|.
name|getUnconsumedMessages
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Send "
operator|+
name|unconsumedMessages
operator|.
name|size
argument_list|()
operator|+
literal|" unconsumed messages"
argument_list|)
expr_stmt|;
synchronized|synchronized
init|(
name|unconsumedMessages
init|)
block|{
for|for
control|(
name|Iterator
argument_list|<
name|Message
argument_list|>
name|it
init|=
name|unconsumedMessages
operator|.
name|iterator
argument_list|()
init|;
name|it
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|messages
operator|++
expr_stmt|;
name|Message
name|msg
init|=
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
name|String
name|id
init|=
name|consumerIdMap
operator|.
name|get
argument_list|(
name|consumer
argument_list|)
decl_stmt|;
name|String
name|destinationName
init|=
name|consumerDestinationNameMap
operator|.
name|get
argument_list|(
name|consumer
argument_list|)
decl_stmt|;
name|writeMessageResponse
argument_list|(
name|writer
argument_list|,
name|msg
argument_list|,
name|id
argument_list|,
name|destinationName
argument_list|)
expr_stmt|;
name|it
operator|.
name|remove
argument_list|()
expr_stmt|;
if|if
condition|(
name|messages
operator|>=
name|maximumMessages
condition|)
block|{
break|break;
block|}
block|}
block|}
comment|// Look for any available messages
while|while
condition|(
name|messages
operator|<
name|maximumMessages
condition|)
block|{
name|message
operator|=
name|consumer
operator|.
name|receiveNoWait
argument_list|()
expr_stmt|;
if|if
condition|(
name|message
operator|==
literal|null
condition|)
block|{
break|break;
block|}
name|messages
operator|++
expr_stmt|;
name|String
name|id
init|=
name|consumerIdMap
operator|.
name|get
argument_list|(
name|consumer
argument_list|)
decl_stmt|;
name|String
name|destinationName
init|=
name|consumerDestinationNameMap
operator|.
name|get
argument_list|(
name|consumer
argument_list|)
decl_stmt|;
name|writeMessageResponse
argument_list|(
name|writer
argument_list|,
name|message
argument_list|,
name|id
argument_list|,
name|destinationName
argument_list|)
expr_stmt|;
block|}
block|}
name|writer
operator|.
name|print
argument_list|(
literal|"</ajax-response>"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|flush
argument_list|()
expr_stmt|;
name|String
name|m
init|=
name|swriter
operator|.
name|toString
argument_list|()
decl_stmt|;
name|response
operator|.
name|getWriter
argument_list|()
operator|.
name|println
argument_list|(
name|m
argument_list|)
expr_stmt|;
block|}
block|}
specifier|protected
name|void
name|writeMessageResponse
parameter_list|(
name|PrintWriter
name|writer
parameter_list|,
name|Message
name|message
parameter_list|,
name|String
name|id
parameter_list|,
name|String
name|destinationName
parameter_list|)
throws|throws
name|JMSException
throws|,
name|IOException
block|{
name|writer
operator|.
name|print
argument_list|(
literal|"<response id='"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|print
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|writer
operator|.
name|print
argument_list|(
literal|"'"
argument_list|)
expr_stmt|;
if|if
condition|(
name|destinationName
operator|!=
literal|null
condition|)
block|{
name|writer
operator|.
name|print
argument_list|(
literal|" destination='"
operator|+
name|destinationName
operator|+
literal|"' "
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|print
argument_list|(
literal|">"
argument_list|)
expr_stmt|;
if|if
condition|(
name|message
operator|instanceof
name|TextMessage
condition|)
block|{
name|TextMessage
name|textMsg
init|=
operator|(
name|TextMessage
operator|)
name|message
decl_stmt|;
name|String
name|txt
init|=
name|textMsg
operator|.
name|getText
argument_list|()
decl_stmt|;
if|if
condition|(
name|txt
operator|.
name|startsWith
argument_list|(
literal|"<?"
argument_list|)
condition|)
block|{
name|txt
operator|=
name|txt
operator|.
name|substring
argument_list|(
name|txt
operator|.
name|indexOf
argument_list|(
literal|"?>"
argument_list|)
operator|+
literal|2
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|print
argument_list|(
name|txt
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|message
operator|instanceof
name|ObjectMessage
condition|)
block|{
name|ObjectMessage
name|objectMsg
init|=
operator|(
name|ObjectMessage
operator|)
name|message
decl_stmt|;
name|Object
name|object
init|=
name|objectMsg
operator|.
name|getObject
argument_list|()
decl_stmt|;
name|writer
operator|.
name|print
argument_list|(
name|object
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|println
argument_list|(
literal|"</response>"
argument_list|)
expr_stmt|;
block|}
comment|/*      * Return the AjaxWebClient for this session+clientId.      * Create one if it does not already exist.      */
specifier|protected
name|AjaxWebClient
name|getAjaxWebClient
parameter_list|(
name|HttpServletRequest
name|request
parameter_list|)
block|{
name|long
name|now
init|=
operator|(
operator|new
name|Date
argument_list|()
operator|)
operator|.
name|getTime
argument_list|()
decl_stmt|;
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
name|String
name|clientId
init|=
name|request
operator|.
name|getParameter
argument_list|(
literal|"clientId"
argument_list|)
decl_stmt|;
comment|// if user doesn't supply a 'clientId', we'll just use a default.
if|if
condition|(
name|clientId
operator|==
literal|null
condition|)
block|{
name|clientId
operator|=
literal|"defaultAjaxWebClient"
expr_stmt|;
block|}
name|String
name|sessionKey
init|=
name|session
operator|.
name|getId
argument_list|()
operator|+
literal|'-'
operator|+
name|clientId
decl_stmt|;
name|AjaxWebClient
name|client
init|=
name|ajaxWebClients
operator|.
name|get
argument_list|(
name|sessionKey
argument_list|)
decl_stmt|;
synchronized|synchronized
init|(
name|ajaxWebClients
init|)
block|{
comment|// create a new AjaxWebClient if one does not already exist for this sessionKey.
if|if
condition|(
name|client
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"creating new AjaxWebClient in "
operator|+
name|sessionKey
argument_list|)
expr_stmt|;
block|}
name|client
operator|=
operator|new
name|AjaxWebClient
argument_list|(
name|request
argument_list|,
name|maximumReadTimeout
argument_list|)
expr_stmt|;
name|ajaxWebClients
operator|.
name|put
argument_list|(
name|sessionKey
argument_list|,
name|client
argument_list|)
expr_stmt|;
block|}
name|client
operator|.
name|updateLastAccessed
argument_list|()
expr_stmt|;
block|}
return|return
name|client
return|;
block|}
comment|/**      * @return the timeout value for read requests which is always>= 0 and<=      *         maximumReadTimeout to avoid DoS attacks      */
specifier|protected
name|long
name|getReadTimeout
parameter_list|(
name|HttpServletRequest
name|request
parameter_list|)
block|{
name|long
name|answer
init|=
name|defaultReadTimeout
decl_stmt|;
name|String
name|name
init|=
name|request
operator|.
name|getParameter
argument_list|(
name|readTimeoutParameter
argument_list|)
decl_stmt|;
if|if
condition|(
name|name
operator|!=
literal|null
condition|)
block|{
name|answer
operator|=
name|asLong
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|answer
argument_list|<
literal|0
operator|||
name|answer
argument_list|>
name|maximumReadTimeout
condition|)
block|{
name|answer
operator|=
name|maximumReadTimeout
expr_stmt|;
block|}
return|return
name|answer
return|;
block|}
comment|/*      * an instance of this class runs every minute (started in init), to clean up old web clients& free resources.      */
specifier|private
class|class
name|ClientCleaner
extends|extends
name|TimerTask
block|{
specifier|public
name|void
name|run
parameter_list|()
block|{
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Cleaning up expired web clients."
argument_list|)
expr_stmt|;
block|}
synchronized|synchronized
init|(
name|ajaxWebClients
init|)
block|{
name|Iterator
name|it
init|=
name|ajaxWebClients
operator|.
name|entrySet
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|it
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|AjaxWebClient
argument_list|>
name|e
init|=
operator|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|AjaxWebClient
argument_list|>
operator|)
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
name|String
name|key
init|=
name|e
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|AjaxWebClient
name|val
init|=
name|e
operator|.
name|getValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"AjaxWebClient "
operator|+
name|key
operator|+
literal|" last accessed "
operator|+
name|val
operator|.
name|getMillisSinceLastAccessed
argument_list|()
operator|/
literal|1000
operator|+
literal|" seconds ago."
argument_list|)
expr_stmt|;
block|}
comment|// close an expired client and remove it from the ajaxWebClients hash.
if|if
condition|(
name|val
operator|.
name|closeIfExpired
argument_list|()
condition|)
block|{
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Removing expired AjaxWebClient "
operator|+
name|key
argument_list|)
expr_stmt|;
block|}
name|it
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
block|}
end_class

end_unit

