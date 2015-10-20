begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License. You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|amqp
operator|.
name|client
operator|.
name|transport
package|;
end_package

begin_import
import|import
name|io
operator|.
name|netty
operator|.
name|buffer
operator|.
name|ByteBuf
import|;
end_import

begin_import
import|import
name|io
operator|.
name|netty
operator|.
name|buffer
operator|.
name|ByteBufAllocator
import|;
end_import

begin_import
import|import
name|io
operator|.
name|netty
operator|.
name|buffer
operator|.
name|CompositeByteBuf
import|;
end_import

begin_import
import|import
name|io
operator|.
name|netty
operator|.
name|buffer
operator|.
name|PooledByteBufAllocator
import|;
end_import

begin_import
import|import
name|io
operator|.
name|netty
operator|.
name|buffer
operator|.
name|UnpooledByteBufAllocator
import|;
end_import

begin_comment
comment|/**  * A {@link ByteBufAllocator} which is partial pooled. Which means only direct  * {@link ByteBuf}s are pooled. The rest is unpooled.  *  * @author<a href="mailto:nmaurer@redhat.com">Norman Maurer</a>  */
end_comment

begin_class
specifier|public
class|class
name|PartialPooledByteBufAllocator
implements|implements
name|ByteBufAllocator
block|{
specifier|private
specifier|static
specifier|final
name|ByteBufAllocator
name|POOLED
init|=
operator|new
name|PooledByteBufAllocator
argument_list|(
literal|false
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|ByteBufAllocator
name|UNPOOLED
init|=
operator|new
name|UnpooledByteBufAllocator
argument_list|(
literal|false
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|PartialPooledByteBufAllocator
name|INSTANCE
init|=
operator|new
name|PartialPooledByteBufAllocator
argument_list|()
decl_stmt|;
specifier|private
name|PartialPooledByteBufAllocator
parameter_list|()
block|{     }
annotation|@
name|Override
specifier|public
name|ByteBuf
name|buffer
parameter_list|()
block|{
return|return
name|UNPOOLED
operator|.
name|heapBuffer
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|ByteBuf
name|buffer
parameter_list|(
name|int
name|initialCapacity
parameter_list|)
block|{
return|return
name|UNPOOLED
operator|.
name|heapBuffer
argument_list|(
name|initialCapacity
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|ByteBuf
name|buffer
parameter_list|(
name|int
name|initialCapacity
parameter_list|,
name|int
name|maxCapacity
parameter_list|)
block|{
return|return
name|UNPOOLED
operator|.
name|heapBuffer
argument_list|(
name|initialCapacity
argument_list|,
name|maxCapacity
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|ByteBuf
name|ioBuffer
parameter_list|()
block|{
return|return
name|UNPOOLED
operator|.
name|heapBuffer
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|ByteBuf
name|ioBuffer
parameter_list|(
name|int
name|initialCapacity
parameter_list|)
block|{
return|return
name|UNPOOLED
operator|.
name|heapBuffer
argument_list|(
name|initialCapacity
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|ByteBuf
name|ioBuffer
parameter_list|(
name|int
name|initialCapacity
parameter_list|,
name|int
name|maxCapacity
parameter_list|)
block|{
return|return
name|UNPOOLED
operator|.
name|heapBuffer
argument_list|(
name|initialCapacity
argument_list|,
name|maxCapacity
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|ByteBuf
name|heapBuffer
parameter_list|()
block|{
return|return
name|UNPOOLED
operator|.
name|heapBuffer
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|ByteBuf
name|heapBuffer
parameter_list|(
name|int
name|initialCapacity
parameter_list|)
block|{
return|return
name|UNPOOLED
operator|.
name|heapBuffer
argument_list|(
name|initialCapacity
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|ByteBuf
name|heapBuffer
parameter_list|(
name|int
name|initialCapacity
parameter_list|,
name|int
name|maxCapacity
parameter_list|)
block|{
return|return
name|UNPOOLED
operator|.
name|heapBuffer
argument_list|(
name|initialCapacity
argument_list|,
name|maxCapacity
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|ByteBuf
name|directBuffer
parameter_list|()
block|{
return|return
name|POOLED
operator|.
name|directBuffer
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|ByteBuf
name|directBuffer
parameter_list|(
name|int
name|initialCapacity
parameter_list|)
block|{
return|return
name|POOLED
operator|.
name|directBuffer
argument_list|(
name|initialCapacity
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|ByteBuf
name|directBuffer
parameter_list|(
name|int
name|initialCapacity
parameter_list|,
name|int
name|maxCapacity
parameter_list|)
block|{
return|return
name|POOLED
operator|.
name|directBuffer
argument_list|(
name|initialCapacity
argument_list|,
name|maxCapacity
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|CompositeByteBuf
name|compositeBuffer
parameter_list|()
block|{
return|return
name|UNPOOLED
operator|.
name|compositeHeapBuffer
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|CompositeByteBuf
name|compositeBuffer
parameter_list|(
name|int
name|maxNumComponents
parameter_list|)
block|{
return|return
name|UNPOOLED
operator|.
name|compositeHeapBuffer
argument_list|(
name|maxNumComponents
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|CompositeByteBuf
name|compositeHeapBuffer
parameter_list|()
block|{
return|return
name|UNPOOLED
operator|.
name|compositeHeapBuffer
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|CompositeByteBuf
name|compositeHeapBuffer
parameter_list|(
name|int
name|maxNumComponents
parameter_list|)
block|{
return|return
name|UNPOOLED
operator|.
name|compositeHeapBuffer
argument_list|(
name|maxNumComponents
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|CompositeByteBuf
name|compositeDirectBuffer
parameter_list|()
block|{
return|return
name|POOLED
operator|.
name|compositeDirectBuffer
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|CompositeByteBuf
name|compositeDirectBuffer
parameter_list|(
name|int
name|maxNumComponents
parameter_list|)
block|{
return|return
name|POOLED
operator|.
name|compositeDirectBuffer
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isDirectBufferPooled
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
block|}
end_class

end_unit
