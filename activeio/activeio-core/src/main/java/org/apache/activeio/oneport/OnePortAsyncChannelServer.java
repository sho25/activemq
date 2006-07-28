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
name|activeio
operator|.
name|oneport
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
name|URI
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
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
name|AcceptListener
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
name|Channel
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
name|adapter
operator|.
name|AsyncToSyncChannel
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
name|adapter
operator|.
name|SyncToAsyncChannel
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
name|AppendedPacket
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
name|async
operator|.
name|AsyncChannel
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
name|async
operator|.
name|AsyncChannelListener
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
name|async
operator|.
name|AsyncChannelServer
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
name|async
operator|.
name|FilterAsyncChannel
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
name|async
operator|.
name|FilterAsyncChannelServer
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
name|filter
operator|.
name|PushbackSyncChannel
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
name|ConcurrentHashMap
import|;
end_import

begin_comment
comment|/**  * Allows multiple protocols share a single ChannelServer.  All protocols sharing the server   * must have a distinct magic number at the beginning of the client's request.  *   * TODO: handle the case where a client opens a connection but sends no data down the stream.  We need  * to timeout that client.  *   * @version $Revision$  */
end_comment

begin_class
specifier|final
specifier|public
class|class
name|OnePortAsyncChannelServer
extends|extends
name|FilterAsyncChannelServer
block|{
comment|/**      * The OnePortAsyncChannelServer listens for incoming connection      * from a normal AsyncChannelServer.  This s the listner used       * to receive the accepted channels.      */
specifier|final
specifier|private
class|class
name|OnePortAcceptListener
implements|implements
name|AcceptListener
block|{
specifier|public
name|void
name|onAccept
parameter_list|(
name|Channel
name|channel
parameter_list|)
block|{
try|try
block|{
name|AsyncChannel
name|asyncChannel
init|=
name|SyncToAsyncChannel
operator|.
name|adapt
argument_list|(
name|channel
argument_list|)
decl_stmt|;
name|ProtocolInspectingAsyncChannel
name|inspector
init|=
operator|new
name|ProtocolInspectingAsyncChannel
argument_list|(
name|asyncChannel
argument_list|)
decl_stmt|;
name|inspector
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|onAcceptError
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|onAcceptError
parameter_list|(
name|IOException
name|error
parameter_list|)
block|{
name|dispose
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**      * This channel filter sniffs the first few bytes of the byte stream       * to see if a ProtocolRecognizer recognizes the protocol.  If it does not      * it just closes the channel, otherwise the associated SubPortAsyncChannelServer      * is notified that it accepted a channel.      *      */
specifier|final
specifier|private
class|class
name|ProtocolInspectingAsyncChannel
extends|extends
name|FilterAsyncChannel
block|{
specifier|private
name|Packet
name|buffer
decl_stmt|;
specifier|public
name|ProtocolInspectingAsyncChannel
parameter_list|(
name|AsyncChannel
name|next
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|next
argument_list|)
expr_stmt|;
name|setAsyncChannelListener
argument_list|(
operator|new
name|AsyncChannelListener
argument_list|()
block|{
specifier|public
name|void
name|onPacket
parameter_list|(
name|Packet
name|packet
parameter_list|)
block|{
if|if
condition|(
name|buffer
operator|==
literal|null
condition|)
block|{
name|buffer
operator|=
name|packet
expr_stmt|;
block|}
else|else
block|{
name|buffer
operator|=
name|AppendedPacket
operator|.
name|join
argument_list|(
name|buffer
argument_list|,
name|packet
argument_list|)
expr_stmt|;
block|}
name|findMagicNumber
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|onPacketError
parameter_list|(
name|IOException
name|error
parameter_list|)
block|{
name|dispose
argument_list|()
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|findMagicNumber
parameter_list|()
block|{
for|for
control|(
name|Iterator
name|iter
init|=
name|recognizerMap
operator|.
name|keySet
argument_list|()
operator|.
name|iterator
argument_list|()
init|;
name|iter
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|ProtocolRecognizer
name|recognizer
init|=
operator|(
name|ProtocolRecognizer
operator|)
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|recognizer
operator|.
name|recognizes
argument_list|(
name|buffer
operator|.
name|duplicate
argument_list|()
argument_list|)
condition|)
block|{
if|if
condition|(
name|UnknownRecognizer
operator|.
name|UNKNOWN_RECOGNIZER
operator|==
name|recognizer
condition|)
block|{
comment|// Dispose the channel.. don't know what to do with it.
name|dispose
argument_list|()
expr_stmt|;
block|}
name|SubPortAsyncChannelServer
name|onePort
init|=
operator|(
name|SubPortAsyncChannelServer
operator|)
name|recognizerMap
operator|.
name|get
argument_list|(
name|recognizer
argument_list|)
decl_stmt|;
if|if
condition|(
name|onePort
operator|==
literal|null
condition|)
block|{
comment|// Dispose the channel.. don't know what to do with it.
name|dispose
argument_list|()
expr_stmt|;
block|}
comment|// Once the magic number is found:
comment|// Stop the channel so that a decision can be taken on what to
comment|// do with the
comment|// channel. When the channel is restarted, the buffered up
comment|// packets wiil get
comment|// delivered.
try|try
block|{
name|stop
argument_list|()
expr_stmt|;
name|setAsyncChannelListener
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|getAsyncChannelListener
argument_list|()
operator|.
name|onPacketError
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
name|Channel
name|channel
init|=
name|getNext
argument_list|()
decl_stmt|;
name|channel
operator|=
name|AsyncToSyncChannel
operator|.
name|adapt
argument_list|(
name|channel
argument_list|)
expr_stmt|;
name|channel
operator|=
operator|new
name|PushbackSyncChannel
argument_list|(
operator|(
name|SyncChannel
operator|)
name|channel
argument_list|,
name|buffer
argument_list|)
expr_stmt|;
name|channel
operator|=
name|SyncToAsyncChannel
operator|.
name|adapt
argument_list|(
name|channel
argument_list|)
expr_stmt|;
name|onePort
operator|.
name|onAccept
argument_list|(
name|channel
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
block|}
block|}
comment|/**      * Clients bind against the OnePortAsyncChannelServer and get       * SubPortAsyncChannelServer which can be used to accept connections.      */
specifier|final
specifier|private
class|class
name|SubPortAsyncChannelServer
implements|implements
name|AsyncChannelServer
block|{
specifier|private
specifier|final
name|ProtocolRecognizer
name|recognizer
decl_stmt|;
specifier|private
name|AcceptListener
name|acceptListener
decl_stmt|;
specifier|private
name|boolean
name|started
decl_stmt|;
comment|/**          * @param recognizer          */
specifier|public
name|SubPortAsyncChannelServer
parameter_list|(
name|ProtocolRecognizer
name|recognizer
parameter_list|)
block|{
name|this
operator|.
name|recognizer
operator|=
name|recognizer
expr_stmt|;
block|}
specifier|public
name|void
name|setAcceptListener
parameter_list|(
name|AcceptListener
name|acceptListener
parameter_list|)
block|{
name|this
operator|.
name|acceptListener
operator|=
name|acceptListener
expr_stmt|;
block|}
specifier|public
name|URI
name|getBindURI
parameter_list|()
block|{
return|return
name|next
operator|.
name|getBindURI
argument_list|()
return|;
block|}
specifier|public
name|URI
name|getConnectURI
parameter_list|()
block|{
return|return
name|next
operator|.
name|getConnectURI
argument_list|()
return|;
block|}
specifier|public
name|void
name|dispose
parameter_list|()
block|{
name|started
operator|=
literal|false
expr_stmt|;
name|recognizerMap
operator|.
name|remove
argument_list|(
name|recognizer
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|start
parameter_list|()
throws|throws
name|IOException
block|{
name|started
operator|=
literal|true
expr_stmt|;
block|}
specifier|public
name|void
name|stop
parameter_list|()
throws|throws
name|IOException
block|{
name|started
operator|=
literal|false
expr_stmt|;
block|}
name|void
name|onAccept
parameter_list|(
name|Channel
name|channel
parameter_list|)
block|{
if|if
condition|(
name|started
operator|&&
name|acceptListener
operator|!=
literal|null
condition|)
block|{
name|acceptListener
operator|.
name|onAccept
argument_list|(
name|channel
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// Dispose the channel.. don't know what to do with it.
name|channel
operator|.
name|dispose
argument_list|()
expr_stmt|;
block|}
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
name|OnePortAsyncChannelServer
operator|.
name|this
operator|.
name|getAdapter
argument_list|(
name|target
argument_list|)
return|;
block|}
block|}
specifier|private
specifier|final
name|ConcurrentHashMap
name|recognizerMap
init|=
operator|new
name|ConcurrentHashMap
argument_list|()
decl_stmt|;
specifier|public
name|OnePortAsyncChannelServer
parameter_list|(
name|AsyncChannelServer
name|server
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|server
argument_list|)
expr_stmt|;
name|super
operator|.
name|setAcceptListener
argument_list|(
operator|new
name|OnePortAcceptListener
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setAcceptListener
parameter_list|(
name|AcceptListener
name|acceptListener
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalAccessError
argument_list|(
literal|"Not supported"
argument_list|)
throw|;
block|}
specifier|public
name|AsyncChannelServer
name|bindAsyncChannel
parameter_list|(
name|ProtocolRecognizer
name|recognizer
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|recognizerMap
operator|.
name|contains
argument_list|(
name|recognizer
argument_list|)
condition|)
throw|throw
operator|new
name|IOException
argument_list|(
literal|"That recognizer is allredy bound."
argument_list|)
throw|;
name|SubPortAsyncChannelServer
name|server
init|=
operator|new
name|SubPortAsyncChannelServer
argument_list|(
name|recognizer
argument_list|)
decl_stmt|;
name|Object
name|old
init|=
name|recognizerMap
operator|.
name|put
argument_list|(
name|recognizer
argument_list|,
name|server
argument_list|)
decl_stmt|;
return|return
name|server
return|;
block|}
block|}
end_class

end_unit

