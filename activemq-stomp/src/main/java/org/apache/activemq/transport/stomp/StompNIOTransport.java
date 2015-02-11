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
name|stomp
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayInputStream
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
name|EOFException
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
name|Socket
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
name|UnknownHostException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|ByteBuffer
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|channels
operator|.
name|SelectionKey
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|channels
operator|.
name|SocketChannel
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|net
operator|.
name|SocketFactory
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
name|nio
operator|.
name|NIOOutputStream
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
name|nio
operator|.
name|SelectorManager
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
name|nio
operator|.
name|SelectorSelection
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
name|tcp
operator|.
name|TcpTransport
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
name|activemq
operator|.
name|wireformat
operator|.
name|WireFormat
import|;
end_import

begin_comment
comment|/**  * An implementation of the {@link Transport} interface for using Stomp over NIO  */
end_comment

begin_class
specifier|public
class|class
name|StompNIOTransport
extends|extends
name|TcpTransport
block|{
specifier|private
name|SocketChannel
name|channel
decl_stmt|;
specifier|private
name|SelectorSelection
name|selection
decl_stmt|;
specifier|private
name|ByteBuffer
name|inputBuffer
decl_stmt|;
name|StompCodec
name|codec
decl_stmt|;
specifier|public
name|StompNIOTransport
parameter_list|(
name|WireFormat
name|wireFormat
parameter_list|,
name|SocketFactory
name|socketFactory
parameter_list|,
name|URI
name|remoteLocation
parameter_list|,
name|URI
name|localLocation
parameter_list|)
throws|throws
name|UnknownHostException
throws|,
name|IOException
block|{
name|super
argument_list|(
name|wireFormat
argument_list|,
name|socketFactory
argument_list|,
name|remoteLocation
argument_list|,
name|localLocation
argument_list|)
expr_stmt|;
block|}
specifier|public
name|StompNIOTransport
parameter_list|(
name|WireFormat
name|wireFormat
parameter_list|,
name|Socket
name|socket
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|wireFormat
argument_list|,
name|socket
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|initializeStreams
parameter_list|()
throws|throws
name|IOException
block|{
name|channel
operator|=
name|socket
operator|.
name|getChannel
argument_list|()
expr_stmt|;
name|channel
operator|.
name|configureBlocking
argument_list|(
literal|false
argument_list|)
expr_stmt|;
comment|// listen for events telling us when the socket is readable.
name|selection
operator|=
name|SelectorManager
operator|.
name|getInstance
argument_list|()
operator|.
name|register
argument_list|(
name|channel
argument_list|,
operator|new
name|SelectorManager
operator|.
name|Listener
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onSelect
parameter_list|(
name|SelectorSelection
name|selection
parameter_list|)
block|{
name|serviceRead
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|onError
parameter_list|(
name|SelectorSelection
name|selection
parameter_list|,
name|Throwable
name|error
parameter_list|)
block|{
if|if
condition|(
name|error
operator|instanceof
name|IOException
condition|)
block|{
name|onException
argument_list|(
operator|(
name|IOException
operator|)
name|error
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|onException
argument_list|(
name|IOExceptionSupport
operator|.
name|create
argument_list|(
name|error
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
name|inputBuffer
operator|=
name|ByteBuffer
operator|.
name|allocate
argument_list|(
literal|8
operator|*
literal|1024
argument_list|)
expr_stmt|;
name|NIOOutputStream
name|outPutStream
init|=
operator|new
name|NIOOutputStream
argument_list|(
name|channel
argument_list|,
literal|8
operator|*
literal|1024
argument_list|)
decl_stmt|;
name|this
operator|.
name|dataOut
operator|=
operator|new
name|DataOutputStream
argument_list|(
name|outPutStream
argument_list|)
expr_stmt|;
name|this
operator|.
name|buffOut
operator|=
name|outPutStream
expr_stmt|;
name|codec
operator|=
operator|new
name|StompCodec
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|serviceRead
parameter_list|()
block|{
try|try
block|{
while|while
condition|(
literal|true
condition|)
block|{
comment|// read channel
name|int
name|readSize
init|=
name|channel
operator|.
name|read
argument_list|(
name|inputBuffer
argument_list|)
decl_stmt|;
comment|// channel is closed, cleanup
if|if
condition|(
name|readSize
operator|==
operator|-
literal|1
condition|)
block|{
name|onException
argument_list|(
operator|new
name|EOFException
argument_list|()
argument_list|)
expr_stmt|;
name|selection
operator|.
name|close
argument_list|()
expr_stmt|;
break|break;
block|}
comment|// nothing more to read, break
if|if
condition|(
name|readSize
operator|==
literal|0
condition|)
block|{
break|break;
block|}
name|receiveCounter
operator|+=
name|readSize
expr_stmt|;
name|inputBuffer
operator|.
name|flip
argument_list|()
expr_stmt|;
name|ByteArrayInputStream
name|input
init|=
operator|new
name|ByteArrayInputStream
argument_list|(
name|inputBuffer
operator|.
name|array
argument_list|()
argument_list|)
decl_stmt|;
name|codec
operator|.
name|parse
argument_list|(
name|input
argument_list|,
name|readSize
argument_list|)
expr_stmt|;
comment|// clear the buffer
name|inputBuffer
operator|.
name|clear
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
name|e
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|onException
argument_list|(
name|IOExceptionSupport
operator|.
name|create
argument_list|(
name|e
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|protected
name|void
name|doStart
parameter_list|()
throws|throws
name|Exception
block|{
name|connect
argument_list|()
expr_stmt|;
name|selection
operator|.
name|setInterestOps
argument_list|(
name|SelectionKey
operator|.
name|OP_READ
argument_list|)
expr_stmt|;
name|selection
operator|.
name|enable
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
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
try|try
block|{
if|if
condition|(
name|selection
operator|!=
literal|null
condition|)
block|{
name|selection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|super
operator|.
name|doStop
argument_list|(
name|stopper
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

