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
comment|/**  * Used by a {@link ReplayBuffer} to replay buffers back over an unreliable transport  *   * @version $Revision$  */
end_comment

begin_interface
specifier|public
interface|interface
name|Replayer
block|{
comment|/**      * Sends the given buffer back to the transport      * if the buffer could be found - otherwise maybe send some kind      * of exception      *       * @param commandId the command ID      * @param buffer the buffer to be sent - or null if the buffer no longer exists in the buffer      */
name|void
name|sendBuffer
parameter_list|(
name|int
name|commandId
parameter_list|,
name|Object
name|buffer
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit

