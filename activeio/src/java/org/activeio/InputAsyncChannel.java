begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Copyright 2004 Hiram Chirino  *  *  Licensed under the Apache License, Version 2.0 (the "License");  *  you may not use this file except in compliance with the License.  *  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|activeio
package|;
end_package

begin_comment
comment|/**  * InputAsyncChannel objects asynchronously push 'up' {@see org.activeio.Packet} objects  * to a registered {@see org.activeio.AsyncChannelListener}.  *   * @version $Revision$  */
end_comment

begin_interface
specifier|public
interface|interface
name|InputAsyncChannel
extends|extends
name|Channel
block|{
comment|/** 	 * Registers the {@see ChannelConsumer} that the protcol will use to deliver packets 	 * coming 'up' the channel. 	 *   	 * @param packetListener 	 */
name|void
name|setAsyncChannelListener
parameter_list|(
name|AsyncChannelListener
name|channelListener
parameter_list|)
function_decl|;
comment|/** 	 * @return the registered Packet consumer 	 */
name|AsyncChannelListener
name|getAsyncChannelListener
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

