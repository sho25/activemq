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
name|reliable
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
comment|/**  * Throws an exception if packets are dropped causing the transport to be  * closed.  *   * @version $Revision$  */
end_comment

begin_class
specifier|public
class|class
name|ExceptionIfDroppedReplayStrategy
implements|implements
name|ReplayStrategy
block|{
specifier|private
name|int
name|maximumDifference
init|=
literal|5
decl_stmt|;
specifier|public
name|boolean
name|onDroppedPackets
parameter_list|(
name|ReliableTransport
name|transport
parameter_list|,
name|int
name|expectedCounter
parameter_list|,
name|int
name|actualCounter
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|difference
init|=
name|actualCounter
operator|-
name|expectedCounter
decl_stmt|;
name|long
name|count
init|=
name|Math
operator|.
name|abs
argument_list|(
name|difference
argument_list|)
decl_stmt|;
if|if
condition|(
name|count
operator|>
name|maximumDifference
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Packets dropped on: "
operator|+
name|transport
operator|+
literal|" count: "
operator|+
name|count
operator|+
literal|" expected: "
operator|+
name|expectedCounter
operator|+
literal|" but was: "
operator|+
name|actualCounter
argument_list|)
throw|;
block|}
comment|// lets discard old commands
return|return
name|difference
operator|>
literal|0
return|;
block|}
specifier|public
name|void
name|onReceivedPacket
parameter_list|(
name|ReliableTransport
name|transport
parameter_list|,
name|long
name|expectedCounter
parameter_list|)
block|{     }
specifier|public
name|int
name|getMaximumDifference
parameter_list|()
block|{
return|return
name|maximumDifference
return|;
block|}
comment|/**      * Sets the maximum allowed difference between an expected packet and an      * actual packet before an error occurs      */
specifier|public
name|void
name|setMaximumDifference
parameter_list|(
name|int
name|maximumDifference
parameter_list|)
block|{
name|this
operator|.
name|maximumDifference
operator|=
name|maximumDifference
expr_stmt|;
block|}
block|}
end_class

end_unit

