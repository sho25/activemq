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
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|command
operator|.
name|Command
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|command
operator|.
name|Endpoint
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|SocketAddress
import|;
end_import

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
comment|/**  *   * @version $Revision$  */
end_comment

begin_class
specifier|public
class|class
name|DatagramHeaderMarshaller
block|{
comment|/**      * Reads any header if applicable and then creates an endpoint object      */
specifier|public
name|Endpoint
name|createEndpoint
parameter_list|(
name|ByteBuffer
name|readBuffer
parameter_list|,
name|SocketAddress
name|address
parameter_list|)
block|{
return|return
operator|new
name|DatagramEndpoint
argument_list|(
name|address
operator|.
name|toString
argument_list|()
argument_list|,
name|address
argument_list|)
return|;
block|}
specifier|public
name|void
name|writeHeader
parameter_list|(
name|Command
name|command
parameter_list|,
name|ByteBuffer
name|writeBuffer
parameter_list|)
block|{
comment|/*         writeBuffer.putLong(command.getCounter());         writeBuffer.putInt(command.getDataSize());         byte flags = command.getFlags();         //System.out.println("Writing header with counter: " + header.getCounter() + " size: " + header.getDataSize() + " with flags: " + flags);         writeBuffer.put(flags);         */
block|}
block|}
end_class

end_unit

