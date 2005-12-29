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
comment|/**  * The Service interface is used control the running state of a channel.  *    * Some channels may use background threads to provide SEDA style processing.  By  * implenting the Service interface, a protcol can allow a container to  * control those threads.  *    * @version $Revision$  */
end_comment

begin_interface
specifier|public
interface|interface
name|Service
block|{
specifier|static
specifier|final
specifier|public
name|long
name|NO_WAIT_TIMEOUT
init|=
literal|0
decl_stmt|;
specifier|static
specifier|final
specifier|public
name|long
name|WAIT_FOREVER_TIMEOUT
init|=
operator|-
literal|1
decl_stmt|;
comment|/** 	 * Starts the channel.  Once started, the channel is in the running state.   	 *   	 * @throws IOException 	 */
name|void
name|start
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/** 	 * Stops the channel.  Once stopped, the channel is in the stopped state. 	 *  	 * @throws IOException 	 */
name|void
name|stop
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**      * Disposes the channel.  Once disposed, the channel cannot be used anymore.      *       * @throws IOException      */
name|void
name|dispose
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

