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
name|util
operator|.
name|Enumeration
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
name|HashSet
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

begin_comment
comment|/**  * A servlet for sending and receiving messages to/from JMS destinations using  * HTTP POST for sending and HTTP GET for receiving.  *<p/>  * You can specify the destination and whether it is a topic or queue via  * configuration details on the servlet or as request parameters.  *<p/>  * For reading messages you can specify a readTimeout parameter to determine how  * long the servlet should block for.  *  * One thing to keep in mind with this solution - due to the nature of REST,  * there will always be a chance of losing messages. Consider what happens when  * a message is retrieved from the broker but the web call is interrupted before  * the client receives the message in the response - the message is lost.  */
end_comment

begin_class
specifier|public
class|class
name|MessageServlet
extends|extends
name|MessageServletSupport
block|{
comment|// its a bit pita that this servlet got intermixed with jetty continuation/rest
comment|// instead of creating a special for that. We should have kept a simple servlet
comment|// for good old fashioned request/response blocked communication.
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|8737914695188481219L
decl_stmt|;
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
name|MessageServlet
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|String
name|readTimeoutParameter
init|=
literal|"readTimeout"
decl_stmt|;
specifier|private
specifier|final
name|String
name|readTimeoutRequestAtt
init|=
literal|"xamqReadDeadline"
decl_stmt|;
specifier|private
specifier|final
name|String
name|oneShotParameter
init|=
literal|"oneShot"
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
literal|20000
decl_stmt|;
specifier|private
name|long
name|requestTimeout
init|=
literal|1000
decl_stmt|;
specifier|private
name|String
name|defaultContentType
init|=
literal|"application/xml"
decl_stmt|;
specifier|private
specifier|final
name|HashMap
argument_list|<
name|String
argument_list|,
name|WebClient
argument_list|>
name|clients
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|WebClient
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|HashSet
argument_list|<
name|MessageAvailableConsumer
argument_list|>
name|activeConsumers
init|=
operator|new
name|HashSet
argument_list|<
name|MessageAvailableConsumer
argument_list|>
argument_list|()
decl_stmt|;
annotation|@
name|Override
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
literal|"replyTimeout"
argument_list|)
expr_stmt|;
if|if
condition|(
name|name
operator|!=
literal|null
condition|)
block|{
name|requestTimeout
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
literal|"defaultContentType"
argument_list|)
expr_stmt|;
if|if
condition|(
name|name
operator|!=
literal|null
condition|)
block|{
name|defaultContentType
operator|=
name|name
expr_stmt|;
block|}
block|}
comment|/**      * Sends a message to a destination      *      * @param request      * @param response      * @throws ServletException      * @throws IOException      */
annotation|@
name|Override
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
try|try
block|{
name|String
name|action
init|=
name|request
operator|.
name|getParameter
argument_list|(
literal|"action"
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
if|if
condition|(
name|action
operator|!=
literal|null
operator|&&
name|clientId
operator|!=
literal|null
operator|&&
name|action
operator|.
name|equals
argument_list|(
literal|"unsubscribe"
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Unsubscribing client "
operator|+
name|clientId
argument_list|)
expr_stmt|;
name|WebClient
name|client
init|=
name|getWebClient
argument_list|(
name|request
argument_list|)
decl_stmt|;
name|client
operator|.
name|close
argument_list|()
expr_stmt|;
name|clients
operator|.
name|remove
argument_list|(
name|clientId
argument_list|)
expr_stmt|;
return|return;
block|}
name|WebClient
name|client
init|=
name|getWebClient
argument_list|(
name|request
argument_list|)
decl_stmt|;
name|String
name|text
init|=
name|getPostedMessageBody
argument_list|(
name|request
argument_list|)
decl_stmt|;
comment|// lets create the destination from the URI?
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
if|if
condition|(
name|destination
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|NoDestinationSuppliedException
argument_list|()
throw|;
block|}
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
literal|"Sending message to: "
operator|+
name|destination
operator|+
literal|" with text: "
operator|+
name|text
argument_list|)
expr_stmt|;
block|}
name|boolean
name|sync
init|=
name|isSync
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
name|text
argument_list|)
decl_stmt|;
name|appendParametersToMessage
argument_list|(
name|request
argument_list|,
name|message
argument_list|)
expr_stmt|;
name|boolean
name|persistent
init|=
name|isSendPersistent
argument_list|(
name|request
argument_list|)
decl_stmt|;
name|int
name|priority
init|=
name|getSendPriority
argument_list|(
name|request
argument_list|)
decl_stmt|;
name|long
name|timeToLive
init|=
name|getSendTimeToLive
argument_list|(
name|request
argument_list|)
decl_stmt|;
name|client
operator|.
name|send
argument_list|(
name|destination
argument_list|,
name|message
argument_list|,
name|persistent
argument_list|,
name|priority
argument_list|,
name|timeToLive
argument_list|)
expr_stmt|;
comment|// lets return a unique URI for reliable messaging
name|response
operator|.
name|setHeader
argument_list|(
literal|"messageID"
argument_list|,
name|message
operator|.
name|getJMSMessageID
argument_list|()
argument_list|)
expr_stmt|;
name|response
operator|.
name|setStatus
argument_list|(
name|HttpServletResponse
operator|.
name|SC_OK
argument_list|)
expr_stmt|;
name|response
operator|.
name|getWriter
argument_list|()
operator|.
name|write
argument_list|(
literal|"Message sent"
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
literal|"Could not post JMS message: "
operator|+
name|e
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
comment|/**      * Supports a HTTP DELETE to be equivalent of consuming a singe message      * from a queue      */
annotation|@
name|Override
specifier|protected
name|void
name|doDelete
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
name|doMessages
argument_list|(
name|request
argument_list|,
name|response
argument_list|)
expr_stmt|;
block|}
comment|/**      * Supports a HTTP DELETE to be equivalent of consuming a singe message      * from a queue      */
annotation|@
name|Override
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
name|doMessages
argument_list|(
name|request
argument_list|,
name|response
argument_list|)
expr_stmt|;
block|}
comment|/**      * Reads a message from a destination up to some specific timeout period      *      * @param request      * @param response      * @throws ServletException      * @throws IOException      */
specifier|protected
name|void
name|doMessages
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
name|MessageAvailableConsumer
name|consumer
init|=
literal|null
decl_stmt|;
try|try
block|{
name|WebClient
name|client
init|=
name|getWebClient
argument_list|(
name|request
argument_list|)
decl_stmt|;
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
if|if
condition|(
name|destination
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|NoDestinationSuppliedException
argument_list|()
throw|;
block|}
name|consumer
operator|=
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
expr_stmt|;
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
comment|// Don't allow concurrent use of the consumer. Do make sure to allow
comment|// subsequent calls on continuation to use the consumer.
if|if
condition|(
name|continuation
operator|.
name|isInitial
argument_list|()
condition|)
block|{
synchronized|synchronized
init|(
name|activeConsumers
init|)
block|{
if|if
condition|(
name|activeConsumers
operator|.
name|contains
argument_list|(
name|consumer
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|ServletException
argument_list|(
literal|"Concurrent access to consumer is not supported"
argument_list|)
throw|;
block|}
else|else
block|{
name|activeConsumers
operator|.
name|add
argument_list|(
name|consumer
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|Message
name|message
init|=
literal|null
decl_stmt|;
name|long
name|deadline
init|=
name|getReadDeadline
argument_list|(
name|request
argument_list|)
decl_stmt|;
name|long
name|timeout
init|=
name|deadline
operator|-
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
comment|// Set the message available listener *before* calling receive to eliminate any
comment|// chance of a missed notification between the time receive() completes without
comment|// a message and the time the listener is set.
synchronized|synchronized
init|(
name|consumer
init|)
block|{
name|Listener
name|listener
init|=
operator|(
name|Listener
operator|)
name|consumer
operator|.
name|getAvailableListener
argument_list|()
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
name|consumer
argument_list|)
expr_stmt|;
name|consumer
operator|.
name|setAvailableListener
argument_list|(
name|listener
argument_list|)
expr_stmt|;
block|}
block|}
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
literal|"Receiving message(s) from: "
operator|+
name|destination
operator|+
literal|" with timeout: "
operator|+
name|timeout
argument_list|)
expr_stmt|;
block|}
comment|// Look for any available messages (need a little timeout). Always
comment|// try at least one lookup; don't block past the deadline.
if|if
condition|(
name|timeout
operator|<=
literal|0
condition|)
block|{
name|message
operator|=
name|consumer
operator|.
name|receiveNoWait
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|timeout
operator|<
literal|10
condition|)
block|{
name|message
operator|=
name|consumer
operator|.
name|receive
argument_list|(
name|timeout
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|message
operator|=
name|consumer
operator|.
name|receive
argument_list|(
literal|10
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|message
operator|==
literal|null
condition|)
block|{
name|handleContinuation
argument_list|(
name|request
argument_list|,
name|response
argument_list|,
name|client
argument_list|,
name|destination
argument_list|,
name|consumer
argument_list|,
name|deadline
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|writeResponse
argument_list|(
name|request
argument_list|,
name|response
argument_list|,
name|message
argument_list|)
expr_stmt|;
name|closeConsumerOnOneShot
argument_list|(
name|request
argument_list|,
name|client
argument_list|,
name|destination
argument_list|)
expr_stmt|;
synchronized|synchronized
init|(
name|activeConsumers
init|)
block|{
name|activeConsumers
operator|.
name|remove
argument_list|(
name|consumer
argument_list|)
expr_stmt|;
block|}
block|}
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
literal|"Could not post JMS message: "
operator|+
name|e
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
specifier|protected
name|void
name|handleContinuation
parameter_list|(
name|HttpServletRequest
name|request
parameter_list|,
name|HttpServletResponse
name|response
parameter_list|,
name|WebClient
name|client
parameter_list|,
name|Destination
name|destination
parameter_list|,
name|MessageAvailableConsumer
name|consumer
parameter_list|,
name|long
name|deadline
parameter_list|)
block|{
comment|// Get an existing Continuation or create a new one if there are no events.
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
name|long
name|timeout
init|=
name|deadline
operator|-
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
if|if
condition|(
operator|(
name|continuation
operator|.
name|isExpired
argument_list|()
operator|)
operator|||
operator|(
name|timeout
operator|<=
literal|0
operator|)
condition|)
block|{
comment|// Reset the continuation on the available listener for the consumer to prevent the
comment|// next message receipt from being consumed without a valid, active continuation.
synchronized|synchronized
init|(
name|consumer
init|)
block|{
name|Object
name|obj
init|=
name|consumer
operator|.
name|getAvailableListener
argument_list|()
decl_stmt|;
if|if
condition|(
name|obj
operator|instanceof
name|Listener
condition|)
block|{
operator|(
operator|(
name|Listener
operator|)
name|obj
operator|)
operator|.
name|setContinuation
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
name|response
operator|.
name|setStatus
argument_list|(
name|HttpServletResponse
operator|.
name|SC_NO_CONTENT
argument_list|)
expr_stmt|;
name|closeConsumerOnOneShot
argument_list|(
name|request
argument_list|,
name|client
argument_list|,
name|destination
argument_list|)
expr_stmt|;
synchronized|synchronized
init|(
name|activeConsumers
init|)
block|{
name|activeConsumers
operator|.
name|remove
argument_list|(
name|consumer
argument_list|)
expr_stmt|;
block|}
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
synchronized|synchronized
init|(
name|consumer
init|)
block|{
name|Listener
name|listener
init|=
operator|(
name|Listener
operator|)
name|consumer
operator|.
name|getAvailableListener
argument_list|()
decl_stmt|;
comment|// register this continuation with our listener.
name|listener
operator|.
name|setContinuation
argument_list|(
name|continuation
argument_list|)
expr_stmt|;
block|}
block|}
specifier|protected
name|void
name|writeResponse
parameter_list|(
name|HttpServletRequest
name|request
parameter_list|,
name|HttpServletResponse
name|response
parameter_list|,
name|Message
name|message
parameter_list|)
throws|throws
name|IOException
throws|,
name|JMSException
block|{
name|int
name|messages
init|=
literal|0
decl_stmt|;
try|try
block|{
name|response
operator|.
name|setHeader
argument_list|(
literal|"Cache-Control"
argument_list|,
literal|"no-cache, no-store, must-revalidate"
argument_list|)
expr_stmt|;
comment|// HTTP
comment|// 1.1
name|response
operator|.
name|setHeader
argument_list|(
literal|"Pragma"
argument_list|,
literal|"no-cache"
argument_list|)
expr_stmt|;
comment|// HTTP 1.0
name|response
operator|.
name|setDateHeader
argument_list|(
literal|"Expires"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
comment|// write a responds
name|PrintWriter
name|writer
init|=
name|response
operator|.
name|getWriter
argument_list|()
decl_stmt|;
comment|// handle any message(s)
if|if
condition|(
name|message
operator|==
literal|null
condition|)
block|{
comment|// No messages so OK response of for ajax else no content.
name|response
operator|.
name|setStatus
argument_list|(
name|HttpServletResponse
operator|.
name|SC_NO_CONTENT
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// We have at least one message so set up the response
name|messages
operator|=
literal|1
expr_stmt|;
name|String
name|type
init|=
name|getContentType
argument_list|(
name|request
argument_list|)
decl_stmt|;
if|if
condition|(
name|type
operator|!=
literal|null
condition|)
block|{
name|response
operator|.
name|setContentType
argument_list|(
name|type
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|isXmlContent
argument_list|(
name|message
argument_list|)
condition|)
block|{
name|response
operator|.
name|setContentType
argument_list|(
name|defaultContentType
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|response
operator|.
name|setContentType
argument_list|(
literal|"text/plain"
argument_list|)
expr_stmt|;
block|}
block|}
name|response
operator|.
name|setStatus
argument_list|(
name|HttpServletResponse
operator|.
name|SC_OK
argument_list|)
expr_stmt|;
name|setResponseHeaders
argument_list|(
name|response
argument_list|,
name|message
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
name|flush
argument_list|()
expr_stmt|;
block|}
block|}
finally|finally
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
literal|"Received "
operator|+
name|messages
operator|+
literal|" message(s)"
argument_list|)
expr_stmt|;
block|}
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
operator|!=
literal|null
condition|)
block|{
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
if|if
condition|(
name|object
operator|!=
literal|null
condition|)
block|{
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
block|}
specifier|protected
name|boolean
name|isXmlContent
parameter_list|(
name|Message
name|message
parameter_list|)
throws|throws
name|JMSException
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
operator|!=
literal|null
condition|)
block|{
comment|// assume its xml when it starts with<
if|if
condition|(
name|txt
operator|.
name|startsWith
argument_list|(
literal|"<"
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
block|}
comment|// for any other kind of messages we dont assume xml
return|return
literal|false
return|;
block|}
specifier|public
name|WebClient
name|getWebClient
parameter_list|(
name|HttpServletRequest
name|request
parameter_list|)
block|{
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
if|if
condition|(
name|clientId
operator|!=
literal|null
condition|)
block|{
synchronized|synchronized
init|(
name|this
init|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Getting local client ["
operator|+
name|clientId
operator|+
literal|"]"
argument_list|)
expr_stmt|;
name|WebClient
name|client
init|=
name|clients
operator|.
name|get
argument_list|(
name|clientId
argument_list|)
decl_stmt|;
if|if
condition|(
name|client
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Creating new client ["
operator|+
name|clientId
operator|+
literal|"]"
argument_list|)
expr_stmt|;
name|client
operator|=
operator|new
name|WebClient
argument_list|()
expr_stmt|;
name|clients
operator|.
name|put
argument_list|(
name|clientId
argument_list|,
name|client
argument_list|)
expr_stmt|;
block|}
return|return
name|client
return|;
block|}
block|}
else|else
block|{
return|return
name|WebClient
operator|.
name|getWebClient
argument_list|(
name|request
argument_list|)
return|;
block|}
block|}
specifier|protected
name|String
name|getContentType
parameter_list|(
name|HttpServletRequest
name|request
parameter_list|)
block|{
name|String
name|value
init|=
name|request
operator|.
name|getParameter
argument_list|(
literal|"xml"
argument_list|)
decl_stmt|;
if|if
condition|(
name|value
operator|!=
literal|null
operator|&&
literal|"true"
operator|.
name|equalsIgnoreCase
argument_list|(
name|value
argument_list|)
condition|)
block|{
return|return
literal|"application/xml"
return|;
block|}
name|value
operator|=
name|request
operator|.
name|getParameter
argument_list|(
literal|"json"
argument_list|)
expr_stmt|;
if|if
condition|(
name|value
operator|!=
literal|null
operator|&&
literal|"true"
operator|.
name|equalsIgnoreCase
argument_list|(
name|value
argument_list|)
condition|)
block|{
return|return
literal|"application/json"
return|;
block|}
return|return
literal|null
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"rawtypes"
argument_list|)
specifier|protected
name|void
name|setResponseHeaders
parameter_list|(
name|HttpServletResponse
name|response
parameter_list|,
name|Message
name|message
parameter_list|)
throws|throws
name|JMSException
block|{
name|response
operator|.
name|setHeader
argument_list|(
literal|"destination"
argument_list|,
name|message
operator|.
name|getJMSDestination
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|response
operator|.
name|setHeader
argument_list|(
literal|"id"
argument_list|,
name|message
operator|.
name|getJMSMessageID
argument_list|()
argument_list|)
expr_stmt|;
comment|// Return JMS properties as header values.
for|for
control|(
name|Enumeration
name|names
init|=
name|message
operator|.
name|getPropertyNames
argument_list|()
init|;
name|names
operator|.
name|hasMoreElements
argument_list|()
condition|;
control|)
block|{
name|String
name|name
init|=
operator|(
name|String
operator|)
name|names
operator|.
name|nextElement
argument_list|()
decl_stmt|;
name|response
operator|.
name|setHeader
argument_list|(
name|name
argument_list|,
name|message
operator|.
name|getObjectProperty
argument_list|(
name|name
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * @return the timeout value for read requests which is always>= 0 and<=      *         maximumReadTimeout to avoid DoS attacks      */
specifier|protected
name|long
name|getReadDeadline
parameter_list|(
name|HttpServletRequest
name|request
parameter_list|)
block|{
name|Long
name|answer
decl_stmt|;
name|answer
operator|=
operator|(
name|Long
operator|)
name|request
operator|.
name|getAttribute
argument_list|(
name|readTimeoutRequestAtt
argument_list|)
expr_stmt|;
if|if
condition|(
name|answer
operator|==
literal|null
condition|)
block|{
name|long
name|timeout
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
name|timeout
operator|=
name|asLong
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|timeout
argument_list|<
literal|0
operator|||
name|timeout
argument_list|>
name|maximumReadTimeout
condition|)
block|{
name|timeout
operator|=
name|maximumReadTimeout
expr_stmt|;
block|}
name|answer
operator|=
name|Long
operator|.
name|valueOf
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|+
name|timeout
argument_list|)
expr_stmt|;
block|}
return|return
name|answer
operator|.
name|longValue
argument_list|()
return|;
block|}
comment|/**      * Close the consumer if one-shot mode is used on the given request.      */
specifier|protected
name|void
name|closeConsumerOnOneShot
parameter_list|(
name|HttpServletRequest
name|request
parameter_list|,
name|WebClient
name|client
parameter_list|,
name|Destination
name|dest
parameter_list|)
block|{
if|if
condition|(
name|asBoolean
argument_list|(
name|request
operator|.
name|getParameter
argument_list|(
name|oneShotParameter
argument_list|)
argument_list|,
literal|false
argument_list|)
condition|)
block|{
try|try
block|{
name|client
operator|.
name|closeConsumer
argument_list|(
name|dest
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|JMSException
name|jms_exc
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"JMS exception on closing consumer after request with one-shot mode"
argument_list|,
name|jms_exc
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/*      * Listen for available messages and wakeup any continuations.      */
specifier|private
specifier|static
class|class
name|Listener
implements|implements
name|MessageAvailableListener
block|{
name|MessageConsumer
name|consumer
decl_stmt|;
name|Continuation
name|continuation
decl_stmt|;
name|Listener
parameter_list|(
name|MessageConsumer
name|consumer
parameter_list|)
block|{
name|this
operator|.
name|consumer
operator|=
name|consumer
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
name|consumer
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
annotation|@
name|Override
specifier|public
name|void
name|onMessageAvailable
parameter_list|(
name|MessageConsumer
name|consumer
parameter_list|)
block|{
assert|assert
name|this
operator|.
name|consumer
operator|==
name|consumer
assert|;
operator|(
operator|(
name|MessageAvailableConsumer
operator|)
name|consumer
operator|)
operator|.
name|setAvailableListener
argument_list|(
literal|null
argument_list|)
expr_stmt|;
synchronized|synchronized
init|(
name|this
operator|.
name|consumer
init|)
block|{
if|if
condition|(
name|continuation
operator|!=
literal|null
condition|)
block|{
name|continuation
operator|.
name|resume
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
end_class

end_unit

