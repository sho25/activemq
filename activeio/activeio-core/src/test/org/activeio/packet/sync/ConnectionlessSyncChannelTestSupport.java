begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Copyright 2004 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|activeio
operator|.
name|packet
operator|.
name|sync
package|;
end_package

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
name|Executor
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
name|ScheduledThreadPoolExecutor
import|;
end_import

begin_import
import|import
name|org
operator|.
name|activeio
operator|.
name|Channel
import|;
end_import

begin_import
import|import
name|org
operator|.
name|activeio
operator|.
name|adapter
operator|.
name|AsyncToSyncChannel
import|;
end_import

begin_import
import|import
name|org
operator|.
name|activeio
operator|.
name|packet
operator|.
name|ByteArrayPacket
import|;
end_import

begin_import
import|import
name|org
operator|.
name|activeio
operator|.
name|packet
operator|.
name|FilterPacket
import|;
end_import

begin_import
import|import
name|org
operator|.
name|activeio
operator|.
name|packet
operator|.
name|Packet
import|;
end_import

begin_import
import|import
name|org
operator|.
name|activeio
operator|.
name|packet
operator|.
name|sync
operator|.
name|SyncChannel
import|;
end_import

begin_import
import|import
name|org
operator|.
name|activeio
operator|.
name|packet
operator|.
name|sync
operator|.
name|datagram
operator|.
name|DatagramContext
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
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|DatagramPacket
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|InetSocketAddress
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|SocketAddress
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|SocketException
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
name|URISyntaxException
import|;
end_import

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|TestCase
import|;
end_import

begin_comment
comment|/**  * @version $Revision$  */
end_comment

