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
name|packet
operator|.
name|Packet
import|;
end_import

begin_comment
comment|/**  * SyncChannel objects allow threaded to synchronously block on the<code>receiveUpPacket</code>  * method to get 'up' {@see org.apache.activeio.Packet} objects when they arrive.  *   * @version $Revision$  */
end_comment

begin_interface
specifier|public
interface|interface
name|SyncChannel
extends|extends
name|Channel
block|{
comment|/**      * Used to synchronously receive a packet of information going 'up' the channel.      * This method blocks until a packet is received or the operation experiences timeout.      *       * @param timeout      * @return the packet received or null if the timeout occurred.      * @throws IOException      */
name|Packet
name|read
parameter_list|(
name|long
name|timeout
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * Sends a packet down the channel towards the media.      *       * @param packet      * @throws IOException      */
name|void
name|write
parameter_list|(
name|Packet
name|packet
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * Some channels may buffer data which may be sent down if flush() is called.      *       * @throws IOException      */
name|void
name|flush
parameter_list|()
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit

