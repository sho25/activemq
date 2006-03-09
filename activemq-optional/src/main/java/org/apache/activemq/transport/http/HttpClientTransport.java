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
name|transport
operator|.
name|http
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
name|ConnectionInfo
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
name|Response
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
name|FutureResponse
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
name|ServiceStopper
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
name|httpclient
operator|.
name|Header
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
name|httpclient
operator|.
name|HttpClient
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
name|httpclient
operator|.
name|HttpMethod
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
name|httpclient
operator|.
name|HttpStatus
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
name|httpclient
operator|.
name|methods
operator|.
name|GetMethod
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
name|httpclient
operator|.
name|methods
operator|.
name|PostMethod
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
name|java
operator|.
name|io
operator|.
name|DataInputStream
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
name|net
operator|.
name|HttpURLConnection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URI
import|;
end_import

begin_comment
comment|/**  * A HTTP {@link org.apache.activemq.transport.TransportChannel} which uses the<a  * href="http://jakarta.apache.org/commons/httpclient/">commons-httpclient</a>  * library  *   * @version $Revision$  */
end_comment

begin_class
specifier|public
class|class
name|HttpClientTransport
extends|extends
name|HttpTransportSupport
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
name|HttpClientTransport
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|int
name|MAX_CLIENT_TIMEOUT
init|=
literal|30000
decl_stmt|;
specifier|private
name|HttpClient
name|sendHttpClient
decl_stmt|;
specifier|private
name|HttpClient
name|receiveHttpClient
decl_stmt|;
specifier|private
name|String
name|clientID
decl_stmt|;
specifier|private
name|String
name|sessionID
decl_stmt|;
specifier|public
name|HttpClientTransport
parameter_list|(
name|TextWireFormat
name|wireFormat
parameter_list|,
name|URI
name|remoteUrl
parameter_list|)
block|{
name|super
argument_list|(
name|wireFormat
argument_list|,
name|remoteUrl
argument_list|)
expr_stmt|;
block|}
specifier|public
name|FutureResponse
name|asyncRequest
parameter_list|(
name|Command
name|command
parameter_list|)
throws|throws
name|IOException
block|{
return|return
literal|null
return|;
block|}
specifier|public
name|void
name|oneway
parameter_list|(
name|Command
name|command
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|command
operator|.
name|getDataStructureType
argument_list|()
operator|==
name|ConnectionInfo
operator|.
name|DATA_STRUCTURE_TYPE
condition|)
name|clientID
operator|=
operator|(
operator|(
name|ConnectionInfo
operator|)
name|command
operator|)
operator|.
name|getClientId
argument_list|()
expr_stmt|;
name|PostMethod
name|httpMethod
init|=
operator|new
name|PostMethod
argument_list|(
name|getRemoteUrl
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|configureMethod
argument_list|(
name|httpMethod
argument_list|)
expr_stmt|;
name|httpMethod
operator|.
name|setRequestBody
argument_list|(
name|getTextWireFormat
argument_list|()
operator|.
name|toString
argument_list|(
name|command
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
name|HttpClient
name|client
init|=
name|getSendHttpClient
argument_list|()
decl_stmt|;
name|client
operator|.
name|setTimeout
argument_list|(
name|MAX_CLIENT_TIMEOUT
argument_list|)
expr_stmt|;
name|int
name|answer
init|=
name|client
operator|.
name|executeMethod
argument_list|(
name|httpMethod
argument_list|)
decl_stmt|;
if|if
condition|(
name|answer
operator|!=
name|HttpStatus
operator|.
name|SC_OK
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Failed to post command: "
operator|+
name|command
operator|+
literal|" as response was: "
operator|+
name|answer
argument_list|)
throw|;
block|}
name|checkSession
argument_list|(
name|httpMethod
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
name|IOExceptionSupport
operator|.
name|create
argument_list|(
literal|"Could not post command: "
operator|+
name|command
operator|+
literal|" due to: "
operator|+
name|e
argument_list|,
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
name|httpMethod
operator|.
name|getResponseBody
argument_list|()
expr_stmt|;
name|httpMethod
operator|.
name|releaseConnection
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|Response
name|request
parameter_list|(
name|Command
name|command
parameter_list|)
throws|throws
name|IOException
block|{
return|return
literal|null
return|;
block|}
specifier|public
name|void
name|run
parameter_list|()
block|{
name|log
operator|.
name|trace
argument_list|(
literal|"HTTP GET consumer thread starting: "
operator|+
name|this
argument_list|)
expr_stmt|;
name|HttpClient
name|httpClient
init|=
name|getReceiveHttpClient
argument_list|()
decl_stmt|;
name|URI
name|remoteUrl
init|=
name|getRemoteUrl
argument_list|()
decl_stmt|;
while|while
condition|(
operator|!
name|isStopped
argument_list|()
condition|)
block|{
name|GetMethod
name|httpMethod
init|=
operator|new
name|GetMethod
argument_list|(
name|remoteUrl
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|configureMethod
argument_list|(
name|httpMethod
argument_list|)
expr_stmt|;
try|try
block|{
name|int
name|answer
init|=
name|httpClient
operator|.
name|executeMethod
argument_list|(
name|httpMethod
argument_list|)
decl_stmt|;
if|if
condition|(
name|answer
operator|!=
name|HttpStatus
operator|.
name|SC_OK
condition|)
block|{
if|if
condition|(
name|answer
operator|==
name|HttpStatus
operator|.
name|SC_REQUEST_TIMEOUT
condition|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"GET timed out"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Failed to perform GET on: "
operator|+
name|remoteUrl
operator|+
literal|" as response was: "
operator|+
name|answer
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|checkSession
argument_list|(
name|httpMethod
argument_list|)
expr_stmt|;
name|Command
name|command
init|=
name|getTextWireFormat
argument_list|()
operator|.
name|readCommand
argument_list|(
operator|new
name|DataInputStream
argument_list|(
name|httpMethod
operator|.
name|getResponseBodyAsStream
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|command
operator|==
literal|null
condition|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Received null command from url: "
operator|+
name|remoteUrl
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|doConsume
argument_list|(
name|command
argument_list|)
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Failed to perform GET on: "
operator|+
name|remoteUrl
operator|+
literal|" due to: "
operator|+
name|e
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|httpMethod
operator|.
name|getResponseBody
argument_list|()
expr_stmt|;
name|httpMethod
operator|.
name|releaseConnection
argument_list|()
expr_stmt|;
block|}
block|}
block|}
comment|// Properties
comment|// -------------------------------------------------------------------------
specifier|public
name|HttpClient
name|getSendHttpClient
parameter_list|()
block|{
if|if
condition|(
name|sendHttpClient
operator|==
literal|null
condition|)
block|{
name|sendHttpClient
operator|=
name|createHttpClient
argument_list|()
expr_stmt|;
block|}
return|return
name|sendHttpClient
return|;
block|}
specifier|public
name|void
name|setSendHttpClient
parameter_list|(
name|HttpClient
name|sendHttpClient
parameter_list|)
block|{
name|this
operator|.
name|sendHttpClient
operator|=
name|sendHttpClient
expr_stmt|;
block|}
specifier|public
name|HttpClient
name|getReceiveHttpClient
parameter_list|()
block|{
if|if
condition|(
name|receiveHttpClient
operator|==
literal|null
condition|)
block|{
name|receiveHttpClient
operator|=
name|createHttpClient
argument_list|()
expr_stmt|;
block|}
return|return
name|receiveHttpClient
return|;
block|}
specifier|public
name|void
name|setReceiveHttpClient
parameter_list|(
name|HttpClient
name|receiveHttpClient
parameter_list|)
block|{
name|this
operator|.
name|receiveHttpClient
operator|=
name|receiveHttpClient
expr_stmt|;
block|}
comment|// Implementation methods
comment|// -------------------------------------------------------------------------
specifier|protected
name|void
name|doStop
parameter_list|(
name|ServiceStopper
name|stopper
parameter_list|)
throws|throws
name|Exception
block|{
comment|// TODO
block|}
specifier|protected
name|HttpClient
name|createHttpClient
parameter_list|()
block|{
return|return
operator|new
name|HttpClient
argument_list|()
return|;
block|}
specifier|protected
name|void
name|configureMethod
parameter_list|(
name|HttpMethod
name|method
parameter_list|)
block|{
if|if
condition|(
name|sessionID
operator|!=
literal|null
condition|)
block|{
name|method
operator|.
name|addRequestHeader
argument_list|(
literal|"Cookie"
argument_list|,
literal|"JSESSIONID="
operator|+
name|sessionID
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|clientID
operator|!=
literal|null
condition|)
block|{
name|method
operator|.
name|setRequestHeader
argument_list|(
literal|"clientID"
argument_list|,
name|clientID
argument_list|)
expr_stmt|;
block|}
block|}
specifier|protected
name|void
name|checkSession
parameter_list|(
name|HttpMethod
name|client
parameter_list|)
block|{
name|Header
name|header
init|=
name|client
operator|.
name|getRequestHeader
argument_list|(
literal|"Set-Cookie"
argument_list|)
decl_stmt|;
if|if
condition|(
name|header
operator|!=
literal|null
condition|)
block|{
name|String
name|set_cookie
init|=
name|header
operator|.
name|getValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|set_cookie
operator|!=
literal|null
operator|&&
name|set_cookie
operator|.
name|startsWith
argument_list|(
literal|"JSESSIONID="
argument_list|)
condition|)
block|{
name|String
index|[]
name|bits
init|=
name|set_cookie
operator|.
name|split
argument_list|(
literal|"[=;]"
argument_list|)
decl_stmt|;
name|sessionID
operator|=
name|bits
index|[
literal|1
index|]
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

