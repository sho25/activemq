begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Copyright 2004 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
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
comment|/**  */
end_comment

begin_class
specifier|public
class|class
name|ByteBufferPacketTest
extends|extends
name|PacketTestSupport
block|{
name|Packet
name|createTestPacket
parameter_list|(
name|int
name|capacity
parameter_list|)
block|{
return|return
operator|new
name|ByteBufferPacket
argument_list|(
name|ByteBuffer
operator|.
name|allocate
argument_list|(
name|capacity
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

end_unit

