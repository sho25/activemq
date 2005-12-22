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
name|stream
operator|.
name|sync
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

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URI
import|;
end_import

begin_comment
comment|/**  * StreamChannelFactory objects can create {@see org.apache.activeio.StreamChannel}  * and {@see org.apache.activeio.StreamChannelServer} objects.   *   * @version $Revision$  */
end_comment

begin_interface
specifier|public
interface|interface
name|StreamChannelFactory
block|{
comment|/**      * Opens a connection to server.      *       * @param location       * @return      */
specifier|public
name|StreamChannel
name|openStreamChannel
parameter_list|(
name|URI
name|location
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * Binds a server at the URI location.      *       * @param location      * @return      */
specifier|public
name|StreamChannelServer
name|bindStreamChannel
parameter_list|(
name|URI
name|location
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit

