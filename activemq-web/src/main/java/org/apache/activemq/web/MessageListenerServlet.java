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
name|HashMap
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
name|org
operator|.
name|mortbay
operator|.
name|util
operator|.
name|ajax
operator|.
name|Continuation
import|;
end_import

begin_import
import|import
name|org
operator|.
name|mortbay
operator|.
name|util
operator|.
name|ajax
operator|.
name|ContinuationSupport
import|;
end_import

begin_comment
comment|/**  * A servlet for sending and receiving messages to/from JMS destinations using  * HTTP POST for sending and HTTP GET for receiving.<p/> You can specify the  * destination and whether it is a topic or queue via configuration details on  * the servlet or as request parameters.<p/> For reading messages you can  * specify a readTimeout parameter to determine how long the servlet should  * block for.  *   * The servlet can be configured with the following init parameters:<dl>  *<dt>defaultReadTimeout</dt><dd>The default time in ms to wait for messages.   * May be overridden by a request using the 'timeout' parameter</dd>  *<dt>maximumReadTimeout</dt><dd>The maximum value a request may specify for the 'timeout' parameter</dd>  *<dt>maximumMessages</dt><dd>maximum messages to send per response</dd>  *<dt></dt><dd></dd>  *</dl>  *    *   * @version $Revision: 1.1.1.1 $  */
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
name|Log
name|log
init|=
name|LogFactory
operator|.
name|getLog
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
block|}
comment|/**      * Sends a message to a destination      *       * @param request      * @param response      * @throws ServletException      * @throws IOException      */
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
name|String
name|message_ids
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
name|log
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|log
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
name|String
index|[]
name|destinations
init|=
name|request
operator|.
name|getParameterValues
argument_list|(
literal|"destination"
argument_list|)
decl_stmt|;
name|String
index|[]
name|messages
init|=
name|request
operator|.
name|getParameterValues
argument_list|(
literal|"message"
argument_list|)
decl_stmt|;
name|String
index|[]
name|types
init|=
name|request
operator|.
name|getParameterValues
argument_list|(
literal|"type"
argument_list|)
decl_stmt|;
if|if
condition|(
name|destinations
operator|.
name|length
operator|!=
name|messages
operator|.
name|length
operator|||
name|messages
operator|.
name|length
operator|!=
name|types
operator|.
name|length
condition|)
block|{
if|if
condition|(
name|log
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"ERROR destination="
operator|+
name|destinations
operator|.
name|length
operator|+
literal|" message="
operator|+
name|messages
operator|.
name|length
operator|+
literal|" type="
operator|+
name|types
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
name|response
operator|.
name|sendError
argument_list|(
name|HttpServletResponse
operator|.
name|SC_BAD_REQUEST
argument_list|,
literal|"missmatched destination, message or type"
argument_list|)
expr_stmt|;
return|return;
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
name|types
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
try|try
block|{
name|String
name|type
init|=
name|types
index|[
name|i
index|]
decl_stmt|;
name|Destination
name|destination
init|=
name|getDestination
argument_list|(
name|client
argument_list|,
name|request
argument_list|,
name|destinations
index|[
name|i
index|]
argument_list|)
decl_stmt|;
if|if
condition|(
name|log
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|log
operator|.
name|debug
argument_list|(
name|i
operator|+
literal|" destination="
operator|+
name|destinations
index|[
name|i
index|]
operator|+
literal|" message="
operator|+
name|messages
index|[
name|i
index|]
operator|+
literal|" type="
operator|+
name|types
index|[
name|i
index|]
argument_list|)
expr_stmt|;
name|log
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
name|Listener
name|listener
init|=
name|getListener
argument_list|(
name|request
argument_list|)
decl_stmt|;
name|Map
name|consumerIdMap
init|=
name|getConsumerIdMap
argument_list|(
name|request
argument_list|)
decl_stmt|;
name|client
operator|.
name|closeConsumer
argument_list|(
name|destination
argument_list|)
expr_stmt|;
comment|// drop any existing consumer.
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
name|messages
index|[
name|i
index|]
argument_list|)
expr_stmt|;
if|if
condition|(
name|log
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|log
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
name|messages
index|[
name|i
index|]
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
name|consumerIdMap
init|=
name|getConsumerIdMap
argument_list|(
name|request
argument_list|)
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
name|client
operator|.
name|closeConsumer
argument_list|(
name|destination
argument_list|)
expr_stmt|;
if|if
condition|(
name|log
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|log
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
name|message
init|=
name|client
operator|.
name|getSession
argument_list|()
operator|.
name|createTextMessage
argument_list|(
name|messages
index|[
name|i
index|]
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
name|message_ids
operator|+=
name|message
operator|.
name|getJMSMessageID
argument_list|()
operator|+
literal|"\n"
expr_stmt|;
if|if
condition|(
name|log
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Sent "
operator|+
name|messages
index|[
name|i
index|]
operator|+
literal|" to "
operator|+
name|destination
argument_list|)
expr_stmt|;
block|}
block|}
else|else
name|log
operator|.
name|warn
argument_list|(
literal|"unknown type "
operator|+
name|type
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|JMSException
name|e
parameter_list|)
block|{
name|log
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
name|log
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|log
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
name|message_ids
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
name|message_ids
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
if|if
condition|(
name|log
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|log
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
comment|/**      * Reads a message from a destination up to some specific timeout period      *       * @param client      *            The webclient      * @param request      * @param response      * @throws ServletException      * @throws IOException      */
specifier|protected
name|void
name|doMessages
parameter_list|(
name|WebClient
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
name|log
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"doMessage timeout="
operator|+
name|timeout
argument_list|)
expr_stmt|;
block|}
name|Continuation
name|continuation
init|=
name|ContinuationSupport
operator|.
name|getContinuation
argument_list|(
name|request
argument_list|,
name|client
argument_list|)
decl_stmt|;
name|Listener
name|listener
init|=
name|getListener
argument_list|(
name|request
argument_list|)
decl_stmt|;
if|if
condition|(
name|listener
operator|!=
literal|null
operator|&&
name|continuation
operator|!=
literal|null
operator|&&
operator|!
name|continuation
operator|.
name|isPending
argument_list|()
condition|)
name|listener
operator|.
name|access
argument_list|()
expr_stmt|;
name|Message
name|message
init|=
literal|null
decl_stmt|;
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
literal|null
decl_stmt|;
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
continue|continue;
comment|// Look for any available messages
name|message
operator|=
name|consumer
operator|.
name|receiveNoWait
argument_list|()
expr_stmt|;
if|if
condition|(
name|log
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|log
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
comment|// Get an existing Continuation or create a new one if there are no
comment|// messages
if|if
condition|(
name|message
operator|==
literal|null
condition|)
block|{
comment|// register this continuation with our listener.
name|listener
operator|.
name|setContinuation
argument_list|(
name|continuation
argument_list|)
expr_stmt|;
comment|// Get the continuation object (may wait and/or retry
comment|// request here).
name|continuation
operator|.
name|suspend
argument_list|(
name|timeout
argument_list|)
expr_stmt|;
block|}
name|listener
operator|.
name|setContinuation
argument_list|(
literal|null
argument_list|)
expr_stmt|;
comment|// prepare the responds
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
name|consumerIdMap
init|=
name|getConsumerIdMap
argument_list|(
name|request
argument_list|)
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
operator|(
name|String
operator|)
name|consumerIdMap
operator|.
name|get
argument_list|(
name|consumer
argument_list|)
decl_stmt|;
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
literal|"'>"
argument_list|)
expr_stmt|;
name|writeMessageResponse
argument_list|(
name|writer
argument_list|,
name|message
argument_list|)
expr_stmt|;
name|writer
operator|.
name|println
argument_list|(
literal|"</response>"
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
continue|continue;
comment|// Look for any available messages
name|message
operator|=
name|consumer
operator|.
name|receiveNoWait
argument_list|()
expr_stmt|;
while|while
condition|(
name|message
operator|!=
literal|null
operator|&&
name|messages
operator|<
name|maximumMessages
condition|)
block|{
name|String
name|id
init|=
operator|(
name|String
operator|)
name|consumerIdMap
operator|.
name|get
argument_list|(
name|consumer
argument_list|)
decl_stmt|;
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
literal|"'>"
argument_list|)
expr_stmt|;
name|writeMessageResponse
argument_list|(
name|writer
argument_list|,
name|message
argument_list|)
expr_stmt|;
name|writer
operator|.
name|println
argument_list|(
literal|"</response>"
argument_list|)
expr_stmt|;
name|messages
operator|++
expr_stmt|;
name|message
operator|=
name|consumer
operator|.
name|receiveNoWait
argument_list|()
expr_stmt|;
block|}
block|}
comment|// Add poll message
comment|// writer.println("<response type='object' id='amqPoll'><ok/></response>");
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
comment|// System.err.println(m);
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
parameter_list|)
throws|throws
name|JMSException
throws|,
name|IOException
block|{
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
block|}
specifier|protected
name|Listener
name|getListener
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
argument_list|()
decl_stmt|;
name|Listener
name|listener
init|=
operator|(
name|Listener
operator|)
name|session
operator|.
name|getAttribute
argument_list|(
literal|"mls.listener"
argument_list|)
decl_stmt|;
if|if
condition|(
name|listener
operator|==
literal|null
condition|)
block|{
name|listener
operator|=
operator|new
name|Listener
argument_list|(
name|WebClient
operator|.
name|getWebClient
argument_list|(
name|request
argument_list|)
argument_list|)
expr_stmt|;
name|session
operator|.
name|setAttribute
argument_list|(
literal|"mls.listener"
argument_list|,
name|listener
argument_list|)
expr_stmt|;
block|}
return|return
name|listener
return|;
block|}
specifier|protected
name|Map
name|getConsumerIdMap
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
argument_list|()
decl_stmt|;
name|Map
name|map
init|=
operator|(
name|Map
operator|)
name|session
operator|.
name|getAttribute
argument_list|(
literal|"mls.consumerIdMap"
argument_list|)
decl_stmt|;
if|if
condition|(
name|map
operator|==
literal|null
condition|)
block|{
name|map
operator|=
operator|new
name|HashMap
argument_list|()
expr_stmt|;
name|session
operator|.
name|setAttribute
argument_list|(
literal|"mls.consumerIdMap"
argument_list|,
name|map
argument_list|)
expr_stmt|;
block|}
return|return
name|map
return|;
block|}
specifier|protected
name|boolean
name|isRicoAjax
parameter_list|(
name|HttpServletRequest
name|request
parameter_list|)
block|{
name|String
name|rico
init|=
name|request
operator|.
name|getParameter
argument_list|(
literal|"rico"
argument_list|)
decl_stmt|;
return|return
name|rico
operator|!=
literal|null
operator|&&
name|rico
operator|.
name|equals
argument_list|(
literal|"true"
argument_list|)
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
comment|/*      * Listen for available messages and wakeup any continuations.      */
specifier|private
class|class
name|Listener
implements|implements
name|MessageAvailableListener
block|{
name|WebClient
name|client
decl_stmt|;
name|long
name|lastAccess
decl_stmt|;
name|Continuation
name|continuation
decl_stmt|;
name|Listener
parameter_list|(
name|WebClient
name|client
parameter_list|)
block|{
name|this
operator|.
name|client
operator|=
name|client
expr_stmt|;
block|}
specifier|public
name|void
name|access
parameter_list|()
block|{
name|lastAccess
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|setContinuation
parameter_list|(
name|Continuation
name|continuation
parameter_list|)
block|{
synchronized|synchronized
init|(
name|client
init|)
block|{
name|this
operator|.
name|continuation
operator|=
name|continuation
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|onMessageAvailable
parameter_list|(
name|MessageConsumer
name|consumer
parameter_list|)
block|{
synchronized|synchronized
init|(
name|client
init|)
block|{
if|if
condition|(
name|log
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"message for "
operator|+
name|consumer
operator|+
literal|"continuation="
operator|+
name|continuation
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|continuation
operator|!=
literal|null
condition|)
name|continuation
operator|.
name|resume
argument_list|()
expr_stmt|;
elseif|else
if|if
condition|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|lastAccess
operator|>
literal|2
operator|*
name|maximumReadTimeout
condition|)
block|{
name|client
operator|.
name|closeConsumers
argument_list|()
expr_stmt|;
block|}
name|continuation
operator|=
literal|null
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

