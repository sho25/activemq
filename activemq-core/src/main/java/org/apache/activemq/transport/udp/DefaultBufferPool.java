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
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_comment
comment|/**  * A default implementation of {@link BufferPool} which keeps a pool of direct  * byte buffers.  *   *   */
end_comment

begin_class
specifier|public
class|class
name|DefaultBufferPool
extends|extends
name|SimpleBufferPool
implements|implements
name|ByteBufferPool
block|{
specifier|private
name|List
argument_list|<
name|ByteBuffer
argument_list|>
name|buffers
init|=
operator|new
name|ArrayList
argument_list|<
name|ByteBuffer
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
name|Object
name|lock
init|=
operator|new
name|Object
argument_list|()
decl_stmt|;
specifier|public
name|DefaultBufferPool
parameter_list|()
block|{
name|super
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
specifier|public
name|DefaultBufferPool
parameter_list|(
name|boolean
name|useDirect
parameter_list|)
block|{
name|super
argument_list|(
name|useDirect
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|synchronized
name|ByteBuffer
name|borrowBuffer
parameter_list|()
block|{
synchronized|synchronized
init|(
name|lock
init|)
block|{
name|int
name|size
init|=
name|buffers
operator|.
name|size
argument_list|()
decl_stmt|;
if|if
condition|(
name|size
operator|>
literal|0
condition|)
block|{
return|return
name|buffers
operator|.
name|remove
argument_list|(
name|size
operator|-
literal|1
argument_list|)
return|;
block|}
block|}
return|return
name|createBuffer
argument_list|()
return|;
block|}
specifier|public
name|void
name|returnBuffer
parameter_list|(
name|ByteBuffer
name|buffer
parameter_list|)
block|{
synchronized|synchronized
init|(
name|lock
init|)
block|{
name|buffers
operator|.
name|add
argument_list|(
name|buffer
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|start
parameter_list|()
throws|throws
name|Exception
block|{     }
specifier|public
name|void
name|stop
parameter_list|()
throws|throws
name|Exception
block|{
synchronized|synchronized
init|(
name|lock
init|)
block|{
comment|/*              * for (Iterator iter = buffers.iterator(); iter.hasNext();) {              * ByteBuffer buffer = (ByteBuffer) iter.next(); }              */
name|buffers
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

