begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**   *   * Copyright 2004 Hiram Chirino  *   * Licensed under the Apache License, Version 2.0 (the "License");   * you may not use this file except in compliance with the License.   * You may obtain a copy of the License at   *   * http://www.apache.org/licenses/LICENSE-2.0  *   * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,   * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   * See the License for the specific language governing permissions and   * limitations under the License.   *   **/
end_comment

begin_package
package|package
name|org
operator|.
name|activeio
operator|.
name|net
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
name|FilterSyncChannel
import|;
end_import

begin_import
import|import
name|org
operator|.
name|activeio
operator|.
name|FilterSyncChannelServer
import|;
end_import

begin_import
import|import
name|org
operator|.
name|activeio
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
name|SyncChannel
import|;
end_import

begin_import
import|import
name|org
operator|.
name|activeio
operator|.
name|SyncChannelFactory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|activeio
operator|.
name|SyncChannelServer
import|;
end_import

begin_comment
comment|/**  * Makes all the channels produced by another [@see org.activeio.SyncChannelFactory}  * have write operations that have built in delays for testing.   *   * @version $Revision$  */
end_comment

begin_class
specifier|public
class|class
name|SlowWriteSyncChannelFactory
implements|implements
name|SyncChannelFactory
block|{
specifier|final
name|SyncChannelFactory
name|next
decl_stmt|;
specifier|private
specifier|final
name|int
name|maxPacketSize
decl_stmt|;
specifier|private
specifier|final
name|long
name|packetDelay
decl_stmt|;
specifier|public
name|SlowWriteSyncChannelFactory
parameter_list|(
specifier|final
name|SyncChannelFactory
name|next
parameter_list|,
name|int
name|maxPacketSize
parameter_list|,
name|long
name|packetDelay
parameter_list|)
block|{
name|this
operator|.
name|next
operator|=
name|next
expr_stmt|;
name|this
operator|.
name|maxPacketSize
operator|=
name|maxPacketSize
expr_stmt|;
name|this
operator|.
name|packetDelay
operator|=
name|packetDelay
expr_stmt|;
block|}
class|class
name|SlowWriteSyncChannel
extends|extends
name|FilterSyncChannel
block|{
specifier|public
name|SlowWriteSyncChannel
parameter_list|(
name|SyncChannel
name|next
parameter_list|)
block|{
name|super
argument_list|(
name|next
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|write
parameter_list|(
name|Packet
name|packet
parameter_list|)
throws|throws
name|IOException
block|{
name|packet
operator|=
name|packet
operator|.
name|slice
argument_list|()
expr_stmt|;
while|while
condition|(
name|packet
operator|.
name|hasRemaining
argument_list|()
condition|)
block|{
name|int
name|size
init|=
name|Math
operator|.
name|max
argument_list|(
name|maxPacketSize
argument_list|,
name|packet
operator|.
name|remaining
argument_list|()
argument_list|)
decl_stmt|;
name|packet
operator|.
name|position
argument_list|(
name|size
argument_list|)
expr_stmt|;
name|Packet
name|remaining
init|=
name|packet
operator|.
name|slice
argument_list|()
decl_stmt|;
name|packet
operator|.
name|flip
argument_list|()
expr_stmt|;
name|Packet
name|data
init|=
name|packet
operator|.
name|slice
argument_list|()
decl_stmt|;
name|super
operator|.
name|write
argument_list|(
name|data
argument_list|)
expr_stmt|;
name|packet
operator|=
name|remaining
expr_stmt|;
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
name|packetDelay
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|InterruptedIOException
argument_list|()
throw|;
block|}
block|}
block|}
block|}
class|class
name|SlowWriteSyncChannelServer
extends|extends
name|FilterSyncChannelServer
block|{
specifier|public
name|SlowWriteSyncChannelServer
parameter_list|(
name|SyncChannelServer
name|next
parameter_list|)
block|{
name|super
argument_list|(
name|next
argument_list|)
expr_stmt|;
block|}
specifier|public
name|Channel
name|accept
parameter_list|(
name|long
name|timeout
parameter_list|)
throws|throws
name|IOException
block|{
name|Channel
name|channel
init|=
name|super
operator|.
name|accept
argument_list|(
name|timeout
argument_list|)
decl_stmt|;
if|if
condition|(
name|channel
operator|!=
literal|null
condition|)
block|{
name|channel
operator|=
operator|new
name|SlowWriteSyncChannel
argument_list|(
operator|(
name|SyncChannel
operator|)
name|channel
argument_list|)
expr_stmt|;
block|}
return|return
name|channel
return|;
block|}
block|}
specifier|public
name|SyncChannelServer
name|bindSyncChannel
parameter_list|(
name|URI
name|location
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|next
operator|.
name|bindSyncChannel
argument_list|(
name|location
argument_list|)
return|;
block|}
specifier|public
name|SyncChannel
name|openSyncChannel
parameter_list|(
name|URI
name|location
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|SlowWriteSyncChannel
argument_list|(
name|next
operator|.
name|openSyncChannel
argument_list|(
name|location
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

end_unit

