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
name|activeio
operator|.
name|packet
operator|.
name|sync
operator|.
name|datagram
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
name|DatagramSocket
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
name|SocketTimeoutException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
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
name|apache
operator|.
name|activeio
operator|.
name|packet
operator|.
name|ByteSequence
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
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
name|apache
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
name|apache
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
name|apache
operator|.
name|activeio
operator|.
name|packet
operator|.
name|sync
operator|.
name|SyncChannelServer
import|;
end_import

begin_comment
comment|/**  * A {@see org.apache.activeio.SynchChannel}implementation that uses  * TCP to talk to the network.  *   * @version $Revision$  */
end_comment

begin_class
specifier|public
class|class
name|DatagramSocketSyncChannel
implements|implements
name|SyncChannel
block|{
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
name|Packet
name|next
parameter_list|,
name|DatagramPacket
name|packet
parameter_list|)
block|{
name|super
argument_list|(
name|next
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
specifier|private
specifier|static
specifier|final
name|int
name|DEFAULT_BUFFER_SIZE
init|=
literal|64
operator|*
literal|1024
decl_stmt|;
specifier|private
specifier|final
name|DatagramSocket
name|socket
decl_stmt|;
specifier|private
name|boolean
name|disposed
decl_stmt|;
specifier|private
name|int
name|curentSoTimeout
decl_stmt|;
comment|/**      * Construct basic helpers      *       * @param wireFormat      * @throws IOException      */
specifier|protected
name|DatagramSocketSyncChannel
parameter_list|(
name|DatagramSocket
name|socket
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|socket
operator|=
name|socket
expr_stmt|;
name|socket
operator|.
name|setReceiveBufferSize
argument_list|(
name|DEFAULT_BUFFER_SIZE
argument_list|)
expr_stmt|;
name|socket
operator|.
name|setSendBufferSize
argument_list|(
name|DEFAULT_BUFFER_SIZE
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|DatagramSocket
name|getSocket
parameter_list|()
block|{
return|return
name|socket
return|;
block|}
comment|/**      * @see org.apache.activeio.packet.sync.SyncChannel#read(long)      */
specifier|public
name|org
operator|.
name|apache
operator|.
name|activeio
operator|.
name|packet
operator|.
name|Packet
name|read
parameter_list|(
name|long
name|timeout
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
if|if
condition|(
name|timeout
operator|==
name|SyncChannelServer
operator|.
name|WAIT_FOREVER_TIMEOUT
condition|)
name|setSoTimeout
argument_list|(
literal|0
argument_list|)
expr_stmt|;
elseif|else
if|if
condition|(
name|timeout
operator|==
name|SyncChannelServer
operator|.
name|NO_WAIT_TIMEOUT
condition|)
name|setSoTimeout
argument_list|(
literal|1
argument_list|)
expr_stmt|;
else|else
name|setSoTimeout
argument_list|(
operator|(
name|int
operator|)
name|timeout
argument_list|)
expr_stmt|;
comment|// FYI: message data is truncated if biger than this buffer.
specifier|final
name|byte
name|data
index|[]
init|=
operator|new
name|byte
index|[
name|DEFAULT_BUFFER_SIZE
index|]
decl_stmt|;
specifier|final
name|DatagramPacket
name|packet
init|=
operator|new
name|DatagramPacket
argument_list|(
name|data
argument_list|,
name|data
operator|.
name|length
argument_list|)
decl_stmt|;
name|socket
operator|.
name|receive
argument_list|(
name|packet
argument_list|)
expr_stmt|;
comment|// A FilterPacket is used to provide the UdpDatagramContext via narrow.
return|return
operator|new
name|UDPFilterPacket
argument_list|(
operator|new
name|ByteArrayPacket
argument_list|(
name|data
argument_list|,
literal|0
argument_list|,
name|packet
operator|.
name|getLength
argument_list|()
argument_list|)
argument_list|,
name|packet
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|SocketTimeoutException
name|e
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
block|}
specifier|private
name|void
name|setSoTimeout
parameter_list|(
name|int
name|i
parameter_list|)
throws|throws
name|SocketException
block|{
if|if
condition|(
name|curentSoTimeout
operator|!=
name|i
condition|)
block|{
name|socket
operator|.
name|setSoTimeout
argument_list|(
name|i
argument_list|)
expr_stmt|;
name|curentSoTimeout
operator|=
name|i
expr_stmt|;
block|}
block|}
comment|/**      * @see org.apache.activeio.Channel#write(org.apache.activeio.packet.Packet)      */
specifier|public
name|void
name|write
parameter_list|(
name|org
operator|.
name|apache
operator|.
name|activeio
operator|.
name|packet
operator|.
name|Packet
name|packet
parameter_list|)
throws|throws
name|IOException
block|{
name|ByteSequence
name|sequence
init|=
name|packet
operator|.
name|asByteSequence
argument_list|()
decl_stmt|;
name|DatagramContext
name|context
init|=
operator|(
name|DatagramContext
operator|)
name|packet
operator|.
name|getAdapter
argument_list|(
name|DatagramContext
operator|.
name|class
argument_list|)
decl_stmt|;
if|if
condition|(
name|context
operator|!=
literal|null
condition|)
block|{
name|socket
operator|.
name|send
argument_list|(
operator|new
name|DatagramPacket
argument_list|(
name|sequence
operator|.
name|getData
argument_list|()
argument_list|,
name|sequence
operator|.
name|getOffset
argument_list|()
argument_list|,
name|sequence
operator|.
name|getLength
argument_list|()
argument_list|,
name|context
operator|.
name|address
argument_list|,
name|context
operator|.
name|port
operator|.
name|intValue
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|socket
operator|.
name|send
argument_list|(
operator|new
name|DatagramPacket
argument_list|(
name|sequence
operator|.
name|getData
argument_list|()
argument_list|,
name|sequence
operator|.
name|getOffset
argument_list|()
argument_list|,
name|sequence
operator|.
name|getLength
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * @see org.apache.activeio.Channel#flush()      */
specifier|public
name|void
name|flush
parameter_list|()
throws|throws
name|IOException
block|{     }
comment|/**      * @see org.apache.activeio.Disposable#dispose()      */
specifier|public
name|void
name|dispose
parameter_list|()
block|{
if|if
condition|(
name|disposed
condition|)
return|return;
name|socket
operator|.
name|close
argument_list|()
expr_stmt|;
name|disposed
operator|=
literal|true
expr_stmt|;
block|}
specifier|public
name|void
name|start
parameter_list|()
throws|throws
name|IOException
block|{     }
specifier|public
name|void
name|stop
parameter_list|()
throws|throws
name|IOException
block|{     }
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
operator|.
name|isAssignableFrom
argument_list|(
name|getClass
argument_list|()
argument_list|)
condition|)
block|{
return|return
name|this
return|;
block|}
return|return
literal|null
return|;
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"Datagram Connection: "
operator|+
name|socket
operator|.
name|getLocalSocketAddress
argument_list|()
operator|+
literal|" -> "
operator|+
name|socket
operator|.
name|getRemoteSocketAddress
argument_list|()
return|;
block|}
block|}
end_class

end_unit

