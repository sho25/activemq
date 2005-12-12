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
name|org
operator|.
name|activeio
operator|.
name|Channel
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

begin_comment
comment|/**  * Allows command objects to be written into a channel  *  * @version $Revision: 1.1 $  */
end_comment

begin_interface
specifier|public
interface|interface
name|AsyncCommandChannel
extends|extends
name|Channel
block|{
comment|/**      * Sends a command down the channel towards the media, using a WireFormat      * to decide how to marshal the command onto the media.      *      * @param command      * @throws java.io.IOException      */
name|void
name|writeCommand
parameter_list|(
name|Object
name|command
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * Allows a listener to be added for commands      *       * @param listener      */
name|void
name|setCommandListener
parameter_list|(
name|CommandListener
name|listener
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

