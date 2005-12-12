begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**   *   * Copyright 2005 James Strachan  *   * Licensed under the Apache License, Version 2.0 (the "License");   * you may not use this file except in compliance with the License.   * You may obtain a copy of the License at   *   * http://www.apache.org/licenses/LICENSE-2.0  *   * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,   * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   * See the License for the specific language governing permissions and   * limitations under the License.   *   **/
end_comment

begin_package
package|package
name|org
operator|.
name|activeio
operator|.
name|command
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataOutputStream
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
name|org
operator|.
name|activeio
operator|.
name|Packet
import|;
end_import

begin_comment
comment|/**  * Provides a mechanism to marshal commands into and out of packets  * or into and out of streams, Channels and Datagrams.  *  * @version $Revision: 1.1 $  */
end_comment

begin_interface
specifier|public
interface|interface
name|WireFormat
block|{
comment|/**      * Packet based marshaling       */
name|Packet
name|marshal
parameter_list|(
name|Object
name|command
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * Packet based un-marshaling       */
name|Object
name|unmarshal
parameter_list|(
name|Packet
name|packet
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * Stream based marshaling       */
name|void
name|marshal
parameter_list|(
name|Object
name|command
parameter_list|,
name|DataOutputStream
name|out
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * Packet based un-marshaling       */
name|Object
name|unmarshal
parameter_list|(
name|DataInputStream
name|in
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * @param the version of the wire format      */
specifier|public
name|void
name|setVersion
parameter_list|(
name|int
name|version
parameter_list|)
function_decl|;
comment|/**      * @return the version of the wire format      */
specifier|public
name|int
name|getVersion
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

