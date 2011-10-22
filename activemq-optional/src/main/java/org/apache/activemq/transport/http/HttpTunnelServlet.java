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
name|transport
operator|.
name|http
package|;
end_package

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
name|DataOutputStream
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
name|io
operator|.
name|InputStreamReader
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
name|concurrent
operator|.
name|ConcurrentMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ConcurrentHashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|LinkedBlockingQueue
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeUnit
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
name|ServletInputStream
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
name|Service
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
name|Command
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
name|WireFormatInfo
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
name|transport
operator|.
name|Transport
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
name|transport
operator|.
name|TransportAcceptListener
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
name|transport
operator|.
name|util
operator|.
name|TextWireFormat
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
name|transport
operator|.
name|xstream
operator|.
name|XStreamWireFormat
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
name|util
operator|.
name|IOExceptionSupport
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
name|util
operator|.
name|ServiceListener
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
comment|/**  * A servlet which handles server side HTTP transport, delegating to the  * ActiveMQ broker. This servlet is designed for being embedded inside an  * ActiveMQ Broker using an embedded Jetty or Tomcat instance.  */
end_comment

begin_class
specifier|public
class|class
name|HttpTunnelServlet
extends|extends
name|HttpServlet
block|{
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
operator|-
literal|3826714430767484333L
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
name|HttpTunnelServlet
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|TransportAcceptListener
name|listener
decl_stmt|;
specifier|private
name|HttpTransportFactory
name|transportFactory
decl_stmt|;
specifier|private
name|TextWireFormat
name|wireFormat
decl_stmt|;
specifier|private
name|ConcurrentMap
argument_list|<
name|String
argument_list|,
name|BlockingQueueTransport
argument_list|>
name|clients
init|=
operator|new
name|ConcurrentHashMap
argument_list|<
name|String
argument_list|,
name|BlockingQueueTransport
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|long
name|requestTimeout
init|=
literal|30000L
decl_stmt|;
specifier|private
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|transportOptions
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
annotation|@
name|Override
specifier|public
name|void
name|init
parameter_list|()
throws|throws
name|ServletException
block|{
name|super
operator|.
name|init
argument_list|()
expr_stmt|;
name|listener
operator|=
operator|(
name|TransportAcceptListener
operator|)
name|getServletContext
argument_list|()
operator|.
name|getAttribute
argument_list|(
literal|"acceptListener"
argument_list|)
expr_stmt|;
if|if
condition|(
name|listener
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|ServletException
argument_list|(
literal|"No such attribute 'acceptListener' available in the ServletContext"
argument_list|)
throw|;
block|}
name|transportFactory
operator|=
operator|(
name|HttpTransportFactory
operator|)
name|getServletContext
argument_list|()
operator|.
name|getAttribute
argument_list|(
literal|"transportFactory"
argument_list|)
expr_stmt|;
if|if
condition|(
name|transportFactory
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|ServletException
argument_list|(
literal|"No such attribute 'transportFactory' available in the ServletContext"
argument_list|)
throw|;
block|}
name|transportOptions
operator|=
operator|(
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
operator|)
name|getServletContext
argument_list|()
operator|.
name|getAttribute
argument_list|(
literal|"transportOptions"
argument_list|)
expr_stmt|;
name|wireFormat
operator|=
operator|(
name|TextWireFormat
operator|)
name|getServletContext
argument_list|()
operator|.
name|getAttribute
argument_list|(
literal|"wireFormat"
argument_list|)
expr_stmt|;
if|if
condition|(
name|wireFormat
operator|==
literal|null
condition|)
block|{
name|wireFormat
operator|=
name|createWireFormat
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|protected
name|void
name|doHead
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
name|createTransportChannel
argument_list|(
name|request
argument_list|,
name|response
argument_list|)
expr_stmt|;
block|}
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
comment|// lets return the next response
name|Command
name|packet
init|=
literal|null
decl_stmt|;
name|int
name|count
init|=
literal|0
decl_stmt|;
try|try
block|{
name|BlockingQueueTransport
name|transportChannel
init|=
name|getTransportChannel
argument_list|(
name|request
argument_list|,
name|response
argument_list|)
decl_stmt|;
if|if
condition|(
name|transportChannel
operator|==
literal|null
condition|)
block|{
return|return;
block|}
name|packet
operator|=
operator|(
name|Command
operator|)
name|transportChannel
operator|.
name|getQueue
argument_list|()
operator|.
name|poll
argument_list|(
name|requestTimeout
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
expr_stmt|;
name|DataOutputStream
name|stream
init|=
operator|new
name|DataOutputStream
argument_list|(
name|response
operator|.
name|getOutputStream
argument_list|()
argument_list|)
decl_stmt|;
name|wireFormat
operator|.
name|marshal
argument_list|(
name|packet
argument_list|,
name|stream
argument_list|)
expr_stmt|;
name|count
operator|++
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ignore
parameter_list|)
block|{         }
if|if
condition|(
name|count
operator|==
literal|0
condition|)
block|{
name|response
operator|.
name|setStatus
argument_list|(
name|HttpServletResponse
operator|.
name|SC_REQUEST_TIMEOUT
argument_list|)
expr_stmt|;
block|}
block|}
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
comment|// Read the command directly from the reader, assuming UTF8 encoding
name|ServletInputStream
name|sis
init|=
name|request
operator|.
name|getInputStream
argument_list|()
decl_stmt|;
name|Command
name|command
init|=
operator|(
name|Command
operator|)
name|wireFormat
operator|.
name|unmarshalText
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
name|sis
argument_list|,
literal|"UTF-8"
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|command
operator|instanceof
name|WireFormatInfo
condition|)
block|{
name|WireFormatInfo
name|info
init|=
operator|(
name|WireFormatInfo
operator|)
name|command
decl_stmt|;
if|if
condition|(
operator|!
name|canProcessWireFormatVersion
argument_list|(
name|info
operator|.
name|getVersion
argument_list|()
argument_list|)
condition|)
block|{
name|response
operator|.
name|sendError
argument_list|(
name|HttpServletResponse
operator|.
name|SC_NOT_FOUND
argument_list|,
literal|"Cannot process wire format of version: "
operator|+
name|info
operator|.
name|getVersion
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|BlockingQueueTransport
name|transport
init|=
name|getTransportChannel
argument_list|(
name|request
argument_list|,
name|response
argument_list|)
decl_stmt|;
if|if
condition|(
name|transport
operator|==
literal|null
condition|)
block|{
return|return;
block|}
name|transport
operator|.
name|doConsume
argument_list|(
name|command
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|boolean
name|canProcessWireFormatVersion
parameter_list|(
name|int
name|version
parameter_list|)
block|{
return|return
literal|true
return|;
block|}
specifier|protected
name|String
name|readRequestBody
parameter_list|(
name|HttpServletRequest
name|request
parameter_list|)
throws|throws
name|IOException
block|{
name|StringBuffer
name|buffer
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
name|BufferedReader
name|reader
init|=
name|request
operator|.
name|getReader
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
else|else
block|{
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
block|}
return|return
name|buffer
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|protected
name|BlockingQueueTransport
name|getTransportChannel
parameter_list|(
name|HttpServletRequest
name|request
parameter_list|,
name|HttpServletResponse
name|response
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|clientID
init|=
name|request
operator|.
name|getHeader
argument_list|(
literal|"clientID"
argument_list|)
decl_stmt|;
if|if
condition|(
name|clientID
operator|==
literal|null
condition|)
block|{
name|response
operator|.
name|sendError
argument_list|(
name|HttpServletResponse
operator|.
name|SC_BAD_REQUEST
argument_list|,
literal|"No clientID header specified"
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|warn
argument_list|(
literal|"No clientID header specified"
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
name|BlockingQueueTransport
name|answer
init|=
name|clients
operator|.
name|get
argument_list|(
name|clientID
argument_list|)
decl_stmt|;
if|if
condition|(
name|answer
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"The clientID header specified is invalid. Client sesion has not yet been established for it: "
operator|+
name|clientID
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
return|return
name|answer
return|;
block|}
specifier|protected
name|BlockingQueueTransport
name|createTransportChannel
parameter_list|(
name|HttpServletRequest
name|request
parameter_list|,
name|HttpServletResponse
name|response
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|String
name|clientID
init|=
name|request
operator|.
name|getHeader
argument_list|(
literal|"clientID"
argument_list|)
decl_stmt|;
if|if
condition|(
name|clientID
operator|==
literal|null
condition|)
block|{
name|response
operator|.
name|sendError
argument_list|(
name|HttpServletResponse
operator|.
name|SC_BAD_REQUEST
argument_list|,
literal|"No clientID header specified"
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|warn
argument_list|(
literal|"No clientID header specified"
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
comment|// Optimistically create the client's transport; this transport may be thrown away if the client has already registered.
name|BlockingQueueTransport
name|answer
init|=
name|createTransportChannel
argument_list|()
decl_stmt|;
comment|// Record the client's transport and ensure that it has not already registered; this is thread-safe and only allows one
comment|// thread to register the client
if|if
condition|(
name|clients
operator|.
name|putIfAbsent
argument_list|(
name|clientID
argument_list|,
name|answer
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|response
operator|.
name|sendError
argument_list|(
name|HttpServletResponse
operator|.
name|SC_BAD_REQUEST
argument_list|,
literal|"A session for clientID '"
operator|+
name|clientID
operator|+
literal|"' has already been established"
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|warn
argument_list|(
literal|"A session for clientID '"
operator|+
name|clientID
operator|+
literal|"' has already been established"
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
comment|// Ensure that the client's transport is cleaned up when no longer
comment|// needed.
name|answer
operator|.
name|addServiceListener
argument_list|(
operator|new
name|ServiceListener
argument_list|()
block|{
specifier|public
name|void
name|started
parameter_list|(
name|Service
name|service
parameter_list|)
block|{
comment|// Nothing to do.
block|}
specifier|public
name|void
name|stopped
parameter_list|(
name|Service
name|service
parameter_list|)
block|{
name|clients
operator|.
name|remove
argument_list|(
name|clientID
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
comment|// Configure the transport with any additional properties or filters.  Although the returned transport is not explicitly
comment|// persisted, if it is a filter (e.g., InactivityMonitor) it will be linked to the client's transport as a TransportListener
comment|// and not GC'd until the client's transport is disposed.
name|Transport
name|transport
init|=
name|answer
decl_stmt|;
try|try
block|{
comment|// Preserve the transportOptions for future use by making a copy before applying (they are removed when applied).
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|options
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|(
name|transportOptions
argument_list|)
decl_stmt|;
name|transport
operator|=
name|transportFactory
operator|.
name|serverConfigure
argument_list|(
name|answer
argument_list|,
literal|null
argument_list|,
name|options
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
name|IOExceptionSupport
operator|.
name|create
argument_list|(
name|e
argument_list|)
throw|;
block|}
comment|// Wait for the transport to be connected or disposed.
name|listener
operator|.
name|onAccept
argument_list|(
name|transport
argument_list|)
expr_stmt|;
while|while
condition|(
operator|!
name|transport
operator|.
name|isConnected
argument_list|()
operator|&&
operator|!
name|transport
operator|.
name|isDisposed
argument_list|()
condition|)
block|{
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|100
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ignore
parameter_list|)
block|{             }
block|}
comment|// Ensure that the transport was not prematurely disposed.
if|if
condition|(
name|transport
operator|.
name|isDisposed
argument_list|()
condition|)
block|{
name|response
operator|.
name|sendError
argument_list|(
name|HttpServletResponse
operator|.
name|SC_BAD_REQUEST
argument_list|,
literal|"The session for clientID '"
operator|+
name|clientID
operator|+
literal|"' was prematurely disposed"
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|warn
argument_list|(
literal|"The session for clientID '"
operator|+
name|clientID
operator|+
literal|"' was prematurely disposed"
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
return|return
name|answer
return|;
block|}
specifier|protected
name|BlockingQueueTransport
name|createTransportChannel
parameter_list|()
block|{
return|return
operator|new
name|BlockingQueueTransport
argument_list|(
operator|new
name|LinkedBlockingQueue
argument_list|<
name|Object
argument_list|>
argument_list|()
argument_list|)
return|;
block|}
specifier|protected
name|TextWireFormat
name|createWireFormat
parameter_list|()
block|{
return|return
operator|new
name|XStreamWireFormat
argument_list|()
return|;
block|}
block|}
end_class

end_unit

