begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|DefaultReplayStrategy
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
name|DefaultReplayStrategy
parameter_list|()
block|{     }
specifier|public
name|DefaultReplayStrategy
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
parameter_list|,
name|int
name|nextAvailableCounter
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
name|int
name|upperLimit
init|=
name|actualCounter
operator|-
literal|1
decl_stmt|;
if|if
condition|(
name|upperLimit
operator|<
name|expectedCounter
condition|)
block|{
name|upperLimit
operator|=
name|expectedCounter
expr_stmt|;
block|}
name|transport
operator|.
name|requestReplay
argument_list|(
name|expectedCounter
argument_list|,
name|upperLimit
argument_list|)
expr_stmt|;
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
block|{
comment|// TODO we could pro-actively evict stuff from the buffer if we knew there was only one client
block|}
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

