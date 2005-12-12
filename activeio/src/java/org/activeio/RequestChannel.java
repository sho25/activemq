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

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_comment
comment|/**  * RequestChannel are used to model the request/reponse exchange that is used  * by higher level protcols such as HTTP and RMI.   *   * @version $Revision$  */
end_comment

begin_interface
specifier|public
interface|interface
name|RequestChannel
extends|extends
name|Channel
block|{
comment|/** 	 * Used to send a packet of information going 'down' the channel and wait for 	 * it's reponse 'up' packet. 	 *  	 * This method blocks until the response packet is received or the operation  	 * experiences a timeout. 	 *  	 * @param request 	 * @param timeout 	 * @return the respnse packet or null if the timeout occured. 	 * @throws IOException 	 */
name|Packet
name|request
parameter_list|(
name|Packet
name|request
parameter_list|,
name|long
name|timeout
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** 	 * Registers the {@see RequestListener} that the protcol will use to deliver request packets 	 * comming 'up' the channel. 	 *   	 * @param packetListener 	 * @throws IOException 	 */
name|void
name|setRequestListener
parameter_list|(
name|RequestListener
name|requestListener
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** 	 * @return the registered RequestListener 	 */
name|RequestListener
name|getRequestListener
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

