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
comment|/**  * This class keeps around a buffer of old commands which have been sent on  * an unreliable transport. The buffers are of type Object as they could be datagrams  * or byte[] or ByteBuffer - depending on the underlying transport implementation.  *   * @version $Revision$  */
end_comment

begin_interface
specifier|public
interface|interface
name|ReplayBuffer
block|{
comment|/**      * Submit a buffer for caching around for a period of time, during which time it can be replayed      * to users interested in it.      */
name|void
name|addBuffer
parameter_list|(
name|int
name|commandId
parameter_list|,
name|Object
name|buffer
parameter_list|)
function_decl|;
name|void
name|setReplayBufferListener
parameter_list|(
name|ReplayBufferListener
name|bufferPoolAdapter
parameter_list|)
function_decl|;
name|void
name|replayMessages
parameter_list|(
name|int
name|fromCommandId
parameter_list|,
name|int
name|toCommandId
parameter_list|,
name|Replayer
name|replayer
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit

