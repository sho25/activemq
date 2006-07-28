begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|Map
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
name|edu
operator|.
name|emory
operator|.
name|mathcs
operator|.
name|backport
operator|.
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ArrayBlockingQueue
import|;
end_import

begin_import
import|import
name|edu
operator|.
name|emory
operator|.
name|mathcs
operator|.
name|backport
operator|.
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeUnit
import|;
end_import

begin_comment
comment|/**  * A servlet which handles server side HTTP transport, delegating to the  * ActiveMQ broker. This servlet is designed for being embedded inside an  * ActiveMQ Broker using an embedded Jetty or Tomcat instance.  *   * @version $Revision$  */
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
name|Log
name|log
init|=
name|LogFactory
operator|.
name|getLog
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
name|TextWireFormat
name|wireFormat
decl_stmt|;
specifier|private
name|Map
name|clients
init|=
operator|new
name|HashMap
argument_list|()
decl_stmt|;
specifier|private
name|long
name|requestTimeout
init|=
literal|30000L
decl_stmt|;
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
return|return;
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
comment|//            while( packet !=null ) {
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
comment|//            	packet = (Command) transportChannel.getQueue().poll(0, TimeUnit.MILLISECONDS);
comment|//            }
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
comment|// Read the command directly from the reader
name|Command
name|command
init|=
name|wireFormat
operator|.
name|readCommand
argument_list|(
name|request
operator|.
name|getReader
argument_list|()
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
return|return;
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
comment|// TODO:
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
name|log
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
synchronized|synchronized
init|(
name|this
init|)
block|{
name|BlockingQueueTransport
name|answer
init|=
operator|(
name|BlockingQueueTransport
operator|)
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
name|log
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
name|log
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
synchronized|synchronized
init|(
name|this
init|)
block|{
name|BlockingQueueTransport
name|answer
init|=
operator|(
name|BlockingQueueTransport
operator|)
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
literal|"' has allready been established"
argument_list|)
expr_stmt|;
name|log
operator|.
name|warn
argument_list|(
literal|"A session for clientID '"
operator|+
name|clientID
operator|+
literal|"' has allready been established"
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
name|answer
operator|=
name|createTransportChannel
argument_list|()
expr_stmt|;
name|clients
operator|.
name|put
argument_list|(
name|clientID
argument_list|,
name|answer
argument_list|)
expr_stmt|;
name|listener
operator|.
name|onAccept
argument_list|(
name|answer
argument_list|)
expr_stmt|;
return|return
name|answer
return|;
block|}
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
name|ArrayBlockingQueue
argument_list|(
literal|10
argument_list|)
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

