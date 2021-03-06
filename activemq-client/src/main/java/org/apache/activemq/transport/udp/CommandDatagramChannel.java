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
name|udp
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
name|net
operator|.
name|SocketAddress
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
name|DatagramChannel
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
name|Endpoint
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
name|LastPartialCommand
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
name|PartialCommand
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
name|openwire
operator|.
name|BooleanStream
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
name|openwire
operator|.
name|OpenWireFormat
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
name|reliable
operator|.
name|ReplayBuffer
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
name|ByteArrayOutputStream
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
comment|/**  * A strategy for reading datagrams and de-fragmenting them together.  *   *   */
end_comment

begin_class
specifier|public
class|class
name|CommandDatagramChannel
extends|extends
name|CommandChannelSupport
block|{
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
name|CommandDatagramChannel
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|DatagramChannel
name|channel
decl_stmt|;
specifier|private
name|ByteBufferPool
name|bufferPool
decl_stmt|;
comment|// reading
specifier|private
name|Object
name|readLock
init|=
operator|new
name|Object
argument_list|()
decl_stmt|;
specifier|private
name|ByteBuffer
name|readBuffer
decl_stmt|;
comment|// writing
specifier|private
name|Object
name|writeLock
init|=
operator|new
name|Object
argument_list|()
decl_stmt|;
specifier|private
name|int
name|defaultMarshalBufferSize
init|=
literal|64
operator|*
literal|1024
decl_stmt|;
specifier|private
specifier|volatile
name|int
name|receiveCounter
decl_stmt|;
specifier|public
name|CommandDatagramChannel
parameter_list|(
name|UdpTransport
name|transport
parameter_list|,
name|OpenWireFormat
name|wireFormat
parameter_list|,
name|int
name|datagramSize
parameter_list|,
name|SocketAddress
name|targetAddress
parameter_list|,
name|DatagramHeaderMarshaller
name|headerMarshaller
parameter_list|,
name|DatagramChannel
name|channel
parameter_list|,
name|ByteBufferPool
name|bufferPool
parameter_list|)
block|{
name|super
argument_list|(
name|transport
argument_list|,
name|wireFormat
argument_list|,
name|datagramSize
argument_list|,
name|targetAddress
argument_list|,
name|headerMarshaller
argument_list|)
expr_stmt|;
name|this
operator|.
name|channel
operator|=
name|channel
expr_stmt|;
name|this
operator|.
name|bufferPool
operator|=
name|bufferPool
expr_stmt|;
block|}
specifier|public
name|void
name|start
parameter_list|()
throws|throws
name|Exception
block|{
name|bufferPool
operator|.
name|setDefaultSize
argument_list|(
name|datagramSize
argument_list|)
expr_stmt|;
name|bufferPool
operator|.
name|start
argument_list|()
expr_stmt|;
name|readBuffer
operator|=
name|bufferPool
operator|.
name|borrowBuffer
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|stop
parameter_list|()
throws|throws
name|Exception
block|{
name|bufferPool
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
specifier|public
name|Command
name|read
parameter_list|()
throws|throws
name|IOException
block|{
name|Command
name|answer
init|=
literal|null
decl_stmt|;
name|Endpoint
name|from
init|=
literal|null
decl_stmt|;
synchronized|synchronized
init|(
name|readLock
init|)
block|{
while|while
condition|(
literal|true
condition|)
block|{
name|readBuffer
operator|.
name|clear
argument_list|()
expr_stmt|;
name|SocketAddress
name|address
init|=
name|channel
operator|.
name|receive
argument_list|(
name|readBuffer
argument_list|)
decl_stmt|;
name|readBuffer
operator|.
name|flip
argument_list|()
expr_stmt|;
if|if
condition|(
name|readBuffer
operator|.
name|limit
argument_list|()
operator|==
literal|0
condition|)
block|{
continue|continue;
block|}
name|receiveCounter
operator|++
expr_stmt|;
name|from
operator|=
name|headerMarshaller
operator|.
name|createEndpoint
argument_list|(
name|readBuffer
argument_list|,
name|address
argument_list|)
expr_stmt|;
name|int
name|remaining
init|=
name|readBuffer
operator|.
name|remaining
argument_list|()
decl_stmt|;
name|byte
index|[]
name|data
init|=
operator|new
name|byte
index|[
name|remaining
index|]
decl_stmt|;
name|readBuffer
operator|.
name|get
argument_list|(
name|data
argument_list|)
expr_stmt|;
comment|// TODO could use a DataInput implementation that talks direct
comment|// to
comment|// the ByteBuffer to avoid object allocation and unnecessary
comment|// buffering?
name|DataInputStream
name|dataIn
init|=
operator|new
name|DataInputStream
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
name|data
argument_list|)
argument_list|)
decl_stmt|;
name|answer
operator|=
operator|(
name|Command
operator|)
name|wireFormat
operator|.
name|unmarshal
argument_list|(
name|dataIn
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
if|if
condition|(
name|answer
operator|!=
literal|null
condition|)
block|{
name|answer
operator|.
name|setFrom
argument_list|(
name|from
argument_list|)
expr_stmt|;
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
literal|"Channel: "
operator|+
name|name
operator|+
literal|" received from: "
operator|+
name|from
operator|+
literal|" about to process: "
operator|+
name|answer
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|answer
return|;
block|}
specifier|public
name|void
name|write
parameter_list|(
name|Command
name|command
parameter_list|,
name|SocketAddress
name|address
parameter_list|)
throws|throws
name|IOException
block|{
synchronized|synchronized
init|(
name|writeLock
init|)
block|{
name|ByteArrayOutputStream
name|largeBuffer
init|=
operator|new
name|ByteArrayOutputStream
argument_list|(
name|defaultMarshalBufferSize
argument_list|)
decl_stmt|;
name|wireFormat
operator|.
name|marshal
argument_list|(
name|command
argument_list|,
operator|new
name|DataOutputStream
argument_list|(
name|largeBuffer
argument_list|)
argument_list|)
expr_stmt|;
name|byte
index|[]
name|data
init|=
name|largeBuffer
operator|.
name|toByteArray
argument_list|()
decl_stmt|;
name|int
name|size
init|=
name|data
operator|.
name|length
decl_stmt|;
name|ByteBuffer
name|writeBuffer
init|=
name|bufferPool
operator|.
name|borrowBuffer
argument_list|()
decl_stmt|;
name|writeBuffer
operator|.
name|clear
argument_list|()
expr_stmt|;
name|headerMarshaller
operator|.
name|writeHeader
argument_list|(
name|command
argument_list|,
name|writeBuffer
argument_list|)
expr_stmt|;
if|if
condition|(
name|size
operator|>
name|writeBuffer
operator|.
name|remaining
argument_list|()
condition|)
block|{
comment|// lets split the command up into chunks
name|int
name|offset
init|=
literal|0
decl_stmt|;
name|boolean
name|lastFragment
init|=
literal|false
decl_stmt|;
name|int
name|length
init|=
name|data
operator|.
name|length
decl_stmt|;
for|for
control|(
name|int
name|fragment
init|=
literal|0
init|;
operator|!
name|lastFragment
condition|;
name|fragment
operator|++
control|)
block|{
comment|// write the header
if|if
condition|(
name|fragment
operator|>
literal|0
condition|)
block|{
name|writeBuffer
operator|=
name|bufferPool
operator|.
name|borrowBuffer
argument_list|()
expr_stmt|;
name|writeBuffer
operator|.
name|clear
argument_list|()
expr_stmt|;
name|headerMarshaller
operator|.
name|writeHeader
argument_list|(
name|command
argument_list|,
name|writeBuffer
argument_list|)
expr_stmt|;
block|}
name|int
name|chunkSize
init|=
name|writeBuffer
operator|.
name|remaining
argument_list|()
decl_stmt|;
comment|// we need to remove the amount of overhead to write the
comment|// partial command
comment|// lets write the flags in there
name|BooleanStream
name|bs
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|wireFormat
operator|.
name|isTightEncodingEnabled
argument_list|()
condition|)
block|{
name|bs
operator|=
operator|new
name|BooleanStream
argument_list|()
expr_stmt|;
name|bs
operator|.
name|writeBoolean
argument_list|(
literal|true
argument_list|)
expr_stmt|;
comment|// the partial data byte[] is
comment|// never null
block|}
comment|// lets remove the header of the partial command
comment|// which is the byte for the type and an int for the size of
comment|// the byte[]
comment|// data type + the command ID + size of the partial data
name|chunkSize
operator|-=
literal|1
operator|+
literal|4
operator|+
literal|4
expr_stmt|;
comment|// the boolean flags
if|if
condition|(
name|bs
operator|!=
literal|null
condition|)
block|{
name|chunkSize
operator|-=
name|bs
operator|.
name|marshalledSize
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|chunkSize
operator|-=
literal|1
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|wireFormat
operator|.
name|isSizePrefixDisabled
argument_list|()
condition|)
block|{
comment|// lets write the size of the command buffer
name|writeBuffer
operator|.
name|putInt
argument_list|(
name|chunkSize
argument_list|)
expr_stmt|;
name|chunkSize
operator|-=
literal|4
expr_stmt|;
block|}
name|lastFragment
operator|=
name|offset
operator|+
name|chunkSize
operator|>=
name|length
expr_stmt|;
if|if
condition|(
name|chunkSize
operator|+
name|offset
operator|>
name|length
condition|)
block|{
name|chunkSize
operator|=
name|length
operator|-
name|offset
expr_stmt|;
block|}
if|if
condition|(
name|lastFragment
condition|)
block|{
name|writeBuffer
operator|.
name|put
argument_list|(
name|LastPartialCommand
operator|.
name|DATA_STRUCTURE_TYPE
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|writeBuffer
operator|.
name|put
argument_list|(
name|PartialCommand
operator|.
name|DATA_STRUCTURE_TYPE
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|bs
operator|!=
literal|null
condition|)
block|{
name|bs
operator|.
name|marshal
argument_list|(
name|writeBuffer
argument_list|)
expr_stmt|;
block|}
name|int
name|commandId
init|=
name|command
operator|.
name|getCommandId
argument_list|()
decl_stmt|;
if|if
condition|(
name|fragment
operator|>
literal|0
condition|)
block|{
name|commandId
operator|=
name|sequenceGenerator
operator|.
name|getNextSequenceId
argument_list|()
expr_stmt|;
block|}
name|writeBuffer
operator|.
name|putInt
argument_list|(
name|commandId
argument_list|)
expr_stmt|;
if|if
condition|(
name|bs
operator|==
literal|null
condition|)
block|{
name|writeBuffer
operator|.
name|put
argument_list|(
operator|(
name|byte
operator|)
literal|1
argument_list|)
expr_stmt|;
block|}
comment|// size of byte array
name|writeBuffer
operator|.
name|putInt
argument_list|(
name|chunkSize
argument_list|)
expr_stmt|;
comment|// now the data
name|writeBuffer
operator|.
name|put
argument_list|(
name|data
argument_list|,
name|offset
argument_list|,
name|chunkSize
argument_list|)
expr_stmt|;
name|offset
operator|+=
name|chunkSize
expr_stmt|;
name|sendWriteBuffer
argument_list|(
name|commandId
argument_list|,
name|address
argument_list|,
name|writeBuffer
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|writeBuffer
operator|.
name|put
argument_list|(
name|data
argument_list|)
expr_stmt|;
name|sendWriteBuffer
argument_list|(
name|command
operator|.
name|getCommandId
argument_list|()
argument_list|,
name|address
argument_list|,
name|writeBuffer
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|// Properties
comment|// -------------------------------------------------------------------------
specifier|public
name|ByteBufferPool
name|getBufferPool
parameter_list|()
block|{
return|return
name|bufferPool
return|;
block|}
comment|/**      * Sets the implementation of the byte buffer pool to use      */
specifier|public
name|void
name|setBufferPool
parameter_list|(
name|ByteBufferPool
name|bufferPool
parameter_list|)
block|{
name|this
operator|.
name|bufferPool
operator|=
name|bufferPool
expr_stmt|;
block|}
comment|// Implementation methods
comment|// -------------------------------------------------------------------------
specifier|protected
name|void
name|sendWriteBuffer
parameter_list|(
name|int
name|commandId
parameter_list|,
name|SocketAddress
name|address
parameter_list|,
name|ByteBuffer
name|writeBuffer
parameter_list|,
name|boolean
name|redelivery
parameter_list|)
throws|throws
name|IOException
block|{
comment|// lets put the datagram into the replay buffer first to prevent timing
comment|// issues
name|ReplayBuffer
name|bufferCache
init|=
name|getReplayBuffer
argument_list|()
decl_stmt|;
if|if
condition|(
name|bufferCache
operator|!=
literal|null
operator|&&
operator|!
name|redelivery
condition|)
block|{
name|bufferCache
operator|.
name|addBuffer
argument_list|(
name|commandId
argument_list|,
name|writeBuffer
argument_list|)
expr_stmt|;
block|}
name|writeBuffer
operator|.
name|flip
argument_list|()
expr_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|String
name|text
init|=
name|redelivery
condition|?
literal|"REDELIVERING"
else|:
literal|"sending"
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Channel: "
operator|+
name|name
operator|+
literal|" "
operator|+
name|text
operator|+
literal|" datagram: "
operator|+
name|commandId
operator|+
literal|" to: "
operator|+
name|address
argument_list|)
expr_stmt|;
block|}
name|channel
operator|.
name|send
argument_list|(
name|writeBuffer
argument_list|,
name|address
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|sendBuffer
parameter_list|(
name|int
name|commandId
parameter_list|,
name|Object
name|buffer
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|buffer
operator|!=
literal|null
condition|)
block|{
name|ByteBuffer
name|writeBuffer
init|=
operator|(
name|ByteBuffer
operator|)
name|buffer
decl_stmt|;
name|sendWriteBuffer
argument_list|(
name|commandId
argument_list|,
name|getReplayAddress
argument_list|()
argument_list|,
name|writeBuffer
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|LOG
operator|.
name|isWarnEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Request for buffer: "
operator|+
name|commandId
operator|+
literal|" is no longer present"
argument_list|)
expr_stmt|;
block|}
block|}
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
block|}
end_class

end_unit

