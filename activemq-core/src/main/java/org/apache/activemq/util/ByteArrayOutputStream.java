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
name|util
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|OutputStream
import|;
end_import

begin_comment
comment|/**  * Very similar to the java.io.ByteArrayOutputStream but this version   * is not thread safe and the resulting data is returned in a ByteSequence  * to avoid an extra byte[] allocation.  */
end_comment

begin_class
specifier|public
class|class
name|ByteArrayOutputStream
extends|extends
name|OutputStream
block|{
name|byte
name|buffer
index|[]
decl_stmt|;
name|int
name|size
decl_stmt|;
specifier|public
name|ByteArrayOutputStream
parameter_list|()
block|{
name|this
argument_list|(
literal|1028
argument_list|)
expr_stmt|;
block|}
specifier|public
name|ByteArrayOutputStream
parameter_list|(
name|int
name|capacity
parameter_list|)
block|{
name|buffer
operator|=
operator|new
name|byte
index|[
name|capacity
index|]
expr_stmt|;
block|}
specifier|public
name|void
name|write
parameter_list|(
name|int
name|b
parameter_list|)
block|{
name|int
name|newsize
init|=
name|size
operator|+
literal|1
decl_stmt|;
name|checkCapacity
argument_list|(
name|newsize
argument_list|)
expr_stmt|;
name|buffer
index|[
name|size
index|]
operator|=
operator|(
name|byte
operator|)
name|b
expr_stmt|;
name|size
operator|=
name|newsize
expr_stmt|;
block|}
specifier|public
name|void
name|write
parameter_list|(
name|byte
name|b
index|[]
parameter_list|,
name|int
name|off
parameter_list|,
name|int
name|len
parameter_list|)
block|{
name|int
name|newsize
init|=
name|size
operator|+
name|len
decl_stmt|;
name|checkCapacity
argument_list|(
name|newsize
argument_list|)
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|b
argument_list|,
name|off
argument_list|,
name|buffer
argument_list|,
name|size
argument_list|,
name|len
argument_list|)
expr_stmt|;
name|size
operator|=
name|newsize
expr_stmt|;
block|}
comment|/**      * Ensures the the buffer has at least the minimumCapacity specified.       * @param minimumCapacity      */
specifier|private
name|void
name|checkCapacity
parameter_list|(
name|int
name|minimumCapacity
parameter_list|)
block|{
if|if
condition|(
name|minimumCapacity
operator|>
name|buffer
operator|.
name|length
condition|)
block|{
name|byte
name|b
index|[]
init|=
operator|new
name|byte
index|[
name|Math
operator|.
name|max
argument_list|(
name|buffer
operator|.
name|length
operator|<<
literal|1
argument_list|,
name|minimumCapacity
argument_list|)
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|,
name|b
argument_list|,
literal|0
argument_list|,
name|size
argument_list|)
expr_stmt|;
name|buffer
operator|=
name|b
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|reset
parameter_list|()
block|{
name|size
operator|=
literal|0
expr_stmt|;
block|}
specifier|public
name|ByteSequence
name|toByteSequence
parameter_list|()
block|{
return|return
operator|new
name|ByteSequence
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|,
name|size
argument_list|)
return|;
block|}
specifier|public
name|byte
index|[]
name|toByteArray
parameter_list|()
block|{
name|byte
name|rc
index|[]
init|=
operator|new
name|byte
index|[
name|size
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|,
name|rc
argument_list|,
literal|0
argument_list|,
name|size
argument_list|)
expr_stmt|;
return|return
name|rc
return|;
block|}
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
name|size
return|;
block|}
specifier|public
name|boolean
name|endsWith
parameter_list|(
specifier|final
name|byte
index|[]
name|array
parameter_list|)
block|{
name|int
name|i
init|=
literal|0
decl_stmt|;
name|int
name|start
init|=
name|size
operator|-
name|array
operator|.
name|length
decl_stmt|;
if|if
condition|(
name|start
operator|<
literal|0
condition|)
block|{
return|return
literal|false
return|;
block|}
while|while
condition|(
name|start
operator|<
name|size
condition|)
block|{
if|if
condition|(
name|buffer
index|[
name|start
operator|++
index|]
operator|!=
name|array
index|[
name|i
operator|++
index|]
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
return|return
literal|true
return|;
block|}
block|}
end_class

end_unit

