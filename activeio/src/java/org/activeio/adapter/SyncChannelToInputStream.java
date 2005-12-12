begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * * Copyright 2004 Hiram Chirino * *  Licensed under the Apache License, Version 2.0 (the "License"); *  you may not use this file except in compliance with the License. *  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * *  Unless required by applicable law or agreed to in writing, software *  distributed under the License is distributed on an "AS IS" BASIS, *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. *  See the License for the specific language governing permissions and *  limitations under the License. */
end_comment

begin_package
package|package
name|org
operator|.
name|activeio
operator|.
name|adapter
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
name|InputStream
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
name|packet
operator|.
name|EOSPacket
import|;
end_import

begin_comment
comment|/**  * Provides an InputStream for a given SynchChannel.  *    * @version $Revision$  */
end_comment

begin_class
specifier|public
class|class
name|SyncChannelToInputStream
extends|extends
name|InputStream
block|{
specifier|private
specifier|final
name|SyncChannel
name|channel
decl_stmt|;
specifier|private
name|Packet
name|lastPacket
decl_stmt|;
specifier|private
name|boolean
name|closed
decl_stmt|;
specifier|private
name|long
name|timeout
init|=
name|Channel
operator|.
name|WAIT_FOREVER_TIMEOUT
decl_stmt|;
comment|/**      * @param channel      */
specifier|public
name|SyncChannelToInputStream
parameter_list|(
specifier|final
name|SyncChannel
name|channel
parameter_list|)
block|{
name|this
operator|.
name|channel
operator|=
name|channel
expr_stmt|;
block|}
comment|/**      * @see java.io.InputStream#read()      */
specifier|public
name|int
name|read
parameter_list|()
throws|throws
name|IOException
block|{
while|while
condition|(
literal|true
condition|)
block|{
if|if
condition|(
name|lastPacket
operator|==
literal|null
condition|)
block|{
try|try
block|{
name|lastPacket
operator|=
name|channel
operator|.
name|read
argument_list|(
name|timeout
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
operator|(
name|IOException
operator|)
operator|new
name|IOException
argument_list|(
literal|"Channel failed: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
operator|.
name|initCause
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
if|if
condition|(
name|lastPacket
operator|.
name|hasRemaining
argument_list|()
condition|)
block|{
return|return
name|lastPacket
operator|.
name|read
argument_list|()
return|;
block|}
block|}
block|}
comment|/**      * @see java.io.InputStream#read(byte[], int, int)      */
specifier|public
name|int
name|read
parameter_list|(
name|byte
index|[]
name|b
parameter_list|,
name|int
name|off
parameter_list|,
name|int
name|len
parameter_list|)
throws|throws
name|IOException
block|{
while|while
condition|(
literal|true
condition|)
block|{
if|if
condition|(
name|lastPacket
operator|==
literal|null
operator|||
operator|!
name|lastPacket
operator|.
name|hasRemaining
argument_list|()
condition|)
block|{
try|try
block|{
name|lastPacket
operator|=
name|channel
operator|.
name|read
argument_list|(
name|timeout
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
operator|(
name|IOException
operator|)
operator|new
name|IOException
argument_list|(
literal|"Channel failed: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
operator|.
name|initCause
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
if|if
condition|(
name|lastPacket
operator|==
name|EOSPacket
operator|.
name|EOS_PACKET
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
if|if
condition|(
name|lastPacket
operator|!=
literal|null
operator|&&
name|lastPacket
operator|.
name|hasRemaining
argument_list|()
condition|)
block|{
return|return
name|lastPacket
operator|.
name|read
argument_list|(
name|b
argument_list|,
name|off
argument_list|,
name|len
argument_list|)
return|;
block|}
block|}
block|}
comment|/**      * @see java.io.InputStream#close()      */
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|closed
operator|=
literal|true
expr_stmt|;
name|super
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
specifier|public
name|boolean
name|isClosed
parameter_list|()
block|{
return|return
name|closed
return|;
block|}
comment|/**      * @param timeout      */
specifier|public
name|void
name|setTimeout
parameter_list|(
name|long
name|timeout
parameter_list|)
block|{
if|if
condition|(
name|timeout
operator|<=
literal|0
condition|)
name|timeout
operator|=
name|Channel
operator|.
name|WAIT_FOREVER_TIMEOUT
expr_stmt|;
name|this
operator|.
name|timeout
operator|=
name|timeout
expr_stmt|;
block|}
comment|/**      * @return      */
specifier|public
name|long
name|getTimeout
parameter_list|()
block|{
if|if
condition|(
name|timeout
operator|==
name|Channel
operator|.
name|WAIT_FOREVER_TIMEOUT
condition|)
return|return
literal|0
return|;
return|return
name|timeout
return|;
block|}
block|}
end_class

end_unit

