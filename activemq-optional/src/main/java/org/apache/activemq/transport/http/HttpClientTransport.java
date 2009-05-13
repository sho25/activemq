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
name|io
operator|.
name|InterruptedIOException
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
name|ShutdownInfo
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
name|ByteArrayInputStream
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
name|IdGenerator
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
name|HttpMethodRetryHandler
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
name|NoHttpResponseException
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
name|HeadMethod
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
name|InputStreamRequestEntity
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
name|httpclient
operator|.
name|params
operator|.
name|HttpClientParams
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
name|params
operator|.
name|HttpMethodParams
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

begin_comment
comment|/**  * A HTTP {@link org.apache.activemq.transport.TransportChannel} which uses the  *<a href="http://jakarta.apache.org/commons/httpclient/">commons-httpclient</a>  * library  *   * @version $Revision$  */
end_comment

begin_class
specifier|public
class|class
name|HttpClientTransport
extends|extends
name|HttpTransportSupport
block|{
specifier|public
specifier|static
specifier|final
name|int
name|MAX_CLIENT_TIMEOUT
init|=
literal|30000
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Log
name|LOG
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
specifier|private
specifier|static
specifier|final
name|IdGenerator
name|CLIENT_ID_GENERATOR
init|=
operator|new
name|IdGenerator
argument_list|()
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
specifier|final
name|String
name|clientID
init|=
name|CLIENT_ID_GENERATOR
operator|.
name|generateId
argument_list|()
decl_stmt|;
specifier|private
name|boolean
name|trace
decl_stmt|;
specifier|private
name|GetMethod
name|httpMethod
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
name|Object
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
name|Object
name|command
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|isStopped
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"stopped."
argument_list|)
throw|;
block|}
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
name|String
name|data
init|=
name|getTextWireFormat
argument_list|()
operator|.
name|marshalText
argument_list|(
name|command
argument_list|)
decl_stmt|;
name|byte
index|[]
name|bytes
init|=
name|data
operator|.
name|getBytes
argument_list|(
literal|"UTF-8"
argument_list|)
decl_stmt|;
name|InputStreamRequestEntity
name|entity
init|=
operator|new
name|InputStreamRequestEntity
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
name|bytes
argument_list|)
argument_list|)
decl_stmt|;
name|httpMethod
operator|.
name|setRequestEntity
argument_list|(
name|entity
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
name|HttpClientParams
name|params
init|=
operator|new
name|HttpClientParams
argument_list|()
decl_stmt|;
name|params
operator|.
name|setSoTimeout
argument_list|(
name|MAX_CLIENT_TIMEOUT
argument_list|)
expr_stmt|;
name|client
operator|.
name|setParams
argument_list|(
name|params
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
if|if
condition|(
name|command
operator|instanceof
name|ShutdownInfo
condition|)
block|{
try|try
block|{
name|stop
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Error trying to stop HTTP client: "
operator|+
name|e
argument_list|,
name|e
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
name|Object
name|request
parameter_list|(
name|Object
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
name|LOG
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
operator|&&
operator|!
name|isStopping
argument_list|()
condition|)
block|{
name|httpMethod
operator|=
operator|new
name|GetMethod
argument_list|(
name|remoteUrl
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
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
name|LOG
operator|.
name|debug
argument_list|(
literal|"GET timed out"
argument_list|)
expr_stmt|;
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|onException
argument_list|(
operator|new
name|InterruptedIOException
argument_list|()
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
else|else
block|{
name|onException
argument_list|(
operator|new
name|IOException
argument_list|(
literal|"Failed to perform GET on: "
operator|+
name|remoteUrl
operator|+
literal|" as response was: "
operator|+
name|answer
argument_list|)
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
else|else
block|{
name|DataInputStream
name|stream
init|=
operator|new
name|DataInputStream
argument_list|(
name|httpMethod
operator|.
name|getResponseBodyAsStream
argument_list|()
argument_list|)
decl_stmt|;
name|Object
name|command
init|=
operator|(
name|Object
operator|)
name|getTextWireFormat
argument_list|()
operator|.
name|unmarshal
argument_list|(
name|stream
argument_list|)
decl_stmt|;
if|if
condition|(
name|command
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|debug
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
name|onException
argument_list|(
name|IOExceptionSupport
operator|.
name|create
argument_list|(
literal|"Failed to perform GET on: "
operator|+
name|remoteUrl
operator|+
literal|" Reason: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
argument_list|)
expr_stmt|;
break|break;
block|}
finally|finally
block|{
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
name|doStart
parameter_list|()
throws|throws
name|Exception
block|{
name|LOG
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
name|HeadMethod
name|httpMethod
init|=
operator|new
name|HeadMethod
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
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Failed to perform GET on: "
operator|+
name|remoteUrl
operator|+
literal|" as response was: "
operator|+
name|answer
argument_list|)
throw|;
block|}
name|super
operator|.
name|doStart
argument_list|()
expr_stmt|;
block|}
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
name|httpMethod
operator|.
name|abort
argument_list|()
expr_stmt|;
block|}
specifier|protected
name|HttpClient
name|createHttpClient
parameter_list|()
block|{
name|HttpClient
name|client
init|=
operator|new
name|HttpClient
argument_list|()
decl_stmt|;
if|if
condition|(
name|getProxyHost
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|client
operator|.
name|getHostConfiguration
argument_list|()
operator|.
name|setProxy
argument_list|(
name|getProxyHost
argument_list|()
argument_list|,
name|getProxyPort
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|client
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
specifier|public
name|boolean
name|isTrace
parameter_list|()
block|{
return|return
name|trace
return|;
block|}
specifier|public
name|void
name|setTrace
parameter_list|(
name|boolean
name|trace
parameter_list|)
block|{
name|this
operator|.
name|trace
operator|=
name|trace
expr_stmt|;
block|}
block|}
end_class

end_unit

