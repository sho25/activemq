begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Copyright 2004 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
package|;
end_package

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|ByteBuffer
import|;
end_import

begin_comment
comment|/**  * Provides a simple pool of ByteBuffer objects.  *   * @version $Revision: 1.1 $  */
end_comment

begin_class
specifier|final
specifier|public
class|class
name|ByteBufferPacketPool
extends|extends
name|PacketPool
block|{
specifier|private
specifier|final
name|int
name|packetSize
decl_stmt|;
comment|/** 	 * Creates a pool of<code>bufferCount</code> ByteBuffers that are  	 * directly allocated being<code>bufferSize</code> big. 	 *  	 * @param packetCount the number of buffers that will be in the pool. 	 * @param packetSize the size of the buffers that are in the pool. 	 */
specifier|public
name|ByteBufferPacketPool
parameter_list|(
name|int
name|packetCount
parameter_list|,
name|int
name|packetSize
parameter_list|)
block|{
name|super
argument_list|(
name|packetCount
argument_list|)
expr_stmt|;
name|this
operator|.
name|packetSize
operator|=
name|packetSize
expr_stmt|;
block|}
specifier|protected
name|Packet
name|allocateNewPacket
parameter_list|()
block|{
return|return
operator|new
name|ByteBufferPacket
argument_list|(
name|ByteBuffer
operator|.
name|allocateDirect
argument_list|(
name|packetSize
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

end_unit

