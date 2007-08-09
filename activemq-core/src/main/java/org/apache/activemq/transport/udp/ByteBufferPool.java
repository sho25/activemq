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
name|udp
package|;
end_package

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|ByteBuffer
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
name|Service
import|;
end_import

begin_comment
comment|/**  * Represents a pool of {@link ByteBuffer} instances.   * This strategy could just create new buffers for each call or  * it could pool them.  *   * @version $Revision$  */
end_comment

begin_interface
specifier|public
interface|interface
name|ByteBufferPool
extends|extends
name|Service
block|{
comment|/**      * Extract a buffer from the pool.      */
name|ByteBuffer
name|borrowBuffer
parameter_list|()
function_decl|;
comment|/**      * Returns the buffer to the pool or just discards it for a non-pool strategy      */
name|void
name|returnBuffer
parameter_list|(
name|ByteBuffer
name|buffer
parameter_list|)
function_decl|;
comment|/**      * Sets the default size of the buffers      */
name|void
name|setDefaultSize
parameter_list|(
name|int
name|defaultSize
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

