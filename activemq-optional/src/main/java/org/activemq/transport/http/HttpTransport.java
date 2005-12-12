begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**   *   * Copyright 2004 Protique Ltd  *   * Licensed under the Apache License, Version 2.0 (the "License");   * you may not use this file except in compliance with the License.   * You may obtain a copy of the License at   *   * http://www.apache.org/licenses/LICENSE-2.0  *   * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,   * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   * See the License for the specific language governing permissions and   * limitations under the License.   *   **/
end_comment

begin_package
package|package
name|org
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
name|activemq
operator|.
name|util
operator|.
name|Callback
import|;
end_import

begin_import
import|import
name|org
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
name|io
operator|.
name|OutputStreamWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Writer
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
name|MalformedURLException
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
name|net
operator|.
name|URL
import|;
end_import

begin_comment
comment|/**  * @version $Revision$  */
end_comment

begin_class
specifier|public
class|class
name|HttpTransport
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
name|HttpTransport
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|HttpURLConnection
name|sendConnection
decl_stmt|;
specifier|private
name|HttpURLConnection
name|receiveConnection
decl_stmt|;
specifier|private
name|URL
name|url
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
name|HttpTransport
parameter_list|(
name|TextWireFormat
name|wireFormat
parameter_list|,
name|URI
name|remoteUrl
parameter_list|)
throws|throws
name|MalformedURLException
block|{
name|super
argument_list|(
name|wireFormat
argument_list|,
name|remoteUrl
argument_list|)
expr_stmt|;
name|url
operator|=
operator|new
name|URL
argument_list|(
name|remoteUrl
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
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
try|try
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
name|HttpURLConnection
name|connection
init|=
name|getSendConnection
argument_list|()
decl_stmt|;
name|String
name|text
init|=
name|getTextWireFormat
argument_list|()
operator|.
name|toString
argument_list|(
name|command
argument_list|)
decl_stmt|;
name|Writer
name|writer
init|=
operator|new
name|OutputStreamWriter
argument_list|(
name|connection
operator|.
name|getOutputStream
argument_list|()
argument_list|)
decl_stmt|;
name|writer
operator|.
name|write
argument_list|(
name|text
argument_list|)
expr_stmt|;
name|writer
operator|.
name|flush
argument_list|()
expr_stmt|;
name|int
name|answer
init|=
name|connection
operator|.
name|getResponseCode
argument_list|()
decl_stmt|;
if|if
condition|(
name|answer
operator|!=
name|HttpURLConnection
operator|.
name|HTTP_OK
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
name|connection
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
literal|"HTTP GET consumer thread starting for transport: "
operator|+
name|this
argument_list|)
expr_stmt|;
name|URI
name|remoteUrl
init|=
name|getRemoteUrl
argument_list|()
decl_stmt|;
while|while
condition|(
operator|!
name|isClosed
argument_list|()
condition|)
block|{
try|try
block|{
name|HttpURLConnection
name|connection
init|=
name|getReceiveConnection
argument_list|()
decl_stmt|;
name|int
name|answer
init|=
name|connection
operator|.
name|getResponseCode
argument_list|()
decl_stmt|;
if|if
condition|(
name|answer
operator|!=
name|HttpURLConnection
operator|.
name|HTTP_OK
condition|)
block|{
if|if
condition|(
name|answer
operator|==
name|HttpURLConnection
operator|.
name|HTTP_CLIENT_TIMEOUT
condition|)
block|{
name|log
operator|.
name|trace
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
name|connection
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
name|connection
operator|.
name|getInputStream
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
literal|"Received null packet from url: "
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
name|Exception
name|e
parameter_list|)
block|{
if|if
condition|(
operator|!
name|isClosed
argument_list|()
condition|)
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
else|else
block|{
name|log
operator|.
name|trace
argument_list|(
literal|"Caught error after closed: "
operator|+
name|e
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
comment|// Implementation methods
comment|// -------------------------------------------------------------------------
specifier|protected
name|HttpURLConnection
name|createSendConnection
parameter_list|()
throws|throws
name|IOException
block|{
name|HttpURLConnection
name|conn
init|=
operator|(
name|HttpURLConnection
operator|)
name|getRemoteURL
argument_list|()
operator|.
name|openConnection
argument_list|()
decl_stmt|;
name|conn
operator|.
name|setDoOutput
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|conn
operator|.
name|setRequestMethod
argument_list|(
literal|"POST"
argument_list|)
expr_stmt|;
name|configureConnection
argument_list|(
name|conn
argument_list|)
expr_stmt|;
name|conn
operator|.
name|connect
argument_list|()
expr_stmt|;
return|return
name|conn
return|;
block|}
specifier|protected
name|HttpURLConnection
name|createReceiveConnection
parameter_list|()
throws|throws
name|IOException
block|{
name|HttpURLConnection
name|conn
init|=
operator|(
name|HttpURLConnection
operator|)
name|getRemoteURL
argument_list|()
operator|.
name|openConnection
argument_list|()
decl_stmt|;
name|conn
operator|.
name|setDoOutput
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|conn
operator|.
name|setDoInput
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|conn
operator|.
name|setRequestMethod
argument_list|(
literal|"GET"
argument_list|)
expr_stmt|;
name|configureConnection
argument_list|(
name|conn
argument_list|)
expr_stmt|;
name|conn
operator|.
name|connect
argument_list|()
expr_stmt|;
return|return
name|conn
return|;
block|}
specifier|protected
name|void
name|checkSession
parameter_list|(
name|HttpURLConnection
name|connection
parameter_list|)
block|{
name|String
name|set_cookie
init|=
name|connection
operator|.
name|getHeaderField
argument_list|(
literal|"Set-Cookie"
argument_list|)
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
specifier|protected
name|void
name|configureConnection
parameter_list|(
name|HttpURLConnection
name|connection
parameter_list|)
block|{
if|if
condition|(
name|sessionID
operator|!=
literal|null
condition|)
block|{
name|connection
operator|.
name|addRequestProperty
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
name|connection
operator|.
name|setRequestProperty
argument_list|(
literal|"clientID"
argument_list|,
name|clientID
argument_list|)
expr_stmt|;
block|}
block|}
specifier|protected
name|URL
name|getRemoteURL
parameter_list|()
block|{
return|return
name|url
return|;
block|}
specifier|protected
name|HttpURLConnection
name|getSendConnection
parameter_list|()
throws|throws
name|IOException
block|{
name|setSendConnection
argument_list|(
name|createSendConnection
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|sendConnection
return|;
block|}
specifier|protected
name|HttpURLConnection
name|getReceiveConnection
parameter_list|()
throws|throws
name|IOException
block|{
name|setReceiveConnection
argument_list|(
name|createReceiveConnection
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|receiveConnection
return|;
block|}
specifier|protected
name|void
name|setSendConnection
parameter_list|(
name|HttpURLConnection
name|conn
parameter_list|)
block|{
if|if
condition|(
name|sendConnection
operator|!=
literal|null
condition|)
block|{
name|sendConnection
operator|.
name|disconnect
argument_list|()
expr_stmt|;
block|}
name|sendConnection
operator|=
name|conn
expr_stmt|;
block|}
specifier|protected
name|void
name|setReceiveConnection
parameter_list|(
name|HttpURLConnection
name|conn
parameter_list|)
block|{
if|if
condition|(
name|receiveConnection
operator|!=
literal|null
condition|)
block|{
name|receiveConnection
operator|.
name|disconnect
argument_list|()
expr_stmt|;
block|}
name|receiveConnection
operator|=
name|conn
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
name|sendConnection
operator|!=
literal|null
condition|)
block|{
name|stopper
operator|.
name|run
argument_list|(
operator|new
name|Callback
argument_list|()
block|{
specifier|public
name|void
name|execute
parameter_list|()
throws|throws
name|Exception
block|{
name|sendConnection
operator|.
name|disconnect
argument_list|()
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|sendConnection
operator|=
literal|null
expr_stmt|;
block|}
if|if
condition|(
name|receiveConnection
operator|!=
literal|null
condition|)
block|{
name|stopper
operator|.
name|run
argument_list|(
operator|new
name|Callback
argument_list|()
block|{
specifier|public
name|void
name|execute
parameter_list|()
throws|throws
name|Exception
block|{
name|receiveConnection
operator|.
name|disconnect
argument_list|()
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|receiveConnection
operator|=
literal|null
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