begin_class
specifier|abstract
specifier|public
class|class
name|ConnectionlessSyncChannelTestSupport
extends|extends
name|TestCase
block|{
specifier|private
specifier|final
name|Log
name|log
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|ConnectionlessSyncChannelTestSupport
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|SyncChannel
name|clientChannel
decl_stmt|;
specifier|private
name|SyncChannel
name|serverChannel
decl_stmt|;
specifier|private
specifier|final
name|Executor
name|sendExecutor
init|=
operator|new
name|ScheduledThreadPoolExecutor
argument_list|(
literal|1
argument_list|)
decl_stmt|;
specifier|public
name|void
name|testSmallSendReceive
parameter_list|()
throws|throws
name|IOException
throws|,
name|URISyntaxException
throws|,
name|InterruptedException
block|{
if|if
condition|(
name|isDisabled
argument_list|()
condition|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"test disabled: "
operator|+
name|getName
argument_list|()
argument_list|)
expr_stmt|;
return|return;
block|}
name|UDPFilterPacket
name|fp
init|=
operator|new
name|UDPFilterPacket
argument_list|(
literal|"Hello World"
operator|.
name|getBytes
argument_list|()
argument_list|,
operator|new
name|InetSocketAddress
argument_list|(
name|getAddress
argument_list|()
argument_list|,
literal|4444
argument_list|)
argument_list|)
decl_stmt|;
name|doSendReceive
argument_list|(
name|fp
operator|.
name|duplicate
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testManySmallSendReceives
parameter_list|()
throws|throws
name|IOException
throws|,
name|URISyntaxException
throws|,
name|InterruptedException
block|{
if|if
condition|(
name|isDisabled
argument_list|()
condition|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"test disabled: "
operator|+
name|getName
argument_list|()
argument_list|)
expr_stmt|;
return|return;
block|}
name|log
operator|.
name|info
argument_list|(
literal|"Start of testManySmallSendReceives"
argument_list|)
expr_stmt|;
name|UDPFilterPacket
name|fp
init|=
operator|new
name|UDPFilterPacket
argument_list|(
literal|"Hello World"
operator|.
name|getBytes
argument_list|()
argument_list|,
operator|new
name|InetSocketAddress
argument_list|(
name|getAddress
argument_list|()
argument_list|,
literal|4444
argument_list|)
argument_list|)
decl_stmt|;
name|long
name|start
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|getTestIterations
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|doSendReceive
argument_list|(
name|fp
operator|.
name|duplicate
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|long
name|end
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"done. Duration: "
operator|+
name|duration
argument_list|(
name|start
argument_list|,
name|end
argument_list|)
operator|+
literal|"s, duration per send: "
operator|+
operator|(
name|unitDuration
argument_list|(
name|start
argument_list|,
name|end
argument_list|,
name|getTestIterations
argument_list|()
argument_list|)
operator|*
literal|1000.0
operator|)
operator|+
literal|"ms"
argument_list|)
expr_stmt|;
block|}
specifier|private
name|float
name|unitDuration
parameter_list|(
name|long
name|start
parameter_list|,
name|long
name|end
parameter_list|,
name|int
name|testIterations
parameter_list|)
block|{
return|return
name|duration
argument_list|(
name|start
argument_list|,
name|end
argument_list|)
operator|/
name|testIterations
return|;
block|}
specifier|private
name|float
name|duration
parameter_list|(
name|long
name|start
parameter_list|,
name|long
name|end
parameter_list|)
block|{
return|return
call|(
name|float
call|)
argument_list|(
operator|(
call|(
name|float
call|)
argument_list|(
name|end
operator|-
name|start
argument_list|)
operator|)
operator|/
literal|1000.0f
argument_list|)
return|;
block|}
specifier|protected
name|int
name|getTestIterations
parameter_list|()
block|{
return|return
literal|1000
return|;
block|}
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Running: "
operator|+
name|getName
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|isDisabled
argument_list|()
condition|)
block|{
return|return;
block|}
name|log
operator|.
name|info
argument_list|(
literal|"Client connecting to: "
operator|+
name|getAddress
argument_list|()
operator|+
literal|":4444"
argument_list|)
expr_stmt|;
name|clientChannel
operator|=
name|AsyncToSyncChannel
operator|.
name|adapt
argument_list|(
name|openClientChannel
argument_list|(
operator|new
name|URI
argument_list|(
literal|"test://"
operator|+
name|getAddress
argument_list|()
operator|+
literal|":4444"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|clientChannel
operator|.
name|start
argument_list|()
expr_stmt|;
name|serverChannel
operator|=
name|AsyncToSyncChannel
operator|.
name|adapt
argument_list|(
name|openServerChannel
argument_list|(
operator|new
name|URI
argument_list|(
literal|"test://"
operator|+
name|getAddress
argument_list|()
operator|+
literal|":4444"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|serverChannel
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
specifier|private
name|void
name|doSendReceive
parameter_list|(
specifier|final
name|Packet
name|outboundPacket
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
name|ByteArrayPacket
name|ip
init|=
operator|new
name|ByteArrayPacket
argument_list|(
operator|new
name|byte
index|[
name|outboundPacket
operator|.
name|remaining
argument_list|()
index|]
argument_list|)
decl_stmt|;
comment|// Do the send async.
name|sendExecutor
operator|.
name|execute
argument_list|(
operator|new
name|Runnable
argument_list|()
block|{
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|clientChannel
operator|.
name|write
argument_list|(
name|outboundPacket
argument_list|)
expr_stmt|;
name|clientChannel
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|int
name|i
init|=
literal|0
decl_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
while|while
condition|(
name|ip
operator|.
name|hasRemaining
argument_list|()
condition|)
block|{
name|Packet
name|packet
init|=
name|serverChannel
operator|.
name|read
argument_list|(
literal|1000
operator|*
literal|5
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|packet
argument_list|)
expr_stmt|;
name|packet
operator|.
name|read
argument_list|(
name|ip
argument_list|)
expr_stmt|;
block|}
name|outboundPacket
operator|.
name|clear
argument_list|()
expr_stmt|;
name|ip
operator|.
name|clear
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|outboundPacket
operator|.
name|sliceAsBytes
argument_list|()
argument_list|,
name|ip
operator|.
name|sliceAsBytes
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|isDisabled
argument_list|()
condition|)
return|return;
name|log
operator|.
name|info
argument_list|(
literal|"Closing down the channels."
argument_list|)
expr_stmt|;
name|serverChannel
operator|.
name|dispose
argument_list|()
expr_stmt|;
name|clientChannel
operator|.
name|dispose
argument_list|()
expr_stmt|;
block|}
specifier|protected
name|boolean
name|isDisabled
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
specifier|public
name|void
name|assertEquals
parameter_list|(
name|byte
index|[]
name|b1
parameter_list|,
name|byte
index|[]
name|b2
parameter_list|)
block|{
name|assertEquals
argument_list|(
name|b1
operator|.
name|length
argument_list|,
name|b2
operator|.
name|length
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|b2
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|assertEquals
argument_list|(
name|b1
index|[
name|i
index|]
argument_list|,
name|b2
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
specifier|abstract
specifier|protected
name|Channel
name|openClientChannel
parameter_list|(
name|URI
name|connectURI
parameter_list|)
throws|throws
name|IOException
function_decl|;
specifier|abstract
specifier|protected
name|Channel
name|openServerChannel
parameter_list|(
name|URI
name|connectURI
parameter_list|)
throws|throws
name|IOException
function_decl|;
specifier|abstract
specifier|protected
name|String
name|getAddress
parameter_list|()
function_decl|;
specifier|private
specifier|final
class|class
name|UDPFilterPacket
extends|extends
name|FilterPacket
block|{
specifier|private
specifier|final
name|DatagramPacket
name|packet
decl_stmt|;
specifier|private
name|UDPFilterPacket
parameter_list|(
name|byte
index|[]
name|buf
parameter_list|,
name|SocketAddress
name|address
parameter_list|)
throws|throws
name|SocketException
block|{
name|super
argument_list|(
operator|new
name|ByteArrayPacket
argument_list|(
name|buf
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|packet
operator|=
operator|new
name|DatagramPacket
argument_list|(
name|buf
argument_list|,
name|buf
operator|.
name|length
argument_list|,
name|address
argument_list|)
expr_stmt|;
block|}
specifier|private
name|UDPFilterPacket
parameter_list|(
name|Packet
name|op
parameter_list|,
name|DatagramPacket
name|packet
parameter_list|)
block|{
name|super
argument_list|(
name|op
argument_list|)
expr_stmt|;
name|this
operator|.
name|packet
operator|=
name|packet
expr_stmt|;
block|}
specifier|public
name|Object
name|getAdapter
parameter_list|(
name|Class
name|target
parameter_list|)
block|{
if|if
condition|(
name|target
operator|==
name|DatagramContext
operator|.
name|class
condition|)
block|{
return|return
operator|new
name|DatagramContext
argument_list|(
name|packet
argument_list|)
return|;
block|}
return|return
name|super
operator|.
name|getAdapter
argument_list|(
name|target
argument_list|)
return|;
block|}
specifier|public
name|Packet
name|filter
parameter_list|(
name|Packet
name|packet
parameter_list|)
block|{
return|return
operator|new
name|UDPFilterPacket
argument_list|(
name|packet
argument_list|,
name|this
operator|.
name|packet
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

