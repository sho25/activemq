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
name|java
operator|.
name|util
operator|.
name|zip
operator|.
name|GZIPInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|zip
operator|.
name|GZIPOutputStream
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
name|ByteArrayOutputStream
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
name|http
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
name|http
operator|.
name|HttpHost
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|HttpRequest
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|HttpRequestInterceptor
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|HttpResponse
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
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
name|http
operator|.
name|auth
operator|.
name|AuthScope
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|auth
operator|.
name|UsernamePasswordCredentials
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|client
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
name|http
operator|.
name|client
operator|.
name|HttpResponseException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|client
operator|.
name|ResponseHandler
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|client
operator|.
name|methods
operator|.
name|HttpGet
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|client
operator|.
name|methods
operator|.
name|HttpHead
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|client
operator|.
name|methods
operator|.
name|HttpOptions
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|client
operator|.
name|methods
operator|.
name|HttpPost
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|conn
operator|.
name|params
operator|.
name|ConnRoutePNames
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|entity
operator|.
name|ByteArrayEntity
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|impl
operator|.
name|client
operator|.
name|BasicResponseHandler
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|impl
operator|.
name|client
operator|.
name|DefaultHttpClient
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|impl
operator|.
name|conn
operator|.
name|PoolingClientConnectionManager
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|message
operator|.
name|AbstractHttpMessage
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|params
operator|.
name|HttpConnectionParams
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|params
operator|.
name|HttpParams
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|protocol
operator|.
name|HttpContext
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|util
operator|.
name|EntityUtils
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
comment|/**  * A HTTP {@link org.apache.activemq.transport.Transport} which uses the  *<a href="http://hc.apache.org/index.html">Apache HTTP Client</a>  * library  */
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
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
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
name|HttpGet
name|httpMethod
decl_stmt|;
specifier|private
specifier|volatile
name|int
name|receiveCounter
decl_stmt|;
specifier|private
name|int
name|soTimeout
init|=
name|MAX_CLIENT_TIMEOUT
decl_stmt|;
specifier|private
name|boolean
name|useCompression
init|=
literal|false
decl_stmt|;
specifier|private
name|boolean
name|canSendCompressed
init|=
literal|false
decl_stmt|;
specifier|private
name|int
name|minSendAsCompressedSize
init|=
literal|0
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
name|HttpPost
name|httpMethod
init|=
operator|new
name|HttpPost
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
if|if
condition|(
name|useCompression
operator|&&
name|canSendCompressed
operator|&&
name|bytes
operator|.
name|length
operator|>
name|minSendAsCompressedSize
condition|)
block|{
name|ByteArrayOutputStream
name|bytesOut
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|GZIPOutputStream
name|stream
init|=
operator|new
name|GZIPOutputStream
argument_list|(
name|bytesOut
argument_list|)
decl_stmt|;
name|stream
operator|.
name|write
argument_list|(
name|bytes
argument_list|)
expr_stmt|;
name|stream
operator|.
name|close
argument_list|()
expr_stmt|;
name|httpMethod
operator|.
name|addHeader
argument_list|(
literal|"Content-Type"
argument_list|,
literal|"application/x-gzip"
argument_list|)
expr_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isTraceEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|trace
argument_list|(
literal|"Sending compressed, size = "
operator|+
name|bytes
operator|.
name|length
operator|+
literal|", compressed size = "
operator|+
name|bytesOut
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|bytes
operator|=
name|bytesOut
operator|.
name|toByteArray
argument_list|()
expr_stmt|;
block|}
name|ByteArrayEntity
name|entity
init|=
operator|new
name|ByteArrayEntity
argument_list|(
name|bytes
argument_list|)
decl_stmt|;
name|httpMethod
operator|.
name|setEntity
argument_list|(
name|entity
argument_list|)
expr_stmt|;
name|HttpClient
name|client
init|=
literal|null
decl_stmt|;
name|HttpResponse
name|answer
init|=
literal|null
decl_stmt|;
try|try
block|{
name|client
operator|=
name|getSendHttpClient
argument_list|()
expr_stmt|;
name|HttpParams
name|params
init|=
name|client
operator|.
name|getParams
argument_list|()
decl_stmt|;
name|HttpConnectionParams
operator|.
name|setSoTimeout
argument_list|(
name|params
argument_list|,
name|soTimeout
argument_list|)
expr_stmt|;
name|answer
operator|=
name|client
operator|.
name|execute
argument_list|(
name|httpMethod
argument_list|)
expr_stmt|;
name|int
name|status
init|=
name|answer
operator|.
name|getStatusLine
argument_list|()
operator|.
name|getStatusCode
argument_list|()
decl_stmt|;
if|if
condition|(
name|status
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
if|if
condition|(
name|answer
operator|!=
literal|null
condition|)
block|{
name|EntityUtils
operator|.
name|consume
argument_list|(
name|answer
operator|.
name|getEntity
argument_list|()
argument_list|)
expr_stmt|;
block|}
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
specifier|private
name|DataInputStream
name|createDataInputStream
parameter_list|(
name|HttpResponse
name|answer
parameter_list|)
throws|throws
name|IOException
block|{
name|Header
name|encoding
init|=
name|answer
operator|.
name|getEntity
argument_list|()
operator|.
name|getContentEncoding
argument_list|()
decl_stmt|;
if|if
condition|(
name|encoding
operator|!=
literal|null
operator|&&
literal|"gzip"
operator|.
name|equalsIgnoreCase
argument_list|(
name|encoding
operator|.
name|getValue
argument_list|()
argument_list|)
condition|)
block|{
return|return
operator|new
name|DataInputStream
argument_list|(
operator|new
name|GZIPInputStream
argument_list|(
name|answer
operator|.
name|getEntity
argument_list|()
operator|.
name|getContent
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
else|else
block|{
return|return
operator|new
name|DataInputStream
argument_list|(
name|answer
operator|.
name|getEntity
argument_list|()
operator|.
name|getContent
argument_list|()
argument_list|)
return|;
block|}
block|}
specifier|public
name|void
name|run
parameter_list|()
block|{
if|if
condition|(
name|LOG
operator|.
name|isTraceEnabled
argument_list|()
condition|)
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
block|}
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
name|HttpGet
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
name|HttpResponse
name|answer
init|=
literal|null
decl_stmt|;
try|try
block|{
name|answer
operator|=
name|httpClient
operator|.
name|execute
argument_list|(
name|httpMethod
argument_list|)
expr_stmt|;
name|int
name|status
init|=
name|answer
operator|.
name|getStatusLine
argument_list|()
operator|.
name|getStatusCode
argument_list|()
decl_stmt|;
if|if
condition|(
name|status
operator|!=
name|HttpStatus
operator|.
name|SC_OK
condition|)
block|{
if|if
condition|(
name|status
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
name|receiveCounter
operator|++
expr_stmt|;
name|DataInputStream
name|stream
init|=
name|createDataInputStream
argument_list|(
name|answer
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
name|stream
operator|.
name|close
argument_list|()
expr_stmt|;
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
if|if
condition|(
name|answer
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|EntityUtils
operator|.
name|consume
argument_list|(
name|answer
operator|.
name|getEntity
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{                     }
block|}
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
if|if
condition|(
name|LOG
operator|.
name|isTraceEnabled
argument_list|()
condition|)
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
block|}
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
name|HttpHead
name|httpMethod
init|=
operator|new
name|HttpHead
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
comment|// Request the options from the server so we can find out if the broker we are
comment|// talking to supports GZip compressed content.  If so and useCompression is on
comment|// then we can compress our POST data, otherwise we must send it uncompressed to
comment|// ensure backwards compatibility.
name|HttpOptions
name|optionsMethod
init|=
operator|new
name|HttpOptions
argument_list|(
name|remoteUrl
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|ResponseHandler
argument_list|<
name|String
argument_list|>
name|handler
init|=
operator|new
name|BasicResponseHandler
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|String
name|handleResponse
parameter_list|(
name|HttpResponse
name|response
parameter_list|)
throws|throws
name|HttpResponseException
throws|,
name|IOException
block|{
for|for
control|(
name|Header
name|header
range|:
name|response
operator|.
name|getAllHeaders
argument_list|()
control|)
block|{
if|if
condition|(
name|header
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
literal|"Accepts-Encoding"
argument_list|)
operator|&&
name|header
operator|.
name|getValue
argument_list|()
operator|.
name|contains
argument_list|(
literal|"gzip"
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Broker Servlet supports GZip compression."
argument_list|)
expr_stmt|;
name|canSendCompressed
operator|=
literal|true
expr_stmt|;
break|break;
block|}
block|}
return|return
name|super
operator|.
name|handleResponse
argument_list|(
name|response
argument_list|)
return|;
block|}
block|}
decl_stmt|;
try|try
block|{
name|httpClient
operator|.
name|execute
argument_list|(
name|httpMethod
argument_list|,
operator|new
name|BasicResponseHandler
argument_list|()
argument_list|)
expr_stmt|;
name|httpClient
operator|.
name|execute
argument_list|(
name|optionsMethod
argument_list|,
name|handler
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
operator|new
name|IOException
argument_list|(
literal|"Failed to perform GET on: "
operator|+
name|remoteUrl
operator|+
literal|" as response was: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
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
if|if
condition|(
name|httpMethod
operator|!=
literal|null
condition|)
block|{
name|httpMethod
operator|.
name|abort
argument_list|()
expr_stmt|;
block|}
block|}
specifier|protected
name|HttpClient
name|createHttpClient
parameter_list|()
block|{
name|DefaultHttpClient
name|client
init|=
operator|new
name|DefaultHttpClient
argument_list|(
operator|new
name|PoolingClientConnectionManager
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|useCompression
condition|)
block|{
name|client
operator|.
name|addRequestInterceptor
argument_list|(
operator|new
name|HttpRequestInterceptor
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|process
parameter_list|(
name|HttpRequest
name|request
parameter_list|,
name|HttpContext
name|context
parameter_list|)
block|{
comment|// We expect to received a compression response that we un-gzip
name|request
operator|.
name|addHeader
argument_list|(
literal|"Accept-Encoding"
argument_list|,
literal|"gzip"
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|getProxyHost
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|HttpHost
name|proxy
init|=
operator|new
name|HttpHost
argument_list|(
name|getProxyHost
argument_list|()
argument_list|,
name|getProxyPort
argument_list|()
argument_list|)
decl_stmt|;
name|client
operator|.
name|getParams
argument_list|()
operator|.
name|setParameter
argument_list|(
name|ConnRoutePNames
operator|.
name|DEFAULT_PROXY
argument_list|,
name|proxy
argument_list|)
expr_stmt|;
if|if
condition|(
name|getProxyUser
argument_list|()
operator|!=
literal|null
operator|&&
name|getProxyPassword
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|client
operator|.
name|getCredentialsProvider
argument_list|()
operator|.
name|setCredentials
argument_list|(
operator|new
name|AuthScope
argument_list|(
name|getProxyHost
argument_list|()
argument_list|,
name|getProxyPort
argument_list|()
argument_list|)
argument_list|,
operator|new
name|UsernamePasswordCredentials
argument_list|(
name|getProxyUser
argument_list|()
argument_list|,
name|getProxyPassword
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|client
return|;
block|}
specifier|protected
name|void
name|configureMethod
parameter_list|(
name|AbstractHttpMessage
name|method
parameter_list|)
block|{
name|method
operator|.
name|setHeader
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
specifier|public
name|int
name|getReceiveCounter
parameter_list|()
block|{
return|return
name|receiveCounter
return|;
block|}
specifier|public
name|int
name|getSoTimeout
parameter_list|()
block|{
return|return
name|soTimeout
return|;
block|}
specifier|public
name|void
name|setSoTimeout
parameter_list|(
name|int
name|soTimeout
parameter_list|)
block|{
name|this
operator|.
name|soTimeout
operator|=
name|soTimeout
expr_stmt|;
block|}
specifier|public
name|void
name|setUseCompression
parameter_list|(
name|boolean
name|useCompression
parameter_list|)
block|{
name|this
operator|.
name|useCompression
operator|=
name|useCompression
expr_stmt|;
block|}
specifier|public
name|boolean
name|isUseCompression
parameter_list|()
block|{
return|return
name|this
operator|.
name|useCompression
return|;
block|}
specifier|public
name|int
name|getMinSendAsCompressedSize
parameter_list|()
block|{
return|return
name|minSendAsCompressedSize
return|;
block|}
comment|/**      * Sets the minimum size that must be exceeded on a send before compression is used if      * the useCompression option is specified.  For very small payloads compression can be      * inefficient compared to the transmission size savings.      *      * Default value is 0.      *      * @param minSendAsCompressedSize      */
specifier|public
name|void
name|setMinSendAsCompressedSize
parameter_list|(
name|int
name|minSendAsCompressedSize
parameter_list|)
block|{
name|this
operator|.
name|minSendAsCompressedSize
operator|=
name|minSendAsCompressedSize
expr_stmt|;
block|}
block|}
end_class

end_unit
