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
name|activemq
operator|.
name|transport
operator|.
name|udp
operator|.
name|replay
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
comment|/**  * Throws an exception if packets are dropped causing the transport to be closed.  *   * @version $Revision$  */
end_comment

begin_class
specifier|public
class|class
name|ExceptionIfDroppedPacketStrategy
implements|implements
name|DatagramReplayStrategy
block|{
specifier|public
name|void
name|onDroppedPackets
parameter_list|(
name|String
name|name
parameter_list|,
name|long
name|expectedCounter
parameter_list|,
name|long
name|actualCounter
parameter_list|)
throws|throws
name|IOException
block|{
name|long
name|count
init|=
name|actualCounter
operator|-
name|expectedCounter
decl_stmt|;
throw|throw
operator|new
name|IOException
argument_list|(
name|name
operator|+
name|count
operator|+
literal|" packet(s) dropped. Expected: "
operator|+
name|expectedCounter
operator|+
literal|" but was: "
operator|+
name|actualCounter
argument_list|)
throw|;
block|}
specifier|public
name|void
name|onReceivedPacket
parameter_list|(
name|String
name|name
parameter_list|,
name|long
name|expectedCounter
parameter_list|)
block|{     }
block|}
end_class

end_unit

