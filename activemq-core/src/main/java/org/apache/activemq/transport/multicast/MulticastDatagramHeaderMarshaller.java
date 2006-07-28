begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|multicast
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
name|DatagramEndpoint
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
name|transport
operator|.
name|udp
operator|.
name|DatagramHeaderMarshaller
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
name|MulticastDatagramHeaderMarshaller
extends|extends
name|DatagramHeaderMarshaller
block|{
specifier|private
specifier|final
name|String
name|localUri
decl_stmt|;
specifier|private
specifier|final
name|byte
index|[]
name|localUriAsBytes
decl_stmt|;
specifier|public
name|MulticastDatagramHeaderMarshaller
parameter_list|(
name|String
name|localUri
parameter_list|)
block|{
name|this
operator|.
name|localUri
operator|=
name|localUri
expr_stmt|;
name|this
operator|.
name|localUriAsBytes
operator|=
name|localUri
operator|.
name|getBytes
argument_list|()
expr_stmt|;
block|}
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
name|int
name|size
init|=
name|readBuffer
operator|.
name|getInt
argument_list|()
decl_stmt|;
name|byte
index|[]
name|data
init|=
operator|new
name|byte
index|[
name|size
index|]
decl_stmt|;
name|readBuffer
operator|.
name|get
argument_list|(
name|data
argument_list|)
expr_stmt|;
return|return
operator|new
name|DatagramEndpoint
argument_list|(
operator|new
name|String
argument_list|(
name|data
argument_list|)
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
name|writeBuffer
operator|.
name|putInt
argument_list|(
name|localUriAsBytes
operator|.
name|length
argument_list|)
expr_stmt|;
name|writeBuffer
operator|.
name|put
argument_list|(
name|localUriAsBytes
argument_list|)
expr_stmt|;
name|super
operator|.
name|writeHeader
argument_list|(
name|command
argument_list|,
name|writeBuffer
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

