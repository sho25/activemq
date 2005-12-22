begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * * Copyright 2004 Hiram Chirino * *  Licensed under the Apache License, Version 2.0 (the "License"); *  you may not use this file except in compliance with the License. *  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * *  Unless required by applicable law or agreed to in writing, software *  distributed under the License is distributed on an "AS IS" BASIS, *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. *  See the License for the specific language governing permissions and *  limitations under the License. */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|activeio
operator|.
name|packet
operator|.
name|async
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
comment|/**  * AsyncChannelFactory objects can create {@see org.apache.activeio.AsyncChannel}  * and {@see org.apache.activeio.AsyncChannelServer} objects.   *   * @version $Revision$  */
end_comment

begin_interface
specifier|public
interface|interface
name|AsyncChannelFactory
block|{
comment|/**      * Opens a connection to server.      *       * @param location      * @return      */
specifier|public
name|AsyncChannel
name|openAsyncChannel
parameter_list|(
name|URI
name|location
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * Binds a server at the URI location.      *       * @param location      * @return      */
specifier|public
name|AsyncChannelServer
name|bindAsyncChannel
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

