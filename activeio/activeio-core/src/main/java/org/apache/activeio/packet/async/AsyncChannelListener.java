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
name|async
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
name|packet
operator|.
name|Packet
import|;
end_import

begin_comment
comment|/**  * A ChannelConsumer object is used to receive 'up' {@see org.apache.activeio.Packet} objects.  *   * TODO: describe the threading model so that the implementor of this interface can know if  * the methods in this interface can block for a long time or not.  I'm thinking that it would  * be best if these methods are not allowed to block for a long time to encourage SEDA style   * processing.  *   * @version $Revision$  */
end_comment

begin_interface
specifier|public
interface|interface
name|AsyncChannelListener
block|{
comment|/** 	 * A {@see AsyncChannel} will call this method to deliver an 'up' packet to a consumer.  	 *    	 * @param packet 	 */
name|void
name|onPacket
parameter_list|(
name|Packet
name|packet
parameter_list|)
function_decl|;
comment|/** 	 * A {@see AsyncChannel} will call this method when a async failure occurs in the channel.       *       * @param error the exception that describes the failure.      */
name|void
name|onPacketError
parameter_list|(
name|IOException
name|error
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

