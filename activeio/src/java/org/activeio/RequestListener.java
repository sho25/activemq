begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Copyright 2004 Hiram Chirino  *  *  Licensed under the Apache License, Version 2.0 (the "License");  *  you may not use this file except in compliance with the License.  *  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|activeio
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
comment|/**  * An RequestListener object is used to receive remote requests from a a {@see org.activeio.RequestChannel}  *   * @version $Revision$  */
end_comment

begin_interface
specifier|public
interface|interface
name|RequestListener
block|{
comment|/** 	 * A {@see RequestChannel} will call this method when a new request arrives. 	 *    	 * @param packet 	 */
name|Packet
name|onRequest
parameter_list|(
name|Packet
name|request
parameter_list|)
function_decl|;
comment|/** 	 * A {@see RequestChannel} will call this method when a async failure occurs when receiving a request.      *       * @param error the exception that describes the failure.      */
name|void
name|onRquestError
parameter_list|(
name|IOException
name|error
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

