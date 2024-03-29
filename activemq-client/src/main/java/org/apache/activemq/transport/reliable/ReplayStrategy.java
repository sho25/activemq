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
comment|/**  * A pluggable strategy for how to deal with dropped packets.  *   *   */
end_comment

begin_interface
specifier|public
interface|interface
name|ReplayStrategy
block|{
comment|/**      * Deals with a dropped packet.       *       * @param transport the transport on which the packet was dropped      * @param expectedCounter the expected command counter      * @param actualCounter the actual command counter      * @param nextAvailableCounter TODO      * @return true if the command should be buffered or false if it should be discarded      */
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
function_decl|;
name|void
name|onReceivedPacket
parameter_list|(
name|ReliableTransport
name|transport
parameter_list|,
name|long
name|expectedCounter
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

