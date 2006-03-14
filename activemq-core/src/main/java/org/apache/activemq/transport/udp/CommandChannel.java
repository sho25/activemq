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
name|Service
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
name|Command
import|;
end_import

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
name|java
operator|.
name|net
operator|.
name|SocketAddress
import|;
end_import

begin_comment
comment|/**  *  * @version $Revision$  */
end_comment

begin_interface
specifier|public
interface|interface
name|CommandChannel
extends|extends
name|Service
block|{
specifier|public
specifier|abstract
name|Command
name|read
parameter_list|()
throws|throws
name|IOException
function_decl|;
specifier|public
specifier|abstract
name|void
name|write
parameter_list|(
name|Command
name|command
parameter_list|,
name|SocketAddress
name|address
parameter_list|)
throws|throws
name|IOException
function_decl|;
specifier|public
specifier|abstract
name|int
name|getDatagramSize
parameter_list|()
function_decl|;
comment|/**      * Sets the default size of a datagram on the network.      */
specifier|public
specifier|abstract
name|void
name|setDatagramSize
parameter_list|(
name|int
name|datagramSize
parameter_list|)
function_decl|;
specifier|public
specifier|abstract
name|DatagramHeaderMarshaller
name|getHeaderMarshaller
parameter_list|()
function_decl|;
specifier|public
specifier|abstract
name|void
name|setHeaderMarshaller
parameter_list|(
name|DatagramHeaderMarshaller
name|headerMarshaller
parameter_list|)
function_decl|;
specifier|public
specifier|abstract
name|void
name|setTargetAddress
parameter_list|(
name|SocketAddress
name|address
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

